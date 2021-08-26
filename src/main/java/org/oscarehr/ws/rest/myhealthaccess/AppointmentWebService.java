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
package org.oscarehr.ws.rest.myhealthaccess;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.conversion.GenericConverter;
import org.oscarehr.common.model.SecObjectName;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentBookTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.model.MHAAppointment;
import org.oscarehr.integration.myhealthaccess.model.MHATelehealthSessionInfo;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.conversion.myhealthaccess.AppointmentBookTransferToAppointmentBookTo1Converter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.myhealthaccess.AppointmentBookingTransfer;
import org.oscarehr.ws.rest.transfer.myhealthaccess.AppointmentTo1;
import org.oscarehr.ws.rest.transfer.myhealthaccess.TelehealthSessionInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("myhealthaccess/integration/{integrationId}/")
@Component("AppointmentWebService")
@Tag(name = "mhaAppointment")
public class AppointmentWebService extends AbstractServiceImpl
{
	@Autowired
	AppointmentService appointmentService;

	@Autowired
	IntegrationDao integrationDao;

	@Autowired
	ClinicService clinicService;

	@GET
	@Path("/appointments")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentTo1> searchAppointments(@PathParam("integrationId") Integer integrationId, @QueryParam("appointmentNo") Integer appointmentNo)
	{
		try
		{
			Integration integration = integrationDao.find(integrationId);
			if (integration != null)
			{
				MHAAppointment appointment = appointmentService.getAppointment(integration, appointmentNo);
				return RestResponse.successResponse(new AppointmentTo1(appointment));
			}
			return RestResponse.successResponse(null);
		}
		catch (RecordNotFoundException e)
		{
			return RestResponse.successResponse(null);
		}
	}

	@POST
	@Path("/appointment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<AppointmentTo1> bookMhaAppointment(@PathParam("integrationId") Integer integrationId, AppointmentBookingTransfer appointmentBookingTransfer)
	{
		this.securityInfoManager.requireOnePrivilege(getLoggedInInfo().getLoggedInProviderNo(), SecurityInfoManager.READ, null, SecObjectName._APPOINTMENT);

		AppointmentBookTo1 appointmentBookTo1 = (new AppointmentBookTransferToAppointmentBookTo1Converter()).convert(appointmentBookingTransfer);
		appointmentBookTo1.setProviderNo(getLoggedInInfo().getLoggedInProviderNo());

		MHAAppointment newAppointment = this.appointmentService.bookMhaAppointment(this.getLoggedInInfo(), appointmentBookTo1);

		return RestResponse.successResponse(new AppointmentTo1(newAppointment));
	}

	@POST
	@Path("/appointment/non_mha/{appointmentNo}/send_general_appt_notification")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> sendGeneralAppointmentNotification(@PathParam("integrationId") Integer integrationId,
																	@PathParam("appointmentNo") Integer appointmentNo)
	{
		Integration integration = integrationDao.find(integrationId);
		ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
				getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		appointmentService.sendGeneralAppointmentNotification(integration, loginTokenTo1.getToken(), appointmentNo);
		return RestResponse.successResponse(true);
	}

	@POST
	@Path("/appointment/{appointmentId}/send_telehealth_appt_notification")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> sendTelehealthAppointmentNotification(@PathParam("integrationId") Integer integrationId,
														   @PathParam("appointmentId") String appointmentId)
	{
		Integration integration = integrationDao.find(integrationId);
		ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
				getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		appointmentService.sendTelehealthAppointmentNotification(integration, loginTokenTo1.getToken(), appointmentId);

		return RestResponse.successResponse(true);
	}

	@GET
	@Path("/appointment/non_mha/{appointmentNo}/session")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<TelehealthSessionInfoDto> getTelehealthSessionInformation(
			@PathParam("integrationId") Integer integrationId,
			@PathParam("appointmentNo") String appointmentNo) throws IllegalAccessException, InstantiationException
	{
		Integration integration = integrationDao.find(integrationId);
		GenericConverter<MHATelehealthSessionInfo, TelehealthSessionInfoDto> genericConverter = new GenericConverter<>(TelehealthSessionInfoDto.class);
		return RestResponse.successResponse(genericConverter.convert(appointmentService.getAppointmentSessionInformation(integration, Integer.parseInt(appointmentNo))));
	}
}
