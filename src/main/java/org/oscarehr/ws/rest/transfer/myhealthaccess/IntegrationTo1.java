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
package org.oscarehr.ws.rest.transfer.myhealthaccess;

import org.oscarehr.integration.model.Integration;

public class IntegrationTo1
{
	private Integer id;
	private String remoteId;
	private String apiKey;
	private Integer siteId;
	private String siteName;
	private String integrationType;

	public IntegrationTo1(Integration integration)
	{
		this.id = integration.getId();
		this.remoteId = integration.getRemoteId();
		this.apiKey = integration.getApiKey();
		this.siteName = integration.getSite().getName();
		this.siteId = integration.getSite().getId();
		this.integrationType = integration.getIntegrationType();
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public String getApiKey()
	{
		return apiKey;
	}

	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}

	public Integer getSiteId()
	{
		return siteId;
	}

	public void setSiteId(Integer siteId)
	{
		this.siteId = siteId;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getIntegrationType()
	{
		return integrationType;
	}

	public void setIntegrationType(String integrationType)
	{
		this.integrationType = integrationType;
	}
}
