/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package integration.tests.util.seleniumUtil;

import integration.tests.util.SeleniumTestBase;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

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

}
