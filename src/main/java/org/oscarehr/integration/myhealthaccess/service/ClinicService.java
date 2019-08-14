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

import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


@Service
public class ClinicService extends BaseService
{
	private final String clinicEndPoint = concatEndpointStrings(
			BASE_END_POINT, "/clinic");

	// Get myhealthaccess user linked to the specified oscar user
	public ClinicUserTo1 getLinkedUser(String clinicID, String oscarUserID)
	{
		ClinicUserTo1 clinicUser = null;
		try
		{
			String getUserAPI = concatEndpointStrings(clinicEndPoint, "/" + clinicID + "/user_from_remote_id/" + oscarUserID);
			clinicUser = executeRequest(getUserAPI, HttpMethod.GET, ClinicUserTo1.class, BaseErrorTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}
		return clinicUser;
	}

	public ClinicUserTo1 getUserByEmail(String clinicID, String email)
	{
		ClinicUserTo1 clinicUser = null;

		try
		{
			String createUserAPI = concatEndpointStrings(clinicEndPoint, "/" + clinicID + "/user_from_email/" + URLEncoder.encode(email, "UTF-8"));
			clinicUser = executeRequest(createUserAPI, HttpMethod.GET, ClinicUserTo1.class, BaseErrorTo1.class);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("Could not encode email address");
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return clinicUser;
	}

	public ClinicUserCreateTo1 createUser(String clinicID, String oscarUserID, String email, String firstName, String lastName)
	{
		ClinicUserCreateTo1 response = null;

		try
		{
			String endPoint = concatEndpointStrings(clinicEndPoint, "/" + clinicID + "/user/create");

			ClinicUserTo1 newUser = new ClinicUserTo1();
			newUser.setEmail(email);
			newUser.setRemoteID(oscarUserID);
			newUser.setFirstName(firstName);
			newUser.setLastName(lastName);


			response = executeRequest(endPoint, HttpMethod.POST, newUser, ClinicUserCreateTo1.class, BaseErrorTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}

	public ClinicUserAccessTokenTo1 getLoginToken(String clinicID, String myHealthAccessUserID, ClinicUserAccessTokenTo1 authToken)
	{
		ClinicUserAccessTokenTo1 loginToken = null;

		try
		{
			String endPoint = concatEndpointStrings(clinicEndPoint, "/" + clinicID + "/user/" + myHealthAccessUserID + "/get_login_token");
			String tokenString = authToken.getToken();
			loginToken = executeRequestWithToken(endPoint, HttpMethod.POST, tokenString, null, ClinicUserAccessTokenTo1.class, BaseErrorTo1.class);
		}

		catch (BaseException e)
		{
			// TODO:  If the authToken is expired, fetch another one and redo the request
			ErrorHandler.handleError(e);
		}


		return loginToken;
	}

	public ClinicUserAccessTokenTo1 getAuthToken(String clinicID, String myHealthAccessUserID, String remoteID, String email, String password)
	{
		ClinicUserAccessTokenTo1 accessToken = null;
		try
		{
			String endpoint = concatEndpointStrings(clinicEndPoint, "/" + clinicID + "/user/" + myHealthAccessUserID + "/get_access_token");
			ClinicUserLoginTo1 loginBody = new ClinicUserLoginTo1(email, password);
			loginBody.setRemoteID(remoteID);

			accessToken = executeRequest(endpoint, HttpMethod.POST, loginBody, ClinicUserAccessTokenTo1.class, BaseErrorTo1.class);
		}
		catch (BaseException e) {
			ErrorHandler.handleError(e);
		}

		return accessToken;
	}

	public ClinicUserAccessTokenTo1 renewAuthToken(String clinicID, String mhaUserID, ClinicUserAccessTokenTo1 authToken)
	{
		ClinicUserAccessTokenTo1 response = null;

		final String renewEndpointRaw = "/%s/user/%s/renew_access_token";
		try
		{
			String endpoint = concatEndpointStrings(clinicEndPoint, String.format(renewEndpointRaw, clinicID, mhaUserID));
			String token = authToken.getToken();
			response =  executeRequestWithToken(endpoint, HttpMethod.POST, token,
			                                    null, ClinicUserAccessTokenTo1.class, BaseErrorTo1.class);
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}

		return response;
	}
}
