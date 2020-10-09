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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.Episode;
import org.oscarehr.common.model.SnomedCore;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PregnancyMapper extends AbstractMapper
{
	private static final Logger logger = MiscUtils.getLogger();

	public PregnancyMapper(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep);
	}

	public int getNumPregnancies()
	{
		return provider.getPREGNANCYReps();
	}

	public List<Episode> getPregnancyEpisodes()
	{
		int numPregnancies = getNumPregnancies();
		List<Episode> episodeList = new ArrayList<>(numPregnancies);
		for(int i = 0; i < numPregnancies; i++)
		{
			Episode pregnancyEpisode = getPregnancyEpisode(i);
			if(pregnancyEpisode != null)
			{
				episodeList.add(pregnancyEpisode);
			}
		}
		return episodeList;
	}

	public Episode getPregnancyEpisode(int rep)
	{
		Episode pregnancy = new Episode();
		pregnancy.setCode(SnomedCore.CODE_NORMAL_PREGNANCY);
		pregnancy.setCodingSystem(Episode.CODE_SYSTEM_NAME_SNOMED_CORE);
		pregnancy.setDescription("Normal pregnancy");
		pregnancy.setNotes(getIndividualPregnancyNoteText(rep));
		pregnancy.setStatus(getStatus(rep));
		// spec only allows for single due date, so use it for both start and end date
		pregnancy.setStartDate(getDueDate(rep));
		if(isComplete(rep))
		{
			pregnancy.setEndDate(getDueDate(rep));
		}

		return pregnancy;
	}

	public CaseManagementNote getMedHistoryMetadataNote()
	{
		CaseManagementNote note = null;
		String noteText = getPregnancyMetaNoteText();

		if(noteText != null && !noteText.isEmpty())
		{
			note = new CaseManagementNote();
			Date noteDate = new Date();
			note.setObservationDate(noteDate);
			note.setUpdateDate(noteDate);
			note.setNote(noteText);
		}
		return note;
	}

	public String getPregnancyMetaNoteText()
	{
		String termBirthNo = StringUtils.trimToEmpty(provider.getZHR().getZhr2_TermBirthNo().getValue());
		String preTermBirthNo = StringUtils.trimToEmpty(provider.getZHR().getZhr3_PerTermBirthNo().getValue());
		String gravida = StringUtils.trimToEmpty(provider.getZHR().getZhr4_Gravida().getValue());
		String abortionsNo = StringUtils.trimToEmpty(provider.getZHR().getZhr5_SpontaneousAbortionsNo().getValue());
		String inducedTerminalsNo = StringUtils.trimToEmpty(provider.getZHR().getZhr6_InducedTerminationNo().getValue());
		String perinatalDeathNo = StringUtils.trimToEmpty(provider.getZHR().getZhr7_PerinatalDeathsNo().getValue());
		String liveChildrenNo = StringUtils.trimToEmpty(provider.getZHR().getZhr8_NumberOfChildrenLiving().getValue());

		StringBuilder noteTextBuilder = new StringBuilder();
		if(!termBirthNo.isEmpty())
		{
			noteTextBuilder.append("Total term births: " + termBirthNo + "\n");
		}
		if(!preTermBirthNo.isEmpty())
		{
			noteTextBuilder.append("Total pre-term births: " + preTermBirthNo + "\n");
		}
		if(!gravida.isEmpty())
		{
			noteTextBuilder.append("Gravida: " + gravida + "\n");
		}
		if(!abortionsNo.isEmpty())
		{
			noteTextBuilder.append("Abortions: " + abortionsNo + "\n");
		}
		if(!inducedTerminalsNo.isEmpty())
		{
			noteTextBuilder.append("Induced Terminations: " + inducedTerminalsNo + "\n");
		}
		if(!perinatalDeathNo.isEmpty())
		{
			noteTextBuilder.append("Perinatal Deaths: " + perinatalDeathNo + "\n");
		}
		if(!liveChildrenNo.isEmpty())
		{
			noteTextBuilder.append("Living Children: " + liveChildrenNo + "\n");
		}

		String noteBody = noteTextBuilder.toString().trim();
		if (!noteBody.isEmpty())
		{
			return "Pregnancy data:\n" + noteBody;
		}
		return null;
	}

	public String getIndividualPregnancyNoteText(int rep)
	{
		String baseText = StringUtils.trimToEmpty(getDueDateConfirmedText(rep));
		String fathersName = StringUtils.trimToEmpty(getFathersName(rep));
		String fathersOccupation = StringUtils.trimToEmpty(getFathersOccupation(rep));
		String fathersDOBString = ConversionUtils.toDateString(getFathersDOB(rep));

		StringBuilder noteTextBuilder = new StringBuilder();
		noteTextBuilder.append(baseText);
		if(!fathersName.isEmpty() || !fathersDOBString.isEmpty() || !fathersOccupation.isEmpty())
		{
			noteTextBuilder.append("\nFathers Info:\n");
			if(!fathersName.isEmpty())
			{
				noteTextBuilder.append(" Name: " + fathersName + "\n");
			}
			if(!fathersDOBString.isEmpty())
			{
				noteTextBuilder.append(" DOB: " + fathersDOBString + "\n");
			}
			if(!fathersOccupation.isEmpty())
			{
				noteTextBuilder.append(" Occupation: " + fathersOccupation + "\n");
			}
		}

		return noteTextBuilder.toString().trim();
	}

	public String getFathersName(int rep)
	{
		return StringUtils.trimToNull(provider.getPREGNANCY(rep).getZPG().getZpg1_FathersName().getValue());
	}

	public Date getFathersDOB(int rep)
	{
		return getNullableDate(provider.getPREGNANCY(rep).getZPG().getZpg2_FathersDOB().getTs1_TimeOfAnEvent().getValue());
	}

	public String getFathersOccupation(int rep)
	{
		return StringUtils.trimToNull(provider.getPREGNANCY(rep).getZPG().getZpg3_FathersOccupation().getValue());
	}

	public String getDueDateConfirmedText(int rep)
	{
		return StringUtils.trimToNull(provider.getPREGNANCY(rep).getZPG().getZpg5_DueDateConfirmed().getValue());
	}

	public Date getDueDate(int rep)
	{
		Date dueDate = getNullableDate(provider.getPREGNANCY(rep).getZPG().getZpg4_DueDate().getTs1_TimeOfAnEvent().getValue());
		if(dueDate == null)
		{
			// if there is no date provided, default to obviously wrong (but valid) date
			dueDate = ConversionUtils.toLegacyDate(LocalDate.of(1900, 1, 1));
		}
		return dueDate;
	}

	public String getStatus(int rep)
	{
		String value = StringUtils.trimToEmpty(provider.getPREGNANCY(rep).getZPG().getZpg5_DueDateConfirmed().getValue());
		switch(value)
		{
			case "No Date": return Episode.STATUS_CURRENT;
			case "Delivery":
			default: return Episode.STATUS_COMPLETE;
		}
	}

	public boolean isComplete(int rep)
	{
		return Episode.STATUS_COMPLETE.equals(getStatus(rep));
	}
}
