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

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.RestServiceLog;

import oscar.log.LogAction;

public class RestLoggingOutInterceptor extends AbstractPhaseInterceptor<Message> {
	
	private static Logger logger = Logger.getLogger(RestLoggingOutInterceptor.class);


	public RestLoggingOutInterceptor() {
		super(Phase.PRE_STREAM);
		addAfter(LoggingOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		logger.info("TEST OUT INTERCEPTOR MESSAGE");
		
		/*final OutputStream os = message.getContent(OutputStream.class);

        if (os != null && !(os instanceof CachedOutputStream) ) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, newOut);
        }*/
		
		
		
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		RestServiceLog restLog = (RestServiceLog)message.getExchange().get("org.oscarehr.ws.rest.util.RestLoggingInInterceptor");
		
		restLog.setDuration(1L);//TODO
		String returnData = null;
		restLog.setRawOutput(returnData);
		
		
		LogAction.updateRestLogEntry(restLog);
		
	}
}
