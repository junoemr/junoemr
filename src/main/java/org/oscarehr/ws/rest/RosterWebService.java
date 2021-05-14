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
import org.oscarehr.rosterStatus.service.RosterStatusService;
import org.oscarehr.rosterStatus.transfer.RosterStatusTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.stereotype.Component;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("roster")
@Component("RosterWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "rosterService")
public class RosterWebService extends AbstractServiceImpl
{
	@Autowired
	RosterStatusService rosterStatusService;

	@Autowired
	DemographicRosterService demographicRosterService;

	@GET
	@Path("/status")
	public RestSearchResponse<RosterStatusTransfer> getRosterStatuses()
	{
		List<RosterStatusTransfer> rosterStatuses = rosterStatusService.getRosterStatusList();
		return RestSearchResponse.successResponseOnePage(rosterStatuses);
	}

	@GET
	@Path("/status/active")
	public RestSearchResponse<RosterStatusTransfer> getActiveRosterStatuses()
	{
		List<RosterStatusTransfer> rosterStatuses = rosterStatusService.getActiveRosterStatusList();
		return RestSearchResponse.successResponseOnePage(rosterStatuses);
	}

	@POST
	@Path("/status/add")
	public RestResponse<RosterStatusTransfer> addStatus(RosterStatusTransfer rosterStatusTransfer)
	{
		String currentProvider = getCurrentProvider().getProviderNo();
		rosterStatusTransfer = rosterStatusService.addStatus(rosterStatusTransfer, currentProvider);

		return RestResponse.successResponse(rosterStatusTransfer);
	}

	@POST
	@Path("/status/")
	public RestResponse<RosterStatusTransfer> editStatus(RosterStatusTransfer rosterStatusTransfer)
	{
		String currentProvider = getCurrentProvider().getProviderNo();
		rosterStatusTransfer = rosterStatusService.editStatus(rosterStatusTransfer, currentProvider);

		return RestResponse.successResponse(rosterStatusTransfer);
	}
}
