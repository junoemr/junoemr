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

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SecRoleDaoTest extends DaoTestFixtures
{
	
	@Autowired
	protected SecRoleDao dao;

	@Before
	public void before() throws Exception
	{
		SchemaUtils.restoreTable(false, "secRole");
	}

	@Test
	public void testFindAll() throws Exception {
		
		String name1 = "alpha";
		String name2 = "bravo";
		String name3 = "charlie";
		
		SecRole secRole1 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole1);
		secRole1.setName(name1);
		secRole1.setDeletedBy(secRole1.getDeletedBy().substring(0, 6));
		dao.persist(secRole1);
		
		SecRole secRole2 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole2);
		secRole2.setName(name2);
		secRole2.setDeletedBy(secRole1.getDeletedBy().substring(0, 6));
		dao.persist(secRole2);
		
		SecRole secRole3 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole3);
		secRole3.setName(name3);
		secRole3.setDeletedBy(secRole1.getDeletedBy().substring(0, 6));
		dao.persist(secRole3);
		
		List<SecRole> expectedResult = new ArrayList<SecRole>(Arrays.asList(secRole1, secRole2, secRole3));		
		List<SecRole> result = dao.findAll();
		
		Logger logger = MiscUtils.getLogger();
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.  Expected Size: " + expectedResult.size() + "Result size: " +result.size());
			fail("Array sizes do not match.");
		}

		for (int i = 0; i < expectedResult.size(); i++) {
			if (!secRolesEquals(expectedResult.get(i), result.get(i)))
			{
				logger.warn("Items do not match.");
				fail("Items do not match.");
			}
		}
		assertTrue(true);
	}

	@Test 
	public void testFindAllOrderByRole() throws Exception {
		
		String name1 = "alpha";
		String name2 = "bravo";
		String name3 = "charlie";
		
		SecRole secRole1 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole1);
		secRole1.setName(name3);
		dao.persist(secRole1);
		
		SecRole secRole2 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole2);
		secRole2.setName(name1);
		dao.persist(secRole2);
		
		SecRole secRole3 = new SecRole();
		EntityDataGenerator.generateTestDataForModelClass(secRole3);
		secRole3.setName(name2);
		dao.persist(secRole3);
		
		List<SecRole> expectedResult = new ArrayList<SecRole>(Arrays.asList(secRole2, secRole3, secRole1));		
		List<SecRole> result = dao.findAllOrderByRole();
		
		Logger logger = MiscUtils.getLogger();
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.  Expected Size: " + expectedResult.size() + "Result size: " +result.size());
			fail("Array sizes do not match.");
		}

		for (int i = 0; i < expectedResult.size(); i++) {
			if (!secRolesEquals(expectedResult.get(i), result.get(i)))
			{
				logger.warn("Items do not match.");
				fail("Items do not match.");
			}
		}
		assertTrue(true);
	}

	private boolean secRolesEquals(SecRole secRole1, SecRole secRole2)
	{
		if(
			secRole1.getName().equals(secRole2.getName()) &&
			secRole1.getDescription().equals(secRole2.getDescription()) &&
			secRole1.isSystemManaged() == secRole2.isSystemManaged()
		)
		{
			return true;
		}

		return false;
	}
}