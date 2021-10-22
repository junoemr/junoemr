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
package org.oscarehr.security;

import org.mockito.Mockito;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjPrivilegePrimaryKey;
import org.oscarehr.security.model.SecObjectName;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.oscarehr.managers.SecurityInfoManager.ALL;
import static org.oscarehr.managers.SecurityInfoManager.NO_RIGHTS;

public abstract class BaseSecurityTest
{
	protected SecObjPrivilege mockSecObjPrivilege(
			Integer roleId,
			String objectName,
			boolean read,
			boolean update,
			boolean create,
			boolean delete,
			String legacyPrivilege,
			boolean inclusive)
	{
		SecObjPrivilegePrimaryKey primaryKey = Mockito.mock(SecObjPrivilegePrimaryKey.class);
		Mockito.when(primaryKey.getObjectName()).thenReturn(objectName);
		Mockito.when(primaryKey.getSecRoleId()).thenReturn(roleId);

		SecObjPrivilege secObjPrivilege = Mockito.mock(SecObjPrivilege.class);
		Mockito.when(secObjPrivilege.getId()).thenReturn(primaryKey);
		Mockito.when(secObjPrivilege.isPermissionRead()).thenReturn(read);
		Mockito.when(secObjPrivilege.isPermissionUpdate()).thenReturn(update);
		Mockito.when(secObjPrivilege.isPermissionCreate()).thenReturn(create);
		Mockito.when(secObjPrivilege.isPermissionDelete()).thenReturn(delete);
		Mockito.when(secObjPrivilege.getPrivilege()).thenReturn(legacyPrivilege);
		Mockito.when(secObjPrivilege.isInclusive()).thenReturn(inclusive);

		return secObjPrivilege;
	}

	protected SecObjPrivilege mockSecObjPrivilege(
			Integer roleId,
			String objectName,
			boolean read,
			boolean update,
			boolean create,
			boolean delete,
			String legacyPrivilege)
	{
		return mockSecObjPrivilege(roleId, objectName, read, update, create, delete, legacyPrivilege, true);
	}

	protected SecObjPrivilege findByObjectName(List<SecObjPrivilege> secObjPrivileges, SecObjectName.OBJECT_NAME objectName)
	{
		return secObjPrivileges.stream()
				.filter(objPrivilege -> objPrivilege.getId().getObjectName().equals(objectName.getValue()))
				.findAny().orElse(null);
	}

	protected void assertCorrectPermissionLevels(boolean read, boolean update, boolean create, boolean delete, SecObjPrivilege actualObject)
	{
		assertEquals("invalid read state", read, actualObject.isPermissionRead());
		assertEquals("invalid update state", update, actualObject.isPermissionUpdate());
		assertEquals("invalid create state", create, actualObject.isPermissionCreate());
		assertEquals("invalid delete state", delete, actualObject.isPermissionDelete());

		String legacyPrivilegeLevel = actualObject.getPrivilege();
		String errorMessage = "invalid legacy privilege";
		if(actualObject.isInclusive())
		{
			if(create && update && read && delete)
			{
				assertEquals(errorMessage, ALL, legacyPrivilegeLevel);
			}
			else if(create)
			{
				assertEquals(errorMessage, SecurityInfoManager.PRIVILEGE_LEVEL.CREATE.asString(), legacyPrivilegeLevel);
			}
			else if (update)
			{
				assertEquals(errorMessage, SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE.asString(), legacyPrivilegeLevel);
			}
			else if (read)
			{
				assertEquals(errorMessage, SecurityInfoManager.PRIVILEGE_LEVEL.READ.asString(), legacyPrivilegeLevel);
			}
			else if (delete)
			{
				assertEquals(errorMessage, SecurityInfoManager.PRIVILEGE_LEVEL.DELETE.asString(), legacyPrivilegeLevel);
			}
		}
		else
		{
			assertEquals(errorMessage, NO_RIGHTS, legacyPrivilegeLevel);
		}
	}
}
