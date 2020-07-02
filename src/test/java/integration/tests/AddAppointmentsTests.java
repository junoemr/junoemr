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
import integration.tests.util.seleniumUtil.PageUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.Set;

public class AddAppointmentsTests extends SeleniumTestBase {

	private static final String[] lastNames = {"Momlastname", "Dadlastname", "Sonlastname"};
	private static final String[] firstNames = {"Momfirstname", "Dadfirstname", "Sonfirstname"};
	private static final String[] dobYear = {"1999", "1988", "2008"};
	private static final String[] dobMonth = {"09", "08", "08"};
	private static final String[] dobDate = {"09", "08", "08"};
	private static final String[] sex = {"F", "M", "F"};
	private static final String[] hin = {"01234567890", "111111111", "9874397159"};

	private static final String language = "English";
	private static final String title = "MS";
	private static final String spoken = "English";
	private static final String address = "31 Bastion Square #302";
	private static final String city = "Victoria";
	private static final String province = "BC";
	private static final String postal = "V8W 1J1";
	private static final String homePhone = "686-8560";
	private static final String homePhoneExt = "101";
	private static final String workPhone = "+1 888-686-8560";
	private static final String workPhoneExt = "102";
	private static final String cellPhone = "+1 888-686-8560";
	private static final String phoneComment = "Prefer Cell";
	private static final String newsletter = "No";
	private static final String aboriginal = "No";
	private static final String email = "ailin.zhu@cloudpractice.ca";
	private static final String phrUserName = "TTestLastName";
	private static final String effYear = "2020";
	private static final String effMonth = "06";
	private static final String effDate = "01";
	private static final String hcType = "BC";
	private static final String hcRenewYear = "2018";
	private static final String hcRenewMonth = "08";
	private static final String hcRenewDate = "08";
	private static final String countryOfOrigin = "CA";
	private static final String sin = "987654321";
	private static final String cytology = "123456789";
	private static final String motherName = "Mom TestLastName";
	private static final String fatherName = "Dad TestLastName";
	private static final String referralDoctor = "Dr Referral";
	private static final String referralDoctorNo = "111111111";
	private static final String rosterStatus = "RO";
	private static final String rosteredYear = "2018";
	private static final String rosteredMonth = "08";
	private static final String rosteredDate = "08";
	private static final String patientStatus = "AC";
	private static final String chartNo = "10001";

	public static void switchToWindow()
	{
		Set<String> allHandles = driver.getWindowHandles();
		allHandles.remove(allHandles.iterator().next());
		String lastHandle = allHandles.iterator().next();
		driver.switchTo().window(lastHandle);
	}

	public static void dropdownSelectByValue(By dropdown, String dropdownSelection)
	{
		Select langOfficial = new Select(driver.findElement(dropdown));
		langOfficial.selectByValue(dropdownSelection);
	}

	public static void dropdownSelectByIndex(By dropdown, int dropdownIndex)
	{
		Select doctor = new Select(driver.findElement(dropdown));
		doctor.selectByIndex(dropdownIndex);
	}

