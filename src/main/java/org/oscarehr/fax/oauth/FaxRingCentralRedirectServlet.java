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
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.log4j.Logger;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="FaxOAuthRedirectServlet",description="Ringcentral OAuth redirect servlet", value="/fax_redirect",loadOnStartup = 1)
public class FaxRingCentralRedirectServlet extends AbstractAuthorizationCodeCallbackServlet implements
	RingCentralOAuthServlet
{
	@Autowired
	private SystemPreferenceService systemPreferenceService;

	private static Logger logger = MiscUtils.getLogger();

	@Override
	public void init(ServletConfig config) throws ServletException
	{
		// Any init code goes here
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}

	@Override
	protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential)
		throws ServletException, IOException
	{
		resp.sendRedirect(""); // TODO: redirect back to fax page
		// TODO: pass credential around?  RingCentalApiConnector.setCredential?
	}

	@Override
	protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
		throws ServletException, IOException
	{
		logger.error(errorResponse.getError());  // TODO: redirect back to fax page
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException
	{
		ClientParametersAuthentication clientId = new ClientParametersAuthentication(
			getClientID(), null);

		AuthorizationCodeFlow flow = new AuthorizationCodeFlow.Builder(
			BearerToken.formEncodedBodyAccessMethod(),
			new NetHttpTransport(),
			new JacksonFactory(),
			new GenericUrl(TOKEN_SERVER_URL),
			clientId,
			getClientID(),
			AUTH_SERVER_URL
		).build();

		// TODO: credential store
		return flow;
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		String redirectUrl = systemPreferenceService.getPropertyValue("fax.ringcentral.redirect_url", null);
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
		return LOCAL_USER_ID;
	}

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
}