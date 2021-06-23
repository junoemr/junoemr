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
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.client.RestClientBase;
import org.oscarehr.integration.myhealthaccess.client.RestClientFactory;
import org.oscarehr.integration.myhealthaccess.dto.PatientAccessDto;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.model.MHAPatient;
import org.oscarehr.integration.myhealthaccess.model.MhaPatientAccess;
import org.springframework.stereotype.Service;

@Service("mhaPatientAccessService")
public class PatientAccessService extends BaseService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

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
}
