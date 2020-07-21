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
package org.oscarehr.ws.rest.integrations.aqs.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import oscar.util.Jackson.LocalDateSerializer;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentQueueTo1
{
	@JsonProperty("id")
	private String remoteId;
	private String organizationId;
	private String queueName;
	private Integer queueLimit;
	private String queueColor;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate createdAt;
	@JsonDeserialize(using = LocalDateDeserializer.class)
	@JsonSerialize(using = LocalDateSerializer.class)
	private LocalDate updatedAt;
	private String createdBy;
	private String createdByType;//TODO change to enum once types are defined
	private String updatedBy;
	private String updatedByType;//TODO change to enum once types are defined

	public AppointmentQueueTo1(String remoteId, String queueName, Integer queueLimit, String queueColor)
	{
		//TODO remove. more for fake data injection.
		this.remoteId = remoteId;
		this.queueName = queueName;
		this.queueLimit = queueLimit;
		this.queueColor = queueColor;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getQueueName()
	{
		return queueName;
	}

	public void setQueueName(String queueName)
	{
		this.queueName = queueName;
	}

	public Integer getQueueLimit()
	{
		return queueLimit;
	}

	public void setQueueLimit(Integer queueLimit)
	{
		this.queueLimit = queueLimit;
	}

	public String getQueueColor()
	{
		return queueColor;
	}

	public void setQueueColor(String queueColor)
	{
		this.queueColor = queueColor;
	}

	public LocalDate getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt)
	{
		this.createdAt = createdAt;
	}

	public LocalDate getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt(LocalDate updatedAt)
	{
		this.updatedAt = updatedAt;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getCreatedByType()
	{
		return createdByType;
	}

	public void setCreatedByType(String createdByType)
	{
		this.createdByType = createdByType;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	public String getUpdatedByType()
	{
		return updatedByType;
	}

	public void setUpdatedByType(String updatedByType)
	{
		this.updatedByType = updatedByType;
	}
}
