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

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.schedule.dao.ScheduleTemplateCodeDao;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.oscarehr.util.SpringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ScheduleTemplateCodeDaoTest extends DaoTestFixtures {

	protected static ScheduleTemplateCodeDao dao = SpringUtils.getBean(ScheduleTemplateCodeDao.class);

	// TemplatesCodes are compared for equality on the code character only
	// MySql performs case insensitive matching unless specified using the BINARY keyword
	// Therefore need to test codes of the same letter, with different cases

	@AfterClass
	public static void cleanUp() throws Exception
	{
		SchemaUtils.restoreTable(false, "scheduletemplatecode");
	}

	@Before
	public void resetTable() throws Exception {
		SchemaUtils.restoreTable(false, "scheduletemplatecode");
	}

	@Test
    public void testCreate() {
		ScheduleTemplateCode template = makeScheduleTemplateCode('A');
        dao.persist(template);
        assertNotNull(template.getId());
	}

	@Test
	public void testFindAll() {
		ScheduleTemplateCode template1 = makeScheduleTemplateCode('A');
		dao.persist(template1);

		ScheduleTemplateCode template2 = makeScheduleTemplateCode('B');
		dao.persist(template2);

		ScheduleTemplateCode template3 = makeScheduleTemplateCode('C');
		dao.persist(template3);

		List<ScheduleTemplateCode> result = dao.findAll();
		assertEquals(3, result.size());
	}

	@Test
	public void testFindByCode()
	{
		Character searchCode = 'S';
		Character fillerCode = 'F';

		ScheduleTemplateCode template1 = makeScheduleTemplateCode(searchCode);
		dao.persist(template1);

		ScheduleTemplateCode template2 = makeScheduleTemplateCode(fillerCode);
		dao.persist(template2);

		ScheduleTemplateCode found = dao.findByCode(Character.toString(searchCode));
		assertEquals(searchCode,found.getCode());
	}

	@Test
	public void testGetByCode()
	{
		Character searchCode = 'S';
		Character fillerCode = 'F';

		ScheduleTemplateCode template1 = makeScheduleTemplateCode(searchCode);
		dao.persist(template1);

		ScheduleTemplateCode template2 = makeScheduleTemplateCode(fillerCode);
		dao.persist(template2);

		ScheduleTemplateCode found = dao.getByCode(searchCode);
		assertEquals(template1.getCode(), found.getCode());
	}


	@Test
	public void testNoOverwriteOnCreateCaseSensitive()
	{
		Character lowerCaseCode = 'a';
		Character upperCaseCode = 'A';

		ScheduleTemplateCode lowerCase = makeScheduleTemplateCode(lowerCaseCode);
		dao.persist(lowerCase);
		assertNotNull(lowerCase.getId());

		ScheduleTemplateCode upperCase = makeScheduleTemplateCode(upperCaseCode);
		dao.persist(upperCase);
		assertNotNull(upperCase.getCode());

		List<ScheduleTemplateCode> results = dao.findAll();
		assertEquals(2, results.size());
	}

	@Test
	public void testGetByCaseSensitive()
	{
		Character lowerCaseCode = 'a';
		Character upperCaseCode = 'A';

		ScheduleTemplateCode lowerCase = makeScheduleTemplateCode(lowerCaseCode);
		dao.persist(lowerCase);

		ScheduleTemplateCode upperCase = makeScheduleTemplateCode(upperCaseCode);
		dao.persist(upperCase);

		lowerCase = dao.getByCode(lowerCaseCode);
		assertNotNull(lowerCase);
		assertEquals("getBy(a) returned the wrong code", lowerCase.getCode(), lowerCaseCode);

		upperCase = dao.getByCode(upperCaseCode);
		assertNotNull(upperCase);
		assertEquals("getBy(A) returned returned the wrong code", upperCase.getCode(), upperCaseCode);
	}

	@Test
	public void testFindByCaseSensitive()
	{
		Character lowerCaseCode = 'a';
		Character upperCaseCode = 'A';

		ScheduleTemplateCode lowerCase = makeScheduleTemplateCode(lowerCaseCode);
		dao.persist(lowerCase);

		ScheduleTemplateCode upperCase = makeScheduleTemplateCode(upperCaseCode);
		dao.persist(upperCase);

		lowerCase = dao.findByCode(Character.toString(lowerCaseCode));
		assertNotNull(lowerCase);
		assertEquals("findBy(a) returned the wrong code", lowerCase.getCode(), lowerCaseCode);

		upperCase = dao.findByCode(Character.toString(upperCaseCode));
		assertNotNull(upperCase);
		assertEquals("findBy(A) returned the wrong code", upperCase.getCode(), upperCaseCode);
	}

	@Test
	public void testOverWriteCaseSensitive()
	{
		Character lowerCaseCode = 'a';
		Character upperCaseCode = 'A';
		String defaultDescription = "description";

		ScheduleTemplateCode lowerCase = makeScheduleTemplateCode(lowerCaseCode);
		lowerCase.setDescription(defaultDescription);
		dao.persist(lowerCase);

		ScheduleTemplateCode upperCase = makeScheduleTemplateCode(upperCaseCode);
		upperCase.setDescription(defaultDescription);
		dao.persist(upperCase);

		// Updating 'a' should not update 'A'
		String lowerCaseDescription = "lowerCaseDescription";

		lowerCase = dao.getByCode(lowerCaseCode);
		lowerCase.setDescription(lowerCaseDescription);
		dao.merge(lowerCase);

		lowerCase = dao.getByCode(lowerCaseCode);
		upperCase = dao.getByCode(upperCaseCode);

		assertEquals("a does not have expected description", lowerCaseDescription, lowerCase.getDescription());
		assertTrue("Updating a also updated A", !upperCase.getDescription().equals(lowerCaseDescription));

		// Updating 'A' should not overwrite 'a'.
		String upperCaseDescription = "UPPERCASEDESCRIPTION";

		upperCase = dao.getByCode(upperCaseCode);
		upperCase.setDescription(upperCaseDescription);
		dao.merge(upperCase);

		upperCase = dao.getByCode(upperCaseCode);
		lowerCase = dao.getByCode(lowerCaseCode);

		assertEquals("TemplateCode A does not have expected description", upperCaseDescription, upperCase.getDescription());
		assertTrue("Updating A also updated a", !lowerCase.getDescription().equals(upperCaseDescription));
	}

	private static ScheduleTemplateCode makeScheduleTemplateCode(Character code)
	{
		ScheduleTemplateCode toReturn = new ScheduleTemplateCode();
		toReturn.setCode(code);
		toReturn.setConfirm("N");

		return toReturn;
	}
}