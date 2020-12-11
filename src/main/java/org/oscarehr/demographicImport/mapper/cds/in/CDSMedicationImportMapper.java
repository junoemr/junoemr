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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.xml.cds.v5_0.model.DrugMeasure;
import org.oscarehr.common.xml.cds.v5_0.model.MedicationsAndTreatments;
import org.oscarehr.common.xml.cds.v5_0.model.YnIndicator;
import org.oscarehr.demographicImport.model.medication.CustomMedication;
import org.oscarehr.demographicImport.model.medication.Medication;
import org.oscarehr.demographicImport.model.medication.StandardMedication;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.stereotype.Component;

import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.Y_INDICATOR_TRUE;

@Component
public class CDSMedicationImportMapper extends AbstractCDSImportMapper<MedicationsAndTreatments, Medication>
{
	public CDSMedicationImportMapper()
	{
		super();
	}

	@Override
	public Medication importToJuno(MedicationsAndTreatments importStructure)
	{
		Medication medication;
		String din = importStructure.getDrugIdentificationNumber();
		String drugName = importStructure.getDrugName();

		if(din != null)
		{
			StandardMedication standardMedication = new StandardMedication();
			standardMedication.setRegionalIdentifier(din);
			standardMedication.setBrandName(drugName);
			standardMedication.setDosage(importStructure.getDosage());
			standardMedication.setUnit(importStructure.getDosageUnitOfMeasure());
			standardMedication.setNoSubs(toBooleanOrNull(importStructure.getSubstitutionNotAllowed()));

			DrugMeasure drugMeasure = importStructure.getStrength();
			if(drugMeasure != null)
			{
				standardMedication.setStrengthAmount(drugMeasure.getAmount());
				standardMedication.setStrengthUnit(drugMeasure.getUnitOfMeasure());
			}
			medication = standardMedication;
		}
		else
		{
			CustomMedication customMedication = new CustomMedication();
			customMedication.setCustomName(drugName);
			medication = customMedication;
		}

		medication.setWrittenDate(toNullablePartialDateTime(importStructure.getPrescriptionWrittenDate()));
		medication.setRxStartDate(toNullablePartialDate(importStructure.getStartDate()));
		medication.setRefillQuantity(toIntOrNull(importStructure.getRefillQuantity()));
		medication.setDrugForm(importStructure.getForm());
		medication.setRoute(importStructure.getRoute());
		medication.setFrequencyCode(getFormattedFrequency(importStructure));
		medication.setDuration(importStructure.getDuration());
		medication.setDurationUnit(null); //TODO - is there a cds default?
		medication.setRefillDuration(toIntOrNull(importStructure.getRefillDuration()));
		medication.setQuantity(importStructure.getQuantity());
		medication.setRefillQuantity(toIntOrNull(importStructure.getRefillQuantity()));
		medication.setLongTerm(getYIndicator(importStructure.getLongTermMedication()));
		medication.setPastMed(getYIndicator(importStructure.getPastMedications()));
		medication.setPrescribingProvider(getPrescribingProvider(importStructure));
		medication.setComment(importStructure.getNotes());
		medication.setInstructions(importStructure.getPrescriptionInstructions());
		medication.setPatientCompliance(getYIndicator(importStructure.getPatientCompliance()));
		medication.setETreatmentType(importStructure.getTreatmentType());
		medication.setRxStatus(importStructure.getPrescriptionStatus());
		medication.setNonAuthoritative(toBooleanOrNull(importStructure.getNonAuthoritativeIndicator()));
		// TODO importStructure.getPriorPrescriptionReferenceIdentifier();
		medication.setDispenseInterval(toIntOrNull(importStructure.getDispenseInterval()));
		// TODO importStructure.getDrugDescription();
		// TODO importStructure.getProblemCode();
		// TODO importStructure.getProtocolIdentifier();


		return medication;
	}

	protected String getFormattedFrequency(MedicationsAndTreatments importStructure)
	{
		String freqCode = importStructure.getFrequency();
		if(freqCode != null)
		{
			return StringUtils.trimToNull(freqCode.replaceAll("PRN", ""));
		}
		return null;
	}

	protected Provider getPrescribingProvider(MedicationsAndTreatments importStructure)
	{
		Provider provider = null;
		MedicationsAndTreatments.PrescribedBy prescribedBy = importStructure.getPrescribedBy();
		if(prescribedBy != null)
		{
			provider = toProvider(prescribedBy.getName());
			provider.setOhipNumber(prescribedBy.getOHIPPhysicianId());
		}
		return provider;
	}

	protected Boolean toBooleanOrNull(String indicator)
	{
		indicator = StringUtils.trimToNull(indicator);
		if(indicator != null)
		{
			if(StringUtils.isNumeric(indicator))
			{
				return !"0".equals(indicator);
			}
			else
			{
				return Boolean.parseBoolean(indicator);
			}
		}
		return null;
	}

	//TODO does this exist somewhere?
	protected Integer toIntOrNull(String integerStr)
	{
		integerStr = StringUtils.trimToNull(integerStr);
		if(StringUtils.isNumeric(integerStr))
		{
			return Integer.parseInt(integerStr);
		}
		return null;
	}

	protected Boolean getYIndicator(YnIndicator ynIndicator)
	{
		if(ynIndicator != null)
		{
			String yIndicatorValue = ynIndicator.getYnIndicatorsimple();
			if(yIndicatorValue != null)
			{
				return yIndicatorValue.equals(Y_INDICATOR_TRUE);
			}
			else
			{
				return ynIndicator.isBoolean();
			}
		}
		return null;
	}
}
