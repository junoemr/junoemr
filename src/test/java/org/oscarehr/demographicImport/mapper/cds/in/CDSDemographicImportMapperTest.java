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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.xml.cds.v5_0.model.Appointments;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.Demographics;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.PersonStatus;
import org.oscarehr.demographicImport.model.appointment.AppointmentStatus;
import org.oscarehr.demographicImport.service.AppointmentStatusCache;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.DateTimeException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.oscarehr.common.model.Appointment.DEFAULT_APPOINTMENT_DURATION_MIN;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_BILLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_CANCELLED;
import static org.oscarehr.common.model.AppointmentStatus.APPOINTMENT_STATUS_NEW;
import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;

public class CDSDemographicImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSDemographicImportMapper cdsDemographicImportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPatientStatus_Null()
	{
		assertEquals(STATUS_ACTIVE, cdsDemographicImportMapper.getPatientStatus(null));
	}

	@Test
	public void testGetPatientStatus_EnumA()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsEnum(PersonStatus.A);

		assertEquals(STATUS_ACTIVE, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_EnumI()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsEnum(PersonStatus.I);

		assertEquals(STATUS_INACTIVE, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_EnumD()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsEnum(PersonStatus.D);

		assertEquals(STATUS_DECEASED, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_PlainA()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsPlainText("A");

		assertEquals(STATUS_ACTIVE, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_PlainI()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsPlainText("I");

		assertEquals(STATUS_INACTIVE, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_PlainD()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsPlainText("D");

		assertEquals(STATUS_DECEASED, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}

	@Test
	public void testGetPatientStatus_PlainInvalid()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		personStatusCode.setPersonStatusAsPlainText("Invalid");

		assertEquals(STATUS_ACTIVE, cdsDemographicImportMapper.getPatientStatus(personStatusCode));
	}
}
