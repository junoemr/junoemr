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
import integration.tests.util.junoUtil.Navigation;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static integration.tests.EchartTests.ECHART_URL;

public class EditEncounterNotesTests extends SeleniumTestBase
{
	private static final String JUNO_URL = "/web/#!/record/2/summary";
	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();
		DatabaseUtil.createTestDemographic();
	}

	@AfterClass
	public static void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable("admission", "casemgmt_note", "demographic",
				"eChart", "eform_data", "eform_instance", "eform_values", "log", "log_ws_rest", "measurementType",
				"provider_recent_demographic_access","validations");
	}

	@Test
	public void editEncounterNotesClassicUITest() throws InterruptedException
	{
		driver.get(Navigation.OSCAR_URL + ECHART_URL);

		String newNote = "Testing Note";
		String editedNote = "Edited Testing Note";
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, document.body.scrollHeight)");

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@name='caseNote_note']")));
		driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).sendKeys(newNote);
		driver.findElement(By.id("saveImg")).click();
		driver.findElement(By.id("newNoteImg")).click();
		driver.findElement(By.linkText("Edit")).click();
		driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).sendKeys(editedNote);
		driver.findElement(By.id("saveImg")).click();
		String text = driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).getText();
		Assert.assertTrue("Edited Note is NOT saved", Pattern.compile(editedNote).matcher(text).find());
	}

	@Test
	public void editEncounterNotesJUNOUITest()
	{
		driver.get(Navigation.OSCAR_URL + JUNO_URL);

		String newNote = "Testing Note JUNO";
		String editedNote = "Edited Testing Note JUNO";
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("noteEditor2")));
		driver.findElement(By.id("noteEditor2")).sendKeys(newNote);
		driver.findElement(By.id("theSave")).click();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@ng-click='$ctrl.editButtonClick()']")));
		driver.findElement(By.xpath("//button[@ng-click='$ctrl.editButtonClick()']")).click();
		driver.findElement(By.id("noteEditor2")).clear();
		driver.findElement(By.id("noteEditor2")).sendKeys(editedNote);
		driver.findElement(By.id("theSave")).click();
		String text = driver.findElement(By.xpath("//p[@class='ng-binding']")).getText();
		Assert.assertTrue("Edited Note is NOT saved in JUNO UI", Pattern.compile(editedNote).matcher(text).find());
	}
}
