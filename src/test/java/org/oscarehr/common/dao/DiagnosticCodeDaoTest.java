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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.DiagnosticCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DiagnosticCodeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DiagnosticCodeDao diagnosticCodeDao;

	public DiagnosticCodeDaoTest() {
	}


	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("diagnosticcode","ctl_diagcode");
	}

	@Test
	public void testCreate() throws Exception {
		DiagnosticCode entity = new DiagnosticCode();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		diagnosticCodeDao.persist(entity);

		assertNotNull(entity.getId());
	}

	@Test
	public void testFindByDiagnosticCode() throws Exception {
		DiagnosticCode entity = new DiagnosticCode();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDiagnosticCode("a");
		diagnosticCodeDao.persist(entity);

		assertEquals(1, diagnosticCodeDao.findByDiagnosticCode(entity.getDiagnosticCode()).size());
	}

	@Test
	public void testFindByDiagnosticCodeAndRegion() throws Exception {
		DiagnosticCode entity = new DiagnosticCode();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDiagnosticCode("a");
		entity.setRegion("b");
		diagnosticCodeDao.persist(entity);

		assertEquals(1,
				diagnosticCodeDao.findByDiagnosticCodeAndRegion(entity.getDiagnosticCode(),entity.getRegion()).size());
	}
	
	@Test
	public void testFindByRegionAndType() {
		List<DiagnosticCode> codes = diagnosticCodeDao.findByRegionAndType("REG", "TYPE");
		assertNotNull(codes);
	}

    @Test
    public void testFindDiagnosictsAndCtlDiagCodesByServiceType() {
	    assertNotNull(diagnosticCodeDao.findDiagnosictsAndCtlDiagCodesByServiceType("TYPE"));
    }
}
