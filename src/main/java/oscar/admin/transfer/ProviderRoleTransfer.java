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
package oscar.admin.transfer;

public class ProviderRoleTransfer
{

	private String providerId;
	private String firstName;
	private String lastName;
	private Boolean superAdmin;

	private String roleName;
	private Integer roleId;
	private Long userRoleId;
	private Long primaryRoleId;

	public ProviderRoleTransfer()
	{
	}


	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public Long getUserRoleId()
	{
		return userRoleId;
	}

	public void setUserRoleId(Long userRoleId)
	{
		this.userRoleId = userRoleId;
	}

	public Integer getRoleId()
	{
		return roleId;
	}

	public void setRoleId(Integer roleId)
	{
		this.roleId = roleId;
	}

	public boolean hasRole()
	{
		return (userRoleId != null);
	}

	public Long getPrimaryRoleId()
	{
		return primaryRoleId;
	}

	public boolean hasPrimaryRoleId()
	{
		return (primaryRoleId != null);
	}

	public void setPrimaryRoleId(Long primaryRoleId)
	{
		this.primaryRoleId = primaryRoleId;
	}

	public Boolean isSuperAdmin()
	{
		return superAdmin;
	}

	public void setSuperAdmin(Boolean superAdmin)
	{
		this.superAdmin = superAdmin;
	}
}
