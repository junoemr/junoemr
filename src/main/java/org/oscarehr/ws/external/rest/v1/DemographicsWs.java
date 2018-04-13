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

import org.apache.log4j.Logger;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.RestResponse;
import org.oscarehr.ws.rest.to.model.DemographicSearchRequest;
import org.oscarehr.ws.rest.to.model.DemographicSearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

@Component("DemographicsWs")
@Path("/v1/demographics/")
@Produces("application/json")
public class DemographicsWs extends AbstractServiceImpl
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicManager demographicManager;

	@GET
	@Path("/find")
	public RestResponse<List<DemographicSearchResult>, String> search(
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage,
			@QueryParam("exactMatch") @DefaultValue("false") Boolean exactMatch,
			@QueryParam("hin") String hin
	)
	{
		List<DemographicSearchResult> response = new ArrayList<>(0);
		HttpHeaders responseHeaders = new HttpHeaders();

		try
		{
			perPage = limitedResultCount(perPage);
			page = validPageNo(page);
			int offset = calculatedOffset(page, perPage);

			DemographicSearchRequest searchRequest = new DemographicSearchRequest();
			searchRequest.setStatusMode(DemographicSearchRequest.STATUSMODE.active);
			searchRequest.setIntegrator(false); //this should be configurable by persona
			searchRequest.setOutOfDomain(true);

			if(hin != null)
			{
				searchRequest.setMode(DemographicSearchRequest.SEARCHMODE.HIN);
				searchRequest.setKeyword(hin.trim());
			}
			else
			{
				logger.warn("Missing Search Parameter");
				return RestResponse.errorResponse("Missing Search Parameter");
			}

			int count = demographicManager.searchPatientsCount(getLoggedInInfo(), searchRequest);
			if(count > 0)
			{
				response = demographicManager.searchPatients(getLoggedInInfo(), searchRequest, offset, perPage);
			}
			responseHeaders.add("total", String.valueOf(count));
			responseHeaders.add("page", String.valueOf(page));
			responseHeaders.add("perPage", String.valueOf(perPage));
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(responseHeaders, response);
	}
}
