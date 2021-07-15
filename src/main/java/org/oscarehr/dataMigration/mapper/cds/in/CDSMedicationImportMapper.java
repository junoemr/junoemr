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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.dataMigration.exception.InvalidFrequencyCodeException;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.medication.CustomMedication;
import org.oscarehr.dataMigration.model.medication.FrequencyCode;
import org.oscarehr.dataMigration.model.medication.Medication;
import org.oscarehr.dataMigration.model.medication.StandardMedication;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.DrugMeasure;
import xml.cds.v5_0.MedicationsAndTreatments;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CDSMedicationImportMapper extends AbstractCDSImportMapper<MedicationsAndTreatments, Medication>
{
	private static final Logger logger = Logger.getLogger(CDSMedicationImportMapper.class);

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

		// "dosage" in CDS == takemin/takemax
		if (importStructure.getDosage() != null)
		{
			medication.setTakeMax(Float.parseFloat(importStructure.getDosage()));
			medication.setTakeMin(Float.parseFloat(importStructure.getDosage()));
		}

		medication.setWrittenDate(getWrittenDate(importStructure));
		medication.setRxStartDate(getStartDate(importStructure));

		// Medication refills
		medication.setRepeat(toIntOrNull(importStructure.getNumberOfRefills()));
		medication.setRefillQuantity(toIntOrNull(importStructure.getRefillQuantity()));
		medication.setRefillDuration(toIntOrNull(importStructure.getRefillDuration()));

		medication.setDrugForm(importStructure.getForm());
		medication.setRoute(importStructure.getRoute());
		medication.setFrequencyCode(getFormattedFrequency(importStructure));
		medication.setDuration(importStructure.getDuration());
		medication.setDurationUnit(null); //TODO - is there a cds default?

		medication.setQuantity(importStructure.getQuantity());
		medication.setLongTerm(getYIndicator(importStructure.getLongTermMedication()));
		medication.setPastMed(getYIndicator(importStructure.getPastMedications()));
		medication.setPrescribingProvider(getPrescribingProvider(importStructure));
		medication.setComment(importStructure.getNotes());
		medication.setInstructions(importStructure.getPrescriptionInstructions());
		medication.setPatientCompliance(getYIndicator(importStructure.getPatientCompliance()));
		medication.setETreatmentType(CDSConstants.TreatmentType.fromValue(importStructure.getTreatmentType()));
		medication.setRxStatus(CDSConstants.PrescriptionStatus.fromValue(importStructure.getPrescriptionStatus()));
		medication.setNonAuthoritative(toBooleanOrNull(importStructure.getNonAuthoritativeIndicator()));
		medication.setDispenseInterval(toIntOrNull(importStructure.getDispenseInterval()));
		medication.setRxEndDate(getEndDate(importStructure));
		medication.setResidualInfo(generateResidualInfo(importStructure));

		return medication;
	}

	protected List<ResidualInfo> generateResidualInfo(MedicationsAndTreatments importStructure)
	{
		List<ResidualInfo> residualInfoList = importAllResidualInfo(importStructure.getResidualInfo());
		if (residualInfoList == null)
		{
			residualInfoList = new ArrayList<>(5); // If residualInfoList is null, max item count is 5
		}

		// No other place to put the following items.
		if (importStructure.getPrescriptionIdentifier() != null)
		{
			ResidualInfo prescriptionIdentifier = new ResidualInfo();
			prescriptionIdentifier.setContentKey("PrescriptionIdentifier");
			prescriptionIdentifier.setContentType("String");
			prescriptionIdentifier.setContentValue(importStructure.getPrescriptionIdentifier());
			residualInfoList.add(prescriptionIdentifier);
		}

		if (importStructure.getPriorPrescriptionReferenceIdentifier() != null)
		{
			ResidualInfo priorPrescriptionReferenceIdentifier = new ResidualInfo();
			priorPrescriptionReferenceIdentifier.setContentKey("PriorPrescriptionReferenceIdentifier");
			priorPrescriptionReferenceIdentifier.setContentType("String");
			priorPrescriptionReferenceIdentifier.setContentValue(importStructure.getPriorPrescriptionReferenceIdentifier());
			residualInfoList.add(priorPrescriptionReferenceIdentifier);
		}

		if (importStructure.getDrugDescription() != null)
		{
			ResidualInfo drugDescription = new ResidualInfo();
			drugDescription.setContentKey("DrugDescription");
			drugDescription.setContentType("String");
			drugDescription.setContentValue(importStructure.getDrugDescription());
			residualInfoList.add(drugDescription);
		}

		if (importStructure.getProblemCode() != null)
		{
			ResidualInfo problemCode = new ResidualInfo();
			problemCode.setContentKey("ProblemCode");
			problemCode.setContentType("String");
			problemCode.setContentValue(importStructure.getProblemCode());
			residualInfoList.add(problemCode);
		}

		if (importStructure.getProtocolIdentifier() != null)
		{
			ResidualInfo protocolIdentifier = new ResidualInfo();
			protocolIdentifier.setContentKey("ProtocolIdentifier");
			protocolIdentifier.setContentType("String");
			protocolIdentifier.setContentValue(importStructure.getProtocolIdentifier());
			residualInfoList.add(protocolIdentifier);
		}

		if (residualInfoList.isEmpty())
		{
			return null;
		}
		return residualInfoList;
	}

	protected String getOutsideProviderName(MedicationsAndTreatments.PrescribedBy prescribedBy)
	{
		if (prescribedBy == null || prescribedBy.getName() == null)
		{
			return null;
		}
		return prescribedBy.getName().getFirstName() + " " + prescribedBy.getName().getLastName();
	}

	protected String getOutsideProviderOhip(MedicationsAndTreatments.PrescribedBy prescribedBy)
	{
		if (prescribedBy == null || prescribedBy.getOHIPPhysicianId() == null)
		{
			return null;
		}
		return prescribedBy.getOHIPPhysicianId();
	}

	protected FrequencyCode getFormattedFrequency(MedicationsAndTreatments importStructure)
	{
		String freqCode = importStructure.getFrequency();
		if(freqCode != null)
		{
			return FrequencyCode.from(StringUtils.trimToNull(freqCode.replaceAll("PRN", "")));
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

	protected PartialDateTime getWrittenDate(MedicationsAndTreatments importStructure)
	{
		return toNullablePartialDateTime(importStructure.getPrescriptionWrittenDate());
	}
	protected PartialDate getStartDate(MedicationsAndTreatments importStructure)
	{
		return toNullablePartialDate(importStructure.getStartDate());
	}
	protected PartialDate getEndDate(MedicationsAndTreatments importStructure)
	{
		PartialDate partialStartDate = getStartDate(importStructure);
		PartialDate partialEndDate = null;
		if(partialStartDate == null)
		{
			partialStartDate = toNullablePartialDateTime(importStructure.getPrescriptionWrittenDate());
		}
		if(partialStartDate == null)
		{
			partialStartDate = PartialDate.from(LocalDate.now()); // it can't be null
		}

		FrequencyCode frequencyCode = getFormattedFrequency(importStructure);
		String quantity = importStructure.getQuantity();
		String dosageStr = importStructure.getDosage();

		if(frequencyCode != null && StringUtils.isNumeric(quantity) && StringUtils.isNumeric(dosageStr))
		{
			try
			{
				double amount = Double.parseDouble(quantity);
				double dosage = Double.parseDouble(dosageStr);
				partialEndDate = PartialDate.from(Medication.calculateEndDate(partialStartDate.toLocalDate(), frequencyCode, amount, dosage));
			}
			catch(InvalidFrequencyCodeException e)
			{
				logger.error(e.getMessage());
			}
			catch(RuntimeException e)
			{
				logger.error("Error calculating medication end date", e);
			}
		}

		if(partialEndDate == null)
		{
			partialEndDate = partialStartDate; // it also can't be null
		}
		return partialEndDate;
	}


	protected Boolean toBooleanOrNull(String indicator)
	{
		indicator = StringUtils.trimToNull(indicator);
		if(indicator != null)
		{
			if (StringUtils.isNumeric(indicator))
			{
				return !"0".equals(indicator);
			}
			else if (indicator.equals(CDSConstants.Y_INDICATOR_TRUE))
			{
				return true;
			}
			else if (indicator.equals(CDSConstants.Y_INDICATOR_TRUE))
			{
				return false;
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
}
