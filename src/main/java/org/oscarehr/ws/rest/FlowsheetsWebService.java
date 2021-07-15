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
import org.oscarehr.flowsheet.model.Flowsheet;
import org.oscarehr.flowsheet.search.FlowsheetCriteriaSearch;
import org.oscarehr.flowsheet.service.FlowsheetService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("flowsheets")
@Component("flowsheetsWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "flowsheetsService")
public class FlowsheetsWebService extends AbstractServiceImpl
{
	@Autowired
	private FlowsheetService flowsheetService;

	@GET
	@Path("/search")
	public RestSearchResponse<Flowsheet> searchFlowsheets(
			@QueryParam("enabled") Boolean isEnabled,
			@QueryParam("includeClinicLevel") @DefaultValue("true") boolean includeClinicLevel,
			@QueryParam("includeProviderLevel") @DefaultValue("false") boolean includeProviderLevel,
			@QueryParam("providerId") String owningProviderId,
			@QueryParam("includeDemographicLevel") @DefaultValue("false") boolean includeDemographicLevel,
			@QueryParam("DemographicId") Integer owningDemographicId,
			@QueryParam("page") @DefaultValue("1") Integer page,
			@QueryParam("perPage") @DefaultValue("10") Integer perPage)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.FLOWSHEET_READ);

		if(includeProviderLevel && owningProviderId == null)
		{
			throw new IllegalArgumentException("A providerId must be provided if includeProviderLevel is true");
		}
		if(includeDemographicLevel && owningDemographicId == null)
		{
			throw new IllegalArgumentException("A demographicId must be provided if includeDemographicLevel is true");
		}

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		FlowsheetCriteriaSearch criteriaSearch = new FlowsheetCriteriaSearch();
		criteriaSearch.setEnabled(isEnabled);
		criteriaSearch.setIncludeClinicLevel(includeClinicLevel);
		criteriaSearch.setIncludeProviderLevel(includeProviderLevel);
		criteriaSearch.setIncludeDemographicLevel(includeDemographicLevel);
		criteriaSearch.setProviderId(owningProviderId);
		criteriaSearch.setDemographicId(owningDemographicId);
		criteriaSearch.setLimit(perPage);
		criteriaSearch.setOffset(offset);
		criteriaSearch.setJunctionTypeOR();

		return flowsheetService.executeCriteriaSearch(criteriaSearch, page, perPage);
	}
}
