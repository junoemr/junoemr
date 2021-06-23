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
package org.oscarehr.common.hl7.copd.mapper.medaccess;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.mapper.HistoryNoteMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.oscarehr.common.hl7.Hl7Const.HL7_SEGMENT_ZPB_10;

public class HistoryNoteMapperMedaccess extends HistoryNoteMapper
{
	public HistoryNoteMapperMedaccess(ZPD_ZTR message, int providerRep, CoPDRecordData recordData) throws HL7Exception
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.MEDACCESS, recordData);
	}

	@Override
	public int getNumMedicalHistoryNotes()
	{
		return getNumMedicalProblemsNotes() + getNumMedicalSurgicalNotes();
	}

	protected int getNumMedicalProblemsNotes()
	{
		// medaccess puts history notes in the ZPB section - normally used by DxImporter
		return provider.getZPBReps();
	}

	protected int getNumMedicalSurgicalNotes()
	{
		return provider.getZPRReps();
	}

	@Override
	public List<CaseManagementNote> getMedicalHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumMedicalHistoryNotes();
		int numSurgical = getNumMedicalSurgicalNotes();
		int numProblems = getNumMedicalProblemsNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for (int i = 0; i < numSurgical; i++)
		{
			CaseManagementNote note = getMedicalHistoryNote(i);
			if (note != null)
			{
				noteList.add(note);
			}
		}

		for (int i = 0; i < numProblems; i++)
		{
			CaseManagementNote note = getMedicalProblemNote(i);
			if (note != null)
			{
				noteList.add(note);
			}
		}

		return noteList;
	}

	/**
	 * Gets medical history information from any available ZPB segments.
	 * Information in this segment pertains to medical diagnosis (both unconfirmed and confirmed).
	 * This is slightly different from our other medical history fetching.
	 * @param rep rep to get ZPB information from
	 * @return base note object for medical history
	 * @throws HL7Exception
	 */
	protected CaseManagementNote getMedicalProblemNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();
		Date diagnosisDate = getMedProblemDiagnosisDate(rep);
		if (diagnosisDate == null)
		{
			diagnosisDate = oldestEncounterNoteDate;
		}
		else
		{
			// if the procedure date was actually valid, set the extended property
			CaseManagementNoteExt ext = new CaseManagementNoteExt();
			ext.setNote(note);
			ext.setKey(CaseManagementNoteExt.PROCEDUREDATE);
			ext.setDateValue(diagnosisDate);

			note.addExtension(ext);
		}

		note.setObservationDate(diagnosisDate);
		note.setUpdateDate(diagnosisDate);

		String noteText = StringUtils.trimToEmpty(getMedProblemNoteText(rep)).replaceAll("~crlf~", "\n");

		if (noteText.isEmpty())
		{
			String warning = "No CPP Med history note text, using dx description instead. (ZPB Rep: " + rep + ")";
			logger.info(warning);
			recordData.addMessage(HL7_SEGMENT_ZPB_10, String.valueOf(rep), warning);
			noteText = StringUtils.trimToEmpty(getMedProblemDiagnosisDescription(rep));
		}

		note.setNote(noteText + " - " + ConversionUtils.toDateString(diagnosisDate));

		return note;
	}

	public Date getMedProblemDiagnosisDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPB(rep).getZpb2_diagnosisDate().getTs1_TimeOfAnEvent().getValue());
	}

	public String getMedProblemDiagnosisDescription(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZPB(rep).getZpb3_diagnosisDescription().getValue());
	}

	public String getMedProblemDiagnosisCode(int rep) throws HL7Exception
	{
		// 3 parts here, unsure which to include:
		// 1 - identifier
		// 2 - text
		// 3 - name of coding system
		return StringUtils.trimToEmpty(provider.getZPB(rep).getZpb4_diagnosisCode().getCe2_Text().getValue());
	}


	public String getMedProblemNoteText(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZPB(rep).getZpb10_noteText().getValue());
	}
}
