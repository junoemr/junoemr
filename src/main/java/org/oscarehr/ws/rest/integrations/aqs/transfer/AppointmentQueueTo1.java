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

import ca.cloudpractice.aqs.client.model.QueueInput;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.springframework.beans.BeanUtils;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentQueueTo1 implements Serializable
{
	@JsonProperty("id")
	private UUID remoteId;
	private String queueName;
	private Integer queueLimit;
	private String queueColor;
	@JsonSerialize(using = OffsetDateTimeSerializer.class)
	private OffsetDateTime createdAt;

	private QueueAvailabilitySettingsTransfer availabilitySettings;

	public static List<AppointmentQueueTo1> fromAppointmentQueueList(List<AppointmentQueue> appointmentQueues)
	{
		ArrayList<AppointmentQueueTo1> appointmentQueueTo1s = new ArrayList<>();

		for (AppointmentQueue appointmentQueue: appointmentQueues)
		{
			appointmentQueueTo1s.add(new AppointmentQueueTo1(appointmentQueue));
		}

		return appointmentQueueTo1s;
	}

	// default constructor required for serialization
	public AppointmentQueueTo1()
	{
	}

	public AppointmentQueueTo1(AppointmentQueue appointmentQueue)
	{
		BeanUtils.copyProperties(appointmentQueue, this, "name", "onDemandBookingSettings");
		this.setQueueName(appointmentQueue.getName());
		//TODO this should come from the aqs queue object
		this.setAvailabilitySettings(new QueueAvailabilitySettingsTransfer());
	}

	public QueueInput asCreateQueueInput()
	{
		QueueInput createQueueInput = new QueueInput();
		createQueueInput.setName(this.getQueueName());
		createQueueInput.setQueueLimit(this.getQueueLimit());
		return createQueueInput;
	}

	public QueueAvailabilitySettingsTransfer getAvailabilitySettings()
	{
		return availabilitySettings;
	}

	public void setAvailabilitySettings(QueueAvailabilitySettingsTransfer availabilitySettings)
	{
		this.availabilitySettings = availabilitySettings;
	}
}
