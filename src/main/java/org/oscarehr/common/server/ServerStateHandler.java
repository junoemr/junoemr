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
package org.oscarehr.common.server;

import org.apache.log4j.Logger;
import org.oscarehr.common.server.dao.SlaveStatusDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.SimpleResolver;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import oscar.OscarProperties;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Responsible for handling server state information.
 * This is where other classes should be able to determine if the server is a master/slave server, in readonly mode, etc.
 */
@Component
public class ServerStateHandler
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final Logger logger = Logger.getLogger(ServerStateHandler.class);

	private static final String forceMasterDomainOverride = props.getProperty("common.server.master_check.force_master_domain");
	private static final String domain = props.getProperty("common.server.master_check.domain");
	private static final String secureDomain = props.getProperty("common.server.master_check.secure_subdomain");
	private static final String primaryServerRegexPattern = props.getProperty("common.server.master_check.primary_server_regex_pattern");
	private static final String secondaryServerRegexPattern = props.getProperty("common.server.master_check.secondary_server_regex_pattern");

	private static final String dnsServers = props.getProperty("common.server.master_check.dns_server_list");
	private static final Set<String> dnsServerList;
	static
	{
		dnsServerList = new HashSet<>((dnsServers != null) ? Arrays.asList(dnsServers.split("\\s*,\\s*")) : new ArrayList<>());
	}

	@Autowired
	private SlaveStatusDao slaveStatusDao;

	/**
	 * checks if the server is running in a 'master' state
	 * @return true if the server is a master server, false if it is not a master
	 * @throws IllegalStateException if the master/slave status cannot be determined
	 */
	public boolean isThisServerMaster() throws IllegalStateException
	{
		boolean isMaster;
		try
		{
			logger.info("Checking master status...");

			InetAddress inetAddress = InetAddress.getLocalHost();
			String localHostname = inetAddress.getHostName();
			logger.info("local hostname: " + localHostname);

			if(forceMasterDomainOverride != null)
			{
				logger.info("Master check has properties file override: " + forceMasterDomainOverride);
			}

			// force this server to master if the override domain matches the localhost
			if(localHostname.equals(forceMasterDomainOverride))
			{
				isMaster = true;
			}
			else
			{
				isMaster = actingAsMaster() && resolvesAsMaster(localHostname);
			}
		}
		catch(Exception e)
		{
			throw new IllegalStateException(e);
		}
		logger.info("This is a " + (isMaster? "master" : "slave") + " server");
		return isMaster;
	}

	private boolean actingAsMaster()
	{
		boolean isSlave = slaveStatusDao.inSlaveMode();
		logger.debug("Slave check: " + isSlave);
		return !isSlave;
	}

	private static boolean resolvesAsMaster(String localHostname)
	{
		boolean isMaster = false;
		boolean resolved = false;
		String serverNumber = null;

		Pattern primPattern = Pattern.compile(primaryServerRegexPattern);
		Matcher primPatternMatcher = primPattern.matcher(localHostname);
		if(primPatternMatcher.find())
		{
			serverNumber = primPatternMatcher.group(1);
		}
		else
		{
			Pattern secPattern = Pattern.compile(secondaryServerRegexPattern);
			Matcher secPatternMatcher = secPattern.matcher(localHostname);
			if(secPatternMatcher.find())
			{
				serverNumber = secPatternMatcher.group(1);
			}
		}
		logger.debug("Found server number: " + serverNumber);
		if(serverNumber != null)
		{
			String secureHostname = secureDomain + serverNumber + "." + domain;
			logger.info("secure hostname: " + secureHostname);

			// for each server in the dns servers list, check that the name resolves.
			// if the server fails, try the next one as a backup, etc.
			for(String server : dnsServerList)
			{
				try
				{
					isMaster = resolvesAsMaster(server, localHostname, secureHostname);
					resolved = true;
					break;
				}
				catch(UnknownHostException | TextParseException | IllegalStateException e)
				{
					logger.warn("[" + server + "]: " + e.getMessage());
				}
			}
			if(!resolved)
			{
				throw new IllegalStateException("All DNS servers failed to resolve master");
			}
		}
		return isMaster;
	}

	private static boolean resolvesAsMaster(String dnsServer, String localHostname, String secureDomain) throws TextParseException, UnknownHostException
	{
		List<String> localIpList = resolveHostByName(dnsServer, null, localHostname);
		List<String> remoteIpList = resolveHostByName(dnsServer, null, secureDomain);

		if(localIpList.size() != 1)
		{
			throw new IllegalStateException("Unable to resolve " + localHostname + " IP");
		}
		if(remoteIpList.size() != 1)
		{
			throw new IllegalStateException("Unable to resolve " + secureDomain + " IP");
		}

		// this is the master if the two IP addresses match
		return (localIpList.get(0).equals(remoteIpList.get(0)));
	}

	/**
	 * resolves an A record by its name using a specified DNS host and port
	 *
	 * @param resolverHost name server hostname or IP address
	 * @param resolverPort name server port
	 * @param name         the DNS name of the A record - the name to resolve
	 * @return a list of IP addresses or an empty list when unable to resolve
	 */
	private static List<String> resolveHostByName(String resolverHost, Integer resolverPort, String name) throws UnknownHostException, TextParseException
	{
		List<String> addressList;
		SimpleResolver resolver = new SimpleResolver(resolverHost);
		if(resolverPort != null)
		{
			resolver.setPort(resolverPort);
		}

		Lookup lookup = new Lookup(name, Type.A);
		Record[] records = lookup.run();
		if(records != null)
		{
			addressList = new ArrayList<>(records.length);
			for(Record record : records)
			{
				if(record instanceof ARecord)
				{
					addressList.add(((ARecord) record).getAddress().getHostAddress());
				}
			}
		}
		else
		{
			addressList = new ArrayList<>(0);
		}
		return addressList;
	}
}