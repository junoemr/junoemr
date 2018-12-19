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

package org.oscarehr.ws.external.soap.logging;

import org.apache.cxf.Bus;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.message.Message;
import org.oscarehr.ws.external.soap.logging.interceptor.SoapLogHTTPInterceptor;
import org.oscarehr.ws.external.soap.logging.interceptor.SoapLogMethodInterceptor;
import org.oscarehr.ws.external.soap.logging.interceptor.SoapLogResponseInterceptor;

import java.util.List;

/**
 * This class bundles together the different SOAP interceptors responsble for creatng a single SOAP log entry for a
 * full cycle request to response exchange.
 */
public class SoapLoggingSuite extends AbstractFeature
{
    private static final SoapLogHTTPInterceptor SOAP_HTTP_INTERCEPTOR = new SoapLogHTTPInterceptor();
    private static final SoapLogMethodInterceptor SOAP_METHOD_INTERCEPTOR = new SoapLogMethodInterceptor();
    private static final SoapLogResponseInterceptor SOAP_RESPONSE_INTERCEPTOR = new SoapLogResponseInterceptor();

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus)
    {
        List<Interceptor<? extends Message>> inInterceptors = provider.getInInterceptors();
        inInterceptors.add(SOAP_HTTP_INTERCEPTOR);
        inInterceptors.add(SOAP_METHOD_INTERCEPTOR);

        List<Interceptor<? extends Message>> inFaultInterceptors = provider.getInFaultInterceptors();
        inFaultInterceptors.add(SOAP_HTTP_INTERCEPTOR);

        List<Interceptor<? extends Message>> outInterceptors = provider.getOutInterceptors();
        outInterceptors.add(SOAP_RESPONSE_INTERCEPTOR);

        List<Interceptor<? extends Message>> outFaultInterceptors = provider.getOutFaultInterceptors();
        outFaultInterceptors.add(SOAP_RESPONSE_INTERCEPTOR);
    }
}
