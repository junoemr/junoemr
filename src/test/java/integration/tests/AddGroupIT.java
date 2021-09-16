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
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.oscarehr.JunoApplication;
import org.oscarehr.common.dao.utils.SchemaUtils;

import java.sql.SQLException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static integration.tests.AddProvidersIT.drApple;
import static integration.tests.AddProvidersIT.drBerry;
import static integration.tests.AddProvidersIT.drCherry;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionJUNOUI;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = JunoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class AddGroupIT extends SeleniumTestBase
{
	public static final String groupName = "TestGroup";
	public static final String valueOfDrApple = groupName + drApple.providerNo;
	public static final String valueOfDrBerry = groupName + drBerry.providerNo;
	public static final String valueOfDrCherry = groupName + drCherry.providerNo;

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestProvider();
	}

	@After
	public void cleanup() throws SQLException, IllegalAccessException, ClassNotFoundException, InstantiationException
	{
		SchemaUtils.restoreTable("admission", "log", "log_ws_rest", "mygroup", "provider", "providerbillcenter");
	}

	public void addGroup(String groupName, int groupSize)
	{
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@name='mygroup_no']")));
		driver.findElement(By.xpath("//input[@name='mygroup_no']")).sendKeys(groupName);
		for (int i = 1; i <= groupSize; i ++)
		{
			driver.findElement(By.xpath("//input[@value='" + i + "']")).click();
		}
		driver.findElement(By.xpath("//input[@value='Save']")).click();
		driver.findElement(By.linkText("View Group List")).click();
	}

	public void deleteGroupMember(String[] groupMemberValue)
	{
		for (int i = 0; i < groupMemberValue.length; i ++)
		{
			driver.findElement(By.xpath("//input[@name='" + groupMemberValue[i] + "']")).click();
		}
		driver.findElement(By.xpath("//input[@value='Delete']")).click();
		driver.findElement(By.linkText("View Group List")).click();
	}

	@Test
	public void addGroupsClassicUITest() throws InterruptedException {
		//Add a New Group with two providers: Dr. Apple and Dr. Berry
		accessAdministrationSectionClassicUI(driver, "Schedule Management", "Add a Group");
		addGroup(groupName, 2);
		Assert.assertTrue("Group is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver));

		//Add a Member: Dr. Cherry
		driver.findElement(By.linkText("New Group/Add a Member")).click();
		addGroup(groupName, 3);
		Assert.assertTrue("New member is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrCherry), driver));

		//Delete the first provider from the Group
		deleteGroupMember(new String[]{valueOfDrApple});
		Assert.assertFalse("Dr. Apple is Not deleted successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver));

		//Delete the rest of the Group
		deleteGroupMember(new String[]{valueOfDrBerry, valueOfDrCherry});
		Assert.assertTrue("Group is Not deleted successfully.",
						(!PageUtil.isExistsBy(By.name(valueOfDrBerry), driver)) &&
						(!PageUtil.isExistsBy(By.name(valueOfDrCherry), driver)));
	}

	@Test
	public void addGroupsJUNOUITest() throws InterruptedException {
		accessAdministrationSectionJUNOUI(driver, "Schedule Management", "Add a Group");

		//Add a New Group with two providers: Dr. Apple and Dr. Berry
		addGroup(groupName, 2);
		Assert.assertTrue("Group is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver));

		//Add a Member: Dr. Cherry
		driver.findElement(By.linkText("New Group/Add a Member")).click();
		addGroup(groupName, 3);
		Assert.assertTrue("Dr. Cherry is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrCherry), driver));

		//Delete the first provider from the Group
		deleteGroupMember(new String[]{valueOfDrApple});
		Assert.assertFalse("Dr. Apple is Not deleted successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver));

		//Delete the rest of the Group
		deleteGroupMember(new String[]{valueOfDrBerry, valueOfDrCherry});
		Assert.assertTrue("Group is Not deleted successfully.",
				(!PageUtil.isExistsBy(By.name(valueOfDrBerry), driver)) &&
						(!PageUtil.isExistsBy(By.name(valueOfDrCherry), driver)));
	}

}



