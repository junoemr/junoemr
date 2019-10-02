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

import org.oscarehr.casemgmt.dto.EncounterCPPNote;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class EncounterCPPNoteService
{
	@Autowired
	CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	private IssueDao issueDao;

	public EncounterSection getInitialSection(
			String contextPath,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String identUrl,
			String title,
			String colour,
			String sectionName
	)
	{

		return getSection(
			contextPath,
			providerNo,
			demographicNo,
			appointmentNo,
			identUrl,
			title,
			colour,
			sectionName,
			EncounterSectionService.INITIAL_ENTRIES_TO_SHOW,
			EncounterSectionService.INITIAL_OFFSET
		);
	}

	public EncounterSection getSection(
			String contextPath,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String identUrl,
			String title,
			String colour,
			String sectionName,
			Integer limit,
			Integer offset
	)
	{

		String addUrl = contextPath + "/CaseManagementEntry.do?method=issueNoteSave" +
				"&providerNo=" + providerNo + "" +
				"&demographicNo=" + demographicNo + "" +
				"&appointmentNo=" + appointmentNo + "" +
				"&noteId=";

		// Get issue id from type
		Issue issue = issueDao.findByCode(sectionName);

		String cppIssues = issue.getId() + ";" + issue.getCode() + ";" + issue.getDescription();

		EncounterSection section = new EncounterSection();

		section.setTitle(title);
		section.setColour(colour);
		section.setCppIssues(cppIssues);
		section.setAddUrl(addUrl);
		section.setIdentUrl(identUrl);
		section.setNotes(getCPPNotes(demographicNo, issue.getIssueId()));

		return section;
	}

	private List<EncounterSectionNote> getCPPNotes(String demographicNo, long issueId)
	{
		// Get notes for that type
		List<EncounterSectionNote> out = new ArrayList<>();

		String[] issueIds = new String[1];
		issueIds[0] = Long.toString(issueId);
		List<EncounterCPPNote> notes = caseManagementNoteDao.getCPPNotes(demographicNo, issueIds);

		for(EncounterCPPNote note: notes)
		{
			EncounterSectionNote sectionNote = new EncounterSectionNote();
			sectionNote.setText(note.getNote());
			sectionNote.setEditors(note.getEditors());
			sectionNote.setRevision(note.getRevision());
			sectionNote.setUpdateDate(note.getUpdateDate());
			sectionNote.setObservationDate(note.getObservationDate());
			sectionNote.setNoteIssuesString(getNoteIssueString(note));
			sectionNote.setNoteExtsString(getNoteExtString(note));
			out.add(sectionNote);
		}

		return out;
	}

	private String getNoteIssueString(EncounterCPPNote note)
	{
		return note.getIssueId().toString() + ";" + note.getCode() + ";" + note.getDescription();
	}

	private String getNoteExtString(EncounterCPPNote note)
	{
		List<String> extStrings = new ArrayList<>();


		if(note.getExtStartDate() != null)
		{
			extStrings.add("Start Date;" +  note.getExtStartDate());
		}

		if(note.getExtResolutionDate() != null)
		{
			extStrings.add("Resolution Date;" +  note.getExtResolutionDate());
		}

		if(note.getExtProcedureDate() != null)
		{
			extStrings.add("Procedure Date;" +  note.getExtProcedureDate());
		}

		if(note.getExtAgeAtOnset() != null)
		{
			extStrings.add("Age at Onset;" +  note.getExtAgeAtOnset());
		}

		if(note.getExtTreatment() != null)
		{
			extStrings.add("Treatment;" +  note.getExtTreatment());
		}

		if(note.getExtProblemStatus() != null)
		{
			extStrings.add("Problem Status;" +  note.getExtProblemStatus());
		}

		if(note.getExtExposureDetail() != null)
		{
			extStrings.add("Exposure Details;" +  note.getExtExposureDetail());
		}

		if(note.getExtRelationship() != null)
		{
			extStrings.add("Relationship;" +  note.getExtRelationship());
		}

		if(note.getExtLifeStage() != null)
		{
			extStrings.add("Life Stage;" +  note.getExtLifeStage());
		}

		if(note.getExtHideCpp() != null)
		{
			extStrings.add("Hide Cpp;" +  note.getExtHideCpp());
		}

		if(note.getExtProblemDescription() != null)
		{
			extStrings.add("Problem Description;" +  note.getExtProblemDescription());
		}

		return StringUtils.join(extStrings, ";");
	}
}
