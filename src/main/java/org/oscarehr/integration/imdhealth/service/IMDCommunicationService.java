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

package org.oscarehr.integration.imdhealth.service;

import java.util.Base64;

import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOCredentials;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSORequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import oscar.util.RESTClient;

@Service
class IMDCommunicationService extends RESTClient
{
	protected static String apiUrl = "ca-v5.api.imdhealth.com";   // Production: api.imdhealth.com


	private static final String HEADER_AUTHORIZATION = "Authorization";

	/**
	 * Connect to iMD oauth endpoint and retrieve a bearer token
	 * @param client_id iMDHealth client_id issued to organization
	 * @param client_secret iMDHealth client_secret issued to organization
	 *
	 * @return 24h bearer token for use with iMDHealth SSO api
	 */
	protected BearerToken getBearerToken(String client_id, String client_secret)
	{
		String authString = client_id + ":" + client_secret;
		String authHeader = Base64.getEncoder().encodeToString(authString.getBytes());

		String endpoint = concatEndpointStrings(apiUrl, "/oauth/token");
		String url = buildUrl(DEFAULT_PROTOCOL, endpoint);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HEADER_AUTHORIZATION, "Basic " + authHeader);

		BearerToken response = doPost(url, headers, null, BearerToken.class);

		return response;
	}

	/**
	 * Connect to the iMDHealth api with oauth bearer token to obtain SSO credentials
	 *
	 * @param token Previously obtained bearer token (see getBearerToken)
	 * @param ssoRequest user and organization info payload
	 *
	 * @return SSO credentials needed to generate a SSO verified login link
	 */
	protected SSOCredentials SSOLogin(BearerToken token, SSORequest ssoRequest)
	{
		String endpoint = concatEndpointStrings(apiUrl, "/v3/sso");
		String url = buildUrl(DEFAULT_PROTOCOL, endpoint);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HEADER_AUTHORIZATION, "Bearer " + token);

		SSOCredentials response = doPost(url, headers, ssoRequest, SSOCredentials.class);

		return response;
	}
}

