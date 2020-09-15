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
package org.oscarehr.integration.aqs.service;

import ca.cloudpractice.aqs.client.ApiException;
import org.oscarehr.integration.aqs.conversion.AppointmentQueueIntegrationModelConverter;
import org.oscarehr.integration.aqs.conversion.AppointmentQueueIntegrationTransferConverter;
import org.oscarehr.integration.aqs.conversion.ContactDtoContactConverter;
import org.oscarehr.integration.aqs.exception.AqsCommunicationException;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("aqs.AppointmentQueueService")
public class AppointmentQueueService extends BaseService
{
	@Autowired
	private AppointmentQueueIntegrationTransferConverter integrationTransferConverter;

	@Autowired
	private AppointmentQueueIntegrationModelConverter integrationModelConverter;

	public List<AppointmentQueue> getAppointmentQueues(Integer securityNo)
	{
		try
		{
			return integrationTransferConverter.convert(getOrganizationApi(securityNo).getAllQueues());
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to fetch appointment queues from AQS server", apiException);
		}
	}

	public AppointmentQueue createAppointmentQueue(AppointmentQueue queue, Integer securityNo)
	{
		try
		{
			return integrationTransferConverter.convert(getOrganizationApi(securityNo).createQueue(
					integrationModelConverter.convert(queue)));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed create new appointment queue on the AQS server", apiException);
		}
	}

	public AppointmentQueue getAppointmentQueue(UUID queueId, Integer securityNo)
	{
		try
		{
			return integrationTransferConverter.convert(getOrganizationApi(securityNo).getQueue(queueId));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to fetch appointment queue [" + queueId + "] from AQS server", apiException);
		}
	}

	public AppointmentQueue updateAppointmentQueue(UUID queueId, AppointmentQueue queue, Integer securityNo)
	{
		try
		{
			return integrationTransferConverter.convert(getOrganizationApi(securityNo).updateQueue(
					queueId, integrationModelConverter.convert(queue)));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to update appointment queue [" + queueId + "] on the AQS server", apiException);
		}
	}

	public void deleteAppointmentQueue(UUID queueId, Integer securityNo)
	{
		try
		{
			getOrganizationApi(securityNo).deleteQueue(queueId);
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to delete appointment queue [" + queueId + "] on the AQS server", apiException);
		}
	}

	public List<Contact> getAppointmentQueueContacts(UUID queueId, Integer securityNo)
	{
		try
		{
			return new ContactDtoContactConverter().convert(getOrganizationApi(securityNo).getQueueContacts(queueId));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to fetch appointment queue contacts for queue with id [" + queueId + "] from AQS server", apiException);
		}
	}

	public Contact addAppointmentQueueContact(UUID queueId, UUID contactId, Integer securityNo)
	{
		try
		{
			return new ContactDtoContactConverter().convert(getOrganizationApi(securityNo).addQueueContact(queueId, contactId));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to add contact [" + contactId + "] to queue [" +queueId + "] on the AQS server", apiException);
		}
	}

	public void removeAppointmentQueueContact(UUID queueId, UUID contactId, Integer securityNo)
	{
		try
		{
			getOrganizationApi(securityNo).removeQueueContact(queueId, contactId);
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to remove contact [" + contactId + "] from queue [" + queueId + "] on the AQS server", apiException);
		}
	}
}
