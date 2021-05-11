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

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.dataMigration.mapper.AbstractExportMapper;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.AddressStructured;
import xml.cds.v5_0.AddressType;
import xml.cds.v5_0.DateFullOrPartial;
import xml.cds.v5_0.DateTimeFullOrPartial;
import xml.cds.v5_0.LifeStage;
import xml.cds.v5_0.ObjectFactory;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.PostalZipCode;
import xml.cds.v5_0.ResidualInformation;
import xml.cds.v5_0.YnIndicator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public abstract class AbstractCDSExportMapper<I, E> extends AbstractExportMapper<I, E>
{
	protected final ObjectFactory objectFactory;

	public AbstractCDSExportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}


	public ObjectFactory getObjectFactory()
	{
		return this.objectFactory;
	}

	/* ==== common helper methods for cds ==== */

	protected xml.cds.v5_0.Address toCdsAddress(Address addressModel, AddressType addressType)
	{
		xml.cds.v5_0.Address cdsAddress = null;
		if(addressModel != null)
		{
			cdsAddress = objectFactory.createAddress();
			AddressStructured structured = objectFactory.createAddressStructured();
			PostalZipCode postalZipCode = objectFactory.createPostalZipCode();
			postalZipCode.setPostalCode(addressModel.getPostalCode());

			structured.setLine1(addressModel.getAddressLine1());
			structured.setLine2(addressModel.getAddressLine2());
			structured.setCity(addressModel.getCity());
			structured.setCountrySubdivisionCode(addressModel.getRegionCode());
			structured.setPostalZipCode(postalZipCode);

			cdsAddress.setStructured(structured);
			cdsAddress.setAddressType(addressType);
		}
		return cdsAddress;
	}

	protected ResidualInformation.DataElement createResidualInfoDataElement(CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType, String name, String value)
	{
		ResidualInformation.DataElement dataElement = objectFactory.createResidualInformationDataElement();
		dataElement.setDataType(dataType.name());
		dataElement.setName(name);
		dataElement.setContent(value);

		return dataElement;
	}

	protected void addNonNullDataElements(ResidualInformation residualInformation, CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType, String name, String value)
	{
		if(value != null && !value.isEmpty())
		{
			residualInformation.getDataElement().add(createResidualInfoDataElement(dataType, name, value));
		}
	}
	protected void addNonNullDataElements(ResidualInformation residualInformation, String name, LocalDate localDate)
	{
		addNonNullDataElements(residualInformation, CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE, name, ConversionUtils.toDateString(localDate));
	}
	protected void addNonNullDataElements(ResidualInformation residualInformation, String name, PartialDate partialDate)
	{
		if(partialDate != null)
		{
			CDSConstants.RESIDUAL_INFO_DATA_TYPE dataType;
			if(partialDate.isFullDate())
			{
				dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE;
			}
			else
			{
				dataType = CDSConstants.RESIDUAL_INFO_DATA_TYPE.DATE_PARTIAL;
			}
			addNonNullDataElements(residualInformation, dataType, name, partialDate.toISOString());
		}
	}

	protected DateTimeFullOrPartial toNullableDateTimeFullOrPartial(LocalDateTime localDateTime, LocalDateTime defaultDateTime)
	{
		DateTimeFullOrPartial dateTimeFullOrPartial = null;
		XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(localDateTime);
		if(calendar != null)
		{
			dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
			dateTimeFullOrPartial.setFullDateTime(calendar);
		}
		else if(defaultDateTime != null)
		{
			dateTimeFullOrPartial = this.toNullableDateTimeFullOrPartial(defaultDateTime, null);
		}
		return dateTimeFullOrPartial;
	}

	protected DateTimeFullOrPartial toNullableDateTimeFullOrPartial(LocalDateTime localDateTime)
	{
		return this.toNullableDateTimeFullOrPartial(localDateTime, null);
	}

	protected DateTimeFullOrPartial toNullableDateTimeFullOrPartial(LocalDate localDate)
	{
		if(localDate == null) return null;
		return this.toNullableDateTimeFullOrPartial(localDate.atStartOfDay(), null);
	}

	protected DateFullOrPartial toNullableDateFullOrPartial(LocalDate localDate, LocalDate defaultDate)
	{
		DateFullOrPartial dateFullOrPartial = null;
		XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(localDate);
		if(calendar != null)
		{
			dateFullOrPartial = objectFactory.createDateFullOrPartial();
			dateFullOrPartial.setFullDate(calendar);
		}
		else if(defaultDate != null)
		{
			dateFullOrPartial = this.toNullableDateFullOrPartial(defaultDate, null);
		}
		return dateFullOrPartial;
	}
	protected DateFullOrPartial toNullableDateFullOrPartial(LocalDate localDate)
	{
		return this.toNullableDateFullOrPartial(localDate, null);
	}

	protected DateFullOrPartial toNullableDateFullOrPartial(PartialDate partialDate, LocalDate defaultDate)
	{
		DateFullOrPartial dateFullOrPartial = toNullableDateFullOrPartial(partialDate);
		if(dateFullOrPartial == null)
		{
			dateFullOrPartial = toNullableDateFullOrPartial(defaultDate);
		}
		return dateFullOrPartial;
	}

	protected DateFullOrPartial toNullableDateFullOrPartial(PartialDate partialDate)
	{
		if(partialDate == null) return null;

		DateFullOrPartial dateFullOrPartial = objectFactory.createDateFullOrPartial();
		XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(partialDate.toLocalDate());

		if(partialDate.isFullDate())
		{
			dateFullOrPartial.setFullDate(calendar);
		}
		else if(partialDate.isYearMonth())
		{
			dateFullOrPartial.setYearMonth(calendar);
		}
		else if(partialDate.isYearOnly())
		{
			dateFullOrPartial.setYearOnly(calendar);
		}
		return dateFullOrPartial;
	}

	protected DateTimeFullOrPartial toNullableDateTimeFullOrPartial(PartialDateTime partialDateTime)
	{
		if(partialDateTime == null) return null;

		DateTimeFullOrPartial dateTimeFullOrPartial = objectFactory.createDateTimeFullOrPartial();
		XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(partialDateTime.toLocalDateTime());

		if(partialDateTime.isFullDateTime())
		{
			dateTimeFullOrPartial.setFullDateTime(calendar);
		}
		else if(partialDateTime.isFullDate())
		{
			dateTimeFullOrPartial.setFullDate(calendar);
		}
		else if(partialDateTime.isYearMonth())
		{
			dateTimeFullOrPartial.setYearMonth(calendar);
		}
		else if(partialDateTime.isYearOnly())
		{
			dateTimeFullOrPartial.setYearOnly(calendar);
		}

		return dateTimeFullOrPartial;
	}

	protected PersonNameSimple toPersonNameSimple(Provider provider)
	{
		if(provider == null)
		{
			return null;
		}
		PersonNameSimple personNameSimple = objectFactory.createPersonNameSimple();
		personNameSimple.setFirstName(provider.getFirstName());
		personNameSimple.setLastName(provider.getLastName());
		return personNameSimple;
	}

	protected LifeStage getLifeStage(String lifeStage)
	{
		if(EnumUtils.isValidEnum(LifeStage.class, lifeStage))
		{
			return LifeStage.fromValue(lifeStage);
		}
		return null;
	}

	protected YnIndicator toYnIndicator(Boolean indicator)
	{
		YnIndicator ynIndicator = null;
		if(indicator != null)
		{
			ynIndicator = objectFactory.createYnIndicator();
			ynIndicator.setBoolean(indicator);
		}
		return ynIndicator;
	}

	protected String toYnIndicatorString(Boolean indicator)
	{
		return ((indicator != null) && indicator) ? CDSConstants.Y_INDICATOR_TRUE : CDSConstants.Y_INDICATOR_FALSE;
	}
}
