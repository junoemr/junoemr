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

package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.apache.log4j.Logger;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DemographicListConverter;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicListTransfer;
import org.oscarehr.ws.rest.exception.MissingArgumentException;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component("DemographicsWs")
@Path("/demographics")
@Produces(MediaType.APPLICATION_JSON)
public class DemographicsWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private SecurityInfoManager securityInfoManager;

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
			@Parameter(description = "Match results by given name")
			@QueryParam("firstName") String firstName,
			@Parameter(description = "Match results by family name")
			@QueryParam("lastName") String lastName,
			@Parameter(description = "Match results by health insurance number")
			@QueryParam("hin") String hin,
			@Parameter(description = "Match results by sex")
			@QueryParam("sex") String sex,
			@Parameter(description = "Match results by date of birth")
			@QueryParam("dateOfBirth") String dateOfBirthStr,
			@Parameter(description = "Match results by address")
			@QueryParam("address") String address,
			@Parameter(description = "Match results chart number")
			@QueryParam("charNo") String chartNo,
			@Parameter(description = "Match results by phone number")
			@QueryParam("phone") String phone,
			@Parameter(description = "Match results by provider id")
			@QueryParam("providerNo") String searchProviderNo
	)
	{
		securityInfoManager.requireAllPrivilege(getOAuthProviderNo(), Permission.DEMOGRAPHIC_READ);

		if(parametersAllNull(firstName, lastName, hin, sex, dateOfBirthStr, address, chartNo, phone, searchProviderNo))
		{
			throw new MissingArgumentException("At least one search parameter is required");
		}
		LocalDate dateOfBirth;
		try
		{
			dateOfBirth = (dateOfBirthStr != null ? LocalDate.parse(dateOfBirthStr, DateTimeFormatter.ISO_LOCAL_DATE) : null);
		}
		catch(Exception e)
		{
			MissingArgumentException exception = new MissingArgumentException("Argument Error");
			exception.addMissingArgument("dateOfBirth", "Invalid Format");
			throw exception;
		}

		// set up the search criteria
		DemographicCriteriaSearch searchQuery = new DemographicCriteriaSearch();
		searchQuery.setHin(hin);
		searchQuery.setSex(sex);
		searchQuery.setFirstName(firstName);
		searchQuery.setLastName(lastName);
		searchQuery.setDateOfBirth(dateOfBirth);
		searchQuery.setAddress(address);
		searchQuery.setChartNo(chartNo);
		searchQuery.setPhone(phone);
		searchQuery.setProviderNo(searchProviderNo);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		searchQuery.setOffset(offset);
		searchQuery.setLimit(perPage);
		searchQuery.setSortDirAscending();

		searchQuery.setSortMode(DemographicCriteriaSearch.SORT_MODE.DemographicNo);
		searchQuery.setStatusMode(DemographicCriteriaSearch.STATUS_MODE.all);
		searchQuery.setCustomWildcardsEnabled(false);

		if(exactMatch)
		{
			searchQuery.setMatchModeExact();
		}
		else
		{
			searchQuery.setMatchModeStart();
		}

		int totalResultCount = demographicDao.criteriaSearchCount(searchQuery);

		List<DemographicListTransfer> response;
		if(totalResultCount > 0)
		{
			List<Demographic> results = demographicDao.criteriaSearch(searchQuery);
			response = DemographicListConverter.getListAsTransferObjects(results);
		}
		else
		{
			response = new ArrayList<>(0);
		}
		return RestSearchResponse.successResponse(response, page, perPage, totalResultCount);
	}
}
