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
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import oscar.OscarProperties;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="FaxOAuthServlet",description="Ringcentral OAuth servlet", value="/oauth",loadOnStartup = 1)
public class RingCentralAuthServlet extends AbstractAuthorizationCodeServlet implements
	RingCentralOAuthServlet
{
	@Override
	public String getClientID()
	{
		// TODO, integrate with openshift secrets management
		String clientId = System.getenv("RINGCENTRAL_CLIENT_ID");
		if (clientId == null)
		{
			throw new RuntimeException("Missing required env variable $RINGCENTRAL_CLIENT_ID");
		}

		return clientId;
	}

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		// Any init code goes here
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException
	{
		ClientParametersAuthentication clientId = new ClientParametersAuthentication(
			getClientID(), null);

		AuthorizationCodeFlow auth = new AuthorizationCodeFlow.Builder(
			BearerToken.formEncodedBodyAccessMethod(),
			new NetHttpTransport(),
			new JacksonFactory(),
			new GenericUrl(TOKEN_SERVER_URL),
			clientId,
			getClientID(),
			AUTH_SERVER_URL
		)
			.enablePKCE()
			.build();

		// TODO datastore (file???)

		return auth;
	}

	@Override protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException
	{
		// Thanks google, to set the state this method has to be overridden
		authorizationUrl.setState("foobar1234"); // TODO, send instance context here
		super.onAuthorization(req, resp, authorizationUrl);
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		String redirectUrl = OscarProperties.getInstance().getProperty("fax.ringcentral.redirect_url", "");
		if (redirectUrl == null)
		{
			throw new RuntimeException("OAuth redirect URL not specified");
		}

		GenericUrl url = new GenericUrl(redirectUrl);
		return url.build();
	}

	@Override
	protected String getUserId(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		// Right now this can be a constant because all users on the same app are accessing the same ringcentral account.
		// Once accounts are enabled per user, this should be tied to the sessionId or getLoggedInInfo.
		return LOCAL_USER_ID;
	}
}