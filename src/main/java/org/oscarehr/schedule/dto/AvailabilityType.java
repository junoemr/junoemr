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

public class AvailabilityType
{
	private String color;
	private String name;
	private Integer preferredEventLengthMinutes;
	private String systemCode;
	private Boolean systemCodeVisible;

	public AvailabilityType(String color, String name, Integer preferredEventLengthMinutes,
		String systemCode)
	{
		this.color = color;
		this.name = name;
		this.preferredEventLengthMinutes = preferredEventLengthMinutes;
		this.systemCode = systemCode;
		this.systemCodeVisible = false;
	}

	public String getColor()
	{
		return color;
	}

	public String getName()
	{
		return name;
	}

	public Integer getPreferredEventLengthMinutes()
	{
		return preferredEventLengthMinutes;
	}

	public String getSystemCode()
	{
		return systemCode;
	}

	public Boolean getSystemCodeVisible()
	{
		return systemCodeVisible;
	}

	public void setSystemCodeVisible(Boolean systemCodeVisible)
	{
		this.systemCodeVisible = systemCodeVisible;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AvailabilityType that = (AvailabilityType) o;
		return Objects.equals(color, that.color) &&
			Objects.equals(name, that.name) &&
			Objects.equals(preferredEventLengthMinutes, that.preferredEventLengthMinutes) &&
			Objects.equals(systemCode, that.systemCode);
	}

	@Override
	public int hashCode()
	{

		return Objects.hash(color, name, preferredEventLengthMinutes, systemCode);
	}

	@Override
	public String toString()
	{
		return "AvailabilityType{" +
			"color='" + color + '\'' +
			", name='" + name + '\'' +
			", preferredEventLengthMinutes=" + preferredEventLengthMinutes +
			", systemCode='" + systemCode + '\'' +
			'}';
	}
}
