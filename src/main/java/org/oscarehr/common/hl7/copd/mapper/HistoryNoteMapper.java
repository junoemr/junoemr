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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryNoteMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public HistoryNoteMapper()
	{
		message = null;
		provider = null;
	}
	public HistoryNoteMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
	}

	// ---------------------------------------------------------------------------------------

	public int getNumSocialHistoryNotes()
	{
		return provider.getZSHReps();
	}

	public int getNumMedicalHistoryNotes()
	{
		return provider.getZPRReps();
	}

	public List<CaseManagementNote> getSocialHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumSocialHistoryNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			noteList.add(getSocialHistoryNote(i));
		}
		return noteList;
	}

	public List<CaseManagementNote> getMedicalHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumMedicalHistoryNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			noteList.add(getMedicalHistoryNote(i));
		}
		return noteList;
	}

	public CaseManagementNote getSocialHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = null;


		note = new CaseManagementNote();



		return note;
	}

	public CaseManagementNote getMedicalHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();
		note.setObservationDate(getMedHistProcedureDate(rep));
		note.setUpdateDate(getMedHistProcedureDate(rep));

		String resultText = StringUtils.trimToNull(getMedHistResults(rep));
		String noteText = StringUtils.trimToEmpty(getMedHistProcedureName(rep));
		if(resultText != null)
		{
			noteText += " - " + resultText;
		}
		note.setNote(noteText);

		return note;
	}

	public Date getMedHistProcedureDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPR(rep).getZpr3_procedureDateTime().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	public String getMedHistProcedureName(int rep) throws HL7Exception
	{
		return provider.getZPR(rep).getZpr2_procedureName().getValue();
	}

	public String getMedHistResults(int rep) throws HL7Exception
	{
		return provider.getZPR(rep).getZpr6_results().getValue();
	}
}
