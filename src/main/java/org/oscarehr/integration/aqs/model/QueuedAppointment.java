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
package org.oscarehr.integration.aqs.model;

import ca.cloudpractice.aqs.client.model.QueuedAppointmentInput;
import ca.cloudpractice.aqs.client.model.QueuedAppointmentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class QueuedAppointment
{

	private UUID id;
	private UUID queueId;
	private Integer queuePosition;
	private Integer demographicNo;
	private OffsetDateTime createdAt;
	private String reason;
	private Integer reasonTypeId;
	private String notes;
	private Integer durationMinutes;
	private Integer siteId;
	private Boolean virtual;
	private Boolean critical;
	private UUID clinicId;
	private QueuedAppointmentStatus status;
	private String createdBy;
	private RemoteUserType createdByType;
	private CommunicationType communicationType;

	/**
	 * construct a QueuedAppointmentInput form this QueuedAppointment
	 * @return - a new QueuedAppointmentInput
	 */
	public QueuedAppointmentInput asQueuedAppointmentInput()
	{
		QueuedAppointmentInput queuedAppointmentInput = new QueuedAppointmentInput();

		BeanUtils.copyProperties(this, queuedAppointmentInput, "demographicNo");
		queuedAppointmentInput.setIntegrationPatientId(this.getDemographicNo().toString());

		return queuedAppointmentInput;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	public Boolean isVirtual()
	{
		return this.getVirtual();
	}

	public Boolean isVideo()
	{
		return this.getCommunicationType() != null && this.getCommunicationType() == CommunicationType.VIDEO;
	}

	public Boolean isAudio()
	{
		return this.getCommunicationType() != null && this.getCommunicationType() == CommunicationType.AUDIO;
	}

	public Boolean isChat()
	{
		return this.getCommunicationType() != null && this.getCommunicationType() == CommunicationType.CHAT;
	}

	public Boolean isCritical()
	{
		return this.getCritical();
	}

}
