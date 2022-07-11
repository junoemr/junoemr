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

package integration.tests.junoUI.homepage;

import static integration.tests.classicUI.search.AddPatientsClassicUIIT.isPatientAdded;
import static integration.tests.util.data.PatientTestCollection.patientLNames;
import static integration.tests.util.seleniumUtil.ActionUtil.dropdownSelectByValue;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.data.PatientTestCollection;
import integration.tests.util.data.PatientTestData;
import junit.framework.Assert;
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
public class AddPatientsJUNOUIIT extends SeleniumTestBase
{
	public static final PatientTestData mom = PatientTestCollection.patientMap.get(patientLNames[0]);
	public static final PatientTestData dad = PatientTestCollection.patientMap.get(patientLNames[1]);
	public static final PatientTestData son = PatientTestCollection.patientMap.get(patientLNames[2]);
	public static final String momFullNameJUNO = mom.lastName + ", " + mom.firstName;
	public static final String dadFullName = dad.lastName + ',' + dad.firstName;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"demographicArchive", "demographiccust", "demographicExt", "demographicExtArchive", "log", "log_ws_rest",
			"program", "provider_recent_demographic_access", "admission", "demographic", "property"
		};
	}

	@Test
	public void addPatientsJUNOUITest()
	{
		// open JUNO UI page
		driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[@title=\"Add a new Patient\"]")));

		// Add a demographic record page
		driver.findElement(By.xpath("//button[@title=\"Add a new Patient\"]")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//juno-input[@label='Last Name']//input")));
		driver.findElement(By.xpath("//juno-input[@label='Last Name']//input")).sendKeys(son.lastName);
		driver.findElement(By.xpath("//juno-input[@label='First Name']//input")).sendKeys(son.firstName);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//juno-select[@label='Gender']//select"), "string:" + son.sex);
		driver.findElement(By.id("input-dob")).sendKeys(son.dobYear + "-" + son.dobMonth + "-" + son.dobDate);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//juno-select-text[@label='Health Insurance Number']//select"), "string:" + son.hcType);
		driver.findElement(By.xpath("//input[@class='ng-pristine ng-untouched ng-valid ng-empty']")).sendKeys(son.hin);
		driver.findElement(By.xpath("//juno-input[@label='Address']//input")).sendKeys(son.address);
		driver.findElement(By.xpath("//juno-input[@label='City']//input")).sendKeys(son.city);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//juno-select[@label='Province']//select"), "string:" + son.province);
		driver.findElement(By.xpath("//juno-input[@label='Postal Code']//input")).sendKeys(son.postal);
		driver.findElement(By.xpath("//juno-input[@label='Email']//input")).sendKeys(son.email);
		dropdownSelectByValue(driver, webDriverWait, By.xpath("//juno-select-text[@label='Preferred Phone']//select"), "string:" + son.preferredPhone);
		driver.findElement(By.xpath("//juno-select-text[@label='Preferred Phone']//input")).sendKeys(son.homePhone);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//juno-button[@click='$ctrl.onAdd()']")));
		driver.findElement(By.xpath("//juno-button[@click='$ctrl.onAdd()']")).click();
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(".//h4[contains(.,'Demographic')]")));
		Assert.assertTrue("Demographic is NOT added successfully.", isPatientAdded(son.lastName, son.firstName,
				By.xpath("//i[@class='icon icon-user-search']"),
				By.xpath("//input[@ng-model='$ctrl.search.term']"),
				By.xpath("//td[contains(., son.lastName)]")));
	}
}