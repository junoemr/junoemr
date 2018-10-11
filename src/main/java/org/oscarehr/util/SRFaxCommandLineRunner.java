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
package org.oscarehr.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.oscarehr.fax.externalApi.srfax.SRFaxApiConnector;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxInboxResult;
import org.oscarehr.fax.externalApi.srfax.result.GetFaxOutboxResult;
import org.oscarehr.fax.externalApi.srfax.result.GetUsageResult;
import org.oscarehr.fax.externalApi.srfax.resultWrapper.ListWrapper;

public class SRFaxCommandLineRunner
{
	private static final Logger logger = Logger.getLogger(SRFaxCommandLineRunner.class);

	/**
	 * Run this in the WEB-INF folder in one of 2 ways:
	 *
	 * deployed: in ~tomcat/webapps/context_path/WEB-INF
	 * un-deployed: in [code_source]/target/oscar-14.0.0-SNAPSHOT/WEB-INF
	 * java -cp "classes/:lib/*:/usr/java/apache-tomcat/lib/*" org.oscarehr.util.SRFaxCommandLineRunner
	 *   [srfax username] [srfax password]
	 */
	public static void main (String [] args)
	{
		if(args == null || args.length != 2)
		{
			BasicConfigurator.configure();
			logger.error("Invalid argument count");
			return;
		}
		BasicConfigurator.configure();

		String srfax_login = args[0];
		String srfax_password = args[1];

		logger.info("BEGIN TEST");

		SRFaxApiConnector apiConnector = new SRFaxApiConnector(srfax_login, srfax_password);

		testGetFaxUsage(apiConnector);
		testGetFaxInbox(apiConnector);
		testGetFaxOutbox(apiConnector);

		logger.info("COMPLETE");
	}

	public static void testGetFaxUsage(SRFaxApiConnector apiConnector)
	{
		logger.info("TEST GET FAX USAGE");
		ListWrapper<GetUsageResult> responseWrapper = apiConnector.Get_Fax_Usage(null,null, null, null, null);

		logger.info("STATUS: " + responseWrapper.getStatus());
		logger.info("DATA: " + responseWrapper.getResult());
	}

	public static void testGetFaxInbox(SRFaxApiConnector apiConnector)
	{
		logger.info("TEST GET FAX INBOX");
		ListWrapper<GetFaxInboxResult> responseWrapper = apiConnector.Get_Fax_Inbox(null, null, null, null, null, null, null);

		logger.info("STATUS: " + responseWrapper.getStatus());
		logger.info("DATA: " + responseWrapper.getResult());
	}

	public static void testGetFaxOutbox(SRFaxApiConnector apiConnector)
	{
		logger.info("TEST GET FAX OUTBOX");
		ListWrapper<GetFaxOutboxResult> responseWrapper = apiConnector.Get_Fax_Outbox(null, null, null, null, null);

		logger.info("STATUS: " + responseWrapper.getStatus());
		logger.info("DATA: " + responseWrapper.getResult());
	}
}