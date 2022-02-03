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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ActionUtil
{
	public static void dropdownSelectByVisibleText(WebDriver driver,
		WebDriverWait webDriverWait, By dropdown, String visibleText)
	{
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
		webDriverWait.until(ExpectedConditions.presenceOfElementLocated(dropdown));
		Select dropdownList = new Select(driver.findElement(dropdown));
		dropdownList.selectByVisibleText(visibleText);
	}
	public static void dropdownSelectByValue(WebDriver driver, WebDriverWait webDriverWait,
		By dropdown, String dropdownSelection)
	{
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
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

	public static void findWaitEditById(WebDriver driver, WebDriverWait webDriverWait, String id, String textNew)
	{
		By elementToEdit = By.id(id);
		findWaitEdit(driver, webDriverWait, elementToEdit, textNew);
	}

	public static void findWaitEdit(WebDriver driver, WebDriverWait webDriverWait, By elementToEdit, String textNew)
	{
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(elementToEdit));
		textEdit(driver, elementToEdit, textNew);
	}

	public static void findWaitClickByXpath(WebDriver driver, WebDriverWait webDriverWait, String xpath)
	{
		By elementToClick = By.xpath(xpath);
		findWaitClick(driver, webDriverWait, elementToClick);
	}

	public static void findWaitClickById(WebDriver driver, WebDriverWait webDriverWait, String id)
	{
		By elementToClick = By.id(id);
		findWaitClick(driver, webDriverWait, elementToClick);
	}

	public static void findWaitClickByLinkText(WebDriver driver, WebDriverWait webDriverWait, String linkText)
	{
		By elementToClick = By.linkText(linkText);
		findWaitClick(driver, webDriverWait, elementToClick);
	}

	public static void findWaitClickByPartialLinkText(WebDriver driver, WebDriverWait webDriverWait, String linkText)
	{
		By elementToClick = By.partialLinkText(linkText);
		findWaitClick(driver, webDriverWait, elementToClick);
	}

	public static void findWaitClick(WebDriver driver, WebDriverWait webDriverWait, By elementToClick)
	{
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
		webDriverWait.until(ExpectedConditions.elementToBeClickable(elementToClick));
		driver.findElement(elementToClick).click();
	}

	public static void findWaitSendKeysByXpath(WebDriver driver, WebDriverWait webDriverWait, String xpath, String keysToSend)
	{
		By element = By.xpath(xpath);
		findWaitSendKeys(driver, webDriverWait, element, keysToSend);
	}

	public static void findWaitSendKeysById(WebDriver driver, WebDriverWait webDriverWait, String id, String keysToSend)
	{
		By element = By.id(id);
		findWaitSendKeys(driver, webDriverWait, element, keysToSend);
	}

	public static void findWaitSendKeys(WebDriver driver, WebDriverWait webDriverWait, By element, String keysToSend)
	{
		webDriverWait.until(waitDriver -> ((JavascriptExecutor)waitDriver).executeScript("return document.readyState").equals("complete"));
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(element));
		driver.findElement(element).sendKeys(keysToSend);
	}
}
