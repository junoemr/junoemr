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
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.dao.DocumentDao.Module;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.document.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DocumentDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DocumentDao documentDao;

	public DocumentDaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"document", "ctl_document", "consultdocs", "provider",
			"providersite", "demographic", "provider_facility", "Facility", "demographic_merged",
			"consultResponseDoc"
		};
	}

	@Test
	public void test() throws Exception {
		Document entity = new Document();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDocumentNo(null);
		documentDao.persist(entity);
		assertNotNull(entity.getId());

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		List<Document> results = documentDao.findByUpdateDate(cal.getTime(), 99);
		assertTrue(results.size() > 0);

		cal.add(Calendar.DAY_OF_YEAR, 2);
		results = documentDao.findByUpdateDate(cal.getTime(), 99);
		assertEquals(0, results.size());
	}

	@Test
	public void testFindConstultDocsDocsAndProvidersByModule() {
		List<Object[]> docs = documentDao.findConstultDocsDocsAndProvidersByModule(Module.DEMOGRAPHIC, 0);
		assertNotNull(docs);
	}
}
