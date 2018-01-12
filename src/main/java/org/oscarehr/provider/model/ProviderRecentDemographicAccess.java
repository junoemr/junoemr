/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.provider.model;

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "provider_recent_demographic_access")
public class ProviderRecentDemographicAccess extends AbstractModel<ProviderRecentDemographicAccessId> implements Serializable
{
	@EmbeddedId
	private ProviderRecentDemographicAccessId providerRecentDemographicAccessId;

	@Column(name = "access_datetime", nullable = false)
	private Date accessDateTime = new Date();

	public ProviderRecentDemographicAccess()
	{
		providerRecentDemographicAccessId = new ProviderRecentDemographicAccessId();
	}
	public ProviderRecentDemographicAccess(Integer providerNo, Integer demographicNo)
	{
		this.setId(new ProviderRecentDemographicAccessId(providerNo, demographicNo));
	}

	@Override
	public ProviderRecentDemographicAccessId getId()
	{
		return providerRecentDemographicAccessId;
	}

	public void setId(ProviderRecentDemographicAccessId providerRecentDemographicAccessId)
	{
		this.providerRecentDemographicAccessId = providerRecentDemographicAccessId;
	}

	public Date getAccessDateTime()
	{
		return accessDateTime;
	}

	public void setAccessDateTime(Date accessDateTime)
	{
		this.accessDateTime = accessDateTime;
	}
	public void setAccessDateTimeToNow()
	{
		setAccessDateTime(new Date());
	}

	public Integer getDemographicNo()
	{
		return providerRecentDemographicAccessId.getDemographicNo();
	}
	public Integer getProviderNo()
	{
		return providerRecentDemographicAccessId.getProviderNo();
	}
}

@Embeddable
class ProviderRecentDemographicAccessId implements Serializable
{
	@Column(name = "demographic_no", nullable = false)
	private Integer demographicNo;
	@Column(name = "provider_no", nullable = false)
	private Integer providerNo;

	public ProviderRecentDemographicAccessId()
	{
		//default constructor
	}

	public ProviderRecentDemographicAccessId(Integer providerNo, Integer demographicNo)
	{
		this.demographicNo = demographicNo;
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

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(Integer providerNo)
	{
		this.providerNo = providerNo;
	}
}
