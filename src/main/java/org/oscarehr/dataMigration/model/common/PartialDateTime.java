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
package org.oscarehr.dataMigration.model.common;

import lombok.Data;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;

@Data
public class PartialDateTime extends PartialDate
{
	protected LocalTime localTime;

	public PartialDateTime(Integer year)
	{
		this(year, null, null);
	}
	public PartialDateTime(Integer year, Integer month)
	{
		this(year, month, null);
	}
	public PartialDateTime(Integer year, Integer month, Integer day)
	{
		this(year, month, day, (LocalTime) null);
	}
	public PartialDateTime(Year year, Month month, Integer day)
	{
		this(year, month, day, null);
	}
	public PartialDateTime(Integer year, Integer month, Integer day, Integer hours)
	{
		this(year, month, day, hours, 0);
	}
	public PartialDateTime(Integer year, Integer month, Integer day, Integer hours, Integer minutes)
	{
		this(year, month, day, LocalTime.of(hours, minutes));
	}
	public PartialDateTime(Integer year, Integer month, Integer day, Integer hours, Integer minutes, Integer seconds)
	{
		this(year, month, day, LocalTime.of(hours, minutes, seconds));
	}
	public PartialDateTime(Integer year, Integer month, Integer day, LocalTime time)
	{
		super(year, month, day);
		this.localTime = time;
	}
	public PartialDateTime(Year year, Month month, Integer day, LocalTime time)
	{
		super(year, month, day);
		this.localTime = time;
	}
	public PartialDateTime(PartialDate partialDate, LocalTime time)
	{
		super(partialDate.getYear(), partialDate.getMonth(), partialDate.getDay());
		this.localTime = time;
	}

	public boolean isFullDateTime()
	{
		return this.isFullDate() && this.localTime != null;
	}

	/**
	 * get a localDateTime representation of the partial date.
	 * if the date is partial, missing day/month values will be set to 01
	 * @return a date representation of the partial date. with default values as necessary
	 */
	public LocalDateTime toLocalDateTime()
	{
		LocalDate localDate = super.toLocalDate();
		LocalTime localTime = (this.localTime != null) ? this.localTime : LocalTime.MIN;
		return LocalDateTime.of(localDate, localTime);
	}

	@Override
	public String toISOString()
	{
		String datePart = super.toISOString();

		if(this.isFullDateTime())
		{
			return datePart + "T" + ConversionUtils.toTimeString(this.localTime);
		}
		return datePart;
	}

	@Override
	public boolean equals(Object object)
	{
		if(object == this)
		{
			return true;
		}
		if(!(object instanceof PartialDateTime))
		{
			return false;
		}
		PartialDateTime partialDate = (PartialDateTime) object;
		return this.toLocalDateTime().equals(partialDate.toLocalDateTime());
	}

	@Override
	public int hashCode()
	{
		LocalDateTime localDateTime = this.toLocalDateTime();
		return (localDateTime != null) ? localDateTime.hashCode() : 0;
	}

	public static PartialDateTime from(PartialDate partialDate)
	{
		if(partialDate != null)
		{
			return new PartialDateTime(partialDate.getYear(), partialDate.getMonth(), partialDate.getDay(), LocalTime.MIN);
		}
		return null;
	}
	public static PartialDateTime from(LocalDate localDate)
	{
		if(localDate != null)
		{
			return from(LocalDateTime.of(localDate, LocalTime.MIN));
		}
		return null;
	}
	public static PartialDateTime from(LocalDateTime localDateTime)
	{
		return from(localDateTime, null);
	}
	public static PartialDateTime from(LocalDateTime localDateTime, org.oscarehr.common.model.PartialDate dbPartialDate)
	{
		PartialDateTime partialDateTime = null;
		if(localDateTime != null)
		{
			PartialDate partialDate = PartialDate.from(localDateTime.toLocalDate(), dbPartialDate);
			partialDateTime = new PartialDateTime(partialDate, localDateTime.toLocalTime());
		}
		return partialDateTime;
	}
}
