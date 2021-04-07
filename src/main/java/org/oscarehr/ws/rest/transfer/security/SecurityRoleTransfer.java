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
package org.oscarehr.ws.rest.transfer.security;

import lombok.Data;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.managers.SecurityInfoManager;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SecurityRoleTransfer implements Serializable
{
	private Integer id;
	private String name;
	private String description;
	private Map<SecObjectName.OBJECT_NAME, List<SecurityInfoManager.PRIVILEGE_LEVEL>> privileges;

	public SecurityRoleTransfer()
	{
		privileges = new HashMap<>();
	}

	public void addPrivilege(SecObjectName.OBJECT_NAME roleName, List<SecurityInfoManager.PRIVILEGE_LEVEL> privilegeLevels)
	{
		privileges.put(roleName, privilegeLevels);
	}
}
