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

package org.oscarehr.ws.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.log4j.Logger;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.managers.AppointmentManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.oscarehr.ws.rest.conversion.NewAppointmentConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.AppointmentTo1;
import org.oscarehr.ws.rest.to.model.NewAppointmentTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/appointment")
@Component("appointmentService")
@Tag(name = "appointment")
public class AppointmentService extends AbstractServiceImpl
{
	Logger logger = MiscUtils.getLogger();

	@Autowired
	private AppointmentManager appointmentManager;

	@POST
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<AppointmentTo1> addAppointment(NewAppointmentTo1 appointmentTo)
	{

		NewAppointmentConverter converter = new NewAppointmentConverter();
		Appointment appointment = converter.getAsDomainObject(getLoggedInInfo(), appointmentTo);

		appointmentManager.addAppointment(getLoggedInInfo(), appointment);

		AppointmentTo1 responseAppointmentTo =
				new AppointmentConverter().getAsTransferObject(getLoggedInInfo(), appointment);

		return RestResponse.successResponse(responseAppointmentTo);
	}

	@PUT
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<AppointmentTo1> updateAppointment(AppointmentTo1 appointmentTo) throws Throwable
	{

		logger.info(appointmentTo.toString());
		AppointmentConverter converter = new AppointmentConverter();
		Appointment appointment = converter.getAsDomainObject(getLoggedInInfo(), appointmentTo);

		appointmentManager.updateAppointment(getLoggedInInfo(), appointment);

		AppointmentTo1 responseAppointmentTo =
				new AppointmentConverter().getAsTransferObject(getLoggedInInfo(), appointment);

		return RestResponse.successResponse(responseAppointmentTo);
	}

	@DELETE
	@Path("/{appointmentNo}")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Integer> deleteAppointment(@PathParam("appointmentNo") Integer appointmentNo)
	{
		appointmentManager.deleteAppointment(getLoggedInInfo(), appointmentNo);

		return RestResponse.successResponse(appointmentNo);
	}
}
