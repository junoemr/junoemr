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
package org.oscarehr.dataMigration.mapper.hrm.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.Person;
import org.springframework.beans.factory.annotation.Autowired;
import xml.hrm.v4_3.Gender;
import xml.hrm.v4_3.PersonStatus;
import xml.hrm.v4_3.AddressType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.oscarehr.demographic.entity.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.entity.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.entity.Demographic.STATUS_INACTIVE;

public class HRMDemographicExportMapperTest
{
	@Autowired
	@InjectMocks
	private HRMDemographicExportMapper hrmDemographicExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testToCdsAddress_Null()
	{
		assertNull(hrmDemographicExportMapper.toHrmAddress(null, AddressType.R));
	}

	@Test
	public void testToCdsAddress_Filled()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = "BC";
		String expectedCountry = "CA";
		String expectedPostal = "V8V0T0";

		String expectedProvinceReturn = expectedCountry + "-" + expectedProvince;

		Address address = new Address();
		address.setAddressLine1(expectedAddressLine1);
		address.setAddressLine2(expectedAddressLine2);
		address.setCity(expectedCity);
		address.setRegionCode(expectedProvince);
		address.setCountryCode(expectedCountry);
		address.setPostalCode(expectedPostal);

		xml.hrm.v4_3.Address resultAddress = hrmDemographicExportMapper.toHrmAddress(address, AddressType.R);
		assertEquals(expectedAddressLine1, resultAddress.getStructured().getLine1());
		assertEquals(expectedAddressLine2, resultAddress.getStructured().getLine2());
		assertEquals(expectedCity, resultAddress.getStructured().getCity());
		assertEquals(expectedProvinceReturn, resultAddress.getStructured().getCountrySubdivisionCode());
		assertEquals(expectedPostal, resultAddress.getStructured().getPostalZipCode().getPostalCode());
	}

	@Test
	public void testGetPatientStatus_Null()
	{
		PersonStatus personStatusCode = hrmDemographicExportMapper.getExportStatusCode(null);
		assertEquals(PersonStatus.A, personStatusCode);
	}

	@Test
	public void testGetPatientStatus_Active()
	{
		PersonStatus personStatusCode = hrmDemographicExportMapper.getExportStatusCode(STATUS_ACTIVE);
		assertEquals(PersonStatus.A, personStatusCode);
	}

	@Test
	public void testGetPatientStatus_Inactive()
	{
		PersonStatus personStatusCode = hrmDemographicExportMapper.getExportStatusCode(STATUS_INACTIVE);
		assertEquals(PersonStatus.I, personStatusCode);
	}

	@Test
	public void testGetPatientStatus_Deceased()
	{
		PersonStatus personStatusCode = hrmDemographicExportMapper.getExportStatusCode(STATUS_DECEASED);
		assertEquals(PersonStatus.D, personStatusCode);
	}

	@Test
	public void testGetPatientStatus_Custom()
	{
		String customStatus = "CUSTOM";
		PersonStatus personStatusCode = hrmDemographicExportMapper.getExportStatusCode(customStatus);
		assertEquals(PersonStatus.A, personStatusCode);
	}

	@Test
	public void testGetExportGender()
	{
		assertEquals(Gender.M, hrmDemographicExportMapper.getExportGender(Person.SEX.MALE));
		assertEquals(Gender.F, hrmDemographicExportMapper.getExportGender(Person.SEX.FEMALE));
		assertEquals(Gender.O, hrmDemographicExportMapper.getExportGender(Person.SEX.OTHER));
		assertEquals(Gender.O, hrmDemographicExportMapper.getExportGender(Person.SEX.TRANSGENDER));
		assertEquals(Gender.U, hrmDemographicExportMapper.getExportGender(Person.SEX.UNKNOWN));
	}

}
