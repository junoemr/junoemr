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
import org.oscarehr.integration.aqs.conversion.AppointmentQueueModelConverter;
import org.oscarehr.integration.aqs.conversion.AppointmentQueueTransferConverter;
import org.oscarehr.integration.aqs.conversion.ContactContactTransferConverter;
import org.oscarehr.integration.aqs.conversion.ContactTransferContactConverter;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.Contact;
import org.oscarehr.integration.aqs.service.AppointmentQueueService;
import org.oscarehr.integration.aqs.service.ContactService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.integrations.aqs.transfer.AppointmentQueueTo1;
import org.oscarehr.ws.rest.integrations.aqs.transfer.ContactTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/integrations/aqs")
@Component("aqs.QueuesWebService")
@Tag(name = "aqsQueues")
public class QueueWebService extends AbstractServiceImpl
{
	@Autowired
	private AppointmentQueueService appointmentQueueService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private AppointmentQueueTransferConverter transferConverter;

	@Autowired
	private AppointmentQueueModelConverter modelConverter;

	@Autowired
	private ContactContactTransferConverter contactContactTransferConverter;

	@Autowired
	private ContactTransferContactConverter contactTransferContactConverter;

	@GET
	@Path("queues/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<AppointmentQueueTo1>> getAppointmentQueues()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_READ);
		return RestResponse.successResponse(modelConverter.convert(
				appointmentQueueService.getAppointmentQueues(getLoggedInInfo().getLoggedInSecurity().getSecurityNo())
		));
	}

	@POST
	@Path("queue")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> createAppointmentQueue(AppointmentQueueTo1 queueTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_CREATE);
		return RestResponse.successResponse(modelConverter.convert(
				appointmentQueueService.createAppointmentQueue(transferConverter.convert(queueTransfer), getLoggedInInfo().getLoggedInSecurity().getSecurityNo())
		));
	}

	@GET
	@Path("queue/{queueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> getAppointmentQueue(@PathParam("queueId") UUID queueId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_READ);
		return RestResponse.successResponse(modelConverter.convert(
				appointmentQueueService.getAppointmentQueue(queueId, getLoggedInInfo().getLoggedInSecurity().getSecurityNo())
		));
	}

	@PUT
	@Path("queue/{queueId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> updateAppointmentQueue(@PathParam("queueId") UUID queueId,
	                                                                AppointmentQueueTo1 queueTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_UPDATE);
		return RestResponse.successResponse(modelConverter.convert(
				appointmentQueueService.updateAppointmentQueue(queueId, transferConverter.convert(queueTransfer), getLoggedInInfo().getLoggedInSecurity().getSecurityNo())));
	}

	@DELETE
	@Path("queue/{queueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteAppointmentQueue(@PathParam("queueId") UUID queueId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_DELETE);
		appointmentQueueService.deleteAppointmentQueue(queueId, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		return RestResponse.successResponse(true);
	}

	@GET
	@Path("newQueue")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> getNewAppointmentQueue()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_READ);
		return RestResponse.successResponse(modelConverter.convert(new AppointmentQueue()));
	}

	@GET
	@Path("queue/{queueId}/contacts")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<ContactTransfer>> getQueueContacts(@PathParam("queueId") UUID queueId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_READ);
		return RestResponse.successResponse(contactContactTransferConverter.convert(appointmentQueueService.getAppointmentQueueContacts(queueId, getLoggedInInfo().getLoggedInSecurity().getSecurityNo())));
	}

	@POST
	@Path("/queue/{queueId}/contact")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<ContactTransfer> addQueueContact(@PathParam("queueId") UUID queueId, @RequestBody ContactTransfer contactTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_CREATE);

		Contact newContact = contactService.createNewContact(contactTransferContactConverter.convert(contactTransfer), getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		appointmentQueueService.addAppointmentQueueContact(queueId, newContact.getRemoteId(), getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		return RestResponse.successResponse(contactContactTransferConverter.convert(newContact));
	}

	@DELETE
	@Path("/queue/{queueId}/contact/{contactId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public void removeQueueContact(@PathParam("queueId") UUID queueId, @PathParam("contactId") UUID contactId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.AQS_QUEUE_CONFIG_DELETE);
		appointmentQueueService.removeAppointmentQueueContact(queueId, contactId, getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
	}

}
