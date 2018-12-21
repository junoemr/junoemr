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

import java.io.IOException;
import java.io.OutputStream;

import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;

import oscar.log.LogAction;

/**
 * This class is responsible for intercepting and logging webservice responses from SOAP services.
 * Messages passed will be logged to the matching soap log entry created by the InInterceptor before being passed to the client.
 *
 * We extend AbstractLoggingInterceptor to get access to the handleFault methods // TODO: Now that it's a feature can probably just extend FaultHandler?
 *
 * This class pairs with the SoapLogMethodInterceptor to log the full webservice post and response.
 */
public class SoapLogResponseInterceptor extends AbstractLoggingInterceptor {
	
	private static final Logger logger = Logger.getLogger(SoapLogResponseInterceptor.class);

	public SoapLogResponseInterceptor() {
		super(Phase.PRE_STREAM);
	}
	
	/**
	 * This method accepts the outgoing webservice response as a Message object
	 * The message object contents depend on the Phase and the actions of any preceding interceptors
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		
        final OutputStream outputStream = message.getContent(OutputStream.class);
        // Write the output while caching it for the log message
        if (outputStream != null) {
            final CacheAndWriteOutputStream outputCopy = new CacheAndWriteOutputStream(outputStream);
            if (threshold > 0) {
                outputCopy.setThreshold(threshold);
            }
            // message content has to be set to a new stream, as streams can only be read once
            message.setContent(OutputStream.class, outputCopy);
            // use the callback to perform log once the stream closes
            outputCopy.registerCallback(new SoapLogOutboundCallback(logger, message));
        }
	}

	@Override
	public void handleFault(Message message) {
		SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().get(SoapLogBuilder.class.getName());

		if (logData != null)
		{
			Exception ex = message.getContent(Exception.class);
			logger.error("SOAP response Fault", ex);

			logData.addErrorData(ex);
			LogAction.saveSoapLogEntry(logData.buildSoapLog());
		}
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

    
    protected SoapLogOutboundCallback(Logger logger, Message message) {
        this.logger = logger;
        this.message = message;
    }
	
	public void onFlush(CachedOutputStream outputStream) {

	}

	public void onClose(CachedOutputStream outputStream) {

		SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().get(SoapLogBuilder.class.getName());

		if (logData != null)
		{
			try
			{
				String response = getResponsePayload(outputStream);
				cacheResponseData(logData, response);
			}
			catch (IOException ex)
			{
				logger.error("SOAP response interceptor fault", ex);
				logData.addErrorData(ex);
			}
			finally
			{
				LogAction.saveSoapLogEntry(logData.buildSoapLog());
			}
		}
		else
		{
			logger.error("Cannot find inbound log fragment");
		}
	}

	private void cacheResponseData(SoapLogBuilder logData, String rawOutput)
	{
		logData.addResponseData(rawOutput);
	}

	private String getResponsePayload(CachedOutputStream outputStream) throws IOException
	{
		StringBuilder rawMessage = new StringBuilder();
		outputStream.writeCacheTo(rawMessage);

		return rawMessage.toString();
	}

}




