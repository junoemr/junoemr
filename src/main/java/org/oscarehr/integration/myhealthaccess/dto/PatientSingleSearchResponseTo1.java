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
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PatientSingleSearchResponseTo1
{
	enum STATUS_CODE
	{
		SUCCESS,
		NOT_FOUND,
		NOT_UNIQUE
	}

	@JsonProperty("patient")
	private PatientTo1 patientTo1;
	@JsonProperty("patients")
	@Getter
	@Setter
	private List<PatientTo1> patientTo1s;
	private STATUS_CODE status;

	public boolean isSuccess()
	{
		return status == STATUS_CODE.SUCCESS;
	}

	public boolean isNotFound()
	{
		return status == STATUS_CODE.NOT_FOUND;
	}

	public boolean isNotUnique()
	{
		return status == STATUS_CODE.NOT_UNIQUE;
	}

	// true if this transfer contains a list of patients
	public boolean hasPatientListResult()
	{
		return this.patientTo1s != null;
	}

	public PatientTo1 getPatientTo1()
	{
		return patientTo1;
	}

	public void setPatientTo1(PatientTo1 patientTo1)
	{
		this.patientTo1 = patientTo1;
	}

	public STATUS_CODE getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = STATUS_CODE.valueOf(status.toUpperCase());
	}

	public void setStatus(STATUS_CODE status)
	{
		this.status = status;
	}
}
