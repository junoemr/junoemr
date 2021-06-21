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

import org.junit.Test;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.oscarehr.common.model.PartialDate.FORMAT_FULL_DATE;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_MONTH;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_ONLY;

public class PartialDateTest
{
	@Test
	public void testPartialDateTimeConstructor_Year()
	{
		PartialDate partialDate = new PartialDate(2021);
		assertEquals(2021, partialDate.getYear().getValue());
	}
	@Test
	public void testPartialDateTimeConstructor_YearMonth()
	{
		PartialDate partialDate = new PartialDate(2021, 6);
		assertEquals(2021, partialDate.getYear().getValue());
		assertEquals(6, partialDate.getMonth().getValue());
	}
	@Test
	public void testPartialDateTimeConstructor_YearMonthDay()
	{
		PartialDate partialDate = new PartialDate(2021, 6, 24);
		assertEquals(2021, partialDate.getYear().getValue());
		assertEquals(6, partialDate.getMonth().getValue());
		assertEquals((Integer) 24, partialDate.getDay());
	}

	@Test
	public void testPartialDateTimeConstructor_YearMonthDayObjects()
	{
		PartialDate partialDate = new PartialDate(Year.of(2021), Month.of(6), 24);
		assertEquals(2021, partialDate.getYear().getValue());
		assertEquals(6, partialDate.getMonth().getValue());
		assertEquals((Integer) 24, partialDate.getDay());
	}

	@Test(expected = DateTimeException.class)
	public void testPartialDateConstructor_InvalidYear()
	{
		new PartialDate(Year.of(Integer.MIN_VALUE), Month.of(6), 24);
	}

	@Test(expected = DateTimeException.class)
	public void testPartialDateConstructor_InvalidMonth()
	{
		new PartialDate(Year.of(2021), Month.of(60), 24);
	}

	@Test(expected = DateTimeException.class)
	public void testPartialDateConstructor_InvalidDay()
	{
		new PartialDate(Year.of(2021), Month.of(2), 29);
	}

	@Test(expected = DateTimeException.class)
	public void testPartialDateConstructor_InvalidPartialYear()
	{
		new PartialDate(null, 2);
	}

	@Test(expected = DateTimeException.class)
	public void testPartialDateConstructor_InvalidPartialMonth()
	{
		new PartialDate(2021, null, 29);
	}

	@Test
	public void testPartialDate_FullDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021, 6, 24);
		assertTrue(partialDate.isFullDate());
		assertFalse(partialDate.isYearMonth());
		assertFalse(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDate_YearMonthDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021, 6);
		assertFalse(partialDate.isFullDate());
		assertTrue(partialDate.isYearMonth());
		assertFalse(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDate_YearOnlyDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021);
		assertFalse(partialDate.isFullDate());
		assertFalse(partialDate.isYearMonth());
		assertTrue(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDate_ToLocalDate()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		PartialDate partialDate = new PartialDate(2021, 4, 21);
		assertEquals(localDate, partialDate.toLocalDate());
	}

	@Test
	public void testPartialDateToISOString_FullDate()
	{
		PartialDate partialDate = new PartialDate(2021, 4, 8);
		assertEquals("2021-04-08", partialDate.toISOString());
	}

	@Test
	public void testPartialDateToISOString_YearMonth()
	{
		PartialDate partialDate = new PartialDate(2021, 4);
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateToISOString_YearOnly()
	{
		PartialDate partialDate = new PartialDate(2021);
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDate_FullDate()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		PartialDate partialDate = PartialDate.from(localDate);
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDate_FullDateWithModel()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_FULL_DATE);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDate_YearMonth()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_MONTH);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDate_YearOnly()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_ONLY);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDate_FullDate()
	{
		PartialDate partialDate = PartialDate.parseDate("2021-04-21");
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDate_YearMonth()
	{
		PartialDate partialDate = PartialDate.parseDate("2021-04");
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDate_YearOnly()
	{
		PartialDate partialDate = PartialDate.parseDate("2021");
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDate_Null()
	{
		assertNull(PartialDate.parseDate(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPartialDateParseDate_Empty()
	{
		PartialDate.parseDate("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPartialDateParseDate_Invalid()
	{
		PartialDate.parseDate("not a date");
	}
}
