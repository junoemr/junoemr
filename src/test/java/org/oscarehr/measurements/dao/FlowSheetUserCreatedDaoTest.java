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
package org.oscarehr.measurements.dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.measurements.model.FlowSheetUserCreated;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FlowSheetUserCreatedDaoTest extends DaoTestFixtures
{
	@Autowired
	protected FlowSheetUserCreatedDao flowSheetUserCreatedDao;


	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("FlowSheetUserCreated");
	}

        @Test
        public void testCreate() throws Exception {
                FlowSheetUserCreated entity = new FlowSheetUserCreated();
                EntityDataGenerator.generateTestDataForModelClass(entity);
                flowSheetUserCreatedDao.persist(entity);
                assertNotNull(entity.getId());
        }

	@Test
	public void testGetAllUserCreatedFlowSheets() throws Exception {
		
		boolean isArchived = true;
		
		FlowSheetUserCreated flowSheetUserCreated1 = new FlowSheetUserCreated();
		EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated1);
		flowSheetUserCreated1.setArchived(!isArchived);
		flowSheetUserCreatedDao.persist(flowSheetUserCreated1);
		
		FlowSheetUserCreated flowSheetUserCreated2 = new FlowSheetUserCreated();
		EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated2);
		flowSheetUserCreated2.setArchived(isArchived);
		flowSheetUserCreatedDao.persist(flowSheetUserCreated2);
		
		FlowSheetUserCreated flowSheetUserCreated3 = new FlowSheetUserCreated();
		EntityDataGenerator.generateTestDataForModelClass(flowSheetUserCreated3);
		flowSheetUserCreated3.setArchived(!isArchived);
		flowSheetUserCreatedDao.persist(flowSheetUserCreated3);
		
		List<FlowSheetUserCreated> expectedResult = new ArrayList<FlowSheetUserCreated>(Arrays.asList(flowSheetUserCreated1, flowSheetUserCreated2, flowSheetUserCreated3));
		List<FlowSheetUserCreated> result = flowSheetUserCreatedDao.getAllUserCreatedFlowSheets();

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
