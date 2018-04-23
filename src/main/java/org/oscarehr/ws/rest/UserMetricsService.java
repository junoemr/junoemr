/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.rest;

import io.prometheus.client.Histogram;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.UserMetricsTo1;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/user_metrics")
@Component("UserMetricsService")
public class UserMetricsService extends AbstractServiceImpl
{
	private static final Histogram requestLatency = Histogram.build().
			name("request_latency_seconds").
			help("Request latency in seconds").
			labelNames("page").
			register();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({MediaType.APPLICATION_JSON , MediaType.APPLICATION_XML})
	public RestResponse<String,String> postMetrics(ArrayList<UserMetricsTo1> data)
	{
		String page = "appointment";
		for(UserMetricsTo1 metric : data)
		{
			if(metric.getMetricName().equals("performance_timing_loadEventEnd"))
			{
				for(Double observedTime : metric.getObservations())
				{
					requestLatency.labels(page).observe(observedTime);
				}
			}
		}
		return RestResponse.successResponse("Success");
	}

	@GET
	@Produces({MediaType.APPLICATION_JSON , MediaType.APPLICATION_XML})
	public RestResponse<String,String> test()
	{
		return RestResponse.successResponse("Success");
	}
}
