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
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryNoteMapper extends AbstractMapper
{
	//because mediplan sucks
	public static final String MEDIPLAN_MEDICAL_NOTE_ID_1="MEDIPLAN SURGICAL HISTORY SECTION";
	public static final String MEDIPLAN_MEDICAL_NOTE_ID_2="MEDIPLAN DEVELOPMENTAL HISTORY SECTION";
	public static final String MEDIPLAN_FAMILY_HISTORY_ID="MEDIPLAN FAMILY HISTORY SECTION";
	public static final String MEDIPLAN_SOCIAL_HISTORY_ID="MEDIPLAN CARE PLAN SECTION";

	private static final Logger logger = MiscUtils.getLogger();
	private final Date oldestEncounterNoteDate; // used as a default for notes with no date info

	private static Map<String, String> relationshipTypeMap = new HashMap<>();

	public HistoryNoteMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		super(message, providerRep, importSource);
		this.oldestEncounterNoteDate = getOldestEncounterNoteContactDate();
	}

	private Date getOldestEncounterNoteContactDate() throws HL7Exception
	{
		int reps = provider.getZPVReps();
		List<Date> noteDateList = new ArrayList<>(reps);

		for(int rep = 0; rep < reps; rep++)
		{
			Date noteDate = getNullableDate(provider.getZPV(rep).getZpv2_contactDate().getTs1_TimeOfAnEvent().getValue());
			if(noteDate != null)
			{
				noteDateList.add(noteDate);
			}
		}
		if(noteDateList.isEmpty())
		{
			return new Date();
		}
		return Collections.min(noteDateList);
	}

	// ---------------------------------------------------------------------------------------

	public int getNumSocialHistoryNotes()
	{
		return provider.getZSHReps();
	}

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


	public int getNumMedicalHistoryNotes()
	{
		return getNumMedicalProblemsNotes() + getNumMedicalSurgicalNotes();
	}

	public int getNumMedicalProblemsNotes()
	{
		return provider.getZPBReps();
	}

	public int getNumMedicalSurgicalNotes()
	{
		return provider.getZPRReps();
	}

	public int getNumFamilyHistoryNotes()
	{
		return provider.getZHFReps();
	}

	public List<CaseManagementNote> getSocialHistoryNoteList() throws HL7Exception
	{
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			return getSocialHistoryNoteListMediplan();
		}
		else
		{
			int numNotes = getNumSocialHistoryNotes();
			List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
			for (int i = 0; i < numNotes; i++)
			{
				CaseManagementNote note = getSocialHistoryNote(i);
				if (note != null)
				{
					noteList.add(note);
				}
			}
			return noteList;
		}
	}

	public List<CaseManagementNote> getSocialHistoryNoteListMediplan() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isSocialHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getSocialHistoryNoteMediplan(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	public List<CaseManagementNote> getFamilyHistoryNoteList() throws HL7Exception
	{
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			return getFamilyHistoryNoteListMediplan();
		}
		else
		{
			int numNotes = getNumFamilyHistoryNotes();
			List<CaseManagementNote> noteList = new ArrayList<>(numNotes);
			for (int i = 0; i < numNotes; i++)
			{
				CaseManagementNote note = getFamilyHistoryNote(i);
				if (note != null)
				{
					noteList.add(note);
				}
			}
			return noteList;
		}
	}

	public List<CaseManagementNote> getFamilyHistoryNoteListMediplan() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isFamilyHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getFamilyHistoryNoteMediplan(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	public List<CaseManagementNote> getMedicalHistoryNoteList() throws HL7Exception
	{
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			return getMedicalHistoryNoteListMediplan();
		}
		else
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
	}

	public List<CaseManagementNote> getMedicalHistoryNoteListMediplan() throws HL7Exception
	{
		ArrayList<CaseManagementNote> outList = new ArrayList<>();
		int numZALs = provider.getZALReps();
		for (int i =0; i < numZALs; i ++)
		{
			if (isMedicalHistoryNoteMediplan(i))
			{
				CaseManagementNote newNote = getMedicalHistoryNoteMediplan(i);
				if (newNote != null)
				{
					outList.add(newNote);
				}
			}
		}
		return outList;
	}

	public CaseManagementNote getMedicalHistoryNoteMediplan(int rep) throws HL7Exception
	{
		// get medical history note out of ZAL segment
		CaseManagementNote note = new CaseManagementNote();

		note.setObservationDate(getMedHistProcedureDateMediplan(rep));
		note.setUpdateDate(getMedHistProcedureDateMediplan(rep));

		// store date in ext
		CaseManagementNoteExt ext = new CaseManagementNoteExt();
		ext.setNote(note);
		ext.setKey(CaseManagementNoteExt.PROCEDUREDATE);
		ext.setDateValue(getMedHistProcedureDateMediplan(rep));

		note.addExtension(ext);
		note.setNote(getMedHistProcedureNameMediplan(rep));

		return note;
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

			Date date = oldestEncounterNoteDate;
			note.setObservationDate(date);
			note.setUpdateDate(date);

			// join values but ignore null
			String noteText = "";
			if(socialAlert != null)
			{
				noteText += StringUtils.trimToEmpty(socialAlert + "\n");
			}
			if(journalNotes != null)
			{
				noteText += StringUtils.trimToEmpty(journalNotes + "\n");
			}
			if(occupation != null)
			{
				noteText += StringUtils.trimToEmpty(occupation + "\n");
			}
			if(employer != null)
			{
				noteText += StringUtils.trimToEmpty(employer + "\n");
			}
			if(education != null)
			{
				noteText += StringUtils.trimToEmpty(education + "\n");
			}
			if(leisureActivities != null)
			{
				noteText += StringUtils.trimToEmpty(leisureActivities + "\n");
			}

			note.setNote(StringUtils.trim(noteText.replaceAll("~crlf~", "\n")) + " - " + ConversionUtils.toDateString(date));
		}
		return note;
	}

	public CaseManagementNote getSocialHistoryNoteMediplan(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		Date date = oldestEncounterNoteDate;
		note.setObservationDate(date);
		note.setUpdateDate(date);

		note.setNote(getSocialHistNoteTextMeidplan(rep));
		return note;
	}

	public CaseManagementNote getFamilyHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		String relation = getFamHistRelationshipToPatient(rep);
		String diagnosisDescription  = getFamHistDiagnosisDescription(rep);
		String causeOfDeath = getFamHistCauseOfDeath(rep);
		String comments = getFamHistComments(rep);

		String noteText = relation + ": ";
		if(diagnosisDescription != null)
		{
			noteText += StringUtils.trimToEmpty(diagnosisDescription + "\n");
		}
		if(causeOfDeath != null)
		{
			noteText += "Cause of death: " + StringUtils.trimToEmpty(causeOfDeath) + "\n";
		}
		if(comments != null)
		{
			noteText += StringUtils.trimToEmpty(comments + "\n");
		}

		Date diagnosisDate = getFamHistDiagnosisDate(rep);
		if(diagnosisDate == null)
		{
			/* Wolf has stated that this field gets used for relationships & family related diseases,
			 * and that if the date is missing or the description is 'unknown', the data can be ignored,
			 * since it indicates a relationship that does not have enough info for the transfer */
			if(importSource.equals(CoPDImportService.IMPORT_SOURCE.WOLF))
			{
				return null;
			}
			diagnosisDate = oldestEncounterNoteDate;
		}

		note.setNote(StringUtils.trim(noteText.replaceAll("~crlf~", "\n")) + " - " + ConversionUtils.toDateString(diagnosisDate));
		note.setObservationDate(diagnosisDate);
		note.setUpdateDate(diagnosisDate);

		return note;
	}

	public CaseManagementNote getFamilyHistoryNoteMediplan(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();
		note.setObservationDate(getFamHistDiagnosisisDateMediplan(rep));
		note.setUpdateDate(getFamHistDiagnosisisDateMediplan(rep));
		note.setNote(getFamHistNoteTextMediplan(rep));
		return note;
	}

	/**
	 * Gets medical history information from any available ZPB segments.
	 * Information in this segment pertains to medical diagnosis (both unconfirmed and confirmed).
	 * This is slightly different from our other medical history fetching.
	 * @param rep rep to get ZPB information from
	 * @return base note object for medical history
	 * @throws HL7Exception
	 */
	public CaseManagementNote getMedicalProblemNote(int rep) throws HL7Exception
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
		note.setNote(noteText + " - " + ConversionUtils.toDateString(diagnosisDate));

		return note;
	}

	/**
	 * Gets medical history information from any available ZPR segments.
	 * Information in this section pertains to medical surgeries.
	 * @param rep rep to get ZPR information from
	 * @return base note object for medical history
	 * @throws HL7Exception
	 */
	public CaseManagementNote getMedicalHistoryNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		// the only date included in the import is the procedure date, use that for everything
		Date procedureDate = getMedHistProcedureDate(rep);
		if(procedureDate == null)
		{
			procedureDate = oldestEncounterNoteDate;
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
		String noteText = StringUtils.trimToEmpty(getMedHistProcedureName(rep)).replaceAll("~crlf~", "\n");
		if(resultText != null)
		{
			noteText += " - " + resultText;
		}
		note.setNote(noteText + " - " + ConversionUtils.toDateString(procedureDate));

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

	public Date getMedHistProcedureDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPR(rep)
				.getZpr3_procedureDateTime().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getMedHistProcedureDateMediplan(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZAL(rep)
				.getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValue());
	}

	public String getMedHistProcedureName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPR(rep).getZpr2_procedureName().getValue());
	}

	public String getMedHistProcedureNameMediplan(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
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
		return getNullableDate(provider.getZHF(rep)
				.getZhf2_diagnosisDate().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getFamHistDiagnosisisDateMediplan(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZAL(rep)
				.getZal2_dateOfAlert().getTs1_TimeOfAnEvent().getValue());
	}

	public String getFamHistDiagnosisDescription(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf3_diagnosisDescription().getValue());
	}

	public String getFamHistRelationshipToPatient(int rep) throws HL7Exception
	{
		String relationCode = StringUtils.trimToNull(provider.getZHF(rep).getZhf4_relationshipToPatient().getValue());
		if(relationCode != null && relationshipTypeMap.containsKey(relationCode.toUpperCase()))
		{
			return relationshipTypeMap.get(relationCode.toUpperCase());
		}
		logger.warn("Invalid relationship code: " + relationCode);
		return (relationCode != null)? relationCode : relationshipTypeMap.get("UNK");//unknown
	}

	public String getFamHistCauseOfDeath(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf7_causeOfDeath().getValue());
	}

	public String getFamHistComments(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZHF(rep).getZhf8_comments().getValue());
	}

	public String getFamHistNoteTextMediplan(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
	}

	public String getSocialHistNoteTextMeidplan(int rep) throws HL7Exception
	{
		return StringUtils.trimToEmpty(provider.getZAL(rep).getZal5_alertTextSent().getValue()).replace(" / ", "\n");
	}

	static
	{
		relationshipTypeMap.put("ANT", "Aunt");
		relationshipTypeMap.put("BRO", "Brother");
		relationshipTypeMap.put("COM", "Common-Law");
		relationshipTypeMap.put("CSN", "Cousin");
		relationshipTypeMap.put("DAU", "Daughter");
		relationshipTypeMap.put("EMP", "Employer");
		relationshipTypeMap.put("FAT", "Father");
		relationshipTypeMap.put("FIL", "Father-in-law");
		relationshipTypeMap.put("FRE", "Friend");
		relationshipTypeMap.put("FTC", "Foster Child");
		relationshipTypeMap.put("FTP", "Foster Parent");
		relationshipTypeMap.put("GRC", "Grandchild");
		relationshipTypeMap.put("GRP", "Grandparent");
		relationshipTypeMap.put("GUA", "Guardian");
		relationshipTypeMap.put("HUS", "Husband");
		relationshipTypeMap.put("MIL", "Mother-in-law");
		relationshipTypeMap.put("MOT", "Mother");
		relationshipTypeMap.put("NEI", "Neighbor");
		relationshipTypeMap.put("NEP", "Nephew");
		relationshipTypeMap.put("NIE", "Niece");
		relationshipTypeMap.put("OTH", "Other");
		relationshipTypeMap.put("PAR", "Parent");
		relationshipTypeMap.put("REL", "Relative");
		relationshipTypeMap.put("ROM", "Roommate");
		relationshipTypeMap.put("SEL", "Self");
		relationshipTypeMap.put("SIS", "Sister");
		relationshipTypeMap.put("SON", "Son");
		relationshipTypeMap.put("SOW", "Social Worker");
		relationshipTypeMap.put("SPO", "Spouse");
		relationshipTypeMap.put("STD", "Step Daughter");
		relationshipTypeMap.put("STF", "Step Father");
		relationshipTypeMap.put("STM", "Step Mother");
		relationshipTypeMap.put("STS", "Step Son");
		relationshipTypeMap.put("UNC", "Uncle");
		relationshipTypeMap.put("UNK", "Unknown");
		relationshipTypeMap.put("WIF", "Wife");
	}
}
