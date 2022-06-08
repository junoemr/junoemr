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

package integration.tests.classicUI.echart;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysById;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
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
public class EditCPPNotesClassicIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]
			{
				"admission", "casemgmt_cpp", "casemgmt_issue", "casemgmt_note", "casemgmt_note_ext",
				"casemgmt_issue_notes", "demographic", "eChart", "hash_audit",  "log", "log_ws_rest",
				"measurementType", "partial_date", "validations"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void editSocialHistoryTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String socialHistoryInCPP = "Social History Note in CPP";
		String socialHistoryInEncounter = "Social History Note in Encounter";
		String editedSocialHistoryNoteInCPP = "Edited Social History Note in CPP";
		String archivedSocialHistory = "Archived Social History Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		PageUtil.switchToLastWindow(driver);

		//Add Social History Notes
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='divR1I1']//descendant::a[contains(., '+')]");
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", socialHistoryInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(socialHistoryInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		findWaitClickById(driver, webDriverWait, "saveImg");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(socialHistoryInCPP)));
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
		driver.findElement(By.xpath("//div[@id='divR1I1']//descendant::a[contains(., '+')]")).click();
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", archivedSocialHistory);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Social History"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(., '" + archivedSocialHistory + "')]")));
		Assert.assertTrue("Social History Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedSocialHistory + "')]"), driver));
	}
	@Test
	public void editMedicalHistoryTest()
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

		//Add Medical History Notes
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='divR1I2']//descendant::a[contains(., '+')]");
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", medicalHistoryInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.id("proceduredate")).sendKeys(precedureDate);
		driver.findElement(By.id("treatment")).sendKeys(treatment);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("lifestage"), "Newborn: Birth to 28 days");
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("hidecpp"), "No");
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(medicalHistoryInCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		findWaitClickById(driver, webDriverWait, "saveImg");//driver.findElement(By.id("saveImg")).click();
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
		driver.findElement(By.xpath("//div[@id='divR1I2']//descendant::a[contains(., '+')]")).click();
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", archivedMedicalHistory);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Medical History"));
		Assert.assertTrue("Medical History Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedMedicalHistory + "')]"), driver));
	}

	@Test
	public void editOngoingConcernsTest()
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

		//Add Ongoing Concerns Notes
		findWaitClick(driver, webDriverWait, By.xpath("//div[@id='divR2I1']//descendant::a[contains(., '+')]"));
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", ongoingConcernsInEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.id("problemdescription")).sendKeys(problemDesc);
		driver.findElement(By.id("problemstatus")).sendKeys(problemStatus);
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("lifestage"), "Newborn: Birth to 28 days");
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("hidecpp"), "No");
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(ongoingConcernsInCPP);
		findWaitClick(driver, webDriverWait, By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']"));
		findWaitClick(driver, webDriverWait, By.id("saveImg"));

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(ongoingConcernsInCPP)));
		Assert.assertTrue("Ongoing Concerns Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(ongoingConcernsInCPP), driver));

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(., '" + ongoingConcernsInEncounter + "')]")));
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
		driver.findElement(By.xpath("//div[@id='divR2I1']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedOngoingConcerns);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Ongoing Concerns"));
		Assert.assertTrue("Ongoing Concerns Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedOngoingConcerns + "')]"), driver));
	}

	@Test
	public void editRemindersTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String remindersInCPP = "Reminders Note in CPP";
		String remindersInEncounter = "Reminders Note in Encounter";
		String editedRemindersNoteInCPP = "Edited Reminders History Note in CPP";
		String archivedReminder = "Archived Reminders Note";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";

		PageUtil.switchToLastWindow(driver);

		//Add Reminders Notes
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='divR2I2']//descendant::a[contains(., '+')]");
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", remindersInEncounter);//driver.findElement(By.id("noteEditTxt")).sendKeys(remindersInEncounter);
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
		driver.findElement(By.xpath("//div[@id='divR2I2']//descendant::a[contains(., '+')]")).click();
		findWaitSendKeysById(driver, webDriverWait, "noteEditTxt", archivedReminder);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Reminders"));
		Assert.assertTrue("Reminders Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedReminder + "')]"), driver));
	}

}