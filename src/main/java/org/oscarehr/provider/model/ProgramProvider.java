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
package org.oscarehr.provider.model;

import com.quatro.model.security.Secrole;
import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.Provider;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * This is the object class that relates to the program_provider table.
 * Any customizations belong here.
 */
@Entity(name = "model.ProgramProvider")
@Table(name = "program_provider")
public class ProgramProvider extends AbstractModel<Long> implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;// fields
	@Column(name = "program_id")
	private Long programId;
	@Column(name = "provider_no")
	private String providerNo;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provider_no", insertable = false, updatable = false)
	private Provider provider;
	@Column(name = "role_id")
	private Long roleId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", insertable = false, updatable = false)
	private Secrole role;
	@Column(name = "team_id")
	private Integer teamId;

	/**
	 * Return the unique identifier of this class
	 *
	 * generator-class="native"
	 * column="id"
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Set the unique identifier of this class
	 *
	 * @param _id the new ID
	 */
	public void setId(Long _id)
	{
		this.id = _id;
	}

	/**
	 * Return the value associated with the column: program_id
	 */
	public Long getProgramId()
	{
		return programId;
	}

	/**
	 * Set the value related to the column: program_id
	 *
	 * @param _programId the program_id value
	 */
	public void setProgramId(Long _programId)
	{
		this.programId = _programId;
	}

	/**
	 * Return the value associated with the column: provider_no
	 */
	public String getProviderNo()
	{
		return providerNo;
	}

	/**
	 * Set the value related to the column: provider_no
	 *
	 * @param _providerNo the provider_no value
	 */
	public void setProviderNo(String _providerNo)
	{
		this.providerNo = _providerNo;
	}

	/**
	 * Return the value associated with the column: role_id
	 */
	public Long getRoleId()
	{
		return roleId;
	}

	/**
	 * Set the value related to the column: role_id
	 *
	 * @param _roleId the role_id value
	 */
	public void setRoleId(Long _roleId)
	{
		this.roleId = _roleId;
	}

	/**
	 * column=role_id
	 */
	public Secrole getRole()
	{
		return this.role;
	}

	/**
	 * Set the value related to the column: role_id
	 *
	 * @param _role the role_id value
	 */
	public void setRole(Secrole _role)
	{
		this.role = _role;
	}

	/**
	 * column=provider_no
	 */
	public Provider getProvider()
	{
		return this.provider;
	}

	/**
	 * Set the value related to the column: provider_no
	 *
	 * @param _provider the provider_no value
	 */
	public void setProvider(Provider _provider)
	{
		this.provider = _provider;
	}

	/**
	 * column=team_id
	 */
	public int getTeamId()
	{
		return teamId;
	}

	/**
	 * Set the value related to the column: team_id
	 *
	 * @param teamId the team_id value
	 */
	public void setTeamId(int teamId)
	{
		this.teamId = teamId;
	}
}