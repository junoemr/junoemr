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

import org.apache.commons.lang.StringUtils;
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
	public static final String X_FORWARDED_FOR_HEADER_NAME = "X-FORWARDED-FOR";
	private static final String IP_SEPARATOR = ",";

	private static final Logger logger = MiscUtils.getLogger();
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final boolean enabled = props.isPropertyActive("web_service_allowed_ips.enabled");

	// XXX: disabled in spring-boot because spring boot handles x-forwarded-for
	//private static final boolean hasProxy = props.isPropertyActive("web_service_allowed_ips.has_proxy");
	private static final boolean hasProxy = false;

	private static final String localIpPrefix = props.getProperty("web_service_allowed_ips.local_ip_prefix");
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
		String xForwardedForValue = httpRequest.getHeader(IPRestrictionFilter.X_FORWARDED_FOR_HEADER_NAME);

		if(isIpBlocked(requestIp, xForwardedForValue))
		{
			//don't log the request. This filter may execute before the rate limiting filter
			request.setProperty(LoggingFilter.PROP_SKIP_LOGGING, true);

			String logIp = getIpToFilter(requestIp, xForwardedForValue);
			logger.warn("Request from unauthorized IP blocked: " + logIp);
			throw new SecurityException("Unauthorized IP Address (" + logIp + ")");
		}
	}

	public static boolean isIpBlocked(String requestIp, String xForwardedForValueIpCsv)
	{
		logger.debug("WHITELIST ENABLED: " + IPRestrictionFilter.enabled);
		logger.debug("USE PROXY: " + IPRestrictionFilter.hasProxy);
		logger.debug("CHECK IP: " + requestIp);
		logger.debug("XFF IPs: " + xForwardedForValueIpCsv);
		logger.debug("IP WHITELIST SET: " + String.join(",", IPRestrictionFilter.whitelistedIPs));

		return isIpBlocked(
				IPRestrictionFilter.enabled,
				IPRestrictionFilter.hasProxy,
				IPRestrictionFilter.localIpPrefix,
				IPRestrictionFilter.whitelistedIPs,
				requestIp,
				xForwardedForValueIpCsv
		);
	}

	public static String getIpToFilter(String requestIp, String xForwardedForValueIpCsv)
	{
		if(hasProxy)
		{
			// If a proxy is used, take the last ip from the list and ensure the request IP is local
			return getMostRecentIpFromCsv(xForwardedForValueIpCsv);
		}

		return requestIp;
	}

	public static boolean isIpBlocked(
			boolean enabled,
			boolean hasProxy,
			String localIpPrefix,
			Set<String> whitelistedIPs,
			String requestIp,
			String xForwardedForValueIpCsv
	)
	{
		if(hasProxy)
		{
			// If a proxy is used, take the last ip from the list and ensure the request IP is local
			String mostRecentIp = getMostRecentIpFromCsv(xForwardedForValueIpCsv);
			return (!isRequestIpLocal(localIpPrefix, requestIp) || isIpBlocked(enabled, whitelistedIPs, mostRecentIp));
		}

		return isIpBlocked(enabled, whitelistedIPs, requestIp);
	}

	private static boolean isRequestIpLocal(String localIpPrefix, String requestIp)
	{
		return (requestIp != null && requestIp.startsWith(localIpPrefix));
	}

	private static String getMostRecentIpFromCsv(String ipCsv)
	{
		if(ipCsv == null)
		{
			return null;
		}

		if(ipCsv.lastIndexOf(IP_SEPARATOR) == -1)
		{
			return ipCsv.trim();
		}

		return StringUtils.substringAfterLast(ipCsv, IP_SEPARATOR).trim();
	}

	private static boolean isIpBlocked(boolean enabled, Set<String> whitelistedIPs, String requestIp)
	{
		return (enabled && !localhost.equals(requestIp) && !whitelistedIPs.contains(requestIp));
	}
}
