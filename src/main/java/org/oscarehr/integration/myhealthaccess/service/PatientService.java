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

import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.PatientConfirmedTo1;
import org.oscarehr.integration.myhealthaccess.dto.PatientSingleSearchResponseTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotUniqueException;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService extends BaseService
{
	@Autowired
	ClinicService clinicService;

	@Autowired
	DemographicDao demographicDao;

	/**
	 * check if the provided demographic is confirmed ("confirmed and liked to this Juno EMR instance")
	 * @param demographicNo - the demographic to check
	 * @param siteName - the site to look the demographic up in.
	 * @return - true if confirmed, false otherwise (including if there is not MHA patient for this demographic)
	 */
	public boolean isPatientConfirmed(Integer demographicNo, String siteName)
	{
		try
		{
			String apiKey = getApiKey(siteName);
			String clinicId = getClinicId(siteName);
			return get(formatEndpoint("/clinic/" + clinicId + "/patients/" + demographicNo + "/confirmed"), apiKey, PatientConfirmedTo1.class).getConfirmed();
		}
		catch(InvalidIntegrationException e)
		{
			MiscUtils.getLogger().warn("Cannot check patient confirmation status for demographic: " +
							demographicNo + " at site: " + siteName + " with error: " + e.getMessage(), e);
		}
		return false;
	}

	public MHAPatient getPatientByHin(Integration integration, String hin, MHAPatient.PROVINCE_CODES hinProvince)
	{
		if (hin == null || hin.isEmpty())
		{
			throw new IllegalArgumentException("hin cannot be null or empty");
		}

		try
		{
			String url = formatEndpoint("/clinic/" + integration.getRemoteId() +
											"/patients/search/hin?health_number=%s&health_care_province=%s", hin, hinProvince);
			PatientSingleSearchResponseTo1 response = get(url, integration.getApiKey(), PatientSingleSearchResponseTo1.class);

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

	public boolean updatePatientConnection(Integration integration, String loginToken, Demographic demographic, Boolean rejected)
	{
		try
		{
			MHAPatient patient = getPatientByHin(integration, demographic.getHin(), MHAPatient.PROVINCE_CODES.valueOf(demographic.getHcType()));

			String action = rejected ? "reject" : "un_reject";
			return postWithToken(
							formatEndpoint("/clinic_user/patient/" + patient.getId() + "/clinic/" + action),
							integration.getApiKey(), null, Boolean.class, loginToken);
		}
		catch(InvalidIntegrationException e)
		{
			logInvalidIntegrationWarn(e);
		}
		catch(RecordNotFoundException e)
		{
			// no MHA patient with hin. suppress.
		}

		return false;
	}

	private void logInvalidIntegrationWarn(InvalidIntegrationException e)
	{
		MiscUtils.getLogger().warn("Could not reject patient connection. Invalid integration. " + e.getMessage());
	}

}
