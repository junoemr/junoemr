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


package org.oscarehr.ws;

import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.OscarLog;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.filter.IPRestrictionFilter;
import oscar.log.LogAction;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import java.util.HashMap;

/**
 * As of WSS 1.6 we no longer need InInterceptors for authentication, that's now moved to the Validator classes.
 * We still want this interceptor here though as it's the only way I currently know of to make excludes for a global
 * security filter.
 */
public class IPInWSS4JInterceptor extends WSS4JInInterceptor implements CallbackHandler
{
	private static final Logger logger = MiscUtils.getLogger();

	public IPInWSS4JInterceptor()
	{
		HashMap<String, Object> properties = new HashMap<>();
		setProperties(properties);
	}

	@Override
	public void handleMessage(SoapMessage message)
	{
		HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
		if (request==null) return; // it's an outgoing request
		String ip = request.getRemoteAddr();

		if(IPRestrictionFilter.isIpBlocked(ip))
		{
			String errorMessage = "Unauthorized IP Address (" + ip + ")";
			logger.error(errorMessage);
			OscarLog oscarLog=new OscarLog();
			oscarLog.setAction("ACCESS_WS");
			oscarLog.setIp(ip);
			oscarLog.setContent(errorMessage);
			LogAction.addLogSynchronous(oscarLog);

			QName faultName = new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Server");
			throw new SoapFault(errorMessage, faultName);
		}
	}

	@Override
	public void handle(Callback[] callbacks)
	{
		// do nothing
	}
}
