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
package org.oscarehr.ws.rest;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.admin.service.AdminNavService;
import org.oscarehr.common.IsPropertiesOn;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Dashboard;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.AppManager;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.DashboardManager;
import org.oscarehr.managers.MessagingManager;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.myoscar.client.ws_manager.MessageManager;
import org.oscarehr.myoscar.utils.MyOscarLoggedInInfo;
import org.oscarehr.phr.util.MyOscarUtils;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.conversion.ProgramProviderConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.DashboardPreferences;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.oscarehr.ws.rest.to.NavbarResponse;
import org.oscarehr.ws.rest.to.PersonaResponse;
import org.oscarehr.ws.rest.to.model.AdminNavGroupTo1;
import org.oscarehr.ws.rest.to.model.MenuItemTo1;
import org.oscarehr.ws.rest.to.model.MenuTo1;
import org.oscarehr.ws.rest.to.model.NavBarMenuTo1;
import org.oscarehr.ws.rest.to.model.PatientListConfigTo1;
import org.oscarehr.ws.rest.to.model.ProgramProviderTo1;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;


@Path("/persona")
public class PersonaService extends AbstractServiceImpl {
	protected Logger logger = MiscUtils.getLogger();
	
	
	@Autowired
	private ProgramManager2 programManager2;
	
	@Autowired
	private MessagingManager messagingManager;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private ConsultationManager consultationManager;
	
	@Autowired
	private AppManager appManager;
	
	@Autowired
	private DashboardManager dashboardManager;

	@Autowired
	private AdminNavService adminNavService;

