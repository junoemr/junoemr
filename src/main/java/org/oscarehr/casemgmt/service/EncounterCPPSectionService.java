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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang.StringEscapeUtils;
import org.oscarehr.casemgmt.dto.EncounterCPPNote;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.ws.rest.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
public abstract class EncounterCPPSectionService extends EncounterSectionService
{
	@Autowired
	CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	private IssueDao issueDao;

	public String getOnClickTitle(SectionParameters sectionParams, long issueId)
	{

		return "return cppNote.showIssueHistory('" + sectionParams.getDemographicNo() + "','" + issueId + "');";
	}

	public String getOnClickPlus(List<EncounterSectionNote> notes, SectionParameters sectionParams, String cppIssues)
	{
		return getShowEditJavascriptCallString(null, notes.size(), sectionParams, cppIssues);
	}

	public String getSummaryCode()
	{
		return NotesService.getSummaryCodeFromSystemCode(getSectionId());
	}

	public EncounterSection getDefaultSection(SectionParameters sectionParams)
	{
		return getSection(sectionParams, EncounterSectionService.INITIAL_ENTRIES_TO_SHOW, EncounterSectionService.INITIAL_OFFSET);
	}

	public EncounterSection getSection(SectionParameters sectionParams, Integer limit, Integer offset)
	{
		// Get issue id from type
		Issue issue = issueDao.findByCode(getSectionId());

		// XXX: don't include the cpp issue
		//String cppIssues = issue.getId() + ";" + issue.getCode() + ";" + issue.getDescription();
		String cppIssues = "";

		EncounterSection section = new EncounterSection();

		section.setTitleKey(getSectionTitleKey());
		section.setColour(getSectionTitleColour());
		// CPP notes should not be condensed into single lines
		section.setDisplayOnSingleLine(false);
		//section.setCppIssues(cppIssues);

		List<EncounterSectionNote> notes = getNotes(sectionParams, issue.getIssueId(), cppIssues);

		section.setOnClickPlus(getOnClickPlus(notes, sectionParams, cppIssues));
		section.setOnClickTitle(getOnClickTitle(sectionParams, issue.getIssueId()));

		section.setNotes(notes);

		return section;
	}

	private String getShowEditJavascriptCallString(EncounterCPPNote note, int noteCount,
												   SectionParameters sectionParams, String cppIssues)
	{
		ResourceBundle oscarR = ResourceBundle.getBundle("oscarResources", sectionParams.getLocale());
		String title = oscarR.getString(getSectionTitleKey());

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		String noteJsonString;
		try
		{
			noteJsonString = mapper.writeValueAsString(note);
		}
		catch(JsonProcessingException e)
		{
			noteJsonString = "";
		}

		String onClickString = "return cppNote.showEdit(" +
				"event," +
				"'" + StringEscapeUtils.escapeHtml(getSectionId()) + "'," +
				"'" + StringEscapeUtils.escapeHtml(title) + "'," +
				StringEscapeUtils.escapeHtml(Integer.toString(noteCount)) + "," +
				"'" + StringEscapeUtils.escapeHtml(cppIssues) + "'," +
				"'" + StringEscapeUtils.escapeHtml(sectionParams.getDemographicNo()) + "'," +
				"'" + StringEscapeUtils.escapeHtml(StringEscapeUtils.escapeJavaScript(noteJsonString)) + "'" +
				");";

		return onClickString;
	}

	private List<EncounterSectionNote> getNotes(SectionParameters sectionParams, long issueId, String cppIssues)
	{
		// Get notes for that type
		List<EncounterSectionNote> out = new ArrayList<>();

		String[] issueIds = new String[1];
		issueIds[0] = Long.toString(issueId);
		List<EncounterCPPNote> notes = caseManagementNoteDao.getCPPNotes(sectionParams.getDemographicNo(), issueIds);

		for(EncounterCPPNote note: notes)
		{
			String title = "Rev:" + note.getRevision() + " - Last update:" +
					ConversionUtils.toDateTimeString(note.getUpdateDate(), ConversionUtils.DEFAULT_DATE_PATTERN);

			EncounterSectionNote sectionNote = new EncounterSectionNote();
			sectionNote.setText(note.getNote());
			sectionNote.setTextLineArray(note.getNote().split("\\r?\\n"));
			sectionNote.setTitle(title);
			sectionNote.setEditors(note.getEditors());
			sectionNote.setRevision(note.getRevision());
			sectionNote.setObservationDate(note.getObservationDate());
			sectionNote.setNoteIssuesString(getNoteIssueString(note));
			sectionNote.setNoteExtsString(getNoteExtString(note));
			sectionNote.setOnClick(getShowEditJavascriptCallString(note, notes.size(), sectionParams, cppIssues));
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
