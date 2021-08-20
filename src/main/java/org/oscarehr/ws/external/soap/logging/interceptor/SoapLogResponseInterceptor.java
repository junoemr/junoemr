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
import org.oscarehr.metrics.prometheus.service.SystemMetricsService;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;

import oscar.log.LogAction;

/**
 * Responsible for submitting response level data to the SOAPLogBuilder so that a single log entry can be created
 * for each request/response chain
 *
 * We extend AbstractLoggingInterceptor to get access to the handleFault methods
 */
public class SoapLogResponseInterceptor extends AbstractLoggingInterceptor {

	private static final Logger logger = Logger.getLogger(SoapLogResponseInterceptor.class);

	public SoapLogResponseInterceptor() {
		super(Phase.PRE_STREAM);
	}
	
	/**
	 * Intercept the SOAP response and extract data for logging.  Since the output steam can only be
	 * read once, replace the old stream with a new copy created from the original
	 */
	@Override
	public void handleMessage(Message message) throws Fault {
		
        final OutputStream outputStream = message.getContent(OutputStream.class);

		// message content has to be set to a new stream, as streams can only be read once
        if (outputStream != null) {
            final CacheAndWriteOutputStream outputCopy = new CacheAndWriteOutputStream(outputStream);
            if (threshold > 0) {
                outputCopy.setThreshold(threshold);
            }

            message.setContent(OutputStream.class, outputCopy);

            // Log only at the end of the response cycle (ie: after the output stream has been closed).  Since
			// will occur in the future, register a callback to trigger then.
            outputCopy.registerCallback(new SoapLogOutboundCallback(logger, message));
        }
	}

	/**
	 * Log SOAP Faults if they occur on the response side of the request/response exchange
	 *
	 * @param message SOAP request/response
	 */
	@Override
	public void handleFault(Message message) {
		SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().remove(SoapLogBuilder.class.getName());

		if (logData != null)
		{
			Exception ex = message.getContent(Exception.class);
			logger.error("SOAP response Fault", ex);

			// record request timing metrics
			SystemMetricsService userMetricsService = SpringUtils.getBean(SystemMetricsService.class);
			userMetricsService.recordSoapApiRequestLatency(logData.calculateElapsedTimeMilliSeconds());

			// log soap request
			logData.addErrorData(ex);
			LogAction.saveSoapLogEntry(logData.buildSoapLog());
		}
	}

	/**
	 * Stub method required due to extending AbstractLoggingInterceptor.
	 */
	@Override
	protected java.util.logging.Logger getLogger() {
		return new java.util.logging.Logger("SoapLogOutboundLogger", null)
		{
			// Stubbed anonymous class extending java.util.logging.Logger
		};
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

	/**
	 * Required to implement the CachedOutputStreamCallback interface.  We don't use it.
	 *
	 * @param outputStream
	 */
	public void onFlush(CachedOutputStream outputStream) { }

	/**
	 * Add response data to the SoapLogBuilder and log the complete request/response cycle after the response stream
	 * has closed.
	 *
	 * @param outputStream OutputStream containing the SOAP response
	 */
	public void onClose(CachedOutputStream outputStream) {

		SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().remove(SoapLogBuilder.class.getName());

		if (logData != null)
		{
			try
			{
				String response = getResponsePayload(outputStream);
				cacheResponseData(logData, response);
			}
			catch (IOException ex)
			{
				// We do not throw a fault here, as this is a logger problem retrieving response text,
				// and not necessarily a fault within the SOAP service.
				logger.error("SOAP response interceptor fault", ex);
				logData.addErrorData(ex);
			}
			finally
			{
				// record timing metrics
				SystemMetricsService userMetricsService = SpringUtils.getBean(SystemMetricsService.class);
				userMetricsService.recordSoapApiRequestLatency(logData.calculateElapsedTimeMilliSeconds());

				// log response to log_ws_soap
				LogAction.saveSoapLogEntry(logData.buildSoapLog());
			}
		}
		else
		{
			logger.error("Cannot find inbound log fragment");
		}
	}

	/**
	 * Add the response data to the SoapLogBuilder
	 *
	 * @param logData LogBuilder
	 * @param rawOutput raw response data text
	 */
	private void cacheResponseData(SoapLogBuilder logData, String rawOutput)
	{
		logData.addResponseData(rawOutput);
	}

	/**
	 * Retrieve the payload of the response output stream
	 *
	 * @param outputStream SOAP output stream
	 * @return raw text contents of the stream
	 * @throws IOException if the output stream cannot be opened
	 */
	private String getResponsePayload(CachedOutputStream outputStream) throws IOException
	{
		StringBuilder rawMessage = new StringBuilder();
		outputStream.writeCacheTo(rawMessage);

		return rawMessage.toString();
	}

}




