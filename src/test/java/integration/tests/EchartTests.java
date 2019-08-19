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

import integration.tests.sql.SqlFiles;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.regex.Pattern;

public class EchartTests extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";

	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "demographic",
				"demographicArchive", "demographiccust", "log", "program", "provider_recent_demographic_access",
				"casemgmt_note", "casemgmt_cpp", "casemgmt_issue", "casemgmt_note_ext", "casemgmt_note_link", "casemgmt_note_lock",
				"casemgmt_tmpsave", "validations", "measurementType", "eChart");

		loadSpringBeans();
		DatabaseUtil.createTestDemographic();
	}

	@Test
	public void testWritingNote() throws InterruptedException
	{
		// login
		if (!Navigation.isLoggedIn(driver))
		{
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}

		driver.get(Navigation.OSCAR_URL + ECHART_URL);

		// create new encounter note
		String noteId = null;
		if (PageUtil.isExistsBy(By.xpath("//textarea[@name='caseNote_note']"), driver))
		{
			noteId = driver.findElement(By.xpath("//textarea[@name='caseNote_note']")).getAttribute("id");
		}

		WebElement newNoteButton = driver.findElement(By.id("newNoteImg"));
		newNoteButton.click();

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
			Assert.assertEquals("Create new note. FAIL", noteId, newNote.getAttribute("id"));
		}
		logger.info("Create new note. OK");

		//write in note
		UUID myUUID = UUID.randomUUID();
		newNote.sendKeys(myUUID.toString());
		Assert.assertTrue("Write to encounter note. FAIL", !newNote.getText().isEmpty());
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
		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		Thread.sleep(5000);
		Assert.assertTrue("Sign and save note. FAILED",
				PageUtil.isExistsBy(By.xpath("//*[contains(., '" + myUUID + "') and contains(., 'Signed on') and contains(@id, 'txt')]"), driver));
		logger.info("Sign and save note. OK");
	}
}
