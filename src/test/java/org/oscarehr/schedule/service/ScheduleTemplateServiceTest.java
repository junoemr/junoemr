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
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.dto.AvailabilityType;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
		List<Object[]> mockData = new ArrayList<>();

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);


		List<CalendarEvent> expectedResult = new ArrayList<>();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsOneTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql = new Time(28800000L);

		mockData.add(new Object[]{
			1,
			"code",
			testDateSql,
			testTimeSql,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 0, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(
			DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoInARowTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (15*60*1000));

		mockData.add(new Object[]{
			1,
			"code",
			testDateSql,
			testTimeSql1,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 0, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 30, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeInARowTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (15*60*1000));
		Time testTimeSql3 = new Time(28800000L + (2*15*60*1000));

		mockData.add(new Object[]{
			1,
			"code",
			testDateSql,
			testTimeSql1,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			3,
			"code",
			testDateSql,
			testTimeSql3,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 0, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoInARowWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (1*15*60*1000));
		Time testTimeSql3 = new Time(28800000L + (2*15*60*1000));
		Time testTimeSql4 = new Time(28800000L + (3*15*60*1000));

		mockData.add(new Object[]{
			1,
			"_",
			testDateSql,
			testTimeSql1,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			3,
			"code",
			testDateSql,
			testTimeSql3,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			4,
			"_",
			testDateSql,
			testTimeSql4,
			null,
			new BigInteger("15"),
			"description1",
			"color1",
			"confirm1",
			"bookingLimit1"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (15*60*1000));

		mockData.add(new Object[]{
			1,
			"code",
			testDateSql,
			testTimeSql1,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			2,
			"code2",
			testDateSql,
			testTimeSql2,
			"stc_code2",
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 0, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		LocalDateTime testStart2 = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		LocalDateTime testEnd2 = LocalDateTime.of(2018, 1, 1, 0, 30, 0);
		AvailabilityType availabilityType2 = new AvailabilityType("color2", "description2", 15, null);
		expectedResult.add(new CalendarEvent(testStart2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color2", CalendarEvent.RENDERING_BACKGROUND, className, 2, "code2", availabilityType2, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsTwoWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (1*15*60*1000));
		Time testTimeSql3 = new Time(28800000L + (2*15*60*1000));
		Time testTimeSql4 = new Time(28800000L + (3*15*60*1000));
		Time testTimeSql5 = new Time(28800000L + (4*15*60*1000));

		mockData.add(new Object[]{
			1,
			"_",
			testDateSql,
			testTimeSql1,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			3,
			"_",
			testDateSql,
			testTimeSql3,
			null,
			new BigInteger("15"),
			"description1",
			"color1",
			"confirm1",
			"bookingLimit1"
		});

		mockData.add(new Object[]{
			4,
			"code",
			testDateSql,
			testTimeSql4,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			5,
			"_",
			testDateSql,
			testTimeSql5,
			null,
			new BigInteger("15"),
			"description1",
			"color1",
			"confirm1",
			"bookingLimit1"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 30, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		LocalDateTime testStart2 = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		LocalDateTime testEnd2 = LocalDateTime.of(2018, 1, 1, 1, 0, 0);
		AvailabilityType availabilityType2 = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 2, "code", availabilityType2, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (1*15*60*1000));
		Time testTimeSql3 = new Time(28800000L + (2*15*60*1000));
		Time testTimeSql4 = new Time(28800000L + (3*15*60*1000));
		Time testTimeSql5 = new Time(28800000L + (4*15*60*1000));

		mockData.add(new Object[]{
			1,
			"_",
			testDateSql,
			testTimeSql1,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			3,
			"code2",
			testDateSql,
			testTimeSql3,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			4,
			"code",
			testDateSql,
			testTimeSql4,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			5,
			"_",
			testDateSql,
			testTimeSql5,
			null,
			new BigInteger("15"),
			"description1",
			"color1",
			"confirm1",
			"bookingLimit1"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 30, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		LocalDateTime testStart2 = LocalDateTime.of(2018, 1, 1, 0, 30, 0);
		LocalDateTime testEnd2 = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		AvailabilityType availabilityType2 = new AvailabilityType("color2", "description2", 15, null);
		expectedResult.add(new CalendarEvent(testStart2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color2", CalendarEvent.RENDERING_BACKGROUND, className, 2, "code2", availabilityType2, null));

		LocalDateTime testStart3 = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		LocalDateTime testEnd3 = LocalDateTime.of(2018, 1, 1, 1, 0, 0);
		AvailabilityType availabilityType3 = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart3.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd3.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 3, "code", availabilityType3, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarEventsThreeDoublesWithBlanksTest()
	{
		LocalDate date = LocalDate.of(2018, 1, 1);
		Integer providerId = 1;
		List<Object[]> mockData = new ArrayList<>();

		Date testDateSql = new Date(1514793600000L);
		Time testTimeSql1 = new Time(28800000L);
		Time testTimeSql2 = new Time(28800000L + (1*15*60*1000));
		Time testTimeSql3 = new Time(28800000L + (2*15*60*1000));
		Time testTimeSql4 = new Time(28800000L + (3*15*60*1000));
		Time testTimeSql5 = new Time(28800000L + (4*15*60*1000));
		Time testTimeSql6 = new Time(28800000L + (5*15*60*1000));
		Time testTimeSql7 = new Time(28800000L + (6*15*60*1000));
		Time testTimeSql8 = new Time(28800000L + (7*15*60*1000));

		mockData.add(new Object[]{
			1,
			"_",
			testDateSql,
			testTimeSql1,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			2,
			"code",
			testDateSql,
			testTimeSql2,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			3,
			"code",
			testDateSql,
			testTimeSql3,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			4,
			"code2",
			testDateSql,
			testTimeSql4,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			5,
			"code2",
			testDateSql,
			testTimeSql5,
			null,
			new BigInteger("15"),
			"description2",
			"color2",
			"confirm2",
			"bookingLimit2"
		});

		mockData.add(new Object[]{
			6,
			"code",
			testDateSql,
			testTimeSql6,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			7,
			"code",
			testDateSql,
			testTimeSql7,
			"stc_code",
			new BigInteger("15"),
			"description",
			"color",
			"confirm",
			"bookingLimit"
		});

		mockData.add(new Object[]{
			8,
			"_",
			testDateSql,
			testTimeSql8,
			null,
			new BigInteger("15"),
			"description1",
			"color1",
			"confirm1",
			"bookingLimit1"
		});

		Mockito.when(scheduleTemplateDao.getRawScheduleSlots(providerId, date)).thenReturn(mockData);
		List<CalendarEvent> result = scheduleTemplateService.getCalendarEvents(providerId, date);

		LocalDateTime testStart = LocalDateTime.of(2018, 1, 1, 0, 15, 0);
		LocalDateTime testEnd = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		String className = null;


		List<CalendarEvent> expectedResult = new ArrayList<>();

		AvailabilityType availabilityType = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 1, "code", availabilityType, null));

		LocalDateTime testStart2 = LocalDateTime.of(2018, 1, 1, 0, 45, 0);
		LocalDateTime testEnd2 = LocalDateTime.of(2018, 1, 1, 1, 15, 0);
		AvailabilityType availabilityType2 = new AvailabilityType("color2", "description2", 15, null);
		expectedResult.add(new CalendarEvent(testStart2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color2", CalendarEvent.RENDERING_BACKGROUND, className, 2, "code2", availabilityType2, null));

		LocalDateTime testStart3 = LocalDateTime.of(2018, 1, 1, 1, 15, 0);
		LocalDateTime testEnd3 = LocalDateTime.of(2018, 1, 1, 1, 45, 0);
		AvailabilityType availabilityType3 = new AvailabilityType("color", "description", 15, null);
		expectedResult.add(new CalendarEvent(testStart3.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), testEnd3.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), "color", CalendarEvent.RENDERING_BACKGROUND, className, 3, "code", availabilityType3, null));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
