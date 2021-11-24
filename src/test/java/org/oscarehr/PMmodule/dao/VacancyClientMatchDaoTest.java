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
package org.oscarehr.PMmodule.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.oscarehr.PMmodule.model.VacancyClientMatch;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VacancyClientMatchDaoTest extends DaoTestFixtures
{
	@Autowired
	public VacancyClientMatchDao vacancyClientMatchDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"vacancy_client_match"
		};
	}

	@Test
	public void testCreate() throws Exception {
		VacancyClientMatch entity = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		vacancyClientMatchDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	@Ignore
	public void testFindByClientIdAndVacancyId() throws Exception {
		
		int clientId1 = 101, clientId2 = 202;
		int vacancyId1 = 111, vacancyId2 = 222;
		
		VacancyClientMatch vCM1 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM1);
		vCM1.setClient_id(clientId1);
		vCM1.setVacancy_id(vacancyId1);
		vacancyClientMatchDao.persist(vCM1);
		
		VacancyClientMatch vCM2 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM2);
		vCM2.setClient_id(clientId2);
		vCM2.setVacancy_id(vacancyId1);
		vacancyClientMatchDao.persist(vCM2);
		
		VacancyClientMatch vCM3 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM3);
		vCM3.setClient_id(clientId1);
		vCM3.setVacancy_id(vacancyId2);
		vacancyClientMatchDao.persist(vCM3);
		
		VacancyClientMatch vCM4 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM4);
		vCM4.setClient_id(clientId2);
		vCM4.setVacancy_id(vacancyId1);
		vacancyClientMatchDao.persist(vCM4);
		
		VacancyClientMatch vCM5 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM5);
		vCM5.setClient_id(clientId2);
		vCM5.setVacancy_id(vacancyId2);
		vacancyClientMatchDao.persist(vCM5);
		
		VacancyClientMatch vCM6 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM6);
		vCM6.setClient_id(clientId2);
		vCM6.setVacancy_id(vacancyId1);
		vacancyClientMatchDao.persist(vCM6);
		
		List<VacancyClientMatch> expectedResult = new ArrayList<VacancyClientMatch>(Arrays.asList(vCM2, vCM4, vCM6));
		List<VacancyClientMatch> result = vacancyClientMatchDao.findByClientIdAndVacancyId(clientId2, vacancyId1);
		
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
	public void testFindByClientId() throws Exception {
		
		int clientId1 = 101, clientId2 = 202;
		
		VacancyClientMatch vCM1 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM1);
		vCM1.setClient_id(clientId1);
		vacancyClientMatchDao.persist(vCM1);
		
		VacancyClientMatch vCM2 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM2);
		vCM2.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM2);
		
		VacancyClientMatch vCM3 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM3);
		vCM3.setClient_id(clientId1);
		vacancyClientMatchDao.persist(vCM3);
		
		VacancyClientMatch vCM4 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM4);
		vCM4.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM4);
		
		VacancyClientMatch vCM5 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM5);
		vCM5.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM5);
		
		List<VacancyClientMatch> expectedResult = new ArrayList<VacancyClientMatch>(Arrays.asList(vCM2, vCM4, vCM5));
		List<VacancyClientMatch> result = vacancyClientMatchDao.findByClientId(clientId2);
		
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
	public void testFindBystatus() throws Exception{
		
		int clientId1 = 101, clientId2 = 202;
		
		VacancyClientMatch vCM1 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM1);
		vCM1.setClient_id(clientId1);
		vacancyClientMatchDao.persist(vCM1);
		
		VacancyClientMatch vCM2 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM2);
		vCM2.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM2);
		
		VacancyClientMatch vCM3 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM3);
		vCM3.setClient_id(clientId1);
		vacancyClientMatchDao.persist(vCM3);
		
		VacancyClientMatch vCM4 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM4);
		vCM4.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM4);
		
		VacancyClientMatch vCM5 = new VacancyClientMatch();
		EntityDataGenerator.generateTestDataForModelClass(vCM5);
		vCM5.setClient_id(clientId2);
		vacancyClientMatchDao.persist(vCM5);
		
		List<VacancyClientMatch> expectedResult = new ArrayList<VacancyClientMatch>(Arrays.asList(vCM2, vCM4, vCM5));
		List<VacancyClientMatch> result = vacancyClientMatchDao.findByClientId(clientId2);
		
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
	public void testUpdateStatus() {
		VacancyClientMatch v = new VacancyClientMatch();
		v.setVacancy_id(1);
		v.setClient_id(1);
		v.setContactAttempts(0);
		v.setForm_id(1);
		v.setLast_contact_date(new java.util.Date());
		v.setMatchPercentage(0);
		v.setStatus(VacancyClientMatch.ACCEPTED);
		vacancyClientMatchDao.persist(v);
		
		vacancyClientMatchDao.updateStatus(VacancyClientMatch.REJECTED, 1, 1);
		
		assertEquals(1, vacancyClientMatchDao.findBystatus(VacancyClientMatch.REJECTED).size());
	}	
}
