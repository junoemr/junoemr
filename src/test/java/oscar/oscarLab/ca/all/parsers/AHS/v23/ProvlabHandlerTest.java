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
package oscar.oscarLab.ca.all.parsers.AHS.v23;

import com.google.common.collect.Lists;
import org.junit.BeforeClass;
import oscar.oscarLab.ca.all.parsers.AbstractMessageHandlerTestBase;
import oscar.oscarLab.ca.all.parsers.MessageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static oscar.oscarLab.ca.all.parsers.AHS.v23.CLSHandler.CLS_MESSAGE_TYPE;

public class ProvlabHandlerTest extends AbstractMessageHandlerTestBase<ProvlabHandler>
{
	private static final String prdSampleFile = "oscar/oscarLab/ca/all/parsers/AHS/v23/PROVLAB_111111_20220722135959.prd";

	private static List<MessageHandler> prdSampleHandlers;

	@BeforeClass
	public static void loadResourceFiles() throws IOException
	{
		prdSampleHandlers = loadResourceFile(
				ProvlabHandlerTest.class.getClassLoader(),
				CLS_MESSAGE_TYPE,
				prdSampleFile);
	}

	@Override
	protected Map<MessageHandler, String> getExpectedAccessionMap()
	{
		Map<MessageHandler, String> matchingMap = new HashMap<>();
		matchingMap.put(prdSampleHandlers.get(0), "10-337-300046");
		matchingMap.put(prdSampleHandlers.get(1), "10-337-300046");
		matchingMap.put(prdSampleHandlers.get(2), "10-337-300047");
		matchingMap.put(prdSampleHandlers.get(3), "10-337-300047");
		matchingMap.put(prdSampleHandlers.get(4), "10-337-300042");
		matchingMap.put(prdSampleHandlers.get(5), "10-337-300048");
		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, String> getExpectedHinMap()
	{
		Map<MessageHandler, String> matchingMap = new HashMap<>();
		matchingMap.put(prdSampleHandlers.get(0), "798274114");
		matchingMap.put(prdSampleHandlers.get(1), "798274114");
		matchingMap.put(prdSampleHandlers.get(2), "798274114");
		matchingMap.put(prdSampleHandlers.get(3), "798274114");
		matchingMap.put(prdSampleHandlers.get(4), "798274114");
		matchingMap.put(prdSampleHandlers.get(5), "798274114");
		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, Integer> getExpectedDocumentCountMap()
	{
		Map<MessageHandler, Integer> matchingMap = new HashMap<>();
		matchingMap.put(prdSampleHandlers.get(0), 0);
		matchingMap.put(prdSampleHandlers.get(1), 0);
		matchingMap.put(prdSampleHandlers.get(2), 0);
		matchingMap.put(prdSampleHandlers.get(3), 0);
		matchingMap.put(prdSampleHandlers.get(4), 0);
		matchingMap.put(prdSampleHandlers.get(5), 0);
		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, List<String>> getExpectedRoutingIdsMap()
	{
		Map<MessageHandler, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(prdSampleHandlers.get(0), Lists.newArrayList("1001745"));
		matchingMap.put(prdSampleHandlers.get(1), Lists.newArrayList("1001745"));
		matchingMap.put(prdSampleHandlers.get(2), Lists.newArrayList("1001745"));
		matchingMap.put(prdSampleHandlers.get(3), Lists.newArrayList("1001745"));
		matchingMap.put(prdSampleHandlers.get(4), Lists.newArrayList("1001745"));
		matchingMap.put(prdSampleHandlers.get(5), Lists.newArrayList("1001745"));
		return matchingMap;
	}
}