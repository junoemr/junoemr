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

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.ActionUtil;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.sql.SQLException;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddAllergiesClassicUIIT extends SeleniumTestBase
{
	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@After
	public void cleanup()
		throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable(
			"admission", "allergies", "demographic", "log", "log_ws_rest", "measurementType", "partial_date", "validations"
		);
	}

	@Test
	public void addAllergiesClassicUITest()
		throws InterruptedException
	{
		String allergyNameQuickButton = "Penicillin";
		String allergyNameSearch = "CHILDREN'S TYLENOL 160MG";
		String allergySearchDescription = "TYLENOL CHILDRENS ELIXIR 160MG/5ML";
		String allergyNameCustom = "Egg white";
		String reaction = "rash";
		String startDate = "1981-01";
		String ageOfOnset = "1";
		String lifeStage = "Infant: 29 days to 2 years";
		String severity = "Moderate";
		String onset = "Gradual";

		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		Thread.sleep(5000);
		String eChartWindowHandle = driver.getWindowHandle();
		driver.findElement(By.linkText("Allergies")).click();
		Thread.sleep(5000);
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		//** Add allergies from quick button. **
		driver.findElement(By.xpath("//button[contains(., '" + allergyNameQuickButton + "')]")).click();
		Thread.sleep(1000);
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		// ** Add allergies from search. **
		driver.findElement(By.id("typeSelectAll")).click();
		driver.findElement(By.id("searchString")).sendKeys(allergyNameSearch);
		Thread.sleep(1000);
		driver.findElement(By.id("searchStringButton")).click();
		Thread.sleep(1000);
		driver.findElement(By.linkText(allergySearchDescription)).click();
		Thread.sleep(1000);
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		//** Add customised allergy. **
		driver.findElement(By.id("searchString")).sendKeys(allergyNameCustom);
		driver.findElement(By.xpath("//button[@value='Custom Allergy']")).click();
		driver.switchTo().alert().accept();
		Thread.sleep(1000);
		addAllergyDetails(reaction, startDate, ageOfOnset, lifeStage, severity, onset);

		//** Verify on Allergies page. **
		Assert.assertTrue("Allergies Page: " + allergyNameSearch + " is NOT added to Allergies by search successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + allergySearchDescription + "')]"), driver));
		Assert.assertTrue("Allergies Page: " + allergyNameQuickButton + " is NOT added to Allergies by quick button successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + allergyNameQuickButton.toUpperCase() + "')]"), driver));
		Assert.assertTrue("Allergies Page: " + allergyNameCustom + " is NOT added to Customized Allergies successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + allergyNameCustom.toUpperCase() + "')]"), driver));

		//** Verify on Allergies section on eChart page. **
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(2000);
		Assert.assertTrue("eChart Page: " + allergyNameSearch + " is NOT added to Allergies by search successfully.",
			PageUtil.isExistsBy(By.linkText(allergySearchDescription), driver));
		Assert.assertTrue("eChart Page: " + allergyNameQuickButton + " is NOT added to Allergies by quick button successfully.",
			PageUtil.isExistsBy(By.partialLinkText(allergyNameQuickButton.toUpperCase()), driver));
		Assert.assertTrue("eChart Page: " + allergyNameCustom + " is NOT added to Customized Allergies successfully.",
			PageUtil.isExistsBy(By.linkText(allergyNameCustom.toUpperCase()), driver));

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
		Thread.sleep(1000);
		driver.switchTo().alert().accept();
		Thread.sleep(1000);

		//** Verify on Allergies page. **
		Assert.assertFalse("Allergies Page: " + allergyNameCustom + " is NOT inactivated successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + allergyNameCustom.toUpperCase() + "')]"), driver));

		//** Verify on Allergies section on eChart page. **
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(2000);
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

}
