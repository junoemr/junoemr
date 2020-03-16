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
package org.oscarehr.billing.CA.BC.dao;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.billing.CA.BC.model.Hl7Pid;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest
public class Hl7PidDaoTest extends DaoTestFixtures
{
	@Autowired
	public Hl7PidDao hl7PidDao;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("hl7_pid", "hl7_link", "hl7_msh", "hl7_obr", "hl7_obx","provider","hl7_message");
	}

	@Test
	public void testCreate() throws Exception {
		Hl7Pid entity = new Hl7Pid();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		hl7PidDao.persist(entity);
		assertNotNull(entity.getId());
	}

	@Test
	public void testFindByMessageId() {
		assertNotNull(hl7PidDao.findByMessageId(100));
	}

	@Test
	public void testFindPidsByStatus () {
		assertNotNull(hl7PidDao.findPidsByStatus("F"));
	}
	
	@Test
	public void testFindPidsAndMshByMessageId () {
		assertNotNull(hl7PidDao.findPidsAndMshByMessageId(100));
	}
		
}
