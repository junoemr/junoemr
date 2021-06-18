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
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import xml.cds.v5_0.Demographics;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.PersonStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;

public class CDSDemographicImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSDemographicImportMapper cdsDemographicImportMapper;

	@Mock
	private PatientImportContextService patientImportContextService;

	@Mock
	private CDSEnrollmentHistoryImportMapper cdsEnrollmentHistoryImportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		CDSImportLogger cdsImportLoggerMock = Mockito.mock(CDSImportLogger.class);
		PatientImportContext patientImportContextMock = Mockito.mock(PatientImportContext.class);
		when(patientImportContextMock.getImportLogger()).thenReturn(cdsImportLoggerMock);
		when(patientImportContextService.getContext()).thenReturn(patientImportContextMock);
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

	/**
	 * ensure enrolled physician is used over family physician when patient is enrolled
	 */
	@Test
	public void testMapCareTeamInfo_EnrolledPhysicianMapping_Rostered() throws Exception
	{
		String familyDocFName = "famDoc";
		String familyDocLName = "incorrect";

		String enrolledDocFName = "enrolledDoc";
		String enrolledDocLName = "correct";
		String enrolledDocOhip = "12345";

		//set up
		ObjectFactory objectFactory = new ObjectFactory();

		List<RosterData> rosterDataList = new ArrayList<>();
		RosterData rosterData = new RosterData();
		rosterData.setRostered(true);

		Provider rosterProvider = new Provider();
		rosterProvider.setFirstName(enrolledDocFName);
		rosterProvider.setLastName(enrolledDocLName);
		rosterProvider.setOhipNumber(enrolledDocOhip);
		rosterData.setRosterProvider(rosterProvider);
		rosterDataList.add(rosterData);

		PersonNameSimple familyDoctor = objectFactory.createPersonNameSimple();
		familyDoctor.setFirstName(familyDocFName);
		familyDoctor.setLastName(familyDocLName);

		Demographics importStructure = mockCareTeamForEnrolledPhysicianCheck(rosterDataList, familyDoctor);

		// run the test
		Demographic demographic = new Demographic();
		cdsDemographicImportMapper.mapCareTeamInfo(importStructure, demographic);

		// check results
		assertNotNull(demographic.getFamilyDoctor());
		assertEquals(enrolledDocFName, demographic.getFamilyDoctor().getFirstName());
		assertEquals(enrolledDocLName, demographic.getFamilyDoctor().getLastName());
		assertEquals(enrolledDocOhip, demographic.getFamilyDoctor().getOhipNumber());
	}

	/**
	 * ensure family physician is used over enrolled physician when patient is not enrolled
	 */
	@Test
	public void testMapCareTeamInfo_EnrolledPhysicianMapping_RosterTerminated() throws Exception
	{
		String familyDocFName = "famDoc";
		String familyDocLName = "correct";

		String enrolledDocFName = "enrolledDoc";
		String enrolledDocLName = "incorrect";
		String enrolledDocOhip = "12345";

		//set up
		ObjectFactory objectFactory = new ObjectFactory();

		List<RosterData> rosterDataList = new ArrayList<>();
		RosterData rosterData = new RosterData();
		rosterData.setRostered(false);

		Provider rosterProvider = new Provider();
		rosterProvider.setFirstName(enrolledDocFName);
		rosterProvider.setLastName(enrolledDocLName);
		rosterProvider.setOhipNumber(enrolledDocOhip);
		rosterData.setRosterProvider(rosterProvider);
		rosterDataList.add(rosterData);

		PersonNameSimple familyDoctor = objectFactory.createPersonNameSimple();
		familyDoctor.setFirstName(familyDocFName);
		familyDoctor.setLastName(familyDocLName);

		Demographics importStructure = mockCareTeamForEnrolledPhysicianCheck(rosterDataList, familyDoctor);

		// run the test
		Demographic demographic = new Demographic();
		cdsDemographicImportMapper.mapCareTeamInfo(importStructure, demographic);

		// check results
		assertNotNull(demographic.getFamilyDoctor());
		assertEquals(familyDocFName, demographic.getFamilyDoctor().getFirstName());
		assertEquals(familyDocLName, demographic.getFamilyDoctor().getLastName());
	}

	private Demographics mockCareTeamForEnrolledPhysicianCheck(List<RosterData> rosterDataList, PersonNameSimple familyDoctor) throws Exception
	{
		Demographics importStructure = Mockito.mock(Demographics.class);
		Demographics.Enrolment enrollment = Mockito.mock(Demographics.Enrolment.class);
		Mockito.when(enrollment.getEnrolmentHistory()).thenReturn(null);
		Mockito.when(cdsEnrollmentHistoryImportMapper.importAll(Mockito.any())).thenReturn(rosterDataList);

		Mockito.when(importStructure.getEnrolment()).thenReturn(enrollment);

		Mockito.when(importStructure.getEmail()).thenReturn("mock@email");
		Mockito.when(importStructure.getChartNumber()).thenReturn("mock-chart");
		Mockito.when(importStructure.getPrimaryPhysician()).thenReturn(null);
		Mockito.when(importStructure.getReferredPhysician()).thenReturn(null);
		Mockito.when(importStructure.getPersonStatusCode()).thenReturn(null);
		Mockito.when(importStructure.getFamilyPhysician()).thenReturn(familyDoctor);

		return importStructure;
	}

}
