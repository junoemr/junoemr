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
import org.oscarehr.dataMigration.model.provider.ProviderModel;
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
		// Medication Refills
		medicationsAndTreatments.setNumberOfRefills(toStringOrNull(medication.getRepeat()));
		medicationsAndTreatments.setRefillQuantity(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setRefillDuration(toStringOrNull(medication.getRefillDuration()));

		medicationsAndTreatments.setForm(medication.getDrugForm());
		medicationsAndTreatments.setRoute(medication.getRoute());

		FrequencyCode frequencyCode = medication.getFrequencyCode();
		medicationsAndTreatments.setFrequency((frequencyCode != null) ? frequencyCode.getCode() : null);
		medicationsAndTreatments.setDuration(medication.getDuration());
		medicationsAndTreatments.setQuantity(medication.getQuantity());
		medicationsAndTreatments.setRefillQuantity(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setLongTermMedication(toYnIndicator(medication.getLongTerm()));
		medicationsAndTreatments.setPastMedications(toYnIndicator(medication.getPastMed()));
		medicationsAndTreatments.setDosage(generateCDSDosage(medication.getTakeMin(), medication.getTakeMax()));
		medicationsAndTreatments.setDrugIdentificationNumber(medication.getRegionalIdentifier());
		medicationsAndTreatments.setDosageUnitOfMeasure(medication.getUnit());
		medicationsAndTreatments.setStrength(getDrugMeasure(medication));
		medicationsAndTreatments.setSubstitutionNotAllowed(toYnIndicatorString(medication.getNoSubs()));

		ProviderModel prescribingProvider = medication.getPrescribingProvider();
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

	/**
	 * Builds a string for the CDS Dosage field based on the provided takeMin and takeMax
	 * Will produce format of "X" or "X - Y"
	 * @param takeMin Medication takeMin value
	 * @param takeMax Medication takeMax value
	 * @return string CDS Dosage
	 */
	protected String generateCDSDosage(float takeMin, float takeMax)
	{
		if (takeMin == takeMax)
		{
			return String.valueOf(takeMin);
		} 
		return takeMin + " - " + takeMax;
	}

	/**
	 * Not used for the CDS Exports. Left in to show this is how you would fill
	 * elements that are specific to StandardMedications, and to keep design pattern the same
	 * when building out to other data formats
	 * @param medicationsAndTreatments The CDS MedicationsAndTreatments to add elements to
	 * @param medication StandardMedication to pull elements from
	 * @return medicationsAndTreatments filled with StandardMedication elements
	 */
	protected MedicationsAndTreatments fillStandardDrugElements(MedicationsAndTreatments medicationsAndTreatments,
	                                                            StandardMedication medication)
	{
		return medicationsAndTreatments;
	}

	/**
	 * Not used for the CDS Exports. Left in to show this is how you would fill
	 * elements that are specific to CustomMedications, and to keep design pattern the same
	 * when building out to other data formats
	 * @param medicationsAndTreatments The CDS MedicationsAndTreatments to add elements to
	 * @param medication CustomMedication to pull elements from
	 * @return medicationsAndTreatments filled with CustomMedication elements
	 */
	protected MedicationsAndTreatments fillCustomDrugElements(MedicationsAndTreatments medicationsAndTreatments,
	                                                          CustomMedication medication)
	{
		return medicationsAndTreatments;
	}

	protected DrugMeasure getDrugMeasure(Medication exportStructure)
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
