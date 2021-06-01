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
import javax.xml.crypto.Data;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static integration.tests.AddPatientsTests.mom;
import static integration.tests.AddPatientsTests.momFullNameJUNO;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

public class RescheduleAppointmentsTests extends SeleniumTestBase
{
	@Autowired
    DatabaseUtil databaseUtil;

    @Before
    public void setup() throws Exception
    {
        loadSpringBeans();
        databaseUtil.createTestDemographic();
        databaseUtil.createTestProvider();
        databaseUtil.createProviderSite();
    }

    @After
    public void cleanup() throws Exception
    {
        SchemaUtils.restoreTable("admission", "appointment","appointmentArchive", "billingservice", "caisi_role",
                "demographic", "documentDescriptionTemplate", "Facility", "issue", "log", "log_ws_rest", "LookupList",
                "LookupListItem", "measurementType", "OscarJob", "OscarJobType",
                "provider", "provider_recent_demographic_access", "providerbillcenter", "ProviderPreference", "providersite",
                "secUserRole", "site", "tickler_text_suggest" );
    }

    @Test
    public void rescheduleAppointmentTestsClassicUI()
            throws InterruptedException
    {
        // Add an appointment at 9:00-9:15 with demographic selected for tomorrow.
        String currWindowHandle = driver.getWindowHandle();
        AddAppointmentsTests addAppointmentsTests = new AddAppointmentsTests();
        addAppointmentsTests.addAppointmentsSchedulePage("09:00", currWindowHandle, mom.firstName);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
        Assert.assertTrue("Appointments is NOT added successfully.",
                PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

        //Edit from "Edit An Appointment" page
        Set<String> oldWindowHandles = driver.getWindowHandles();
        PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles);
        //Cut & Paste from 9:00 to 9:45
        driver.findElement(By.xpath("//input[@value='Cut']")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
        Thread.sleep(2000);
        driver.findElement(By.linkText("09:45")).click();
        PageUtil.switchToLastWindow(driver);
        driver.findElement(By.id("pasteButton")).click();
        driver.findElement(By.id("addButton")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText("09:45")));
        String apptXpath = "//a[@title='9:45 AM - 10:00 AM']/../../td/a[contains(., '" + mom.lastName +"')]";
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(apptXpath)));
        Assert.assertTrue("Appointment is NOT Cut/Pasted to 9:45am successfully",
                PageUtil.isExistsBy(By.xpath(apptXpath), driver));

        //Copy & paste from 9:45 to 10:45
        PageUtil.switchToNewWindow(driver, By.className("apptLink"), oldWindowHandles);
        driver.findElement(By.xpath("//input[@value='Copy']")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
        Thread.sleep(2000);
        driver.findElement(By.linkText("10:45")).click();
        PageUtil.switchToLastWindow(driver);
        driver.findElement(By.id("pasteButton")).click();
        driver.findElement(By.id("addButton")).click();
        PageUtil.switchToWindow(currWindowHandle, driver);
        Thread.sleep(2000);
        String apptCopyXpath = "//a[@title='10:45 AM - 11:00 AM']/../../td/a[contains(., '" + mom.lastName +"')]";
        Assert.assertTrue("Appointment is NOT Copied/Pasted to 10:45am successfully",
                PageUtil.isExistsBy(By.xpath(apptCopyXpath), driver));
    }

    @Test
    public void rescheduleAppointmentTestsJUNOUI()
            throws InterruptedException
    {
        // Add an appointment at 10:00-10:15 with demographic selected for the day after tomorrow.
        driver.findElement(By.xpath("//img[@alt='View Next DAY']")).click();
        String currWindowHandle = driver.getWindowHandle();
        AddAppointmentsTests addAppointmentsTests = new AddAppointmentsTests();
        addAppointmentsTests.addAppointmentsSchedulePage("10:00", currWindowHandle, mom.firstName);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(mom.lastName)));
        Assert.assertTrue("Appointments is NOT added successfully.",
                PageUtil.isExistsBy(By.partialLinkText(mom.lastName), driver));

        accessSectionJUNOUI(driver, "Schedule");
        webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@title='Next Day']")));
        driver.findElement(By.xpath("//button[@title='Next Day']")).click();
        driver.findElement(By.xpath("//button[@title='Next Day']")).click();
        Select providerDropDown = new Select(driver.findElement(By.id("schedule-select")));
        providerDropDown.selectByVisibleText("oscardoc, doctor");

        //Reschedule the appointment from 10:00am to 10:45am
        driver.findElement(By.xpath("//span[contains(., '" + momFullNameJUNO + "')]")).click();
        textEdit(driver, By.id("input-startTime"), "10:45 AM");
        driver.findElement(By.xpath("//button[contains(., 'Modify')]")).click();

        String timeFrameExpected = "10:45:00";
        driver.findElement(By.xpath("//span[contains(., '" + momFullNameJUNO + "')]")).click();
        String startTimeAfterReschedule = driver.findElement(By.id("input-startTime")).getAttribute("value");
        driver.findElement(By.xpath("//button[@title='Cancel']")).click();
        Assert.assertEquals("Appointment is NOT rescheduled from 10:00 to 10:45",
                timeFrameExpected.substring(0, 5), startTimeAfterReschedule.substring(0, 5));

        //Drag and Drop to 11:00
        Actions act = new Actions(driver);
        String startTimeDNDExpected = "11:00:00";
        WebElement apptBeforeDND = driver.findElement(By.xpath("//span[contains(., '" + momFullNameJUNO + "')]"));
        List<WebElement> schedulesRight = driver.findElements(By.className("fc-bgevent"));
        List<WebElement> schedulesLeft = driver.findElement((By.className("fc-slats"))).findElements(By.tagName("tr"));
        for (int i = 0; i < schedulesLeft.size(); i++ )
        {
            String timeFrame = schedulesLeft.get(i).getAttribute("data-time");
            if (timeFrame.equals(startTimeDNDExpected))
            {
                act.dragAndDrop(apptBeforeDND, schedulesRight.get(i)).build().perform();
                break;
            }
        }
        driver.findElement(By.xpath("//span[contains(., '" + momFullNameJUNO + "')]")).click();
        String startTimeAfterDND = driver.findElement(By.id("input-startTime")).getAttribute("value");
        Assert.assertEquals("Appointment is NOT rescheduled from 10:45 to 11:00",
                startTimeDNDExpected.substring(0, 5), startTimeAfterDND.substring(0, 5));
    }
}
