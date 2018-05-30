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

import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

@Service
@Transactional
public class Appointment
{
	@Autowired
	OscarAppointmentDao oscarAppointmentDao;

	public List<CalendarEvent> getCalendarEvents(Integer providerId, LocalDate startDate, LocalDate endDate)
	{
		List<CalendarEvent> calendarEvents = new ArrayList<>();


		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			oscarAppointmentDao.findAppointmentDetailsByDateAndProvider(
				startDate, endDate, providerId, null);

		for(List<AppointmentDetails> dateList: appointments.values())
		{
			for(AppointmentDetails details: dateList)
			{
				LocalDateTime startDateTime = LocalDateTime.of(details.getDate(), details.getStartTime());
				LocalDateTime endDateTime = LocalDateTime.of(details.getDate(), details.getEndTime());

				String birthdayString = null;
				if(details.getBirthday() != null)
				{
					birthdayString = details.getBirthday().format(DateTimeFormatter.ISO_LOCAL_DATE);
				}

				CalendarAppointment appointment = new CalendarAppointment(
					details.getAppointmentNo(),
					birthdayString,
					details.getLastName() + ", " + details.getFirstName(),
					null, // TODO get phone number
					details.getDemographicNo(),
					null, // TODO get patient's doctor
					startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
					endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
					details.getStatus(),
					null,
					details.getReason(),
					null,
					false,
					false,
					null
				);

				calendarEvents.add(new CalendarEvent(
					startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
					endDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
					details.getColor(),
					null,
					"text-dark",       // TODO remove?
					details.getAppointmentNo(), // TODO remove?
					null,
					null,
					appointment
				));
			}
		}

		return calendarEvents;
	}
}
