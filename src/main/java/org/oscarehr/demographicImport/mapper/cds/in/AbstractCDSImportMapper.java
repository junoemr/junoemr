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

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.DateFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.DateTimeFullOrPartial;
import org.oscarehr.common.xml.cds.v5_0.model.LifeStage;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameSimple;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumberType;
import org.oscarehr.common.xml.cds.v5_0.model.ResidualInformation;
import org.oscarehr.common.xml.cds.v5_0.model.YnIndicator;
import org.oscarehr.demographicImport.mapper.AbstractImportMapper;
import org.oscarehr.demographicImport.model.common.PartialDate;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.common.PhoneNumber;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.Y_INDICATOR_TRUE;

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

	protected ResidualInformation.DataElement getResidualDataElement(ResidualInformation residualInformation, String ... keys)
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

	protected PhoneNumber getPhoneNumber(org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber importNumber)
	{
		if(importNumber == null)
		{
			return null;
		}
		PhoneNumber phoneNumber = new PhoneNumber();

		//TODO handle discrete phone number cases
		for(JAXBElement<String> phoneElement : importNumber.getContent())
		{
			String key = phoneElement.getName().getLocalPart();
			String value = phoneElement.getValue();
			if("phoneNumber".equals(key) || "number".equals(key))
			{
				phoneNumber.setNumber(value);
			}
			else if("extension".equals(key))
			{
				phoneNumber.setExtension(value);
			}
			else
			{
				logger.error("Unknown Phone number component key: '" + key + "'");
			}
		}

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
			return fillPartialCalendar(
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
			return fillPartialCalendar(
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
			LocalDateTime dateTime = fillPartialCalendar(
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
			LocalDateTime dateTime = fillPartialCalendar(
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

	private LocalDateTime fillPartialCalendar(
			XMLGregorianCalendar fullDateTime,
			XMLGregorianCalendar fullDate,
			XMLGregorianCalendar yearMonth,
			XMLGregorianCalendar yearOnly)
	{
		if(fullDateTime != null)
		{
			return ConversionUtils.toNullableLocalDateTime(fullDateTime);
		}
		else
		{
			return fillPartialCalendar(fullDate, yearMonth, yearOnly);
		}
	}

	private LocalDateTime fillPartialCalendar(
			XMLGregorianCalendar fullDate,
			XMLGregorianCalendar yearMonth,
			XMLGregorianCalendar yearOnly)
	{
		XMLGregorianCalendar xmlGregorianCalendar = null;
		if(fullDate != null)
		{
			xmlGregorianCalendar = fullDate;
		}
		else if (yearMonth != null)
		{
			xmlGregorianCalendar = yearMonth;
			xmlGregorianCalendar.setDay(1);
		}
		else if(yearOnly != null)
		{
			xmlGregorianCalendar = yearOnly;
			xmlGregorianCalendar.setMonth(1);
			xmlGregorianCalendar.setDay(1);
		}

		if(xmlGregorianCalendar != null)
		{
			xmlGregorianCalendar.setHour(0);
			xmlGregorianCalendar.setMinute(0);
			xmlGregorianCalendar.setSecond(0);
		}
		return ConversionUtils.toNullableLocalDateTime(xmlGregorianCalendar);
	}
}
