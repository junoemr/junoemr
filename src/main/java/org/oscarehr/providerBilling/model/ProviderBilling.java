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
package org.oscarehr.providerBilling.model;


import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "provider_billing")
public class ProviderBilling extends AbstractModel<Integer>
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id = null;
	@Column(name = "provider_no")
	private Integer providerNo;
	@Column(name = "bc_rural_retention_code")
	private String bcRuralRetentionCode;
	@Column(name = "bc_rural_retention_name")
	private String bcRuralRetentionName;
	@Column(name = "bc_service_location_code")
	private String bcServiceLocationCode;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(Integer providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getBcRuralRetentionCode()
	{
		return bcRuralRetentionCode;
	}

	public void setBcRuralRetentionCode(String bcRuralRetentionCode)
	{
		this.bcRuralRetentionCode = bcRuralRetentionCode;
	}

	public String getBcRuralRetentionName()
	{
		return bcRuralRetentionName;
	}

	public void setBcRuralRetentionName(String bcRuralRetentionName)
	{
		this.bcRuralRetentionName = bcRuralRetentionName;
	}

	public String getBcServiceLocationCode()
	{
		return bcServiceLocationCode;
	}

	public void setBcServiceLocationCode(String bcServiceLocationCode)
	{
		this.bcServiceLocationCode = bcServiceLocationCode;
	}
}
