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

import integration.tests.util.seleniumUtil.PageUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.util.MiscUtils;
import oscar.OscarProperties;

public class Navigation
{
	private static final OscarProperties properties = OscarProperties.getInstance();

	public static final String OSCAR_URL = "http://localhost";
	public static final String ECHART_URL = "/oscarEncounter/IncomingEncounter.do" +
		"?providerNo=" + AuthUtils.TEST_PROVIDER_ID +
		"&appointmentNo=" +
		"&demographicNo=1" +
		"&curProviderNo=" +
		"&reason=Tel-Progress+Note&encType=" +
		"&curDate=2019-4-17" +
		"&appointmentDate=" +
		"&startTime=" +
		"&status=";
	public static final String Consultation_URL = "/oscarEncounter/IncomingConsultation.do?providerNo=" + AuthUtils.TEST_PROVIDER_ID + "&userName=doctor+" + AuthUtils.TEST_USER_NAME;

	private static Logger logger = MiscUtils.getLogger();

	/**
	 * login to juno emr
	 * @param username the user name to use at login
	 * @param password the password to use at login
	 * @param pin the pin to use at login
	 * @param baseUrl the base url of the juno server (Ex "https://localhost:9090/")
	 * @param driver the selenium driver to use
	 */
	public static void doLogin(String username, String password, String pin, String baseUrl, WebDriver driver)
	{
		logger.info("Logging in....");

		driver.get(baseUrl + "/index.jsp");
		WebElement userNameInput = driver.findElement(By.name("username"));
		WebElement passwordInput = driver.findElement(By.name("password"));
		WebElement pinInput 	 = driver.findElement(By.name("pin"));
		WebElement loginForm     = driver.findElement(By.name("loginForm"));

		userNameInput.sendKeys(username);
		passwordInput.sendKeys(password);
		pinInput.sendKeys(pin);

		String oldUrl = driver.getCurrentUrl();
		loginForm.submit();
		PageUtil.waitForPageChange(oldUrl, driver);

		logger.info("Logged in!");
	}

	/**
	 * checks if the webdriver already has a session object from Oscar (does not insure said object is valid)
	 * @param driver
	 * @return true if driver appears to be logged in
	 */
	public static boolean isLoggedIn(WebDriver driver)
	{
		Cookie session = driver.manage().getCookieNamed("JSESSIONID");
		return session != null;
	}

	public static String getOscarUrl(int serverPort)
	{
		return getOscarUrl(Integer.toString(serverPort));
	}

	public static String getOscarUrl(String serverPort)
	{
		return OSCAR_URL + ":" + serverPort + "/" + properties.getProjectHome();
	}
}