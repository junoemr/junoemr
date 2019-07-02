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
package integration.tests.util;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.util.MiscUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SeleniumTestBase
{
	public static final Integer WEB_DRIVER_IMPLICIT_TIMEOUT = 20;
	private static final String GECKO_DRIVER="src/test/resources/vendor/geckodriver";

	protected static WebDriver driver;
	protected static Logger logger= MiscUtils.getLogger();

	@BeforeClass
	public static void buildWebDriver() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException
	{
		//load database (during the integration-test phase this will only populate table creation maps)
		SchemaUtils.createDatabaseAndTables();

		//build and start selenium web driver
		createWebDriver();

		//practically all integration tests rely on the security table. restore it.
		SchemaUtils.restoreTable("security");
	}

	@AfterClass
	public static void closeWebDriver()
	{
		driver.quit();
	}


	private static void createWebDriver()
	{
		System.setProperty("webdriver.gecko.driver", GECKO_DRIVER);
		FirefoxBinary ffb = new FirefoxBinary();
		FirefoxOptions ffo = new FirefoxOptions();
		ffb.addCommandLineOptions("--headless");
		ffo.setBinary(ffb);
		driver = new FirefoxDriver(ffo);
		driver.manage().timeouts().implicitlyWait(WEB_DRIVER_IMPLICIT_TIMEOUT, TimeUnit.SECONDS);
	}

	protected static void loadSpringBeans()
	{
		DaoTestFixtures.setupBeanFactory();
	}
}
