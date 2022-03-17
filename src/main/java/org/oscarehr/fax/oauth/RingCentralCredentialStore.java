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

package org.oscarehr.fax.oauth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import java.io.File;
import java.io.IOException;

public class RingCentralCredentialStore
{
	private static AuthorizationCodeFlow oAuthWorkFlow;
	public static final String LOCAL_USER_ID = "com.junoemr.fax.ringcentral";

	private static final String BASE_URL = "https://platform.devtest.ringcentral.com";
	private static final String AUTH_SERVER_URL = BASE_URL + "/restapi/oauth/authorize";
	private static final String TOKEN_SERVER_URL = BASE_URL + "/restapi/oauth/token";

	private static final File DATASTORE_DIR = new File("/tmp/com.junoemr.fax.datastore");

	private static Logger logger = MiscUtils.getLogger();

	static
	{
		try
		{
			oAuthWorkFlow = new AuthorizationCodeFlow.Builder(
				BearerToken.formEncodedBodyAccessMethod(),
				new NetHttpTransport(),
				new JacksonFactory(),
				new GenericUrl(TOKEN_SERVER_URL),
				makeClientParams(),
				getClientID(),
				AUTH_SERVER_URL
			)
				.setDataStoreFactory(new FileDataStoreFactory(DATASTORE_DIR))
				.enablePKCE()
				.build();
		}
		catch (IOException e)
		{
			logger.error("Could not init datastore", e);
		}
	}

	public static Credential getCredential(String id) throws IOException
	{
		return oAuthWorkFlow.loadCredential(id);
	}

	private static ClientParametersAuthentication makeClientParams()
	{
		return new ClientParametersAuthentication(getClientID(), null);
	}

	private static String getClientID()
	{
		String clientId = System.getenv("FAX.RINGCENTRAL.CLIENT_ID");
		if (clientId == null)
		{
			throw new RuntimeException("Missing required env variable $FAX.RINGCENTRAL.CLIENT_ID");
		}

		return clientId;
	}

	protected static String getRedirectURL()
	{
		String clientId = System.getenv("FAX.RINGCENTRAL.REDIRECT_URL");
		if (clientId == null)
		{
			throw new RuntimeException("Missing required env variable $FAX.RINGCENTRAL.REDIRECT_URL");
		}

		return clientId;
	}

	protected static String getUserId()
	{
		return LOCAL_USER_ID;
	}

	@Synchronized
	protected static AuthorizationCodeFlow getFlow()
	{
		return oAuthWorkFlow;
	}
}