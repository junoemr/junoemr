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

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.data.SiteTestCollection;
import integration.tests.util.data.SiteTestData;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddSitesClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "site", "log_ws_rest", "provider_recent_demographic_access", "property"
		};
	}

	public static void addNewSites(SiteTestData site, String frameName)
	{
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//a[contains(.,'System Management')]")));
		findWaitClickByXpath(driver, webDriverWait, ".//a[contains(.,'System Management')]");
		findWaitClickByXpath(driver, webDriverWait, ".//a[contains(.,'Satellite-sites Admin')]");
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameName));
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Add New Site']")));
		driver.findElement(By.xpath("//input[@value='Add New Site']")).click();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='site.name']")));
		driver.findElement(By.xpath("//input[@name='site.name']")).sendKeys(site.siteName);
		driver.findElement(By.xpath("//input[@name='site.shortName']")).sendKeys(site.shortName);
		driver.findElement(By.xpath("//input[@name='site.bgColor']")).sendKeys(site.address);
		driver.findElement(By.xpath("//input[@name='site.status']")).click();
		driver.findElement(By.xpath("//input[@name='site.phone']")).sendKeys(site.telephone);
		driver.findElement(By.xpath("//input[@name='site.fax']")).sendKeys(site.fax);
		driver.findElement(By.xpath("//input[@name='site.address']")).sendKeys(site.address);
		driver.findElement(By.xpath("//input[@name='site.city']")).sendKeys(site.city);
		dropdownSelectByValue(driver, webDriverWait, By.id("province-select"), site.province);
		driver.findElement(By.xpath("//input[@name='site.postal']")).sendKeys(site.postCode);
		driver.findElement(By.xpath("//input[@name='site.bcFacilityNumber']")).sendKeys(site.bcpFacilityNumber);
		driver.findElement(By.id("save-button")).click();
	}

	@Test
	public void addSitesClassicUITest()
	{
		SiteTestData site = SiteTestCollection.siteMap.get(SiteTestCollection.siteNames[0]);

		// open administration panel
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-panel")));
		clickWaitSwitchToLast(driver, webDriverWait, By.id("admin-panel"));
		addNewSites(site, "myFrame");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(site.siteName)));
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(site.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));
	}
}