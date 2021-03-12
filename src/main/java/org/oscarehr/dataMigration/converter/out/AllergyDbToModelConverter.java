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
package org.oscarehr.dataMigration.converter.out;

import org.oscarehr.allergy.model.Allergy;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AllergyDbToModelConverter extends
		BaseDbToModelConverter<Allergy, org.oscarehr.dataMigration.model.allergy.Allergy>
{

	@Autowired
	private PartialDateDao partialDateDao;

	@Autowired
	private CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Override
	public org.oscarehr.dataMigration.model.allergy.Allergy convert(Allergy input)
	{
		if(input == null)
		{
			return null;
		}
		org.oscarehr.dataMigration.model.allergy.Allergy allergy = new org.oscarehr.dataMigration.model.allergy.Allergy();
		BeanUtils.copyProperties(input, allergy, "entryDate", "startDate", "providerNo",
				"ageOfOnset", "regionalIdentifier", "onsetOfReaction");

		org.oscarehr.common.model.PartialDate dbPartialDate = partialDateDao.getPartialDate(
				org.oscarehr.common.model.PartialDate.TABLE_ALLERGIES,
				input.getId(),
				org.oscarehr.common.model.PartialDate.ALLERGIES_STARTDATE);

		PartialDate startDatePartial = PartialDate.from(ConversionUtils.toNullableLocalDate(input.getStartDate()), dbPartialDate);
		allergy.setStartDate(startDatePartial);
		allergy.setEntryDateTime(ConversionUtils.toNullableLocalDateTime(input.getEntryDate()));
		allergy.setDrugIdentificationNumber(input.getRegionalIdentifier());
		allergy.setProvider(findProvider(input.getProviderNo()));
		allergy.setAgeOfOnset(input.getAgeOfOnset() != null ? Long.parseLong(input.getAgeOfOnset()) : null);
		allergy.setAnnotation(getNote(input));
		allergy.setOnsetOfReaction(org.oscarehr.dataMigration.model.allergy.Allergy.REACTION_ONSET.fromCodeString(
				(input.getOnsetOfReaction() != null) ? Integer.parseInt(input.getOnsetOfReaction()) : null));

		return allergy;
	}

	private String getNote(Allergy input)
	{
		String noteString = null;
		CaseManagementNoteLink link = caseManagementNoteLinkDao.findLatestAllergyNoteLinkById(input.getId());
		if(link != null)
		{
			CaseManagementNote note = link.getNote();
			noteString = note.getNote();
		}
		return noteString;
	}
}
