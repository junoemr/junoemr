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

package org.oscarehr.ws.external.soap.logging.interceptor;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.common.MaskParameter;
import org.oscarehr.ws.common.SkipContentLogging;
import org.oscarehr.ws.external.soap.logging.model.SoapServiceLog;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class is responsible for logging at the service/method level of a SOAP request.
 *
 * Together with SoapLogHTTPInterceptor and SoapLogResponseInterceptor, these three interceptors form a suite which is
 * able to log the full webservice request and response.
 */
public class SoapLogMethodInterceptor extends AbstractSoapInterceptor
{
	private static Logger logger = Logger.getLogger(SoapLogMethodInterceptor.class);

	public SoapLogMethodInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	/**
	 *  Add authentication and SOAP service information to the SOAP request/response log
	 *
	 * @param message Soap message to be logged
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault
	{
		SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapServiceLog.class.getName());
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromRequest(request);

		// Log the provider no here, because the transport level interceptor is processed before authentication
		// This could also go in the authentication interceptor
		if (info != null)
		{
			String providerNo = info.getLoggedInProviderNo();
			soapLog.setProviderNo(providerNo);
		}

		Method soapMethod = getMessageTargetMethod(message);
		soapLog.setSoapMethod(soapMethod.getName());

		String postData = (String) message.getExchange().remove(SoapLogHTTPInterceptor.class.getName());

		if (!skipLoggingContent(soapMethod))
		{
			postData = applyParameterMasking(soapMethod, postData);
			soapLog.setRawPost(postData);
		}
	}

	/**
	 *	Get the java method which is invoked by the soap message
	 *
	 * @param message:  The soap message
	 * @return the Method
	 */
	private Method getMessageTargetMethod(Message message)
	{
		Exchange exchange = message.getExchange();
		BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);
		MethodDispatcher dispatcher = (MethodDispatcher) exchange.get(Service.class).get(MethodDispatcher.class.getName());

		return dispatcher.getMethod(bop);
	}

	/**
	 * Determines if the method supplied is annotated with the SkipContentLogging annotation
	 *
	 * @param method the method to examine
	 * @return true if the method is annotated with SkipContentLogging
	 */
	private boolean skipLoggingContent(Method method)
	{
		SkipContentLogging skipLogging = AnnotationUtils.getMethodAnnotation(method, SkipContentLogging.class);

		return skipLogging != null;
	}

	/**
	 * Apply parameter masking (if any) to the raw message to sanitize any specified XML element values in the SOAP message,
	 * replacing the contents with asterisks.  Uses a SAX parser to handle the XML message.
	 *
	 * @param method Webservice method which may be annotated
	 * @param postData The raw SOAP message.
	 *
	 * @return postData with any specified parameter masking applied
	 */
	private String applyParameterMasking(Method method, String postData)
	{
		MaskParameter maskParameters = AnnotationUtils.getMethodAnnotation(method, MaskParameter.class);

		if (maskParameters != null)
		{
			try
			{
				String[] fieldNames = maskParameters.name();
				HashSet<String> fieldSet = new HashSet<>(Arrays.asList(fieldNames));

				XMLReader xr = new XMLFilterImpl(XMLReaderFactory.createXMLReader()) {
					boolean shouldMask = false;

					@Override
					public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
					{
						if (fieldSet.contains(qName))
						{
							shouldMask = true;
						}

						super.startElement(uri, localName, qName, atts);
					}

					@Override
					public void endElement(String uri, String localName, String qName) throws SAXException {
						if (fieldSet.contains(qName) && shouldMask)
						{
							shouldMask = false;
						}
						super.endElement(uri, localName, qName);
					}

					@Override
					public void characters(char[] ch, int start, int length) throws SAXException {
						if (shouldMask)
						{
							ch = MaskParameter.MASK.toCharArray();
							start = 0;
							length = ch.length;
						}

						super.characters(ch, start, length);
					}
				};

				Source src = new SAXSource(xr, new InputSource(new StringReader(postData)));
				StringWriter writer = new StringWriter();
				Result res = new StreamResult(writer);
				TransformerFactory.newInstance().newTransformer().transform(src, res);

				postData = writer.toString();
			}
			catch (Exception e)
			{
				throw new Fault(e);
			}
		}

		return postData;
	}
}

