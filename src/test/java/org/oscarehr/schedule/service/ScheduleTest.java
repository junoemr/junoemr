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
package org.oscarehr.schedule.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.appointment.service.Appointment;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTest
{
	@Autowired
	@InjectMocks
	private Schedule scheduleService;

	@Mock
	private ScheduleTemplateService scheduleTemplateService;

	@Mock
	private Appointment appointmentService;

	@Mock
	private HttpSession session;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCalendarEventsEmptyTest()
	{
		Integer providerId = 1;
		LocalDate startDate = LocalDate.of(2018, 1, 1);
		LocalDate endDate = LocalDate.of(2018, 1, 1);
		String siteName = "site";
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(20, 0);
		Integer scheduleSlotLength = 15;//min

		List<CalendarEvent> mockData = new ArrayList<>();

		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate, scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(mockData);
		Mockito.when(appointmentService.getCalendarEvents(session, providerId, startDate, endDate, siteName)).thenReturn(mockData);

		List<CalendarEvent> result = scheduleService.getCalendarEvents(session, providerId, startDate, endDate, scheduleStartTime, scheduleEndTime, siteName, null, scheduleSlotLength);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneScheduleTemplateTest()
	{
		Integer providerId = 1;
		LocalDate startDate = LocalDate.of(2018, 1, 1);
		LocalDate endDate = LocalDate.of(2018, 1, 1);
		String siteName = "site";
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(20, 0);
		Integer scheduleSlotLength = 15;//min

		LocalDateTime start1 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end1 = LocalDateTime.of(2018,1,1,0,15,0);
		String color1 = "color1";
		String className1 = "className1";
		Integer resourceId1 = 1;
		String scheduleTemplateCode1 = "code1";

		List<CalendarEvent> templateMockData = new ArrayList<>();
		templateMockData.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));

		List<CalendarEvent> appointmentMockData = new ArrayList<>();


		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate, scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData);
		Mockito.when(appointmentService.getCalendarEvents(session, providerId, startDate, endDate, siteName)).thenReturn(appointmentMockData);

		List<CalendarEvent> result = scheduleService.getCalendarEvents(session, providerId, startDate, endDate, scheduleStartTime, scheduleEndTime, siteName, null, 15);

		List<CalendarEvent> expectedResult = new ArrayList<>();
		expectedResult.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneAppointmentTest()
	{
		Integer providerId = 1;
		LocalDate startDate = LocalDate.of(2018, 1, 1);
		LocalDate endDate = LocalDate.of(2018, 1, 3);
		String siteName = "site";
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(20, 0);
		Integer scheduleSlotLength = 15;//min

		LocalDateTime start1 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end1 = LocalDateTime.of(2018,1,1,0,15,0);
		String color1 = "color1";
		String className1 = "className1";
		Integer resourceId1 = 1;
		String scheduleTemplateCode1 = "code1";

		List<CalendarEvent> templateMockData = new ArrayList<>();

		List<CalendarEvent> appointmentMockData = new ArrayList<>();
		appointmentMockData.add(new CalendarEvent(start1, end1, color1, null, className1, resourceId1, scheduleTemplateCode1, null, null));

		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate, scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData);
		Mockito.when(appointmentService.getCalendarEvents(session, providerId, startDate, endDate, siteName)).thenReturn(appointmentMockData);

		List<CalendarEvent> result = scheduleService.getCalendarEvents(session, providerId, startDate, endDate, scheduleStartTime, scheduleEndTime, siteName, null, scheduleSlotLength);

		List<CalendarEvent> expectedResult = new ArrayList<>();
		expectedResult.add(new CalendarEvent(start1, end1, color1, null, className1, resourceId1, scheduleTemplateCode1, null, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneEachTest()
	{
		Integer providerId = 1;
		LocalDate startDate = LocalDate.of(2018, 1, 1);
		LocalDate endDate = LocalDate.of(2018, 1, 3);
		String siteName = "site";
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(20, 0);
		Integer scheduleSlotLength = 15;//min

		LocalDateTime start1 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end1 = LocalDateTime.of(2018,1,1,0,15,0);
		String color1 = "color1";
		String className1 = "className1";
		Integer resourceId1 = 1;
		String scheduleTemplateCode1 = "code1";

		LocalDateTime start2 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end2 = LocalDateTime.of(2018,1,1,0,30,0);
		String color2 = "color2";
		String className2 = "className2";
		Integer resourceId2 = 2;
		String scheduleTemplateCode2 = "code2";

		List<CalendarEvent> templateMockData = new ArrayList<>();
		templateMockData.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));

		List<CalendarEvent> appointmentMockData = new ArrayList<>();
		appointmentMockData.add(new CalendarEvent(start2, end2, color2, null, className2, resourceId2, scheduleTemplateCode2, null, null));

		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate, scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData);
		Mockito.when(appointmentService.getCalendarEvents(session, providerId, startDate, endDate, siteName)).thenReturn(appointmentMockData);

		List<CalendarEvent> result = scheduleService.getCalendarEvents(session, providerId, startDate, endDate, scheduleStartTime, scheduleEndTime, siteName, null, scheduleSlotLength);

		List<CalendarEvent> expectedResult = new ArrayList<>();
		expectedResult.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));
		expectedResult.add(new CalendarEvent(start2, end2, color2, null, className2, resourceId2, scheduleTemplateCode2, null, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoTemplatesTest()
	{
		Integer providerId = 1;
		LocalDate startDate = LocalDate.of(2018, 1, 1);
		LocalDate endDate = LocalDate.of(2018, 1, 3);
		String siteName = "site";
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(20, 0);
		Integer scheduleSlotLength = 15;//min

		LocalDateTime start1 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end1 = LocalDateTime.of(2018,1,1,0,15,0);
		String color1 = "color1";
		String className1 = "className1";
		Integer resourceId1 = 1;
		String scheduleTemplateCode1 = "code1";

		LocalDateTime start2 = LocalDateTime.of(2018,1,1,0,0,0);
		LocalDateTime end2 = LocalDateTime.of(2018,1,1,0,30,0);
		String color2 = "color2";
		String className2 = "className2";
		Integer resourceId2 = 2;
		String scheduleTemplateCode2 = "code2";

		List<CalendarEvent> templateMockData1 = new ArrayList<>();
		templateMockData1.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));

		List<CalendarEvent> templateMockData2 = new ArrayList<>();

		List<CalendarEvent> templateMockData3 = new ArrayList<>();
		templateMockData3.add(new CalendarEvent(start2, end2, color2, CalendarEvent.RENDERING_BACKGROUND, className2, resourceId2, scheduleTemplateCode2, null, null));

		List<CalendarEvent> appointmentMockData = new ArrayList<>();

		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate, scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData1);
		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate.plusDays(1), scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData2);
		Mockito.when(scheduleTemplateService.getCalendarEvents(providerId, startDate.plusDays(2), scheduleStartTime, scheduleEndTime, null, scheduleSlotLength)).thenReturn(templateMockData3);
		Mockito.when(appointmentService.getCalendarEvents(session, providerId, startDate, endDate, siteName)).thenReturn(appointmentMockData);

		List<CalendarEvent> result = scheduleService.getCalendarEvents(session, providerId, startDate, endDate, scheduleStartTime, scheduleEndTime, siteName, null, scheduleSlotLength);

		List<CalendarEvent> expectedResult = new ArrayList<>();
		expectedResult.add(new CalendarEvent(start1, end1, color1, CalendarEvent.RENDERING_BACKGROUND, className1, resourceId1, scheduleTemplateCode1, null, null));
		expectedResult.add(new CalendarEvent(start2, end2, color2, CalendarEvent.RENDERING_BACKGROUND, className2, resourceId2, scheduleTemplateCode2, null, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
