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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class RecentDemographicAccessPK implements Serializable
{
	@Column(name = "demographic_no", nullable = false)
	private Integer demographicNo;
	@Column(name = "provider_no", nullable = false)
	private Integer providerNo;

	public RecentDemographicAccessPK()
	{
		//default constructor
	}

	public RecentDemographicAccessPK(Integer providerNo, Integer demographicNo)
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

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof RecentDemographicAccessPK)
		{
			RecentDemographicAccessPK pk = (RecentDemographicAccessPK) obj;
			return (demographicNo.equals(pk.demographicNo) && providerNo.equals(pk.providerNo));
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(demographicNo, providerNo);
	}
}
