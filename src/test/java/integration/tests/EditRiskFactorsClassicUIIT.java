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
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

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
public class EditRiskFactorsClassicUIIT extends SeleniumTestBase
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
	public void editRiskFactorsTest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String riskFactorsCPP = "Risk Factors in CPP";
		String riskFactorsEncounter = "Risk Factors in Encounter";
		String editedriskFactorsCPP = "Edited Risk Factors in CPP";
		String archivedriskFactors = "Archived Risk Factors";
		String startDate = "2020-01-01";
		String resolutionDate = "2021-01-01";
		PageUtil.switchToLastWindow(driver);

		//Add Risk Factors Notes
		driver.findElement(By.xpath("//div[@id='menuTitleriskFactors']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(riskFactorsEncounter);
		driver.findElement(By.id("startdate")).sendKeys(startDate);
		driver.findElement(By.id("resolutiondate")).sendKeys(resolutionDate);
		driver.findElement(By.xpath("//input[@title='Copy to Current Note']")).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(riskFactorsCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		driver.findElement(By.id("saveImg")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(riskFactorsCPP)));
		Assert.assertTrue("Risk Factors Note is NOT Added in CPP successfully",
				PageUtil.isExistsBy(By.linkText(riskFactorsCPP), driver));

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(., '" + riskFactorsEncounter + "')]")));
 		Assert.assertTrue("Risk Factors Note is NOT Copied in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + riskFactorsEncounter + "')]"), driver));

		//Edit Risk Factors Note
		driver.findElement(By.linkText(riskFactorsCPP)).click();
		driver.findElement(By.id("noteEditTxt")).clear();
		driver.findElement(By.id("noteEditTxt")).sendKeys(editedriskFactorsCPP);
		driver.findElement(By.xpath("//form[@id='frmIssueNotes']//descendant::input[@title='Sign & Save']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(editedriskFactorsCPP)));
		Assert.assertTrue("Risk Factors Note is NOT Edited in CPP successfully",
				PageUtil.isExistsBy(By.linkText(editedriskFactorsCPP), driver));

		//Archive Risk Factors Note
		driver.findElement(By.xpath("//div[@id='menuTitleriskFactors']//descendant::a[contains(., '+')]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditTxt")));
		driver.findElement(By.id("noteEditTxt")).sendKeys(archivedriskFactors);
		driver.findElement(By.xpath("//input[@title='Archive']")).click();
		clickWaitSwitchToLast(driver, webDriverWait, By.linkText("Risk Factors"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(., '" + archivedriskFactors + "')]")));
		Assert.assertTrue("Risk Factors Note is NOT Archived successfully",
				PageUtil.isExistsBy(By.xpath("//div[contains(., '" + archivedriskFactors + "')]"), driver));
	}
}
