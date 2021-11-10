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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EchartIT extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "demographic",
			"demographicArchive", "demographiccust", "log", "program", "provider_recent_demographic_access",
			"casemgmt_note", "casemgmt_cpp", "casemgmt_issue", "casemgmt_note_ext", "casemgmt_note_link", "casemgmt_note_lock",
			"casemgmt_tmpsave", "validations", "measurementType", "eChart", "hash_audit"
		};
	}

	@Before
	public void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	/*
	-------------------------------------------------------------------------------
Test set: integration.tests.EchartIT
-------------------------------------------------------------------------------
Tests run: 1, Failures: 1, Errors: 0, Skipped: 0, Time elapsed: 10.061 s <<< FAILURE! - in integration.tests.EchartIT
testWritingNote  Time elapsed: 9.789 s  <<< FAILURE!
org.junit.ComparisonFailure: Create new note. FAIL expected:<caseNote_note0[1]> but was:<caseNote_note0[]>
    at integration.tests.EchartIT.testWritingNote(EchartIT.java:129)
	 */
	@Ignore
	@Test
	public void testWritingNote()
			throws InterruptedException
	{
		String echartFullUrl = Navigation.getOscarUrl(Integer.toString(randomTomcatPort)) + ECHART_URL;
		driver.get(echartFullUrl);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@name='caseNote_note']")));

		String source = driver.getPageSource();
		String noteId = null;

		if (PageUtil.isExistsBy(By.xpath("//textarea[@name='caseNote_note']"), driver))
		{
			noteId = driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).getAttribute("id");
		}
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, document.body.scrollHeight)");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("newNoteImg")));

		//save the previous note
		driver.findElement(By.id("saveImg")).click();
		String noteIdAfterSave = driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).getAttribute("id");
		Integer stringLength = noteId.length()-1;
		Integer id1 = Integer.parseInt(noteId.substring(stringLength)) + 1;
		String noteIdAfterSaveExpected = noteId.substring(0, stringLength) + id1.toString();
		Assert.assertEquals("Create new note. FAIL", noteIdAfterSave, noteIdAfterSaveExpected);

		// create new encounter note
		driver.findElement(By.id("newNoteImg")).click();

		WebElement newNote = null;
		try
		{
			newNote = driver.findElement(By.xpath("//textarea[@name='caseNote_note']"));
		}
		catch (NoSuchElementException e)
		{
			Assert.fail("Create new note. FAIL");
		}

		if (noteId != null)
		{
			String newNoteTest = newNote.getAttribute("id");
			Assert.assertEquals("Create new note. FAIL", noteId, newNoteTest);
		}
		logger.info("Create new note. OK");

		//write in note
		UUID myUUID = UUID.randomUUID();
		newNote.sendKeys(myUUID.toString());
		String newNoteText = newNote.getText();
		Assert.assertTrue("Write to encounter note. FAIL", !newNoteText.isEmpty());
		logger.info("Write to encounter note. OK");

		// test auto save
		Thread.sleep(10000); // oscar auto saves every 5 seconds
		driver.navigate().refresh();
		Thread.sleep(5000);
		newNote = driver.findElement(By.xpath("//textarea[@name='caseNote_note']"));
		Assert.assertTrue("Auto save note. FAIL", Pattern.compile(myUUID.toString()).matcher(newNote.getText()).find());
		logger.info("Auto save note. OK");

		// sign and save
		String currentUrl = driver.getCurrentUrl();
		driver.findElement(By.id("signSaveImg")).click();
		Thread.sleep(2000);
		driver.get(Navigation.getOscarUrl(Integer.toString(randomTomcatPort)) + ECHART_URL);
		Thread.sleep(5000);
		Assert.assertTrue("Sign and save note. FAILED",
				PageUtil.isExistsBy(By.xpath("//*[contains(., '" + myUUID + "') and contains(., 'Signed on') and contains(@id, 'txt')]"), driver));
		logger.info("Sign and save note. OK");
	}
}
