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

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.app.AppOAuth1Config;
import org.oscarehr.app.OAuth1Utils;
import org.oscarehr.common.dao.AppDefinitionDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.eform.dao.EFormDao.EFormSortOrder;
import org.oscarehr.common.model.AppDefinition;
import org.oscarehr.common.model.AppUser;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.eform.model.EForm;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.common.model.EncounterForm;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.managers.FormsManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.conversion.EFormConverter;
import org.oscarehr.ws.rest.conversion.EncounterFormConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.model.EFormTo1;
import org.oscarehr.ws.rest.to.model.EncounterFormTo1;
import org.oscarehr.ws.rest.to.model.FormListTo1;
import org.oscarehr.ws.rest.to.model.FormTo1;
import org.oscarehr.ws.rest.to.model.MenuTo1;
import org.oscarehr.ws.rest.to.model.SummaryItemTo1;
import org.oscarehr.ws.rest.to.model.SummaryTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import oscar.eform.EFormExportZip;
import oscar.oscarEncounter.data.EctFormData;
import oscar.oscarProvider.data.ProviderMyOscarIdData;


/**
 * Service for interacting with forms (eforms and sql table forms in oscar) 
 */
@Path("/forms")
@Component("formsService")
@Tag(name = "Form")
@Transactional
public class FormsService extends AbstractServiceImpl {
	Logger logger = MiscUtils.getLogger();

	public enum FORM_TYPE
	{
		EFORM,
		FORM
	}
	
	@Autowired
	private FormsManager formsManager;
	
	@Autowired
	private AppDefinitionDao appDefinitionDao;

	@Autowired
	EFormDataService eFormDataService;

	@Autowired
	SecurityInfoManager securityInfoManager;
	
	@GET
	@Path("/{demographicNo}/all")
	@Produces("application/json")
	public FormListTo1 getFormsForHeading(@PathParam("demographicNo") Integer demographicNo ,@QueryParam("heading") String heading){
		FormListTo1 formListTo1 = new FormListTo1();
		if(heading.equals("Completed")){
			List<EFormData> completedEforms = formsManager.findInstancedByDemographicId(getLoggedInInfo(),demographicNo);
			Collections.sort(completedEforms, Collections.reverseOrder(EFormData.FORM_DATE_COMPARATOR));
			populateFormListTo1WithEForms(formListTo1, completedEforms, demographicNo);
		}
		else if (heading.equals("Revisions"))
		{
			List<EFormData> eformRevisions = formsManager.getEFormRevisionsInstances(demographicNo);
			Collections.sort(eformRevisions, Collections.reverseOrder(EFormData.FORM_DATE_COMPARATOR));
			populateFormListTo1WithEForms(formListTo1, eformRevisions, demographicNo);
		}
		else if (heading.equals("Deleted"))
		{
			List<EFormData> eformRevisions = formsManager.getDeletedEFormInstances(demographicNo);
			Collections.sort(eformRevisions, Collections.reverseOrder(EFormData.FORM_DATE_COMPARATOR));
			populateFormListTo1WithEForms(formListTo1, eformRevisions, demographicNo);
		}
		else if (heading.equals("Add")){  // Only two options right now.  Need to change this anyways
			List<EForm> eforms =  formsManager.findByStatus(getLoggedInInfo(),true, null);  //This will have to change to accommodate forms too.
			Collections.sort(eforms,EForm.FORM_NAME_COMPARATOR);
			for(EForm eform : eforms){
				int formId = eform.getId();
				String name = eform.getFormName();
				String subject = eform.getSubject();
				String status = null;
				Date date = eform.getFormDate();
				Boolean showLatestFormOnly = eform.isShowLatestFormOnly();
				formListTo1.add(FormTo1.create(null, demographicNo, formId, FormsManager.EFORM, name, subject, status, date, null, showLatestFormOnly));
			}
		}
		else
		{
			throw new NotImplementedException("Requested Heading is not implemented");
		}
		return formListTo1;
	}

	/**
	 * get all completed forms for the given demographic
	 * @param demographicNo - demographic
	 * @return - list of completed froms
	 */
	@GET
	@Path("/{demographicNo}/all/completed")
	@Produces("application/json")
	public FormListTo1 getAllCompletedForms(@PathParam("demographicNo") Integer demographicNo)
	{
		FormListTo1 formListTo1 = new FormListTo1();

		// get eforms
		List<EFormData> completedEforms = formsManager.findInstancedByDemographicId(getLoggedInInfo(),demographicNo);
		populateFormListTo1WithEForms(formListTo1, completedEforms, demographicNo);

		// get forms
		List<EctFormData.PatientForm> patientForms = formsManager.getCompletedEncounterForms(demographicNo.toString());
		populateFormListTo1WithPatientForms(formListTo1, patientForms, demographicNo);

		return formListTo1;
	}

