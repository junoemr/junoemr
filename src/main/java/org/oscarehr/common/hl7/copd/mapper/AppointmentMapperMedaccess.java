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
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.demographicImport.service.CoPDImportService;

import java.util.Date;

public class AppointmentMapperMedaccess extends AppointmentMapper
{
	public AppointmentMapperMedaccess(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, importSource);
	}


	@Override
	public String getStatus(int rep, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		String apptStatus = getAppointmentStatusFromApptReason(rep);

		if (apptStatus.isEmpty())
		{
			Date apptDate = getAppointmentDate(rep);
			if (apptDate.compareTo(new Date()) < 0)
			{
				// appointment date is before current date
				return AppointmentStatus.APPOINTMENT_STATUS_BILLED;
			}
			return AppointmentStatus.APPOINTMENT_STATUS_NEW;
		}
		else
		{
			return apptStatus;
		}
	}

	/**
	 * get the appointment status by looking at the appointment reason text field
	 * @param rep - the rep of the appointment to look at
	 * @return - the appointment status or "" if no mapping could be found.
	 * @throws HL7Exception
	 */
	protected String getAppointmentStatusFromApptReason(int rep) throws HL7Exception
	{
		String apptReason = message.getPATIENT().getSCH(rep).getSch29_zAppointmentReason().getValue();
		String apptStatus = StringUtils.trimToEmpty(apptReason.split(":")[0]);

		switch(apptStatus)
		{
			case "Done":
			{
				return AppointmentStatus.APPOINTMENT_STATUS_BILLED;
			}
			case "Cancelled":
			{
				return AppointmentStatus.APPOINTMENT_STATUS_CANCELLED;
			}
			case "No Show":
			{
				return AppointmentStatus.APPOINTMENT_STATUS_NO_SHOW;
			}
			default :
			{
				logger.warn("Unmapped appointment status: " + apptStatus);
				return "";
			}
		}
	}

}
