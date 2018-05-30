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

import org.oscarehr.schedule.dao.ScheduleTemplateCodeDao;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.dto.AvailabilityType;
import org.oscarehr.schedule.dto.CalendarEvent;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class ScheduleTemplateService
{
	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;
	@Autowired
	ScheduleTemplateCodeDao scheduleTemplateCodeDao;

	private final String NO_APPOINTMENT_CHARACTER = "_";
	private final String SCHEDULE_TEMPLATE_CLASSNAME= null;


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

		List<CalendarEvent> calendarEvents = new ArrayList<>();

		Iterator<Object[]> iterator = results.iterator();

		int resourceId = 1; // Increments to identify rows
		Object[] previousRow = null; // Save the previous row to at to result
		LocalDateTime startDateTime = null;

		while(iterator.hasNext())
		{
			Object[] result = iterator.next();

			String currentCode = (String)result[1];

			// If the code changed or if this is the last row
			//   save the previous row with the saved start date
			//   reset the saved row and start date
			if(previousRow != null && previousRow[1] != null && !previousRow[1].equals(currentCode))
			{
				calendarEvents.add(createCalendarEvent(startDateTime, previousRow, resourceId++));

				previousRow = null;
				startDateTime = null;
			}

			// If this is the last row, also add a result for that
			if(!iterator.hasNext() && !NO_APPOINTMENT_CHARACTER.equals(currentCode))
			{
				// Use this date if there wasn't one set already
				if(startDateTime == null)
				{
					startDateTime = ConversionUtils.getLocalDateTimeFromSqlDateAndTime(
						(java.sql.Date) result[2],
						(java.sql.Time) result[3]
					);
				}

				// Add this row because it is the last
				calendarEvents.add(createCalendarEvent(startDateTime, result, resourceId++));
			}

			// If this is not a _, save the current row and maybe start date

			if(!NO_APPOINTMENT_CHARACTER.equals(currentCode))
			{
				previousRow = result;
				if(startDateTime == null)
				{
					startDateTime = ConversionUtils.getLocalDateTimeFromSqlDateAndTime(
						(java.sql.Date) result[2],
						(java.sql.Time)result[3]
					);
				}
			}
		}

		return calendarEvents;
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
			startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
			color,
			CalendarEvent.RENDERING_BACKGROUND,
			SCHEDULE_TEMPLATE_CLASSNAME,
			resourceId,
			code,
			availabilityType,
			null);
	}
}
