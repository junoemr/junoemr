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

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.BedDemographicStatus;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BedDemographicStatusDaoTest extends DaoTestFixtures
{
	@Autowired
	protected BedDemographicStatusDao bedDemographicStatusDao;

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"bed_demographic_status"
		};
	}


	@Test
	public void testCreate() throws Exception {
		
		BedDemographicStatus entity = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		bedDemographicStatusDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testBedDemographicStatusExists() throws Exception {
		
		BedDemographicStatus bDS1 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS1);
		bedDemographicStatusDao.persist(bDS1);
		
		BedDemographicStatus bDS2 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS2);
		bedDemographicStatusDao.persist(bDS2);
		
		boolean expectedResult = true;
		boolean result = bedDemographicStatusDao.bedDemographicStatusExists(bDS1.getId());
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetBedDemographicStatuses() throws Exception {
		
		BedDemographicStatus bDS1 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS1);
		bedDemographicStatusDao.persist(bDS1);
		
		BedDemographicStatus bDS2 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS2);
		bedDemographicStatusDao.persist(bDS2);
		
		BedDemographicStatus bDS3 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS3);
		bedDemographicStatusDao.persist(bDS3);
		
		BedDemographicStatus expectedResult[] = {bDS1, bDS2, bDS3};
		BedDemographicStatus result[] = bedDemographicStatusDao.getBedDemographicStatuses();
		
		Logger logger = MiscUtils.getLogger();
				
		if (result.length != expectedResult.length) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.length; i++) {
			if (!expectedResult[i].equals(result[i])){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetBedDemographicStatus() throws Exception {
		
		BedDemographicStatus bDS1 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS1);
		bedDemographicStatusDao.persist(bDS1);
		
		BedDemographicStatus bDS2 = new BedDemographicStatus();
		EntityDataGenerator.generateTestDataForModelClass(bDS2);
		bedDemographicStatusDao.persist(bDS2);
		
		BedDemographicStatus expectedResult = bDS1;
		BedDemographicStatus result = bedDemographicStatusDao.getBedDemographicStatus(bDS1.getId());
		
		assertEquals(expectedResult, result);
	}
}