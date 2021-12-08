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

import static integration.tests.EditFamilyHistoryClassicUIIT.editCPPNoteTest;
import static integration.tests.util.junoUtil.Navigation.ECHART_URL;

import integration.tests.config.TestConfig;
import integration.tests.util.SeleniumTestBase;
import integration.tests.util.junoUtil.Navigation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.JunoApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {JunoApplication.class, TestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EditOtherMedsClassicUIIT extends SeleniumTestBase
{

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"casemgmt_cpp", "casemgmt_issue", "casemgmt_issue_notes", "casemgmt_note",
			"casemgmt_note_ext", "eChart", "hash_audit", "log"
		};
	}

	@Before
	public void setup()
	{
		loadSpringBeans();
		databaseUtil.createTestDemographic();
	}

	@Test
	public void editOtherMedsTest()
	{
		String otherMeds = "Other Meds";
		String otherMedsId = "menuTitleoMeds";
		driver.get(Navigation.getOscarUrl(randomTomcatPort) + ECHART_URL);
		editCPPNoteTest(otherMeds, otherMedsId);
	}
}
