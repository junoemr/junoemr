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

package integration.tests.junoUI;

import static integration.tests.classicUI.AddSitesClassicUIIT.addNewSites;
import static integration.tests.util.seleniumUtil.SectionAccessUtil.accessSectionJUNOUI;

import integration.tests.util.SeleniumTestBase;
import integration.tests.util.data.SiteTestCollection;
import integration.tests.util.data.SiteTestData;
import integration.tests.util.seleniumUtil.PageUtil;
import junit.framework.Assert;
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
public class AddSitesJUNOUIIT extends SeleniumTestBase
{
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"admission", "log", "site", "log_ws_rest", "provider_recent_demographic_access", "property"
		};
	}

	@Test
	public void addSitesJUNOUITest()
	{
		SiteTestData siteJuno = SiteTestCollection.siteMap.get(SiteTestCollection.siteNames[1]);
		accessSectionJUNOUI(driver, webDriverWait, "Admin");
		addNewSites(siteJuno, "content-frame");
		webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(siteJuno.siteName)));
		Assert.assertTrue(PageUtil.isExistsBy(By.linkText(siteJuno.siteName), driver));
		Assert.assertTrue(PageUtil.isExistsBy(By.xpath(".//td[contains(.,site.shortName)]"), driver));

	}
}