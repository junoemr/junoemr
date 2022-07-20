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
package org.oscarehr.demographicArchive.converter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.converter.AbstractModelConverterTest;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographicArchive.entity.DemographicArchive;
import org.oscarehr.demographicArchive.entity.DemographicCustArchive;
import org.oscarehr.demographicRoster.entity.RosterTerminationReason;
import org.oscarehr.rosterStatus.entity.RosterStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

public class DemographicToArchiveConverterTest extends AbstractModelConverterTest<DemographicToArchiveConverter, Demographic, DemographicArchive>
{
	@Autowired
	@InjectMocks
	private DemographicToArchiveConverter demographicToArchiveConverter;

	@Mock
	protected DemographicExtToArchiveConverter demographicExtToArchiveConverter;

	@Mock
	protected DemographicCustToArchiveConverter demographicCustToArchiveConverter;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	/**
	 * ensure all fields are copied by the converter
	 */
	@Test
	public void test_convert_allFieldsCopied()
	{
		Mockito.doReturn(new LinkedList<>()).when(demographicExtToArchiveConverter).convert(Mockito.anyCollection());
		Mockito.doReturn(new DemographicCustArchive()).when(demographicCustToArchiveConverter).convert(Mockito.any(DemographicCust.class));

		Demographic source = new Demographic();
		source.setDemographicId(1);
		source.setDemographicCust(new DemographicCust());
		source.setDemographicExtSet(new HashSet<>());

		// base info
		source.setFirstName("first name");
		source.setMiddleName("middle name");
		source.setLastName("last name");
		source.setTitle("Mr");
		source.setDateOfBirth(LocalDate.of(1990, 4, 20));
		source.setSex(Person.SEX.MALE.getValue());
		source.setHin("9151247483");
		source.setVer("AA");
		source.setHcType("BC");
		source.setHcEffectiveDate(new Date());
		source.setHcRenewDate(new Date());
		source.setChartNo("12345");
		source.setSin("989000989");
		source.setPatientStatus(Demographic.STATUS_ACTIVE);
		source.setPatientStatusDate(new Date());
		source.setDateJoined(new Date());
		source.setEndDate(new Date());

		//contact info
		source.setAddress("1234 some place");
		source.setCity("everywhere");
		source.setProvince("BC");
		source.setPostal("V0V0V0");
		source.setEmail("test@test.com");
		source.setPhone("5551231234");
		source.setPhone2("5551231234");
		source.setPreviousAddress("some place else");

		// physician info
		source.setProviderNo("999999");
		source.setReferralDoctor("");
		source.setFamilyDoctor("");

		// roster info
		source.setRosterStatus(RosterStatus.ROSTER_STATUS_ROSTERED);
		source.setRosterDate(new Date());
		source.setRosterTerminationDate(new Date());
		source.setRosterTerminationReason(String.valueOf(RosterTerminationReason.ASSIGNED_IN_ERROR.getTerminationCode()));

		// other info
		source.setLastUpdateUser("999999");
		source.setLastUpdateDate(new Date());
		source.setPcnIndicator("55");
		source.setAlias("alias");
		source.setChildren("children");
		source.setSourceOfIncome("$$$");
		source.setCitizenship("citizen");
		source.setAnonymous("anon");
		source.setSpokenLanguage("Spanish");
		source.setOfficialLanguage(DemographicModel.OFFICIAL_LANGUAGE.ENGLISH.getValue());
		source.setCountryOfOrigin("CA");
		source.setNewsletter("no");
		source.setVeteranNo("5566");
		source.setNameOfMother("mother");
		source.setNameOfFather("father");

		source.setElectronicMessagingConsentGivenAt(new Date());
		source.setElectronicMessagingConsentRejectedAt(new Date());

		DemographicArchive destination = demographicToArchiveConverter.convert(source);
		testPropertiesNotNull(destination, "id", "myOscarUserName");
	}

	@Override
	protected DemographicToArchiveConverter getConverter()
	{
		return demographicToArchiveConverter;
	}
}