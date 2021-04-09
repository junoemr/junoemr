/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package org.oscarehr.security.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class SecObjPrivilegePrimaryKey implements Serializable
{
	protected Integer secRoleId = null;
	protected String objectName = null;

	public SecObjPrivilegePrimaryKey()
	{
		// do nothing, required by jpa
	}

	public SecObjPrivilegePrimaryKey(Integer secRoleId, String objectName)
	{
		this.secRoleId = secRoleId;
		this.objectName = objectName;
	}

	public Integer getSecRoleId()
	{
		return secRoleId;
	}

	public void setSecRoleId(Integer roleId)
	{
		this.secRoleId = roleId;
	}

	public String getObjectName()
	{
		return objectName;
	}

	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	@Override
	public String toString()
	{
		return ("secRoleId=" + secRoleId + ", objectName=" + objectName);
	}

	@Override
	public int hashCode()
	{
		return (toString().hashCode());
	}

	@Override
	public boolean equals(Object o) {
		try {
			SecObjPrivilegePrimaryKey o1 = (SecObjPrivilegePrimaryKey) o;
			return ((secRoleId.equals(o1.secRoleId)) && (objectName.equals(o1.objectName)));
		} catch (RuntimeException e) {
			return (false);
		}
	}

}
