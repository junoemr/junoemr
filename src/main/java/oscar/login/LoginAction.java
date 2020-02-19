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
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.service.ProviderManager;
import org.oscarehr.PMmodule.web.OcanForm;
import org.oscarehr.common.dao.FacilityDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.ServiceRequestTokenDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.ServiceRequestToken;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.AppManager;
import org.oscarehr.phr.util.MyOscarUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.LoggedInUserFilter;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.quatro.model.security.LdapSecurity;

import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarSecurity.CRHelper;
import oscar.util.CBIUtil;

public final class LoginAction extends DispatchAction {
	
	/**
	 * This variable is only intended to be used by this class and the jsp which sets the selected facility.
	 * This variable represents the queryString key used to pass the facility ID to this class.
	 */
    public static final String SELECTED_FACILITY_ID="selectedFacilityId";

    private static final Logger logger = MiscUtils.getLogger();
    private static final String LOG_PRE = "Login!@#$: ";
    
    
    private ProviderManager providerManager = (ProviderManager) SpringUtils.getBean("providerManager");
    private AppManager appManager = SpringUtils.getBean(AppManager.class);
    private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean("facilityDao");
    private ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean("providerPreferenceDao");
    private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
    private UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
	
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	boolean ajaxResponse = request.getParameter("ajaxResponse") != null?Boolean.valueOf(request.getParameter("ajaxResponse")):false;
    	
    	String ip = request.getRemoteAddr();
        Boolean isMobileOptimized = request.getSession().getAttribute("mobileOptimized") != null;
    	
        LoginCheckLogin cl = new LoginCheckLogin();
        
        String userName = "";
        String password = "";
        String pin = "";
        String nextPage= "";
        boolean forcedpasswordchange = true;
        String where = "failure";
        
