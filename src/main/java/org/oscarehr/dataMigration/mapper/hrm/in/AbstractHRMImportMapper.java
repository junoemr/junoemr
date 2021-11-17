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

import org.oscarehr.dataMigration.mapper.AbstractImportMapper;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.hrm.v4_3.DateFullOrPartial;
import xml.hrm.v4_3.ObjectFactory;
import xml.hrm.v4_3.PersonNameSimple;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public abstract class AbstractHRMImportMapper<I, E> extends AbstractImportMapper<I, E>
{
	protected final ObjectFactory objectFactory;
	
	public AbstractHRMImportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}
	
	protected LocalDateTime toNullableLocalDateTime(DateFullOrPartial fullOrPartial)
	{
		if (fullOrPartial != null)
		{
			return ConversionUtils.fillPartialCalendar(
					fullOrPartial.getDateTime(),
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
		}
		return null;
	}
	
	protected LocalDate toNullableLocalDate(DateFullOrPartial fullOrPartial)
	{
		if (fullOrPartial != null)
		{
			LocalDateTime dateTime = ConversionUtils.fillPartialCalendar(
					fullOrPartial.getDateTime(),
					fullOrPartial.getFullDate(),
					fullOrPartial.getYearMonth(),
					fullOrPartial.getYearOnly());
			
			if (dateTime != null)
			{
				return dateTime.toLocalDate();
			}
		}
		return null;
	}
	
	protected PartialDateTime toPartialDateTime(DateFullOrPartial fullOrPartial)
	{
		if(fullOrPartial != null)
		{
			XMLGregorianCalendar xmlFullDateTime = fullOrPartial.getDateTime();
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
	
	protected List<Reviewer> stubReviewers(String reviewerOHIPNo, DateFullOrPartial reviewDate)
	{
		if (reviewerOHIPNo == null)
		{
			return null;
		}
		
		List<Reviewer> reviewers = new ArrayList<>();
		Reviewer reviewer = new Reviewer();
		reviewer.setOhipNumber(reviewerOHIPNo);
		reviewer.setReviewDateTime(toPartialDateTime(reviewDate));
		reviewer.setFirstName("Reviewer");
		reviewer.setLastName("OMD HRM");
		
		reviewers.add(reviewer);
		
		return reviewers;
	}
	
	protected Provider stubProviderFromPersonName(PersonNameSimple name)
	{
		if (name == null)
		{
			return null;
		}
		
		Provider provider = new Provider();
		provider.setFirstName(name.getFirstName());
		provider.setLastName(name.getLastName());
		
		return provider;
	}
	
	
	protected HrmDocument.ReportClass fromNullableString(xml.hrm.v4_3.ReportClass clazz)
	{
		if (clazz == null)
		{
			return null;
		}
		
		return HrmDocument.ReportClass.fromValueString(clazz.value());
	}
	
	protected HrmDocument.ReportStatus fromNullableString(String status)
	{
		if (status == null)
		{
			return null;
		}
		
		return HrmDocument.ReportStatus.fromValueString(status);
	}
}