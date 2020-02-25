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
import org.oscarehr.util.MiscUtils;
import oscar.oscarRx.data.RxPrescriptionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MedicationMapper extends AbstractMapper
{

	public static final int MEDICATION_DRUG_NAME_LENGTH = 60;

	enum MEDICATION_FREQUENCY_CODES
	{
		BID, 		// Two times daily
		ONCE, 	// One time only
		Q1_2H, 	// Every 1 to 2 hours
		Q12H, 	// Every 12 hours
		Q1H,		// Every hour
		Q2_3H,	// Every 2 to 3 hours
		Q2D,		// Every other day
		Q2H,		// Every 2 hours
		Q3_4H,	// Every 3 to 4 hours
		Q3H,		// Every 3 hours
		Q4_6H,	// Every 4 to 6 hours
		Q4H,		// Every 4 hours
		Q6_8H,	// Every 6 to 8 hours
		Q6H,		// Every 6 hours
		Q8_12H, // Every 8 to 12 hours
		Q8H,		// Every 8 hours
		QAM,		// Every morning
		QD,			// Once daily
		QHS,		// Every day at bedtime
		QID,		// Four times daily
		QNOON,  // Every day at noon
		QPM,		// Every evening
		STAT,   // NOW
		TID  		// Three times a day
	}


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
						// try to calculate from duration, frequency and quantity.
						endDate = getCalculatedEndDateAlternate(rep, startDate);
						if (endDate == null)
						{
							//end date can't be null
							endDate = startDate;
						}
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
		String drugName = getRequestedGiveCodeText(rep);
		if (drugName != null && drugName.length() > MEDICATION_DRUG_NAME_LENGTH)
		{
			logger.warn("Name of custom drug is too long and will be truncated: '" + drugName + "'");
			drugName = StringUtils.left(drugName, MEDICATION_DRUG_NAME_LENGTH);
		}
		drug.setCustomName(drugName);
		drug.setCustomInstructions(false);

		drug.setQuantity(String.valueOf(getRequestedDispenseAmount(rep)));
		drug.setUnitName(getRequestedDispenseUnits(rep));
		drug.setDosage(getDosageString(rep, 0));
		drug.setRepeat(getNumberOfRefills(rep));
		drug.setNoSubs(!allowSubstitutions(rep));

		drug.setRoute(getRouteId(rep));
		drug.setFreqCode(getFrequencyCode(rep, 0));

		drug.setDispenseInterval(getDispenseInterval(rep));
		drug.setDrugForm(getDispenseUnitsId(rep));

		drug.setPosition(0); // this is display order. set to all zero so that medications are ordered by date.
		drug.setSpecialInstruction(getPharmacyInstructions(rep));

		drug.setSpecial(generateSpecial(drug, rep));

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

	public String generateSpecial(Drug drug, int rep) throws HL7Exception
	{
		List<String> valueList = new ArrayList<>();
		valueList.add(StringUtils.trimToEmpty(drug.getCustomName()));

		for (int tq1Rep =0; tq1Rep < getNumTimingQuantity(rep); tq1Rep ++)
		{
			if (getDosageString(rep, tq1Rep) != null)
			{
				valueList.add(getDosageString(rep, tq1Rep) + " ");
			}
			if (getRouteId(rep) != null)
			{
				valueList.add(getRouteId(rep));
			}
			if (getFrequencyCode(rep, tq1Rep) != null)
			{
				valueList.add(getFrequencyCode(rep, tq1Rep));
			}
			if (getTimingQuantityTextInstruction(rep, tq1Rep) != null)
			{
				valueList.add(getTimingQuantityTextInstruction(rep, tq1Rep));
			}

			if ((tq1Rep + 1) < getNumTimingQuantity(rep))
			{
				Date startDate = getTimingQuantityStartTime(rep, tq1Rep);
				Date endDate = getTimingQuantityStartTime(rep, tq1Rep + 1);
				if (startDate != null && endDate != null)
				{
					long dateDiff = endDate.getTime() - startDate.getTime();
					valueList.add(" For: " + TimeUnit.DAYS.convert(dateDiff, TimeUnit.MILLISECONDS) + " days then, ");
				}
				else
				{
					valueList.add(" For unknown duration then, ");
					MiscUtils.getLogger().warn("Failed to produce variable dosage duration." +
									" One of the dates was null start [" + startDate + "] end [" + endDate + "] for drug: " + drug.getCustomName());
				}
			}
		}

		if (drug.getSpecialInstruction() != null)
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
		if (hasTimingQuantity(rep) && getServiceDurationUnit(rep, 0) != null)
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

	/**
	 * calculate medication end date based on frequency, quantity and dosage.
	 * @param rep - the medication rep
	 * @param rxDate - the rx start date
	 * @return - the rx end date or null if end date cannot be determined
	 */
	private Date getCalculatedEndDateAlternate(int rep, Date rxDate) throws HL7Exception
	{
		try
		{
			Double dosage = Double.parseDouble(getDosage(rep, 0, 0));
			if (isDosageRange(rep, 0))
			{
				Double secondDosage = Double.parseDouble(getDosage(rep, 0, 1));
				if (secondDosage > dosage)
				{
					dosage = secondDosage;
				}
			}

			Double frequencyScaler = frequencyCodeToScaler(getFrequencyCode(rep, 0));
			Double amount = getRequestedDispenseAmount(rep);

			if (frequencyScaler != -1)
			{
				int durationDays = Math.toIntExact(Math.round(amount / (dosage * frequencyScaler)));

				Calendar cal = Calendar.getInstance();
				cal.setTime(rxDate);
				cal.add(Calendar.DATE, durationDays);
				return cal.getTime();
			}
		}
		catch (RuntimeException re)
		{
			MiscUtils.getLogger().warn(re.toString(), re);
		}

		return null;
	}

	/**
	 * convert frequency codes outlined here, HISCA-POS-Transfer-Data.pdf. to scaler. With 1.0 being once a day.
	 * 0.5, every other day  and so on
	 * @param frequencyCode - the frequency code to convert
	 * @return - a frequency scaler.
	 */
	private Double frequencyCodeToScaler(String frequencyCode)
	{
		try
		{
			MEDICATION_FREQUENCY_CODES freq = MEDICATION_FREQUENCY_CODES.valueOf(frequencyCode.replace("-", "_"));

			switch (freq)
			{
				case QD:
				case QPM:
				case QNOON:
				case QHS:
					return 1.0;
				case Q12H:
				case Q8_12H:
				case BID:
					return 2.0;
				case Q2H:
				case Q1_2H:
					return 12.0;
				case Q1H:
					return 24.0;
				case Q3H:
				case Q2_3H:
					return 8.0;
				case Q4H:
				case Q3_4H:
					return 6.0;
				case QID:
				case Q6H:
				case Q4_6H:
					return 4.0;
				case TID:
				case Q8H:
				case Q6_8H:
					return 3.0;
				case Q2D:
					return 0.5;
				case STAT:
				case ONCE:
					return -1.0;
			}
		}
		catch (IllegalArgumentException e)
		{
			// may be dynamic code type. Try dynamic matching
			Matcher match = Pattern.compile("Q?(\\d+)(\\w)").matcher(frequencyCode);
			if (match.matches())
			{
				Double num = Double.parseDouble(match.group(1));
				String unit = match.group(2);
				switch (unit)
				{
					case "ID":
						return num;
					case "D":
						return 1.0 / num;
					case "H":
						return 24.0 / num;
					case "L":
						return 1.0 / (30.0 * num);
					case "M":
						return 1440.0 / num;
					case "S":
						return 86400.0 / num;
					case "W":
						return 1 / (7.0 * num);
				}
			}
		}

		throw new RuntimeException("Frequency code conversion error. No mapping for [" + frequencyCode + "]!");
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
		String text =  StringUtils.trimToNull(provider.getMEDS(rep).getRXO()
				.getRxo1_RequestedGiveCode().getCwe2_Text().getValue());

		if (text == null)
		{// some times text is in CWE.9
			text = StringUtils.trimToNull(provider.getMEDS(rep).getRXO().getRxo1_RequestedGiveCode().getCwe9_OriginalText().getValue());
		}

		return text;
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

	public String getDosageString(int rep, int tq1Rep)
	{
		if (provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1Reps() > 0)
		{
			if (provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep).getQuantityReps() <= 1)
			{
				return getDosage(rep, tq1Rep, 0);
			}
			else
			{
				return StringUtils.trimToEmpty(getDosage(rep, tq1Rep, 0) + "-" + getDosage(rep, tq1Rep, 1));
			}
		}
		return "";
	}

	public String getDosage(int rep, int tq1Rep, int quantityRep)
	{
		return StringUtils.trimToEmpty(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep).getQuantity(quantityRep).getCq1_Quantity().getValue());
	}

	public boolean isDosageRange(int rep, int tq1Rep)
	{
		return provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep).getQuantityReps() > 1;
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

	public String getFrequencyCode(int rep, int tq1Rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep)
				.getTq13_RepeatPattern(0).getRpt1_RepeatPatternCode().getCwe1_Identifier().getValue());
	}

	public Date getTimingQuantityStartTime(int rep, int tq1Rep)
	{
		return getNullableDateTime(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep).getStartDateTime().getTs1_Time().getValue());
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

	public String getTimingQuantityTextInstruction(int rep, int tq1Rep)
	{
		return StringUtils.trimToNull(provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1(tq1Rep).getTextInstruction().getValue());
	}

	/**
	 * translate duration unit in to standard form.
	 * @param durationUnit - unit to translate
	 * @return - the standard format of the duration unit
	 */
	protected String translateDurationUnits(String durationUnit)
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

	protected int getNumTimingQuantity(int rep)
	{
		return provider.getMEDS(rep).getTIMING_QUANTITY().getTQ1Reps();
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
