/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.ws.rest.myhealthaccess.clinic;

import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.messaging.backend.myhealthaccess.model.MhaMessageable;
import org.oscarehr.messaging.model.MessageableType;
import org.oscarehr.messaging.service.MessagingService;
import org.oscarehr.ws.rest.AbstractServiceImpl;

public class MessagingBaseWebService extends AbstractServiceImpl
{
	protected MessagingService messagingService;
	protected IntegrationDao integrationDao;

	public MessagingBaseWebService(MessagingService messagingService, IntegrationDao integrationDao)
	{
		this.messagingService = messagingService;
		this.integrationDao = integrationDao;
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	protected MhaMessageable messageableFromIntegrationId(String integrationId)
	{
		Integration integration = this.integrationDao.find(Integer.parseInt(integrationId));

		return new MhaMessageable(
				integration.getRemoteId(),
				integration.getSite() == null ? "" : integration.getSite().getName(),
				MessageableType.MHA_CLINIC);
	}

	protected MhaMessageable messageableFromIdType(String id, MessageableType type)
	{
		if (id != null && type != null)
		{
			return new MhaMessageable(id, "", type);
		}
		else
		{
			return null;
		}
	}
}
