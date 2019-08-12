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
package org.oscarehr.appointment.dto;

import java.time.LocalDateTime;

public class AppointmentEditRecord
{
	private Integer id;
	private Integer appointmentNo;
	private String providerNo;
	private LocalDateTime appointmentDate;
	private Integer demographicNo;
	private LocalDateTime updateDateTime;
	private LocalDateTime  createDateTime;
	private String lastUpdateUser;
	private String updateUserDisplayName;
	private String creator;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getUpdateUserDisplayName()
	{
		return updateUserDisplayName;
	}

	public void setUpdateUserDisplayName(String updateUserDisplayName)
	{
		this.updateUserDisplayName = updateUserDisplayName;
	}

	public LocalDateTime getAppointmentDate()
	{
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDateTime appointmentDate)
	{
		this.appointmentDate = appointmentDate;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public LocalDateTime getUpdateDateTime()
	{
		return updateDateTime;
	}

	public void setUpdateDateTime(LocalDateTime updateDateTime)
	{
		this.updateDateTime = updateDateTime;
	}

	public LocalDateTime getCreateDateTime()
	{
		return createDateTime;
	}

	public void setCreateDateTime(LocalDateTime createDateTime)
	{
		this.createDateTime = createDateTime;
	}

	public String getLastUpdateUser()
	{
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser)
	{
		this.lastUpdateUser = lastUpdateUser;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}
}
