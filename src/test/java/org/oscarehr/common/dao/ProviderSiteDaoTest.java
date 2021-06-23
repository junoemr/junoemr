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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.ProviderSitePK;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderSiteDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ProviderSiteDao providerSiteDao;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("providersite", "site");
	}

	@Test
	public void testCreate() {
		ProviderSite entity = new ProviderSite();
		entity.setId(new ProviderSitePK());
		entity.getId().setProviderNo("000001");
		entity.getId().setSiteId(1);
		providerSiteDao.persist(entity);
		assertNotNull(entity.getId());
		assertNotNull(providerSiteDao.find(entity.getId()));
	}

	@Test
	public void testFindByProviderNo() throws Exception {
		
		String providerNo1 = "101";
		String providerNo2 = "202";
		
		ProviderSite providerSite1 = new ProviderSite();
		EntityDataGenerator.generateTestDataForModelClass(providerSite1);
		providerSite1.setId(new ProviderSitePK());
		providerSite1.getId().setProviderNo(providerNo1);
		providerSiteDao.persist(providerSite1);
		
		ProviderSite providerSite2 = new ProviderSite();
		EntityDataGenerator.generateTestDataForModelClass(providerSite2);
		providerSite2.setId(new ProviderSitePK());
		providerSite2.getId().setProviderNo(providerNo2);
		providerSiteDao.persist(providerSite2);
		
		List<ProviderSite> expectedResult = new ArrayList<ProviderSite>(Arrays.asList(providerSite1));
		List<ProviderSite> result = providerSiteDao.findByProviderNo(providerNo1);

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
    public void testFindActiveProvidersWithSites() {
	    assertNotNull(providerSiteDao.findActiveProvidersWithSites("100"));
    }
}
