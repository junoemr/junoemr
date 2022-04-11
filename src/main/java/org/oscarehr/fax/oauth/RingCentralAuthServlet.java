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
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeServlet;
import com.google.gson.Gson;
import org.apache.http.client.utils.URIBuilder;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.fax.service.FaxAccountService;
import org.oscarehr.fax.transfer.FaxAccountTransferOutbound;
import org.oscarehr.integration.ringcentral.api.RingCentralApiConnector;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.UriUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name="FaxOAuthServlet",description="Ringcentral OAuth servlet", value="/fax/ringcentral/oauth", loadOnStartup = 1)
public class RingCentralAuthServlet extends AbstractAuthorizationCodeServlet
{
	@Autowired
	private FaxAccountService faxAccountService;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		RingCentralApiConnector apiConnector = new RingCentralApiConnector();
		RingCentralAccountInfoResult result = apiConnector.getAccountInfo();

		FaxAccountTransferOutbound faxAccountTransferOutbound = faxAccountService.findOrCreateByLoginId(FaxProvider.RINGCENTRAL, String.valueOf(result.getId()));

		try
		{
			String contextPath = req.getContextPath();
			URIBuilder redirect = new URIBuilder(contextPath + "/web/");
			URIBuilder fragment = new URIBuilder("!/admin/faxConfig");

			// tell page to open existing account using params
			fragment.addParameter("accountId", faxAccountTransferOutbound.getId().toString());
			redirect.setFragment(fragment.toString());
			resp.sendRedirect(redirect.toString());
		}
		catch (URISyntaxException e)
		{
			throw new MalformedURLException(e.getMessage());
		}
	}

	@Override
	protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException
	{
		return RingCentralCredentialStore.getFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		return RingCentralCredentialStore.getRedirectURL();
	}

	@Override
	protected String getUserId(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		return RingCentralCredentialStore.getUserId();
	}

	@Override protected void onAuthorization(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeRequestUrl authorizationUrl) throws ServletException, IOException
	{
		String domain = req.getRequestURL().toString().replace(req.getRequestURI(), "");

		Map<String, String> stateMap = new HashMap<>();
		stateMap.put("host", domain + req.getContextPath());
		stateMap.put("path", "/fax/ringcentral/redirect");
		stateMap.put("module", "fax.ringcentral");

		authorizationUrl.setState(UriUtils.encodeQueryParam(new Gson().toJson(stateMap), StandardCharsets.UTF_8));
		super.onAuthorization(req, resp, authorizationUrl);
	}
}