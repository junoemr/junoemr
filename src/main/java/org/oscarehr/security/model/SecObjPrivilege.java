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

import static org.oscarehr.managers.SecurityInfoManager.PRIVILEGE_LEVEL;
import static org.oscarehr.security.model.SecObjectName.OBJECT_NAME;

@Data
@Entity
@Table(name = "secObjPrivilege")
@Where(clause="deleted_at IS NULL")
public class SecObjPrivilege extends AbstractModel<SecObjPrivilegePrimaryKey>
{
	public enum PERMISSION
	{
		CONFIGURE_BILLING_READ(OBJECT_NAME.ADMIN_BILLING, PRIVILEGE_LEVEL.READ),
		CONFIGURE_BILLING_CREATE(OBJECT_NAME.ADMIN_BILLING, PRIVILEGE_LEVEL.CREATE),
		CONFIGURE_BILLING_UPDATE(OBJECT_NAME.ADMIN_BILLING, PRIVILEGE_LEVEL.UPDATE),
		CONFIGURE_BILLING_DELETE(OBJECT_NAME.ADMIN_BILLING, PRIVILEGE_LEVEL.DELETE),

		CONFIGURE_CONSULT_READ(OBJECT_NAME.ADMIN_CONSULT, PRIVILEGE_LEVEL.READ),
		CONFIGURE_CONSULT_CREATE(OBJECT_NAME.ADMIN_CONSULT, PRIVILEGE_LEVEL.CREATE),
		CONFIGURE_CONSULT_UPDATE(OBJECT_NAME.ADMIN_CONSULT, PRIVILEGE_LEVEL.UPDATE),
		CONFIGURE_CONSULT_DELETE(OBJECT_NAME.ADMIN_CONSULT, PRIVILEGE_LEVEL.DELETE),

		CONFIGURE_SECURITY_ROLES_READ(OBJECT_NAME.ADMIN_SECURITY, PRIVILEGE_LEVEL.READ),
		CONFIGURE_SECURITY_ROLES_CREATE(OBJECT_NAME.ADMIN_SECURITY, PRIVILEGE_LEVEL.CREATE),
		CONFIGURE_SECURITY_ROLES_UPDATE(OBJECT_NAME.ADMIN_SECURITY, PRIVILEGE_LEVEL.UPDATE),
		CONFIGURE_SECURITY_ROLES_DELETE(OBJECT_NAME.ADMIN_SECURITY, PRIVILEGE_LEVEL.DELETE),

		TICKLER_READ(OBJECT_NAME.TICKLER, PRIVILEGE_LEVEL.READ),
		TICKLER_CREATE(OBJECT_NAME.TICKLER, PRIVILEGE_LEVEL.CREATE),
		TICKLER_UPDATE(OBJECT_NAME.TICKLER, PRIVILEGE_LEVEL.UPDATE),
		TICKLER_DELETE(OBJECT_NAME.TICKLER, PRIVILEGE_LEVEL.DELETE);

		private final OBJECT_NAME objectName;
		private final PRIVILEGE_LEVEL privilegeLevel;

		PERMISSION(OBJECT_NAME objectName, PRIVILEGE_LEVEL privilegeLevel)
		{
			this.objectName = objectName;
			this.privilegeLevel = privilegeLevel;
		}

		public OBJECT_NAME getObjectName()
		{
			return this.objectName;
		}

		public PRIVILEGE_LEVEL getPrivilegeLevel()
		{
			return this.privilegeLevel;
		}

		public static PERMISSION from(OBJECT_NAME objectName, PRIVILEGE_LEVEL privilegeLevel)
		{
			for (PERMISSION permission : PERMISSION.values())
			{
				if (permission.getObjectName().equals(objectName) && permission.getPrivilegeLevel().equals(privilegeLevel))
				{
					return permission;
				}
			}
			return null;
		}
	}

	@EmbeddedId
	private SecObjPrivilegePrimaryKey id;

	private String privilege = "|0|";

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

	@MapsId("objectName")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectName", referencedColumnName = "objectName", nullable = false, insertable = false, updatable = false)
	private SecObjectName secObjectName;

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
