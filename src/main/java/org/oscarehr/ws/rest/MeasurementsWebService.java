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

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.dao.MeasurementTypeDao;
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.measurements.converter.MeasurementTypeToTransferConverter;
import org.oscarehr.measurements.dto.MeasurementTypeTransfer;
import org.oscarehr.measurements.search.MeasurementTypeCriteriaSearch;
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
import java.util.List;

@Path("/measurements")
@Component("measurementsWebService")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "measurements")
public class MeasurementsWebService extends AbstractServiceImpl
{
	@Autowired
	private MeasurementTypeDao measurementTypeDao;

	@Autowired
	private MeasurementTypeToTransferConverter measurementTypeToTransferConverter;

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public RestSearchResponse<MeasurementTypeTransfer> searchMeasurementTypes(
			@QueryParam("keyword") String searchParam,
			@QueryParam("page")
			@DefaultValue("1")
			@Parameter(description = "Requested result page")
					Integer page,
			@QueryParam("perPage")
			@DefaultValue("10")
			@Parameter(description = "Number of results per page")
					Integer perPage)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.PREVENTION_READ);

		page = validPageNo(page);
		perPage = limitedResultCount(perPage);
		int offset = calculatedOffset(page, perPage);

		MeasurementTypeCriteriaSearch measurementTypeCriteriaSearch = new MeasurementTypeCriteriaSearch();

		measurementTypeCriteriaSearch.setOffset(offset);
		measurementTypeCriteriaSearch.setLimit(perPage);
		measurementTypeCriteriaSearch.setSortDirAscending();
		measurementTypeCriteriaSearch.setJunctionTypeOR();
		measurementTypeCriteriaSearch.setName(searchParam);
		measurementTypeCriteriaSearch.setType(searchParam);

		int total =  measurementTypeDao.criteriaSearchCount(measurementTypeCriteriaSearch);
		List<MeasurementType> results = measurementTypeDao.criteriaSearch(measurementTypeCriteriaSearch);

		return  RestSearchResponse.successResponse(measurementTypeToTransferConverter.convert(results), page, perPage, total);
	}
}
