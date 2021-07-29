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
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
public class CalendarAppointment implements Serializable
{
	private Integer appointmentNo;
	private String billingRegion;
	private String billingForm;
	private String billingRdohip;
	private String userProviderNo;
	private String userFirstName;
	private String userLastName;
	private LocalDate demographicDob;
	private String demographicName;
	private String demographicPhone;
	private Integer demographicNo;
	private Integer providerNo;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String eventStatusCode;
	private String eventStatusModifier;
	private Integer numInvoices;
	private String reason;
	private Integer reasonCode;
	private String notes;
	private String tagNames;
	private String site;
	private String type;
	private String resources;
	private String urgency;
	private boolean doNotBook;
	private boolean critical;
	private boolean tagSelfBooked;
	private boolean tagSelfCancelled;
	private boolean virtual;
	private boolean sendNotification;
	private String tagSystemCodes;
	private String appointmentName;
	private boolean isConfirmed;
	private Integer creatorSecurityId;
	private String bookingSource;

	public CalendarAppointment()
	{}

	public CalendarAppointment(Integer appointmentNo, String billingRegion, String billingForm,
	                           String billingRdohip, String userProviderNo, String userFirstName,
	                           String userLastName, LocalDate demographicDob, String demographicName,
	                           String demographicPhone, Integer demographicNo, Integer providerNo,
	                           LocalDateTime startTime, LocalDateTime endTime, String eventStatusCode,
	                           String eventStatusModifier, Integer numInvoices, String reason, Integer reasonCode,
	                           String notes, String tagNames, String site, String type,
	                           String resources, String urgency, boolean doNotBook, boolean tagSelfBooked,
	                           boolean tagSelfCancelled, boolean virtual, String tagSystemCodes, boolean isConfirmed,
	                           Integer creatorSecurityId,  String bookingSource, boolean critical)
	{
		this.appointmentNo = appointmentNo;
		this.billingRegion = billingRegion;
		this.billingForm = billingForm;
		this.billingRdohip = billingRdohip;
		this.userProviderNo = userProviderNo;
		this.userFirstName = userFirstName;
		this.userLastName = userLastName;
		this.demographicDob = demographicDob;
		this.demographicName = demographicName;
		this.demographicPhone = demographicPhone;
		this.demographicNo = demographicNo;
		this.providerNo = providerNo;
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventStatusCode = eventStatusCode;
		this.eventStatusModifier = eventStatusModifier;
		this.numInvoices = numInvoices;
		this.reason = reason;
		this.reasonCode = reasonCode;
		this.notes = notes;
		this.tagNames = tagNames;
		this.site = site;
		this.type = type;
		this.resources = resources;
		this.urgency = urgency;
		this.doNotBook = doNotBook;
		this.tagSelfBooked = tagSelfBooked;
		this.tagSelfCancelled = tagSelfCancelled;
		this.tagSystemCodes = tagSystemCodes;
		this.virtual = virtual;
		this.isConfirmed = isConfirmed;
		this.creatorSecurityId = creatorSecurityId;
		this.bookingSource = bookingSource;
		this.critical = critical;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof CalendarAppointment)) return false;
		CalendarAppointment that = (CalendarAppointment) o;
		return tagSelfBooked == that.tagSelfBooked &&
				tagSelfCancelled == that.tagSelfCancelled &&
				virtual == that.virtual &&
				critical == that.critical &&
				Objects.equals(appointmentNo, that.appointmentNo) &&
				Objects.equals(billingRegion, that.billingRegion) &&
				Objects.equals(billingForm, that.billingForm) &&
				Objects.equals(billingRdohip, that.billingRdohip) &&
				Objects.equals(userProviderNo, that.userProviderNo) &&
				Objects.equals(userFirstName, that.userFirstName) &&
				Objects.equals(userLastName, that.userLastName) &&
				Objects.equals(demographicDob, that.demographicDob) &&
				Objects.equals(demographicName, that.demographicName) &&
				Objects.equals(demographicPhone, that.demographicPhone) &&
				Objects.equals(demographicNo, that.demographicNo) &&
				Objects.equals(providerNo, that.providerNo) &&
				Objects.equals(startTime, that.startTime) &&
				Objects.equals(endTime, that.endTime) &&
				Objects.equals(eventStatusCode, that.eventStatusCode) &&
				Objects.equals(eventStatusModifier, that.eventStatusModifier) &&
				Objects.equals(numInvoices, that.numInvoices) &&
				Objects.equals(reason, that.reason) &&
				Objects.equals(notes, that.notes) &&
				Objects.equals(tagNames, that.tagNames) &&
				Objects.equals(site, that.site) &&
				Objects.equals(type, that.type) &&
				Objects.equals(resources, that.resources) &&
				Objects.equals(urgency, that.urgency) &&
				Objects.equals(tagSystemCodes, that.tagSystemCodes) &&
				Objects.equals(isConfirmed, that.isConfirmed) &&
				Objects.equals(bookingSource, that.bookingSource) &&
				Objects.equals(creatorSecurityId, that.creatorSecurityId);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(appointmentNo, billingRegion, billingForm, billingRdohip, userProviderNo, userFirstName, userLastName, demographicDob, demographicName, demographicPhone, demographicNo, providerNo, startTime, endTime, eventStatusCode, eventStatusModifier, numInvoices, reason, notes, tagNames, site, type, resources, urgency, tagSelfBooked, tagSelfCancelled, tagSystemCodes, bookingSource, creatorSecurityId, critical);
	}
}
