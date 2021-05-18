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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.oscarehr.dataMigration.mapper.AbstractImportMapper;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.common.ResidualInfo;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.AddressStructured;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.LifeStage;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.PhoneNumberType;
import xml.cds.v5_0.PostalZipCode;
import xml.cds.v5_0.ResidualInformation;
import xml.cds.v5_0.YnIndicator;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_USA;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.Y_INDICATOR_TRUE;

@Component
public abstract class AbstractCDSImportMapper<I, E> extends AbstractImportMapper<I, E>
{
	private static final Logger logger = Logger.getLogger(AbstractCDSImportMapper.class);

	public AbstractCDSImportMapper()
	{
	}

	@Override
	public String toString()
	{
		return ReflectionToStringBuilder.toString(this);
	}

	protected String getLifeStage(LifeStage lifeStage)
	{
		if(lifeStage != null)
		{
			return lifeStage.value();
		}
		return null;
	}

	protected Long getAgeAtOnset(BigInteger ageAtOnset)
	{
		if(ageAtOnset != null)
		{
			return ageAtOnset.longValue();
		}
		return null;
	}

	/**
	 Convert residual info to the model structure.
	 @param residualInformation the data element to convert
	 @param ignoreKeys any data key names that should be ignored. this is intended for keys that are known to be imported elsewhere
	 */
	protected List<ResidualInfo> importAllResidualInfo(ResidualInformation residualInformation, String ... ignoreKeys)
	{
		List<ResidualInfo> residualInfoList = null;
		if(residualInformation != null)
		{
			residualInfoList = new ArrayList<>(residualInformation.getDataElement().size());
			for(ResidualInformation.DataElement dataElement : residualInformation.getDataElement())
			{
				if(Arrays.stream(ignoreKeys).noneMatch(dataElement.getName()::equals))
				{
					ResidualInfo residualInfo = new ResidualInfo();
					residualInfo.setKey(dataElement.getName());
					residualInfo.setValue(dataElement.getContent());
					residualInfo.setValueType(dataElement.getDataType());
					residualInfoList.add(residualInfo);
				}
			}
		}
		return residualInfoList;
	}

	protected Long getResidualDataElementAsLong(ResidualInformation residualInformation, String ... keys)
	{
		ResidualInformation.DataElement dataElement = getResidualDataElement(residualInformation, keys);
		if(dataElement != null)
		{
			return Long.parseLong(dataElement.getContent());
		}
		return null;
	}
	protected LocalDate getResidualDataElementAsDate(ResidualInformation residualInformation, String ... keys)
	{
		ResidualInformation.DataElement dataElement = getResidualDataElement(residualInformation, keys);
		if(dataElement != null)
		{
			return ConversionUtils.toLocalDate(dataElement.getContent());
		}
		return null;
	}
	protected String getResidualDataElementAsString(ResidualInformation residualInformation, String ... keys)
	{
		ResidualInformation.DataElement dataElement = getResidualDataElement(residualInformation, keys);
		if(dataElement != null)
		{
			return dataElement.getContent();
		}
		return null;
	}

	private ResidualInformation.DataElement getResidualDataElement(ResidualInformation residualInformation, String ... keys)
	{
		if(residualInformation != null && keys != null)
		{
			for(ResidualInformation.DataElement dataElement : residualInformation.getDataElement())
			{
				if(Arrays.stream(keys).anyMatch(dataElement.getName()::equals))
				{
					return dataElement;
				}
			}
		}
		return null;
	}

	protected Provider toProvider(PersonNameSimple personNameSimple)
	{
		Provider provider = null;
		if(personNameSimple != null)
		{
			provider = new Provider();
			provider.setFirstName(personNameSimple.getFirstName());
			provider.setLastName(personNameSimple.getLastName());
		}
		return provider;
	}

