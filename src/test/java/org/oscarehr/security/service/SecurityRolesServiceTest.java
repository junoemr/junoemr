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
package org.oscarehr.security.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.security.BaseSecurityTest;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.ws.rest.transfer.security.SecurityPermissionTransfer;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.oscarehr.managers.SecurityInfoManager.ALL;
import static org.oscarehr.managers.SecurityInfoManager.PRIVILEGE_LEVEL;

public class SecurityRolesServiceTest extends BaseSecurityTest
{
	@Autowired
	@InjectMocks
	private SecurityRolesService securityRolesService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testGetPrivilegesForRole_objectProperties()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		String expectedKey = Permission.APPOINTMENT_READ.getObjectName().getValue();
		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege actualObject = secObjPrivileges.get(0);
		assertEquals("secRole not assigned correctly", mockSecRole, actualObject.getSecRole());
		assertEquals("provider id not assigned correctly", providerId, actualObject.getProviderNo());
		assertTrue("incorrect inclusive state", actualObject.isInclusive());
		assertNotNull("id cannot be null", actualObject.getId());
		assertEquals("privilege role id must match secRoleId", mockSecRole.getId(), actualObject.getId().getSecRoleId());
		assertEquals("privilege name must match expectedKey", expectedKey, actualObject.getId().getObjectName());
		assertCorrectPermissionLevels(true, false, false, false, actualObject);
	}

	@Test
	public void testGetPrivilegesForRole_permissions_updateOnly()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_UPDATE
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege actualObject = secObjPrivileges.get(0);
		assertTrue("incorrect inclusive state", actualObject.isInclusive());
		assertCorrectPermissionLevels(false, true, false, false, actualObject);
	}

	@Test
	public void testGetPrivilegesForRole_permissions_createOnly()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_CREATE
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege actualObject = secObjPrivileges.get(0);
		assertTrue("incorrect inclusive state", actualObject.isInclusive());
		assertCorrectPermissionLevels(false, false, true, false, actualObject);
	}

	@Test
	public void testGetPrivilegesForRole_permissions_deleteOnly()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_DELETE
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege actualObject = secObjPrivileges.get(0);
		assertTrue("incorrect inclusive state", actualObject.isInclusive());
		assertCorrectPermissionLevels(false, false, false, true, actualObject);
	}

	@Test
	public void testGetPrivilegesForRole_permissions_full()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ,
				Permission.APPOINTMENT_UPDATE,
				Permission.APPOINTMENT_CREATE,
				Permission.APPOINTMENT_DELETE
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege actualObject = secObjPrivileges.get(0);
		assertTrue("incorrect inclusive state", actualObject.isInclusive());
		assertCorrectPermissionLevels(true, true, true, true, actualObject);
	}

	@Test
	public void testGetPrivilegesForRole_permissions_muiltiObject()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ,
				Permission.APPOINTMENT_UPDATE,
				Permission.DEMOGRAPHIC_READ,
				Permission.DEMOGRAPHIC_UPDATE,
				Permission.DEMOGRAPHIC_CREATE
		);
		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		assertEquals("wrong number of permissions found", 2, secObjPrivileges.size());

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull("missing appointment privilege object", appointmentPriv);
		assertTrue("incorrect inclusive state", appointmentPriv.isInclusive());
		assertCorrectPermissionLevels(true, true, false, false, appointmentPriv);

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull("missing demographic privilege object", demoPriv);
		assertTrue("incorrect inclusive state", demoPriv.isInclusive());
		assertCorrectPermissionLevels(true, true, true, false, demoPriv);
	}

	@Test
	public void testGetPrivilegesForRole_parentInheritance_simple()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		SecRole parentSecRole = mockSecRole(2, "parent");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ,
				Permission.DEMOGRAPHIC_READ
		);
		SecObjPrivilege parentPermission = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, false, false, false, PRIVILEGE_LEVEL.READ.asString());
		Mockito.when(parentSecRole.getPrivilegesWithInheritance()).thenReturn(Arrays.asList(parentPermission));
		Mockito.when(mockSecRole.getParentSecRole()).thenReturn(parentSecRole);

		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		// should only have the appointment privilege, since the parent has the identical demographic privilege
		assertEquals("wrong number of permissions found", 1, secObjPrivileges.size());

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull("missing appointment privilege object", appointmentPriv);
		assertTrue("incorrect inclusive state", appointmentPriv.isInclusive());
		assertCorrectPermissionLevels(true, false, false, false, appointmentPriv);
	}

	@Test
	public void testGetPrivilegesForRole_parentInheritance_differentPrivilegeLevels()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		SecRole parentSecRole = mockSecRole(2, "parent");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ,
				Permission.DEMOGRAPHIC_READ
		);
		SecObjPrivilege parentPermission = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL);
		Mockito.when(parentSecRole.getPrivilegesWithInheritance()).thenReturn(Arrays.asList(parentPermission));
		Mockito.when(mockSecRole.getParentSecRole()).thenReturn(parentSecRole);

		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		// should have both the appointment and demographic privileges, since the parent demographic privilege has more rights
		assertEquals("wrong number of permissions found", 2, secObjPrivileges.size());

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull("missing appointment privilege object", appointmentPriv);
		assertTrue("incorrect inclusive state", appointmentPriv.isInclusive());
		assertCorrectPermissionLevels(true, false, false, false, appointmentPriv);

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull("missing demographic privilege object", demoPriv);
		assertTrue("incorrect inclusive state", demoPriv.isInclusive());
		assertCorrectPermissionLevels(true, false, false, false, demoPriv);
	}

	@Test
	public void testGetPrivilegesForRole_parentInheritance_removedPrivilege()
	{
		String providerId = "9001";
		SecRole mockSecRole = mockSecRole(1, "test");
		SecRole parentSecRole = mockSecRole(2, "parent");
		List<Permission> permissions = Arrays.asList(
				Permission.APPOINTMENT_READ
		);
		SecObjPrivilege parentPermission = mockSecObjPrivilege(2, SecObjectName.OBJECT_NAME.DEMOGRAPHIC.getValue(),
				true, true, true, true, ALL);

		Mockito.when(parentSecRole.getPrivilegesWithInheritance()).thenReturn(Arrays.asList(parentPermission));
		Mockito.when(mockSecRole.getParentSecRole()).thenReturn(parentSecRole);

		List<SecObjPrivilege> secObjPrivileges = securityRolesService.getPrivilegesForRole(providerId, mockSecRole, toTransfers(permissions));

		// should have both the appointment and demographic privileges, but the demographic should not be inclusive
		assertEquals("wrong number of permissions found", 2, secObjPrivileges.size());

		SecObjPrivilege appointmentPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.APPOINTMENT);
		assertNotNull("missing appointment privilege object", appointmentPriv);
		assertTrue("incorrect inclusive state", appointmentPriv.isInclusive());
		assertCorrectPermissionLevels(true, false, false, false, appointmentPriv);

		SecObjPrivilege demoPriv = findByObjectName(secObjPrivileges, SecObjectName.OBJECT_NAME.DEMOGRAPHIC);
		assertNotNull("missing demographic privilege object", demoPriv);
		assertFalse("incorrect inclusive state", demoPriv.isInclusive());
		assertCorrectPermissionLevels(false, false, false, false, demoPriv);
	}

	private SecRole mockSecRole(Integer id, String name)
	{
		SecRole secRole = Mockito.mock(SecRole.class);
		Mockito.when(secRole.getId()).thenReturn(id);
		Mockito.when(secRole.getName()).thenReturn(name);
		return secRole;
	}

	private List<SecurityPermissionTransfer> toTransfers(List<Permission> permissions)
	{
		return permissions.stream().map(this::toTransfer).collect(Collectors.toList());
	}

	private SecurityPermissionTransfer toTransfer(Permission permission)
	{
		SecurityPermissionTransfer transfer = new SecurityPermissionTransfer();
		transfer.setPermission(permission);
		return transfer;
	}

}
