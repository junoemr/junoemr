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

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByLinkText;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditEpisodesClassicUIIT extends SeleniumTestBase
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

	@Test
	public void addEpisodeTest()
	{
		String codingSystem = "icd9";
		String description = "ABN AMNION NEC AFF NB";
		String startDate = "2022-01-01";
		String endDate = "2022-03-03";
		String status = "Current";

		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		PageUtil.switchToLastWindow(driver);

		//Add Episode
		driver.findElement(
			By.xpath("//div[@id='menuTitleepisode']//descendant::a[contains(., '+')]")).click();
		PageUtil.switchToLastWindow(driver);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("search_coding_system")));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("search_coding_system"), codingSystem);
		WebElement descriptionDropdown = driver.findElement(By.id("description"));
		descriptionDropdown.sendKeys(description);
		findWaitClickByLinkText(driver, webDriverWait, description);
		driver.findElement(By.id("startDate")).sendKeys(startDate);
		driver.findElement(By.id("endDate")).sendKeys(endDate);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("episode.status"), status);
		driver.findElement(By.xpath("//input[@type='submit']")).click();

		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(description)));
		Assert.assertTrue(" Episode is NOT added successfully",
			PageUtil.isExistsBy(By.linkText(description), driver));
	}
}
