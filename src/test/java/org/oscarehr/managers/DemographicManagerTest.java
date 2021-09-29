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
package org.oscarehr.managers;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class DemographicManagerTest
{
	@Autowired
	@InjectMocks
	private DemographicManager manager;

	@Mock
	private SecurityInfoManager securityInfoManager;

	@Mock
	private DemographicDao demographicDao;

	private LoggedInInfo loggedInInfo;
	private Demographic demographic;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		DaoTestFixtures.setupBeanFactory();

		Provider provider = new Provider();
		provider.setProviderNo("999998"); // Admin Provider No.
		loggedInInfo = new LoggedInInfo();
		loggedInInfo.setLoggedInProvider(provider);

		Mockito.doNothing().when(securityInfoManager).requireOnePrivilege(provider.getProviderNo(), SecurityInfoManager.CREATE, null, "_demographic");
		Mockito.doNothing().when(demographicDao).save(demographic);

		demographic = new Demographic();

		demographic.setFirstName("Joe");
		demographic.setLastName("Exotic");
		demographic.setSex("M");
		demographic.setYearOfBirth("1970");
		demographic.setMonthOfBirth("01");
		demographic.setDateOfBirth("01");
		demographic.setDemographicNo(123456789);
	}

	@Test
	public void addDemographicWithValidation_Pass()
	{
		try
		{
			manager.addDemographicWithValidation(loggedInInfo, demographic);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}

		Mockito.verify(demographicDao, times(1)).save(demographic);
	}

	@Test
	public void addDemographicWithValidation_NullFirstName_Fail()
	{
		String expectedMessage = DemographicManager.FIRST_NAME_REQUIRED;
		demographic.setFirstName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_EmptyFirstName_Fail()
	{
		String expectedMessage = DemographicManager.FIRST_NAME_REQUIRED;
		String badFirstName = "";
		demographic.setFirstName(badFirstName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullLastName_Fail()
	{
		String expectedMessage = DemographicManager.LAST_NAME_REQUIRED;
		demographic.setLastName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_EmptyLastName_Fail()
	{
		String expectedMessage = DemographicManager.LAST_NAME_REQUIRED;
		String badLastName = "";
		demographic.setLastName(badLastName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullSex_Fail()
	{
		String expectedMessage = DemographicManager.SEX_REQUIRED;
		demographic.setSex(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidSex_Fail()
	{
		String badSex = "badSex";
		demographic.setSex(badSex);
		String expectedMessage = DemographicManager.SEX_INVALID;

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullYearOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_REQUIRED;
		demographic.setYearOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidYearOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_NUMERIC;
		String badYear = "bad year";
		demographic.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_YearOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_4_DIGIT;
		String badYear = "999";
		demographic.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_YearOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_4_DIGIT;
		String badYear = "10000";
		demographic.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullMonthOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_REQUIRED;
		demographic.setMonthOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidMonthOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "badMonth";
		demographic.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_MonthOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "0";
		demographic.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_MonthOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "13";
		demographic.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullDateOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_REQUIRED;
		demographic.setDateOfBirth((String) null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidDateOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "badDate";
		demographic.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_DateOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "0";
		demographic.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_DateOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "32";
		demographic.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidBirthday_Fail()
	{
		String expectedMessage = DemographicManager.BIRTHDAY_INVALID;
		String badYear = "2001";
		String badMonth = "2";
		String badDate = "29";
		demographic.setYearOfBirth(badYear);
		demographic.setMonthOfBirth(badMonth);
		demographic.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidFamilyDoctor_Fail()
	{
		String expectedMessage = DemographicManager.FAMILY_DOCTOR_INVALID;
		demographic.setFamilyDoctor("Badly formmated");

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_HtmlTagInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "<text>Victoria</text>";
		demographic.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_QuotesInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "\"Victoria\"";
		demographic.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_SemicolonInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "Victoria;";
		demographic.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	private void actAndAssertExceptionMessage(String expectedMessage)
	{
		try
		{
			manager.addDemographicWithValidation(loggedInInfo, demographic);
			fail("Did not throw an exception.");
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage().contains(expectedMessage));
			Mockito.verify(demographicDao, times(0)).save(demographic);
		}
	}
}