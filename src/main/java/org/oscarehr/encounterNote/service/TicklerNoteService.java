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

import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.oscarehr.encounterNote.model.Issue;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TicklerNoteService extends HistoryNoteService
{
	/**
	 * Create a new tickler note based on an already existing tickler note.
	 * If there is no previous note associated with this tickler, a new note will be created,
	 * If a previous note exists, this will create a new note with the given text appended to the previous notes' text
	 * @param noteText - the new text for the new note
	 * @param tickler - the ticker
	 * @param providerNo - the provider number for the note
	 * @param demographicNo - the demographic number
	 * @return - the new note
	 */
	public CaseManagementNote saveTicklerNoteFromPrevious(String noteText, Tickler tickler, String providerNo, Integer demographicNo)
	{
		CaseManagementNoteLink link = caseManagementNoteLinkDao.findLatestTicklerNoteLinkById(tickler.getId());
		CaseManagementNote ticklerNote;
		boolean addNoteHeader = false;
		if(link != null)
		{
			CaseManagementNote previousNote = link.getNote();
			ticklerNote = new CaseManagementNote(previousNote);// get a copy without an ID
			ticklerNote.setNote(previousNote.getNote() + "\n\n" + noteText);
			ticklerNote.setUuid(null); // because this copy should be saved as a new note
		}
		else
		{
			ticklerNote = new CaseManagementNote();
			ticklerNote.setNote(noteText);
			addNoteHeader = true;
		}
		return saveTicklerNote(ticklerNote, tickler, providerNo, demographicNo, addNoteHeader);
	}

	/**
	 * save a new tickler note. auto-sets all the note requirements needed to make the given note appear as a tickler note
	 * @param noteText - the note text to be saved
	 * @param tickler - the tickler to link the note with
	 * @param providerNo - the provider number for the note
	 * @param demographicNo - the demographic number
	 * @return - the new note
	 */
	public CaseManagementNote saveTicklerNote(String noteText, Tickler tickler, String providerNo, Integer demographicNo)
	{
		CaseManagementNote ticklerNote = new CaseManagementNote();
		ticklerNote.setNote(noteText);
		return saveTicklerNote(ticklerNote, tickler, providerNo, demographicNo);
	}

	/**
	 * save a new tickler note. auto-sets all the note requirements needed to make the given note appear as a tickler note
	 * @param note - the note to be saved
	 * @param tickler - the tickler to link the note with
	 * @param providerNo - the provider number for the note
	 * @param demographicNo - the demographic number
	 * @return - the new note
	 */
	public CaseManagementNote saveTicklerNote(CaseManagementNote note, Tickler tickler, String providerNo, Integer demographicNo)
	{
		note.setDemographic(demographicDao.find(demographicNo));
		note.setProvider(providerDataDao.find(providerNo));
		return saveTicklerNote(note, tickler, true);
	}

	protected CaseManagementNote saveTicklerNote(CaseManagementNote note, Tickler tickler, String providerNo, Integer demographicNo, boolean addNoteHeader)
	{
		note.setDemographic(demographicDao.find(demographicNo));
		note.setProvider(providerDataDao.find(providerNo));
		return saveTicklerNote(note, tickler, addNoteHeader);
	}

	/**
	 * save a new tickler note. auto-sets all the note requirements needed to make the given note appear as a tickler note
	 * @param note - the note to be saved
	 * @param tickler - the tickler to link the note with
	 * @return - the new note
	 */
	public CaseManagementNote saveTicklerNote(CaseManagementNote note, Tickler tickler)
	{
		return saveTicklerNote(note, tickler, true);
	}
	protected CaseManagementNote saveTicklerNote(CaseManagementNote note, Tickler tickler, boolean addNoteHeader)
	{
		if(note.getSigningProvider() == null)
		{
			note.setSigningProvider(note.getProvider());
		}
		String headerText = addNoteHeader ? getNoteHeaderText(Tickler.HEADER_NAME) + "\n" : "";
		note.setNote(headerText + note.getNote() + "\n" + getSignatureText(note.getSigningProvider()));
		note.setArchived(false);
		note = saveHistoryNote(note, Issue.SUMMARY_CODE_TICKLER_NOTE);

		CaseManagementNoteLink link = new CaseManagementNoteLink();
		link.setNote(note);
		link.setLinkedTicklerId(tickler.getId());
		caseManagementNoteLinkDao.persist(link);

		return note;
	}
}
