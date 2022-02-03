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
/**
 * @author Shazib
 */
package org.oscarehr.common.dao;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjPrivilegePrimaryKey;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class SecObjPrivilegeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected SecObjPrivilegeDao secObjPrivilegeDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"secObjPrivilege", "secUserRole"
		};
	}

	@Test
	public void testFindByObjectNames() throws Exception {

		String objectName1 = "alphaName1";
		String objectName2 = "alphaName2";
		String objectName3 = "alphaName3";

		Integer roleId1 = 1;
		Integer roleId2 = 2;

		SecObjPrivilege secObjPrivilege1 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege1);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey1 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey1.setObjectName(objectName1);
		secObjPrivilegePrimaryKey1.setSecRoleId(roleId1);
		secObjPrivilege1.setId(secObjPrivilegePrimaryKey1);
		secObjPrivilegeDao.persist(secObjPrivilege1);

		SecObjPrivilege secObjPrivilege2 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege2);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey2 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey2.setObjectName(objectName2);
		secObjPrivilegePrimaryKey2.setSecRoleId(roleId1);
		secObjPrivilege2.setId(secObjPrivilegePrimaryKey2);
		secObjPrivilegeDao.persist(secObjPrivilege2);

		SecObjPrivilege secObjPrivilege3 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege3);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey3 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey3.setObjectName(objectName3);
		secObjPrivilegePrimaryKey3.setSecRoleId(roleId2);
		secObjPrivilege3.setId(secObjPrivilegePrimaryKey3);
		secObjPrivilegeDao.persist(secObjPrivilege3);

		Collection<String> objectNames = new ArrayList<String>(Arrays.asList(objectName1, objectName2));

		List<SecObjPrivilege> expectedResult = new ArrayList<SecObjPrivilege>(Arrays.asList(secObjPrivilege1, secObjPrivilege2));
		List<SecObjPrivilege> result = secObjPrivilegeDao.findByObjectNames(objectNames);

		Logger logger = MiscUtils.getLogger();

		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))) {
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}

	@Test
	public void testFindByRoleUserGroup() throws Exception {

		String objectName1 = "alphaName1";
		Integer roleId1 = 1;
		String objectName2 = "alphaName2";
		Integer roleId2 = 2;

		SecObjPrivilege secObjPrivilege1 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege1);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey1 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey1.setObjectName(objectName1);
		secObjPrivilegePrimaryKey1.setSecRoleId(roleId1);
		secObjPrivilege1.setId(secObjPrivilegePrimaryKey1);
		secObjPrivilegeDao.persist(secObjPrivilege1);

		SecObjPrivilege secObjPrivilege2 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege2);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey2 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey2.setObjectName(objectName2);
		secObjPrivilegePrimaryKey2.setSecRoleId(roleId2);
		secObjPrivilege2.setId(secObjPrivilegePrimaryKey2);
		secObjPrivilegeDao.persist(secObjPrivilege2);

		SecObjPrivilege secObjPrivilege3 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege3);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey3 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey3.setObjectName(objectName1);
		secObjPrivilegePrimaryKey3.setSecRoleId(roleId2);
		secObjPrivilege3.setId(secObjPrivilegePrimaryKey3);
		secObjPrivilegeDao.persist(secObjPrivilege3);

		SecObjPrivilege secObjPrivilege4 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege4);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey4 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey4.setObjectName(objectName2);
		secObjPrivilegePrimaryKey4.setSecRoleId(roleId1);
		secObjPrivilege4.setId(secObjPrivilegePrimaryKey4);
		secObjPrivilegeDao.persist(secObjPrivilege4);

		List<SecObjPrivilege> expectedResult = new ArrayList<SecObjPrivilege>(Arrays.asList(secObjPrivilege1, secObjPrivilege4));
		List<SecObjPrivilege> result = secObjPrivilegeDao.findByRoleId(roleId1);

		Logger logger = MiscUtils.getLogger();

		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))) {
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}

	@Test
	public void testFindByObjectName() throws Exception {

		String objectName1 = "alphaName1";
		Integer roleId1 = 1;
		String objectName2 = "alphaName2";
		Integer roleId2 = 2;

		SecObjPrivilege secObjPrivilege1 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege1);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey1 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey1.setObjectName(objectName1);
		secObjPrivilegePrimaryKey1.setSecRoleId(roleId1);
		secObjPrivilege1.setId(secObjPrivilegePrimaryKey1);
		secObjPrivilegeDao.persist(secObjPrivilege1);

		SecObjPrivilege secObjPrivilege2 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege2);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey2 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey2.setObjectName(objectName2);
		secObjPrivilegePrimaryKey2.setSecRoleId(roleId1);
		secObjPrivilege2.setId(secObjPrivilegePrimaryKey2);
		secObjPrivilegeDao.persist(secObjPrivilege2);

		SecObjPrivilege secObjPrivilege3 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege3);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey3 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey3.setObjectName(objectName1);
		secObjPrivilegePrimaryKey3.setSecRoleId(roleId2);
		secObjPrivilege3.setId(secObjPrivilegePrimaryKey3);
		secObjPrivilegeDao.persist(secObjPrivilege3);

		SecObjPrivilege secObjPrivilege4 = new SecObjPrivilege();
		EntityDataGenerator.generateTestDataForModelClass(secObjPrivilege4);
		SecObjPrivilegePrimaryKey secObjPrivilegePrimaryKey4 = new SecObjPrivilegePrimaryKey();
		secObjPrivilegePrimaryKey4.setObjectName(objectName2);
		secObjPrivilegePrimaryKey4.setSecRoleId(roleId2);
		secObjPrivilege4.setId(secObjPrivilegePrimaryKey4);
		secObjPrivilegeDao.persist(secObjPrivilege4);

		List<SecObjPrivilege> expectedResult = new ArrayList<SecObjPrivilege>(Arrays.asList(secObjPrivilege1, secObjPrivilege3));
		List<SecObjPrivilege> result = secObjPrivilegeDao.findByObjectName(objectName1);

		Logger logger = MiscUtils.getLogger();

		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))) {
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}

	@Test
	public void testCountObjectsByName() {
		secObjPrivilegeDao.countObjectsByName("OBJ NAME");
	}

	@Test
	public void testFindByFormNamePrivilegeAndProviderNo() {
		assertNotNull(secObjPrivilegeDao.findByFormNamePrivilegeAndProviderNo("frm", "priv", "prov"));
	}

}
