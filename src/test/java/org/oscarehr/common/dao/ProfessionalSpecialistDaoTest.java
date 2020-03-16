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
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProfessionalSpecialistDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ProfessionalSpecialistDao professionalSpecialistDao;
	
	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable(false, "professionalSpecialists");
	}

	@Test
	public void testFindAll() throws Exception {

		String fName1 = "FirstName1";
		String fName2 = "FirstName2";
		String fName3 = "FirstName3";
		
		String lName1 = "LastName1";
		String lName2 = "LastName2";
		String lName3 = "LastName3";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setFirstName(fName2);
		professionalSpecialist1.setLastName(lName2);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setFirstName(fName3);
		professionalSpecialist2.setLastName(lName3);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setFirstName(fName1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist3, professionalSpecialist1, professionalSpecialist2));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findAll();

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
	public void testFindByEDataUrlNotNull() throws Exception {
		
		String fName1 = "FirstName1";
		String fName2 = "FirstName2";
		String fName3 = "FirstName3";
		String fName4 = "FirstName4";
		
		String lName1 = "LastName1";
		String lName2 = "LastName2";
		String lName3 = "LastName3";
		String lName4 = "LastName4";
		
		String eDataUrl1 = "eData1";
		String eDataUrl2 = "eData2";
		String eDataUrl3 = "eData3";
		String eDataUrl4 = null;
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setFirstName(fName2);
		professionalSpecialist1.setLastName(lName2);
		professionalSpecialist1.seteDataUrl(eDataUrl1);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setFirstName(fName3);
		professionalSpecialist2.setLastName(lName3);
		professionalSpecialist2.seteDataUrl(eDataUrl2);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setFirstName(fName1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.seteDataUrl(eDataUrl3);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		ProfessionalSpecialist professionalSpecialist4 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist4);
		professionalSpecialist4.setFirstName(fName4);
		professionalSpecialist4.setLastName(lName4);
		professionalSpecialist4.seteDataUrl(eDataUrl4);
		professionalSpecialist4.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist4);
		
		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist3, professionalSpecialist1, professionalSpecialist2));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findByEDataUrlNotNull();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match. Expected: " + expectedResult.size() + " Result: " + result.size());
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
	public void testFindByFullName() throws Exception {
		
		String fName1 = "FirstName1";
		String fName2 = "FirstName3";
		
		String lName1 = "LastName1";
		String lName2 = "LastName3";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setFirstName(fName1);
		professionalSpecialist1.setLastName(lName1);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setFirstName(fName2);
		professionalSpecialist2.setLastName(lName2);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setFirstName(fName1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist1, professionalSpecialist3));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findByFullName(lName1, fName1);

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
	public void testFindByLastName() throws Exception {
		
		String fName1 = "FirstName1";
		String fName2 = "FirstName3";
		
		String lName1 = "LastName1";
		String lName2 = "LastName3";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setFirstName(fName1);
		professionalSpecialist1.setLastName(lName1);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setFirstName(fName2);
		professionalSpecialist2.setLastName(lName2);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setFirstName(fName1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist1, professionalSpecialist3));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findByLastName(lName1);

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
	public void testFindBySpecialty() throws Exception {
				
		String lName1 = "LastName1";
		String lName2 = "LastName2";
		String lName3 = "LastName3";
		
		String specialty1 = "alpha";
		String specialty2 = "bravo";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setSpecialtyType(specialty1);
		professionalSpecialist1.setLastName(lName2);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setSpecialtyType(specialty2);
		professionalSpecialist2.setLastName(lName3);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setSpecialtyType(specialty1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist3, professionalSpecialist1));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findBySpecialty(specialty1);

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
	public void testFindByReferralNo() throws Exception {
		
		String lName1 = "LastName1";
		String lName2 = "LastName2";
		String lName3 = "LastName3";
		
		String referralNo1 = "alpha";
		String referralNo2 = "bravo";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setReferralNo(referralNo1);
		professionalSpecialist1.setLastName(lName2);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setReferralNo(referralNo2);
		professionalSpecialist2.setLastName(lName3);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setReferralNo(referralNo1);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);

		List<ProfessionalSpecialist> expectedResult = new ArrayList<ProfessionalSpecialist>(Arrays.asList(professionalSpecialist3, professionalSpecialist1));
		List<ProfessionalSpecialist> result = professionalSpecialistDao.findByReferralNo(referralNo1);

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
	public void testGetByReferralNo() throws Exception {
		
		String lName1 = "LastName1";
		String lName2 = "LastName2";
		String lName3 = "LastName3";
		
		String referralNo1 = "alpha";
		String referralNo2 = "bravo";
		String referralNo3 = "charlie";
		
		ProfessionalSpecialist professionalSpecialist1 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist1);
		professionalSpecialist1.setReferralNo(referralNo1);
		professionalSpecialist1.setLastName(lName2);
		professionalSpecialist1.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist1);
		
		ProfessionalSpecialist professionalSpecialist2 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist2);
		professionalSpecialist2.setSpecialtyType(referralNo2);
		professionalSpecialist2.setLastName(lName3);
		professionalSpecialist2.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist2);
		
		ProfessionalSpecialist professionalSpecialist3 = new ProfessionalSpecialist();
		EntityDataGenerator.generateTestDataForModelClass(professionalSpecialist3);
		professionalSpecialist3.setSpecialtyType(referralNo3);
		professionalSpecialist3.setLastName(lName1);
		professionalSpecialist3.setHideFromView(false);
		professionalSpecialistDao.persist(professionalSpecialist3);
		
		ProfessionalSpecialist expectedResult = professionalSpecialist1;
		ProfessionalSpecialist result = professionalSpecialistDao.getByReferralNo(referralNo1);
		
		assertEquals(expectedResult, result);
	}
}