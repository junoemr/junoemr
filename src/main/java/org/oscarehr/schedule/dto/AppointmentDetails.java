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

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDetails
{

	Integer appointmentNo;
	Integer demographicNo;
	LocalDate date;
	LocalTime startTime;
	LocalTime endTime;
	String name;
	String notes;
	String reason;
	Integer reasonCode;
	String location;
	String resources;
	String type;
	String style;
	String bookingSource;
	String status;
	String statusTitle;
	String color;
	String iconImage;
	String shortLetterColour;
	String shortLetters;

	public AppointmentDetails(
		Integer appointmentNo,
		Integer demographicNo,
		LocalDate date,
		LocalTime startTime,
		LocalTime endTime,
		String name,
		String notes,
		String reason,
		Integer reasonCode,
		String location,
		String resources,
		String type,
		String style,
		String bookingSource,
		String status,
		String statusTitle,
		String color,
		String iconImage,
		String shortLetterColour,
		String shortLetters
	)
	{
		this.appointmentNo = appointmentNo;
		this.demographicNo = demographicNo;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.notes = notes;
		this.reason = reason;
		this.reasonCode = reasonCode;
		this.location = location;
		this.resources = resources;
		this.type = type;
		this.style = style;
		this.bookingSource = bookingSource;
		this.status = status;
		this.statusTitle = statusTitle;
		this.color = color;
		this.iconImage = iconImage;
		this.shortLetters = shortLetters;
		this.shortLetterColour = shortLetterColour;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public LocalDate getDate()
	{
		return date;
	}

	public LocalTime getStartTime()
	{
		return startTime;
	}

	public LocalTime getEndTime()
	{
		return endTime;
	}

	public String getName()
	{
		return name;
	}

	public String getNotes()
	{
		return notes;
	}

	public String getReason()
	{
		return reason;
	}

	public Integer getReasonCode()
	{
		return reasonCode;
	}

	public String getLocation()
	{
		return location;
	}

	public String getResources()
	{
		return resources;
	}

	public String getType()
	{
		return type;
	}

	public String getStyle()
	{
		return style;
	}

	public String getBookingSource()
	{
		return bookingSource;
	}

	public String getStatus()
	{
		return status;
	}

	public String getStatusTitle()
	{
		return statusTitle;
	}

	public String getColor()
	{
		return color;
	}

	public String getIconImage()
	{
		return iconImage;
	}

	public String getShortLetterColour()
	{
		return shortLetterColour;
	}

	public String getShortLetters()
	{
		return shortLetters;
	}
}
