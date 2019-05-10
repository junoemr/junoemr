/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package org.oscarehr.schedule.service;

import com.google.common.collect.RangeMap;
import org.oscarehr.schedule.dao.ScheduleTemplateCodeDao;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.dto.AvailabilityType;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.ConversionUtils;

import java.math.BigInteger;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScheduleTemplateService
{
	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;
	@Autowired
	ScheduleTemplateCodeDao scheduleTemplateCodeDao;

	private static final String NO_APPOINTMENT_CHARACTER = "_";
	private static final String SCHEDULE_TEMPLATE_CLASSNAME= null;


	/**
	 * Find the public and private templates available to the provider.
	 * @param providerNo provider id
	 * @return List of public and private templates.
	 */
	public List<ScheduleTemplate> getPublicAndPrivateTemplates(String providerNo)
	{
		List<ScheduleTemplate> templateList = scheduleTemplateDao.findByProviderNo("Public");
		List<ScheduleTemplate> providerTemplates = scheduleTemplateDao.findByProviderNo(providerNo);

		templateList.addAll(providerTemplates);
		return templateList;
	}

	public List<ScheduleTemplateCode> getScheduleTemplateCodes()
	{
		return scheduleTemplateCodeDao.findAll();
	}

	/**
	 * Gets a list of schedule template slots and creates a list with adjacent slots of the same
	 * type grouped together.  It basically converts from the Juno database format into the format
	 * required for cp-calendar.  This is essentially doing a group by, but would end up being a
	 * very hideous sql query if done that way.
	 * @param date The day to get the schedule for
	 * @param providerId The provider to get the schedule for
	 * @return A list of CalendarEvent objects
	 */
	public List<CalendarEvent> getCalendarEvents(Integer providerId, LocalDate date)
	{
		List<Object[]> results = scheduleTemplateDao.getRawScheduleSlots(providerId, date);

		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(
				date, providerId);

		List<CalendarEvent> calendarEvents = new ArrayList<>();

		int slotLengthInMinutes = 15;
		LocalTime startTime = LocalTime.of(8,0);
		LocalTime endTime = LocalTime.of(19,45);

		for(LocalTime slotTime = startTime; slotTime.isBefore(endTime); slotTime = plusNoWrap(slotTime, slotLengthInMinutes))
		{
			LocalTime slotEndTime = plusNoWrap(slotTime, slotLengthInMinutes);
			LocalDateTime startDateTime = LocalDateTime.of(date, slotTime);

			ScheduleSlot slot = scheduleSlots.get(slotTime);

			/* add a fake event if there is no schedule slot at this time,
			it is the no-appt slot marker, or the slot ends before the time period */
			if(slot == null
					|| NO_APPOINTMENT_CHARACTER.equals(slot.getCode())
					|| slot.getAppointmentDateTime().toLocalTime().plusMinutes(slot.getDurationMinutes()).isBefore(slotEndTime))
			{
				calendarEvents.add(createFakeCalendarEvent(startDateTime, slotLengthInMinutes, providerId));
			}

		}

		for(Object[] result : results)
		{
			String currentCode = (String)result[1];

			if(!NO_APPOINTMENT_CHARACTER.equals(currentCode))
			{
				LocalDateTime startDateTime = ConversionUtils.getLocalDateTimeFromSqlDateAndTime(
						(java.sql.Date) result[2],
						(java.sql.Time) result[3]
				);
				// Add this row because it is the last
				calendarEvents.add(createCalendarEvent(startDateTime, result, providerId));
			}
		}
		return calendarEvents;
	}

	private LocalTime plusNoWrap(LocalTime time, int slotLengthInMinutes)
	{
		LocalTime outTime = time.plusMinutes(slotLengthInMinutes);

		if(outTime.compareTo(time) == -1 || slotLengthInMinutes >= (24*60))
		{
			return LocalTime.MAX;
		}

		return outTime;
	}

	private CalendarEvent createFakeCalendarEvent(LocalDateTime startDateTime, int durationMin, int resourceId)
	{
		Object[] fakeResult = {
				null, //position
				null, //code char
				java.sql.Date.valueOf(startDateTime.toLocalDate()), //appt date
				java.sql.Time.valueOf(startDateTime.toLocalTime()), //appt time
				null, //code
				BigInteger.valueOf(durationMin),  //duration
				"No Schedule", //description
				null, //colour
				"N", //confirm
				10 //booking limit
		};
		return createCalendarEvent(startDateTime, fakeResult, resourceId);
	}

	private CalendarEvent createCalendarEvent(LocalDateTime startDateTime, Object[] result, int resourceId)
	{
		java.sql.Date appointmentDate = (java.sql.Date) result[2];
		Time appointmentTime = (java.sql.Time) result[3];
		String code = (String) result[1];
		Integer durationMinutes = ((BigInteger) result[5]).intValue();
		String description = (String) result[6];
		String color = (String) result[7];

		LocalDateTime appointmentDateTime = ConversionUtils.getLocalDateTimeFromSqlDateAndTime(
			appointmentDate,
			appointmentTime
		);

		// package up the event and add to the list
		LocalDateTime endDateTime =
			appointmentDateTime.plus(Duration.ofMinutes(durationMinutes));

		AvailabilityType availabilityType = new AvailabilityType(
			color,
			description,
			durationMinutes,
			null
		);

		return new CalendarEvent(
			startDateTime, //.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			endDateTime, //.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			color,
			CalendarEvent.RENDERING_BACKGROUND,
			SCHEDULE_TEMPLATE_CLASSNAME,
			resourceId,
			code,
			availabilityType,
			null);
	}
}
