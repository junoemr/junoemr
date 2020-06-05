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
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.oscarehr.common.dao.utils.AuthUtils;

import java.util.List;
import java.util.Set;


public class AddPatientsTests extends SeleniumTestBase
{
	@Test
	public void AddPatientsTest() throws Exception {
		// login
		if (!Navigation.isLoggedIn(driver)) {
			Navigation.doLogin(AuthUtils.TEST_USER_NAME, AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
		}

		// open patient search page
		WebElement searchPatientButton = driver.findElement((By.xpath("//input[@title=\"Search for patient records\"]")));
		searchPatientButton.click();

		String currWindowHandle = driver.getWindowHandle();
		Set<String> oldWindowHandles = driver.getWindowHandles();
		Thread.sleep(2000);
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);

		Assert.assertEquals("more than one window opened when opening eform", 1, newWindows.size());
		PageUtil.switchToWindow(newWindows.get(0), driver);
		Thread.sleep(2000);

		// Add a demographic record page
		WebElement createDemographicButton = driver.findElement((By.xpath("//input[@title=\"Create A New Demographic\"]")));
		createDemographicButton.click();
		Thread.sleep(2000);

		WebElement lastName = driver.findElement(By.id("last_name"));
		lastName.sendKeys("TestLastName");
		WebElement firstName = driver.findElement((By.id("first_name")));
		firstName.sendKeys("TestFirstName");
		WebElement langOfficial = driver.findElement(By.id("official_lang"));
		langOfficial.click();
		WebElement langOfficialOpt = driver.findElement(By.xpath("//option[@value='English']"));
		langOfficialOpt.click();
		WebElement title = driver.findElement(By.id("title"));
		title.click();
		WebElement titleOpt = driver.findElement(By.xpath("//option[@value='Ms']"));
		titleOpt.click();
		WebElement spokenLang = driver.findElement(By.xpath("//select[@name='spoken']"));
		spokenLang.click();
		WebElement spokenLangOpt = driver.findElement(By.xpath("//option[@value='English']"));
		spokenLangOpt.click();
		WebElement address = driver.findElement(By.id("address"));
		address.sendKeys("31 Bastion Square #302");
		WebElement city = driver.findElement(By.id("city"));
		city.sendKeys("Victoria");
		WebElement province = driver.findElement(By.id("province"));
		province.click();
		WebElement provinceOpt = driver.findElement((By.xpath("//option[@value='BC']")));
		provinceOpt.click();
		WebElement postCode = driver.findElement(By.id("postal"));
		postCode.sendKeys("V8W 1J1");
		WebElement hPhone = driver.findElement(By.id("phone"));
		hPhone.sendKeys("+1 888-686-8560");
		WebElement hPhoneExt = driver.findElement(By.id("hPhoneExt"));
		hPhoneExt.sendKeys("101");
		WebElement wPhone = driver.findElement(By.xpath("//input[@name='phone2']"));
		wPhone.sendKeys("+1 888-686-8560");
		WebElement wPhoneExt = driver.findElement(By.xpath("//input[@name='wPhoneExt']"));
		wPhoneExt.sendKeys("102");
		WebElement cPhone = driver.findElement(By.xpath("//input[@name='demo_cell']"));
		cPhone.sendKeys("+1 888-686-8560");
		WebElement phoneComment = driver.findElement(By.xpath("//textarea[@name='phoneComment']"));
		phoneComment.sendKeys("Prefer cell");
		WebElement newsletter = driver.findElement(By.xpath("//select[@name='newsletter']"));
		newsletter.click();
		WebElement newsletterOpt = driver.findElement((By.xpath("//option[@value='No']")));
		newsletterOpt.click();
		WebElement aboriginal = driver.findElement(By.xpath("//select[@name='aboriginal']"));
		aboriginal.click();
		WebElement aboriginalOpt = driver.findElement(By.xpath("//select[@value='No']"));
		aboriginalOpt.click();
		WebElement email = driver.findElement(By.id("email"));
		email.sendKeys("ailin.zhu@cloudpractice.ca");
		WebElement  oscarUserName= driver.findElement(By.xpath("//input[@name='myOscarUserName']"));
		oscarUserName.sendKeys("TTestLastName");
		WebElement birthYear = driver.findElement(By.id("year_of_birth"));
		birthYear.sendKeys("1999");
		WebElement birthMonth = driver.findElement(By.id("month_of_birth"));
		birthMonth.sendKeys("09");
		birthMonth.click();
		WebElement birthDate = driver.findElement(By.id("date_of_birth"));
		birthDate.sendKeys("09");
		birthDate.click();
		WebElement sex = driver.findElement(By.id("sex"));
		sex.click();
		WebElement sexOpt = driver.findElement(By.xpath("//option[@value='Male']"));
		sexOpt.click();
		WebElement hin = driver.findElement(By.id("hin"));
		hin.sendKeys("01234567890");
		WebElement effYear = driver.findElement(By.id("eff_date_year"));
		effYear.sendKeys("2020");
		WebElement effMonth = driver.findElement(By.id("eff_date_month"));
		effMonth.sendKeys("06");
		WebElement effdate = driver.findElement(By.id("eff_date_date"));
		effdate.sendKeys("01");
		WebElement hcType = driver.findElement(By.id("hc_type"));
















	}

}
