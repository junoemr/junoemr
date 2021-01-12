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

package org.oscarehr.ws.rest.integrations.imdhealth;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.integration.imdhealth.service.IMDHealthService;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.integrations.imdhealth.transfer.IMDHealthCredentialsTo1;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.myhealthaccess.IntegrationTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/integrations/iMDHealth")
@Component("IMDHealthWebService")
@Tag(name = "iMDHealth")
public class iMDHealthWebService extends AbstractServiceImpl
{
	@Autowired
	IntegrationService integrationService;

	@Autowired
	IMDHealthService imdHealthService;

	@GET
	@Path("/")
	public RestResponse<IMDHealthCredentialsTo1> getIntegration(@QueryParam("siteId") Integer siteId)
	{
		Integration integration = integrationService.findIntegrationByTypeAndSite(Integration.INTEGRATION_TYPE_IMD_HEALTH, siteId);
		IMDHealthCredentialsTo1 response = new IMDHealthCredentialsTo1(integration);
		return RestResponse.successResponse(response);
	}

	@GET
	@Path("/SSOLink")
	public RestResponse<String> getSSOLink(@QueryParam("siteId") Integer siteId)
	{
		String ssoLink = imdHealthService.getSSOLink(getHttpServletRequest(), siteId);
		return RestResponse.successResponse(ssoLink);
	}

	@POST
	@Path("/")
	public RestResponse<IntegrationTo1> updateIntegration(IMDHealthCredentialsTo1 credentials)
	{
		imdHealthService.updateSSOCredentials(credentials.getClientId(), credentials.getClientSecret(), credentials.getSiteId());
		// TODO: security permissions
		return RestResponse.successResponse(null);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("imdHealth/TestIntegration")
	public RestResponse<Boolean> testIntegration(@QueryParam("site") String siteId)
	{
		// TODO: Stub, permissions
		return RestResponse.successResponse(false);
	}

	// TODO: search integrations (multisite)
}
