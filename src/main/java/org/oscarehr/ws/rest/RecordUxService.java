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


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.service.CaseManagementPrint;
import org.oscarehr.common.dao.DashboardDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.EncounterTemplateDao;
import org.oscarehr.common.model.Dashboard;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.ConsultationManager;
import org.oscarehr.managers.PreferenceManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.rest.conversion.EncounterTemplateConverter;
import org.oscarehr.ws.rest.conversion.summary.LabsDocsSummary;
import org.oscarehr.ws.rest.conversion.summary.Summary;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.EncounterTemplateResponse;
import org.oscarehr.ws.rest.to.model.EncounterTemplateTo1;
import org.oscarehr.ws.rest.to.model.MenuItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.oscarehr.ws.rest.transfer.DashboardTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.OscarProperties;
import oscar.oscarProvider.data.ProviderMyOscarIdData;



@Path("/recordUX/")
@Component("recordUxService")
public class RecordUxService extends AbstractServiceImpl {
	private static Logger logger = MiscUtils.getLogger();
	
	@Autowired
	private SecurityInfoManager securityInfoManager;
	
	@Autowired
	private ConsultationManager consultationManager;
	
	@Autowired
	private EncounterTemplateDao encounterTemplateDao;
	
	@Autowired
	private PreferenceManager preferenceManager;

	@Autowired
	private SystemPreferenceService systemPreferenceService;

	@Autowired
	private DashboardDao dashboardDao;

	/**
	$scope.recordtabs2 = [ 
	 {id : 0,name : 'Master',url : 'partials/master.html'},
	 {id : 1,name : 'Summary',url : 'partials/summary.html'},
	 {id : 2,name : 'Rx',url : 'partials/rx.jsp'},
	 {id : 3,name : 'Msg',url : 'partials/summary.html'},
	 {id : 4,name : 'Trackers',url : 'partials/tracker.jsp'},
	 {id : 5,name : 'Consults',url : 'partials/summary.html'},
	 {id : 6,name : 'Forms',url : 'partials/formview.html'},
	 {id : 7,name : 'Prevs/Measurements',url : 'partials/summary.html'},
	 {id : 8,name : 'Ticklers',url : 'partials/summary.html'},
	 {id : 9,name : 'MyOscar',url : 'partials/blank.jsp'},
	 {id : 10,name : 'Allergies',url : 'partials/summary.html'},
	 {id : 11,name : 'CPP',url : 'partials/cpp.html'},
	 {id : 12,name : 'Labs/Docs',url : 'partials/labview.html'},
	 {id : 13,name : 'Billing',url : 'partials/billing.jsp'}
	 ];
	...
	**/
	
	@GET
	@Path("/{demographicNo}/recordMenu")
	@Produces("application/json")
	public List<MenuItemTo1> getRecordMenu(@PathParam("demographicNo") Integer demographicNo)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		String loggedInProviderId = getLoggedInProviderId();
		ResourceBundle bundle = getResourceBundle();
		
		int idCounter = 0;

