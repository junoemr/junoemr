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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import xml.cds.v5_0.AddressType;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.LifeStage;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.ResidualInformation;
import xml.cds.v5_0.YnIndicator;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CDSExportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSExportMapper cdsExportMapper;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testLifeStage_Null()
	{
		assertNull(cdsExportMapper.getLifeStage(null));
	}

	@Test
	public void testLifeStageEnumConversion()
	{
		assertEquals(LifeStage.N, cdsExportMapper.getLifeStage("N"));
		assertEquals(LifeStage.I, cdsExportMapper.getLifeStage("I"));
		assertEquals(LifeStage.C, cdsExportMapper.getLifeStage("C"));
		assertEquals(LifeStage.T, cdsExportMapper.getLifeStage("T"));
		assertEquals(LifeStage.A, cdsExportMapper.getLifeStage("A"));
	}

	@Test
	public void testToCdsAddress_Null()
	{
		assertNull(cdsExportMapper.toCdsAddress(null, AddressType.R));
	}

	@Test
	public void testToCdsAddress_Filled()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = "BC";
		String expectedCountry = "CA";
		String expectedPostal = "V8V0T0";

		Address address = new Address();
		address.setAddressLine1(expectedAddressLine1);
		address.setAddressLine2(expectedAddressLine2);
		address.setCity(expectedCity);
		address.setRegionCode(expectedProvince);
		address.setCountryCode(expectedCountry);
		address.setPostalCode(expectedPostal);

		xml.cds.v5_0.Address resultAddress = cdsExportMapper.toCdsAddress(address, AddressType.R);
		assertEquals(expectedAddressLine1, resultAddress.getStructured().getLine1());
		assertEquals(expectedAddressLine2, resultAddress.getStructured().getLine2());
		assertEquals(expectedCity, resultAddress.getStructured().getCity());
		assertEquals(expectedProvince, resultAddress.getStructured().getCountrySubdivisionCode());
		assertEquals(expectedPostal, resultAddress.getStructured().getPostalZipCode().getPostalCode());
	}

	@Test
	public void testPersonNameSimple_Null()
	{
		assertNull(cdsExportMapper.toPersonNameSimple(null));
	}

	@Test
	public void testPersonNameSimple()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		Provider provider = new Provider();
		provider.setFirstName(expectedFirstName);
		provider.setLastName(expectedLastName);

		PersonNameSimple personNameSimple = cdsExportMapper.toPersonNameSimple(provider);

		assertEquals(expectedFirstName, personNameSimple.getFirstName());
		assertEquals(expectedLastName, personNameSimple.getLastName());
	}

	@Test
	public void testCreateResidualInfoDataElement()
	{
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.TEXT;
		String name = "key";
		String value = "value";

		ResidualInformation.DataElement dataElement = cdsExportMapper.createResidualInfoDataElement(dataType, name, value);

		assertEquals(name, dataElement.getName());
		assertEquals(dataType.toString(), dataElement.getDataType());
		assertEquals(value, dataElement.getContent());
	}

	@Test
	public void testAddNonNullDataElements_String()
	{
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.TEXT;
		String name = "key";
		String value = "value";

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, dataType, name, value);
		ResidualInformation.DataElement dataElement = residualInformation.getDataElement().get(0);

		assertEquals(name, dataElement.getName());
		assertEquals(dataType.toString(), dataElement.getDataType());
		assertEquals(value, dataElement.getContent());
	}
	@Test
	public void testAddNonNullDataElements_StringNull()
	{
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.TEXT;
		String name = "key";
		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, dataType, name, (String) null);
		assertTrue(residualInformation.getDataElement().isEmpty());
	}

	@Test
	public void testAddNonNullDataElements_LocalDate()
	{
		String name = "key";
		LocalDate value = LocalDate.of(2021, 1, 6);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, value);
		ResidualInformation.DataElement dataElement = residualInformation.getDataElement().get(0);

		assertEquals(name, dataElement.getName());
		assertEquals(CDSConstants.ResidualInfoDataType.DATE.toString(), dataElement.getDataType());
		assertEquals("2021-01-06", dataElement.getContent());
	}

	@Test
	public void testAddNonNullDataElements_LocalDateNull()
	{
		String name = "key";
		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, (LocalDate) null);
		assertTrue(residualInformation.getDataElement().isEmpty());
	}

	@Test
	public void testAddNonNullDataElements_PartialDateFullDate()
	{
		String name = "key";
		PartialDate value = new PartialDate(2021, 1, 6);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, value);
		ResidualInformation.DataElement dataElement = residualInformation.getDataElement().get(0);

		assertEquals(name, dataElement.getName());
		assertEquals(CDSConstants.ResidualInfoDataType.DATE.toString(), dataElement.getDataType());
		assertEquals("2021-01-06", dataElement.getContent());
	}

	@Test
	public void testAddNonNullDataElements_PartialDateYearMonth()
	{
		String name = "key";
		PartialDate value = new PartialDate(2021, 1);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, value);
		ResidualInformation.DataElement dataElement = residualInformation.getDataElement().get(0);

		assertEquals(name, dataElement.getName());
		assertEquals(CDSConstants.ResidualInfoDataType.DATE_PARTIAL.toString(), dataElement.getDataType());
		assertEquals("2021-01", dataElement.getContent());
	}

	@Test
	public void testAddNonNullDataElements_PartialDateYearOnly()
	{
		String name = "key";
		PartialDate value = new PartialDate(2021);

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, value);
		ResidualInformation.DataElement dataElement = residualInformation.getDataElement().get(0);

		assertEquals(name, dataElement.getName());
		assertEquals(CDSConstants.ResidualInfoDataType.DATE_PARTIAL.toString(), dataElement.getDataType());
		assertEquals("2021", dataElement.getContent());
	}

	@Test
	public void testAddNonNullDataElements_PartialDateNull()
	{
		String name = "key";
		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		cdsExportMapper.addNonNullDataElements(residualInformation, name, (PartialDate) null);
		assertTrue(residualInformation.getDataElement().isEmpty());
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_Null()
	{
		assertNull(cdsExportMapper.toNullableDateTimeFullOrPartial((LocalDateTime) null));
		assertNull(cdsExportMapper.toNullableDateTimeFullOrPartial((LocalDate) null));
		assertNull(cdsExportMapper.toNullableDateTimeFullOrPartial((PartialDateTime) null));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_LocalDateTime()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 1, 6, 12, 30, 55);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(localDateTime);
		assertEquals("2021-01-06T12:30:55", String.valueOf(dateTimeFullOrPartial.getFullDateTime()));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_LocalDate()
	{
		LocalDate localDate = LocalDate.of(2021, 1, 6);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(localDate);
		assertEquals("2021-01-06T00:00:00", String.valueOf(dateTimeFullOrPartial.getFullDateTime()));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_PartialDateTimeFullDateTime()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 1, 6, 12, 30, 55);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(partialDateTime);
		assertEquals("2021-01-06T12:30:55", String.valueOf(dateTimeFullOrPartial.getFullDateTime()));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_PartialDateTimeFullDate()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 1, 6);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(partialDateTime);
		assertEquals("2021-01-06T00:00:00", String.valueOf(dateTimeFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_PartialDateTimeYearMonth()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 4);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(partialDateTime);
		assertEquals("2021-04-01T00:00:00", String.valueOf(dateTimeFullOrPartial.getYearMonth()));
	}

	@Test
	public void TestToNullableDateTimeFullOrPartial_PartialDateTimeYearOnly()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial(partialDateTime);
		assertEquals("2021-01-01T00:00:00", String.valueOf(dateTimeFullOrPartial.getYearOnly()));
	}
	@Test
	public void TestToNullableDateFullOrPartial_PartialDateTimeDefault()
	{
		LocalDateTime localDateTime = LocalDateTime.of(2021, 1, 6, 12, 30, 55);
		DateTimeFullOrPartial dateTimeFullOrPartial = cdsExportMapper.toNullableDateTimeFullOrPartial((LocalDateTime) null, localDateTime);
		assertEquals("2021-01-06T12:30:55", String.valueOf(dateTimeFullOrPartial.getFullDateTime()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_Null()
	{
		assertNull(cdsExportMapper.toNullableDateFullOrPartial((LocalDate) null));
		assertNull(cdsExportMapper.toNullableDateFullOrPartial((PartialDate) null));
		assertNull(cdsExportMapper.toNullableDateFullOrPartial((PartialDateTime) null));
	}

	@Test
	public void TestToNullableDateFullOrPartial_LocalDate()
	{
		LocalDate localDate = LocalDate.of(2021, 1, 6);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial(localDate);
		assertEquals("2021-01-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_PartialDateTimeFullDateTime()
	{
		PartialDateTime partialDateTime = new PartialDateTime(2021, 1, 6, 12, 30, 55);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial(partialDateTime);
		assertEquals("2021-01-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_PartialDateFullDate()
	{
		PartialDate partialDate = new PartialDate(2021, 4, 6);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial(partialDate);
		assertEquals("2021-04-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_PartialDateYearMonth()
	{
		PartialDate partialDate = new PartialDate(2021, 4);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial(partialDate);
		assertEquals("2021-04-01", String.valueOf(dateFullOrPartial.getYearMonth()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_PartialDateYearOnly()
	{
		PartialDate partialDate = new PartialDate(2021);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial(partialDate);
		assertEquals("2021-01-01", String.valueOf(dateFullOrPartial.getYearOnly()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_PartialDateDefault()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 6);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial((PartialDate) null, localDate);
		assertEquals("2021-04-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void TestToNullableDateFullOrPartial_LocalDateDefault()
	{
		LocalDate localDate = LocalDate.of(2021, 4, 6);
		DateFullOrPartial dateFullOrPartial = cdsExportMapper.toNullableDateFullOrPartial((LocalDate) null, localDate);
		assertEquals("2021-04-06", String.valueOf(dateFullOrPartial.getFullDate()));
	}

	@Test
	public void testToYnIndicator_Null()
	{
		assertNull(cdsExportMapper.toYnIndicator(null));
	}
	@Test
	public void testToYnIndicator_False()
	{
		YnIndicator ynIndicator = cdsExportMapper.toYnIndicator(false);
		assertFalse(ynIndicator.isBoolean());
	}
	@Test
	public void testToYnIndicator_True()
	{
		YnIndicator ynIndicator = cdsExportMapper.toYnIndicator(true);
		assertTrue(ynIndicator.isBoolean());
	}

	@Test
	public void testToYnIndicatorString_Null()
	{
		assertEquals(CDSConstants.Y_INDICATOR_FALSE, cdsExportMapper.toYnIndicatorString(null));
	}

	@Test
	public void testToYnIndicatorString_False()
	{
		assertEquals(CDSConstants.Y_INDICATOR_FALSE, cdsExportMapper.toYnIndicatorString(false));
	}

	@Test
	public void testToYnIndicatorString_True()
	{
		assertEquals(CDSConstants.Y_INDICATOR_TRUE, cdsExportMapper.toYnIndicatorString(true));
	}

}
