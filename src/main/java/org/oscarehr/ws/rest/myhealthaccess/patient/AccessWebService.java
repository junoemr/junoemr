
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
 
package org.oscarehr.ws.rest.myhealthaccess.patient;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.conversion.GenericConverter;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.model.MhaPatientAccess;
import org.oscarehr.integration.myhealthaccess.service.PatientAccessService;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.myhealthaccess.ConnectPatientByVerificationCodeDto;
import org.oscarehr.ws.rest.transfer.myhealthaccess.LinkToEmrDto;
import org.oscarehr.ws.rest.transfer.myhealthaccess.PatientAccessDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("myhealthaccess/integration/{integrationId}/patient/{remoteId}/access/")
@Component("mhaPatientAccessWebService")
@Tag(name = "mhaPatient")
public class AccessWebService extends AbstractServiceImpl
{
	protected IntegrationDao integrationDao;
	protected PatientAccessService patientAccessService;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public AccessWebService(IntegrationDao integrationDao, PatientAccessService patientAccessService)
	{
		this.integrationDao = integrationDao;
		this.patientAccessService = patientAccessService;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<PatientAccessDto> getPatientAccess(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId)
	{
		try
		{
			Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

			PatientAccessDto transfer = (new GenericConverter<MhaPatientAccess, PatientAccessDto>(PatientAccessDto.class))
					.convert(this.patientAccessService.getPatientAccess(integration, remoteId));

			return RestResponse.successResponse(transfer);
		}
		catch(InvalidAccessException | RecordNotFoundException e)
		{
			return null;
		}
	}

	@POST
	@Path("/connectByVerificationCode")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<PatientAccessDto> connectPatientByVerificationCode(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId,
			ConnectPatientByVerificationCodeDto connectPatientDto
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		PatientAccessDto transfer = (new GenericConverter<MhaPatientAccess, PatientAccessDto>(PatientAccessDto.class)).convert(
				this.patientAccessService.connectToPatientByAccountIdCode(integration, getLoggedInInfo(), connectPatientDto.getVerificationCode()));

		return RestResponse.successResponse(transfer);
	}

	@PATCH
	@Path("/linkToEmrPatient")
	@Consumes(MediaType.APPLICATION_JSON)
	public RestResponse<Void> linkPatientToEmrPatient(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId,
			LinkToEmrDto linkToEmrDto
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		this.patientAccessService.linkToEmrRecord(integration, getLoggedInInfo(), linkToEmrDto.getDemographicNo(), remoteId);

		return null;
	}

	@PATCH
	@Path("/confirm")
	public RestResponse<Void> confirmPatient(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		this.patientAccessService.confirmConnection(integration, getLoggedInInfo(), remoteId);

		return null;
	}

	@PATCH
	@Path("/cancelConfirmation")
	public RestResponse<Void> cancelPatientConfirmation(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		this.patientAccessService.cancelConnectionConfirmation(integration, getLoggedInInfo(), remoteId);

		return null;
	}

	@PATCH
	@Path("/verify")
	public RestResponse<Void> verifyPatient(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		this.patientAccessService.verifyConnection(integration, getLoggedInInfo(), remoteId);

		return null;
	}

	@PATCH
	@Path("/cancelVerification")
	public RestResponse<Void> cancelPatientVerification(
			@PathParam("integrationId") String integrationId,
			@PathParam("remoteId") String remoteId
	)
	{
		Integration integration = this.integrationDao.findOrThrow(Integer.parseInt(integrationId));

		this.patientAccessService.cancelConnectionVerification(integration, getLoggedInInfo(), remoteId);

		return null;
	}

}
