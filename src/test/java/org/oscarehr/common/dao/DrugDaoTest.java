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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.rx.dao.DrugDao;
import org.oscarehr.rx.model.Drug;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DrugDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DrugDao drugDao;

	@Override
	@Test
	public void doSimpleExceptionTest() {
		MiscUtils.getLogger().error("Unable to run doSimpleExceptionTest on this DAO");
	}
	
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"drugs", "prescription", "demographic_merged"
		};
	}

	@Test
	public void testFindDrugsAndPrescriptions() {
		// EntityDataGenerator.generateTestDataForModelClass(entity);

		List<Object[]> results = drugDao.findDrugsAndPrescriptions(99);
		assertNotNull(results);
		assertTrue(true);
		assertFalse(false);
	}

	@Test
	public void testAll() {
		List<Drug> drugs = null;
		drugs = drugDao.findByDemographicIdAndAtc(999, "");
		assertNotNull(drugs);

		drugs = drugDao.findByDemographicIdAndRegion(999, "");
		assertNotNull(drugs);

		drugs = drugDao.findByDemographicIdAndDrugId(999, 0);
		assertNotNull(drugs);

		drugDao.findByEverything(null, 0, null, null, null, null, 0, null, 0, 0, null, null, null, null, null, 0, null, false, false, null, null, null, false, false, false, false, null, null, null, false);
		drugDao.getMaxPosition(999);
	}

	@Test
	public void testFindByParameter() {
		List<Object[]> drugs = drugDao.findByParameter("BN", "1");
		assertNotNull(drugs);
	}

	@Test
	public void testFindByRegionBrandDemographicAndProvider() {
		List<Drug> drugs = drugDao.findByRegionBrandDemographicAndProvider("RI", "BN", 1, "1");
		assertNotNull(drugs);
	}

	@Test
	public void testFindByBrandNameDemographicAndProvider() {
		drugDao.findByBrandNameDemographicAndProvider("BN", 1, "1");
	}

	@Test
	public void testFindByCustomNameDemographicIdAndProviderNo() {
		drugDao.findByCustomNameDemographicIdAndProviderNo("BN", 1, "1");
	}

	@Test
	public void testFindId() {
		drugDao.findLastNotArchivedId("BN", "GN", 1);
	}

	@Test
	public void testFindByDemographicIdRegionalIdentifierAndAtcCode() {
		drugDao.findByDemographicIdRegionalIdentifierAndAtcCode("ATC", "RI", 1);
	}
	
	@Test
	public void testFindSpecialInstructions() {
		List<String> sis = drugDao.findSpecialInstructions();
		assertNotNull(sis);
	}
	
}
