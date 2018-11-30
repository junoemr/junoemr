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

import org.apache.commons.io.IOUtils;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.log.model.SoapServiceLog;
import oscar.log.LogAction;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is an upstream inbound logger responsible for logging information about the HTTP
 * component of the SOAP protocol.
 */
public class SoapLogMessageBodyInterceptor extends AbstractSoapInterceptor
{
    static final Logger logger = MiscUtils.getLogger();

    public SoapLogMessageBodyInterceptor()
    {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault
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

    private SoapServiceLog buildServiceLog(Message message)
    {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        SoapServiceLog log = new SoapServiceLog();
        log.setIp(request.getRemoteAddr());
        log.setUrl(request.getRequestURL().toString());
        log.setDuration(0L);
        log.setHttpMethod(request.getMethod());

        LoggedInInfo info = LoggedInInfo.getLoggedInInfoFromRequest(request);

        if (info != null)
        {
            String providerNo = info.getLoggedInProviderNo();
            log.setProviderNo(providerNo);
        }

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
                message.getExchange().put(SoapLogMessageBodyInterceptor.class.getName(), soapMessage);
            }
            catch (IOException e)
            {
                logger.error("Error retrieving message body data for SOAP ws og");
                log.setErrorMessage(e.getMessage());
            }
        }

        return log;
    }
}
