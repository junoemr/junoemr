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

package org.oscarehr.casemgmt.service;

import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import oscar.oscarRx.data.RxPrescriptionData.Prescription;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterMedicationService extends EncounterSectionService
{
	public static final String SECTION_ID = "Rx";
	protected static final String SECTION_TITLE_KEY = "oscarEncounter.NavBar.Medications";
	protected static final String SECTION_TITLE_COLOUR = "#7D2252";

	@Override
	public String getSectionId()
	{
		return SECTION_ID;
	}

	@Override
	protected String getSectionTitleKey()
	{
		return SECTION_TITLE_KEY;
	}

	@Override
	protected String getSectionTitleColour()
	{
		return SECTION_TITLE_COLOUR;
	}

	@Override
	protected String getOnClickPlus(SectionParameters sectionParams)
	{
		String url = sectionParams.getContextPath() + "/oscarRx/choosePatient.do" +
				"?providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&ltm=true" +
				"&reRxExpiredLTM=true" +
				"&s=true" +
				"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(580,1027,'" + getWinName(sectionParams) + "', '" + url + "')";
	}

	@Override
	protected String getOnClickTitle(SectionParameters sectionParams)
	{
		String url = sectionParams.getContextPath() + "/oscarRx/choosePatient.do" +
				"?providerNo=" + encodeUrlParam(sectionParams.getProviderNo()) +
				"&demographicNo=" + encodeUrlParam(sectionParams.getDemographicNo());

		return "popupPage(580,1027,'" + getWinName(sectionParams) + "', '" + url + "')";
	}

	private String getWinName(SectionParameters sectionParams)
	{
		return "Rx" + sectionParams.getDemographicNo();
	}

	public EncounterNotes getNotes(
			SectionParameters sectionParams, Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		//grab all of the diseases associated with patient and add a list item for each
		oscar.oscarRx.data.RxPrescriptionData prescriptData = new oscar.oscarRx.data.RxPrescriptionData();
		Prescription[] prescriptions = prescriptData.getUniquePrescriptionsByPatient(Integer.parseInt(sectionParams.getDemographicNo()));

		long now = System.currentTimeMillis();
		long month = 1000L * 60L * 60L * 24L * 30L;
		for (Prescription drug: prescriptions)
		{
			if(
				drug.isArchived() ||
				drug.isHideCpp() || (
					!(drug.isCurrent() && (drug.getEndDate().getTime() - now <= month)) &&
					!drug.isCurrent() &&
					!drug.isLongTerm()
				)
			)
			{
				continue;
			}


			EncounterSectionNote sectionNote = new EncounterSectionNote();

			String fullTitle = "";
			if (drug.getFullOutLine() != null)
			{
				fullTitle = drug.getFullOutLine().replaceAll(";", " ");
			}

			sectionNote.setText(EncounterSectionService.getTrimmedText(fullTitle));
			sectionNote.setTitleClasses(getTitleClasses(drug, now, month).toArray(new String[0]));

			String startDate = ConversionUtils.toDateString(drug.getRxDate());
			String endDate = ConversionUtils.toDateString(drug.getEndDate());
			sectionNote.setTitle(fullTitle + " " + startDate + " - " + endDate);

			out.add(sectionNote);
		}

		return EncounterNotes.limitedEncounterNotes(out, null, null);
	}

	private static List<String> getTitleClasses(Prescription drug, long referenceTime, long durationToSoon)
	{
		List<String> titleClasses = new ArrayList<>();

		if (drug.isCurrent() && drug.getEndDate() != null && (drug.getEndDate().getTime() - referenceTime <= durationToSoon))
		{
			titleClasses.add("expireInReference");
		}

		if ((drug.isCurrent() && !drug.isArchived()) || drug.isLongTerm())
		{
			titleClasses.add("currentDrug");
		}

		if (drug.isArchived())
		{
			titleClasses.add("archivedDrug");
		}

		if(drug.isExpired())
		{
			titleClasses.add("expiredDrug");
		}

		if(drug.isLongTerm())
		{
			titleClasses.add("longTermMed");
		}

		if(drug.isDiscontinued())
		{
			titleClasses.add("discontinued");
		}

		if(drug.getOutsideProviderName() !=null && !drug.getOutsideProviderName().equals("")  )
		{
			titleClasses.clear();
			titleClasses.add("external");
		}

		return titleClasses;
	}

	public static String getClassColour(Prescription drug, long referenceTime, long durationToSoon)
	{
		List<String> titleClassList = getTitleClasses(drug, referenceTime, durationToSoon);
		return "class=\"" + String.join(" ", titleClassList) + "\"";
	}
}
