package org.oscarehr.managers;

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
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoAsCurrentClassAndMethod();
	Demographic demo;

	@Before
	public void setupTest()
	{
		Provider provider = new Provider();
		provider.setProviderNo("999998"); // Admin Provider No.
		loggedInInfo = new LoggedInInfo();
		loggedInInfo.setLoggedInProvider(provider);

		demo = new Demographic();

		demo.setFirstName("Joe");
		demo.setLastName("Exotic");
		demo.setSex("M");
		demo.setYearOfBirth("1970");
		demo.setMonthOfBirth("01");
		demo.setDateOfBirth("01");
	}

	@Test
	public void addDemographicWithValidation_Pass()
	{
		try
		{
			manager.addDemographicWithValidation(loggedInInfo, demo);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
		List<Demographic> demos = manager.getDemographicWithLastFirstDOB(loggedInInfo,
				demo.getLastName(),
				demo.getFirstName(),
				demo.getYearOfBirth(),
				demo.getMonthOfBirth(),
				demo.getDateOfBirth());

		assertFalse(demos.isEmpty());
	}

	@Test
	public void addDemographicWithValidation_NullFirstName_Fail()
	{
		String expectedMessage = DemographicManager.FIRST_NAME_REQUIRED;
		demo.setFirstName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_EmptyFirstName_Fail()
	{
		String expectedMessage = DemographicManager.FIRST_NAME_REQUIRED;
		String badFirstName = "";
		demo.setFirstName(badFirstName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullLastName_Fail()
	{
		String expectedMessage = DemographicManager.LAST_NAME_REQUIRED;
		demo.setLastName(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_EmptyLastName_Fail()
	{
		String expectedMessage = DemographicManager.LAST_NAME_REQUIRED;
		String badLastName = "";
		demo.setLastName(badLastName);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullSex_Fail()
	{
		String expectedMessage = DemographicManager.SEX_REQUIRED;
		demo.setSex(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidSex_Fail()
	{
		String badSex = "badSex";
		demo.setSex(badSex);
		String expectedMessage = DemographicManager.SEX_INVALID;

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullYearOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_REQUIRED;
		demo.setYearOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidYearOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_NUMERIC;
		String badYear = "bad year";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_YearOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_4_DIGIT;
		String badYear = "999";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_YearOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.YEAR_OF_BIRTH_4_DIGIT;
		String badYear = "10000";
		demo.setYearOfBirth(badYear);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullMonthOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_REQUIRED;
		demo.setMonthOfBirth(null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidMonthOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "badMonth";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_MonthOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "0";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_MonthOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.MONTH_OF_BIRTH_INVALID;
		String badMonth = "13";
		demo.setMonthOfBirth(badMonth);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_NullDateOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_REQUIRED;
		demo.setDateOfBirth((String) null);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidDateOfBirth_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "badDate";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_DateOfBirthTooLow_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "0";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_DateOfBirthTooHigh_Fail()
	{
		String expectedMessage = DemographicManager.DATE_OF_BIRTH_INVALID;
		String badDate = "32";
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidBirthday_Fail()
	{
		String expectedMessage = DemographicManager.BIRTHDAY_INVALID;
		String badYear = "2001";
		String badMonth = "2";
		String badDate = "29";
		demo.setYearOfBirth(badYear);
		demo.setMonthOfBirth(badMonth);
		demo.setDateOfBirth(badDate);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void addDemographicWithValidation_InvalidFamilyDoctor_Fail()
	{
		String expectedMessage = DemographicManager.FAMILY_DOCTOR_INVALID;
		demo.setFamilyDoctor("Badly formmated");

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_HtmlTagInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "<text>Victoria</text>";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_QuotesInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "\"Victoria\"";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	@Test
	public void DemographicManagerTest_SemicolonInField_Fail()
	{
		String expectedMessage = DemographicManager.FIELD_UNSAFE;
		String badCity = "Victoria;";
		demo.setCity(badCity);

		actAndAssertExceptionMessage(expectedMessage);
	}

	private void actAndAssertExceptionMessage(String expectedMessage)
	{
		try
		{
			manager.addDemographicWithValidation(loggedInInfo, demo);
			fail("Did not throw an exception.");
		}
		catch (Exception e)
		{
			assertTrue(e.getMessage().contains(expectedMessage));
		}
	}
}