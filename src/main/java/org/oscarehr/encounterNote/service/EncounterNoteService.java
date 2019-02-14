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
package org.oscarehr.encounterNote.service;

import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.encounterNote.dao.CaseManagementIssueDao;
import org.oscarehr.encounterNote.dao.CaseManagementIssueNoteDao;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.dao.CaseManagementTmpSaveDao;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.CaseManagementIssue;
import org.oscarehr.encounterNote.model.CaseManagementIssueNote;
import org.oscarehr.encounterNote.model.CaseManagementIssueNotePK;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EncounterNoteService
{
	@Autowired
	CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	CaseManagementIssueNoteDao caseManagementIssueNoteDao;

	@Autowired
	CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Autowired
	CaseManagementIssueDao caseManagementIssueDao;

	@Autowired
	CaseManagementTmpSaveDao caseManagementTmpSaveDao;

	@Autowired
	IssueDao issueDao;

	@Autowired
	ProgramManager programManager;

	@Autowired
	SecRoleDao secRoleDao;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	DemographicDao demographicDao;

	public CaseManagementNote saveChartNote(CaseManagementNote note)
	{
		return saveChartNote(note, null);
	}
	public CaseManagementNote saveChartNote(CaseManagementNote note, String providerNo, Integer demographicNo)
	{
		return saveChartNote(note, null, providerNo, demographicNo);
	}
	public CaseManagementNote saveChartNote(CaseManagementNote note, List<Issue> issueList, String providerNo, Integer demographicNo)
	{
		note.setDemographic(demographicDao.find(demographicNo));
		note.setProvider(providerDataDao.find(providerNo));
		return saveChartNote(note, issueList);
	}
	public CaseManagementNote saveChartNote(CaseManagementNote note, List<Issue> issueList)
	{
		note.setIncludeIssueInNote(true);
		note = saveNote(note);

		if(issueList != null && !issueList.isEmpty())
		{
			for(Issue issue : issueList)
			{
				// if there exists a casemgmt_issue for the demographic, use that
				// otherwise, create a new casemgmt_issue
				CaseManagementIssue caseManagementIssue = caseManagementIssueDao.findByIssueId(issue.getId());
				if(caseManagementIssue == null)
				{
					caseManagementIssue = new CaseManagementIssue();
					caseManagementIssue.setAcute(false);
					caseManagementIssue.setCertain(false);
					caseManagementIssue.setMajor(false);
					caseManagementIssue.setProgramId(programManager.getDefaultProgramId());
					caseManagementIssue.setResolved(false);
					caseManagementIssue.setIssue(issue);
					caseManagementIssue.setType(issue.getRole());
					caseManagementIssue.setDemographic(note.getDemographic());
					caseManagementIssue.setUpdateDate(note.getUpdateDate());

					caseManagementIssueDao.persist(caseManagementIssue);
				}
				// link the note and the issue
				CaseManagementIssueNotePK caseManagementIssueNotePK = new CaseManagementIssueNotePK(caseManagementIssue, note);
				CaseManagementIssueNote caseManagementIssueNote = new CaseManagementIssueNote(caseManagementIssueNotePK);
				caseManagementIssueNoteDao.persist(caseManagementIssueNote);
			}
		}
		return note;
	}

	public CaseManagementNote saveAllergyNote(CaseManagementNote note, Allergy allergy)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		note = saveNote(note);

		CaseManagementNoteLink link = new CaseManagementNoteLink();
		link.setNote(note);
		link.setAllergy(allergy.getAllergyId());
		caseManagementNoteLinkDao.persist(link);

		return note;
	}

	public CaseManagementNote saveDrugNote(CaseManagementNote note, Drug drug)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		note = saveNote(note);

		CaseManagementNoteLink link = new CaseManagementNoteLink();
		link.setNote(note);
		link.setDrug(drug.getId());
		caseManagementNoteLinkDao.persist(link);

		return note;
	}

	public CaseManagementNote saveMedicalHistoryNote(CaseManagementNote note)
	{
		return saveHistoryNote(note, Issue.SUMMARY_CODE_MEDICAL_HISTORY);
	}

	public CaseManagementNote saveSocialHistoryNote(CaseManagementNote note)
	{
		return saveHistoryNote(note, Issue.SUMMARY_CODE_SOCIAL_HISTORY);
	}

	public CaseManagementNote saveFamilyHistoryNote(CaseManagementNote note)
	{
		return saveHistoryNote(note, Issue.SUMMARY_CODE_FAMILY_HISTORY);
	}

	public CaseManagementNote saveReminderNote(CaseManagementNote note)
	{
		return saveHistoryNote(note, Issue.SUMMARY_CODE_REMINDERS);
	}

	private CaseManagementNote saveHistoryNote(CaseManagementNote note, String summaryCode)
	{
		CaseManagementIssue caseManagementIssue = caseManagementIssueDao.findByIssueCode(
				note.getDemographic().getDemographicId(), summaryCode);

		// save the base note
		note.setSigned(true);
		note.setIncludeIssueInNote(true);
		note.setPosition(1);
		note = saveNote(note);

		// create the demographic specific issue if it does not exist
		if(caseManagementIssue == null)
		{
			// grab the master issue for reference/link
			Issue issue = issueDao.findByCode(summaryCode);

			caseManagementIssue = new CaseManagementIssue();
			caseManagementIssue.setAcute(false);
			caseManagementIssue.setCertain(false);
			caseManagementIssue.setMajor(false);
			caseManagementIssue.setProgramId(programManager.getDefaultProgramId());
			caseManagementIssue.setResolved(false);
			caseManagementIssue.setIssue(issue);
			caseManagementIssue.setType(issue.getRole());
			caseManagementIssue.setDemographic(note.getDemographic());
			caseManagementIssue.setUpdateDate(note.getUpdateDate());

			caseManagementIssueDao.persist(caseManagementIssue);
		}

		// link the note and the issue
		CaseManagementIssueNotePK caseManagementIssueNotePK = new CaseManagementIssueNotePK(caseManagementIssue, note);
		CaseManagementIssueNote caseManagementIssueNote = new CaseManagementIssueNote(caseManagementIssueNotePK);
		caseManagementIssueNoteDao.persist(caseManagementIssueNote);
		return note;
	}

	private CaseManagementNote saveNote(CaseManagementNote note)
	{
		if(note.getUpdateDate() == null)
		{
			note.setUpdateDate(new Date());
		}
		if(note.getObservationDate() == null)
		{
			note.setObservationDate(new Date());
		}
		if(note.getBillingCode() == null)
		{
			note.setBillingCode("");
		}
		if(note.getEncounterType() == null)
		{
			note.setEncounterType("");
		}
		if(note.getProgramNo() == null)
		{
			note.setProgramNo(String.valueOf(programManager.getDefaultProgramId()));
		}
		if(note.getHistory() == null)
		{
			note.setHistory(note.getNote());
		}
		if(note.getReporterCaisiRole() == null)
		{
			note.setReporterCaisiRole(getCaisiRole());
		}
		if(note.getReporterProgramTeam() == null)
		{
			note.setReporterProgramTeam("0");
		}
		if(note.getPosition() == null)
		{
			note.setPosition(0);
		}

		if(note.getUuid() == null)
		{
			note.setUuid(UUID.randomUUID().toString());
		}

		caseManagementNoteDao.persist(note);

		return note;
	}

	/**
	 * one day we will get rid of this
	 */
	private String getCaisiRole()
	{
		SecRole secRole = secRoleDao.findByName("doctor");
		if(secRole != null)
		{
			return String.valueOf(secRole.getId());
		}
		return "0";
	}

	/**
	 * create a new copy of the existing note, without an ID
	 * @param noteId - id of the note to copy
	 * @return a copy of the note
	 */
	public CaseManagementNote getNoteRevisionCopy(Long noteId)
	{
		CaseManagementNote noteToCopy = caseManagementNoteDao.find(noteId);
		return new CaseManagementNote(noteToCopy);
	}

	/**
	 * This method is intended for use in the case that multiple saves are required for constructing a single note revision,
	 * specifically multiple flowsheet measurements.
	 * This should be avoided in any other use case if at all possible.
	 * @deprecated to discourage future use
	 * @return the persisted note model
	 */
	@Deprecated
	public CaseManagementNote addNewNoteWithUUID(String uuid, String textToAppend, String providerNo, Integer demographicNo)
	{
		ProviderData provider = providerDataDao.find(providerNo);
		Demographic demographic = demographicDao.find(demographicNo);

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = new Date();
		String formattedDate = "[" + df.format(date) + " .: ]";

		CaseManagementNote newNote = new CaseManagementNote();
		newNote.setNote(formattedDate + "\n" + textToAppend);
		newNote.setProvider(provider);
		newNote.setDemographic(demographic);
		newNote.setSigned(true);
		newNote.setSigningProvider(provider);
		newNote.setUuid(uuid);
		return saveChartNote(newNote);
	}
	/**
	 * This method is intended for use in the case that multiple saves are required for constructing a single note revision,
	 * specifically multiple flowsheet measurements.
	 * This should be avoided in any other use case if at all possible.
	 * @deprecated to discourage future use
	 * @return the persisted note model
	 */
	@Deprecated
	public CaseManagementNote appendTextToNote(CaseManagementNote note, String textToAppend, String providerNo, Integer demographicNo)
	{
		note.setProvider(providerDataDao.find(providerNo));
		note.setDemographic(demographicDao.find(demographicNo));
		note.setNote(note.getNote() + "\n" + textToAppend);
		note.setHistory(note.getNote());
		caseManagementNoteDao.merge(note);
		return note;
	}
}