    	if (request.getParameter("forcedpasswordchange") != null && request.getParameter("forcedpasswordchange").equalsIgnoreCase("true")) {
    		//Coming back from force password change.
    	    userName = (String) request.getSession().getAttribute("userName");
    	    password = (String) request.getSession().getAttribute("password");
    	    pin = (String) request.getSession().getAttribute("pin");
    	    nextPage = (String) request.getSession().getAttribute("nextPage");
    	    
    	    String newPassword = ((LoginForm) form).getNewPassword();
    	    String confirmPassword = ((LoginForm) form).getConfirmPassword();
    	    String oldPassword = ((LoginForm) form).getOldPassword();
    	   
    	    
    	    try{
        	    String errorStr = errorHandling(password, newPassword, confirmPassword, encodePassword(oldPassword), oldPassword);
        	    
        	    //Error Handling
        	    if (errorStr != null && !errorStr.isEmpty()) {
    	        	String newURL = mapping.findForward("forcepasswordreset").getPath();
    	        	newURL = newURL + errorStr;  	        	
    	            return(new ActionForward(newURL));  
        	    }
        	   
        	    persistNewPassword(userName, newPassword);
        	            	    
        	    password = newPassword;
        	            	    
        	    //Remove the attributes from session
        	    removeAttributesFromSession(request);
         	}  
         	catch (Exception e) {
         		logger.error("Error", e);
                String newURL = mapping.findForward("error").getPath();
                newURL = newURL + "?errormsg=Setting values to the session.";   
                
        	    //Remove the attributes from session
        	    removeAttributesFromSession(request);
        	    
                return(new ActionForward(newURL));  
         	}

    	    //make sure this checking doesn't happen again
    	    forcedpasswordchange = false;
    	    
    	} else {
    		userName = ((LoginForm) form).getUsername();
    	    password = ((LoginForm) form).getPassword();
    	    pin = ((LoginForm) form).getPin();
    	    nextPage=request.getParameter("nextPage");

		    String username=(String)request.getSession().getAttribute("user");
		    logger.debug("nextPage: "+nextPage);
	        if (nextPage!=null) {
	        	// set current facility
	            String facilityIdString=request.getParameter(SELECTED_FACILITY_ID);
	            Facility facility=facilityDao.find(Integer.parseInt(facilityIdString));
	            request.getSession().setAttribute(SessionConstants.CURRENT_FACILITY, facility);
	            LogAction.addLogEntry(username, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId="+facilityIdString, ip);
	            if(facility.isEnableOcanForms()) {
	            	request.getSession().setAttribute("ocanWarningWindow", OcanForm.getOcanWarningMessage(facility.getId()));
	            }
	            return mapping.findForward(nextPage);
	        }
	        
	        if (cl.isBlocked(ip, userName)) {
	        	logger.info(LOG_PRE + " Blocked: " + userName);
		        LogAction.addLogEntry(username, null, LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_FAILURE,
				        null, ip, "Blocked " + userName + " for repeated login attempts");
	            // change to block page
	            String newURL = mapping.findForward("error").getPath();
	            newURL = newURL + "?errormsg=Your account is locked. Please contact your administrator to unlock.";
	            
	            if(ajaxResponse) {
	            	JSONObject json = new JSONObject();
	            	json.put("success", false);
	            	json.put("error", "Your account is locked. Please contact your administrator to unlock.");
	            	response.setContentType("text/x-json");
	            	json.write(response.getWriter());
	            	return null;
	            }
	            
	            return(new ActionForward(newURL));
	        }
	                
	        logger.debug("ip was not blocked: "+ip);
        
    	}
        
        String[] strAuth;
        try {
            strAuth = cl.auth(userName, password, pin, ip);
        }
        catch (Exception e) {
        	logger.error("Error", e);
            String newURL = mapping.findForward("error").getPath();
            if (e.getMessage() != null && e.getMessage().startsWith("java.lang.ClassNotFoundException")) {
                newURL = newURL + "?errormsg=Database driver " + e.getMessage().substring(e.getMessage().indexOf(':') + 2) + " not found.";
            }
            else {
                newURL = newURL + "?errormsg=Database connection error: " + e.getMessage() + ".";
            }
            
            if(ajaxResponse) {
            	JSONObject json = new JSONObject();
            	json.put("success", false);
            	json.put("error", "Database connection error:"+e.getMessage() + ".");
            	response.setContentType("text/x-json");
            	json.write(response.getWriter());
            	return null;
            }
            
            return(new ActionForward(newURL));
        }
        logger.debug("strAuth : "+Arrays.toString(strAuth));
        if (strAuth != null && strAuth.length != 1) { // login successfully
        	
        	
        	//is the provider record inactive?
        	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
            Provider p = providerDao.getProvider(strAuth[0]);
            if(p == null || (p.getStatus() != null && p.getStatus().equals("0"))) {
            	logger.info(LOG_PRE + " Inactive: " + userName);
            	LogAction.addLogEntry(strAuth[0], LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_FAILURE, "inactive");
            	
                String newURL = mapping.findForward("error").getPath();
                newURL = newURL + "?errormsg=Your account is inactive. Please contact your administrator to activate.";
                return(new ActionForward(newURL));
            }
            
            /* 
             * This section is added for forcing the initial password change.
             */
            Security security = getSecurity(userName);
            if (!OscarProperties.getInstance().getBooleanProperty("mandatory_password_reset", "false") && 
            	security.isForcePasswordReset() != null && security.isForcePasswordReset() && forcedpasswordchange	) {
            	
            	String newURL = mapping.findForward("forcepasswordreset").getPath();
            	
            	try{
            	   setUserInfoToSession( request, userName,  password,  pin, nextPage);
            	}  
            	catch (Exception e) {
            		logger.error("Error", e);
                    newURL = mapping.findForward("error").getPath();
                    newURL = newURL + "?errormsg=Setting values to the session.";            		
            	}

                return(new ActionForward(newURL));            	
            }
                        
            // invalidate the existing session
            HttpSession session = request.getSession(false);
            if (session != null) {
            	if(request.getParameter("invalidate_session") != null && request.getParameter("invalidate_session").equals("false")) {
            		//don't invalidate in this case..messes up authenticity of OAUTH
            	} else {
            		session.invalidate();
            	}
            }
            session = request.getSession(); // Create a new session for this user

            logger.debug("Assigned new session for: " + strAuth[0] + " : " + strAuth[3] + " : " + strAuth[4]);
            LogAction.addLogEntry(strAuth[0], LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, null, ip);

            // initial db setting
            OscarProperties pvar = OscarProperties.getInstance();
            MyOscarUtils.setDeterministicallyMangledPasswordSecretKeyIntoSession(session, password);
            

            String providerNo = strAuth[0];
            session.setAttribute("user", strAuth[0]);
            session.setAttribute("userfirstname", strAuth[1]);
            session.setAttribute("userlastname", strAuth[2]);
            session.setAttribute("userrole", strAuth[4]);
            session.setAttribute("oscar_context_path", request.getContextPath());
            session.setAttribute("expired_days", strAuth[5]);
            // If a new session has been created, we must set the mobile attribute again
            if (isMobileOptimized) session.setAttribute("mobileOptimized","true");
            // initiate security manager
            String default_pmm = null;
            
            
            
            // get preferences from preference table
        	ProviderPreference providerPreference=providerPreferenceDao.find(providerNo);
        	
            
                
        	if (providerPreference==null) providerPreference=new ProviderPreference();
         	
        	session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE, providerPreference);
        	
            if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable()) {
            	String tklerProviderNo = null;
            	UserProperty prop = propDao.getProp(providerNo, UserProperty.PROVIDER_FOR_TICKLER_WARNING);
        		if (prop == null) {
        			tklerProviderNo = providerNo;
        		} else {
        			tklerProviderNo = prop.getValue();
        		}
            	session.setAttribute("tklerProviderNo",tklerProviderNo);
            	
                session.setAttribute("newticklerwarningwindow", providerPreference.getNewTicklerWarningWindow());
                session.setAttribute("default_pmm", providerPreference.getDefaultCaisiPmm());
                session.setAttribute("caisiBillingPreferenceNotDelete", String.valueOf(providerPreference.getDefaultDoNotDeleteBilling()));
                
                default_pmm = providerPreference.getDefaultCaisiPmm();
                @SuppressWarnings("unchecked")
                ArrayList<String> newDocArr = (ArrayList<String>)request.getSession().getServletContext().getAttribute("CaseMgmtUsers");    
                if("enabled".equals(providerPreference.getDefaultNewOscarCme())) {
                	newDocArr.add(providerNo);
                	session.setAttribute("CaseMgmtUsers", newDocArr);
                }
            }
            session.setAttribute("starthour", providerPreference.getStartHour().toString());
            session.setAttribute("endhour", providerPreference.getEndHour().toString());
            session.setAttribute("everymin", providerPreference.getEveryMin().toString());
            session.setAttribute("groupno", providerPreference.getMyGroupNo());
                
