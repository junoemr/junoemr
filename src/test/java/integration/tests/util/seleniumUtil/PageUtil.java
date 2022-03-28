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

import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class PageUtil
{
	/**
	 * wait until the webdriver changes pages
	 * @param oldUrl
	 * @param webDriverWait
	 */
	public static void waitForPageChange(String oldUrl, WebDriverWait webDriverWait)
	{
		webDriverWait.until(ExpectedConditions.not(ExpectedConditions.urlMatches(oldUrl)));
	}

	/**
	 * check if a given WebElement exists.  Will take a long time to timeout if used to find the
	 * absence of an element.
	 * @param search search method used to find element
	 * @param driver webDriver
	 * @return true / false depending on if the WebElement exists in the DOM
	 */
	public static boolean isExistsBy(By search, WebDriver driver)
	{
		try
		{
			driver.findElement(search);
			return true;
		}
		catch (NoSuchElementException e)
		{
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

	public static List<String> getNewWindowHandles(Set<String> oldHandles, WebDriver driver)
	{
		ArrayList<String> output = new ArrayList<>();
		Set<String> windowHandles = driver.getWindowHandles();

		for (String handle : windowHandles)
		{
			if (!oldHandles.contains(handle))
			{
				output.add(handle);
			}
		}

		return output;
	}

	public static void switchToWindow(String windowHandle, WebDriver driver)
	{
		WebDriver.TargetLocator targetLocator = driver.switchTo();
		targetLocator.window(windowHandle);
	}

	public static void switchToWindowRefreshAndWait(String windowHandle, WebDriver driver, WebDriverWait webDriverWait, By element)
	{
		WebDriver.TargetLocator targetLocator = driver.switchTo();
		targetLocator.window(windowHandle);
		driver.navigate().refresh();
		webDriverWait.until(
			ExpectedConditions.presenceOfElementLocated(element));
	}

	public static void clickWaitSwitchToLast(WebDriver driver, WebDriverWait webDriverWait, By clickTarget)
	{
		int handleCount = driver.getWindowHandles().size();
		Set<String> allHandles = driver.getWindowHandles();

		findWaitClick(driver, webDriverWait, clickTarget);
		webDriverWait.until(ExpectedConditions.numberOfWindowsToBe(handleCount + 1));
		switchToLastWindow(driver);
	}

	public static void switchToLastWindow(WebDriver driver)
	{
		Set<String> allHandles = driver.getWindowHandles();
		Integer allHandlesSize = allHandles.size();
		for (int i = 0; i < allHandlesSize - 1; i++)
		{
			allHandles.remove(allHandles.iterator().next());
		}
		String lastHandle = allHandles.iterator().next();
		driver.switchTo().window(lastHandle);
		driver.manage().window().maximize();
	}

	public static void  switchToNewWindow(WebDriver driver, By textlink,
		Set<String> oldWindowHandles, WebDriverWait webDriverWait)
	{
		int expectedNumberOfWindows = driver.getWindowHandles().size() + 1;
		findWaitClick(driver, webDriverWait, textlink);
		webDriverWait.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));
		List<String> newWindows = PageUtil.getNewWindowHandles(oldWindowHandles, driver);
		PageUtil.switchToWindow(newWindows.get(0), driver);
		driver.manage().window().maximize();
	}

	public static void accessEncounterPage(WebDriver driver)
	{
		driver.findElement((By.xpath("//a[@title=\"Search for patient records\"]"))).click();
		PageUtil.switchToLastWindow(driver);
		driver.findElement(By.xpath("//input[@title='Search active patients']")).click();
		driver.findElement(By.linkText("E")).click();
		PageUtil.switchToLastWindow(driver);
	}
}
