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
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.AddProvidersTests.drApple;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(DatabaseUtil.class)
public class AssignRolesTests extends SeleniumTestBase
{
    public static String xpathProvider = "(//td[contains(., '" + drApple.providerNo + "')])";
    public static String xpathOption = "//following-sibling::td/select[@name='roleNew']";
    public static String xpathDropdown = xpathProvider + xpathOption;

    @Autowired
    private DatabaseUtil databaseUtil;

    @Before
    public void setup() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
    {
        loadSpringBeans();
        databaseUtil.createTestProvider();
    }

    @After
    public void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
    {
        SchemaUtils.restoreTable("admission", "log", "property", "program_provider", "provider", "providerbillcenter", "secUserRole");
    }

    public static void assignRoles(String xpathDropdown, String xpathProviderNo, String role, String xpathAction)
    {
        dropdownSelectByValue(driver, By.xpath(xpathDropdown), role);
        String xpath = xpathProviderNo + xpathAction;
        driver.findElement(By.xpath(xpath)).click();
    }

    public static String optionSelected(String xpath)
    {
        Select dropdownPrimary = new Select(driver.findElement(By.xpath(xpath)));
        String primaryRoleSelected = dropdownPrimary.getFirstSelectedOption().getText();
        return primaryRoleSelected ;
    }

    @Test
    public void assignRolesClassicUITest()
            throws InterruptedException
    {
        accessAdministrationSectionClassicUI(driver, "User Management", "Assign Role to Provider");
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='keyword']")));
        driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(drApple.lastName);
        driver.findElement(By.xpath("//input[@name='search']")).click();

        //Add admin role
        String xpathAdd = "//following-sibling::td/input[@value='Add']";
        assignRoles(xpathDropdown, xpathProvider, "admin", xpathAdd);
        String messageAdd = "Role admin is added. (" + drApple.providerNo + ")";
        Assert.assertTrue("Admin is NOT assigned to the provider successfully.",
                PageUtil.isExistsBy(By.xpath("//font[contains(., '" + messageAdd + "')]"), driver));

        //Add doctor role
        assignRoles(xpathDropdown, xpathProvider, "doctor", xpathAdd);

        //Update nurse role
        String xpathUpdated = xpathProvider + "[2]" + xpathOption;
        assignRoles(xpathUpdated, xpathProvider + "[2]", "nurse", "//following-sibling::td/input[@value='Update']");
        String roleUpdated = optionSelected(xpathUpdated);
        Assert.assertEquals("Nurse is NOT updated to the provider successfully.", "nurse", roleUpdated);

        //Set primary role
        dropdownSelectByValue(driver, By.id("primaryRoleProvider"), drApple.providerNo);
        dropdownSelectByValue(driver, By.id("primaryRoleRole"), "admin");
        driver.findElement(By.xpath("//input[@value='Set Primary Role']")).click();
        driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(drApple.lastName);
        driver.findElement(By.xpath("//input[@name='search']")).click();
        Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//td[contains(., 'Yes')]"), driver));

        String path = "//td[contains(., 'Yes')]/preceding-sibling::td//select[@name='roleNew']";
        String primaryRoleSelected = optionSelected(path);
        Assert.assertEquals("Admin is NOT added as primary role successfully.", "admin", primaryRoleSelected);

        //Delete Role Nurse.
        String xpathDelete = "(//td[contains(., '" + drApple.providerNo + "')])[2]//following-sibling::td/input[@value='Delete']";
        driver.findElement(By.xpath(xpathDelete)).click();
        String messageDelete = "Role nurse is deleted. (" + drApple.providerNo + ")";
        Assert.assertTrue("Role nurse is NOT deleted successfully.",
                PageUtil.isExistsBy(By.xpath("//font[contains(., '" + messageDelete + "')]"), driver));
    }
}
