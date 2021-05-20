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
package org.oscarehr.common.hl7.copd.mapper.medaccess;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.mapper.AppointmentMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AppointmentMapperMedaccess extends AppointmentMapper
{
	public AppointmentMapperMedaccess(ZPD_ZTR message, CoPDRecordData recordData)
	{
		super(message, CoPDImportService.IMPORT_SOURCE.MEDACCESS, recordData);
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
	 * MedAccess SOMETIMES has a strange reason format. <???>:<appointment type>:<reason>
	 * They've given us that as well as <appointment type>:<reason> so we have to check both.
	 * @param rep - the appointment you want
	 * @return - the appointment reason
	 * @throws HL7Exception - if there is an error parsing the hl7
	 */
	@Override
	public String getReason(int rep) throws HL7Exception
	{
		String appReason = StringUtils.trimToNull(message.getPATIENT().getSCH(rep).getSch29_zAppointmentReason().getValue());
		if (appReason != null)
		{
			List<String> reasonList = Arrays.asList(appReason.split(":"));
			if (reasonList.size() > 0)
			{
				return reasonList.get(reasonList.size() - 1);
			}
		}
		return "";
	}

	/**
	 * MedAccess SOMETIMES has a strange type format. <???>:<appointment type>:<reason>
	 * They've given us that as well as <appointment type>:<reason> so we have to check both.
	 * get the appointment type from the beginning of the reason string
	 * @param rep - the rep that you want the type for
	 * @return - the type string
	 * @throws HL7Exception - if an hl7 parsing error occurs.
	 */
	@Override
	public String getType(int rep) throws HL7Exception
	{
		String apptType = StringUtils.trimToNull(message.getPATIENT().getSCH(rep).getSch29_zAppointmentReason().getValue());
		if (apptType != null)
		{
			List<String> typeList = Arrays.asList(apptType.split(":"));
			if (typeList.size() >= 2)
			{
				return typeList.get(typeList.size() - 2);
			}
		}
		return "";
	}

	/**
	 * get the appointment status by looking at the appointment reason text field
	 * @param rep - the rep of the appointment to look at
	 * @return - the appointment status or "" if no mapping could be found.
	 * @throws HL7Exception - if there is an error parsing the hl7
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
			case "Booked":
			{
				return AppointmentStatus.APPOINTMENT_STATUS_NEW;
			}
			default :
			{
				logger.warn("Unmapped appointment status: " + apptStatus);
				return "";
			}
		}
	}

}
