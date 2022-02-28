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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographicRoster.model.DemographicRoster;
import org.springframework.beans.factory.annotation.Autowired;
import xml.cds.v5_0.Demographics;
import xml.cds.v5_0.Gender;
import xml.cds.v5_0.PersonStatus;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.oscarehr.demographic.entity.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.entity.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.entity.Demographic.STATUS_INACTIVE;

public class CDSDemographicExportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSDemographicExportMapper cdsDemographicExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPatientStatus_Null()
	{
		Demographics.PersonStatusCode personStatusCode = cdsDemographicExportMapper.getExportStatusCode(null);
		assertEquals(PersonStatus.A, personStatusCode.getPersonStatusAsEnum());
	}

	@Test
	public void testGetPatientStatus_Active()
	{
		Demographics.PersonStatusCode personStatusCode = cdsDemographicExportMapper.getExportStatusCode(STATUS_ACTIVE);
		assertEquals(PersonStatus.A, personStatusCode.getPersonStatusAsEnum());
	}

	@Test
	public void testGetPatientStatus_Inactive()
	{
		Demographics.PersonStatusCode personStatusCode = cdsDemographicExportMapper.getExportStatusCode(STATUS_INACTIVE);
		assertEquals(PersonStatus.I, personStatusCode.getPersonStatusAsEnum());
	}

	@Test
	public void testGetPatientStatus_Deceased()
	{
		Demographics.PersonStatusCode personStatusCode = cdsDemographicExportMapper.getExportStatusCode(STATUS_DECEASED);
		assertEquals(PersonStatus.D, personStatusCode.getPersonStatusAsEnum());
	}

	@Test
	public void testGetPatientStatus_Custom()
	{
		String customStatus = "CUSTOM";
		Demographics.PersonStatusCode personStatusCode = cdsDemographicExportMapper.getExportStatusCode(customStatus);
		assertEquals(customStatus, personStatusCode.getPersonStatusAsPlainText());
	}

	@Test
	public void testGetExportGender()
	{
		assertEquals(Gender.M, cdsDemographicExportMapper.getExportGender(Person.SEX.MALE));
		assertEquals(Gender.F, cdsDemographicExportMapper.getExportGender(Person.SEX.FEMALE));
		assertEquals(Gender.O, cdsDemographicExportMapper.getExportGender(Person.SEX.OTHER));
		assertEquals(Gender.O, cdsDemographicExportMapper.getExportGender(Person.SEX.TRANSGENDER));
		assertEquals(Gender.U, cdsDemographicExportMapper.getExportGender(Person.SEX.UNKNOWN));
	}

	@Test
	public void testGetEnrollmentHistory_rosteredFieldsFilled()
	{
		RosterData rosterData = new RosterData();
		rosterData.setRostered(true);
		rosterData.setRosterDateTime(LocalDateTime.of(2021, 12, 24, 12, 0, 0));

		ProviderModel mockProvider = new ProviderModel();
		mockProvider.setFirstName("test");
		mockProvider.setLastName("provider");
		rosterData.setRosterProvider(mockProvider);

		// add invalid properties that should not appear in exported results
		rosterData.setTerminationDateTime(LocalDateTime.of(2021, 1, 1, 10, 0, 0));
		rosterData.setTerminationReason(DemographicRoster.ROSTER_TERMINATION_REASON.ASSIGNED_IN_ERROR);

		Demographics.Enrolment.EnrolmentHistory enrolmentHistory = cdsDemographicExportMapper.getEnrollmentHistory(rosterData);

		assertEquals(CDSConstants.ENROLLMENT_STATUS_TRUE, enrolmentHistory.getEnrollmentStatus());
		assertNotNull(enrolmentHistory.getEnrollmentDate());
		assertNotNull(enrolmentHistory.getEnrolledToPhysician());
		assertNull(enrolmentHistory.getEnrollmentTerminationDate());
		assertNull(enrolmentHistory.getTerminationReason());
	}

	@Test
	public void testGetEnrollmentHistory_terminatedFieldsFilled()
	{
		RosterData rosterData = new RosterData();
		rosterData.setRostered(false);

		ProviderModel mockProvider = new ProviderModel();
		mockProvider.setFirstName("test");
		mockProvider.setLastName("provider");
		rosterData.setRosterProvider(mockProvider);

		rosterData.setRosterDateTime(LocalDateTime.of(2021, 12, 24, 12, 0, 0));
		rosterData.setTerminationDateTime(LocalDateTime.of(2021, 1, 1, 10, 0, 0));
		rosterData.setTerminationReason(DemographicRoster.ROSTER_TERMINATION_REASON.ASSIGNED_IN_ERROR);


		Demographics.Enrolment.EnrolmentHistory enrolmentHistory = cdsDemographicExportMapper.getEnrollmentHistory(rosterData);

		assertEquals(CDSConstants.ENROLLMENT_STATUS_FALSE, enrolmentHistory.getEnrollmentStatus());
		assertNotNull(enrolmentHistory.getEnrollmentDate());
		assertNotNull(enrolmentHistory.getEnrolledToPhysician());
		assertNotNull(enrolmentHistory.getEnrollmentTerminationDate());
		assertNotNull(enrolmentHistory.getTerminationReason());
	}

}
