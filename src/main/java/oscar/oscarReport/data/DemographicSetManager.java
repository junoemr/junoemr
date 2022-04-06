/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.oscarReport.data;

import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DemographicSetsDao;
import org.oscarehr.common.model.DemographicSets;
import org.oscarehr.util.SpringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemographicSetManager {

	private final DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);
	private final DemographicSetsDao demographicSetsDao = SpringUtils.getBean(DemographicSetsDao.class);

	public DemographicSetManager() {
	}

	public void addDemographicSet(String setName, String demographic) {
		List<String> list = new ArrayList<String>();
		list.add(demographic);
		addDemographicSet(setName, list);
	}

	public void addDemographicSet(String setName, List<String> demoList)
	{
		for(String demographicNo : demoList)
		{
			DemographicSets existingSet = demographicSetsDao.findBySetNameAndDemographicNo(setName, Integer.parseInt(demographicNo));
			if(existingSet != null)
			{
				existingSet.setArchive("0");
				demographicSetsDao.merge(existingSet);
			}
			else
			{
				DemographicSets ds = new DemographicSets();
				ds.setName(setName);
				ds.setDemographic(demographicDao.getDemographic(demographicNo));
				ds.setArchive("0");
				demographicSetsDao.persist(ds);
			}
		}
	}

	public List<String> getDemographicSet(String setName) {
		List<String> retval = new ArrayList<String>();
		List<DemographicSets> dss = demographicSetsDao.findBySetName(setName);
		for (DemographicSets ds : dss) {
			retval.add(String.valueOf(ds.getDemographicNo()));
		}
		return retval;
	}

	public List<String> getDemographicSet(List<String> setNames) {
		List<String> retval = new ArrayList<String>();

		List<DemographicSets> dss = demographicSetsDao.findBySetNames(setNames);
		for (DemographicSets ds : dss) {
			retval.add(String.valueOf(ds.getDemographicNo()));
		}

		return retval;
	}

	public List<Map<String, String>> getDemographicSetExt(String setName) {
		List<Map<String, String>> retval = new ArrayList<Map<String, String>>();

		List<DemographicSets> dss = demographicSetsDao.findBySetName(setName);
		for (DemographicSets ds : dss) {
			Map<String, String> h = new HashMap<String, String>();
			h.put("demographic_no", String.valueOf(ds.getDemographicNo()));
			String el = ds.getEligibility();
			if (el == null || el.equalsIgnoreCase("null")) {
				el = "0";
			}
			h.put("eligibility", el);
			retval.add(h);
		}

		return retval;
	}

	public List<String> getIneligibleDemographicSet(String setName) {
		List<String> retval = new ArrayList<String>();
		List<DemographicSets> dss = demographicSetsDao.findBySetNameAndEligibility(setName, "1");
		for (DemographicSets ds : dss) {
			retval.add(String.valueOf(ds.getDemographicNo()));
		}
		return retval;
	}

	public List<String> getEligibleDemographicSet(String setName) {
		List<String> retval = new ArrayList<String>();
		List<DemographicSets> dss = demographicSetsDao.findBySetNameAndEligibility(setName, "0");
		for (DemographicSets ds : dss) {
			retval.add(String.valueOf(ds.getDemographicNo()));
		}
		return retval;
	}

	public void setDemographicIneligible(String setName, String demoNo)
	{
		DemographicSets demographicSets = demographicSetsDao.findBySetNameAndDemographicNo(setName, Integer.parseInt(demoNo));
		demographicSets.setEligibility("1");
		demographicSetsDao.merge(demographicSets);
	}

	public void setDemographicDelete(String setName, String demoNo)
	{
		DemographicSets demographicSets = demographicSetsDao.findBySetNameAndDemographicNo(setName, Integer.parseInt(demoNo));
		demographicSets.setArchive("1");
		demographicSetsDao.merge(demographicSets);
	}

	public List<String> getDemographicSets(String demoNo)
	{
		return demographicSetsDao.findSetNamesByDemographicNo(Integer.parseInt(demoNo));
	}

	public List<String> getDemographicSets()
	{
		return demographicSetsDao.findSetNames();
	}
}
