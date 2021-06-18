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
package org.oscarehr.dataMigration.converter.out;

import org.oscarehr.common.model.Appointment;
import org.oscarehr.dataMigration.model.appointment.Site;
import org.oscarehr.dataMigration.service.AppointmentStatusCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AppointmentDbToModelConverter extends
		BaseDbToModelConverter<Appointment, org.oscarehr.dataMigration.model.appointment.Appointment>
{
	@Autowired
	private AppointmentStatusCache appointmentStatusCache;

	@Override
	public org.oscarehr.dataMigration.model.appointment.Appointment convert(Appointment input)
	{
		if(input == null)
		{
			return null;
		}
		org.oscarehr.dataMigration.model.appointment.Appointment appointment = new org.oscarehr.dataMigration.model.appointment.Appointment();
		BeanUtils.copyProperties(
				input,
				appointment,
				"queuedAppointmentLink",
				"appointmentDate",
				"startTime",
				"endTime",
				"providerNo",
				"creator",
				"demographicNo",
				"status",
				"location"
		);

		appointment.setAppointmentStartDateTime(ConversionUtils.toLocalDateTime(input.getStartTimeAsFullDate()));
		appointment.setAppointmentEndDateTime(ConversionUtils.toLocalDateTime(input.getEndTimeAsFullDate()));
		appointment.setProvider(findProvider(input.getProviderNo()));
		appointment.setStatus(appointmentStatusCache.findByCode(input.getAppointmentStatus()));

		// TODO load this from a cache similar to appointmentStatus
		// we don't need it for exporting anything right now though
		if(properties.isMultisiteEnabled())
		{
			Site site = new Site();
			site.setName(input.getLocation());
			appointment.setSite(site);
		}
		else
		{
			appointment.setLocation(input.getLocation());
		}

		return appointment;
	}
}
