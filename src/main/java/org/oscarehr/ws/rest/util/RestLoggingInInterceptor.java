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

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.RestServiceLog;
import org.oscarehr.util.LoggedInInfo;

import oscar.log.LogAction;

public class RestLoggingInInterceptor extends AbstractPhaseInterceptor<Message> {
	
	private static Logger logger = Logger.getLogger(RestLoggingInInterceptor.class);
	
	public RestLoggingInInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		logger.info("TEST IN INTERCEPTOR MESSAGE");
		
//		OutputStream os = message.getContent ( OutputStream.class );
//		if(os != null) {
//	        CacheAndWriteOutputStream cwos = new CacheAndWriteOutputStream ( os);
//	        message.setContent ( OutputStream.class, cwos );
//	
//			cwos.registerCallback(new LoggingInCallBack(message, os));
//		}
		
		
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
		String restMessage = String.valueOf(message.get(Message.REST_MESSAGE));
		String wsInterface = String.valueOf(message.get(Message.ENDPOINT_ADDRESS));
		String userAgent = request.getHeader("User-Agent");
		String url = request.getRequestURL().toString();
		String queryString = request.getQueryString();
		
		RestServiceLog restLog = new RestServiceLog();
		
		String postData = null;
		
//		postData = getRawMessageData(message);

		LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = info.getLoggedInProviderNo();

		restLog.setProviderNo(providerNo);
		restLog.setDuration(0L);
		restLog.setIp(request.getRemoteAddr());
		restLog.setUserAgent(userAgent);
		restLog.setServiceType(wsInterface);
		restLog.setMethodName(restMessage);
		restLog.setUrl(url);
		restLog.setRawQueryString(queryString);
		restLog.setRawPost(postData);
		restLog.setRawOutput(null);
		
		LogAction.saveRestLogEntry(restLog);
		
		message.getExchange().put("org.oscarehr.ws.rest.util.RestLoggingInInterceptor", restLog);
	}

//	@Override
//	protected java.util.logging.Logger getLogger() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
    /*private LoggingMessage setupBuffer(Message message) {
        String id = (String)message.getExchange().get(LoggingMessage.ID_KEY);
        if (id == null) {
            id = LoggingMessage.nextId();
            message.getExchange().put(LoggingMessage.ID_KEY, id);
        }
        final LoggingMessage buffer 
            = new LoggingMessage("Outbound Message\n---------------------------",
                                 id);
        
        Integer responseCode = (Integer)message.get(Message.RESPONSE_CODE);
        if (responseCode != null) {
            buffer.getResponseCode().append(responseCode);
        }
        
        String encoding = (String)message.get(Message.ENCODING);
        if (encoding != null) {
            buffer.getEncoding().append(encoding);
        }            
        String httpMethod = (String)message.get(Message.HTTP_REQUEST_METHOD);
        if (httpMethod != null) {
            buffer.getHttpMethod().append(httpMethod);
        }
        String address = (String)message.get(Message.ENDPOINT_ADDRESS);
        if (address != null) {
            buffer.getAddress().append(address);
        }
        String ct = (String)message.get(Message.CONTENT_TYPE);
        if (ct != null) {
            buffer.getContentType().append(ct);
        }
        Object headers = message.get(Message.PROTOCOL_HEADERS);
        if (headers != null) {
            buffer.getHeader().append(headers);
        }
        return buffer;
    }
    
    
	private String getRawMessageData(Message message) {
		String messageDataString = null;
		
		LoggingMessage buffer = setupBuffer(message);

		final Writer iowriter = message.getContent(Writer.class);
		StringWriter out = new StringWriter();
        if (!(iowriter instanceof StringWriter)) {
            out = new StringWriter();
        }
		
		String ct = (String) message.get(Message.CONTENT_TYPE);
		try {
			messageDataString = writePayload(buffer.getPayload(), out, ct);
		}
		catch (Exception ex) {
			logger.error("Error parsing Message Data", ex);
			// ignore
		}
		return messageDataString;
	}
	protected String writePayload(StringBuilder builder, StringWriter stringWriter, String contentType) throws Exception {

		StringBuffer buffer = stringWriter.getBuffer();
		builder.append(buffer);
		return builder.toString();
	}*/
}

class LoggingInCallBack implements CachedOutputStreamCallback {
	
	private static Logger logger = Logger.getLogger(LoggingInCallBack.class);
    private final Message message;
    private final OutputStream origStream;
	
    public LoggingInCallBack(final Message msg, final OutputStream os) {
        this.message = msg;
        this.origStream = os;
    }
	
	@Override
	public void onClose(CachedOutputStream cos) {
		try {
			if (cos != null) {
				String data = IOUtils.toString(cos.getInputStream());
				addNewLogEntry(message, data);
				//TODO
			}

		}
		catch (Exception e) {
			logger.error("Error parsing Message Data", e);
		}
	}

	@Override
	public void onFlush(CachedOutputStream os) {
		// TODO Auto-generated method stub

	}
	
	private void addNewLogEntry(Message message, String postData) {
		HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
		
//		String restMessage = String.valueOf(message.get(Message.REST_MESSAGE));
//		String wsInterface = String.valueOf(message.get(Message.ENDPOINT_ADDRESS));
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
		restLog.setServiceType(null);
		restLog.setMethodName(null);
		restLog.setUrl(url);
		restLog.setRawQueryString(queryString);
		restLog.setRawPost(postData);
		restLog.setRawOutput(null);
		
		LogAction.saveRestLogEntry(restLog);
		message.getExchange().put("org.oscarehr.ws.rest.util.RestLoggingInInterceptor", restLog);
	}
}

