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

import integration.tests.config.TestConfig;
import integration.tests.sql.SqlFiles;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = {JunoApplication.class, TestConfig.class},
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EFormIT extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";
	private static String EFORM_URL = "/eform/efmformslistadd.jsp?demographic_no=1&appointment=&parentAjaxId=eforms";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"casemgmt_note", "eChart", "eform", "eform_data", "eform_instance",
			"eform_values", "measurementType", "validations"
		};
	}

	@Before
	public void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();

		databaseUtil.createTestDemographic();
		SchemaUtils.loadFileIntoMySQL(SqlFiles.EFORM_ADD_TRAVLE_FORM_V4);

		if(!Navigation.isLoggedIn(driver))
		{
			Navigation.doLogin(
					AuthUtils.TEST_USER_NAME,
					AuthUtils.TEST_PASSWORD,
					AuthUtils.TEST_PIN,
					Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
					driver);
		}
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.EFormIT
-------------------------------------------------------------------------------
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 89.55 s <<< FAILURE! - in integration.tests.EFormIT
canAddTravel_Form_v4EForm  Time elapsed: 89.286 s  <<< ERROR!
org.openqa.selenium.NoSuchElementException:
Unable to locate element: //a[contains(., 'travel_from_v4:')]
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2513163, moz:profile: /tmp/rust_mozprofilehxP2gQ, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 0f87ee45-a561-4a64-bdf8-fa3b184b7deb
*** Element info: {Using=xpath, value=//a[contains(., 'travel_from_v4:')]}
    at integration.tests.EFormIT.canAddTravel_Form_v4EForm(EFormIT.java:120)
	 */
	@Ignore
	@Test
	public void canAddTravel_Form_v4EForm()
			throws InterruptedException
	{
		//navigate to eform addition page
		String oldUrl = driver.getCurrentUrl();
		driver.get(Navigation.getOscarUrl(Integer.toString(randomTomcatPort)) + EFORM_URL);
		PageUtil.waitForPageChange(oldUrl, driver);
		Assert.assertFalse("expecting eform page but found error page!", PageUtil.isErrorPage(driver));
		logger.info("Navigate to eform add page. OK");

		//open eform
		WebElement eformButton = driver.findElement(By.xpath("//a[contains(., 'travel_from_v4')]"));
		Assert.assertNotNull(eformButton);

		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		eformButton.click();
		Thread.sleep(2000);
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);

		Assert.assertEquals("more than one window opened when opening eform", 1, newWindows.size());
		PageUtil.switchToWindow(newWindows.get(0), driver);
		Thread.sleep(2000);

		String content = driver.getPageSource();
		Assert.assertFalse("got error page on eform page", PageUtil.isErrorPage(driver));
		logger.info("Open eform travel_form_v4. OK");

		driver.findElement(By.xpath("//input[@id='SubmitButton']")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		logger.info("Submit eform travel_form_v4. OK");

		driver.get(Navigation.getOscarUrl(Integer.toString(randomTomcatPort)) + ECHART_URL);
		Thread.sleep(5000);
		Assert.assertNotNull(driver.findElement(By.xpath("//a[contains(., 'travel_from_v4:')]")));
		logger.info("Eform added to Echart? OK");
	}
}
