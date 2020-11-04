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

import ca.cloudpractice.aqs.client.model.QueuedAppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.OffsetDateTime;
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
	private QueuedAppointmentStatus status;
	private AppointmentQueueOnDemandSettingsTransfer appointmentQueueOnDemandSettings;
	private QueueAvailabilitySettingsTransfer availabilitySettings;
	private Integer defaultAppointmentDurationMinutes;
	private Boolean isAvailable;

	public AppointmentQueueTo1()
	{
	}
}
