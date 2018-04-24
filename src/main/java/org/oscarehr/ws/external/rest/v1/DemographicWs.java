/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.provider.service.RecentDemographicAccessService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicConverter;
import org.oscarehr.ws.external.rest.v1.transfer.DemographicTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Component("DemographicWs")
@Path("/demographic")
@Produces(MediaType.APPLICATION_JSON)
public class DemographicWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	DemographicManager demographicManager;

	@Autowired
	RecentDemographicAccessService recentDemographicAccessService;

	@Autowired
	ProgramManager programManager;

	private static final DemographicConverter demographicConverter = new DemographicConverter();

	@GET
	@Path("/{id}")
	@Operation(summary = "Retrieve an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransfer, String> getDemographic(@PathParam("id") Integer demographicNo)
	{
		DemographicTransfer demographicTransfer;
		try
		{
			String providerNoStr = getOAuthProviderNo();
			Demographic demographic = demographicManager.getDemographic(providerNoStr, demographicNo);
			demographicTransfer = demographicConverter.getAsTransferObject(null, demographic);
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			return RestResponse.errorResponse("User Permissions Error");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(demographicTransfer);
	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Update an existing patient demographic record by demographic id.")
	public RestResponse<DemographicTransfer, String> putDemographic(@PathParam("id") Integer demographicNo,
	                                                                @Valid DemographicTransfer demographicTo)
	{
		return RestResponse.errorResponse("Not Implemented");
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new patient demographic record to the system.")
	public RestResponse<Integer, String> postDemographic(@Valid DemographicTransfer demographicTo)
	{
		Demographic demographic;
		try
		{
			demographic = demographicConverter.getAsDomainObject(null, demographicTo);

			if(demographic.getDemographicNo() != null)
			{
				return RestResponse.errorResponse("Demographic number for a new record must be null");
			}
			String providerNoStr = getOAuthProviderNo();
			int providerNo = Integer.parseInt(providerNoStr);
			String ip = getHttpServletRequest().getRemoteAddr();

			/* set some default values */
			demographic.setLastUpdateDate(new Date());
			demographic.setLastUpdateUser(providerNoStr);

			demographicManager.createDemographic(providerNoStr, demographic, getDefaultProgramId());

			LogAction.addLogEntry(providerNoStr, demographic.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, null, ip);
			recentDemographicAccessService.updateAccessRecord(providerNo, demographic.getDemographicNo());
		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			return RestResponse.errorResponse("User Permissions Error");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(demographic.getDemographicNo());
	}

	private Integer getDefaultProgramId()
	{
		return programManager.getProgramIdByProgramName("OSCAR");
	}
}
