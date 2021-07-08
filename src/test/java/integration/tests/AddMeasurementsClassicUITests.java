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
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import integration.tests.util.seleniumUtil.PageUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;

import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

public class AddMeasurementsClassicUITests extends SeleniumTestBase
{
	@BeforeClass
	public static void setup()
	{
		loadSpringBeans();
		DatabaseUtil.createTestDemographic();
	}

	@AfterClass
	public static void cleanup()
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		SchemaUtils.restoreTable(
				"admission", "caisi_role",  "casemgmt_note", "demographic", "documentDescriptionTemplate", "dxresearch",
				"eChart", "Facility", "issue", "log", "LookupList", "LookupListItem", "measurementType", "measurements",
				"OscarJob", "OscarJobType", "provider", "ProviderPreference",  "quickListUser", "roster_status",
				"secUserRole", "tickler_text_suggest", "validations"

		);
	}

	public void addMeasurements(String currWindowHandle, String flowsheetName, String flowsheetSelected, String measurementSelected )
			throws InterruptedException
	{
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(1000);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("measurements")));
		if (PageUtil.isExistsBy((By.id("imgmeasurements5")), driver))
		{
			driver.findElement(By.id("imgmeasurements5")).click();
		}
		driver.findElement(By.linkText(flowsheetName)).click();
		Thread.sleep(2000);
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath("//span[contains(., '" + flowsheetSelected + "')]")).click();
		Thread.sleep(5000);
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);
		driver.findElement(By.id("value(inputValue-0)")).sendKeys(measurementSelected);
		driver.findElement(By.xpath("//input[@value='Save']")).click();
	}

	@Test
	public void addMeasurementsStandardFlowsheetClassicUITest()
			throws InterruptedException
	{
		// ** Add flowsheets to Disease Registry. **
		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		Thread.sleep(5000);
		String currWindowHandle = driver.getWindowHandle();
		AddDiseaseRegistryClassicUITests addDiseaseRegistry = new AddDiseaseRegistryClassicUITests();
		addDiseaseRegistry.addDiseaseRegistry();

		//** Add measurements  **
		String flowsheetNameINR = "INR Flowsheet";
		String iNRSelected = "INR";
		String measurementINR = "2";
		String noteExpectedINR = iNRSelected + "    " + measurementINR;

		String flowsheetNameHIV = "HIV Flowsheet";
		String hIVSelected = "FBS";
		String measurementFBS = "40";
		String noteExpectedFBS = hIVSelected + "    " + measurementFBS;

		String flowsheetNameHeartFailure = "Heart Failure Flowsheet";
		String heartFailureSelected = "BP";
		String measurementBP = "120/80";
		String noteExpectedBP = heartFailureSelected + "    " + measurementBP;

		String flowsheetNameHypertension = "Hypertension Flowsheet";
		String hypertensionSelected = "eGFR";
		String measurementEGFR = "20";
		String noteExpectedEGFR = hypertensionSelected + "    " + measurementEGFR;

		String flowsheetNameASTHMA = "ASTHMA";
		String aSTHMASelected = "Asthma Limits Physical Activity";
		String measurementAsthmaLimitsPhysicalActivity = "no";
		String noteExpectedAsthmaLimitsPhysicalActivity = aSTHMASelected + "    " + measurementAsthmaLimitsPhysicalActivity;

		String flowsheetNameDiabetes = "Diabetes Flowsheet";
		String diabetesSelected = "A1C";
		String measurementA1C = "4";
		String noteExpectedA1C = diabetesSelected + "    " + measurementA1C;

		String flowsheetNameChronicObstructivePulmonary = "Chronic Obstructive Pulmonary";
		String chronicObstructivePulmonarySelected = "Pulmonary Rehabilitation Referral";
		String measurementPulmonaryRehabilitationReferral = "yes";
		String noteExpectedPulmonaryRehabilitationReferral = chronicObstructivePulmonarySelected + "    " + measurementPulmonaryRehabilitationReferral;

		//** Add measurements from HIV. **
		addMeasurements(currWindowHandle, flowsheetNameHIV, hIVSelected, measurementFBS);

		//** Add measurements from INR. **
		addMeasurements(currWindowHandle, flowsheetNameINR, iNRSelected, measurementINR);

		//** Add measurements from ASTHMA. **
		addMeasurements(currWindowHandle, flowsheetNameASTHMA, aSTHMASelected, measurementAsthmaLimitsPhysicalActivity);

		//** Add measurements from Chronic Obstructive Pulmonary. **
		addMeasurements(currWindowHandle, flowsheetNameChronicObstructivePulmonary, chronicObstructivePulmonarySelected, measurementPulmonaryRehabilitationReferral);

		//** Add measurements from Heart Failure Flowsheet. **
		addMeasurements(currWindowHandle, flowsheetNameHeartFailure, heartFailureSelected, measurementBP);

		//** Add measurements from Hypertension Flowsheet. **
		addMeasurements(currWindowHandle, flowsheetNameHypertension, hypertensionSelected, measurementEGFR);

		//** Add measurements from Diabetes Flowsheet. **
		addMeasurements(currWindowHandle, flowsheetNameDiabetes, diabetesSelected, measurementA1C);

		//** Verify from Measurements **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(1000);
		driver.findElement(By.id("imgmeasurements5")).click();
		Assert.assertTrue("Measurement " + diabetesSelected + " from " + flowsheetNameDiabetes + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '"+ diabetesSelected + "')]"), driver));
		Assert.assertTrue("Measurement " + aSTHMASelected + " from " + flowsheetNameASTHMA + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + aSTHMASelected + "')]"), driver));
		Assert.assertTrue("Measurement " + heartFailureSelected + " from " + flowsheetNameHeartFailure + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + heartFailureSelected + "')]"), driver));
		Assert.assertTrue("Measurement " + hypertensionSelected + " from " + flowsheetNameHypertension + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + StringUtils.capitalize(hypertensionSelected) + "')]"), driver));
		Assert.assertTrue("Measurement " + hIVSelected + " from " + flowsheetNameHIV + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + hIVSelected + "')]"), driver));
		Assert.assertTrue("Measurement " + iNRSelected + " from " + flowsheetNameINR + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + iNRSelected + "')]"), driver));
		Assert.assertTrue("Measurement " + chronicObstructivePulmonarySelected + " from " + flowsheetNameChronicObstructivePulmonary + " is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., '" + chronicObstructivePulmonarySelected + "')]"), driver));

		//** Verify the measurements from eChart Notes **
		String noteText = driver.findElement(By.id("caseNote_note7")).getText();
		Assert.assertTrue(hIVSelected + " from " + flowsheetNameHIV + " is NOT added to note successfully",
				noteText.contains(noteExpectedFBS));
		Assert.assertTrue(iNRSelected + " from " + flowsheetNameINR + " is NOT added to note successfully",
				noteText.contains(noteExpectedINR));
		Assert.assertTrue(aSTHMASelected + " from " + flowsheetNameASTHMA + " is NOT added to note successfully",
				noteText.contains(noteExpectedAsthmaLimitsPhysicalActivity));
		Assert.assertTrue(chronicObstructivePulmonarySelected + " from " + flowsheetNameChronicObstructivePulmonary + " is NOT added to note successfully",
				noteText.contains(noteExpectedPulmonaryRehabilitationReferral));
		Assert.assertTrue(heartFailureSelected + " from " + flowsheetNameHeartFailure + " is NOT added to note successfully",
				noteText.contains(noteExpectedBP));
		Assert.assertTrue(hypertensionSelected + " from " + flowsheetNameHypertension + " is NOT added to note successfully",
				noteText.contains(noteExpectedEGFR));
		Assert.assertTrue(diabetesSelected + " from " + flowsheetNameDiabetes + " is NOT added to note successfully",
				noteText.contains(noteExpectedA1C));
	}

