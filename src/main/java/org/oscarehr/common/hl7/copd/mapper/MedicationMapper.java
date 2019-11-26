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
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.rx.model.Prescription;
import oscar.oscarRx.data.RxPrescriptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MedicationMapper extends AbstractMapper
{
	public MedicationMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public int getNumMedications()
	{
		return provider.getMEDSReps();
	}

	public Drug getDrug(int rep) throws HL7Exception
	{
		Drug drug = new Drug();

		Date writtenDate;
		Date startDate;
		Date endDate;
		switch(importSource)
		{
			case WOLF:
			{
				// prescribed date: ORC-9 else ORC-15, else ZRX-3
				writtenDate = getWOLFCreatedDate(rep);
				// start date: ORC-15, else ZRX-3
				startDate = getWOLFStartDate(rep);
				// end date: ZRX-3, else calculate? else ORC-15(start date)
				endDate = getWOLFEndDate(rep);
				break;
			}
			default:
			case UNKNOWN:
			{
				writtenDate = getTransactionDate(rep);
				startDate = getAdministrationStartDate(rep);
				endDate = getAdministrationStopDate(rep);
				if(endDate == null)
				{
					// try to calculate from TQ1
					endDate = getCalculatedEndDate(rep, startDate);
					if (endDate == null)
					{
						//end date can't be null
						endDate = startDate;
					}
				}
				break;
			}
		}

		drug.setCreateDate(writtenDate);
		drug.setWrittenDate(writtenDate);
		drug.setRxDate(startDate);
		drug.setEndDate(endDate);
		drug.setLongTerm(isLongTerm(rep));

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

		drug.setPosition(0); // this is display order. set to all zero so that medications are ordered by date.
		drug.setSpecialInstruction(getPharmacyInstructions(rep));

		drug.setSpecial(generateSpecial(drug));
		return drug;
	}

	/**
	 * check if the drug at rep is the most recent prescription of the drug.
	 * @param rep - the rep of the drug to check.
	 * @return - true if the drug is the most recent, false otherwise.
	 */
	public boolean isDrugMostRecent(int rep) throws HL7Exception
	{
		String drugId = getRequestedGiveCodeId(rep);
		Date drugStart = getAdministrationStartDate(rep);

		if (drugStart != null && drugId != null && !"NONDRUG".equals(drugId))
		{
			for (int i = 0; i < getNumMedications(); i++)
			{
				if (getAdministrationStartDate(i) != null && getRequestedGiveCodeId(i) != null && drugId.equals(getRequestedGiveCodeId(i)) && drugStart.before(getAdministrationStartDate(i)))
				{
					return false;
				}
			}
		}

		return true;
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

	/** ORC-9 else ORC-15, else ZRX-3 */
	private Date getWOLFCreatedDate(int rep) throws HL7Exception
	{
		Date createdDate = getTransactionDate(rep);
		if(createdDate == null)
		{
			createdDate = getOrderEffectiveDate(rep);
		}
		if(createdDate == null)
		{
			createdDate = getAdministrationStopDate(rep);
		}
		return createdDate;
	}

	/** start date: ORC-15, else ZRX-3 */
	private Date getWOLFStartDate(int rep) throws HL7Exception
	{
		Date startDate = getOrderEffectiveDate(rep);
		if(startDate == null)
		{
			startDate = getAdministrationStopDate(rep);
		}
		return startDate;
	}

	/** ZRX-3, else calculate? else start date */
	private Date getWOLFEndDate(int rep) throws HL7Exception
	{
		Date endDate = getAdministrationStopDate(rep);
		if(endDate == null)
		{
			endDate = getCalculatedEndDate(rep, getWOLFStartDate(rep));
		}
		if(endDate == null)
		{
			endDate = getOrderEffectiveDate(rep);
		}
		return endDate;
	}

	private Date getCalculatedEndDate(int rep, Date rxDate)
	{
		if (hasTimingQuantity(rep))
		{
			List<String> durationUnits = Arrays.asList("W", "M", "D", "Y");
			Integer duration = getServiceDurationQuantity(rep, 0);
			String durationUnit = translateDurationUnits(getServiceDurationUnit(rep, 0));
			int repeats = getNumberOfRefills(rep);

			if (rxDate != null && duration > 0 && durationUnit != null && durationUnits.contains(durationUnit.substring(0, 1).toUpperCase()))
			{
				durationUnit = durationUnit.substring(0, 1).toUpperCase();
				return RxPrescriptionData.Prescription.calcEndDate(rxDate, duration.toString(), durationUnit, repeats);
			}
		}
		return null;
	}

	// ---- ORC ----

	public Date getTransactionDate(int rep)
	{
		return getNullableDate(provider.getMEDS(rep).getORC()
				.getOrc9_DateTimeOfTransaction().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getOrderEffectiveDate(int rep)
	{
		return getNullableDate(provider.getMEDS(rep).getORC()
				.getOrc15_OrderEffectiveDateTime().getTs1_TimeOfAnEvent().getValue());
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
		return subsCode != null && (subsCode.equalsIgnoreCase("Y"));
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

	private boolean hasTimingQuantity(int rep)
	{
		return provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1Reps() > 0;
	}

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

	public int getServiceDurationQuantity(int rep, int tq1Rep)
	{
		String durationUnitStr = StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq16_ServiceDuration().getCq1_Quantity().getValue());
		if(durationUnitStr != null)
		{
			return Integer.parseInt(durationUnitStr);
		}
		return 0;
	}
	public String getServiceDurationUnit(int rep, int tq1Rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq16_ServiceDuration().getCq2_Units().getCe1_Identifier().getValue());
	}

	/**
	 * translate duration unit in to standard form.
	 * @param durationUnit - unit to translate
	 * @return - the standard format of the duration unit
	 */
	private String translateDurationUnits(String durationUnit)
	{
		HashMap<String, String> durationHash = new HashMap<>();

		durationHash.put("W", "W");
		durationHash.put("D", "D");
		durationHash.put("M", "M");
		durationHash.put("Week", "W");
		durationHash.put("Day", "D");
		durationHash.put("MO30", "M");
		durationHash.put("Year", "Y");

		if (durationHash.containsKey(durationUnit))
		{
			return durationHash.get(durationUnit);
		}
		else
		{
			throw new IllegalArgumentException("Duration [" + durationUnit + "] has no mapping");
		}
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

	// ---- ZRX ----

	public Date getAdministrationStartDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getMEDS(rep).getZRX()
				.getZrx2_administrationStartDate().getTs1_TimeOfAnEvent().getValue());
	}
	public Date getAdministrationStopDate(int rep) throws HL7Exception
	{
		return getNullableDate(provider.getMEDS(rep).getZRX()
				.getZrx3_administrationStopDate().getTs1_TimeOfAnEvent().getValue());
	}

	public boolean isLongTerm(int rep) throws HL7Exception
	{
		String drugUseType = StringUtils.trimToEmpty(provider.getMEDS(rep).getZRX().getZrx6_drugUseType().getCe1_Identifier().getValue());

		switch(drugUseType.toUpperCase())
		{
			case "C" : return true; // for Continuous, long term use
			case "S" :              // for Short term use
			default  : return false;
		}
	}

	// ---- NOTES ----

	public String getNoteText(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getNOTES().getNTE().getNte3_Comment(0).getValue());
	}
}
