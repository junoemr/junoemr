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
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditUnresolvedIssuesClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
				"casemgmt_cpp", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
				"casemgmt_note_ext", "eChart", "hash_audit", "log", "property", "measurementType",
				"demographic", "admission", "log_ws_rest","validations"

		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void AddUnresolvedIssuesTest() throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String issue = "PERINATAL INTEST PERFOR";
		String issueChange = "MALIGN NEOPL VULVA";
		PageUtil.switchToLastWindow(driver);

		//Add Unresolved Issue
		driver.findElement(By.id("issueAutocomplete")).sendKeys(issue);
		webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#issueTable div.enTemplate_name_auto_complete li"), "7776 - " + issue));
		driver.findElement(By.id("issueAutocomplete")).sendKeys(Keys.ENTER);
		driver.findElement(By.id("asgnIssues")).click();
		driver.findElement(By.id("saveImg")).click();
		driver.navigate().refresh();
		Assert.assertTrue("Unresolved issue is NOT added to Unresolved Issues section successfully",
				PageUtil.isExistsBy(By.partialLinkText(issue), driver));

		driver.findElement(By.id("displayUnresolvedIssuesButton")).click();
 		Assert.assertTrue("Unresolved issue is NOT added in Encounter note successfully",
				PageUtil.isExistsBy(By.xpath("//tbody[@id='setIssueListBody']//descendant::a[contains(., '" + issue + "')]"), driver));

		//Edit Unresolved Issue
		driver.findElement(By.linkText("Change")).click();
		driver.findElement(By.id("issueAutocomplete")).sendKeys(issueChange);
		webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#issueTable div.enTemplate_name_auto_complete li"), "1844 - " + issueChange));
		driver.findElement(By.id("issueAutocomplete")).sendKeys(Keys.ENTER);
		webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("#issueTable div.enTemplate_name_auto_complete li")));
		findWaitClickById(driver, webDriverWait, "changeIssues");
		findWaitClickById(driver, webDriverWait, "saveImg");
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(issueChange)));
		Assert.assertTrue("Changed unresolved issue is NOT added to Unresolved Issues section successfully",
			PageUtil.isExistsBy(By.partialLinkText(issueChange), driver));

		driver.findElement(By.id("displayUnresolvedIssuesButton")).click();
		Assert.assertTrue("Chaned unresolved issue is NOT added in Encounter note successfully",
			PageUtil.isExistsBy(By.xpath("//tbody[@id='setIssueListBody']//descendant::a[contains(., '" + issueChange + "')]"), driver));

	}
}
