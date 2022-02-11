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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.oscarehr.dataMigration.exception.InvalidImportDataException;
import org.oscarehr.dataMigration.model.appointment.Appointment;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.dataMigration.service.AppointmentStatusCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.Appointments;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.oscarehr.common.model.Appointment.DEFAULT_APPOINTMENT_DURATION_MIN;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_BILLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_NEW;

@Component
public class CDSAppointmentImportMapper extends AbstractCDSImportMapper<Appointments, Appointment>
{
	@Autowired
	private AppointmentStatusCache appointmentStatusCache;

	public CDSAppointmentImportMapper()
	{
		super();
	}

	@Override
	public Appointment importToJuno(Appointments importStructure) throws InvalidImportDataException
	{
		Appointment appointment = new Appointment();

		LocalDateTime appointmentDateTime = getAppointmentStartDateTime(importStructure);
		appointment.setAppointmentStartDateTime(appointmentDateTime);
		appointment.setAppointmentEndDateTime(getCalculatedEndDateTime(appointmentDateTime, importStructure.getDuration()));
		appointment.setStatus(getStatus(importStructure.getAppointmentStatus(), appointmentDateTime));
		appointment.setProvider(getImportProvider(importStructure));
		appointment.setReason(importStructure.getAppointmentPurpose());
		appointment.setNotes(importStructure.getAppointmentNotes());
		appointment.setSite(patientImportContextService.getContext().getImportPreferences().getDefaultSite());

		return appointment;
	}

	protected LocalDateTime getAppointmentStartDateTime(Appointments importStructure) throws InvalidImportDataException
	{
		LocalDate appointmentDate = toNullableLocalDate(importStructure.getAppointmentDate());
		LocalTime appointmentTime = ConversionUtils.toNullableLocalTime(importStructure.getAppointmentTime());
		if(appointmentDate == null || appointmentTime == null)
		{
			throw new InvalidImportDataException("Appointment must have a valid date and time");
		}
		return LocalDateTime.of(appointmentDate, appointmentTime);
	}

	protected LocalDateTime getCalculatedEndDateTime(LocalDateTime appointmentDateTime, BigInteger duration)
	{
		if(duration == null)
		{
			duration = BigInteger.valueOf(DEFAULT_APPOINTMENT_DURATION_MIN);
			logDefaultValueUse("[" + appointmentDateTime + "] Missing appointment duration value", duration);
		}
		return appointmentDateTime.plusMinutes(duration.longValue());
	}

	protected ProviderModel getImportProvider(Appointments importStructure)
	{
		ProviderModel provider = null;
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
			appointmentStatus = appointmentStatusCache.findByName(importStatus);
		}

		// if we couldn't match it, use the default
		if(appointmentStatus == null)
		{
			appointmentStatus = appointmentStatusCache.findByCode(defaultStatusCode);

			if(importStatus != null) // don't want a warning if there was no status
			{
				logDefaultValueUse("[" + appointmentDateTime + "] Unknown appointment status value '" + importStatus + "'", defaultStatusCode);
			}
		}
		return appointmentStatus;
	}
}
