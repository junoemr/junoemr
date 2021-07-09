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

package org.oscarehr.login.service;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.web.OcanForm;
import org.oscarehr.common.dao.FacilityDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.login.dto.LoginForwardURL;
import org.oscarehr.phr.util.MyOscarUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.LoggedInUserFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarSecurity.CRHelper;
import oscar.util.CBIUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoginService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String LOG_PRE = "Login!@#$: ";

	@Autowired
	FacilityDao facilityDao;

	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProviderPreferenceDao providerPreferenceDao;

	@Autowired
	SecurityDao securityDao;

	@Autowired
	UserPropertyDAO userPropertyDAO;

	/**
	 * This method is called upon a successful login with a mapping, request, login information (hash of information
	 * needed to determine the user to login) and whether there is a forced password change
	 *
	 * @param mapping
	 * @param request
	 * @return where
	 */
	public LoginForwardURL loginSuccess(ActionMapping mapping,
	                                    HttpServletRequest request,
	                                    String userName,
	                                    String providerNo,
	                                    String userFirstName,
	                                    String userLastName,
	                                    String profession,
	                                    String userRole,
	                                    String expiredDays,
	                                    String password,
	                                    String pin,
	                                    String nextPage,
	                                    Boolean forcedPasswordChange
	)
	{
		logger.info("Successfully logged in " + userName);

		//is the provider record inactive?
		ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
		Provider provider = providerDao.getProvider(providerNo);
		if (provider == null || (provider.getStatus() != null && provider.getStatus().equals("0")))
		{
			logger.info(LOG_PRE + " Inactive: " + userName);
			LogAction.addLogEntry(providerNo, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_FAILURE, "inactive");

			String newURL = mapping.findForward("error").getPath();
			newURL = newURL + "?errormsg=Your account is inactive. Please contact your administrator to activate.";
			return new LoginForwardURL(newURL, true, false);
		}

		/*
		 * This section is added for forcing the initial password change.
		 */
		Security security = getSecurity(userName);
		if (!OscarProperties.getInstance().getBooleanProperty("mandatory_password_reset", "false") &&
				security.isForcePasswordReset() != null && security.isForcePasswordReset() && forcedPasswordChange)
		{

			String newURL = mapping.findForward("forcepasswordreset").getPath();

			try
			{
				setUserInfoToSession(request, userName, password, pin, nextPage);
			}
			catch (Exception e)
			{
				logger.error("Error", e);
				newURL = mapping.findForward("error").getPath();
				newURL = newURL + "?errormsg=Setting values to the session.";
			}
			return new LoginForwardURL(newURL, true, false);
		}

		// invalidate the existing session
		HttpSession session = request.getSession(false);
		if (session != null)
		{
			if (request.getParameter("invalidate_session") != null && request.getParameter("invalidate_session").equals("false"))
			{
				//don't invalidate in this case..messes up authenticity of OAUTH
			}
			else
			{
				session.invalidate();
			}
		}
		session = request.getSession(); // Create a new session for this user

		logger.debug("Assigned new session for: " + providerNo + " : " + profession + " : " + userRole);
		String ip = request.getRemoteAddr();
		LogAction.addLogEntry(providerNo, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, null, ip);

		// initial db setting
		OscarProperties pvar = OscarProperties.getInstance();
		MyOscarUtils.setDeterministicallyMangledPasswordSecretKeyIntoSession(session, password);

		session.setAttribute("user", providerNo);
		session.setAttribute("userfirstname", userFirstName);
		session.setAttribute("userlastname", userLastName);
		session.setAttribute("userrole", userRole);
		session.setAttribute("oscar_context_path", request.getContextPath());
		session.setAttribute("expired_days", expiredDays);

		// If a new session has been created, we must set the mobile attribute again
		Boolean isMobileOptimized = request.getSession().getAttribute("mobileOptimized") != null;
		if (isMobileOptimized)
		{
			session.setAttribute("mobileOptimized", "true");
		}

		// get preferences from preference table
		ProviderPreference providerPreference = providerPreferenceDao.find(providerNo);


		if (providerPreference == null)
		{
			providerPreference = new ProviderPreference();
		}

		session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE, providerPreference);

		if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable())
		{
			String tklerProviderNo = null;
			UserProperty prop = userPropertyDAO.getProp(providerNo, UserProperty.PROVIDER_FOR_TICKLER_WARNING);
			if (prop == null)
			{
				tklerProviderNo = providerNo;
			}
			else
			{
				tklerProviderNo = prop.getValue();
			}
			session.setAttribute("tklerProviderNo", tklerProviderNo);

			session.setAttribute("newticklerwarningwindow", providerPreference.getNewTicklerWarningWindow());
			session.setAttribute("default_pmm", providerPreference.getDefaultCaisiPmm());
			session.setAttribute("caisiBillingPreferenceNotDelete", String.valueOf(providerPreference.getDefaultDoNotDeleteBilling()));

			@SuppressWarnings("unchecked")
			ArrayList<String> newDocArr = (ArrayList<String>) request.getSession().getServletContext().getAttribute("CaseMgmtUsers");
			if ("enabled".equals(providerPreference.getDefaultNewOscarCme()))
			{
				newDocArr.add(providerNo);
				session.setAttribute("CaseMgmtUsers", newDocArr);
			}
		}
		session.setAttribute("starthour", providerPreference.getStartHour().toString());
		session.setAttribute("endhour", providerPreference.getEndHour().toString());
		session.setAttribute("everymin", providerPreference.getEveryMin().toString());
		session.setAttribute("groupno", providerPreference.getMyGroupNo());

		String where = "provider";
		if (OscarProperties.getInstance().getProperty("useProgramLocation", "false").equals("true"))
		{
			where = "programLocation";
		}

		CRHelper.recordLoginSuccess(userName, providerNo, request);
		session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER, provider);
		session.setAttribute(SessionConstants.LOGGED_IN_SECURITY, security);

		LoggedInInfo loggedInInfo = LoggedInUserFilter.generateLoggedInInfoFromSession(request);

		MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedIn(loggedInInfo, true);

		List<Integer> facilityIds = providerDao.getFacilityIds(provider.getProviderNo());
		if (facilityIds.size() > 1)
		{
			return new LoginForwardURL("/select_facility.jsp?nextPage=" + where, true, false);
		}
		else if (facilityIds.size() == 1)
		{
			// set current facility
			Facility facility = facilityDao.find(facilityIds.get(0));
			request.getSession().setAttribute("currentFacility", facility);
			LogAction.addLogEntry(providerNo, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId=" + facilityIds.get(0), ip);
			if (facility.isEnableOcanForms())
			{
				request.getSession().setAttribute("ocanWarningWindow", OcanForm.getOcanWarningMessage(facility.getId()));
			}
			if (facility.isEnableCbiForm())
			{
				request.getSession().setAttribute("cbiReminderWindow", CBIUtil.getCbiSubmissionFailureWarningMessage(facility.getId(), provider.getProviderNo()));
			}
		}
		else
		{
			List<Facility> facilities = facilityDao.findAll(true);
			if (facilities != null && facilities.size() >= 1)
			{
				Facility fac = facilities.get(0);
				int first_id = fac.getId();
				ProviderDao.addProviderToFacility(providerNo, first_id);
				Facility facility = facilityDao.find(first_id);
				request.getSession().setAttribute("currentFacility", facility);
				LogAction.addLogEntry(providerNo, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId=" + first_id, ip);
			}
		}

		//are they using the new UI?
		UserProperty prop = userPropertyDAO.getProp(provider.getProviderNo(), UserProperty.COBALT);
		if (prop != null && prop.getValue() != null && prop.getValue().equals(UserProperty.PROPERTY_ON_YES))
		{
			where = "cobalt";
		}
		return new LoginForwardURL(where, false, true);
	}

	/**
	 * This method encodes the password, before setting to session.
	 *
	 * @param password
	 * @return encoded password
	 * @throws Exception
	 */
	public String encodePassword(String password) throws Exception
	{

		MessageDigest md = MessageDigest.getInstance("SHA");

		StringBuilder sbTemp = new StringBuilder();
		byte[] btNewPasswd = md.digest(password.getBytes());
		for (int i = 0; i < btNewPasswd.length; i++) sbTemp = sbTemp.append(btNewPasswd[i]);

		return sbTemp.toString();

	}

	/**
	 * Set user info to session
	 *
	 * @param request
	 * @param userName
	 * @param password
	 * @param pin
	 * @param nextPage
	 */
	private void setUserInfoToSession(HttpServletRequest request, String userName, String password, String pin, String nextPage) throws Exception
	{
		request.getSession().setAttribute("userName", userName);
		request.getSession().setAttribute("password", encodePassword(password));
		request.getSession().setAttribute("pin", pin);
		request.getSession().setAttribute("nextPage", nextPage);
	}

	/**
	 * get the security record based on the username
	 * @param username username to get security record for
	 * @return null if no entry found, otherwise corresponding security entry
	 */
	private Security getSecurity(String username) {

		SecurityDao securityDao = (SecurityDao) SpringUtils.getBean("securityDao");
		Security security = securityDao.findByUserName(username);

		// attempt email lookup.
		if (security == null)
		{
			security = securityDao.findByEmail(username);
		}

		if (security == null) {
			return null;
		}

		return security;
	}

	/**
	 * Persists the new password
	 *
	 * @param userName
	 * @param newPassword
	 */
	public void persistNewPassword(String userName, String newPassword) throws Exception
	{
		Security security = getSecurity(userName);
		security.setPassword(encodePassword(newPassword));
		security.setForcePasswordReset(Boolean.FALSE);
		securityDao.saveEntity(security);

	}
}
