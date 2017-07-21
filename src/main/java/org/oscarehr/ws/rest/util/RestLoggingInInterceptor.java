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

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

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

public class RestLoggingInInterceptor extends AbstractLoggingInterceptor {
	
	private static Logger logger = Logger.getLogger(RestLoggingInInterceptor.class);
	
	public RestLoggingInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {				
		try {
			logger.info("REST LOGGING IN RestLoggingInInterceptor!!!");
			// now get the request body
			InputStream is = message.getContent(InputStream.class);
			CachedOutputStream os = new CachedOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			message.setContent(InputStream.class, os.getInputStream());
			is.close();

			String postData = IOUtils.toString(os.getInputStream());
			logger.info("The request is:\n" + postData);
			os.close();
			
			addNewLogEntry(message, postData);
		}

		catch (Exception e) {
			logger.error("Error in incoming REST Interceptor", e);
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
		
		LogAction.saveRestLogEntry(restLog);
		message.getExchange().put("org.oscarehr.ws.rest.util.RestLoggingInInterceptor", restLog);
	}

	@Override
	protected java.util.logging.Logger getLogger() {
		return null;
	}
}

