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
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
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

	public int getNumFamilyHistoryNotes()
	{
		return provider.getZHFReps();
	}

	public List<CaseManagementNote> getSocialHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumSocialHistoryNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			CaseManagementNote note = getSocialHistoryNote(i);
			if(note != null)
			{
				noteList.add(note);
			}
		}
		return noteList;
	}

	public List<CaseManagementNote> getFamilyHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumFamilyHistoryNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			CaseManagementNote note = getFamilyHistoryNote(i);
			if(note != null)
			{
				noteList.add(note);
			}
		}
		return noteList;
	}

	public List<CaseManagementNote> getMedicalHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumMedicalHistoryNotes();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			CaseManagementNote note = getMedicalHistoryNote(i);
			if(note != null)
			{
				noteList.add(note);
			}
		}
		return noteList;
	}

	public CaseManagementNote getSocialHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = null;

		String socialAlert = getSocHistSocialAlert(rep);
		String journalNotes = getSocHistJournalNotes(rep);
		String occupation = getSocHistOccupation(rep);
		String employer = getSocHistEmployer(rep);
		String education = getSocHistEducation(rep);
		String leisureActivities = getSocHistLeisureActivities(rep);

		if(socialAlert != null || journalNotes != null || occupation != null || employer != null || education != null || leisureActivities != null)
		{
			note = new CaseManagementNote();

			Date date = new Date();
			note.setObservationDate(date);
			note.setUpdateDate(date);

			// join values but ignore null
			String noteText = "";
			if(socialAlert != null)
			{
				noteText += socialAlert + "\n";
			}
			if(journalNotes != null)
			{
				noteText += journalNotes + "\n";
			}
			if(occupation != null)
			{
				noteText += occupation + "\n";
			}
			if(employer != null)
			{
				noteText += employer + "\n";
			}
			if(education != null)
			{
				noteText += education + "\n";
			}
			if(leisureActivities != null)
			{
				noteText += leisureActivities + "\n";
			}

			note.setNote(StringUtils.trim(noteText));
		}
		return note;
	}

	public CaseManagementNote getFamilyHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		String relation = getFamHistRelationshipToPatient(rep);
		String diagnosisDescription  = getFamHistDiagnosisDescrption(rep);
		String causeOfDeath = getFamHistCauseOfDeath(rep);
		String comments = getFamHistComments(rep);

		String noteText = relation + ": ";
		if(diagnosisDescription != null)
		{
			noteText += diagnosisDescription + "\n";
		}
		if(causeOfDeath != null)
		{
			noteText += "Cause of death: " + causeOfDeath + "\n";
		}
		if(comments != null)
		{
			noteText += comments + "\n";
		}
		note.setNote(StringUtils.trim(noteText));

		Date diagnosisDate = getFamHistDiagnosisDate(rep);
		note.setObservationDate(diagnosisDate);
		note.setUpdateDate(diagnosisDate);

		return note;
	}

	public CaseManagementNote getMedicalHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		// the only date included in the import is the procedure date, use that for everything
		Date procedureDate = getMedHistProcedureDate(rep);
		if(procedureDate == null)
		{
			procedureDate = new Date(); //TODO pick a good default
		}
		else
		{
			// if the procedure date was actually valid, set the extended property
			CaseManagementNoteExt ext = new CaseManagementNoteExt();
			ext.setNote(note);
			ext.setKey(CaseManagementNoteExt.PROCEDUREDATE);
			ext.setDateValue(procedureDate);

			note.addExtension(ext);
		}

		note.setObservationDate(procedureDate);
		note.setUpdateDate(procedureDate);

		String resultText = getMedHistResults(rep);
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
		String dateStr = provider.getZPR(rep).getZpr3_procedureDateTime().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public String getMedHistProcedureName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPR(rep).getZpr2_procedureName().getValue());
	}

	public String getMedHistResults(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPR(rep).getZpr6_results().getValue());
	}

	public String getSocHistSocialAlert(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh2_socialAlert().getValue());
	}

	public String getSocHistJournalNotes(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh3_journalNotes().getValue());
	}

	public String getSocHistOccupation(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh5_occupation().getValue());
	}

	public String getSocHistEmployer(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh6_employer().getValue());
	}

	public String getSocHistEducation(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh7_education().getValue());
	}

	public String getSocHistLeisureActivities(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZSH(rep).getZsh8_leisureActivities().getValue());
	}

	public Date getFamHistDiagnosisDate(int rep) throws HL7Exception
	{
		String dateStr = provider.getZHF(rep).getZhf2_diagnosisDate().getTs1_TimeOfAnEvent().getValue();
		if(dateStr==null || dateStr.trim().isEmpty() || dateStr.equals("00000000"))
		{
			return null;
		}
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public String getFamHistDiagnosisDescrption(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf3_diagnosisDescription().getValue());
	}

	public String getFamHistRelationshipToPatient(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf4_relationshipToPatient().getValue());
	}

	public String getFamHistCauseOfDeath(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf7_causeOfDeath().getValue());
	}

	public String getFamHistComments(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf8_comments().getValue());
	}
}
