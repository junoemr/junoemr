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

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.medication.FrequencyCode;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.dataMigration.model.medication.StandardMedication;
import org.oscarehr.rx.model.Drug;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DrugModelToDbConverter extends BaseModelToDbConverter<Medication, Drug>
{
	@Override
	public Drug convert(Medication input)
	{
		Drug drug = new Drug();

		BeanUtils.copyProperties(input, drug,
				"rxStartDate", "rxEndDate", "writtenDate", "createdDateTime",
				"lastRefillDate", "archivedDateTime", "pickupDateTime", "lastUpdateDateTime",
				"gcnSeqNo", "noSubs", "prn", "archived", "startDateUnknown", "repeat", "pastMed",
				"eTreatmentType", "rxStatus");

		drug.setEndDate(ConversionUtils.toNullableLegacyDate(input.getRxEndDate()));
		drug.setRxDate(ConversionUtils.toNullableLegacyDate(input.getRxStartDate()));
		drug.setWrittenDate(ConversionUtils.toNullableLegacyDate(input.getWrittenDate()));
		drug.setCreateDate(ConversionUtils.toNullableLegacyDateTime(input.getCreatedDateTime()));
		drug.setLastRefillDate(ConversionUtils.toNullableLegacyDate(input.getLastRefillDate()));
		drug.setArchivedDate(ConversionUtils.toNullableLegacyDateTime(input.getArchivedDateTime()));
		drug.setPickUpDateTime(ConversionUtils.toNullableLegacyDateTime(input.getPickupDateTime()));
		drug.setLastUpdateDate(ConversionUtils.toNullableLegacyDateTime(input.getLastUpdateDateTime()));

		drug.setProviderNo(findOrCreateProviderRecord(input.getPrescribingProvider(), false).getId());

		FrequencyCode frequencyCode = input.getFrequencyCode();
		drug.setFreqCode((frequencyCode != null) ? frequencyCode.getCode() : null);
		drug.setDurUnit(input.getDurationUnit());
		drug.setSpecial(generateSpecial(input));

		// these can't be null, do these manually
		drug.setArchived(BooleanUtils.toBooleanDefaultIfNull(input.getArchived(), false));
		drug.setDispenseInternal(BooleanUtils.toBooleanDefaultIfNull(input.getDispenseInternal(), false));
		drug.setRepeat(toIntDefaultIfNull(input.getRepeat(), 0));
		drug.setPosition(0);
		drug.setPastMed(BooleanUtils.toBooleanDefaultIfNull(input.getPastMed(), false));
		drug.setETreatmentType((input.getETreatmentType() != null) ? input.getETreatmentType().getValue() : null);
		drug.setRxStatus((input.getRxStatus() != null) ? input.getRxStatus().getValue() : null);

		// takeMin and takeMax == dosage in UI
		drug.setTakeMax(input.getTakeMax());
		drug.setTakeMin(input.getTakeMin());

		drug.setNoSubs(BooleanUtils.toBooleanDefaultIfNull(input.getNoSubs(), false));
		// dosage column in db == Strength in UI
		if (input.getStrengthAmount() != null && input.getStrengthUnit() != null)
		{
			drug.setDosage(input.getStrengthAmount() + " " + input.getStrengthUnit());
		}

		if(input instanceof StandardMedication)
		{
			StandardMedication standardMedication = (StandardMedication) input;
			drug.setGcnSeqNo(toIntDefaultIfNull(standardMedication.getGcnSeqNo(), 0));
			drug.setPrn(BooleanUtils.toBooleanDefaultIfNull(standardMedication.getPrn(), false));
		}

		// unknown start date if null, but start date must be set to something in database
		if(drug.getRxDate() == null)
		{
			drug.setStartDateUnknown(true);
			drug.setRxDate(new Date());
		}

		drug.setNonAuthoritative(input.getNonAuthoritative());
		drug.setDispenseInterval(input.getDispenseInterval());

		return drug;
	}

	public String generateSpecial(Medication medication)
	{
		List<String> valueList = new ArrayList<>();
		valueList.add(StringUtils.trimToEmpty(medication.getDrugName()));

		if( medication instanceof StandardMedication)
		{
			StandardMedication standardMedication = (StandardMedication) medication;
			if (standardMedication.getDosage() != null)
			{
				valueList.add(standardMedication.getDosage());
			}
		}

		if (medication.getRoute() != null)
		{
			valueList.add(medication.getRoute());
		}
		if (medication.getFrequencyCode() != null)
		{
			valueList.add(medication.getFrequencyCode().getCode());
		}
		if (medication.getInstructions() != null)
		{
			valueList.add(medication.getInstructions());
		}

		String quantity = "Qty:" + (medication.getQuantity() == null ? "0" : medication.getQuantity());
		String quantityUnits = " " + (medication.getDurationUnit() == null ? "" : medication.getDurationUnit());
		String repeat = "Repeats:" + (medication.getRepeat() == null ? 0 : medication.getRepeat());
		valueList.add(quantity + quantityUnits + " " + repeat);

		return String.join("\n", valueList);
	}

	private int toIntDefaultIfNull(Integer integer, int defaultVal)
	{
		return (integer != null) ? integer : defaultVal;
	}
}
