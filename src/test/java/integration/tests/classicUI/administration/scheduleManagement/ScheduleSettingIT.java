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
package integration.tests.classicUI.administration.scheduleManagement;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;
import static integration.tests.util.seleniumUtil.PageUtil.switchToNewWindow;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.seleniumUtil.PageUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScheduleSettingIT extends SeleniumTestBase {

	public static String templateTitleGeneral = "P:General";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "program_provider",
			"provider", "provider_billing", "providerbillcenter", "rschedule", "secUserRole",
			"scheduledate", "scheduleholiday", "scheduletemplate", "scheduletemplatecode", "property"
		};
	}

	@Before
	public void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();
		databaseUtil.createTestProvider();
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
				driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr["
						+ tr + "]/td[" + inputPosition + "]/input")).sendKeys(templateCode);
			}
		}
	}

	public static List<String> getDailySchedule()
	{
		ArrayList<String> daySchedule = new ArrayList<>();
		for (int d = 0; d < 7; d++)
		{
			int tr = d + 1;
			String dayScheduleElement =
					driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td["
							+ tr + "]/a/font[3]")).getText();
			daySchedule.add(dayScheduleElement);
		}
		return daySchedule;
	}

	public static void setWeeklySchedule(By weekday, By template, By addingButton)
	{
		findWaitClick(driver, webDriverWait, weekday);
		findWaitClick(driver, webDriverWait, template);
		findWaitClick(driver, webDriverWait, addingButton);
	}

	public static void setupTemplate(String currWindowHandle, Set<String> oldWindowHandles)
	{
		PageUtil.switchToWindow(currWindowHandle, driver);
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText("Template Setting")));
		switchToNewWindow(driver, By.linkText("Template Setting"), oldWindowHandles, webDriverWait);
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
		driver.findElement(By.xpath("//input[@name='name']")).sendKeys("General");
		driver.findElement(By.xpath("//input[@name='summary']")).sendKeys("15 mins duration");
		//15 mins duration 9-12
		setDailySchedule(3, 4, 7, 5, 15, 15,3,"1");
		//30 mins breaks 12-12:30
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[2]/input")).sendKeys("b");
		driver.findElement(By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[4]/input")).sendKeys("b");
		//15 mins duration 13-15
		setDailySchedule(3, 4, 7, 5, 15, 15,4, "1");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
	}

	public static void setupSchedule(String currWindowHandle, String providerNo, String templateTitle1, String templateTitle2)
	{
		PageUtil.switchToWindow(currWindowHandle, driver);
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='provider_no']"), providerNo
		);
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
		setWeeklySchedule(By.xpath("//input[@name='checksun']"), By.xpath("//option[@value='"+templateTitle1+"']"), By.xpath("//input[@name='sunto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkmon']"), By.xpath("//option[@value='"+templateTitle1+"']"), By.xpath("//input[@name='monto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checktue']"), By.xpath("//option[@value='"+templateTitle2+"']"), By.xpath("//input[@name='tueto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkwed']"), By.xpath("//option[@value='"+templateTitle1+"']"), By.xpath("//input[@name='wedto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkthu']"), By.xpath("//option[@value='"+templateTitle2+"']"), By.xpath("//input[@name='thuto1']"));
		setWeeklySchedule(By.xpath("//input[@name='checkfri']"), By.xpath("//option[@value='"+templateTitle1+"']"), By.xpath("//input[@name='frito1']"));
		setWeeklySchedule(By.xpath("//input[@name='checksat']"), By.xpath("//option[@value='"+templateTitle1+"']"), By.xpath("//input[@name='satto1']"));
		findWaitClickById(driver, webDriverWait, "submitBTNID");
		findWaitClickByXpath(driver, webDriverWait, "//a[contains(.,'next month')]");
	}

	@Test
	public void setScheduleTest()
			throws Exception
	{
		String holidayName = "Happy Monday";
		// open Schedule Template Setting page
		accessAdministrationSectionClassicUI(driver, webDriverWait, "Schedule Management","Schedule Setting"
		);
		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();

		//Holiday Setting - Add the Next Month, the Second Row, Monday as a holiday!
		switchToNewWindow(driver, By.xpath(".//a[contains(.,'Holiday Setting')]"), oldWindowHandles,
			webDriverWait);
		findWaitClickByXpath(driver, webDriverWait, ".//a[contains(.,'next month')]");
		findWaitClickByXpath(driver, webDriverWait, "//table/tbody/tr/td[2]/table[3]/tbody/tr[3]/td[2]/input");
		findWaitSendKeysByXpath(driver, webDriverWait, ".//input[@name='holiday_name']", holidayName);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//font[contains(.,holidayName)]")));
		Assert.assertTrue("Holiday setting is failed.", PageUtil.isExistsBy(By.xpath(".//font[contains(.,holidayName)]"), driver));
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Close']");

		//Template Code Setting
		// Delete "A|Academic"
		PageUtil.switchToWindow(currWindowHandle, driver);
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));
		switchToNewWindow(driver, By.xpath(".//a[contains(., 'Template Code Setting')]"), oldWindowHandles,
			webDriverWait);
		dropdownSelectByValue(driver, webDriverWait, By.xpath(".//select[@name='code']"), "A");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Edit']");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Delete']");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='code']")));
		Assert.assertFalse("Template is NOT deleted successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "A"));

		//Set "5|Dr. Apple 5 mins"
		driver.findElement(By.xpath("//input[@name='code']")).sendKeys("5");
		driver.findElement(By.xpath("//input[@name='description']")).sendKeys("Dr. Apple 5 mins");
		driver.findElement(By.xpath("//input[@name='duration']")).sendKeys("5");
		driver.findElement(By.xpath("//input[@name='color']")).sendKeys("#FFCCDD");
		driver.findElement(By.xpath("//input[@name='junoColor']")).sendKeys("#FFCCDD");
		driver.findElement(By.id("bookinglimit")).sendKeys("1");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Wk']");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='code']")));
		Assert.assertTrue("Template is NOT added successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "5"));

		//Edit "a|Administrative Work" to be " b|Break 30 mins"
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='code']"), "a");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Edit']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='code']")));
		textEdit(driver, By.xpath("//input[@name='code']"), "b");
		textEdit(driver, By.xpath("//input[@name='description']"), "Break 30 mins");
		textEdit(driver, By.xpath("//input[@name='duration']"), "30");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='code']")));
		Assert.assertTrue("Template is NOT edited successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='code']"), "b"));
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Exit']");

		//Template setting for public
		setupTemplate(currWindowHandle, oldWindowHandles);
		String templateTitleGeneralUpdate = "P:General-Updated";
		String templateTitleTueThur = "Tue/Thur Schedule";
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='name']")));
		Assert.assertTrue("Template for public is NOT added successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), templateTitleGeneral));

		//Edit Template
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='name']"), templateTitleGeneral
		);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Edit']");
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='name']", "General-Updated");
		//30 mins break 12-12:30 -> 1 break and a 30 mins appointment
		textEdit(driver, By.xpath("html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[4]/input"), "2");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='name']")));
		Assert.assertTrue("Template for public is NOT updated successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), templateTitleGeneralUpdate));

		//Delete Template
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='name']"), templateTitleGeneralUpdate
		);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Edit']");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Delete']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='name']")));
		Assert.assertFalse("Template for public is NOT deleted successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "General-Updated"));
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Exit']");

		//Template Setting for Dr. Apple
		PageUtil.switchToWindow(currWindowHandle, driver);
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='providerid']"),
				ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]).providerNo
		);
		switchToNewWindow(driver, By.xpath("//a[contains(., 'Template Setting')]"), oldWindowHandles,
			webDriverWait);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='step1']"), "5");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Go']");
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='name']", "Tue/Thur Schedule");
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='summary']", "Tue/Thur Combination");
		//5 mins duration 9-10
		setDailySchedule(1, 12, 15, 1, 5, 5, 3, "5");
		//30 mins break 12-12:30
		findWaitSendKeysByXpath(driver, webDriverWait, "html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[2]/input", "b");
		//45 mins duration 12:30-14:00
		findWaitSendKeysByXpath(driver, webDriverWait, "html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[8]/input", "3");
		findWaitSendKeysByXpath(driver, webDriverWait, "html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[4]/td[18]/input", "3");
		//15 mins duration 10-11
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(document.body.scrollWidth, 0)");
		setDailySchedule(1, 4, 28, 1, 15, 5, 3, "1");
		//60 mins duration 11-12
		findWaitSendKeysByXpath(driver, webDriverWait, "html/body/table/tbody/tr/td[2]/form[3]/table[1]/tbody/tr[4]/td/table/tbody/tr[3]/td[41]/input", "6");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//select[@name='name']")));
		Assert.assertTrue("Template for Dr. Apple is NOT added successfully.",
				isTemplateInDropdownOpions(By.xpath("//select[@name='name']"), "Tue/Thur Schedule"));
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Exit']");

		//Set from Provider - Dr. Apple
  		setupSchedule(currWindowHandle, ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]).providerNo,
				templateTitleGeneral, templateTitleTueThur);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(
			By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[2]/a/font[2]")));
		String happenMondayScheduled =
				driver.findElement(By.xpath("html/body/form/table/tbody/tr/td[2]/center/p[1]/table[1]/tbody/tr[3]/td[2]/a/font[2]")).getText();
		Assert.assertEquals("Happy Monday does NOT show in schedule.", happenMondayScheduled, "Happy Monday");
		List<String> daySchedule = getDailySchedule();
		Assert.assertTrue("Schedule setting for Monday is NOT completed successfully.", daySchedule.get(1).contains(templateTitleGeneral));
		Assert.assertTrue("Schedule setting for Tuesday is NOT completed successfully.", daySchedule.get(2).contains(templateTitleTueThur));
		Assert.assertTrue("Schedule setting for Wednesday is NOT completed successfully.", daySchedule.get(3).contains(templateTitleGeneral));
		Assert.assertTrue("Schedule setting for Thursday is NOT completed successfully.", daySchedule.get(4).contains(templateTitleTueThur));
		Assert.assertTrue("Schedule setting for Friday is NOT completed successfully.", daySchedule.get(5).contains(templateTitleGeneral));
		Assert.assertTrue("Schedule setting for Saturday is NOT completed successfully.", daySchedule.get(6).contains(templateTitleGeneral));
		Assert.assertTrue("Schedule setting for Sunday is NOT completed successfully.", daySchedule.get(0).contains(templateTitleGeneral));
		findWaitClickById(driver, webDriverWait, "submitBTNID");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//td[contains(.,'You have finished one Schedule Setting successfully.')]")));
		Assert.assertTrue("Schedule setting is failed.",
				PageUtil.isExistsBy(By.xpath(".//td[contains(.,'You have finished one Schedule Setting successfully.')]"), driver));
	}

}