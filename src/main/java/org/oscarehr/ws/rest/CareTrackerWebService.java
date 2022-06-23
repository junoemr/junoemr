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
import org.oscarehr.careTracker.model.CareTrackerModel;
import org.oscarehr.careTracker.service.CareTrackerService;
import org.oscarehr.careTracker.transfer.CareTrackerCreateTransfer;
import org.oscarehr.careTracker.transfer.CareTrackerUpdateTransfer;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("careTracker")
@Component("careTrackerWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "careTracker")
public class CareTrackerWebService extends AbstractServiceImpl
{
	@Autowired
	private CareTrackerService careTrackerService;

	@POST
	@Path("/")
	public RestResponse<CareTrackerModel> createCareTracker(CareTrackerCreateTransfer careTracker)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_CREATE);
		return RestResponse.successResponse(careTrackerService.addNewCareTracker(getLoggedInProviderId(), careTracker));
	}

	@GET
	@Path("/{id}")
	public RestResponse<CareTrackerModel> getCareTracker(@PathParam("id") Integer careTrackerId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_READ);
		return RestResponse.successResponse(careTrackerService.getCareTracker(careTrackerId));
	}

	@PUT
	@Path("/{id}")
	public RestResponse<CareTrackerModel> updateCareTracker(@PathParam("id") Integer careTrackerId, CareTrackerUpdateTransfer careTracker)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_UPDATE);
		return RestResponse.successResponse(careTrackerService.updateCareTracker(getLoggedInProviderId(), careTrackerId, careTracker));
	}

	@PATCH
	@Path("/{id}/enabled")
	public RestResponse<Boolean> setEnabledState(@PathParam("id") Integer careTrackerId, @QueryParam("state") boolean enabled)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_UPDATE);
		return RestResponse.successResponse(careTrackerService.setCareTrackerEnabled(getLoggedInProviderId(), careTrackerId, enabled));
	}

	@DELETE
	@Path("/{id}")
	public RestResponse<Boolean> deleteCareTracker(@PathParam("id") Integer careTrackerId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_DELETE);
		careTrackerService.deleteCareTracker(getLoggedInProviderId(), careTrackerId);
		return RestResponse.successResponse(true);
	}

	@POST
	@Path("/{id}/clone")
	public RestResponse<CareTrackerModel> cloneCareTracker(
			@PathParam("id") Integer careTrackerId,
			@QueryParam("providerId") String providerId,
			@QueryParam("demographicId") Integer demographicId)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.CARE_TRACKER_CREATE);

		CareTrackerModel clone;
		if(demographicId != null)
		{
			clone = careTrackerService.addNewDemographicCareTrackerCopy(getLoggedInProviderId(), careTrackerId, demographicId);
		}
		else if (providerId != null)
		{
			clone = careTrackerService.addNewProviderCareTrackerCopy(getLoggedInProviderId(), careTrackerId, providerId);
		}
		else
		{
			clone = careTrackerService.addNewCareTrackerCopy(getLoggedInProviderId(), careTrackerId);
		}
		return RestResponse.successResponse(clone);
	}
}
