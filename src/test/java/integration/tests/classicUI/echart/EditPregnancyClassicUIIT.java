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

package integration.tests.classicUI.echart;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
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
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditPregnancyClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]
			{
				"admission", "demographic", "Episode", "log", "log_ws_rest", "measurementType", "validations"
			};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	String pregnancyType = "Normal";
	String statusComplete = "Complete";
	String statusDelete = "Deleted";

	@Test
	public void AddPregnancyTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);

		//Add Normal Pregnancy
		addPregnancy();
		Assert.assertTrue(pregnancyType + " is NOT added successfully",
			PageUtil.isExistsBy(By.partialLinkText(pregnancyType), driver));
	}

	@Test
	public void EditPregnancyTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String eChartWindowHandle = driver.getWindowHandle();

		//Add Normal Pregnancy
		addPregnancy();
		String pregnancyXpath =
			"//ul[@id='pregnancylist']//descendant::a[contains(.,'" + pregnancyType + "')]";

		//Change Status to Complete by clicking the link of the added pregnancy type
		driver.findElement(By.xpath(pregnancyXpath)).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("endDate")).click();
		driver.findElement(By.xpath("//td[contains(@class, 'today')]")).click();
		String noteXpath =
			"//h5[contains(., 'Set the date of completion to close this episode')]//following-sibling::table//descendant::textarea[@id='notes']";
		String submitXpath = "//h5[contains(., 'Set the date of completion to close this episode')]//parent::fieldset//descendant::input[@type='submit']";
		driver.findElement(By.xpath(noteXpath)).sendKeys(statusComplete);
		driver.findElement(By.xpath(submitXpath)).click();

		//Verify the color change on Echart page.
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(
			ExpectedConditions.presenceOfElementLocated(By.partialLinkText(pregnancyType)));
		String styleColor = driver.findElement(By.xpath(pregnancyXpath)).getAttribute("style");
		String styleColorExpected = "color: red;";
		Assert.assertEquals(pregnancyType + " is NOT Completed successfully.", styleColorExpected,
			styleColor);

		//Verify the status change on Pregnancy Management page
		accessPregnancyManagementPage();
		String status = driver.findElement(By.xpath("//option[@selected='selected']"))
			.getAttribute("value");
		String statusExpected = statusComplete;
		Assert.assertEquals("Status is NOT Completed.", statusExpected, status);
	}

	@Test
	public void DeletePregnancyTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String eChartWindowHandle = driver.getWindowHandle();

		//Add Normal Pregnancy
		addPregnancy();
		accessPregnancyManagementPage();

		//Delete the Pregnancy
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("episode.status"), statusDelete);
		driver.findElement(By.xpath("//input[@value='Submit']")).click();
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Pregnancies")));
		Assert.assertFalse(pregnancyType + " is Not delted successfully.", PageUtil.isExistsBy(By.partialLinkText(pregnancyType), driver));
	}

	private void addPregnancy()
	{
		Actions action = new Actions(driver);
		WebElement plusButton = driver.findElement(
			By.xpath("//div[@id='menuTitlepregnancy']//descendant::a[contains(., '+')]"));
		action.moveToElement(plusButton).perform();
		driver.findElement(By.linkText(pregnancyType)).click();
		driver.navigate().refresh();
		webDriverWait.until(
			ExpectedConditions.presenceOfElementLocated(By.partialLinkText(pregnancyType)));
	}

	private void accessPregnancyManagementPage()
	{
		driver.findElement(By.linkText("Pregnancies")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.partialLinkText(pregnancyType)).click();
		PageUtil.switchToLastWindow(driver);
	}
}