	@GET
	@Path("/navbar")
	@Produces("application/json")
	public NavbarResponse getMyNavbar() {
		String currentUserId = getLoggedInProviderId();
		ResourceBundle bundle = getResourceBundle();
		
		NavbarResponse result = new NavbarResponse();
		
		/* program domain, current program */
		List<ProgramProvider> ppList = programManager2.getProgramDomain(getLoggedInInfo(),currentUserId);
		ProgramProviderConverter ppConverter = new ProgramProviderConverter();
		List<ProgramProviderTo1> programDomain = new ArrayList<ProgramProviderTo1>();
		
		for(ProgramProvider pp:ppList) {
			programDomain.add(ppConverter.getAsTransferObject(getLoggedInInfo(),pp));
		}
		result.setProgramDomain(programDomain);
		
		ProgramProvider pp = programManager2.getCurrentProgramInDomain(getLoggedInInfo(),currentUserId);
		if(pp != null) {
			ProgramProviderTo1 ppTo = ppConverter.getAsTransferObject(getLoggedInInfo(),pp);
			result.setCurrentProgram(ppTo);
		} else {
			if(result.getProgramDomain() != null && result.getProgramDomain().size()>0) {
				result.setCurrentProgram(result.getProgramDomain().get(0));
			}
		}
		
		/* counts */
		// Remove demographic message count. Leaving comments for future debugging to address the previously mentioned JSON error.
		// int messageCount = messagingManager.getMyInboxMessageCount(getLoggedInInfo(),provider.getProviderNo(), false);
		MenuTo1 messengerMenu = new MenuTo1();
		int menuItemCounter = 0;

		if(securityInfoManager.hasPrivileges(currentUserId, Permission.MESSAGE_READ))
		{
			int ptMessageCount = messagingManager.getMyInboxMessageCount(getLoggedInInfo(), currentUserId, true);
			messengerMenu.add(menuItemCounter++, bundle.getString("navbar.newOscarMessages"), "" + ptMessageCount, "classic");
		}
		
		
		if(MyOscarUtils.isMyOscarEnabled(currentUserId)){
			String phrMessageCount = "-";
			MyOscarLoggedInInfo myOscarLoggedInInfo=MyOscarLoggedInInfo.getLoggedInInfo(getLoggedInInfo().getSession());

			if (myOscarLoggedInInfo!=null && myOscarLoggedInInfo.isLoggedIn()){
				try{
					int phrMCount = MessageManager.getUnreadActiveMessageCount(myOscarLoggedInInfo, myOscarLoggedInInfo.getLoggedInPersonId());
					phrMessageCount = ""+phrMCount;
				}catch (Exception e){
					// we'll force a re-login if this ever fails for any reason what so ever.
					MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedInAsynchronously(getLoggedInInfo(), true);
				}
			}
			messengerMenu.add(menuItemCounter++, bundle.getString("navbar.newMyOscarMessages"), phrMessageCount, "phr");
		}

		if (securityInfoManager.hasPrivileges(currentUserId, Permission.K2A_READ)
				&& appManager.isK2AEnabled())
		{
			String k2aMessageCount = appManager.getK2ANotificationNumber(getLoggedInInfo());
			messengerMenu.add(menuItemCounter++, bundle.getString("navbar.newK2ANotifications"), k2aMessageCount, "k2a");
		}
		
		
		/* this is manual right now. Need to have this generated from some kind
		 * of user data
		 */
		NavBarMenuTo1 navBarMenu = new NavBarMenuTo1();
		navBarMenu.setMessengerMenu(messengerMenu);

		MenuTo1 patientSearchMenu = new MenuTo1().add(0, bundle.getString("navbar.menu.newPatient"), null, "#/newpatient")
				.add(1, bundle.getString("navbar.menu.advancedSearch"), null, "#/search");
		navBarMenu.setPatientSearchMenu(patientSearchMenu);
		
		int idCounter = 0;
		
		MenuTo1 menu = new MenuTo1()
				.addWithState(idCounter++,bundle.getString("navbar.menu.dashboard"), null, "dashboard");

		if(securityInfoManager.hasPrivileges(currentUserId, Permission.APPOINTMENT_READ))
		{
			if (OscarProperties.getInstance().isScheduleEnabled() || getLoggedInInfo().getLoggedInProvider().getSuperAdmin())
			{
				menu.addWithState(idCounter++, bundle.getString("navbar.menu.schedule"), null, "schedule");
			}
			else
			{
				menu.add(idCounter++, bundle.getString("navbar.menu.schedule"), null, "../provider/providercontrol.jsp");
			}
		}

		if(securityInfoManager.hasPrivileges(currentUserId, Permission.LAB_READ, Permission.DOCUMENT_READ, Permission.HRM_READ))
		{
			menu.addWithState(idCounter++, bundle.getString("navbar.menu.inbox"), null, "inbox");
		}

		if(securityInfoManager.hasPrivileges(currentUserId, Permission.CONSULTATION_READ))
		{
			if (!consultationManager.isConsultResponseEnabled())
			{
				menu.addWithState(idCounter++, bundle.getString("navbar.menu.consults"), null, "consultRequests");
			}
			else if (!consultationManager.isConsultRequestEnabled())
			{
				menu.addWithState(idCounter++, bundle.getString("navbar.menu.consults"), null, "consultResponses");
			}

			//consult menu
			if (consultationManager.isConsultRequestEnabled() && consultationManager.isConsultResponseEnabled())
			{
				MenuItemTo1 consultMenu = new MenuItemTo1(idCounter++, bundle.getString("navbar.menu.consults"), null);
				consultMenu.setDropdown(true);
				MenuTo1 consultMenuList = new MenuTo1()
						.addWithState(idCounter++, bundle.getString("navbar.menu.consultRequests"), null, "consultRequests")
						.addWithState(idCounter++, bundle.getString("navbar.menu.consultResponses"), null, "consultResponses");
				consultMenu.setDropdownItems(consultMenuList.getItems());
				menu.getItems().add(consultMenu);
			}
		}

		if(securityInfoManager.hasPrivileges(currentUserId, Permission.TICKLER_READ))
		{
			menu.addWithState(idCounter++, bundle.getString("navbar.menu.tickler"), null, "ticklers");
		}
			//.add(0,"K2A",null,"#/k2a")
		if(securityInfoManager.hasPrivileges(currentUserId, Permission.BILLING_READ))
		{
			menu.addWithState(idCounter++, bundle.getString("navbar.menu.billing"), null, "billing");
		}

		//TODO add "star" states, Ex: admin.* to indicate any state starting with admin
		menu.addWithStates(idCounter++,bundle.getString("navbar.menu.admin"),null,
							Arrays.asList("admin.landingPage",
											"admin.frame",
											"admin.faxConfig",
											"admin.faxSendReceive",
											"admin.integrationModules",
											"admin.panelManagement",
											"admin.iceFall",
											"admin.iceFall.settings",
											"admin.iceFall.activity",
											"admin.addUser",
											"admin.editUser",
											"admin.viewUser",
											"admin.manageUsers",
											"admin.manageAppointmentQueues"));
		if(securityInfoManager.hasPrivileges(currentUserId, Permission.REPORT_READ))
		{
			menu.addWithState(idCounter++, bundle.getString("navbar.menu.reports"), null, "reports");
		}
		if(securityInfoManager.hasPrivileges(currentUserId, Permission.DOCUMENT_READ))
		{
			menu.addWithState(idCounter++, bundle.getString("navbar.menu.documents"), null, "documents");
		}

		if (securityInfoManager.hasPrivileges(currentUserId, Permission.DEMOGRAPHIC_READ, Permission.APPOINTMENT_READ)
				&& IsPropertiesOn.isTelehealthEnabled())
		{
			menu.addNewTab(idCounter++, "MyHealthAccess", null,
					"../integrations/myhealthaccess.do?method=connectOrList");
		}

		MenuItemTo1 moreMenu = new MenuItemTo1(idCounter++, bundle.getString("navbar.menu.more"), null);
		moreMenu.setDropdown(true);
		
		// MenuTo1 moreMenuList = new MenuTo1()
		// .addWithState(idCounter++,bundle.getString("navbar.menu.billing"),null,"billing")
		// .addWithState(idCounter++,bundle.getString("navbar.menu.admin"),null,"admin")
		// .addWithState(idCounter++,bundle.getString("navbar.menu.reports"),null,"reports")
		// .addWithState(idCounter++,bundle.getString("navbar.menu.documents"),null,"documents");
		// moreMenu.setDropdownItems(moreMenuList.getItems());
		// menu.getItems().add(moreMenu);
		
		navBarMenu.setMenu(menu);
	
		MenuTo1 userMenu = new MenuTo1()
		.addWithState(0,bundle.getString("navbar.menu.settings"),null,"settings")
		.addNewWindow(1,bundle.getString("navbar.menu.help"),null,"https://help.oscarhost.ca")
		.addWithState(2,bundle.getString("navbar.menu.logout"),null,"logout");
		navBarMenu.setUserMenu(userMenu);

		result.setMenus(navBarMenu);
		
		return result;
	}

