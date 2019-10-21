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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.Site;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserTo1;
import org.oscarehr.integration.myhealthaccess.exception.InvalidAccessException;
import org.oscarehr.integration.myhealthaccess.exception.DuplicateRecordException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.model.MHAUserToken;
import org.oscarehr.integration.service.IntegrationService;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.telehealth.service.MyHealthAccessService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyHealthAccess extends DispatchAction
{
	private static MyHealthAccessService myHealthAccessService = SpringUtils.getBean(MyHealthAccessService.class);
	private static IntegrationService integrationService = SpringUtils.getBean(IntegrationService.class);
	private static final Logger logger = MiscUtils.getLogger();

	public ActionForward openTelehealth(ActionMapping mapping, ActionForm form,
	                                     HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			IntegrationData integrationData = getIntegrationData(request);

			if (!integrationData.userIntegrationExists())
			{
				throw new RecordNotFoundException();
			}

			MHAUserToken longToken = MHAUserToken.decodeToken(integrationData.getUserAccessToken());

			if (longToken == null || longToken.isExpired())
			{
				return createActionRedirect(request, mapping, Action.LOGIN, Param.SITE_NAME, Param.APPOINTMENT);
			}

			if (longToken.shouldRenew())
			{
				myHealthAccessService.renewLongToken(integrationData);
			}

			return getRemoteRedirect(integrationData, request);
		}
		catch (InvalidIntegrationException e)
		{
			return redirectLogin(request, mapping, e.getMessage());
		}
		catch (RecordNotFoundException e)
		{
			return createActionRedirect(request, mapping, Action.LOGIN, Param.SITE_NAME, Param.APPOINTMENT);
		}
	}

	public ActionForward login(ActionMapping mapping, ActionForm form,
							   HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		String email = request.getParameter(Param.EMAIL);
		String password = request.getParameter(Param.PASSWORD);

		try
		{
			IntegrationData integrationData = getIntegrationData(request);

			if (!integrationData.userIntegrationExists())
			{
				Site site = integrationData.getIntegration().getSite();

				String siteEmail = userSiteEmail(email, site);
				String junoUserId = Integer.toString(loggedInUser.getId());

				ClinicUserLoginTo1 userLogin = new ClinicUserLoginTo1(siteEmail, password, junoUserId);
				integrationData = myHealthAccessService.createUserIntegration(integrationData, loggedInUser, userLogin);
			}

			return getRemoteRedirect(integrationData, request);
		}
		catch (InvalidIntegrationException e)
		{
			logger.info("MyHealthAccess attempt with no MyHealthAccess integration");
			return redirectLogin(request, mapping, e.getMessage());
		}
		catch (RecordNotFoundException e)
		{
			ActionRedirect redirect = createActionRedirect(request, mapping, Action.CREATE_USER, Param.EMAIL, Param.SITE_NAME);
			redirect.addParameter(Param.EMAIL, e.getMessage());
			return redirect;
		}
		catch (InvalidAccessException e)
		{
			logger.info("Invalid credentials for MHA user: " + request.getParameter(Param.EMAIL));
			return redirectLogin(request, mapping, e.getMessage());
		}
		catch (Exception e)
		{
			return redirectLogin(request, mapping, e.getMessage());
		}
	}

	public ActionForward createUser(ActionMapping mapping, ActionForm form,
	                                HttpServletRequest request, HttpServletResponse response)
	{
		String email = request.getParameter(Param.EMAIL);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProviderData loggedInProvider = loggedInInfo.getLoggedInProvider().getProvider();
		Security loggedInUser = loggedInInfo.getLoggedInSecurity();

		try
		{
			IntegrationData integrationData = getIntegrationData(request);
			Site site = integrationData.getIntegration().getSite();

			String siteEmail = userSiteEmail(email, site);

			ClinicUserTo1 userInfo = new ClinicUserTo1(Integer.toString(loggedInUser.getSecurityNo()), siteEmail,
														loggedInProvider.getFirstName(), loggedInProvider.getLastName());

			UserIntegrationAccess integrationAccess = myHealthAccessService.createRemoteUser(integrationData, loggedInUser, userInfo);

			ActionRedirect confirmRedirect = createActionRedirect(request, mapping, Action.CONFIRM_USER, Param.SITE_NAME, Param.APPOINTMENT);
			confirmRedirect.addParameter(Param.EMAIL, email);
			confirmRedirect.addParameter(Param.REMOTE_USER, integrationAccess.getRemoteUserId());

			return confirmRedirect;
		}
		catch (InvalidIntegrationException e)
		{
			return redirectLogin(request, mapping, e.getMessage());
		}
		catch (DuplicateRecordException e)
		{
			ActionRedirect loginAction = createActionRedirect(request, mapping, Action.LOGIN, Param.SITE_NAME, Param.APPOINTMENT);
			loginAction.addParameter(Param.EMAIL, email);

			return loginAction;
		}
	}

	public ActionForward confirmUser(ActionMapping mapping, ActionForm form,
	                                 HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			IntegrationData integrationData = getIntegrationData(request);
			return getRemoteRedirect(integrationData, request);
		}
		catch (InvalidIntegrationException e)
		{
			return redirectLogin(request, mapping, e.getMessage());
		}
	}

	/*
	 * Helper Methods
	 */
	private IntegrationData getIntegrationData(HttpServletRequest request) throws InvalidIntegrationException
	{
		String siteName = request.getParameter(Param.SITE_NAME);

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		Security security = loggedInInfo.getLoggedInSecurity();

		Integration integration = integrationService.findMhaIntegration(siteName);

		if (integration == null)
		{
			String noIntegrationError = InvalidIntegrationException.NO_INTEGRATION_MHA;

			if (!StringUtils.isNullOrEmpty(siteName))
			{
				noIntegrationError = String.format("%s for %s", noIntegrationError, siteName);
			}
			throw new InvalidIntegrationException(noIntegrationError);
		}

		IntegrationData integrationData = new IntegrationData(integration);

		UserIntegrationAccess userIntegrationAccess = integrationService.findMhaUserAccessBySecurityAndSiteName(security, siteName);
		integrationData.setUserIntegrationAccess(userIntegrationAccess);

		return integrationData;
	}

	private ActionRedirect createActionRedirect(HttpServletRequest request, ActionMapping mapping, String action, String ...params)
	{
		ActionRedirect actionRedirect = new ActionRedirect(mapping.findForward(action));

		for (String param : params)
		{
			actionRedirect.addParameter(param, request.getParameter(param));
		}

		return actionRedirect;
	}

	private ActionRedirect redirectLogin(HttpServletRequest request, ActionMapping mapping, String errorMessage)
	{
		ActionRedirect loginAction = createActionRedirect(request, mapping, Action.LOGIN,
				Param.SITE_NAME, Param.EMAIL, Param.APPOINTMENT);
		loginAction.addParameter(Param.ERROR, errorMessage);

		return loginAction;
	}

	private ActionForward getRemoteRedirect(IntegrationData integrationData, HttpServletRequest request)
	{
		String appointmentNo = request.getParameter(Param.APPOINTMENT);
		String myHealthAccessURL = myHealthAccessService.getTelehealthURL(integrationData, appointmentNo);

		ActionRedirect myHealthAccessRedirectAction = new ActionRedirect();
		myHealthAccessRedirectAction.setPath(myHealthAccessURL);
		myHealthAccessRedirectAction.setRedirect(true);
		return myHealthAccessRedirectAction;
	}

	private String userSiteEmail(String email, Site site)
	{
		if (site == null)
		{
			return email;
		}

		String siteIdentifier = StringUtils.stripSpaces(site.getShortName());

		int separationIndex = email.lastIndexOf("@");
		String emailStart = email.substring(0, separationIndex);
		String emailEnd = email.substring(separationIndex);

		email = String.format("%s+%s%s", emailStart, siteIdentifier, emailEnd).toLowerCase();

		return email;
	}

	private final static class Param
	{
		public static final String EMAIL = "email";
		public static final String PASSWORD = "password";
		public static final String SITE_NAME = "siteName";
		public static final String REMOTE_USER = "remoteUser";
		public static final String APPOINTMENT = "appt";
		public static final String ERROR = "errorMessage";
	}

	private final static class Action
	{
		public static final String LOGIN = "mhaLogin";
		public static final String CREATE_USER = "createUser";
		public static final String CONFIRM_USER = "confirmUser";
	}
}