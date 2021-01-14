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

import org.oscarehr.encounterNote.model.CaseManagementIssue;
import org.oscarehr.encounterNote.model.CaseManagementIssueNote;
import org.oscarehr.encounterNote.model.CaseManagementIssueNotePK;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.encounterNote.model.Issue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public abstract class HistoryNoteService extends BaseNoteService
{
	protected CaseManagementNote saveHistoryNote(CaseManagementNote note, String summaryCode)
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

	protected void addAnnotationLink(CaseManagementNote note, String annotationText)
	{
		if(annotationText != null)
		{
			CaseManagementNote annotationNote = buildAnnotationNote(note, annotationText);
			CaseManagementNoteLink annotationLink = new CaseManagementNoteLink(annotationNote);
			annotationLink.setLinkedCaseManagementNoteId(Math.toIntExact(note.getId()));
			saveNote(annotationNote); // will also save the link through cascade
		}
	}

	private CaseManagementNote buildAnnotationNote(CaseManagementNote note, String annotationText)
	{
		CaseManagementNote annotationNote = null;
		if(annotationText != null)
		{
			annotationNote = new CaseManagementNote();
			annotationNote.setNote(annotationText);
			annotationNote.setProvider(note.getProvider());
			annotationNote.setSigned(note.getSigned());
			annotationNote.setSigningProvider(note.getSigningProvider());
			annotationNote.setObservationDate(note.getObservationDate());
			annotationNote.setDemographic(note.getDemographic());
		}
		return annotationNote;
	}
}
