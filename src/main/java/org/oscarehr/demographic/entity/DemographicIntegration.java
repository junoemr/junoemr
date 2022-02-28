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
package org.oscarehr.demographic.entity;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "demographic_integration")
public class DemographicIntegration extends AbstractModel<Integer>
{
	@Id
	@Column(name = "demographic_no")
	private Integer demographicNo;
	@Column(name = "created_at")
	private Date createdAt;
	@Column(name = "updated_at")
	private Date updatedAt;
	@Column(name = "deleted_at")
	private Date deletedAt;
	@Column(name = "integration_type")
	private String integrationType;
	@Column(name = "created_by_source")
	private String createdBySource;
	@Column(name = "created_by_remote_id")
	private String createdByRemoteId;
	@Column(name = "remote_id")
	private String remoteId;

	public Integer getId()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt)
	{
		this.updatedAt = updatedAt;
	}

	public Date getDeletedAt()
	{
		return deletedAt;
	}

	public void setDeletedAt(Date deletedAt)
	{
		this.deletedAt = deletedAt;
	}

	public String getIntegrationType()
	{
		return integrationType;
	}

	public void setIntegrationType(String integrationType)
	{
		this.integrationType = integrationType;
	}

	public String getCreatedBySource()
	{
		return createdBySource;
	}

	public void setCreatedBySource(String createdBySource)
	{
		this.createdBySource = createdBySource;
	}

	public String getCreatedByRemoteId()
	{
		return createdByRemoteId;
	}

	public void setCreatedByRemoteId(String createdByRemoteId)
	{
		this.createdByRemoteId = createdByRemoteId;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}
}
