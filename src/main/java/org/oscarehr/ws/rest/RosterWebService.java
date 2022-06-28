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
import org.oscarehr.demographicRoster.service.DemographicRosterService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.rosterStatus.service.RosterStatusService;
import org.oscarehr.rosterStatus.model.RosterStatusModel;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("roster")
@Component("RosterWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "rosterService")
public class RosterWebService extends AbstractServiceImpl
{
	@Autowired
	RosterStatusService rosterStatusService;

	@Autowired
	DemographicRosterService demographicRosterService;

	@Autowired
	SecurityInfoManager securityInfoManager;

	@GET
	@Path("/statuses")
	public RestSearchResponse<RosterStatusModel> getRosterStatuses(@QueryParam("active") Boolean active)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);
		List<RosterStatusModel> rosterStatuses = rosterStatusService.getRosterStatusList(active);
		return RestSearchResponse.successResponseOnePage(rosterStatuses);
	}

	@POST
	@Path("/status")
	public RestResponse<RosterStatusModel> addStatus(RosterStatusModel rosterStatusModel)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_CREATE);
		String currentProvider = getCurrentProvider().getProviderNo();
		rosterStatusModel = rosterStatusService.addStatus(rosterStatusModel, currentProvider);

		return RestResponse.successResponse(rosterStatusModel);
	}

	@PUT
	@Path("/status/{id}")
	public RestResponse<RosterStatusModel> editStatus(
			@PathParam("id") Integer id,
			RosterStatusModel rosterStatusModel)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_UPDATE);
		String currentProvider = getCurrentProvider().getProviderNo();
		rosterStatusModel = rosterStatusService.editStatus(rosterStatusModel, currentProvider);

		return RestResponse.successResponse(rosterStatusModel);
	}
}
