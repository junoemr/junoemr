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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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
	private boolean tagSelfBooked;
	private boolean tagSelfCancelled;
	private boolean virtual;
	private String tagSystemCodes;
	private String appointmentName;

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
	                           boolean tagSelfCancelled, boolean virtual, String tagSystemCodes)
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
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public String getBillingRegion()
	{
		return billingRegion;
	}

	public void setBillingRegion(String billingRegion)
	{
		this.billingRegion = billingRegion;
	}

	public String getBillingForm()
	{
		return billingForm;
	}

	public void setBillingForm(String billingForm)
	{
		this.billingForm = billingForm;
	}

	public String getBillingRdohip()
	{
		return billingRdohip;
	}

	public String getUserProviderNo()
	{
		return userProviderNo;
	}

	public void setUserProviderNo(String userProviderNo)
	{
		this.userProviderNo = userProviderNo;
	}

	public String getUserFirstName()
	{
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	public String getUserLastName()
	{
		return userLastName;
	}

	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}

	public void setBillingRdohip(String billingRdohip)
	{
		this.billingRdohip = billingRdohip;
	}

	public LocalDate getDemographicDob()
	{
		return demographicDob;
	}

	public void setDemographicDob(LocalDate demographicDob)
	{
		this.demographicDob = demographicDob;
	}

	public String getDemographicName()
	{
		return demographicName;
	}

	public void setDemographicName(String demographicName)
	{
		this.demographicName = demographicName;
	}

	public String getDemographicPhone()
	{
		return demographicPhone;
	}

	public void setDemographicPhone(String demographicPhone)
	{
		this.demographicPhone = demographicPhone;
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

	public LocalDateTime getStartTime()
	{
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime)
	{
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime()
	{
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime)
	{
		this.endTime = endTime;
	}

	public String getEventStatusCode()
	{
		return eventStatusCode;
	}

	public void setEventStatusCode(String eventStatusCode)
	{
		this.eventStatusCode = eventStatusCode;
	}

	public String getEventStatusModifier()
	{
		return eventStatusModifier;
	}

	public void setEventStatusModifier(String eventStatusModifier)
	{
		this.eventStatusModifier = eventStatusModifier;
	}

	public Integer getNumInvoices()
	{
		return numInvoices;
	}

	public void setNumInvoices(Integer numInvoices)
	{
		this.numInvoices = numInvoices;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public Integer getReasonCode()
	{
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode)
	{
		this.reasonCode = reasonCode;
	}

	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public String getTagNames()
	{
		return tagNames;
	}

	public void setTagNames(String tagNames)
	{
		this.tagNames = tagNames;
	}

	public String getSite()
	{
		return site;
	}

	public void setSite(String site)
	{
		this.site = site;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getResources()
	{
		return resources;
	}

	public void setResources(String resources)
	{
		this.resources = resources;
	}

	public String getUrgency()
	{
		return urgency;
	}

	public void setUrgency(String urgency)
	{
		this.urgency = urgency;
	}

	public boolean isDoNotBook()
	{
		return doNotBook;
	}

	public void setDoNotBook(boolean doNotBook)
	{
		this.doNotBook = doNotBook;
	}

	public boolean isTagSelfBooked()
	{
		return tagSelfBooked;
	}

	public void setTagSelfBooked(boolean tagSelfBooked)
	{
		this.tagSelfBooked = tagSelfBooked;
	}

	public boolean isTagSelfCancelled()
	{
		return tagSelfCancelled;
	}

	public void setTagSelfCancelled(boolean tagSelfCancelled)
	{
		this.tagSelfCancelled = tagSelfCancelled;
	}

	public boolean isVirtual()
	{
		return virtual;
	}

	public void setIsVirtual(boolean virtual)
	{
		this.virtual = virtual;
	}

	public String getTagSystemCodes()
	{
		return tagSystemCodes;
	}

	public void setTagSystemCodes(String tagSystemCodes)
	{
		this.tagSystemCodes = tagSystemCodes;
	}

	public String getAppointmentName()
	{
		return appointmentName;
	}

	public void setAppointmentName(String appointmentName)
	{
		this.appointmentName = appointmentName;
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
				Objects.equals(tagSystemCodes, that.tagSystemCodes);
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(appointmentNo, billingRegion, billingForm, billingRdohip, userProviderNo, userFirstName, userLastName, demographicDob, demographicName, demographicPhone, demographicNo, providerNo, startTime, endTime, eventStatusCode, eventStatusModifier, numInvoices, reason, notes, tagNames, site, type, resources, urgency, tagSelfBooked, tagSelfCancelled, tagSystemCodes);
	}

	@Override
	public String toString()
	{
		return "CalendarAppointment{" +
				"appointmentNo=" + appointmentNo +
				", billingRegion='" + billingRegion + '\'' +
				", billingForm='" + billingForm + '\'' +
				", billingRdohip='" + billingRdohip + '\'' +
				", userProviderNo='" + userProviderNo + '\'' +
				", userFirstName='" + userFirstName + '\'' +
				", userLastName='" + userLastName + '\'' +
				", demographicDob=" + demographicDob +
				", demographicName='" + demographicName + '\'' +
				", demographicPhone='" + demographicPhone + '\'' +
				", demographicNo=" + demographicNo +
				", providerNo=" + providerNo +
				", startTime=" + startTime +
				", endTime=" + endTime +
				", eventStatusCode='" + eventStatusCode + '\'' +
				", eventStatusModifier='" + eventStatusModifier + '\'' +
				", numInvoices=" + numInvoices +
				", reason='" + reason + '\'' +
				", notes='" + notes + '\'' +
				", tagNames='" + tagNames + '\'' +
				", site='" + site + '\'' +
				", type='" + type + '\'' +
				", resources='" + resources + '\'' +
				", urgency='" + urgency + '\'' +
				", tagSelfBooked=" + tagSelfBooked +
				", tagSelfCancelled=" + tagSelfCancelled +
				", isVirtual=" + virtual +
				", tagSystemCodes='" + tagSystemCodes + '\'' +
				'}';
	}
}
