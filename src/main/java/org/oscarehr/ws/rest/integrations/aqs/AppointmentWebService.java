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
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueuedAppointmentTo1;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/integrations/aqs/queue/{queueId}/")
@Component("aqs.AppointmentWebService")
@Tag(name = "aqsAppointments")
public class AppointmentWebService
{

	@GET
	@Path("appointments/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<QueuedAppointmentTo1>> getAppointmentsInQueue(@PathParam("queueId") Integer queueId)
	{
		ArrayList<QueuedAppointmentTo1> list = new ArrayList<>();

		//TODO real data
		switch(queueId)
		{
			case 0:
				list.add(new QueuedAppointmentTo1(0, "1" , "foobar", "Jon Duo"));
				list.add(new QueuedAppointmentTo1(1, "2" , "warts on ass", "Frank. Dr."));
				break;
			case 1:
				list.add(new QueuedAppointmentTo1(0, "3" , "power over whelming!", "Man "));
				list.add(new QueuedAppointmentTo1(1, "4" , "Its over 9000!", "Super Saiyan "));
				list.add(new QueuedAppointmentTo1(2, "5" , "What! 9000!", "Other guy"));
				break;
			case 2:
				for (Integer i =0; i < 64; i ++)
				{
					list.add(new QueuedAppointmentTo1(i, i.toString(), "Long", "appts"));
				}
				break;
		}

		return RestResponse.successResponse(list);
	}

}
