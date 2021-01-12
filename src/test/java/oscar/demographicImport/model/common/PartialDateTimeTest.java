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
package oscar.demographicImport.model.common;

import org.junit.Test;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.model.common.PartialDateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.oscarehr.common.model.PartialDate.FORMAT_FULL_DATE;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_MONTH;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_ONLY;

public class PartialDateTimeTest
{
	@Test
	public void testPartialDateTimeFullDateTimeCheck()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 6, 24, 12, 55, 30);
		assertTrue(partialDateTime.isFullDateTime());
		assertTrue(partialDateTime.isFullDate());
		assertFalse(partialDateTime.isYearMonth());
		assertFalse(partialDateTime.isYearOnly());
	}
	@Test
	public void testPartialDateTimeFullDateCheck()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 6, 24);
		assertFalse(partialDateTime.isFullDateTime());
		assertTrue(partialDateTime.isFullDate());
		assertFalse(partialDateTime.isYearMonth());
		assertFalse(partialDateTime.isYearOnly());
	}

	@Test
	public void testPartialDateTimeYearMonthDateCheck()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 6);
		assertFalse(partialDateTime.isFullDateTime());
		assertFalse(partialDateTime.isFullDate());
		assertTrue(partialDateTime.isYearMonth());
		assertFalse(partialDateTime.isYearOnly());
	}

	@Test
	public void testPartialDateTimeYearOnlyDateCheck()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021);
		assertFalse(partialDateTime.isFullDateTime());
		assertFalse(partialDateTime.isFullDate());
		assertFalse(partialDateTime.isYearMonth());
		assertTrue(partialDateTime.isYearOnly());
	}

	@Test
	public void testPartialDateTimeToISOStringFullDateTime()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 4, 8, 9, 4, 5);
		assertEquals("2021-04-08T09:04:05", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeToISOStringFullDate()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 4, 8);
		assertEquals("2021-04-08", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeToISOStringYearMonth()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 4);
		assertEquals("2021-04", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeToISOStringYearOnly()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021);
		assertEquals("2021", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromLocalDateFullDate()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		PartialDateTime partialDateTime = PartialDateTime.from(localDate);
		assertEquals("2021-04-21T00:00:00", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromLocalDateTimeFullDateTime()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 21, 12, 24, 55);
		PartialDateTime partialDateTime = PartialDateTime.from(localDateTime);
		assertEquals("2021-04-21T12:24:55", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromPartialDateFullDate()
	{
		PartialDate partialDate = new PartialDate(2021, 4,21);
		PartialDateTime partialDateTime = PartialDateTime.from(partialDate);
		assertEquals("2021-04-21T00:00:00", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromPartialDateYearMonth()
	{
		PartialDate partialDate = new PartialDate(2021, 4);
		PartialDateTime partialDateTime = PartialDateTime.from(partialDate);
		assertEquals("2021-04", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromPartialDateYearOnly()
	{
		PartialDate partialDate = new PartialDate(2021);
		PartialDateTime partialDateTime = PartialDateTime.from(partialDate);
		assertEquals("2021", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromLocalDateFullDateWithModel()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 21, 12, 24, 55);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_FULL_DATE);
		PartialDateTime partialDateTime = PartialDateTime.from(localDateTime, partialDateModel);
		assertEquals("2021-04-21T12:24:55", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromLocalDateYearMonthWithModel()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 21, 12, 24, 55);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_MONTH);
		PartialDateTime partialDateTime = PartialDateTime.from(localDateTime, partialDateModel);
		assertEquals("2021-04", partialDateTime.toISOString());
	}

	@Test
	public void testPartialDateTimeFromLocalDateYearOnlyWithModel()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 4, 21, 12, 24, 55);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_ONLY);
		PartialDateTime partialDateTime = PartialDateTime.from(localDateTime, partialDateModel);
		assertEquals("2021", partialDateTime.toISOString());
	}

}
