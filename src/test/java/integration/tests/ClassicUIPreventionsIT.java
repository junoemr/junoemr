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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
-------------------------------------------------------------------------------
Test set: integration.tests.ClassicUIPreventionsIT
-------------------------------------------------------------------------------
Tests run: 2, Failures: 0, Errors: 2, Skipped: 0, Time elapsed: 18.164 s <<< FAILURE! - in integration.tests.ClassicUIPreventionsIT
handleInjectionPreventions  Time elapsed: 9.585 s  <<< ERROR!
org.openqa.selenium.WebDriverException:
Reached error page: about:neterror?e=connectionFailure&u=http%3A//localhost/oscarPrevention/index.jsp%3Fdemographic_no%3D1&c=UTF-8&d=Firefox%20can%E2%80%99t%20establish%20a%20connection%20to%20the%20server%20at%20localhost.
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2350709, moz:profile: /tmp/rust_mozprofileHydZPV, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: d80c41b1-627a-4c8a-a8fc-123127256b7e
    at integration.tests.ClassicUIPreventionsIT.handleInjectionPreventions(ClassicUIPreventionsIT.java:114)

addExamPrevention  Time elapsed: 8.351 s  <<< ERROR!
org.openqa.selenium.WebDriverException:
Reached error page: about:neterror?e=connectionFailure&u=http%3A//localhost/oscarPrevention/index.jsp%3Fdemographic_no%3D1&c=UTF-8&d=Firefox%20can%E2%80%99t%20establish%20a%20connection%20to%20the%20server%20at%20localhost.
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2351007, moz:profile: /tmp/rust_mozprofileExRFw3, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: c9167a0a-a7f2-485c-97b7-41f3bde5438c
    at integration.tests.ClassicUIPreventionsIT.addExamPrevention(ClassicUIPreventionsIT.java:169)
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClassicUIPreventionsIT extends SeleniumTestBase
{
	// Reused URLs to navigate to
	private static final String PREVENTION_URL = "/oscarPrevention/index.jsp?demographic_no=1";
	private static final String PREVENTION_INJECTION_URL = "/oscarPrevention/AddPreventionData.jsp?prevention=COVID-19&demographic_no=1&prevResultDesc=";
	private static final String EXAM_PREVENTION_URL = "/oscarPrevention/AddPreventionData.jsp?prevention=Smoking&demographic_no=1&prevResultDesc=";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "demographic", "demographicArchive", "demographiccust", "log", "preventions",
			"preventionsExt"
		};
	}

	@Before
	public void setup()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void handleInjectionPreventions()
			throws InterruptedException
	{
		// *** Add prevention ***
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + PREVENTION_INJECTION_URL);

		String originalName = "A vaccine";
		String originalLocation = "The clinic";
		String originalRoute = "that way";
		String originalDose = "1 of 2";
		String originalLot = "lot id would go here!";
		String originalManufacture = "Moderna";
		String originalComments = "Hello, world! I'm a vaccination comment saying that the patient got sick after!";
		LocalDate nextDate = LocalDate.now().plusMonths(1);
		String originalNeverReason = "well this is sure clear";

		// fill in various empty fields on page
		driver.findElement(By.xpath("//input[@name='name']")).sendKeys(originalName);
		driver.findElement(By.xpath("//input[@name='location']")).sendKeys(originalLocation);
		driver.findElement(By.xpath("//input[@name='route']")).sendKeys(originalRoute);
		driver.findElement(By.xpath("//input[@name='dose']")).sendKeys(originalDose);
		// UI indicates there should be a select here??
		driver.findElement(By.xpath("//input[@name='lot']")).sendKeys(originalLot);
		driver.findElement(By.xpath("//input[@name='manufacture']")).sendKeys(originalManufacture);
		driver.findElement(By.xpath("//textarea[@name='comments']")).sendKeys(originalComments);
		driver.findElement(By.xpath("//a[contains(@onclick, 'showHideNextDate')]")).click();
		driver.findElement(By.xpath("//input[@name='neverReason']")).sendKeys(originalNeverReason);

		// NOTE: This does not work. The input doesn't actually accept text input, it forces usage of calendar.
		// Probably a good thing in reality, but it means I have to get more creative when attempting to input a date.
		driver.findElement(By.xpath("//input[@name='nextDate']")).sendKeys(nextDate.toString());

		// save
		driver.findElement(By.xpath("//input[@type='submit']")).click();

		// window closes, find following URL and verify entry shows
		driver.get(Navigation.OSCAR_URL + PREVENTION_URL);

		Set<String> oldWindowHandles = driver.getWindowHandles();

		Assert.assertTrue("Can't find anything resembling an added prevention on page",
				PageUtil.isExistsBy(By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]"), driver));

		Assert.assertTrue("Can't find COVID-19 reference element on page", PageUtil.isExistsBy(
				By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]" +
						"//preceding::div[@class='headPrevention _nifty']//" +
						"child::p//" +
						"child::a[contains(@onclick, 'AddPreventionData.jsp?prevention=COVID-19&demographic_no=1')]"), driver));

		// *** Verify prevention ***

		// Attempt to view prevention and verify information is correct
		driver.findElement(By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]")).click();
		Thread.sleep(2000);
		PageUtil.switchToLastWindow(driver);

		// Pull out current assigned values and make sure they match
		String currentName = driver.findElement(By.xpath("//input[@name='name']")).getAttribute("value");
		String currentLocation = driver.findElement(By.xpath("//input[@name='location']")).getAttribute("value");
		String currentRoute = driver.findElement(By.xpath("//input[@name='route']")).getAttribute("value");
		String currentDose = driver.findElement(By.xpath("//input[@name='dose']")).getAttribute("value");
		String currentLot = driver.findElement(By.xpath("//input[@name='lot']")).getAttribute("value");
		String currentManufacture = driver.findElement(By.xpath("//input[@name='manufacture']")).getAttribute("value");
		String currentComments = driver.findElement(By.xpath("//textarea[@name='comments']")).getText();
		driver.findElement(By.xpath("//a[contains(@onclick, 'showHideNextDate')]")).click();
		String currentNeverReason = driver.findElement(By.xpath("//input[@name='neverReason']")).getAttribute("value");;

		Assert.assertEquals("Prevention name not updated successfully", originalName, currentName);
		Assert.assertEquals("Prevention location not updated successfully", originalLocation, currentLocation);
		Assert.assertEquals("Prevention route not updated successfully", originalRoute, currentRoute);
		Assert.assertEquals("Prevention dose not updated successfully", originalDose, currentDose);
		Assert.assertEquals("Prevention lot not updated successfully", originalLot, currentLot);
		Assert.assertEquals("Prevention manufacture not updated successfully", originalManufacture, currentManufacture);
		Assert.assertEquals("Prevention comments not updated successfully", originalComments, currentComments);
		Assert.assertEquals("Prevention never reason field not updated successfully", originalNeverReason, currentNeverReason);
	}

	@Test
	public void addExamPrevention()
			throws InterruptedException
	{
		// *** Add prevention ***
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + EXAM_PREVENTION_URL);

		String originalComments = "I'm a smoking check!";

		// you should be able to do nothing here and hit save, but for testing purposes we'll fill in comments
		driver.findElement(By.xpath("//textarea[@name='comments']")).sendKeys(originalComments);
		driver.findElement(By.xpath("//input[@type='submit']")).click();

		// window closes, find following URL and verify entry shows
		driver.get(Navigation.OSCAR_URL + PREVENTION_URL);

		Set<String> oldWindowHandles = driver.getWindowHandles();

		// Click on prevention to edit it
		Assert.assertTrue("Can't find anything resembling an added exam prevention on page", PageUtil.isExistsBy(
				By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]" +
				"//preceding::div[@class='headPrevention _nifty']//" +
				"child::p//" +
				"child::a[contains(@onclick, 'AddPreventionData.jsp?prevention=Smoking')]"), driver));

		PageUtil.switchToNewWindow(driver,
				By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]"), oldWindowHandles);
		String currentComment = driver.findElement(By.xpath("//textarea[@name='comments']")).getAttribute("value");
		Assert.assertEquals("Exam-style prevention comments not updated successfully", originalComments, currentComment);
	}
}
