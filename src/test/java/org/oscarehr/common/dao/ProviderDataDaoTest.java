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
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProviderDataDaoTest extends DaoTestFixtures
{
	@Autowired
	protected ProviderDataDao providerDataDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"provider","providersite","secUserRole"
		};
	}

	@Test
	public void testFindByTypeAndOhip() {
		List<ProviderData> data = providerDataDao.findByTypeAndOhip("doctor", "OHIP NO");
		assertNotNull(data);
	}
	
	@Test
	public void testFindByType() {
		List<ProviderData> data = providerDataDao.findByType("doctor");
		assertNotNull(data);
	}
	
	@Test
	public void testFindByName() {
		List<ProviderData> data = providerDataDao.findByName(null, null, false);
		assertNotNull(data);
		
		data = providerDataDao.findByName(null, null, true);
		assertNotNull(data);
		
		data = providerDataDao.findByName(null, "FIRST", true);
		assertNotNull(data);
		
		data = providerDataDao.findByName(null, "FIRST", false);
		assertNotNull(data);
		
		data = providerDataDao.findByName("LAST", null, false);
		assertNotNull(data);
		
		data = providerDataDao.findByName("LAST", null, true);
		assertNotNull(data);
		
		data = providerDataDao.findByName("LAST", "FIRST", true);
		assertNotNull(data);
		
		data = providerDataDao.findByName("LAST", "FIRST", false);
		assertNotNull(data);
	}
	
	@Test
	public void testFindAll() {
		List<ProviderData> data = providerDataDao.findAll(true);
		assertNotNull(data);
		
		data = providerDataDao.findAll(false);
		assertNotNull(data);
	}
	
	@Test
	public void testGetLastId() {
		ProviderData pd = newProvider("-1001");
		providerDataDao.persist(pd);
		
		pd = newProvider("-2");
		providerDataDao.persist(pd);
		
		pd = newProvider("1");
		providerDataDao.persist(pd);
		
		Integer id = providerDataDao.getLastId();
		assertEquals(new Integer(-1001), id);
	}

	protected ProviderData newProvider(String id) {
	    ProviderData result = new ProviderData();
	    result.set(id);
	    try {
	        EntityDataGenerator.generateTestDataForModelClass(result);
        } catch (Exception e) {
        	fail();
        }	    
	    return result;
    }
}
