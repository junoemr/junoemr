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
import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;
import static integration.tests.util.seleniumUtil.PageUtil.switchToNewWindow;

public class ScheduleSettingTests extends SeleniumTestBase {
	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "program_provider",
				"provider", "provider_billing", "providerbillcenter", "rschedule", "secUserRole",
				"scheduledate", "scheduleholiday", "scheduletemplate", "scheduletemplatecode");
		loadSpringBeans();
		DatabaseUtil.createTestProvider();
	}

	public static boolean isTemplateInDropdownOpions(By dropdownpath, String valueExpected)
	{
		WebElement dropdown = driver.findElement(dropdownpath);
		Select select = new Select(dropdown);
		List<WebElement> options = select.getOptions();
		boolean match = false;
		for(WebElement code : options)
		{
			if (code.getAttribute("value").equals(valueExpected))
			{
				match = true;
				break;
			}
		}
		return match;
	}

	public static void setDailySchedule(int numHours, int numSlotsPerHour, int startingCell, int numCellsPerHour,
										int apptDuration, int durationSelected, int tr, String templateCode)
	{
		for (int i = 0; i < numHours; i++)
		{
			for (int j = 0; j < numSlotsPerHour; j++)
			{
				int inputPosition = startingCell + i * numCellsPerHour + j * (apptDuration / durationSelected);
				driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[" + tr +
						"]/td[" + inputPosition + "]/input")).sendKeys(templateCode);
			}
		}
	}

	public static void setWeeklySchedule(By weekday, By template, By addingButton)
	{
		driver.findElement(weekday).click();
		driver.findElement(template).click();
		driver.findElement(addingButton).click();
	}

	@Test
	public void setScheduleTest() throws Exception
	{
		String holidayName = "Happy Monday";
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}

		// open administration panel
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath(".//a[contains(.,'Schedule Management')]")).click();
		driver.findElement(By.xpath(".//a[contains(.,'Schedule Setting')]")).click();
		driver.switchTo().frame("myFrame");
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();

		//Holiday Setting - Add the Next Month, the Second Row, Monday as a holiday!
		switchToNewWindow(driver, By.xpath(".//a[contains(.,'Holiday Setting')]"), oldWindowHandles);
		driver.findElement(By.xpath(".//a[contains(.,'next month')]")).click();
		driver.findElement(By.xpath("//table/tbody/tr/td[2]/table[3]/tbody/tr[3]/td[2]/input")).click();
		driver.findElement(By.xpath(".//input[@name='holiday_name']")).sendKeys(holidayName);
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Holiday setting is failed.", PageUtil.isExistsBy(By.xpath(".//font[contains(.,holidayName)]"), driver));
		driver.findElement(By.xpath("//input[@value='Close']")).click();

		//Template Code Setting
		// Delete "A|Academic"
		PageUtil.switchToWindow(currWindowHandle, driver);
		switchToNewWindow(driver, By.xpath("//a[contains(., 'Template Code Setting')]"), oldWindowHandles);
		dropdownSelectByValue(driver, By.xpath("//select[@name='code']"), "A");
		driver.findElement(By.xpath("//input[@value='Edit']")).click();
		driver.findElement(By.xpath("//input[@value='Delete']")).click();
		Assert.assertFalse("Template is NOT deleted successfully.", isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "A"));

		//Set "5|Dr. Apple 5 mins"
		driver.findElement(By.xpath("//input[@name='code']")).sendKeys("5");
		driver.findElement(By.xpath("//input[@name='description']")).sendKeys("Dr. Apple 5 mins");
		driver.findElement(By.xpath("//input[@name='duration']")).sendKeys("5");
		driver.findElement(By.xpath("//input[@name='color']")).sendKeys("#FFCCDD");
		driver.findElement(By.xpath("//input[@name='junoColor']")).sendKeys("#FFCCDD");
		driver.findElement(By.id("bookinglimit")).sendKeys("1");
		driver.findElement(By.xpath("//input[@value='Wk']")).click();
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Template is NOT added successfully.", isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "5"));

		//Edit "a|Administrative Work" to be " b|Break 30 mins"
		dropdownSelectByValue(driver, By.xpath("//select[@name='code']"), "a");
		driver.findElement(By.xpath("//input[@value='Edit']")).click();
		textEdit(driver, By.xpath("//input[@name='code']"), "b");
		textEdit(driver, By.xpath("//input[@name='description']"), "Break 30 mins");
		textEdit(driver, By.xpath("//input[@name='duration']"), "30");
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Template is NOT edited successfully.", isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "b"));
		driver.findElement(By.xpath("//input[@value='Exit']")).click();

		//Template setting for public
		PageUtil.switchToWindow(currWindowHandle, driver);
		switchToNewWindow(driver, By.xpath("//a[contains(., 'Template Setting')]"), oldWindowHandles);
		driver.findElement(By.xpath("//input[@name='name']")).sendKeys("General");
		driver.findElement(By.xpath("//input[@name='summary']")).sendKeys("15 mins duration");
		//15 mins duration 9-12
		setDailySchedule(3, 4, 7, 5, 15, 15,3,"1");
		//30 mins breaks 12-12:30
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[2]/input")).sendKeys("b");
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[4]/input")).sendKeys("b");
		//15 mins duration 13-15
		setDailySchedule(3, 4, 7, 5, 15, 15,4, "1");
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Template for public is NOT added successfully.", isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "P:General"));

		//Edit Template
		dropdownSelectByValue(driver, By.xpath("//select[@name='name']"), "P:General");
		driver.findElement(By.xpath("//input[@value='Edit']")).click();
		driver.findElement(By.xpath("//input[@name='name']")).sendKeys("General-Updated");
		//30 mins break 12-12:30 -> 1 break and a 30 mins appointment
		textEdit(driver, By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[4]/input"), "2");
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Template for public is NOT updated successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "P:General-Updated"));

		//Delete Template
		dropdownSelectByValue(driver, By.xpath("//select[@name='name']"), "P:General-Updated");
		driver.findElement(By.xpath("//input[@value='Edit']")).click();
		driver.findElement(By.xpath("//input[@value='Delete']")).click();
		Assert.assertFalse("Template for public is NOT deleted successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "General-Updated"));
		driver.findElement(By.xpath("//input[@value='Exit']")).click();

		//Template Setting for Dr. Apple
		PageUtil.switchToWindow(currWindowHandle, driver);
		dropdownSelectByValue(driver, By.xpath("//select[@name='providerid']"), ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]).providerNo);
		switchToNewWindow(driver, By.xpath("//a[contains(., 'Template Setting')]"), oldWindowHandles);
		dropdownSelectByValue(driver, By.xpath("//select[@name='step1']"), "5");
		driver.findElement(By.xpath("//input[@value='Go']")).click();
		driver.findElement(By.xpath("//input[@name='name']")).sendKeys("Tue/Thur Schedule");
		driver.findElement(By.xpath("//input[@name='summary']")).sendKeys("Tue/Thur Combination");
		//5 mins duration 9-10
		setDailySchedule(1, 12, 15, 1, 5, 5, 3, "5");
		//30 mins break 12-12:30
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[2]/input")).sendKeys("b");
		//45 mins duration 12:30-14:00
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[8]/input")).sendKeys("3");
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[18]/input")).sendKeys("3");
		//15 mins duration 10-11
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(document.body.scrollWidth, 0)");
		setDailySchedule(1, 4, 28, 1, 15, 5, 3, "1");
		//60 mins duration 11-12
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[3]/td[41]/input")).sendKeys("6");
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		Assert.assertTrue("Template for Dr. Apple is NOT added successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "Tue/Thur Schedule"));
		driver.findElement(By.xpath("//input[@value='Exit']")).click();

		//Set from Provider - Dr. Apple
		PageUtil.switchToWindow(currWindowHandle, driver);
		dropdownSelectByValue(driver, By.xpath("//select[@name='provider_no']"), ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]).providerNo);
		LocalDate currentDate = LocalDate.now();
		String month = Integer.toString(currentDate.getMonthValue());
		String year = Integer.toString(currentDate.getYear());
		String endYear = Integer.toString(currentDate.getYear()+1);
		driver.findElement(By.xpath("//input[@name='syear']")).sendKeys(year);
		driver.findElement(By.xpath("//input[@name='smonth']")).sendKeys(month);
		driver.findElement(By.xpath("//input[@name='sday']")).sendKeys("1");
		driver.findElement(By.xpath("//input[@name='eyear']")).sendKeys(endYear);
		driver.findElement(By.xpath("//input[@name='emonth']")).sendKeys(month);
		driver.findElement(By.xpath("//input[@name='eday']")).sendKeys("1");
		setWeeklySchedule(By.xpath("//input[@name='checksun']"), By.xpath("//option[@value='P:General']"), By.xpath("//input[@name='sunto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkmon']"), By.xpath("//option[@value='P:General']"), By.xpath("//input[@name='monto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checktue']"), By.xpath("//option[@value='Tue/Thur Schedule']"), By.xpath("//input[@name='tueto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkwed']"), By.xpath("//option[@value='P:General']"), By.xpath("//input[@name='wedto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkthu']"), By.xpath("//option[@value='Tue/Thur Schedule']"), By.xpath("//input[@name='thuto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkfri']"), By.xpath("//option[@value='P:General']"), By.xpath("//input[@name='frito1']"));
		setWeeklySchedule(By.xpath("//input[@name='checksat']"), By.xpath("//option[@value='P:General']"), By.xpath("//input[@name='satto1']"));
		driver.findElement(By.id("submitBTNID")).click();
		driver.findElement(By.xpath("//a[contains(.,'next month')]")).click();

		String happenMondayScheduled = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[2]/a/font[2]")).getText();
		Assert.assertEquals("Happy Monday does NOT show in schedule.", happenMondayScheduled, "Happy Monday");
		String monSchedule = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[2]/a/font[3]")).getText();
		String tueSchedule = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[3]/a/font[3]")).getText();
		String wedSchedule = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[4]/a/font[3]")).getText();
		String thuSchedule = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[5]/a/font[3]")).getText();
		String friSchedule = driver.findElement(By.xpath("/html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[6]/a/font[3]")).getText();
		String satSchedule = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[7]/a/font[3]")).getText();
		String sunSchedule = driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[1]/a/font[3]")).getText();
		Assert.assertTrue("Schedule setting for Monday is NOT completed successfully.", monSchedule.contains("P:General"));
		Assert.assertTrue("Schedule setting for Tuesday is NOT completed successfully.", tueSchedule.contains("Tue/Thur Schedule"));
		Assert.assertTrue("Schedule setting for Wednesday is NOT completed successfully.", wedSchedule.contains("P:General"));
		Assert.assertTrue("Schedule setting for Thursday is NOT completed successfully.", thuSchedule.contains("Tue/Thur Schedule"));
		Assert.assertTrue("Schedule setting for Friday is NOT completed successfully.", friSchedule.contains("P:General"));
		Assert.assertTrue("Schedule setting for Saturday is NOT completed successfully.", satSchedule.contains("P:General"));
		Assert.assertTrue("Schedule setting for Sunday is NOT completed successfully.", sunSchedule.contains("P:General"));
		driver.findElement(By.id("submitBTNID")).click();
		Assert.assertTrue("Schedule setting is failed.",
				PageUtil.isExistsBy(By.xpath(".//td[contains(.,'You have finished one Schedule Setting successfully.')]"), driver));
	}

}

