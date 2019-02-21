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

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CalendarAppointment
{
	private Integer appointmentNo;
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
	private String notes;
	private String tagNames;
	private String site;
	private boolean tagSelfBooked;
	private boolean tagSelfCancelled;
	private String tagSystemCodes;

	public CalendarAppointment(Integer appointmentNo, LocalDate demographicDob,
		String demographicName, String demographicPhone,
		Integer demographicNo, Integer providerNo,
		LocalDateTime startTime, LocalDateTime endTime, String eventStatusCode,
		String eventStatusModifier, Integer numInvoices, String reason, String notes, String tagNames,
		String site, boolean tagSelfBooked, boolean tagSelfCancelled, String tagSystemCodes)
	{
		this.appointmentNo = appointmentNo;
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
		this.notes = notes;
		this.tagNames = tagNames;
		this.site = site;
		this.tagSelfBooked = tagSelfBooked;
		this.tagSelfCancelled = tagSelfCancelled;
		this.tagSystemCodes = tagSystemCodes;
	}

	public Integer getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(Integer appointmentNo)
	{
		this.appointmentNo = appointmentNo;
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

	public String getTagSystemCodes()
	{
		return tagSystemCodes;
	}

	public void setTagSystemCodes(String tagSystemCodes)
	{
		this.tagSystemCodes = tagSystemCodes;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CalendarAppointment that = (CalendarAppointment) o;

		if (tagSelfBooked != that.tagSelfBooked) return false;
		if (tagSelfCancelled != that.tagSelfCancelled) return false;
		if (appointmentNo != null ? !appointmentNo.equals(that.appointmentNo) : that.appointmentNo != null)
			return false;
		if (demographicDob != null ? !demographicDob.equals(that.demographicDob) : that.demographicDob != null)
			return false;
		if (demographicName != null ? !demographicName.equals(that.demographicName) : that.demographicName != null)
			return false;
		if (demographicPhone != null ? !demographicPhone.equals(that.demographicPhone) : that.demographicPhone != null)
			return false;
		if (demographicNo != null ? !demographicNo.equals(that.demographicNo) : that.demographicNo != null)
			return false;
		if (providerNo != null ? !providerNo.equals(that.providerNo) : that.providerNo != null)
			return false;
		if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null)
			return false;
		if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
		if (eventStatusCode != null ? !eventStatusCode.equals(that.eventStatusCode) : that.eventStatusCode != null)
			return false;
		if (eventStatusModifier != null ? !eventStatusModifier.equals(that.eventStatusModifier) : that.eventStatusModifier != null)
			return false;
		if (numInvoices != null ? !numInvoices.equals(that.numInvoices) : that.numInvoices != null)
			return false;
		if (reason != null ? !reason.equals(that.reason) : that.reason != null) return false;
		if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
		if (tagNames != null ? !tagNames.equals(that.tagNames) : that.tagNames != null)
			return false;
		if (site != null ? !site.equals(that.site) : that.site != null) return false;
		return tagSystemCodes != null ? tagSystemCodes.equals(that.tagSystemCodes) : that.tagSystemCodes == null;
	}

	@Override
	public int hashCode()
	{
		int result = appointmentNo != null ? appointmentNo.hashCode() : 0;
		result = 31 * result + (demographicDob != null ? demographicDob.hashCode() : 0);
		result = 31 * result + (demographicName != null ? demographicName.hashCode() : 0);
		result = 31 * result + (demographicPhone != null ? demographicPhone.hashCode() : 0);
		result = 31 * result + (demographicNo != null ? demographicNo.hashCode() : 0);
		result = 31 * result + (providerNo != null ? providerNo.hashCode() : 0);
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
		result = 31 * result + (eventStatusCode != null ? eventStatusCode.hashCode() : 0);
		result = 31 * result + (eventStatusModifier != null ? eventStatusModifier.hashCode() : 0);
		result = 31 * result + (numInvoices != null ? numInvoices.hashCode() : 0);
		result = 31 * result + (reason != null ? reason.hashCode() : 0);
		result = 31 * result + (notes != null ? notes.hashCode() : 0);
		result = 31 * result + (tagNames != null ? tagNames.hashCode() : 0);
		result = 31 * result + (site != null ? site.hashCode() : 0);
		result = 31 * result + (tagSelfBooked ? 1 : 0);
		result = 31 * result + (tagSelfCancelled ? 1 : 0);
		result = 31 * result + (tagSystemCodes != null ? tagSystemCodes.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return "CalendarAppointment{" +
				"appointmentNo=" + appointmentNo +
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
				", tagSelfBooked=" + tagSelfBooked +
				", tagSelfCancelled=" + tagSelfCancelled +
				", tagSystemCodes='" + tagSystemCodes + '\'' +
				'}';
	}
}
