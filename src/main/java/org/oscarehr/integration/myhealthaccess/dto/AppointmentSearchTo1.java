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

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentSearchTo1
{
	private STATUS_CODE status;
	private List<AppointmentTo1> appointments;

	enum STATUS_CODE
	{
		SUCCESS,
		NOT_FOUND
	}

	public Boolean isSuccess()
	{
		return status == STATUS_CODE.SUCCESS;
	}

	public Boolean isNotFound()
	{
		return status == STATUS_CODE.NOT_FOUND;
	}

	public STATUS_CODE getStatus()
	{
		return status;
	}

	public void setStatus(STATUS_CODE status)
	{
		this.status = status;
	}

	public void setStatus(String status)
	{
		this.status = STATUS_CODE.valueOf(status.toUpperCase());
	}

	public List<AppointmentTo1> getAppointments()
	{
		return appointments;
	}

	public void setAppointments(List<AppointmentTo1> appointments)
	{
		this.appointments = appointments;
	}
}
