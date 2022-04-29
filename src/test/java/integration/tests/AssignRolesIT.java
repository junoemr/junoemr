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

import static integration.tests.AddProvidersIT.drApple;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.seleniumUtil.PageUtil;
import java.sql.SQLException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AssignRolesIT extends SeleniumTestBase
{
    public static String xpathProvider = "(//td[contains(., '" + drApple.providerNo + "')])";
    public static String xpathOption = "//following-sibling::td/select[@name='roleNew']";
    public static String xpathDropdown = xpathProvider + xpathOption;

    @Override
    protected String[] getTablesToRestore()
    {
        return new String[]{
            "admission", "log", "property", "program_provider", "provider", "providerbillcenter", "recyclebin", "secUserRole"
        };
    }

    @Before
    public void setup() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
    {
        loadSpringBeans();
        databaseUtil.createTestProvider();
    }

    public static void assignRoles(String xpathDropdown, String xpathProviderNo, String role, String xpathAction)
    {
        dropdownSelectByVisibleText(driver, webDriverWait, By.xpath(xpathDropdown), role);
		String xpath = xpathProviderNo + xpathAction;
		findWaitClickByXpath(driver, webDriverWait, xpath);
    }

    public static String optionSelected(String xpath)
    {
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
        Select dropdownPrimary = new Select(driver.findElement(By.xpath(xpath)));
        String primaryRoleSelected = dropdownPrimary.getFirstSelectedOption().getText();
        return primaryRoleSelected;
    }

    @Test
    public void assignRolesClassicUITest()
    {
        accessAdministrationSectionClassicUI(driver, webDriverWait, "User Management", "Assign Role to Provider");
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='keyword']")));
        driver.findElement(By.xpath("//input[@name='keyword']")).sendKeys(drApple.lastName);
        driver.findElement(By.xpath("//input[@name='search']")).click();

        //Add admin role
        String xpathAdd = "//following-sibling::td/input[@value='Add']";
        assignRoles(xpathDropdown, xpathProvider, "admin", xpathAdd);
        String messageAdd = "Role 3 is added. (" + drApple.providerNo + ")";//For some reason it was changed to show the role value.
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
        dropdownSelectByValue(driver, webDriverWait, By.id("primaryRoleProvider"), drApple.providerNo);
        dropdownSelectByVisibleText(driver, webDriverWait, By.id("primaryRoleRole"), "admin");
        driver.findElement(By.xpath("//input[@value='Set Primary Role']")).click();
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='keyword']", drApple.lastName);
		findWaitClickByXpath(driver, webDriverWait, "//input[@name='search']");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., 'Yes')]")));
        Assert.assertTrue(PageUtil.isExistsBy(By.xpath("//td[contains(., 'Yes')]"), driver));

        String path = "//td[contains(., 'Yes')]/preceding-sibling::td//select[@name='roleNew']";
        String primaryRoleSelected = optionSelected(path);
        Assert.assertEquals("Admin is NOT added as primary role successfully.", "admin", primaryRoleSelected);

        //Delete Role Nurse.
        String xpathDelete = "(//td[contains(., '" + drApple.providerNo + "')])[2]//following-sibling::td/input[@value='Delete']";
        driver.findElement(By.xpath(xpathDelete)).click();
        String messageDelete = "Role nurse is deleted. (" + drApple.providerNo + ")";
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//font[contains(., '" + messageDelete + "')]")));
        Assert.assertTrue("Role nurse is NOT deleted successfully.",
                PageUtil.isExistsBy(By.xpath("//font[contains(., '" + messageDelete + "')]"), driver));
    }
}