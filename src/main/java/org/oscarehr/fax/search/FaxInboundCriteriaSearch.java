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
package org.oscarehr.fax.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class FaxInboundCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE
	{
		CreationDate
	}

	private Long faxAccountId;
	private Integer documentNo;
	private LocalDate startDate;
	private LocalDate endDate;

	private SORTMODE sortMode = SORTMODE.CreationDate;


	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();

		// left join demographic merged and only return the result if it isn't merged
		criteria.createAlias(alias + ".faxAccount", "fa", Criteria.INNER_JOIN);
		criteria.createAlias(alias + ".document", "doc", Criteria.INNER_JOIN);

		if(getFaxAccountId() != null)
		{
			criteria.add(Restrictions.eq("fa.id", getFaxAccountId()));
		}
		if(getDocumentNo() != null)
		{
			criteria.add(Restrictions.eq("doc.documentNo", getDocumentNo()));
		}
		if(getEndDate() != null)
		{
			criteria.add(Restrictions.le("createdAt", Timestamp.from(getEndDate().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC))));
		}
		if(getStartDate() != null)
		{
			criteria.add(Restrictions.ge("createdAt", Timestamp.from(getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC))));
		}
		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case CreationDate:
			default: criteria.addOrder(getOrder("createdAt")); break;
		}
	}

	public Long getFaxAccountId()
	{
		return faxAccountId;
	}

	public void setFaxAccountId(Long faxAccountId)
	{
		this.faxAccountId = faxAccountId;
	}

	public Integer getDocumentNo()
	{
		return documentNo;
	}

	public void setDocumentNo(Integer documentNo)
	{
		this.documentNo = documentNo;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public SORTMODE getSortMode()
	{
		return sortMode;
	}

	public void setSortMode(SORTMODE sortMode)
	{
		this.sortMode = sortMode;
	}
}
