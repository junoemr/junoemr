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
import ca.cloudpractice.aqs.client.model.QueuedAppointmentStatus;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.aqs.conversion.IntegerToQueuedAppointmentMoveDtoConverter;
import org.oscarehr.integration.aqs.dao.QueuedAppointmentLinkDao;
import org.oscarehr.integration.aqs.exception.AqsCommunicationException;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.integration.aqs.model.QueuedAppointmentLink;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.model.MHAAppointment;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.OscarAuditLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QueuedAppointmentService extends BaseService
{
	@Autowired
	private QueuedAppointmentLinkDao queuedAppointmentLinkDao;

	@Autowired
	private AppointmentService mhaAppointmentService;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private org.oscarehr.appointment.service.Appointment appointmentService;

	@Autowired
	private SiteDao siteDao;

	@Autowired
	private IntegrationService integrationService;

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
			return new QueuedAppointment(getOrganizationApi(securityNo).getQueuedAppointment(queuedAppointmentId));
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
	 * @param loggedInInfo - the security no of the person performing the delete.
	 */
	public void deleteQueuedAppointment(UUID appointmentId, UUID queueId, String reason, LoggedInInfo loggedInInfo)
	{
		try
		{
			getOrganizationApi(loggedInInfo.getLoggedInSecurity().getSecurityNo()).dequeueAppointment(appointmentId, reason);

			OscarAuditLogger.getInstance().log(loggedInInfo, OscarAuditLogger.ACTION.AQS_CANCEL_APPOINTMENT.name(),
			                                   OscarAuditLogger.CONTENT.AQS.name(),
			                                   "Queued appointment [" + appointmentId + "] canceled with reason: " + reason);
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to delete appointment [" + appointmentId + "] from the AQS server", apiException);
		}
	}

	/**
	 * move an appointment to the specified queue position
	 * @param appointmentId - the queued appointment to move
	 * @param queuePosition - the new position of said appointment
	 * @param securityNo - the security no of the user performing this action
	 */
	public QueuedAppointment moveQueuedAppointment(UUID appointmentId, Integer queuePosition, Integer securityNo)
	{
		try
		{
			return new QueuedAppointment(getOrganizationApi(securityNo).moveAppointment(appointmentId, (new IntegerToQueuedAppointmentMoveDtoConverter()).convert(queuePosition)));
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to update queued appointment [" + appointmentId + "]'s position on the AQS server", apiException);
		}
	}

	/**
	 * Update a queued appointment on the AQS server
	 * @param queuedAppointment - the appointment to update
	 * @param loggedInInfo - the users logged in info
	 */
	public void updateQueuedAppointment(QueuedAppointment queuedAppointment, LoggedInInfo loggedInInfo)
	{
		try
		{
			getOrganizationApi(loggedInInfo.getLoggedInSecurity().getSecurityNo())
							.updateQueuedAppointment(queuedAppointment.getId(), queuedAppointment.asQueuedAppointmentInput());

			OscarAuditLogger.getInstance().log(loggedInInfo, OscarAuditLogger.ACTION.AQS_UPDATE_APPOINTMENT.name(),
			                                   OscarAuditLogger.CONTENT.AQS.name(),
			                                   "Queued appointment [" + queuedAppointment.getId() + "] updated");
		}
		catch (ApiException apiException)
		{
			throw new AqsCommunicationException("Failed to update queued appointment [" + queuedAppointment.getId() + "] on the AQS server", apiException);
		}
	}

	/**
	 * move a queued appointment in to the schedule updating all necessary servers (AQS & MHA)
	 * @param queuedAppointmentId - the id of the queued appointment to move in to the schedule
	 * @param queueId - the queue the above appointment is contained in
	 * @param providerNo - the provider who's schedule the appointment is getting moved to
	 * @param siteId - the site the appointment is getting booked in to
	 * @param loggedInInfo - logged in info
	 * @param httpServletRequest - http servlet request
	 * @return - the newly scheduled appointment
	 */
	@Transactional
	public Appointment scheduleQueuedAppointment(UUID queuedAppointmentId, UUID queueId, String providerNo,
	                                             Integer siteId, LoggedInInfo loggedInInfo, HttpServletRequest httpServletRequest)
	{
		QueuedAppointment queuedAppointment = getQueuedAppointment(queuedAppointmentId, queueId, loggedInInfo.getLoggedInSecurity().getSecurityNo());
		Demographic demographic = demographicDao.find(queuedAppointment.getDemographicNo());
		Date now = new Date();

		if (queuedAppointment.getStatus() != QueuedAppointmentStatus.QUEUED)
		{
			throw new ValidationException("Queued Appointment [" + queuedAppointment.getId() +"] is no longer in the queue");
		}

		// create new juno appointment
		Appointment appointment = new Appointment();
		appointment.setProviderNo(providerNo);
		appointment.setDemographicNo(demographic.getId());
		appointment.setAppointmentDate(now);
		appointment.setStartTime(now);
		appointment.setCreateDateTime(now);
		appointment.setStatus(Appointment.TODO);
		appointment.setCreator(providerNo);
		appointment.setReason(queuedAppointment.getReason());
		appointment.setNotes(queuedAppointment.getNotes());
		appointment.setName(demographic.getDisplayName());
		appointment.setIsVirtual(true);

		// book 15 min appointment
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, 15);
		appointment.setEndTime(calendar.getTime());

		// set site, if provided
		if (siteId != null)
		{
			Site site = siteDao.find(siteId);
			appointment.setLocation(site.getName());
		}

		// save
		Appointment newAppointment = appointmentService.saveNewAppointment(appointment, loggedInInfo, httpServletRequest, false);

		// link to AQS queued appointment id
		QueuedAppointmentLink queuedAppointmentLink = new QueuedAppointmentLink();
		queuedAppointmentLink.setAppointment(newAppointment);
		queuedAppointmentLink.setQueueId(queueId.toString());
		queuedAppointmentLink.setQueuedAppointmentId(queuedAppointment.getId().toString());
		queuedAppointmentLinkDao.persist(queuedAppointmentLink);

		// mark appointment as schedule on AQS server
		queuedAppointment.setStatus(QueuedAppointmentStatus.SCHEDULED);
		updateQueuedAppointment(queuedAppointment, loggedInInfo);

		// book the appointment in to MHA
		mhaAppointmentService.bookTelehealthAppointment(loggedInInfo, newAppointment, false, UUID.fromString(queuedAppointment.getCreatedBy()));

		// link the mha appointments telehealth session to the AQS telehealth session
		Integration integration = integrationService.findMhaIntegration(StringUtils.trimToNull(appointment.getLocation()));
		MHAAppointment mhaAppointment = mhaAppointmentService.getAppointment(integration, newAppointment.getId());
		mhaAppointmentService.linkAppointmentToAqsTelehealth(integration, loggedInInfo, mhaAppointment, queuedAppointmentId);

		OscarAuditLogger.getInstance().log(loggedInInfo, OscarAuditLogger.ACTION.AQS_SCHEDULE_APPOINTMENT.name(),
		                                   OscarAuditLogger.CONTENT.AQS.name(), demographic.getId(),
		                                   "Queued appointment [" + queuedAppointment.getId() + "] scheduled in to provider [" +
						                                    providerNo +"]'s schedule");

		return newAppointment;
	}
}
