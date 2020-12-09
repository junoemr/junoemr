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
package org.oscarehr.demographicImport.converter.out;

import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographicImport.service.AppointmentStatusCache;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AppointmentDbToModelConverter extends
		BaseDbToModelConverter<Appointment, org.oscarehr.demographicImport.model.appointment.Appointment>
{
	@Override
	public org.oscarehr.demographicImport.model.appointment.Appointment convert(Appointment input)
	{
		if(input == null)
		{
			return null;
		}
		org.oscarehr.demographicImport.model.appointment.Appointment appointment = new org.oscarehr.demographicImport.model.appointment.Appointment();
		BeanUtils.copyProperties(
				input,
				appointment,
				"appointmentDate",
				"startTime",
				"endTime",
				"providerNo",
				"creator",
				"demographicNo",
				"status"
		);

		appointment.setAppointmentStartDateTime(ConversionUtils.toLocalDateTime(input.getStartTimeAsFullDate()));
		appointment.setAppointmentEndDateTime(ConversionUtils.toLocalDateTime(input.getEndTimeAsFullDate()));
		appointment.setProvider(findProvider(input.getProviderNo()));
		appointment.setStatus(AppointmentStatusCache.findByCode(input.getAppointmentStatus()));

		return appointment;
	}
}
