
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
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("myhealthaccess/integration/{integrationId}/")
@Component("mhaIntegrationWebService")
@Tag(name = "mhaIntegration")
public class IntegrationWebService extends AbstractServiceImpl
{
	protected IntegrationDao integrationDao;
	protected ClinicService clinicService;
	protected SecurityInfoManager securityInfoManager;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public IntegrationWebService(IntegrationDao integrationDao, ClinicService clinicService, SecurityInfoManager securityInfoManager)
	{
		this.integrationDao = integrationDao;
		this.clinicService = clinicService;
		this.securityInfoManager = securityInfoManager;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> deleteMhaIntegration(@PathParam("integrationId") String integrationId)
	{
		securityInfoManager.requireSuperAdminFlag(getLoggedInInfo().getLoggedInProviderNo());

		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));
		this.integrationDao.remove(integration);

		return RestResponse.successResponse(true);
	}

	@GET
	@Path("/testConnection")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> testConnection(@PathParam("integrationId") String integrationId)
	{
		try
		{
			Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));
			ClinicStatusResponseTo1 clinicStatusResponseTo1 = this.clinicService.testConnection(integration);

			return RestResponse.successResponse(
					clinicStatusResponseTo1.getStatusIdentifier().equals(ClinicStatusResponseTo1.STATUS_IDENTIFIER_CONNECTED));
		}
		catch(RuntimeException e)
		{
			MiscUtils.getLogger().info("MHA test connection failed for integration [" + integrationId + "]", e);
			return RestResponse.successResponse(false);
		}
	}
}
