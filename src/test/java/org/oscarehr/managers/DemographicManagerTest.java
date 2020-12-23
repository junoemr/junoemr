package org.oscarehr.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import java.util.List;

public class DemographicManagerTest extends DaoTestFixtures
{

	DemographicManager manager = SpringUtils.getBean(DemographicManager.class);
	LoggedInInfo l = LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod();
	Demographic demo;

	@Before
	public void setupTest()
	{
		Provider provider = new Provider();
		provider.setProviderNo("999998"); // Admin Provider No.
		l = new LoggedInInfo();
		l.setLoggedInProvider(provider);

		demo = new Demographic();

		demo.setFirstName("Joe");
		demo.setLastName("Exotic");
		demo.setSex("M");
		demo.setYearOfBirth("1970");
		demo.setMonthOfBirth("01");
		demo.setDateOfBirth("01");
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_Pass()
	{
		try
		{
			manager.addDemographicWithValidation(l, demo);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		List<Demographic> demos = manager.getDemographicWithLastFirstDOB(l,
				demo.getLastName(),
				demo.getFirstName(),
				demo.getYearOfBirth(),
				demo.getMonthOfBirth(),
				demo.getDateOfBirth());

		assertFalse(demos.isEmpty());
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullFirstName_Fail()
	{
		String expectedMessage = "firstName is a required field.";
		demo.setFirstName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_EmptyFirstName_Fail()
	{
		String expectedMessage = "firstName is a required field.";
		String badFirstName = "";
		demo.setFirstName(badFirstName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullLastName_Fail()
	{
		String expectedMessage = "lastName is a required field.";
		demo.setLastName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_EmptyLastName_Fail()
	{
		String expectedMessage = "lastName is a required field.";
		String badLastName = "";
		demo.setLastName(badLastName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullSex_Fail()
	{
		String expectedMessage = "sex is a required field.";
		demo.setSex(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidSex_Fail()
	{
		String badSex = "badSex";
		demo.setSex(badSex);
		String expectedMessage = "sex must be either \"M\" or \"F\" (received " +
				badSex + ").";

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullYearOfBirth_Fail()
	{
		String expectedMessage = "yearOfBirth is a required field.";
		demo.setYearOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidYearOfBirth_Fail()
	{
		String expectedMessage = "yearOfBirth should be a numeric value.";
		String badYear = "bad year";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_YearOfBirthTooLow_Fail()
	{
		String expectedMessage = "yearOfBirth is expected to be a 4-digit number.";
		String badYear = "999";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_YearOfBirthTooHigh_Fail()
	{
		String expectedMessage = "yearOfBirth is expected to be a 4-digit number.";
		String badYear = "10000";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullMonthOfBirth_Fail()
	{
		String expectedMessage = "monthOfBirth is a required field.";
		demo.setMonthOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidMonthOfBirth_Fail()
	{
		String expectedMessage = "monthOfBirth should be a number between 1 and 12.";
		String badMonth = "badMonth";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_MonthOfBirthTooLow_Fail()
	{
		String expectedMessage = "monthOfBirth should be a number between 1 and 12.";
		String badMonth = "0";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_MonthOfBirthTooHigh_Fail()
	{
		String expectedMessage = "monthOfBirth should be a number between 1 and 12.";
		String badMonth = "13";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_NullDateOfBirth_Fail()
	{
		String expectedMessage = "dateOfBirth is a required field.";
		demo.setDateOfBirth((String) null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidDateOfBirth_Fail()
	{
		String expectedMessage = "dateOfBirth should be a number between 1 and 31 (depending on month).";
		String badDate = "badDate";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_DateOfBirthTooLow_Fail()
	{
		String expectedMessage = "dateOfBirth should be a number between 1 and 31 (depending on month).";
		String badDate = "0";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_DateOfBirthTooHigh_Fail()
	{
		String expectedMessage = "dateOfBirth should be a number between 1 and 31 (depending on month).";
		String badDate = "32";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidBirthday_Fail()
	{
		String expectedMessage = "Need a valid birth date.";
		String badYear = "2001";
		String badMonth = "2";
		String badDate = "29";
		demo.setYearOfBirth(badYear);
		demo.setMonthOfBirth(badMonth);
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_AddDemographicWithValidation_InvalidFamilyDoctor_Fail()
	{
		String expectedMessage = "familyDoctor is formatted incorrectly.";
		demo.setFamilyDoctor("Badly formmated");

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_HtmlTagInField_Fail()
	{
		String expectedMessage = "No html tags and no quotes, line breaks " +
				"or semicolons are allowed.";
		String badCity = "<text>Victoria</text>";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_QuotesInField_Fail()
	{
		String expectedMessage = "No html tags and no quotes, line breaks " +
				"or semicolons are allowed.";
		String badCity = "\"Victoria\"";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_SemicolonInField_Fail()
	{
		String expectedMessage = "No html tags and no quotes, line breaks " +
				"or semicolons are allowed.";
		String badCity = "Victoria;";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	private void actAndAssertExceptionMessage(String expectedMessage)
	{
		try
		{
			manager.addDemographicWithValidation(l, demo);
			fail("Did not throw an exception.");
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage().contains(expectedMessage));
		}
	}
}