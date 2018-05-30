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

import java.util.Objects;

public class CalendarAppointment
{
	private Integer scheduleUuid;
	private String demographicPatientDob; // TODO change to LocalDate
	private String demographicPatientName;
	private String demographicPatientPhone;
	private Integer demographicPatientUuid;
	private Integer demographicPractitionerUuid;
	private String startTime; // TODO change to LocalDateTime
	private String endTime; // TODO change to LocalDateTime
	private String eventStatusUuid;
	private Integer numInvoices;
	private String reason;
	private String tagNames;
	private boolean tagSelfBooked;
	private boolean tagSelfCancelled;
	private String tagSystemCodes;

	public CalendarAppointment(Integer scheduleUuid, String demographicPatientDob,
		String demographicPatientName, String demographicPatientPhone,
		Integer demographicPatientUuid, Integer demographicPractitionerUuid, String startTime,
		String endTime, String eventStatusUuid, Integer numInvoices, String reason,
		String tagNames, boolean tagSelfBooked, boolean tagSelfCancelled,
		String tagSystemCodes)
	{
		this.scheduleUuid = scheduleUuid;
		this.demographicPatientDob = demographicPatientDob;
		this.demographicPatientName = demographicPatientName;
		this.demographicPatientPhone = demographicPatientPhone;
		this.demographicPatientUuid = demographicPatientUuid;
		this.demographicPractitionerUuid = demographicPractitionerUuid;
		this.startTime = startTime;
		this.endTime = endTime;
		this.eventStatusUuid = eventStatusUuid;
		this.numInvoices = numInvoices;
		this.reason = reason;
		this.tagNames = tagNames;
		this.tagSelfBooked = tagSelfBooked;
		this.tagSelfCancelled = tagSelfCancelled;
		this.tagSystemCodes = tagSystemCodes;
	}

	public Integer getScheduleUuid()
	{
		return scheduleUuid;
	}

	public String getDemographicPatientDob()
	{
		return demographicPatientDob;
	}

	public String getDemographicPatientName()
	{
		return demographicPatientName;
	}

	public String getDemographicPatientPhone()
	{
		return demographicPatientPhone;
	}

	public Integer getDemographicPatientUuid()
	{
		return demographicPatientUuid;
	}

	public Integer getDemographicPractitionerUuid()
	{
		return demographicPractitionerUuid;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public String getEndTime()
	{
		return endTime;
	}

	public String getEventStatusUuid()
	{
		return eventStatusUuid;
	}

	public Integer getNumInvoices()
	{
		return numInvoices;
	}

	public String getReason()
	{
		return reason;
	}

	public String getTagNames()
	{
		return tagNames;
	}

	public boolean isTagSelfBooked()
	{
		return tagSelfBooked;
	}

	public boolean isTagSelfCancelled()
	{
		return tagSelfCancelled;
	}

	public String getTagSystemCodes()
	{
		return tagSystemCodes;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalendarAppointment that = (CalendarAppointment) o;
		return tagSelfBooked == that.tagSelfBooked &&
			tagSelfCancelled == that.tagSelfCancelled &&
			Objects.equals(scheduleUuid, that.scheduleUuid) &&
			Objects.equals(demographicPatientDob, that.demographicPatientDob) &&
			Objects.equals(demographicPatientName, that.demographicPatientName) &&
			Objects.equals(demographicPatientPhone, that.demographicPatientPhone) &&
			Objects.equals(demographicPatientUuid, that.demographicPatientUuid) &&
			Objects.equals(demographicPractitionerUuid, that.demographicPractitionerUuid) &&
			Objects.equals(startTime, that.startTime) &&
			Objects.equals(endTime, that.endTime) &&
			Objects.equals(eventStatusUuid, that.eventStatusUuid) &&
			Objects.equals(numInvoices, that.numInvoices) &&
			Objects.equals(reason, that.reason) &&
			Objects.equals(tagNames, that.tagNames) &&
			Objects.equals(tagSystemCodes, that.tagSystemCodes);
	}

	@Override
	public int hashCode()
	{

		return Objects
			.hash(scheduleUuid, demographicPatientDob, demographicPatientName,
				demographicPatientPhone,
				demographicPatientUuid, demographicPractitionerUuid, startTime, endTime,
				eventStatusUuid, numInvoices, reason, tagNames, tagSelfBooked, tagSelfCancelled,
				tagSystemCodes);
	}

	@Override
	public String toString()
	{
		return "CalendarAppointment{" +
			"scheduleUuid=" + scheduleUuid +
			", demographicPatientDob=" + demographicPatientDob +
			", demographicPatientName='" + demographicPatientName + '\'' +
			", demographicPatientPhone='" + demographicPatientPhone + '\'' +
			", demographicPatientUuid=" + demographicPatientUuid +
			", demographicPractitionerUuid=" + demographicPractitionerUuid +
			", startTime=" + startTime +
			", endTime=" + endTime +
			", eventStatusUuid='" + eventStatusUuid + '\'' +
			", numInvoices=" + numInvoices +
			", reason='" + reason + '\'' +
			", tagNames='" + tagNames + '\'' +
			", tagSelfBooked=" + tagSelfBooked +
			", tagSelfCancelled=" + tagSelfCancelled +
			", tagSystemCodes='" + tagSystemCodes + '\'' +
			'}';
	}
}
