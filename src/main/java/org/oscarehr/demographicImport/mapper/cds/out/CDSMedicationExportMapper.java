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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.oscarehr.common.xml.cds.v5_0.model.DrugMeasure;
import org.oscarehr.common.xml.cds.v5_0.model.MedicationsAndTreatments;
import org.oscarehr.common.xml.cds.v5_0.model.YnIndicator;
import org.oscarehr.demographicImport.model.medication.CustomMedication;
import org.oscarehr.demographicImport.model.medication.Medication;
import org.oscarehr.demographicImport.model.medication.StandardMedication;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.stereotype.Component;

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

		medicationsAndTreatments.setPrescriptionWrittenDate(toNullableDateTimeFullOrPartial(medication.getWrittenDate()));
		medicationsAndTreatments.setStartDate(toNullableDateFullOrPartial(medication.getRxStartDate()));
		medicationsAndTreatments.setNumberOfRefills(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setForm(medication.getDrugForm());
		medicationsAndTreatments.setRoute(medication.getRoute());
		medicationsAndTreatments.setFrequency(medication.getFreqCode());
		medicationsAndTreatments.setDuration(medication.getDuration());
		medicationsAndTreatments.setRefillDuration(toStringOrNull(medication.getRefillDuration()));
		medicationsAndTreatments.setQuantity(medication.getQuantity());
		medicationsAndTreatments.setRefillQuantity(toStringOrNull(medication.getRefillQuantity()));
		medicationsAndTreatments.setLongTermMedication(getLongTermIndicator(medication));
		medicationsAndTreatments.setPastMedications(getPastMedicationIndicator(medication));

		Provider prescribingProvider = medication.getPrescribingProvider();
		if(prescribingProvider != null)
		{
			MedicationsAndTreatments.PrescribedBy prescribedBy = objectFactory.createMedicationsAndTreatmentsPrescribedBy();
			prescribedBy.setName(toPersonNameSimple(prescribingProvider));
			prescribedBy.setOHIPPhysicianId(prescribingProvider.getOhipNumber());
			medicationsAndTreatments.setPrescribedBy(prescribedBy);
		}

		medicationsAndTreatments.setNotes(medication.getComment());
		medicationsAndTreatments.setPrescriptionInstructions(medication.getInstructions());
		medicationsAndTreatments.setPatientCompliance(getComplianceIndicator(medication));
		medicationsAndTreatments.setTreatmentType(medication.getETreatmentType());
		medicationsAndTreatments.setPrescriptionStatus(medication.getRxStatus());
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
		String drugName = (medication.getBrandName() != null) ? medication.getBrandName() : medication.getGenericName();
		medicationsAndTreatments.setDrugName(drugName);

		medicationsAndTreatments.setDrugIdentificationNumber(medication.getRegionalIdentifier());
		medicationsAndTreatments.setDosage(medication.getDosage());
		medicationsAndTreatments.setDosageUnitOfMeasure(medication.getUnit());
		medicationsAndTreatments.setStrength(getDrugMeasure(medication));

		medicationsAndTreatments.setSubstitutionNotAllowed(getSubsNotAllowedIndicator(medication));

		return medicationsAndTreatments;
	}

	protected MedicationsAndTreatments fillCustomDrugElements(MedicationsAndTreatments medicationsAndTreatments,
	                                                          CustomMedication medication)
	{
		medicationsAndTreatments.setDrugName(medication.getCustomName());


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

	protected YnIndicator getLongTermIndicator(Medication exportStructure)
	{
		YnIndicator ynIndicator = objectFactory.createYnIndicator();
		ynIndicator.setBoolean(exportStructure.getLongTerm());
		return ynIndicator;
	}
	protected YnIndicator getPastMedicationIndicator(Medication exportStructure)
	{
		YnIndicator ynIndicator = objectFactory.createYnIndicator();
		ynIndicator.setBoolean(exportStructure.getPastMed());
		return ynIndicator;
	}
	protected YnIndicator getComplianceIndicator(Medication exportStructure)
	{
		YnIndicator ynIndicator = objectFactory.createYnIndicator();
		ynIndicator.setBoolean(exportStructure.getPatientCompliance());
		return ynIndicator;
	}
	protected String getSubsNotAllowedIndicator(StandardMedication exportStructure)
	{
		return exportStructure.getNoSubs() ? "Y" : "N";
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
