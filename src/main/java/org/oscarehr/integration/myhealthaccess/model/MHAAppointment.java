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
package org.oscarehr.integration.myhealthaccess.model;

import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentTo1;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.BeanUtils;

import java.time.ZonedDateTime;

public class MHAAppointment
{
	private String id;
	private Boolean cancelled;
	private Boolean virtual;
	private ZonedDateTime startDateTime;
	private ZonedDateTime startDateTimeUtc;
	private ZonedDateTime endDateTime;
	private ZonedDateTime endDateTimeUtc;
	private Integer appointmentNo;
	private Integer demographicNo;
	private Integer providerNo;
	private String appName;
	private APPOINTMENT_TYPE appointmentType;

	public enum APPOINTMENT_TYPE {
		REGULAR,
		ONE_TIME_LINK,
		ON_DEMAND_AUDIO_CALL,
	}

	public MHAAppointment(AppointmentTo1 appointmentTo1)
	{
		BeanUtils.copyProperties(appointmentTo1, this);
	}

	public Appointment getAppointment()
	{
		OscarAppointmentDao oscarAppointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
		return oscarAppointmentDao.find(this.appointmentNo);
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Boolean getCancelled()
	{
		return cancelled;
	}

	public void setCancelled(Boolean cancelled)
	{
		this.cancelled = cancelled;
	}

	public Boolean getVirtual()
	{
		return virtual;
	}

	public void setVirtual(Boolean virtual)
	{
		this.virtual = virtual;
	}

	public ZonedDateTime getStartDateTime()
	{
		return startDateTime;
	}

	public void setStartDateTime(ZonedDateTime startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	public ZonedDateTime getStartDateTimeUtc()
	{
		return startDateTimeUtc;
	}

	public void setStartDateTimeUtc(ZonedDateTime startDateTimeUtc)
	{
		this.startDateTimeUtc = startDateTimeUtc;
	}

	public ZonedDateTime getEndDateTime()
	{
		return endDateTime;
	}

	public void setEndDateTime(ZonedDateTime endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	public ZonedDateTime getEndDateTimeUtc()
	{
		return endDateTimeUtc;
	}

	public void setEndDateTimeUtc(ZonedDateTime endDateTimeUtc)
	{
		this.endDateTimeUtc = endDateTimeUtc;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(Integer providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getAppName()
	{
		return appName;
	}

	public void setAppName(String appName)
	{
		this.appName = appName;
	}

	public APPOINTMENT_TYPE getAppointmentType()
	{
		return appointmentType;
	}

	public void setAppointmentType(APPOINTMENT_TYPE appointmentType)
	{
		this.appointmentType = appointmentType;
	}
}