	public static boolean isPatientAdded(String lastName, String firstName, By searchPage, By searchTerm, By nameRow) throws InterruptedException {
		driver.findElement(searchPage).click();
		WebElement searchTermField = driver.findElement(searchTerm);
		searchTermField.sendKeys(lastName + ", " + firstName);
		searchTermField.sendKeys(Keys.ENTER);
		Thread.sleep(1000);
		return PageUtil.isExistsBy(nameRow, driver);
	}
/*
	@Test
	public void addPatientsClassicUITest() throws Exception {
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}
		// open patient search page
		Thread.sleep(2000);
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		switchToWindow();
		Thread.sleep(2000);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographiccontrol')]")).click();
		Thread.sleep(2000);

		driver.findElement(By.id("last_name")).sendKeys(lastNames[0]);
		driver.findElement(By.id("first_name")).sendKeys(firstNames[0]);
		dropdownSelectByValue(By.id("official_lang"), language);
		dropdownSelectByValue(By.id("title"), title);
		dropdownSelectByValue(By.xpath("//select[@name='spoken_lang']"), spoken);
		driver.findElement(By.id("address")).sendKeys(address);
		driver.findElement(By.id("city")).sendKeys(city);
		dropdownSelectByValue(By.id("province"), province);
		driver.findElement(By.id("postal")).sendKeys(postal);
		driver.findElement(By.id("phone")).sendKeys(homePhone);
		driver.findElement(By.id("hPhoneExt")).sendKeys(homePhoneExt);
		driver.findElement(By.xpath("//input[@name='phone2']")).sendKeys(workPhone);
		driver.findElement(By.xpath("//input[@name='wPhoneExt']")).sendKeys(workPhoneExt);
		driver.findElement(By.xpath("//input[@name='demo_cell']")).sendKeys(cellPhone);
		driver.findElement(By.xpath("//textarea[@name='phoneComment']")).sendKeys(phoneComment);
		dropdownSelectByValue(By.xpath("//select[@name='newsletter']"), newsletter);
		dropdownSelectByValue(By.xpath("//select[@name='aboriginal']"), aboriginal);
		driver.findElement(By.id("email")).sendKeys(email);
		driver.findElement(By.xpath("//input[@name='myOscarUserName']")).sendKeys(phrUserName);
		driver.findElement(By.id("year_of_birth")).sendKeys(dobYear[0]);
		driver.findElement(By.id("month_of_birth")).sendKeys(dobMonth[0]);
		driver.findElement(By.id("date_of_birth")).sendKeys(dobDate[0]);
		dropdownSelectByValue(By.id("sex"), sex[0]);
		driver.findElement(By.id("hin")).sendKeys(hin[0]);
		driver.findElement(By.id("eff_date_year")).sendKeys(effYear);
		driver.findElement(By.id("eff_date_month")).sendKeys(effMonth);
		driver.findElement(By.id("eff_date_date")).sendKeys(effDate);
		driver.findElement(By.id("hc_type")).sendKeys(hcType);
		driver.findElement(By.id("hc_renew_date_year")).sendKeys(hcRenewYear);
		driver.findElement(By.id("hc_renew_date_month")).sendKeys(hcRenewMonth);
		driver.findElement(By.id("hc_renew_date_date")).sendKeys(hcRenewDate);
		dropdownSelectByValue(By.id("countryOfOrigin"), countryOfOrigin);
		driver.findElement(By.xpath("//input[@name='sin']")).sendKeys(sin);
		driver.findElement(By.xpath("//input[@name='cytolNum']")).sendKeys(cytology);
		driver.findElement(By.id("nameOfMother")).sendKeys(motherName);
		driver.findElement(By.id("nameOfFather")).sendKeys(fatherName);
		dropdownSelectByIndex(By.xpath("//select[@name='staff']"), 1);
		dropdownSelectByIndex(By.xpath("//select[@name='cust1']"), 0);
		dropdownSelectByIndex(By.xpath("//select[@name='cust4']"), 0);
		dropdownSelectByIndex(By.xpath("//select[@name='cust2']"), 0);
		driver.findElement(By.xpath("//input[@name='referral_doctor_name']")).sendKeys(referralDoctor);
		driver.findElement(By.xpath("//input[@name='referral_doctor_no']")).sendKeys(referralDoctorNo);
		dropdownSelectByValue(By.id("roster_status"), rosterStatus);
		driver.findElement(By.xpath("//input[@name='roster_date_year']")).sendKeys(rosteredYear);
		driver.findElement(By.xpath("//input[@name='roster_date_month']")).sendKeys(rosteredMonth);
		driver.findElement(By.xpath("//input[@name='roster_date_date']")).sendKeys(rosteredDate);
		dropdownSelectByValue(By.id("patient_status"), patientStatus);
		driver.findElement(By.id("chart_no")).sendKeys(chartNo);
		dropdownSelectByIndex(By.id("name_list_id"), 0);
		driver.findElement(By.id("waiting_list_note")).sendKeys("Waiting List Note");
		driver.findElement(By.id("waiting_list_referral_date")).sendKeys("2020-06-06");
		dropdownSelectByIndex(By.id("rsid"), 0);
		driver.findElement(By.id("cust3")).sendKeys("Alert Note");
		driver.findElement(By.id("content")).sendKeys("Notes");
		driver.findElement(By.id("btnAddRecord")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(lastNames[0], firstNames[0],
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
		switchToWindow();
		Thread.sleep(2000);

		// Add a demographic record page
		driver.findElement(By.xpath(".//a[contains(@href,'demographicaddrecordcustom')]")).click();
		Thread.sleep(2000);

		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(lastNames[1]);
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(firstNames[1]);
		driver.findElement(By.xpath("//input[@name='year_of_birth']")).sendKeys(dobYear[1]);
		dropdownSelectByValue(By.xpath("//select[@name='month_of_birth']"), dobMonth[1]);
		dropdownSelectByValue(By.xpath("//select[@name='date_of_birth']"), dobDate[1]);
		dropdownSelectByValue(By.xpath("//select[@name='sex']"),sex[1]);
		driver.findElement(By.xpath("//input[@name='hin']")).sendKeys(hin[1]);
		dropdownSelectByValue(By.xpath("//select[@name='patient_status']"), patientStatus);
		dropdownSelectByIndex(By.xpath("//select[@name='staff']"), 0);
		driver.findElement(By.xpath("//input[@name='submit']")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h2[contains(.,'Successful Addition of a Demographic Record.')]")));
		Assert.assertTrue(isPatientAdded(lastNames[1], firstNames[1],
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

		driver.findElement(By.id("input-lastName")).sendKeys(lastNames[2]);
		driver.findElement(By.id("input-firstName")).sendKeys(firstNames[2]);
		dropdownSelectByValue(By.id("input-gender"), "string:" + sex[2]);
		driver.findElement(By.id("input-dob")).sendKeys(dobYear[2] + "-" + dobMonth[2] + "-" + dobDate[2]);
		driver.findElement(By.id("input-hin")).sendKeys(hin[2]);
		dropdownSelectByValue(By.id("input-hcType"), "string:" + hcType);
		driver.findElement(By.id("input-address")).sendKeys(address);
		driver.findElement(By.id("input-city")).sendKeys(city);
		dropdownSelectByValue(By.id("input-province"), "string:" + province);
		driver.findElement(By.id("input-postal-code")).sendKeys(postal);
		driver.findElement(By.id("input-email")).sendKeys(email);
		driver.findElement(By.id("input-phone")).sendKeys(homePhone);
		driver.findElement(By.xpath("//button[contains(., 'Add')]")).click();
		Thread.sleep(1000);

		Assert.assertTrue(isPatientAdded(lastNames[2], firstNames[2],
				By.xpath("//button[@title='Search']"),
				By.xpath("//input[@placeholder='Search Term']"),
				By.xpath("//tr[@class='ng-scope']")));
	}*/
}

