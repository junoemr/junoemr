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

package org.oscarehr.integration.clinicaid.service.v2;

import org.apache.commons.codec.binary.Base64;
import org.oscarehr.integration.clinicaid.dto.v2.base.ClinicAidResponse;
import org.oscarehr.integration.clinicaid.dto.v2.MasterNumber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import oscar.OscarProperties;
import oscar.util.RESTClient;


@Service
@ConditionalOnProperty(value="billing_type", havingValue="CLINICAID")
public class ClinicAidCommunicationService extends RESTClient
{
	private static OscarProperties props = OscarProperties.getInstance();
	
	private static String CLINICAID_API_DOMAIN = props.getProperty("clinicaid_api_domain");
	private static String CLINICAID_API_KEY = props.getProperty("clinicaid_api_key");
	private static String CLINICAID_INSTANCE_ID = props.getProperty("clinicaid_instance_name");
	private static String API_PATH = "/api/v2/";
	
	ClinicAidResponse<MasterNumber> getOntarioMasterNumber(String masterNumber)
	{
		String endPoint = CLINICAID_API_DOMAIN + API_PATH + "ontario_master_numbers/" + masterNumber;
		HttpHeaders authHeader = buildAuthHeader();
		
		ParameterizedTypeReference<ClinicAidResponse<MasterNumber>> typeRef = new ParameterizedTypeReference<ClinicAidResponse<MasterNumber>>(){};
		
		return doGet(endPoint, authHeader, null, typeRef);
	}
	
	
	private HttpHeaders buildAuthHeader()
	{
		HttpHeaders header = new HttpHeaders();
		
		String userPassString =  CLINICAID_INSTANCE_ID + ":" + CLINICAID_API_KEY;
		String userPassBase64String = new String(new Base64().encode(userPassString.getBytes()));
		userPassBase64String = userPassBase64String.replaceAll("\n", "").replaceAll("\r", "");
		String basicAuthString = "Basic " + userPassBase64String;
		
		header.add("Authorization", basicAuthString);
		
		return header;
	}
}
