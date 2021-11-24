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
import org.oscarehr.common.model.PharmacyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PharmacyInfoDaoTest extends DaoTestFixtures
{
	@Autowired
	protected PharmacyInfoDao pharmacyInfoDao;

	public PharmacyInfoDaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"pharmacyInfo"
		};
	}

	@Test
	public void testCreate() throws Exception {
		PharmacyInfo entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testAddPharmacy() {
		int size = pharmacyInfoDao.getAllPharmacies().size();
		pharmacyInfoDao.addPharmacy("name","address","city","province","postalCode", "333-333-3333","444-444-4444", "555-555-5555",
				"a@b.com", "1", "notes");
		int newSize = pharmacyInfoDao.getAllPharmacies().size();
		assertEquals(size+1,newSize);
		pharmacyInfoDao.addPharmacy("name","address","city","province","postalCode", "333-333-3333","444-444-4444", "555-555-5555",
				"a@b.com", "1", "notes");
		newSize = pharmacyInfoDao.getAllPharmacies().size();
		assertEquals(size+2,newSize);
	}

	@Test
	public void testDeletePharmacy() throws Exception {
		PharmacyInfo entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());

		pharmacyInfoDao.deletePharmacy(entity.getId());

		Character status = PharmacyInfo.DELETED;
		assertEquals(status, pharmacyInfoDao.find(entity.getId()).getStatus());
	}

	@Test
	public void testGetPharmacy() throws Exception {
		PharmacyInfo entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());

		PharmacyInfo obj = pharmacyInfoDao.getPharmacy(entity.getId());

		assertEquals(obj.getId(),entity.getId());
	}

	@Test
	public void testGetPharmacyByRecordID() throws Exception {
		PharmacyInfo entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());

		PharmacyInfo obj = pharmacyInfoDao.getPharmacyByRecordID(entity.getId());

		assertEquals(obj.getId(),entity.getId());
	}

	@Test
	public void testGetAllPharmacies() throws Exception {
		int size = pharmacyInfoDao.getAllPharmacies().size();
		PharmacyInfo entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setStatus(PharmacyInfo.ACTIVE);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());
		entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setStatus(PharmacyInfo.ACTIVE);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());
		entity = new PharmacyInfo();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setStatus(PharmacyInfo.ACTIVE);
		pharmacyInfoDao.persist(entity);
		assertNotNull(entity.getId());

		assertEquals(pharmacyInfoDao.getAllPharmacies().size(),size+3);
	}
}
