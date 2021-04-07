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

import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;
import org.oscarehr.integration.imdhealth.transfer.inbound.SSOSessionCredentials;
import org.oscarehr.integration.imdhealth.transfer.outbound.SSORequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.util.RESTClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
class IMDCommunicationService extends RESTClient
{
	private static String PROP_KEY_API = "imdhealth_api_domain";
	private static final String HEADER_AUTHORIZATION = "Authorization";

	protected static String apiUrl = OscarProperties.getInstance().getProperty(PROP_KEY_API);

	/**
	 * Connect to iMD oauth endpoint and retrieve a bearer token
	 *
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

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("grant_type", "client_credentials");
		requestBody.put("scope", "sso");

		BearerToken response = doPost(url, headers, requestBody, BearerToken.class);
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
	protected SSOSessionCredentials SSOLogin(BearerToken token, SSORequest ssoRequest)
	{
		String endpoint = concatEndpointStrings(apiUrl, "/v3/sso");
		String url = buildUrl(DEFAULT_PROTOCOL, endpoint);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HEADER_AUTHORIZATION, "Bearer " + token.getAccessToken());

		SSOSessionCredentials response = doPost(url, headers, ssoRequest, SSOSessionCredentials.class);
		return response;
	}
}

