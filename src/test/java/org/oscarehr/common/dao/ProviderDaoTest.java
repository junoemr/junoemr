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

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ProviderDao providerDao;

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("provider", "providersite", "demographic","provider_facility","Facility");
	}

	@Test
	public void testFindOhip() {
		List<Provider> providers = providerDao.getProvidersWithNonEmptyOhip();
		assertNotNull(providers);
	}
	
	@Test
	public void testGetCurrentTeamProviders() {
		providerDao.getCurrentTeamProviders("999998");
	}
	
	@Test
	public void testActiveProviders() {
		assertNotNull(providerDao.getActiveProviders());
	}
	
	@Test
	public void testGetActiveTeamsViaSites() {
		assertNotNull(providerDao.getActiveTeamsViaSites("100"));
	}
	
	@Test
	public void testGetProviderByPatientId() {
		assertNotNull(providerDao.getProviderByPatientId(100));
	}

	@Test
	public void test () {
		assertNotNull(providerDao.getProvidersByTypePattern("%nurse%"));
	}
	
	@Test
	public void testGetProviderIds () {
		assertNotNull(providerDao.getProviderIds(1));
	}
	
	@Test
	public void testGetFacilityIds () {
		assertNotNull(providerDao.getFacilityIds("999998"));
	}

    @Test
    public void testDetDoctorsWithNonEmptyCredentials() {
	    assertNotNull(providerDao.getDoctorsWithNonEmptyCredentials());
    }
}
