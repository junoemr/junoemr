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
package org.oscarehr.appointment.dto;

import java.util.Objects;

public class CalendarAppointmentStatus
{
	private String color;
	private String displayLetter;
	private String name;
	private boolean rotates;
	private Integer sortOrder;
	private String systemCode;
	private String icon;
	private boolean enabled;

	public CalendarAppointmentStatus(String color, String displayLetter, String name,
		boolean rotates, Integer sortOrder, String systemCode, String icon, boolean enabled)
	{
		this.color = color;
		this.displayLetter = displayLetter;
		this.name = name;
		this.rotates = rotates;
		this.sortOrder = sortOrder;
		this.systemCode = systemCode;
		this.icon = icon;
		this.enabled = enabled;
	}

	public String getColor()
	{
		return color;
	}

	public String getDisplayLetter()
	{
		return displayLetter;
	}

	public String getName()
	{
		return name;
	}

	public boolean isRotates()
	{
		return rotates;
	}

	public Integer getSortOrder()
	{
		return sortOrder;
	}

	public String getSystemCode()
	{
		return systemCode;
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(color, displayLetter, name, rotates, sortOrder, systemCode, icon, enabled);
	}

	public String getIcon()
	{
		return icon;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CalendarAppointmentStatus that = (CalendarAppointmentStatus) o;
		return rotates == that.rotates &&
			Objects.equals(color, that.color) &&
			Objects.equals(displayLetter, that.displayLetter) &&
			Objects.equals(name, that.name) &&
			Objects.equals(sortOrder, that.sortOrder) &&
			Objects.equals(systemCode, that.systemCode) &&
			Objects.equals(icon, that.icon) &&
			Objects.equals(enabled, that.enabled);
	}

	@Override
	public String toString()
	{
		return "CalendarAppointmentStatus{" +
			"color='" + color + '\'' +
			", displayLetter='" + displayLetter + '\'' +
			", name='" + name + '\'' +
			", rotates=" + rotates +
			", sortOrder=" + sortOrder +
			", systemCode='" + systemCode + '\'' +
			", icon='" + icon + '\'' +
			'}';
	}

	public boolean isEnabled()
	{
		return enabled;
	}
}
