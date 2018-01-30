/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.schedule.dto;

import java.time.LocalDateTime;

public class ScheduleSlot
{
	//private List<AppointmentDetails> appointmentDetails = null;
	private LocalDateTime appointmentDateTime;

	private String code;
	private Integer durationMinutes;
	private String description;
	private String color;
	private String confirm;
	private Integer bookingLimit;

	public ScheduleSlot(LocalDateTime appointmentDateTime, String code, Integer durationMinutes,
		String description, String color, String confirm, Integer bookingLimit)//,
		//List<AppointmentDetails> appointmentDetails)
	{
		this.appointmentDateTime = appointmentDateTime;
		this.code = code;
		this.durationMinutes = durationMinutes;
		this.description = description;
		this.color = color;
		this.confirm = confirm;
		this.bookingLimit = bookingLimit;
		//this.appointmentDetails = appointmentDetails;
	}

	public LocalDateTime getAppointmentDateTime()
	{
		return appointmentDateTime;
	}

	public String getCode()
	{
		return code;
	}

	public Integer getDurationMinutes()
	{
		return durationMinutes;
	}

	public String getDescription()
	{
		return description;
	}

	public String getColor()
	{
		return color;
	}

	public String getConfirm()
	{
		return confirm;
	}

	public Integer getBookingLimit()
	{
		return bookingLimit;
	}

	/*
	public List<AppointmentDetails> getAppointmentDetails()
	{
		return appointmentDetails;
	}
	*/
}
