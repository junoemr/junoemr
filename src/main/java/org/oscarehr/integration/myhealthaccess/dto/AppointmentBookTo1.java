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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.oscarehr.common.model.Appointment;
import oscar.util.ConversionUtils;
import oscar.util.Jackson.ZonedDateTimeStringDeserializer;
import oscar.util.Jackson.ZonedDateTimeStringSerializer;

import java.time.ZonedDateTime;

public class AppointmentBookTo1
{
	@JsonProperty("appointment_no")
	String appointmentNo;
	@JsonProperty("provider_no")
	String providerNo;
	@JsonProperty("demographic_no")
	String demographicNo;
	@JsonProperty("start_datetime")
	@JsonSerialize(using = ZonedDateTimeStringSerializer.class)
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	ZonedDateTime startDateTime;
	@JsonProperty("end_datetime")
	@JsonSerialize(using = ZonedDateTimeStringSerializer.class)
	@JsonDeserialize(using = ZonedDateTimeStringDeserializer.class)
	ZonedDateTime endDateTime;
	@JsonProperty("location")
	String site;
	@JsonProperty("reason")
	String reason;
	@JsonProperty("notes")
	String notes;
	@JsonProperty("type")
	String type;
	@JsonProperty("status")
	String status;
	@JsonProperty("is_virtual")
	Boolean isVirtual;


	public AppointmentBookTo1(Appointment appointment)
	{
		this.appointmentNo = appointment.getId().toString();
		this.providerNo = appointment.getProviderNo();
		this.demographicNo = String.valueOf(appointment.getDemographicNo());
		this.startDateTime = ConversionUtils.toZonedDateTime(appointment.getStartTimeAsFullDate());
		this.endDateTime = ConversionUtils.toZonedDateTime(appointment.getEndTimeAsFullDate());
		this.site = appointment.getLocation();
		this.reason = appointment.getReason();
		this.notes = appointment.getNotes();
		this.isVirtual = appointment.getIsVirtual();
		this.status = appointment.getStatus();
	}

	public String getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(String appointmentNo)
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

	public String getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(String demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public ZonedDateTime getStartDateTime()
	{
		return startDateTime;
	}

	public void setStartDateTime(ZonedDateTime startDateTime)
	{
		this.startDateTime = startDateTime;
	}

	public ZonedDateTime getEndDateTime()
	{
		return endDateTime;
	}

	public void setEndDateTime(ZonedDateTime endDateTime)
	{
		this.endDateTime = endDateTime;
	}

	public String getSite()
	{
		return site;
	}

	public void setSite(String site)
	{
		this.site = site;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Boolean getVirtual()
	{
		return isVirtual;
	}

	public void setVirtual(Boolean virtual)
	{
		isVirtual = virtual;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}
