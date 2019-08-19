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
package org.oscarehr.appointment.service;

import org.oscarehr.appointment.dto.CalendarAppointmentStatus;
import org.oscarehr.appointment.model.AppointmentStatusList;
import org.oscarehr.managers.AppointmentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentStatusService
{
	@Autowired
	private AppointmentManager appointmentManager;

	/**
	 * Gets a list of appointment statuses for use in the calendar.
	 * @return List of appointment statuses.
	 */
	@Transactional
	public List<CalendarAppointmentStatus> getCalendarAppointmentStatusList()
	{
		AppointmentStatusList appointmentStatusList = AppointmentStatusList.factory(appointmentManager);

		return appointmentStatusList.getCalendarAppointmentStatusList();
	}
}
