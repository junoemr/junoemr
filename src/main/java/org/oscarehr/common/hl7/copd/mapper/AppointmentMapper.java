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
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.common.hl7.copd.model.v24.segment.SCH;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.provider.model.ProviderData;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppointmentMapper extends AbstractMapper
{
	public AppointmentMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, importSource);
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

		if (appointmentDate == null && CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			appointmentDate = getCreationDate(rep);
		}

		appointment.setAppointmentDate(appointmentDate);
		appointment.setStartTime(appointmentDate);
		appointment.setEndTime(getAppointmentEnd(rep));

		// hopefully one day appointments will handle null values correctly. until then, trim to empty
		appointment.setNotes(StringUtils.trimToEmpty(getNotes(rep)));
		appointment.setReason(StringUtils.trimToEmpty(getReason(rep)));
		appointment.setCreateDateTime(getCreationDate(rep));
		appointment.setStatus(getStatus(rep, importSource));
		appointment.setType("");
		appointment.setLocation("");
		appointment.setResources("");
		appointment.setReasonCode(17); // TODO look this up somewhere

		return appointment;
	}

	public ProviderData getAppointmentProvider(int rep) throws HL7Exception
	{
		ProviderData provider = null;

		String firstName = getProviderGivenName(rep);
		String lastName = getProviderFamilyName(rep);

		if(lastName != null && firstName != null)
		{
			provider = new ProviderData();
			provider.setFirstName(firstName);
			provider.setLastName(lastName);
		}
		return provider;
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
		return getNullableDate(dateStr);
	}

	public Date getAppointmentDate(int rep) throws HL7Exception
	{
		SCH sch = message.getPATIENT().getSCH(rep);
		Date apptDate = ConversionUtils.getLegacyDateFromDateString(sch.getSch11_AppointmentTimingQuantity(0).getStartDateTime().getTimeOfAnEvent().getValue(), "yyyyMMddHHmmss");
		return apptDate;
	}

	public Date getAppointmentEnd(int rep) throws HL7Exception
	{
		SCH sch = message.getPATIENT().getSCH(rep);
		Date appointmentDate = getAppointmentDate(rep);
		Integer apptDuration = Integer.parseInt(sch.getSch9_AppointmentDuration().getValue());
		String apptDurationUnit = sch.getSch10_AppointmentDurationUnits().getCe1_Identifier().getValue();

		if (appointmentDate == null && CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{// if no appointment date, use creation date instead.
			appointmentDate = getCreationDate(rep);
		}

		return calcEndTime(appointmentDate, apptDuration, apptDurationUnit);
	}

	public String getStatus(int rep) throws HL7Exception
	{
		return getStatus(rep, CoPDImportService.IMPORT_SOURCE.UNKNOWN);
	}

	public String getStatus(int rep, CoPDImportService.IMPORT_SOURCE importSource) throws HL7Exception
	{
		//TODO how to determine status from import data?
		Date apptDate = getAppointmentDate(rep);

		if (apptDate == null && CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			apptDate = getCreationDate(rep);
		}

		// attempt to map status based on znote text
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			String znote = getNotes(rep);
			if (znote != null)
			{
				try
				{
					return getStatusFromNote(znote);
				}
				catch (RuntimeException e)
				{
					// use default logic
				}
			}

		}

		if (apptDate.compareTo(new Date()) < 0)
		{
			// appointment date is before current date
			return AppointmentStatus.APPOINTMENT_STATUS_BILLED;
		}
		return AppointmentStatus.APPOINTMENT_STATUS_NEW;
	}

	private String getStatusFromNote(String note) throws RuntimeException
	{
		Matcher statusMatcher = Pattern.compile("^\\s*([\\w\\d]+)").matcher(note);
		if (statusMatcher.find())
		{
			String statusString = statusMatcher.group(1);

			switch (statusString)
			{
				case "Done":
				case "Billed":
				{
					return AppointmentStatus.APPOINTMENT_STATUS_BILLED;
				}
				case "Arrived":
				{
					return AppointmentStatus.APPOINTMENT_STATUS_HERE;
				}
				case "Cancel":
				case "Resched":
				case "Recall":
				{
					return AppointmentStatus.APPOINTMENT_STATUS_CANCELLED;
				}
				case "No":
				{
					if (note.contains("Show"))
					{
						return AppointmentStatus.APPOINTMENT_STATUS_NO_SHOW;
					}
				}
				case "Left":
				{
					return AppointmentStatus.APPOINTMENT_STATUS_NO_SHOW;
				}
			}
		}
		throw new RuntimeException("Cannot match znote text to appointment status");
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

	public String getProviderGivenName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getSCH(rep).
				getSch16_FillerContactPerson(0).getXcn3_GivenName().getValue());
	}

	public String getProviderFamilyName(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(message.getPATIENT().getSCH(rep).
				getSch16_FillerContactPerson(0).getXcn2_FamilyName().getFn1_Surname().getValue());
	}
}
