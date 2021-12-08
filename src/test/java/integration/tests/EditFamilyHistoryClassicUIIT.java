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

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditFamilyHistoryClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
				"casemgmt_cpp", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
				"casemgmt_note_ext", "eChart", "hash_audit", "log"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void editFamilyHistoryTest()
	{
		String familyHistory = "Family History";
		String familyHistoryId = "menuTitlefamHistory";
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		editCPPNoteTest(familyHistory, familyHistoryId);
	}

	public static void editCPPNoteTest(String cppType, String cppTypeID)
	{
		String noteCPP = cppType + " in CPP";
		String noteEncounter = cppType + " in Encounter";
		String editedNoteCPP = "Edited " + noteCPP;
		String archivedNote = "Archived " + cppType;
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		String eChartWindowHandle = driver.getWindowHandle();
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();

		//Add Notes
		driver.findElement(
			By.xpath("//div[@id='" + cppTypeID + "']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(noteEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(noteCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
		driver.findElement(By.id("saveImg")).click();
		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);//wait until note is saved.
		Assert.assertTrue(cppType + " Note is NOT Added in CPP successfully",
			PageUtil.isExistsBy(By.linkText(noteCPP), driver));
		Assert.assertTrue(cppType + " Note is NOT Copied in Encounter note successfully",
			PageUtil.isExistsBy(By.xpath("//div[contains(., '" + noteEncounter + "')]"), driver));

		//Edit Note
		driver.findElement(By.linkText(noteCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedNoteCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue(cppType + " Note is NOT Edited in CPP successfully",
			PageUtil.isExistsBy(By.linkText(editedNoteCPP), driver));

		//Archive Note
		driver.findElement(By.xpath("//div[@id='" + cppTypeID + "']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedNote);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText(cppType)).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue(cppType + " Note is NOT Archived successfully",
			PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedNote + "')]"), driver));
	}
}
