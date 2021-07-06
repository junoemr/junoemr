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
import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

import javax.validation.constraints.NotNull;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.oscarehr.common.model.PartialDate.FORMAT_FULL_DATE;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_MONTH;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_ONLY;

@Data
public class PartialDate extends AbstractTransientModel
{
	@NotNull
	private Year year;
	private Month month;
	private Integer day;

	protected PartialDate()
	{
		// javax needs an empty constructor
	}

	public PartialDate(Integer year)
	{
		this(year, null, null);
	}
	public PartialDate(Integer year, Integer month)
	{
		this(year, month, null);
	}
	public PartialDate(Integer year, Integer month, Integer day)
	{
		this((year != null) ? Year.of(year) : null, (month != null) ? Month.of(month) : null, day);
	}
	public PartialDate(Year year, Month month, Integer day)
	{
		this.year = year;
		this.month = month;
		this.day = day;

		if(year == null)
		{
			throw new DateTimeException("Year must be set");
		}

		if(day != null && month == null)
		{
			throw new DateTimeException("Month must be set if day is set");
		}

		if(day != null && day > month.length(year.isLeap()))
		{
			throw new DateTimeException( day + " is not a valid day for the month of " + month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + ", " + year.getValue());
		}
	}

	public boolean isFullDate()
	{
		return this.day != null && this.month != null && this.year != null;
	}
	public boolean isYearMonth()
	{
		return this.day == null && this.month != null && this.year != null;
	}
	public boolean isYearOnly()
	{
		return this.day == null && this.month == null && this.year != null;
	}

	/**
	 * get a localDate representation of the partial date.
	 * if the date is partial, missing day/month values will be set to 01
	 * @return a date representation of the partial date. with default values as necessary
	 */
	public LocalDate toLocalDate()
	{
		if(this.year == null)
		{
			return null;
		}

		int month = (this.month == null) ? 1 : this.month.getValue();
		int day = (this.day == null) ? 1 : this.day;
		return LocalDate.of(this.year.getValue(), month, day);
	}

	public String getFormatString()
	{
		String format = null;
		if(this.isFullDate())
		{
			format = org.oscarehr.common.model.PartialDate.FORMAT_FULL_DATE;
		}
		else if(this.isYearMonth())
		{
			format = org.oscarehr.common.model.PartialDate.FORMAT_YEAR_MONTH;
		}
		else if(this.isYearOnly())
		{
			format = org.oscarehr.common.model.PartialDate.FORMAT_YEAR_ONLY;
		}
		return format;
	}

	public String toISOString()
	{
		if(this.isFullDate())
		{
			return this.year.getValue() + "-" +
					StringUtils.leftPad(String.valueOf(this.month.getValue()), 2, "0") + "-" +
					StringUtils.leftPad(String.valueOf(this.getDay()), 2, "0");
		}
		else if(this.isYearMonth())
		{
			return this.year.getValue() + "-" +
					StringUtils.leftPad(String.valueOf(this.month.getValue()), 2, "0");
		}
		else if(this.isYearOnly())
		{
			return String.valueOf(this.year.getValue());
		}
		return null;
	}

	@Override
	public boolean equals(Object object)
	{
		if(object == this)
		{
			return true;
		}
		if(!(object instanceof PartialDate))
		{
			return false;
		}
		PartialDate partialDate = (PartialDate) object;
		return this.toLocalDate().equals(partialDate.toLocalDate());
	}

	@Override
	public int hashCode()
	{
		LocalDate localDate = this.toLocalDate();
		return (localDate != null) ? localDate.hashCode() : 0;
	}

	public static PartialDate from(LocalDate localDate)
	{
		return from(localDate, null);
	}
	public static PartialDate from(LocalDate localDate, org.oscarehr.common.model.PartialDate dbPartialDate)
	{
		PartialDate partial = null;
		if(localDate != null)
		{
			if(dbPartialDate == null || FORMAT_FULL_DATE.equals(dbPartialDate.getFormat()))
			{
				partial = new PartialDate(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
			}
			else if(FORMAT_YEAR_MONTH.equals(dbPartialDate.getFormat()))
			{
				partial = new PartialDate(localDate.getYear(), localDate.getMonthValue());
			}
			else if(FORMAT_YEAR_ONLY.equals(dbPartialDate.getFormat()))
			{
				partial = new PartialDate(localDate.getYear());
			}
		}
		return partial;
	}

	public static PartialDate parseDate(String isoDateString)
	{
		PartialDate partialDate = null;
		if(isoDateString != null)
		{
			String[] parts = isoDateString.split("-");
			if(parts.length == 3)
			{
				partialDate = new PartialDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
			}
			else if(parts.length == 2)
			{
				partialDate = new PartialDate(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
			}
			else if(parts.length == 1)
			{
				partialDate = new PartialDate(Integer.parseInt(parts[0]));
			}
			else
			{
				throw new IllegalArgumentException(isoDateString + "is not a valid date/partial date format");
			}
		}
		return partialDate;
	}

	public static boolean allFieldsEmpty(PartialDate partialDate)
	{
		if (partialDate.getYear() == null && partialDate.getMonth() == null && partialDate.getDay() == null)
		{
			return true;
		}
		return false;
	}
}
