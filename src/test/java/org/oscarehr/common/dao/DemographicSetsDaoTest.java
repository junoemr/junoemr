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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DemographicSets;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicSetsDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DemographicSetsDao demographicSetsDao;

	@Autowired
	protected DemographicDao demographicDao;

	public DemographicSetsDaoTest() {
	}

	Demographic demographic = null;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("demographicSets", "admission", "demographic", "lst_gender", "demographic_merged", "program", "health_safety", "provider");
		//SchemaUtils.restoreAllTables();

		demographic = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(demographic);
		demographic.setDemographicNo(null);
		demographicDao.save(demographic);
	}

	@Test
	public void testCreate() throws Exception {
		DemographicSets entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		demographicSetsDao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testFindBySetName() throws Exception {
		DemographicSets entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		assertEquals(2, demographicSetsDao.findBySetName("a").size());

	}

	@Test
	public void testFindBySetNames() throws Exception {
		DemographicSets entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("b");
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		List<String> names = new ArrayList<String>();
		names.add("a");
		names.add("b");

		assertEquals(2, demographicSetsDao.findBySetNames(names).size());

	}

	@Test
	public void testFindBySetNameAndEligibility() throws Exception {
		DemographicSets entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		entity.setEligibility("0");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		entity.setEligibility("1");
		demographicSetsDao.persist(entity);

		assertEquals(1, demographicSetsDao.findBySetNameAndEligibility("a","0").size());
		assertEquals(1, demographicSetsDao.findBySetNameAndEligibility("a","1").size());

	}

	@Test
	public void testFindSetNamesByDemographicNo() throws Exception {
		DemographicSets entity = new DemographicSets();
		//Demographic demographic = demographicDao.getDemographic("1");
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setName("a");
		entity.setDemographic(demographic);
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		//entity.setDemographic(new Demographic(1));
		entity.setName("b");
		entity.setDemographic(demographic);
		entity.setArchive("0");
		demographicSetsDao.persist(entity);

		List<String> names = demographicSetsDao.findSetNamesByDemographicNo(demographic.getDemographicNo());
		assertEquals(2,names.size());
		assertTrue(names.contains("a"));
		assertTrue(names.contains("b"));
	}

	@Test
	public void testFindSetNames() throws Exception {
		DemographicSets entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("a");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("b");
		demographicSetsDao.persist(entity);

		entity = new DemographicSets();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setName("b");
		demographicSetsDao.persist(entity);

		assertEquals(2, demographicSetsDao.findSetNames().size());
	}
}
