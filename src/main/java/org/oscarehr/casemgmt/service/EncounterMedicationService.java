/*
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
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarRx.data.RxPrescriptionData.Prescription;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EncounterMedicationService extends EncounterSectionService
{
	public EncounterNotes getNotes(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			Integer limit,
			Integer offset
	)
	{
		List<EncounterSectionNote> out = new ArrayList<>();

		//grab all of the diseases associated with patient and add a list item for each
		oscar.oscarRx.data.RxPrescriptionData prescriptData = new oscar.oscarRx.data.RxPrescriptionData();
		Prescription[] prescriptions = prescriptData.getUniquePrescriptionsByPatient(Integer.parseInt(demographicNo));

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

			//Date date = drug.getRxDate();
			//String serviceDateStr = DateUtils.formatDate(date, request.getLocale());

			String fullTitle = "";
			if (drug.getFullOutLine() != null)
			{
				fullTitle = drug.getFullOutLine().replaceAll(";", " ");
			}
			String title = StringUtils.maxLenString(fullTitle, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES);
			//String formattedTitle = "<span " + getClassColour(drug, now, month) + ">" + title + "</span>";

			//item.setTitle(strTitle);
			//item.setLinkTitle(tmp + " " + serviceDateStr + " - " + drug.getEndDate());
			//item.setURL("return false;");

			sectionNote.setText(title);
			sectionNote.setTitleClasses(getTitleClasses(drug, now, month).toArray(new String[0]));

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

		/*
		StringBuilder sb = new StringBuilder("class=\"");

		if (drug.isCurrent() && drug.getEndDate() != null && (drug.getEndDate().getTime() - referenceTime <= durationToSoon))
		{
			sb.append("expireInReference ");
		}

		if ((drug.isCurrent() && !drug.isArchived()) || drug.isLongTerm())
		{
			sb.append("currentDrug ");
		}

		if (drug.isArchived())
		{
			sb.append("archivedDrug ");
		}

		if(drug.isExpired())
		{
			sb.append("expiredDrug ");
		}

		if(drug.isLongTerm())
		{
			sb.append("longTermMed ");
		}

		if(drug.isDiscontinued())
		{
			sb.append("discontinued ");
		}

		if(drug.getOutsideProviderName() !=null && !drug.getOutsideProviderName().equals("")  )
		{
			sb = new StringBuilder("class=\"");
			sb.append("external ");
		}

		String retval = sb.toString();

		if(retval.equals("class=\""))
		{
			return "";
		}

		return retval.substring(0,retval.length()) + "\"";
		 */
	}
}
