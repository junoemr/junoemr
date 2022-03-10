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

package org.oscarehr.fax.auth;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="FaxServlet",description="Ringcentral OAuth servlet", value="/oauth",loadOnStartup = 1)
public class FaxServlet extends AbstractAuthorizationCodeServlet
{
	Logger logger = MiscUtils.getLogger();

	private static final String baseUrl = "";
	private static final String tokenServer = baseUrl + "/restapi/oauth/authorize";

	private String getClientID()
	{
		String clientId = System.getenv("RINGCENTRAL_CLIENT_SECRET");
		if (clientId == null)
		{
			throw new RuntimeException("Missing required environment variable");
		}

		return clientId;
	}

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		logger.info("####################################################### HELLO ###############################");
		super.init(config);
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException
	{
		ClientParametersAuthentication clientSecret = new ClientParametersAuthentication(getClientID(), null);

		AuthorizationCodeFlow auth = new AuthorizationCodeFlow.Builder(
			BearerToken.formEncodedBodyAccessMethod(),
			new NetHttpTransport(),
			new JacksonFactory(),
			new GenericUrl("tokenserverUrl"),
			clientSecret,
			getClientID(),
			"authServerUrl"
		).build();

		return auth;
	}

	@Override protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException
	{
		authorizationUrl.setState("xyz");
		super.onAuthorization(req, resp, authorizationUrl);
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		GenericUrl url = new GenericUrl(httpServletRequest.getRequestURL().toString()); url.setRawPath("/oauthcallback");
		return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		return "ringcentral";  // Right now this can be a constant because all users on the same app are accessing the same ringcentral account;
	}
}