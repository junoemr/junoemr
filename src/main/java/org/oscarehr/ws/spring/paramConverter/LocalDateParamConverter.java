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
package org.oscarehr.ws.spring.paramConverter;

import oscar.util.ConversionUtils;

import javax.ws.rs.ext.ParamConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * for serializing/deserializing LocalDate object as a @QueryParam
 * maybe this can be removed with a newer version of something
 */
public class LocalDateParamConverter implements ParamConverter<LocalDate>
{
	public LocalDate fromString(String value)
	{
		return ConversionUtils.toLocalDate(value, DateTimeFormatter.ofPattern(ConversionUtils.DEFAULT_DATE_PATTERN));
	}

	public String toString(LocalDate value)
	{
		return ConversionUtils.toDateString(value, ConversionUtils.DEFAULT_DATE_PATTERN);
	}
}
