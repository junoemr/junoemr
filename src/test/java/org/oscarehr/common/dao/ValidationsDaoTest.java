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
import org.oscarehr.common.model.Validations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ValidationsDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ValidationsDao validationsDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"validations", "measurements", "provider"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Validations entity = new Validations();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		validationsDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindByAll() {
		assertNotNull(validationsDao.findByAll(null, null, null, null, null, null, null));
		assertNotNull(validationsDao.findByAll("RE", null, null, null, null, null, null));
		assertNotNull(validationsDao.findByAll(null, 2.0, null, null, null, null, null));
		assertNotNull(validationsDao.findByAll(null, null, 1.0, null, null, null, null));
		assertNotNull(validationsDao.findByAll(null, null, null, 100, null, null, null));
		assertNotNull(validationsDao.findByAll(null, null, null, null, 200, null, null));
		assertNotNull(validationsDao.findByAll(null, null, null, null, null, true, null));
		assertNotNull(validationsDao.findByAll(null, null, null, null, null, null, false));
		assertNotNull(validationsDao.findByAll("BR", 1.0, 2.0, 199, 0, false, false));
	}
	
	@Test
	public void testFindValidationsBy() {
		assertNotNull(validationsDao.findValidationsBy(10, "type", 10));
	}
	
	@Test
	public void testFindByName() {
		assertNotNull(validationsDao.findByName("NM"));
	}
}
