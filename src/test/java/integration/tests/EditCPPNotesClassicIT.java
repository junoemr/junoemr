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

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
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
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditCPPNotesClassicIT extends SeleniumTestBase
{
	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@After
	public void cleanup()
			throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable("casemgmt_cpp", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
				"casemgmt_note_ext", "eChart", "hash_audit", "log");
	}

	@Test
	public void editSocialHistoryTest()
			throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String socialHistoryInCPP = "Social History Note in CPP";
		String socialHistoryInEncounter = "Social History Note in Encounter";
		String editedSocialHistoryNoteInCPP = "Edited Social History Note in CPP";
		String archivedSocialHistory = "Archived Social History Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		//Add Social History Notes
		driver.findElement(By.xpath("//div[@id='divR1I1']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(socialHistoryInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(socialHistoryInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("saveImg")).click();
		Thread.sleep(1000);//wait until note is saved.
		Assert.assertTrue("Social History Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(socialHistoryInCPP), driver));
 		Assert.assertTrue("Social History Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + socialHistoryInEncounter + "')]"), driver));

		//Edit Social History Note
		driver.findElement(By.linkText(socialHistoryInCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedSocialHistoryNoteInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue("Social History Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedSocialHistoryNoteInCPP), driver));

		//Archive Social History Note
		driver.findElement(By.xpath("//div[@id='divR1I1']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedSocialHistory);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText("Social History")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Social History Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedSocialHistory + "')]"), driver));
	}
	@Test
	public void editMedicalHistoryTest()
			throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String medicalHistoryInCPP = "Medical History Note in CPP";
		String medicalHistoryInEncounter = "Medical History Note in Encounter";
		String editedMedicalHistoryNoteInCPP = "Edited Medical History Note in CPP";
		String archivedMedicalHistory = "Archived Medical History Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		String precedureDate = "2020-12-12";
		String treatment = "Heat";

		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		//Add Medical History Notes
		driver.findElement(By.xpath("//div[@id='divR1I2']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(medicalHistoryInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.id("proceduredate")).sendKeys(precedureDate);
		driver.findElement(By.id("treatment")).sendKeys(treatment);
		dropdownSelectByVisibleText(driver, By.id("lifestage"), "Newborn: Birth to 28 days");
		dropdownSelectByVisibleText(driver, By.id("hidecpp"), "No");
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(medicalHistoryInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("saveImg")).click();
		Assert.assertTrue("Medical History Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(medicalHistoryInCPP), driver));
		Assert.assertTrue("Medical History Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + medicalHistoryInEncounter + "')]"), driver));

		//Edit Medical History Note
		driver.findElement(By.linkText(medicalHistoryInCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedMedicalHistoryNoteInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue("Medical History Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedMedicalHistoryNoteInCPP), driver));

		//Archive Medical History Note
		driver.findElement(By.xpath("//div[@id='divR1I2']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedMedicalHistory);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText("Medical History")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Medical History Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedMedicalHistory + "')]"), driver));
	}

	@Test
	public void editOngoingConcernsTest()
			throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String ongoingConcernsInCPP = "Ongoing Concerns Note in CPP";
		String ongoingConcernsInEncounter = "Ongoing Concerns Note in Encounter";
		String editedOngoingConcernsNoteInCPP = "Edited Ongoing Concerns History Note in CPP";
		String archivedOngoingConcerns = "Archived Ongoing Concerns Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		String problemDesc = "High Blood Pressure";
		String problemStatus = "Active";

		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		//Add Ongoing Concerns Notes
		driver.findElement(By.xpath("//div[@id='divR2I1']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(ongoingConcernsInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.id("problemdescription")).sendKeys(problemDesc);
		driver.findElement(By.id("problemstatus")).sendKeys(problemStatus);
		dropdownSelectByVisibleText(driver, By.id("lifestage"), "Newborn: Birth to 28 days");
		dropdownSelectByVisibleText(driver, By.id("hidecpp"), "No");
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(ongoingConcernsInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Thread.sleep(1000);
		driver.findElement(By.id("saveImg")).click();
		Thread.sleep(1000);
		Assert.assertTrue("Ongoing Concerns Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(ongoingConcernsInCPP), driver));
		Assert.assertTrue("Ongoing Concerns Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + ongoingConcernsInEncounter + "')]"), driver));

		//Edit Ongoing Concerns Note
		driver.findElement(By.linkText(ongoingConcernsInCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedOngoingConcernsNoteInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue("Ongoing Concerns Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedOngoingConcernsNoteInCPP), driver));

		//Archive Ongoing Concerns Note
		driver.findElement(By.xpath("//div[@id='divR2I1']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedOngoingConcerns);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText("Ongoing Concerns")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Ongoing Concerns Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedOngoingConcerns + "')]"), driver));
	}

	@Test
	public void editRemindersTest()
			throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String remindersInCPP = "Reminders Note in CPP";
		String remindersInEncounter = "Reminders Note in Encounter";
		String editedRemindersNoteInCPP = "Edited Reminders History Note in CPP";
		String archivedReminder = "Archived Reminders Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";

		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);

		//Add Reminders Notes
		driver.findElement(By.xpath("//div[@id='divR2I2']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(remindersInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(remindersInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		driver.findElement(By.id("saveImg")).click();
		Assert.assertTrue("Reminders Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(remindersInCPP), driver));
		Assert.assertTrue("Reminders Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + remindersInEncounter + "')]"), driver));

		//Edit Reminders Note
		driver.findElement(By.linkText(remindersInCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedRemindersNoteInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue("Reminders Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedRemindersNoteInCPP), driver));

		//Archive Reminders Note
		driver.findElement(By.xpath("//div[@id='divR2I2']//descendant::a[@title='Add Item']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedReminder);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText("Reminders")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Reminders Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedReminder + "')]"), driver));
	}

}
