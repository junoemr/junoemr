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
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.AddGroupIT.valueOfDrApple;
import static integration.tests.AddGroupIT.valueOfDrBerry;
import static integration.tests.AddPatientsIT.mom;
import static integration.tests.AddProvidersIT.drApple;
import static integration.tests.AddProvidersIT.drBerry;
import static integration.tests.ScheduleSettingIT.getDailySchedule;
import static integration.tests.ScheduleSettingIT.setupSchedule;
import static integration.tests.ScheduleSettingIT.setupTemplate;
import static integration.tests.ScheduleSettingIT.templateTitleGeneral;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddAppointmentsIT extends SeleniumTestBase
{
	@Before
	public void setup() throws Exception
	{
		SchemaUtils.restoreTable("admission", "appointment", "demographic", "log", "log_ws_rest", "mygroup",
				"program_provider", "property",	"provider", "providerArchive", "provider_billing", "providerbillcenter",
				"ProviderPreference", "providersite", "secUserRole", "site",
				"rschedule", "scheduledate", "scheduletemplate", "scheduletemplatecode");
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
		databaseUtil.createProviderSite();
	}

	@After
	public void cleanup() throws Exception
	{
		SchemaUtils.restoreTable("admission", "appointment", "demographic", "log", "log_ws_rest", "mygroup",
				"program_provider", "property",	"provider", "providerArchive", "provider_billing", "providerbillcenter",
				"ProviderPreference", "providersite", "secUserRole", "site",
				"rschedule", "scheduledate", "scheduletemplate", "scheduletemplatecode");
	}

	public void appointmentDateDisplay(By appointmentDateBy, String appointmentDate)
	{
		boolean isAppointmentDateDisplayed = false;
		isAppointmentDateDisplayed = PageUtil.isExistsBy(appointmentDateBy, driver);
		if (!isAppointmentDateDisplayed)//If the next Wednesday is not in current week.
		{
			driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
			driver.findElement(By.partialLinkText(appointmentDate)).click();
		}
	}

	public void addAppointmentWithDemo(By timeFrame, String currWindowHandle, String status, String demoFName) throws InterruptedException
	{
		driver.findElement(timeFrame).click();
		PageUtil.switchToLastWindow(driver);
		addAppointmentPageWithDemo(currWindowHandle, status, demoFName);
	}

	public void	addAppointmentWithNODemo(By timeFrame, Set<String> oldWindowHandles, String currWindowHandle, String status)
			throws InterruptedException
	{
		webDriverWait.until(ExpectedConditions.elementToBeClickable(timeFrame));
		driver.findElement(timeFrame).click();
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);
		PageUtil.switchToWindow(newWindows.get(newWindows.size() - 1), driver);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@name='status']")));
		dropdownSelectByValue(driver, By.xpath("//select[@name='status']"), status);
		driver.findElement(By.id("addButton")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
	}

	public void addAppointmentPageWithDemo(String secCurrWindowHandle, String status, String demoFName) throws InterruptedException
	{
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("searchBtn")));
		driver.findElement(By.id("searchBtn")).click();
		driver.findElement(By.xpath(".//td[contains(., '" + demoFName +"')]")).click();
		dropdownSelectByValue(driver, By.xpath("//select[@name='reasonCode']"), "4");//Follow-Up
		driver.findElement(By.id("reason")).sendKeys("Appointment Reason.");
		dropdownSelectByValue(driver, By.xpath("//select[@name='status']"), status);//To Do
		driver.findElement(By.xpath("//input[@name='type']")).sendKeys("Appointment Type");
		driver.findElement(By.xpath("//textarea[@name='notes']")).sendKeys("Appointment Notes");
		driver.findElement(By.xpath("//input[@name='resources']")).sendKeys("Appointment Resources");
		driver.findElement(By.xpath("//input[@value='critical']")).click();
		driver.findElement(By.id("addButton")).click();
		PageUtil.switchToWindow(secCurrWindowHandle, driver);
	}

	public void addAppointmentsSchedulePage(String time, String currWindowHandle, String demofName) throws InterruptedException
	{
		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		addAppointmentWithDemo(By.linkText(time), currWindowHandle, "t", demofName);//To Do
	}

	@Test
	public void addAppointmentsSchedulePageTest() throws InterruptedException {
		Thread.sleep(100000);
		// Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Add an appointment at 10:00-10:15 with NO demographic selected.
		addAppointmentWithNODemo(By.linkText("10:00"),oldWindowHandles, currWindowHandle, "t");//To Do
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(".")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("."), driver));
	}

	/*@Test
	public void addAppointmentsSchedulePageWeeklyViewTest() throws InterruptedException {
		//Weekly View - next week
		driver.findElement(By.xpath("//input[@name='weekview']")).click();
		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		// Add an appointment at 9:00-9:15 next Monday with demographic selected.
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		String xpathAt9 =
				"//a[contains(., 'Mon.,')]/ancestor::tr/following-sibling::tr" +
						"/descendant::td//a[@title='9:00 a.m. - 9:15 a.m.']";
		addAppointmentWithDemo(By.xpath(xpathAt9), currWindowHandle, "t", mom.firstName);//To Do
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Add an appointment at 10:00-10:15 Tuesday with NO demographic selected.
		String xpathAt10 =
				"//a[contains(., 'Tue.,')]/ancestor::tr/following-sibling::tr" +
						"/descendant::td//a[@title='10:00 a.m. - 10:15 a.m.']";
		addAppointmentWithNODemo(By.xpath(xpathAt10),oldWindowHandles, currWindowHandle, "t");//To Do
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(".")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("."), driver));
	}
*/
/*

	@Test
	public void addAppointmentsSchedulePageFlipViewTest() throws InterruptedException {
		//Flip View - next Month
		driver.findElement(By.xpath("//input[@name='flipview']")).click();
		driver.findElement(By.xpath("//a[@title='Next Month']")).click();
		// Add an appointment at 9:00-9:15 the same date of next Month with demographic selected.
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		Date apptAt9Date= calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date apptAt10Date = calendar.getTime();
		String apptAt9DateFormated = dateFormat.format(apptAt9Date);
		String apptAt10DateFormated = dateFormat.format(apptAt10Date);
		String xpathAt9 =
				"//a[contains(., '" + apptAt9DateFormated + "')]/parent::td" +
						"/following-sibling::td[@title='9:00']" +
						"/descendant::td[@style='vertical-align:middle;']";
		addAppointmentWithDemo(By.xpath(xpathAt9), currWindowHandle, "t", mom.firstName);//To Do

		//Add an appointment at 10:00-10:15 the next date as tomorrow of next Month with NO demographic selected.
		String xpathAt10 =
				"//a[contains(., '" + apptAt10DateFormated + "')]/parent::td" +
						"/following-sibling::td[@title='10:00']" +
						"/descendant::td[@style='vertical-align:middle;']";
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathAt10)));
		addAppointmentWithNODemo(By.xpath(xpathAt10),oldWindowHandles, currWindowHandle, "t");// To Do
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[contains(., '" + apptAt9DateFormated + "')]")).click();
		driver.findElement(By.xpath("//a[@title='View all providers in the group']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("."), driver));
	}
*/

	@Test
	public void addAppointmentsSearchToolTest() throws InterruptedException {
		String currWindowHandle = driver.getWindowHandle();
		//Setup Schedule
		accessAdministrationSectionClassicUI(driver, "Schedule Management", "Schedule Setting");
		String windowHandleScheduleSetting = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
 		setupTemplate(windowHandleScheduleSetting, oldWindowHandles);
		setupSchedule(windowHandleScheduleSetting, AuthUtils.TEST_PROVIDER_ID, templateTitleGeneral, templateTitleGeneral);
		List<String> daySchedule = getDailySchedule();
		Assert.assertTrue("Schedule setting for Monday is NOT completed successfully.",
				daySchedule.get(1).contains(templateTitleGeneral));
		Assert.assertTrue("Schedule setting for Tuesday is NOT completed successfully.",
				daySchedule.get(2).contains(templateTitleGeneral));

		//Search available schedule for Wednesdays
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.findElement(By.xpath("//input[@name='searchview']")).click();
		PageUtil.switchToLastWindow(driver);
		driver.manage().window().maximize();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='provider_no']")));
		dropdownSelectByValue(driver, By.xpath("//select[@name='provider_no']"), AuthUtils.TEST_PROVIDER_ID);
		dropdownSelectByValue(driver, By.xpath("//select[@name='dayOfWeek']"), "4"); //Wednesday
		driver.findElement(By.xpath("//input[@value='Search']")).click();
		String secCurrWindowHandle = driver.getWindowHandle();

		// Add an appointment at the first available spot on Wednesday with demographic selected.
		String xpathFirst = "/html/body/center/table/tbody/tr[2]/td[1]";
		driver.findElement(By.xpath(xpathFirst)).click();
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);
		PageUtil.switchToWindow(newWindows.get(2), driver);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='appointment_date']")));
		WebElement appointmentDateElement = driver.findElement(By.cssSelector("input[name='appointment_date']"));
		String appointmentDate = appointmentDateElement.getAttribute("value");
		addAppointmentPageWithDemo(secCurrWindowHandle, "P", mom.firstName);//Picked

		// Add an appointment at the next available Wednesday with NO demographic selected.
		driver.navigate().refresh(); //page refresh
		String xpathNext = "/html/body/center/table/tbody/tr[2]/td[1]";
		addAppointmentWithNODemo(By.xpath(xpathNext),oldWindowHandles, secCurrWindowHandle, "H");//Here
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.findElement(By.xpath("//input[@name='weekview']")).click();
		appointmentDateDisplay(By.partialLinkText(appointmentDate), appointmentDate);
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//img[@title='Picked']"), driver));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.xpath("//img[@title='Here']"), driver));
	}

	/*@Test
	public void addAppointmentsGroupViewTest() throws InterruptedException
	{
		String groupName = "TestGroup";
		driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		PageUtil.switchToWindow(currWindowHandle, driver);
		//Setup Groups
		accessAdministrationSectionClassicUI(driver, "Schedule Management", "Add a Group");
		AddGroupIT addGroupIT = new AddGroupIT();
		addGroupIT.addGroup(groupName, 2);
		Assert.assertTrue("Group is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver));
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("mygroup_no")));
		dropdownSelectByValue(driver, By.id("mygroup_no"), "_grp_TestGroup");
		String xpathAt9 =
				"//a[contains(.,'" + drApple.lastName + "')]" +
						"/ancestor::tr/following-sibling::tr" +
						"/descendant::td//a[@title='9:00 a.m. - 9:15 a.m.']";
		addAppointmentWithDemo(By.xpath(xpathAt9), currWindowHandle, "t", mom.firstName);//To Do
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
		Assert.assertTrue("Appointment with demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Schedule View of the first provider
		driver.findElement(By.xpath("//a[contains(., '" + drApple.lastName + "')]")).click();
		Assert.assertTrue(
				"Appointment with demographic selected is NOT added successfully under the first provider.",
				PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

		//Add an appointment at 10:00-10:15 with NO demographic selected under the second provider.
		driver.findElement(By.linkText("Group View")).click();
		String xpathAt10 =
				"//a[contains(.,'" + drBerry.lastName + "')]" +
						"/ancestor::tr/following-sibling::tr" +
						"/descendant::td//a[@title='10:00 a.m. - 10:15 a.m.']";
		addAppointmentWithNODemo(By.xpath(xpathAt10),oldWindowHandles, currWindowHandle, "t");//To Do
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(".")));
		Assert.assertTrue("Appointment with NO demographic selected is NOT added successfully.",
				PageUtil.isExistsBy(By.linkText("."), driver));

		//Schedule View of the second provider
		driver.findElement(By.xpath("//a[contains(., '" + drBerry.lastName + "')]")).click();
		Assert.assertTrue(
				"Appointment with NO demographic selected is NOT added successfully under the second provider.",
				PageUtil.isExistsBy(By.linkText("."), driver));
	}*/

}
