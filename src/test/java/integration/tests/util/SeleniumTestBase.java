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

import integration.tests.config.TestConfig;
import integration.tests.util.junoUtil.DatabaseUtil;
import integration.tests.util.junoUtil.Navigation;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.sql.SQLException;
import org.springframework.context.annotation.Import;

@Import(TestConfig.class)
public class SeleniumTestBase
{
	@LocalServerPort
	protected int randomTomcatPort;

	@Autowired
	protected DatabaseUtil databaseUtil;

	@Autowired
	protected JunoProperties junoProperties;


	public static final Integer WEB_DRIVER_IMPLICIT_TIMEOUT = 60;
	public static final Integer WEB_DRIVER_EXPLICIT_TIMEOUT = 120;
	private static final String GECKO_DRIVER="src/test/resources/vendor/geckodriver";
	private static final String INTEGRATION_PROPERTIES_FILE = "src/test/resources/integration.properties";

	protected static WebDriver driver;
	protected static Logger logger= MiscUtils.getLogger();

	protected String tomcatPort;

	public static WebDriverWait webDriverWait;

	@BeforeClass
	synchronized public static void buildWebDriver() throws SQLException, InstantiationException,
			IllegalAccessException, ClassNotFoundException, IOException
	{
		oscar.OscarProperties p = oscar.OscarProperties.getInstance();
		p.readFromFile(INTEGRATION_PROPERTIES_FILE);

		//load database (during the integration-test phase this will only populate table creation maps)
		SchemaUtils.createDatabaseAndTables();

		//build and start selenium web driver
		System.setProperty("webdriver.gecko.driver", GECKO_DRIVER);

		//practically all integration tests rely on the security table. restore it.
		SchemaUtils.restoreTable("security");

	}

	@Before
	public void login()
	{
		FirefoxBinary ffb = new FirefoxBinary();
		FirefoxOptions ffo = new FirefoxOptions();
		if(junoProperties.getTest().isHeadless())
		{
			ffb.addCommandLineOptions("--headless");
		}
		ffo.setBinary(ffb);
		driver = new FirefoxDriver(ffo);
		Navigation.doLogin(
			AuthUtils.TEST_USER_NAME,
			AuthUtils.TEST_PASSWORD,
			AuthUtils.TEST_PIN,
			Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
			driver
		);
		driver.manage().window().setSize(new Dimension(1920, 1080));
		webDriverWait = new WebDriverWait(driver, WEB_DRIVER_EXPLICIT_TIMEOUT);
	}

	@Before
	@After
	public void resetDatabase()
		throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		if(getTablesToRestore().length > 0)
		{
			SchemaUtils.restoreTable(getTablesToRestore());
		}
	}

	@After
	public void closeWebDriver()
	{
		driver.quit();
	}

	protected static void loadSpringBeans()
	{
		DaoTestFixtures.setupBeanFactory();
	}

	protected String[] getTablesToRestore()
	{
		return new String[0];
	}
}
