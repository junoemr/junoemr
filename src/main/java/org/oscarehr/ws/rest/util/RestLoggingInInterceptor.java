/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.rest.util;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.RestServiceLog;
import org.oscarehr.util.LoggedInInfo;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * This class is responsible for intercepting and logging webservice calls to REST services.
 * Messages passed will be logged to the rest log before being handled by the regular endpoints
 * This class pairs with the RestLoggingOutInterceptor to log the full webservice post and response.
 * @author robert
 */
public class RestLoggingInInterceptor extends AbstractLoggingInterceptor {
	
	private static Logger logger = Logger.getLogger(RestLoggingInInterceptor.class);
	
	public RestLoggingInInterceptor() {
		super(Phase.RECEIVE);
	}

	/**
	 * This method accepts the incoming webservice call as a Message object
	 * The message object contents depend on the Phase and the actions of any preceding interceptors
	 * @param message
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
			logger.debug("REST LOGGING IN:\n" + postData);
			os.close();
			
			addNewLogEntry(message, postData);
		}
		catch (IOException e) {
			logger.error("IO Error in incoming REST Interceptor", e);
		}
	}
	@Override
	public void handleFault(Message message)
	{
		Exception e = message.getContent(Exception.class);

		logger.error("Incoming Interceptor Fault", e);
		
		// ensure we log something if the fault occurs after the interceptor stores the incoming data
		RestServiceLog restLog = (RestServiceLog)message.getExchange().get(RestLoggingInInterceptor.class.getName());
		if(restLog != null) {
			Date createdAt = restLog.getCreatedAt();
			long duration  = new Date().getTime() - createdAt.getTime();
			
			restLog.setDuration(duration);
			String errorMessage = e.getClass().getSimpleName() + ": " + String.valueOf(e.getMessage());
			restLog.setErrorMessage(errorMessage);
			
			LogAction.saveRestLogEntry(restLog);
		}
	}

	private void addNewLogEntry(Message message, String postData) {
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

		String userAgent = request.getHeader("User-Agent");
		String url = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		
		RestServiceLog restLog = new RestServiceLog();
		
		LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = info.getLoggedInProviderNo();

		restLog.setProviderNo(providerNo);
		restLog.setDuration(0L);
		restLog.setIp(request.getRemoteAddr());
		restLog.setUserAgent(userAgent);
		restLog.setUrl(url);
		restLog.setRawQueryString(queryString);
		restLog.setRawPost(postData);
		restLog.setRawOutput(null);
		// save the entry object to the exchange so it can be accessed by the loggingOutInterceptor
		message.getExchange().put(RestLoggingInInterceptor.class.getName(), restLog);
	}

	/**
	 * We don't use this, but it is a required method for any logging interceptor
	 */
	@Override
	protected java.util.logging.Logger getLogger() {
		return new RestLoggingOutLogger("RestLoggingInLogger", null);
	}
}
/**
 * Dummy Logger, Needed for the getLogger method we aren't using
 */
class RestLoggingInLogger extends java.util.logging.Logger {

	protected RestLoggingInLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}
}

