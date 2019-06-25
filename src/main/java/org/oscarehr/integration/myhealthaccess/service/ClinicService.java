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

import org.oscarehr.integration.myhealthaccess.dto.BaseErrorTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.dto.GenericErrorTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.util.MiscUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Service
public class ClinicService extends BaseService
{
	private final String clinicEndPoint = concatEndpointStrings(
			BASE_END_POINT, "/clinic");

	public ClinicService()
	{
	}

	// Get myhealthaccess user linked to the specified oscar user
	public ClinicUserTo1 getLinkedUser(String clinicID, String userID)
	{
		ClinicUserTo1 clinicUser = null;
		try
		{
			String endPoint = concatEndpointStrings(clinicEndPoint, "/" +
					clinicID + "/user_from_remote_id/" + userID);
			clinicUser = executeRequest(
					endPoint, HttpMethod.GET, ClinicUserTo1.class, BaseErrorTo1.class);
		} catch (BaseException e)
		{
			handleRecordNotFound(e);
		}
		return clinicUser;
	}

	public ClinicUserTo1 getUserByEmail(String clinicID, String email)
	{
		MiscUtils.getLogger().error("email2: " + email);
		String endPoint = null;
		try
		{
			endPoint = concatEndpointStrings(clinicEndPoint, "/" +
					clinicID + "/user_from_email/" + URLEncoder.encode(email, "UTF-8"));
		} catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("Could not encode email address");
		}
		MiscUtils.getLogger().error("endPoint: " + endPoint);
		ClinicUserTo1 clinicUser = null;
		try
		{
			clinicUser = executeRequest(
					endPoint, HttpMethod.GET, ClinicUserTo1.class, BaseErrorTo1.class);
		}
		catch (BaseException e)
		{
			handleRecordNotFound(e);
		}
		return clinicUser;
	}

	public ClinicUserAccessTokenTo1 getLoginToken(
			String clinicID,
			String myHealthAccessUserID,
			String email,
			String password,
			String oscarUserID
			)
			throws IOException, NoSuchAlgorithmException, KeyManagementException
	{
		String endPoint = concatEndpointStrings(clinicEndPoint, "/" +
				clinicID + "/user/" + myHealthAccessUserID + "/get_login_token?" +
				"remote_id=" + URLEncoder.encode(oscarUserID, "UTF-8"));
		ClinicUserLoginTo1 userLogin = new ClinicUserLoginTo1(email, password);
		return executeRequest(
				endPoint, HttpMethod.POST, userLogin, ClinicUserAccessTokenTo1.class,
				BaseErrorTo1.class);
	}

	private void handleRecordNotFound(BaseException e)
	{
		MiscUtils.getLogger().error("HANDLING base exception");
		if (e.getErrorObject().isHasGenericErrors())
		{
			MiscUtils.getLogger().error("HAS GENERIC ERRORS");
			// TODO Get the first generic error. I'm thinking for an external API we might want
			// to change this to only ever return one. It gets too complicated when you can have
			// multiple error messages
			GenericErrorTo1 genericError = e.getErrorObject().getGenericErrors().get(0);
			MiscUtils.getLogger().error("Code: " + genericError.getCode());
			MiscUtils.getLogger().error("Message: " + genericError.getMessage());
			MiscUtils.getLogger().error("Message: " + genericError.getMessage());
			if (genericError.getCode().equals(GenericErrorTo1.ERROR_RECORD_NOT_FOUND))
			{
				MiscUtils.getLogger().error(
						genericError.getCode() + " : " + genericError.getMessage());
				throw new RecordNotFoundException("Unable to find MyHealthAccess record");
			}
			MiscUtils.getLogger().error("DOESN'T MATCH");
			throw e;
		}
	}
}
