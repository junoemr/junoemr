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
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

public class AddSitesTests extends SeleniumTestBase {

	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "site");
	}

	@Test
	public void addProvidersClassicUITest() throws Exception {
		String siteName = "Test Clinic";
		String shortName = "TC";
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}
		// open administration panel
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.linkText("System Management")).click();
		driver.findElement(By.linkText("Satellite-sites Admin")).click();
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='Add New Site']")).click();
		driver.findElement(By.xpath("//input[@name='site.name']")).sendKeys(siteName);
		driver.findElement(By.xpath("//input[@name='site.shortName']")).sendKeys(shortName);
		driver.findElement(By.xpath("//input[@name='site.bgColor']")).sendKeys("#1b2c3d");
		driver.findElement(By.xpath("//input[@name='site.status']")).click();
		driver.findElement(By.xpath("//input[@name='site.phone']")).sendKeys("250-250-2599");
		driver.findElement(By.xpath("//input[@name='site.fax']")).sendKeys("250-250-2598");
		driver.findElement(By.xpath("//input[@name='site.address']")).sendKeys("101 2500 Johnson St");
		driver.findElement(By.xpath("//input[@name='site.city']")).sendKeys("Victoria");
		dropdownSelectByValue(driver, By.id("province-select"), "BC");
		driver.findElement(By.xpath("//input[@name='site.postal']")).sendKeys("V1N 2X3");
		driver.findElement(By.xpath("//input[@name='site.bcFacilityNumber']")).sendKeys("111-222");
		driver.findElement(By.id("save-button")).click();
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(shortName), driver));
	}
}


