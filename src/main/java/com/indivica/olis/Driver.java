/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package com.indivica.olis;

import ca.ssha._2005.hial.ArrayOfError;
import ca.ssha._2005.hial.ArrayOfString;
import ca.ssha._2005.hial.Response;
import ca.ssha.www._2005.hial.OLISStub;
import ca.ssha.www._2005.hial.OLISStub.HIALRequest;
import ca.ssha.www._2005.hial.OLISStub.HIALRequestSignedRequest;
import ca.ssha.www._2005.hial.OLISStub.OLISRequest;
import ca.ssha.www._2005.hial.OLISStub.OLISRequestResponse;
import com.indivica.olis.queries.Query;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;
import org.oscarehr.common.model.OscarMsgType;
import org.oscarehr.common.model.Provider;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.olis.OLISProtocolSocketFactory;
import org.oscarehr.olis.OLISUtils;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.exception.MissingArgumentException;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarMessenger.data.MsgProviderData;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.oscarehr.common.model.UserProperty.OLIS_EMR_ID;

public class Driver
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final JunoProperties junoProperties = SpringUtils.getBean(JunoProperties.class);
	private static final SystemPreferenceService systemPreferenceService = SpringUtils.getBean(SystemPreferenceService.class);

	public static DriverResponse submitOLISQuery(@NotNull Provider loggedInProvider, @NotNull Query query)
	{
		return submitOLISQuery(loggedInProvider, query, null);
	}
	public static DriverResponse submitOLISQuery(@NotNull Provider loggedInProvider, @NotNull Query query, @Null String continuationPointer)
	{
		DriverResponse response;
		String logData = null;
		String actionStatus = LogConst.STATUS_FAILURE;

		try
		{
			if(StringUtils.isBlank(systemPreferenceService.getPreferenceValue(OLIS_EMR_ID, null)))
			{
				throw new MissingArgumentException("OLIS EMR ID is not set");
			}

			OLISMessage message = new OLISMessage(loggedInProvider, query, continuationPointer);

			System.setProperty("javax.net.ssl.trustStore", junoProperties.getOlis().getTruststore());
			System.setProperty("javax.net.ssl.trustStorePassword", junoProperties.getOlis().getTruststorePassword());
			
			OLISRequest olisRequest = new OLISRequest();
			olisRequest.setHIALRequest(new HIALRequest());
			String olisRequestURL = junoProperties.getOlis().getRequestUrl();
			OLISStub olis = new OLISStub(olisRequestURL);
			olis._getServiceClient().getOptions().setProperty(HTTPConstants.CUSTOM_PROTOCOL_HANDLER,
					new Protocol("https",(ProtocolSocketFactory)  new OLISProtocolSocketFactory(),443));
			
			olisRequest.getHIALRequest().setClientTransactionID(message.getTransactionId());
			olisRequest.getHIALRequest().setSignedRequest(new HIALRequestSignedRequest());

			String olisHL7String = message.getOlisHL7String().replaceAll("\n", "\r");
			String msgInXML = String.format("<Request xmlns=\"http://www.ssha.ca/2005/HIAL\"><Content><![CDATA[%s]]></Content></Request>", olisHL7String);

			String signedRequest;
			if(junoProperties.getOlis().getReturnedCert() != null)
			{
				signedRequest = Driver.signData2(msgInXML);
			}
			else
			{
				signedRequest = Driver.signData(msgInXML);
			}
			olisRequest.getHIALRequest().getSignedRequest().setSignedData(signedRequest);
			logData = olisHL7String;

			logger.info("OLIS Request" +
					"\nclientTransactionId: " + message.getTransactionId() +
					"\nrequest URL: " + olisRequestURL +
					"\nhl7 request query:\n" + olisHL7String.replaceAll("\r", "\n"));

			if (junoProperties.getOlis().isSimulate())
			{
				//TODO how to handle this without request object?
//				String olisResponseContent = (String) request.getSession().getAttribute("olisResponseContent");
//				request.setAttribute("olisResponseContent", response);
//				request.getSession().setAttribute("olisResponseContent", null);

//				DriverResponse response = new DriverResponse();
//				response.setHl7Response(olisResponseContent);
				response = new DriverResponse();
				actionStatus = LogConst.STATUS_SUCCESS;
			}
			else
			{
				OLISRequestResponse olisResponse = olis.oLISRequest(olisRequest);
				actionStatus = LogConst.STATUS_SUCCESS;

				String signedData = olisResponse.getHIALResponse().getSignedResponse().getSignedData();
				String unsignedData = Driver.unsignData(signedData);

				response = readResponseFromXML(loggedInProvider.getProviderNo(), unsignedData);
				response.setHl7Request(olisHL7String);
			}
		}
		catch(MissingArgumentException e)
		{
			logger.warn("Can't perform OLIS query due to missing arguments: " + e.getMessage());
			response = new DriverResponse();
			response.setSearchException(e);
			notifyOlisError(loggedInProvider.getProviderNo(), e.getMessage());
			logData = e.getMessage();
		}
		catch(Exception e)
		{
			logger.error("Can't perform OLIS query due to exception.", e);
			response = new DriverResponse();
			response.setSearchException(e);
			notifyOlisError(loggedInProvider.getProviderNo(), e.getMessage());
			logData = e.getMessage();
		}
		finally
		{
			LogAction.addLogEntry(loggedInProvider.getProviderNo(), null,
					LogConst.ACTION_QUERY, LogConst.CON_OLIS_LAB, actionStatus, null, null,
					logData);
		}
		return response;
	}

	public static DriverResponse readResponseFromXML(@NotNull String loggedInProviderNo, String olisResponse)
	{
		DriverResponse response = new DriverResponse();
		try
		{
			Response root = OLISUtils.getOLISResponse(olisResponse);
			if (root.getErrors() != null)
			{
				List<String> errorStringList = new LinkedList<>();

				// Read all the errors
				ArrayOfError errors = root.getErrors();
				List<ca.ssha._2005.hial.Error> errorList = errors.getError();

				for (ca.ssha._2005.hial.Error error : errorList)
				{
					String errorString = "ERROR " + error.getNumber() + " (" + error.getSeverity() + ") : " + error.getMessage();
					logger.debug(errorString);

					ArrayOfString details = error.getDetails();
					if(details != null)
					{
						errorString += String.join("\n", details.getString());
					}
					errorStringList.add(errorString);
				}
				response.setErrors(errorStringList);
			}
			else if(root.getContent() != null)
			{
				response.setHl7Response(root.getContent());
			}
		}
		catch(Exception e)
		{
			logger.error("Couldn't read XML from OLIS response.", e);
			notifyOlisError(loggedInProviderNo, "Couldn't read XML from OLIS response." + "\n" + e);
		}
		return response;
	}

	public static String unsignData(String data) {

		byte[] dataBytes = Base64.decode(data);

		try {

			CMSSignedData s = new CMSSignedData(dataBytes);
			Store certs = s.getCertificates();
			SignerInformationStore signers = s.getSignerInfos();
			@SuppressWarnings("unchecked")
			Collection<SignerInformation> c = signers.getSigners();
			Iterator<SignerInformation> it = c.iterator();
			while (it.hasNext()) {
				X509CertificateHolder cert = null;
				SignerInformation signer = it.next();
				Collection certCollection = certs.getMatches(signer.getSID());
				@SuppressWarnings("unchecked")
				Iterator<X509CertificateHolder> certIt = certCollection.iterator();
				cert = certIt.next();

				if (!signer.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) throw new Exception("Doesn't verify");
			}

			CMSProcessableByteArray cpb = (CMSProcessableByteArray) s.getSignedContent();
			byte[] signedContent = (byte[]) cpb.getContent();
			String content = new String(signedContent, StandardCharsets.ISO_8859_1);
			return content;
		} catch (Exception e) {
			logger.error("error", e);
		}
		return null;

	}

	//Method uses a jks and a returned cert separately instead of needing to 
	//import the cert into PKCS12 file.
	public static String signData2(String data) {
		X509Certificate cert = null;
		PrivateKey priv = null;
		KeyStore keystore = null;
		String pwd = junoProperties.getOlis().getSslKeystorePassword();
		String keystoreAlias = junoProperties.getOlis().getSslKeystoreAlias();
		String result = null;
		try {
			Security.addProvider(new BouncyCastleProvider());

			keystore = KeyStore.getInstance("JKS");
			// Load the keystore
			keystore.load(new FileInputStream(junoProperties.getOlis().getKeystore()), pwd.toCharArray());

			Enumeration<String> e = keystore.aliases();

			// print keystore aliases in debug mode.
			if(logger.isDebugEnabled())
			{
				while(e.hasMoreElements())
				{
					logger.debug("keystore alis: " + e.nextElement());
				}
			}

			// Get the private key and the certificate
			priv = (PrivateKey) keystore.getKey(keystoreAlias, pwd.toCharArray());

			FileInputStream is = new FileInputStream(junoProperties.getOlis().getReturnedCert());
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(is);

			// I'm not sure if this is necessary

			ArrayList<Certificate> certList = new ArrayList<Certificate>();
			certList.add(cert);

			Store certs = new JcaCertStore(certList);
			
			// Encrypt data
			CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();

			// What digest algorithm i must use? SHA1? MD5? RSA?...
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256withRSA").setProvider("BC").build(priv);
			sgen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
	                     .build(sha1Signer, cert));
			
			// I'm not sure this is necessary
			sgen.addCertificates(certs);
			
			// I think that the 2nd parameter need to be false (detached form)
			CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(data.getBytes()), true);
			
			byte[] signedData = csd.getEncoded();
			byte[] signedDataB64 = Base64.encode(signedData);

			result = new String(signedDataB64);

		} catch (Exception e) {
			logger.error("Can't sign HL7 message for OLIS", e);
		}
		return result;
	}

	public static String signData(String data) {
		X509Certificate cert = null;
		PrivateKey priv = null;
		KeyStore keystore = null;
		String pwd = "Olis2011";
		String result = null;
		try {
			Security.addProvider(new BouncyCastleProvider());

			keystore = KeyStore.getInstance("PKCS12", "SunJSSE");
			// Load the keystore
			keystore.load(new FileInputStream(junoProperties.getOlis().getKeystore()), pwd.toCharArray());

			Enumeration e = keystore.aliases();
			String name = "";

			if (e != null) {
				while (e.hasMoreElements()) {
					String n = (String) e.nextElement();
					if (keystore.isKeyEntry(n)) {
						name = n;
					}
				}
			}

			// Get the private key and the certificate
			priv = (PrivateKey) keystore.getKey(name, pwd.toCharArray());
			cert = (X509Certificate) keystore.getCertificate(name);

			// I'm not sure if this is necessary

			ArrayList<Certificate> certList = new ArrayList<Certificate>();
			certList.add(cert);
			
			Store certs = new JcaCertStore(certList);

			// Encrypt data
			CMSSignedDataGenerator sgen = new CMSSignedDataGenerator();

			// What digest algorithm i must use? SHA1? MD5? RSA?...
			ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(priv);
			sgen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build())
	                     .build(sha1Signer, cert));
			

			// I'm not sure this is necessary
			sgen.addCertificates(certs);
			
			// I think that the 2nd parameter need to be false (detached form)
			CMSSignedData csd = sgen.generate(new CMSProcessableByteArray(data.getBytes()), true);
			
			byte[] signedData = csd.getEncoded();
			byte[] signedDataB64 = Base64.encode(signedData);

			result = new String(signedDataB64);

		} catch (Exception e) {
			logger.error("Can't sign HL7 message for OLIS", e);
		}
		return result;
	}

	private static void notifyOlisError(@NotNull String loggedInProviderNo, String errorMsg)
	{
		String message = "OSCAR attempted to perform a fetch of OLIS data at " + new Date() + " but there was an error during the task.\n\nSee below for the error message:\n" + errorMsg;

		oscar.oscarMessenger.data.MsgMessageData messageData = new oscar.oscarMessenger.data.MsgMessageData();

		ArrayList<MsgProviderData> sendToProviderListData = new ArrayList<>();
		MsgProviderData mpd = new MsgProviderData();
		mpd.providerNo = loggedInProviderNo;
		mpd.locationId = "145";
		sendToProviderListData.add(mpd);

		String sentToString = messageData.createSentToString(sendToProviderListData);
		messageData.sendMessage2(message, "OLIS Retrieval Error", "System", sentToString, "-1", sendToProviderListData, null, null, OscarMsgType.GENERAL_TYPE);
	}
}
