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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.common.dao.utils.AuthUtils;
import java.util.Set;

public class AddProvidersTests extends SeleniumTestBase {

	private static final String[] providerNo = {"200001", "200002", "200003"};
	private static final String[] lastNames = {"Drlname", "Adminlname", "Nurselname"};
	private static final String[] firstNames = {"Drfname", "Adminfname", "Nursefname"};
	private static final String[] type = {"doctor", "admin", "nurse"};
	private static final String[] specialty = {"Family", "111111111", "9874397159"};
	private static final String[] team = {"Clinic", "111111111", "9874397159"};
	private static final String[] sex = {"M", "F", "F"};
	private static final String[] dob = {"1980-02-02", "1988-08-08", "2000-08-08"};
	private static final String address = "31 Bastion Square #302";
	private static final String homePhone = "250-686-8560";
	private static final String workPhone = "+1 888-686-8560";
	private static final String email = "ailin.zhu@cloudpractice.ca";
	private static final String pager = "71077777";
	private static final String cell = "250-250-2500";
	private static final String otherPhone = "250-686-8560";
	private static final String fax = "+1 888-686-8560";
	private static final String mspNo = "6060666";
	private static final String thirdPartyBillinNo = "1010101";
	private static final String billingNo = "1010102";
	private static final String alternateBillingNo = "1010103";
	private static final String bcpEligibility = "1";//yes
	private static final String ihaProviderMnemonic = "PROG17H17-Hydroxyprogesterone";
	private static final String specialtyCodeNo = "Family010";
	private static final String groupBillingNo = "CA-123456";
	private static final String cpsidNo = "987654321";
	private static final String selfLearningUsername = "druser";
	private static final String selfLearningPassword = "Welcome@123";
	private static final String status = "1";//active

	public static void switchToLastWindow()
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

	@Test
	public void addProvidersClassicUITest() throws Exception {
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}
		// open administration panel
		Thread.sleep(2000);
		driver.findElement(By.id("admin-panel")).click();
		switchToLastWindow();
		Thread.sleep(2000);

		// Add a provider record page
		driver.findElement(By.xpath(".//h5[contains(.,'Add a Provider Record')]")).click();
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='Suggest']")).click();
		Assert.assertNotNull(driver.findElement(By.xpath("//input[@name='provider_no']")));

		driver.findElement(By.xpath("//input[@name='provider_no']")).clear();
		driver.findElement(By.xpath("//input[@name='provider_no']")).sendKeys(providerNo[0]);
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(lastNames[0]);
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(firstNames[0]);
		dropdownSelectByValue(By.id("provider_type"), type[0]);
		driver.findElement(By.xpath("//input[@name='specialty']")).sendKeys(specialty[0]);
		driver.findElement(By.xpath("//input[@name='team']")).sendKeys(team[0]);
		dropdownSelectByValue(By.id("sex"),sex[0]);
		driver.findElement(By.xpath("//input[@name='dob']")).sendKeys(dob[0]);
		driver.findElement(By.xpath("//input[@name='address']")).sendKeys(address);
		driver.findElement(By.xpath("//input[@name='phone']")).sendKeys(homePhone);
		driver.findElement(By.xpath("//input[@name='workphone']")).sendKeys(workPhone);
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys(email);
		driver.findElement(By.xpath("//input[@name='xml_p_pager']")).sendKeys(pager);
		driver.findElement(By.xpath("//input[@name='xml_p_cell']")).sendKeys(cell);

		//Scroll the web page till end.
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		driver.findElement(By.xpath("//input[@name='xml_p_phone2']")).sendKeys(otherPhone);
		driver.findElement(By.xpath("//input[@name='xml_p_fax']")).sendKeys(fax);
		driver.findElement(By.xpath("//input[@name='ohip_no']")).sendKeys(mspNo);
		driver.findElement(By.xpath("//input[@name='rma_no']")).sendKeys(thirdPartyBillinNo);
		driver.findElement(By.xpath("//input[@name='billing_no']")).sendKeys(billingNo);
		driver.findElement(By.xpath("//input[@name='hso_no']")).sendKeys(alternateBillingNo);
		driver.findElement(By.xpath("//input[@name='alberta_e_delivery_ids']")).sendKeys(ihaProviderMnemonic);
		driver.findElement(By.xpath("//input[@name='xml_p_specialty_code']")).sendKeys(specialtyCodeNo);
		driver.findElement(By.xpath("//input[@name='xml_p_billinggroup_no']")).sendKeys(groupBillingNo);
		driver.findElement(By.xpath("//input[@name='practitionerNo']")).sendKeys(cpsidNo);
		dropdownSelectByIndex(By.xpath("//select[@name='billcenter']"), 0);//dropdown empty
		driver.findElement(By.xpath("//input[@name='xml_p_slpusername']")).sendKeys(selfLearningUsername);
		driver.findElement(By.xpath("//input[@name='xml_p_slppassword']")).sendKeys(selfLearningPassword);
		dropdownSelectByValue(By.xpath("//select[@name='status']"), status);
		driver.findElement(By.xpath("//input[@name='submitbtn']")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h1[contains(.,'Successful Addition of a Provider Record.')]")));
		driver.switchTo().defaultContent();
		driver.findElement(By.xpath(".//a[contains(.,'User Management')]")).click();
		driver.findElement(By.xpath(".//a[contains(.,'Search/Edit/Delete Provider Records')]")).click();
		Thread.sleep(1000);
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='search_providerno']")).click();
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(providerNo[0]);
		driver.findElement(By.xpath("//input[@name='button']")).click();
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//tr[contains(.,providerNo[0])]"), driver));

	}

	/*@Test
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

