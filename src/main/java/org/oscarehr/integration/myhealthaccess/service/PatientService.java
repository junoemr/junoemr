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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.client.RestClientBase;
import org.oscarehr.integration.myhealthaccess.client.RestClientFactory;
import org.oscarehr.integration.myhealthaccess.dto.PatientInviteTo1;
import org.oscarehr.integration.myhealthaccess.dto.PatientSingleSearchResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.PatientTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotUniqueException;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service("myHealthPatientService")
public class PatientService extends BaseService
{
	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicExtDao demographicExtDao;


	public boolean isPatientConfirmed(Integer demographicNo, Integration integration)
	{
		return isPatientConfirmed(demographicNo, integration, Arrays.asList(MHAPatient.LINK_STATUS.CONFIRMED, MHAPatient.LINK_STATUS.VERIFIED));
	}

	/**
	 * check if the provided demographic is confirmed ("confirmed and liked to this Juno EMR instance")
	 * @param demographicNo - the demographic to check
	 * @param integration - the integration to look the demographic up in.
	 * @param confirmedStatuses - the statuses to be considered confirmed. Should be left default in almost all situations
	 * @return - true if confirmed, false otherwise (including if there is not MHA patient for this demographic)
	 */
	public boolean isPatientConfirmed(Integer demographicNo, Integration integration, List<MHAPatient.LINK_STATUS> confirmedStatuses)
	{
		try
		{
			if (integration != null)
			{
				try
				{
					MHAPatient patient = getConfirmedPatientByDemographicNo(integration, demographicNo);
					if (patient != null)
					{
						return confirmedStatuses.contains(patient.getLinkStatus());
					}
				}
				catch(RecordNotFoundException | RecordNotUniqueException e)
				{
					return false;
				}
			}
		}
		catch(InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}
		return false;
	}

	/**
	 * get a mha patient via hin lookup
	 * @param integration - the mha integration to look in
	 * @param hin - the health number to search
	 * @param hinProvince - the province of the health number
	 * @return an MHA patient object.
	 */
	public MHAPatient getPatientByHin(Integration integration, String hin, MHAPatient.PROVINCE_CODES hinProvince)
	{
		if (hin == null || hin.isEmpty())
		{
			throw new IllegalArgumentException("hin cannot be null or empty");
		}

		try
		{
			RestClientBase restClient = RestClientFactory.getRestClient(integration);

			String url = restClient.formatEndpoint("/clinic/" + integration.getRemoteId() +
											"/patients?search_by=hin&health_number=%s&health_care_province=%s", hin, hinProvince);
			PatientSingleSearchResponseTo1 response = restClient.doGet(url, PatientSingleSearchResponseTo1.class);

			if (response.isSuccess())
			{
				return new MHAPatient(response.getPatientTo1());
			}
			else if (response.isNotFound())
			{
				throw new RecordNotFoundException("Could not find MHA patient with hin: " + hin + " province: " + hinProvince.toString());
			}
			else if (response.isNotUnique())
			{
				throw new RecordNotUniqueException("Multiple patients with hin: " + hin + " province: " + hinProvince.toString());
			}
		}
		catch(InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}

		return null;
	}

	/**
	 * search MHA patients connected to the integration by keyword
	 * @param integration - the integration to search
	 * @param keyword - the keyword to search by. matches first_name OR last_name OR email. MUST be 3 + characters.
	 * @return list of MHA patients.
	 * @throws IllegalArgumentException - if the provided keyword is less than 3 characters long
	 */
	public List<MHAPatient> searchPatientsByKeyword(Integration integration, String keyword)
	{
		if (keyword == null || keyword.length() < 3)
		{
			throw new IllegalArgumentException("Keyword must be at least 3 characters long");
		}

		try
		{
			RestClientBase restClient = RestClientFactory.getRestClient(integration);

			MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
			queryParams.add("search_by", "keyword");
			queryParams.add("keyword", keyword);

			String url = restClient.formatEndpointFull("/clinic/%s/patients", Arrays.asList(integration.getRemoteId()), queryParams);
			PatientSingleSearchResponseTo1 response = restClient.doGet(url, PatientSingleSearchResponseTo1.class);

			if (response.isSuccess())
			{
				return response.getPatientTo1s().stream().map(MHAPatient::new).collect(Collectors.toList());
			}
		}
		catch (InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}

		return new ArrayList<>();
	}

