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
package org.oscarehr.security.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.ws.rest.transfer.security.SecurityPermissionTransfer;
import org.oscarehr.ws.rest.transfer.security.SecurityRoleTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecRoleToSecurityRoleTransferConverter extends AbstractModelConverter<SecRole, SecurityRoleTransfer>
{
	@Autowired
	private SecObjPrivilegeToPermissionConverter secObjPrivilegeToPermissionConverter;

	@Autowired
	private PermissionToSecurityPermissionTransferConverter permissionToSecurityPermissionTransferConverter;

	@Override
	public SecurityRoleTransfer convert(SecRole secRole)
	{
		return this.convert(secRole, true);
	}

	@Override
	public List<SecurityRoleTransfer> convert(Collection<SecRole> entities)
	{
		return this.convert(entities, true);
	}

	public List<SecurityRoleTransfer> convert(Collection<SecRole> entities, boolean includePrivileges)
	{
		return entities.stream().map((entity) -> convert(entity, includePrivileges)).collect(Collectors.toList());
	}

	public SecurityRoleTransfer convert(SecRole secRole, boolean includePrivileges)
	{
		SecurityRoleTransfer transfer = new SecurityRoleTransfer();
		transfer.setId(secRole.getId());
		transfer.setName(secRole.getName());
		transfer.setDescription(secRole.getDescription());
		transfer.setSystemManaged(secRole.isSystemManaged());

		SecRole parentRole = secRole.getParentSecRole();
		if(parentRole != null)
		{
			transfer.setParentRoleId(parentRole.getId());
		}
		List<SecRole> childRoles = secRole.getChildSecRoles();
		if(childRoles != null && !childRoles.isEmpty())
		{
			transfer.setChildRoleIds(childRoles.stream().map(SecRole::getId).collect(Collectors.toList()));
		}

		// privileges are not always needed and may have additional database hits as they are lazy loaded
		if(includePrivileges)
		{
			List<Permission> permissions = secObjPrivilegeToPermissionConverter.convertToSingleList(secRole.getPrivilegesWithInheritance());
			List<SecurityPermissionTransfer> permissionTransfers = permissionToSecurityPermissionTransferConverter.convert(permissions);
			transfer.setSecurityPermissions(permissionTransfers);
		}
		return transfer;
	}
}
