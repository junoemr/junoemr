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
import integration.tests.util.seleniumUtil.PageUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysById;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddEformsClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "billingservice", "caisi_role", "demographic", "documentDescriptionTemplate",
			"eform_data", "eform_values", "Facility", "issue", "log", "log_ws_rest", "measurementType",
			"LookupList", "LookupListItem", "OscarJob", "OscarJobType", "program_provider", "property", "provider",
			"providerArchive", "providerbillcenter", "ProviderPreference", "roster_status", "security", "secUserRole",
			"tickler_text_suggest", "validations", "log_ws_rest", "provider_recent_demographic_access"
		};
	}

	@Before
	public void setup()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
		databaseUtil.createTestProvider();
	}

	@Test
	public void addFormsTest()
			throws InterruptedException
	{
		String subject = "EFormTest";
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		findWaitClickByXpath(driver, webDriverWait, "//div[@id='menuTitleeforms']//descendant::a[contains(., '+')]");
		PageUtil.switchToLastWindow(driver);
		findWaitClick(driver, webDriverWait, By.linkText("letter"));
		PageUtil.switchToLastWindow(driver);
		findWaitSendKeysById(driver, webDriverWait, "subject", subject);
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Submit']");
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(subject)));
		Assert.assertTrue("Eform Letter is NOT added successfully.", PageUtil.isExistsBy(By.partialLinkText(subject), driver));
	}
}
