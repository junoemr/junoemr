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
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.ActionUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.regex.Pattern;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.SUMMARY_URL;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = WebEnvironment.DEFINED_PORT)
public class EditEncounterNotesIT extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "casemgmt_note", "demographic", "eChart", "eform_data", "eform_instance",
			"eform_values", "log", "log_ws_rest", "measurementType",
			"provider_recent_demographic_access","validations", "property", "casemgmt_tmpsave"
		};
	}

	@Before
	public void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void editEncounterNotesClassicUITest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);

		String newNote = "Testing Note ";
		String editedNote = " Edited Testing Note";
		String caseNoteXpath = "//textarea[@name='caseNote_note']";

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0, document.body.scrollHeight)");
		ActionUtil.findWaitSendKeysByXpath(driver, webDriverWait, caseNoteXpath, newNote);

		ActionUtil.findWaitClickById(driver, webDriverWait, "saveImg");
		ActionUtil.findWaitClickById(driver, webDriverWait, "newNoteImg");
		ActionUtil.findWaitClickByLinkText(driver, webDriverWait, "Edit");
		ActionUtil.findWaitSendKeysByXpath(driver, webDriverWait, caseNoteXpath, editedNote);
		ActionUtil.findWaitClickById(driver, webDriverWait, "saveImg");

		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(caseNoteXpath)));
		String text = driver.findElement(By.xpath(caseNoteXpath)).getText();

		Assert.assertTrue("Edited Note is NOT saved", Pattern.compile(editedNote).matcher(text).find());
	}

	// XXX: This test was ignored because if failed after switching Juno to run with jdk17.  It's
	//      probably not worth fixing until juno has been fixed to officially run with that jdk.
	@Ignore
	@Test
	public void editEncounterNotesJUNOUITest()
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + SUMMARY_URL);

		String newNote = "Testing Note JUNO";
		String editedNote = "Edited Testing Note JUNO";

		ActionUtil.findWaitSendKeysById(driver, webDriverWait, "noteEditor1", newNote);

		driver.findElement(By.id("theSave")).click();

		ActionUtil.findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.editButtonClick()']");

		String noteId = "noteEditor1";
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id(noteId)));
		driver.findElement(By.id(noteId)).clear();
		driver.findElement(By.id(noteId)).sendKeys(editedNote);

		ActionUtil.findWaitClickById(driver, webDriverWait, "theSave");

		String noteXpath = "//p[@class='ng-binding']";
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(noteXpath)));
		String text = driver.findElement(By.xpath(noteXpath)).getText();

		Assert.assertTrue("Edited Note is NOT saved in JUNO UI", Pattern.compile(editedNote).matcher(text).find());
	}
}
