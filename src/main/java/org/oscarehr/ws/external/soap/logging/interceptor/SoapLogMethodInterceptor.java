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

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.MethodDispatcher;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;

import org.oscarehr.ws.external.soap.logging.SoapLogBuilder;

import javax.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;


/**
 * This class is responsible for logging at the service/method level of a SOAP request.
 *
 * Together with SoapLogHTTPInterceptor and SoapLogResponseInterceptor, these three interceptors form a suite which is
 * able to log the full webservice request and response.
 */
public class SoapLogMethodInterceptor extends AbstractSoapInterceptor
{
	private static final Logger logger = Logger.getLogger(SoapLogMethodInterceptor.class);

	public SoapLogMethodInterceptor() {
		super(Phase.PRE_LOGICAL);
	}

	/**
	 * Add authentication and SOAP service information to the SOAP request/response log
	 *
	 * @param message Soap message to be logged
	 */
	@Override
	public void handleMessage(SoapMessage message) throws Fault
    {
        SoapLogBuilder logData = (SoapLogBuilder) message.getExchange().remove(SoapLogBuilder.class.getName());

        if (logData != null)
        {
            cacheMethodData(logData, message);
            message.getExchange().put(SoapLogBuilder.class.getName(), logData);
        }
        else
        {
            logger.error("Could not find soap log fragment");
        }
    }

    private void cacheMethodData(SoapLogBuilder logData, Message message)
    {
        HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        Method soapMethod = getMessageTargetMethod(message);
        logData.addMethodData(request, soapMethod);
	}

	/**
	 *	Get the java method which is invoked by the soap message
	 *
	 * @param message:  The soap message
	 * @return the Method
	 */
	private Method getMessageTargetMethod(Message message)
	{
		Exchange exchange = message.getExchange();
		BindingOperationInfo bop = exchange.get(BindingOperationInfo.class);
		MethodDispatcher dispatcher = (MethodDispatcher) exchange.get(Service.class).get(MethodDispatcher.class.getName());

		return dispatcher.getMethod(bop);
	}
}

