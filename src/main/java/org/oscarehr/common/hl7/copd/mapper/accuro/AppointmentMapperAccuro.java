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
package org.oscarehr.common.hl7.copd.mapper.accuro;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.mapper.AppointmentMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;

import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;

public class AppointmentMapperAccuro extends AppointmentMapper
{
	public AppointmentMapperAccuro(ZPD_ZTR message, CoPDRecordData recordData)
	{
		super(message, CoPDImportService.IMPORT_SOURCE.ACCURO, recordData);
	}

	/**
	 * Accuro provides no appointment reason. However support has requested that notes be placed in the the reason field
	 * @param rep - the "reason" rep to get
	 * @return - the note "reason"
	 * @throws HL7Exception
	 */
	@Override
	public String getReason(int rep) throws HL7Exception
	{
		// place notes in the reason field
		return StringUtils.trimToNull(message.getPATIENT().getSCH(rep).getSch30_zNotes().getValue());
	}

	/**
	 * Accuro provides notes. However support has requested that notes be placed in the the reason field.
	 * Thus we have no notes
	 * @param rep - ignored
	 * @return - always empty string
	 * @throws HL7Exception - never thrown
	 */
	public String getNotes(int rep) throws HL7Exception
	{
		return "";
	}

}
