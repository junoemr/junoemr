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
import org.oscarehr.common.model.Property;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PropertyDaoTest extends DaoTestFixtures
{
	@Autowired
	protected PropertyDao propertyDao;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("property");
	}

	@Test
	public void testFindByName() throws Exception {
		
		String name1 = "alpha1";
		String name2 = "bravo1";
		String name3 = "charlie1";
		
		Property property1 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property1);
		property1.setName(name1);
		property1.setProviderNo("111");
		propertyDao.persist(property1);
		
		Property property2 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property2);
		property2.setName(name2);
		property2.setProviderNo("111");
		propertyDao.persist(property2);
		
		Property property3 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property3);
		property3.setName(name3);
		property3.setProviderNo("111");
		propertyDao.persist(property3);
		
		List<Property> expectedResult = new ArrayList<Property>(Arrays.asList(property1));
		List<Property> result = propertyDao.findByName(name1);

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
	public void testFindByNameAndProvider() throws Exception {
		
		String name1 = "alpha2";
		String name2 = "bravo2";
		String name3 = "charlie2";
		String name4 = "delta2";
		
		String providerNo1 = "101";
		String providerNo2 = "202";
		
		Property property1 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property1);
		property1.setName(name1);
		property1.setProviderNo(providerNo1);
		propertyDao.persist(property1);
		
		Property property2 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property2);
		property2.setName(name2);
		property2.setProviderNo(providerNo2);
		propertyDao.persist(property2);
		
		Property property3 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property3);
		property3.setName(name3);
		property3.setProviderNo(providerNo1);
		propertyDao.persist(property3);
		
		Property property4 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property4);
		property4.setName(name4);
		property4.setProviderNo(providerNo2);
		propertyDao.persist(property4);
		
		List<Property> expectedResult = new ArrayList<Property>(Arrays.asList(property1, property3));
		List<Property> result = propertyDao.findByProvider(providerNo1);

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
	public void testCheckByName() throws Exception {
		
		String name1 = "alpha3";
		String name2 = "bravo3";
		String name3 = "charlie3";
		
		Property property1 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property1);
		property1.setName(name1);
		property1.setProviderNo("111");
		propertyDao.persist(property1);
		
		Property property2 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property2);
		property2.setName(name2);
		property2.setProviderNo("111");
		propertyDao.persist(property2);
		
		Property property3 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property3);
		property3.setName(name3);
		property3.setProviderNo("111");
		propertyDao.persist(property3);
		
		Property expectedResult = property2;
		Property result = propertyDao.checkByName(name2);
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testFindByNameAndValue() throws Exception {

		String name1 = "alpha4";
		String name2 = "bravo4";
		String name3 = "charlie4";
		String name4 = "delta4";
		
		String value1 = "111";
		String value2 = "222";
		
		Property property1 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property1);
		property1.setName(name1);
		property1.setValue(value1);
		property1.setProviderNo("111");
		propertyDao.persist(property1);
		
		Property property2 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property2);
		property2.setName(name2);
		property2.setValue(value1);
		property2.setProviderNo("111");
		propertyDao.persist(property2);
		
		Property property3 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property3);
		property3.setName(name3);
		property3.setValue(value1);
		property3.setProviderNo("111");
		propertyDao.persist(property3);
		
		Property property4 = new Property();
		EntityDataGenerator.generateTestDataForModelClass(property4);
		property4.setName(name4);
		property4.setValue(value2);
		property4.setProviderNo("111");
		propertyDao.persist(property4);
		
		List<Property> expectedResult = new ArrayList<Property>(Arrays.asList(property1));
		List<Property> result = propertyDao.findByNameAndValue(name1, value1);

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
