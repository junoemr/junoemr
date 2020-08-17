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
import org.oscarehr.integration.aqs.exception.AqsCommunicationException;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QueuedAppointmentService extends BaseService
{
	/**
	 * calls through to lower definition of getAppointmentsInQueue
	 * @param appointmentQueue - queue object to get appointments for
	 * @return - list of queued appointments
	 */
	public List<QueuedAppointment> getAppointmentsInQueue(AppointmentQueue appointmentQueue, Integer securityNo)
	{
		return getAppointmentsInQueue(appointmentQueue.getRemoteId(), securityNo);
	}

	/**
	 * get a list of appointments in the queue
	 * @param queueId - the queue to get the appointment list for
	 * @return - list of queued appointments
	 */
	public List<QueuedAppointment> getAppointmentsInQueue(UUID queueId, Integer securityNo)
	{
		try
		{
			return getOrganizationApi(securityNo).getAllAppointments(queueId).stream().map(QueuedAppointment::new).collect(Collectors.toList());
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to get appointments in queue [" + queueId + "] from the AQS server", apiException);
		}
	}

	/**
	 * get a queued appointment
	 * @param queuedAppointmentId - the queued appointment id to get
	 * @param queueId - the queue from which to fetch the appointment
	 * @param securityNo - the security no of the user performing the action
	 * @return - the queued appointment
	 */
	public QueuedAppointment getQueuedAppointment(UUID queuedAppointmentId, UUID queueId, Integer securityNo)
	{
		try
		{
			return new QueuedAppointment(getOrganizationApi(securityNo).getAppointment(queuedAppointmentId, queueId));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to get appointments in queue [" + queueId + "] from the AQS server", apiException);
		}
	}

	/**
	 * delete an appointment form the appointment queue
	 * @param appointmentId - the remote id of the appointment to delete
	 * @param queueId - the queue in which the appointment is contained
	 * @param reason - the reason for deleting the appointment free text
	 * @param securityNo - the security no of the person performing the delete.
	 */
	public void deleteQueuedAppointment(UUID appointmentId, UUID queueId, String reason, Integer securityNo)
	{
		try
		{
			getOrganizationApi(securityNo).dequeueAppointment(appointmentId, reason);
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to get appointments in queue [" + appointmentId + "] from the AQS server", apiException);
		}
	}
}