	/**
	 * get all revisions of all forms for the given demographic
	 * @param demographicNo - demographic
	 * @return - list of form revisions
	 */
	@GET
	@Path("/{demographicNo}/all/revisions")
	@Produces("application/json")
	public FormListTo1 getAllFormRevisions(@PathParam("demographicNo") Integer demographicNo)
	{
		FormListTo1 formListTo1 = new FormListTo1();

		// get eforms
		List<EFormData> eformRevisions = formsManager.getEFormRevisionsInstances(demographicNo);
		populateFormListTo1WithEForms(formListTo1, eformRevisions, demographicNo);

		// get forms
		List<EctFormData.PatientForm> patientForms = formsManager.getEncounterFormRevisions(demographicNo.toString());
		populateFormListTo1WithPatientForms(formListTo1, patientForms, demographicNo);

		return formListTo1;
	}

	/**
	 * get all delete froms for the given demographic
	 * @param demographicNo - demographic number
	 * @return - deleted from list
	 */
	@GET
	@Path("/{demographicNo}/all/deleted")
	@Produces("application/json")
	public FormListTo1 getAllDeletedForms(@PathParam("demographicNo") Integer demographicNo)
	{
		FormListTo1 formListTo1 = new FormListTo1();

		List<EFormData> eformRevisions = formsManager.getDeletedEFormInstances(demographicNo);
		populateFormListTo1WithEForms(formListTo1, eformRevisions, demographicNo);

		return formListTo1;
	}

	/**
	 * get all forms available for addition to demographics.
	 * @return - all forms
	 */
	@GET
	@Path("/allForms")
	@Produces("application/json")
	public FormListTo1 getAllNewForms()
	{
		FormListTo1 formListTo1 = new FormListTo1();

		// get eforms
		List<EForm> eforms = formsManager.findByStatus(getLoggedInInfo(), true, null);
		for(EForm eform : eforms){
			int formId = eform.getId();
			formListTo1.add(FormTo1.create(null, 0, formId, FormsManager.EFORM,
					eform.getFormName(), eform.getSubject(), null, eform.getFormDate(), null, eform.isShowLatestFormOnly()));
		}

		// get forms
		List<EncounterForm> forms = formsManager.getAllEncounterForms();
		for(EncounterForm form : forms)
		{
			if (!form.isHidden())
			{
				formListTo1.add(FormTo1.create(null, 0, 0, FormsManager.FORM,
						form.getFormName(), form.getFormValue(), null, null, null, true));
			}
		}

		return formListTo1;
	}

	@GET
	@Path("/allEForms")
	@Produces("application/json")
	public AbstractSearchResponse<EFormTo1> getAllEFormNames(){
		AbstractSearchResponse<EFormTo1> response = new AbstractSearchResponse<EFormTo1>();
		response.setContent(new EFormConverter(true).getAllAsTransferObjects(getLoggedInInfo(),formsManager.findByStatus(getLoggedInInfo(), true, EFormSortOrder.NAME)));
		response.setTotal(response.getContent().size());
		return response;

	}
	
	@GET
	@Path("/allEncounterForms")
	@Produces("application/json")
	public AbstractSearchResponse<EncounterFormTo1> getAllFormNames(){
		AbstractSearchResponse<EncounterFormTo1> response = new AbstractSearchResponse<EncounterFormTo1>();
		response.setContent(new EncounterFormConverter().getAllAsTransferObjects(getLoggedInInfo(),formsManager.getAllEncounterForms()));
		response.setTotal(response.getContent().size());
		return response;
		
	}
	
	@GET
	@Path("/selectedEncounterForms")
	@Produces("application/json")
	public AbstractSearchResponse<EncounterFormTo1> getSelectedFormNames(){
		AbstractSearchResponse<EncounterFormTo1> response = new AbstractSearchResponse<EncounterFormTo1>();
		response.setContent(new EncounterFormConverter().getAllAsTransferObjects(getLoggedInInfo(),formsManager.getSelectedEncounterForms()));
		response.setTotal(response.getContent().size());
		return response;

	}

