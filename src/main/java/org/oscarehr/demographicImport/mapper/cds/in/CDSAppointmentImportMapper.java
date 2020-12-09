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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.Appointments;
import org.oscarehr.demographicImport.model.appointment.Appointment;
import org.oscarehr.demographicImport.model.appointment.AppointmentStatus;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.oscarehr.demographicImport.service.AppointmentStatusCache;
import oscar.util.ConversionUtils;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import static org.oscarehr.common.model.Appointment.DEFAULT_APPOINTMENT_DURATION_MIN;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_BILLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_NEW;

public class CDSAppointmentImportMapper extends AbstractCDSImportMapper<Appointments, Appointment>
{
	public CDSAppointmentImportMapper()
	{
		super();
	}

	@Override
	public Appointment importToJuno(Appointments importStructure)
	{
		Appointment appointment = new Appointment();

		LocalDateTime appointmentDateTime = getAppointmentStartDateTime(importStructure);
		appointment.setAppointmentStartDateTime(appointmentDateTime);
		appointment.setAppointmentEndDateTime(getCalculatedEndDateTime(appointmentDateTime, importStructure.getDuration()));
		appointment.setStatus(getStatus(importStructure.getAppointmentStatus(), appointmentDateTime));
		appointment.setProvider(getImportProvider(importStructure));
		appointment.setReason(importStructure.getAppointmentPurpose());
		appointment.setNotes(importStructure.getAppointmentNotes());

		return appointment;
	}

	protected LocalDateTime getAppointmentStartDateTime(Appointments importStructure)
	{
		LocalDate appointmentDate = toNullableLocalDate(importStructure.getAppointmentDate());
		LocalTime appointmentTime = ConversionUtils.toLocalTime(importStructure.getAppointmentTime());
		return LocalDateTime.of(appointmentDate, appointmentTime);
	}

	protected LocalDateTime getCalculatedEndDateTime(LocalDateTime appointmentDateTime, BigInteger duration)
	{
		if(duration == null)
		{
			duration = BigInteger.valueOf(DEFAULT_APPOINTMENT_DURATION_MIN);
		}
		return appointmentDateTime.plus(duration.longValue(), ChronoUnit.MINUTES);
	}

	protected Provider getImportProvider(Appointments importStructure)
	{
		Provider provider = null;
		Appointments.Provider importProvider = importStructure.getProvider();
		if(importProvider != null)
		{
			provider = toProvider(importProvider.getName());
			provider.setOhipNumber(importProvider.getOHIPPhysicianId());
		}
		return provider;
	}

	protected AppointmentStatus getStatus(String importStatus, LocalDateTime appointmentDateTime)
	{
		// by default future appointments are NEW, and past are BILLED (complete)
		String defaultStatusCode = (appointmentDateTime.isAfter(LocalDateTime.now())) ? APPOINTMENT_STATUS_NEW : APPOINTMENT_STATUS_BILLED;

		AppointmentStatus appointmentStatus = null;
		if(importStatus != null)
		{
			appointmentStatus = AppointmentStatusCache.findByName(importStatus);
		}

		// if we couldn't match it, use the default
		if(appointmentStatus == null)
		{
			appointmentStatus = AppointmentStatusCache.findByCode(defaultStatusCode);
		}
		return appointmentStatus;
	}
}
