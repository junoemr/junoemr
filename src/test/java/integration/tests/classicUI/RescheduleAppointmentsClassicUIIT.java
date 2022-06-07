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

import static integration.tests.classicUI.AddPatientsClassicUIIT.mom;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.seleniumUtil.PageUtil;
import java.util.Set;
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

public class RescheduleAppointmentsClassicUIIT extends SeleniumTestBase
{
    @Override
    protected String[] getTablesToRestore()
    {
        return new String[]{
            "admission", "appointment", "appointmentArchive", "billingservice", "caisi_role",
            "demographic", "documentDescriptionTemplate", "Facility", "issue", "log", "log_ws_rest",
			"LookupList", "LookupListItem", "measurementType", "OscarJob", "OscarJobType",
            "provider", "provider_recent_demographic_access", "providerbillcenter",
			"ProviderPreference", "providersite", "secUserRole", "site", "tickler_text_suggest",
			"property"
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
    public void rescheduleAppointmentTestsClassicUI()
    {
        // Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
        String currWindowHandle = driver.getWindowHandle();
        AddAppointmentsClassicUIIT addAppointmentsTests = new AddAppointmentsClassicUIIT();
        addAppointmentsTests.addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
        Assert.assertTrue("Appointments is NOT added successfully.",
                PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

        //Edit from "Edit An Appointment" page
        Set<String> oldWindowHandles = driver.getWindowHandles();
        PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles,
			webDriverWait);
        //Cut & Paste from 9:00 to 9:45
        driver.findElement(By.xpath("//input[@value='Cut']")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
		findWaitClickByXpath(driver, webDriverWait, "//a[@title='9:45 a.m. - 10:00 a.m.']");
        PageUtil.switchToLastWindow(driver);
        driver.findElement(By.id("pasteButton")).click();
        driver.findElement(By.id("addButton")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText("09:45")));
        String apptXpath = "//a[@title='9:45 a.m. - 10:00 a.m.']/../../td/a[contains(., '" + mom.lastName +"')]";
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(apptXpath)));
        Assert.assertTrue("Appointment is NOT Cut/Pasted to 9:45am successfully",
                PageUtil.isExistsBy(By.xpath(apptXpath), driver));

        //Copy & paste from 9:45 to 10:45
        PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles,
			webDriverWait);
        driver.findElement(By.xpath("//input[@value='Copy']")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
		findWaitClickByXpath(driver, webDriverWait, "//a[@title='10:45 a.m. - 11:00 a.m.']");
        PageUtil.switchToLastWindow(driver);
        driver.findElement(By.id("pasteButton")).click();
        driver.findElement(By.id("addButton")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.partialLinkText("09:45")));
        String apptCopyXpath = "//a[@title='10:45 a.m. - 11:00 a.m.']/../../td/a[contains(., '" + mom.lastName +"')]";
        Assert.assertTrue("Appointment is NOT Copied/Pasted to 10:45am successfully",
                PageUtil.isExistsBy(By.xpath(apptCopyXpath), driver));
    }
}