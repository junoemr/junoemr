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

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class IPRestrictionFilterTest
{
	boolean enabled;
	boolean hasProxy;
	String localIpPrefix;
	Set<String> whitelistedIPs;

	@Before
	public void init()
	{
		enabled = true;
		hasProxy = false;
		localIpPrefix = "10.";
		whitelistedIPs = new HashSet<>();

		whitelistedIPs.add("1.1.1.1");
		whitelistedIPs.add("2.2.2.2");
	}

	@Test
	public void isIpBlocked_disabled()
	{
		enabled = false;

		String requestIp = "";
		String xForwardedForValue = "";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_basicRequest()
	{
		String requestIp = "1.1.1.1";
		String xForwardedForValue = "";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_basicRequestNullXFF()
	{
		String requestIp = "1.1.1.1";
		String xForwardedForValue = null;

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_basicBlankFailure()
	{
		String requestIp = "";
		String xForwardedForValue = "";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_basicNoMatchFailure()
	{
		String requestIp = "1.1.1.2";
		String xForwardedForValue = "";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_basicSameSubstringFailure()
	{
		String requestIp = "1.1.1.1.1";
		String xForwardedForValue = "";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyRequest()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "1.1.1.1";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyRequestSpaces()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = " 1.1.1.1 ";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyFailNotLocal()
	{
		hasProxy = true;

		String requestIp = "11.0.0.0";
		String xForwardedForValue = "1.1.1.1";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxySuccessTwoIps()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "3.3.3.3, 1.1.1.1";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxySuccessTwoIpsNoSpace()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "3.3.3.3,1.1.1.1";

		assertFalse(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyFailureTwoIps()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "3.3.3.3, 4.4.4.4";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyFailureTwoIpsWrongOneValid()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "1.1.1.1, 4.4.4.4";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyFailureBlankLastIp()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "1.1.1.1,";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}

	@Test
	public void isIpBlocked_proxyFailureBlankLastIpWithSpace()
	{
		hasProxy = true;

		String requestIp = "10.0.0.0";
		String xForwardedForValue = "1.1.1.1, ";

		assertTrue(IPRestrictionFilter.isIpBlocked(enabled, hasProxy, localIpPrefix, whitelistedIPs, requestIp, xForwardedForValue));
	}
}