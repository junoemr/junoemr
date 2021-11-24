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
import org.oscarehr.billing.CA.ON.model.Billing3rdPartyAddress;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Billing3rdPartyAddressDaoTest extends DaoTestFixtures
{
	@Autowired
	protected Billing3rdPartyAddressDao billing3rdPartyAddressDao;

	public Billing3rdPartyAddressDaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"billing_on_3rdPartyAddress"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Billing3rdPartyAddress entity = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		billing3rdPartyAddressDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindByCompanyName() throws Exception {
		
		String companyName1 = "sigma";
		String companyName2 = "epsilon";
		
		Billing3rdPartyAddress billing3rdPartyAddress1 = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(billing3rdPartyAddress1);
		billing3rdPartyAddress1.setCompanyName(companyName1);
		billing3rdPartyAddressDao.persist(billing3rdPartyAddress1);
		
		Billing3rdPartyAddress billing3rdPartyAddress2 = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(billing3rdPartyAddress2);
		billing3rdPartyAddress2.setCompanyName(companyName2);
		billing3rdPartyAddressDao.persist(billing3rdPartyAddress2);
		
		Billing3rdPartyAddress billing3rdPartyAddress3 = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(billing3rdPartyAddress3);
		billing3rdPartyAddress3.setCompanyName(companyName2);
		billing3rdPartyAddressDao.persist(billing3rdPartyAddress3);
		
		Billing3rdPartyAddress billing3rdPartyAddress4 = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(billing3rdPartyAddress4);
		billing3rdPartyAddress4.setCompanyName(companyName1);
		billing3rdPartyAddressDao.persist(billing3rdPartyAddress4);
		
		Billing3rdPartyAddress billing3rdPartyAddress5 = new Billing3rdPartyAddress();
		EntityDataGenerator.generateTestDataForModelClass(billing3rdPartyAddress5);
		billing3rdPartyAddress5.setCompanyName(companyName1);
		billing3rdPartyAddressDao.persist(billing3rdPartyAddress5);
		
		List<Billing3rdPartyAddress> expectedResult = new ArrayList<Billing3rdPartyAddress>(Arrays.asList(billing3rdPartyAddress1, billing3rdPartyAddress4, billing3rdPartyAddress5));
		List<Billing3rdPartyAddress> result = billing3rdPartyAddressDao.findByCompanyName(companyName1);

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
    public void testFindAddresses() {
    	assertNotNull(billing3rdPartyAddressDao.findAddresses(null, null, null, null, null));
    }

	@Override
    protected List<String> getSimpleExceptionTestExcludes() {
		List<String> excludes = super.getSimpleExceptionTestExcludes();
		// this is very JSP specific method that includes a mix of SQL fields, we will test it manuall in #testFindAddress
		excludes.add("findAddresses");		
	    return excludes;
    }
}
