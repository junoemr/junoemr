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
	public static final String RENDERING_BACKGROUND = "background";

	/* required by Full Calendar */
	private LocalDateTime start;
	private LocalDateTime end;
	private String color;
	private String rendering;
	private String className;
	private Integer resourceId;

	/* application specific */
	private String scheduleTemplateCode; //TODO-legacy remove? this code exists in availability data
	private AvailabilityType availabilityType; // contains all schedule slot data
	private CalendarAppointment data; // contains appointment data

	public CalendarEvent(LocalDateTime start, LocalDateTime end, String color, String rendering, String className,
		Integer resourceId, String scheduleTemplateCode,
		AvailabilityType availabilityType, CalendarAppointment data)
	{
		this.start = start;
		this.end = end;
		this.color = color;
		this.rendering = rendering;
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

	public String getRendering()
	{
		return rendering;
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
		return Objects.equals(RENDERING_BACKGROUND, that.RENDERING_BACKGROUND) &&
			Objects.equals(start, that.start) &&
			Objects.equals(end, that.end) &&
			Objects.equals(color, that.color) &&
			Objects.equals(rendering, that.rendering) &&
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
			.hash(RENDERING_BACKGROUND, start, end, color, rendering, className, resourceId,
				scheduleTemplateCode, availabilityType, data);
	}

	@Override
	public String toString()
	{
		return "CalendarEvent{" +
			"RENDERING_BACKGROUND='" + RENDERING_BACKGROUND + '\'' +
			", start='" + start + '\'' +
			", end='" + end + '\'' +
			", color='" + color + '\'' +
			", rendering='" + rendering + '\'' +
			", className='" + className + '\'' +
			", resourceId=" + resourceId +
			", scheduleTemplateCode='" + scheduleTemplateCode + '\'' +
			", availabilityType=" + availabilityType +
			", data=" + data +
			'}';
	}
}
