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
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.integration.aqs.service.AppointmentQueueService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.integrations.aqs.transfer.AppointmentQueueTo1;
import org.oscarehr.ws.rest.integrations.aqs.transfer.OnDemandBookingSettingsTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

@Path("/integrations/aqs")
@Component("aqs.QueuesWebService")
@Tag(name = "aqsQueues")
public class QueueWebService extends AbstractServiceImpl
{
	@Autowired
	private AppointmentQueueService appointmentQueueService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@GET
	@Path("queues/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<AppointmentQueueTo1>> getAppointmentQueues()
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(AppointmentQueueTo1.fromAppointmentQueueList(appointmentQueueService.getAppointmentQueues()));
	}

	@POST
	@Path("queue")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> createAppointmentQueue(AppointmentQueueTo1 queueTransfer)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.WRITE, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(new AppointmentQueueTo1(appointmentQueueService.createAppointmentQueue(queueTransfer)));
	}

	@GET
	@Path("queue/{queueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> getAppointmentQueue(@PathParam("queueId") String queueId)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(new AppointmentQueueTo1(appointmentQueueService.getAppointmentQueue(queueId)));
	}

	@PUT
	@Path("queue/{queueId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentQueueTo1> updateAppointmentQueue(@PathParam("queueId") String queueId,
	                                                                AppointmentQueueTo1 queueTransfer)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.WRITE, null, SecObjectName._ADMIN);
		return RestResponse.successResponse(new AppointmentQueueTo1(appointmentQueueService.updateAppointmentQueue(queueId, queueTransfer)));
	}

	@DELETE
	@Path("queue/{queueId}")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteAppointmentQueue(@PathParam("queueId") String queueId)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.DELETE, null, SecObjectName._ADMIN);
		appointmentQueueService.deleteAppointmentQueue(queueId);
		return RestResponse.successResponse(true);
	}

	@GET
	@Path("odb/settings")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<OnDemandBookingSettingsTransfer> getOnDemandBookingSettings()
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.READ, null, SecObjectName._ADMIN);
		// TODO update queue server
		return RestResponse.successResponse(new OnDemandBookingSettingsTransfer());
	}

	@PUT
	@Path("odb/settings")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<OnDemandBookingSettingsTransfer> setOnDemandBookingSettings(OnDemandBookingSettingsTransfer settingsTransfer)
	{
		securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.WRITE, null, SecObjectName._ADMIN);
		// TODO update queue server
		return RestResponse.successResponse(settingsTransfer);
	}

}
