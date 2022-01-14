/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.IssueGroup;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IssueGroupDaoTest extends DaoTestFixtures
{
	@Autowired
	protected IssueGroupDao issueGroupDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"IssueGroup"
		};
	}

	@Test
	public void testCreate() throws Exception {
		
		IssueGroup issueGroup1 = new IssueGroup();
		EntityDataGenerator.generateTestDataForModelClass(issueGroup1);
		issueGroupDao.persist(issueGroup1);
		
		IssueGroup issueGroup2 = new IssueGroup();
		EntityDataGenerator.generateTestDataForModelClass(issueGroup2);
		issueGroupDao.persist(issueGroup2);
		
		IssueGroup issueGroup3 = new IssueGroup();
		EntityDataGenerator.generateTestDataForModelClass(issueGroup3);
		issueGroupDao.persist(issueGroup3);
		
		IssueGroup issueGroup4 = new IssueGroup();
		EntityDataGenerator.generateTestDataForModelClass(issueGroup4);
		issueGroupDao.persist(issueGroup4);
		
		List<IssueGroup> expectedResult = new ArrayList<IssueGroup>(Arrays.asList(issueGroup1, issueGroup2, issueGroup3, issueGroup4));
		List<IssueGroup> result = issueGroupDao.findAll();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
}