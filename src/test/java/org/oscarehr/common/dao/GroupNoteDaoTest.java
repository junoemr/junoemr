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
/**
 * @author Shazib
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.assertEquals;
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
import org.oscarehr.common.model.GroupNoteLink;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GroupNoteDaoTest extends DaoTestFixtures
{
	@Autowired
	protected GroupNoteDao groupNoteDao;
	
	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("GroupNoteLink");
	}

	@Test
	public void testFindLinksByDemographic() throws Exception {
		
		int demographicNo1 = 101;
		int demographicNo2 = 202;
		
		boolean isActive = true;
		
		GroupNoteLink groupNoteLink1 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink1);
		groupNoteLink1.setDemographicNo(demographicNo1);
		groupNoteLink1.setActive(isActive);
		groupNoteDao.persist(groupNoteLink1);
		
		GroupNoteLink groupNoteLink2 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink2);
		groupNoteLink2.setDemographicNo(demographicNo2);
		groupNoteLink2.setActive(isActive);
		groupNoteDao.persist(groupNoteLink2);
		
		GroupNoteLink groupNoteLink3 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink3);
		groupNoteLink3.setDemographicNo(demographicNo1);
		groupNoteLink3.setActive(isActive);
		groupNoteDao.persist(groupNoteLink3);
		
		GroupNoteLink groupNoteLink4 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink4);
		groupNoteLink4.setDemographicNo(demographicNo1);
		groupNoteLink4.setActive(!isActive);
		groupNoteDao.persist(groupNoteLink4);
		
		List<GroupNoteLink> expectedResult = new ArrayList<GroupNoteLink>(Arrays.asList(groupNoteLink1, groupNoteLink3));
		List<GroupNoteLink> result = groupNoteDao.findLinksByDemographic(demographicNo1);

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

	@Test
	public void testFindLinksByNoteId() throws Exception {
		
		int noteId1 = 101;
		int noteId2 = 202;
		
		boolean isActive = true;
		
		GroupNoteLink groupNoteLink1 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink1);
		groupNoteLink1.setNoteId(noteId1);
		groupNoteLink1.setActive(isActive);
		groupNoteDao.persist(groupNoteLink1);
		
		GroupNoteLink groupNoteLink2 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink2);
		groupNoteLink2.setNoteId(noteId2);
		groupNoteLink2.setActive(isActive);
		groupNoteDao.persist(groupNoteLink2);
		
		GroupNoteLink groupNoteLink3 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink3);
		groupNoteLink3.setNoteId(noteId1);
		groupNoteLink3.setActive(isActive);
		groupNoteDao.persist(groupNoteLink3);
		
		GroupNoteLink groupNoteLink4 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink4);
		groupNoteLink4.setNoteId(noteId1);
		groupNoteLink4.setActive(!isActive);
		groupNoteDao.persist(groupNoteLink4);
		
		List<GroupNoteLink> expectedResult = new ArrayList<GroupNoteLink>(Arrays.asList(groupNoteLink1, groupNoteLink3));
		List<GroupNoteLink> result = groupNoteDao.findLinksByNoteId(noteId1);

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

	@Test
	public void testGetNumberOfLinksByNoteId() throws Exception {

		int noteId1 = 101;
		int noteId2 = 202;
				
		GroupNoteLink groupNoteLink1 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink1);
		groupNoteLink1.setNoteId(noteId1);
		groupNoteDao.persist(groupNoteLink1);
		
		GroupNoteLink groupNoteLink2 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink2);
		groupNoteLink2.setNoteId(noteId2);
		groupNoteDao.persist(groupNoteLink2);
		
		GroupNoteLink groupNoteLink3 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink3);
		groupNoteLink3.setNoteId(noteId1);
		groupNoteDao.persist(groupNoteLink3);
		
		GroupNoteLink groupNoteLink4 = new GroupNoteLink();
		EntityDataGenerator.generateTestDataForModelClass(groupNoteLink4);
		groupNoteLink4.setNoteId(noteId1);
		groupNoteDao.persist(groupNoteLink4);
		
		int expectedResult = 3;
		int result = groupNoteDao.getNumberOfLinksByNoteId(noteId1);
		
		assertEquals(expectedResult, result);
	}
}