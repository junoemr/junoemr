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

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.LabPatientPhysicianInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LabPatientPhysicianInfoDaoTest extends DaoTestFixtures
{
	@Autowired
	protected LabPatientPhysicianInfoDao labPatientPhysicianInfoDao;

	public LabPatientPhysicianInfoDaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"labPatientPhysicianInfo", "patientLabRouting", "labPatientPhysicianInfo", "providerLabRouting","labReportInformation"
		};
	}

	@Test
	public void testCreate() throws Exception {
		LabPatientPhysicianInfo entity = new LabPatientPhysicianInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		labPatientPhysicianInfoDao.persist(entity);

		assertNotNull(entity.getId());
	}

	@Test
	public void testFindRoutings() {
		assertNotNull(labPatientPhysicianInfoDao.findRoutings(100, "T"));
	}

	@Test
	public void testFindByPatientName() {
		assertNotNull(labPatientPhysicianInfoDao.findByPatientName("ST", "LAB", "100", "LNAME", "FNAME", "HIN"));
	}

	@Test
	public void testFindByDemographic() {
		assertNotNull(labPatientPhysicianInfoDao.findByDemographic(199, "D"));
	}
}
