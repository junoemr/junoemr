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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.exception.InvalidImportDataException;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import xml.cds.v5_0.Appointments;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.ObjectFactory;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
import org.oscarehr.dataMigration.service.AppointmentStatusCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.oscarehr.common.model.Appointment.DEFAULT_APPOINTMENT_DURATION_MIN;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_BILLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_CANCELLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_NEW;

public class CDSAppointmentImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSAppointmentImportMapper cdsAppointmentImportMapper;

	@Mock
	private AppointmentStatusCache appointmentStatusCache;

	@Mock
	private PatientImportContextService patientImportContextService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		AppointmentStatus appointmentStatusNewMock = new AppointmentStatus();
		appointmentStatusNewMock.setStatusCode(APPOINTMENT_STATUS_NEW);
		AppointmentStatus appointmentStatusPastMock = new AppointmentStatus();
		appointmentStatusPastMock.setStatusCode(APPOINTMENT_STATUS_BILLED);
		AppointmentStatus appointmentStatusCancelledMock = new AppointmentStatus();
		appointmentStatusCancelledMock.setStatusCode(APPOINTMENT_STATUS_CANCELLED);

		Mockito.when(appointmentStatusCache.findByCode(Mockito.anyString())).thenAnswer(invocationOnMock -> {
			String statusCode = invocationOnMock.getArgument(0);
			switch(statusCode)
			{
				case APPOINTMENT_STATUS_NEW: return appointmentStatusNewMock;
				case APPOINTMENT_STATUS_BILLED: return appointmentStatusPastMock;
				case APPOINTMENT_STATUS_CANCELLED: return appointmentStatusCancelledMock;
			}
			return null;
		});

		Mockito.when(appointmentStatusCache.findByName(Mockito.anyString())).thenAnswer(invocationOnMock -> {
			String statusName = invocationOnMock.getArgument(0);
			switch(statusName)
			{
				case "To Do": return appointmentStatusNewMock;
				case "Billed": return appointmentStatusPastMock;
				case "Cancelled": return appointmentStatusCancelledMock;
			}
			return null;
		});

		CDSImportLogger cdsImportLoggerMock = Mockito.mock(CDSImportLogger.class);
		PatientImportContext patientImportContextMock = Mockito.mock(PatientImportContext.class);
		when(patientImportContextMock.getImportLogger()).thenReturn(cdsImportLoggerMock);
		when(patientImportContextService.getContext()).thenReturn(patientImportContextMock);
	}

	@Test(expected = InvalidImportDataException.class)
	public void testGetAppointmentStartDateTime_Null() throws InvalidImportDataException
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Appointments appointments = objectFactory.createAppointments();
		appointments.setAppointmentDate(null);
		appointments.setAppointmentTime(null);
		cdsAppointmentImportMapper.getAppointmentStartDateTime(appointments);
	}

	@Test
	public void testGetAppointmentStartDateTime_FullDate() throws DatatypeConfigurationException, InvalidImportDataException
	{
		LocalDateTime appointmentDateTime = LocalDateTime.of(2021, 1, 12, 11, 30, 0);

		XMLGregorianCalendar dayCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(appointmentDateTime.toLocalDate().toString());
		XMLGregorianCalendar timeCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(appointmentDateTime.toLocalDate().toString());
		timeCalendar.setHour(appointmentDateTime.getHour());
		timeCalendar.setMinute(appointmentDateTime.getMinute());
		timeCalendar.setSecond(appointmentDateTime.getSecond());

		ObjectFactory objectFactory = new ObjectFactory();
		Appointments appointments = objectFactory.createAppointments();
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(dayCalendar);

		appointments.setAppointmentDate(dateFullOrPartial);
		appointments.setAppointmentTime(timeCalendar);

		assertEquals(appointmentDateTime, cdsAppointmentImportMapper.getAppointmentStartDateTime(appointments));
	}

	@Test
	public void testGetCalculatedEndDateTime_Null()
	{
		LocalDateTime startDateTime = LocalDateTime.of(2021, 1, 12, 11, 30, 0);
		LocalDateTime expectedEndDateTime = startDateTime.plusMinutes(DEFAULT_APPOINTMENT_DURATION_MIN);

		assertEquals(expectedEndDateTime, cdsAppointmentImportMapper.getCalculatedEndDateTime(startDateTime, null));
	}

	@Test
	public void testGetCalculatedEndDateTime_Numeric()
	{
		long duration = 25L;
		BigInteger bigDuration = BigInteger.valueOf(duration);
		LocalDateTime startDateTime = LocalDateTime.of(2021, 1, 12, 11, 30, 0);
		LocalDateTime expectedEndDateTime = startDateTime.plusMinutes(duration);

		assertEquals(expectedEndDateTime, cdsAppointmentImportMapper.getCalculatedEndDateTime(startDateTime, bigDuration));
	}

	@Test
	public void testGetStatus_DefaultPast()
	{
		// start date time in the past
		LocalDateTime startDateTime = LocalDateTime.now().minusMinutes(60);
		AppointmentStatus appointmentStatus = cdsAppointmentImportMapper.getStatus(null, startDateTime);
		assertEquals(APPOINTMENT_STATUS_BILLED, appointmentStatus.getStatusCode());
	}

	@Test
	public void testGetStatus_DefaultFuture()
	{
		// start date time in the future
		LocalDateTime startDateTime = LocalDateTime.now().plusDays(2);
		AppointmentStatus appointmentStatus = cdsAppointmentImportMapper.getStatus(null, startDateTime);
		assertEquals(APPOINTMENT_STATUS_NEW, appointmentStatus.getStatusCode());
	}

	@Test
	public void testGetStatus_FromStatusName_Found()
	{
		LocalDateTime startDateTime = LocalDateTime.now().minusDays(10);
		AppointmentStatus appointmentStatus = cdsAppointmentImportMapper.getStatus("Cancelled", startDateTime);
		assertEquals(APPOINTMENT_STATUS_CANCELLED, appointmentStatus.getStatusCode());
	}

	@Test
	public void testGetStatus_FromStatusName_NotFound()
	{
		LocalDateTime startDateTime = LocalDateTime.now().minusDays(10);
		AppointmentStatus appointmentStatus = cdsAppointmentImportMapper.getStatus("Not a status", startDateTime);
		assertEquals(APPOINTMENT_STATUS_BILLED, appointmentStatus.getStatusCode());
	}
}