		List<MenuItemTo1> menulist = new ArrayList<>();

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DEMOGRAPHIC_READ))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter, "Details", Arrays.asList("record.details")));
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.ECHART_READ))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "Summary", Arrays.asList("record.summary")));
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.FORM_READ, Permission.EFORM_READ))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "Forms", Arrays.asList("record.forms", "record.forms.completed", "record.forms.revisions", "record.forms.deleted", "record.forms.add")));
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.TICKLER_READ))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "Tickler", Arrays.asList("record.tickler")));
		}
		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.MEASUREMENT_READ))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "Health Tracker", Arrays.asList("record.tracker")));
		}

		if (systemPreferenceService.isPreferenceEnabled(UserProperty.INTEGRATION_IMDHEALTH_ENABLED, false))
		{
			menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "Patient Education", Arrays.asList("record.patientEducation")));
		}

		//PHR
		if( ProviderMyOscarIdData.idIsSet(loggedInInfo.getLoggedInProviderNo())) {
			DemographicDao demographicDao=(DemographicDao)SpringUtils.getBean("demographicDao");
			Demographic demographic=demographicDao.getDemographic(""+demographicNo);
			
			if (demographic.getMyOscarUserName()==null ||demographic.getMyOscarUserName().equals("")) {		/*register link -myoscar (strikethrough) links to create account*/
				menulist.add(new MenuItemTo1(idCounter++, "PHR", "../phr/indivo/RegisterIndivo.jsp?demographicNo="+demographicNo));
			}else{
				menulist.add(MenuItemTo1.generateStateMenuItem(idCounter++, "PHR", Arrays.asList("record.phr")));
				
			}
			
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CONSULTATION_READ))
		{
			//add notification if patient has outstanding consultation requests (incomplete requests > 1 month)
			String outstanding = consultationManager.hasOutstandingConsultations(loggedInInfo, demographicNo)? "outstanding" : null;
			
			if (!consultationManager.isConsultRequestEnabled() && consultationManager.isConsultResponseEnabled()) {
				menulist.add(new MenuItemTo1(idCounter++,  demographicNo, bundle.getString("navbar.menu.consults"), Arrays.asList("record.consultResponses"), null));
			}
			else if (consultationManager.isConsultRequestEnabled() && consultationManager.isConsultResponseEnabled()) {
				MenuItemTo1 consultMenu = new MenuItemTo1(idCounter++, bundle.getString("navbar.menu.consults"), null, outstanding);
				consultMenu.setDropdown(true);
				
				List<MenuItemTo1> consultList = new ArrayList<MenuItemTo1>();
				consultList.add(new MenuItemTo1(idCounter++, demographicNo, bundle.getString("navbar.menu.consultRequests"), Arrays.asList("record.consultRequests"), outstanding));
				consultList.add(new MenuItemTo1(idCounter++, demographicNo, bundle.getString("navbar.menu.consultResponses"), Arrays.asList("record.consultResponses"), null));
				consultMenu.setDropdownItems(consultList);
				
				menulist.add(consultMenu);
			}
			else {
				menulist.add(new MenuItemTo1(idCounter++,  demographicNo, bundle.getString("navbar.menu.consults"), Arrays.asList("record.consultRequests"), outstanding));
			}
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.RX_READ))
		{
			menulist.add(new MenuItemTo1(idCounter++, "Rx", "../oscarRx/choosePatient.do?demographicNo="+demographicNo));
		}

		//END PHR
		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DX_READ))
		{
			menulist.add(new MenuItemTo1(idCounter++, "Disease Registry", "../oscarResearch/oscarDxResearch/setupDxResearch.do?quickList=&demographicNo="+demographicNo));
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.MESSAGE_READ))
		{
			menulist.add(new MenuItemTo1(idCounter++, "Create Message", "../oscarMessenger/SendDemoMessage.do?demographic_no="+demographicNo));
		}

		if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DOCUMENT_READ))
		{
			menulist.add(new MenuItemTo1(idCounter++, "Documents", "../dms/documentReport.jsp?function=demographic&doctype=lab&functionid="+demographicNo));
		}

		if (systemPreferenceService.isPreferenceEnabled(UserProperty.CARE_CONNECT_ENABLED, false))
		{
			menulist.add(new MenuItemTo1(idCounter++, "Care Connect", "../integration/careConnect/careConnectForm.jsp?demoNo=" + demographicNo));
		}

		// END OF MORE MENU

		return menulist;
		/*
		 * ADD A WAY TO CHECK IF THE USER HAS AUTHORIZATION to specific modules.
		 * 		  
	    | _newCasemgmt.allergies                      
		| _newCasemgmt.riskFactors                       
		| _newCasemgmt.calculators           
		| _newCasemgmt.templates             
		| _newCasemgmt.cpp                   
		 */
	}
	 
	//This will be more dynamic in the future but will 
	@GET
	@Path("/{demographicNo}/summary/{summaryName}") //@Path("/leftsideSummary")
	@Produces("application/json")
	public List<SummaryTo1> getSummary(
			@PathParam("demographicNo") Integer demographicNo,
			@PathParam("summaryName") String summaryName)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		String loggedInProviderId = getLoggedInProviderId();
		OscarProperties properties = OscarProperties.getInstance();

		logger.debug("getting summary:"+summaryName+" for demo "+demographicNo+"  loggedInInfo "+loggedInInfo);
		List<SummaryTo1> summaryList = null;
		int count = 0;

		if("right".equals(summaryName))
		{
			summaryList = new ArrayList<>();

			if (securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DOCUMENT_READ, Permission.LAB_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.INCOMING_POS))
			{
				SummaryTo1 labDocSummary = new SummaryTo1("Incoming", count++, SummaryTo1.INCOMING_CODE);
				labDocSummary.setDisplaySize(String.valueOf(LabsDocsSummary.DISPLAY_SIZE));
				summaryList.add(labDocSummary);
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DECISION_SUPPORT_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.DS_SUPPORT_POS))
			{
				summaryList.add(new SummaryTo1("Decision Support", count++, SummaryTo1.DECISIONSUPPORT_CODE));
			}
		}
		else if("left".equals(summaryName))
		{
			summaryList = new ArrayList<>();
			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.PREVENTION_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.PREVENTION_POS))
			{
				summaryList.add(new SummaryTo1("Preventions", count++, SummaryTo1.PREVENTIONS));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.RX_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.MEDS_POS))
			{
				summaryList.add(new SummaryTo1("Medications", count++, SummaryTo1.MEDICATIONS_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.OTHER_MEDS_POS))
			{
				summaryList.add(new SummaryTo1("Other Meds",count++,SummaryTo1.OTHERMEDS_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.ONGOING_POS))
			{
				summaryList.add(new SummaryTo1("Ongoing Concerns", count++, SummaryTo1.ONGOINGCONCERNS_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.DX_READ)
				&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.DISEASE_REGISTRY_POS))
			{
				summaryList.add(new SummaryTo1("Disease Registry", count++, SummaryTo1.DISEASE_REGISTRY_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.MED_HX_POS))
			{
				summaryList.add(new SummaryTo1("Medical History", count++, SummaryTo1.MEDICALHISTORY_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ))
			{
				summaryList.add(new SummaryTo1("Social History", count++, SummaryTo1.SOCIALHISTORY_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.FAM_HX_POS))
			{
				summaryList.add(new SummaryTo1("Family History", count++, SummaryTo1.FAMILYHISTORY_CODE));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.REMINDERS_POS))
			{
				summaryList.add(new SummaryTo1("Reminders", count++, SummaryTo1.REMINDERS_CODE));
			}

			if (securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.CPP_NOTE_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.RISK_FACTORS_POS))
			{
				summaryList.add(new SummaryTo1("Risk Factors", count++, SummaryTo1.RISK_FACTORS));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.ALLERGY_READ)
					&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.ALLERGIES_POS))
			{
				summaryList.add(new SummaryTo1("Allergies", count++, SummaryTo1.ALLERGIES));
			}

			if(securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.FORM_READ, Permission.EFORM_READ)
				&& preferenceManager.displaySummaryItem(loggedInInfo, PreferenceManager.ASSESSMENTS_POS))
			{
				summaryList.add(new SummaryTo1("Forms", count++, SummaryTo1.FORMS_CODE));
			}
			
			if(properties.isModuleEnabled(OscarProperties.Module.MODULE_HRM)
					&& securityInfoManager.hasPrivileges(loggedInProviderId, demographicNo, Permission.HRM_READ))
			{
				summaryList.add(new SummaryTo1("HRM Documents", count++, SummaryTo1.HRM_DOCUMENTS));
			}
		}
		return summaryList;
	}
	
	private static final Map<String, String> MY_MAP = createMap();

    private static Map<String, String> createMap() {
        Map<String, String> result = new HashMap<String, String>();

        result.put("preventions","preventionsSummary");
    	result.put("meds","rxSummary");
    	result.put("othermeds","issueNoteSummary");
        result.put("ongoingconcerns","ongoingConcernDxRegSummary");
        result.put("diseaseregistry", "diseaseRegistrySummary");
        result.put("medhx","issueNoteSummary"); 
		result.put("socfamhx","issueNoteSummary"); 		
		result.put("reminders","issueNoteSummary");
		result.put("forms","formsSummary");
		result.put("outgoing","formsSummary");	
		result.put("sochx","issueNoteSummary"); 
		result.put("famhx","issueNoteSummary"); 
		result.put("incoming","labsDocsSummary");
		result.put("dssupport","decisionSupportSummary");
		result.put("allergies","allergiesSummary");
		result.put("riskfactors","issueNoteSummary");
		result.put("hrmdocuments", "HRMDocumentSummary");
		
        return Collections.unmodifiableMap(result);
    }

	@GET
	@Path("/{demographicNo}/fullSummary/{summaryCode}")
	@Produces("application/json")
	public SummaryTo1 getFullSummmary(@PathParam("demographicNo") Integer demographicNo, @PathParam(value = "summaryCode") String summaryCode)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), demographicNo, Permission.ECHART_READ);

		Summary summaryInterface = (Summary) SpringUtils.getBean(MY_MAP.get(summaryCode));
		SummaryTo1 summary = summaryInterface.getSummary(loggedInInfo, demographicNo, summaryCode);

		logger.debug("outgoing summary object:" + summary);
		return summary;
	}

	@GET
	@Path("/{demographicNo}/getFamilyHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getFamilyHistory(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.FAMILYHISTORY_CODE);
	}
	
	@GET
	@Path("/{demographicNo}/getMedicalHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getMedicalHistory(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.MEDICALHISTORY_CODE);
	}

	@GET
	@Path("/{demographicNo}/getSocialHistory")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getSocialHistory(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.SOCIALHISTORY_CODE);
	}

	@GET
	@Path("/{demographicNo}/getOngoingConcerns")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getOngoingConcerns(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.ONGOINGCONCERNS_CODE);
	}

	@GET
	@Path("/{demographicNo}/getDiseaseRegistry")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getDiseaseRegistry(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.DISEASE_REGISTRY_CODE);
	}

	@GET
	@Path("/{demographicNo}/getOtherMeds")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getOtherMeds(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.OTHERMEDS_CODE);
	}

	@GET
	@Path("/{demographicNo}/getReminders")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getReminders(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.REMINDERS_CODE);
	}

	@GET
	@Path("/{demographicNo}/getRiskFactors")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getRiskFactors(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.RISK_FACTORS);
	}

	@GET
	@Path("/{demographicNo}/getAllergies")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getAllergies(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.ALLERGIES);
	}

	@GET
	@Path("/{demographicNo}/getPreventions")
	@Produces(MediaType.APPLICATION_JSON)
	public SummaryTo1 getPreventions(@PathParam("demographicNo") Integer demographicNo)
	{
		return getFullSummmary(demographicNo, SummaryTo1.PREVENTIONS);
	}

	boolean getBoolean(JSONObject jsonobject, String key)
	{
		if(jsonobject.containsKey(key))
		{
			return jsonobject.getBoolean(key);
		}
		return false;
	}

	Integer getInt(JSONObject jsonobject, String key)
	{
    	if(jsonobject.containsKey(key))
		{
			return (Integer) jsonobject.get(key);
		}
		return null;
	}
	
	String getString(JSONObject jsonobject,String key){
		if(jsonobject.containsKey(key)){
			return jsonobject.getString(key);
		}
		return null;
	}
	
	
	
	@GET
	@Path("/{demographicNo}/print")
	@Produces("application/pdf")
	@SkipContentLoggingOutbound
	public StreamingOutput print(@PathParam("demographicNo") Integer demographicNo,
								 @QueryParam("printOps") String jsonString,
								 @Context HttpServletRequest request)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), demographicNo, Permission.ECHART_READ);

		JSONObject jsonobject = JSONObject.fromObject(jsonString);

		final Integer demographicNof = demographicNo;
		boolean printAllNotesType = "all".equalsIgnoreCase(getString(jsonobject,"printType"));
		final boolean printDateRangeNotes = "dates".equalsIgnoreCase(getString(jsonobject,"printType"));
		
		Calendar startCal = null;
		Calendar endCal = null;
		if(printDateRangeNotes){
			if(jsonobject.containsKey("dates")){
				JSONObject datesJson = jsonobject.getJSONObject("dates");
				if(datesJson.containsKey("start")){
					startCal = javax.xml.bind.DatatypeConverter.parseDateTime(datesJson.getString("start"));
				}
				if(datesJson.containsKey("end")){
					endCal = javax.xml.bind.DatatypeConverter.parseDateTime(datesJson.getString("end"));
				}
			}
			if(startCal != null && endCal != null){
				printAllNotesType = true;
			}
		}
		final Calendar startCalf = startCal;
		final Calendar endCalf = endCal;
		
		final boolean printAllNotes = printAllNotesType; 
		
		final LoggedInInfo loggedInInfo = getLoggedInInfo();
		final boolean printCPP  = getBoolean(jsonobject,"cpp");
		final boolean printRx   = getBoolean(jsonobject,"rx");
		final boolean printLabs = getBoolean(jsonobject,"labs");
		final Integer siteSelected =  getInt(jsonobject, "selectedSite");
		
		final JSONArray keyArray = jsonobject.getJSONArray("selectedList");
		final String[] noteIds = new String[keyArray.size()];
		for(int i = 0; i < keyArray.size(); i++) {
			noteIds[i] = keyArray.getString(i);
		}

		return new StreamingOutput() {
			@Override
			public void write(java.io.OutputStream os)
					throws IOException, WebApplicationException {
				try{
					CaseManagementPrint cmp = new CaseManagementPrint();
					cmp.doPrint(loggedInInfo,demographicNof, printAllNotes,noteIds,printCPP,printRx,printLabs,siteSelected,startCalf,endCalf, request,  os);
		        }catch(Exception e){
		        		logger.error("error streaming",e);
		        }finally{
		        	IOUtils.closeQuietly(os);
		        }
				
			}  
	    };
	}
	
	@POST
	@Path("/searchTemplates")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public EncounterTemplateResponse getEncounterTemplates(
			JSONObject obj,
			@QueryParam("startIndex") Integer startIndex,
			@QueryParam("itemsToReturn") Integer itemsToReturn)
	{

		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ECHART_READ);

		String name = obj.getString("name");
		
		List<EncounterTemplate> et = encounterTemplateDao.findByName(name + "%", startIndex, itemsToReturn);
		
		List<EncounterTemplateTo1> transfers = new EncounterTemplateConverter().getAllAsTransferObjects(getLoggedInInfo(), et);
		
		EncounterTemplateResponse response = new EncounterTemplateResponse();
		response.setTemplates(transfers);
		
		return response;
	}
	
	@POST
	@Path("/template")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public EncounterTemplateResponse getEncounterTemplate(JSONObject obj)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ECHART_READ);

		String name = obj.getString("name");
		
		List<EncounterTemplate> et = new ArrayList<EncounterTemplate>();
		
		et.add(encounterTemplateDao.find(name));
		
		List<EncounterTemplateTo1> transfers = new EncounterTemplateConverter().getAllAsTransferObjects(getLoggedInInfo(), et);
		
		EncounterTemplateResponse response = new EncounterTemplateResponse();
		response.setTemplates(transfers);

		return response;
	}

	@GET
	@Path("/dashboards")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<DashboardTo1>> getDashboardList()
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ECHART_READ);

		ArrayList<DashboardTo1> dashboardTo1s = new ArrayList<>();
    	List<Dashboard> dashboards = dashboardDao.getDashboards();
    	for (Dashboard dashboard : dashboards)
			{
				dashboardTo1s.add(new DashboardTo1(dashboard));
			}
    	return RestResponse.successResponse(dashboardTo1s);
	}
	
}