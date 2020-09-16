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
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddSitesTests extends SeleniumTestBase {


	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "site");
	}

	public void addNewSites(SiteTestData site, WebDriverWait wait)
	{
/*
		try
		{
			Thread.sleep(1000000);
		}
		catch(Exception e)
		{

		}
*/
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
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='Add New Site']")));
		driver.findElement(By.xpath("//input[@value='Add New Site']")).click();
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
	public void addSitesClassicUITest() throws Exception {
		SiteTestData site = SiteTestCollection.siteMap.get(SiteTestCollection.siteNames[0]);
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(
					AuthUtils.TEST_USER_NAME,
					AuthUtils.TEST_PASSWORD,
					AuthUtils.TEST_PIN,
					Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
					driver);
		}
		// open administration panel
		WebDriverWait wait = new WebDriverWait(driver, WEB_DRIVER_EXPLICIT_TIMEOUT);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("admin-panel")));
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);
		addNewSites(site, wait);
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(site.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));
	}

	@Test
	public void addSitesJUNOUITest() throws Exception
	{
		SiteTestData siteJuno = SiteTestCollection.siteMap.get(SiteTestCollection.siteNames[1]);
		// login
		Navigation.doLogin(
				AuthUtils.TEST_USER_NAME,
				AuthUtils.TEST_PASSWORD,
				AuthUtils.TEST_PIN,
				Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
				driver);

		// open JUNO UI page
		driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();

		// open administration panel
		driver.findElement(By.linkText("More")).click();
		driver.findElement(By.linkText("Admin")).click();
		WebDriverWait wait = new WebDriverWait(driver, WEB_DRIVER_EXPLICIT_TIMEOUT);
		addNewSites(siteJuno, wait);
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(siteJuno.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));
	}
}



