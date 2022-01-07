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

package integration.tests.util.junoUtil;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import oscar.util.ConversionUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;

public class AppointmentUtil
{
	private static final String COLUMN_HEADER_DATE_FORMAT = "EEEE MMM d";
	public static void skipTwoDaysJUNOUI(WebDriver driver, WebDriverWait webDriverWait)
	{
		String nextDaySelector = "//button[@title='Next Day']";

		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextDaySelector)));

		String todayDateString = driver.findElement(By.cssSelector("#ca-calendar th.fc-today")).getAttribute("data-date");
		LocalDate dateToday = ConversionUtils.toLocalDate(todayDateString);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern(COLUMN_HEADER_DATE_FORMAT);
		String oneDayLaterString = dtf.format(dateToday.plusDays(1));
		String twoDaysLaterString = dtf.format(dateToday.plusDays(2));

		findWaitClickByXpath(driver, webDriverWait, nextDaySelector);

		webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#ca-calendar th.fc-future span"), "(0) " + oneDayLaterString));
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath(nextDaySelector)));
		findWaitClickByXpath(driver, webDriverWait, nextDaySelector);

		webDriverWait.until(ExpectedConditions.textToBePresentInElementLocated(By.cssSelector("#ca-calendar th.fc-future span"), "(0) " + twoDaysLaterString));
	}
}
