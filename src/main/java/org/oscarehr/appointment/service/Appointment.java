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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.SxmlMisc;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

@Service
@Transactional
public class Appointment
{
	@Autowired
	OscarAppointmentDao oscarAppointmentDao;

	private String formatName(String upperFirstName, String upperLastName)
	{
		List<String> outputList = new ArrayList<>();

		if(upperLastName != null)
		{
			outputList.add(WordUtils.capitalize(upperLastName.toLowerCase()));
		}

		if(upperFirstName != null)
		{
			outputList.add(WordUtils.capitalize(upperFirstName.toLowerCase()));
		}

		if(outputList.size() == 0)
		{
			return null;
		}

		return String.join(", ", outputList);
	}

	private String getStatus(String statusString)
	{
		if(statusString != null && statusString.length() > 0)
		{
			return statusString.substring(0, 1);
		}

		return null;
	}

	private String getStatusModifier(String statusString)
	{
		if(statusString != null && statusString.length() > 1)
		{
			return statusString.substring(1,2);
		}

		return null;
	}

	public List<CalendarEvent> getCalendarEvents(HttpSession session,
	                                             Integer providerId, LocalDate startDate, LocalDate endDate, String siteName)
	{
		return getCalendarEvents(session, providerId, startDate, endDate, siteName, new ArrayList<>(0));
	}
	public List<CalendarEvent> getCalendarEvents(HttpSession session,
		Integer providerId, LocalDate startDate, LocalDate endDate, String siteName, List<Integer> hiddenDays)
	{
		List<CalendarEvent> calendarEvents = new ArrayList<>();


		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			oscarAppointmentDao.findAppointmentDetailsByDateAndProvider(
				startDate, endDate, providerId, siteName);

		for(List<AppointmentDetails> dateList: appointments.values())
		{
			for(AppointmentDetails details: dateList)
			{
				LocalDateTime startDateTime =
						LocalDateTime.of(details.getDate(), details.getStartTime());

				int dayValue = startDateTime.getDayOfWeek().getValue() % 7;
				if (hiddenDays.contains(dayValue))
				{
					// don't include appointments on hidden days
					continue;
				}

				// Add an extra minute because oscar stores the endtime minus a minute
				// set the seconds to 0 to prevent overlap issues
				LocalDateTime endDateTime =
						LocalDateTime.of(details.getDate(), details.getEndTime()).plusMinutes(1).withSecond(0);


				String rawStatus = details.getStatus();

				String province = OscarProperties.getInstance().getBillingTypeUpperCase();
				String defaultView = OscarProperties.getInstance().getProperty("default_view");
				String userProviderNo = (String) session.getAttribute("user");
				String userFirstName = (String) session.getAttribute("userfirstname");
				String userLastName = (String) session.getAttribute("userlastname");

				String rdohip = null;
				if(oscar.OscarProperties.getInstance().isPropertyActive("auto_populate_billingreferral_bc"))
				{
					rdohip = SxmlMisc.getXmlContent(StringUtils.trimToEmpty(details.getFamilyDoctor()),"rdohip");
					rdohip = rdohip !=null ? rdohip : null;
				}
				boolean isSelfBooked = org.oscarehr.common.model.Appointment.BookingSource.MYOSCAR_SELF_BOOKING.name().equals(details.getBookingSource());

				CalendarAppointment appointment = new CalendarAppointment(
						details.getAppointmentNo(),
						province,
						defaultView,
						rdohip,
						userProviderNo,
						userFirstName,
						userLastName,
						details.getBirthday(),
						formatName(details.getFirstName(), details.getLastName()),
						null, // TODO get phone number
						details.getDemographicNo(),
						null, // TODO get patient's doctor
						startDateTime,
						endDateTime,
						getStatus(rawStatus),
						getStatusModifier(rawStatus),
						null,
						details.getReason(),
						details.getReasonCode(),
						details.getNotes(),
						null,
						details.getLocation(),
						details.getType(),
						details.getResources(),
						details.getUrgency(),
						details.getName().equals(org.oscarehr.common.model.Appointment.DONOTBOOK),
						isSelfBooked,
						false,
						details.isVirtual(),
						null
				);
				// for the case where appointments are saved with a name but no demographic
				if((appointment.getDemographicNo() == null || appointment.getDemographicNo() == 0) && details.getName() != null)
				{
					appointment.setAppointmentName(details.getName());
				}

				calendarEvents.add(new CalendarEvent(
					startDateTime,
					endDateTime,
					details.getJunoColor(),
					null,
					"text-dark",       // TODO remove?
					providerId, // TODO remove?
					null,
					null,
					appointment
				));
			}
		}

		return calendarEvents;
	}
}
