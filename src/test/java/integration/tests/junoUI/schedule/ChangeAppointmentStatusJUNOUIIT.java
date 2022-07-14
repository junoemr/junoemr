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

package integration.tests.junoUI.schedule;

import static integration.tests.classicUI.search.AddPatientsClassicUIIT.mom;
import static integration.tests.classicUI.search.AddPatientsClassicUIIT.momFullNameJUNO;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

import integration.tests.classicUI.schedule.AddAppointmentsClassicUIIT;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.AppointmentUtil;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangeAppointmentStatusJUNOUIIT extends SeleniumTestBase
{
	String statusExpectedTD = "To Do";
	String statusExpectedDP = "Daysheet Printed";
	String statusExpectedCusomized2 = "Customized 2";
	String statusExpectedCancelled = "Cancelled";

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

	@Test
	public void changeAppointmentStatusTestsJUNOUI()
	{
		// Add an appointment at 10:00-10:15 with demographic selected for the day after tomorrow.
		String viewNextDaySelector = "//img[@alt='View Next DAY']";
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(viewNextDaySelector)));
		driver.findElement(By.xpath(viewNextDaySelector)).click();

		String currWindowHandle = driver.getWindowHandle();
		AddAppointmentsClassicUIIT addAppointmentsTests = new AddAppointmentsClassicUIIT();
		addAppointmentsTests.addAppointmentsSchedulePage("10:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		accessSectionJUNOUI(driver, webDriverWait, "Schedule");

		AppointmentUtil.skipTwoDaysJUNOUI(driver, webDriverWait);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("schedule-select"), "oscardoc, doctor");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@class='icon icon-status onclick-event-status icon-starbill rotate']")));
		WebElement statusButton = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-starbill rotate']"));
		String statusTD = statusButton.getAttribute("title");
		Assert.assertEquals("Status is NOT To Do", statusExpectedTD, statusTD);

		//Edit by clicking the status button from Schedule page
		statusButton.click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@class='icon icon-status onclick-event-status icon-todo rotate']")));
		String statusDP = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-todo rotate']"))
				.getAttribute("title");
		Assert.assertEquals("JUNO UI: Status is NOT updated to Daysheet Printed Successfully", statusExpectedDP, statusDP);

		//Edit from "Modify Appointment" page
		driver.findElement(By.xpath("//span[contains(., '" + momFullNameJUNO + "')]")).click();
		dropdownSelectByValue(driver, webDriverWait, By.id("input-event-appt-status"), "C");//Cancelled
		driver.findElement(By.xpath("//button[contains(., 'Modify')]")).click();
		String statusCancelled = driver.findElement(By.xpath("//i[@class='icon icon-status onclick-event-status icon-cancel rotate']"))
				.getAttribute("title");
		Assert.assertEquals("JUNO UI: Status is NOT updated to Customized 2 Successfully", statusExpectedCancelled, statusCancelled);
	}
}