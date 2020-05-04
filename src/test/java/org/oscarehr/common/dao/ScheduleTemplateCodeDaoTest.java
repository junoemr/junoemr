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
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.schedule.dao.ScheduleTemplateCodeDao;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.oscarehr.util.SpringUtils;

public class ScheduleTemplateCodeDaoTest extends DaoTestFixtures {

	protected static ScheduleTemplateCodeDao dao = SpringUtils.getBean(ScheduleTemplateCodeDao.class);

	// TemplatesCodes are compared for equality on the code character only
	// MySql performs case insensitive matching unless specified using the BINARY keyword
	// Therefore need to test codes of the same letter, with different cases
	private static char testCode = 'a';
	private static char testCodeUpperCase = 'A';

	private static ScheduleTemplateCode template1 = new ScheduleTemplateCode();
	private static ScheduleTemplateCode template2 = new ScheduleTemplateCode();
	private static ScheduleTemplateCode template3  = new ScheduleTemplateCode();

	private static ScheduleTemplateCode templateCaseSensitive = new ScheduleTemplateCode();

	@BeforeClass
	public static void setupTable() throws Exception {
		SchemaUtils.restoreTable(false, "scheduletemplatecode");

		EntityDataGenerator.generateTestDataForModelClass(template1);
		template1.setCode(testCode);
		dao.persist(template1);

		EntityDataGenerator.generateTestDataForModelClass(template2);
		template2.setCode('B');
		dao.persist(template2);

		EntityDataGenerator.generateTestDataForModelClass(template3);
		template3.setCode('c');

		EntityDataGenerator.generateTestDataForModelClass(templateCaseSensitive);
		templateCaseSensitive.setCode(testCodeUpperCase);
	}

	@AfterClass
	public static void restoreTable() throws Exception {
		SchemaUtils.restoreTable(false, "scheduletemplatecode");
	}

	@Test
    public void testCreate() throws Exception {
        dao.persist(template3);
        assertNotNull(template3.getId());
	}

	@Test
	public void testFindAll() throws Exception {

		List<ScheduleTemplateCode> result = dao.findAll();
		assertEquals(3, result.size());
	}

	@Test
	public void testFindByCode()
	{
		ScheduleTemplateCode found = dao.findByCode(Character.toString(testCode));
		assertEquals(template1.getCode(), found.getCode());
	}

	@Test
	public void testGetByCode()
	{
		ScheduleTemplateCode found = dao.getByCode(testCode);
		assertEquals(template1.getCode(), found.getCode());
	}


	@Test
	public void testNoOverwriteOnCreateCaseSensitive()
	{
		// creating code 'A' should not delete code 'a' due to binary matching
		dao.persist(templateCaseSensitive);
		assertNotNull(templateCaseSensitive.getId());

		List<ScheduleTemplateCode> results = dao.findAll();
		assertEquals(4, results.size());

		ScheduleTemplateCode littleAGet = dao.getByCode(testCode);
		ScheduleTemplateCode bigAGet = dao.getByCode(testCodeUpperCase);

		// Test getByCode for case sensitivity

		assertNotNull(littleAGet);
		assertEquals("getBy(a) yielded another code", (char) littleAGet.getCode(), testCode);

		assertNotNull(bigAGet);
		assertEquals("getBy(A) yielded another code", (char) bigAGet.getCode(), testCodeUpperCase);


		// Test findByCode for case sensitivity

		ScheduleTemplateCode littleAFind = dao.findByCode(Character.toString(testCode));
		ScheduleTemplateCode bigAFind = dao.findByCode(Character.toString(testCodeUpperCase));

		assertNotNull(littleAFind);
		assertEquals("findBy(a) yielded another code", (char) littleAFind.getCode(), testCode);

		assertNotNull(bigAFind);
		assertEquals("findBy(A) yielded another code", (char) bigAFind.getCode(), testCodeUpperCase);
	}

	@Test
	public void testNoOverWriteOnChangeCaseSensitive()
	{
		// Updating 'a' should not overwrite 'A'
		ScheduleTemplateCode littleA = dao.getByCode(testCode);

		String description = "Testing a";
		littleA.setDescription(description);

		dao.merge(littleA);
		ScheduleTemplateCode test1LittleA = dao.getByCode(testCode);
		ScheduleTemplateCode test1BigA = dao.getByCode(testCodeUpperCase);

		// Check both are still there
		assertNotNull("TemplateCode a not found", test1LittleA);
		assertNotNull("TemplateCode A not found", test1BigA);

		// Check that the description was applied only to 'a'
		assertEquals("TemplateCode a does not have expected description", description, test1LittleA.getDescription());
		assertTrue("TemplateCode A was overwritten", !test1LittleA.getDescription().equals(test1BigA.getDescription()));


		// Updating 'A' should not overwrite 'a'.
		// Testing the other way around helps detect any faults regardless of any (possibly lucky) DB ordering
		ScheduleTemplateCode bigA = dao.getByCode(testCodeUpperCase);

		String descriptionUpperCase = "Testing A";
		bigA.setDescription(descriptionUpperCase);

		dao.merge(bigA);
		ScheduleTemplateCode test2LittleA = dao.getByCode(testCode);
		ScheduleTemplateCode test2BigA = dao.getByCode(testCodeUpperCase);

		// Check both are still there
		assertNotNull("TemplateCode a not found", test2LittleA);
		assertNotNull("TemplateCode A not found", test2BigA);

		// Check that the description was applied only to 'A'
		assertEquals("TemplateCode A does not have expected description", descriptionUpperCase, test2BigA.getDescription());
		assertTrue("TemplateCode a was overwritten", !test2BigA.getDescription().equals(test2LittleA.getDescription()));
	}
}