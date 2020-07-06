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

public class PatientInviteTo1
{
	@JsonProperty("remote_id")
	private String remoteId;
	private PatientTo1 patient;
	@JsonProperty("primary_provider_id")
	private String primaryProviderId;

	public PatientInviteTo1(PatientTo1 patient, String remoteId, String primaryProviderId)
	{
		this.patient = patient;
		this.remoteId = remoteId;
		this.primaryProviderId = primaryProviderId;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public PatientTo1 getPatient()
	{
		return patient;
	}

	public void setPatient(PatientTo1 patient)
	{
		this.patient = patient;
	}

	public String getPrimaryProviderId()
	{
		return primaryProviderId;
	}

	public void setPrimaryProviderId(String primaryProviderId)
	{
		this.primaryProviderId = primaryProviderId;
	}
}
