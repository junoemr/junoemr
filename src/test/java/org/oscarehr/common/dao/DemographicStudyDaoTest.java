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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.DemographicStudy;
import org.oscarehr.common.model.DemographicStudyPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicStudyDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DemographicStudyDao demographicStudyDao;

	public DemographicStudyDaoTest() {
	}


	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("demographicstudy");
	}

	@Test
	public void testCreate() throws Exception {
		DemographicStudy entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(1);
		entity.getId().setStudyNo(1);
		demographicStudyDao.persist(entity);

	}

	@Test
	public void testFind() throws Exception {
		DemographicStudy entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(1);
		entity.getId().setStudyNo(1);
		demographicStudyDao.persist(entity);

		DemographicStudyPK pk = new DemographicStudyPK();
		pk.setDemographicNo(1);
		pk.setStudyNo(1);
		assertNotNull(demographicStudyDao.find(pk));
	}

	public void testRemoveByDemographicNo() throws Exception {
		DemographicStudy entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(1);
		entity.getId().setStudyNo(1);
		demographicStudyDao.persist(entity);

		entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(1);
		entity.getId().setStudyNo(2);
		demographicStudyDao.persist(entity);

		entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(2);
		entity.getId().setStudyNo(1);
		demographicStudyDao.persist(entity);

		assertEquals(2, demographicStudyDao.removeByDemographicNo(1));
	}

	@Test
	public void testFindByDemographicNoAndStudyNo() throws Exception {
		DemographicStudy entity = new DemographicStudy();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new DemographicStudyPK());
		entity.getId().setDemographicNo(1);
		entity.getId().setStudyNo(1);
		demographicStudyDao.persist(entity);

		assertNotNull(demographicStudyDao.findByDemographicNoAndStudyNo(1,1));
	}

}
