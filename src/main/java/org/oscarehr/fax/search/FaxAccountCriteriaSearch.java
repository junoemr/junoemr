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

public class FaxAccountCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORTMODE
	{
		ID
	}

	private Long id;

	private SORTMODE sortMode = SORTMODE.ID;

	private Boolean integrationEnabledStatus = null;
	private Boolean inboundEnabledStatus = null;
	private Boolean outboundEnabledStatus = null;


	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{

		if(getId() != null)
		{
			criteria.add(Restrictions.eq("id", getId()));
		}

		if(getIntegrationEnabledStatus() != null)
		{
			criteria.add(Restrictions.eq("integrationEnabled", getIntegrationEnabledStatus()));
		}

		if(getInboundEnabledStatus() != null)
		{
			criteria.add(Restrictions.eq("inboundEnabled", getInboundEnabledStatus()));
		}

		if(getOutboundEnabledStatus() != null)
		{
			criteria.add(Restrictions.eq("outboundEnabled", getOutboundEnabledStatus()));
		}

		setOrderByCriteria(criteria);
		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case ID:
			default: criteria.addOrder(getOrder("id")); break;
		}
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Boolean getIntegrationEnabledStatus()
	{
		return integrationEnabledStatus;
	}

	public void setIntegrationEnabledStatus(Boolean integrationEnabledStatus)
	{
		this.integrationEnabledStatus = integrationEnabledStatus;
	}

	public Boolean getInboundEnabledStatus()
	{
		return inboundEnabledStatus;
	}

	public void setInboundEnabledStatus(Boolean inboundEnabledStatus)
	{
		this.inboundEnabledStatus = inboundEnabledStatus;
	}

	public Boolean getOutboundEnabledStatus()
	{
		return outboundEnabledStatus;
	}

	public void setOutboundEnabledStatus(Boolean outboundEnabledStatus)
	{
		this.outboundEnabledStatus = outboundEnabledStatus;
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
