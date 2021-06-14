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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.model.medication.CustomMedication;
import org.oscarehr.dataMigration.model.medication.FrequencyCode;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.dataMigration.model.medication.StandardMedication;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.DrugMeasure;
import xml.cds.v5_0.MedicationsAndTreatments;

@Component
public class CDSMedicationExportMapper extends AbstractCDSExportMapper<MedicationsAndTreatments, Medication>
{
	public CDSMedicationExportMapper()
	{
		super();
	}

	@Override
	public MedicationsAndTreatments exportFromJuno(Medication medication)
	{
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();

		medicationsAndTreatments.setDrugName(medication.getDrugName());
		medicationsAndTreatments.setPrescriptionWrittenDate(toNullableDateTimeFullOrPartial(medication.getWrittenDate()));
		medicationsAndTreatments.setStartDate(toNullableDateFullOrPartial(medication.getRxStartDate()));
		medicationsAndTreatments.setNumberOfRefills(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setForm(medication.getDrugForm());
		medicationsAndTreatments.setRoute(medication.getRoute());

		FrequencyCode frequencyCode = medication.getFrequencyCode();
		medicationsAndTreatments.setFrequency((frequencyCode != null) ? frequencyCode.getCode() : null);
		medicationsAndTreatments.setDuration(medication.getDuration());
		medicationsAndTreatments.setRefillDuration(toStringOrNull(medication.getRefillDuration()));
		medicationsAndTreatments.setQuantity(medication.getQuantity());
		medicationsAndTreatments.setRefillQuantity(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setLongTermMedication(toYnIndicator(medication.getLongTerm()));
		medicationsAndTreatments.setPastMedications(toYnIndicator(medication.getPastMed()));

		Provider prescribingProvider = medication.getPrescribingProvider();
		if(prescribingProvider != null)
		{
			MedicationsAndTreatments.PrescribedBy prescribedBy = objectFactory.createMedicationsAndTreatmentsPrescribedBy();
			prescribedBy.setName(toPersonNameSimple(prescribingProvider));
			prescribedBy.setOHIPPhysicianId(prescribingProvider.getOhipNumber());
			medicationsAndTreatments.setPrescribedBy(prescribedBy);
		}

		medicationsAndTreatments.setNotes(medication.getComment());
		medicationsAndTreatments.setPrescriptionInstructions(
				StringUtils.trimToNull(
					StringUtils.trimToEmpty(medication.getInstructions()) + "\n" +
						StringUtils.trimToEmpty(medication.getSpecialInstructions())
				));
		medicationsAndTreatments.setPatientCompliance(toYnIndicator(medication.getPatientCompliance()));
		medicationsAndTreatments.setTreatmentType((medication.getETreatmentType() != null) ? medication.getETreatmentType().getValue() : null);
		medicationsAndTreatments.setPrescriptionStatus((medication.getRxStatus() != null) ? medication.getRxStatus().getValue() : null);
		medicationsAndTreatments.setNonAuthoritativeIndicator(toStringOrNull(medication.getNonAuthoritative()));
		medicationsAndTreatments.setPriorPrescriptionReferenceIdentifier(null); //TODO
		medicationsAndTreatments.setDispenseInterval(toStringOrNull(medication.getDispenseInterval()));
		medicationsAndTreatments.setDrugDescription(null); //TODO
		medicationsAndTreatments.setProblemCode(null); //TODO
		medicationsAndTreatments.setProtocolIdentifier(null); //TODO

		if(medication instanceof StandardMedication)
		{
			return fillStandardDrugElements(medicationsAndTreatments, (StandardMedication) medication);
		}
		else if(medication instanceof CustomMedication)
		{
			return fillCustomDrugElements(medicationsAndTreatments, (CustomMedication) medication);
		}
		else
		{
			return medicationsAndTreatments;
		}
	}

	protected MedicationsAndTreatments fillStandardDrugElements(MedicationsAndTreatments medicationsAndTreatments,
	                                                            StandardMedication medication)
	{
		medicationsAndTreatments.setDrugIdentificationNumber(medication.getRegionalIdentifier());
		medicationsAndTreatments.setDosage(medication.getDosage());
		medicationsAndTreatments.setDosageUnitOfMeasure(medication.getUnit());
		medicationsAndTreatments.setStrength(getDrugMeasure(medication));

		medicationsAndTreatments.setSubstitutionNotAllowed(toYnIndicatorString(medication.getNoSubs()));

		return medicationsAndTreatments;
	}

	protected MedicationsAndTreatments fillCustomDrugElements(MedicationsAndTreatments medicationsAndTreatments,
	                                                          CustomMedication medication)
	{
		return medicationsAndTreatments;
	}

	protected DrugMeasure getDrugMeasure(StandardMedication exportStructure)
	{
		DrugMeasure drugMeasure = null;

		String strengthAmount = exportStructure.getStrengthAmount();
		String strengthUnit = exportStructure.getStrengthUnit();
		if(strengthAmount != null && strengthUnit != null)
		{
			drugMeasure = objectFactory.createDrugMeasure();
			drugMeasure.setAmount(strengthAmount);
			drugMeasure.setUnitOfMeasure(strengthUnit);
		}
		return drugMeasure;
	}

	protected String toStringOrNull(Boolean bool)
	{
		String boolStr = null;
		if(bool != null)
		{
			boolStr = String.valueOf(bool);
		}
		return boolStr;
	}
	protected String toStringOrNull(Integer num)
	{
		String numString = null;
		if(num != null)
		{
			numString = String.valueOf(num);
		}
		return numString;
	}
}
