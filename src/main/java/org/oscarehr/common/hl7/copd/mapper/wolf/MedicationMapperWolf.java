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
import org.oscarehr.common.hl7.copd.mapper.MedicationMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;

import java.util.Date;

public class MedicationMapperWolf extends MedicationMapper
{
	public MedicationMapperWolf(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.WOLF);
	}

	/** ORC-9 else ORC-15, else ZRX-3 */
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
			createdDate = getAdministrationStopDate(rep);
		}
		return createdDate;
	}

	/** start date: ORC-15, else ZRX-3 */
	@Override
	protected Date getStartDate(int rep) throws HL7Exception
	{
		Date startDate = getOrderEffectiveDate(rep);
		if(startDate == null)
		{
			startDate = getAdministrationStopDate(rep);
		}
		return startDate;
	}

	/** ZRX-3, else calculate? else start date */
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
			endDate = getOrderEffectiveDate(rep);
		}
		if(endDate == null)
		{
			// can't be null
			endDate = getStartDate(rep);
		}
		return endDate;
	}

	/** Normally this is based on the flag, but Wolf always sends everything as long term and that's never the desired state */
	@Override
	public boolean isLongTerm(int rep) throws HL7Exception
	{
		return false;
	}

}
