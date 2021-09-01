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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddEformsClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "billingservice", "caisi_role", "demographic", "documentDescriptionTemplate", "eform_data",
			"Facility", "issue", "log","measurementType", "LookupList", "LookupListItem", "OscarJob", "OscarJobType",
			"provider", "providerbillcenter", "ProviderPreference", "roster_status", "secUserRole", "tickler_text_suggest", "validations"
		};
	}

	@Before
	public void setup()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.AddEformsClassicUIIT
-------------------------------------------------------------------------------
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 39.73 s <<< FAILURE! - in integration.tests.AddEformsClassicUIIT
addFormsTest  Time elapsed: 39.484 s  <<< ERROR!
org.openqa.selenium.NoSuchElementException:
Unable to locate element: #subject
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2508163, moz:profile: /tmp/rust_mozprofileAAHlia, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 884afb84-dfc1-4786-8705-050e49ea018a
*** Element info: {Using=id, value=subject}
    at integration.tests.AddEformsClassicUIIT.addFormsTest(AddEformsClassicUIIT.java:80)

	 */
	@Ignore
	@Test
	public void addFormsTest()
			throws InterruptedException
	{
		String subject = "EFormTest";
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
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
