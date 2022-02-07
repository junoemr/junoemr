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

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DxresearchDAOTest extends DaoTestFixtures
{

	// TODO-legacy Make it protected when test ignores are merged in
	@Autowired
	private DxresearchDAO dxresearchDAO;

	public DxresearchDAOTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"dxresearch", "demographic","lst_gender","admission","demographic_merged",
			"program","health_safety","provider","providersite","site","program_team",
			"measurements", "measurementType", "measurementsExt", "quickList", "icd9","ichppccode",
			"billing", "billingdetail"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Dxresearch dr = new Dxresearch();
		EntityDataGenerator.generateTestDataForModelClass(dr);
		dxresearchDAO.persist(dr);
		assertNotNull(dr.getId());
	}

	@Test
	public void testFindByDemographicNoResearchCodeAndCodingSystem() {
		List<Dxresearch> list = dxresearchDAO.findByDemographicNoResearchCodeAndCodingSystem(1, "CODE", "SYS");
		assertNotNull(list);
	}
	
	@Test
	public void testGetDataForInrReport() {
		List<Object[]> list = dxresearchDAO.getDataForInrReport(new Date(), new Date());
		assertNotNull(list);
	}


	@Test
	public void testCountResearches() {
		assertNotNull(dxresearchDAO.countResearches("CDE", new Date(), new Date()));
	}

	@Test
	public void testCountBillingResearches() {
		assertNotNull(dxresearchDAO.countBillingResearches("CDE", "DIAG", "CREATOR", new Date(), new Date()));
	}
}