	@GET
	@Path("/setDefaultProgramInDomain")
	public GenericRESTResponse setDefaultProgram(@QueryParam("programId") Integer programId) {
		programManager2.setCurrentProgramInDomain(getLoggedInInfo().getLoggedInProviderNo(), programId);
		return new GenericRESTResponse();
	}

	@GET
	@Path("/patientList/config")
	@Produces("application/json")
	public PatientListConfigTo1 getMyPatientListConfig(){
		Provider provider = getCurrentProvider();
		PatientListConfigTo1 patientListConfigTo1 = new PatientListConfigTo1();
		UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
		String numberOfApptsToShow = propDao.getStringValue(provider.getProviderNo(), "patientListConfig.numberOfApptsToShow");
		if(numberOfApptsToShow != null){
			try{
				patientListConfigTo1.setNumberOfApptstoShow(Integer.parseInt(numberOfApptsToShow));
			}catch(Exception e){
				logger.error("numberOfAppts is not a number"+numberOfApptsToShow,e);
			}
		}
		
		String showReason = propDao.getStringValue(provider.getProviderNo(), "patientListConfig.showReason");
		if(showReason != null){
			try{
				patientListConfigTo1.setShowReason(Boolean.parseBoolean(showReason)); 
			}catch(Exception e){
				logger.error("showReason is not a boolean"+showReason,e);
			}
		}

		String showStatus = propDao.getStringValue(provider.getProviderNo(), "patientListConfig.showStatus");
		if(showStatus != null){
			try{
				patientListConfigTo1.setShowStatus(Boolean.parseBoolean(showStatus));
			}catch(Exception e){
				logger.error("showReason is not a boolean"+showStatus,e);
			}
		}
		
		return patientListConfigTo1;
	}
	
	@POST
	@Path("/patientList/config")
	@Produces("application/json")
	@Consumes("application/json")
	public PatientListConfigTo1 saveMyPatientListConfig(PatientListConfigTo1 patientListConfigTo1){
		Provider provider = getCurrentProvider();
		
		UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
		Integer numberOfApptsToShow =  patientListConfigTo1.getNumberOfApptstoShow();
		
		if(numberOfApptsToShow != null && numberOfApptsToShow > 0){
			UserProperty prop = propDao.getProp(provider.getProviderNo(), "patientListConfig.numberOfApptsToShow");
			if(prop != null) {
				prop.setValue(String.valueOf(numberOfApptsToShow));
			} else {
				prop = new UserProperty();
				prop.setName("patientListConfig.numberOfApptsToShow");
				prop.setProviderNo(provider.getProviderNo());
				prop.setValue(String.valueOf(numberOfApptsToShow));
			}
			propDao.saveProp(prop);
		}
		
		boolean showReason =  patientListConfigTo1.isShowReason();
		UserProperty prop = propDao.getProp(provider.getProviderNo(), "patientListConfig.showReason");
		if(prop != null) {
			prop.setValue(Boolean.toString(showReason));
		} else {
			prop = new UserProperty();
			prop.setName("patientListConfig.showReason");
			prop.setProviderNo(provider.getProviderNo());
			prop.setValue(Boolean.toString(showReason));
		}
		propDao.saveProp(prop);

		boolean showStatus = patientListConfigTo1.isShowStatus();
		UserProperty propShowStatus = propDao.getProp(provider.getProviderNo(), "patientListConfig.showStatus");
		if(propShowStatus != null)
		{
			propShowStatus.setValue(Boolean.toString(showStatus));
		}
		else
		{
			propShowStatus = new UserProperty();
			propShowStatus.setName("patientListConfig.showStatus");
			propShowStatus.setProviderNo(provider.getProviderNo());
			propShowStatus.setValue(Boolean.toString(showStatus));
		}
		propDao.saveProp(propShowStatus);
		
		return patientListConfigTo1;
	}
	
