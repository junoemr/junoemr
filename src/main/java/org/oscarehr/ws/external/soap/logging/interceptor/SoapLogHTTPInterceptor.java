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

import org.apache.commons.io.IOUtils;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.metrics.prometheus.service.SystemMetricsService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is an upstream inbound logger responsible for logging information about SOAP requests at
 * the transport (HTTP) level.  This class extends AbstractLoggingInterceptor for access to the handleFault method.
 */
public class SoapLogHTTPInterceptor extends AbstractLoggingInterceptor
{
    static final Logger logger = MiscUtils.getLogger();

    public SoapLogHTTPInterceptor()
    {
        super(Phase.RECEIVE);
    }

    /**
     * If the request is a GET for the WSDL, then build the log and persist it, as the message will not propagate down
     * far enough to register with the response interceptor.  Otherwise, register transport data with the logBuilder.
     *
     * @param message SOAP request/response
     * @throws Fault If there is a problem reading the payload of the message
     */
    @Override
    public void handleMessage(Message message) throws Fault
    {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        SoapLogBuilder logData = new SoapLogBuilder();
        cacheTransportData(logData, message);

        if (request.getMethod().equals(HttpMethod.GET))
        {
            // These are requests for the WSDL.  Log them at this level because they don't propagate down
            // far enough to hit (in) PRE_LOGICAL or (out) PRE_STREAM level interceptors

            LogAction.saveSoapLogEntry(logData.buildSoapLog());
        }
        else
        {
            message.getExchange().put(SoapLogBuilder.class.getName(), logData);
        }
    }

    /**
     * Extract transport level data from the Message and add it to a LogBuilder.  Since the input stream can only
     * be read once, this method reads the stream, then attaches a new stream containing the contents of the old stream
     * to the Message.
     *
     * @param logData logBuilder to attach the transport level data to
     * @param message SOAP request/response
     * @throws Fault if the input stream cannot be read, or the output stream cannot be instantitated properly
     */
    private void cacheTransportData(SoapLogBuilder logData, Message message) throws Fault
    {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        StringBuilder rawPostData = new StringBuilder();
        try
        {
            InputStream inStream = message.getContent(InputStream.class);
            CachedOutputStream outStream = new CachedOutputStream();

            IOUtils.copy(inStream, outStream);

            outStream.flush();
            inStream.close();

            message.setContent(InputStream.class, outStream.getInputStream());
            rawPostData.append(IOUtils.toString(outStream.getInputStream()));
            outStream.close();
            logData.addTransportData(request, rawPostData.toString());
        }
        catch (IOException | NullPointerException ex) // NullPointerException can be thrown if the inStream can't be found
        {
            final String emptyPostBody = "";
            // Try and salvage some of the transport data
            logData.addTransportData(request, emptyPostBody);

            logger.error("Unable to retrieve raw soap message");
            throw new Fault(ex);
        }
    }

    /**
     * Log all SOAP faults arising from the inbound request if something goes wrong.
     *
     * Since interceptors are unwound backwards when a Fault is thrown, the handle fault method of this
     * class will be called last.
     *
     * @param message Inbound SOAP request
     */
    @Override
    public void handleFault(Message message)
    {
        Exception ex = message.getContent(Exception.class);

        logger.error("SOAP request fault", ex);
        SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().remove(SoapLogBuilder.class.getName());

        if (logData != null)
        {
            // record request timing metrics
            SystemMetricsService userMetricsService = SpringUtils.getBean(SystemMetricsService.class);
            userMetricsService.recordSoapApiRequestLatency(logData.calculateElapsedTimeMilliSeconds());

            // log soap request
            logData.addErrorData(ex);
            LogAction.saveSoapLogEntry(logData.buildSoapLog());
        }
    }

    /**
     * This is a stub method needed because we extend AbstractLogginginterceptor.
     */
    @Override
    protected java.util.logging.Logger getLogger()
    {
        return new java.util.logging.Logger("SoapLogInboundLogger", null)
        {
            // Anonymous inner class extending java.util.logging.Logger
        };
    }
}
