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

package integration.tests.classicUI;

import static integration.tests.classicUI.AddPatientsClassicUIIT.mom;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.seleniumUtil.PageUtil;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangeAppointmentStatusClassicUIIT extends SeleniumTestBase
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

	public static String apptStatusHoverOver()
	{
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("apptStatus")));
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
	public void changeAppointmentStatusTestsClassicUI()
			throws InterruptedException
	{
		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		String currWindowHandle = driver.getWindowHandle();
		AddAppointmentsClassicUIIT addAppointmentsTests = new AddAppointmentsClassicUIIT();
		addAppointmentsTests.addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		WebElement statusButton = driver.findElement(By.className("apptStatus"));
		String statusTD = apptStatusHoverOver();
		Assert.assertEquals("Status is NOT To Do", statusExpectedTD, statusTD);

		//Edit by clicking the status button from Schedule page
		statusButton.click();
		Thread.sleep(2000);//wait for clicking to change the status.
		driver.navigate().refresh();
		String statusDP = apptStatusHoverOver();
		Assert.assertEquals("Classic UI: Status is NOT updated to Daysheet Printed Successfully", statusExpectedDP, statusDP);

		//Edit from "Edit An Appointment" page
		Set<String> oldWindowHandles = driver.getWindowHandles();
		PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles,
			webDriverWait);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='status']"), "b");//Customized 2
		driver.findElement(By.id("updateButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		String statusCus2 = apptStatusHoverOver();
		Assert.assertEquals("Classic UI: Status is NOT updated to Customized 2 Successfully", statusExpectedCusomized2, statusCus2);
	}
}