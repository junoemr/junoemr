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
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.springframework.beans.BeanUtils;
import oscar.util.Jackson.ZonedDateTimeStringSerializer;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueuedAppointmentTo1 implements Serializable
{
	@JsonProperty("id")
	private String remoteId;
	private String queueId;
	private Integer queuePosition;
	private String status;//TODO change to enum once statuses are defined
	@JsonProperty("integrationPatientId")
	private String demographicNo;
	@JsonSerialize(using = ZonedDateTimeStringSerializer.class)
	private ZonedDateTime createdAt;
	@JsonSerialize(using = ZonedDateTimeStringSerializer.class)
	private ZonedDateTime updatedAt;
	private String createdBy;
	private String createdByType;//TODO change to enum once types are defined
	private String updatedBy;
	private String updatedByType;//TODO change to enum once types are defined
	private String demographicName;
	private String reason;

	public static List<QueuedAppointmentTo1> fromQueuedAppointmentList(List<QueuedAppointment> queuedAppointments)
	{
		ArrayList<QueuedAppointmentTo1> queuedAppointmentTo1s = new ArrayList<>();

		for (QueuedAppointment queuedAppointment : queuedAppointments)
		{
			queuedAppointmentTo1s.add(new QueuedAppointmentTo1(queuedAppointment));
		}
		return queuedAppointmentTo1s;
	}

	// default constructor required for serialization
	public QueuedAppointmentTo1()
	{
	}

	public QueuedAppointmentTo1(QueuedAppointment queuedAppointment)
	{
		BeanUtils.copyProperties(queuedAppointment, this);
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

	public ZonedDateTime getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt)
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