	@PUT
	@Path("/delete/{id}")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<Boolean> deleteForm(@PathParam("id") Integer id, @QueryParam("type") String type)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(), "W", null, "_eform");

		FORM_TYPE fType = FORM_TYPE.valueOf(type.toUpperCase());
		if (fType == FORM_TYPE.EFORM)
		{
			eFormDataService.deleteEForm(id);
		}
		else if (fType == FORM_TYPE.FORM)
		{
			throw new NotImplementedException("not implemented yet");
		}

		return RestResponse.successResponse(true);
	}

	@PUT
	@Path("/restore/{id}")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<Boolean> restoreForm(@PathParam("id") Integer id, @QueryParam("type") String type)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInInfo().getLoggedInProviderNo(), "W", null, "_eform");

		FORM_TYPE fType = FORM_TYPE.valueOf(type.toUpperCase());
		if (fType == FORM_TYPE.EFORM)
		{
			eFormDataService.restoreEForm(id);
		}
		else if (fType == FORM_TYPE.FORM)
		{
			throw new NotImplementedException("not implemented yet");
		}

		return RestResponse.successResponse(true);
	}
	
		
	@GET
	@Path("/{demographicNo}/completedEncounterForms")
	@Produces("application/json")
	public RestResponse<List<FormTo1>> getCompletedFormNames(@PathParam("demographicNo") String demographicNo){
		List<FormTo1> formList = new ArrayList<FormTo1>();

		List<EncounterForm> encounterForms = formsManager.getAllEncounterForms();
		Collections.sort(encounterForms, EncounterForm.BC_FIRST_COMPARATOR);

		for (EncounterForm encounterForm : encounterForms) {
			String table = StringUtils.trimToNull(encounterForm.getFormTable());
			if (table != null) {
			
				EctFormData.PatientForm[] pforms = EctFormData.getPatientFormsFromLocalAndRemote(getLoggedInInfo(), demographicNo, table);
				int formId = 0;
				String name = encounterForm.getFormName();
				
				if (pforms.length > 0) {
				
					EctFormData.PatientForm pfrm = pforms[0];
					formId = Integer.parseInt(pfrm.getFormId());
					Date date;
					
					//d-MMM-y
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
					String dateStr = pfrm.getCreated();
					try {
						date = formatter.parse(dateStr);
					} catch (ParseException ex) {
						date = null;
					}

					formList.add(FormTo1.create(null, Integer.parseInt(demographicNo), formId, FormsManager.FORM, name, null, null, date, null,false ));

				}

			}
		}

		return RestResponse.successResponse(formList);
	}
	
	@GET
	@Path("/groupNames")
	@Produces("application/json")
	public AbstractSearchResponse<String> getGroupNames(){
		AbstractSearchResponse<String> response = new AbstractSearchResponse<String>();

		response.setContent(formsManager.getGroupNames());
		response.setTotal(response.getContent().size());
		return response;

	}
	
	@GET
	@Path("/getFavouriteFormGroup")
	@Produces("application/json")
	public SummaryTo1 getFavouriteFormGroups(){
		UserPropertyDAO userPropertyDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
		String groupName = userPropertyDao.getStringValue(getLoggedInInfo().getLoggedInProviderNo(),"favourite_eform_group");
		logger.debug("favourite eform group name "+groupName);
		if(groupName == null) return null;
		List<EForm> eforms = formsManager.getEfromInGroupByGroupName(getLoggedInInfo(),  groupName);
		SummaryTo1 formSummary = new SummaryTo1(groupName,0,null); 
		List<SummaryItemTo1> summaryItems = formSummary.getSummaryItem();
		for(EForm eform: eforms){
			SummaryItemTo1 summaryItem = new SummaryItemTo1(eform.getId(), eform.getFormName(),"record.forms.new","eform");
			summaryItems.add(summaryItem);
		}
		return formSummary;
	}
	
	
	@GET
	@Path("/getFormGroups")
	@Produces("application/json")
	public List<SummaryTo1> getGroupsWithForms(){
		int count = 0;
		List<SummaryTo1> summaryList = new ArrayList<SummaryTo1>();
		List<String> groupNames = formsManager.getGroupNames();
		
		if(groupNames != null){
			Collections.sort(groupNames);
			for(String groupName:groupNames){
				SummaryTo1 formSummary = new SummaryTo1(groupName,count++,null); 
				summaryList.add(formSummary);
				List<EForm> eforms = formsManager.getEfromInGroupByGroupName(getLoggedInInfo(),  groupName);
				List<SummaryItemTo1> summaryItems = formSummary.getSummaryItem();
				for(EForm eform: eforms){
					SummaryItemTo1 summaryItem = new SummaryItemTo1(eform.getId(), eform.getFormName(),"record.forms.new","eform");
					summaryItems.add(summaryItem);
				}
			}
		}
		return summaryList;
	}
	
	@POST
	@Path("/getK2AEForm")
	@Consumes("application/json")
	@Produces("application/json")
	public AbstractSearchResponse<String> getK2AEForm(String id) {
		AbstractSearchResponse<String> response = new AbstractSearchResponse<String>();
		MiscUtils.getLogger().info("EForm id is: " + id);
		
        try {
        	AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
        	AppOAuth1Config appAuthConfig = AppOAuth1Config.fromDocument(k2aApp.getConfig());
		    URL url = new URL(appAuthConfig.getBaseURL() + "/download/eform/" + id);
		    url.openStream();
		    EFormExportZip eFormExportZip = new EFormExportZip();
		    List<String> errors = eFormExportZip.importForm(url.openStream());
		    if(errors != null) {
			    response.setContent(errors);
			}
			response.setTotal(1);
        } catch (Exception e) {
        	MiscUtils.getLogger().error("Error parsing data - " + e);
	        return null;
        }
        return response;
	}
	
	@POST
	@Path("/getAllK2AEForms")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public AbstractSearchResponse<String> getAllK2AEForms(String jsonString) {
		try {
			AbstractSearchResponse<String> response = new AbstractSearchResponse<String>();
			JSONArray jsonArray = JSONArray.fromObject(jsonString);
			List<String> errors = new ArrayList<String>();
			int totalEFormsProcessed = 0;
			
			AppDefinition k2aApp = appDefinitionDao.findByName("K2A");
			AppOAuth1Config appAuthConfig = AppOAuth1Config.fromDocument(k2aApp.getConfig());
			
	        for (int i = 0; i < jsonArray.size(); i++) {
	        		JSONObject eform = jsonArray.getJSONObject(i);
				    URL url = new URL(appAuthConfig.getBaseURL() + "/download/eform/" + eform.getString("id"));
			        
					url.openStream();
					EFormExportZip eFormExportZip = new EFormExportZip();
					List<String> eformErrors = eFormExportZip.importForm(url.openStream());
					if(eformErrors != null) {
					    errors.add("failed to upload eform: " + eform.getString("name") + ", eform may already exist in OSCAR.");
					}
					totalEFormsProcessed++;
	        }
			response.setTotal(totalEFormsProcessed);
	        response.setContent(errors);
	        return response;
		} catch (Exception e) {
            MiscUtils.getLogger().error("Error parsing data - " + e);
    	    return null;
        }
	}
	
	@GET
	@Path("/{demographicNo}/formOptions")
	@Produces("application/json")
	public MenuTo1 getFormOptions(@PathParam("demographicNo") String demographicNo){
		ResourceBundle bundle = getResourceBundle();
		MenuTo1 formMenu = new MenuTo1();
		int idCounter =0;

		//formMenu.add(idCounter++, bundle.getString("global.saveAsPDF"), "PDF", "URL"); 
		if( ProviderMyOscarIdData.idIsSet(getLoggedInInfo().getLoggedInProviderNo())) {
			DemographicDao demographicDao=(DemographicDao)SpringUtils.getBean("demographicDao");
			Demographic demographic=demographicDao.getDemographic(""+demographicNo);
			if (demographic.getMyOscarUserName()!=null && !demographic.getMyOscarUserName().equals("")) {		/*register link -myoscar (strikethrough) links to create account*/
				formMenu.add(idCounter++, bundle.getString("global.send2PHR"), "send2PHR", "url");
			}
		}
		return formMenu;
	}
	
	public static String getK2AEFormsList(LoggedInInfo loggedInInfo, AppDefinition k2aApp, AppUser k2aUser) {
		try {
			String requestURI = "/ws/api/eforms/getEForms";
			String retval = OAuth1Utils.getOAuthGetResponse( loggedInInfo,k2aApp, k2aUser, requestURI, requestURI);
			return retval;
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * populate a FormListTo1 with EFormData
	 * @param formList - the form list to populate
	 * @param eforms - the eform data to put in the list
	 * @param demographicNo - demographic
	 * @return - a populated FormListTo1
	 */
	private FormListTo1 populateFormListTo1WithEForms(FormListTo1 formList, List<EFormData> eforms, Integer demographicNo)
	{
		for(EFormData eformData: eforms)
		{
			int id = eformData.getId();
			int formId = eformData.getFormId();
			String name = eformData.getFormName();
			String subject = eformData.getSubject();
			String status = eformData.getSubject();
			Date date = eformData.getFormDate();
			Date createDate = null;
			if (eformData.getEFormInstance() != null)
			{
				createDate = eformData.getEFormInstance().getCreatedAt();
			}
			Boolean showLatestFormOnly = eformData.isShowLatestFormOnly();
			formList.add(FormTo1.create(id, demographicNo, formId, FormsManager.EFORM, name, subject,
					status, date, createDate, showLatestFormOnly));
		}

		return formList;
	}

	/**
	 * populate a FormListTo1 with PatientForm data
	 * @param formList - form list to populate
	 * @param patientForms - patient forms to use for population
	 * @param demographicNo - demographic
	 * @return - a filled in FormListTo1
	 */
	private FormListTo1 populateFormListTo1WithPatientForms(FormListTo1 formList, List<EctFormData.PatientForm> patientForms, Integer demographicNo)
	{
		for (EctFormData.PatientForm form : patientForms)
		{
			formList.add(FormTo1.create(Integer.parseInt(form.getFormId()), demographicNo, 0, FormsManager.FORM, form.getFormName(), null, null,
					form.edited, form.created,false ));
		}

		return formList;
	}
}
