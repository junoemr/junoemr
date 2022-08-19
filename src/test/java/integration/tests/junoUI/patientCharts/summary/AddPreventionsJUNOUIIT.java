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

package integration.tests.junoUI.patientCharts.summary;

import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.addExamPreventions;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.addPrevention;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.EDITED_COMMENT;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_COMMENTS;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_DOSE;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_LOCATION;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_LOT;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_MANUFACTURE;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_NAME;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_NEVERREASON;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.ORIGINAL_ROUTE;
import static integration.tests.classicUI.echart.AddPreventionsClassicUIIT.XPATH;
import static integration.tests.util.junoUtil.Navigation.EXAM_PREVENTION_URL;
import static integration.tests.util.junoUtil.Navigation.PREVENTION_INJECTION_URL;
import static integration.tests.util.junoUtil.Navigation.PREVENTION_URL;
import static integration.tests.util.junoUtil.Navigation.SUMMARY_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitEdit;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import java.sql.SQLException;
import java.util.Set;
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
public class AddPreventionsJUNOUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "demographic", "demographicArchive", "demographiccust", "log", "preventions",
			"preventionsExt", "property", "provider_recent_demographic_access"
		};
	}

	@Before
	public void setup()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void addPreventionsTest()
	{
		// *** Add prevention ***
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + PREVENTION_INJECTION_URL);
		addPrevention();

		// window closes, find following URL and verify entry shows
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + PREVENTION_URL);

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]")));
		Assert.assertTrue("Can't find anything resembling an added prevention on page",
				PageUtil.isExistsBy(By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]"), driver));

		Assert.assertTrue("Can't find COVID-19 reference element on page", PageUtil.isExistsBy(
				By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]" +
						"//preceding::div[@class='headPrevention _nifty']//" +
						"child::p//" +
						"child::a[contains(@onclick, 'AddPreventionData.jsp?prevention=COVID-19&demographic_no=1')]"), driver));

		// *** Verify prevention ***

		// Attempt to view prevention and verify information is correct
		driver.findElement(By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]")).click();
		findWaitClickByXpath(driver, webDriverWait, "//div[contains(@onclick, 'AddPreventionData.jsp?id=')]");
		PageUtil.switchToLastWindow(driver);

		// Pull out current assigned values and make sure they match
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='name']")));
		String currentName = driver.findElement(By.xpath("//input[@name='name']")).getAttribute("value");
		String currentLocation = driver.findElement(By.xpath("//input[@name='location']")).getAttribute("value");
		String currentRoute = driver.findElement(By.xpath("//input[@name='route']")).getAttribute("value");
		String currentDose = driver.findElement(By.xpath("//input[@name='dose']")).getAttribute("value");
		String currentLot = driver.findElement(By.xpath("//input[@name='lot']")).getAttribute("value");
		String currentManufacture = driver.findElement(By.xpath("//input[@name='manufacture']")).getAttribute("value");
		String currentComments = driver.findElement(By.xpath("//textarea[@name='comments']")).getText();
		driver.findElement(By.xpath("//a[contains(@onclick, 'showHideNextDate')]")).click();
		String currentNeverReason = driver.findElement(By.xpath("//input[@name='neverReason']")).getAttribute("value");;

		Assert.assertEquals("Prevention name not updated successfully", ORIGINAL_NAME, currentName);
		Assert.assertEquals("Prevention location not updated successfully", ORIGINAL_LOCATION, currentLocation);
		Assert.assertEquals("Prevention route not updated successfully", ORIGINAL_ROUTE, currentRoute);
		Assert.assertEquals("Prevention dose not updated successfully", ORIGINAL_DOSE, currentDose);
		Assert.assertEquals("Prevention lot not updated successfully", ORIGINAL_LOT, currentLot);
		Assert.assertEquals("Prevention manufacture not updated successfully", ORIGINAL_MANUFACTURE, currentManufacture);
		Assert.assertEquals("Prevention comments not updated successfully", ORIGINAL_COMMENTS, currentComments);
		Assert.assertEquals("Prevention never reason field not updated successfully", ORIGINAL_NEVERREASON, currentNeverReason);

		//Verify on summary page.
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + SUMMARY_URL);
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("Notes")));
		findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.addBtnCallback()']");
		findWaitClickByXpath(driver, webDriverWait, "//button[@ng-click='$ctrl.toggleShowAllItems()']");
		Assert.assertTrue("Prevention not added successfully on Summary page.", PageUtil.isExistsBy(By.xpath("//a[@title='COVID-19 ']"), driver));
	}

	@Test
	public void editExamPreventionTest()
	{
		// *** Add prevention ***
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + EXAM_PREVENTION_URL);
		addExamPreventions();

		// window closes, find following URL and verify entry shows
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + PREVENTION_URL);
		Set<String> oldWindowHandles = driver.getWindowHandles();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH)));

		// Click on prevention to edit it
		Assert.assertTrue("Can't find anything resembling an added exam prevention on page", PageUtil.isExistsBy(
				By.xpath(XPATH), driver));

		PageUtil.switchToNewWindow(driver,
				By.xpath("//div[contains(@onclick, 'AddPreventionData.jsp?id=')]"), oldWindowHandles,
			webDriverWait);
		findWaitEdit(driver, webDriverWait, By.xpath("//textarea[@name='comments']"), EDITED_COMMENT);
		String currentComment = driver.findElement(By.xpath("//textarea[@name='comments']")).getAttribute("value");
		Assert.assertEquals("Exam-style prevention comments not updated successfully", EDITED_COMMENT, currentComment);
	}
}