/*
	@Test
	public void addMeasurementsCustomizedFlowsheetClassicUITest()
			throws InterruptedException
	{
		String flowsheetName = "Test Flowsheet";
		String flowsheetTrigger = "780";
		String flowsheetTriggerIcd9 = "icd9:" + flowsheetTrigger;
		String flowsheetWarningColor = "Red";
		String flowsheetRecommendationColor = "Green";
		String assertString = flowsheetName + " is Not added successfully. ";
		String measurmentsHEAD = "Head Circumference";

		// ** Add customized flowsheet "Test Flowsheet" and measurement "Head Circumference" **
		SectionAccessUtil.accessAdministrationSectionClassicUI(driver, "System Management", "Create New Flowsheet");
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("displayName")).sendKeys(flowsheetName);
		driver.findElement(By.id("dxcodeTriggers")).sendKeys(flowsheetTriggerIcd9);
		driver.findElement(By.id("warningColour")).sendKeys(flowsheetWarningColor);
		driver.findElement(By.id("recommendationColour")).sendKeys(flowsheetRecommendationColor);
		driver.findElement(By.xpath("//input[@name='Submit']")).click();
		Thread.sleep(2000);
		Assert.assertTrue(assertString, PageUtil.isExistsBy(By.linkText(flowsheetName), driver));

		driver.findElement(By.linkText(flowsheetName)).click();
		driver.findElement(By.linkText("Add")).click();
		driver.findElement(By.xpath("//option[@value='HEAD']")).click();
		driver.findElement(By.id("display_name")).sendKeys(measurmentsHEAD);
		driver.findElement(By.xpath("//input[@name='guideline']")).sendKeys("Test Guideline");
		driver.findElement(By.id("value_name")).sendKeys("Test Value Name");
		driver.findElement(By.xpath("//input[@value='Save']")).click();

		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		Thread.sleep(5000);
		String currWindowHandle = driver.getWindowHandle();

		//** Add icd9 code to Disease Registry **
		driver.findElement(By.id("menuTitleDx")).click();
		Thread.sleep(5000);
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath("//input[@name='xml_research1']")).sendKeys(flowsheetTrigger);
		driver.findElement(By.xpath("//input[@value='Add']")).click();

		//** Add Measurements from eChart page **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("measurements")));
		driver.findElement(By.linkText(flowsheetName)).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.linkText(measurmentsHEAD)).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.id("value(inputValue-0)")).sendKeys("45");
		driver.findElement(By.xpath("//input[@value='Save']")).click();

		//** Verify the measurements from eChart page **
		driver.navigate().refresh();
		Thread.sleep(1000);
		Assert.assertTrue("Measurement Head Circumference is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'HEAD')]"), driver));
	}
*/
	/*@Test
	public void addMeasurementsCDMIndicatorClassicUITest()
			throws InterruptedException
	{
		String flowsheetNameCDI = "CDM Indicators";
		String measurementWeight = "55";
		String measurementHeight = "165";
		String measurementDiabetesType = "2";
		String measurementUrineACR = "40";
		String measurementDilatedEyeExam = "yes";
		String measurementDentalExam = "yes";
		String measurementPainfulNeuropathy = "no";
		String measurementFootExam = "yes";
		String measurementDrugCoverage = "no";
		String measurementSmokingStatus = "no";

		String noteExpectedWeight = "Weight " + measurementWeight;
		String noteExpectedHeight = "Height " + measurementHeight;
		String noteExpectedDiabetesType = "DiabetesType " + measurementDiabetesType;
		String noteExpectedUrineACR = "UrineACR " + measurementUrineACR;
		String noteExpectedDilatedEyeExam = "DilatedEyeExam " + measurementDilatedEyeExam;
		String noteExpectedDentalExam = "DentalExam " + measurementDentalExam;
		String noteExpectedPainfulNeurophathy = "PainfulNeuropathy " + measurementPainfulNeuropathy;
		String noteExpectedFootExam = "FootExam " + measurementFootExam;
		String noteExpectedDrugCoverage = "DrugCoverage " + measurementDrugCoverage;
		String noteExpectedSmokingStatus = "SmokingStatus " + measurementSmokingStatus;

		// ** Add customized flowsheet "Test Flowsheet" and measurement "Head Circumference" **
		SectionAccessUtil.accessAdministrationSectionClassicUI(driver, "System Management", "Manage Flowsheets");
		PageUtil.switchToLastWindow(driver);

		driver.get(Navigation.OSCAR_URL + ECHART_URL);
		Thread.sleep(10000);
		String currWindowHandle = driver.getWindowHandle();

		//** Add Measurements from CDM indicators on eChart page **
		Thread.sleep(5000);
		driver.findElement(By.linkText(flowsheetNameCDI)).click();
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);
		driver.findElement(By.id("DiabetesType")).sendKeys(measurementDiabetesType);
		driver.findElement(By.id("Weight")).sendKeys(measurementWeight);
		driver.findElement(By.id("Height")).sendKeys(measurementHeight);
		driver.findElement(By.id("UrineACR")).sendKeys(measurementUrineACR);
		driver.findElement(By.id("DilatedEyeExam")).sendKeys(measurementDilatedEyeExam);
		driver.findElement(By.id("DentalExam")).sendKeys(measurementDentalExam);
		driver.findElement(By.id("PainfulNeuropathy")).sendKeys(measurementPainfulNeuropathy);
		driver.findElement(By.id("FootExam")).sendKeys(measurementFootExam);
		driver.findElement(By.id("DrugCoverage")).sendKeys(measurementDrugCoverage);
		driver.findElement(By.id("SmokingStatus")).sendKeys(measurementSmokingStatus);
		driver.findElement(By.id("submit-btn")).click();

		//** Add measurements from INR Flowsheet **
		String flowsheetINRcode = "42731";
		String flowsheetNameINR = "INR Flowsheet";
		String measurementINR = "2";
		String noteExpectedINR = "INR    " + measurementINR;
		PageUtil.switchToWindow(currWindowHandle, driver);

		//** Add INR Flowsheet from Disease Registry. **
		driver.findElement(By.id("menuTitleDx")).click();
		Thread.sleep(5000);
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath("//input[@name='xml_research1']")).sendKeys(flowsheetINRcode);
		driver.findElement(By.xpath("//input[@value='Add']")).click();

		//** Add measurements from eChart Page. **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(1000);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("measurements")));
		driver.findElement(By.linkText(flowsheetNameINR)).click();
		Thread.sleep(2000);
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath("//span[contains(., 'INR')]")).click();
		Thread.sleep(10000);
		PageUtil.switchToLastWindow(driver);
		Thread.sleep(2000);
		driver.findElement(By.id("value(inputValue-0)")).sendKeys(measurementINR);
		driver.findElement(By.xpath("//input[@value='Save']")).click();

		//** Verify from Measurements **
		PageUtil.switchToWindow(currWindowHandle, driver);
		driver.navigate().refresh();
		Thread.sleep(1000);
		driver.findElement(By.id("imgmeasurements5")).click();
		Assert.assertTrue("Measurement HT is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'HT')]"), driver));
		Assert.assertTrue("Measurement WT is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'WT')]"), driver));
		Assert.assertTrue("UrineACR is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Alb creat ratio')]"), driver));
		Assert.assertTrue("Dilated Eye Exam is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Dilated Eye Exam')]"), driver));
		Assert.assertTrue("Dental Exam is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Dental Exam Every 6 Months')]"), driver));
		Assert.assertTrue("Painful Neuropathy is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Painful Neuropathy')]"), driver));
		Assert.assertTrue("Foot Exam is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Foot Exam')]"), driver));
		Assert.assertTrue("Drug Coverage is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Drug Coverage')]"), driver));
		Assert.assertTrue("Smoking Status is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'Smoking Status')]"), driver));
		Assert.assertTrue("INR is NOT added successfully",
				PageUtil.isExistsBy(By.xpath("//span[contains(., 'INR')]"), driver));

		//** Verify the measurements from eChart Notes **
		String noteText = driver.findElement(By.id("caseNote_note2")).getText();
		Assert.assertTrue("Weight is NOT added to note successfully",
				noteText.contains(noteExpectedWeight));
		Assert.assertTrue("Height is NOT added to note successfully",
				noteText.contains(noteExpectedHeight));
		Assert.assertTrue("Diabetes Type is NOT added to note successfully",
				noteText.contains(noteExpectedDiabetesType));
		Assert.assertTrue("Urine ACR is NOT added to note successfully",
				noteText.contains(noteExpectedUrineACR));
		Assert.assertTrue("Dilated Eye Exam is NOT added to note successfully",
				noteText.contains(noteExpectedDilatedEyeExam));
		Assert.assertTrue("Dental Exam is NOT added to note successfully",
			noteText.contains(noteExpectedDentalExam));
		Assert.assertTrue("Painful Neurophathy is NOT added to note successfully",
			noteText.contains(noteExpectedPainfulNeurophathy));
		Assert.assertTrue("Foot Exam is NOT added to note successfully",
			noteText.contains(noteExpectedFootExam));
		Assert.assertTrue("Drug Coverage is NOT added to note successfully",
			noteText.contains(noteExpectedDrugCoverage));
		Assert.assertTrue("Smoking Status is NOT added to note successfully",
			noteText.contains(noteExpectedSmokingStatus));
		Assert.assertTrue("INR is NOT added to note successfully",
			noteText.contains(noteExpectedINR));
	}*/
}
