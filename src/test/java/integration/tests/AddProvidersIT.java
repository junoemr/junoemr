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

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.data.ProviderTestCollection;
import integration.tests.util.data.ProviderTestData;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddProvidersIT extends SeleniumTestBase
{
	public static final ProviderTestData drApple = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[0]);
	public static final ProviderTestData drBerry = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[1]);
	public static final ProviderTestData drCherry = ProviderTestCollection.providerMap.get(ProviderTestCollection.providerLNames[2]);

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "program_provider", "provider", "provider_billing",
			"providerbillcenter", "secUserRole", "property"
		};
	}

	@Test
	public void addProvidersClassicUITest()
	{
		// open administration panel
		findWaitClickById(driver, webDriverWait, "admin-panel");
		PageUtil.switchToLastWindow(driver);

		// Add a provider record page
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//h5[contains(.,'Add a Provider Record')]")));
		driver.findElement(By.xpath(".//h5[contains(.,'Add a Provider Record')]")).click();

		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));

		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Suggest']");
		driver.findElement(By.xpath("//input[@name='provider_no']")).clear();
		driver.findElement(By.xpath("//input[@name='provider_no']")).sendKeys(drApple.providerNo);
		driver.findElement(By.xpath("//input[@name='last_name']")).sendKeys(drApple.lastName);
		driver.findElement(By.xpath("//input[@name='first_name']")).sendKeys(drApple.firstName);
		dropdownSelectByValue(driver, webDriverWait, By.id("provider_type"), drApple.type);
		driver.findElement(By.xpath("//input[@name='specialty']")).sendKeys(drApple.specialty);
		driver.findElement(By.xpath("//input[@name='team']")).sendKeys(drApple.team);
		dropdownSelectByValue(driver, webDriverWait, By.id("sex"), drApple.sex);
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
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='billcenter']"), drApple.billCenter
		);//dropdown empty
		driver.findElement(By.xpath("//input[@name='xml_p_slpusername']")).sendKeys(drApple.selfLearningUsername);
		driver.findElement(By.xpath("//input[@name='xml_p_slppassword']")).sendKeys(drApple.selfLearningPassword);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//select[@name='status']"), drApple.status
		);
		driver.findElement(By.xpath("//input[@name='submitbtn']")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//h1[contains(.,'Successful Addition of a Provider Record.')]")));
		Assert.assertNotNull(driver.findElement(By.xpath(".//h1[contains(.,'Successful Addition of a Provider Record.')]")));

		driver.switchTo().defaultContent();
		findWaitClickByXpath(driver, webDriverWait, ".//a[contains(.,'User Management')]");
		findWaitClickByXpath(driver, webDriverWait, ".//a[contains(.,'Search/Edit/Delete Provider Records')]");
		webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("myFrame"));
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='search_providerno']");
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='keyword']", drApple.providerNo);
		findWaitClickByXpath(driver, webDriverWait, "//input[@name='button']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("html/body/center/center/table/tbody/tr[2]/td[3]")));
		WebElement providerAdded = driver.findElement(By.xpath("html/body/center/center/table/tbody/tr[2]/td[3]"));
		String providerAddedLName = providerAdded.getText();
		Assert.assertEquals(drApple.lastName, providerAddedLName);
	}
}


