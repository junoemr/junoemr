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
import org.oscarehr.demographicImport.model.medication.Medication;
import org.oscarehr.demographicImport.model.provider.Provider;

public class CDSMedicationExportMapper extends AbstractCDSExportMapper<MedicationsAndTreatments, Medication>
{
	public CDSMedicationExportMapper()
	{
		super();
	}

	@Override
	public MedicationsAndTreatments exportFromJuno(Medication exportStructure)
	{
		MedicationsAndTreatments medicationsAndTreatments = objectFactory.createMedicationsAndTreatments();

		medicationsAndTreatments.setPrescriptionWrittenDate(toNullableDateTimeFullOrPartial(exportStructure.getWrittenDate()));
		medicationsAndTreatments.setStartDate(toNullableDateFullOrPartial(exportStructure.getRxStartDate()));
		medicationsAndTreatments.setDrugIdentificationNumber(exportStructure.getRegionalIdentifier());
		medicationsAndTreatments.setDrugName(exportStructure.getName());
		medicationsAndTreatments.setStrength(getDrugMeasure(exportStructure));
		medicationsAndTreatments.setNumberOfRefills(toStringOrNull(exportStructure.getRefillQuantity()));
		medicationsAndTreatments.setDosage(exportStructure.getDosage());
		medicationsAndTreatments.setDosageUnitOfMeasure(exportStructure.getUnit());
		medicationsAndTreatments.setForm(exportStructure.getDrugForm());
		medicationsAndTreatments.setRoute(exportStructure.getRoute());
		medicationsAndTreatments.setFrequency(exportStructure.getFreqCode());
		medicationsAndTreatments.setDuration(exportStructure.getDuration());
		medicationsAndTreatments.setRefillDuration(toStringOrNull(exportStructure.getRefillDuration()));
		medicationsAndTreatments.setQuantity(exportStructure.getQuantity());
		medicationsAndTreatments.setRefillQuantity(toStringOrNull(exportStructure.getRefillQuantity()));
		medicationsAndTreatments.setLongTermMedication(getLongTermIndicator(exportStructure));
		medicationsAndTreatments.setPastMedications(getPastMedicationIndicator(exportStructure));

		Provider prescribingProvider = exportStructure.getPrescribingProvider();
		if(prescribingProvider != null)
		{
			MedicationsAndTreatments.PrescribedBy prescribedBy = objectFactory.createMedicationsAndTreatmentsPrescribedBy();
			prescribedBy.setName(toPersonNameSimple(prescribingProvider));
			prescribedBy.setOHIPPhysicianId(prescribingProvider.getOhipNumber());
			medicationsAndTreatments.setPrescribedBy(prescribedBy);
		}

		medicationsAndTreatments.setNotes(exportStructure.getComment());
		medicationsAndTreatments.setPrescriptionInstructions(exportStructure.getInstructions());
		medicationsAndTreatments.setPatientCompliance(getComplianceIndicator(exportStructure));
		medicationsAndTreatments.setTreatmentType(exportStructure.getETreatmentType());
		medicationsAndTreatments.setPrescriptionStatus(exportStructure.getRxStatus());
		medicationsAndTreatments.setNonAuthoritativeIndicator(toStringOrNull(exportStructure.getNonAuthoritative()));
		medicationsAndTreatments.setPriorPrescriptionReferenceIdentifier(null); //TODO
		medicationsAndTreatments.setDispenseInterval(toStringOrNull(exportStructure.getDispenseInterval()));
		medicationsAndTreatments.setDrugDescription(null); //TODO
		medicationsAndTreatments.setSubstitutionNotAllowed(getSubsNotAllowedIndicator(exportStructure));
		medicationsAndTreatments.setProblemCode(null); //TODO
		medicationsAndTreatments.setProtocolIdentifier(null); //TODO

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
	protected String getSubsNotAllowedIndicator(Medication exportStructure)
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
