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

import java.util.List;

import integration.tests.util.junoUtil.DatabaseUtil;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.CtlDocType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CtlDocTypeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected CtlDocTypeDao ctlDocTypeDao;

	private final String lower = "mydocuments";
	private final String upper = "MyDocuments";

	@Before
	public void setup() throws Exception
	{
		// ctl_doctype_maventest table comes initialized with 9 demographic doctypes and 8 provider doctypes
		SchemaUtils.restoreTable("ctl_doctype");
	}

	@AfterClass
	public static void restore() throws Exception
	{
		SchemaUtils.restoreTable("ctl_doctype");
	}

	@Test
	public void insert()
	{
		Integer id = ctlDocTypeDao.addDocType("test", CtlDocType.MODULE_PROVIDER);
		assertNotNull(id);
	}

	// Lookup should be case sensitive on docType
	@Test
	public void findCaseSensitive()
	{
		assertNotNull(ctlDocTypeDao.addDocType(lower, CtlDocType.MODULE_DEMOGRAPHIC));
		assertNotNull(ctlDocTypeDao.addDocType(upper, CtlDocType.MODULE_DEMOGRAPHIC));

		List<CtlDocType> lowerResult = ctlDocTypeDao.findByDocTypeAndModule(lower, CtlDocType.MODULE_DEMOGRAPHIC);
		List<CtlDocType> upperResult = ctlDocTypeDao.findByDocTypeAndModule(upper, CtlDocType.MODULE_DEMOGRAPHIC);

		assertEquals(1, lowerResult.size());
		assertEquals(1, upperResult.size());

		CtlDocType lowerType = lowerResult.get(0);
		CtlDocType upperType = upperResult.get(0);

		assertEquals(lower, lowerType.getDocType());
		assertEquals(upper, upperType.getDocType());
	}

	// Updating should be case sensitive on docType
	@Test
	public void changeDocTypeStatusCaseSensitive()
	{
		assertNotNull(ctlDocTypeDao.addDocType(lower, CtlDocType.MODULE_PROVIDER));
		assertNotNull(ctlDocTypeDao.addDocType(upper, CtlDocType.MODULE_PROVIDER));

		assertEquals((Integer) 1, ctlDocTypeDao.updateDocTypeStatus(lower, CtlDocType.MODULE_PROVIDER, CtlDocType.Status.Inactive.toString()));

		List<CtlDocType> lowerResult = ctlDocTypeDao.findByDocTypeAndModule(lower, CtlDocType.MODULE_PROVIDER);
		assertEquals(lowerResult.size(), 1);

		CtlDocType inactiveLower = lowerResult.get(0);
		assertEquals(CtlDocType.Status.Inactive.toString(), inactiveLower.getStatus());

		CtlDocType origUpper = ctlDocTypeDao.findByDocTypeAndModule(upper, CtlDocType.MODULE_PROVIDER).get(0);
		assertEquals(CtlDocType.Status.Active.toString(), origUpper.getStatus());
	}

	// Updating one doctype should not change the other.
	@Test
	public void changeDocTypeStatusDifferentModule()
	{
		assertNotNull(ctlDocTypeDao.addDocType(lower, CtlDocType.MODULE_DEMOGRAPHIC));
		assertEquals(new Integer(0), ctlDocTypeDao.updateDocTypeStatus(lower, CtlDocType.Status.Inactive.toString(), CtlDocType.MODULE_PROVIDER));
	}


	@Test
	public void findByStatusAndModule()
	{
		List<CtlDocType> result = ctlDocTypeDao.findByStatusAndModule(new String[]{
				CtlDocType.Status.Active.toString()
		}, CtlDocType.MODULE_DEMOGRAPHIC);

		assertNotNull(result);
		assertEquals(9, result.size());
	}

	@Test
	public void findByStatusAndModuleMultipleStatuses()
	{
		CtlDocType tmp = new CtlDocType();
		tmp.setModule(CtlDocType.MODULE_PROVIDER);
		tmp.setDocType("testDao1Test");
		tmp.setStatus(CtlDocType.Status.Inactive.toString());
		ctlDocTypeDao.persist(tmp);
		assertNotNull(tmp.getId());

		CtlDocType tmp2 = new CtlDocType();
		tmp2.setModule(CtlDocType.MODULE_PROVIDER);
		tmp2.setDocType("testDao2Test");
		tmp2.setStatus(CtlDocType.Status.Inactive.toString());
		ctlDocTypeDao.persist(tmp2);
		assertNotNull(tmp2.getId());

		int expectedProviderDocTypes = 8;

		List<CtlDocType> result = ctlDocTypeDao.findByStatusAndModule(new String[]{
				CtlDocType.Status.Active.toString()
				}, CtlDocType.MODULE_PROVIDER);

		assertNotNull(result);
		assertEquals(8, result.size());

		result = ctlDocTypeDao.findByStatusAndModule(new String[] {
				CtlDocType.Status.Active.toString(),
				CtlDocType.Status.Inactive.toString()
		        }, CtlDocType.MODULE_PROVIDER);

		assertNotNull(result);
		assertEquals(expectedProviderDocTypes + 2, result.size());
	}

	@Test
	public void findByDocTypeAndModule()
	{
		final String docTypeName = "testDao1Test";

		CtlDocType tmp = new CtlDocType();
		tmp.setModule(CtlDocType.MODULE_PROVIDER);
		tmp.setDocType(docTypeName);
		tmp.setStatus(CtlDocType.Status.Active.toString());
		ctlDocTypeDao.persist(tmp);
		assertNotNull(tmp.getId());

		List<CtlDocType> results = ctlDocTypeDao.findByDocTypeAndModule("testDao1Test", CtlDocType.MODULE_PROVIDER);
		assertEquals(results.size(),1);

		CtlDocType docType = results.get(0);

		assertEquals(docTypeName, docType.getDocType());
		assertEquals(CtlDocType.MODULE_PROVIDER, docType.getModule());
		assertEquals(CtlDocType.Status.Active.toString(), docType.getStatus());
	}

	@Test
	// Module is case insensitive
	public void findByModule()
	{
		List<CtlDocType> providerDocTypes = ctlDocTypeDao.findByModule(CtlDocType.MODULE_PROVIDER);
		assertEquals(8, providerDocTypes.size());

		List<CtlDocType> providerDocTypesCapitalized = ctlDocTypeDao.findByModule("Provider");
		assertEquals(8, providerDocTypesCapitalized.size());

		List<CtlDocType> demographicDocTypes = ctlDocTypeDao.findByModule(CtlDocType.MODULE_DEMOGRAPHIC);
		assertEquals(9, demographicDocTypes.size());
	}
}
