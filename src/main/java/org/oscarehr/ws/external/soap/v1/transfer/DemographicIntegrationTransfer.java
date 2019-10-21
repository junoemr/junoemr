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
package org.oscarehr.ws.external.soap.v1.transfer;

import java.io.Serializable;

public class DemographicIntegrationTransfer implements Serializable
{
	private String remoteId;
	private String integrationType;
	private String createdBySource;
	private String createdByRemoteId;

	public String getIntegrationType()
	{
		return integrationType;
	}

	public void setIntegrationType(String integrationType)
	{
		this.integrationType = integrationType;
	}

	public String getCreatedBySource()
	{
		return createdBySource;
	}

	public void setCreatedBySource(String createdBySource)
	{
		this.createdBySource = createdBySource;
	}

	public String getCreatedByRemoteId()
	{
		return createdByRemoteId;
	}

	public void setCreatedByRemoteId(String createdByRemoteId)
	{
		this.createdByRemoteId = createdByRemoteId;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}
}
