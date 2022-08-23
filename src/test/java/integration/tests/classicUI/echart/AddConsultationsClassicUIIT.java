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

package integration.tests.classicUI.echart;

import static integration.tests.util.junoUtil.Navigation.CONSULTATION_URL;
import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByVisibleText;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddConsultationsClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "consultationRequestExt", "consultationRequests", "demographic", "log",
			"measurementType", "program", "validations", "log_ws_rest", "property"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void addConsultationsClassicUITest()
			throws InterruptedException
	{
		String serviceName = "Cardiology";
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String eChartWindowHandle = driver.getWindowHandle();
		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//div[@id='menuTitleconsultation']//descendant::a[contains(., '+')]"));
		dropdownSelectByVisibleText(driver, webDriverWait, By.id("service"), serviceName);
		findWaitClickByXpath(driver, webDriverWait, "//input[@name='submitSaveOnly']");

		//** Verify under Consultations section on eChart page. **
		PageUtil.switchToWindow(eChartWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(serviceName)));
		Assert.assertTrue(serviceName + " is NOT added under Consultations successfully.",
				PageUtil.isExistsBy(By.linkText(serviceName), driver));

		//** Verify from Consultations top menu. **
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + CONSULTATION_URL);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(serviceName)));
		Assert.assertTrue(serviceName + " is NOT added under Consultations successfully.",
				PageUtil.isExistsBy(By.linkText(serviceName), driver));
	}
}