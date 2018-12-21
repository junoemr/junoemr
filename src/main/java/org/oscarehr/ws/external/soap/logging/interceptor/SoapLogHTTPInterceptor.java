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
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is an upstream inbound logger responsible for logging information about SOAP requests at
 * the transport (HTTP) level.
 */
public class SoapLogHTTPInterceptor extends AbstractLoggingInterceptor
{
    static final Logger logger = MiscUtils.getLogger();

    public SoapLogHTTPInterceptor()
    {
        super(Phase.RECEIVE);
    }

    /**
     * Create a log entry for the SOAP request/response exchange.  If the request is an HTTP GET, then
     * only the request portion will be logged.  The log entry is attached to the message and allowed to propgate
     * out to the response level interceptors where it will be combined with response information and
     * persisted as a single entry for the complete exchange.
     *
     * @param message Abstract message representing the request
     * @throws Fault If something goes wrong with the SOAP request
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
     * Build the HTTP transport level component of the SOAP log
     *
     * @param message Inbound SOAP request
     * @return A log object with the transport level fields initialized.
     */
    private void cacheTransportData(SoapLogBuilder logData, Message message)
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
        }
        catch (IOException | NullPointerException ex) // NullPointerException can be thrown if the inStream can't be found
        {
            logger.error("Unable to retrieve raw soap message");
            logData.addErrorData(ex);
        }
        finally
        {
            logData.addTransportData(request, rawPostData.toString());
        }
    }

    /**
     * Log all SOAP faults arising from the inbound request if something goes wrong.
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
            logData.addErrorData(ex);
            LogAction.saveSoapLogEntry(logData.buildSoapLog());
        }
    }

    /**
     * We don't use this, but it is a required method for any logging interceptor
     */
    @Override
    protected java.util.logging.Logger getLogger()
    {
        return new SoapLogOutboundLogger("SoapLogInboundLogger", null);
    }


    /**
     * Dummy Logger, Needed for the getLogger method we aren't using
     */
    class SoapLogInboundLogger extends java.util.logging.Logger
    {
        protected SoapLogInboundLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
        }
    }
}
