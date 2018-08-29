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
import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AllergyMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public AllergyMapper()
	{
		message = null;
		provider = null;
	}
	public AllergyMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
	}

	public int getNumAllergies()
	{
		return provider.getALLERGYReps();
	}

	public List<Allergy> getAllergyList() throws HL7Exception
	{
		int numAllergies = getNumAllergies();
		List<Allergy> allergyList = new ArrayList<>(numAllergies);
		for(int i=0; i< numAllergies; i++)
		{
			allergyList.add(getAllergy(i));
		}
		return allergyList;
	}

	public Allergy getAllergy(int rep) throws HL7Exception
	{
		Allergy allergy = new Allergy();

		String description = getDescription(rep);
		if(description == null)
		{
			description = "INVALID/MISSING DESCRIPTION";
			logger.warn("Missing allergy description. values set to:" + description);
		}
		allergy.setStartDate(getStartDate(rep));
		allergy.setEntryDate(getStartDate(rep));
		allergy.setDescription(description);
		allergy.setArchived(false);
		allergy.setTypeCode(0);// TODO can numeric code be mapped from string in IAM.2.1?
		allergy.setDrugrefId("0");
		allergy.setSeverityOfReaction(getSeverity(rep));
		allergy.setOnsetOfReaction(Allergy.ONSET_CODE_UNKNOWN);
		allergy.setReaction(getReaction(rep));
		allergy.setPosition(0);

		allergy.setAgeOfOnset(getAgeAtOnset(rep));

		return allergy;
	}

	public List<CaseManagementNote> getAllergyNoteList() throws HL7Exception
	{
		int numAllergies = getNumAllergies();
		List<CaseManagementNote> allergyNoteList = new ArrayList<>(numAllergies);
		for(int i=0; i< numAllergies; i++)
		{
			allergyNoteList.add(getAllergyNote(i));
		}
		return allergyNoteList;
	}

	public CaseManagementNote getAllergyNote(int rep) throws HL7Exception
	{
		String nteNote = StringUtils.trimToNull(getComment(rep));
		CaseManagementNote note = null;
		if(nteNote != null)
		{
			note = new CaseManagementNote();
			note.setNote(nteNote.replaceAll("~crlf~", "\n"));
			note.setObservationDate(getStartDate(rep));
			note.setUpdateDate(getStartDate(rep));
		}
		return note;
	}

	public Date getStartDate(int rep)
	{
		return ConversionUtils.fromDateString(provider.getALLERGY(rep).getIAM().getIam13_ReportedDateTime().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	public String getDescription(int rep)
	{
		return StringUtils.trimToNull(provider.getALLERGY(rep).getIAM().getIam3_AllergenCodeMnemonicDescription().getCwe9_OriginalText().getValue());
	}

	public String getReaction(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getALLERGY(rep).getIAM().getIam5_AllergyReactionCode(0).getCwe2_Text().getValue());
	}

	public String getSeverity(int rep)
	{
		String severityCode = StringUtils.trimToEmpty(provider.getALLERGY(rep).getIAM().getIam4_AllergySeverityCode().getCe1_Identifier().getValue());
		switch(severityCode)
		{
			case "MI": return Allergy.SEVERITY_CODE_MILD;
			case "MO": return Allergy.SEVERITY_CODE_MODERATE;
			case "SV": return Allergy.SEVERITY_CODE_SEVERE;
			default: return Allergy.SEVERITY_CODE_UNKNOWN;
		}
	}

	public String getAgeAtOnset(int rep)
	{
		String dobDateStr = message.getPATIENT().getPID().getDateTimeOfBirth().getTimeOfAnEvent().getValue();
		LocalDate dob = ConversionUtils.toLocalDate(dobDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

		String startDateStr = provider.getALLERGY(rep).getIAM().getIam13_ReportedDateTime().getTs1_TimeOfAnEvent().getValue();
		LocalDate startDate = ConversionUtils.toLocalDate(startDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

		if(dob != null && startDate != null)
		{
			return String.valueOf(Period.between(dob, startDate).getYears());
		}
		return "0";
	}

	public String getComment(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getALLERGY(rep).getNTE().getComment(0).getValue());
	}
}
