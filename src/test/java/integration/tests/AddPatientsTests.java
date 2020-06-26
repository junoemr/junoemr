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
import integration.tests.util.junoUtil.Patient;
import integration.tests.util.junoUtil.PatientCollection;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;

import static integration.tests.AddAppointmentsTests.dropdownSelectByIndex;
import static integration.tests.AddAppointmentsTests.dropdownSelectByValue;

public class AddPatientsTests extends SeleniumTestBase {

	public static boolean isPatientAdded(String lastName, String firstName, By searchPage, By searchTerm, By nameRow) throws InterruptedException {
		driver.findElement(searchPage).click();
		WebElement searchTermField = driver.findElement(searchTerm);
		searchTermField.sendKeys(lastName + ", " + firstName);
		searchTermField.sendKeys(Keys.ENTER);
		Thread.sleep(1000);
		return PageUtil.isExistsBy(nameRow, driver);
	}

	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "demographic",
				"demographicArchive", "demographiccust", "demographicExt", "demographicExtArchive", "log", "log_ws_rest",
				"program", "provider_recent_demographic_access");
	}

	@Test
	public void addPatientsClassicUITest() throws Exception {
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}
		// open patient search page
		Thread.sleep(2000);
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographiccontrol')]")).click();
		Thread.sleep(2000);

		//Patient p = PatientCollection.patients.get(0);
		Patient mom = PatientCollection.patientMap.get("Momlastname");
		driver.findElement(By.id("last_name")).sendKeys(mom.getLastNames());
		driver.findElement(By.id("first_name")).sendKeys(mom.getFirstNames());
		dropdownSelectByValue(By.id("official_lang"), mom.getLanguage());
		dropdownSelectByValue(By.id("title"), mom.getTitle());
		dropdownSelectByValue(By.xpath("//select[@name='spoken_lang']"), mom.getSpoken());
		driver.findElement(By.id("address")).sendKeys(mom.getAddress());
		driver.findElement(By.id("city")).sendKeys(mom.getCity());
		dropdownSelectByValue(By.id("province"), mom.getProvince());
		driver.findElement(By.id("postal")).sendKeys(mom.getPostal());
		driver.findElement(By.id("phone")).sendKeys(mom.getHomePhone());
		driver.findElement(By.id("hPhoneExt")).sendKeys(mom.getHomePhoneExt());
		driver.findElement(By.xpath("//input[@name='phone2']")).sendKeys(mom.getWorkPhone());
		driver.findElement(By.xpath("//input[@name='wPhoneExt']")).sendKeys(mom.getWorkPhoneExt());
		driver.findElement(By.xpath("//input[@name='demo_cell']")).sendKeys(mom.getCellPhone());
		driver.findElement(By.xpath("//textarea[@name='phoneComment']")).sendKeys(mom.getPhoneComment());
		dropdownSelectByValue(By.xpath("//select[@name='newsletter']"), mom.getNewsletter());
		dropdownSelectByValue(By.xpath("//select[@name='aboriginal']"), mom.getAboriginal());
		driver.findElement(By.id("email")).sendKeys(mom.getEmail());
		driver.findElement(By.xpath("//input[@name='myOscarUserName']")).sendKeys(mom.getPhrUserName());
		driver.findElement(By.id("year_of_birth")).sendKeys(mom.getDobYear());
		driver.findElement(By.id("month_of_birth")).sendKeys(mom.getDobMonth());
		driver.findElement(By.id("date_of_birth")).sendKeys(mom.getDobDate());
		dropdownSelectByValue(By.id("sex"), mom.getSex());
		driver.findElement(By.id("hin")).sendKeys(mom.getHin());
		driver.findElement(By.id("eff_date_year")).sendKeys(mom.getEffYear());
		driver.findElement(By.id("eff_date_month")).sendKeys(mom.getEffMonth());
		driver.findElement(By.id("eff_date_date")).sendKeys(mom.getEffDate());
		driver.findElement(By.id("hc_type")).sendKeys(mom.getHcType());
		driver.findElement(By.id("hc_renew_date_year")).sendKeys(mom.getHcRenewYear());
		driver.findElement(By.id("hc_renew_date_month")).sendKeys(mom.getHcRenewMonth());
		driver.findElement(By.id("hc_renew_date_date")).sendKeys(mom.getHcRenewDate());
		dropdownSelectByValue(By.id("countryOfOrigin"), mom.getCountryOfOrigin());
		driver.findElement(By.xpath("//input[@name='sin']")).sendKeys(mom.getSin());
		driver.findElement(By.xpath("//input[@name='cytolNum']")).sendKeys(mom.getCytology());
		driver.findElement(By.id("nameOfMother")).sendKeys(mom.getMotherName());
		driver.findElement(By.id("nameOfFather")).sendKeys(mom.getFatherName());
		dropdownSelectByIndex(By.xpath("//select[@name='staff']"), 1);
		dropdownSelectByIndex(By.xpath("//select[@name='cust1']"), 0);
		dropdownSelectByIndex(By.xpath("//select[@name='cust4']"), 0);
		dropdownSelectByIndex(By.xpath("//select[@name='cust2']"), 0);
		driver.findElement(By.xpath("//input[@name='referral_doctor_name']")).sendKeys(mom.getReferralDoctor());
		driver.findElement(By.xpath("//input[@name='referral_doctor_no']")).sendKeys(mom.getReferralDoctorNo());
		dropdownSelectByValue(By.id("roster_status"), mom.getRosterStatus());
		driver.findElement(By.xpath("//input[@name='roster_date_year']")).sendKeys(mom.getRosteredYear());
		driver.findElement(By.xpath("//input[@name='roster_date_month']")).sendKeys(mom.getRosteredMonth());
		driver.findElement(By.xpath("//input[@name='roster_date_date']")).sendKeys(mom.getRosteredDate());
		dropdownSelectByValue(By.id("patient_status"), mom.getPatientStatus());
		driver.findElement(By.id("chart_no")).sendKeys(mom.getChartNo());
		dropdownSelectByIndex(By.id("name_list_id"), 0);
		driver.findElement(By.id("waiting_list_note")).sendKeys("Waiting List Note");
		driver.findElement(By.id("waiting_list_referral_date")).sendKeys("2020-06-06");
		dropdownSelectByIndex(By.id("rsid"), 0);
		driver.findElement(By.id("cust3")).sendKeys("Alert Note");
		driver.findElement(By.id("content")).sendKeys("Notes");
		driver.findElement(By.id("btnAddRecord")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(mom.getLastNames(), mom.getFirstNames(),
				By.xpath("//a[contains(.,'Back to Demographic Search Page')]"),
				By.xpath("//input[@class='wideInput']"),
				By.xpath("//tr[@class='odd']")));
	}

	@Test
	public void addPatientsClassicUIQuickFormTest() throws Exception {
		// login
		Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		// open patient search page
		Thread.sleep(2000);
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographicaddrecordcustom')]")).click();
		Thread.sleep(2000);
		Patient dad = PatientCollection.patientMap.get("Dadlastname");
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(dad.getLastNames());
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(dad.getFirstNames());
		driver.findElement(By.xpath("//input[@name='year_of_birth']")).sendKeys(dad.getDobYear());
		dropdownSelectByValue(By.xpath("//select[@name='month_of_birth']"), dad.getDobMonth());
		dropdownSelectByValue(By.xpath("//select[@name='date_of_birth']"), dad.getDobDate());
		dropdownSelectByValue(By.xpath("//select[@name='sex']"), dad.getSex());
		driver.findElement(By.xpath("//input[@name='hin']")).sendKeys(dad.getHin());
		dropdownSelectByValue(By.xpath("//select[@name='patient_status']"), dad.getPatientStatus());
		dropdownSelectByIndex(By.xpath("//select[@name='staff']"), 0);
		driver.findElement(By.xpath("//input[@name='submit']")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(dad.getLastNames(), dad.getFirstNames(),
				By.xpath("//a[contains(.,'Back to Demographic Search Page')]"),
				By.xpath("//input[@class='wideInput']"),
				By.xpath("//tr[@class='odd']")));
	}

	@Test
	public void addPatientsJUNOUITest() throws Exception {
		// login
		Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		Thread.sleep(2000);

		// open JUNO UI page
		driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		Thread.sleep(2000);

		// Add a demographic record page
		driver.findElement(By.xpath("//button[@title=\"Add a new Patient\"]")).click();
		Thread.sleep(2000);

		Patient son = PatientCollection.patientMap.get("Sonlastname");
		driver.findElement(By.id("input-lastName")).sendKeys(son.getLastNames());
		driver.findElement(By.id("input-firstName")).sendKeys(son.getFirstNames());
		dropdownSelectByValue(By.id("input-gender"), "string:" + son.getSex());
		driver.findElement(By.id("input-dob")).sendKeys(son.getDobYear() + "-" + son.getDobMonth() + "-" + son.getDobDate());
		driver.findElement(By.id("input-hin")).sendKeys(son.getHin());
		dropdownSelectByValue(By.id("input-hcType"), "string:" + son.getHcType());
		driver.findElement(By.id("input-address")).sendKeys(son.getAddress());
		driver.findElement(By.id("input-city")).sendKeys(son.getCity());
		dropdownSelectByValue(By.id("input-province"), "string:" + son.getProvince());
		driver.findElement(By.id("input-postal-code")).sendKeys(son.getPostal());
		driver.findElement(By.id("input-email")).sendKeys(son.getEmail());
		driver.findElement(By.id("input-phone")).sendKeys(son.getHomePhone());
		driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();
		Thread.sleep(1000);

		Assert.assertTrue(isPatientAdded(son.getLastNames(), son.getFirstNames(),
				By.xpath("//button[@title='Search']"),
				By.xpath("//input[@placeholder='Search Term']"),
				By.xpath("//tr[@class='ng-scope']")));
	}
}

