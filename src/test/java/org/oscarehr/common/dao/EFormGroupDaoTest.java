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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.EFormGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EFormGroupDaoTest extends DaoTestFixtures
{
	@Autowired
	protected EFormGroupDao eFormGroupDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"eform_groups"
		};
	}

	@Test
	public void testDeleteByNameAndFormId() {
		EFormGroup negativeCheck = new EFormGroup();
		negativeCheck.setGroupName("WOTTERASS");
		negativeCheck.setFormId(99999);
		eFormGroupDao.persist(negativeCheck);
		
		populate();

		int count = eFormGroupDao.deleteByName("BLAHAMUHA");
		assertTrue(count == 1);

		populate();

		count = eFormGroupDao.deleteByNameAndFormId("BLAHAMUHA", 10);
		assertTrue(count == 1);
		
		negativeCheck = eFormGroupDao.find(negativeCheck.getId());
		assertNotNull(negativeCheck);
	}

	protected void populate() {
		EFormGroup group = new EFormGroup();
		group.setGroupName("BLAHAMUHA");
		group.setFormId(10);
		eFormGroupDao.persist(group);
	}

}
