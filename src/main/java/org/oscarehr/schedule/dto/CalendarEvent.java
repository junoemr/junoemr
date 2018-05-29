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
import java.util.Objects;

public class CalendarEvent
{
	private LocalDateTime start;
	private LocalDateTime end;
	private String color;
	private String className;
	private Integer resourceId;
	private String scheduleTemplateCode;
	private AvailabilityType availabilityType;
	private CalendarAppointment data;

	public CalendarEvent(LocalDateTime start, LocalDateTime end, String color,
		String className, Integer resourceId, String scheduleTemplateCode,
		AvailabilityType availabilityType, CalendarAppointment data)
	{
		this.start = start;
		this.end = end;
		this.color = color;
		this.className = className;
		this.resourceId = resourceId;
		this.scheduleTemplateCode = scheduleTemplateCode;
		this.availabilityType = availabilityType;
		this.data = data;
	}

	public LocalDateTime getStart()
	{
		return start;
	}

	public LocalDateTime getEnd()
	{
		return end;
	}

	public String getColor()
	{
		return color;
	}

	public String getClassName()
	{
		return className;
	}

	public Integer getResourceId()
	{
		return resourceId;
	}

	public String getScheduleTemplateCode()
	{
		return scheduleTemplateCode;
	}

	public AvailabilityType getAvailabilityType()
	{
		return availabilityType;
	}

	public CalendarAppointment getData()
	{
		return data;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalendarEvent that = (CalendarEvent) o;
		return Objects.equals(start, that.start) &&
			Objects.equals(end, that.end) &&
			Objects.equals(color, that.color) &&
			Objects.equals(className, that.className) &&
			Objects.equals(resourceId, that.resourceId) &&
			Objects.equals(scheduleTemplateCode, that.scheduleTemplateCode) &&
			Objects.equals(availabilityType, that.availabilityType) &&
			Objects.equals(data, that.data);
	}

	@Override
	public int hashCode()
	{

		return Objects
			.hash(start, end, color, className, resourceId, scheduleTemplateCode, availabilityType,
				data);
	}

	@Override
	public String toString()
	{
		return "CalendarEvent{" +
			"start=" + start +
			", end=" + end +
			", color='" + color + '\'' +
			", className='" + className + '\'' +
			", resourceId=" + resourceId +
			", scheduleTemplateCode='" + scheduleTemplateCode + '\'' +
			", availabilityType=" + availabilityType +
			", data=" + data +
			'}';
	}
}
