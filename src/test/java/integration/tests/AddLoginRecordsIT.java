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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;

import java.sql.SQLException;
import java.util.Set;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.AddProvidersIT.drApple;
import static integration.tests.AddProvidersIT.drBerry;
import static integration.tests.AssignRolesIT.assignRoles;
import static integration.tests.AssignRolesIT.xpathDropdown;
import static integration.tests.AssignRolesIT.xpathOption;
import static integration.tests.AssignRolesIT.xpathProvider;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionJUNOUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddLoginRecordsIT extends SeleniumTestBase
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

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "property", "provider", "providerbillcenter", "security", "secUserRole"
		};
	}

	@Before
	public void setup() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		loadSpringBeans();
		databaseUtil.createTestProvider();
	}

	public String passwordValidation(String passwordInput) throws InterruptedException
	{
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(passwordInput);
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
		String alertMessage8Symbols = driver.switchTo().alert().getText();
		return alertMessage8Symbols;
	}

	private void addLoginRecord(String password, String providerNo, String pin)///
	{
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='conPassword']")).sendKeys(password);
		dropdownSelectByValue(driver, By.id("provider_no"), providerNo, webDriverWait);

		//Add today as expiry date
		driver.findElement(By.xpath("//input[@name='b_ExpireSet']")).click();
		driver.findElement(By.id("date_ExpireDate_cal")).click();
		driver.findElement(By.xpath("//div[@class='calendar']//div[contains(., 'Today')]")).click();
		driver.findElement(By.xpath("//input[@name='pin']")).sendKeys(pin);
		driver.findElement(By.xpath("//input[@name='conPin']")).sendKeys(pin);
		dropdownSelectByValue(driver, By.xpath("//select[@name='forcePasswordReset']"), "1",
			webDriverWait);
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
	}

	private void removeExpiryDate(String userName, String providerNo)
	{
		Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.getOscarUrl(randomTomcatPort), driver,
			webDriverWait);
		String currWindowHandle1 = driver.getWindowHandle();
		PageUtil.switchToWindow(currWindowHandle1, driver);
		accessAdministrationSectionClassicUI(driver, "User Management", "Search/Edit/Delete Security Records",
			webDriverWait);
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(providerNo);
		driver.findElement(By.xpath("//input[@value='Search']")).click();
		driver.findElement(By.linkText(userName)).click();
		driver.findElement(By.xpath("//input[@name='b_ExpireSet']")).click();
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();
	}

	private void resetPassword(String password, String passwordUpdated)
	{
		driver.findElement(By.xpath("//input[@name='oldPassword']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='newPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@value='Update']")).click();
	}

	private void accessLoginApple(String currWindowHandle)
	{
		PageUtil.switchToWindow(currWindowHandle, driver);
		accessAdministrationSectionClassicUI(driver, "User Management", "Add a Login Record",
			webDriverWait);
		driver.findElement(By.xpath("//input[@name='user_name']")).sendKeys(userNameApple);
	}

	@Test
	@Ignore
	public void addLoginRecordsClassicUITest()
		throws InterruptedException
	{
		String currWindowHandle = driver.getWindowHandle();
		//Assign Roles
		accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider",
			webDriverWait);
		assignRoles(xpathDropdown, xpathProvider, "admin", "//following-sibling::td/input[@value='Add']");
		String message = "Role " + role + " is added. (" + drApple.providerNo + ")";
		Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
				PageUtil.isExistsBy(By.xpath("//font[contains(., '" + message + "')]"), driver));

		accessLoginApple(currWindowHandle);
		//password validation
		String alertMessage8Symbols = passwordValidation("1234");
		Assert.assertEquals(message8SymbolsExpected, alertMessage8Symbols);
		accessLoginApple(currWindowHandle);
		String alertMessageContent = passwordValidation("12345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent);
		accessLoginApple(currWindowHandle);
		String alertMessageContent1 = passwordValidation("1@345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent1);

		//Account expired
		accessLoginApple(currWindowHandle);
		addLoginRecord(password, drApple.providerNo, pin);
		Navigation.doLogin(userNameApple, password, pin, Navigation.getOscarUrl(randomTomcatPort), driver,
			webDriverWait);
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//p[contains(., 'Your account is expired. Please contact your administrator.')]"), driver));

		//Remove expiry date.
		removeExpiryDate(userNameApple, drApple.providerNo);
		Navigation.doLogin(userNameApple, password, pin, Navigation.getOscarUrl(randomTomcatPort), driver,
			webDriverWait);
		Assert.assertTrue(Navigation.isLoggedIn(driver));

		//Reset password
		Navigation.doLogin(userNameApple, password, pin,
				Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
				driver, webDriverWait);
		resetPassword(password, passwordUpdated);
		Assert.assertTrue(Navigation.isLoggedIn(driver));
	}

	@Test
	@Ignore
	public void addLoginRecordsJUNOUITest()
			throws InterruptedException
	{
		String xpathProvider = "(//td[contains(., '" + drBerry.providerNo + "')])";
		String xpathDropdown = xpathProvider + xpathOption;
		//Assign Roles
		accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider",
			webDriverWait);
		assignRoles(xpathDropdown, xpathProvider, "admin", "//following-sibling::td/input[@value='Add']");
		String message = "Role " + role + " is added. (" + drBerry.providerNo + ")";
		Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
				PageUtil.isExistsBy(By.xpath("//font[contains(., '" + message + "')]"), driver));

		Set<String> handles = driver.getWindowHandles();
		PageUtil.switchToWindow(handles.iterator().next(), driver);
		accessAdministrationSectionJUNOUI(driver, "User Management", "Add a Login Record",
			webDriverWait);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='user_name']")));
		driver.findElement(By.xpath("//input[@name='user_name']")).sendKeys(userNameBerry);

		//password validation
		String alertMessage8Symbols = passwordValidation("1234");
		Assert.assertEquals(message8SymbolsExpected, alertMessage8Symbols);
		driver.switchTo().alert().accept();
		String alertMessageContent = passwordValidation("12345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent);
		driver.switchTo().alert().accept();
		String alertMessageContent1 = passwordValidation("1@345678");
		Assert.assertEquals(messageContentExpected, alertMessageContent1);
		driver.switchTo().alert().accept();

		//Account expired.
		addLoginRecord(password, drBerry.providerNo, pin);
		Navigation.doLogin(userNameBerry, password, pin, Navigation.getOscarUrl(randomTomcatPort), driver,
			webDriverWait);
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//p[contains(., 'Your account is expired. Please contact your administrator.')]"), driver));

		//Remove Expiry date.
		removeExpiryDate(userNameBerry, drBerry.providerNo);
		Navigation.doLogin(userNameBerry, password, pin, Navigation.getOscarUrl(randomTomcatPort), driver,
			webDriverWait);
		Assert.assertTrue(Navigation.isLoggedIn(driver));

		//Reset password
		Navigation.doLogin(userNameBerry, password, pin, Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
				driver, webDriverWait);
		resetPassword(password, passwordUpdated);
		Assert.assertTrue(Navigation.isLoggedIn(driver));
	}
}



