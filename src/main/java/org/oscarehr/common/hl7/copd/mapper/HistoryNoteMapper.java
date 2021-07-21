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
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
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
	protected final CoPDRecordData recordData;
	protected static final Logger logger = MiscUtils.getLogger();
	protected final Date oldestEncounterNoteDate; // used as a default for notes with no date info

	protected static Map<String, String> relationshipTypeMap = new HashMap<>();

	public HistoryNoteMapper(ZPD_ZTR message, int providerRep, ImporterExporterFactory.IMPORT_SOURCE importSource, CoPDRecordData recordData) throws HL7Exception
	{
		super(message, providerRep, importSource);
		this.oldestEncounterNoteDate = getOldestEncounterNoteContactDate();
		this.recordData = recordData;
	}

	protected Date getOldestEncounterNoteContactDate() throws HL7Exception
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

	public int getNumMedicalHistoryNotes()
	{
		return provider.getZPRReps();
	}

	public int getNumFamilyHistoryNotes()
	{
		return provider.getZHFReps();
	}

	public int getNumDemographicContacts()
	{
		return provider.getNK1Reps();
	}

	public List<CaseManagementNote> getSocialHistoryNoteList() throws HL7Exception
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

	public List<CaseManagementNote> getFamilyHistoryNoteList() throws HL7Exception
	{
		int numNotes = getNumFamilyHistoryNotes();
		int numContacts = getNumDemographicContacts();
		List<CaseManagementNote> noteList = new ArrayList<>(numNotes + numContacts);
		for (int i = 0; i < numNotes; i++)
		{
			CaseManagementNote note = getFamilyHistoryNote(i);
			if (note != null)
			{
				noteList.add(note);
			}
		}
		for (int i = 0; i < numContacts; i++)
		{
			CaseManagementNote note = getContactAsNote(i);
			if (note != null)
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
		for (int i = 0; i < numNotes; i++)
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
			diagnosisDate = oldestEncounterNoteDate;
		}

		note.setNote(StringUtils.trim(noteText.replaceAll("~crlf~", "\n")) + " - " + ConversionUtils.toDateString(diagnosisDate));
		note.setObservationDate(diagnosisDate);
		note.setUpdateDate(diagnosisDate);

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

	protected CaseManagementNote getContactAsNote(int rep) throws HL7Exception
	{
		String contactNoteText = "";

		String contactFirstName = getContactFirstName(rep);
		String contactLastName = getContactLastName(rep);
		String contactAddress = getContactAddressLine(rep);
		String contactRelationship = getContactRelationship(rep);
		String contactPhone = getContactPhone(rep);
		String contactRole = getContactRole(rep);
		String contactOther = getOtherContactInfo(rep);

		if(contactFirstName != null || contactLastName != null)
		{
			String contactTitle = getContactNamePrefix(rep);
			contactNoteText += StringUtils.trimToEmpty(StringUtils.trimToEmpty(contactTitle) + " " +
							StringUtils.trimToEmpty(StringUtils.trimToEmpty(contactFirstName) + " " + StringUtils.trimToEmpty(contactLastName))) + "\n";
		}
		if(contactRelationship != null)
		{
			contactNoteText += contactRelationship;
		}
		if(contactRole != null)
		{
			contactNoteText += contactRole;
		}
		if(contactOther != null)
		{
			contactNoteText += contactOther;
		}
		if(contactAddress != null)
		{
			contactNoteText += contactAddress;
		}
		if(contactPhone != null)
		{
			contactNoteText += contactPhone;
		}

		// if there is text, make the note
		CaseManagementNote note = null;
		if(StringUtils.trimToNull(contactNoteText) != null)
		{
			note = new CaseManagementNote();
			note.setNote(contactNoteText);

			// no date field in NK1 segment, so we have to pick something
			note.setObservationDate(oldestEncounterNoteDate);
			note.setUpdateDate(oldestEncounterNoteDate);
		}
		return note;
	}

	public Date getMedHistProcedureDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getZPR(rep)
				.getZpr3_procedureDateTime().getTs1_TimeOfAnEvent().getValue());
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
		return getNullableDate(provider.getZHF(rep)
				.getZhf2_diagnosisDate().getTs1_TimeOfAnEvent().getValue());
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

	protected String getContactFirstName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk12_NKName(0).getXpn2_GivenName().getValue());
	}
	protected String getContactLastName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk12_NKName(0).getXpn1_FamilyName().getFn1_Surname().getValue());
	}
	protected String getContactNamePrefix(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk12_NKName(0).getXpn5_PrefixEgDR().getValue());
	}
	protected String getContactRelationship(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk13_Relationship().getCe1_Identifier().getValue());
	}
	protected String getContactAddressLine(int rep) throws HL7Exception
	{
		String streetAddr = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getStreetAddress().getSad1_StreetOrMailingAddress().getValue());
		String streetNum = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getStreetAddress().getSad2_StreetName().getValue());
		String dwellingNum = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getStreetAddress().getSad3_DwellingNumber().getValue());
		String city = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getXad3_City().getValue());
		String province = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getXad4_StateOrProvince().getValue());
		String country = StringUtils.trimToEmpty(provider.getNK1(rep).getNk14_Address(0).getXad6_Country().getValue());

		return StringUtils.trimToNull(String.join(" ", streetAddr, streetNum, dwellingNum, city, province, country));
	}
	protected String getContactPhone(int rep) throws HL7Exception
	{
		String areaCode = StringUtils.trimToEmpty(provider.getNK1(rep).getNk15_PhoneNumber(0).getXtn6_AreaCityCode().getValue());
		String number = StringUtils.trimToEmpty(provider.getNK1(rep).getNk15_PhoneNumber(0).getXtn7_PhoneNumber().getValue());

		return StringUtils.trimToNull(areaCode + number);
	}
	protected String getContactRole(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getNK1(rep).getNk17_ContactRole().getCe1_Identifier().getValue());
	}
	protected String getOtherContactInfo(int rep) throws HL7Exception
	{
		return null;
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
