<%--

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

--%>
<%@ page import="org.apache.cxf.rs.security.oauth.client.OAuthClientUtils" %>
<%@ page import="org.apache.cxf.jaxrs.client.WebClient" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.net.URI" %>
<%@ page import="org.oscarehr.common.dao.AppDefinitionDao" %>
<%@ page import="org.oscarehr.common.dao.AppUserDao" %>
<%@ page import="org.oscarehr.common.model.AppDefinition" %>
<%@ page import="org.oscarehr.common.model.AppUser" %>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="org.oscarehr.app.AppOAuth1Config" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%
	AppUserDao appUserDao = SpringUtils.getBean(AppUserDao.class);
	AppDefinitionDao appDefinitionDao = SpringUtils.getBean(AppDefinitionDao.class);

	Integer appId = null;
	AppDefinition appDef = null;

	AppOAuth1Config oauth1config = null;
	OAuthClientUtils.Consumer consumer = null;
	try {
		String paramId = request.getParameter("id");
		if("K2A".equals(paramId)) {
			appDef = appDefinitionDao.findByName("K2A");
			if(appDef!=null) appId = appDef.getId();
		}
		else {
			appId = (paramId != null) ? Integer.parseInt(paramId) : (Integer) session.getAttribute("appId");
			appDef = appDefinitionDao.find(appId);
		}

		if(appDef!=null) {
			oauth1config = AppOAuth1Config.fromDocument(appDef.getConfig());
			consumer = new OAuthClientUtils.Consumer(oauth1config.getConsumerKey(), oauth1config.getConsumerSecret());
		}
	}
	catch(NumberFormatException e) {
		MiscUtils.getLogger().error("Invalid app id", e);
	}
	catch(Exception e) {
		MiscUtils.getLogger().error("Error", e);
	}
	if(appDef == null || oauth1config == null || consumer == null) {
		response.sendRedirect("close.jsp");
	}
	else {
		if (request.getParameter("oauth_verifier") == null) {      //need to request a token
			try {
				WebClient requestTokenService = WebClient.create(oauth1config.getRequestTokenService()).encoding("text/plain;charset=UTF-8"); // /oauth/request_token
				requestTokenService = requestTokenService.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				URI callback = new URI("" + request.getRequestURL());
				MiscUtils.getLogger().error("" + request.getRequestURL());
				Map<String, String> extraParams = null;
				String authorizationServiceURI = oauth1config.getAuthorizationServiceURI();

				OAuthClientUtils.Token requestToken = OAuthClientUtils.getRequestToken(requestTokenService, consumer, callback, extraParams);
				session.setAttribute("requestToken", requestToken.getToken());
				session.setAttribute("requestTokenS", requestToken.getSecret());
				session.setAttribute("appId", appId);
				URI authUrl = OAuthClientUtils.getAuthorizationURI(authorizationServiceURI, requestToken.getToken());
				response.sendRedirect(authUrl.toString());
			}
			catch (Exception e) {
				MiscUtils.getLogger().error("Error getting Request Token from app " + appId + " for user " + (String) session.getAttribute("user"), e);
				session.setAttribute("oauthMessage", "Error requesting token from app");
				response.sendRedirect("close.jsp");
			}
		}
		else {
			try {
				WebClient accessTokenService = WebClient.create(oauth1config.getAccessTokenService()).encoding("text/plain;charset=UTF-8"); // /oauth/request_token
				accessTokenService = accessTokenService.accept("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				String oauthVerifier = request.getParameter("oauth_verifier");

				String token = (String) session.getAttribute("requestToken");
				String secret = (String) session.getAttribute("requestTokenS");
				OAuthClientUtils.Token requestToken = new OAuthClientUtils.Token(token,secret);
				OAuthClientUtils.Token accessToken = OAuthClientUtils.getAccessToken(accessTokenService, consumer, requestToken, oauthVerifier);

				//appUserDao
				AppUser appuser = new AppUser();
				appuser.setAppId(appId);
				appuser.setProviderNo((String) session.getAttribute("user"));

				String authenticationData = AppOAuth1Config.getTokenXML(accessToken.getToken(), accessToken.getSecret());
				appuser.setAuthenticationData(authenticationData);

				appuser.setAdded(new Date());
				appUserDao.saveEntity(appuser);
				session.setAttribute("oauthMessage", "Success");
			}
			catch (Exception e) {
				session.setAttribute("oauthMessage", "Error with verifing authentication");
				MiscUtils.getLogger().error("Error returning from app " + appId + " for user " + (String) session.getAttribute("user"), e);
			}
			finally {
				session.removeAttribute("requestToken");
				session.removeAttribute("requestTokenS");
				session.removeAttribute("appId");
			}
			response.sendRedirect("close.jsp");
		}
	}
%>