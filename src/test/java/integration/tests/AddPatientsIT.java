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
import integration.tests.util.data.PatientTestCollection;
import integration.tests.util.data.PatientTestData;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;

import static integration.tests.util.data.PatientTestCollection.patientLNames;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByIndex;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddPatientsIT extends SeleniumTestBase
{
	public static final PatientTestData mom = PatientTestCollection.patientMap.get(patientLNames[0]);
	public static final PatientTestData dad = PatientTestCollection.patientMap.get(patientLNames[1]);
	public static final PatientTestData son = PatientTestCollection.patientMap.get(patientLNames[2]);
	public static final String momFullNameJUNO = mom.lastName + ", " + mom.firstName;
	public static final String dadFullName = dad.lastName + ',' + dad.firstName;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"demographicArchive", "demographiccust", "demographicExt", "demographicExtArchive", "log", "log_ws_rest",
			"program", "provider_recent_demographic_access", "admission", "demographic", "property"
		};
	}

	public static boolean isPatientAdded(String lastName, String firstName, By searchPage, By searchTerm, By nameRow)
			throws InterruptedException
	{
		driver.findElement(searchPage).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(searchTerm));
		WebElement searchTermField = driver.findElement(searchTerm);
		searchTermField.sendKeys(lastName + ", " + firstName);
		searchTermField.sendKeys(Keys.ENTER);
		Thread.sleep(2000);
		return PageUtil.isExistsBy(nameRow, driver);
	}

	@Test
	public void addPatientsClassicUITest()
			throws Exception
	{
		// open patient search page
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title=\"Search for patient records\"]")));
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographiccontrol')]")).click();
		driver.findElement(By.id("last_name")).sendKeys(mom.lastName);
		driver.findElement(By.id("first_name")).sendKeys(mom.firstName);
		dropdownSelectByValue(driver, By.id("official_lang"), mom.language);
		dropdownSelectByValue(driver, By.id("title"), mom.title);
		dropdownSelectByValue(driver, By.xpath("//select[@name='spoken_lang']"), mom.spoken);
		driver.findElement(By.id("address")).sendKeys(mom.address);
		driver.findElement(By.id("city")).sendKeys(mom.city);
		dropdownSelectByValue(driver, By.id("province"), mom.province);
		driver.findElement(By.id("postal")).sendKeys(mom.postal);
		driver.findElement(By.id("phone")).sendKeys(mom.homePhone);
		driver.findElement(By.id("hPhoneExt")).sendKeys(mom.homePhoneExt);
		driver.findElement(By.xpath("//input[@name='phone2']")).sendKeys(mom.workPhone);
		driver.findElement(By.xpath("//input[@name='wPhoneExt']")).sendKeys(mom.workPhoneExt);
		driver.findElement(By.xpath("//input[@name='demo_cell']")).sendKeys(mom.cellPhone);
		driver.findElement(By.xpath("//textarea[@name='phoneComment']")).sendKeys(mom.phoneComment);
		dropdownSelectByValue(driver, By.xpath("//select[@name='newsletter']"), mom.newsletter);
		dropdownSelectByValue(driver, By.xpath("//select[@name='aboriginal']"), mom.aboriginal);
		driver.findElement(By.id("email")).sendKeys(mom.email);
		driver.findElement(By.xpath("//input[@name='myOscarUserName']")).sendKeys(mom.phrUserName);
		driver.findElement(By.id("year_of_birth")).sendKeys(mom.dobYear);
		driver.findElement(By.id("month_of_birth")).sendKeys(mom.dobMonth);
		driver.findElement(By.id("date_of_birth")).sendKeys(mom.dobDate);
		dropdownSelectByValue(driver, By.id("sex"), mom.sex);
		driver.findElement(By.id("hin")).sendKeys(mom.hin);
		driver.findElement(By.id("eff_date_year")).sendKeys(mom.effYear);
		driver.findElement(By.id("eff_date_month")).sendKeys(mom.effMonth);
		driver.findElement(By.id("eff_date_date")).sendKeys(mom.effDate);
		driver.findElement(By.id("hc_type")).sendKeys(mom.hcType);
		driver.findElement(By.id("hc_renew_date_year")).sendKeys(mom.hcRenewYear);
		driver.findElement(By.id("hc_renew_date_month")).sendKeys(mom.hcRenewMonth);
		driver.findElement(By.id("hc_renew_date_date")).sendKeys(mom.hcRenewDate);
		dropdownSelectByValue(driver, By.id("countryOfOrigin"), mom.countryOfOrigin);
		driver.findElement(By.xpath("//input[@name='sin']")).sendKeys(mom.sin);
		driver.findElement(By.xpath("//input[@name='cytolNum']")).sendKeys(mom.cytology);
		driver.findElement(By.id("nameOfMother")).sendKeys(mom.motherName);
		driver.findElement(By.id("nameOfFather")).sendKeys(mom.fatherName);
		dropdownSelectByIndex(driver, By.xpath("//select[@name='staff']"), 1);
		dropdownSelectByIndex(driver, By.xpath("//select[@name='cust1']"), 0);
		dropdownSelectByIndex(driver, By.xpath("//select[@name='cust4']"), 0);
		dropdownSelectByIndex(driver, By.xpath("//select[@name='cust2']"), 0);
		driver.findElement(By.xpath("//input[@name='referral_doctor_name']")).sendKeys(mom.referralDoctor);
		driver.findElement(By.xpath("//input[@name='referral_doctor_no']")).sendKeys(mom.referralDoctorNo);
		dropdownSelectByValue(driver, By.id("roster_status"), mom.rosterStatus);
		driver.findElement(By.xpath("//input[@name='roster_date_year']")).sendKeys(mom.rosteredYear);
		driver.findElement(By.xpath("//input[@name='roster_date_month']")).sendKeys(mom.rosteredMonth);
		driver.findElement(By.xpath("//input[@name='roster_date_date']")).sendKeys(mom.rosteredDate);
		dropdownSelectByValue(driver, By.id("patient_status"), mom.patientStatus);
		driver.findElement(By.id("chart_no")).sendKeys(mom.chartNo);
		dropdownSelectByIndex(driver, By.id("name_list_id"), 0);
		driver.findElement(By.id("waiting_list_note")).sendKeys("Waiting List Note");
		driver.findElement(By.id("waiting_list_referral_date")).sendKeys("2020-06-06");
		dropdownSelectByIndex(driver, By.id("rsid"), 0);
		driver.findElement(By.id("cust3")).sendKeys("Alert Note");
		driver.findElement(By.id("content")).sendKeys("Notes");
		driver.findElement(By.id("btnAddRecord")).click();
		Thread.sleep(2000);
		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(mom.lastName, mom.firstName,
				By.xpath("//a[contains(.,'Back to Demographic Search Page')]"),
				By.xpath("//input[@class='wideInput']"),
				By.xpath("//tr[@class='odd']")));
	}

	@Test
	public void addPatientsClassicUIQuickFormTest()
			throws Exception
	{
		// open patient search page
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@title=\"Search for patient records\"]")));
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographicaddrecordcustom')]")).click();
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(dad.lastName);
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(dad.firstName);
		driver.findElement(By.xpath("//input[@name='year_of_birth']")).sendKeys(dad.dobYear);
		dropdownSelectByValue(driver, By.xpath("//select[@name='month_of_birth']"), dad.dobMonth);
		dropdownSelectByValue(driver, By.xpath("//select[@name='date_of_birth']"), dad.dobDate);
		dropdownSelectByValue(driver, By.xpath("//select[@name='sex']"), dad.sex);
		driver.findElement(By.xpath("//input[@name='hin']")).sendKeys(dad.hin);
		dropdownSelectByValue(driver, By.xpath("//select[@name='patient_status']"), dad.patientStatus);
		dropdownSelectByIndex(driver, By.xpath("//select[@name='staff']"), 0);
		driver.findElement(By.xpath("//input[@name='submit']")).click();

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(dad.lastName, dad.firstName,
				By.xpath("//a[contains(.,'Back to Demographic Search Page')]"),
				By.xpath("//input[@class='wideInput']"),
				By.xpath("//tr[@class='odd']")));
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.AddPatientsIT
-------------------------------------------------------------------------------
Tests run: 3, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 162.2 s <<< FAILURE! - in integration.tests.AddPatientsIT
addPatientsJUNOUITest  Time elapsed: 129.334 s  <<< ERROR!
org.openqa.selenium.TimeoutException: Expected condition failed: waiting for visibility of element located by By.xpath: //button[@ng-click='$ctrl.clickHandler()'] (tried for 120 second(s) with 500 milliseconds interval)
    at integration.tests.AddPatientsIT.addPatientsJUNOUITest(AddPatientsIT.java:243)
