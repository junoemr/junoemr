/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest.conversion;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.to.model.AppointmentTo1;
import org.springframework.beans.BeanUtils;
import oscar.util.ConversionUtils;
import oscar.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AppointmentConverter extends AbstractConverter<Appointment, AppointmentTo1> {

	private boolean includeDemographic;
	private boolean includeProvider;

	private DemographicDao demographicDao = SpringUtils.getBean(DemographicDao.class);

	private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);

	protected Logger logger = MiscUtils.getLogger();

	public AppointmentConverter() {
		
	}
	
	public AppointmentConverter(boolean includeDemographic, boolean includeProvider) {
		this.includeDemographic = includeDemographic;
		this.includeProvider = includeProvider;
	}

	public Appointment getAsDomainObject(CalendarAppointment t) throws ConversionException
	{
		logger.info(t);

		Demographic demographic = null;
		if(t.getDemographicNo() != null && t.getDemographicNo() > 0)
		{
			demographic = demographicDao.getDemographicById(t.getDemographicNo());
		}

		// Copy the defaults from the old frontend
		int demographicNo = 0;
		String name = "";
		if (demographic != null)
		{
			demographicNo = demographic.getDemographicNo();
			name = demographic.getDisplayName();
		}
		// sometimes a name string is set without aq demographic (appts without attached demographic)
		else if (t.getAppointmentName() != null)
		{
			name = t.getAppointmentName();
		}
		String status = t.getEventStatusCode();
		String statusModifier = t.getEventStatusModifier();
		if(statusModifier != null)
		{
			status += statusModifier;
		}

		Date adjustedAppointmentDate =
				Date.from(t.getStartTime().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

		Date adjustedStartDate = Date.from(t.getStartTime().atZone(ZoneId.systemDefault()).toInstant());

		// Remove a minute off of the end date because that's how oscar does things.
		Date adjustedEndDate = Date.from(t.getEndTime().minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

		Appointment appointment = new Appointment();

		appointment.setId(t.getAppointmentNo());

		appointment.setProviderNo(String.valueOf(t.getProviderNo()));
		appointment.setAppointmentDate(adjustedAppointmentDate);
		appointment.setStartTime(adjustedStartDate);
		appointment.setEndTime(adjustedEndDate);
		appointment.setDemographicNo(demographicNo);
		appointment.setNotes(t.getNotes());
		appointment.setReason(t.getReason());
		appointment.setReasonCode(t.getReasonCode());
		appointment.setLocation(t.getSite());
		appointment.setStatus(status);
		appointment.setResources(t.getResources());
		appointment.setUrgency(t.getUrgency());
		appointment.setIsVirtual(t.isVirtual());
		appointment.setType(t.getType());

		if(t.isDoNotBook())
		{
			appointment.setName(Appointment.DONOTBOOK);
		}
		else
		{
			appointment.setName(org.apache.commons.lang3.StringUtils.left(name, 50));
		}


		//String resources = StringUtils.transformNullInEmptyString(t.getResources());
		//String type = StringUtils.transformNullInEmptyString(t.getType());
		//String urgency = StringUtils.transformNullInEmptyString(t.getUrgency());

		/*
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		appointment.setCreateDateTime(t.getCreateDateTime());
		appointment.setUpdateDateTime(t.getUpdateDateTime());
		appointment.setCreatorSecurityId(t.getCreatorSecurityId());
		appointment.setReasonCode(t.getReasonCode());
		*/

		//appointment.setType(type);
		//appointment.setUrgency(urgency);
		//appointment.setResources(resources);
		//appointment.setProgramId(t.getProgramId());
		//appointment.setStyle(t.getStyle());
		//appointment.setBilling(t.getBilling());
		//appointment.setImportedStatus(t.getImportedStatus());
		//appointment.setRemarks(t.getRemarks());
		Appointment.BookingSource bookingSource = (t.isTagSelfBooked())? Appointment.BookingSource.MYOSCAR_SELF_BOOKING : null;
		appointment.setBookingSource(bookingSource);
		appointment.setCreatorSecurityId(t.getCreatorSecurityId());

		return appointment;
	}

	@Override
    public Appointment getAsDomainObject(LoggedInInfo loggedInInfo, AppointmentTo1 t) throws ConversionException
	{
		logger.info(t);


		// XXX: incoming date is parsed as UTC, but then converted to PST (probably default time
		//      zone for this part of the app.  This is a hack that converts it to a string in the
		//      UTC time zone, so it is the same as what was sent, then it is parsed without time
		//      zone information in the default time zone.
		TimeZone timeZoneUTC = TimeZone.getTimeZone("UTC");

		String format = "yyyy-MM-dd HH:mm";
		SimpleDateFormat dateFormatterUTC = new SimpleDateFormat(format);
		dateFormatterUTC.setTimeZone(timeZoneUTC);

		SimpleDateFormat dateFormatterDefault = new SimpleDateFormat(format);

		Date adjustedAppointmentDate = null;
		Date adjustedStartDate = null;
		Date adjustedEndDate = null;
		try {
			adjustedAppointmentDate = dateFormatterDefault.parse(dateFormatterUTC.format(t.getAppointmentDate()));
			adjustedStartDate = dateFormatterDefault.parse(dateFormatterUTC.format(t.getStartTime()));
			Date intermediateEndDate = dateFormatterDefault.parse(dateFormatterUTC.format(t.getEndTime()));

			// Remove a minute from the enddate because that's how Oscar does it.
			Calendar cal = Calendar.getInstance();
			cal.setTime(intermediateEndDate);
			cal.add(Calendar.MINUTE, -1);
			adjustedEndDate = cal.getTime();

		} catch (ParseException e) {
			logger.warn("Cannot parse new appointment dates");
			throw new ConversionException("Could not parse date/times of appointment. please check format");
		}

		Demographic demographic = demographicDao.getDemographicById(t.getDemographicNo());

		// Copy the defaults from the old frontend
		int demographicNo = 0;
		String demographicName = "";
		if (demographic != null)
		{
			demographicNo = demographic.getDemographicNo();
			demographicName = demographic.getDisplayName();
		}

		String resources = StringUtils.transformNullInEmptyString(t.getResources());
		String type = StringUtils.transformNullInEmptyString(t.getType());
		String urgency = StringUtils.transformNullInEmptyString(t.getUrgency());

		Appointment appointment = new Appointment();

		appointment.setId(t.getId());

		appointment.setProviderNo(t.getProviderNo());
		appointment.setAppointmentDate(adjustedAppointmentDate);
		appointment.setStartTime(adjustedStartDate);
		appointment.setEndTime(adjustedEndDate);
		appointment.setName(demographicName);
		appointment.setDemographicNo(demographicNo);
		appointment.setProgramId(t.getProgramId());
		appointment.setNotes(t.getNotes());
		appointment.setReason(t.getReason());
		appointment.setReasonCode(t.getReasonCode());
		appointment.setLocation(t.getLocation());
		appointment.setResources(resources);
		appointment.setType(type);
		appointment.setStyle(t.getStyle());
		appointment.setBilling(t.getBilling());
		appointment.setStatus(t.getStatus());
		appointment.setImportedStatus(t.getImportedStatus());
		appointment.setCreateDateTime(t.getCreateDateTime());
		appointment.setUpdateDateTime(t.getUpdateDateTime());
		appointment.setLastUpdateUser(loggedInInfo.getLoggedInProviderNo());
		appointment.setRemarks(t.getRemarks());
		appointment.setUrgency(urgency);
		appointment.setCreatorSecurityId(t.getCreatorSecurityId());
		appointment.setBookingSource(t.getBookingSource());

		return appointment;
    }

	public CalendarAppointment getAsCalendarAppointment(Appointment appointment)
	{
		Demographic demographic = null;
		if(appointment.getDemographicNo() > 0)
		{
			demographic = demographicDao.getDemographicById(appointment.getDemographicNo());
		}

		LocalDate birthDate = null;
		String displayName = null;
		String phone = null;
		Integer demographicNo = null;

		if(demographic != null)
		{
			birthDate = demographic.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			displayName = demographic.getDisplayName();
			phone = demographic.getPhone();
			demographicNo = demographic.getDemographicNo();
		}

		// set appointment name if the demographic is not assigned
		String appointmentName = null;
		if(appointment.getName() != null && appointment.getName().isEmpty())
		{
			appointmentName = appointment.getName();
		}

		CalendarAppointment calendarAppointment = new CalendarAppointment();
		calendarAppointment.setAppointmentNo(appointment.getId());
		calendarAppointment.setDemographicDob(birthDate);
		calendarAppointment.setDemographicName(displayName);
		calendarAppointment.setDemographicPhone(phone);
		calendarAppointment.setDemographicNo(demographicNo);
		calendarAppointment.setProviderNo(Integer.parseInt(appointment.getProviderNo())); //TODO make this a string
		calendarAppointment.setStartTime(ConversionUtils.toLocalDateTime(appointment.getStartTime()));
		calendarAppointment.setEndTime(ConversionUtils.toLocalDateTime(appointment.getEndTime()).plusMinutes(1));
		calendarAppointment.setEventStatusCode(appointment.getStatus());
		calendarAppointment.setEventStatusModifier(appointment.getAppointmentStatusModifier());
		calendarAppointment.setReason(appointment.getReason());
		calendarAppointment.setReasonCode(appointment.getReasonCode());
		calendarAppointment.setNotes(appointment.getNotes());
		calendarAppointment.setType(appointment.getType());
		calendarAppointment.setResources(appointment.getResources());
		calendarAppointment.setSite(appointment.getLocation());
		calendarAppointment.setTagSelfBooked(Appointment.BookingSource.MYOSCAR_SELF_BOOKING.equals(appointment.getBookingSource()));
		calendarAppointment.setTagSelfCancelled(false);
		calendarAppointment.setDoNotBook(appointment.getName().equals(Appointment.DONOTBOOK));
		calendarAppointment.setAppointmentName(appointmentName);
		calendarAppointment.setConfirmed(appointment.isConfirmed());
		calendarAppointment.setCritical(appointment.getUrgency().equals("critical"));
		if (appointment.getBookingSource() != null)
		{
			calendarAppointment.setBookingSource(appointment.getBookingSource().toString());
		}

		return calendarAppointment;
	}

	@Override
    public AppointmentTo1 getAsTransferObject(LoggedInInfo loggedInInfo, Appointment d) throws ConversionException
	{
	   AppointmentTo1 t = new AppointmentTo1();
	   
	   BeanUtils.copyProperties(d, t);
	   
	   if(includeDemographic && t.getDemographicNo() > 0) {
		   t.setDemographic(demographicDao.getDemographicById(t.getDemographicNo()));
	   }
	   
	   if(includeProvider && t.getProviderNo() != null) {
		   t.setProvider(providerDao.getProvider(t.getProviderNo()));
	   }
	  
	   return t;
    }

	
}
