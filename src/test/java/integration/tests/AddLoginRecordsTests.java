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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import java.util.Set;

import static integration.tests.AddProvidersTests.drApple;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

class AssignRolesTests
{
	static void assignRolesTest(WebDriver driver, String role, String providerLastName)
	{
		accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider");
		driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(providerLastName);
		driver.findElement(By.xpath("//input[@name='search']")).click();
		WebElement providerRow = driver.findElement(By.xpath("//td[contains(., '" + drApple.providerNo + "')]"));
		Select roleDropdown = new Select(providerRow.findElement(By.xpath("//select[@name='roleNew']")));
		roleDropdown.selectByValue(role);
		providerRow.findElement(By.xpath("//input[@value='Add']")).click();
		String ss = "Role admin is added. (" + drApple.providerNo + ")";
		Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
				PageUtil.isExistsBy(By.xpath("//font[contains(., '" + ss + "')]"), driver));
		driver.close();
	}
}

public class AddLoginRecordsTests extends SeleniumTestBase
{
	String userName = drApple.firstName + "." + drApple.lastName;
	String password = "Welcome@123";
	String passwordUpdated = "Welcome@1234";
	String pin = "1234";
	String role = "admin";

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

	@Test
	public void addLoginRecordsClassicUITest()
	{
		String currWindowHandle = driver.getWindowHandle();
		AssignRolesTests.assignRolesTest(driver, role, drApple.lastName);
		PageUtil.switchToWindow(currWindowHandle, driver);
		accessAdministrationSectionClassicUI(driver, "User Management", "Add a Login Record");
		driver.findElement(By.xpath("//input[@name='user_name']")).sendKeys(userName);
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='conPassword']")).sendKeys(password);
		dropdownSelectByValue(driver, By.id("provider_no"), drApple.providerNo);
		driver.findElement(By.xpath("//input[@name='pin']")).sendKeys(pin);
		driver.findElement(By.xpath("//input[@name='conPin']")).sendKeys(pin);
		dropdownSelectByValue(driver, By.xpath("//select[@name='forcePasswordReset']"), "1");
		driver.findElement(By.xpath("//input[@name='subbutton']")).click();

		Navigation.doLogin(userName, password, pin, Navigation.OSCAR_URL, driver);
		Assert.assertTrue(Navigation.isLoggedIn(driver));

		//Reset password
		driver.findElement(By.xpath("//input[@name='oldPassword']")).sendKeys(password);
		driver.findElement(By.xpath("//input[@name='newPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys(passwordUpdated);
		driver.findElement(By.xpath("//input[@value='Update']")).click();
		Assert.assertTrue(Navigation.isLoggedIn(driver));
	}
}



