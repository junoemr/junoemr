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
import org.oscarehr.encounterNote.converter.TempNoteToModelConverter;
import org.oscarehr.encounterNote.dao.CaseManagementTmpSaveDao;
import org.oscarehr.encounterNote.model.CaseManagementTmpSave;
import org.oscarehr.encounterNote.model.TempNoteModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class TempNoteService
{
	@Autowired
	protected CaseManagementTmpSaveDao caseManagementTmpSaveDao;

	@Autowired
	private ProgramManager programManager;

	@Autowired
	protected TempNoteToModelConverter tempNoteToModelConverter;

	/**
	 * retrieve a tempNote if it exists, keyed on the provider and demographic id
	 * @param providerId the provider id the note is keyed on
	 * @param demographicId the demographic id the note is keyed on
	 * @return the optional model
	 */
	public Optional<TempNoteModel> getTempNote(String providerId, Integer demographicId)
	{
		Optional<CaseManagementTmpSave> existingTempSave = caseManagementTmpSaveDao.findOptional(providerId, demographicId);
		return existingTempSave.map((tmpSave) -> tempNoteToModelConverter.convert(tmpSave));
	}

	/**
	 * create or update a temp note, keyed on the provider and demographic id
	 * @param providerId the provider id the note is keyed on
	 * @param demographicId the demographic id the note is keyed on
	 * @param noteInput the note text to save
	 * @param noteId the optional note id, for linking to an existing note
	 * @return the updated model
	 */
	public TempNoteModel setTempNote(String providerId, Integer demographicId, String noteInput, Integer noteId)
	{
		Optional<CaseManagementTmpSave> existingTempSave = caseManagementTmpSaveDao.findOptional(providerId, demographicId);
		CaseManagementTmpSave tempSave = existingTempSave.orElse(new CaseManagementTmpSave());

		tempSave.setNoteId(noteId);
		tempSave.setNote(noteInput);
		tempSave.setUpdateDateTime(ZonedDateTime.now());

		if(existingTempSave.isPresent())
		{
			caseManagementTmpSaveDao.merge(tempSave);
		}
		else
		{
			tempSave.setDemographicNo(demographicId);
			tempSave.setProviderNo(providerId);
			tempSave.setProgramId(programManager.getProgramIdByProgramName("OSCAR"));// can we remove program id outright?
			caseManagementTmpSaveDao.persist(tempSave);
		}
		return tempNoteToModelConverter.convert(tempSave);
	}

	/**
	 * delete the temp save note if it exists
	 * @param providerId the provider id the note is keyed on
	 * @param demographicId the demographic id the note is keyed on
	 * @return true if a temp note was deleted, false otherwise
	 */
	public boolean deleteTempNote(String providerId, Integer demographicId)
	{
		Optional<CaseManagementTmpSave> existingTempSave = caseManagementTmpSaveDao.findOptional(providerId, demographicId);
		if(existingTempSave.isPresent())
		{
			caseManagementTmpSaveDao.remove(existingTempSave.get());
			return true;
		}
		return false;
	}
}
