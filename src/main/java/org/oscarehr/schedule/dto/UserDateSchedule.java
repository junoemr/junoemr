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
package org.oscarehr.schedule.dto;

import com.google.common.collect.RangeMap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.SortedMap;

public class UserDateSchedule
{
	private Integer providerNo;
	private LocalDate scheduleDate;

	private String firstName;
	private String lastName;

	private RangeMap<LocalTime, ScheduleSlot> scheduleSlots;
	private SortedMap<LocalTime, List<AppointmentDetails>> appointments;

	private boolean isAvailable;

	public UserDateSchedule(
		Integer providerNo, LocalDate scheduleDate, String firstName, String lastName,
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots,
		SortedMap<LocalTime, List<AppointmentDetails>> appointments, boolean isAvailable)
	{
		this.providerNo = providerNo;
		this.scheduleDate = scheduleDate;
		this.firstName = firstName;
		this.lastName = lastName;
		this.scheduleSlots = scheduleSlots;
		this.appointments = appointments;
		this.isAvailable = isAvailable;
	}

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public LocalDate getScheduleDate()
	{
		return scheduleDate;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getFullName()
	{
		return firstName + ' ' + lastName;
	}

	public RangeMap<LocalTime, ScheduleSlot> getScheduleSlots()
	{
		return scheduleSlots;
	}

	public SortedMap<LocalTime, List<AppointmentDetails>> getAppointments()
	{
		return appointments;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public boolean hasSchedule()
	{
		return scheduleSlots.asMapOfRanges().size() > 0;
	}

	public boolean isAvailable()
	{
		return isAvailable;
	}
}