	protected Provider toProviderNames(String providerNameString)
	{
		Provider provider = null;
		if(providerNameString != null && providerNameString.contains(","))
		{
			String[] providerNames = providerNameString.split(",", 2);
			provider = new Provider();
			provider.setLastName(providerNames[0]);
			provider.setFirstName(providerNames[1]);
		}
		return provider;
	}

	protected Boolean getYIndicator(YnIndicator ynIndicator)
	{
		if(ynIndicator != null)
		{
			String yIndicatorValue = ynIndicator.getYnIndicatorsimple();
			if(yIndicatorValue != null)
			{
				return yIndicatorValue.equals(Y_INDICATOR_TRUE);
			}
			else
			{
				return ynIndicator.isBoolean();
			}
		}
		return null;
	}

	protected PhoneNumber getPhoneNumber(xml.cds.v5_0.PhoneNumber importNumber)
	{
		if(importNumber == null)
		{
			return null;
		}

		String area = null;
		String exchange = null;
		String number = null;
		String extension = null;

		for(JAXBElement<String> phoneElement : importNumber.getContent())
		{
			String key = phoneElement.getName().getLocalPart();
			String value = phoneElement.getValue();
			if("phoneNumber".equals(key) || "number".equals(key))
			{
				number = value;
			}
			else if("extension".equals(key))
			{
				extension = value;
			}
			else if("areaCode".equals(key))
			{
				area = value;
			}
			else if("exchange".equals(key))
			{
				exchange = value;
			}
			else
			{
				logger.error("Unknown Phone number component key: '" + key + "'");
			}
		}

		String fullNumber = StringUtils.trimToEmpty(area) + StringUtils.trimToEmpty(exchange) + StringUtils.trimToEmpty(number);
		PhoneNumber phoneNumber = PhoneNumber.of(fullNumber, extension);

		PhoneNumberType type = importNumber.getPhoneNumberType();
		if(PhoneNumberType.R.equals(type))
		{
			phoneNumber.setPhoneTypeHome();
		}
		else if(PhoneNumberType.W.equals(type))
		{
			phoneNumber.setPhoneTypeWork();
		}
		else if(PhoneNumberType.C.equals(type))
		{
			phoneNumber.setPhoneTypeCell();
		}
		else
		{
			logger.error("Invalid Phone Number Type: " + type);
		}

		return phoneNumber;
	}

	protected String getSubregionCode(String subDivisionCode)
	{
		String regionCode = null;
		if(subDivisionCode != null)
		{
			// most expected case, something like 'CA-BC'
			if(!subDivisionCode.startsWith("-") && subDivisionCode.contains("-"))
			{
				regionCode = subDivisionCode.split("-")[1];
			}
			else if(subDivisionCode.length() == 2) // something like 'BC' or 'ON'
			{
				regionCode = subDivisionCode.toUpperCase();
			}
			// if it's not one of these special case codes (which we can't use anyways, but are valid)
			else if(!subDivisionCode.equals("-50") // not available or temporary
					&& !subDivisionCode.equals("-70") // asked, unknown
					&& !subDivisionCode.equals("-90")) // Not applicable
			{
				logEvent("Unknown CountrySubdivisionCode: " + subDivisionCode);
			}
		}

		return regionCode;
	}

