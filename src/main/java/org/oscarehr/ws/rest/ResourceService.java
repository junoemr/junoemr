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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.oscarehr.app.OAuth1Utils;
import org.oscarehr.common.dao.AppDefinitionDao;
import org.oscarehr.common.dao.AppUserDao;
import org.oscarehr.common.dao.ResourceStorageDao;
import org.oscarehr.common.model.AppDefinition;
import org.oscarehr.common.model.AppUser;
import org.oscarehr.common.model.ResourceStorage;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.AppManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.model.NotificationTo1;
import org.springframework.beans.factory.annotation.Autowired;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.oscarPrevention.PreventionDS;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


@Path("/resources")
public class ResourceService extends AbstractServiceImpl {
	private static final Logger logger = MiscUtils.getLogger();
	
	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private AppDefinitionDao appDefinitionDao;
	
	@Autowired
	AppManager appManager;
	
	@Autowired
	private AppUserDao appUserDao;
	
	@Autowired
	private ResourceStorageDao resourceStorageDao;
	
	@Autowired
	private PreventionDS preventionDS;

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	// this is duplicated in AppService for some reason.
	@GET
	@Path("/K2AActive/")
	@Produces("application/json")
	public RestResponse<Boolean> isK2AActive(@Context HttpServletRequest request)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ);

    	boolean k2aEnabled = systemPreferenceService.isPreferenceEnabled(UserProperty.INTEGRATION_KNOW2ACT_ENABLED, false);
    	boolean k2aInit = appManager.getAppDefinition(getLoggedInInfo(), "K2A") != null;

		return RestResponse.successResponse(k2aEnabled && k2aInit);
	}

	private String getK2aResource(LoggedInInfo loggedInInfo, String requestURI, String baseRequestURI) {
		AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
		if (k2aApp != null) {
			AppUser k2aUser = appUserDao.findForProvider(k2aApp.getId(), loggedInInfo.getLoggedInProvider().getProviderNo());
			if (k2aUser != null) {
				return OAuth1Utils.getOAuthGetResponse(loggedInInfo, k2aApp, k2aUser, requestURI, baseRequestURI);
			}
		}
		return null;
	}
	
	@GET
	@Path("/preventionRulesList")
	@Produces("application/json")
	public RestResponse<JSONArray> getPreventionRulesListFromK2A(@Context HttpServletRequest request)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ, Permission.PREVENTION_READ);

		JSONArray retArray = new JSONArray();
		try
		{
			String resource = getK2aResource(loggedInInfo,
					"/ws/api/oscar/get/PREVENTION_RULES/list",
					"/ws/api/oscar/get/PREVENTION_RULES/list");
			if(resource == null) {
				return RestResponse.errorResponse("Failed to load Resource");
			}

			JSONArray rulesArray = JSONArray.fromObject(resource);

			//id  |  type  |       created_at       |       updated_at       | created_by | updated_by |       body        |      name      | private 
			logger.info("rules json" + rulesArray);
			for (int i = 0; i < rulesArray.size(); i++) {
				JSONObject jobject = new JSONObject();
				JSONObject rule = (JSONObject) rulesArray.get(i);
				jobject.put("id", rule.getString("id"));
				jobject.put("name", rule.getString("name"));
				jobject.put("rulesXML", rule.getString("body"));
				jobject.put("created_at", rule.getString("created_at"));
				jobject.put("author", rule.getString("author"));

				retArray.add(jobject);
			}

		}
		catch (Exception e) {
			logger.error("Error retrieving prevention list", e);
			return RestResponse.errorResponse("Error retrieving prevention list");
		}
		return RestResponse.successResponse(retArray);
	}
	
	@GET
	@Path("/currentPreventionRulesVersion")
	@Produces("application/json")
	public RestResponse<String> getCurrentPreventionRulesVersion()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ, Permission.PREVENTION_READ);

		try {
			ResourceBundle bundle = getResourceBundle();

			String preventionPath = OscarProperties.getInstance().getProperty("PREVENTION_FILE");
			if (preventionPath != null) {
				return RestResponse.successResponse(bundle.getString("prevention.currentrules.propertyfile"));
			}
			else {
				ResourceStorage resourceStorage = resourceStorageDao.findActive(ResourceStorage.PREVENTION_RULES);
				if (resourceStorage != null) {
					return RestResponse.successResponse(bundle.getString("prevention.currentrules.resourceStorage") +
							" " + resourceStorage.getResourceName());
				}
			}
			return RestResponse.successResponse(bundle.getString("prevention.currentrules.default"));
		}
		catch (Exception e) {
			logger.error("Unexpected Error", e);
			return RestResponse.errorResponse("Unexpected Error");
		}
	}
	
	@POST
	@Path("/loadPreventionRulesById/{id}")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<String> addK2AReport(@PathParam("id") String id, @Context HttpServletRequest request, JSONObject jSONObject)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_CREATE, Permission.PREVENTION_CREATE);

		try {

			//Log agreement
			if(jSONObject.containsKey("agreement")){
				String action = "oauth1_AGREEMENT";
		    	String content = "PREVENTION_RULES_AGREEMENT";
		    	String contentId = id;
		    	String demographicNo = null;
		    	String data = jSONObject.getString("agreement");
		    	LogAction.addLog(loggedInInfo, action, content, contentId, demographicNo, data);
			}
			
			
			String resource = getK2aResource(loggedInInfo,"/ws/api/oscar/get/PREVENTION_RULES/id/"+id, "/ws/api/oscar/get/PREVENTION_RULES/id/"+id);
			
			if(resource !=null){
				//JSONObject jSONObject = JSONObject.fromObject(resource);
				ResourceStorage resourceStorage = new ResourceStorage();
				resourceStorage.setActive(true);
				resourceStorage.setResourceName(jSONObject.getString("name"));
				resourceStorage.setResourceType(ResourceStorage.PREVENTION_RULES);
				if(jSONObject.containsKey("uuid")){
					resourceStorage.setUuid(jSONObject.getString("uuid"));
				}
				resourceStorage.setUploadDate(new Date());
				resourceStorage.setFileContents(resource.getBytes());
				resourceStorage.setUuid(null);
				
				List<ResourceStorage> currActive=  resourceStorageDao.findActiveAll(ResourceStorage.PREVENTION_RULES);
				if(currActive != null){
					for(ResourceStorage rs: currActive){
						rs.setActive(false);
						resourceStorageDao.merge(rs);
					}
				}
				resourceStorageDao.persist(resourceStorage);
				preventionDS.reloadRuleBase();
			}
		}
		catch (Exception e) {
			logger.error("Error saving Resource to Storage", e);
			return RestResponse.errorResponse("Failed to save Resource to Storage");
		}
		return RestResponse.successResponse("Success");
	}

	@GET
	@Path("/notifications")
	@Produces("application/json")
	public RestResponse<List<NotificationTo1>> getNotifications(@Context HttpServletRequest request)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ);

		List<NotificationTo1> list = new ArrayList<NotificationTo1>();
		try {
			String notificationStr = getK2aResource(loggedInInfo, "/ws/api/notification", "/ws/api/notification");
			if(notificationStr == null) {
				return RestResponse.errorResponse("Failed to load Resource");
			}
			JSONObject notifyObject = JSONObject.fromObject(notificationStr);

			if(notifyObject.getInt("numberOfNotifications") > 0) {
				JSONArray notifyArrList = notifyObject.getJSONArray("notification");
				for (int i = 0; i < notifyArrList.size(); i++) {
					list.add(NotificationTo1.fromJSON(notifyArrList.getJSONObject(i)));
				}
			}
		}
		catch (Exception e) {
			logger.error("Error loading notifications", e);
			return RestResponse.errorResponse("Error loading notifications");
		}
		return RestResponse.successResponse(list);
	}

	@GET
	@Path("/notifications/number")
	@Produces("application/json")
	public RestResponse<String> getNotificationsNumber(@Context HttpServletRequest request)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ);
		try
		{
			String notificationStr = getK2aResource(loggedInInfo, "/ws/api/notification", "/ws/api/notification");
			if(notificationStr != null) {
				JSONObject notifyObject = JSONObject.fromObject(notificationStr);
				String k2aNoficationCount = notifyObject.getString("numberOfNotifications");
				return RestResponse.successResponse(k2aNoficationCount);
			}
			return RestResponse.successResponse("-");
		}
		catch (Exception e) {
			logger.error("Error geting notifcations", e);
			return RestResponse.errorResponse("Failed to load Notification Count");
		}
	}
	
	@POST
	@Path("/notifications/readmore")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<String> getMoreInfoNotificationURL(@Context HttpServletRequest request, JSONObject jSONObject)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_READ);

		String retval = "";
		try
		{
			AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
			if (k2aApp != null) {
				AppUser k2aUser = appUserDao.findForProvider(k2aApp.getId(), loggedInInfo.getLoggedInProvider().getProviderNo());

				if (k2aUser != null) {
					retval = OAuth1Utils.getOAuthPostResponse(loggedInInfo, k2aApp, k2aUser, "/ws/api/notification/readmore", "/ws/api/notification/readmore", OAuth1Utils.getProviderK2A(), NotificationTo1.fromJSON(jSONObject));
					logger.debug(retval);
				}
			}
			return RestResponse.successResponse(retval);
		}
		catch (Exception e) {
			logger.error("ERROR:", e);
			return RestResponse.errorResponse("Failed to get Notification URL");
		}
	}

	@POST
	@Path("/notifications/{id}/ack/")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<String> markNotificationAsAck(@PathParam("id") String id, @Context HttpServletRequest request, JSONObject jSONObject)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.K2A_UPDATE);

		String retval = "";
		try
		{
			AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
			if (k2aApp != null) {
				AppUser k2aUser = appUserDao.findForProvider(k2aApp.getId(), loggedInInfo.getLoggedInProvider().getProviderNo());

				if (k2aUser != null) {
					retval = OAuth1Utils.getOAuthPostResponse(loggedInInfo, k2aApp, k2aUser, "/ws/api/notification/ack", "/ws/api/notification/ack", OAuth1Utils.getProviderK2A(), NotificationTo1.fromJSON(jSONObject));
					logger.debug(retval);
				}
			}
			return RestResponse.successResponse(retval);
		}
		catch (Exception e) {
			logger.error("ERROR:", e);
			return RestResponse.errorResponse("Failed to mark as Acknowledged");
		}
	}
}