package oscar.login;

import com.quatro.model.security.LdapSecurity;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.service.ProviderManager;
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
import org.oscarehr.phr.util.MyOscarUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.LoggedInUserFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarSecurity.CRHelper;
import oscar.util.CBIUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginUtil
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String LOG_PRE = "Login!@#$: ";


	private ProviderManager providerManager = (ProviderManager) SpringUtils.getBean("providerManager");
	private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean("facilityDao");
	private ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean("providerPreferenceDao");
	private UserPropertyDAO propDao = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");
	private Boolean isFowarding = false;

	/**
	 * This method is called upon a successful login with a mapping, request, login information (hash of information
	 * needed to determine the user to login) and whether there is a forced password change
	 *
	 * @param mapping
	 * @param request
	 * @param loginInfo
	 * @param forcedPasswordChange
	 * @return where
	 */
	public String loginSuccess(ActionMapping mapping, HttpServletRequest request, HashMap<String, String> loginInfo, Boolean forcedPasswordChange)
	{
		String providerNo = loginInfo.get("providerNo");
		String userName = loginInfo.get("userName");
		String userFirstName = loginInfo.get("userFirstName");
		String userLastName = loginInfo.get("userLastName");
		String profession = loginInfo.get("doctor");
		String userRole = loginInfo.get("userRole");
		String expiredDays = loginInfo.get("expiredDays");
		String password = loginInfo.get("expiredDays");
		String pin = loginInfo.get("pin");
		String nextPage = loginInfo.get("nextPage");
		logger.info("Successfully logged in " + userName);

		//is the provider record inactive?
		ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
		Provider p = providerDao.getProvider(providerNo);
		if (p == null || (p.getStatus() != null && p.getStatus().equals("0")))
		{
			logger.info(LOG_PRE + " Inactive: " + userName);
			LogAction.addLogEntry(providerNo, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_FAILURE, "inactive");

			String newURL = mapping.findForward("error").getPath();
			newURL = newURL + "?errormsg=Your account is inactive. Please contact your administrator to activate.";
			setIsForwarding(true);
			return newURL;
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
			setIsForwarding(true);
			return newURL;
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
		// initiate security manager
		String default_pmm = null;


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
			UserProperty prop = propDao.getProp(providerNo, UserProperty.PROVIDER_FOR_TICKLER_WARNING);
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

			default_pmm = providerPreference.getDefaultCaisiPmm();
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
		if (default_pmm != null && "enabled".equals(default_pmm))
		{
			where = "caisiPMM";
		}

		if (where.equals("provider") && OscarProperties.getInstance().getProperty("useProgramLocation", "false").equals("true"))
		{
			where = "programLocation";
		}

		String quatroShelter = OscarProperties.getInstance().getProperty("QUATRO_SHELTER");
		if (quatroShelter != null && quatroShelter.equals("on"))
		{
			where = "shelterSelection";
		}

		CRHelper.recordLoginSuccess(userName, providerNo, request);

		String username = (String) session.getAttribute("user");
		Provider provider = providerManager.getProvider(username);
		session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER, provider);
		session.setAttribute(SessionConstants.LOGGED_IN_SECURITY, security);

		LoggedInInfo loggedInInfo = LoggedInUserFilter.generateLoggedInInfoFromSession(request);

		MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedIn(loggedInInfo, true);

		List<Integer> facilityIds = providerDao.getFacilityIds(provider.getProviderNo());
		if (facilityIds.size() > 1)
		{
			setIsForwarding(true);
			return "/select_facility.jsp?nextPage=" + where;
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

		if (pvar.getProperty("LOGINTEST", "").equalsIgnoreCase("yes"))
		{
			String proceedURL = mapping.findForward(where).getPath();
			request.getSession().setAttribute("proceedURL", proceedURL);
			setIsForwarding(true);
			return "LoginTest";
		}

		//are they using the new UI?
		UserProperty prop = propDao.getProp(provider.getProviderNo(), UserProperty.COBALT);
		if (prop != null && prop.getValue() != null && prop.getValue().equals("yes"))
		{
			where = "cobalt";
		}
		return where;
	}

	/**
	 * This method gets whether the returned URL should be forwarded immediately
	 *
	 * @return isFowarding
	 */
	public Boolean getIsForwarding()
	{
		return isFowarding;
	}

	/**
	 * This method sets whether the returned URL should be forwarded immediately
	 *
	 * @param isFowarding
	 */
	private void setIsForwarding(Boolean isFowarding)
	{
		this.isFowarding = isFowarding;
	}

	/**
	 * This method encodes the password, before setting to session.
	 *
	 * @param password
	 * @return
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
	 *
	 * @param username username to get security record for
	 * @return null if no entry found, otherwise corresponding security entry
	 */
	private Security getSecurity(String username)
	{

		SecurityDao securityDao = (SecurityDao) SpringUtils.getBean("securityDao");
		Security security = securityDao.findByUserName(username);

		if (security == null)
		{
			return null;
		}
		else if (OscarProperties.isLdapAuthenticationEnabled())
		{
			security = new LdapSecurity(security);
		}

		return security;
	}

	/**
	 * Persists the new password
	 *
	 * @param userName
	 * @param newPassword
	 * @return
	 */
	public void persistNewPassword(String userName, String newPassword) throws Exception
	{
		Security security = getSecurity(userName);
		security.setPassword(encodePassword(newPassword));
		security.setForcePasswordReset(Boolean.FALSE);
		SecurityDao securityDao = (SecurityDao) SpringUtils.getBean("securityDao");
		securityDao.saveEntity(security);

	}
}
