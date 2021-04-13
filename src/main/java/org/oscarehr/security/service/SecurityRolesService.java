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

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.security.dao.SecObjectNameDao;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjPrivilegePrimaryKey;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.ws.rest.transfer.security.SecurityObjectTransfer;
import org.oscarehr.ws.rest.transfer.security.SecurityRoleTransfer;
import org.oscarehr.ws.rest.transfer.security.UserSecurityRolesTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;
import oscar.log.LogConst;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.oscarehr.managers.SecurityInfoManager.ALL;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SecurityRolesService
{
	@Autowired
	private SecRoleDao secRoleDao;

	@Autowired
	private SecUserRoleDao secUserRoleDao;

	@Autowired
	private SecObjPrivilegeDao secObjPrivilegeDao;

	@Autowired
	private SecObjectNameDao secObjectNameDao;

	public UserSecurityRolesTransfer getUserSecurityRolesTransfer(String providerId)
	{
		UserSecurityRolesTransfer transfer = new UserSecurityRolesTransfer();
		List<SecurityRoleTransfer> roleTransfers = new ArrayList<>();
		Set<SecObjPrivilege> privilegeObjects = new HashSet<>(); // remove duplicates during insert
		for (SecUserRole secUserRole : secUserRoleDao.getUserRoles(providerId))
		{
			// queries here could be reduced, but in most cases 1-3 roles is expected
			SecRole role = secUserRole.getSecRole();
			roleTransfers.add(getRoleTransfer(role, false));
			privilegeObjects.addAll(role.getSecObjPrivilege());
		}
		transfer.setAccessObjects(getSecurityObjectsTransferAccessMap(new ArrayList<>(privilegeObjects)));
		transfer.setRoles(roleTransfers);
		return transfer;
	}

	public List<SecurityObjectTransfer> getAllSecurityObjectsTransfer()
	{
		List<SecurityObjectTransfer> objectTransfers = new ArrayList<>();

		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();
		for (SecObjectName.OBJECT_NAME objectName : SecObjectName.OBJECT_NAME.values())
		{
			objectTransfers.add(getSecurityObjectTransfer(objectName, nameEntityMap.get(objectName.getValue())));
		}
		return objectTransfers;
	}

	public List<SecurityRoleTransfer> getAllRoles()
	{
		List<SecRole> allRoles = secRoleDao.findAll();
		List<SecurityRoleTransfer> roleTransfers = new ArrayList<>(allRoles.size());

		for(SecRole secRole : allRoles)
		{
			roleTransfers.add(getRoleTransfer(secRole, false));
		}
		return roleTransfers;
	}

	public SecurityRoleTransfer getRole(Integer roleId)
	{
		return getRoleTransfer(secRoleDao.find(roleId), true);
	}

	public SecurityRoleTransfer addRole(String providerId, SecurityRoleTransfer newRoleTransfer)
	{
		if(newRoleTransfer.getId() != null)
		{
			throw new InvalidArgumentException("Id of new role must be null");
		}
		if(StringUtils.isBlank(newRoleTransfer.getName()) || secRoleDao.roleExistsWithName(newRoleTransfer.getName()))
		{
			throw new InvalidArgumentException("Role Name must be unique and non-empty");
		}
		SecRole secRole = convertSecRole(new SecRole(), newRoleTransfer);
		secRoleDao.persist(secRole);
		saveSecurityObjectsForRole(providerId, secRole, convertPrivileges(secRole, newRoleTransfer.getAccessObjects().values()));

		LogAction.addLogEntry(providerId, null, LogConst.ACTION_ADD, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				String.valueOf(secRole.getId()), null, "Role: " + secRole.getName());
		return getRoleTransfer(secRole, false);
	}

	public SecurityRoleTransfer updateRole(String providerId, Integer roleId, SecurityRoleTransfer updatedRoleTransfer)
	{
		if(roleId == null || !roleId.equals(updatedRoleTransfer.getId()))
		{
			throw new InvalidArgumentException("Id to update cannot be null and must match transfer ID");
		}
		SecRole secRole = convertSecRole(secRoleDao.find(roleId), updatedRoleTransfer);
		secRoleDao.merge(secRole);
		saveSecurityObjectsForRole(providerId, secRole, convertPrivileges(secRole, updatedRoleTransfer.getAccessObjects().values()));

		LogAction.addLogEntry(providerId, null, LogConst.ACTION_UPDATE, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				String.valueOf(secRole.getId()), null, "Role: " + secRole.getName());
		return getRoleTransfer(secRole, false);
	}

	public boolean deleteRole(String providerId, Integer roleId)
	{
		SecRole role = secRoleDao.find(roleId);

		// remove existing provider connections to this role
		for(SecUserRole secUserRole : role.getSecUserRoles())
		{
			secUserRoleDao.remove(secUserRole);
		}

		role.setDeletedBy(providerId);
		role.setDeletedAt(LocalDateTime.now());
		secRoleDao.merge(role);

		for(SecObjPrivilege privilege : role.getSecObjPrivilege())
		{
			privilege.setDeletedAt(LocalDateTime.now());
			secObjPrivilegeDao.merge(privilege);
		}
		return true;
	}

	/*
	 * ======================================= private methods =======================================
	 */

	private SecRole convertSecRole(SecRole secRole, SecurityRoleTransfer input)
	{
		secRole.setId(input.getId());
		secRole.setName(input.getName());
		secRole.setDescription(input.getDescription());
		return secRole;
	}

	private List<SecObjPrivilege> convertPrivileges(SecRole secRole, Collection<SecurityObjectTransfer> privilegeTransfers)
	{
		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();
		return privilegeTransfers.stream()
				.map((secObjTransfer) -> convertPrivilege(secRole, secObjTransfer, nameEntityMap))
				.filter(Objects::nonNull).collect(Collectors.toList());
	}

	private SecObjPrivilege convertPrivilege(SecRole secRole, SecurityObjectTransfer privilegeTransfer, Map<String, SecObjectName> nameEntityMap)
	{
		SecObjPrivilege secObjPrivilege = null;
		String privilegeLevel = getPrivilegeForModel(privilegeTransfer.getPrivileges());

		if(privilegeLevel != null)
		{
			secObjPrivilege = new SecObjPrivilege();
			secObjPrivilege.setId(new SecObjPrivilegePrimaryKey(secRole.getId(), privilegeTransfer.getName().getValue()));
			secObjPrivilege.setSecRole(secRole);
			secObjPrivilege.setRoleUserGroup(secRole.getName());
			secObjPrivilege.setPriority(0);
			secObjPrivilege.setPrivilege(privilegeLevel);
			secObjPrivilege.setSecObjectName(nameEntityMap.get(secObjPrivilege.getId().getObjectName()));
		}

		return secObjPrivilege;
	}

	private void saveSecurityObjectsForRole(String providerId, SecRole secRole, List<SecObjPrivilege> secObjPrivileges)
	{
		// delete all existing objects and re-add them
		// otherwise we need to do a diff on the objects added/removed/changed
		secObjPrivilegeDao.deleteByRole(secRole.getId());
		for (SecObjPrivilege secObjPrivilege : secObjPrivileges)
		{
			secObjPrivilege.setProviderNo(providerId);
			secObjPrivilegeDao.persist(secObjPrivilege);
		}
	}

	private SecurityRoleTransfer getRoleTransfer(
			SecRole secRole,
			boolean includePrivileges)
	{
		SecurityRoleTransfer transfer = new SecurityRoleTransfer();
		transfer.setId(secRole.getId());
		transfer.setName(secRole.getName());
		transfer.setDescription(secRole.getDescription());

		// privileges are not always needed and may have additional database hits as they are lazy loaded
		if(includePrivileges)
		{
			transfer.setAccessObjects(getSecurityObjectsTransferAccessMap(secRole.getSecObjPrivilege()));
		}
		return transfer;
	}

	private Map<SecObjectName.OBJECT_NAME, SecurityObjectTransfer> getSecurityObjectsTransferAccessMap(List<SecObjPrivilege> privileges)
	{
		Map<SecObjectName.OBJECT_NAME, SecurityObjectTransfer> accessObjects  = new HashMap<>();
		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();
		for (SecObjPrivilege privilege : privileges)
		{
			SecurityObjectTransfer securityObjectTransfer = getSecurityObjectTransfer(
					privilege, nameEntityMap.get(privilege.getId().getObjectName()));
			accessObjects.put(securityObjectTransfer.getName(), securityObjectTransfer);
		}
		return accessObjects;
	}

	private SecurityObjectTransfer getSecurityObjectTransfer(
			SecObjPrivilege secObjPrivilege,
			SecObjectName secObjectName)
	{
		String objectName = secObjPrivilege.getId().getObjectName();
		SecurityObjectTransfer transfer = getSecurityObjectTransfer(
				SecObjectName.OBJECT_NAME.fromValueString(objectName), secObjectName);
		transfer.setPrivileges(getPrivilegeLevels(secObjPrivilege.getPrivilege()));
		return transfer;
	}

	private SecurityObjectTransfer getSecurityObjectTransfer(
			SecObjectName.OBJECT_NAME objectName,
			SecObjectName secObjectName)
	{
		SecurityObjectTransfer transfer = new SecurityObjectTransfer();
		transfer.setName(objectName);
		transfer.setPrivileges(new ArrayList<>());

		if (secObjectName != null)
		{
			transfer.setDescription(secObjectName.getDescription());
		}
		return transfer;
	}

	private List<SecurityInfoManager.PRIVILEGE_LEVEL> getPrivilegeLevels(String privilege)
	{
		List<SecurityInfoManager.PRIVILEGE_LEVEL> privilegeList = new ArrayList<>(4);
		switch(privilege)
		{
			case SecurityInfoManager.ALL: privilegeList.add(SecurityInfoManager.PRIVILEGE_LEVEL.DELETE);
			case SecurityInfoManager.WRITE: privilegeList.add(SecurityInfoManager.PRIVILEGE_LEVEL.WRITE);
			case SecurityInfoManager.UPDATE: privilegeList.add(SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE);
			case SecurityInfoManager.READ: privilegeList.add(SecurityInfoManager.PRIVILEGE_LEVEL.READ); break;
			case SecurityInfoManager.DELETE: privilegeList.add(SecurityInfoManager.PRIVILEGE_LEVEL.DELETE); break;
		}
		return privilegeList;
	}

	private String getPrivilegeForModel(List<SecurityInfoManager.PRIVILEGE_LEVEL> privileges)
	{
		String privilegeLevel = null;
		if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.DELETE)
				&& privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.WRITE))
		{
			privilegeLevel = ALL;
		}
		else if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.WRITE))
		{
			privilegeLevel = SecurityInfoManager.PRIVILEGE_LEVEL.WRITE.asString();
		}
		else if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE))
		{
			privilegeLevel = SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE.asString();
		}
		else if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.READ))
		{
			privilegeLevel = SecurityInfoManager.PRIVILEGE_LEVEL.READ.asString();
		}
		return privilegeLevel;
	}

}
