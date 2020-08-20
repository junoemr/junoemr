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

package org.oscarehr.ws.rest.integrations.aqs;

import ca.cloudpractice.aqs.client.model.QueuedAppointmentStatus;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.aqs.dao.QueuedAppointmentLinkDao;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.integration.aqs.model.QueuedAppointmentLink;
import org.oscarehr.integration.aqs.service.QueuedAppointmentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.oscarehr.ws.rest.integrations.aqs.transfer.BookQueuedAppointmentTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.AppointmentTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.validation.ValidationException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Path("/integrations/aqs/queue/{queueId}/appointment")
@Component("aqs.QueuedAppointmentWebService")
@Tag(name = "aqsQueuedAppointment")
public class QueuedAppointmentWebService extends AbstractServiceImpl
{
	@Autowired
	private QueuedAppointmentService queuedAppointmentService;

	@Autowired
	private QueuedAppointmentLinkDao queuedAppointmentLinkDao;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private org.oscarehr.appointment.service.Appointment appointmentService;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private SiteDao siteDao;

	@DELETE
	@Path("{appointmentId}/")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteAppointment(@PathParam("queueId") UUID queueId, @PathParam("appointmentId") UUID appointmentId, String reason)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.DELETE, null, SecObjectName._APPOINTMENT);
		queuedAppointmentService.deleteQueuedAppointment(appointmentId, queueId, reason, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		return RestResponse.successResponse(true);
	}

	@POST
	@Path("{appointmentId}/book")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public RestResponse<AppointmentTo1> bookQueuedAppointment(@PathParam("queueId") UUID queueId,
	                                                          @PathParam("appointmentId") UUID appointmentId,
	                                                          BookQueuedAppointmentTransfer bookQueuedAppointmentTransfer) throws ValidationException
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.WRITE, null, SecObjectName._APPOINTMENT);
		QueuedAppointment queuedAppointment = queuedAppointmentService.getQueuedAppointment(appointmentId, queueId, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		Demographic demographic = demographicDao.find(queuedAppointment.getDemographicNo());
		ProviderData provider = providerDataDao.find(bookQueuedAppointmentTransfer.getProviderNo());
		Date now = new Date();

		if (queuedAppointment.getStatus() != QueuedAppointmentStatus.QUEUED)
		{
			throw new ValidationException("Queued Appointment [" + queuedAppointment.getId() +"] is no longer in the queue");
		}

		// create new juno appointment
		Appointment appointment = new Appointment();
		appointment.setProviderNo(bookQueuedAppointmentTransfer.getProviderNo());
		appointment.setDemographicNo(demographic.getId());
		appointment.setAppointmentDate(now);
		appointment.setStartTime(now);
		appointment.setCreateDateTime(now);
		appointment.setStatus(Appointment.TODO);
		appointment.setCreator(provider.getDisplayName());
		appointment.setBookingSource(Appointment.BookingSource.OSCAR);
		appointment.setReason(queuedAppointment.getReason());
		appointment.setNotes(queuedAppointment.getNotes());
		appointment.setName("");
		appointment.setIsVirtual(true);

		// book 15 min appointment
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, 15);
		appointment.setEndTime(calendar.getTime());

		// set site, if provided
		if (bookQueuedAppointmentTransfer.getSiteId() != null)
		{
			Site site = siteDao.find(bookQueuedAppointmentTransfer.getSiteId());
			appointment.setLocation(site.getName());
		}

		// save
		Appointment newAppointment = appointmentService.saveNewAppointment(appointment, getLoggedInInfo(), getHttpServletRequest(), false);

		// link to AQS queued appointment id
		QueuedAppointmentLink queuedAppointmentLink = new QueuedAppointmentLink();
		queuedAppointmentLink.setAppointment(newAppointment);
		queuedAppointmentLink.setQueueId(queueId.toString());
		queuedAppointmentLink.setQueuedAppointmentId(queuedAppointment.getId().toString());
		queuedAppointmentLinkDao.persist(queuedAppointmentLink);

		// mark appointment as schedule on AQS server
		queuedAppointment.setStatus(QueuedAppointmentStatus.SCHEDULED);
		queuedAppointmentService.updateQueuedAppointment(queuedAppointment, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());

		// return newly created appointment
		AppointmentConverter converter = new AppointmentConverter(true, true);
		return RestResponse.successResponse(converter.getAsTransferObject(getLoggedInInfo(), newAppointment));
	}
}
