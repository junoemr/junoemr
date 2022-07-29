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

import static oscar.oscarLab.ca.all.parsers.AHS.v23.AHSRuralDIHandler.AHS_RURAL_DI_LAB_TYPE;


public class AHSRuralDIHandlerTest extends AbstractMessageHandlerTestBase<AHSRuralDIHandler>
{
	private static final String conformanceFile1 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.arad";
	private static final String conformanceFile2 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.crad";
	private static final String conformanceFile3 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.drad";
	private static final String conformanceFile4 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.erad";
	private static final String conformanceFile5 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.lrad";
	private static final String conformanceFile6 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.nrad";
	private static final String conformanceFile7 = "oscar/oscarLab/ca/all/parsers/AHS/v23/RURALDIMESSAGES.201606210400.prad";


	private static List<MessageHandler> conformance1Handlers;
	private static List<MessageHandler> conformance2Handlers;
	private static List<MessageHandler> conformance3Handlers;
	private static List<MessageHandler> conformance4Handlers;
	private static List<MessageHandler> conformance5Handlers;
	private static List<MessageHandler> conformance6Handlers;
	private static List<MessageHandler> conformance7Handlers;

	@BeforeClass
	public static void loadResourceFiles() throws IOException
	{
		ClassLoader classLoader = AHSRuralDIHandlerTest.class.getClassLoader();
		conformance1Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile1);
		conformance2Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile2);
		conformance3Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile3);
		conformance4Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile4);
		conformance5Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile5);
		conformance6Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile6);
		conformance7Handlers = loadResourceFile(classLoader, AHS_RURAL_DI_LAB_TYPE, conformanceFile7);
	}

	@Override
	protected Map<MessageHandler, String> getExpectedAccessionMap()
	{
		Map<MessageHandler, String> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), "1389.001AWL");
		matchingMap.put(conformance2Handlers.get(0), "1883.002CLR");
		matchingMap.put(conformance2Handlers.get(1), "1883.001CLR");
		matchingMap.put(conformance3Handlers.get(0), "25955.001DRD");
		matchingMap.put(conformance4Handlers.get(0), "1868.001KD");
		matchingMap.put(conformance5Handlers.get(0), "1803.001LMH");
		matchingMap.put(conformance6Handlers.get(0), "990.001NNL");
		matchingMap.put(conformance7Handlers.get(0), "2984.001PQE");

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, Integer> getExpectedDocumentCountMap()
	{
		Map<MessageHandler, Integer> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), 0);
		matchingMap.put(conformance2Handlers.get(0), 0);
		matchingMap.put(conformance2Handlers.get(1), 0);
		matchingMap.put(conformance3Handlers.get(0), 0);
		matchingMap.put(conformance4Handlers.get(0), 0);
		matchingMap.put(conformance5Handlers.get(0), 0);
		matchingMap.put(conformance6Handlers.get(0), 0);
		matchingMap.put(conformance7Handlers.get(0), 0);

		return matchingMap;
	}

	@Override
	protected Map<MessageHandler, List<String>> getExpectedRoutingIdsMap()
	{
		Map<MessageHandler, List<String>> matchingMap = new HashMap<>();
		matchingMap.put(conformance1Handlers.get(0), Lists.newArrayList("AAROSTEP", "CASEYBE", "WELBMARC"));
		matchingMap.put(conformance2Handlers.get(0), Lists.newArrayList("CHALTARA", "CASEYBE", "WELBMARC"));
		matchingMap.put(conformance2Handlers.get(1), Lists.newArrayList("CHALTARA", "CASEYBE", "WELBMARC"));
		matchingMap.put(conformance3Handlers.get(0), Lists.newArrayList("BARTMART", "CASEYBE"));
		matchingMap.put(conformance4Handlers.get(0), Lists.newArrayList("DAFOWILL", "WELBMARC", "CASEYBE",
				"YEDCTBS", "EDGEBOYD", "HADAVI", "KAODINA", "LOBAGARY", "MACDANNE", "DOHEJOSE", "NABIW", "ZAIDAKRA", "YALTMATH", "WALSJOHN"));
		matchingMap.put(conformance5Handlers.get(0), Lists.newArrayList("CASEYBE", "WELBMARC"));
		matchingMap.put(conformance6Handlers.get(0), Lists.newArrayList("GHITSAND", "WELBMARC", "ELEYJUDY", "CASEYBE"));
		matchingMap.put(conformance7Handlers.get(0), Lists.newArrayList("CASEYBE"));

		return matchingMap;
	}
}