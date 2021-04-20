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

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;

import static integration.tests.AddProvidersTests.drBerry;
import static integration.tests.AddPatientsTests.mom;
import static integration.tests.EchartTests.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;

public class EditTicklersClassicUITests extends SeleniumTestBase
{
	@BeforeClass
	public static void setup()
	{
		loadSpringBeans();
		DatabaseUtil.createTestDemographic();
		DatabaseUtil.createTestProvider();
		DatabaseUtil.createProviderSite();
	}

	@AfterClass
	public static void cleanup()
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		SchemaUtils.restoreTable(
				"admission", "caisi_role", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
				"casemgmt_note_link", "demographic", "documentDescriptionTemplate", "Facility", "issue", "log",
				"LookupList", "LookupListItem", "measurementType", "OscarJob", "OscarJobType",
				"provider", "providerbillcenter", "ProviderPreference", "providersite", "secUserRole", "site",
				"tickler", "tickler_comments", "tickler_text_suggest", "validations"
		);
	}

	@Test
	public void editTicklerTest() throws InterruptedException
	{
		String priority = "High";
		String clinic = "Test Clinic";
		String reminderMessage = "This is test tickler message.";
		String suggestedText = "Re-Booked for followup";

		// *** Add Tickler Note ***
		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("menuTitletickler")));
		driver.findElement(By.id("menuTitletickler")).click();
		PageUtil.switchToLastWindow(driver);
		dropdownSelectByVisibleText(driver, By.xpath("//select[@name='priority']"), priority);
		dropdownSelectByVisibleText(driver, By.id("site"), clinic);
		driver.findElement(By.xpath("//textarea[@name='textarea']")).sendKeys(reminderMessage);
		driver.findElement(By.xpath("//input[@value='Submit & Write to Encounter']")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		Thread.sleep(1000);

		//*** Verify the update on Echart page ***
		Assert.assertTrue("Tickler note is NOT added Successfully.",
				PageUtil.isExistsBy(By.linkText(reminderMessage), driver));
		Assert.assertTrue("Tickler note is NOT added to Encounter Successfully.",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + reminderMessage + "')]"), driver));

		//*** Verify the update on Tickler Management page. ***
		driver.findElement(By.linkText("Tickler")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Tickler note is Not added Successfully.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., '" + mom.lastName + "')]"), driver));
		Assert.assertTrue("Tickler priority is NOT set Successfully.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., '" + priority + "')]"), driver));

		// *** Edit Tickler notes ***
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.findElement(By.linkText(reminderMessage)).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.linkText("Edit")).click();
		PageUtil.switchToLastWindow(driver);
		dropdownSelectByVisibleText(driver, By.xpath("//select[@name='status']"), "Complete");
		dropdownSelectByVisibleText(driver, By.xpath("//select[@name='assignedToProviders']"),
				drBerry.lastName + " ," + drBerry.firstName);
		dropdownSelectByVisibleText(driver, By.xpath("//select[@name='suggestedText']"), suggestedText);
		driver.findElement(By.xpath("//input[@name='pasteMessage']")).click();
		driver.findElement(By.xpath("//input[@name='updateTicklerAndSaveEncounter']")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(1000);

		//*** Verify the update on Echart page ***
		Assert.assertTrue("Suggested Text is NOT updated to Encounter Successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + suggestedText + "')]"), driver));
		Assert.assertFalse("Tickler is Not completed successfully.",
				PageUtil.isExistsBy(By.linkText(reminderMessage), driver));

		//*** Verify the update on Tickler Management page. ***
		driver.findElement(By.linkText("Tickler")).click();
		PageUtil.switchToLastWindow(driver);
		dropdownSelectByVisibleText(driver, By.id("ticklerview"), "Completed");
		driver.findElement(By.xpath("//input[@value='Create Report']")).click();
		Assert.assertTrue("Provider is Not updated Successfully.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., '" + drBerry.lastName + "')]"), driver));
		Assert.assertTrue("Suggested Text is NOT updated Successfully.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., '" + suggestedText + "')]"), driver));
		Assert.assertTrue("Status is Not updated as Complete.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., 'Complete')]"), driver));

		//*** Delete the Tickler Note. ***
		driver.findElement(By.xpath("//input[@name='checkbox']")).click();
		driver.findElement(By.xpath("//input[@value='Delete']")).click();
		dropdownSelectByVisibleText(driver, By.id("ticklerview"), "Deleted");
		driver.findElement(By.xpath("//input[@value='Create Report']")).click();

		//*** Verify the update on Tickler Management page. ***
		Assert.assertTrue("Tickler is NOT deleted Successfully.",
				PageUtil.isExistsBy(By.xpath("//td[contains(., '" + reminderMessage + "')]"), driver));

		//*** Verify the update on Echart page ***
		PageUtil.switchToWindow(currWindowHandle, driver);
		Assert.assertFalse("Tickler note is NOT deleted Successfully.",
				PageUtil.isExistsBy(By.linkText(reminderMessage), driver));
	}
}
