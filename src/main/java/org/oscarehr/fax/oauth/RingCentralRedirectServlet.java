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
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.integration.ringcentral.api.RingCentralApiConnector;
import org.oscarehr.integration.ringcentral.api.result.RingCentralAccountInfoResult;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

@WebServlet(name="FaxOAuthRedirectServlet",description="Ringcentral OAuth redirect servlet", value="/fax/ringcentral/redirect", loadOnStartup = 1)
public class RingCentralRedirectServlet extends AbstractAuthorizationCodeCallbackServlet
{
	private static Logger logger = MiscUtils.getLogger();
	@Autowired
	private FaxAccountDao faxAccountDao;

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
		RingCentralApiConnector apiConnector = new RingCentralApiConnector();
		RingCentralAccountInfoResult result = apiConnector.getAccountInfo();

		List<FaxAccount> faxAccounts = faxAccountDao.findByLoginId(FaxProvider.RINGCENTRAL, String.valueOf(result.getId()));

		String contextPath = req.getContextPath();
		String redirectPath = contextPath + "/fax/ringcentral/oauth";
		// Per library documentation, this should redirect back to the AuthServlet onSuccess
		// The Oauth servlet will then redirect us back to the fax admin page
		try
		{
			URIBuilder redirect = new URIBuilder(redirectPath);
			if(faxAccounts.isEmpty())
			{
				// new account setup parameters
				redirect.addParameter("type", FaxProvider.RINGCENTRAL.name());
				redirect.addParameter("accountId", result.getId().toString());
			}
			resp.sendRedirect(redirect.toString());
		}
		catch (URISyntaxException e)
		{
			throw new MalformedURLException(e.getMessage());
		}
	}

	@Override
	protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse)
		throws ServletException, IOException
	{
		logger.error(errorResponse.getError());

		String contextPath = req.getContextPath();
		resp.sendRedirect(contextPath + "/web/#!/admin/faxConfig");
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
}
