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
package org.oscarehr.common.hl7.copd.mapper.wolf;

import ca.uhn.hl7v2.HL7Exception;
import org.oscarehr.common.hl7.Hl7Const;
import org.oscarehr.common.hl7.copd.mapper.MedicationMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.rx.model.Drug;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.Date;

public class MedicationMapperWolf extends MedicationMapper
{
	private final CoPDRecordData recordData;
	public MedicationMapperWolf(ZPD_ZTR message, int providerRep, CoPDRecordData recordData)
	{
		super(message, providerRep, ImporterExporterFactory.IMPORT_SOURCE.WOLF);
		this.recordData = recordData;
	}

	@Override
	public Drug getDrug(int rep) throws HL7Exception
	{
		Drug drug = super.getDrug(rep);

		if (drug.getRxDate() == null)
		{
			Date defaultDate = ConversionUtils.toLegacyDate(LocalDate.of(1900, 1, 1));
			drug.setRxDate(defaultDate);
			drug.setEndDate(defaultDate);
			recordData.addMessage(Hl7Const.HL7_GROUP_MEDS, String.valueOf(rep),
					"Medication missing Start Date. Set to " + ConversionUtils.toDateString(defaultDate) + "\n[" + drug.getCustomName() + "]");
		}

		return drug;
	}

	/** ORC-9 else ORC-15, else ZRX-2 */
	@Override
	protected Date getWrittenDate(int rep) throws HL7Exception
	{
		Date createdDate = getTransactionDate(rep);
		if(createdDate == null)
		{
			createdDate = getOrderEffectiveDate(rep);
		}
		if(createdDate == null)
		{
			createdDate = getAdministrationStartDate(rep);
		}
		return createdDate;
	}

	/** start date: ZRX-2, else ORC-15 */
	@Override
	protected Date getStartDate(int rep) throws HL7Exception
	{
		Date startDate = getAdministrationStartDate(rep);
		if(startDate == null)
		{
			startDate = getOrderEffectiveDate(rep);
		}
		return startDate;
	}

	/** ZRX-3, else calculate? else ORC-15, else start date */
	@Override
	protected Date getEndDate(int rep) throws HL7Exception
	{
		Date endDate = getAdministrationStopDate(rep);
		if(endDate == null)
		{
			endDate = getCalculatedEndDate(rep, getStartDate(rep));
		}
		if(endDate == null)
		{
			endDate = getCalculatedEndDateAlternate(rep, getStartDate(rep));
		}
		if(endDate == null)
		{
			endDate = getOrderEffectiveDate(rep);
		}
		if(endDate == null)
		{
			// can't be null
			endDate = getStartDate(rep);
		}
		return endDate;
	}

	/** Normally this is based on the flag, but Wolf always sends everything as long term and that's never the desired state
	 * instead we are going to set long term if the med does not have an end date, or info to calculate an end date
	 */
	@Override
	public boolean isLongTerm(int rep) throws HL7Exception
	{
		return (getAdministrationStopDate(rep) == null
				&& getCalculatedEndDate(rep, getStartDate(rep)) == null
				&& getCalculatedEndDateAlternate(rep, getStartDate(rep)) == null);
	}

}
