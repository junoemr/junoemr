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
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ScheduleTemplateDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.Provider;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.ResourceSchedule;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.dto.UserDateSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;

@Service("Schedule")
public class Schedule
{
	@Autowired
	MyGroupDao myGroupDao;

	@Autowired
	ProviderDao providerDao;

	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;

	@Autowired
	OscarAppointmentDao appointmentDao;

	public Schedule()
	{

	}

	/**
	 * Get the schedule for the provider on the date.
	 * @param providerNo Provider to get schedule for.
	 * @param date Date to get schedule for.
	 * @return The schedule for this provider.
	 */
	public ResourceSchedule getResourceScheduleByProvider(String providerNo, LocalDate date)
	{
		Provider provider = providerDao.getProvider(providerNo);

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		// get a UserDateSchedule for each
		userDateSchedules.add(getUserDateSchedule(
			date,
			new Integer(provider.getProviderNo()),
			provider.getFirstName(),
			provider.getLastName()
		));

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	/**
	 * Get the schedule for the provided date for each member of the group.
	 * @param group The name of the group to get the schedule for.
	 * @param date The date to get the schedule for.
	 * @return The schedule for the group.
	 */
	public ResourceSchedule getResourceScheduleByGroup(String group, LocalDate date)
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

	/**
	 * Get the provider's schedule for the week (sun-sat) that includes the date.
	 * @param providerNo Provider to get the schedule for.
	 * @param date Get the schedule for the week (sun-sat) including this date.
	 * @return The schedule for the week.
	 */
	public ResourceSchedule getWeekScheduleByProvider(String providerNo, LocalDate date)
	{
		Provider provider = providerDao.getProvider(providerNo);

		// Get date of the sunday on or before
		final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.CANADA).getFirstDayOfWeek();
		LocalDate sunday = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		// Get 7 days worth of schedule, starting on the first day of the week
		for(int i = 0; i < 7; i++)
		{
			LocalDate currentDay = sunday.plusDays(i);

			// get a UserDateSchedule for each
			userDateSchedules.add(getUserDateSchedule(
				currentDay,
				new Integer(provider.getProviderNo()),
				provider.getFirstName(),
				provider.getLastName()
			));
		}

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	private UserDateSchedule getUserDateSchedule(
		LocalDate date, Integer providerNo, String firstName, String lastName)
	{
		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(date, providerNo);

		// Get appointments
		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			appointmentDao.findAppointmentDetailsByDateAndProvider(date, providerNo);

		return new UserDateSchedule(providerNo, date, firstName, lastName, scheduleSlots, appointments);
	}
}
