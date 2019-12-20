/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.integration.iceFall.service;

import org.apache.commons.io.IOUtils;
import org.castor.core.util.Base64Encoder;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallNoSuchCustomerException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallPdfGenerationException;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionResponseTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCreatePrescriptionTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallCustomerTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorListTo1;
import org.oscarehr.integration.iceFall.service.transfer.IceFallDoctorTo1;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class IceFallService
{
	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	@Autowired
	IceFallRESTService iceFallRESTService;

	@Autowired
	DemographicExtDao demographicExtDao;

	@Autowired
	EFormDataService eFormDataService;

	public static final String CANOPY_CUSTOMER_ID_KEY = "canopy_customer_id";

	/**
	 * get icefall credentials from DB
	 * @return - the icefall credentials
	 */
	public IceFallCredentials getCredentials()
	{
		return iceFallCredentialsDao.getCredentials();
	}

	/**
	 * save icefall credentials to the database
	 * @param creds - the credentials to save
	 * @return - the saved credentials object
	 */
	public IceFallCredentials updateCredentials(IceFallCredentials creds)
	{
		iceFallCredentialsDao.merge(creds);
		return creds;
	}

	/**
	 * submit eform to ice fall
	 * @param provider - the provider doing the submission
	 * @param demo - the demographic to whom this eform pertains
	 * @param templateId - the eform template id
	 * @param isInstance - set to true if the templateId is an eform instance id (fdid);
	 * @param eformValues - the eform values map (oscardb=)
	 * @param request - httpservlet request
	 */
	public void sendIceFallForm(Provider provider, Demographic demo, Integer templateId, boolean isInstance, Map<String, String> eformValues, HttpServletRequest request)
	{
		sendIceFallForm(provider, demo, templateId, isInstance, eformValues, request.getScheme(), request.getContextPath());
	}

	/**
	 * submit eform to ice fall
	 * @param provider - the provider doing the submission
	 * @param demo - the demographic to whom this eform pertains
	 * @param templateId - the eform template id
	 * @param isInstance - set to true if the templateId is an eform instance id (fdid);
	 * @param eformValues - the eform values map (oscardb=)
	 * @param httpSchema - the http schema of the server
	 * @param contextPath - the context path of the server
	 */
	public void sendIceFallForm(Provider provider, Demographic demo, Integer templateId, boolean isInstance, Map<String, String> eformValues, String httpSchema, String contextPath)
	{
		//login to api
		iceFallRESTService.authenticate();

		//get doctor id
		Integer iceFallDocId = findDoctorId(provider, iceFallRESTService.getDoctorList());

		//get customer id
		IceFallCustomerTo1 canopyCustomer = null;
		try
		{
			canopyCustomer = getDemoCanopyInfo(demo);
		}
		catch(IceFallNoSuchCustomerException e)
		{// remote customer does not exist create one.
			//TODO create canopy customer
			throw new IceFallNoSuchCustomerException("Add Canopy Customer not implemented!");
		}

		IceFallCreatePrescriptionTo1 iceFallCreatePrescriptionTo1 = new IceFallCreatePrescriptionTo1();
		iceFallCreatePrescriptionTo1.setCustomerId(canopyCustomer.getCustomerId());

		//TMP VALUES CHANGE
		iceFallCreatePrescriptionTo1.setDosage(2.5f);
		iceFallCreatePrescriptionTo1.setRegistrationExpiry(LocalDate.of(2020,1,1));
		iceFallCreatePrescriptionTo1.setType("DRIED_CANNABIS");
		iceFallCreatePrescriptionTo1.setThcLimit(50);
		iceFallCreatePrescriptionTo1.setDiagnosis("TEST_1");
		iceFallCreatePrescriptionTo1.setClinicId(42);
		iceFallCreatePrescriptionTo1.setPages(1);
		iceFallCreatePrescriptionTo1.setDocumentData(getEformPDFDateForSubmission(provider, demo, templateId, eformValues, httpSchema, contextPath, isInstance));
		iceFallCreatePrescriptionTo1.setDoctorId(iceFallDocId);
		//TMP VALUES CHANGE

		IceFallCreatePrescriptionResponseTo1 responseTo1 = iceFallRESTService.sendPrescription(iceFallCreatePrescriptionTo1);
	}

	protected IceFallCustomerTo1 getDemoCanopyInfo(Demographic demo)
	{
		DemographicExt demoExt = demographicExtDao.getDemographicExt(demo.getId(), CANOPY_CUSTOMER_ID_KEY);
		if (demoExt != null && !demoExt.getValue().isEmpty())
		{
			return iceFallRESTService.getCustomerInformation(Integer.parseInt(demoExt.getValue()));
		}

		throw new IceFallNoSuchCustomerException("No Canopy Id for demographic [" + demo.getDemographicId() + "]");
	}


	/**
	 * locate the ice fall doctor id of the current provider
	 * @param doctorListTo1 - the list of ids to search
	 * @return - the id of the currently logged in provider
	 */
	protected Integer findDoctorId(Provider provider, IceFallDoctorListTo1 doctorListTo1)
	{
		for (IceFallDoctorTo1 doc : doctorListTo1.getResults())
		{
			if (
							doc.getFirstName().trim().equals(provider.getFirstName().trim()) &&
											doc.getLastName().trim().equals(provider.getLastName().trim()) &&
											doc.getEmail().trim().equals(provider.getEmail())
			)
			{
				return doc.getId();
			}
		}

		//TODO figure out the "authorize_bodystream@tweed.com" doctor thing.
		throw new RuntimeException("Could not find doctor! And authorize doctor is not configured");
	}

	/**
	 * get the eform as a base64 encoded pdf
	 * @param provider - the provider to print the eform under
	 * @param demographic - the demographic to whom the eform pertains
	 * @param templateId - the eforms template id
	 * @param eformValues - the eform values
	 * @param httpSchema - the http schema to use
	 * @param context - the context path to use
	 * @return - a base64 encoded PDF
	 */
	protected String getEformPDFDateForSubmission(Provider provider, Demographic demographic, Integer templateId, Map<String, String> eformValues, String httpSchema, String context, boolean isInstance)
	{
		Map<String,String> formOpenerMap = new HashMap<>();
		String subject = "Submitted to Ice Fall";
		String eformLink = "";

		EFormData eformData = null;
		if (isInstance)
		{
			eformData = eFormDataService.saveExistingEForm(templateId, demographic.getDemographicId(), Integer.parseInt(provider.getProviderNo()), subject, formOpenerMap, eformValues, eformLink);
		}
		else
		{
			eformData = eFormDataService.saveNewEForm(templateId, demographic.getDemographicId(), Integer.parseInt(provider.getProviderNo()), subject, formOpenerMap, eformValues, eformLink);
		}
		return new String(printToPDF(eformData.getId(), provider.getProviderNo(), httpSchema, context));
	}

	/**
	 * print an eform to pdf
	 * @param fdid - the instance id of the eform
	 * @param providerNo - the provider number that the form should be printed under
	 * @param httpSchema - the httpschema to use
	 * @param context - the context path of the oscar server.
	 * @return - a byte array of the eform data.
	 */
	protected byte[] printToPDF(Integer fdid, String providerNo, String httpSchema, String context)
	{
		String localUrl = WKHtmlToPdfUtils.getEformRequestUrl(providerNo,
						"", httpSchema, context);

		GenericFile tmpFile = null;
		try
		{
			tmpFile = FileFactory.createTempFile("pdf");
			WKHtmlToPdfUtils.convertToPdf(localUrl + fdid, tmpFile.getFileObject());
			return Base64.getEncoder().encode(IOUtils.toByteArray(tmpFile.asFileInputStream()));
		}
		catch (IOException | HtmlToPdfConversionException e)
		{
			throw new IceFallPdfGenerationException("Failed to convert eform [" + fdid + "] to PDF", e);
		}
		finally
		{
			if (tmpFile != null)
			{
				try
				{
					tmpFile.deleteFile();
				}
				catch (IOException e)
				{
					MiscUtils.getLogger().error("Failed to delete temp file [" + tmpFile.getName() + "] It may have been leaked!");
				}
			}
		}
	}


}
