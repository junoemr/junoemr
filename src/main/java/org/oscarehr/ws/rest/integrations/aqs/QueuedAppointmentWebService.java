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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.integration.aqs.conversion.QueuedAppointmentBookingTransferQueuedAppointmentConverter;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.oscarehr.integration.aqs.service.QueuedAppointmentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.oscarehr.ws.rest.integrations.aqs.transfer.BookQueuedAppointmentTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueuedAppointmentBookingTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueuedAppointmentMoveTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueuedAppointmentTo1;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.AppointmentTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.UUID;

@Path("/integrations/aqs/queue/{queueId}/appointment")
@Component("aqs.QueuedAppointmentWebService")
@Tag(name = "aqsQueuedAppointment")
public class QueuedAppointmentWebService extends AbstractServiceImpl
{
	@Autowired
	private QueuedAppointmentService queuedAppointmentService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	QueuedAppointmentBookingTransferQueuedAppointmentConverter queuedAppointmentConverter;

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<QueuedAppointmentTo1> createQueuedAppointment(@PathParam("queueId") UUID queueId, QueuedAppointmentBookingTransfer queuedAppointmentBookingTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUED_APPOINTMENTS_CREATE);
		QueuedAppointment newQueuedAppointment = queuedAppointmentConverter.convert(queuedAppointmentBookingTransfer);
		newQueuedAppointment = queuedAppointmentService.bookQueuedAppointment(queueId, newQueuedAppointment, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		return RestResponse.successResponse(new QueuedAppointmentTo1(newQueuedAppointment));
	}

	@DELETE
	@Path("{appointmentId}/")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteAppointment(@PathParam("queueId") UUID queueId, @PathParam("appointmentId") UUID appointmentId, String reason)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUED_APPOINTMENTS_DELETE);
		queuedAppointmentService.deleteQueuedAppointment(appointmentId, queueId, reason, getLoggedInInfo());
		return RestResponse.successResponse(true);
	}

	@PATCH
	@Path("{appointmentId}/move")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<QueuedAppointmentTo1> moveAppointment(@PathParam("queueId") UUID queueId, @PathParam("appointmentId") UUID appointmentId, QueuedAppointmentMoveTransfer queuedAppointmentMoveTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUED_APPOINTMENTS_CREATE);

		QueuedAppointment queuedAppointment = queuedAppointmentService.moveQueuedAppointment(appointmentId, queuedAppointmentMoveTransfer.getQueuePosition(), getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		return RestResponse.successResponse(new QueuedAppointmentTo1(queuedAppointment));
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
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUED_APPOINTMENTS_CREATE);

		// schedule the queued appointment
		Appointment newAppointment = queuedAppointmentService.scheduleQueuedAppointment(appointmentId, queueId, bookQueuedAppointmentTransfer.getProviderNo(),
		                                                                                bookQueuedAppointmentTransfer.getSiteId(), getLoggedInInfo(), getHttpServletRequest());
		// return newly created appointment
		AppointmentConverter converter = new AppointmentConverter(true, true);
		return RestResponse.successResponse(converter.getAsTransferObject(getLoggedInInfo(), newAppointment));
	}
}
