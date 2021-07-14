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
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

public class AddEformsClassicUITests extends SeleniumTestBase
{
	@Autowired
	DatabaseUtil databaseUtil;

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
	}

	@After
	public void cleanup()
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		SchemaUtils.restoreTable(
				"admission", "billingservice", "caisi_role", "demographic", "documentDescriptionTemplate", "eform_data",
				"Facility", "issue", "log","measurementType", "LookupList", "LookupListItem", "OscarJob", "OscarJobType",
				"provider", "ProviderPreference", "roster_status", "secUserRole", "tickler_text_suggest", "validations"
		);
	}

	@Test
	public void addFormsTest()
			throws InterruptedException
	{
		String subject = "EFormTest";
		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("menuTitleeforms")));
		driver.findElement(By.xpath("//div[@id='menuTitleeforms']//descendant::a[contains(., '+')]")).click();
		Thread.sleep(10000);
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);
		driver.findElement(By.linkText("letter")).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("subject")).sendKeys(subject);
		driver.findElement((By.xpath("//input[@value='Submit']"))).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(2000);
		Assert.assertTrue("Eform Letter is NOT added successfully.", PageUtil.isExistsBy(By.partialLinkText(subject), driver));
	}
}
