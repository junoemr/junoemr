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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.rx.model.Prescription;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MedicationMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public MedicationMapper()
	{
		message = null;
		provider = null;
	}
	public MedicationMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
	}

	public int getNumMedications()
	{
		return provider.getMEDSReps();
	}

	public Drug getDrug(int rep) throws HL7Exception
	{
		Drug drug = new Drug();

		Date writtenDate = getTransactionDate(rep);
		drug.setCreateDate(writtenDate);
		drug.setWrittenDate(writtenDate);
		drug.setRxDate(writtenDate);
		drug.setEndDate(writtenDate);

		// import drugs as custom
		drug.setCustomName(getRequestedGiveCodeText(rep));
		drug.setCustomInstructions(false);

		drug.setQuantity(String.valueOf(getRequestedDispenseAmount(rep)));
		drug.setUnitName(getRequestedDispenseUnits(rep));
		drug.setRepeat(getNumberOfRefills(rep));
		drug.setNoSubs(!allowSubstitutions(rep));

		drug.setRoute(getRouteId(rep));
		drug.setFreqCode(getFrequencyCode(rep, 0));

		drug.setDispenseInterval(getDispenseInterval(rep));
		drug.setDrugForm(getDispenseUnitsId(rep));

		drug.setPosition(rep+1); // this is for display order
		drug.setSpecialInstruction(getPharmacyInstructions(rep));

		drug.setSpecial(generateSpecial(drug));
		return drug;
	}

	public Prescription getPrescription(int rep)
	{
		Prescription prescription = new Prescription();
		Date writtenDate = getTransactionDate(rep);
		prescription.setDatePrescribed(writtenDate);

		return prescription;
	}

	public String generateSpecial(Drug drug)
	{
		List<String> valueList = new ArrayList<>();
		valueList.add(StringUtils.trimToEmpty(drug.getCustomName()));
		if(drug.getDosage() != null)
		{
			valueList.add(drug.getDosage());
		}
		if(drug.getRoute() != null)
		{
			valueList.add(drug.getRoute());
		}
		if(drug.getFreqCode() != null)
		{
			valueList.add(drug.getFreqCode());
		}
		if(drug.getSpecialInstruction() != null)
		{
			valueList.add(drug.getSpecialInstruction());
		}

		String quantity = "Qty:" + (drug.getQuantity() == null ? "0" : drug.getQuantity());
		String quantityUnits = " " + (drug.getUnitName() == null ? "" : drug.getUnitName());
		String repeat = "Repeats:" + (drug.getRepeat() == null ? 0 : drug.getRepeat());
		valueList.add(quantity + quantityUnits + " " + repeat);

		return String.join("\n", valueList);
	}

	public CaseManagementNote getDrugNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = null;
		String noteText = getNoteText(rep);

		if(noteText != null)
		{
			note = new CaseManagementNote();

			Date writtenDate = getTransactionDate(rep);
			note.setObservationDate(writtenDate);
			note.setUpdateDate(writtenDate);
			note.setNote(noteText);
		}

		return note;
	}

	// ---- ORC ----

	public Date getTransactionDate(int rep)
	{
		return ConversionUtils.fromDateString(provider.getMEDS(rep).getORC()
				.getOrc9_DateTimeOfTransaction().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	public Date getOrderEffectiveDate(int rep)
	{
		return ConversionUtils.fromDateString(provider.getMEDS(rep).getORC()
				.getOrc15_OrderEffectiveDateTime().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}

	// ---- RXO ----

	public String getRequestedGiveCodeId(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo1_RequestedGiveCode().getCwe1_Identifier().getValue());
	}

	public String getRequestedGiveCodeText(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo1_RequestedGiveCode().getCwe2_Text().getValue());
	}

	public String getRequestedGiveCodeNameOfCodingSystem(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo1_RequestedGiveCode().getCwe3_NameOfCodingSystem().getValue());
	}

	public String getPharmacyInstructions(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo6_ProviderSPharmacyTreatmentInstructions(0).getCwe9_OriginalText().getValue());
	}

	public boolean allowSubstitutions(int rep)
	{
		String subsCode = provider.getMEDS(rep).getRXO().getRxo9_AllowSubstitutions().getValue();
		return (subsCode.equalsIgnoreCase("Y"));
	}

	public Double getRequestedDispenseAmount(int rep)
	{
		String dosageStr = StringUtils.trimToNull(provider.getMEDS(rep).getRXO().
				getRxo11_RequestedDispenseAmount().getValue());
		if(dosageStr != null)
		{
			return Double.parseDouble(dosageStr);
		}
		return null;
	}
	public String getRequestedDispenseUnits(int rep)
	{
		String dosageUnitStr = StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo12_RequestedDispenseUnits().getCe1_Identifier().getValue());
		// default to Rxo4 (Requested Give Units) if blank
		if(dosageUnitStr == null)
		{
			dosageUnitStr = StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
					.getRxo4_RequestedGiveUnits().getCe1_Identifier().getValue());
		}
		return dosageUnitStr;
	}

	public Integer getNumberOfRefills(int rep)
	{
		return Integer.parseInt(provider.getMEDS(rep).getRXO().getRxo13_NumberOfRefills().getValue());
	}

	public Integer getDispenseInterval(int rep) throws HL7Exception
	{
		String intervalStr = StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo28_dispensingInterval(0).getCq1_Quantity().getValue());
		if(intervalStr != null)
		{
			return Integer.parseInt(intervalStr);
		}
		return null;
	}

	// ---- TIMING_QUANTITY ----

	public Integer getQuantity(int rep, int tq1Rep)
	{
		String quantityStr = StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq12_Quantity().getCq1_Quantity().getValue());
		if(quantityStr != null)
		{
			return Integer.parseInt(quantityStr);
		}
		return 0;
	}
	public String getQuantityUnitId(int rep, int tq1Rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq12_Quantity().getCq2_Units().getCe1_Identifier().getValue());
	}
	public String getQuantityUnitText(int rep, int tq1Rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq12_Quantity().getCq2_Units().getCe2_Text().getValue());
	}

	public String getFrequencyCode(int rep, int tq1Rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq13_RepeatPattern(0).getRpt1_RepeatPatternCode().getCwe1_Identifier().getValue());
	}

	// ---- RXE ----

	public String getDispenseUnitsId(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXE()
				.getRxe11_DispenseUnits().getCe1_Identifier().getValue());
	}

	public String getDispenseUnitsText(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXE()
				.getRxe11_DispenseUnits().getCe2_Text().getValue());
	}

	// ---- RXR ----

	public String getRouteId(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXR().getRxr1_Route().getCe1_Identifier().getValue());
	}

	public String getRouteText(int rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getRXR().getRxr1_Route().getCe2_Text().getValue());
	}

	// ---- NOTES ----

	public String getNoteText(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getNOTES().getNTE().getNte3_Comment(0).getValue());
	}
}
