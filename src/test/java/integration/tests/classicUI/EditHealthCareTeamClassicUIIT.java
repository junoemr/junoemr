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

package integration.tests.classicUI;

import static integration.tests.classicUI.AddProvidersIT.drBerry;
import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.PageUtil.switchToWindowRefreshAndWait;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.ActionUtil;
import integration.tests.util.seleniumUtil.PageUtil;
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
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditHealthCareTeamClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]
			{
				"admission", "Contact", "demographic", "DemographicContact", "log", "log_ws_rest", "measurementType",
				"provider", "provider_recent_demographic_access", "providerbillcenter", "validations"
			};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
	}

	String externalType = "CARDIO & THORACIC";
	String externalLName = "Zhu";
	String externalFName = "Specialist";
	String externalWorkNumber = "250-250-2500";
	String externalFaxNumber = "250-250-2501";
	String internalFullName = drBerry.lastName + ", " + drBerry.firstName;
	String externalFullName = externalLName + "," + externalFName;

	@Test
	public void AddInternalProviderTest()
	{
		String eChartWindowHandle = accessEChartPage();
		String manageWindowHandle = accessManageHealthCarePage();
		addInternalContact();
		Assert.assertTrue("Internal Provider is NOT added successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + internalFullName + "')]"), driver));

		//Verify on EeChart page
		switchToWindowRefreshAndWait(eChartWindowHandle, driver, webDriverWait, By.linkText("Health Care Team"));
		Assert.assertTrue("eChart page: Internal Provider is NOT added successfully.",
			PageUtil.isExistsBy(By.partialLinkText(internalFullName), driver));
	}

	@Test
	public void AddExternalProviderTest()
	{
		String eChartWindowHandle = accessEChartPage();
		String manageWindowHandle = accessManageHealthCarePage();
		addExternalContact();
		switchToWindowRefreshAndWait(manageWindowHandle, driver, webDriverWait, By.xpath("//td[contains(., '" + externalLName + "')]"));
		Assert.assertTrue("External Provider is NOT added successfully",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + externalFullName + "')]"), driver));

		//Verify on EeChart page
		switchToWindowRefreshAndWait(eChartWindowHandle, driver, webDriverWait, By.linkText("Health Care Team"));
		Assert.assertTrue("eChart page: External Provider is NOT added successfully.",
			PageUtil.isExistsBy(By.partialLinkText(externalFullName), driver));
	}

	@Test
	public void EditExternalProviderTest()
	{
		String eChartWindowHandle = accessEChartPage();
		String manageWindowHandle = accessManageHealthCarePage();
		addExternalContact();

		//Edit External Provider by adding Fax number
		switchToWindowRefreshAndWait(manageWindowHandle, driver, webDriverWait, By.xpath("//td[contains(., '" + externalLName + "')]"));
		ActionUtil.findWaitClickById(driver, webDriverWait, "edit2_1");
		PageUtil.switchToLastWindow(driver);
		ActionUtil.findWaitSendKeysById(driver, webDriverWait, "pcontact.fax", externalFaxNumber);
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//input[@value='Exit']");

		//Verify the edit on Manage Health Care Team page
		switchToWindowRefreshAndWait(manageWindowHandle, driver, webDriverWait, By.xpath("//td[contains(., '" + externalLName + "')]"));
		Assert.assertTrue("External Provider Fax is NOT added successfully.",
			PageUtil.isExistsBy(By.xpath("//td[contains(., '" + externalFaxNumber + "')]"),
				driver));

		//Verify the edit on eChart page
		switchToWindowRefreshAndWait(eChartWindowHandle, driver, webDriverWait, By.linkText("Health Care Team"));
		ActionUtil.findWaitClickByPartialLinkText(driver, webDriverWait, externalFullName);
		PageUtil.switchToLastWindow(driver);
		String faxNumber = driver.findElement(By.id("pcontact.fax")).getAttribute("value");
		Assert.assertEquals("eChart Page: External Provider Fax is NOT added successfully.",
			faxNumber, externalFaxNumber);
	}

	@Test
	public void RemoveInternalProviderTest()
	{
		String eChartWindowHandle = accessEChartPage();
		String manageWindowHandle = accessManageHealthCarePage();
		addInternalContact();

		//Remove Internal provider
		ActionUtil.findWaitClickById(driver, webDriverWait, "remove0_1");

		//Verify the removal from eChart page.
		switchToWindowRefreshAndWait(eChartWindowHandle, driver, webDriverWait, By.linkText("Health Care Team"));
		Assert.assertFalse("eChart page: Internal Provider is NOT removed successfully.",
			PageUtil.isExistsBy(By.partialLinkText(internalFullName), driver));
	}

	@Test
	public void RemoveExternalProviderTest()
	{
		String eChartWindowHandle = accessEChartPage();
		String manageWindowHandle = accessManageHealthCarePage();
		addExternalContact();

		//Remove External provider
		switchToWindowRefreshAndWait(manageWindowHandle, driver, webDriverWait, By.xpath("//td[contains(., '" + externalLName + "')]"));
		ActionUtil.findWaitClickById(driver, webDriverWait, "remove2_1");

		//Verify the removal from eChart page.
		switchToWindowRefreshAndWait(eChartWindowHandle, driver, webDriverWait, By.linkText("Health Care Team"));
		Assert.assertFalse("eChart page: Internal Provider is NOT removed successfully.",
			PageUtil.isExistsBy(By.partialLinkText(internalFullName), driver));
		Assert.assertFalse("eChart page: External Provider is NOT removed successfully.",
			PageUtil.isExistsBy(By.partialLinkText(externalFullName), driver));
	}
	
	private String accessEChartPage()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String eChartWindowHandle = driver.getWindowHandle();
		return eChartWindowHandle;
	}

	private String accessManageHealthCarePage()
	{
		driver.findElement(
			By.xpath("//div[@id='menuTitlecontacts']//descendant::a[contains(., '+')]")).click();

		//Add Internal Provider
		PageUtil.switchToLastWindow(driver);
		String manageWindowHandle = driver.getWindowHandle();
		return manageWindowHandle;
	}

	private void addInternalContact()
	{
		ActionUtil.dropdownSelectByValue(driver, webDriverWait, By.id("internalProviderList"),
			drBerry.providerNo);
		ActionUtil.findWaitClickById(driver, webDriverWait, "addHealthCareTeamButton");
		webDriverWait.until(
			ExpectedConditions.presenceOfElementLocated(By.xpath("//td[contains(., '" + drBerry.lastName + "')]")));
	}

	private void addExternalContact()
	{
		ActionUtil.dropdownSelectByVisibleText(driver, webDriverWait,
			By.id("searchInternalExternal"), "external");
		ActionUtil.dropdownSelectByVisibleText(driver, webDriverWait,
			By.id("selectHealthCareTeamRoleType"), externalType);
		ActionUtil.findWaitClickById(driver, webDriverWait, "searchHealthCareTeamButton");

		//Add External Contacts
		PageUtil.switchToLastWindow(driver);
		ActionUtil.findWaitClickByLinkText(driver, webDriverWait, "Add/Edit Professional Contact");
		ActionUtil.findWaitSendKeysById(driver, webDriverWait, "pcontact.lastName", externalLName);
		ActionUtil.findWaitSendKeysById(driver, webDriverWait, "pcontact.firstName", externalFName);
		ActionUtil.findWaitSendKeysById(driver, webDriverWait, "pcontact.workPhone",
			externalWorkNumber);
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//input[@value='Exit']");
	}
}