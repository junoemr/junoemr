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

package integration.tests;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.ActionUtil;
import integration.tests.util.seleniumUtil.PageUtil;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddAllergiesClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "allergies", "demographic", "log", "log_ws_rest", "measurementType",
			"partial_date", "validations", "property"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	// Ignored because it can't connect to drugref
	@Ignore
	@Test
	public void addAllergiesClassicUITest() throws InterruptedException
	{
		String allergyNameQuickButton = "Penicillin";
		String allergyNameSearch = "AMINOBENZOIC ACID";
		String allergySearchDescription = "AMINOBENZOIC ACID";
		String allergyNameCustom = "Egg white";
		String reaction = "rash";
		String startDate = "1981-01";
		String ageOfOnset = "1";
		String lifeStage = "Infant: 29 days to 2 years";
		String severity = "Moderate";
		String onset = "Gradual";

		String eChartPage = "eChartPage:";
		String allergiesPage = "Allergies Page";

		String errorMessageSearch = " is NOT added to Allergies by search successfully.";
		String errorMessageQuickButton = " is NOT added to Allergies by quick button successfully.";
		String errorMessageCustomized = " is NOT added to Customized Allergies successfully.";

		List<String> pageTypes = Arrays.asList(eChartPage, allergiesPage);
		List<String> allergyNames = Arrays.asList(allergyNameSearch, allergyNameQuickButton, allergyNameCustom);
		List<String> errorMessages = Arrays.asList(errorMessageSearch, errorMessageQuickButton, errorMessageCustomized);

		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText("Allergies")));
		String eChartWindowHandle = driver.getWindowHandle();
		driver.findElement(By.linkText("Allergies")).click();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		String allergiesWindowHandle = driver.getWindowHandle();
		Map<String, String> pageHandleMap = new HashMap<String, String>();
		pageHandleMap.put(eChartPage, eChartWindowHandle);
		pageHandleMap.put(allergiesPage, allergiesWindowHandle);
		PageUtil.switchToLastWindow(driver);

		//** Add allergies from quick button. **
		driver.findElement(By.xpath("//button[contains(., '" + allergyNameQuickButton + "')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@name='reactionDescription']")));
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		// ** Add allergies from search. **
		driver.findElement(By.id("typeSelectAll")).click();
		driver.findElement(By.id("searchString")).sendKeys(allergyNameSearch);
		driver.findElement(By.id("searchStringButton")).click();
		driver.findElement(By.linkText(allergySearchDescription)).click();
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		//** Add customised allergy. **
		driver.findElement(By.id("searchString")).sendKeys(allergyNameCustom);
		driver.findElement(By.xpath("//button[@value='Custom Allergy']")).click();
		driver.switchTo().alert().accept();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		HashMap<String, Boolean> assertionTestData = new HashMap<String, Boolean>();
		for (String pageType : pageTypes)
		{
			if (!driver.getWindowHandle().equals(pageHandleMap.get(pageType)))
			{
				PageUtil.switchToWindow(pageHandleMap.get(pageType), driver);
				driver.navigate().refresh();
				driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
			}
			for (int i = 0; i < allergyNames.size(); i++)
			{
				assertionTestData.put(createErrorMessageString(pageType, allergyNames.get(i), errorMessages.get(i)),
					PageUtil.isExistsBy(By.partialLinkText(allergyNames.get(i).toUpperCase()), driver));
			}
		}
		assertAllValuesTrue(assertionTestData);

		//** Modify Penicillins Allergy**
		String reaction_modified = "rash updated";
		String startDate_modified = "1983-01-01";
		String ageOfOnset_modified = "2";
		String lifeStage_modified = "Child: 2 years to 15 years";
		String severity_modified = "Mild";
		String onset_modified = "Slow";
		PageUtil.switchToLastWindow(driver);

		String modifyButtonEggWhiteXpath = "//table[@class='allergy_table']/descendant::td[contains(., '" + allergyNameQuickButton.toUpperCase() + "')]" +
			"/parent::tr/descendant::a[contains(., 'Modify')]";
		driver.findElement(By.xpath(modifyButtonEggWhiteXpath)).click();
		addAllergyDetails(reaction_modified, startDate_modified, ageOfOnset_modified, lifeStage_modified, severity_modified, onset_modified);

		//** Verify the updated content from Allergies page **
		String allergyInfoXpath = "//table[@class='allergy_table']/descendant::td[contains(., '" + allergyNameQuickButton.toUpperCase() + "')]/parent::tr";
		String allergyInfo = driver.findElement(By.xpath(allergyInfoXpath)).getText();
		Boolean reactionStatus = allergyInfo.contains(reaction_modified);
		Boolean startDateStatus = allergyInfo.contains(startDate_modified);
		Boolean ageOfOnsetStatus = allergyInfo.contains(ageOfOnset_modified);
		Boolean lifeStageStatus = allergyInfo.contains("Child");
		Boolean severityStatus = allergyInfo.contains(severity_modified);
		Boolean onsetStatus = allergyInfo.contains(onset_modified);
		Assert.assertTrue("Allergies Page: Allergy information for " + allergyNameQuickButton + " is NOT modified successfully",
			reactionStatus && startDateStatus && ageOfOnsetStatus && lifeStageStatus && severityStatus && onsetStatus );

		//** Inactivate Egg White Allergy **
		String inactivateButtonEggWhite = "//table[@class='allergy_table']/descendant::td[contains(., '" + allergyNameCustom.toUpperCase() + "')]" +
			"/parent::tr/descendant::a[contains(., 'Inactivate')]";
		driver.findElement(By.xpath(inactivateButtonEggWhite)).click();
		driver.switchTo().alert().accept();
		driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);

		//** Verify on Allergies page. **
		Assert.assertFalse("Allergies Page: " + allergyNameCustom + " is NOT inactivated successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + allergyNameCustom.toUpperCase() + "')]"), driver));

		//** Verify on Allergies section on eChart page. **
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		Assert.assertFalse("eChart Page: " + allergyNameCustom + " is NOT inactivated successfully.",
			PageUtil.isExistsBy(By.linkText(allergyNameCustom.toUpperCase()), driver));
	}

	protected void addAllergyDetails(String reaction, String startDate, String ageOfOnset, String lifeStage, String severity, String onset)
	{
		textEdit(driver, By.xpath("//textarea[@name='reactionDescription']"), reaction);
		textEdit(driver, By.id("startDate"), startDate);
		textEdit(driver, By.id("ageOfOnset"), ageOfOnset);
		ActionUtil.dropdownSelectByVisibleText(driver, By.id("lifeStage"), lifeStage);
		ActionUtil.dropdownSelectByVisibleText(driver, By.name("severityOfReaction"), severity);
		ActionUtil.dropdownSelectByVisibleText(driver, By.name("onSetOfReaction"), onset);
		driver.findElement(By.xpath("//input[@type='submit']")).click();
	}

	private void assertAllValuesTrue(HashMap<String, Boolean> map)
	{
		for (Map.Entry<String, Boolean> item : map.entrySet())
		{
			Assert.assertTrue(item.getKey(), item.getValue());
		}
	}

	private String createErrorMessageString(String pageType, String testItem, String errorMessage)
	{
		return pageType + " page: " + testItem + " " + errorMessage ;
	}
}
