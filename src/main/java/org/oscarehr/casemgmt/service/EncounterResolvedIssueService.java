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

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.oscarEncounter.pageUtil.NavBarDisplayDAO;

import java.util.ArrayList;
import java.util.List;

public class EncounterResolvedIssueService extends EncounterSectionService
{
	public static final String SECTION_ID = "resolvedIssues";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.NavBar.resolvedIssues";
	protected static final String SECTION_TITLE_COLOUR = "#151B8D";

	@Autowired
	private CaseManagementIssueService caseManagementIssueService;

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
		return "";
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<CaseManagementIssue> issues = caseManagementIssueService.getResolvedIssues(
				sectionParams.getLoggedInInfo(),
				sectionParams.getDemographicNo(),
				sectionParams.getProviderNo(),
				sectionParams.getProgramId()
		);

		List<EncounterSectionNote> out = new ArrayList<>();

		for (int idx = 0; idx < issues.size(); ++idx)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();

			NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();

			CaseManagementIssue issue = issues.get(idx);
			String text = issue.getIssue().getDescription();

			sectionNote.setText(EncounterSectionService.getTrimmedText(text));
			sectionNote.setTitle(text);

			String onClickString = "noteFilter.selectIssueFilterValue('" + issue.getIssue().getId() + "');return noteFilter.filter(false);";
			sectionNote.setOnClick(onClickString);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}
}
