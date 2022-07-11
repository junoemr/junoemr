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

package integration.tests.junoUI.admin.scheduleManagement;

import static integration.tests.classicUI.administration.scheduleManagement.AddGroupClassicUIIT.addGroup;
import static integration.tests.classicUI.administration.scheduleManagement.AddGroupClassicUIIT.deleteGroupMember;
import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drApple;
import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drBerry;
import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drCherry;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionJUNOUI;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.seleniumUtil.PageUtil;
import java.sql.SQLException;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:integration-test.properties")
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AddGroupJUNOUIIT extends SeleniumTestBase
{
	public static final String groupName = "TestGroup";
	public static final String valueOfDrApple = groupName + drApple.providerNo;
	public static final String valueOfDrBerry = groupName + drBerry.providerNo;
	public static final String valueOfDrCherry = groupName + drCherry.providerNo;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "log_ws_rest", "mygroup", "provider", "providerbillcenter",
			"property"
		};
	}

	@Before
	public void setup()
		throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		loadSpringBeans();
		databaseUtil.createTestProvider();
	}

	@Test
	public void addGroupsJUNOUITest()
	{
		accessAdministrationSectionJUNOUI(driver, webDriverWait, "Schedule Management", "Add a Group"
		);

		//Add a New Group with two providers: Dr. Apple and Dr. Berry
		addGroup(groupName, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrBerry)));
		Assert.assertTrue("Group is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver));

		//Add a Member: Dr. Cherry
		driver.findElement(By.linkText("New Group/Add a Member")).click();
		addGroup(groupName, 3);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrBerry)));
		Assert.assertTrue("Dr. Cherry is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrCherry), driver));

		//Delete the first provider from the Group
		deleteGroupMember(new String[]{valueOfDrApple});
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrBerry)));
		Assert.assertFalse("Dr. Apple is Not deleted successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver));

		//Delete the rest of the Group
		deleteGroupMember(new String[]{valueOfDrBerry, valueOfDrCherry});
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Delete']")));
		Assert.assertTrue("Group is Not deleted successfully.",
				(!PageUtil.isExistsBy(By.name(valueOfDrBerry), driver)) &&
						(!PageUtil.isExistsBy(By.name(valueOfDrCherry), driver)));
	}

}