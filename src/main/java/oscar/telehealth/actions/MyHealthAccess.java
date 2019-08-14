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
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserCreateTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.DuplicateRecordException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.model.MHAUserToken;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class MyHealthAccess extends DispatchAction
{
	private static MyHealthAccessService myHealthAccessService = SpringUtils.getBean(MyHealthAccessService.class);
	private static SecurityDao securityDao = SpringUtils.getBean(SecurityDao.class);

	public ActionForward startTelehealth(ActionMapping mapping, ActionForm form,
	                                     HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			ClinicUserTo1 remoteUser = fetchRemoteUser(request);
			String remoteUserID = remoteUser.getMyhealthaccesID();

			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
			Security loggedInUser = loggedInInfo.getLoggedInSecurity();

			MHAUserToken longToken = MHAUserToken.decodeToken(loggedInUser.getMyHealthAccessLongToken());

			if (longToken == null || longToken.isExpired())
			{
				ActionRedirect loginAction = new ActionRedirect(mapping.findForward("mhaLogin"));
				loginAction.addParameter("siteName", request.getParameter("siteName"));
				loginAction.addParameter("appt", request.getParameter("appt"));
				loginAction.addParameter("remoteUser", remoteUserID);
				return loginAction;
			}

			Site site = getSite(request);
			String appointmentNo = request.getParameter("appt");
			String mhaRemoteID = remoteUser.getMyhealthaccesID();

			if (longToken.expiresWithinDays(7))
			{
				MHAUserToken renewedToken = myHealthAccessService.renewLongToken(site, remoteUserID, loggedInUser);
				persistToken(request.getSession(), loggedInUser, renewedToken.getToken());
			}

			String endpoint = myHealthAccessService.buildTeleHealthRedirectURL(mhaRemoteID, site, appointmentNo);

			return pushToMyHealthAccess(endpoint, site, mhaRemoteID, loggedInUser);
		}
		catch (RecordNotFoundException e)
		{
			ActionRedirect createUserAction = new ActionRedirect(mapping.findForward("createUser"));
			createUserAction.addParameter("siteName", request.getParameter("siteName"));
			createUserAction.addParameter("appt", request.getParameter("appt"));

			return createUserAction;
		}
	}

	public ActionForward createUser(ActionMapping mapping, ActionForm form,
	                                HttpServletRequest request, HttpServletResponse response)
	{
		String email = request.getParameter("email");

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProviderData loggedInProvider = loggedInInfo.getLoggedInProvider().getProvider();
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		try
		{
			ClinicUserCreateTo1 remoteUser = myHealthAccessService.createUser(loggedInUser, loggedInProvider, email, getSite(request));
			persistToken(request.getSession(), loggedInUser, remoteUser.getAccessToken());

			ActionRedirect confirmUserAction = new ActionRedirect(mapping.findForward("confirmUser"));
			confirmUserAction.addParameter("siteName", request.getParameter("siteName"));
			confirmUserAction.addParameter("appt", request.getParameter("appt"));
			confirmUserAction.addParameter("email", email);
			confirmUserAction.addParameter("remoteUser", remoteUser.getMyhealthaccesID());

			return confirmUserAction;
		}
		catch (DuplicateRecordException e)
		{
			ClinicUserTo1 remoteUser = myHealthAccessService.getUserByEmail(email, getSite(request));

			ActionRedirect loginAction = new ActionRedirect(mapping.findForward("mhaLogin"));
			loginAction.addParameter("siteName", request.getParameter("siteName"));
			loginAction.addParameter("appt", request.getParameter("appt"));
			loginAction.addParameter("email", email);
			loginAction.addParameter("remoteUser", remoteUser.getMyhealthaccesID());

			return loginAction;
		}
	}

	public ActionForward confirmUser(ActionMapping mapping, ActionForm form,
	                                 HttpServletRequest request, HttpServletResponse response)
	{
		Site site = getSite(request);
		String appointmentNo = request.getParameter("appt");
		String remoteUser = request.getParameter("remoteUser");

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		String endpoint = myHealthAccessService.buildTeleHealthRedirectURL(remoteUser, site, appointmentNo);

		return pushToMyHealthAccess(endpoint, getSite(request), remoteUser, loggedInUser);
	}

	public ActionForward login(ActionMapping mapping, ActionForm form,
	                           HttpServletRequest request, HttpServletResponse response)
			throws NoSuchAlgorithmException, IOException, KeyManagementException
	{
		String email = request.getParameter("email");
		String password = request.getParameter("password");

		Site site = getSite(request);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();
		String remoteUser = request.getParameter("remoteUser");

		try
		{
			MHAUserToken longToken = myHealthAccessService.getLongToken(site, remoteUser, loggedInUser, email, password);
			persistToken(request.getSession(), loggedInUser, longToken.getToken());
		}
		catch (BaseException e)
		{
			if (e.getErrorObject().hasAuthError())
			{
				ActionRedirect loginAction = new ActionRedirect(mapping.findForward("mhaLogin"));
				loginAction.addParameter("siteName", request.getParameter("siteName"));
				loginAction.addParameter("email", email);
				loginAction.addParameter("appt", request.getParameter("appt"));
				loginAction.addParameter("errorMessage", "Failed to authenticate");
				return loginAction;
			}

			throw e;
		}

		String appointmentNo = request.getParameter("appt");
		String endpoint = myHealthAccessService.buildTeleHealthRedirectURL(remoteUser, site, appointmentNo);

		return pushToMyHealthAccess(endpoint, getSite(request), remoteUser, loggedInUser);
	}

	private ActionForward pushToMyHealthAccess(String myHealthAccessURL, Site site, String remoteUser, Security loggedInUser)
	{
		MHAUserToken shortToken = myHealthAccessService.getShortToken(site, remoteUser, loggedInUser);

		myHealthAccessURL = myHealthAccessURL + "#token=" + URLEncoder.encode(shortToken.getToken());

		ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
		myHealthAccessRedirectAction.setPath(myHealthAccessURL);
		myHealthAccessRedirectAction.setRedirect(true);
		return myHealthAccessRedirectAction;
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

	private ClinicUserTo1 fetchRemoteUser(HttpServletRequest request)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		try
		{
			return myHealthAccessService.getLinkedUser(loggedInUser, getSite(request));
		}

		catch (RecordNotFoundException e)
		{
			String email = request.getParameter("email");

			if (email != null && !email.isEmpty())
			{
				return myHealthAccessService.getUserByEmail(email, getSite(request));
			}
			else
			{
				throw e;
			}
		}
	}

	private void persistToken(HttpSession session, Security loggedInUser, String token)
	{
		Security securityRecord = securityDao.find(loggedInUser.getId());
		securityRecord.setMyHealthAccessLongToken(token);
		securityDao.merge(securityRecord);

		LoggedInInfo currentUser = LoggedInInfo.getLoggedInInfoFromSession(session);
		currentUser.getLoggedInSecurity().setMyHealthAccessLongToken(token);
		LoggedInInfo.setLoggedInInfoIntoSession(session, currentUser);
	}
}
