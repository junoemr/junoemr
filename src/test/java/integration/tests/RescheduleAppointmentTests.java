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
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.util.Set;

import static integration.tests.AddAppointmentsTests.addAppointmentsSchedulePage;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

class AddAppointmentIntegrationTest
{
	static void addAppointmentTest(WebDriver driver, String time)
	{
		String currWindowHandle = driver.getWindowHandle();
		addAppointmentsSchedulePage(time, currWindowHandle);
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("Test,Test"), driver));
	}
}

public class RescheduleAppointmentTests extends SeleniumTestBase
{
	String statusExpectedTD = "To Do";
	String statusExpectedDP = "Daysheet Printed";
	String statusExpectedCusomized2 = "Customized 2";
	String statusExpectedCancelled = "Cancelled";
	static String patientFName = "Test";
	static String patientLName = "Test";
	static String patientName = patientLName + "," + patientFName;
	WebDriverWait wait = new WebDriverWait(driver, WEB_DRIVER_EXPLICIT_TIMEOUT);

	@BeforeClass
	public static void setup() throws Exception
	{
		loadSpringBeans();
		DatabaseUtil.createTestDemographic(patientFName, patientLName, "F");
		DatabaseUtil.createTestProvider();
		DatabaseUtil.createProviderSite();
	}

	@AfterClass
	public static void cleanup() throws Exception
	{
		SchemaUtils.restoreTable("admission", "appointment","appointmentArchive", "billingservice", "caisi_role",
				"demographic", "documentDescriptionTemplate", "Facility", "issue", "log", "log_ws_rest", "LookupList",
				"LookupListItem", "measurementType", "OscarJob", "OscarJobType",
				"provider", "provider_recent_demographic_access", "providerbillcenter", "ProviderPreference", "providersite",
				"secUserRole", "site", "tickler_text_suggest" );
	}

	public String apptStatusHoverOver()
	{
		wait.until(ExpectedConditions.elementToBeClickable(By.className("apptStatus")));
		WebElement statusButton = driver.findElement(By.className("apptStatus"));
		Actions builder = new Actions(driver);
		builder.clickAndHold().moveToElement(statusButton);
		builder.moveToElement(statusButton).build().perform();
		WebElement tollTip = statusButton.findElement(By.tagName("img"));
		builder.moveToElement(driver.findElement(By.className("apptLink"))).build().perform();
		String status = tollTip.getAttribute("title");
		return status;
	}

	@Test
	public void rescheduleAppointmentTestsClassicUI() throws InterruptedException
	{
		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		AddAppointmentIntegrationTests.addAppointmentTest(driver, "09:00");
		//Edit from "Edit An Appointment" page
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles);
		//Cut & Paste from 9:00 to 9:45
		driver.manage().window().maximize();
		driver.findElement(By.xpath("//input[@value='Cut']")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("09:45")));
		driver.findElement(By.linkText("09:45")).click();
		PageUtil.switchToLastWindow(driver);
		driver.manage().window().maximize();
		driver.findElement(By.id("pasteButton")).click();
		driver.findElement(By.id("addButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		String apptXpath = "//a[@title='9:45 AM - 10:00 AM']/../../td/a[contains(., '" + patientName +"')]";
		Assert.assertTrue("Appointment is NOT Cut/Paste to 9:45am successfully",
				PageUtil.isExistsBy(By.xpath(apptXpath), driver));
	}

	@Test
	public void rescheduleAppointmentTestsJUNOUI()
	{
		// Add an appointment at 10:00-10:15 with demographic selected for the day after tomorrow.
		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		AddAppointmentIntegrationTests.addAppointmentTest(driver, "10:00");
		accessSectionJUNOUI(driver, "Schedule");
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Next Day']")));
		driver.findElement(By.xpath("//button[@title='Next Day']")).click();
		driver.findElement(By.xpath("//button[@title='Next Day']")).click();
		Select providerDropDown = new Select(driver.findElement(By.id("schedule-select")));
		providerDropDown.selectByVisibleText("oscardoc, doctor");
		WebElement statusButton = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-starbill rotate']"));
		String statusTD = statusButton.getAttribute("title");
		Assert.assertEquals("Status is NOT To Do", statusExpectedTD, statusTD);

		//Edit by clicking the status button from Schedule page
		statusButton.click();
		String statusDP = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-todo rotate']"))
				.getAttribute("title");
		Assert.assertEquals("Status is NOT updated to Daysheet Printed Successfully", statusExpectedDP, statusDP);

		//Edit from "Modify Appointment" page
		driver.findElement(By.xpath("//span[contains(., 'Test, Test')]")).click();
		dropdownSelectByValue(driver, By.id("input-event-appt-status"), "C");//Cancelled
		driver.findElement(By.xpath("//button[contains(., 'Modify')]")).click();
		String statusCancelled = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-cancel rotate']"))
				.getAttribute("title");
		Assert.assertEquals("Status is NOT updated to Customized 2 Successfully", statusExpectedCancelled, statusCancelled);
	}
}
