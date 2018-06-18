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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.model.v24.segment.SCH;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AppointmentMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;

	public AppointmentMapper()
	{
		message = null;
	}
	public AppointmentMapper(ZPD_ZTR message)
	{
		this.message = message;
	}

	public int getNumAppointments()
	{
		return this.message.getPATIENT().getSCHReps();
	}

	public List<Appointment> getAppointmentList() throws HL7Exception
	{
		int numAppointments = getNumAppointments();
		List<Appointment> appointmentList = new ArrayList<>(numAppointments);
		for(int i=0; i< numAppointments; i++)
		{
			appointmentList.add(getAppointment(i));
		}
		return appointmentList;
	}

	public Appointment getAppointment(int rep) throws HL7Exception
	{
		Appointment appointment = new Appointment();
		Date appointmentDate = getAppointmentDate(rep);

		appointment.setAppointmentDate(appointmentDate);
		appointment.setStartTime(appointmentDate);
		appointment.setEndTime(getAppointmentEnd(rep));

		appointment.setNotes(getNotes(rep));
		appointment.setReason(getReason(rep));
		appointment.setCreateDateTime(getCreationDate(rep));
		appointment.setStatus("t"); //TODO how to determine status?

		return appointment;
	}

	public String getNotes(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getSCH(rep).getSch30_zNotes().getValue());
	}

	public String getReason(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getSCH(rep).getSch29_zAppointmentReason().getValue());
	}

	public Date getCreationDate(int rep) throws HL7Exception
	{
		String dateStr = message.getPATIENT().getSCH(rep).getSch28_zCreationDate().getTs1_TimeOfAnEvent().getValue();
		return ConversionUtils.fromDateString(dateStr, "yyyyMMdd");
	}

	public Date getAppointmentDate(int rep) throws HL7Exception
	{
		SCH sch = message.getPATIENT().getSCH(rep);
		return ConversionUtils.fromDateString(sch.getSch11_AppointmentTimingQuantity(0).getStartDateTime().getTimeOfAnEvent().getValue(), "yyyyMMddHHmmss");
	}

	public Date getAppointmentEnd(int rep) throws HL7Exception
	{
		SCH sch = message.getPATIENT().getSCH(rep);
		Date appointmentDate = getAppointmentDate(rep);
		Integer apptDuration = Integer.parseInt(sch.getSch9_AppointmentDuration().getValue());
		String apptDurationUnit = sch.getSch10_AppointmentDurationUnits().getCe1_Identifier().getValue();

		return calcEndTime(appointmentDate, apptDuration, apptDurationUnit);
	}

	private Date calcEndTime(Date startTime, Integer duration, String units)
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(startTime); // sets calendar time/date

		// adds duration to the date/time
		switch(StringUtils.lowerCase(units))
		{
			case "hour": cal.add(Calendar.HOUR_OF_DAY, duration); break;
			case "min": cal.add(Calendar.MINUTE, duration); break;
			case "second": cal.add(Calendar.SECOND, duration); break;
			default: throw new RuntimeException("Unknown appointment duration unit: " + units);
		}

		cal.add(Calendar.SECOND, -1); // subtract 1 second from appointment time for oscar
		return cal.getTime();
	}
}
