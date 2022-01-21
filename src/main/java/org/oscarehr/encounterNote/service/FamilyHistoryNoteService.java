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

import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.dataMigration.converter.in.note.FamilyHistoryNoteModelToDbConverter;
import org.oscarehr.dataMigration.model.encounterNote.FamilyHistoryNote;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.oscarehr.encounterNote.model.Issue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class FamilyHistoryNoteService extends HistoryNoteService
{

	@Autowired
	protected FamilyHistoryNoteModelToDbConverter familyHistoryNoteModelToDbConverter;

	public CaseManagementNote saveFamilyHistoryNote(FamilyHistoryNote noteModel, Demographic demographic)
	{
		CaseManagementNote note = familyHistoryNoteModelToDbConverter.convert(noteModel);

		note.setDemographic(demographic);
		CaseManagementNote savedNote = saveFamilyHistoryNote(note);
		addAnnotationLink(savedNote, noteModel.getAnnotation(), noteModel.getResidualInfo());

		if(savedNote.getNoteExtensionList() != null)
		{
			// now that notes have id's, save the partial date data
			for(CaseManagementNoteExt ext : savedNote.getNoteExtensionList())
			{
				if(CaseManagementNoteExt.STARTDATE.equals(ext.getKey()))
				{
					saveExtPartialDate(noteModel.getStartDate(), ext.getId());
				}
				if(CaseManagementNoteExt.RESOLUTIONDATE.equals(ext.getKey()))
				{
					saveExtPartialDate(noteModel.getResolutionDate(), ext.getId());
				}
			}
		}

		return savedNote;
	}
	public List<CaseManagementNote> saveFamilyHistoryNote(List<FamilyHistoryNote> noteModelList, Demographic demographic)
	{
		List<CaseManagementNote> dbNoteList = new ArrayList<>(noteModelList.size());

		preSetDefaultProgramIdAndCaisiRole(noteModelList);
		for(FamilyHistoryNote note : noteModelList)
		{
			dbNoteList.add(saveFamilyHistoryNote(note, demographic));
		}
		return dbNoteList;
	}

	public CaseManagementNote saveFamilyHistoryNote(CaseManagementNote note)
	{
		return saveHistoryNote(note, Issue.SUMMARY_CODE_FAMILY_HISTORY);
	}
}
