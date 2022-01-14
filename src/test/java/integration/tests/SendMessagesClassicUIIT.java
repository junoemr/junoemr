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
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.data.PatientTestCollection.patientLNames;
import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByLinkText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMessagesClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "billingservice", "caisi_role", "demographic", "documentDescriptionTemplate", "Facility",
			"issue", "log", "LookupList", "LookupListItem", "measurementType", "messagelisttbl", "messagetbl", "msgDemoMap",
			"OscarJob", "OscarJobType", "provider", "providerbillcenter", "ProviderPreference", "secUserRole",
			"tickler_text_suggest", "validations", "log_ws_rest", "property"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void sendMessagesBetweenClinicUsersTest()
	{
		String subject = "Message between users";
		clickWaitSwitchToLast(driver, webDriverWait, By.id("oscar_new_msg"));

		//** Send Message **
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Compose Message")));
		driver.findElement(By.linkText("Compose Message")).click();
		composeMessage(subject);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Send Message']");

		//** Verify from Sent Messages **
		findWaitClickByLinkText(driver, webDriverWait, "Back to Inbox");
		findWaitClickByLinkText(driver, webDriverWait, "Sent Messages");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subject)));
		Assert.assertTrue("Message is Not sent successfully.", PageUtil.isExistsBy(By.linkText(subject), driver));
		//** Verify from Inbox **
		findWaitClickByLinkText(driver, webDriverWait, "Refresh Inbox");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subject)));
		Assert.assertTrue("Message is NOT received.", PageUtil.isExistsBy(By.linkText(subject), driver));

		//** Archive message from Inbox **
		archiveMessage();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subject)));
		Assert.assertTrue("Message is NOT archived successfully.", PageUtil.isExistsBy(By.linkText(subject), driver));

		//** UnArchive Message **
		unarchiveMessage();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subject)));
		Assert.assertTrue("Message is NOT UnArchived successfully.", PageUtil.isExistsBy(By.linkText(subject), driver));
	}

	@Test
	public void sendMessagesPatientAttachedTest()
	{
		String subjectPatientAttached = "Message with Patient Attached";
		String patientLName = patientLNames[1];
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("oscar_new_msg")));
		clickWaitSwitchToLast(driver, webDriverWait, By.id("oscar_new_msg"));
		String currWindowHandle = driver.getWindowHandle();

		//** Send Message **
		findWaitClickByLinkText(driver, webDriverWait, "Compose Message");
		composeMessage(subjectPatientAttached);
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='keyword']", patientLName);
		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//input[@name='searchDemo']"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("2")));
		driver.findElement(By.linkText("2")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Send Message']");

		//** Verify from Sent Messages **
		findWaitClickByLinkText(driver, webDriverWait, "Back to Inbox");
		findWaitClickByLinkText(driver, webDriverWait, "Sent Messages");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subjectPatientAttached)));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., '" + patientLName + "')]")));
		Assert.assertTrue("Message with patient attached is Not sent successfully.",
				PageUtil.isExistsBy(By.linkText(subjectPatientAttached), driver) &&
					PageUtil.isExistsBy(By.xpath("//td[contains(., '" + patientLName + "')]"), driver));

		//** Verify from Inbox **
		findWaitClickByLinkText(driver, webDriverWait, "Refresh Inbox");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subjectPatientAttached)));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., '" + patientLName + "')]")));
		Assert.assertTrue("Message with patient attached is NOT received.",
				PageUtil.isExistsBy(By.linkText(subjectPatientAttached), driver) &&
					PageUtil.isExistsBy(By.xpath("//td[contains(., '" + patientLName + "')]"), driver));

		//** Archive message from Inbox **
		archiveMessage();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subjectPatientAttached)));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., '" + patientLName + "')]")));
		Assert.assertTrue("Message with patient attached is NOT archived successfully.",
				PageUtil.isExistsBy(By.linkText(subjectPatientAttached), driver)&&PageUtil.isExistsBy(By.xpath("//td[contains(., '" + patientLName + "')]"), driver));

		//** UnArchive Message **
		unarchiveMessage();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subjectPatientAttached)));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., '" + patientLName + "')]")));
		Assert.assertTrue("Message with patient attached is NOT UnArchived successfully.",
				PageUtil.isExistsBy(By.linkText(subjectPatientAttached), driver)&&PageUtil.isExistsBy(By.xpath("//td[contains(., '" + patientLName + "')]"), driver));
	}

	@Test
	public void sendMessagesPatientEchartTest()
	{
		String subjectEchart = "Message from eChart";
		String patientLName = patientLNames[0];
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//div[@id='menuTitlemsgs']//descendant::a[contains(., '+')]"));

		//** Send Message **
		composeMessage(subjectEchart);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Send Message']");

		//** Verify from Sent Messages **
		findWaitClickByLinkText(driver, webDriverWait, "Back to Inbox");
		findWaitClickByLinkText(driver, webDriverWait, "Sent Messages");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(subjectEchart)));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//td[contains(., '" + patientLName + "')]")));
		Assert.assertTrue("Message with patient attached is Not sent successfully.",
				PageUtil.isExistsBy(By.linkText(subjectEchart), driver) &&
					PageUtil.isExistsBy(By.xpath("//td[contains(., '" + patientLName + "')]"), driver));

		//** Verify from eChart page **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Message from eChart")));
		Assert.assertTrue("Message was NOT sent from eChart successfully.", PageUtil.isExistsBy(By.linkText("Message from eChart"), driver));
	}

	private void composeMessage(String subject)
	{
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='subject']", subject);
		findWaitSendKeysByXpath(driver, webDriverWait, "//textarea[@name='message']", "Test Message");
		driver.findElements(By.xpath("//input[@name='tblDFR1']")).get(1).click();
	}

	private void archiveMessage()
	{
		int size = driver.findElements(By.xpath("//input[@name='messageNo']")).size();
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='" + size + "']");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='archive']");
		findWaitClickByLinkText(driver, webDriverWait, "Deleted Message");
	}

	private void unarchiveMessage()
	{
		findWaitClickByXpath(driver, webDriverWait, "//input[@name='messageNo']");
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='unarchive']");
		findWaitClickByLinkText(driver, webDriverWait, "Refresh Inbox");
	}

}
