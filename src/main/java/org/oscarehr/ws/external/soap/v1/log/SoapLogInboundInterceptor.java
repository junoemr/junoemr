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

import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
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
import org.oscarehr.ws.common.SkipContentLogging;
import org.oscarehr.ws.external.soap.v1.log.model.SoapServiceLog;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * This class is responsible for intercepting and logging webservice calls to SOAP webservices.
 * Messages are logged prior to being passed to the webservice method.
 * This class pairs with the SoapLogOutboundInterceptor to log the full webservice post and response.
 */
public class SoapLogInboundInterceptor extends AbstractLoggingInterceptor {
	
	private static Logger logger = Logger.getLogger(SoapLogInboundInterceptor.class);
	
	public SoapLogInboundInterceptor() {
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
		// now get the request body
		InputStream is = message.getContent(InputStream.class);
		CachedOutputStream os = new CachedOutputStream();
		try {
			IOUtils.copy(is, os);
			os.flush();
			
			message.setContent(InputStream.class, os.getInputStream());
			is.close();

			String postData = IOUtils.toString(os.getInputStream());
			os.close();

			attachInboundLogEntry(message, postData);
		}
		catch (IOException e) {
			logger.error("IO Error in incoming SOAP logging interceptor", e);
		}
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
		SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapLogInboundInterceptor.class.getName());
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
	 *  Store the inbound part of the exchange in the message itself.  When it comes time to log the outbound response,
	 *  we will combine it with the outbound log component to form a single log entry.
	 */
	private void attachInboundLogEntry(Message message, String postData)
	{
		SoapServiceLog soapLog = buildInboundEntry(message, postData);
		message.getExchange().put(SoapLogInboundInterceptor.class.getName(), soapLog);
	}

	/**
	 * Populates the inbound component of the logging entry
	 *
	 * @param message  The soap message to log
	 * @param postData The raw soap body, including the envelope
	 * @return A soap service log with the inbound portion filled out.  The outbound fields will still be null.
	 */
	private SoapServiceLog buildInboundEntry(Message message, String postData)
	{
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		String url = request.getRequestURL().toString();

		SoapServiceLog soapLog = new SoapServiceLog();

		LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromRequest(request);

		if (info != null)
		{
			String providerNo = info.getLoggedInProviderNo();
			soapLog.setProviderNo(providerNo);
		}

		soapLog.setDuration(0L);
		soapLog.setIp(request.getRemoteAddr());
		soapLog.setUrl(url);
		soapLog.setRawOutput(null);

		Method soapMethod = getMessageTargetMethod(message);

		if (shouldLogContent(soapMethod))
		{
			soapLog.setRawPost(postData);
		}

		return soapLog;
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
	private boolean shouldLogContent(Method method)
	{
		Annotation skipLogging = AnnotationUtils.getMethodAnnotation(method, SkipContentLogging.class);

		return skipLogging != null;
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

