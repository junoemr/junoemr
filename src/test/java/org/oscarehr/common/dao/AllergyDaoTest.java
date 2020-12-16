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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.allergy.dao.AllergyDao;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.allergy.model.Allergy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AllergyDaoTest extends DaoTestFixtures
{
	@Autowired
	protected AllergyDao allergyDao;

	public AllergyDaoTest() {
	}

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable(new String[] { "allergies", "demographic_merged" });
	}

	@Test
	public void testCreate() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergyDao.persist(allergy);
		assertNotNull(allergy.getId());
	}

	@Test
	public void testCount() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergyDao.persist(allergy);
		assertEquals(allergyDao.getCountAll(), 1);

	}

	@Test
	public void testFindAllergies() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(1);
		allergyDao.persist(allergy);

		allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(1);
		allergyDao.persist(allergy);

		allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(2);
		allergyDao.persist(allergy);

		assertEquals(allergyDao.findAllergies(1).size(), 2);
		assertEquals(allergyDao.findAllergies(2).size(), 1);
		
		Calendar cal=new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		List<Allergy> results= allergyDao.findByUpdateDate(cal.getTime(), 99);
		assertTrue(results.size()>0);

		cal.add(Calendar.DAY_OF_YEAR, 2);
		results= allergyDao.findByUpdateDate(cal.getTime(), 99);
		assertEquals(0, results.size());
	}

	@Test
	public void testFindActive() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(3);
		allergy.setArchived(false);
		allergyDao.persist(allergy);

		allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(3);
		allergy.setArchived(false);
		allergyDao.persist(allergy);

		allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergy.setDemographicNo(3);
		allergy.setArchived(true);
		allergyDao.persist(allergy);

		assertEquals(allergyDao.findActiveAllergies(3).size(), 2);

	}

	public void testFind() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergyDao.persist(allergy);
		Integer id = allergy.getId();
		allergy = allergyDao.find(allergy.getId());
		assertNotNull(allergy);
		assertEquals(id, allergy.getId());
	}

	@Test
	public void testDelete() throws Exception {
		Allergy allergy = new Allergy();
		EntityDataGenerator.generateTestDataForModelClass(allergy);
		allergyDao.persist(allergy);
		allergyDao.remove(allergy.getId());
		assertEquals(allergyDao.getCountAll(), 0);

	}
}
