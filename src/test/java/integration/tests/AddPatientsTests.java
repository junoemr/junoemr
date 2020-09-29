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
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;

import static integration.tests.util.data.PatientTestCollection.patientLNames;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByIndex;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

public class AddPatientsTests extends SeleniumTestBase
{
	WebDriverWait wait = new WebDriverWait(driver, WEB_DRIVER_EXPLICIT_TIMEOUT);

	@AfterClass
	public static void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable("admission", "demographic",
				"demographicArchive", "demographiccust", "demographicExt", "demographicExtArchive", "log", "log_ws_rest",
				"program", "provider_recent_demographic_access");
	}

	public boolean isPatientAdded(String lastName, String firstName, By searchPage, By searchTerm, By nameRow) throws InterruptedException
	{
		driver.findElement(searchPage).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(searchTerm));
		WebElement searchTermField = driver.findElement(searchTerm);
		searchTermField.sendKeys(lastName + ", " + firstName);
		searchTermField.sendKeys(Keys.ENTER);
		Thread.sleep(1000);
		return PageUtil.isExistsBy(nameRow, driver);
	}

	@Test
	public void addPatientsClassicUITest() throws Exception
	{
		// open patient search page
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographiccontrol')]")).click();

		//Patient p = PatientCollection.patients.get(0);
		PatientTestData mom = PatientTestCollection.patientMap.get(patientLNames[0]);
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

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(mom.lastName, mom.firstName,
				By.xpath("//a[contains(.,'Back to Demographic Search Page')]"),
				By.xpath("//input[@class='wideInput']"),
				By.xpath("//tr[@class='odd']")));
	}

	@Test
	public void addPatientsClassicUIQuickFormTest() throws Exception
	{
		// open patient search page
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographicaddrecordcustom')]")).click();
		PatientTestData dad = PatientTestCollection.patientMap.get(patientLNames[1]);
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

	@Test
	public void addPatientsJUNOUITest() throws Exception
	{
		// open JUNO UI page
		driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title=\"Add a new Patient\"]")));

		// Add a demographic record page
		driver.findElement(By.xpath("//button[@title=\"Add a new Patient\"]")).click();
		PatientTestData son = PatientTestCollection.patientMap.get(patientLNames[2]);
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
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@ng-click='$ctrl.onAdd()']")));
		driver.findElement(By.xpath("//button[@ng-click='$ctrl.onAdd()']")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title='Search']")));

		Assert.assertTrue(isPatientAdded(son.lastName, son.firstName,
				By.xpath("//button[@title='Search']"),
				By.xpath("//input[@placeholder='Search Term']"),
				By.xpath("//tr[@class='ng-scope']")));
	}
}

