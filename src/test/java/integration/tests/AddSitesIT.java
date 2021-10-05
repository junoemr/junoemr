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
import integration.tests.util.data.SiteTestCollection;
import integration.tests.util.data.SiteTestData;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddSitesIT extends SeleniumTestBase
{
	@After
	public void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "site");
	}

	public static void addNewSites(SiteTestData site)
	{
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//a[contains(.,'System Management')]")));
		driver.findElement(By.xpath(".//a[contains(.,'System Management')]")).click();
		driver.findElement(By.xpath(".//a[contains(.,'Satellite-sites Admin')]")).click();
		if (PageUtil.isExistsBy(By.id("myFrame"), driver))
		{
			driver.switchTo().frame("myFrame");
		}
		else
		{
			driver.switchTo().frame("content-frame");
		}
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
		dropdownSelectByValue(driver, By.id("province-select"), site.province);
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
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);
		addNewSites(site);
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(site.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));
	}


	@Test
	public void addSitesJUNOUITest()
	{
		SiteTestData siteJuno = SiteTestCollection.siteMap.get(SiteTestCollection.siteNames[1]);
		accessSectionJUNOUI(driver, "Admin");
		addNewSites(siteJuno);
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(siteJuno.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));

	}
}