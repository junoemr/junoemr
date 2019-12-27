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

package org.oscarehr.integration.iceFall.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "icefall_log")
public class IceFallLog extends AbstractModel<Integer> implements Serializable
{
	public enum STATUS {
		ERROR,
		SENT
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	@Column(name = "status")
	private String status;
	@Column(name = "message")
	private String message;
	@Column(name = "form_id")
	private Integer formId;
	@Column(name = "demographic_no")
	private Integer demographicNo;
	@Column(name = "is_form_instance")
	private Boolean formInstance;
	@Column(name = "sending_provider_no")
	private String sendingProviderNo;
	@Column(name = "created_at")
	private Date createdAt;

	@Override
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setStatus(STATUS status)
	{
		this.status = status.name();
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public Integer getFormId()
	{
		return formId;
	}

	public void setFormId(Integer formId)
	{
		this.formId = formId;
	}

	public Boolean getFormInstance()
	{
		return formInstance;
	}

	public void setFormInstance(Boolean formInstance)
	{
		this.formInstance = formInstance;
	}

	public String getSendingProviderNo()
	{
		return sendingProviderNo;
	}

	public void setSendingProviderNo(String sendingProviderNo)
	{
		this.sendingProviderNo = sendingProviderNo;
	}

	public Date getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(Date createDate)
	{
		this.createdAt = createDate;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}
}
