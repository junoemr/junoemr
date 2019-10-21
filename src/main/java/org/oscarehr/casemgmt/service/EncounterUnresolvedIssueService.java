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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.util.CppUtils;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterUnresolvedIssueService extends EncounterSectionService
{
	private static final String SECTION_ID = "unresolvedIssues";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.NavBar.unresolvedIssues";
	protected static final String SECTION_TITLE_COLOUR = "#CC9900";

	private CaseManagementManager caseManagementMgr;

	public void setCaseManagementManager(CaseManagementManager caseManagementMgr)
	{
		this.caseManagementMgr = caseManagementMgr;
	}

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
		return "";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		// XXX: I feel like this won't work
		return "$('check_issue').value='';document.caseManagementViewForm.submit();";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		// grab all of the diseases associated with patient and add a list item for each
		List<CaseManagementIssue> issues = null;
		int demographicId = Integer.parseInt(sectionParams.getDemographicNo());
		issues = caseManagementMgr.getIssues(demographicId);
		issues = caseManagementMgr.filterIssues(
				sectionParams.getLoggedInInfo(),
				sectionParams.getProviderNo(),
				issues,
				sectionParams.getProgramId()
		);

		List<CaseManagementIssue> issues_unr = new ArrayList<CaseManagementIssue>();

		//only list unresolved issues
		for(CaseManagementIssue issue : issues)
		{
			if(containsIssue(CppUtils.cppCodes,issue.getIssue().getCode()))
			{
				continue;
			}

			if(!issue.isResolved())
			{
				boolean dup=false;
				for(CaseManagementIssue tmp: issues_unr)
				{
					if(issue.getIssue_id() == tmp.getIssue_id())
					{
						dup=true;
						break;
					}
				}

				if(!dup)
				{
					issues_unr.add(issue);
				}
			}
		}


		for (int idx = 0; idx < issues_unr.size(); ++idx)
		{
			//NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			CaseManagementIssue issue = issues_unr.get(idx);
			String tmp = issue.getIssue().getDescription();

			String strTitle = StringUtils.maxLenString(tmp, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);

			sectionNote.setText(strTitle);

			String onClickString = "setIssueCheckbox('" + issue.getId() + "');return filter(false);";
			sectionNote.setOnClick(onClickString);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}

	public static boolean containsIssue(String[]  issues, String issueCode)
	{
		for (String caseManagementIssue : issues)
		{
			if (caseManagementIssue.equals(issueCode))
			{
				return(true);
			}
		}

		return false;
	}
}
