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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name="FaxOAuthServlet",description="Ringcentral OAuth servlet", value="/fax/ringcentral/oauth", loadOnStartup = 1)
public class RingCentralAuthServlet extends AbstractAuthorizationCodeServlet
{
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException
	{
		String contextPath = req.getContextPath();
		String redirectPath = contextPath + "/web/#!/admin/faxConfig";

		if (req.getQueryString() != null)
		{
			redirectPath += "?" + req.getQueryString();
		}

		resp.sendRedirect(redirectPath);
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
		authorizationUrl.setState("foobar1234"); // TODO, send instance context here
		super.onAuthorization(req, resp, authorizationUrl);
	}
}
