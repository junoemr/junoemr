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
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.oscarehr.common.hl7.Hl7Const.HL7_SEGMENT_SCH_11;

public class AppointmentMapper extends AbstractMapper
{
	private final CoPDRecordData recordData;

	public static final int APPOINTMENT_TYPE_LENGTH = 50;
	public static final int APPOINTMENT_REASON_LENGTH = 80;
	public static final String DEFAULT_APPOINTMENT_DURATION_HR = "1";
	public static final String DEFAULT_APPOINTMENT_DURATION_MIN = "15";
	public static final Date FALLBACK_APPOINTMENT_DATE = new Date(1900, 1, 1, 0 ,0);

	public AppointmentMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource, CoPDRecordData recordData)
	{
		super(message, importSource);
		this.recordData = recordData;
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

		// hopefully one day appointments will handle null values correctly. until then, trim to empty
		appointment.setNotes(StringUtils.trimToEmpty(getNotes(rep)));
		appointment.setCreateDateTime(getCreationDate(rep));
		appointment.setStatus(getStatus(rep, importSource));
		// Some appointment types exceed character length of column and may get truncated
		String type = getType(rep);
		if (type != null && type.length() > APPOINTMENT_TYPE_LENGTH)
		{
			logger.warn("Appointment has a type that is getting truncated: '" + type + "'");
			type = StringUtils.left(type, APPOINTMENT_TYPE_LENGTH);
		}

		String reason = getReason(rep);
		if (reason != null && reason.length() > APPOINTMENT_REASON_LENGTH)
		{
			logger.warn("Appointment has a reason that is getting truncated: '" + reason + "'");
			reason = StringUtils.left(reason, APPOINTMENT_REASON_LENGTH);
		}

		// hopefully one day appointments will handle null values correctly. until then, trim to empty
		appointment.setReason(StringUtils.trimToEmpty(reason));
		appointment.setType(type);
		appointment.setLocation("");
		appointment.setResources("");

		org.oscarehr.appointment.service.Appointment appointmentService = SpringUtils.getBean(org.oscarehr.appointment.service.Appointment.class);
		appointment.setReasonCode(appointmentService.getIdForAppointmentReasonCode(Appointment.DEFAULT_REASON));

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
		String value = message.getPATIENT().getSCH(rep).getSch30_zNotes().getValue();
		value = (value == null) ? null : value.replaceAll("~crlf~", "\n");
		return StringUtils.trimToNull(value);
	}

	public String getReason(int rep) throws HL7Exception
	{
		String value = message.getPATIENT().getSCH(rep).getSch29_zAppointmentReason().getValue();
		value = (value == null) ? null : value.replaceAll("~crlf~", "\n");
		return StringUtils.trimToNull(value);
	}

	/**
	 * get the appointment type
	 * @param rep - the rep that you want the type for
 	 * @return - the type string
	 * @throws HL7Exception - if an hl7 parsing error occurs.
	 */
	public String getType(int rep) throws HL7Exception
	{
		return "";
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

		if (apptDate == null)
		{
			Date creationDate = getCreationDate(rep);

			if (creationDate != null)
			{
				String warning = "Appointment date is null, falling back to creation date: " + creationDate.toString() + "\n";
				logger.info(warning);
				recordData.addMessage(HL7_SEGMENT_SCH_11, String.valueOf(rep), warning);
				apptDate = creationDate;
			}
			else
			{
				String warning = "Appointment date and creation date are both null, using fallback date 1900-01-01 00:00:00\n" + sch.toString() + "\n";
				logger.warn(warning);
				recordData.addMessage(HL7_SEGMENT_SCH_11, String.valueOf(rep), warning);
				apptDate = FALLBACK_APPOINTMENT_DATE;
			}
		}

		return apptDate;
	}

	public Date getAppointmentEnd(int rep) throws HL7Exception
	{
		SCH sch = message.getPATIENT().getSCH(rep);
		Date appointmentDate = getAppointmentDate(rep);
		String apptDurationRawValue = sch.getSch9_AppointmentDuration().getValue();
		String apptDurationUnit = sch.getSch10_AppointmentDurationUnits().getCe1_Identifier().getValue();
		int apptDuration;
		// If they don't send us an appt time, use either 15m or 1h depending on units
		if (apptDurationRawValue == null || apptDurationRawValue.isEmpty())
		{
			if ("HR".equals(apptDurationUnit.toUpperCase()))
			{
				logger.error("Bad appointment duration value, defaulting to 1 hr:"  + apptDurationRawValue);
				apptDurationRawValue = DEFAULT_APPOINTMENT_DURATION_HR;
			}
			else
			{
				logger.error("Bad appointment duration value, defaulting to 15 min: " + apptDurationRawValue);
				apptDurationRawValue = DEFAULT_APPOINTMENT_DURATION_MIN;
			}
		}
		// Round down duration. "12.5" parses to 12
		double apptDurationDouble = Double.parseDouble(apptDurationRawValue);
		apptDuration = (int) apptDurationDouble;

		if(Math.abs(apptDuration - apptDurationDouble) > 0.1)
		{
			logger.warn("Appointment duration " + apptDurationDouble + " " + apptDurationUnit + "is being truncated to " + apptDuration + " " + apptDurationUnit);
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
