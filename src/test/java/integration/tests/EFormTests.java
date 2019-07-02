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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class EFormTests extends SeleniumTestBase
{
	private static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&appointmentNo=&demographicNo=1&curProviderNo=&reason=Tel-Progress+Note&encType=&curDate=2019-4-17&appointmentDate=&startTime=&status=";
	private static String EFORM_URL = "/eform/efmformslistadd.jsp?demographic_no=1&appointment=&parentAjaxId=eforms";

	@BeforeClass
	public static void setup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, InterruptedException
	{
		SchemaUtils.restoreTable("admission", "demographic",
				"demographicArchive", "demographiccust", "log", "program", "provider_recent_demographic_access",
				"casemgmt_note", "casemgmt_cpp", "casemgmt_issue", "casemgmt_note_ext", "casemgmt_note_link", "casemgmt_note_lock",
				"casemgmt_tmpsave", "validations", "measurementType", "eChart", "eform", "eform_values");

		loadSpringBeans();
		DatabaseUtil.createTestDemographic();

		SchemaUtils.loadFileIntoMySQL(SqlFiles.EFORM_ADD_TRAVLE_FORM_V4);
	}

	@Before
	public void login()
	{
		if(!Navigation.isLoggedIn(driver))
		{
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}
	}

	@Test
	public void canAddTravel_Form_v4EForm() throws InterruptedException
	{
		//navigate to eform addition page
		String oldUrl = driver.getCurrentUrl();
		driver.get(Navigation.OSCAR_URL + EFORM_URL);
		PageUtil.waitForPageChange(oldUrl, driver);
		Assert.assertFalse("expecting eform page but found error page!", PageUtil.isErrorPage(driver));
		logger.info("Navigate to eform add page. OK");

		//open eform
		WebElement eformButton = driver.findElement(By.xpath("//a[contains(., 'travel_from_v4')]"));
		Assert.assertNotNull(eformButton);

		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		eformButton.click();
		Thread.sleep(2000);
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);

		Assert.assertEquals("more than one window opened when opening eform", 1, newWindows.size());
		PageUtil.switchToWindow(newWindows.get(0), driver);
		Thread.sleep(2000);

		Assert.assertFalse("got error page on eform page", PageUtil.isErrorPage(driver));
		logger.info("Open eform travel_form_v4. OK");

		driver.findElement(By.xpath("//input[@id='SubmitButton']")).click();
		PageUtil.switchToWindow(currWindowHandle, driver);
		logger.info("Submit eform travel_form_v4. OK");

		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		Thread.sleep(5000);
		Assert.assertNotNull(driver.findElement(By.xpath("//a[contains(., 'travel_from_v4:')]")));
		logger.info("Eform added to Echart? OK");
	}
}
