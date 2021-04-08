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

import com.quatro.model.security.Secuserrole;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.security.dao.SecObjectNameDao;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.ws.rest.transfer.security.SecurityObjectTransfer;
import org.oscarehr.ws.rest.transfer.security.SecurityRoleTransfer;
import org.oscarehr.ws.rest.transfer.security.UserSecurityRolesTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SecurityRolesService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private SecRoleDao secRoleDao;

	@Autowired
	private SecObjPrivilegeDao secObjPrivilegeDao;

	@Autowired
	private SecObjectNameDao secObjectNameDao;

	public List<SecurityRoleTransfer> getAllRoles()
	{
		List<SecRole> allRoles = secRoleDao.findAll();
		List<SecurityRoleTransfer> roleTransfers = new ArrayList<>(allRoles.size());

		for(SecRole secRole : allRoles)
		{
			roleTransfers.add(getRoleTransfer(secRole, false, false));
		}
		return roleTransfers;
	}

	public SecurityRoleTransfer getRoleTransfer(Integer roleId)
	{
		return getRoleTransfer(secRoleDao.find(roleId), true, false);
	}

	public UserSecurityRolesTransfer getUserSecurityRolesTransfer(String providerNo)
	{
		UserSecurityRolesTransfer transfer = new UserSecurityRolesTransfer();
		List<String> roleNames = new ArrayList<>();
		List<SecObjPrivilege> privilegeObjects = new LinkedList<>();
		for(Secuserrole role : securityInfoManager.getRoles(providerNo))
		{
			roleNames.add(role.getRoleName());
			privilegeObjects.addAll(secObjPrivilegeDao.findByRoleId(role.getId()));
			// queries here could be reduced, but are expected to be 1-3 in most cases
		}

		for(SecObjPrivilege secObjPrivilege : privilegeObjects)
		{
			SecurityObjectTransfer securityObjectTransfer = getSecurityObjectTransfer(secObjPrivilege, false);
			transfer.addAccess(securityObjectTransfer.getName(), securityObjectTransfer);
		}

		transfer.setRoles(roleNames);
		return transfer;
	}

	public List<SecurityObjectTransfer> getAllSecurityObjectsTransfer()
	{
		//this results in a lot of database hits. 1 per enum value - could be improved
		List<SecurityObjectTransfer> objectTransfers = new ArrayList<>();
		for (SecObjectName.OBJECT_NAME secObjPrivilege : SecObjectName.OBJECT_NAME.values())
		{
			objectTransfers.add(getSecurityObjectTransfer(secObjPrivilege, true));
		}
		return objectTransfers;
	}

	/*
	 * ======================================= private methods =======================================
	 */

	private SecurityRoleTransfer getRoleTransfer(
			SecRole secRole,
			boolean includePrivileges,
			boolean includePrivilegeDescription)
	{
		SecurityRoleTransfer transfer = new SecurityRoleTransfer();
		transfer.setId(secRole.getId());
		transfer.setName(secRole.getName());
		transfer.setDescription(secRole.getDescription());

		// privileges are not always needed and may have additional database hits as they are lazy loaded
		if(includePrivileges)
		{
			for (SecObjPrivilege privilege : secRole.getSecObjPrivilege())
			{
				SecurityObjectTransfer securityObjectTransfer = getSecurityObjectTransfer(
						privilege, includePrivilegeDescription);
				transfer.addAccess(securityObjectTransfer.getName(), securityObjectTransfer);
			}
		}
		return transfer;
	}

	private SecurityObjectTransfer getSecurityObjectTransfer(
			SecObjPrivilege secObjPrivilege,
			boolean includePrivilegeDescription)
	{
		String objectName = secObjPrivilege.getId().getObjectName();
		SecurityObjectTransfer transfer = getSecurityObjectTransfer(
				SecObjectName.OBJECT_NAME.fromValueString(objectName), includePrivilegeDescription);
		transfer.setPrivileges(getPrivilegeLevels(secObjPrivilege.getPrivilege()));
		return transfer;
	}

	private SecurityObjectTransfer getSecurityObjectTransfer(
			SecObjectName.OBJECT_NAME objectName,
			boolean includePrivilegeDescription)
	{
		SecurityObjectTransfer transfer = new SecurityObjectTransfer();
		transfer.setName(objectName);
		transfer.setPrivileges(new ArrayList<>());

		// descriptions can add a lot of database hits. only load them if we need them
		if(includePrivilegeDescription)
		{
			Optional<SecObjectName> SecObjectNameOptional = secObjectNameDao.findOptional(objectName.getValue());
			SecObjectNameOptional.ifPresent(secObjectName -> transfer.setDescription(secObjectName.getDescription()));
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

}
