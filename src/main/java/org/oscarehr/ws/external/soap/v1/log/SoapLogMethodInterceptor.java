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

package org.oscarehr.ws.external.soap.v1.log;

import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.utils.AnnotationUtils;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.log4j.Logger;
import org.oscarehr.ws.common.MaskParameter;
import org.oscarehr.ws.common.SkipContentLogging;
import org.oscarehr.ws.external.soap.v1.log.model.SoapServiceLog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import oscar.log.LogAction;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Date;


// TODO:  This should just extract the method and pass it to the outbound interceptor
/**
 * This class is responsible for logging at the service/method level of a SOAP request.
 *
 * Together with SoapLogMessageBodyInterceptor and SoapLogOutboundInterceptor, these three interceptors form a suite which is
 * able to log the full webservice request and response.
 */
public class SoapLogMethodInterceptor extends AbstractLoggingInterceptor {

	private static Logger logger = Logger.getLogger(SoapLogMethodInterceptor.class);

	public SoapLogMethodInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	/**
	 * This method accepts the incoming webservice call as a Message object
	 * The message object contents depend on the Phase and the actions of any preceding interceptors
	 *
	 * @param message Soap message to be logged
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		buildInboundEntry(message);
	}

	/**
	 * Log an error message if something goes wrong.  The error message is a Java exception and not a Soap fault
	 *
	 * @param message Soap message encountering an Exception.
	 */
	@Override
	public void handleFault(Message message)
	{
		Exception e = message.getContent(Exception.class);

		logger.error("Incoming SOAP logging interceptor Fault", e);
		
		// ensure we log something if the fault occurs after the interceptor stores the incoming data
		SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapServiceLog.class.getName());

		if(soapLog != null) {
			Date createdAt = soapLog.getCreatedAt();
			long duration  = new Date().getTime() - createdAt.getTime();
			
			soapLog.setDuration(duration);
			String errorMessage = e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage());
			soapLog.setErrorMessage(errorMessage);
			
			LogAction.saveSoapLogEntry(soapLog);
		}
	}


	/**
	 * Populates the inbound component of the logging entry
	 *
	 * @param message  The soap message to log
	 */
	private void buildInboundEntry(Message message)
	{
		SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapServiceLog.class.getName());

		Method soapMethod = getMessageTargetMethod(message);
		soapLog.setSoapMethod(soapMethod.getName());

		String postData = (String) message.getExchange().remove(SoapLogMessageBodyInterceptor.class.getName());

		if (!skipLoggingContent(soapMethod))
		{
			applyParameterMasking(soapMethod, postData);
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
	 * Apply parameter masking (if any) to the raw message to sanitize sensitive fields prior to logging.
	 *
	 * @param method Webservice method which may be annotated
	 * @param postData The raw SOAP message which was marshalled into the webservice.
	 */
	private void applyParameterMasking(Method method, String postData)
	{
		MaskParameter maskParameters = AnnotationUtils.getMethodAnnotation(method, MaskParameter.class);

		if (maskParameters != null)
		{
			try
			{
				String[] fieldNames = maskParameters.name();

				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = dbFactory.newDocumentBuilder();

				InputSource is = new InputSource(new StringReader(postData));
				Document doc = builder.parse(is);
				doc.normalizeDocument();

				for (int i = 0; i < fieldNames.length; i++)
				{
					String toFilter = fieldNames[i];
					NodeList nodes = doc.getElementsByTagName(toFilter);
						for (int j=0; i < nodes.getLength(); i++)
						{
							Node node = nodes.item(j);
							Element elem = (Element) node;
							elem.getElementsByTagName(toFilter).item(0).setTextContent(MaskParameter.MASK);
						}
				}
			}
			catch (Exception e)
			{
				// TODO: Hmmm...
			}
		}
	}

	/**
	 * We don't use this, but it is a required method for any logging interceptor
	 */
	@Override
	protected java.util.logging.Logger getLogger() {
		return new SoapLogOutboundLogger("SoapLogInboundLogger", null);
	}

}

/**
 * Dummy Logger, Needed for the getLogger method we aren't using
 */
class SoapLogInboundLogger extends java.util.logging.Logger {

	protected SoapLogInboundLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}
}

