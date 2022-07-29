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

import static oscar.oscarLab.ca.all.parsers.AHS.v23.AHSMeditechHandler.AHS_MEDITECH_LAB_TYPE;


public class AHSMeditechHandlerTest extends AbstractMessageHandlerTestBase<AHSMeditechHandler>
{
	private static final String conformanceFile1 = "oscar/oscarLab/ca/all/parsers/AHS/v23/PDOCMESSAGES.201702080400.lrad";
	private static final String conformanceFile2 = "oscar/oscarLab/ca/all/parsers/AHS/v23/PDOCMESSAGES.201702090400.lrad";
	private static final String conformanceFile3 = "oscar/oscarLab/ca/all/parsers/AHS/v23/PDOCMESSAGES.201702100400.lrad";

	private static List<MessageHandler> conformance1Handlers;
	private static List<MessageHandler> conformance2Handlers;
	private static List<MessageHandler> conformance3Handlers;

	@BeforeClass
	public static void loadResourceFiles() throws IOException
	{
		ClassLoader classLoader = AHSMeditechHandlerTest.class.getClassLoader();
		conformance1Handlers = loadResourceFile(classLoader, AHS_MEDITECH_LAB_TYPE, conformanceFile1);
		conformance2Handlers = loadResourceFile(classLoader, AHS_MEDITECH_LAB_TYPE, conformanceFile2);
		conformance3Handlers = loadResourceFile(classLoader, AHS_MEDITECH_LAB_TYPE, conformanceFile3);
	}

	@Override
	protected Map<MessageHandler, String> getExpectedAccessionMap()
	{
		Map<MessageHandler, String> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), "1348LMH");
		matchingMap.put(conformance1Handlers.get(1), "1348LMH");
		matchingMap.put(conformance1Handlers.get(2), "1349LMH");
		matchingMap.put(conformance2Handlers.get(0), "1349LMH");
		matchingMap.put(conformance3Handlers.get(0), "1349LMH");
		matchingMap.put(conformance3Handlers.get(1), "1348LMH");

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, Integer> getExpectedDocumentCountMap()
	{
		Map<MessageHandler, Integer> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), 0);
		matchingMap.put(conformance1Handlers.get(1), 0);
		matchingMap.put(conformance1Handlers.get(2), 0);
		matchingMap.put(conformance2Handlers.get(0), 0);
		matchingMap.put(conformance3Handlers.get(0), 0);
		matchingMap.put(conformance3Handlers.get(1), 0);

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, List<String>> getExpectedRoutingIdsMap()
	{
		Map<MessageHandler, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), Lists.newArrayList("435186", "CIUBSERG", "BARTMART", "WALSJOHN"));
		matchingMap.put(conformance1Handlers.get(1), Lists.newArrayList("435186", "CIUBSERG", "BARTMART"));
		matchingMap.put(conformance1Handlers.get(2), Lists.newArrayList("435186", "CIUBSERG", "BARTMART"));
		matchingMap.put(conformance2Handlers.get(0), Lists.newArrayList("435186", "CIUBSERG", "BARTMART"));
		matchingMap.put(conformance3Handlers.get(0), Lists.newArrayList("435186", "CIUBSERG", "BARTMART"));
		matchingMap.put(conformance3Handlers.get(1), Lists.newArrayList("435186", "CIUBSERG", "BARTMART"));

		return matchingMap;
	}
}