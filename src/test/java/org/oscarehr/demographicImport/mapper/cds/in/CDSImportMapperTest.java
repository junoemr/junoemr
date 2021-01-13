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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.DateTimeFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.LifeStage;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameSimple;
import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.common.xml.cds.v5_0.model.YnIndicator;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.util.ConversionUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CDSImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSImportMapper cdsImportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testLifeStageNull()
	{
		assertNull(cdsImportMapper.getLifeStage(null));
	}

	@Test
	public void testLifeStageEnumConversion()
	{
		assertEquals("N", cdsImportMapper.getLifeStage(LifeStage.N));
		assertEquals("I", cdsImportMapper.getLifeStage(LifeStage.I));
		assertEquals("C", cdsImportMapper.getLifeStage(LifeStage.C));
		assertEquals("T", cdsImportMapper.getLifeStage(LifeStage.T));
		assertEquals("A", cdsImportMapper.getLifeStage(LifeStage.A));
	}

	@Test
	public void testAgeAtOnsetNull()
	{
		assertNull(cdsImportMapper.getAgeAtOnset(null));
	}

	@Test
	public void testAgeAtOnsetConversion()
	{
		Long expectedLong = 32L;
		BigInteger bigInteger = BigInteger.valueOf(expectedLong);
		assertEquals(expectedLong, cdsImportMapper.getAgeAtOnset(bigInteger));
	}

	@Test
	public void testToProviderNull()
	{
		assertNull(cdsImportMapper.toProvider(null));
	}

	@Test
	public void testToProviderConversionPersonSimple()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		ObjectFactory objectFactory = new ObjectFactory();
		PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();
		personNameSimple.setFirstName(expectedFirstName);
		personNameSimple.setLastName(expectedLastName);

		Provider convertedProvider = cdsImportMapper.toProvider(personNameSimple);

		assertEquals(expectedFirstName, convertedProvider.getFirstName());
		assertEquals(expectedLastName, convertedProvider.getLastName());
	}

	@Test
	public void testToProviderNamesNull()
	{
		assertNull(cdsImportMapper.toProviderNames(null));
	}

	@Test
	public void testToProviderNamesConversion()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		String namesString = expectedLastName + "," + expectedFirstName;

		Provider convertedProvider = cdsImportMapper.toProviderNames(namesString);

		assertEquals(expectedFirstName, convertedProvider.getFirstName());
		assertEquals(expectedLastName, convertedProvider.getLastName());
	}

	@Test
	public void testYIndicatorConversionNull()
	{
		assertNull(cdsImportMapper.getYIndicator(null));
	}

	@Test
	public void testYIndicatorConversionBooleanTrue()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setBoolean(true);

		assertTrue(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversionBooleanFalse()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setBoolean(false);

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversionSimpleTrue()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple(CDSConstants.Y_INDICATOR_TRUE);

		assertTrue(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversionSimpleFalse()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple(CDSConstants.Y_INDICATOR_FALSE);

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversionSimpleInvalid()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple("I");

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testResidualDataElementAsLongNull()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsLong(null, "key"));
	}

	@Test
	public void testResidualDataElementAsLong()
	{
		String dataKey = "key";
		CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.NUMERIC;
		Long expectedLongValue = Long.valueOf(32);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setName(dataKey);
		dataElement.setDataType(dataType.name());
		dataElement.setContent(String.valueOf(expectedLongValue));
		residualInformation.getDataElement().add(dataElement);

		assertEquals(expectedLongValue, cdsImportMapper.getResidualDataElementAsLong(residualInformation, dataKey));
	}

	@Test
	public void testResidualDataElementAsDateNull()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsDate(null, "key"));
	}

	@Test
	public void testResidualDataElementAsDate()
	{
		String dataKey = "key";
		CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE;
		LocalDate expectedDateValue = LocalDate.of(2021, 1, 6);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setName(dataKey);
		dataElement.setDataType(dataType.name());
		dataElement.setContent(ConversionUtils.toDateString(expectedDateValue));
		residualInformation.getDataElement().add(dataElement);

		assertEquals(expectedDateValue, cdsImportMapper.getResidualDataElementAsDate(residualInformation, dataKey));
	}

	@Test
	public void testResidualDataElementAsStringNull()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsString(null, "key"));
	}

	@Test
	public void testResidualDataElementAsString()
	{
		String dataKey = "key";
		CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.TEXT;
		String expectedStringValue = "sample string value";

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setName(dataKey);
		dataElement.setDataType(dataType.name());
		dataElement.setContent(expectedStringValue);
		residualInformation.getDataElement().add(dataElement);

		assertEquals(expectedStringValue, cdsImportMapper.getResidualDataElementAsString(residualInformation, dataKey));
	}

	@Test
	public void testNullablePartialDate_Null()
	{
		assertNull(cdsImportMapper.toNullablePartialDate(null));
	}

	@Test
	public void testNullablePartialDate_PartialDateFullDate() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(calendar);

		PartialDate resultDate = cdsImportMapper.toNullablePartialDate(dateFullOrPartial);

		assertTrue(resultDate.isFullDate());
		assertEquals(2021, resultDate.getYear().getValue());
		assertEquals(1, resultDate.getMonth().getValue());
		assertEquals((Integer) 6, resultDate.getDay());
	}

	@Test
	public void testNullablePartialDate_PartialDateYearMonth() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setYearMonth(calendar);

		PartialDate resultDate = cdsImportMapper.toNullablePartialDate(dateFullOrPartial);

		assertTrue(resultDate.isYearMonth());
		assertEquals(2021, resultDate.getYear().getValue());
		assertEquals(1, resultDate.getMonth().getValue());
	}

	@Test
	public void testNullablePartialDate_PartialDateYearOnly() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setYearOnly(calendar);

		PartialDate resultDate = cdsImportMapper.toNullablePartialDate(dateFullOrPartial);

		assertTrue(resultDate.isYearOnly());
		assertEquals(2021, resultDate.getYear().getValue());
	}

	@Test
	public void testNullablePartialDateTime_Null()
	{
		assertNull(cdsImportMapper.toNullablePartialDateTime(null));
	}

	@Test
	public void testNullablePartialDateTime_PartialDateFullDateTime() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06T12:24:00");
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDateTime(calendar);

		PartialDateTime resultDateTime = cdsImportMapper.toNullablePartialDateTime(dateTimeFullOrPartial);

		assertTrue(resultDateTime.isFullDateTime());
		assertEquals(2021, resultDateTime.getYear().getValue());
		assertEquals(1, resultDateTime.getMonth().getValue());
		assertEquals((Integer) 6, resultDateTime.getDay());
		assertEquals(LocalTime.of(12, 24, 0), resultDateTime.getLocalTime());
	}

	@Test
	public void testNullablePartialDateTime_PartialDateFullDate() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06");
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDate(calendar);

		PartialDateTime resultDateTime = cdsImportMapper.toNullablePartialDateTime(dateTimeFullOrPartial);

		assertTrue(resultDateTime.isFullDate());
		assertEquals(2021, resultDateTime.getYear().getValue());
		assertEquals(1, resultDateTime.getMonth().getValue());
		assertEquals((Integer) 6, resultDateTime.getDay());
	}

	@Test
	public void testNullablePartialDateTime_PartialDateYearMonth() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01");
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setYearMonth(calendar);

		PartialDateTime resultDateTime = cdsImportMapper.toNullablePartialDateTime(dateTimeFullOrPartial);

		assertTrue(resultDateTime.isYearMonth());
		assertEquals(2021, resultDateTime.getYear().getValue());
		assertEquals(1, resultDateTime.getMonth().getValue());
	}

	@Test
	public void testNullablePartialDateTime_PartialDateYearOnly() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021");
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setYearOnly(calendar);

		PartialDateTime resultDateTime = cdsImportMapper.toNullablePartialDateTime(dateTimeFullOrPartial);

		assertTrue(resultDateTime.isYearOnly());
		assertEquals(2021, resultDateTime.getYear().getValue());
	}

	@Test
	public void testNullableLocalDateTime_Null()
	{
		assertNull(cdsImportMapper.toNullableLocalDateTime((DateFullOrPartial) null));
		assertNull(cdsImportMapper.toNullableLocalDateTime((DateTimeFullOrPartial) null));
	}

	@Test
	public void testNullableLocalDateTime_DateFullOrPartial() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();
		LocalDateTime expectedDateTime = LocalDateTime.of(2021, 1, 6, 0, 0, 0);

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(calendar);

		assertEquals(expectedDateTime, cdsImportMapper.toNullableLocalDateTime(dateFullOrPartial));
	}

	@Test
	public void testNullableLocalDateTime_DateTimeFullOrPartial() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();
		LocalDateTime expectedDateTime = LocalDateTime.of(2021, 1, 6, 12, 30, 5);

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(expectedDateTime.toString());
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDateTime(calendar);

		assertEquals(expectedDateTime, cdsImportMapper.toNullableLocalDateTime(dateTimeFullOrPartial));
	}

	@Test
	public void testNullableLocalDate_Null()
	{
		assertNull(cdsImportMapper.toNullableLocalDate((DateFullOrPartial) null));
		assertNull(cdsImportMapper.toNullableLocalDate((DateTimeFullOrPartial) null));
	}

	@Test
	public void testNullableLocalDate_DateFullOrPartial() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();
		LocalDate expectedDate = LocalDate.of(2021, 1, 6);

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06");
		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		dateFullOrPartial.setFullDate(calendar);

		assertEquals(expectedDate, cdsImportMapper.toNullableLocalDate(dateFullOrPartial));
	}

	@Test
	public void testNullableLocalDate_DateTimeFullOrPartial() throws DatatypeConfigurationException
	{
		ObjectFactory objectFactory = new ObjectFactory();
		LocalDate expectedDate = LocalDate.of(2021, 1, 6);

		XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar("2021-01-06T12:30:55");
		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		dateTimeFullOrPartial.setFullDateTime(calendar);

		assertEquals(expectedDate, cdsImportMapper.toNullableLocalDate(dateTimeFullOrPartial));
	}
}
