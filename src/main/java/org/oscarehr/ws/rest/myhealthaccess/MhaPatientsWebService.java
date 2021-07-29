
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
 
package org.oscarehr.ws.rest.myhealthaccess;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.PatientTo1;
import org.oscarehr.integration.myhealthaccess.service.PatientService;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Path("myhealthaccess/integration/{integrationId}/")
@Component("PatientsWebService")
@Tag(name = "mhaPatient")
public class MhaPatientsWebService extends AbstractServiceImpl
{

	protected PatientService patientService;
	protected IntegrationDao integrationDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public MhaPatientsWebService(PatientService patientService, IntegrationDao integrationDao)
	{
		this.patientService = patientService;
		this.integrationDao = integrationDao;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@GET
	@Path("patients/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<PatientTo1>> searchPatients(
			@PathParam("integrationId") String integrationId,
			@QueryParam("keyword") String keyword,
			@QueryParam("accountIdCode") String accountIdCode)
	{
		Integration integration = integrationDao.find(Integer.parseInt(integrationId));

		if (keyword != null)
		{
			return RestResponse.successResponse(
					this.patientService.searchPatientsByKeyword(integration, keyword).stream().map(PatientTo1::new).collect(Collectors.toList()));
		}
		else if (accountIdCode != null)
		{
			return RestResponse.successResponse(Collections.singletonList(
					new PatientTo1(this.patientService.getPatientByAccountIdCodeCode(integration, accountIdCode))));
		}
		else
		{
			throw new IllegalArgumentException("One of 'keyword' or 'accountIdCode' must be provided");
		}
	}
}
