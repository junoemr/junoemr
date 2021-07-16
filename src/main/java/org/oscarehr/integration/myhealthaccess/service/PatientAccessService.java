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

package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.common.conversion.GenericConverter;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.client.RestClientBase;
import org.oscarehr.integration.myhealthaccess.client.RestClientFactory;
import org.oscarehr.integration.myhealthaccess.dto.EmrLinkDto;
import org.oscarehr.integration.myhealthaccess.dto.PatientAccessDto;
import org.oscarehr.integration.myhealthaccess.dto.PatientConnectByAccountIdCodeDto;
import org.oscarehr.integration.myhealthaccess.model.MhaPatientAccess;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("mhaPatientAccessService")
public class PatientAccessService extends BaseService
{
	protected ClinicService clinicService;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	PatientAccessService(ClinicService clinicService)
	{
		this.clinicService = clinicService;
	}

	/**
	 * get a patient access record from MHA
	 * @param integration - the integration to get the record from
	 * @param remoteId - the remoteId of the patient who's access record is to be retrieved
	 * @return the patient access record
	 * @throws RecordNotFoundException - if no patient exits with the provided id.
	 * @throws InvalidAccessException - if you do not have access to the requested patient record.
	 */
	public MhaPatientAccess getPatientAccess(Integration integration, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint("/clinic/%s/patient/%s/access/", integration.getRemoteId(), remoteId);

		return (new GenericConverter<PatientAccessDto, MhaPatientAccess>(MhaPatientAccess.class)).convert(restClient.doGet(url, PatientAccessDto.class));
	}

	/**
	 * connect a patient to the clinic by account id code
	 * @param integration - the integration (clinic) to connect the patient to
	 * @param loggedInInfo - current user's logged in info
	 * @param idCode - the id code of the patient you are connecting to. Used to validate this operation.
	 * @return the newly created patient access record
	 * @throws InvalidAccessException - if id code is not valid
	 */
	public MhaPatientAccess connectToPatientByAccountIdCode(Integration integration, LoggedInInfo loggedInInfo, String idCode)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/patient_invite_by_verification_code");

		PatientConnectByAccountIdCodeDto transfer = new PatientConnectByAccountIdCodeDto();
		transfer.setVerificationCode(idCode);

		return (new GenericConverter<PatientAccessDto, MhaPatientAccess>(MhaPatientAccess.class)).convert(
				restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), transfer, PatientAccessDto.class));
	}

	/**
	 * link the remote patient to the local EMR record.
	 * @param integration - integration to perform the link in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param demographicNo - the demographic to link to
	 * @param remoteId - the remoteId to link to the demographic
	 */
	public void linkToEmrRecord(Integration integration, LoggedInInfo loggedInInfo, String demographicNo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/link_to_emr",
				remoteId);

		EmrLinkDto emrLinkDto = new EmrLinkDto();
		emrLinkDto.setDemographicNo(demographicNo);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), emrLinkDto, Void.class);
	}

	/**
	 * confirm a patients connection.
	 * @param integration - integration to perform the confirmation in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to confirm
	 */
	public void confirmConnection(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/confirm",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	/**
	 * cancel a patients connection confirmation
	 * @param integration - integration to perform the cancellation in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to cancel the confirmation on.
	 */
	public void cancelConnectionConfirmation(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/cancel_confirm",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	/**
	 * verify a patients connection.
	 * @param integration - integration to perform the verification in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to verify
	 */
	public void verifyConnection(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/verify",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	/**
	 * cancel a patients connection verification
	 * @param integration - integration to perform the cancellation in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to cancel the verification on.
	 */
	public void cancelConnectionVerification(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/cancel_verify",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	/**
	 * reject a patients connection to this clinic
	 * @param integration - integration to perform the rejection in
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to reject from the clinic.
	 */
	public void rejectConnection(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/reject",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	/**
	 * cancel the rejection of a patients connection to this clinic
	 * @param integration - integration to cancel the rejection in.
	 * @param loggedInInfo - the currently logged in user's info
	 * @param remoteId - the remoteId to cancel the rejection for.
	 */
	public void cancelRejectConnection(Integration integration, LoggedInInfo loggedInInfo, String remoteId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint(
				"/clinic_user/self/clinic/patient/%s/connection/cancel_reject",
				remoteId);

		restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), null, Void.class);
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * get a clinic_user login token
	 * @param integration - the MHA integration
	 * @param loggedInInfo - the provider's logged in info
	 * @return - the providers clinic_user login token
	 */
	protected String getLoginToken(Integration integration, LoggedInInfo loggedInInfo)
	{
		return clinicService.loginOrCreateClinicUser(
				loggedInInfo,
				Optional.ofNullable(integration.getSite()).map(Site::getName).orElse(null))
				.getToken();
	}
}