	protected Address getAddress(xml.cds.v5_0.Address importAddress)
	{
		Address address = null;
		if(importAddress != null)
		{
			address = new Address();
			AddressStructured structured = importAddress.getStructured();
			if(structured != null)
			{
				address.setAddressLine1(structured.getLine1());
				address.setAddressLine2(StringUtils.trimToNull(
						StringUtils.trimToEmpty(structured.getLine2()) + "\n" + StringUtils.trimToEmpty(structured.getLine3())));
				address.setCity(structured.getCity());

				String subDivisionCode = structured.getCountrySubdivisionCode();

				String countryCode = null;
				PostalZipCode postalZipCode = structured.getPostalZipCode();
				if(postalZipCode != null)
				{
					String postalCode = postalZipCode.getPostalCode();
					String zipCode = postalZipCode.getZipCode();
					if(postalCode != null)
					{
						countryCode = COUNTRY_CODE_CANADA;
						address.setPostalCode(postalCode);
					}
					else if(zipCode != null)
					{
						countryCode = COUNTRY_CODE_USA;
						address.setPostalCode(zipCode);
					}
				}

				if(subDivisionCode != null)
				{
					// if the country code is valid, use this instead of the implied code based on postal code
					if(subDivisionCode.startsWith(COUNTRY_CODE_CANADA))
					{
						countryCode = COUNTRY_CODE_CANADA;
					}
					else if(subDivisionCode.startsWith(COUNTRY_CODE_USA))
					{
						countryCode = COUNTRY_CODE_USA;
					}
				}
				address.setRegionCode(getSubregionCode(subDivisionCode));
				address.setCountryCode(countryCode);
			}
			else
			{
				address.setAddressLine1(importAddress.getFormatted());
			}
			address.setResidencyStatusCurrent(); //TODO how to tell if this is the main address
		}
		return address;
	}

	protected PartialDate toNullablePartialDate(DateFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			XMLGregorianCalendar xmlFullDate = fullOrPartial.getFullDate();
			XMLGregorianCalendar xmlYearMonth = fullOrPartial.getYearMonth();
			XMLGregorianCalendar xmlYearOnly = fullOrPartial.getYearOnly();

			if(xmlFullDate != null)
			{
				return new PartialDate(xmlFullDate.getYear(), xmlFullDate.getMonth(), xmlFullDate.getDay());
			}
			else if (xmlYearMonth != null)
			{
				return new PartialDate(xmlYearMonth.getYear(), xmlYearMonth.getMonth());
			}
			else if(xmlYearOnly != null)
			{
				return new PartialDate(xmlYearOnly.getYear());
			}
		}
		return null;
	}

	protected PartialDateTime toNullablePartialDateTime(DateTimeFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			XMLGregorianCalendar xmlFullDateTime = fullOrPartial.getFullDateTime();
			XMLGregorianCalendar xmlFullDate = fullOrPartial.getFullDate();
			XMLGregorianCalendar xmlYearMonth = fullOrPartial.getYearMonth();
			XMLGregorianCalendar xmlYearOnly = fullOrPartial.getYearOnly();

			if(xmlFullDateTime != null)
			{
				return new PartialDateTime(xmlFullDateTime.getYear(), xmlFullDateTime.getMonth(), xmlFullDateTime.getDay(),
						xmlFullDateTime.getHour(), xmlFullDateTime.getMinute(), xmlFullDateTime.getSecond());
			}
			else if(xmlFullDate != null)
			{
				return new PartialDateTime(xmlFullDate.getYear(), xmlFullDate.getMonth(), xmlFullDate.getDay());
			}
			else if (xmlYearMonth != null)
			{
				return new PartialDateTime(xmlYearMonth.getYear(), xmlYearMonth.getMonth());
			}
			else if(xmlYearOnly != null)
			{
				return new PartialDateTime(xmlYearOnly.getYear());
			}
		}
		return null;
	}

	protected LocalDateTime toNullableLocalDateTime(DateTimeFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			return ConversionUtils.fillPartialCalendar(
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
			return ConversionUtils.fillPartialCalendar(
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
			LocalDateTime dateTime = ConversionUtils.fillPartialCalendar(
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
			if(dateTime != null)
			{
				return dateTime.toLocalDate();
			}
		}
		return null;
	}
	protected LocalDate toNullableLocalDate(DateTimeFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			LocalDateTime dateTime = ConversionUtils.fillPartialCalendar(
					fullOrPartial.getFullDateTime(),
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
			if(dateTime != null)
			{
				return dateTime.toLocalDate();
			}
		}
		return null;
	}
}
