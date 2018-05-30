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
package org.oscarehr.eform.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "eform_instance")
public class EFormInstance extends AbstractModel<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="eform_id")
	private EForm eFormTemplate;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="current_eform_data_id")
	private EFormData currentEFormData;

	@OneToMany(fetch=FetchType.LAZY, mappedBy = "eFormInstance")
	private List<EFormData> eFormDataList;

	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();

	@Column(name = "deleted")
	private Boolean deleted = false;

	@Override
	public Long getId()
	{
		return id;
	}

	public EForm getEFormTemplate()
	{
		return eFormTemplate;
	}

	public void setEFormTemplate(EForm eFormTemplate)
	{
		this.eFormTemplate = eFormTemplate;
	}

	public EFormData getCurrentEFormData()
	{
		return currentEFormData;
	}

	public void setCurrentEFormData(EFormData currentEFormData)
	{
		this.currentEFormData = currentEFormData;
	}

	public List<EFormData> getEFormDataList()
	{
		return eFormDataList;
	}

	public void setEFormDataList(List<EFormData> eFormDataList)
	{
		this.eFormDataList = eFormDataList;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Date createdAt)
	{
		this.createdAt = createdAt;
	}

	public Boolean getDeleted()
	{
		return deleted;
	}

	public void setDeleted(Boolean deleted)
	{
		this.deleted = deleted;
	}
}
