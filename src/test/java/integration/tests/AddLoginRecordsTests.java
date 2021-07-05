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
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import java.util.Set;

import static integration.tests.AddProvidersTests.drApple;
import static integration.tests.AddProvidersTests.drBerry;
import static integration.tests.AssignRolesTests.assignRoles;
import static integration.tests.AssignRolesTests.xpathDropdown;
import static integration.tests.AssignRolesTests.xpathOption;
import static integration.tests.AssignRolesTests.xpathProvider;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionJUNOUI;

public class AddLoginRecordsTests extends SeleniumTestBase
{
	String userNameApple = drApple.firstName + "." + drApple.lastName;
	String userNameBerry = drBerry.firstName + "." + drBerry.lastName;
	String password = "Welcome@123";
	String passwordUpdated = "Welcome@1234";
	String pin = "1234";
	String role = "admin";

	String message8SymbolsExpected = "Password is too short, minimum length is 8 symbols.";
	String messageContentExpected =
			"Password must contain at least 3 of the following: capital chars, lower chars, digits, special chars.";

	@BeforeClass
	public static void setup() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		SchemaUtils.restoreTable("admission", "log", "property", "provider", "providerbillcenter", "security", "secUserRole");
		loadSpringBeans();
		DatabaseUtil.createTestProvider();
	}

	@AfterClass
	public static void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable("admission", "log", "property", "provider", "providerbillcenter", "security", "secUserRole");
	}

	public String passwordValidation(String passwordInput)
	{
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(passwordInput);
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
		Alert alert = driver.switchTo().alert();
		String alertMessage8Symbols = alert.getText();
		alert.accept();
		return alertMessage8Symbols;
	}

	public void addLoginRecord(String password, String providerNo, String pin)///
	{
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='conPassword']")).sendKeys(password);
		dropdownSelectByValue(driver, By.id("provider_no"), providerNo);

		//Add today as expiry date
		driver.findElement(By.xpath("//input[@name='b_ExpireSet']")).click();
		driver.findElement(By.id("date_ExpireDate_cal")).click();
		driver.findElement(By.xpath("//div[@class='calendar']//div[contains(., 'Today')]")).click();
		driver.findElement(By.xpath("//input[@name='pin']")).sendKeys(pin);
		driver.findElement(By.xpath("//input[@name='conPin']")).sendKeys(pin);
		dropdownSelectByValue(driver, By.xpath("//select[@name='forcePasswordReset']"), "1");
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
	}

	public void removeExpiryDate(String userName, String providerNo) throws InterruptedException////
	{
		Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		String currWindowHandle1 = driver.getWindowHandle();
		PageUtil.switchToWindow(currWindowHandle1, driver);
		accessAdministrationSectionClassicUI(driver, "User Management", "Search/Edit/Delete Security Records");
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(providerNo);
		driver.findElement(By.xpath("//input[@value='Search']")).click();
		driver.findElement(By.linkText(userName)).click();
		driver.findElement(By.xpath("//input[@name='b_ExpireSet']")).click();
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
	}

	public void resetPassword(String password, String passwordUpdated)
	{
		driver.findElement(By.xpath("//input[@name='oldPassword']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='newPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@value='Update']")).click();
	}

	@Test
	public void addLoginRecordsClassicUITest()
			throws InterruptedException
	{
		String currWindowHandle = driver.getWindowHandle();
		//Assign Roles
		accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider");
		assignRoles(xpathDropdown, xpathProvider, "admin", "//following-sibling::td/input[@value='Add']");
		String message = "Role " + role + " is added. (" + drApple.providerNo + ")";
		Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
				PageUtil.isExistsBy(By.xpath("//font[contains(., '" + message + "')]"), driver));
		driver.close();

		PageUtil.switchToWindow(currWindowHandle, driver);
		accessAdministrationSectionClassicUI(driver, "User Management", "Add a Login Record");
		driver.findElement(By.xpath("//input[@name='user_name']")).sendKeys(userNameApple);

		//password validation
		String alertMessage8Symbols = passwordValidation("1234");
		Assert.assertEquals(message8SymbolsExpected, alertMessage8Symbols);
		String alertMessageContent = passwordValidation("12345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent);
		String alertMessageContent1 = passwordValidation("1@345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent1);

		//Account expired
		addLoginRecord(password, drApple.providerNo, pin);
		Navigation.doLogin(userNameApple, password, pin, Navigation.OSCAR_URL, driver);
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//p[contains(., 'Your account is expired. Please contact your administrator.')]"), driver));

		//Remove expiry date.
		removeExpiryDate(userNameApple, drApple.providerNo);
		Navigation.doLogin(userNameApple, password, pin, Navigation.OSCAR_URL, driver);
		Assert.assertTrue(Navigation.isLoggedIn(driver));

		//Reset password
		resetPassword(password, passwordUpdated);
		Assert.assertTrue(Navigation.isLoggedIn(driver));
	}

	@Test

	public void addLoginRecordsJUNOUITest()
			throws InterruptedException
	{
		String xpathProvider = "(//td[contains(., '" + drBerry.providerNo + "')])";
		String xpathDropdown = xpathProvider + xpathOption;
		//Assign Roles
		accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider");
		assignRoles(xpathDropdown, xpathProvider, "admin", "//following-sibling::td/input[@value='Add']");
		String message = "Role " + role + " is added. (" + drBerry.providerNo + ")";
		Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
				PageUtil.isExistsBy(By.xpath("//font[contains(., '" + message + "')]"), driver));
		driver.close();

		Set<String> handles = driver.getWindowHandles();
		PageUtil.switchToWindow(handles.iterator().next(), driver);
		accessAdministrationSectionJUNOUI(driver, "User Management", "Add a Login Record");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='user_name']")));
		driver.findElement(By.xpath("//input[@name='user_name']")).sendKeys(userNameBerry);

		//password validation
		String alertMessage8Symbols = passwordValidation("1234");
		Assert.assertEquals(message8SymbolsExpected, alertMessage8Symbols);
		String alertMessageContent = passwordValidation("12345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent);
		String alertMessageContent1 = passwordValidation("1@345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent1);

		//Account expired.
		addLoginRecord(password, drBerry.providerNo, pin);
		Navigation.doLogin(userNameBerry, password, pin, Navigation.OSCAR_URL, driver);
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//p[contains(., 'Your account is expired. Please contact your administrator.')]"), driver));

		//Remove Expiry date.
		removeExpiryDate(userNameBerry, drBerry.providerNo);
		Navigation.doLogin(userNameBerry, password, pin, Navigation.OSCAR_URL, driver);
		Assert.assertTrue(Navigation.isLoggedIn(driver));

		//Reset password
		resetPassword(password, passwordUpdated);
		Assert.assertTrue(Navigation.isLoggedIn(driver));
	}
}



