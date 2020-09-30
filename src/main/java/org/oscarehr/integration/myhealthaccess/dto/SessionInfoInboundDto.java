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

package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionInfoInboundDto
{
	@JsonProperty("id")
	private String remoteId;
	@JsonProperty("media_mode")
	private String mediaMode;
	@JsonProperty("appointment_id")
	private String appointmentNo;
	@JsonProperty("clinic_profile_id")
	private UUID clinicProfileId;
	@JsonProperty("clinic_appointment_id")
	private UUID clinicAppointmentId;
	@JsonProperty("session_type")
	private String sessionType;
	@JsonProperty("patient_in_session")
	private Boolean patientInSession;
	@JsonProperty("patient_last_seen_at_status")
	private String patientLastSeenStatus;
}
