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

import integration.tests.util.SeleniumTestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PageUtil
{
	/**
	 * wait until the webdriver changes pages
	 * @param oldUrl
	 * @param driver
	 */
	public static void waitForPageChange(String oldUrl, WebDriver driver)
	{
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.not(ExpectedConditions.urlMatches(oldUrl)));
	}

	/**
	 * check if a given WebElement exists (with no implicit delay)
	 * @param search search method used to find element
	 * @param driver webDriver
	 * @return true / false depending on if the WebElement exists in the DOM
	 */
	public static boolean isExistsBy(By search, WebDriver driver)
	{
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		try
		{
			driver.findElement(search);
			driver.manage().timeouts().implicitlyWait(SeleniumTestBase.WEB_DRIVER_IMPLICIT_TIMEOUT, TimeUnit.SECONDS);
			return true;
		}
		catch (NoSuchElementException e)
		{
			driver.manage().timeouts().implicitlyWait(SeleniumTestBase.WEB_DRIVER_IMPLICIT_TIMEOUT, TimeUnit.SECONDS);
			return false;
		}
	}

	public static boolean isErrorPage(WebDriver driver)
	{
		// check for error page title
		if (Pattern.compile("Error report").matcher(driver.getTitle()).find())
		{
			return true;
		}

		// look for apache error pages
		List<WebElement> titles =  driver.findElements(By.xpath("//title"));
		for (WebElement title : titles)
		{
			if (Pattern.compile("HTTP Status").matcher(title.getText()).find())
			{
				return true;
			}
		}

		if (PageUtil.isExistsBy(By.xpath("//h1[contains(., 'HTTP Status')]"), driver))
		{
			return true;
		}

		// look for hibernate error page
		if (PageUtil.isExistsBy(By.xpath("//h1[contains(., 'Error Page')]"), driver))
		{
			return true;
		}

		return false;
	}
}