            where = "provider";

            if (where.equals("provider") && default_pmm != null && "enabled".equals(default_pmm)) {
                where = "caisiPMM";
            }
            
            if (where.equals("provider") && OscarProperties.getInstance().getProperty("useProgramLocation", "false").equals("true") ) {
                where = "programLocation";
            }

            String quatroShelter = OscarProperties.getInstance().getProperty("QUATRO_SHELTER");
            if(quatroShelter!= null && quatroShelter.equals("on")) {
            	where = "shelterSelection";
            }

            CRHelper.recordLoginSuccess(userName, strAuth[0], request);

            String username = (String) session.getAttribute("user");
            Provider provider = providerManager.getProvider(username);
            session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER, provider);
            session.setAttribute(SessionConstants.LOGGED_IN_SECURITY, cl.getSecurity());

            LoggedInInfo loggedInInfo = LoggedInUserFilter.generateLoggedInInfoFromSession(request);
            
		    MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedIn(loggedInInfo, true);
            
            List<Integer> facilityIds = providerDao.getFacilityIds(provider.getProviderNo());
            if (facilityIds.size() > 1) {
                return(new ActionForward("/select_facility.jsp?nextPage=" + where));
            }
            else if (facilityIds.size() == 1) {
                // set current facility
                Facility facility=facilityDao.find(facilityIds.get(0));
                request.getSession().setAttribute("currentFacility", facility);
                LogAction.addLogEntry(strAuth[0], LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId="+facilityIds.get(0), ip);
                if(facility.isEnableOcanForms()) {
                	request.getSession().setAttribute("ocanWarningWindow", OcanForm.getOcanWarningMessage(facility.getId()));
                }
                if(facility.isEnableCbiForm()) {
                	request.getSession().setAttribute("cbiReminderWindow", CBIUtil.getCbiSubmissionFailureWarningMessage(facility.getId(),provider.getProviderNo() ));
                }
            }
            else {
        		List<Facility> facilities = facilityDao.findAll(true);
        		if(facilities!=null && facilities.size()>=1) {
        			Facility fac = facilities.get(0);
        			int first_id = fac.getId();
        			ProviderDao.addProviderToFacility(providerNo, first_id);
        			Facility facility=facilityDao.find(first_id);
        			request.getSession().setAttribute("currentFacility", facility);
        			LogAction.addLogEntry(strAuth[0], LogConst.ACTION_LOGIN, LogConst.CON_LOGIN, LogConst.STATUS_SUCCESS, "facilityId="+first_id, ip);
            	}
            }

            if( pvar.getProperty("LOGINTEST","").equalsIgnoreCase("yes")) {
                String proceedURL = mapping.findForward(where).getPath();
                request.getSession().setAttribute("proceedURL", proceedURL);               
                return mapping.findForward("LoginTest");
            }
            
            //are they using the new UI?
            UserProperty prop = propDao.getProp(provider.getProviderNo(), UserProperty.COBALT);
            if(prop != null && prop.getValue() != null && prop.getValue().equals("yes")) {
            	where="cobalt";
            }
        }
        // expired password
        else if (strAuth != null && strAuth.length == 1 && strAuth[0].equals("expired")) {
        	logger.warn("Expired password");
            cl.updateLoginList(ip, userName);
            String newURL = mapping.findForward("error").getPath();
            newURL = newURL + "?errormsg=Your account is expired. Please contact your administrator.";
            
            if(ajaxResponse) {
            	JSONObject json = new JSONObject();
            	json.put("success", false);
            	json.put("error", "Your account is expired. Please contact your administrator.");
            	response.setContentType("text/x-json");
            	json.write(response.getWriter());
            	return null;
            }
            
            return(new ActionForward(newURL));
        }
        else { 
        	logger.debug("go to normal directory");

        	// go to normal directory
            // request.setAttribute("login", "failed");
            cl.updateLoginList(ip, userName);
            CRHelper.recordLoginFailure(userName, request);
            
            if(ajaxResponse) {
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
        if(request.getParameter("oauth_token") != null) {
    		String proNo = (String)request.getSession().getAttribute("user");
    		ServiceRequestTokenDao serviceRequestTokenDao = SpringUtils.getBean(ServiceRequestTokenDao.class);
    		ServiceRequestToken srt = serviceRequestTokenDao.findByTokenId(request.getParameter("oauth_token"));
    		if(srt != null) {
    			srt.setProviderNo(proNo);
    			serviceRequestTokenDao.merge(srt);
    		}
    	}
        
        if(ajaxResponse) {
        	logger.debug("rendering ajax response");
        	Provider prov = providerDao.getProvider((String)request.getSession().getAttribute("user"));
        	JSONObject json = new JSONObject();
        	json.put("success", true);
        	json.put("providerName", prov.getFormattedName());
        	json.put("providerNo", prov.getProviderNo());
        	response.setContentType("text/x-json");
        	json.write(response.getWriter());
        	return null;
        }

    	logger.debug("rendering standard response : "+where);
        return mapping.findForward(where);
    }
    
    
    /**
     * Removes attributes from session
     * @param request
     */
    private void removeAttributesFromSession(HttpServletRequest request) {
	    request.getSession().removeAttribute("userName");
	    request.getSession().removeAttribute("password");
	    request.getSession().removeAttribute("pin");
	    request.getSession().removeAttribute("nextPage");
    }
    
    /**
     * Set user info to session
     * @param request
     * @param userName
     * @param password
     * @param pin
     * @param nextPage
     */
    private void setUserInfoToSession(HttpServletRequest request,String userName, String password, String pin,String nextPage) throws Exception{
    	request.getSession().setAttribute("userName", userName);
    	request.getSession().setAttribute("password", encodePassword(password));
    	request.getSession().setAttribute("pin", pin);
    	request.getSession().setAttribute("nextPage", nextPage);
    
    }
    
     /**
      * Performs the error handling
     * @param password
     * @param newPassword
     * @param confirmPassword
     * @param oldPassword
     * @return
     */
    private String errorHandling(String password, String  newPassword, String  confirmPassword, String  encodedOldPassword, String  oldPassword){
	    
    	String newURL = "";

	    if (!encodedOldPassword.equals(password)) {
     	   newURL = newURL + "?errormsg=Your old password, does NOT match the password in the system. Please enter your old password.";  
     	} else if (!newPassword.equals(confirmPassword)) {
      	   newURL = newURL + "?errormsg=Your new password, does NOT match the confirmed password. Please try again.";  
      	} else if (!Boolean.parseBoolean(OscarProperties.getInstance().getProperty("IGNORE_PASSWORD_REQUIREMENTS")) && newPassword.equals(oldPassword)) {
       	   newURL = newURL + "?errormsg=Your new password, is the same as your old password. Please choose a new password.";  
       	} 
    	    
	    return newURL;
     }
    
    
    /**
     * This method encodes the password, before setting to session.
     * @param password
     * @return
     * @throws Exception
     */
    private String encodePassword(String password) throws Exception{

    	MessageDigest md = MessageDigest.getInstance("SHA");
    	
    	StringBuilder sbTemp = new StringBuilder();
	    byte[] btNewPasswd= md.digest(password.getBytes());
	    for(int i=0; i<btNewPasswd.length; i++) sbTemp = sbTemp.append(btNewPasswd[i]);
	
	    return sbTemp.toString();
	    
    }
    
    
    /**
     * get the security record based on the username
     * @param username username to get security record for
     * @return null if no entry found, otherwise corresponding security entry
     */
    private Security getSecurity(String username) {

		SecurityDao securityDao = (SecurityDao) SpringUtils.getBean("securityDao");
		Security security = securityDao.findByUserName(username);

		if (security == null) {
			return null;
		} else if (OscarProperties.isLdapAuthenticationEnabled()) {
			security = new LdapSecurity(security);
		}
		
		return security;
    }	
    
    
    /**
     * Persists the new password
     * @param userName
     * @param newPassword
     * @return
     */
    private void  persistNewPassword(String userName, String newPassword) throws Exception{
    
	    Security security = getSecurity(userName);
	    security.setPassword(encodePassword(newPassword));
	    security.setForcePasswordReset(Boolean.FALSE);
	    SecurityDao securityDao = (SecurityDao) SpringUtils.getBean("securityDao");	    
	    securityDao.saveEntity(security); 
		
    }
         
	public ApplicationContext getAppContext() {
		return WebApplicationContextUtils.getWebApplicationContext(getServlet().getServletContext());
	}
}
