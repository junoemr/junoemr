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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.security.dao.SecObjectNameDao;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.ws.rest.transfer.security.SecurityPermissionTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PermissionToSecurityPermissionTransferConverter extends AbstractModelConverter<Permission, SecurityPermissionTransfer>
{
	@Autowired
	private SecObjectNameDao secObjectNameDao;

	@Override
	public SecurityPermissionTransfer convert(Permission permission)
	{
		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();
		return this.convert(permission, nameEntityMap);
	}

	@Override
	public List<SecurityPermissionTransfer> convert(Collection<? extends Permission> entities)
	{
		Map<String, SecObjectName> nameEntityMap = secObjectNameDao.findAllMappedById();
		return entities.stream().map((permission) -> this.convert(permission, nameEntityMap)).collect(Collectors.toList());
	}

	public SecurityPermissionTransfer convert(Permission permission, Map<String, SecObjectName> nameEntityMap)
	{
		SecurityPermissionTransfer transfer = new SecurityPermissionTransfer();
		transfer.setDescription(permission.getPrivilegeLevel().name() + " " +
				StringUtils.lowerCase(nameEntityMap.get(permission.getObjectName().getValue()).getDescription()));
		transfer.setPermission(permission);
		return transfer;
	}
}
