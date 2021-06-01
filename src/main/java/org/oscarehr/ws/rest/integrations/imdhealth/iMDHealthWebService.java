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
import org.oscarehr.integration.exception.IntegrationException;
import org.oscarehr.integration.imdhealth.service.IMDHealthService;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.integrations.imdhealth.transfer.IMDHealthCredentialsTo1;
import org.oscarehr.ws.rest.integrations.imdhealth.transfer.IMDHealthIntegrationTo1;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/integrations/iMDHealth")
@Component("IMDHealthWebService")
@Tag(name = "iMDHealth")
@Produces(MediaType.APPLICATION_JSON)
public class iMDHealthWebService extends AbstractServiceImpl
{
	@Autowired
	IntegrationService integrationService;

	@Autowired
	IMDHealthService imdHealthService;

	@GET
	@Path("/")
	public RestResponse<List<IMDHealthIntegrationTo1>> getIMDHealthIntegrations()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_READ);

		List<Integration> integrations = integrationService.findIntegrationsByType(Integration.INTEGRATION_TYPE_IMD_HEALTH);

		List<IMDHealthIntegrationTo1> response = new ArrayList<>();
		integrations.forEach(integration -> response.add(IMDHealthIntegrationTo1.fromIntegration(integration)));

		return RestResponse.successResponse(response);
	}

	@GET
	@Path("/SSOLink")
	public RestResponse<String> getSSOLink(@QueryParam("demographicNo") Integer demographicNo, @QueryParam("siteId") Integer siteId) throws IntegrationException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);

		// TODO demographicNo
		String ssoLink = imdHealthService.getSSOLink(getHttpServletRequest().getSession(), demographicNo, siteId);
		return RestResponse.successResponse(ssoLink);
	}

	@GET
	@Path("/{integrationId}/sync")
	public RestResponse<List<String>> syncIntegrations(@PathParam("integrationId") Integer integrationId) throws IntegrationException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_CREATE);

		List<String> synced = imdHealthService.initializeAllUsers(integrationId);
		return RestResponse.successResponse(synced);
	}

	@PUT
	@Path("/")
	public RestResponse<Integer> updateIntegration(IMDHealthCredentialsTo1 credentials)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_CREATE);

		Integration integration = imdHealthService.updateSSOCredentials(getHttpServletRequest().getSession(),
		                                      credentials.getClientId(),
		                                      credentials.getClientSecret(),
		                                      credentials.getSiteId());

		return RestResponse.successResponse(integration.getId());
	}

	@PUT
	@Path("/{integrationId}/Test")
	public RestResponse<Boolean> testIntegration(@PathParam("integrationId") Integer integrationId) throws IntegrationException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_READ);

		boolean credentialsValid = imdHealthService.testIntegration(integrationId);
		return RestResponse.successResponse(credentialsValid);
	}

	@DELETE
	@Path("/{integrationId}")
	public RestResponse<Integer> deleteIntegration(@PathParam("integrationId") Integer integrationId) throws IntegrationException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_DELETE);

		Integer returnValue = null;
		Integration integration = imdHealthService.removeIntegration(getHttpServletRequest().getSession(), integrationId);

		if (integration != null)
		{
			returnValue = integration.getId();
		}

		return RestResponse.successResponse(returnValue);
	}
}
