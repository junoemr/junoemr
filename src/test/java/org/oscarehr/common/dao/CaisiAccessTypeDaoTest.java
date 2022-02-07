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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.CaisiAccessType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaisiAccessTypeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected CaisiAccessTypeDao caisiAccessTypeDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"access_type"
		};
	}
	
	@Test
	public void testFindAll() throws Exception {
		CaisiAccessType caisiAccessType1 = new CaisiAccessType();
		EntityDataGenerator.generateTestDataForModelClass(caisiAccessType1);
		CaisiAccessType caisiAccessType2 = new CaisiAccessType();
		EntityDataGenerator.generateTestDataForModelClass(caisiAccessType2);
		CaisiAccessType caisiAccessType3 = new CaisiAccessType();
		EntityDataGenerator.generateTestDataForModelClass(caisiAccessType3);
		
		caisiAccessTypeDao.persist(caisiAccessType1);
		caisiAccessTypeDao.persist(caisiAccessType2);
		caisiAccessTypeDao.persist(caisiAccessType3);
		
		List<CaisiAccessType> result = caisiAccessTypeDao.findAll();
		List<CaisiAccessType> expectedResult = new ArrayList<CaisiAccessType>(Arrays.asList(
				caisiAccessType1,
				caisiAccessType2,
				caisiAccessType3
				));

		assertTrue(result.containsAll(expectedResult));
	}

}
