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
import org.oscarehr.ws.external.soap.logging.model.SoapServiceLog;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

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
        SoapServiceLog log = buildServiceLog(message);

        if (request.getMethod().equals(HttpMethod.GET))
        {
            // These are requests for the WSDL.  Log them at this level because they don't propagate down
            // far enough to hit (in) PRE_LOGICAL or (out) PRE_STREAM level interceptors

            LogAction.saveSoapLogEntry(log);
        }
        else
        {
            message.getExchange().put(SoapServiceLog.class.getName(), log);
        }
    }

    /**
     * Build the HTTP transport level component of the SOAP log
     *
     * @param message Inbound SOAP request
     * @return A log object with the transport level fields initialized.
     */
    private SoapServiceLog buildServiceLog(Message message)
    {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        SoapServiceLog log = new SoapServiceLog();
        log.setIp(request.getRemoteAddr());
        log.setUrl(request.getRequestURL().toString());
        log.setDuration(0L);
        log.setHttpMethod(request.getMethod());

        InputStream inStream = message.getContent(InputStream.class);
        if (inStream != null) {
            CachedOutputStream outStream = new CachedOutputStream();
            try
            {
                IOUtils.copy(inStream, outStream);

                outStream.flush();
                inStream.close();

                message.setContent(InputStream.class, outStream.getInputStream());
                String soapMessage = IOUtils.toString(outStream.getInputStream());
                outStream.close();

                // Store raw xml body in the message instead of the log at this point.  We'll deal with it at the PRE_STREAM
                // level to determine if it's needed or not.  This avoids ever having the soap log entry in an invalid
                // state (eg:  adding it now, then to removing it later if a method-level annotation determines that
                // content logging should be skipped).
                message.getExchange().put(SoapLogHTTPInterceptor.class.getName(), soapMessage);
            }
            catch (IOException e)
            {
                logger.error("Error retrieving message body data for SOAP ws og");
                log.setErrorMessage(e.getMessage());
            }
        }

        return log;
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
        Exception e = message.getContent(Exception.class);

        logger.error("SOAP request fault", e);

        // ensure we log something if the fault occurs after the interceptor stores the incoming data
        SoapServiceLog soapLog = (SoapServiceLog) message.getExchange().get(SoapServiceLog.class.getName());

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
     * We don't use this, but it is a required method for any logging interceptor
     */
    @Override
    protected java.util.logging.Logger getLogger() {
        return new SoapLogOutboundLogger("SoapLogInboundLogger", null);
    }


    /**
     * Dummy Logger, Needed for the getLogger method we aren't using
     */
    class SoapLogInboundLogger extends java.util.logging.Logger {

        protected SoapLogInboundLogger(String name, String resourceBundleName) {
            super(name, resourceBundleName);
        }
    }
}
