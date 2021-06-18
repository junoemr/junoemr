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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.mapper.HistoryNoteMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.oscarehr.util.MiscUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryNoteMapperMediplan extends HistoryNoteMapper
{
	//because mediplan sucks
	public static final String MEDIPLAN_MEDICAL_NOTE_ID_1="MEDIPLAN SURGICAL HISTORY SECTION";
	public static final String MEDIPLAN_MEDICAL_NOTE_ID_2="MEDIPLAN DEVELOPMENTAL HISTORY SECTION";
	public static final String MEDIPLAN_FAMILY_HISTORY_ID="MEDIPLAN FAMILY HISTORY SECTION";
	public static final String MEDIPLAN_SOCIAL_HISTORY_ID="MEDIPLAN CARE PLAN SECTION";

	private static final Logger logger = MiscUtils.getLogger();

	public HistoryNoteMapperMediplan(ZPD_ZTR message, int providerRep, CoPDRecordData recordData) throws HL7Exception
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.MEDIPLAN, recordData);
	}

	// ---------------------------------------------------------------------------------------

	private boolean isMedicalHistoryNoteMediplan(int rep) throws HL7Exception
	{
		String ZALText = provider.getZAL(rep).getZal5_alertTextSent().getValue();
		if ( ZALText != null)
		{
			 return ZALText.indexOf(MEDIPLAN_MEDICAL_NOTE_ID_1) == 0 || ZALText.indexOf(MEDIPLAN_MEDICAL_NOTE_ID_2) == 0;
		}
		return false;
	}

	private boolean isFamilyHistoryNoteMediplan(int rep) throws HL7Exception
	{
		String ZALText = provider.getZAL(rep).getZal5_alertTextSent().getValue();
		if ( ZALText != null)
		{
			return ZALText.indexOf(MEDIPLAN_FAMILY_HISTORY_ID) == 0;
		}
		return false;
	}

	private boolean isSocialHistoryNoteMediplan(int rep) throws HL7Exception
	{
		String ZALText = provider.getZAL(rep).getZal5_alertTextSent().getValue();
		if ( ZALText != null)
		{
			return ZALText.indexOf(MEDIPLAN_SOCIAL_HISTORY_ID) == 0;
		}
		return false;
	}

	@Override
	public List<CaseManagementNote> getSocialHistoryNoteList() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isSocialHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getSocialHistoryNote(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	@Override
	public List<CaseManagementNote> getFamilyHistoryNoteList() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isFamilyHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getFamilyHistoryNote(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	@Override
	public List<CaseManagementNote> getMedicalHistoryNoteList() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isMedicalHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getMedicalHistoryNote(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	@Override
	public CaseManagementNote getSocialHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		Date date = oldestEncounterNoteDate;
		note.setObservationDate(date);
		note.setUpdateDate(date);

		note.setNote(getSocialHistNoteTextMeidplan(rep));
		return note;
	}

	@Override
	public CaseManagementNote getFamilyHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();
		note.setObservationDate(getFamHistDiagnosisDate(rep));
		note.setUpdateDate(getFamHistDiagnosisDate(rep));
		note.setNote(getFamHistNoteTextMediplan(rep));
		return note;
	}


	/**
	 * Gets medical history information from any available ZPR segments.
	 * Information in this section pertains to medical surgeries.
	 * @param rep rep to get ZPR information from
	 * @return base note object for medical history
	 * @throws HL7Exception
	 */
	@Override
	public CaseManagementNote getMedicalHistoryNote(int rep) throws HL7Exception
	{
		// get medical history note out of ZAL segment
		CaseManagementNote note = new CaseManagementNote();

		note.setObservationDate(getMedHistProcedureDate(rep));
		note.setUpdateDate(getMedHistProcedureDate(rep));

		// store date in ext
		CaseManagementNoteExt ext = new CaseManagementNoteExt();
		ext.setNote(note);
		ext.setKey(CaseManagementNoteExt.PROCEDUREDATE);
		ext.setDateValue(getMedHistProcedureDate(rep));

		note.addExtension(ext);
		note.setNote(getMedHistProcedureName(rep));

		return note;
	}

	@Override
	public Date getMedHistProcedureDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZAL(rep)
				.getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValue());
	}

	@Override
	public String getMedHistProcedureName(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
	}

	@Override
	public Date getFamHistDiagnosisDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZAL(rep)
				.getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValue());
	}

	public String getFamHistNoteTextMediplan(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
	}

	public String getSocialHistNoteTextMeidplan(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
	}
}
