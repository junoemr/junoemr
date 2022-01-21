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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.AppointmentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppointmentTypeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected AppointmentTypeDao appointmentTypeDao;

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"appointmentType"
		};
	}

	@Test
	public void testCreate() throws Exception {
		AppointmentType entity = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		appointmentTypeDao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testListAll() throws Exception {
		AppointmentType appointmentType1 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType1);
		appointmentType1.setName("A");
		
		AppointmentType appointmentType2 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType2);
		appointmentType2.setName("C");
		
		AppointmentType appointmentType3 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType3);
		appointmentType3.setName("B");
		
		appointmentTypeDao.persist(appointmentType1);
		appointmentTypeDao.persist(appointmentType2);
		appointmentTypeDao.persist(appointmentType3);
		
		List<AppointmentType> result = appointmentTypeDao.listAll();
		List<AppointmentType> expectedResult = new ArrayList<AppointmentType>(Arrays.asList(
				appointmentType2, appointmentType3, appointmentType1 ));
		
		if (result.size() != expectedResult.size()) {
			fail("Array sizes do not match.");
		}

		assertTrue(true);
	}
	
	@Test
	public void testFindAppointmentTypeByName() throws Exception {
		String name = "A";
		
		AppointmentType appointmentType1 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType1);
		appointmentType1.setName("A");
		
		AppointmentType appointmentType2 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType2);
		appointmentType2.setName("C");
		
		AppointmentType appointmentType3 = new AppointmentType();
		EntityDataGenerator.generateTestDataForModelClass(appointmentType3);
		appointmentType3.setName("B");
		
		appointmentTypeDao.persist(appointmentType1);
		appointmentTypeDao.persist(appointmentType2);
		appointmentTypeDao.persist(appointmentType3);
		
		AppointmentType result = appointmentTypeDao.findByAppointmentTypeByName(name);
		AppointmentType expectedResult = appointmentType1;
		
		assertEquals(expectedResult, result);
	}
}
