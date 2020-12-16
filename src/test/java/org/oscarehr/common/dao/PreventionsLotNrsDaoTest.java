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
import java.util.Collections;


import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.PreventionsLotNrs;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PreventionsLotNrsDaoTest extends DaoTestFixtures
{
	@Autowired
	protected PreventionsLotNrsDao preventionsLotNrsDao;
	
	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("PreventionsLotNrs");
	}

	@Test
	public void testFindLotNrData() throws Exception {
		String prevention = "Flu";
		String lotNr1 = "abcdef1";
		String lotNr2 = "abcdef2";
				
		PreventionsLotNrs p1 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p1);
		p1.setLotNr(lotNr1);
		p1.setPreventionType(prevention);
		p1.setProviderNo("unit_tester");
		p1.setCreationDate(new java.util.Date());
		p1.setDeleted(false);
		preventionsLotNrsDao.persist(p1);
		
		PreventionsLotNrs p2 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p2);
		p2.setLotNr(lotNr2);
		p2.setPreventionType(prevention);
		p2.setProviderNo("unit_tester");
		p2.setCreationDate(new java.util.Date());
		p2.setDeleted(false);
		preventionsLotNrsDao.persist(p2);
		
		List<PreventionsLotNrs> expectedResult = new ArrayList<PreventionsLotNrs>(Arrays.asList(p1, p2));
		List<PreventionsLotNrs> result = preventionsLotNrsDao.findLotNrData(false);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items do not match. Correct lot nr data not found.");
				fail("Items do not match. Correct lot nr data not found.");
			}
		}
		
		List<PreventionsLotNrs> result2 = preventionsLotNrsDao.findLotNrData(true);
		
		if (result2.size() != 0) {
			logger.warn("Deleted items found but do not exist in test setup.");
			fail("Deleted items found but do not exist in test setup.");
		}

		assertTrue(true);
	}
	
	@Test
	public void testFindByName() throws Exception {
		String prevention = "Flu";
		String lotNr1 = "abcdef1";
		String lotNr2 = "abcdef2";
		String lotNr3 = "abcdef3";
				
		PreventionsLotNrs p1 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p1);
		p1.setLotNr(lotNr1);
		p1.setPreventionType(prevention);
		p1.setProviderNo("unit_tester");
		p1.setCreationDate(new java.util.Date());
		p1.setDeleted(false);
		preventionsLotNrsDao.persist(p1);
		
		PreventionsLotNrs p2 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p2);
		p2.setLotNr(lotNr2);
		p2.setPreventionType(prevention);
		p2.setProviderNo("unit_tester");
		p2.setCreationDate(new java.util.Date());
		p2.setDeleted(false);
		preventionsLotNrsDao.persist(p2);
		
		PreventionsLotNrs p3 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p3);
		p3.setLotNr(lotNr3);
		p3.setPreventionType(prevention);
		p3.setProviderNo("unit_tester");
		p3.setCreationDate(new java.util.Date());
		p3.setDeleted(true);
		preventionsLotNrsDao.persist(p3);
		
		PreventionsLotNrs singlePrevLotNr = preventionsLotNrsDao.findByName(prevention, lotNr3, true);
		
		Logger logger = MiscUtils.getLogger();
		if (!singlePrevLotNr.equals(p3))
		{		
			logger.warn("Deleted lot number not found.");
			fail("Deleted lot number not found.");
		}
		assertTrue(true);
}
	
	@Test
	public void testFindLotNrs() throws Exception
	{
		String prevention = "Flu";
		List<String> expectedResult = new ArrayList<>();
		List<PreventionsLotNrs> preventionLotNrs = new ArrayList<>();

		for (int i = 0; i < 5; i++)
		{
			String lotNr = "abcdef";
			lotNr += String.valueOf(i);
			expectedResult.add(lotNr);
			PreventionsLotNrs prevLotNr = new PreventionsLotNrs();
			EntityDataGenerator.generateTestDataForModelClass(prevLotNr);
			prevLotNr.setLotNr(expectedResult.get(i));
			prevLotNr.setPreventionType(prevention);
			prevLotNr.setProviderNo("unit_test");
			prevLotNr.setCreationDate(new java.util.Date());
			prevLotNr.setDeleted(false);
			preventionLotNrs.add(prevLotNr);
			preventionsLotNrsDao.persist(preventionLotNrs.get(i));
		}
		//we need to assign one specific date to a few lotNrs to test the ordering results
		preventionLotNrs.get(0).setLastUpdateDate(new java.util.Date(2020, 10, 30, 11, 11, 11));
		preventionsLotNrsDao.merge(preventionLotNrs.get(0));
		preventionLotNrs.get(1).setLastUpdateDate(new java.util.Date(2020, 10, 30, 11, 11, 11));
		preventionsLotNrsDao.merge(preventionLotNrs.get(1));
		preventionLotNrs.get(2).setLastUpdateDate(new java.util.Date(2020, 10, 30, 11, 11, 11));
		preventionsLotNrsDao.merge(preventionLotNrs.get(2));

		//reverse the order since the results from the database are ordered to return the most recently updated lotNrs first
		Collections.reverse(expectedResult);
		List<String> result = preventionsLotNrsDao.findLotNrs(prevention, null);
		
		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size())
		{
			logger.warn("FindLotNrs array sizes do not match.");
			logger.warn("resultsize="+result.size()+"expectedResult.size="+expectedResult.size());
			fail("FindLotNrs array sizes do not match.");
		}

		for (int i = 0; i < expectedResult.size(); i++)
		{
			if (!expectedResult.get(i).equals(result.get(i)))
			{
				logger.warn("FindLotNrs items do not match. All lot nr items not found.");
				fail("FindLotNrs items do not match. All lot nr items not found.");
			}
		}
		assertTrue(true);
}
	
	
	@Test
	public void testFindPagedData() throws Exception {
		String prevention = "Flu";
		String lotNr1 = "abcdef1";
		String lotNr2 = "abcdef2";
		String lotNr3 = "abcdef3";
				
		PreventionsLotNrs p1 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p1);
		p1.setLotNr(lotNr1);
		p1.setPreventionType(prevention);
		p1.setProviderNo("unit_tester");
		p1.setCreationDate(new java.util.Date());
		p1.setDeleted(false);
		preventionsLotNrsDao.persist(p1);
		
		PreventionsLotNrs p2 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p2);
		p2.setLotNr(lotNr2);
		p2.setPreventionType(prevention);
		p2.setProviderNo("unit_tester");
		p2.setCreationDate(new java.util.Date());
		p2.setDeleted(false);
		preventionsLotNrsDao.persist(p2);
		
		PreventionsLotNrs p3 = new PreventionsLotNrs();
		EntityDataGenerator.generateTestDataForModelClass(p3);
		p3.setLotNr(lotNr3);
		p3.setPreventionType(prevention);
		p3.setProviderNo("unit_tester");
		p3.setCreationDate(new java.util.Date());
		p3.setDeleted(true);
		preventionsLotNrsDao.persist(p3);
		
		List<PreventionsLotNrs> result = preventionsLotNrsDao.findPagedData(prevention, null, 0, 10);
		
		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != 3) {
			logger.warn("FindPagedData call does not return correct number of items.");
			fail("FindPagedData call does not return correct number of items.");
		}
		assertTrue(true);
}
}
