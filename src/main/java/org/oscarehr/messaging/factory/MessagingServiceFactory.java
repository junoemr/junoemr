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

package org.oscarehr.messaging.factory;

import org.oscarehr.messaging.backend.myhealthaccess.service.MessagingService;
import org.oscarehr.messaging.model.MessagingBackendType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagingServiceFactory
{
	org.oscarehr.messaging.backend.myhealthaccess.service.MessagingService mhaMessagingService;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public MessagingServiceFactory(org.oscarehr.messaging.backend.myhealthaccess.service.MessagingService mhaMessagingService)
	{
		this.mhaMessagingService = mhaMessagingService;
	}

	/**
	 * get messaging service by backed type.
	 * @param backendType - the messaging backend to use.
 	 * @return - a messaging service built on the specified backend.
	 * @throws IllegalArgumentException - if backend type is not supported.
	 */
	public MessagingService build(MessagingBackendType backendType)
	{
		switch(backendType)
		{
			case MHA:
				return this.mhaMessagingService;
			default:
				throw new IllegalArgumentException("Backend type [" + backendType.name() + "] is not supported");
		}
	}
}
