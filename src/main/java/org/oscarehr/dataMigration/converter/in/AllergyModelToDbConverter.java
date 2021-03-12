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
package org.oscarehr.dataMigration.converter.in;

import org.oscarehr.allergy.model.Allergy;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.dataMigration.model.allergy.Allergy.REACTION_ONSET;
import static org.oscarehr.dataMigration.model.allergy.Allergy.REACTION_SEVERITY;

@Component
public class AllergyModelToDbConverter extends BaseModelToDbConverter<org.oscarehr.dataMigration.model.allergy.Allergy, Allergy>
{

	@Override
	public Allergy convert(org.oscarehr.dataMigration.model.allergy.Allergy input)
	{
		Allergy allergy = new Allergy();
		BeanUtils.copyProperties(input, allergy, "entryDateTime", "startDate", "provider",
				"ageOfOnset", "drugIdentificationNumber", "onsetOfReaction");

		allergy.setEntryDate(ConversionUtils.toNullableLegacyDateTime(input.getEntryDateTime()));
		allergy.setStartDate(ConversionUtils.toNullableLegacyDate(input.getStartDate()));
		allergy.setProviderNo(findOrCreateProviderRecord(input.getProvider(), false).getId());
		allergy.setAgeOfOnset((input.getAgeOfOnset()) != null ? String.valueOf(input.getAgeOfOnset()) : null);
		allergy.setRegionalIdentifier(input.getDrugIdentificationNumber());

		REACTION_ONSET reactionOnset = (input.getOnsetOfReaction() != null) ? input.getOnsetOfReaction() : REACTION_ONSET.UNKNOWN;
		allergy.setOnsetOfReaction(String.valueOf(reactionOnset.getOnsetCode()));

		REACTION_SEVERITY severity = (input.getSeverityOfReaction() != null) ? input.getSeverityOfReaction() : REACTION_SEVERITY.UNKNOWN;
		allergy.setSeverityOfReaction(String.valueOf(severity.getSeverityCode()));

		if(allergy.getTypeCode() == null)
		{
			allergy.setTypeCode(0); // can't bew null in db
		}

		return allergy;
	}
}
