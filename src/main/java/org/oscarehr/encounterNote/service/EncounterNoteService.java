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

import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.dataMigration.converter.in.note.EncounterNoteModelToDbConverter;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.document.model.Document;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.encounterNote.model.CaseManagementIssue;
import org.oscarehr.encounterNote.model.CaseManagementIssueNote;
import org.oscarehr.encounterNote.model.CaseManagementIssueNotePK;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EncounterNoteService extends BaseNoteService
{
	@Autowired
	protected EncounterNoteModelToDbConverter encounterNoteModelToDbConverter;



	public CaseManagementNote saveChartNote(EncounterNote noteModel, Demographic demographic)
	{
		CaseManagementNote note = encounterNoteModelToDbConverter.convert(noteModel);

		note.setDemographic(demographic);
		CaseManagementNote savedNote = saveChartNote(note, null);
		//TODO handle partial dates?

		return savedNote;
	}
	public List<CaseManagementNote> saveChartNotes(List<EncounterNote> noteModelList, Demographic demographic)
	{
		List<CaseManagementNote> dbNoteList = new ArrayList<>(noteModelList.size());

		preSetDefaultProgramIdAndCaisiRole(noteModelList);
		for(EncounterNote encounterNote : noteModelList)
		{
			dbNoteList.add(saveChartNote(encounterNote, demographic));
		}
		return dbNoteList;
	}

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

		CaseManagementNoteLink link = new CaseManagementNoteLink(note);
		link.setLinkedAllergyId(allergy.getAllergyId());
		return saveNote(note);
	}

	public CaseManagementNote saveDrugNote(CaseManagementNote note, Drug drug)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		CaseManagementNoteLink link = new CaseManagementNoteLink(note);
		link.setLinkedDrugId(drug.getId());

		return saveNote(note);
	}

	public CaseManagementNote saveDocumentNote(CaseManagementNote note, Document document)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		CaseManagementNoteLink link = new CaseManagementNoteLink(note);
		link.setLinkedDocumentId(document.getId());

		return saveNote(note);
	}

	public CaseManagementNote saveEFormNote(CaseManagementNote note, EFormData eForm, List<Issue> issues)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		CaseManagementNoteLink link = new CaseManagementNoteLink(note);
		link.setLinkedEFormId(eForm.getId());

		return saveChartNote(note, issues);
	}

	public CaseManagementNote saveLabObxNote(EncounterNote noteModel, Demographic demographic, Hl7TextMessage hl7TextMessage, int obrIndex, int obxIndex)
	{
		CaseManagementNote note = encounterNoteModelToDbConverter.convert(noteModel);
		note.setDemographic(demographic);
		return saveLabObxNote(note, hl7TextMessage, obrIndex, obxIndex);
	}

	public CaseManagementNote saveLabObxNote(CaseManagementNote note, Hl7TextMessage hl7TextMessage, int obrIndex, int obxIndex)
	{
		note.setIncludeIssueInNote(true);
		note.setSigned(true);
		note.setArchived(false);

		CaseManagementNoteLink link = new CaseManagementNoteLink(note);
		link.setLinkedHl7LabId(hl7TextMessage.getId());
		link.setOtherId(obrIndex + "-" + obxIndex);

		return saveNote(note);
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

	/**
	 * This method is intended for use of building a hashmap of CPP notes for a specific demographic
	 * This should be avoided in any other use case if at all possible.
	 * @deprecated to discourage future use of returning a hash-map
	 * @return the completed hash-map with all cpp notes to print
	 */
	@Deprecated
	public HashMap<String, List<CaseManagementNote>> buildCPPHashMapForDemographic(Integer demographicNo)
	{
		HashMap<String, List<CaseManagementNote>>cpp = new HashMap<>();

		List<CaseManagementNote> allCPPNotes = caseManagementNoteDao.findLatestRevisionOfAllNotes(demographicNo, true);
		List<CaseManagementNote> medicalHistoryNotes = new ArrayList<>();
		List<CaseManagementNote> socialHistoryNotes = new ArrayList<>();
		List<CaseManagementNote> familyHistoryNotes = new ArrayList<>();
		List<CaseManagementNote> reminderNotes = new ArrayList<>();
		List<CaseManagementNote> otherMedsNotes = new ArrayList<>();
		List<CaseManagementNote> concernsNotes = new ArrayList<>();
		List<CaseManagementNote> riskFactorsNotes = new ArrayList<>();

		// For each cpp note, use the note id to determine the issue code string
		for (CaseManagementNote note : allCPPNotes)
		{
			// Get the issue code
			List<CaseManagementIssueNote> issueNotes = note.getIssueNoteList();

			for (CaseManagementIssueNote issueNote : issueNotes)
			{
				// Find a match and add the note to the correct list
				Issue currentIssue = issueNote.getId().getCaseManagementIssue().getIssue();

				switch (currentIssue.getCode())
				{
					case Issue.SUMMARY_CODE_MEDICAL_HISTORY:
						medicalHistoryNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_SOCIAL_HISTORY:
						socialHistoryNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_FAMILY_HISTORY:
						familyHistoryNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_REMINDERS:
						reminderNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_OTHER_MEDS:
						otherMedsNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_CONCERNS:
						concernsNotes.add(note);
						break;
					case Issue.SUMMARY_CODE_RISK_FACTORS:
						riskFactorsNotes.add(note);
						break;
				}
			}
		}

		cpp.put(Issue.SUMMARY_CODE_MEDICAL_HISTORY, medicalHistoryNotes);
		cpp.put(Issue.SUMMARY_CODE_SOCIAL_HISTORY, socialHistoryNotes);
		cpp.put(Issue.SUMMARY_CODE_FAMILY_HISTORY, familyHistoryNotes);
		cpp.put(Issue.SUMMARY_CODE_REMINDERS, reminderNotes);
		cpp.put(Issue.SUMMARY_CODE_OTHER_MEDS, otherMedsNotes);
		cpp.put(Issue.SUMMARY_CODE_CONCERNS, concernsNotes);
		cpp.put(Issue.SUMMARY_CODE_RISK_FACTORS, riskFactorsNotes);

		return cpp;
	}
}
