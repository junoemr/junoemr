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

import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.medication.CustomMedication;
import org.oscarehr.dataMigration.model.medication.FrequencyCode;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.dataMigration.model.medication.StandardMedication;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.oscarDemographic.pageUtil.Util;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.util.Map;

@Component
public class MedicationDbToModelConverter extends BaseDbToModelConverter<Drug, Medication>
{
	@Autowired
	private PartialDateDao partialDateDao;

	@Override
	public Medication convert(Drug input)
	{
		if(input == null)
		{
			return null;
		}
		Medication medication;
		if(input.isCustom())
		{
			medication = new CustomMedication();
		}
		else
		{
			medication = new StandardMedication();
			parseDosageValue(input, (StandardMedication) medication);
		}

		BeanUtils.copyProperties(input, medication,
				"rxDate", "endDate", "writtenDate", "createDate",
				"lastRefillDate", "archivedDate", "pickupDateTime", "lastUpdateDate", "freqCode");

		Map<Integer, org.oscarehr.common.model.PartialDate> partialDateMap = partialDateDao.getAllForTableEntry(
				org.oscarehr.common.model.PartialDate.TABLE_DRUGS, input.getId());


		org.oscarehr.common.model.PartialDate dbPartialDate = partialDateMap.get(org.oscarehr.common.model.PartialDate.DRUGS_WRITTENDATE);
		PartialDateTime writtenDate = PartialDateTime.from(ConversionUtils.toNullableLocalDateTime(input.getWrittenDate()), dbPartialDate);
		medication.setWrittenDate(writtenDate);

		if(!input.getStartDateUnknown())
		{
			org.oscarehr.common.model.PartialDate dbPartialStartDate = partialDateMap.get(org.oscarehr.common.model.PartialDate.DRUGS_STARTDATE);
			PartialDate startDate = PartialDate.from(ConversionUtils.toNullableLocalDate(input.getRxDate()), dbPartialStartDate);
			medication.setRxStartDate(startDate);
		}

		org.oscarehr.common.model.PartialDate dbPartialEndDate = partialDateMap.get(org.oscarehr.common.model.PartialDate.DRUGS_ENDDATE);
		medication.setRxEndDate(PartialDate.from(ConversionUtils.toNullableLocalDate(input.getEndDate()), dbPartialEndDate));

		medication.setPickupDateTime(ConversionUtils.toNullableLocalDateTime(input.getPickUpDateTime()));
		medication.setLastRefillDate(ConversionUtils.toNullableLocalDate(input.getLastRefillDate()));
		medication.setCreatedDateTime(ConversionUtils.toNullableLocalDateTime(input.getCreateDate()));
		medication.setLastUpdateDateTime(ConversionUtils.toNullableLocalDateTime(input.getLastUpdateDate()));
		medication.setArchivedDateTime(ConversionUtils.toNullableLocalDateTime(input.getArchivedDate()));
		medication.setFrequencyCode(FrequencyCode.from(input.getFreqCode()));

		medication.setDurationUnit(input.getDurUnit());

		return medication;
	}

	// this was basically taken from cds 4 export
	private void parseDosageValue(Drug input, StandardMedication medication)
	{
		String rawDosage = input.getDosage();
		if(StringUtils.filled(rawDosage))
		{
			String[] strength = rawDosage.split(" ");

			// amount & unit separated by space
			if(Util.leadingNum(strength[0]).equals(strength[0]))
			{
				medication.setStrengthAmount(strength[0]);
				medication.setStrengthUnit((strength.length > 1) ? strength[1] : "unit");
			}
			else // amount & unit not separated, probably e.g. 50mg / 2tablet
			{
				if(strength.length > 1 && strength[1].equals("/"))
				{
					if(strength.length > 2)
					{
						String unit1 = Util.leadingNum(strength[2]).equals("") ? "1" : Util.leadingNum(strength[2]);
						String unit2 = Util.trailingTxt(strength[2]).equals("") ? "unit" : Util.trailingTxt(strength[2]);

						medication.setStrengthAmount(Util.leadingNum(strength[0]) + "/" + unit1);
						medication.setStrengthUnit(Util.trailingTxt(strength[0]) + "/" + unit2);
					}
				}
				else
				{
					medication.setStrengthAmount(Util.leadingNum(strength[0]));
					medication.setStrengthUnit(Util.trailingTxt(strength[0]));
				}
			}
		}
	}
}
