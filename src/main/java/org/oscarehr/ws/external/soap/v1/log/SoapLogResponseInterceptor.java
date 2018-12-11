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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.oscarehr.ws.external.soap.v1.log.model.SoapServiceLog;
import oscar.log.LogAction;

/**
 * This class is responsible for intercepting and logging webservice responses from SOAP services.
 * Messages passed will be logged to the matching soap log entry created by the InInterceptor before being passed to the client
 *
 * This class pairs with the SoapLogMethodInterceptor to log the full webservice post and response.
 */
public class SoapLogResponseInterceptor extends AbstractLoggingInterceptor {
	
	private static Logger logger = Logger.getLogger(SoapLogResponseInterceptor.class);

	public SoapLogResponseInterceptor() {
		super(Phase.PRE_STREAM);
	}
	
	/**
	 * This method accepts the outgoing webservice response as a Message object
	 * The message object contents depend on the Phase and the actions of any preceding interceptors
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		
        final OutputStream os = message.getContent(OutputStream.class);
        // Write the output while caching it for the log message
        if (os != null) {
            final CacheAndWriteOutputStream newOut = new CacheAndWriteOutputStream(os);
            if (threshold > 0) {
                newOut.setThreshold(threshold);
            }
            // message content has to be set to a new stream, as streams can only be read once
            message.setContent(OutputStream.class, newOut);
            // use the callback to perform log once the stream closes
            newOut.registerCallback(new SoapLogOutboundCallback(logger, message, os));
        }
	}
	@Override
	public void handleFault(Message message) {
		Exception e = message.getContent(Exception.class);
		logger.error("Outgoing log Fault", e);
	}

	/**
	 * We don't use this, but it is a required method for any log log
	 */
	@Override
	protected java.util.logging.Logger getLogger() {
		return new SoapLogOutboundLogger("SoapLogOutboundLogger", null);
	}
}

/**
 * Dummy Logger, Needed for the getLogger method we aren't using
 */
class SoapLogOutboundLogger extends java.util.logging.Logger {

	protected SoapLogOutboundLogger(String name, String resourceBundleName) {
		super(name, resourceBundleName);
	}
}

/**
 * Customized implementation of the CachedOutputStreamCallback
 * This allows the log to only log data after the output stream is closed.
 */
class SoapLogOutboundCallback implements CachedOutputStreamCallback {

	private final Logger logger;
    private final Message message;
    private final OutputStream origStream;

    
    public SoapLogOutboundCallback(Logger logger, Message message, OutputStream os) {
        this.logger = logger;
        this.message = message;
        this.origStream = os;
    }
	
	public void onFlush(CachedOutputStream cos) {
	}

	public void onClose(CachedOutputStream cos) {
				
		try
		{
			String messageBody = extractRawMessageBody(cos);
			SoapServiceLog soapLog = buildOutboundMessage(message, messageBody);
			LogAction.saveSoapLogEntry(soapLog);
		}
		catch (Exception e)
		{
			logger.error("SOAP response interceptor fault", e);
		}
	}

	private String extractRawMessageBody(CachedOutputStream cos) throws IOException
	{
		StringBuilder builder = new StringBuilder();
		cos.writeCacheTo(builder);

		return builder.toString();
	}
	
	private SoapServiceLog buildOutboundMessage(Message message, String messageBody) {

		SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapServiceLog.class.getName());

		if(soapLog != null) {
			Date createdAt = soapLog.getCreatedAt();
			long duration  = new Date().getTime() - createdAt.getTime();
			
			soapLog.setDuration(duration);
			soapLog.setRawOutput(messageBody);
		}
		else {
			logger.error("Soap logging outbound interceptor cannot find inbound log fragment");
		}

		return soapLog;
	}
}




