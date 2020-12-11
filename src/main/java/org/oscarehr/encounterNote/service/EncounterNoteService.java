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

import org.oscarehr.PMmodule.dao.ProgramDao;
import org.oscarehr.PMmodule.model.Program;
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.common.model.Tickler;
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
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.oscarehr.ws.rest.to.model.NoteIssueTo1;
import org.oscarehr.ws.rest.to.model.NoteTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.time.LocalDateTime;
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

	@Autowired
	ProgramDao programDao;

	public NoteIssueTo1 getLatestUnsignedNote(Integer demographicNo, Integer providerNo)
	{
		CaseManagementNote note = caseManagementNoteDao.getLatestUnsignedNote(demographicNo, providerNo);

		return getNoteTo1FromNote(note);
	}

	public NoteIssueTo1 getNoteToEdit(Integer demographicNo, Integer noteId)
	{
		CaseManagementNote note = caseManagementNoteDao.find(noteId.longValue());

		if (!note.getDemographic().getId().equals(demographicNo))
		{
			return null;
		}

		return getNoteTo1FromNote(note);
	}

	public CaseManagementNote getAnnotation(Integer parentNoteId)
	{
		if(parentNoteId == null)
		{
			return null;
		}

		CaseManagementNoteLink noteLink = caseManagementNoteLinkDao.getNoteLinkByTableIdAndTableName(
				parentNoteId, CaseManagementNoteLink.CASEMGMTNOTE);

		CaseManagementNote note = null;
		if(noteLink != null)
		{
			note = caseManagementNoteDao.find(noteLink.getNote().getNoteId());
		}

		return note;
	}

	private NoteIssueTo1 getNoteTo1FromNote(CaseManagementNote note)
	{
		if(note == null)
		{
			return null;
		}

		NoteTo1 noteTo = new NoteTo1();

		boolean editable = note.getSigned() || note.getLocked();
		SecRole secRole = null;
		if(note.getReporterCaisiRole() != null)
		{
			secRole = secRoleDao.find(Integer.parseInt(note.getReporterCaisiRole()));
		}

		noteTo.setNoteId(note.getNoteId().intValue());
		try
		{
			noteTo.setAppointmentNo(note.getAppointment().getId().intValue());
		}
		catch(EntityNotFoundException e)
		{
			// Do nothing
		}
		noteTo.setObservationDate(note.getObservationDate());
		noteTo.setProviderNo(note.getProvider().getProviderNo().toString());
		Program program = programDao.getProgram(Integer.parseInt(note.getProgramNo()));
		noteTo.setProgramName(program.getName());
		noteTo.setUuid(note.getUuid());
		noteTo.setUpdateDate(note.getUpdateDate());
		//noteTo.setDocumentId((row[column++]));
		noteTo.setArchived(note.getArchived());
		noteTo.setIsSigned(note.getSigned());
		noteTo.setIsEditable(editable);
		noteTo.setRevision(caseManagementNoteDao.getRevision(note.getUuid()).toString());
		noteTo.setProviderName(note.getProvider().getDisplayName());
		noteTo.setStatus(note.getStatus());
		//noteTo.setLocation();
		noteTo.setRoleName(secRole.getName());
		noteTo.setHasHistory(note.getHasHistory());
		noteTo.setLocked(note.getLocked());
		noteTo.setNote(note.getNote());
		noteTo.setDocument(false);
		noteTo.setDeleted(false);
		noteTo.setRxAnnotation(false);
		noteTo.setEformData(false);
		noteTo.setEncounterForm(false);
		noteTo.setInvoice(false);
		noteTo.setTicklerNote(false);
		noteTo.setEncounterType(note.getEncounterType());

		noteTo.setEditorNames(new ArrayList<String>(caseManagementNoteDao.getEditorNames(note.getUuid())));

		noteTo.setIssueDescriptions(note.getIssueDescriptions());
		noteTo.setReadOnly(false);
		//noteTo.setGroupNote(getBooleanFromInteger(row[column++]));
		noteTo.setCpp(false);
		noteTo.setEncounterTime(note.getEncounterTime());
		noteTo.setEncounterTransportationTime(note.getEncounterTransportationTime());


		//assigned issues..remove the CPP one.
		List<CaseManagementIssueNote> issueNotes = new ArrayList<>(note.getIssueNoteList());

		List<CaseManagementIssueTo1> issueTos = new ArrayList<>();
		for(CaseManagementIssueNote issueNote : issueNotes)
		{
			issueTos.add(CaseManagementIssueConverter.getAsTransferObject(issueNote.getId().getCaseManagementIssue()));
		}

		//set NoteIssue to return
		NoteIssueTo1 noteIssue = new NoteIssueTo1();
		noteIssue.setEncounterNote(noteTo);
		noteIssue.setAssignedCMIssues(issueTos);

		return noteIssue;
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
		CaseManagementNoteLink link = caseManagementNoteLinkDao.findLatestByTableAndTableId(CaseManagementNoteLink.TICKLER, tickler.getId());
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
		link.setTickler(tickler.getId());
		caseManagementNoteLinkDao.persist(link);

		return note;
	}

	public String getNoteHeaderText(String reason)
	{
		String dateStr = ConversionUtils.toDateTimeString(LocalDateTime.now(), ConversionUtils.DISPLAY_DATE_PATTERN);
		return "[" + dateStr + " .: " + reason + "]";
	}

	public String getSignatureText(ProviderData signingProvider)
	{
		String dateStr = ConversionUtils.toDateTimeString(LocalDateTime.now(), ConversionUtils.DISPLAY_DATE_TIME_PATTERN);
		return "[Signed on " + dateStr + " by " + StringUtils.trimToEmpty(signingProvider.getFirstName() + " " + signingProvider.getLastName()) + "]";
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
