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
package org.oscarehr.common.hl7.copd.mapper.mediplan;

import ca.uhn.hl7v2.HL7Exception;
import org.oscarehr.common.hl7.copd.mapper.AlertMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.encounterNote.model.CaseManagementNote;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlertMapperMediplan extends AlertMapper
{
	public AlertMapperMediplan(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.MEDIPLAN);
	}

	@Override
	public List<CaseManagementNote> getReminderNoteList() throws HL7Exception
	{
		int numNotes = getNumAlerts();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for (int i = 0; i < numNotes; i++)
		{
			CaseManagementNote note = getReminderNote(i, importSource);
			if (note != null && !isNoteFilteredMediplan(note))
			{
				noteList.add(note);
			}
		}
		return noteList;
	}

	private boolean isNoteFilteredMediplan(CaseManagementNote note)
	{
		return (note.getNote().indexOf(HistoryNoteMapperMediplan.MEDIPLAN_FAMILY_HISTORY_ID) == 0 ||
				note.getNote().indexOf(HistoryNoteMapperMediplan.MEDIPLAN_MEDICAL_NOTE_ID_1) == 0 ||
				note.getNote().indexOf(HistoryNoteMapperMediplan.MEDIPLAN_MEDICAL_NOTE_ID_2) == 0 ||
				note.getNote().indexOf(HistoryNoteMapperMediplan.MEDIPLAN_SOCIAL_HISTORY_ID) == 0 ||
				note.getNote().indexOf(AllergyMapperMediplan.MEDIPLAN_ALLERGY_NOTE_ID) == 0);
	}

	@Override
	public CaseManagementNote getReminderNote(int rep, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		CaseManagementNote note = null;

		String noteText = getNoteText(rep);
		if(noteText != null)
		{
			note = new CaseManagementNote();

			Date date = getAlertDate(rep);
			note.setObservationDate(date);
			note.setUpdateDate(date);
			note.setNote(noteText.replace(" / ", "\n"));
		}
		return note;
	}
}
