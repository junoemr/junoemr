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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.oscarehr.integration.myhealthaccess.model.MHAAppointment;
import oscar.util.Jackson.ZonedDateTimeStringDeserializer;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentTo1
{
	private String id;
	@JsonProperty("is_cancelled")
	private Boolean cancelled;
	@JsonProperty("is_virtual")
	private Boolean virtual;
	@JsonProperty("start_datetime")
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	private ZonedDateTime startDateTime;
	@JsonProperty("start_datetime_utc")
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	private ZonedDateTime startDateTimeUtc;
	@JsonProperty("end_datetime")
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	private ZonedDateTime endDateTime;
	@JsonProperty("end_datetime_utc")
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	private ZonedDateTime endDateTimeUtc;
	@JsonProperty("appointment_no")
	private Integer appointmentNo;
	@JsonProperty("demographic_no")
	private Integer demographicNo;
	@JsonProperty("provider_no")
	private Integer providerNo;
	@JsonProperty("app_name")
	private String appName;
	@JsonProperty("appointment_type")
	private MHAAppointment.APPOINTMENT_TYPE appointmentType;

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

	public MHAAppointment.APPOINTMENT_TYPE getAppointmentType()
	{
		return appointmentType;
	}

	public void setAppointmentType(MHAAppointment.APPOINTMENT_TYPE appointmentType)
	{
		this.appointmentType = appointmentType;
	}

	public void setAppointmentType(String appointmentType)
	{
		this.appointmentType = MHAAppointment.APPOINTMENT_TYPE.valueOf(appointmentType.toUpperCase());
	}
}
