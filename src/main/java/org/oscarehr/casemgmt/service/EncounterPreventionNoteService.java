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

package org.oscarehr.casemgmt.service;

import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.casemgmt.exception.EncounterSectionException;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.prevention.dao.PreventionDao;
import org.oscarehr.prevention.dto.PreventionListData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarPrevention.Prevention;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.PreventionDisplayConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class EncounterPreventionNoteService extends EncounterSectionService
{
	public static final String SECTION_ID = "preventions";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.LeftNavBar.Prevent";
	protected static final String SECTION_TITLE_COLOUR = "#009999";

	@Autowired
	PreventionDS pf;

	@Autowired
	PreventionDao preventionDao;

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		return SECTION_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		return getOnClick(sectionParams);
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		return getOnClick(sectionParams);
	}

	private String getOnClick(SectionParameters sectionParams)
	{
		String winName = "prevention" + sectionParams.getDemographicNo();

		String url = sectionParams.getContextPath() + "/oscarPrevention/index.jsp" +
				"?demographic_no=" + sectionParams.getDemographicNo();

		return "popupPage(700,960,'" + winName + "', '" + url + "')";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	) throws EncounterSectionException
	{
		List<EncounterSectionNote> noteList = new ArrayList<>();

		//list warnings first as module items
		Prevention p = PreventionData.getPrevention(sectionParams.getLoggedInInfo(), Integer.valueOf(sectionParams.getDemographicNo()));

		// Might throw an exception
		// XXX: make this exception better (FactException)
		try
		{
			pf.getMessages(p);
		}
		catch(FactException e)
		{
			MiscUtils.getLogger().error("Error in the prevention section of the encounter page", e);
			throw new EncounterSectionException("Error getting prevention warnings", e);
		}

		//now we list prevention modules as items
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String,String>> prevList = pdc.getPreventions();
		Map warningTable = p.getWarningMsgs();

		Map<String, PreventionListData>	preventionListDataMap = preventionDao.getPreventionListData(sectionParams.getDemographicNo());

		String onClickString = getOnClick(sectionParams);

		DemographicData dData = new DemographicData();
		Demographic demographic = dData.getDemographic(sectionParams.getLoggedInInfo(), sectionParams.getDemographicNo());

		List<EncounterSectionNote> items = new ArrayList<>();
		List<EncounterSectionNote> warnings = new ArrayList<>();

		for (int i = 0 ; i < prevList.size(); i++)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			Map<String,String> h = prevList.get(i);
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


			boolean show = pdc.display(h, demographic, preventionCount);

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

				sectionNote.setText(h.get("name"));
				sectionNote.setOnClick(onClickString);

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

		Collections.sort(items, new EncounterSectionNote.SortChronologicBlankDateFirst());

		noteList.addAll(warnings);
		noteList.addAll(items);

		return EncounterNotes.limitedEncounterNotes(noteList, offset, limit);
	}
}
