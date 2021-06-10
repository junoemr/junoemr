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
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotUniqueException;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("myHealthPatientService")
public class PatientService extends BaseService
{
	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicExtDao demographicExtDao;


	public boolean isPatientConfirmed(Integer demographicNo, Integration integration)
	{
		return isPatientConfirmed(
				demographicNo,
				integration,
				Arrays.asList(MHAPatient.LINK_STATUS.CONFIRMED, MHAPatient.LINK_STATUS.VERIFIED));
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
	 * @param isNewborn - if the patient is a newborn or not. This determines which set of patients MHA will search.
	 * @return an MHA patient object.
	 */
	public MHAPatient getPatientByHin(Integration integration, String hin, MHAPatient.PROVINCE_CODES hinProvince, boolean isNewborn)
	{
		if (hin == null || hin.isEmpty())
		{
			throw new IllegalArgumentException("hin cannot be null or empty");
		}

		try
		{
			RestClientBase restClient = RestClientFactory.getRestClient(integration);

			String url = restClient.formatEndpoint(
					"/clinic/" + integration.getRemoteId() +
					"/patients?search_by=hin&health_number=%s&health_care_province=%s&newborn=%s",
					hin,
					hinProvince,
					isNewborn);

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
				return getPatientByHin(
						integration,
						demographic.getHin(),
						MHAPatient.PROVINCE_CODES.valueOf(demographic.getHcType()),
						demographic.isMarkedAsBCNewborn());
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
			if (isPatientConfirmed(
					demographic.getId(),
					integration,
					Arrays.asList(MHAPatient.LINK_STATUS.CONFIRMED, MHAPatient.LINK_STATUS.VERIFIED, MHAPatient.LINK_STATUS.CLINIC_REJECTED)))
			{
				patient = getConfirmedPatientByDemographicNo(integration, demographic.getId());
			}
			else
			{
				patient = getPatientByHin(
						integration,
						demographic.getHin(),
						MHAPatient.PROVINCE_CODES.valueOf(demographic.getHcType()),
						demographic.isMarkedAsBCNewborn());
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
