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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.appointment.dao.AppointmentStatusDao;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppointmentStatusDaoTest extends DaoTestFixtures
{
	@Autowired
	protected AppointmentStatusDao appointmentStatusDao;

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"appointment_status","appointment"
		};
	}

	@Test
	public void testCreate() throws Exception {
		AppointmentStatus entity = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setJunoColor("#000000");
		appointmentStatusDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindAll() throws Exception {
		
		AppointmentStatus apptStatus1 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus1);
		apptStatus1.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus1);
		
		AppointmentStatus apptStatus2 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus2);
		apptStatus2.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus2);
		
		AppointmentStatus apptStatus3 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus3);
		apptStatus3.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus3);
		
		AppointmentStatus apptStatus4 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus4);
		apptStatus4.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus4);
		
		List<AppointmentStatus> expectedResult = new ArrayList<AppointmentStatus>(Arrays.asList(apptStatus1, apptStatus2, apptStatus3, apptStatus4));
		List<AppointmentStatus> result = appointmentStatusDao.findAll();

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
	public void testFindByStatus() throws Exception {
		
		String status1 = "alpha", status2 = "bravo", status3 = "charlie";
		
		AppointmentStatus apptStatus1 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus1);
		apptStatus1.setStatus(status1);
		apptStatus1.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus1);
		
		AppointmentStatus apptStatus2 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus2);
		apptStatus2.setStatus(status2);
		apptStatus2.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus2);
		
		AppointmentStatus apptStatus3 = new AppointmentStatus();
		EntityDataGenerator.generateTestDataForModelClass(apptStatus3);
		apptStatus3.setStatus(status3);
		apptStatus3.setJunoColor("#000000");
		appointmentStatusDao.persist(apptStatus3);
		
		AppointmentStatus expectedResult = apptStatus2;
		AppointmentStatus result = appointmentStatusDao.findByStatus(status2);
		
		assertEquals(expectedResult, result);
	}
}