package org.oscarehr.dataMigration.mapper.hrm.in;

import org.junit.Test;
import org.mockito.InjectMocks;
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

public class AbstractHRMReportImportMapperTest
{
	@Autowired
	@InjectMocks
	HRMReportImportMapper hrmReportImportMapper;

	protected ObjectFactory objectFactory;
	protected DatatypeFactory dataTypeFactory;

	public AbstractHRMReportImportMapperTest() throws DatatypeConfigurationException
	{
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
}