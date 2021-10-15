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
package org.oscarehr.careTrackerDecisionSupport.service;

import org.drools.IntegrationException;
import org.drools.RuleBase;
import org.drools.io.RuleBaseLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import oscar.OscarProperties;
import oscar.oscarEncounter.oscarMeasurements.MeasurementFlowSheet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class DroolsCachingService
{
	private static final ConcurrentMap<String, RuleBase> ruleBaseMap = new ConcurrentHashMap<>();

	public synchronized RuleBase getDroolsRuleBase(String drlFilename) throws IntegrationException, IOException, SAXException
	{
		if(ruleBaseMap.containsKey(drlFilename))
		{
			return ruleBaseMap.get(drlFilename);
		}
		else
		{
			RuleBase ruleBase = loadRuleBase(drlFilename);
			ruleBaseMap.put(drlFilename, ruleBase);
			return ruleBase;
		}
	}

	/** ported from MeasurementFlowSheet */
	private synchronized RuleBase loadRuleBase(String drlFilename) throws IntegrationException, IOException, SAXException
	{
		RuleBase ruleBase = null;
		boolean fileFound = false;
		String measurementDirPath = OscarProperties.getInstance().getProperty("MEASUREMENT_DS_DIRECTORY");

		if(measurementDirPath != null)
		{
			File file = new File(measurementDirPath, drlFilename);
			if(file.isFile() || file.canRead())
			{
				FileInputStream fis = new FileInputStream(file);
				ruleBase = RuleBaseLoader.loadFromInputStream(fis);
				fileFound = true;
			}
		}

		if(!fileFound)
		{
			URL url = MeasurementFlowSheet.class.getResource("/oscar/oscarEncounter/oscarMeasurements/flowsheets/" + drlFilename);
			ruleBase = RuleBaseLoader.loadFromUrl(url);
		}

		return ruleBase;
	}
}
