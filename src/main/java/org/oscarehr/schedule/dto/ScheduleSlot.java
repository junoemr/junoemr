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
package org.oscarehr.schedule.dto;

import java.time.LocalDateTime;

public class ScheduleSlot
{
	private LocalDateTime appointmentDateTime;

	private String code;
	private Integer durationMinutes;
	private String description;
	private String color;
	private String junoColor;
	private String confirm;
	private Integer bookingLimit;

	public ScheduleSlot(LocalDateTime appointmentDateTime, String code, Integer durationMinutes,
		String description, String color, String junoColor, String confirm, Integer bookingLimit)
	{
		this.appointmentDateTime = appointmentDateTime;
		this.code = code;
		this.durationMinutes = durationMinutes;
		this.description = description;
		this.color = color;
		this.junoColor = junoColor;
		this.confirm = confirm;
		this.bookingLimit = bookingLimit;
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

	public String getJunoColor()
	{
		return junoColor;
	}

	public String getConfirm()
	{
		return confirm;
	}

	public Integer getBookingLimit()
	{
		return bookingLimit;
	}
}
