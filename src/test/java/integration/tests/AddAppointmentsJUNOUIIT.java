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

import static integration.tests.AddPatientsIT.mom;
import static integration.tests.util.data.SiteTestCollection.siteNames;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysById;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionJUNOUI;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.seleniumUtil.ActionUtil;
import integration.tests.util.seleniumUtil.PageUtil;
import java.util.List;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddAppointmentsJUNOUIIT extends SeleniumTestBase
{
	static String patientFName = "Test";
	static String patientLName = "Test";
	static String patientName = patientLName + "," + patientFName;
	String patientNameJUNO = patientLName + ", " + patientFName;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "appointment", "appointment_status",
			"demographic", "log", "log_ws_rest", "mygroup", "program_provider", "property",
			"provider", "providerArchive", "provider_billing", "providerbillcenter",
			"ProviderPreference", "providersite", "secUserRole", "site", "rschedule",
			"scheduledate", "scheduletemplate", "scheduletemplatecode",
			"provider_recent_demographic_access"
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

	public void selectTimeSlot(String startTimeExpected)
	{
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("fc-bgevent")));
		List<WebElement> schedulesRight = driver.findElements(By.className("fc-bgevent"));
		List<WebElement> schedulesLeft = driver.findElement((By.className("fc-slats"))).findElements(By.tagName("tr"));
		for (int i = 0; i < schedulesLeft.size(); i++ )
		{
			String timeFrame = schedulesLeft.get(i).getAttribute("data-time");
			if (timeFrame.equals(startTimeExpected))
			{
				schedulesRight.get(i).click();
				break;
			}
		}
	}

	public void	addAppointmentWithNODemo(String startTimeExpected, String siteName, String apptStatus)
	{
		selectTimeSlot(startTimeExpected);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-event-appt-status"), apptStatus);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-site"), siteName);
		driver.findElement(By.xpath("//button[@type='submit']")).click();
	}

	public void addAppointmentWithDemo(String startTimeExpected, String patientFName, String apptStatus, String siteName)
	{
		selectTimeSlot(startTimeExpected);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-patient")));
		driver.findElement(By.id("input-patient")).findElement(By.tagName("input")).sendKeys(patientFName);

		By patientElement = By.xpath("//span[contains(., '" + patientFName + "')]");
		webDriverWait.until(ExpectedConditions.elementToBeClickable(patientElement));
		driver.findElement(patientElement).click();

		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-event-appt-status"), apptStatus);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-reason-code")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-reason-code"), "Follow-Up");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@id='input-site']/option[text()='" + siteName + "']")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("input-site"), siteName);
		findWaitSendKeysById(driver, webDriverWait, "input-notes", "Appointment Notes");
		findWaitSendKeysById(driver, webDriverWait, "input-event_reason", "Appointment Reason");
		findWaitClickByXpath(driver, webDriverWait, "//button[@type='submit']");
	}

	public void addSiteNAssignRole(String providerLName, String siteName)
	{
		String xpathProvider = "//td[contains(., '" + providerLName + "')]//following-sibling::" +
				"td[@class='provider-button-column flex-row justify-content-center']" +
				"//button[@title='Edit Provider']";
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, xpathProvider);

		//Scroll down to "Contact Information"
		JavascriptExecutor js = (JavascriptExecutor) driver;
		By siteAssignmentElement = By.xpath("//h6[contains(., 'Site Assignment')]");
		webDriverWait.until(ExpectedConditions.elementToBeClickable(siteAssignmentElement));
		WebElement Element = driver.findElement(siteAssignmentElement);
		js.executeScript("arguments[0].scrollIntoView();", Element);

		//Add site for Dr. Berry
		driver.findElement(By.id("name-siteSelection")).sendKeys("Test");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//a[@title='" + siteName +"']");
		driver.findElement(By.xpath("//button[@ng-click='$ctrl.addSiteAssignment($ctrl.currentSiteSelection.value)']")).click();

		//Assign role "doctor" to Dr. Berry.
		driver.findElement(By.id("name-access_roles")).sendKeys("doc");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//a[@title='doctor']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.addUserRole($ctrl.currentRoleSelection.value)']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.submit()']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.close()']");
	}

	@Test
	public void addAppointmentsSchedulePageTest()
	{
		// open JUNO UI page,
		accessSectionJUNOUI(driver, webDriverWait, "Schedule");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("schedule-select")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("schedule-select"), "oscardoc, doctor" );

		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		String startTimeExpected = "09:00:00";
		String apptStatusAt9 = "To Do";
		addAppointmentWithDemo(startTimeExpected, mom.firstName, apptStatusAt9, siteNames[0]);

		By statusElement = By.xpath("//i[@title='" + apptStatusAt9 + "']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(statusElement));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(statusElement, driver));

		//Add an appointment at 10:00-10:15 with NO demographic selected.
		String startTimeExpectedNoDemo = "10:00:00";
		String apptStatusAt10 = "Customized 1";
		addAppointmentWithNODemo(startTimeExpectedNoDemo, siteNames[0], apptStatusAt10);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@title='" + apptStatusAt10 + "']")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//i[@title='" + apptStatusAt10 + "']"), driver));
	}

	@Test
	public void addAppointmentsSchedulePageWeeklyViewTest()
			throws InterruptedException
	{
		// open JUNO UI page, Add site to Dr. Apple and Dr. Berry
		accessAdministrationSectionJUNOUI(driver, webDriverWait, "User Management", "Manage Users");
		addSiteNAssignRole("Apple", siteNames[0]);
		// open JUNO UI page,
		accessSectionJUNOUI(driver, webDriverWait, "Schedule");
		//Weekly View - next week
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(., 'Week')]")));
		driver.findElement(By.xpath("//button[contains(., 'Week')]")).click();
		driver.findElement(By.xpath("//button[@title='Next Day']")).click();

		// Add an appointment at 11:00-11:15 next Sunday with demographic selected.
		String startTimeExpected = "11:00:00";
		String apptStatusAt11 = "Customized 2";
		addAppointmentWithDemo(startTimeExpected, mom.firstName, apptStatusAt11, siteNames[0]);
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//i[@title='" + apptStatusAt11 + "']"), driver));

		//Add an appointment at 12:00-12:15 with NO demographic selected.
		String startTimeExpectedNoDemo = "12:00:00";
		String apptStatusAt12 = "To Do";
		addAppointmentWithNODemo(startTimeExpectedNoDemo, siteNames[0], apptStatusAt12);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@title='" + apptStatusAt12 + "']")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//i[@title='" + apptStatusAt12 + "']"), driver));
	}

	@Test
	public void addAppointmentsGroupViewTest() throws InterruptedException {
		// open JUNO UI page, Add site to Dr. Apple and Dr. Berry
		accessAdministrationSectionJUNOUI(driver, webDriverWait, "User Management", "Manage Users");
		addSiteNAssignRole("Apple", siteNames[0]);
		accessSectionJUNOUI(driver, webDriverWait, "Manage Users");
		addSiteNAssignRole("Berry", siteNames[0]);

		//Add Group
		String testGroup = "TestGroup";
		accessAdministrationSectionJUNOUI(driver, webDriverWait, "Schedule Management", "Add a Group"
		);
		AddGroupIT addGroupIT = new AddGroupIT();
		addGroupIT.addGroup(testGroup, 2);
		driver.switchTo().defaultContent();
		findWaitClick(driver, webDriverWait, By.linkText("Schedule"));

		//Schedule page
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("schedule-select")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("schedule-select"), testGroup);
		// Add an appointment at 13:00-13:15 next Sunday with demographic selected.
		String startTimeExpected = "13:00:00";
		String apptStatusAt13 = "Customized 4";
		addAppointmentWithDemo(startTimeExpected, mom.firstName, apptStatusAt13, siteNames[0]);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@title='" + apptStatusAt13 + "']")));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//i[@title='" + apptStatusAt13 + "']"), driver));

		//Add an appointment at 14:00-14:15 with NO demographic selected.
		String startTimeExpectedNoDemo = "14:00:00";
		String apptStatusAt14 = "Customized 5";
		addAppointmentWithNODemo(startTimeExpectedNoDemo, siteNames[0], apptStatusAt14);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//i[@title='" + apptStatusAt14 + "']")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//i[@title='" + apptStatusAt14 + "']"), driver));
	}
}
