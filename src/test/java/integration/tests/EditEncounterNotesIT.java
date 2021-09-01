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
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditEncounterNotesIT extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "casemgmt_note", "demographic",
			"eChart", "eform_data", "eform_instance", "eform_values", "log", "log_ws_rest", "measurementType",
			"provider_recent_demographic_access","validations"
		};
	}

	@Before
	public void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.EditEncounterNotesIT
-------------------------------------------------------------------------------
Tests run: 2, Failures: 0, Errors: 1, Skipped: 1, Time elapsed: 9.75 s <<< FAILURE! - in integration.tests.EditEncounterNotesIT
editEncounterNotesClassicUITest  Time elapsed: 9.473 s  <<< ERROR!
org.openqa.selenium.UnhandledAlertException:
Dismissed user prompt dialog: Your current note has not been saved.  Click OK to save it or Cancel to continue editing the current note.:
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2382955, moz:profile: /tmp/rust_mozprofilei69jIo, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 76690858-b0f2-4e0c-befb-e65fc97704ad
*** Element info: {Using=link text, value=Edit}
    at integration.tests.EditEncounterNotesIT.editEncounterNotesClassicUITest(EditEncounterNotesIT.java:86)

	 */
	@Ignore
	@Test
	public void editEncounterNotesClassicUITest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);

		String newNote = "Testing Note";
		String editedNote = "Edited Testing Note";
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, document.body.scrollHeight)");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@name='caseNote_note']")));
		driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).sendKeys(newNote);
		driver.findElement(By.id("saveImg")).click();
		driver.findElement(By.id("newNoteImg")).click();
		driver.findElement(By.linkText("Edit")).click();
		driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).sendKeys(editedNote);
		driver.findElement(By.id("saveImg")).click();
		String text = driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).getText();
		Assert.assertTrue("Edited Note is NOT saved", Pattern.compile(editedNote).matcher(text).find());
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.EditEncounterNotesIT
-------------------------------------------------------------------------------
Tests run: 2, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 19.131 s <<< FAILURE! - in integration.tests.EditEncounterNotesIT
editEncounterNotesJUNOUITest  Time elapsed: 9.495 s  <<< ERROR!
org.openqa.selenium.WebDriverException:
Reached error page: about:neterror?e=connectionFailure&u=http%3A//localhost/oscarEncounter/IncomingEncounter.do%3FproviderNo%3D999998%26appointmentNo%3D%26demographicNo%3D1%26curProviderNo%3D%26reason%3DTel-Progress+Note%26encType%3D%26curDate%3D2019-4-17%26appointmentDate%3D%26startTime%3D%26status%3D&c=UTF-8&d=Firefox%20can%E2%80%99t%20establish%20a%20connection%20to%20the%20server%20at%20localhost.
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2350129, moz:profile: /tmp/rust_mozprofiledge7y8, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: a7d9f5ba-1a73-4b95-b189-fc0e89ba8176
    at integration.tests.EditEncounterNotesIT.editEncounterNotesJUNOUITest(EditEncounterNotesIT.java:95)

	 */
	@Ignore
	@Test
	public void editEncounterNotesJUNOUITest()
	{
		driver.get(Navigation.OSCAR_URL + ECHART_URL);

		String newNote = "Testing Note JUNO";
		String editedNote = "Edited Testing Note JUNO";
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditor2")));
		driver.findElement(By.id("noteEditor2")).sendKeys(newNote);
		driver.findElement(By.id("theSave")).click();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@ng-click='$ctrl.editButtonClick()']")));
		driver.findElement(By.xpath("//button[@ng-click='$ctrl.editButtonClick()']")).click();
		driver.findElement(By.id("noteEditor2")).clear();
		driver.findElement(By.id("noteEditor2")).sendKeys(editedNote);
		driver.findElement(By.id("theSave")).click();
		String text = driver.findElement(By.xpath("//p[@class='ng-binding']")).getText();
		Assert.assertTrue("Edited Note is NOT saved in JUNO UI", Pattern.compile(editedNote).matcher(text).find());
	}
}
