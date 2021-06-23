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
package integration.tests;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BasicOscarTests extends SeleniumTestBase
{
	@Autowired
	Environment environment;

	@Test
	public void isOscarReachable()
	{
		driver.get(Navigation.getOscarUrl(Integer.toString(randomTomcatPort)) + "/index.jsp");
		Assert.assertTrue("Cannot reach login page",!driver.getTitle().isEmpty());
	}

	@Test
	public void canLogin() throws Exception
	{
		Navigation.doLogin(
				AuthUtils.TEST_USER_NAME,
				AuthUtils.TEST_PASSWORD,
				AuthUtils.TEST_PIN,
				Navigation.getOscarUrl(Integer.toString(randomTomcatPort)),
				driver
		);
	}

	/*@Test
	public void canLogin() throws Exception {
		Navigation.doLogin(AuthUtils.TEST_USER_NAME,AuthUtils.TEST_PASSWORD, AuthUtils.TEST_PIN, Navigation.OSCAR_URL, driver);
	}*/
}
