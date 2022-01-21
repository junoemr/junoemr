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
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.contact.dao.DemographicContactDao;
import org.oscarehr.contact.entity.DemographicContact;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicContactDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DemographicContactDao demographicContactDao;

	Logger logger = MiscUtils.getLogger();

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("DemographicContact");
	}

	@Test
	/**
	 * Ensures that the findByDemographicNo() method selects records where
	 * demographic number matches specified demographic number.
	 */
	public void testFindByDemographicNo() throws Exception {
		int demographicNo = 10;

		DemographicContact contact1 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact1);
		contact1.setDemographicNo(demographicNo);
		contact1.setDeleted(false);

		// Deleted is true; should not be selected
		DemographicContact contact2 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact2);
		contact2.setDemographicNo(demographicNo);
		contact2.setDeleted(true);

		DemographicContact contact3 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact3);
		contact3.setDemographicNo(demographicNo);
		contact3.setDeleted(false);

		// Demographic number does not match; should not be selected
		DemographicContact contact4 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact4);
		contact4.setDemographicNo(5);
		contact4.setDeleted(false);

		demographicContactDao.persist(contact1);
		demographicContactDao.persist(contact2);
		demographicContactDao.persist(contact3);
		demographicContactDao.persist(contact4);

		List<DemographicContact> result = demographicContactDao.findByDemographicNo(demographicNo);
		List<DemographicContact> expectedResult = new ArrayList<DemographicContact>(Arrays.asList(
				contact1,
				contact3
				));

		assertEquals(expectedResult.size(), result.size());
		assertTrue(result.containsAll(expectedResult));
	}

	@Test
	/**
	 * Ensures that the findByDemographicNoAndCategory() method selects records where
	 * demographic number matches, and the category matches.
	 */
	public void testFindByDemographicNoAndCategory() throws Exception {
		int demographicNo = 10;
		String category = "CAT1";

		DemographicContact contact1 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact1);
		contact1.setDemographicNo(demographicNo);
		contact1.setDeleted(false);
		contact1.setCategory(category);

		// Deleted is true; should not be selected
		DemographicContact contact2 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact2);
		contact2.setDemographicNo(demographicNo);
		contact2.setDeleted(true);
		contact2.setCategory(category);

		// Category does not match; should not be selected
		DemographicContact contact3 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact3);
		contact3.setDemographicNo(demographicNo);
		contact3.setDeleted(false);
		contact3.setCategory("CAT2");

		// Demographic number does not match; should not be selected
		DemographicContact contact4 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact4);
		contact4.setDemographicNo(5);
		contact4.setDeleted(false);
		contact4.setCategory(category);

		DemographicContact contact5 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact5);
		contact5.setDemographicNo(demographicNo);
		contact5.setDeleted(false);
		contact5.setCategory(category);

		demographicContactDao.persist(contact1);
		demographicContactDao.persist(contact2);
		demographicContactDao.persist(contact3);
		demographicContactDao.persist(contact4);
		demographicContactDao.persist(contact5);

		List<DemographicContact> result = demographicContactDao.findByDemographicNoAndCategory(demographicNo, category);
		List<DemographicContact> expectedResult = new ArrayList<DemographicContact>(Arrays.asList(
				contact1,
				contact5
				));
		
		assertEquals(expectedResult.size(), result.size());
		assertTrue(result.containsAll(expectedResult));
	}

	@Test
	/**
	 * Ensures that the find() method selects records where
	 * the demographic id, category, and contact id all match.
	 */
	public void testFind_demoNumContactId() throws Exception {
		int demographicNo = 10;
		String category = "CAT1";
		String contactId = "101";

		DemographicContact contact1 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact1);
		contact1.setDemographicNo(demographicNo);
		contact1.setDeleted(false);
		contact1.setCategory(category);
		contact1.setContactId(contactId);

		// Deleted is true; should not be selected
		DemographicContact contact2 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact2);
		contact2.setDemographicNo(demographicNo);
		contact2.setDeleted(true);
		contact2.setCategory(category);
		contact2.setContactId(contactId);

		DemographicContact contact3 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact3);
		contact3.setDemographicNo(demographicNo);
		contact3.setDeleted(false);
		contact3.setCategory("CAT2");
		contact3.setContactId(contactId);

		// Demographic number does not match; should not be selected
		DemographicContact contact4 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact4);
		contact4.setDemographicNo(5);
		contact4.setDeleted(false);
		contact4.setCategory(category);
		contact4.setContactId(contactId);

		// Contact ID does not match; should not be selected.
		DemographicContact contact5 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact5);
		contact5.setDemographicNo(demographicNo);
		contact5.setDeleted(false);
		contact5.setCategory(category);
		contact5.setContactId("102");
		
		DemographicContact contact6 = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(contact6);
		contact6.setDemographicNo(demographicNo);
		contact6.setDeleted(false);
		contact6.setCategory(category);
		contact6.setContactId(contactId);
		
		demographicContactDao.persist(contact1);
		demographicContactDao.persist(contact2);
		demographicContactDao.persist(contact3);
		demographicContactDao.persist(contact4);
		demographicContactDao.persist(contact5);
		demographicContactDao.persist(contact6);
		
		List<DemographicContact> results = demographicContactDao.findByDemographicNoAndCategory(demographicNo, category);
		List<DemographicContact> expectedResult = new ArrayList<>(Arrays.asList(
				contact1,
				contact3,
				contact6
				));
		
		assertEquals(expectedResult.size(), results.size());
		assertTrue(results.containsAll(expectedResult));
	}

	public void testCreate() throws Exception {
		DemographicContact entity = new DemographicContact();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		demographicContactDao.persist(entity);
		assertNotNull(entity.getId());
	}
}
