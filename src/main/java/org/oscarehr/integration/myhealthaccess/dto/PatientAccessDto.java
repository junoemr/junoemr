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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.integration.myhealthaccess.model.MhaUserType;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class PatientAccessDto implements Serializable
{
	// core information
	@JsonProperty("patient_user_id")
	protected String patientId;
	@JsonProperty("clinic_profile_id")
	protected String clinicId;
	@JsonProperty("link_status")
	protected MHAPatient.LINK_STATUS linkStatus;

	// user permissions
	@JsonProperty("can_message_clinic")
	protected Boolean canMessage;
	@JsonProperty("can_cancel_appointments")
	protected Boolean canCancelAppointments;

	// detail connection info
	// confirmation
	@JsonProperty("clinic_confirmed_at")
	protected ZonedDateTime confirmedAt;
	@JsonProperty("confirmed_by")
	protected String confirmedById;
	@JsonProperty("confirmed_by_type")
	protected MhaUserType confirmedByType;
	@JsonProperty("confirming_user_name")
	protected String confirmingUserName;
	// verification
	@JsonProperty("clinic_verified_at")
	protected ZonedDateTime verifiedAt;
	@JsonProperty("verified_by")
	protected String verifiedById;
	@JsonProperty("verified_by_type")
	protected MhaUserType verifiedByType;
	@JsonProperty("verifier_user_name")
	protected String verifierUserName;
}
