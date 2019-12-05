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

package org.oscarehr.integration.iceFall.service;

import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.oscarehr.integration.iceFall.service.transfer.IceFallAuthenticationTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.RESTClient;

@Service
public class IceFallRESTService
{
	public static final String API_BASE = "api/";

	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	/**
	 * authenticate with the icefall api, updating the api token
	 */
	public void authenticate()
	{
		IceFallCredentials iceFallCredentials = iceFallCredentialsDao.getCredentials();
		IceFallAuthenticationTo1 credentials = new IceFallAuthenticationTo1();;
		credentials.setUsername(iceFallCredentials.getUsername());
		credentials.setPassword(iceFallCredentials.getPassword());

		RESTClient.doPost(getIceFallUrlBase() + "/api-token-auth/", null, credentials, )
	}


	private String getIceFallUrlBase()
	{
		//TODO property setting. or perhaps option
		return "https://api.canopygrowthweb.com/";
	}
}