	/**
	 * get a confirmed MHA patient by demographic number
	 * @param integration - the integration to search
	 * @param demographicNo - the demographic number to look up.
	 * @return mha patient object
	 */
	public MHAPatient getConfirmedPatientByDemographicNo(Integration integration, Integer demographicNo)
	{
		try
		{
			RestClientBase restClient = RestClientFactory.getRestClient(integration);

			String url = restClient.formatEndpoint("/clinic/" + integration.getRemoteId() +
					"/patients?search_by=remote_id&remote_id=%s", demographicNo);
			PatientSingleSearchResponseTo1 response = restClient.doGet(url, PatientSingleSearchResponseTo1.class);

			if (response.isSuccess())
			{
				return new MHAPatient(response.getPatientTo1());
			}
			else if (response.isNotFound())
			{
				throw new RecordNotFoundException("Could not find MHA patient with demographicNo: " + demographicNo.toString());
			}
			else if (response.isNotUnique())
			{
				throw new RecordNotUniqueException("Multiple patients with demographicNo: " + demographicNo.toString());
			}
		}
		catch(InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}

		return null;
	}

	/**
	 * get at remote patient from MHA by remote id
	 * @param integration - the integration to fetch the patient from
	 * @param remotePatientId - the remote patient id to fetch
	 * @return - the remote patient
	 * @throws RecordNotFoundException - if no patient exits with the provided id.
	 * @throws InvalidAccessException - if you do not have access to the requested patient record.
	 */
	public MHAPatient getRemotePatient(Integration integration, String remotePatientId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		String url = restClient.formatEndpoint("/clinic/%s/patient/%s/", integration.getRemoteId(), remotePatientId);

		return (new MHAPatient(restClient.doGet(url, PatientTo1.class)));
	}

	/**
	 * get a remote MHA patient
	 * @param demographic - the demographic who's remote MHA patient will be fetched
	 * @return - the remote MHA patient.
	 */
	public MHAPatient getPatient(Integration integration, Demographic demographic)
	{
		if (isPatientConfirmed(demographic.getId(), integration))
		{
			return getConfirmedPatientByDemographicNo(integration, demographic.getId());
		}
		else
		{
			if (StringUtils.trimToNull(demographic.getHin()) != null)
			{
				return getPatientByHin(integration, demographic.getHin(), MHAPatient.PROVINCE_CODES.valueOf(demographic.getHcType()));
			}
			else
			{
				throw new RecordNotFoundException("Demographic is not confirmed and has no HIN.");
			}
		}
	}

	public boolean updatePatientConnection(Integration integration, String loginToken, Demographic demographic, Boolean rejected)
	{
		try
		{
			// lookup MHA patient
			MHAPatient patient = null;
			// we must consider CLINIC_REJECTED as confirmed to deal with edge case around un_rejecting confirmed patient who's HIN does not match in MHA.
			if (isPatientConfirmed(demographic.getId(), integration,
					Arrays.asList(MHAPatient.LINK_STATUS.CONFIRMED, MHAPatient.LINK_STATUS.VERIFIED, MHAPatient.LINK_STATUS.CLINIC_REJECTED)))
			{
				patient = getConfirmedPatientByDemographicNo(integration, demographic.getId());
			}
			else
			{
				patient = getPatientByHin(integration, demographic.getHin(), MHAPatient.PROVINCE_CODES.valueOf(demographic.getHcType()));
			}

			String action = rejected ? "reject_connection" : "cancel_reject_connection";

			RestClientBase restClient = RestClientFactory.getRestClient(integration);
			return restClient.doPostWithToken(
					restClient.formatEndpoint("/clinic_user/self/clinic/patient/" + patient.getId() + "/" + action),
					loginToken,
					null,
					Boolean.class);
		}
		catch(InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}

		return false;
	}

	public void patientInvite(Integration integration, String loginToken, Demographic demographic)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/patient_invite");

		// get cell phone etc. from ext table
		DemographicExt ext = demographicExtDao.getDemographicExt(demographic.getId(), DemographicExt.KEY_DEMO_CELL);
		String cellPhone = (ext != null) ? ext.getValue() : null;

		PatientTo1 patientTransfer = new PatientTo1(demographic, cellPhone);
		PatientInviteTo1 patientInvite = new PatientInviteTo1(patientTransfer, String.valueOf(demographic.getId()), demographic.getProviderNo());

		Boolean response = restClient.doPostWithToken(url, loginToken, patientInvite, Boolean.class);
	}

	private void logInvalidIntegrationWarn(InvalidIntegrationException e)
	{
		MiscUtils.getLogger().warn("Could not connect to MHA. Invalid integration. " + e.getMessage());
	}

}
