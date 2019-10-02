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

import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.casemgmt.dto.EncounterNotes;
import org.oscarehr.casemgmt.dto.EncounterSectionNote;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarRx.data.RxPatientData;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class EncounterAllergyService extends EncounterSectionService
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

		// grab all of the diseases associated with patient and add a list item for each
		Integer demographicId = Integer.parseInt(demographicNo);

		Allergy[] allergies =
				RxPatientData.getPatient(loggedInInfo, demographicId).getActiveAllergies();

		// --- get local allergies ---
		for (int idx = 0; idx < allergies.length; ++idx)
		{

			Date date = allergies[idx].getEntryDate();

			EncounterSectionNote sectionNote = makeItem(date, allergies[idx].getDescription(),
					allergies[idx].getSeverityOfReaction());

			out.add(sectionNote);
		}

		// --- sort all results ---
		Collections.sort(out, new EncounterSectionNote.SortChronologicDateAsc());

		return EncounterNotes.limitedEncounterNotes(out, offset, limit);
	}

	private static EncounterSectionNote makeItem(Date entryDate, String description,
												 String severity)
	{
		EncounterSectionNote sectionNote = new EncounterSectionNote();

		sectionNote.setUpdateDate(
				ConversionUtils.toNullableLocalDate(entryDate).atStartOfDay());

		if (severity != null && severity.equals(Allergy.SEVERITY_CODE_SEVERE))
		{
			sectionNote.setColour(COLOUR_HIGHLITE);
		}
		else if (severity != null && severity.equals(Allergy.SEVERITY_CODE_MODERATE))
		{
			sectionNote.setColour(COLOUR_WARNING);
		}

		sectionNote.setText(StringUtils.maxLenString(description, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES));

		//item.setLinkTitle(description + " " + DateUtils.formatDate(entryDate, locale));
		//item.setURL("return false;");

		return sectionNote;
	}
}
