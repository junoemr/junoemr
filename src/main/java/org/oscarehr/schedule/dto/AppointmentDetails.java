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

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.common.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDetails
{

	private Integer appointmentNo;
	private Integer demographicNo;
	private LocalDate date;
	private LocalTime startTime;
	private LocalTime endTime;
	private String name;
	private String notes;
	private String reason;
	private Integer reasonCode;
	private String location;
	private String resources;
	private String type;
	private String style;
	private String bookingSource;
	private String status;
	private String urgency;
	private String statusTitle;
	private String color;
	private String junoColor;
	private String iconImage;
	private Integer shortLetterColour;
	private String shortLetters;
	private String firstName;
	private String lastName;
	private String ver;
	private String hin;
	private String chart_no;
	private String familyDoctor;
	private String rosterStatus;
	private LocalDate hcRenewDate;
	private String custNotes;
	private String custAlert;
	private String colorProperty;
	private LocalDate birthday;
	private boolean hasTicklers;
	private String ticklerMessages;
	private boolean isVirtual;
	private boolean isConfirmed;
	private Integer creatorSecurityId;
	@Getter
	@Setter
	private Appointment.VirtualAppointmentType virtualAppointmentType;

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
		String urgency,
		String statusTitle,
		String color,
		String junoColor,
		String iconImage,
		Integer shortLetterColour,
		String shortLetters,
		String firstName,
		String lastName,
		String ver,
		String hin,
		String chart_no,
		String familyDoctor,
		String rosterStatus,
		LocalDate hcRenewDate,
		String custNotes,
		String custAlert,
		String colorProperty,
		LocalDate birthday,
		boolean hasTicklers,
		String ticklerMessages,
		boolean isVirtual,
		boolean isConfirmed,
		Integer creatorSecurityId,
		Appointment.VirtualAppointmentType virtualAppointmentType
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
		this.creatorSecurityId = creatorSecurityId;
		this.status = status;
		this.urgency = urgency;
		this.statusTitle = statusTitle;
		this.color = color;
		this.junoColor = junoColor;
		this.iconImage = iconImage;
		this.shortLetters = shortLetters;
		this.shortLetterColour = shortLetterColour;
		this.firstName = firstName;
		this.lastName = lastName;
		this.ver = ver;
		this.hin = hin;
		this.chart_no = chart_no;
		this.familyDoctor = familyDoctor;
		this.rosterStatus = rosterStatus;
		this.hcRenewDate = hcRenewDate;
		this.custNotes = custNotes;
		this.custAlert = custAlert;
		this.colorProperty = colorProperty;
		this.birthday = birthday;
		this.hasTicklers = hasTicklers;
		this.ticklerMessages = ticklerMessages;
		this.isVirtual = isVirtual;
		this.isConfirmed = isConfirmed;
		this.virtualAppointmentType = virtualAppointmentType;
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

	public String getUrgency()
	{
		return urgency;
	}

	public String getStatusTitle()
	{
		return statusTitle;
	}

	public String getColor()
	{
		return color;
	}

	public String getJunoColor()
	{
		return junoColor;
	}

	public void setJunoColor(String junoColor)
	{
		this.junoColor = junoColor;
	}

	public String getIconImage()
	{
		return iconImage;
	}

	public String getShortLetterColour()
	{
		return shortLetterColour.toString();
	}

	public String getShortLetters()
	{
		return shortLetters;
	}

	public String getFirstName()
	{
		return firstName;
	}

	public String getLastName()
	{
		return lastName;
	}

	public String getVer()
	{
		return ver;
	}

	public String getRosterStatus()
	{
		return rosterStatus;
	}

	public LocalDate getHcRenewDate()
	{
		return hcRenewDate;
	}

	public String getCustNotes()
	{
		return custNotes;
	}

	public String getCustAlert()
	{
		return custAlert;
	}

	public String getColorProperty()
	{
		return colorProperty;
	}

	public LocalDate getBirthday()
	{
		return birthday;
	}

	public boolean hasTicklers()
	{
		return hasTicklers;
	}
	public boolean isVirtual()
	{
		return isVirtual;
	}

	public String getTicklerMessages()
	{
		return ticklerMessages;
	}

	public String getHin()
	{
		return hin;
	}

	public String getChart_no()
	{
		return chart_no;
	}

	public String getFamilyDoctor()
	{
		return familyDoctor;
	}

	public boolean isConfirmed()
	{
		return isConfirmed;
	}

	public Integer getCreatorSecurityId()
	{
		return creatorSecurityId;
	}

	public void setCreatorSecurityId(Integer creatorSecurityId)
	{
		this.creatorSecurityId = creatorSecurityId;
	}
}
