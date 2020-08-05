/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.login;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.web.OcanForm;
import org.oscarehr.common.dao.FacilityDao;
import org.oscarehr.common.dao.ServiceRequestTokenDao;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ServiceRequestToken;
import org.oscarehr.login.dto.LoginForwardURL;
import org.oscarehr.login.service.LoginService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarSecurity.CRHelper;

public final class LoginAction extends DispatchAction
{

	/**
	 * This variable is only intended to be used by this class and the jsp which sets the selected facility.
	 * This variable represents the queryString key used to pass the facility ID to this class.
	 */
	public static final String SELECTED_FACILITY_ID = "selectedFacilityId";

	private static final Logger logger = MiscUtils.getLogger();
	private static final String LOG_PRE = "Login!@#$: ";

	private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean("facilityDao");
	private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
	private LoginService loginService = SpringUtils.getBean(LoginService.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		boolean ajaxResponse = request.getParameter("ajaxResponse") != null ? Boolean.valueOf(request.getParameter("ajaxResponse")) : false;

		String ip = request.getRemoteAddr();

		LoginCheckLogin cl = new LoginCheckLogin();

		String userName = "";
		String password = "";
		String pin = "";
		String nextPage = "";
		boolean forcedPasswordChange = true;
		String where = "failure";

		if (request.getParameter("forcedpasswordchange") != null && request.getParameter("forcedpasswordchange").equalsIgnoreCase("true"))
		{
			//Coming back from force password change.
			userName = (String) request.getSession().getAttribute("userName");
			password = (String) request.getSession().getAttribute("password");
			pin = (String) request.getSession().getAttribute("pin");
			nextPage = (String) request.getSession().getAttribute("nextPage");

			String newPassword = ((LoginForm) form).getNewPassword();
			String confirmPassword = ((LoginForm) form).getConfirmPassword();
			String oldPassword = ((LoginForm) form).getOldPassword();


			try
			{
				String errorStr = errorHandling(password, newPassword, confirmPassword, loginService.encodePassword(oldPassword), oldPassword);

				//Error Handling
				if (errorStr != null && !errorStr.isEmpty())
				{
					String newURL = mapping.findForward("forcepasswordreset").getPath();
					newURL = newURL + errorStr;
					return (new ActionForward(newURL));
				}

				loginService.persistNewPassword(userName, newPassword);

				password = newPassword;

				//Remove the attributes from session
				removeAttributesFromSession(request);
			}
			catch (Exception e)
			{
				logger.error("Error", e);
				String newURL = mapping.findForward("error").getPath();
				newURL = newURL + "?errormsg=Setting values to the session.";

				//Remove the attributes from session
				removeAttributesFromSession(request);

				return (new ActionForward(newURL));
			}

			//make sure this checking doesn't happen again
			forcedPasswordChange = false;

		}
		else
		{
			userName = ((LoginForm) form).getUsername();
			password = ((LoginForm) form).getPassword();
			pin = ((LoginForm) form).getPin();
			nextPage = request.getParameter("nextPage");

			String username = (String) request.getSession().getAttribute("user");
			logger.debug("nextPage: " + nextPage);
			if (nextPage != null)
			{
				// set current facility
				String facilityIdString = request.getParameter(SELECTED_FACILITY_ID);
				Facility facility = facilityDao.find(Integer.parseInt(facilityIdString));
				request.getSession().setAttribute(SessionConstants.CURRENT_FACILITY, facility);
				LogAction.addLogEntry(username, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId=" + facilityIdString, ip);
				if (facility.isEnableOcanForms())
				{
					request.getSession().setAttribute("ocanWarningWindow", OcanForm.getOcanWarningMessage(facility.getId()));
				}
				return mapping.findForward(nextPage);
			}

			if (cl.isBlocked(ip, userName))
			{
				logger.info(LOG_PRE + " Blocked: " + userName);
				LogAction.addLogEntry(username, null, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_FAILURE,
						null, ip, "Blocked " + userName + " for repeated login attempts");
				// change to block page
				String newURL = mapping.findForward("error").getPath();
				newURL = newURL + "?errormsg=Your account is locked. Please contact your administrator to unlock.";

				if (ajaxResponse)
				{
					JSONObject json = new JSONObject();
					json.put("success", false);
					json.put("error", "Your account is locked. Please contact your administrator to unlock.");
					response.setContentType("text/x-json");
					json.write(response.getWriter());
					return null;
				}

				return (new ActionForward(newURL));
			}

			logger.debug("ip was not blocked: " + ip);

		}

		String[] strAuth;
		try
		{
			strAuth = cl.auth(userName, password, pin, ip);
		}
		catch (Exception e)
		{
			logger.error("Error", e);
			String newURL = mapping.findForward("error").getPath();
			if (e.getMessage() != null && e.getMessage().startsWith("java.lang.ClassNotFoundException"))
			{
				newURL = newURL + "?errormsg=Database driver " + e.getMessage().substring(e.getMessage().indexOf(':') + 2) + " not found.";
			}
			else
			{
				newURL = newURL + "?errormsg=Database connection error: " + e.getMessage() + ".";
			}

			if (ajaxResponse)
			{
				JSONObject json = new JSONObject();
				json.put("success", false);
				json.put("error", "Database connection error:" + e.getMessage() + ".");
				response.setContentType("text/x-json");
				json.write(response.getWriter());
				return null;
			}

			return (new ActionForward(newURL));
		}
		logger.debug("strAuth : " + Arrays.toString(strAuth));
		if (strAuth != null && strAuth.length != 1)
		{ // login successfully

			LoginForwardURL loginForwardURL = loginService.loginSuccess(mapping,
					request,
					userName,
					strAuth[0],
					strAuth[1],
					strAuth[2],
					strAuth[3],
					strAuth[4],
					strAuth[5],
					password,
					pin,
					nextPage,
					forcedPasswordChange);
			where = loginForwardURL.getUrl();
			if (loginForwardURL.getForwarding())
			{
				return mapping.findForward(where);
			}
		}
		// expired password
		else if (strAuth != null && strAuth.length == 1 && strAuth[0].equals("expired"))
		{
			logger.warn("Expired password");
			cl.updateLoginList(ip, userName);
			String newURL = mapping.findForward("error").getPath();
			newURL = newURL + "?errormsg=Your account is expired. Please contact your administrator.";

			if (ajaxResponse)
			{
				JSONObject json = new JSONObject();
				json.put("success", false);
				json.put("error", "Your account is expired. Please contact your administrator.");
				response.setContentType("text/x-json");
				json.write(response.getWriter());
				return null;
			}

			return (new ActionForward(newURL));
		}
		else
		{
			logger.debug("go to normal directory");

			// go to normal directory
			// request.setAttribute("login", "failed");
			cl.updateLoginList(ip, userName);
			CRHelper.recordLoginFailure(userName, request);

			if (ajaxResponse)
			{
				JSONObject json = new JSONObject();
				json.put("success", false);
				response.setContentType("text/x-json");
				json.put("error", "Invalid Credentials");
				json.write(response.getWriter());
				return null;
			}

			return mapping.findForward(where);
		}

		logger.debug("checking oauth_token");
		if (request.getParameter("oauth_token") != null)
		{
			String proNo = (String) request.getSession().getAttribute("user");
			ServiceRequestTokenDao serviceRequestTokenDao = SpringUtils.getBean(ServiceRequestTokenDao.class);
			ServiceRequestToken srt = serviceRequestTokenDao.findByTokenId(request.getParameter("oauth_token"));
			if (srt != null)
			{
				srt.setProviderNo(proNo);
				serviceRequestTokenDao.merge(srt);
			}
		}

		if (ajaxResponse)
		{
			logger.debug("rendering ajax response");
			Provider prov = providerDao.getProvider((String) request.getSession().getAttribute("user"));
			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("providerName", prov.getFormattedName());
			json.put("providerNo", prov.getProviderNo());
			response.setContentType("text/x-json");
			json.write(response.getWriter());
			return null;
		}

		logger.debug("rendering standard response : " + where);
		return mapping.findForward(where);
	}


	/**
	 * Removes attributes from session
	 *
	 * @param request
	 */
	private void removeAttributesFromSession(HttpServletRequest request)
	{
		request.getSession().removeAttribute("userName");
		request.getSession().removeAttribute("password");
		request.getSession().removeAttribute("pin");
		request.getSession().removeAttribute("nextPage");
	}

	/**
	 * Performs the error handling
	 *
	 * @param password
	 * @param newPassword
	 * @param confirmPassword
	 * @param oldPassword
	 * @return
	 */
	private String errorHandling(String password, String newPassword, String confirmPassword, String encodedOldPassword, String oldPassword)
	{

		String newURL = "";

		if (!encodedOldPassword.equals(password))
		{
			newURL = newURL + "?errormsg=Your old password, does NOT match the password in the system. Please enter your old password.";
		}
		else if (!newPassword.equals(confirmPassword))
		{
			newURL = newURL + "?errormsg=Your new password, does NOT match the confirmed password. Please try again.";
		}
		else if (!Boolean.parseBoolean(OscarProperties.getInstance().getProperty("IGNORE_PASSWORD_REQUIREMENTS")) && newPassword.equals(oldPassword))
		{
			newURL = newURL + "?errormsg=Your new password, is the same as your old password. Please choose a new password.";
		}

		return newURL;
	}
}
