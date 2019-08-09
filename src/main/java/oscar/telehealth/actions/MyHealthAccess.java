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

package oscar.telehealth.actions;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserAccessTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MyHealthAccess extends DispatchAction
{
	private MyHealthAccessService myHealthAccessService = SpringUtils.getBean(MyHealthAccessService.class);
	private DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
	private ProviderData loggedInProvider;
	private Security loggedInUser;
	private ClinicUserTo1 remoteUser;

	public ActionForward startTelehealth(ActionMapping mapping, ActionForm form, HttpServletRequest request,
										 HttpServletResponse response) throws UnsupportedEncodingException
	{
		try
		{
			MiscUtils.getLogger().error("Set logged in data");
			setLoggedInData(request);
			fetchRemoteUser(request);
		}
		catch (RecordNotFoundException e)
		{
			MiscUtils.getLogger().error("Couldn't find user. Creating...");

			ActionRedirect createUserRedirect = new ActionRedirect(mapping.findForward("createUser"));
			createUserRedirect.addParameter("demographicNo", request.getParameter("demographicNo"));
			createUserRedirect.addParameter("siteName", request.getParameter("siteName"));

			return createUserRedirect;
		}

		ClinicUserAccessTokenTo1 myHealthAccessAuthToken = loggedInUser.getMyHealthAccessAuthToken();

		// TODO check expiry
		if (myHealthAccessAuthToken == null)
		{
			MiscUtils.getLogger().error("token null or expired");
			ActionRedirect loginAction = new ActionRedirect(mapping.findForward("pushLogin"));
			loginAction.addParameter("demographicNo", request.getParameter("demographicNo"));
			loginAction.addParameter("siteName", request.getParameter("siteName"));
			return loginAction;     // get a new long token
		}

		Demographic patient = getDemographic(request);
		Site site = getSite(request);

		String myHealthAccessURL = myHealthAccessService.buildTeleHealthRedirectURL(
				remoteUser,
				patient,
				site);

		return pushToMyHealthAccess(myHealthAccessURL, site);
	}

	public ActionForward createUser(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String email = request.getParameter("email");

		try
		{
			ClinicUserTo1 newUser = myHealthAccessService.createUser(loggedInUser, loggedInProvider, email, getSite(request));
			this.remoteUser = newUser;

			ActionRedirect confirmUserAction = new ActionRedirect(mapping.findForward("confirmUser"));
			confirmUserAction.addParameter("demographicNo", request.getParameter("demographicNo"));
			confirmUserAction.addParameter("siteName", request.getParameter("siteName"));
			confirmUserAction.addParameter("email", email);

			return confirmUserAction;
		}
		catch (Exception e)     // TODO:  Record already exists exception
		{
			myHealthAccessService.getUserByEmail(email, getSite(request));

			ActionRedirect loginAction = new ActionRedirect(mapping.findForward("pushLogin"));
			loginAction.addParameter("demographicNo", request.getParameter("demographicNo"));
			loginAction.addParameter("siteName", request.getParameter("siteName"));
			loginAction.addParameter("email", email);

			return loginAction;
		}
	}

	private ActionForward pushToMyHealthAccess(String myHealthAccessURL, Site site)
	{
		ClinicUserAccessTokenTo1 loginToken = loggedInUser.getMyHealthAccessLoginToken();
		if (loginToken == null)  // TODO: check expiry
		{
			loginToken = myHealthAccessService.getLoginToken(site, remoteUser, loggedInUser);
			loggedInUser.setMyHealthAccessLoginToken(loginToken.getToken());
		}

		myHealthAccessURL = myHealthAccessURL + "#token=" + loginToken.getToken();

		ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
		myHealthAccessRedirectAction.setPath(myHealthAccessURL);
		myHealthAccessRedirectAction.setRedirect(true);
		return myHealthAccessRedirectAction;
	}

	public ActionForward login(ActionMapping mapping, ActionForm form,
	                           HttpServletRequest request, HttpServletResponse response)
			throws NoSuchAlgorithmException, IOException, KeyManagementException
	{

		// TODO: This sets the long token

		setLoggedInData(request);
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Site site = getSite(request);
		try
		{
			 myHealthAccessService.getAuthToken(site, remoteUser, loggedInUser, email, password);
		}
		catch (BaseException e)
		{
			if (e.getErrorObject().hasAuthError())
			{
				ActionRedirect loginAction = new ActionRedirect(mapping.findForward("pushLogin"));
				loginAction.addParameter("demographicNo", request.getParameter("demographicNo"));
				loginAction.addParameter("siteName", request.getParameter("siteName"));
				loginAction.addParameter("email", email);
				loginAction.addParameter("errorMessage", "Failed to authenticate");
				return loginAction;
			}
			throw e;
		}

		Demographic patient = getDemographic(request);

		String myHealthAccessURL = myHealthAccessService.buildTeleHealthRedirectURL(
				remoteUser,
				patient,
				site);

		return pushToMyHealthAccess(myHealthAccessURL, getSite(request));
	}

	public ActionForward confirmUser(ActionMapping mapping, ActionForm form,
	                                   HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException
	{
		Site site = getSite(request);
		Demographic patient = getDemographic(request);

		String myHealthAccessURL = myHealthAccessService.buildTeleHealthRedirectURL(
				remoteUser,
				patient,
				site);

		return pushToMyHealthAccess(myHealthAccessURL, getSite(request));
	}

	private Demographic getDemographic(HttpServletRequest request)
	{
		String demographicNo = request.getParameter("demographicNo");

		if(demographicNo == null || demographicNo.isEmpty())
		{
			return null;
		}

		return demographicDao.find(Integer.parseInt(demographicNo));
	}

	private Site getSite(HttpServletRequest request)
	{
		String siteName = request.getParameter("siteName");
		if (siteName == null || siteName.isEmpty())
		{
			return null;
		}

		SiteDao siteDao = SpringUtils.getBean(SiteDao.class);
		return siteDao.findByName(siteName);
	}

	private void setLoggedInData(HttpServletRequest request)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		loggedInProvider = loggedInInfo.getLoggedInProvider().getProvider();
		loggedInUser = loggedInInfo.getLoggedInSecurity();
	}

	private void fetchRemoteUser(HttpServletRequest request)
	{
		try
		{
			remoteUser = myHealthAccessService.getLinkedUser(loggedInUser, getSite(request));
		}

		catch (RecordNotFoundException e)
		{
			String email = request.getParameter("email");

			if (email != null && !email.isEmpty())
			{
				remoteUser = myHealthAccessService.getUserByEmail(email, getSite(request));
			}
			else
			{
				throw e;
			}
		}

		MiscUtils.getLogger().error("Linked user: " + remoteUser.getMyhealthaccesID());
	}
}
