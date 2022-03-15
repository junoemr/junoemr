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
import org.apache.log4j.Logger;
import org.oscarehr.fax.dao.FaxAccountDao;
import org.oscarehr.fax.model.FaxAccount;
import org.oscarehr.fax.provider.FaxProvider;
import org.oscarehr.integration.ringcentral.api.RingcentralApiConnector;
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
import java.util.List;

@WebServlet(name="FaxOAuthRedirectServlet",description="Ringcentral OAuth redirect servlet", value="/fax_redirect",loadOnStartup = 1)
public class RingCentralRedirectServlet extends AbstractAuthorizationCodeCallbackServlet
{
	private static Logger logger = MiscUtils.getLogger();

	protected String REDIRECT_URL = System.getenv("RINGCENTRAL_REDIRECT_URL");

	@Autowired
	RingcentralApiConnector apiConnector;

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
		apiConnector.setCredential(credential);
		RingCentralAccountInfoResult result = apiConnector.getAccountInfo();
		List<FaxAccount> faxAccounts = faxAccountDao.findByLoginId(FaxProvider.RINGCENTRAL, String.valueOf(result.getId()));

		String contextPath = req.getContextPath();
		if(faxAccounts.isEmpty()) // new account setup
		{
			resp.sendRedirect(contextPath + "/web/#!/admin/faxConfig" +
					"?type=" + FaxProvider.RINGCENTRAL +
					"&accountId=" + result.getId());
		}
		else // existing account re-login
		{
			//todo - how do we handle 2 accounts linked to the same account?
			resp.sendRedirect(contextPath + "/web/#!/admin/faxConfig");
		}
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
		return apiConnector.getOauthLoginFlow();
	}

	@Override
	protected String getRedirectUri(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		return REDIRECT_URL;
	}

	@Override
	protected String getUserId(HttpServletRequest httpServletRequest)
		throws ServletException, IOException
	{
		return RingcentralApiConnector.LOCAL_USER_ID;
	}
}