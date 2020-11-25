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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.DateTimeFullOrPartial;
import org.oscarehr.demographicImport.mapper.AbstractImportMapper;
import oscar.util.ConversionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class AbstractCDSImportMapper<I, E> extends AbstractImportMapper<I, E>
{
	public AbstractCDSImportMapper()
	{
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	protected LocalDateTime toNullableLocalDateTime(DateTimeFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			return fillPartialCalendar(
					fullOrPartial.getFullDateTime(),
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
		}
		return null;
	}

	protected LocalDateTime toNullableLocalDateTime(DateFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			return fillPartialCalendar(
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
		}
		return null;
	}

	protected LocalDate toNullableLocalDate(DateFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			LocalDateTime dateTime = fillPartialCalendar(
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
			return dateTime.toLocalDate();
		}
		return null;
	}

	private LocalDateTime fillPartialCalendar(
			XMLGregorianCalendar fullDateTime,
			XMLGregorianCalendar fullDate,
			XMLGregorianCalendar yearMonth,
			XMLGregorianCalendar yearOnly)
	{
		if(fullDateTime != null)
		{
			return ConversionUtils.toNullableLocalDateTime(fullDateTime);
		}
		else
		{
			return fillPartialCalendar(fullDate, yearMonth, yearOnly);
		}
	}

	private LocalDateTime fillPartialCalendar(
			XMLGregorianCalendar fullDate,
			XMLGregorianCalendar yearMonth,
			XMLGregorianCalendar yearOnly)
	{
		XMLGregorianCalendar xmlGregorianCalendar = null;
		if(fullDate != null)
		{
			xmlGregorianCalendar = fullDate;
		}
		else if (yearMonth != null)
		{
			xmlGregorianCalendar = yearMonth;
			xmlGregorianCalendar.setDay(1);
		}
		else if(yearOnly != null)
		{
			xmlGregorianCalendar = yearOnly;
			xmlGregorianCalendar.setMonth(1);
			xmlGregorianCalendar.setDay(1);
		}

		if(xmlGregorianCalendar != null)
		{
			xmlGregorianCalendar.setHour(0);
			xmlGregorianCalendar.setMinute(0);
			xmlGregorianCalendar.setSecond(0);
		}
		return ConversionUtils.toNullableLocalDateTime(xmlGregorianCalendar);
	}
}
