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
package integration.tests.util.seleniumUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static integration.tests.util.seleniumUtil.PageUtil.isExistsBy;

public class SectionAccessUtil
{
	public static void accessAdministrationSectionClassicUI(WebDriver driver, String sectionName, String subSectionName, WebDriverWait webDriverWait)
	{
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("admin-panel")));
		driver.findElement(By.id("admin-panel")).click();
		PageUtil.switchToLastWindow(driver);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText(sectionName)));
		driver.findElement(By.linkText(sectionName)).click();
		driver.findElement(By.linkText(subSectionName)).click();
		driver.switchTo().frame("myFrame");
	}

	public static void accessAdministrationSectionJUNOUI(WebDriver driver, String sectionName, String subSectionName, WebDriverWait webDriverWait)
			throws InterruptedException
	{
		if (isExistsBy(By.xpath("//img[@title=\"Go to Juno UI\"]"), driver))
		{
			// open JUNO UI page
			driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		}
		// open administration panel
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("main-nav-collapse")));
		driver.findElement(By.linkText("Admin")).click();
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText(sectionName)));
		driver.findElement(By.linkText(sectionName)).click();
		driver.findElement(By.linkText(subSectionName)).click();
		Thread.sleep(2000);
		if (isExistsBy(By.tagName("iframe"), driver))
		{
			driver.switchTo().frame("content-frame");
		}
	}

	public static void accessSectionJUNOUI(WebDriver driver, String sectionName, WebDriverWait webDriverWait)
	{
		if (isExistsBy(By.xpath("//img[@title=\"Go to Juno UI\"]"), driver))
		{
			// open JUNO UI page
			driver.findElement(By.xpath("//img[@title=\"Go to Juno UI\"]")).click();
		}
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.linkText(sectionName)));
		driver.findElement(By.linkText(sectionName)).click();
	}
}
