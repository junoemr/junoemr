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

	public List<CalendarEvent> getCalendarEventsScheduleOnly(Integer providerId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer siteId)
	{
		// get the schedule slot length, return null if no schedule is set
		Integer scheduleSlotLength = scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId);
		if(scheduleSlotLength != null)
		{
			return getAllCalendarEvents(providerId, date, startTime, endTime, siteId, scheduleSlotLength);
		}
		return null;
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
	public List<CalendarEvent> getCalendarEvents(Integer providerId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer siteId, int defaultSlotLengthInMin)
	{
		// get the schedule slot length, or use the default
		Integer scheduleSlotLength = scheduleTemplateDao.getScheduleSlotLengthInMin(providerId, date, siteId);
		if(scheduleSlotLength == null)
		{
			scheduleSlotLength = defaultSlotLengthInMin;
		}
		return getAllCalendarEvents(providerId, date, startTime, endTime, siteId, scheduleSlotLength);
	}
	private List<CalendarEvent> getAllCalendarEvents(Integer providerId, LocalDate date, LocalTime startTime, LocalTime endTime, Integer siteId, int scheduleSlotLength)
	{
		List<CalendarEvent> calendarEvents = new ArrayList<>();

		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(date, providerId, siteId);
		String prevCode = null;

		for(LocalTime slotTime = startTime; slotTime.isBefore(endTime); slotTime = plusNoWrap(slotTime, scheduleSlotLength))
		{
			LocalDateTime startDateTime = LocalDateTime.of(date, slotTime);
			ScheduleSlot slot = scheduleSlots.get(slotTime);

			CalendarEvent event;
			boolean visibleCode = false;

			/* add a fake event if there is no schedule slot at this time,
			it is the no-appt slot marker, or the slot ends before the time period */
			if(slot == null || NO_APPOINTMENT_CHARACTER.equals(slot.getCode()))
			{
				event = createFakeCalendarEvent(startDateTime, scheduleSlotLength, providerId);
			}
			else
			{
				// Add this row because it is the last
				event = createCalendarEvent(slot, scheduleSlotLength, providerId);
				visibleCode = (prevCode == null || !prevCode.equals(event.getAvailabilityType().getSystemCode()));
			}

			// if the code changes, set it to be visible. this allows the calendar to display codes once for repeated types
			event.getAvailabilityType().setSystemCodeVisible(visibleCode);
			prevCode = event.getAvailabilityType().getSystemCode();

			calendarEvents.add(event);
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
		ScheduleSlot slot = new ScheduleSlot(
				startDateTime,
				null,
				durationMin,
				"No Schedule",
				null,
				null,
				"N",
				10);

		return createCalendarEvent(slot, durationMin, resourceId);
	}

	private CalendarEvent createCalendarEvent(ScheduleSlot slot, int scheduleSlotLength, int resourceId)
	{
		// package up the event and add to the list
		LocalDateTime startDateTime = slot.getAppointmentDateTime();
		LocalDateTime endDateTime = startDateTime.plus(Duration.ofMinutes(scheduleSlotLength));

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
				SCHEDULE_TEMPLATE_CLASSNAME,
				resourceId,
				slot.getCode(),
				availabilityType,
				null);
	}
}
