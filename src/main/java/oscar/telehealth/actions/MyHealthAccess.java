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
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
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
	private MyHealthAccessService myHealthAccessService =
			SpringUtils.getBean(MyHealthAccessService.class);
	private ClinicService clinicService = SpringUtils.getBean(ClinicService.class);
	private DemographicDao demographicDao =
			(DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");

	private ProviderData loggedInProvider;
	private Security loggedInUser;
	private ClinicUserTo1 remoteUser;

	public ActionForward startTelehealth(ActionMapping mapping,
										 ActionForm form,
										 HttpServletRequest request,
										 HttpServletResponse response) throws UnsupportedEncodingException
	{
		MiscUtils.getLogger().error("Start Telehealth!");
		try
		{
			MiscUtils.getLogger().error("Set logged in data");
			setLoggedInData(request);
		} catch (RecordNotFoundException e)
		{
			MiscUtils.getLogger().error("Couldn't find user. Creating...");
			ActionRedirect createUserAction =
					new ActionRedirect(mapping.findForward("createUser"));
			createUserAction.addParameter(
					"demographicNo", request.getParameter("demographicNo"));
			createUserAction.addParameter("siteName", request.getParameter("siteName"));
			return createUserAction;
		}

		MiscUtils.getLogger().error("Got logged in data");

		ClinicUserAccessTokenTo1 myHealthAccessAuthToken = loggedInUser.getMyHealthAccessAuthToken();
		if (myHealthAccessAuthToken == null || myHealthAccessAuthToken.isExpired())
		{
			MiscUtils.getLogger().error("token null or expired");
			ActionRedirect loginAction = new ActionRedirect(mapping.findForward("pushLogin"));
			loginAction.addParameter(
					"demographicNo", request.getParameter("demographicNo"));
			loginAction.addParameter("siteName", request.getParameter("siteName"));
			return loginAction;
		}

		return pushLogin(myHealthAccessAuthToken, mapping, form, request, response);
	}

	public ActionForward createUser(ActionMapping mapping,
									ActionForm form,
									HttpServletRequest request,
									HttpServletResponse response)
	{
		String email = request.getParameter("email");
		MiscUtils.getLogger().error("Create User with email: " + email);

		ClinicUserTo1 clinicUser = null;
		try
		{
			clinicUser = myHealthAccessService.getUserByEmail(email, getSite(request));
		} catch (RecordNotFoundException e)
		{
			// CREATE USER if not exists
			clinicUser = myHealthAccessService.createUser(
					loggedInUser, loggedInProvider, email, getSite(request));
			ActionRedirect createdUserAction =
					new ActionRedirect(mapping.findForward("createdUser"));
			createdUserAction.addParameter(
					"demographicNo", request.getParameter("demographicNo"));
			createdUserAction.addParameter("siteName", request.getParameter("siteName"));
			return createdUserAction;
		}

		ActionRedirect loginAction =
				new ActionRedirect(mapping.findForward("pushLogin"));
		loginAction.addParameter(
				"demographicNo", request.getParameter("demographicNo"));
		loginAction.addParameter("siteName", request.getParameter("siteName"));
		loginAction.addParameter("email", email);
		return loginAction;
	}

	public ActionForward login(ActionMapping mapping,
							   ActionForm form,
							   HttpServletRequest request,
							   HttpServletResponse response) throws
			NoSuchAlgorithmException,
			IOException,
			KeyManagementException
	{

		setLoggedInData(request);
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		ClinicUserAccessTokenTo1 myHealthAccessAuthToken;
		try
		{
			myHealthAccessAuthToken = myHealthAccessService.getLoginToken(
					loggedInUser,
					getSite(request),
					remoteUser.getMyhealthaccesID(),
					email,
					password);
		} catch (BaseException e)
		{
			if (e.getErrorObject().hasAuthError())
			{
				ActionRedirect loginAction =
						new ActionRedirect(mapping.findForward("pushLogin"));
				loginAction.addParameter(
						"demographicNo", request.getParameter("demographicNo"));
				loginAction.addParameter("siteName", request.getParameter("siteName"));
				loginAction.addParameter("email", email);
				loginAction.addParameter("errorMessage", "Failed to authenticate");
				return loginAction;
			}
			throw e;
		}
		return pushLogin(myHealthAccessAuthToken, mapping, form, request, response);
	}


	public ActionForward pushLogin(
			ClinicUserAccessTokenTo1 myHealthAccessAuthToken,
			ActionMapping mapping,
			ActionForm form,
			HttpServletRequest request,
			HttpServletResponse response) throws UnsupportedEncodingException
	{
		MiscUtils.getLogger().error("LOGGING IN!");
		Demographic patient = getDemographic(request);
//		if (patient == null)
//		{
//			ActionRedirect errorAction = new ActionRedirect(mapping.findForward("error"));
//			errorAction.addParameter(
//					"errorMessage",
//					"Failed to find patient record");
//			return errorAction;
//		}

		Site site = getSite(request);
		String myHealthAccessURL = myHealthAccessService.buildTeleHealthRedirectURL(
				myHealthAccessAuthToken,
				remoteUser,
				patient,
				site);

		ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
		myHealthAccessRedirectAction.setPath(myHealthAccessURL);
		myHealthAccessRedirectAction.setRedirect(true);
		return myHealthAccessRedirectAction;
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
		MiscUtils.getLogger().error("Logged in user: " + Integer.toString(loggedInUser.getId()));
		try
		{
			remoteUser = myHealthAccessService.getLinkedUser(loggedInUser, getSite(request));
		} catch (RecordNotFoundException e)
		{
			String email = request.getParameter("email");
			if (email != null && !email.isEmpty())
			{
				remoteUser = myHealthAccessService.getUserByEmail(email, getSite(request));
			} else
			{
				throw e;
			}
		}
		MiscUtils.getLogger().error("LInked user: " + remoteUser.getMyhealthaccesID());
	}
}
