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
import org.oscarehr.security.converter.PermissionToSecurityPermissionTransferConverter;
import org.oscarehr.security.converter.SecObjPrivilegeToPermissionConverter;
import org.oscarehr.security.converter.SecRoleToSecurityRoleTransferConverter;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjPrivilegePrimaryKey;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.ws.rest.transfer.security.SecurityPermissionTransfer;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
	private SecObjPrivilegeToPermissionConverter secObjPrivilegeToPermissionConverter;

	@Autowired
	private PermissionToSecurityPermissionTransferConverter permissionToSecurityPermissionTransferConverter;

	@Autowired
	private SecRoleToSecurityRoleTransferConverter secRoleToSecurityRoleTransferConverter;

	public UserSecurityRolesTransfer getUserSecurityRolesTransfer(String providerId)
	{
		UserSecurityRolesTransfer transfer = new UserSecurityRolesTransfer();
		List<SecUserRole> secUserRoles = secUserRoleDao.getUserRoles(providerId);
		List<SecRole> secRoles = secUserRoles.stream().map(SecUserRole::getSecRole).collect(Collectors.toList());

		transfer.setSecurityPermissions(permissionToSecurityPermissionTransferConverter.convert(getRolePermissions(secUserRoles)));
		transfer.setRoles(secRoleToSecurityRoleTransferConverter.convert(secRoles, false));
		return transfer;
	}

	public List<SecurityPermissionTransfer> getAllSecurityPermissionsTransfer()
	{
		return permissionToSecurityPermissionTransferConverter.convert(Arrays.asList(Permission.values()));
	}

	public List<Permission> getSecurityPermissions(SecRole secRole)
	{
		return secObjPrivilegeToPermissionConverter.convertToSingleList(secRole.getPrivilegesWithInheritance());
	}

	public List<Permission> getSecurityPermissionsForUser(String providerId)
	{
		return getRolePermissions(secUserRoleDao.getUserRoles(providerId));
	}

	@Deprecated // for legacy use only
	public List<SecObjPrivilege> getSecurityObjectsForUser(String providerId)
	{
		List<SecObjPrivilege> secObjPrivileges = new LinkedList<>();
		for(SecUserRole userRole : secUserRoleDao.getUserRoles(providerId))
		{
			secObjPrivileges.addAll(userRole.getSecRole().getPrivilegesWithInheritance());
		}
		return new ArrayList<>(secObjPrivileges);
	}

	public List<SecurityRoleTransfer> getAllRoles()
	{
		List<SecRole> allRoles = secRoleDao.findAll();
		return secRoleToSecurityRoleTransferConverter.convert(allRoles, false);
	}

	public SecurityRoleTransfer getRole(Integer roleId)
	{
		return secRoleToSecurityRoleTransferConverter.convert(secRoleDao.find(roleId), true);
	}

	public SecurityRoleTransfer addRole(String loggedInProviderId, SecurityRoleTransfer newRoleTransfer)
	{
		if(newRoleTransfer.getId() != null)
		{
			throw new InvalidArgumentException("Id of new role must be null");
		}
		if(StringUtils.isBlank(newRoleTransfer.getName()) || secRoleDao.roleExistsWithName(newRoleTransfer.getName()))
		{
			throw new InvalidArgumentException("Role Name must be unique and non-empty");
		}
		SecRole secRole = copyToSecRole(new SecRole(), newRoleTransfer);
		secRole.setSystemManaged(false); // can never add new system managed roles
		secRoleDao.persist(secRole);
		saveSecurityObjectsForRole(loggedInProviderId, secRole, newRoleTransfer.getSecurityPermissions());

		LogAction.addLogEntry(loggedInProviderId, null, LogConst.ACTION_ADD, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				String.valueOf(secRole.getId()), null, "Role: " + secRole.getName());
		return secRoleToSecurityRoleTransferConverter.convert(secRole, false);
	}

	public SecurityRoleTransfer updateRole(String loggedInProviderId, Integer roleId, SecurityRoleTransfer updatedRoleTransfer)
	{
		if(roleId == null || !roleId.equals(updatedRoleTransfer.getId()))
		{
			throw new InvalidArgumentException("Id to update cannot be null and must match transfer ID");
		}
		SecRole secRole = secRoleDao.find(roleId);
		if(secRole.isSystemManaged())
		{
			throw new RuntimeException("System managed roles cannot be modified");
		}
		secRole = copyToSecRole(secRoleDao.find(roleId), updatedRoleTransfer);
		secRoleDao.merge(secRole);
		saveSecurityObjectsForRole(loggedInProviderId, secRole, updatedRoleTransfer.getSecurityPermissions());

		LogAction.addLogEntry(loggedInProviderId, null, LogConst.ACTION_UPDATE, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				String.valueOf(secRole.getId()), null, "Role: " + secRole.getName());
		return secRoleToSecurityRoleTransferConverter.convert(secRole, false);
	}

	public boolean deleteRole(String loggedInProviderId, Integer roleId)
	{
		SecRole role = secRoleDao.find(roleId);
		if(role.isSystemManaged())
		{
			throw new RuntimeException("System managed roles cannot be deleted");
		}

		// remove existing provider connections to this role
		for(SecUserRole secUserRole : role.getSecUserRoles())
		{
			secUserRoleDao.remove(secUserRole);
		}

		role.setDeletedBy(loggedInProviderId);
		role.setDeletedAt(LocalDateTime.now());
		secRoleDao.merge(role);

		for(SecObjPrivilege privilege : role.getSecObjPrivilege())
		{
			privilege.setDeletedAt(LocalDateTime.now());
			secObjPrivilegeDao.merge(privilege);
		}

		LogAction.addLogEntry(loggedInProviderId, null, LogConst.ACTION_DELETE, LogConst.CON_SECURITY, LogConst.STATUS_SUCCESS,
				String.valueOf(role.getId()), null, "Role: " + role.getName());
		return true;
	}

	/*
	 * ======================================= private methods =======================================
	 */

	private SecRole copyToSecRole(SecRole secRole, SecurityRoleTransfer input)
	{
		secRole.setId(input.getId());
		secRole.setName(input.getName());
		secRole.setDescription(input.getDescription());
		secRole.setSystemManaged(input.isSystemManaged());
		if(input.getParentRoleId() != null)
		{
			secRole.setParentSecRole(secRoleDao.find(input.getParentRoleId()));
		}
		return secRole;
	}

	private void saveSecurityObjectsForRole(String providerId, SecRole secRole, List<SecurityPermissionTransfer> transfers)
	{
		Integer roleId = secRole.getId();
		List<Permission> permissions = transfers.stream().map(SecurityPermissionTransfer::getPermission).collect(Collectors.toList());
		List<SecObjPrivilege> currentlyAssignedPrivileges = secObjPrivilegeDao.findByRoleId(roleId);
//		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();

		// delete all the existing entries that are valid permissions
		for (SecObjPrivilege current : currentlyAssignedPrivileges)
		{
			if (Permission.includesObjectAsValue(SecObjectName.OBJECT_NAME.fromValueString(current.getId().getObjectName())))
			{
				secObjPrivilegeDao.remove(current);
			}
		}

		// group each security object with a list of permission levels (read/write/create/delete)
		Map<SecObjectName.OBJECT_NAME, List<SecurityInfoManager.PRIVILEGE_LEVEL>> privilegeMap = new HashMap<>();
		for(Permission permission : permissions)
		{
			SecObjectName.OBJECT_NAME objectName = permission.getObjectName();

			List<SecurityInfoManager.PRIVILEGE_LEVEL> privilegeLevels = privilegeMap.get(objectName);
			if(privilegeLevels == null)
			{
				privilegeLevels = new LinkedList<>();
				privilegeLevels.add(permission.getPrivilegeLevel());
				privilegeMap.put(objectName, privilegeLevels);
			}
			else
			{
				privilegeLevels.add(permission.getPrivilegeLevel());
			}
		}

		// build the new security objects
		Map<String, SecObjPrivilege> objectMap = new HashMap<>();
		for (Map.Entry<SecObjectName.OBJECT_NAME, List<SecurityInfoManager.PRIVILEGE_LEVEL>> entry : privilegeMap.entrySet())
		{
			SecObjectName.OBJECT_NAME objectName = entry.getKey();
			List<SecurityInfoManager.PRIVILEGE_LEVEL> privilegeLevels = entry.getValue();

			SecObjPrivilege secObjPrivilege = new SecObjPrivilege();
			secObjPrivilege.setId(new SecObjPrivilegePrimaryKey(roleId, objectName.getValue()));
			secObjPrivilege.setProviderNo(providerId);
			secObjPrivilege.setRoleUserGroup(secRole.getName());
			secObjPrivilege.setPriority(0);
			secObjPrivilege.setPrivilege(getLegacyPrivilege(privilegeLevels));
			secObjPrivilege.setPermissionRead(privilegeLevels.contains(SecurityInfoManager.PRIVILEGE_LEVEL.READ));
			secObjPrivilege.setPermissionUpdate(privilegeLevels.contains(SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE));
			secObjPrivilege.setPermissionCreate(privilegeLevels.contains(SecurityInfoManager.PRIVILEGE_LEVEL.CREATE));
			secObjPrivilege.setPermissionDelete(privilegeLevels.contains(SecurityInfoManager.PRIVILEGE_LEVEL.DELETE));
			secObjPrivilege.setSecRole(secRole);
			secObjPrivilege.setInclusive(true);
//			secObjPrivilege.setSecObjectName(nameEntityMap.get(secObjPrivilege.getId().getObjectName()));
			objectMap.put(objectName.getValue(), secObjPrivilege);
		}

		SecRole parentSecRole = secRole.getParentSecRole();
		if(parentSecRole != null)
		{
			// get parent permissions
			List<SecObjPrivilege> parentPrivileges = parentSecRole.getPrivilegesWithInheritance().stream()
					.filter((privilege) -> Permission.includesObjectAsValue(SecObjectName.OBJECT_NAME.fromValueString(privilege.getId().getObjectName())))
					.collect(Collectors.toList());

			for(SecObjPrivilege parentPrivilege : parentPrivileges)
			{
				String parentObjectName = parentPrivilege.getId().getObjectName();
				SecObjPrivilege childPrivilege = objectMap.get(parentObjectName);
				// if parent has permission that is missing from child list, add a copy of it and set included = false
				if(childPrivilege == null)
				{
					SecObjPrivilege parentCopy = new SecObjPrivilege(parentPrivilege);
					parentCopy.setInclusive(false);
					parentCopy.setProviderNo(providerId);
					parentCopy.setRoleUserGroup(secRole.getName());
					parentCopy.setId(new SecObjPrivilegePrimaryKey(roleId, parentObjectName));
					parentCopy.setSecRole(secRole);
//					parentCopy.setSecObjectName(nameEntityMap.get(parentObjectName));
					objectMap.put(parentObjectName, parentCopy);
				}
				// if permission and privileges identical to parent, remove them from the list
				else if ((childPrivilege.isPermissionRead() == parentPrivilege.isPermissionRead())
						&& (childPrivilege.isPermissionUpdate() == parentPrivilege.isPermissionUpdate())
						&& (childPrivilege.isPermissionCreate() == parentPrivilege.isPermissionCreate())
						&& (childPrivilege.isPermissionDelete() == parentPrivilege.isPermissionDelete()))
				{
					objectMap.remove(parentObjectName);
				}
			}
		}

		// save the remaining ones
		for(SecObjPrivilege secObjPrivilege : objectMap.values())
		{
			secObjPrivilegeDao.persist(secObjPrivilege);
		}
	}

	private List<Permission> getRolePermissions(List<SecUserRole> userRoles)
	{
		// use hash set to easily remove duplicates on add
		Set<Permission> permissionSet = new HashSet<>();
		for(SecUserRole userRole : userRoles)
		{
			permissionSet.addAll(secObjPrivilegeToPermissionConverter.convertToSingleList(userRole.getSecRole().getPrivilegesWithInheritance()));
		}
		return new ArrayList<>(permissionSet);
	}

	private String getLegacyPrivilege(List<SecurityInfoManager.PRIVILEGE_LEVEL> privileges)
	{
		String privilegeLevel = null;
		if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.DELETE)
				&& privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.CREATE))
		{
			privilegeLevel = ALL;
		}
		else if(privileges.contains(SecurityInfoManager.PRIVILEGE_LEVEL.CREATE))
		{
			privilegeLevel = SecurityInfoManager.PRIVILEGE_LEVEL.CREATE.asString();
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
