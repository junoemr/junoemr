/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.schedule.service;

import com.google.common.collect.RangeMap;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ScheduleTemplateDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.ResourceSchedule;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.dto.UserDateSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

@Service("Schedule")
public class Schedule
{
	@Autowired
	MyGroupDao myGroupDao;

	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;

	@Autowired
	OscarAppointmentDao appointmentDao;

	public Schedule()
	{

	}

	public ResourceSchedule getResourceSchedule(String group, LocalDate date)
	{
		List<MyGroup> results = myGroupDao.getGroupByGroupNo(group);

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		for(MyGroup result: results)
		{
			// get a UserDateSchedule for each
			userDateSchedules.add(getUserDateSchedule(
				date,
				new Integer(result.getId().getProviderNo()),
				result.getFirstName(),
				result.getLastName()
			));
		}

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	public UserDateSchedule getUserDateSchedule(
		LocalDate date, Integer providerNo, String firstName, String lastName)
	{
		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(date, providerNo);

		// Get appointments
		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			appointmentDao.findAppointmentDetailsByDateAndProvider(date, providerNo);

		/*
		for(AppointmentDetails appointment: appointments)
		{
			scheduleSlots.get(appointment.getStartTime()).addAppointmentDetails(appointment);
		}
		*/

		return new UserDateSchedule(providerNo, firstName, lastName, scheduleSlots, appointments);
	}
}
