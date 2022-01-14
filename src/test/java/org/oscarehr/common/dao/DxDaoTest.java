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
import org.oscarehr.common.model.DxAssociation;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DxDaoTest extends DaoTestFixtures
{
	// TODO-legacy Make it protected when test ignores are merged in
	@Autowired
	private DxDao dxDao;
	
	Logger logger = MiscUtils.getLogger();

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"dx_associations"
		};
	}

	@Test
	/**
	 * Ensures that the removeAssociations() method deletes 
	 * associations from the table.
	 * @throws Exception
	 */
	public void testRemoveAssociations() throws Exception {
		DxAssociation dx1 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx1);
		dx1.setDxCodeType("A");
		dx1.setDxCode("A");
		
		DxAssociation dx2 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx2);
		dx2.setDxCodeType("C");
		dx2.setDxCode("B");
		
		DxAssociation dx3 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx3);
		dx3.setDxCodeType("B");
		dx3.setDxCode("C");
		
		DxAssociation dx4 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx4);
		dx4.setDxCodeType("B");
		dx4.setDxCode("B");
		
		dxDao.persist(dx1);
		dxDao.persist(dx2);
		dxDao.persist(dx3);
		dxDao.persist(dx4);
		
		assertEquals(4, dxDao.removeAssociations());
	}

	@Test
	/**s
	 * Ensure that findAssociation() method return records where
	 * both the dx_code and dx_codetype match.
	 * @throws Exception
	 */
	public void testFindAssociation() throws Exception {
		String codeType = "A";
		String code = "A1";
		
		DxAssociation dx1 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx1);
		dx1.setDxCodeType(codeType);
		dx1.setDxCode(code);
		
		DxAssociation dx2 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx2);
		dx2.setDxCodeType(codeType);
		dx2.setDxCode("B");
		
		DxAssociation dx3 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx3);
		dx3.setDxCodeType("B");
		dx3.setDxCode(code);
		
		DxAssociation dx4 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx4);
		dx4.setDxCodeType(codeType);
		dx4.setDxCode(code);
		
		dxDao.persist(dx1);
		dxDao.persist(dx4);
		
		List<DxAssociation> result = dxDao.findAllAssociations();
		List<DxAssociation> expectedResult = new ArrayList<DxAssociation>(Arrays.asList(
				dx1, dx4
				));
		
		assertEquals(expectedResult.size(), result.size());
		assertTrue(result.containsAll(expectedResult));
	}
	
	@Test
	/**
	 * Ensures that the findAllAssociations() method returns all records
	 * ordered by the codetype and code.
	 * @throws Exception
	 */
	public void testFindAllAssociations() throws Exception {
		DxAssociation dx1 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx1);
		dx1.setDxCodeType("A");
		dx1.setDxCode("D");
		
		DxAssociation dx2 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx2);
		dx2.setDxCodeType("A");
		dx2.setDxCode("B");
		
		DxAssociation dx3 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx3);
		dx3.setDxCodeType("A");
		dx3.setDxCode("C");
		
		DxAssociation dx4 = new DxAssociation();
		EntityDataGenerator.generateTestDataForModelClass(dx4);
		dx4.setDxCodeType("A");
		dx4.setDxCode("A");
		
		dxDao.persist(dx1);
		dxDao.persist(dx2);
		dxDao.persist(dx3);
		dxDao.persist(dx4);

		List<DxAssociation> result = dxDao.findAllAssociations();
		List<DxAssociation> expectedResult = new ArrayList<DxAssociation>(Arrays.asList(
				dx4, dx2, dx3, dx1
				));

		// fail if list sizes aren't the same
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		
		// make sure each item in the list is as expected
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items not ordered by CodeType and Code.");
				fail("Items not sorted by CodeType and Code.");
			}
		}
		assertTrue(true);
	}
}
