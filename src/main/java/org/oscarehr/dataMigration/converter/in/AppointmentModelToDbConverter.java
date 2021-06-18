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
package org.oscarehr.dataMigration.converter.in;

import org.oscarehr.common.model.Appointment;
import org.oscarehr.dataMigration.model.appointment.Site;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AppointmentModelToDbConverter extends BaseModelToDbConverter<org.oscarehr.dataMigration.model.appointment.Appointment, Appointment>
{
	@Override
	public Appointment convert(org.oscarehr.dataMigration.model.appointment.Appointment input)
	{
		Appointment appointment = new Appointment();

		appointment.setProviderNo(findOrCreateProviderRecord(input.getProvider(), false).getId());
		appointment.setAppointmentDate(ConversionUtils.toLegacyDate(input.getAppointmentStartDateTime().toLocalDate()));
		appointment.setStartTime(ConversionUtils.toLegacyDateTime(input.getAppointmentStartDateTime()));
		appointment.setEndTime(ConversionUtils.toLegacyDateTime(input.getAppointmentEndDateTime()));
		appointment.setStatus(input.getStatus().getStatusCode());
		appointment.setReason(input.getReason());
		appointment.setNotes(input.getNotes());
		appointment.setCreator(IMPORT_PROVIDER);

		if(properties.isMultisiteEnabled())
		{
			Site assignedSite = input.getSite();
			if(assignedSite == null)
			{
				throw new RuntimeException("Missing site assignment");
			}
			appointment.setLocation(assignedSite.getName());
		}
		else
		{
			appointment.setLocation(input.getLocation());
		}

		return appointment;
	}
}
