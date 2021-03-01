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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

import static integration.tests.AddPatientsTests.dad;
import static integration.tests.AddPatientsTests.dadFullName;
import static integration.tests.AddPatientsTests.mom;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

public class EditAppointmentsTests extends SeleniumTestBase
{
	@Autowired
	DatabaseUtil databaseUtil;

	String apptDurationUpdated = "30";
	String nameUpdated = dadFullName;
	String reasonCodeUpdated = "Counselling";
	String reasonUpdated = "Appointment Reason Updated";
	String typeUpdated = "Select an Appointment Type";
	String notesUpdated = "Appointment Notes Updated";
	String resourcesUpdated = "Appointment Resources Updated";

	@Before
	public void setup() throws Exception
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
		databaseUtil.createProviderSite();
	}

	@After
	public void cleanup() throws Exception
	{
		SchemaUtils.restoreTable("admission", "appointment","appointmentArchive", "caisi_role",  "demographic",
				"documentDescriptionTemplate", "issue", "log", "log_ws_rest", "LookupList", "LookupListItem",
				"measurementType", "mygroup", "OscarJob", "OscarJobType", "program_provider", "property", "provider",
				"provider_billing", "providerArchive", "providerbillcenter", "ProviderPreference", "providersite",
				"rschedule", "secUserRole", "scheduledate", "scheduletemplate", "scheduletemplatecode", "site",
				"tickler_text_suggest" );
	}

	public String getDropdownValue(By dropdownBy)
	{
		Select dropdown = new Select(driver.findElement(dropdownBy));
		WebElement option = dropdown.getFirstSelectedOption();
		return option.getText();
	}

	@Test
	public void editAppointmentTestsClassicUI() throws InterruptedException
	{
		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		String currWindowHandle = driver.getWindowHandle();
		AddAppointmentsTests addAppointmentsTests = new AddAppointmentsTests();
		addAppointmentsTests.addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Edit from "Edit An Appointment" page
		Set<String> oldWindowHandles = driver.getWindowHandles();
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@type='submit']")));
		driver.findElement(By.xpath("//input[@type='submit']")).click();
		driver.findElement(By.xpath("//input[@name='keyword']")).clear();
		driver.findElement(By.xpath("//input[@value='Search']")).click();
		driver.findElement(By.xpath(".//td[contains(., '" + dad.firstName + "')]")).click();
		driver.findElement(By.xpath("//input[@name='duration']")).clear();
		driver.findElement(By.xpath("//input[@name='duration']")).sendKeys(apptDurationUpdated);
		dropdownSelectByVisibleText(driver, By.xpath("//select[@name='reasonCode']"), reasonCodeUpdated);
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
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles);
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
		AddAppointmentsTests addAppointmentsTests = new AddAppointmentsTests();
		addAppointmentsTests.addAppointmentsSchedulePage("10:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		accessSectionJUNOUI(driver, "Schedule");
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Next Day']")));
		driver.findElement(By.xpath("//button[@title='Next Day']")).click();
		driver.findElement(By.xpath("//button[@title='Next Day']")).click();
		Select providerDropDown = new Select(driver.findElement(By.id("schedule-select")));
		providerDropDown.selectByVisibleText("oscardoc, doctor");

		//Edit from "Modify Appointment" page
		driver.findElement(By.xpath("//span[contains(., '" + mom.firstName + "')]")).click();
		driver.findElement(By.id("input-patient")).findElement(By.tagName("input")).clear();
		driver.findElement(By.id("input-patient")).findElement(By.tagName("input")).sendKeys(dad.firstName);
		driver.findElement(By.xpath("//span[contains(., '" + dad.firstName + "')]")).click();
		dropdownSelectByVisibleText(driver, By.id("input-type"), typeUpdated);
		driver.findElement(By.id("input-duration")).clear();
		driver.findElement(By.id("input-duration")).sendKeys(apptDurationUpdated);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-reason-code")));
		dropdownSelectByVisibleText(driver, By.id("input-reason-code"), reasonCodeUpdated);
		driver.findElement(By.id("input-notes")).clear();
		driver.findElement(By.id("input-notes")).sendKeys(notesUpdated);
		driver.findElement(By.id("input-event_reason")).clear();
		driver.findElement(By.id("input-event_reason")).sendKeys(reasonUpdated);
		driver.findElement(By.xpath("//label[@class='form-control checkmark']")).click();
		driver.findElement(By.xpath("//button[contains(., 'Modify')]")).click();
		Assert.assertTrue("Patient is NOT updated successfully.",
				PageUtil.isExistsBy(By.partialLinkText(dad.lastName), driver));

		driver.findElement(By.xpath("//span[contains(., '" + dad.firstName + "')]")).click();
		String type = getDropdownValue(By.id("input-type"));
		String apptDuration = driver.findElement(By.id("input-duration")).getAttribute("value");
		String reasonCode = getDropdownValue(By.id("input-reason-code"));
		String notes = driver.findElement(By.id("input-notes")).getAttribute("value");
		String reason = driver.findElement(By.id("input-event_reason")).getAttribute("value");
		boolean critialStatus = driver.findElement(By.xpath("//label[@class='form-control checkmark']")).isSelected();

		Assert.assertEquals("Type is NOT updated successfully.", typeUpdated, type);
		Assert.assertEquals("Duration is NOT updated successfully.", apptDurationUpdated, apptDuration);
		Assert.assertEquals("ReasonCode is NOT updated successfully.", reasonCodeUpdated, reasonCode);
		Assert.assertEquals("Notes are NOT udpated successfully.", notesUpdated, notes);
		Assert.assertEquals("Reason is NOT updated successfully.", reasonUpdated, reason);
		Assert.assertFalse("Critical status is NOT updated successfully", critialStatus);
	}
}
