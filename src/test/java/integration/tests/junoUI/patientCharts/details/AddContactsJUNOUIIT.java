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

package integration.tests.junoUI.patientCharts.details;

import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drApple;
import static integration.tests.util.junoUtil.Navigation.DETAILS_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByLinkText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;
import static integration.tests.util.seleniumUtil.PageUtil.clickSwitchToWindow;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;
import static integration.tests.util.seleniumUtil.PageUtil.isExistsBy;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddContactsJUNOUIIT extends SeleniumTestBase
{
	static String patientFName = "Test";
	static String patientLName = "Test";
	static String patientName = patientLName + "," + patientFName;
	String patientNameJUNO = patientLName + ", " + patientFName;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "demographic", "DemographicContact", "log", "professionalSpecialists",
			"provider", "provider_recent_demographic_access", "providerbillcenter",
			"providersite", "site"
		};
	}

	@Before
	public void setup() throws Exception
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
		databaseUtil.createProviderSite();
	}

	@Test
	public void addInternalProfessionalContactTest()
	{
		// open JUNO UI Patient Details page
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + DETAILS_URL);

		// Add Dr. Apple as Internal Professional Contacts
		String detailPageHandle = driver.getWindowHandle();
		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//h3[contains(., 'Professional Contacts')]//button"));
		String contactManagePageHandle = driver.getWindowHandle();
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='procontact_container']//following-sibling::a");
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Search"));
		clickSwitchToWindow(driver, webDriverWait, By.linkText(drApple.providerNo), contactManagePageHandle);
		clickSwitchToWindow(driver, webDriverWait, By.xpath("//input[@value='Submit']"), detailPageHandle);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//juno-input[@label='Last Name']")));

		//Verified the added professional contact, Dr. Apple
		Assert.assertTrue("Dr. Apple is added as a internal professional contact successfully.",
			isExistsBy(By.xpath("//h3[contains(., 'Professional Contacts')]//following-sibling::div[contains(., '" + drApple.lastName + "')]"),
				driver));
	}

	@Test
	public void addExternalProfessionalContactTest()
	{
		String externalLName = "Smith";
		String externalFName = "External";
		String phone = "2502502500";
		String address = "31 ABC Ave";
		// open JUNO UI Patient Details page
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + DETAILS_URL);

		// Add Dr. Smith as an External Professional Contacts
		String detailPageHandle = driver.getWindowHandle();
		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//h3[contains(., 'Professional Contacts')]//button"));

		String contactManagePageHandle = driver.getWindowHandle();
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='procontact_container']//following-sibling::a");
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("procontact_1.type"), "Professional Specialist");
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Search"));
		findWaitClickByLinkText(driver, webDriverWait, "Add/Edit Professional Specialist");

		String searchPageHandle = driver.getWindowHandle();
		findWaitClickByLinkText(driver, webDriverWait, "Add Specialist");
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='firstName']", externalFName);
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='lastName']", externalLName);
		findWaitSendKeysByXpath(driver, webDriverWait, "//textarea[@name='address']", address);
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='phone']", phone);
		clickSwitchToWindow(driver, webDriverWait, By.xpath("//input[@value='Add Specialist']"),contactManagePageHandle);
		clickSwitchToWindow(driver, webDriverWait, By.linkText("Search"),searchPageHandle);
		findWaitClickByXpath(driver, webDriverWait, "//input[@type='submit']");
		clickSwitchToWindow(driver, webDriverWait, By.xpath("//td[contains(., '"+ externalLName + "')]"), contactManagePageHandle);
		clickSwitchToWindow(driver, webDriverWait, By.xpath("//input[@value='Submit']"), detailPageHandle);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//juno-input[@label='Last Name']")));

		//Verified the added professional contact, Dr. Apple
		Assert.assertTrue("Dr. Smith is added as an external professional contact successfully.",
			isExistsBy(By.xpath("//h3[contains(., 'Professional Contacts')]//following-sibling::div[contains(., '" + externalLName + "')]"),
				driver));
	}
}