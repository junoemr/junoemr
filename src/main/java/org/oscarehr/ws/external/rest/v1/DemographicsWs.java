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
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.log4j.Logger;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicListConverter;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicListTransfer;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Component("DemographicsWs")
@Path("/demographics")
@Produces(MediaType.APPLICATION_JSON)
public class DemographicsWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicManager demographicManager;

	@GET
	@Path("/search")
	@Operation(summary = "Search demographics by parameter")
	public RestSearchResponse<DemographicListTransfer> search(
			@QueryParam("page")
			@DefaultValue("1")
			@Parameter(description = "Requested result page")
					Integer page,
			@QueryParam("perPage")
			@DefaultValue("10")
			@Parameter(description = "Number of results per page")
					Integer perPage,
			@QueryParam("exactMatch")
			@DefaultValue("false")
			@Parameter(description = "When true, search results will only be returned if they are a complete match. Otherwise partial matches may be returned.")
					Boolean exactMatch,
			@Parameter(description = "Match results by health insurance number")
			@QueryParam("hin") String hin
	)
	{
		List<DemographicListTransfer> response = new ArrayList<>(0);

		int totalResultCount;
		try
		{
			perPage = limitedResultCount(perPage);
			page = validPageNo(page);
			int offset = calculatedOffset(page, perPage);

			DemographicSearchRequest searchRequest = new DemographicSearchRequest();
			searchRequest.setStatusMode(DemographicSearchRequest.STATUSMODE.all);
			searchRequest.setIntegrator(false); //this should be configurable by persona
			searchRequest.setOutOfDomain(true);
			searchRequest.setExactMatch(exactMatch);

			if(hin != null)
			{
				searchRequest.setMode(DemographicSearchRequest.SEARCHMODE.HIN);
				searchRequest.setKeyword(hin.trim());
			}
			else
			{
				logger.warn("Missing Search Parameter");
				return RestSearchResponse.errorResponse("Missing Search Parameter");
			}

			totalResultCount = demographicManager.searchPatientsCount(getLoggedInInfo(), searchRequest);
			if(totalResultCount > 0)
			{
				List<DemographicSearchResult> list = demographicManager.searchPatients(getLoggedInInfo(), searchRequest, offset, perPage);
				response = DemographicListConverter.getListAsTransferObjects(list);
			}
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestSearchResponse.errorResponse("System Error");
		}
		return RestSearchResponse.successResponse(response, page, perPage, totalResultCount);
	}
}