Caused by: org.openqa.selenium.NoSuchElementException:
Unable to locate element: //button[@ng-click='$ctrl.clickHandler()']
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2356765, moz:profile: /tmp/rust_mozprofileJnsTv2, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 0d4f748a-5686-499b-bfe9-a6b482d75e62
*** Element info: {Using=xpath, value=//button[@ng-click='$ctrl.clickHandler()']}
    at integration.tests.AddPatientsIT.addPatientsJUNOUITest(AddPatientsIT.java:243)
	 */
	@Ignore
	@Test
	public void addPatientsJUNOUITest()
			throws Exception
	{
		// open JUNO UI page
		driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title=\"Add a new Patient\"]")));

		// Add a demographic record page
		driver.findElement(By.xpath("//button[@title=\"Add a new Patient\"]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("input-lastName")));
		driver.findElement(By.id("input-lastName")).sendKeys(son.lastName);
		driver.findElement(By.id("input-firstName")).sendKeys(son.firstName);
		dropdownSelectByValue(driver, By.id("input-gender"), "string:" + son.sex);
		driver.findElement(By.id("input-dob")).sendKeys(son.dobYear + "-" + son.dobMonth + "-" + son.dobDate);
		driver.findElement(By.id("input-address")).click();
		driver.findElement(By.id("input-hin")).sendKeys(son.hin);
		dropdownSelectByValue(driver, By.id("input-hcType"), "string:" + son.hcType);
		driver.findElement(By.id("input-address")).sendKeys(son.address);
		driver.findElement(By.id("input-city")).sendKeys(son.city);
		dropdownSelectByValue(driver, By.id("input-province"), "string:" + son.province);
		driver.findElement(By.id("input-postal-code")).sendKeys(son.postal);
		driver.findElement(By.id("input-email")).sendKeys(son.email);
		driver.findElement(By.id("input-phone")).sendKeys(son.homePhone);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@ng-click=\"$ctrl.clickHandler($event)\"]")));
		driver.findElement(By.xpath("//button[@ng-click=\"$ctrl.clickHandler($event)\"]")).click();
		Thread.sleep(2000);

		Assert.assertTrue(isPatientAdded(son.lastName, son.firstName,
				By.xpath("//button[@title='Search']"),
				By.xpath("//input[@placeholder='Search Term']"),
				By.xpath("//tr[@class='ng-scope']")));
	}
}

