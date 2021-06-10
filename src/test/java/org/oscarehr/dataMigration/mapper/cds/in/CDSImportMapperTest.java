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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.dataMigration.logger.cds.CDSImportLogger;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.AddressStructured;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.LifeStage;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.PhoneNumberType;
import xml.cds.v5_0.PostalZipCode;
import xml.cds.v5_0.ResidualInformation;
import xml.cds.v5_0.YnIndicator;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class CDSImportMapperTest
{
	@Autowired
	@InjectMocks
	private CDSImportMapper cdsImportMapper;

	@Mock
	private PatientImportContextService patientImportContextService;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		CDSImportLogger cdsImportLoggerMock = Mockito.mock(CDSImportLogger.class);
		PatientImportContext patientImportContextMock = Mockito.mock(PatientImportContext.class);
		when(patientImportContextMock.getImportLogger()).thenReturn(cdsImportLoggerMock);
		when(patientImportContextService.getContext()).thenReturn(patientImportContextMock);
	}

	@Test
	public void testLifeStage_Null()
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
	public void testAgeAtOnset_Null()
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
	public void testGetSubregionCode_Null()
	{
		assertNull(cdsImportMapper.getSubregionCode(null));
	}

	@Test
	public void testGetSubregionCode_ProvinceOnly()
	{
		assertEquals("BC", cdsImportMapper.getSubregionCode("BC"));
	}

	@Test
	public void testGetSubregionCode_CountryAndProvince()
	{
		assertEquals("BC", cdsImportMapper.getSubregionCode("CA-BC"));
	}

	@Test
	public void testGetSubregionCode_SpecialCodes()
	{
		assertNull(cdsImportMapper.getSubregionCode("-50"));
		assertNull(cdsImportMapper.getSubregionCode("-70"));
		assertNull(cdsImportMapper.getSubregionCode("-90"));
	}

	@Test
	public void testGetSubregionCode_Invalid()
	{
		assertNull(cdsImportMapper.getSubregionCode("notValid"));
	}

	@Test
	public void testGetCountryCode_Null()
	{
		assertNull(cdsImportMapper.getCountryCode(null));
	}

	@Test
	public void testGetCountryCode_CountryAndProvince()
	{
		assertEquals("CA", cdsImportMapper.getCountryCode("CA-BC"));
		assertEquals("US", cdsImportMapper.getCountryCode("US-NY"));
	}

	@Test
	public void testGetAddress_Null()
	{
		assertNull(cdsImportMapper.getAddress(null));
	}

	@Test
	public void testGetAddress_StructuredCA_Full()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = "BC";
		String expectedCountry = "CA";
		String expectedPostal = "V8V0T0";

		xml.cds.v5_0.Address importAddress = createTestImportAddressCA(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				expectedCountry + "-" + expectedProvince,
				expectedPostal
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedProvince, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedPostal, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredCA_NoPostal()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = "BC";
		String expectedCountry = "CA";
		String expectedPostal = null;

		xml.cds.v5_0.Address importAddress = createTestImportAddressCA(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				expectedCountry + "-" + expectedProvince,
				expectedPostal
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedProvince, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedPostal, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredCA_NoSubdivisionCode()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = null;
		String expectedCountry = "CA";
		String expectedPostal = "V8V0T0";

		xml.cds.v5_0.Address importAddress = createTestImportAddressCA(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				null,
				expectedPostal
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedProvince, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedPostal, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredCA_NoCountry()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedProvince = null;
		String expectedCountry = null;
		String expectedPostal = null;

		xml.cds.v5_0.Address importAddress = createTestImportAddressCA(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				null,
				expectedPostal
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedProvince, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedPostal, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredUS_Full()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedState = "NY";
		String expectedCountry = "US";
		String expectedZip = "99750-0077";

		xml.cds.v5_0.Address importAddress = createTestImportAddressUS(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				expectedCountry + "-" + expectedState,
				expectedZip
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedState, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedZip, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredUS_NoZip()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedState = "NY";
		String expectedCountry = "US";
		String expectedZip = null;

		xml.cds.v5_0.Address importAddress = createTestImportAddressUS(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				expectedCountry + "-" + expectedState,
				expectedZip
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedState, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedZip, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredUS_NoSubdivisionCode()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedState = null;
		String expectedCountry = "US";
		String expectedZip = "99750-0077";

		xml.cds.v5_0.Address importAddress = createTestImportAddressUS(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				null,
				expectedZip
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedState, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedZip, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_StructuredUS_NoCountry()
	{
		String expectedAddressLine1 = "line 1";
		String expectedAddressLine2 = "line 2";
		String expectedCity = "city1";
		String expectedState = null;
		String expectedCountry = null;
		String expectedZip = null;

		xml.cds.v5_0.Address importAddress = createTestImportAddressUS(
				expectedAddressLine1,
				expectedAddressLine2,
				expectedCity,
				null,
				expectedZip
		);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedAddressLine1, resultAddress.getAddressLine1());
		assertEquals(expectedAddressLine2, resultAddress.getAddressLine2());
		assertEquals(expectedCity, resultAddress.getCity());
		assertEquals(expectedState, resultAddress.getRegionCode());
		assertEquals(expectedCountry, resultAddress.getCountryCode());
		assertEquals(expectedZip, resultAddress.getPostalCode());
	}

	@Test
	public void testGetAddress_Formatted()
	{
		String expectedFormattedAddress = "any address string";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.Address importAddress = objectFactory.createAddress();
		importAddress.setFormatted(expectedFormattedAddress);

		Address resultAddress = cdsImportMapper.getAddress(importAddress);

		assertEquals(expectedFormattedAddress, resultAddress.getAddressLine1());
	}

	@Test
	public void testGetPhoneNumber_Null()
	{
		assertNull(cdsImportMapper.getPhoneNumber(null));
	}

	@Test
	public void testGetPhoneNumber_2Part_NumberOnly()
	{
		String expectedNumber = "2505551111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberPhoneNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedNumber, resultNumber.getNumber());
		assertNull(resultNumber.getExtension());
	}

	@Test
	public void testGetPhoneNumber_2Part_NumberExtension()
	{
		String expectedNumber = "2505551111";
		String expectedExtension = "123";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberPhoneNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		// add extension element
		JAXBElement<String> extension = objectFactory.createPhoneNumberExtension(expectedExtension);
		phoneNumber.getContent().add(extension);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedNumber, resultNumber.getNumber());
		assertEquals(expectedExtension, resultNumber.getExtension());
	}

	@Test
	public void testGetPhoneNumber_Discrete_AreaNumber()
	{
		String expectedArea = "250";
		String expectedNumber = "5551111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add area code element
		JAXBElement<String> area = objectFactory.createPhoneNumberAreaCode(expectedArea);
		phoneNumber.getContent().add(area);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedArea + expectedNumber, resultNumber.getNumber());
	}

	@Test
	public void testGetPhoneNumber_Discrete_AreaNumberExtension()
	{
		String expectedArea = "250";
		String expectedNumber = "5551111";
		String expectedExtension = "123";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add area code element
		JAXBElement<String> area = objectFactory.createPhoneNumberAreaCode(expectedArea);
		phoneNumber.getContent().add(area);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		// add extension element
		JAXBElement<String> extension = objectFactory.createPhoneNumberExtension(expectedExtension);
		phoneNumber.getContent().add(extension);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedArea + expectedNumber, resultNumber.getNumber());
		assertEquals(expectedExtension, resultNumber.getExtension());
	}

	@Test
	public void testGetPhoneNumber_Discrete_AreaNumberExchange()
	{
		String expectedArea = "250";
		String expectedExchange = "555";
		String expectedNumber = "1111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add area code element
		JAXBElement<String> area = objectFactory.createPhoneNumberAreaCode(expectedArea);
		phoneNumber.getContent().add(area);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		// add exchange element
		JAXBElement<String> exchange = objectFactory.createPhoneNumberExchange(expectedExchange);
		phoneNumber.getContent().add(exchange);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedArea + expectedExchange + expectedNumber, resultNumber.getNumber());
	}

	@Test
	public void testGetPhoneNumber_Discrete_AreaNumberExtensionExchange()
	{
		String expectedArea = "250";
		String expectedExchange = "555";
		String expectedNumber = "1111";
		String expectedExtension = "123";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();
		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		// add area code element
		JAXBElement<String> area = objectFactory.createPhoneNumberAreaCode(expectedArea);
		phoneNumber.getContent().add(area);

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		// add extension element
		JAXBElement<String> extension = objectFactory.createPhoneNumberExtension(expectedExtension);
		phoneNumber.getContent().add(extension);

		// add exchange element
		JAXBElement<String> exchange = objectFactory.createPhoneNumberExchange(expectedExchange);
		phoneNumber.getContent().add(exchange);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(expectedArea + expectedExchange + expectedNumber, resultNumber.getNumber());
		assertEquals(expectedExtension, resultNumber.getExtension());
	}

	@Test
	public void testGetPhoneNumber_TypeR()
	{
		String expectedNumber = "2505551111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberPhoneNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		phoneNumber.setPhoneNumberType(PhoneNumberType.R);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(PhoneNumber.PHONE_TYPE.HOME, resultNumber.getPhoneType());
	}

	@Test
	public void testGetPhoneNumber_TypeW()
	{
		String expectedNumber = "2505551111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberPhoneNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		phoneNumber.setPhoneNumberType(PhoneNumberType.W);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(PhoneNumber.PHONE_TYPE.WORK, resultNumber.getPhoneType());
	}

	@Test
	public void testGetPhoneNumber_TypeC()
	{
		String expectedNumber = "2505551111";

		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.PhoneNumber phoneNumber = objectFactory.createPhoneNumber();

		// add the number element
		JAXBElement<String> number = objectFactory.createPhoneNumberPhoneNumber(expectedNumber);
		phoneNumber.getContent().add(number);

		phoneNumber.setPhoneNumberType(PhoneNumberType.C);

		PhoneNumber resultNumber = cdsImportMapper.getPhoneNumber(phoneNumber);
		assertEquals(PhoneNumber.PHONE_TYPE.CELL, resultNumber.getPhoneType());
	}

	@Test
	public void testToProvider_Null()
	{
		assertNull(cdsImportMapper.toProvider(null));
	}

	@Test
	public void testToProvider_PersonSimple()
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
	public void testToProviderNames_Null()
	{
		assertNull(cdsImportMapper.toProviderNames(null));
	}

	@Test
	public void testToProviderNames_String()
	{
		String expectedFirstName = "first";
		String expectedLastName = "last";

		String namesString = expectedLastName + "," + expectedFirstName;

		Provider convertedProvider = cdsImportMapper.toProviderNames(namesString);

		assertEquals(expectedFirstName, convertedProvider.getFirstName());
		assertEquals(expectedLastName, convertedProvider.getLastName());
	}

	@Test
	public void testYIndicatorConversion_Null()
	{
		assertNull(cdsImportMapper.getYIndicator(null));
	}

	@Test
	public void testYIndicatorConversion_BooleanTrue()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setBoolean(true);

		assertTrue(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversion_BooleanFalse()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setBoolean(false);

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversion_SimpleTrue()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple(CDSConstants.Y_INDICATOR_TRUE);

		assertTrue(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversion_SimpleFalse()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple(CDSConstants.Y_INDICATOR_FALSE);

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testYIndicatorConversion_SimpleInvalid()
	{
		ObjectFactory objectFactory = new ObjectFactory();
		YnIndicator yIndicator = objectFactory.createYnIndicator();
		yIndicator.setYnIndicatorsimple("I");

		assertFalse(cdsImportMapper.getYIndicator(yIndicator));
	}

	@Test
	public void testResidualDataElementAsLong_Null()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsLong(null, "key"));
	}

	@Test
	public void testResidualDataElementAsLong()
	{
		String dataKey = "key";
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.NUMERIC;
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
	public void testResidualDataElementAsDate_Null()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsDate(null, "key"));
	}

	@Test
	public void testResidualDataElementAsDate()
	{
		String dataKey = "key";
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.DATE;
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
	public void testResidualDataElementAsString_Null()
	{
		assertNull(cdsImportMapper.getResidualDataElementAsString(null, "key"));
	}

	@Test
	public void testResidualDataElementAsString()
	{
		String dataKey = "key";
		CDSConstants.ResidualInfoDataType dataType = CDSConstants.ResidualInfoDataType.TEXT;
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
	public void testImportAllResidualInfo_Null()
	{
		assertNull(cdsImportMapper.importAllResidualInfo(null));
	}

	@Test
	public void testImportAllResidualInfo_Simple()
	{
		String dataKey = "key";
		String dataType = "unknownDataType";
		String expectedStringValue = "sample string value";

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setName(dataKey);
		dataElement.setDataType(dataType);
		dataElement.setContent(expectedStringValue);
		residualInformation.getDataElement().add(dataElement);

		List<ResidualInfo> residualInfoList = cdsImportMapper.importAllResidualInfo(residualInformation);

		assertEquals(1, residualInfoList.size());
		ResidualInfo actualResult0 = residualInfoList.get(0);
		assertEquals(dataKey, actualResult0.getContentKey());
		assertEquals(dataType, actualResult0.getContentType());
		assertEquals(expectedStringValue, actualResult0.getContentValue());
	}

	@Test
	public void testImportAllResidualInfo_IgnoreList()
	{
		String dataKey1 = "key";
		CDSConstants.ResidualInfoDataType dataType1 = CDSConstants.ResidualInfoDataType.TEXT;
		String expectedStringValue1 = "sample string value";

		String dataKey2 = "ignored key";
		CDSConstants.ResidualInfoDataType dataType2 = CDSConstants.ResidualInfoDataType.TEXT;
		String expectedStringValue2 = "ignored value";

		ObjectFactory objectFactory = new ObjectFactory();
		ResidualInformation residualInformation = objectFactory.createResidualInformation();

		ResidualInformation.DataElement dataElement1 = objectFactory.createResidualInformationDataElement();
		dataElement1.setName(dataKey1);
		dataElement1.setDataType(dataType1.name());
		dataElement1.setContent(expectedStringValue1);
		residualInformation.getDataElement().add(dataElement1);

		ResidualInformation.DataElement dataElement2 = objectFactory.createResidualInformationDataElement();
		dataElement2.setName(dataKey2);
		dataElement2.setDataType(dataType2.name());
		dataElement2.setContent(expectedStringValue2);
		residualInformation.getDataElement().add(dataElement2);

		List<ResidualInfo> residualInfoList = cdsImportMapper.importAllResidualInfo(residualInformation, dataKey2);

		// the elements with the ignored keys should not be in the returned list
		assertEquals(1, residualInfoList.size());
		ResidualInfo actualResult0 = residualInfoList.get(0);
		assertEquals(dataKey1, actualResult0.getContentKey());
		assertEquals(dataType1.name(), actualResult0.getContentType());
		assertEquals(expectedStringValue1, actualResult0.getContentValue());
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

	private xml.cds.v5_0.Address createTestImportAddressCA(
			String addressLine1,
			String addressLine2,
			String city,
			String subDivisionCode,
			String postal)
	{
		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.Address importAddress = objectFactory.createAddress();
		AddressStructured addressStructured = objectFactory.createAddressStructured();
		PostalZipCode postalZipCode = objectFactory.createPostalZipCode();

		addressStructured.setLine1(addressLine1);
		addressStructured.setLine2(addressLine2);
		addressStructured.setCity(city);
		addressStructured.setCountrySubdivisionCode(subDivisionCode);
		postalZipCode.setPostalCode(postal);
		addressStructured.setPostalZipCode(postalZipCode);
		importAddress.setStructured(addressStructured);

		return importAddress;
	}

	private xml.cds.v5_0.Address createTestImportAddressUS(
			String addressLine1,
			String addressLine2,
			String city,
			String subDivisionCode,
			String zip)
	{
		ObjectFactory objectFactory = new ObjectFactory();
		xml.cds.v5_0.Address importAddress = objectFactory.createAddress();
		AddressStructured addressStructured = objectFactory.createAddressStructured();
		PostalZipCode postalZipCode = objectFactory.createPostalZipCode();

		addressStructured.setLine1(addressLine1);
		addressStructured.setLine2(addressLine2);
		addressStructured.setCity(city);
		addressStructured.setCountrySubdivisionCode(subDivisionCode);
		postalZipCode.setZipCode(zip);
		addressStructured.setPostalZipCode(postalZipCode);
		importAddress.setStructured(addressStructured);

		return importAddress;
	}
}
