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
package org.oscarehr.securityLog.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

public class SecurityLogCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE
	{
		CreationDate
	}
	private SORTMODE sortMode = SORTMODE.CreationDate;

	private LocalDate startDate;
	private LocalDate endDate;
	private String providerNo;
	private String contentType;

	private List<String> providerIdFilterList;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		if(getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("providerNo", getProviderNo()));
		}
		if(getContentType() != null)
		{
			criteria.add(Restrictions.eq("content", getContentType()));
		}
		else
		{
			criteria.add(Restrictions.isNotNull("content"));
		}

		if(getEndDate() != null)
		{
			criteria.add(Restrictions.le("created", Timestamp.from(getEndDate().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC))));
		}
		if(getStartDate() != null)
		{
			criteria.add(Restrictions.ge("created", Timestamp.from(getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC))));
		}

		if(getProviderIdFilterList() != null)
		{
			criteria.add(Restrictions.in("providerNo", getProviderIdFilterList()));
		}

		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case CreationDate:
			default: criteria.addOrder(getOrder("created")); break;
		}
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getContentType()
	{
		return contentType;
	}

	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public List<String> getProviderIdFilterList()
	{
		return providerIdFilterList;
	}

	public void setProviderIdFilterList(List<String> providerIdFilterList)
	{
		this.providerIdFilterList = providerIdFilterList;
	}
}
