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

import lombok.Data;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "secObjPrivilege")
@Where(clause="deleted_at IS NULL")
public class SecObjPrivilege extends AbstractModel<SecObjPrivilegePrimaryKey>
{
	@EmbeddedId
	private SecObjPrivilegePrimaryKey id;

	@Deprecated
	private String privilege = "|0|";

	@Column(name = "permission_read")
	private boolean permissionRead;

	@Column(name = "permission_update")
	private boolean permissionUpdate;

	@Column(name = "permission_create")
	private boolean permissionCreate;

	@Column(name = "permission_delete")
	private boolean permissionDelete;

	@Column(name = "inclusive")
	private boolean inclusive;

	@Deprecated
	private int priority = 0;

	@Column(name = "provider_no")
	private String providerNo = null;

	@Deprecated // property/column to be removed once all references use roleId
	private String roleUserGroup;

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime deletedAt;

	@MapsId("secRoleId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "secRoleId", referencedColumnName = "role_no", nullable = false, insertable = false, updatable = false)
	private SecRole secRole;

//	@MapsId("objectName")
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "objectName", referencedColumnName = "objectName", nullable = false, insertable = false, updatable = false)
//	private SecObjectName secObjectName;

	public SecObjPrivilege()
	{
	}

	public SecObjPrivilege(SecObjPrivilege toCopy)
	{
		this.id = null;
		this.privilege = toCopy.getPrivilege();
		this.permissionRead = toCopy.isPermissionRead();
		this.permissionUpdate = toCopy.isPermissionUpdate();
		this.permissionCreate = toCopy.isPermissionCreate();
		this.permissionDelete = toCopy.isPermissionDelete();
		this.priority = toCopy.getPriority();
		this.providerNo = toCopy.getProviderNo();
		this.inclusive = toCopy.isInclusive();
		this.roleUserGroup = toCopy.getRoleUserGroup();
		this.deletedAt = null;
		this.secRole = toCopy.getSecRole();
//		this.secObjectName = null;
	}

	@Override
	public SecObjPrivilegePrimaryKey getId()
	{
		return id;
	}

	@Override
	public int hashCode()
	{
		return this.id.hashCode();
	}
}
