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
import xml.hrm.v4_3.ReportClass;

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
		if(fullOrPartial != null)
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
	
	protected PartialDateTime toPartialDateTime(DateFullOrPartial xmlDate)
	{
		if (xmlDate == null)
		{
			return null;
		}
		
		return PartialDateTime.from(toNullableLocalDateTime(xmlDate));
	}
	
	// TODO:  This will almost certainly blow up, a reviewer is not a provider, it's just an OHIP number
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
		reviewer.setFirstName("HRM Document");      // TODO
		reviewer.setLastName("HRM Document");       // TODO
		
		reviewers.add(reviewer);
		
		return reviewers;
	}
	
	// TODO:  This will almost certainly blow up, the Author is a SimpleName, not a provider
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
	
	
	protected HrmDocument.REPORT_CLASS getReportClass(ReportClass clazz)
	{
		if (clazz == null)
		{
			return null;
		}
		
		return HrmDocument.REPORT_CLASS.fromValueString(clazz.value());
	}
	
	protected HrmDocument.REPORT_STATUS getStatus(String status)
	{
		if (status == null)
		{
			return null;
		}
		
		return HrmDocument.REPORT_STATUS.fromValueString(status);
	}
}
