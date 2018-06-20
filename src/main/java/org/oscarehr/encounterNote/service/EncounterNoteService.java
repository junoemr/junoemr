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
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class EncounterNoteService
{
	@Autowired
	CaseManagementNoteDao caseManagementNoteDao;

	@Autowired
	CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Autowired
	ProgramManager programManager;

	@Autowired
	SecRoleDao secRoleDao;

	public CaseManagementNote saveChartNote(CaseManagementNote note)
	{
		return saveNote(note);
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

	private CaseManagementNote saveNote(CaseManagementNote note)
	{
		note.setUpdateDate(new Date());

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
}
