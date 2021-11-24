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
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PatientLabRoutingDaoTest extends DaoTestFixtures
{
	@Autowired
	protected PatientLabRoutingDao patientLabRoutingDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"patientLabRouting", "labTestResults", "labPatientPhysicianInfo", "mdsOBX", "mdsMSH", "hl7_msh",
			"hl7_pid", "hl7_obr", "hl7_obx", "hl7_orc", "consultdocs", "mdsZRG", "mdsMSH",
			"mdsPID","mdsPV1","mdsZFR","mdsOBR"
		};
	}

	@Test
	public void testFindDemographicByLabId() {
		patientLabRoutingDao.findDemographicByLabId(1);
	}

	@Test
	public void testFindDemographic() {
		patientLabRoutingDao.findDemographics("TYPE", 10);
	}

	@Test
	public void testFindUniqueTestNames() {
		assertNotNull(patientLabRoutingDao.findUniqueTestNames(100, "MDS"));
	}

	@Test
	public void testFindUniqueTestNamesForPatientExcelleris() {
		assertNotNull(patientLabRoutingDao.findUniqueTestNamesForPatientExcelleris(100, "MDS"));
	}

	@Test
	public void testFindByDemographicAndLabType() {
		assertNotNull(patientLabRoutingDao.findByDemographicAndLabType(100, "MDS"));
	}
	
	@Test
	public void testFindRoutingsAndTests() {
		assertNotNull(patientLabRoutingDao.findRoutingsAndTests(100, "MDS"));
		assertNotNull(patientLabRoutingDao.findRoutingsAndTests(100, "MDS", "TEST"));
	}
	
	@Test
	public void testFindHl7InfoForRoutingsAndTests() {
		assertNotNull(patientLabRoutingDao.findHl7InfoForRoutingsAndTests(100, "MDS", "TEST"));
	}

	@Test
	public void testFindRoutingsAndConsultDocsByRequestId() {
		assertNotNull(patientLabRoutingDao.findRoutingsAndConsultDocsByRequestId(100, "L"));
	}
	
}