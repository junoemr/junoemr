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
import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.data.ProviderTestData;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddProvidersTests extends SeleniumTestBase
{
	public static final ProviderTestData drApple = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]);
	public static final ProviderTestData drBerry = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[1]);
	public static final ProviderTestData drCherry = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[2]);

	@AfterClass
	public static void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "log", "program_provider",
				"provider", "provider_billing", "providerbillcenter", "secUserRole");
	}

	@Test
	public void addProvidersClassicUITest()
			throws Exception
	{
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(
					AuthUtils.TEST_USER_NAME,
					AuthUtils.TEST_PASSWORD,
					AuthUtils.TEST_PIN,
					Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
					driver);
		}

		// open administration panel
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);

		// Add a provider record page
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//h5[contains(.,'Add a Provider Record')]")));
		driver.findElement(By.xpath(".//h5[contains(.,'Add a Provider Record')]")).click();
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='Suggest']")).click();
		driver.findElement(By.xpath("//input[@name='provider_no']")).clear();
		driver.findElement(By.xpath("//input[@name='provider_no']")).sendKeys(drApple.providerNo);
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(drApple.lastName);
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(drApple.firstName);
		dropdownSelectByValue(driver, By.id("provider_type"), drApple.type);
		driver.findElement(By.xpath("//input[@name='specialty']")).sendKeys(drApple.specialty);
		driver.findElement(By.xpath("//input[@name='team']")).sendKeys(drApple.team);
		dropdownSelectByValue(driver, By.id("sex"), drApple.sex);
		driver.findElement(By.xpath("//input[@name='dob']")).sendKeys(drApple.dob);
		driver.findElement(By.xpath("//input[@name='address']")).sendKeys(drApple.address);
		driver.findElement(By.xpath("//input[@name='phone']")).sendKeys(drApple.homePhone);
		driver.findElement(By.xpath("//input[@name='workphone']")).sendKeys(drApple.workPhone);
		driver.findElement(By.xpath("//input[@name='email']")).sendKeys(drApple.email);
		driver.findElement(By.xpath("//input[@name='xml_p_pager']")).sendKeys(drApple.pager);
		driver.findElement(By.xpath("//input[@name='xml_p_cell']")).sendKeys(drApple.cell);

		//Scroll the web page till end.
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,document.body.scrollHeight)");
		driver.findElement(By.xpath("//input[@name='xml_p_phone2']")).sendKeys(drApple.otherPhone);
		driver.findElement(By.xpath("//input[@name='xml_p_fax']")).sendKeys(drApple.fax);
		driver.findElement(By.xpath("//input[@name='ohip_no']")).sendKeys(drApple.mspNo);
		driver.findElement(By.xpath("//input[@name='rma_no']")).sendKeys(drApple.thirdPartyBillinNo);
		driver.findElement(By.xpath("//input[@name='billing_no']")).sendKeys(drApple.billingNo);
		driver.findElement(By.xpath("//input[@name='hso_no']")).sendKeys(drApple.alternateBillingNo);
		driver.findElement(By.xpath("//input[@name='alberta_e_delivery_ids']")).sendKeys(drApple.ihaProviderMnemonic);
		driver.findElement(By.xpath("//input[@name='xml_p_specialty_code']")).sendKeys(drApple.specialtyCodeNo);
		driver.findElement(By.xpath("//input[@name='xml_p_billinggroup_no']")).sendKeys(drApple.groupBillingNo);
		driver.findElement(By.xpath("//input[@name='practitionerNo']")).sendKeys(drApple.cpsidNo);
		dropdownSelectByValue(driver, By.xpath("//select[@name='billcenter']"), drApple.billCenter);//dropdown empty
		driver.findElement(By.xpath("//input[@name='xml_p_slpusername']")).sendKeys(drApple.selfLearningUsername);
		driver.findElement(By.xpath("//input[@name='xml_p_slppassword']")).sendKeys(drApple.selfLearningPassword);
		dropdownSelectByValue(driver, By.xpath("//select[@name='status']"), drApple.status);
		driver.findElement(By.xpath("//input[@name='submitbtn']")).click();
		Assert.assertNotNull(driver.findElement(By.xpath(".//h1[contains(.,'Successful Addition of a Provider Record.')]")));

		driver.switchTo().defaultContent();
		driver.findElement(By.xpath(".//a[contains(.,'User Management')]")).click();
		driver.findElement(By.xpath(".//a[contains(.,'Search/Edit/Delete Provider Records')]")).click();
		Thread.sleep(1000);
		driver.switchTo().frame("myFrame");
		driver.findElement(By.xpath("//input[@value='search_providerno']")).click();
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(drApple.providerNo);
		driver.findElement(By.xpath("//input[@name='button']")).click();
		WebElement providerAdded = driver.findElement(By.xpath("html/body/center/center/table/tbody/tr[2]/td[3]"));
		String providerAddedLName = providerAdded.getText();
		Assert.assertEquals(drApple.lastName, providerAddedLName);
	}
}


