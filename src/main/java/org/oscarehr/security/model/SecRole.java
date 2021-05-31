/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.security.model;

import lombok.Data;
import org.hibernate.annotations.Where;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Entity
@Table(name = "secRole")
@Where(clause="deleted_at IS NULL")
public class SecRole extends AbstractModel<Integer> implements Serializable, Comparable<SecRole>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_no")
	private Integer id;

	@Column(name = "role_name")
	private String name;

	private String description;

	@Column(name = "system_managed")
	private boolean systemManaged;

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private String deletedBy;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "secRole")
	private List<SecObjPrivilege> secObjPrivilege;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "secRole")
	private List<SecUserRole> secUserRoles;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_sec_role_id", referencedColumnName = "role_no")
	private SecRole parentSecRole;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parentSecRole")
	private List<SecRole> childSecRoles;

	@Override
	public Integer getId()
	{
		return id;
	}

	@PreUpdate
	public void checkSystemManaged() throws IllegalAccessException
	{
		if(this.isSystemManaged())
		{
			throw new IllegalAccessException("System managed roles cannot be modified");
		}
	}

	public int compareTo(SecRole o)
	{
		return (name.compareTo(o.name));
	}

	public List<SecObjPrivilege> getPrivilegesWithInheritance()
	{
		SecRole parentRole = this.getParentSecRole();
		List<SecObjPrivilege> rolePrivileges = this.getSecObjPrivilege();
		if(parentRole == null)
		{
			return rolePrivileges;
		}
		List<SecObjPrivilege> parentPrivileges = parentRole.getPrivilegesWithInheritance();

		// create a map of privileges
		Map<String, SecObjPrivilege> objectMap = new HashMap<>();
		for(SecObjPrivilege secObjPrivilege : parentPrivileges)
		{
			objectMap.put(secObjPrivilege.getId().getObjectName(), secObjPrivilege);
		}

		for(SecObjPrivilege rolePrivilege : rolePrivileges)
		{
			if(rolePrivilege.isInclusive())
			{
				// overwrite or add the child value, as it takes precedence
				objectMap.put(rolePrivilege.getId().getObjectName(), rolePrivilege);
			}
			else
			{
				// remove the excluded roles from parent list if they exist
				objectMap.remove(rolePrivilege.getId().getObjectName());
			}
		}
		return new ArrayList<>(objectMap.values());
	}
}
