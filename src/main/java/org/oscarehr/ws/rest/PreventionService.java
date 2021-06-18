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

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.service.PreventionManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.conversion.PreventionConverter;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.PreventionResponse;
import org.oscarehr.ws.rest.to.model.PreventionTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;


@Path("/preventions")
@Component("preventionService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "preventions")
public class PreventionService extends AbstractServiceImpl
{
	@Autowired
	private PreventionManager preventionManager;

	@GET
	@Path("/active")
	@Produces(MediaType.APPLICATION_JSON)
	public PreventionResponse getCurrentPreventions(@QueryParam("demographicNo") Integer demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.PREVENTION_READ);

		List<Prevention> preventions = preventionManager.getPreventionsByDemographicNo(getLoggedInInfo(), demographicNo);
		
		List<PreventionTo1> preventionsT = new PreventionConverter().getAllAsTransferObjects(getLoggedInInfo(), preventions);
		
		PreventionResponse response = new PreventionResponse();
		response.setPreventions(preventionsT);
		
		return response;
	}

	@GET
	@Path("/types")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<String> getPreventionTypes()
	{
		return RestSearchResponse.successResponseOnePage(preventionManager.getPreventionTypeList());
	}
}
