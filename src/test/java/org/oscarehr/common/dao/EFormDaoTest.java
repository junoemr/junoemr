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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.eform.dao.EFormDao;
import org.oscarehr.eform.dao.EFormDao.EFormSortOrder;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.eform.model.EForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EFormDaoTest extends DaoTestFixtures
{

	protected Integer populatedFormId;

	@Autowired
	protected EFormDao eFormDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"eform","eform_groups"
		};
	}

	@Before
	public void createTestEform() throws Exception
	{
	    if(populatedFormId == null)
	    {
			EForm eform = new EForm();
			EntityDataGenerator.generateTestDataForModelClass(eform);
			eform.setFormName("NUVASHENAH");
			eFormDao.persist(eform);

			populatedFormId = eform.getId();
		}
	}

	@Test
	public void testFindByStatus()
	{
		List<EForm> eforms = eFormDao.findByStatus(true, EFormSortOrder.DATE);
		assertFalse(eforms.isEmpty());
		
		eforms = eFormDao.findByStatus(true, EFormSortOrder.FILE_NAME);
		assertFalse(eforms.isEmpty());
		
		eforms = eFormDao.findByStatus(true, EFormSortOrder.NAME);
		assertFalse(eforms.isEmpty());
		
		eforms = eFormDao.findByStatus(true, EFormSortOrder.SUBJECT);
		assertFalse(eforms.isEmpty());
		
		eforms = eFormDao.findByStatus(false);
		assertNotNull(eforms.isEmpty());
	}
	
	@Test
	public void testFindMaxIdForActiveForm()
	{
		Integer id = eFormDao.findMaxIdForActiveForm("NUVASHENAH");
		assertNotNull(id);
		assertTrue(id > 0);
	}
	
	@Test
	public void testCountFormsOtherThanSpecified()
	{
		Long count = eFormDao.countFormsOtherThanSpecified("NUVASHENAH", populatedFormId);
		assertNotNull(count);
		assertTrue(count >= 0);		
	}
	
}
