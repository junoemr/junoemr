/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.security.model;

import org.junit.Test;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.BaseSecurityTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.oscarehr.managers.SecurityInfoManager.ALL;
import static org.oscarehr.managers.SecurityInfoManager.NO_RIGHTS;

public class SecRoleTest extends BaseSecurityTest
{
	@Test
	public void testGetPrivilegesWithInheritance_noParent()
	{
		SecObjPrivilege privilegeA = mockSecObjPrivilege(1, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL, true);
		SecObjPrivilege privilegeB = mockSecObjPrivilege(1, SecObjectName.OBJECT_NAME.APPOINTMENT.getValue(),
				true, true, true, false, SecurityInfoManager.PRIVILEGE_LEVEL.CREATE.asString(), true);

		SecRole role = buildSecRole(1, "testRole", Arrays.asList(privilegeA, privilegeB));

		// run the method we are testing
		List<SecObjPrivilege> secObjPrivileges = role.getPrivilegesWithInheritance();
		assertEquals("wrong number of permissions found", 2, secObjPrivileges.size());

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull(demoPriv);
		assertCorrectPermissionLevels(true, true, true, true, demoPriv);

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull(appointmentPriv);
		assertCorrectPermissionLevels(true, true, true, false, appointmentPriv);
	}

	@Test
	public void testGetPrivilegesWithInheritance_simpleExtension()
	{
		SecObjPrivilege privilegeA = mockSecObjPrivilege(1, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL, true);
		SecObjPrivilege privilegeB = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.APPOINTMENT.getValue(),
				true, true, true, false, SecurityInfoManager.PRIVILEGE_LEVEL.CREATE.asString(), true);

		SecRole parentRole = buildSecRole(1, "testParent", Arrays.asList(privilegeA));
		SecRole role = buildSecRole(2, "testRole", Arrays.asList(privilegeB));
		role.setParentSecRole(parentRole);

		// run the method we are testing
		List<SecObjPrivilege> secObjPrivileges = role.getPrivilegesWithInheritance();
		assertEquals("wrong number of permissions found", 2, secObjPrivileges.size());

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull(demoPriv);
		assertCorrectPermissionLevels(true, true, true, true, demoPriv);

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull(appointmentPriv);
		assertCorrectPermissionLevels(true, true, true, false, appointmentPriv);
	}

	@Test
	public void testGetPrivilegesWithInheritance_reducedPrivileges()
	{
		SecObjPrivilege privilegeA = mockSecObjPrivilege(1, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL, true);
		SecObjPrivilege privilegeB = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, false, false, false, SecurityInfoManager.PRIVILEGE_LEVEL.READ.asString(), true);

		SecRole parentRole = buildSecRole(1, "testParent", Arrays.asList(privilegeA));
		SecRole role = buildSecRole(2, "testRole", Arrays.asList(privilegeB));
		role.setParentSecRole(parentRole);

		// run the method we are testing
		List<SecObjPrivilege> secObjPrivileges = role.getPrivilegesWithInheritance();
		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull(demoPriv);
		assertCorrectPermissionLevels(true, false, false, false, demoPriv);
	}

	@Test
	public void testGetPrivilegesWithInheritance_removedPrivileges()
	{
		SecObjPrivilege privilegeA = mockSecObjPrivilege(1, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL, true);
		SecObjPrivilege privilegeB = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				false, false, false, false, NO_RIGHTS, false);
		SecObjPrivilege privilegeC = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.APPOINTMENT.getValue(),
				true, true, true, true, ALL, true);

		SecRole parentRole = buildSecRole(1, "testParent", Arrays.asList(privilegeA));
		SecRole role = buildSecRole(2, "testRole", Arrays.asList(privilegeB, privilegeC));
		role.setParentSecRole(parentRole);

		// run the method we are testing
		List<SecObjPrivilege> secObjPrivileges = role.getPrivilegesWithInheritance();
		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull(appointmentPriv);
		assertCorrectPermissionLevels(true, true, true, true, appointmentPriv);
	}

	private SecRole buildSecRole(Integer id, String name, List<SecObjPrivilege> privilegeList)
	{
		SecRole secRole = new SecRole();
		secRole.setId(id);
		secRole.setName(name);
		secRole.setSecObjPrivilege(privilegeList);
		return secRole;
	}
}
