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
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class ClinicService extends BaseService
{
	private final String clinicEndPoint = concatEndpointStrings(
			BASE_END_POINT, "/clinic");

	public ClinicService()
	{
	}

	// Get myhealthaccess user linked to the specified oscar user
	public ClinicUserTo1 getLinkedUser(String clinicID, String userID)
			throws IOException, NoSuchAlgorithmException, KeyManagementException
	{
		String endPoint = concatEndpointStrings(clinicEndPoint, "/" +
				clinicID + "/user_from_remote_id/" + userID);
		ClinicUserTo1 clinicUser = executeRequest(
				endPoint, HttpMethod.GET, ClinicUserTo1.class, BaseErrorTo1.class);
		return clinicUser;
	}

	public ClinicUserAccessTokenTo1 getLoginToken(
			String clinicID, String myhealthAccessUserID, String email, String password)
			throws IOException, NoSuchAlgorithmException, KeyManagementException
	{
		String endPoint = concatEndpointStrings(clinicEndPoint, "/" +
				clinicID + "/user/" + myhealthAccessUserID + "/get_login_token");
		ClinicUserLoginTo1 userLogin = new ClinicUserLoginTo1(email, password);
		return executeRequest(
				endPoint, HttpMethod.POST, userLogin, ClinicUserAccessTokenTo1.class,
				BaseErrorTo1.class);
	}
}
