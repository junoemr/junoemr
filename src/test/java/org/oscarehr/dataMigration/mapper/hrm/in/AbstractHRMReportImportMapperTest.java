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

package org.oscarehr.dataMigration.mapper.hrm.in;

import org.junit.Test;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.ObjectFactory;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AbstractHRMReportImportMapperTest
{
	@Autowired
	private final AbstractHRMImportMapper hrmReportImportMapper;

	protected ObjectFactory objectFactory;
	protected DatatypeFactory dataTypeFactory;

	// Extend with a stub class, since logic we're testing is in an abstract class.
	public class TestAbstractHRMReportImportMapper extends AbstractHRMImportMapper<Void, Void>
	{
		@Override
		public Void importToJuno(Void importStructure) throws Exception
		{
			return null;
		}
	}

	public AbstractHRMReportImportMapperTest() throws DatatypeConfigurationException
	{
		hrmReportImportMapper = new TestAbstractHRMReportImportMapper();
		objectFactory = new ObjectFactory();
		dataTypeFactory = DatatypeFactory.newInstance();
	}

	@Test
	public void toNullableLocalDate_fromDateTime()
	{
		LocalDate expectedDate = LocalDate.of(2021, 1, 6);

		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2021-01-06T12:30:55");
		DateFullOrPartial dateTimeFullOrPartial = objectFactory.createDateFullOrPartial();
		dateTimeFullOrPartial.setDateTime(calendar);

		assertEquals(expectedDate, hrmReportImportMapper.toNullableLocalDate(dateTimeFullOrPartial));
	}

	@Test
	public void toNullableLocalDate_fromDate()
	{
		LocalDate expectedDate = LocalDate.of(2021, 1, 6);

		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2021-01-06");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(calendar);

		assertEquals(expectedDate, hrmReportImportMapper.toNullableLocalDate(dateFullOrPartial));
	}

	@Test
	public void toNullableLocalDate_fromPartialDateYearMonth()
	{
		LocalDate expectedDate = LocalDate.of(2021,3,1);

		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2021-03-20T10:11:12");
		DateFullOrPartial yearMonthOnly = objectFactory.createDateFullOrPartial();
		yearMonthOnly.setYearMonth(calendar);

		assertEquals(expectedDate, hrmReportImportMapper.toNullableLocalDate(yearMonthOnly));
	}

	@Test
	public void toNullableLocalDate_fromPartialDateYear()
	{
		LocalDate expectedDate = LocalDate.of(1986, 1, 1);
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("1986-08-28T01:02:03");
		DateFullOrPartial yearOnly = objectFactory.createDateFullOrPartial();
		yearOnly.setYearOnly(calendar);

		assertEquals(expectedDate, hrmReportImportMapper.toNullableLocalDate(yearOnly));
	}

	@Test
	public void toNullableLocalDate_fromNull()
	{
		assertNull(hrmReportImportMapper.toNullableLocalDate(null));
	}

	@Test
	public void toNullableLocalDateTime_fromDateTime()
	{
		String dateTimeString = "2021-06-29T12:24:00";
		LocalDateTime expectedDateTime = LocalDateTime.parse(dateTimeString);

		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar(dateTimeString);
		DateFullOrPartial dateTime = objectFactory.createDateFullOrPartial();
		dateTime.setDateTime(calendar);

		assertEquals(expectedDateTime, hrmReportImportMapper.toNullableLocalDateTime(dateTime));
	}

	@Test
	public void toNullableLocalDateTime_fromDate()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("1987-11-07T00:00:00");
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("1987-11-07T12:12:12");

		DateFullOrPartial date = objectFactory.createDateFullOrPartial();
		date.setFullDate(calendar);

		assertEquals(expectedDateTime, hrmReportImportMapper.toNullableLocalDateTime(date));
	}

	@Test
	public void toNullableLocalDateTime_fromPartialDateYearMonth()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("1995-05-01T00:00:00");

		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("1995-05-20");
		DateFullOrPartial yearMonth = objectFactory.createDateFullOrPartial();
		yearMonth.setYearMonth(calendar);

		assertEquals(expectedDateTime, hrmReportImportMapper.toNullableLocalDateTime(yearMonth));

	}

	@Test
	public void toNullableLocalDateTime_fromPartialDateYear()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("2010-01-01T00:00:00");
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2010-02-28T13:14:15");
		DateFullOrPartial yearOnly = objectFactory.createDateFullOrPartial();
		yearOnly.setYearOnly(calendar);

		assertEquals(expectedDateTime, hrmReportImportMapper.toNullableLocalDateTime(yearOnly));
	}

	@Test
	public void toNullableLocalDateTime_fromNull()
	{
		assertNull(hrmReportImportMapper.toNullableLocalDateTime(null));
	}

	@Test
	public void toPartialDateTime_fromNull()
	{
		assertNull(hrmReportImportMapper.toPartialDateTime(null));
	}

	@Test
	public void toPartialDateTime_fromDateTime()
	{
		String dateString = "2012-04-19T03:04:05";
		LocalDateTime expectedDateTime = LocalDateTime.parse(dateString);
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar(dateString);
		DateFullOrPartial fullDateTime = objectFactory.createDateFullOrPartial();
		fullDateTime.setDateTime(calendar);

		PartialDateTime partialDateTime = hrmReportImportMapper.toPartialDateTime(fullDateTime);
		assertTrue(partialDateTime.isFullDateTime());
		assertEquals(partialDateTime.toLocalDateTime(), expectedDateTime);
	}

	@Test
	public void toPartialDateTime_fromDate()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("2012-05-19T00:00:00");
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2012-05-19T10:11:12");
		DateFullOrPartial fullDate = objectFactory.createDateFullOrPartial();
		fullDate.setFullDate(calendar);

		PartialDateTime partialDateTime = hrmReportImportMapper.toPartialDateTime(fullDate);
		assertTrue(partialDateTime.isFullDate());
		assertEquals(partialDateTime.toLocalDateTime(), expectedDateTime);
	}

	@Test
	public void toPartialDateTime_fromYearMonth()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("2014-09-01T00:00:00");
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2014-09-04T09:10:15");
		DateFullOrPartial yearMonth = objectFactory.createDateFullOrPartial();
		yearMonth.setYearMonth(calendar);

		PartialDateTime partialDateTime = hrmReportImportMapper.toPartialDateTime(yearMonth);
		assertTrue(partialDateTime.isYearMonth());
		assertEquals(partialDateTime.toLocalDateTime(), expectedDateTime);
	}

	@Test
	public void toPartialDateTime_yearOnly()
	{
		LocalDateTime expectedDateTime = LocalDateTime.parse("2012-01-01T00:00:00");
		XMLGregorianCalendar calendar = dataTypeFactory.newXMLGregorianCalendar("2012-07-14T11:12:13");
		DateFullOrPartial yearOnly = objectFactory.createDateFullOrPartial();
		yearOnly.setYearOnly(calendar);

		PartialDateTime partialDateTime = hrmReportImportMapper.toPartialDateTime(yearOnly);
		assertTrue(partialDateTime.isYearOnly());
		assertEquals(partialDateTime.toLocalDateTime(), expectedDateTime);
	}
}