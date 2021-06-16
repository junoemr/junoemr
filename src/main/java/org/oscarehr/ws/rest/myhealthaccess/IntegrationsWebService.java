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
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.dao.UserIntegrationAccessDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.myhealthaccess.IntegrationTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("myhealthaccess/integrations")
@Component("mhaIntegrationsWebService")
@Tag(name = "mhaIntegration")
public class IntegrationsWebService extends AbstractServiceImpl
{
	@Autowired
	IntegrationService integrationService;

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<IntegrationTo1>> searchIntegrations(@QueryParam("site") String siteName, @QueryParam("all") Boolean allSites)
	{
		if (allSites != null && allSites)
		{
			return RestResponse.successResponse(IntegrationTo1.fromIntegrationList(integrationService.getMyHealthAccessIntegrations()));
		}
		else
		{
			Integration integration = integrationService.findMhaIntegration(siteName);
			if (integration != null)
			{
				return RestResponse.successResponse(Arrays.asList(new IntegrationTo1(integration)));
			}
		}
		return RestResponse.successResponse(new ArrayList<>());
	}
}
