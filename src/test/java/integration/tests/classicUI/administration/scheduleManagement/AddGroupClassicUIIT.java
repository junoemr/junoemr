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

package integration.tests.classicUI.administration.scheduleManagement;

import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drApple;
import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drBerry;
import static integration.tests.classicUI.administration.userManagement.AddProvidersIT.drCherry;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClick;
import static integration.tests.util.seleniumUtil.ActionUtil.findWaitClickByXpath;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessAdministrationSectionClassicUI;

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

public class AddGroupClassicUIIT extends SeleniumTestBase
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

	public static void addGroup(String groupName, int groupSize)
	{
		webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='mygroup_no']")));
		driver.findElement(By.xpath("//input[@name='mygroup_no']")).sendKeys(groupName);
		for (int i = 1; i <= groupSize; i ++)
		{
			driver.findElement(By.xpath("//input[@value='" + i + "']")).click();
		}
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Save']");
		findWaitClick(driver, webDriverWait, By.linkText("View Group List"));
	}

	public static void deleteGroupMember(String[] groupMemberValue)
	{
		for (int i = 0; i < groupMemberValue.length; i ++)
		{
			findWaitClickByXpath(driver, webDriverWait, "//input[@name='" + groupMemberValue[i] + "']");
		}
		findWaitClickByXpath(driver, webDriverWait, "//input[@value='Delete']");
		findWaitClick(driver, webDriverWait, By.linkText("View Group List"));
	}

	@Test
	public void addGroupsClassicUITest()
	{
		//Add a New Group with two providers: Dr. Apple and Dr. Berry
		accessAdministrationSectionClassicUI(driver, webDriverWait, "Schedule Management", "Add a Group"
        );
		addGroup(groupName, 2);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrBerry)));
		Assert.assertTrue("Group is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver));

		//Add a Member: Dr. Cherry
		driver.findElement(By.linkText("New Group/Add a Member")).click();
		addGroup(groupName, 3);
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrCherry)));
		Assert.assertTrue("New member is Not added successfully.",
				PageUtil.isExistsBy(By.name(valueOfDrApple), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrBerry), driver) &&
						PageUtil.isExistsBy(By.name(valueOfDrCherry), driver));

		//Delete the first provider from the Group
		deleteGroupMember(new String[]{valueOfDrApple});
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.name(valueOfDrCherry)));
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