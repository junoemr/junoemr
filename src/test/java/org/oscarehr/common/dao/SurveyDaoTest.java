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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Survey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SurveyDaoTest extends DaoTestFixtures
{
	@Autowired
	protected SurveyDao surveyDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"survey"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Survey entity = new Survey();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		surveyDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindByName() throws Exception {
		
		String description1 = "alpha";
		String description2 = "bravo";
		String description3 = "charlie";
		
		Survey survey1 = new Survey();
		EntityDataGenerator.generateTestDataForModelClass(survey1);
		survey1.setDescription(description1);
		surveyDao.persist(survey1);
		
		Survey survey2 = new Survey();
		EntityDataGenerator.generateTestDataForModelClass(survey2);
		survey2.setDescription(description2);
		surveyDao.persist(survey2);
		
		Survey survey3 = new Survey();
		EntityDataGenerator.generateTestDataForModelClass(survey3);
		survey3.setDescription(description3);
		surveyDao.persist(survey3);
		
		Survey expectedReslut = survey2;
		Survey result = surveyDao.findByName(description2);
		
		assertEquals(expectedReslut, result);	
	}
}
