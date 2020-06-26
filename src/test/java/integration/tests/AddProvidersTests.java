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
import integration.tests.util.junoUtil.Provider;
import integration.tests.util.junoUtil.ProviderCollection;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import java.io.IOException;
import java.sql.SQLException;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByIndex;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

public class AddProvidersTests extends SeleniumTestBase {

	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "program_provider",
				"provider", "provider_billing", "providerbillcenter", "secUserRole");
	}

	public static boolean isPatientAdded(String lastName, String firstName, By searchPage, By searchTerm, By nameRow) throws InterruptedException {
		driver.findElement(searchPage).click();
		WebElement searchTermField = driver.findElement(searchTerm);
		searchTermField.sendKeys(lastName + ", " + firstName);
		searchTermField.sendKeys(Keys.ENTER);
		Thread.sleep(1000);
		return PageUtil.isExistsBy(nameRow, driver);
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
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		// Add a provider record page
		driver.findElement(By.xpath(".//a[contains(@rel,'provideraddarecord')]")).click();
		Thread.sleep(1000);

		Provider drApple = ProviderCollection.providerMap.get("Apple");
		driver.findElement(By.xpath("//input[@name='provider_no']")).clear();
		driver.findElement(By.xpath("//input[@name='provider_no']")).sendKeys(drApple.getProviderNo());
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(drApple.getLastNames());
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(drApple.getFirstNames());
		dropdownSelectByValue(driver, By.id("provider_type"), drApple.getType());
		driver.findElement(By.xpath("//input[@name='specialty']")).sendKeys(drApple.getSpecialty());
		driver.findElement(By.xpath("//input[@name='team']")).sendKeys(drApple.getTeam());
		dropdownSelectByValue(driver, By.id("sex"), drApple.getSex());
		driver.findElement(By.xpath("//input[@name='dob']")).sendKeys(drApple.getDob());
		driver.findElement(By.xpath("//input[@name='address']")).sendKeys(drApple.getAddress());
		driver.findElement(By.xpath("//input[@name='phone']")).sendKeys(drApple.getHomePhone());
		driver.findElement(By.xpath("//input[@name='workphone']")).sendKeys(drApple.getWorkPhone());
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys(drApple.getEmail());
		driver.findElement(By.xpath("//input[@name='xml_p_pager']")).sendKeys(drApple.getPager());
		driver.findElement(By.xpath("//input[@name='xml_p_cell']")).sendKeys(drApple.getCell());

		//Scroll the web page till end.
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		driver.findElement(By.xpath("//input[@name='xml_p_phone2']")).sendKeys(drApple.getOtherPhone());
		driver.findElement(By.xpath("//input[@name='xml_p_fax']")).sendKeys(drApple.getFax());
		driver.findElement(By.xpath("//input[@name='ohip_no']")).sendKeys(drApple.getMspNo());
		driver.findElement(By.xpath("//input[@name='rma_no']")).sendKeys(drApple.getThirdPartyBillinNo());
		driver.findElement(By.xpath("//input[@name='billing_no']")).sendKeys(drApple.getBillingNo());
		driver.findElement(By.xpath("//input[@name='hso_no']")).sendKeys(drApple.getAlternateBillingNo());
		driver.findElement(By.xpath("//input[@name='alberta_e_delivery_ids']")).sendKeys(drApple.getIhaProviderMnemonic());
		driver.findElement(By.xpath("//input[@name='xml_p_specialty_code']")).sendKeys(drApple.getSpecialtyCodeNo());
		driver.findElement(By.xpath("//input[@name='xml_p_billinggroup_no']")).sendKeys(drApple.getGroupBillingNo());
		driver.findElement(By.xpath("//input[@name='practitionerNo']")).sendKeys(drApple.getCpsidNo());
		dropdownSelectByIndex(driver, By.xpath("//select[@name='billcenter']"), 0);//dropdown empty
		driver.findElement(By.xpath("//input[@name='xml_p_slpusername']")).sendKeys(drApple.getSelfLearningUsername());
		driver.findElement(By.xpath("//input[@name='xml_p_slppassword']")).sendKeys(drApple.getSelfLearningPassword());
		dropdownSelectByValue(driver, By.xpath("//select[@name='status']"), drApple.getStatus());
		driver.findElement(By.xpath("//input[@name='submitbtn']")).click();
		Thread.sleep(1000);

		Assert.assertNotNull(driver.findElement(By.xpath(".//h1[contains(.,'Successful Addition of a Provider Record.')]")));
		driver.findElement(By.xpath(".//a[contains(.,'User Management')]")).click();
		driver.findElement(By.xpath(".//a[contains(.,'Search/Edit/Delete Provider Records')]")).click();

		Thread.sleep(1000);
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='search_providerno']")).click();
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(drApple.getProviderNo());
		driver.findElement(By.xpath("//input[@name='button']")).click();
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//tr[contains(.,drApple.getProviderNo())]"), driver));

	}
}


