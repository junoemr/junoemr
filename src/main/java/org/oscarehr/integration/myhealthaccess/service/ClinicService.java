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

import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserShortTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class ClinicService extends BaseService
{
	private final String clinicEndPoint = concatEndpointStrings(BASE_END_POINT, "/clinic");

	public ClinicUserTo1 createUser(IntegrationData integrationData, ClinicUserTo1 newUser)
	{
		final String ENDPOINT_CREATE_USER = "/%/user/create";

		ClinicUserTo1 response = null;
		String apiKey = integrationData.getApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();

		try
		{
			String endpoint = formatEndpoint(ENDPOINT_CREATE_USER, clinicId);
			response = post(endpoint, apiKey, newUser, ClinicUserTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	public ClinicUserShortTokenTo1 getShortToken(IntegrationData integrationData) throws InvalidAccessException
	{
		final String ENDPOINT_SHORT_TOKEN = "/%s/user/%s/get_short_token";

		String apiKey = integrationData.getApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();
		String accessToken = integrationData.getUserAccessToken();
		String remoteUserId = integrationData.getRemoteUserId();

		ClinicUserShortTokenTo1 loginToken = null;

		try
		{
			String endpoint = formatEndpoint(ENDPOINT_SHORT_TOKEN, clinicId, remoteUserId);
			loginToken = postWithToken(endpoint, apiKey, null, ClinicUserShortTokenTo1.class, accessToken);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}


		return loginToken;
	}

	public ClinicUserTo1 getLongToken(IntegrationData integrationData, ClinicUserLoginTo1 userLogin)
	{
		final String ENDPOINT_LONG_TOKEN = "/%s/get_long_token";

		String apiKey = integrationData.getApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();
		String remoteUserId = integrationData.getRemoteUserId();

		ClinicUserTo1 accessToken = null;
		try
		{
			String endpoint = formatEndpoint(ENDPOINT_LONG_TOKEN, clinicId, remoteUserId);
			accessToken = post(endpoint, apiKey, userLogin, ClinicUserTo1.class);
		}
		catch (BaseException e) {
			ErrorHandler.handleError(e);
		}

		return accessToken;
	}

	public ClinicUserTo1 renewLongToken(IntegrationData integrationData)
	{
		final String ENDPOINT_RENEW = "/%s/user/%s/renew_long_token";

		String apiKey = integrationData.getApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();
		String remoteUserId = integrationData.getRemoteUserId();
		String token = integrationData.getUserIntegrationAccess().getAccessToken();

		ClinicUserTo1 response = null;

		try
		{
			String endpoint = formatEndpoint(ENDPOINT_RENEW, clinicId, remoteUserId);
			response =  postWithToken(endpoint, apiKey, null, ClinicUserTo1.class, token);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	/*
	 * Helper Methods
	 */
	private String formatEndpoint(String endpoint, Object... args)
	{
		return concatEndpointStrings(clinicEndPoint, String.format(endpoint, args));
	}

	private <S, T> T get(String endPoint, String apiKey, Class<T> responseClass)
	{
		return executeRequest(endPoint, apiKey, HttpMethod.GET, null, responseClass, BaseErrorTo1.class);
	}

	private <S, T> T getWithToken(String endPoint, String apiKey, S body, Class<T> responseClass, String token)
	{
		return executeRequestWithToken(endPoint, apiKey, HttpMethod.GET, token, body, responseClass, BaseErrorTo1.class);
	}

	private <S, T> T post(String endPoint, String apiKey, S body, Class<T> responseClass)
	{
		return executeRequest(endPoint, apiKey, HttpMethod.POST, body, responseClass, BaseErrorTo1.class);
	}

	private <S, T> T postWithToken(String endPoint, String apiKey, S body, Class<T> responseClass, String token)
	{
		return executeRequestWithToken(endPoint, apiKey, HttpMethod.GET, token, body, responseClass, BaseErrorTo1.class);
	}
}
