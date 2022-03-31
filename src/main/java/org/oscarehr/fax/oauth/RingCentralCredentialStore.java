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
import org.oscarehr.config.JunoProperties;
import org.oscarehr.util.MiscUtils;
import org.springframework.http.HttpHeaders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RingCentralCredentialStore
{
	private static AuthorizationCodeFlow oAuthWorkFlow;
	public static final String LOCAL_USER_ID = "com.junoemr.fax.ringcentral";
	public static final long ACCESS_TOKEN_REFRESH_THRESHOLD_SECONDS = 3600L;

	private static final String OAUTH_PATH = "/restapi/oauth";
	private static final String AUTH_ENDPOINT = "/authorize";
	private static final String TOKEN_ENDPOINT = "/token";

	private static String clientId;
	private static String clientSecret;

	private static final File DATASTORE_DIR = new File("/tmp/com.junoemr.fax.datastore");
	private static Logger logger = MiscUtils.getLogger();

	public static void init(JunoProperties.FaxConfig faxConfig)
	{
		RingCentralCredentialStore.clientId = faxConfig.getRingcentralClientId();
		RingCentralCredentialStore.clientSecret= faxConfig.getRingcentralClientSecret();

		final String base_url = faxConfig.getRingcentralApiLocation() + OAUTH_PATH;
		final String auth_url = base_url + AUTH_ENDPOINT;
		final String token_url = base_url + TOKEN_ENDPOINT;

		try
		{
			oAuthWorkFlow = new AuthorizationCodeFlow.Builder(
				BearerToken.formEncodedBodyAccessMethod(),
				new NetHttpTransport(),
				new JacksonFactory(),
				new GenericUrl(token_url),
				makeClientParams(),
				getClientID(),
				auth_url
			)
				.setDataStoreFactory(new FileDataStoreFactory(DATASTORE_DIR))
				.enablePKCE()
				.build();
		}
		catch (IOException e)
		{
			logger.error("Could not init OAuth workflow", e);
		}
	}

	public static Credential getCredential(String id) throws IOException
	{
		return oAuthWorkFlow.loadCredential(id);
	}

	public static void deleteCredential(String id) throws IOException
	{
		oAuthWorkFlow.getCredentialDataStore().delete(id);
	}

	/**
	 * Construct a base64 encoded string of the format client_id:clientSecret.
	 *
	 * @return base64 auth string suitable for Authentication: Basic header.
	 */
	public static String makeBasicAuthentication()
	{
		return HttpHeaders.encodeBasicAuth(getClientID(), getClientSecret(), StandardCharsets.UTF_8);
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

	private static ClientParametersAuthentication makeClientParams()
	{
		return new ClientParametersAuthentication(getClientID(), null);
	}

	private static String getClientID()
	{
		if (clientId == null)
		{
			throw new RuntimeException("Missing ringcentral client id");
		}

		return clientId;
	}

	private static String getClientSecret()
	{
		if (clientSecret == null)
		{
			throw new RuntimeException("Missing ringcentral client secret");
		}

		return clientSecret;
	}
}