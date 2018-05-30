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

import com.google.common.collect.RangeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class AppointmentTest
{
	@Autowired
	@InjectMocks
	private Appointment appointmentService;

	@Mock
	private OscarAppointmentDao appointmentDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCalendarEventsEmptyTest()
	{
		LocalDate startDate = LocalDate.of(2018,1,1);
		LocalDate endDate = LocalDate.of(2018,1,1);
		Integer providerId = 1;
		String site = null;

		SortedMap<LocalTime, List<AppointmentDetails>> mockData = new TreeMap<>();

		Mockito.when(appointmentDao.findAppointmentDetailsByDateAndProvider(startDate, endDate, providerId, site)).thenReturn(mockData);

		List<CalendarEvent> result = appointmentService.getCalendarEvents(providerId, startDate, endDate);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneTest()
	{
		LocalDate startDate = LocalDate.of(2018,1,1);
		LocalDate endDate = LocalDate.of(2018,1,1);
		Integer providerId = 1;
		String site = null;
		LocalTime keyTime = LocalTime.of(0,0);

		SortedMap<LocalTime, List<AppointmentDetails>> mockData = new TreeMap<>();

		List<AppointmentDetails> valueList = new ArrayList<>();

		valueList.add(new AppointmentDetails(
			1,
			1,
			startDate,
			keyTime,
			keyTime,
			"name1",
			"notes1",
			"reason1",
			1,
			"site1",
			null,
			null,
			null,
			null,
			"A",
			null,
			null,
			"color1",
			null,
			null,
			null,
			"first1",
			"last1",
			null,
			null,
			null,
			null,
			null,
			null,
			LocalDate.of(2000,1,1),
			false,
			null
		));

		mockData.put(keyTime, valueList);

		Mockito.when(appointmentDao.findAppointmentDetailsByDateAndProvider(startDate, endDate, providerId, site)).thenReturn(mockData);

		List<CalendarEvent> result = appointmentService.getCalendarEvents(providerId, startDate, endDate);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(new CalendarEvent(
			LocalDateTime.of(2018,1,1,0,0,0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			LocalDateTime.of(2018,1,1,0,0,0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			"color1",
			null,
			"text-dark",
			1,
			null,
			null,
			new CalendarAppointment(
				1,
				LocalDate.of(2000,1,1).format(DateTimeFormatter.ISO_LOCAL_DATE),
				"last1, first1",
				null, // TODO get phone number
				1,
				null, // TODO get patient's doctor
				LocalDateTime.of(2018,1,1,0,0,0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				LocalDateTime.of(2018,1,1,0,0,0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
				"A",
				null,
				"reason1",
				null,
				false,
				false,
				null
			)
		));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
