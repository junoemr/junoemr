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

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.dto.AvailabilityType;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ScheduleTemplateServiceTest
{
	@Autowired
	@InjectMocks
	private ScheduleTemplateService scheduleTemplateService;

	@Mock
	private ScheduleTemplateDao scheduleTemplateDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getCalendarEventsEmptyTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer defaultSlotLengthMin = 15;
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(null);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, defaultSlotLengthMin);

		List<CalendarEvent> expectedResult = new ArrayList<>();
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), defaultSlotLengthMin, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 15), defaultSlotLengthMin, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 30), defaultSlotLengthMin, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 45), defaultSlotLengthMin, providerId));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer defaultSlotLengthMin = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"code",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(defaultSlotLengthMin);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("juno-color", "description", 15, "code");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 0),
				LocalDateTime.of(2018, 1, 1, 8, 15),
				"juno-color", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code", availabilityType, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 15), defaultSlotLengthMin, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 30), defaultSlotLengthMin, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 45), defaultSlotLengthMin, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, defaultSlotLengthMin);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoInARowTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"code",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType1 = new AvailabilityType("juno-color", "description", 15, "code");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 0),
				LocalDateTime.of(2018, 1, 1, 8, 15),
				"juno-color", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code", availabilityType1, null));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 30), scheduleSlotLength, providerId));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 45), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeInARowTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"code",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		LocalTime time3 = LocalTime.of(8,30,0);
		mockData.put(Range.closedOpen(time3, time3.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time3),
						"code3",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType1 = new AvailabilityType("juno-color", "description", 15, "code");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 0),
				LocalDateTime.of(2018, 1, 1, 8, 15),
				"juno-color", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code", availabilityType1, null));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		AvailabilityType availabilityType3 = new AvailabilityType("juno-color3", "description3", 15, "code3");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 30),
				LocalDateTime.of(2018, 1, 1, 8, 45),
				"juno-color3", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code3", availabilityType3, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 45), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoInARowWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"_",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		LocalTime time3 = LocalTime.of(8,30,0);
		mockData.put(Range.closedOpen(time3, time3.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time3),
						"code3",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));

		LocalTime time4 = LocalTime.of(8,45,0);
		mockData.put(Range.closedOpen(time4, time4.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time4),
						"_",
						15,
						"description4",
						"color4",
						"juno-color4",
						"confirm4",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), scheduleSlotLength, providerId));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		AvailabilityType availabilityType3 = new AvailabilityType("juno-color3", "description3", 15, "code3");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 30),
				LocalDateTime.of(2018, 1, 1, 8, 45),
				"juno-color3", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code3", availabilityType3, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 45), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 0);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"code",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,45,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), scheduleSlotLength, providerId));
		AvailabilityType availabilityType1 = new AvailabilityType("juno-color", "description", 15, "code");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code", availabilityType1, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 30), scheduleSlotLength, providerId));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 45),
				LocalDateTime.of(2018, 1, 1, 9, 0),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 15);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"_",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		LocalTime time3 = LocalTime.of(8,30,0);
		mockData.put(Range.closedOpen(time3, time3.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time3),
						"_",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));

		LocalTime time4 = LocalTime.of(8,45,0);
		mockData.put(Range.closedOpen(time4, time4.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time4),
						"code4",
						15,
						"description4",
						"color4",
						"juno-color4",
						"confirm4",
						1
				));
		LocalTime time5 = LocalTime.of(9,0,0);
		mockData.put(Range.closedOpen(time5, time5.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time5),
						"_",
						15,
						"description5",
						"color5",
						"juno-color5",
						"confirm5",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), scheduleSlotLength, providerId));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 30), scheduleSlotLength, providerId));
		AvailabilityType availabilityType4 = new AvailabilityType("juno-color4", "description4", 15, "code4");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 45),
				LocalDateTime.of(2018, 1, 1, 9, 0),
				"juno-color4", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code4", availabilityType4, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 9, 0), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(9, 15);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"_",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));

		LocalTime time3 = LocalTime.of(8,30,0);
		mockData.put(Range.closedOpen(time3, time3.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time3),
						"code3",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));

		LocalTime time4 = LocalTime.of(8,45,0);
		mockData.put(Range.closedOpen(time4, time4.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time4),
						"code4",
						15,
						"description4",
						"color4",
						"juno-color4",
						"confirm4",
						1
				));
		LocalTime time5 = LocalTime.of(9,0,0);
		mockData.put(Range.closedOpen(time5, time5.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time5),
						"_",
						15,
						"description5",
						"color5",
						"juno-color5",
						"confirm5",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), scheduleSlotLength, providerId));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		AvailabilityType availabilityType3 = new AvailabilityType("juno-color3", "description3", 15, "code3");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 30),
				LocalDateTime.of(2018, 1, 1, 8, 45),
				"juno-color3", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code3", availabilityType3, null));
		AvailabilityType availabilityType4 = new AvailabilityType("juno-color4", "description4", 15, "code4");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 45),
				LocalDateTime.of(2018, 1, 1, 9, 0),
				"juno-color4", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code4", availabilityType4, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 9, 0), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeDoublesWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		LocalTime scheduleStartTime = LocalTime.of(8, 0);
		LocalTime scheduleEndTime = LocalTime.of(10, 0);
		Integer scheduleSlotLength = 15;//min
		RangeMap<LocalTime, ScheduleSlot> mockData = TreeRangeMap.create();
		Integer siteId = null;

		LocalTime time1 = LocalTime.of(8,0,0);
		mockData.put(Range.closedOpen(time1, time1.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time1),
						"_",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		LocalTime time2 = LocalTime.of(8,15,0);
		mockData.put(Range.closedOpen(time2, time2.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time2),
						"code1",
						15,
						"description1",
						"color1",
						"juno-color1",
						"confirm1",
						1
				));

		LocalTime time3 = LocalTime.of(8,30,0);
		mockData.put(Range.closedOpen(time3, time3.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time3),
						"code1",
						15,
						"description1",
						"color1",
						"juno-color1",
						"confirm1",
						1
				));

		LocalTime time4 = LocalTime.of(8,45,0);
		mockData.put(Range.closedOpen(time4, time4.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time4),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));
		LocalTime time5 = LocalTime.of(9,0,0);
		mockData.put(Range.closedOpen(time5, time5.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time5),
						"code2",
						15,
						"description2",
						"color2",
						"juno-color2",
						"confirm2",
						1
				));
		LocalTime time6 = LocalTime.of(9,15,0);
		mockData.put(Range.closedOpen(time6, time6.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time6),
						"code3",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));
		LocalTime time7 = LocalTime.of(9,30,0);
		mockData.put(Range.closedOpen(time7, time7.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time7),
						"code3",
						15,
						"description3",
						"color3",
						"juno-color3",
						"confirm3",
						1
				));
		LocalTime time8 = LocalTime.of(9,45,0);
		mockData.put(Range.closedOpen(time8, time8.plus(Duration.ofMinutes(15))),
				new ScheduleSlot(
						LocalDateTime.of(date, time8),
						"_",
						15,
						"description",
						"color",
						"juno-color",
						"confirm",
						1
				));

		Mockito.when(scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId)).thenReturn(scheduleSlotLength);
		Mockito.when(scheduleTemplateDao.findScheduleSlots(date, providerId, siteId)).thenReturn(mockData);

		List<CalendarEvent> expectedResult = new ArrayList<>();

		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 8, 0), scheduleSlotLength, providerId));
		AvailabilityType availabilityType1 = new AvailabilityType("juno-color1", "description1", 15, "code1");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 15),
				LocalDateTime.of(2018, 1, 1, 8, 30),
				"juno-color1", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code1", availabilityType1, null));
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 30),
				LocalDateTime.of(2018, 1, 1, 8, 45),
				"juno-color1", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code1", availabilityType1, null));
		AvailabilityType availabilityType2 = new AvailabilityType("juno-color2", "description2", 15, "code2");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 8, 45),
				LocalDateTime.of(2018, 1, 1, 9, 0),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 9, 0),
				LocalDateTime.of(2018, 1, 1, 9, 15),
				"juno-color2", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code2", availabilityType2, null));
		AvailabilityType availabilityType3 = new AvailabilityType("juno-color3", "description3", 15, "code3");
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 9, 15),
				LocalDateTime.of(2018, 1, 1, 9, 30),
				"juno-color3", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code3", availabilityType3, null));
		expectedResult.add(new CalendarEvent(
				LocalDateTime.of(2018, 1, 1, 9, 30),
				LocalDateTime.of(2018, 1, 1, 9, 45),
				"juno-color3", CalendarEvent.RENDERING_BACKGROUND, null, providerId, "code3", availabilityType3, null));
		expectedResult.add(createFakeCalendarEvent(LocalDateTime.of(2018, 1, 1, 9, 45), scheduleSlotLength, providerId));

		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date, scheduleStartTime, scheduleEndTime, siteId, scheduleSlotLength);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	private CalendarEvent createFakeCalendarEvent(LocalDateTime startDateTime, int durationMin, int resourceId)
	{
		ScheduleSlot slot = new ScheduleSlot(
				startDateTime,
				null,
				durationMin,
				"No Schedule",
				null,
				null,
				"N",
				10);

		LocalDateTime endDateTime = startDateTime.plus(Duration.ofMinutes(durationMin));

		AvailabilityType availabilityType = new AvailabilityType(
				slot.getJunoColor(),
				slot.getDescription(),
				slot.getDurationMinutes(),
				slot.getCode()
		);

		return new CalendarEvent(
				startDateTime,
				endDateTime,
				slot.getJunoColor(),
				CalendarEvent.RENDERING_BACKGROUND,
				null,
				resourceId,
				slot.getCode(),
				availabilityType,
				null);
	}
}
