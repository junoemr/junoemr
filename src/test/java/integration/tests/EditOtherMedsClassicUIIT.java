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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditOtherMedsClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"casemgmt_cpp", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
			"casemgmt_note_ext", "eChart", "hash_audit", "log", "admission", "demographic",
			"log_ws_rest", "measurementType", "partial_date", "property", "validations"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void editOtherMedsTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String otherMedsCPP = "Other Meds in CPP";
		String otherMedsEncounter = "Other Meds in Encounter";
		String editedOtherMedsCPP = "Edited Other Meds in CPP";
		String archivedOtherMeds = "Archived Other Meds";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		PageUtil.switchToLastWindow(driver);

		//Add Other Meds Notes
		driver.findElement(By.xpath("//div[@id='menuTitleoMeds']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(otherMedsEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(otherMedsCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		driver.findElement(By.id("saveImg")).click();
		Assert.assertTrue("Other Meds Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(otherMedsCPP), driver));
 		Assert.assertTrue("Other Meds Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + otherMedsEncounter + "')]"), driver));

		//Edit Other Meds Note
		driver.findElement(By.linkText(otherMedsCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedOtherMedsCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		Assert.assertTrue("Other Meds Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedOtherMedsCPP), driver));

		//Archive Other Meds Note
		driver.findElement(By.xpath("//div[@id='menuTitleoMeds']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedOtherMeds);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		driver.findElement(By.linkText("Other Meds")).click();
		PageUtil.switchToLastWindow(driver);
		Assert.assertTrue("Other Meds Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedOtherMeds + "')]"), driver));
	}
}
