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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickById;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitSendKeysByXpath;
import static integration.tests.util.seleniumUtil.ActionUtil.textEdit;
import static integration.tests.util.seleniumUtil.PageUtil.clickWaitSwitchToLast;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AddDiseaseRegistryClassicUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "demographic", "dxresearch", "log", "measurementType", "quickListUser",
			"validations", "provider_recent_demographic_access", "log_ws_rest", "property"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	public void addDiseaseRegistry()
			throws InterruptedException
	{
		String heartFailure = "428";
		String diabetes = "250";
		String painAssistant = "7194";
		String asthma = "493";
		String hypertension = "401";
		String chronicObstructivePulmonary = "416";
		String ckd = "585";
		String hiv = "042";
		String inr = "42731";

		clickWaitSwitchToLast(driver, webDriverWait, By.xpath("//div[@id='menuTitleDx']//descendant::a[contains(., '+')]"));
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='xml_research1']", heartFailure);
		findWaitSendKeysByXpath(driver, webDriverWait, "//input[@name='xml_research2']", diabetes);
		driver.findElement(By.xpath("//input[@name='xml_research3']")).sendKeys(painAssistant);
		driver.findElement(By.xpath("//input[@name='xml_research4']")).sendKeys(asthma);
		driver.findElement(By.xpath("//input[@name='xml_research5']")).sendKeys(hypertension);
		driver.findElement(By.xpath("//input[@value='Add']")).click();

		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='xml_research1']")));
		textEdit(driver, By.xpath("//input[@name='xml_research1']"), chronicObstructivePulmonary);
		textEdit(driver, By.xpath("//input[@name='xml_research2']"), ckd);
		textEdit(driver, By.xpath("//input[@name='xml_research3']"), hiv);
		textEdit(driver, By.xpath("//input[@name='xml_research4']"), inr);
		driver.findElement(By.xpath("//input[@name='xml_research5']")).clear();
		driver.findElement(By.xpath("//input[@value='Add']")).click();
	}

	@Test
	public void addDiseaseRegistryClassicUITest()
			throws InterruptedException
	{
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		String currWindowHandle = driver.getWindowHandle();
		addDiseaseRegistry();

		//** Verify from Disease Registry **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		findWaitClickById(driver, webDriverWait, "imgDx5");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("CHR PULMONARY HEART DIS*")));
		Assert.assertTrue("CHR PULMONARY HEART DIS icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("CHR PULMONARY HEART DIS*"), driver));
		Assert.assertTrue("CHRONIC RENAL FAILURE icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("CHRONIC RENAL FAILURE"), driver));
		Assert.assertTrue("HUMAN IMMUNO VIRUS DIS icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("HUMAN IMMUNO VIRUS DIS"), driver));
		Assert.assertTrue("ATRIAL FIBRILLATION icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("ATRIAL FIBRILLATION"), driver));
		Assert.assertTrue("HEART FAILURE* icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("HEART FAILURE*"), driver));
		Assert.assertTrue("DIABETES MELLITUS* icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("DIABETES MELLITUS*"), driver));
		Assert.assertTrue("PAIN IN JOINT* icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("PAIN IN JOINT*"), driver));
		Assert.assertTrue("ASTHMA* icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("ASTHMA*"), driver));
		Assert.assertTrue("ESSENTIAL HYPERTENSION* icd is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("ESSENTIAL HYPERTENSION*"), driver));

		//** Verify from Measurements **
		driver.navigate().refresh();
		findWaitClickById(driver, webDriverWait, "imgmeasurements5");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("HIV Flowsheet")));
		Assert.assertTrue("HIV Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("HIV Flowsheet"), driver));
		Assert.assertTrue("INR Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("INR Flowsheet"), driver));
		Assert.assertTrue("ASTHMA Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("ASTHMA"), driver));
		Assert.assertTrue("Chronic Obstructive Pulmonary Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("Chronic Obstructive Pulmonary"), driver));
		Assert.assertTrue("Heart Failure Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("Heart Failure Flowsheet"), driver));
		Assert.assertTrue("Hypertension Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("Hypertension Flowsheet"), driver));
		Assert.assertTrue("Diabetes Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("Diabetes Flowsheet"), driver));
		Assert.assertTrue("CKD Flowsheet is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("CKD Flowsheet"), driver));
		Assert.assertTrue("Pain Assistant is NOT added successfully",
				PageUtil.isExistsBy(By.linkText("Pain Assistant"), driver));
	}
}