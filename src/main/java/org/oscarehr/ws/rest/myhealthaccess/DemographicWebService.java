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
import org.apache.commons.validator.EmailValidator;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.PatientTo1;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotUniqueException;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.myhealthaccess.service.PatientService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("myhealthaccess/integration/{integrationId}/")
@Component("PatientWebService")
@Tag(name = "mhaDemographic")
public class DemographicWebService extends AbstractServiceImpl
{
	@Autowired
	PatientService patientService;

	@Autowired
	IntegrationDao integrationDao;

	@Autowired
	DemographicDao demographicDao;

	@Autowired
	ClinicService clinicService;

	@GET
	@Path("demographic/{demographic_no}/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<PatientTo1> getMHAPatient(@PathParam("integrationId") Integer integrationId,
	                                              @PathParam("demographic_no") String demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);

		Integration integration = integrationDao.find(integrationId);
		Demographic demographic = demographicDao.find(Integer.parseInt(demographicNo));
		try
		{
			MHAPatient patient = patientService.getPatient(integration, demographic);
			return RestResponse.successResponse(new PatientTo1(patient));
		}
		catch (RecordNotFoundException | RecordNotUniqueException e)
		{
			return RestResponse.successResponse(null);
		}
	}

	@GET
	@Path("demographic/{demographic_no}/confirmed")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> isPatientConfirmed(@PathParam("integrationId") Integer integrationId,
	                                                @PathParam("demographic_no") String demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_READ);
		Integration integration = integrationDao.find(integrationId);
		return RestResponse.successResponse(patientService.isPatientConfirmed(Integer.parseInt(demographicNo), integration));
	}

	@PATCH
	@Path("demographic/{demographic_no}/reject_connection")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> rejectPatientConnection(@PathParam("integrationId") Integer integrationId,
	                                                     @PathParam("demographic_no") String demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_UPDATE);
		updatePatientConnection(integrationId, demographicNo, true);
		return RestResponse.successResponse(true);
	}

	@PATCH
	@Path("demographic/{demographic_no}/cancel_reject_connection")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> cancelRejectPatientConnection(@PathParam("integrationId") Integer integrationId,
	                                                           @PathParam("demographic_no") String demographicNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_UPDATE);
		updatePatientConnection(integrationId, demographicNo, false);
		return RestResponse.successResponse(true);
	}

	@POST
	@Path("/demographic/{demographicId}/invite")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<Boolean> patientInvite(@PathParam("integrationId") Integer integrationId,
	                                           @PathParam("demographicId") Integer demographicId,
	                                           @QueryParam("email") String patientEmail)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DEMOGRAPHIC_UPDATE);
		Integration integration = integrationDao.find(integrationId);
		Demographic demographic = demographicDao.find(demographicId);

		if (!isEmailValid(patientEmail))
		{
			throw new ValidationException("Missing or invalid patient email");
		}

		// update the demographic email as it may not be set
		if(!patientEmail.equals(demographic.getEmail()))
		{
			demographic.setEmail(patientEmail);
			demographicDao.merge(demographic);
		}

		ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
				getLoggedInInfo().getLoggedInSecurity().getSecurityNo());
		patientService.patientInvite(integration, loginTokenTo1.getToken(), demographic);

		return RestResponse.successResponse(true);
	}


	protected void updatePatientConnection(Integer integrationId, String demographicNo, Boolean rejected)
	{
		Integration integration = integrationDao.find(integrationId);
		Demographic demographic = demographicDao.find(Integer.parseInt(demographicNo));
		ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration,
				getLoggedInInfo().getLoggedInSecurity().getSecurityNo());

		patientService.updatePatientConnection(integration, loginTokenTo1.getToken(), demographic, rejected);
	}

	private boolean isEmailValid(String emailAddr)
	{
		boolean isValid = false;
		if(emailAddr != null && !emailAddr.trim().isEmpty())
		{
			EmailValidator eValidator = EmailValidator.getInstance();
			isValid = eValidator.isValid(emailAddr);
		}
		return isValid;
	}
}
