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

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.billing.CA.BC.model.Hl7Link;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Hl7LinkDaoTest extends DaoTestFixtures
{
	@Autowired
	public Hl7LinkDao hl7LinkDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"hl7_link", "hl7_pid", "hl7_link", "hl7_obr", "demographic", "lst_gender", "admission","demographic_merged",
			"hl7_message", "program", "health_safety","provider","providersite","site","program_team"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Hl7Link entity = new Hl7Link();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(1);
		hl7LinkDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindLabs() {
		assertNotNull(hl7LinkDao.findLabs());
	}
	
	@Test
	public void testFindMagicLinks() {
		assertNotNull(hl7LinkDao.findMagicLinks());
	}
	
    @Test
    public void testFindLinksAndRequestDates() {
	    assertNotNull(hl7LinkDao.findLinksAndRequestDates(100));
    }

    @Test
    public void testFindReports() {
	    assertNotNull(hl7LinkDao.findReports(new Date(), new Date(), "-ULL", "patient_name", "CMD"));
	    assertNotNull(hl7LinkDao.findReports(new Date(), new Date(), "-APL", "patient_name", "CMD"));
	    assertNotNull(hl7LinkDao.findReports(new Date(), new Date(), "-UAP", "patient_name", "CMD"));
    }
    
	@Override
    protected List<String> getSimpleExceptionTestExcludes() {
		List<String> result = super.getSimpleExceptionTestExcludes(); 
		result.add("findReports");
	    return result;
    }
    
}
