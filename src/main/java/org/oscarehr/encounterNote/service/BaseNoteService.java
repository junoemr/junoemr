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

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.PMmodule.dao.ProgramDao;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.dataMigration.model.encounterNote.BaseNote;
import org.oscarehr.demographic.dao.DemographicDao;
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
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.model.SecRole;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public abstract class BaseNoteService
{
	@Autowired
	protected CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	protected CaseManagementIssueNoteDao caseManagementIssueNoteDao;

	@Autowired
	protected CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Autowired
	protected CaseManagementIssueDao caseManagementIssueDao;

	@Autowired
	protected CaseManagementTmpSaveDao caseManagementTmpSaveDao;

	@Autowired
	protected IssueDao issueDao;

	@Autowired
	protected ProgramManager programManager;

	@Autowired
	protected SecRoleDao secRoleDao;

	@Autowired
	protected ProviderDataDao providerDataDao;

	@Autowired
	protected DemographicDao demographicDao;

	@Autowired
	protected PartialDateDao partialDateDao;

	@Autowired
	protected ProgramDao programDao;

	public NoteIssueTo1 getLatestUnsignedNote(Integer demographicNo, Integer providerNo)
	{
		CaseManagementNote note = caseManagementNoteDao.getLatestUnsignedNote(demographicNo, providerNo);

		return getNoteTo1FromNote(note);
	}

	public NoteIssueTo1 getNoteToEdit(Integer demographicNo, Integer noteId)
	{
		CaseManagementNote note = caseManagementNoteDao.find(noteId.longValue());

		if(note == null || !note.getDemographic().getId().equals(demographicNo))
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
			Appointment appointment = note.getAppointment();
			if(appointment != null)
			{
				noteTo.setAppointmentNo(appointment.getId().intValue());
			}
		}
		catch(EntityNotFoundException e)
		{
			// Do nothing
		}

		// Tickler notes and rx annotations can be edited, so they are checked and set in the transfer object.
		// The other statuses (cpp note, document, etc) aren't checked because they aren't editable.
		// TODO: add the other types?

		// Check if it's a tickler note (checked when copying case management issues)
		boolean isTicklerNote = false;

		// Check if it's an rx annotation
		boolean isRxAnnotation = false;

		CaseManagementNoteLink noteLink = caseManagementNoteLinkDao.getNoteLinkByNoteIdAndTableName(
				note, CaseManagementNoteLink.DRUGS);

		if(noteLink != null)
		{
			isRxAnnotation = true;
		}


		//assigned issues..remove the CPP one.
		List<CaseManagementIssueNote> issueNotes = new ArrayList<>(note.getIssueNoteList());

		List<CaseManagementIssueTo1> issueTos = new ArrayList<>();
		for(CaseManagementIssueNote issueNote : issueNotes)
		{
			CaseManagementIssueNotePK id = issueNote.getId();

			if(id != null)
			{

				CaseManagementIssue caseManagementIssue = id.getCaseManagementIssue();

				issueTos.add(CaseManagementIssueConverter.getAsTransferObject(caseManagementIssue));

				// A tickler note is identified by an attached issue
				if(caseManagementIssue != null)
				{
					Issue issue = caseManagementIssue.getIssue();

					if(Issue.SUMMARY_CODE_TICKLER_NOTE.equals(issue.getCode()))
					{
						isTicklerNote = true;
					}
				}
			}
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
		noteTo.setRxAnnotation(isRxAnnotation);
		noteTo.setEformData(false);
		noteTo.setEncounterForm(false);
		noteTo.setInvoice(false);
		noteTo.setTicklerNote(isTicklerNote);
		noteTo.setEncounterType(note.getEncounterType());

		noteTo.setEditorNames(new ArrayList<String>(caseManagementNoteDao.getEditorNames(note.getUuid())));

		noteTo.setIssueDescriptions(note.getIssueDescriptions());
		noteTo.setReadOnly(false);
		//noteTo.setGroupNote(getBooleanFromInteger(row[column++]));
		noteTo.setCpp(false);
		noteTo.setEncounterTime(note.getEncounterTime());
		noteTo.setEncounterTransportationTime(note.getEncounterTransportationTime());

		//set NoteIssue to return
		NoteIssueTo1 noteIssue = new NoteIssueTo1();
		noteIssue.setEncounterNote(noteTo);
		noteIssue.setAssignedCMIssues(issueTos);

		return noteIssue;
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

	protected CaseManagementNote saveNote(CaseManagementNote note)
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
	protected String getCaisiRole()
	{
		SecRole secRole = secRoleDao.findSystemDefaultRole();
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

	public void saveExtPartialDate(org.oscarehr.dataMigration.model.common.PartialDate dateToSave, Long extensionId)
	{
		partialDateDao.setPartialDate(dateToSave,
				PartialDate.TABLE.CASEMGMT_NOTE_EXT,
				Math.toIntExact(extensionId),
				PartialDate.FIELD_CASEMGMT_NOTE_EXT_VALUE);
	}

	/**
	 * load program ID outside of loop to prevent excess queries
	 */
	public void preSetDefaultProgramIdAndCaisiRole(List<? extends BaseNote> noteModelList)
	{
		String defaultProgramId = String.valueOf(programManager.getDefaultProgramId());
		String defaultRoleId = getCaisiRole();
		for(BaseNote noteModel : noteModelList)
		{
			if(noteModel.getProgramId() == null)
			{
				noteModel.setProgramId(defaultProgramId);
			}
			if(noteModel.getRoleId() == null)
			{
				noteModel.setRoleId(defaultRoleId);
			}
		}
	}
}
