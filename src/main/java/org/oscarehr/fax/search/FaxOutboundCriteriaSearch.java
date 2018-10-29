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
import org.oscarehr.fax.model.FaxOutbound;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class FaxOutboundCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE
	{
		DemographicNo,
		CreationDate
	}

	private String sentTo;
	private String providerNo;
	private Integer demographicNo;
	private FaxOutbound.FileType fileType;
	private FaxOutbound.Status status;
	private Long faxAccountId;
	private LocalDate beforeDate;

	private SORTMODE sortMode = SORTMODE.CreationDate;


	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		String alias = criteria.getAlias();

		// left join demographic merged and only return the result if it isn't merged
		criteria.createAlias(alias + ".faxAccount", "fa", Criteria.INNER_JOIN);

		if(getFaxAccountId() != null)
		{
			criteria.add(Restrictions.eq("fa.id", getFaxAccountId()));
		}
		if(getFileType() != null)
		{
			criteria.add(Restrictions.eq("fileType", getFileType()));
		}
		if(getSentTo() != null)
		{
			criteria.add(Restrictions.eq("sentTo", getSentTo()));
		}
		if(getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("providerNo", getProviderNo()));
		}
		if(getDemographicNo() != null)
		{
			criteria.add(Restrictions.eq("demographicNo", getDemographicNo()));
		}
		if(getStatus() != null)
		{
			criteria.add(Restrictions.eq("status", getStatus()));
		}
		if(getBeforeDate() != null)
		{
			criteria.add(Restrictions.le("createdAt", Timestamp.from(getBeforeDate().atStartOfDay().toInstant(ZoneOffset.UTC))));
		}
		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case DemographicNo: criteria.addOrder(getOrder("demographicNo")); break;
			case CreationDate:
			default: criteria.addOrder(getOrder("createdAt")); break;
		}
	}

	public String getSentTo()
	{
		return sentTo;
	}

	public void setSentTo(String sentTo)
	{
		this.sentTo = sentTo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public FaxOutbound.FileType getFileType()
	{
		return fileType;
	}

	public void setFileType(FaxOutbound.FileType fileType)
	{
		this.fileType = fileType;
	}

	public FaxOutbound.Status getStatus()
	{
		return status;
	}

	public void setStatus(FaxOutbound.Status status)
	{
		this.status = status;
	}

	public Long getFaxAccountId()
	{
		return faxAccountId;
	}

	public void setFaxAccountId(Long faxAccountId)
	{
		this.faxAccountId = faxAccountId;
	}

	public LocalDate getBeforeDate()
	{
		return beforeDate;
	}

	public void setBeforeDate(LocalDate beforeDate)
	{
		this.beforeDate = beforeDate;
	}
}
