/*
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

package org.oscarehr.casemgmt.service;

import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dto.PreventionListData;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarPrevention.Prevention;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EncounterPreventionNoteService extends EncounterSectionService
{
	@Autowired
	PreventionDS pf;

	@Autowired
	PreventionDao preventionDao;

	public List<EncounterSectionNote> getNotes(LoggedInInfo loggedInInfo, String roleName, String providerNo, String demographicNo, String appointmentNo, String programId)
			throws FactException
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		//list warnings first as module items
		Prevention p = PreventionData.getPrevention(loggedInInfo, Integer.valueOf(demographicNo));

		// Might throw an exception
		// XXX: make this exception better (FactException)
		pf.getMessages(p);

		//now we list prevention modules as items
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String,String>> prevList = pdc.getPreventions();
		Map warningTable = p.getWarningMsgs();

		Map<String, PreventionListData>	preventionListDataMap = preventionDao.getPreventionListData(demographicNo);


		DemographicData dData = new DemographicData();
		Demographic demographic = dData.getDemographic(loggedInInfo, demographicNo);

		List<EncounterSectionNote> items = new ArrayList<>();
		List<EncounterSectionNote> warnings = new ArrayList<>();

		for (int i = 0 ; i < prevList.size(); i++)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			HashMap<String,String> h = prevList.get(i);
			String prevName = h.get("name");

			PreventionListData preventionListData = preventionListDataMap.get(prevName);

			int preventionCount = 0;
			if(preventionListData != null)
			{
				preventionCount = preventionListData.getPreventionCount();
			}


			//ArrayList<Map<String,Object>> alist = PreventionData.getPreventionData(loggedInInfo, prevName, Integer.valueOf(demographicNo));

			// alist - list of prevention details
			// What is read:
			// - alist.size()
			// - last record (most recent)
			//   - id
			//   - prevention_date_asDate
			//   - refused
			// - last record ext
			//   - result


			// Ignore, integrator
			//Date demographicDateOfBirth=PreventionData.getDemographicDateOfBirth(loggedInInfo, Integer.valueOf(demographicNo));
			//PreventionData.addRemotePreventions(loggedInInfo, alist, Integer.valueOf(demographicNo),prevName,demographicDateOfBirth);

			// Does a few things
			// - Checks if it's hidden (this can be loaded before and checked quicker)
			//    - property table, hide_prevention_item key
			//    - Only hides if there are no preventions (alist.size() > 0)
			// - Shows if it has more than the min threshold (showIfMinRecordNum)
			// - Show if it meets the age requirements (based on min/max age being defined)
			// - Show if there are no ages set but sex matches


			boolean show = pdc.display(loggedInInfo, h, demographic, preventionCount);

			if(show)
			{
				if( preventionCount > 0 )
				{
					sectionNote.setUpdateDate(preventionListData.getPreventionDate());

					if(
						preventionListData.getRefused() != null &&
						preventionListData.getRefused().equals('2')
					)
					{
						sectionNote.setColour(COLOUR_INELLIGIBLE);
					}
					else if(
						preventionListData.getPreventionResult() != null &&
						preventionListData.getPreventionResult().equalsIgnoreCase("pending")
					)
					{
						sectionNote.setColour(COLOUR_PENDING);
					}
				}

				String title = StringUtils.maxLenString(h.get("name"),  MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
				//item.setTitle(title);
				//item.setLinkTitle(h.get("desc"));
				//item.setURL(url);
				sectionNote.setText(title);

				//if there's a warning associated with this prevention set item apart
				if( warningTable.containsKey(prevName) )
				{
					sectionNote.setColour(COLOUR_HIGHLITE);
					warnings.add(sectionNote);
				}
				else
				{
					items.add(sectionNote);
				}
			}
		}

		Collections.sort(items, new EncounterSectionNote.SortChronologicAsc());

		out.addAll(warnings);
		out.addAll(items);

		return out;
	}
}
