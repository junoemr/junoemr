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

import org.junit.Assert;
import org.junit.Test;
import org.oscarehr.demographicImport.model.common.PartialDate;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.oscarehr.common.model.PartialDate.FORMAT_FULL_DATE;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_MONTH;
import static org.oscarehr.common.model.PartialDate.FORMAT_YEAR_ONLY;

public class PartialDateTest
{
	@Test
	public void testPartialDateFullDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021, 6, 24);
		assertTrue(partialDate.isFullDate());
		assertFalse(partialDate.isYearMonth());
		assertFalse(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDateYearMonthDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021, 6);
		assertFalse(partialDate.isFullDate());
		assertTrue(partialDate.isYearMonth());
		assertFalse(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDateYearOnlyDateCheck()
	{
		PartialDate partialDate = new PartialDate(2021);
		assertFalse(partialDate.isFullDate());
		assertFalse(partialDate.isYearMonth());
		assertTrue(partialDate.isYearOnly());
	}

	@Test
	public void testPartialDateToLocalDate()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		PartialDate partialDate = new PartialDate(2021, 4, 21);
		assertEquals(localDate, partialDate.toLocalDate());
	}

	@Test
	public void testPartialDateToISOStringFullDate()
	{
		PartialDate partialDate = new PartialDate(2021, 4, 8);
		assertEquals("2021-04-08", partialDate.toISOString());
	}

	@Test
	public void testPartialDateToISOStringYearMonth()
	{
		PartialDate partialDate = new PartialDate(2021, 4);
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateToISOStringYearOnly()
	{
		PartialDate partialDate = new PartialDate(2021);
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDateFullDate()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		PartialDate partialDate = PartialDate.from(localDate);
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDateFullDateWithModel()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_FULL_DATE);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDateYearMonth()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_MONTH);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateFromLocalDateYearOnly()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 21);
		org.oscarehr.common.model.PartialDate partialDateModel = new org.oscarehr.common.model.PartialDate(0, 0, 0, FORMAT_YEAR_ONLY);
		PartialDate partialDate = PartialDate.from(localDate, partialDateModel);
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDateFullDate()
	{
		PartialDate partialDate = PartialDate.parseDate("2021-04-21");
		assertEquals("2021-04-21", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDateYearMonth()
	{
		PartialDate partialDate = PartialDate.parseDate("2021-04");
		assertEquals("2021-04", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDateYearOnly()
	{
		PartialDate partialDate = PartialDate.parseDate("2021");
		assertEquals("2021", partialDate.toISOString());
	}

	@Test
	public void testPartialDateParseDateInvalid()
	{
		try
		{
			PartialDate.parseDate("not a date");
			fail();
		}
		catch(Exception e)
		{
			Assert.assertTrue(true);
		}
	}
}
