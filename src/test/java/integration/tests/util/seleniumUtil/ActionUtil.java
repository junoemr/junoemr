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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ActionUtil
{
	public static void dropdownSelectByVisibleText(WebDriver driver, By dropdown, String visibleText)
	{
		Select dropdownList = new Select(driver.findElement(dropdown));
		dropdownList.selectByVisibleText(visibleText);
	}
	public static void dropdownSelectByValue(WebDriver driver, By dropdown, String dropdownSelection, WebDriverWait webDriverWait)
	{
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("option[value='" + dropdownSelection + "']")));
		Select dropdownList = new Select(driver.findElement(dropdown));
		dropdownList.selectByValue(dropdownSelection);
	}

	public static void dropdownSelectByIndex(WebDriver driver, By dropdown, int dropdownIndex)
	{
		Select dropdownList = new Select(driver.findElement(dropdown));
		dropdownList.selectByIndex(dropdownIndex);
	}

	public static void textEdit(WebDriver driver, By textField, String textNew)
	{
		WebElement text = driver.findElement(textField);
		text.clear();
		text.sendKeys(textNew);
	}

	public static void findWaitClick(WebDriver driver, WebDriverWait webDriverWait, String xpath)
	{
		By elementToClick = By.xpath(xpath);
		webDriverWait.until(ExpectedConditions.elementToBeClickable(elementToClick));
		driver.findElement(elementToClick).click();
	}
}
