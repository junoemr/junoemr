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
package org.oscarehr.dataMigration.mapper.hrm.out;

import org.oscarehr.dataMigration.mapper.AbstractExportMapper;
import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.ObjectFactory;
import xml.hrm.v4_3.PersonNameSimple;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public abstract class AbstractHRMExportMapper<I, E> extends AbstractExportMapper<I, E>
{
	protected final ObjectFactory objectFactory;

	public AbstractHRMExportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}


	public ObjectFactory getObjectFactory()
	{
		return this.objectFactory;
	}

	/* ==== common helper methods for cds ==== */

	protected DateFullOrPartial toNullableDateFullOrPartial(LocalDate localDate)
	{
		DateFullOrPartial dateFullOrPartial = null;
		if(localDate != null)
		{
			dateFullOrPartial = objectFactory.createDateFullOrPartial();
			XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(localDate);
			dateFullOrPartial.setFullDate(calendar);
		}
		return dateFullOrPartial;
	}

	protected DateFullOrPartial toNullableDateFullOrPartial(LocalDateTime localDateTime)
	{
		DateFullOrPartial dateFullOrPartial = null;
		if(localDateTime != null)
		{
			dateFullOrPartial = objectFactory.createDateFullOrPartial();
			XMLGregorianCalendar calendar = ConversionUtils.toNullableXmlGregorianCalendar(localDateTime);
			dateFullOrPartial.setDateTime(calendar);
		}
		return dateFullOrPartial;
	}

	protected DateFullOrPartial toNullableDateFullOrPartial(PartialDate partialDate)
	{
		DateFullOrPartial dateFullOrPartial = null;
		if(partialDate != null)
		{
			dateFullOrPartial = objectFactory.createDateFullOrPartial();
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
		}
		return dateFullOrPartial;
	}

	protected PersonNameSimple toPersonNameSimple(Provider provider)
	{
		PersonNameSimple personNameSimple = null;
		if(provider != null)
		{
			personNameSimple = objectFactory.createPersonNameSimple();
			personNameSimple.setFirstName(provider.getFirstName());
			personNameSimple.setLastName(provider.getLastName());
		}
		return personNameSimple;
	}
}