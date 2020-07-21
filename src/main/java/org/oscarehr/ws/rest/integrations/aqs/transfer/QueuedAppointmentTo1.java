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
public class QueuedAppointmentTo1
{
	@JsonProperty("id")
	private String remoteId;
	private String queueId;
	private Integer queuePosition;
	private String status;//TODO change to enum once statuses are defined
	@JsonProperty("integrationPatientId")
	private String demographicNo;
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
	private String demographicName;
	private String reason;

	public QueuedAppointmentTo1(Integer queuePosition, String demographicNo, String reason, String demographicDisplayName)
	{
		//TODO remove once REST calls in.
		this.demographicNo = demographicNo;
		this.reason = reason;
		this.queuePosition = queuePosition;
		this.demographicName = demographicDisplayName;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public String getQueueId()
	{
		return queueId;
	}

	public void setQueueId(String queueId)
	{
		this.queueId = queueId;
	}

	public Integer getQueuePosition()
	{
		return queuePosition;
	}

	public void setQueuePosition(Integer queuePosition)
	{
		this.queuePosition = queuePosition;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(String demographicNo)
	{
		this.demographicNo = demographicNo;
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

	public String getDemographicName()
	{
		return demographicName;
	}

	public void setDemographicName(String demographicName)
	{
		this.demographicName = demographicName;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}
}
