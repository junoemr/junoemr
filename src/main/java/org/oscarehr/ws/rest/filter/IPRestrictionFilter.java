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
package org.oscarehr.ws.rest.filter;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class IPRestrictionFilter implements ContainerRequestFilter
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final boolean enabled = props.isPropertyActive("web_service_allowed_ips.enabled");
	private static final String allowedIPs = props.getProperty("web_service_allowed_ips");
	private static final String systemAllowedIPs = props.getProperty("web_service_allowed_system_ips");
	private static final String localhost = "127.0.0.1";

	private static final Set<String> whitelistedIPs;
	static
	{
		List<String> systemIPList = (systemAllowedIPs!=null)? Arrays.asList(systemAllowedIPs.split("\\s*,\\s*")) : new ArrayList<>(0);
		List<String> allowedIPList = (allowedIPs!=null)? Arrays.asList(allowedIPs.split("\\s*,\\s*")) : new ArrayList<>(0);

		whitelistedIPs = new HashSet<>();
		whitelistedIPs.addAll(systemIPList);
		whitelistedIPs.addAll(allowedIPList);
	}

	@Context
	private HttpServletRequest httpRequest;

	/**
	 * Request filter
	 */
	@Override
	public void filter(ContainerRequestContext request)
	{
		String requestIp = httpRequest.getRemoteAddr();
		if(isIpBlocked(requestIp))
		{
			//don't log the request. This filter may execute before the rate limiting filter
			request.setProperty(LoggingFilter.PROP_SKIP_LOGGING, true);
			logger.warn("Request from unauthorized IP blocked: " + requestIp);
			throw new SecurityException("Unauthorized IP Address (" + requestIp + ")");
		}
	}

	public static boolean isIpBlocked(String requestIp)
	{
		logger.debug("WHITELIST ENABLED: " + enabled);
		logger.debug("CHECK IP: " + requestIp);
		logger.debug("IP WHITELIST SET: " + String.join(",", whitelistedIPs));

		return (enabled && !localhost.equals(requestIp) && !whitelistedIPs.contains(requestIp));
	}
}