	/**
	 * This will be a REST based way to get access to groups of preferences. It's not fully implemented yet
	 * 
	 * @param obj
	 * @return PersonaResponse
	 */
	@POST
	@Path("/preferences")
	@Produces("application/json")
	@Consumes("application/json")
	public PersonaResponse getPreferences(JSONObject obj) {
		Provider provider = getCurrentProvider();
		
		//not yet used..need a way to just load specific groups of properties.
		String type = obj.getString("type");
		
		PersonaResponse response = new PersonaResponse();
		DashboardPreferences prefs = new DashboardPreferences();
		
		//this needs to be more structured after the alpha. Create a manager a way to load with defaults
		UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
		String strVal = propDao.getStringValue(provider.getProviderNo(), "dashboard.expiredTicklersOnly");
		if(strVal == null) {
			prefs.setExpiredTicklersOnly(true);
		}
		else if(strVal != null && "true".equalsIgnoreCase(strVal)) {
			prefs.setExpiredTicklersOnly(true);
		}
		
		response.setDashboardPreferences(prefs);

		return response;
	}
	
	@POST
	@Path("/updatePreferences")
	@Produces("application/json")
	@Consumes("application/json")
	public GenericRESTResponse updatePreferences(JSONObject json)
	{
		String loggedInProviderId = getLoggedInProviderId();
		securityInfoManager.requireAllPrivilege(loggedInProviderId, Permission.PREFERENCE_UPDATE);
		GenericRESTResponse response = new GenericRESTResponse();

		Boolean value = null;
		
		if(json.has("expiredTicklersOnly")) {
			value = json.getBoolean("expiredTicklersOnly");
		}
		
		if(value != null) {

			UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
			UserProperty prop = propDao.getProp(loggedInProviderId, "dashboard.expiredTicklersOnly");
			if(prop != null) {
				prop.setValue(String.valueOf(value));
			} else {
				prop = new UserProperty();
				prop.setName("dashboard.expiredTicklersOnly");
				prop.setProviderNo(loggedInProviderId);
				prop.setValue(String.valueOf(value));
			}
			
			propDao.saveProp(prop);
			
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
		}
		
		
		return response;
	
	}
	
	
	@GET
	@Path("/dashboardMenu")
	@Produces("application/json")
	public NavbarResponse getDashboardMenu() {
		
		List<Dashboard> dashboards = dashboardManager.getDashboards( getLoggedInInfo() );
		
		ResourceBundle bundle = getResourceBundle();
		
		NavbarResponse result = new NavbarResponse();
		
		if( dashboards != null ) {
			NavBarMenuTo1 navBarMenu = new NavBarMenuTo1();
			
			MenuTo1 dashboardMenu = new MenuTo1();
			dashboardMenu.add(null, bundle.getString( "navbar.menu.dashboard" ), null, "dashboard");
			
			if( ! dashboards.isEmpty() ) {
				
				MenuItemTo1 dashboardDropdownMenu = new MenuItemTo1( null, bundle.getString("navbar.menu.dashboard"), null );			
				MenuTo1 dashboardDropdownList = new MenuTo1();
				
				for( Dashboard dashboard : dashboards ) {
					dashboardDropdownList.addWithState( dashboard.getId(), 
							dashboard.getName(), dashboard.getName(), "DashboardDisplay/"+dashboard.getId());
				}
				
				dashboardDropdownMenu.setDropdown( Boolean.TRUE );
				dashboardDropdownMenu.setDropdownItems( dashboardDropdownList.getItems() );
				
				dashboardMenu.getItems().add( dashboardDropdownMenu );
				
			}
			
			navBarMenu.setMenu( dashboardMenu );
			
			result.setMenus( navBarMenu );
		}
		
		return result;
	}

	@GET
	@Path("/adminNav")
	@Produces("application/json")
	public RestResponse<List<AdminNavGroupTo1>> getAdminNavItems()
	{
		return RestResponse.successResponse(adminNavService.getAdminNavGroups(getHttpServletRequest().getContextPath(), getOscarResourcesBundle(), getCurrentProvider().getProviderNo(), getHttpServletRequest().getSession()));
	}

}
