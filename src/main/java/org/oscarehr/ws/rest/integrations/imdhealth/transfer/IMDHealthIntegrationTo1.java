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

package org.oscarehr.ws.rest.integrations.imdhealth.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.oscarehr.integration.model.Integration;

import java.io.Serializable;

@Data
@NoArgsConstructor
@JsonIgnoreProperties
public class IMDHealthIntegrationTo1 implements Serializable
{
	private Integer integrationId;
	private String clientId;
	private Integer siteId;
	private String siteName;

	/**
	 * Maps the generic Integration class to it's usage for IMDHealth.
	 * @param integration Integration as stored in the database
	 * @return integration as used by IMDHealth
	 * @throws RuntimeException if integration is not an IMDHealth integration
	 */
	public static IMDHealthIntegrationTo1 fromIntegration(Integration integration)
	{
		if (!integration.getIntegrationType().equals(Integration.INTEGRATION_TYPE_IMD_HEALTH))
		{
			throw new RuntimeException();
		}

		IMDHealthIntegrationTo1 iMDIntegration = new IMDHealthIntegrationTo1();
		iMDIntegration.integrationId = integration.getId();
		iMDIntegration.clientId = integration.getRemoteId();

		if (integration.getSite() != null)
		{
			iMDIntegration.siteId = integration.getSite().getId();
			iMDIntegration.siteName = integration.getSite().getName();
		}

		return iMDIntegration;
	}
}
