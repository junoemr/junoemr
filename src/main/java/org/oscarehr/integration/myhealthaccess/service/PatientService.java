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

import org.oscarehr.integration.myhealthaccess.dto.PatientConfirmedTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService extends BaseService
{
	@Autowired
	ClinicService clinicService;

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
			return get(buildPatientConfirmedURL(clinicId, demographicNo), apiKey, PatientConfirmedTo1.class).getConfirmed();
		}
		catch(InvalidIntegrationException e)
		{
			MiscUtils.getLogger().error(e.getMessage(), e);
		}
		return false;
	}

	protected String buildPatientConfirmedURL(String clinicId, Integer demographicNo)
	{
		return BASE_END_POINT + "/clinic/" + clinicId + "/patients/" + demographicNo + "/confirmed";
	}
}
