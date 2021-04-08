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
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "secObjPrivilege")
public class SecObjPrivilege extends AbstractModel<SecObjPrivilegePrimaryKey>
{
	@EmbeddedId
	private SecObjPrivilegePrimaryKey id;

	private String privilege = "|0|";

	private int priority = 0;

	@Column(name = "provider_no")
	private String providerNo = null;

	@Deprecated // property/column to be removed once all references use roleId
	private String roleUserGroup;

	@MapsId("roleId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "roleId", referencedColumnName = "role_no", insertable = false, updatable = false)
	private SecRole secRole;

	@MapsId("objectName")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "objectName", referencedColumnName = "objectName", insertable = false, updatable = false)
	private SecObjectName secObjectName;

	@Override
	public SecObjPrivilegePrimaryKey getId()
	{
		return id;
	}
}
