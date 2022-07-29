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
package oscar.oscarLab.ca.all.parsers.AHS.v251;

import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import oscar.oscarLab.ca.all.parsers.AHS.ConnectCareLabType;
import oscar.oscarLab.ca.all.parsers.AbstractMessageHandlerTestBase;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConnectCareProviderCommunicationsHandlerTest extends AbstractMessageHandlerTestBase<ConnectCareProviderCommunicationsHandler>
{
	private static final String conformanceFile1 = "oscar/oscarLab/ca/all/parsers/AHS/v251/800105_20220414115959.cccomm";

	private static List<MessageHandler> conformance1Handlers;

	@BeforeClass
	public static void loadResourceFiles() throws IOException
	{
		conformance1Handlers = loadResourceFile(
				ConnectCareProviderCommunicationsHandler.class.getClassLoader(),
				ConnectCareLabType.CCCOMM.name(),
				conformanceFile1);
	}

	@Override
	protected Map<MessageHandler, String> getExpectedAccessionMap()
	{
		Map<MessageHandler, String> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), "403120653784_A0");
		matchingMap.put(conformance1Handlers.get(1), "403120653788_A0");
		matchingMap.put(conformance1Handlers.get(2), "403120653789_A0");
		matchingMap.put(conformance1Handlers.get(3), "403120653788_A1");
		matchingMap.put(conformance1Handlers.get(4), "403120653789_A1");

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, Integer> getExpectedDocumentCountMap()
	{
		Map<MessageHandler, Integer> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), 1);
		matchingMap.put(conformance1Handlers.get(1), 2);
		matchingMap.put(conformance1Handlers.get(2), 2);
		matchingMap.put(conformance1Handlers.get(3), 3);
		matchingMap.put(conformance1Handlers.get(4), 1);

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, List<String>> getExpectedRoutingIdsMap()
	{
		Map<MessageHandler, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), Lists.newArrayList("84332", "14100", "40696", "171650", "73798", "10263"));
		matchingMap.put(conformance1Handlers.get(1), Lists.newArrayList("84332", "10263", "40696", "171650", "73798", "14100"));
		matchingMap.put(conformance1Handlers.get(2), Lists.newArrayList("84332", "14100", "10263", "171650", "73798", "40696"));
		matchingMap.put(conformance1Handlers.get(3), Lists.newArrayList("84332", "10263", "40696", "171650", "73798", "14100"));
		matchingMap.put(conformance1Handlers.get(4), Lists.newArrayList("16272", "14100", "10263", "171650", "73798"));

		return matchingMap;
	}
}