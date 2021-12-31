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
import integration.tests.util.seleniumUtil.ActionUtil;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.JunoApplication;

import java.util.Set;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.AddPatientsIT.dad;
import static integration.tests.AddPatientsIT.dadFullName;
import static integration.tests.AddPatientsIT.mom;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditAppointmentsIT extends SeleniumTestBase
{
	String apptDurationUpdated = "30";
	String nameUpdated = dadFullName;
	String reasonCodeUpdated = "Counselling";
	String reasonUpdated = "Appointment Reason Updated";
	String typeUpdated = "Select an Appointment Type";
	String notesUpdated = "Appointment Notes Updated";
	String resourcesUpdated = "Appointment Resources Updated";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "appointment","appointmentArchive", "caisi_role",  "demographic",
			"documentDescriptionTemplate", "issue", "log", "log_ws_rest", "LookupList", "LookupListItem",
			"measurementType", "mygroup", "OscarJob", "OscarJobType", "program_provider", "property", "provider",
			"provider_billing", "providerArchive", "providerbillcenter", "ProviderPreference", "providersite",
			"rschedule", "secUserRole", "scheduledate", "scheduletemplate", "scheduletemplatecode", "site",
			"tickler_text_suggest", "provider_recent_demographic_access"
		};
	}

	@Before
	public void setup() throws Exception
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
		databaseUtil.createProviderSite();
	}

	public String getDropdownValue(By dropdownBy)
	{
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(dropdownBy));
		Select dropdown = new Select(driver.findElement(dropdownBy));
		WebElement option = dropdown.getFirstSelectedOption();
		return option.getText();
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.EditAppointmentsIT
-------------------------------------------------------------------------------
Tests run: 2, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 33.489 s <<< FAILURE! - in integration.tests.EditAppointmentsIT
editAppointmentTestsClassicUI  Time elapsed: 12.743 s  <<< ERROR!
org.openqa.selenium.NoSuchElementException:
Unable to locate element: .//td[contains(., 'Momfname')]
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2353279, moz:profile: /tmp/rust_mozprofile2qebiT, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 98044904-ce86-40b1-bb52-a4f1942d6de7
*** Element info: {Using=xpath, value=.//td[contains(., 'Momfname')]}
    at integration.tests.EditAppointmentsIT.editAppointmentTestsClassicUI(EditAppointmentsIT.java:98)
	 */
	@Ignore
	@Test
	public void editAppointmentTestsClassicUI()
			throws InterruptedException
	{
		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		String currWindowHandle = driver.getWindowHandle();
		AddAppointmentsIT addAppointmentsTests = new AddAppointmentsIT();
		addAppointmentsTests.addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Edit from "Edit An Appointment" page
		Set<String> oldWindowHandles = driver.getWindowHandles();
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles,
			webDriverWait);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit']")));
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		driver.findElement(By.xpath("//input[@name='keyword']")).clear();
		driver.findElement(By.xpath("//input[@value='Search']")).click();
		driver.findElement(By.xpath(".//td[contains(., '" + dad.firstName + "')]")).click();
		driver.findElement(By.xpath("//input[@name='duration']")).clear();
		driver.findElement(By.xpath("//input[@name='duration']")).sendKeys(apptDurationUpdated);
		dropdownSelectByVisibleText(driver, webDriverWait, By.xpath("//select[@name='reasonCode']"), reasonCodeUpdated);
		driver.findElement(By.id("reason")).clear();
		driver.findElement(By.id("reason")).sendKeys(reasonUpdated);
		driver.findElement(By.xpath("//input[@name='type']")).clear();
		driver.findElement(By.xpath("//input[@name='type']")).sendKeys(typeUpdated);
		driver.findElement(By.xpath("//textarea[@name='notes']")).clear();
		driver.findElement(By.xpath("//textarea[@name='notes']")).sendKeys(notesUpdated);
		driver.findElement(By.xpath("//input[@name='resources']")).clear();
		driver.findElement(By.xpath("//input[@name='resources']")).sendKeys(resourcesUpdated);
		driver.findElement(By.xpath("//input[@value='critical']")).click();
		driver.findElement(By.id("updateButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Assert.assertTrue("Patient is NOT updated successfully.", PageUtil.isExistsBy(By.partialLinkText(dad.lastName), driver));

		driver.findElement(By.partialLinkText(dad.lastName)).click();
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles,
			webDriverWait);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit']")));
		String name = driver.findElement(By.xpath("//input[@name='keyword']")).getAttribute("value");
		String apptDuration = driver.findElement(By.xpath("//input[@name='duration']")).getAttribute("value");
		String reasonCode = getDropdownValue(By.xpath("//select[@name='reasonCode']"));
		String reason = driver.findElement(By.id("reason")).getText();
		String type = driver.findElement(By.xpath("//input[@name='type']")).getAttribute("value");
		String notes = driver.findElement(By.xpath("//textarea[@name='notes']")).getText();
		String resources = driver.findElement(By.xpath("//input[@name='resources']")).getAttribute("value");
		boolean critialStatus = driver.findElement(By.xpath("//input[@value='critical']")).isSelected();

		Assert.assertEquals("Patient name is NOT updated successfully.", nameUpdated, name);
		Assert.assertEquals("Duration is NOT updated successfully.", apptDurationUpdated, apptDuration);
		Assert.assertEquals("ReasonCode is NOT updated successfully.", reasonCodeUpdated, reasonCode);
		Assert.assertEquals("Reason is NOT updated successfully.", reasonUpdated, reason);
		Assert.assertEquals("Type is NOT updated successfully.", typeUpdated, type);
		Assert.assertEquals("Notes are NOT udpated successfully.", notesUpdated, notes);
		Assert.assertEquals("Resources are NOT updated successfully.", resourcesUpdated, resources);
		Assert.assertFalse("Critical status is NOT updated successfully", critialStatus);
	}

	@Test
	public void changeAppointmentStatusTestsJUNOUI() throws InterruptedException {
		// Add an appointment at 10:00-10:15 with demographic selected for the day after tomorrow.
		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		String currWindowHandle = driver.getWindowHandle();
		AddAppointmentsIT addAppointmentsTests = new AddAppointmentsIT();
		addAppointmentsTests.addAppointmentsSchedulePage("10:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		accessSectionJUNOUI(driver, webDriverWait, "Schedule");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@title='Next Day']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@title='Next Day']");
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("schedule-select"), "oscardoc, doctor");

		//Edit from "Modify Appointment" page
		findWaitClick(driver, webDriverWait, By.xpath("//span[contains(., '" + mom.firstName + "')]"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("input")));
		driver.findElement(By.id("input-patient")).findElement(By.tagName("input")).clear();
		driver.findElement(By.id("input-patient")).findElement(By.tagName("input")).sendKeys(dad.firstName);

		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//span[contains(., '" + dad.firstName + "')]");

		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-type"), typeUpdated);
		driver.findElement(By.id("input-duration")).clear();
		driver.findElement(By.id("input-duration")).sendKeys(apptDurationUpdated);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-reason-code")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-reason-code"), reasonCodeUpdated);
		driver.findElement(By.id("input-notes")).clear();
		driver.findElement(By.id("input-notes")).sendKeys(notesUpdated);
		driver.findElement(By.id("input-event_reason")).clear();
		driver.findElement(By.id("input-event_reason")).sendKeys(reasonUpdated);
		//driver.findElement(By.xpath("//label[@class='form-control checkmark']")).click();
		driver.findElement(By.xpath("//label[contains(., 'Critical')]")).click();
		driver.findElement(By.xpath("//button[contains(., 'Modify')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(dad.lastName)));
		Assert.assertTrue("Patient is NOT updated successfully.",
				PageUtil.isExistsBy(By.partialLinkText(dad.lastName), driver));

		driver.findElement(By.xpath("//span[contains(., '" + dad.firstName + "')]")).click();
		String type = getDropdownValue(By.id("input-type"));
		String apptDuration = driver.findElement(By.id("input-duration")).getAttribute("value");
		String reasonCode = getDropdownValue(By.id("input-reason-code"));
		String notes = driver.findElement(By.id("input-notes")).getAttribute("value");
		String reason = driver.findElement(By.id("input-event_reason")).getAttribute("value");
		boolean critialStatus = driver.findElement(By.xpath("//label[contains(., 'Critical')]")).isSelected();

		Assert.assertEquals("Type is NOT updated successfully.", typeUpdated, type);
		Assert.assertEquals("Duration is NOT updated successfully.", apptDurationUpdated, apptDuration);
		Assert.assertEquals("ReasonCode is NOT updated successfully.", reasonCodeUpdated, reasonCode);
		Assert.assertEquals("Notes are NOT udpated successfully.", notesUpdated, notes);
		Assert.assertEquals("Reason is NOT updated successfully.", reasonUpdated, reason);
		Assert.assertFalse("Critical status is NOT updated successfully", critialStatus);
	}
}
