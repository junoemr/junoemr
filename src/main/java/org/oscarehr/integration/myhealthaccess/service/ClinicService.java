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

import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicStatusResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class ClinicService extends BaseService
{
	// Clinic API calls
	public ClinicUserCreateResponseTo1 createClinicUser(IntegrationData integrationData, ClinicUserCreateTo1 newUser)
	{
		String endpoint = "/clinic/%s/clinic_user/create";

		ClinicUserCreateResponseTo1 response = null;
		String apiKey = integrationData.getClinicApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId);
			response = post(endpoint, apiKey, newUser, ClinicUserCreateResponseTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	public ClinicUserLoginTokenTo1 clinicUserLogin(IntegrationData integrationData) throws InvalidAccessException
	{
		String endpoint = "/clinic_user/%s/api_key_login";

		String apiKey = integrationData.getUserApiKey();
		String remoteUserId = integrationData.getRemoteUserId();

		ClinicUserLoginTokenTo1 loginToken = null;

		try
		{
			endpoint = formatEndpoint(endpoint, remoteUserId);
			loginToken = post(endpoint, apiKey, null, ClinicUserLoginTokenTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return loginToken;
	}

	public ClinicStatusResponseTo1 testConnection(Integration integration)
	{
		String endpoint = "/clinic/%s/test_connection";

		String apiKey = integration.getApiKey();
		String clinicId = integration.getRemoteId();

		ClinicStatusResponseTo1 response = null;

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId);
			response = get(endpoint, apiKey, ClinicStatusResponseTo1.class);
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
		return concatEndpointStrings(BASE_END_POINT, String.format(endpoint, args));
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
		return executeRequestWithToken(endPoint, apiKey, HttpMethod.POST, token, body, responseClass, BaseErrorTo1.class);
	}
}
