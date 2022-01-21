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

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.CustomFilter;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomFilterDaoTest extends DaoTestFixtures
{
	@Autowired
	protected CustomFilterDao customFilterDao;

	@Autowired
	private ProviderDao providerDao;
	
	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"custom_filter","custom_filter_providers","custom_filter_assignees","tickler", "tickler_update","tickler_comments","custom_filter","provider","demographic","program","lst_gender", "admission", "demographic_merged",
			"health_safety", "providersite", "site", "program_team","log", "Facility","program_queue"
		};
	}
	
	@Test
	public void testSave() throws Exception {
		 CustomFilter entity = new CustomFilter();
		 EntityDataGenerator.generateTestDataForModelClass(entity);
		 entity.setProviderNo("999998");
		 entity.setProgramId("10015");
		 
		
		 
		Provider p = providerDao.getProvider("999998");
		 entity.getAssignees().add(p);
		 
		 customFilterDao.persist(entity);
		 
		 CustomFilter cf = customFilterDao.find(entity.getId());
		 assertTrue(cf != null);
		 assertTrue(cf.getProgram() != null);
		 assertTrue(cf.getAssignees().size() == 1);
	}

}
