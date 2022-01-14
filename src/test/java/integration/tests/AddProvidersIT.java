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
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.oscarehr.JunoApplication;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;

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

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.AddProvidersIT
-------------------------------------------------------------------------------
Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 10.452 s <<< FAILURE! - in integration.tests.AddProvidersIT
addProvidersClassicUITest  Time elapsed: 9.524 s  <<< ERROR!
org.openqa.selenium.NoSuchElementException:
Unable to locate element: .//h1[contains(.,'Successful Addition of a Provider Record.')]
For documentation on this error, please visit: https://www.seleniumhq.org/exceptions/no_such_element.html
Build info: version: '3.141.59', revision: 'e82be7d358', time: '2018-11-14T08:17:03'
System info: host: 'fedora', ip: '127.0.0.1', os.name: 'Linux', os.arch: 'amd64', os.version: '5.13.8-200.fc34.x86_64', java.version: '1.8.0_302'
Driver info: org.openqa.selenium.firefox.FirefoxDriver
Capabilities {acceptInsecureCerts: true, browserName: firefox, browserVersion: 90.0.2, javascriptEnabled: true, moz:accessibilityChecks: false, moz:buildID: 20210804102508, moz:geckodriverVersion: 0.29.0, moz:headless: true, moz:processID: 2348078, moz:profile: /tmp/rust_mozprofiledrzUIc, moz:shutdownTimeout: 60000, moz:useNonSpecCompliantPointerOrigin: false, moz:webdriverClick: true, pageLoadStrategy: normal, platform: LINUX, platformName: LINUX, platformVersion: 5.13.8-200.fc34.x86_64, proxy: Proxy(), setWindowRect: true, strictFileInteractability: false, timeouts: {implicit: 0, pageLoad: 300000, script: 30000}, unhandledPromptBehavior: dismiss and notify}
Session ID: 7004a14c-aa65-4887-8c95-fcd555918f3b
*** Element info: {Using=xpath, value=.//h1[contains(.,'Successful Addition of a Provider Record.')]}
    at integration.tests.AddProvidersIT.addProvidersClassicUITest(AddProvidersIT.java:124)
	 */
	@Ignore
	@Test
	public void addProvidersClassicUITest()
			throws Exception
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


