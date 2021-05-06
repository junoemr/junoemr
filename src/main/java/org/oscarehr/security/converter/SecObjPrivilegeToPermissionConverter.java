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
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjPrivilege;
import org.oscarehr.security.model.SecObjectName;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SecObjPrivilegeToPermissionConverter extends AbstractModelConverter<SecObjPrivilege, List<Permission>>
{
	/**
	 * returns a single list of permissions, combined and flattened from the conversion of each original input
	 * @param entities - the entities to convert
	 * @return - the list
	 */
	public List<Permission> convertToSingleList(Collection<SecObjPrivilege> entities)
	{
		return entities.stream().map(this::convert).flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toList());
	}

	@Override
	public List<Permission> convert(SecObjPrivilege secObjPrivilege)
	{
		List<Permission> permissions = new LinkedList<>();
		SecObjectName.OBJECT_NAME objectName = SecObjectName.OBJECT_NAME.fromValueString(secObjPrivilege.getId().getObjectName());

		if(secObjPrivilege.isPermissionRead())
		{
			permissions.add(Permission.from(objectName, SecurityInfoManager.PRIVILEGE_LEVEL.READ));
		}
		if(secObjPrivilege.isPermissionUpdate())
		{
			permissions.add(Permission.from(objectName, SecurityInfoManager.PRIVILEGE_LEVEL.UPDATE));
		}
		if(secObjPrivilege.isPermissionCreate())
		{
			permissions.add(Permission.from(objectName, SecurityInfoManager.PRIVILEGE_LEVEL.CREATE));
		}
		if(secObjPrivilege.isPermissionDelete())
		{
			permissions.add(Permission.from(objectName, SecurityInfoManager.PRIVILEGE_LEVEL.DELETE));
		}
		return permissions;
	}
}
