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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

public class AddAppointmentsTests extends SeleniumTestBase {
	@BeforeClass
	public static void setup() throws Exception {
		SchemaUtils.restoreTable("admission", "appointment", "log", "program_provider", "property",
				"provider", "providerArchive", "provider_billing", "providerbillcenter", "ProviderPreference", "providersite", "secUserRole", "site");
		loadSpringBeans();

		DatabaseUtil.createTestDemographic();
		DatabaseUtil.createTestProvider();
		DatabaseUtil.createProviderSite();
	}

	@Test
	public void addAppointmentsTest() throws Exception {
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}

		// Add an appointment at 9:00-9:15 with demographic selected.
		String currWindowHandle = driver.getWindowHandle();
		driver.findElement(By.linkText("09:00")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("searchBtn")).click();
		driver.findElement(By.xpath("/html/body/div[2]/form/table/tbody/tr[2]/td[1]/input")).click();
		dropdownSelectByValue(driver, By.xpath("//select[@name='reasonCode']"), "4");//Follow-Up
		driver.findElement(By.id("reason")).sendKeys("Appointment Reason.");
		dropdownSelectByValue(driver, By.xpath("//select[@name='status']"), "t");//To Do
		driver.findElement(By.xpath("//input[@name='type']")).sendKeys("Appointment Type");
		driver.findElement(By.xpath("//textarea[@name='notes']")).sendKeys("Appointment Notes");
		driver.findElement(By.xpath("//input[@name='resources']")).sendKeys("Appointment Resources");
		driver.findElement(By.xpath("//input[@value='critical']")).click();
		driver.findElement(By.id("addButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		WebDriverWait wait = new WebDriverWait(driver,30);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Test,Test")));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("Test,Test"), driver));

		//Add an appointment at 10:00-10:15 with NO demographic selected.
		driver.findElement(By.linkText("10:00")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("addButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(".")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("."), driver));
	}
}
