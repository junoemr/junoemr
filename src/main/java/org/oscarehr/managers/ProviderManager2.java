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

package org.oscarehr.managers;

import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.casemgmt.model.ProviderExt;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.dao.ProviderExtDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.model.Property;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.common.model.ProviderPreference.QuickLink;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.model.ProviderSettings;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.log.LogAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProviderManager2 {
	private static Logger logger=MiscUtils.getLogger();
	
	
	@Autowired
	private ProviderDao providerDao;

	@Autowired
	private PropertyDao propertyDao;
	
	@Autowired
	private ProviderPreferenceDao providerPreferenceDao;

	@Autowired
	private ProviderExtDao providerExtDao;
	
	public List<Provider> getProviders(LoggedInInfo loggedInInfo, Boolean active) {
		List<Provider> results = null;

		if (active == null) results = providerDao.getProviders();
		else results = providerDao.getProviders(active);

		//--- log action ---
		LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProviders", "active=" + active);

		return (results);
	}

	public Provider getProvider(LoggedInInfo loggedInInfo, String providerNo) {

		Provider result = providerDao.getProvider(providerNo);

		//--- log action ---
		LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProvider", "providerNo=" + providerNo);

		return (result);
	}

	public List<Provider> getProvidersByIds(LoggedInInfo loggedInInfo, List<String> ids) {
		List<Provider> results = new ArrayList<Provider>();
		for(String id:ids) {
			results.add(getProvider(loggedInInfo, id));
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProviders", "providerNo=" + id);
		}
		return results;
	}

	public List<Property> getProviderProperties(LoggedInInfo loggedInInfo, String providerNo, String propertyName)
	{
		List<Property> results=propertyDao.findByNameAndProvider(propertyName, providerNo);
		
		//--- log action ---
		LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProviderProperties", "providerNo=" + providerNo+", propertyName="+propertyName);
		
		return(results);
	}
	
	/*
	 * Format is LastName[,FirstName]
	 */
	public List<Provider> searchProviderByNames(LoggedInInfo loggedInInfo, String searchString, int startIndex, int itemsToReturn) {
		
		List<Provider> results = providerDao.searchProviderByNamesString(searchString, startIndex, itemsToReturn);
		
		if(logger.isDebugEnabled()) {
			logger.debug("searchProviderByNames, searchString="+searchString+", result.size="+results.size());
		}
		
		//--- log action ---
		for (Provider provider : results) {
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.searchProviderByNames result", "provideRNo=" + provider.getProviderNo());
		}

		return (results);
	}
	
	public List<Provider> search(LoggedInInfo loggedInInfo, String term, boolean active, int startIndex, int itemsToReturn) {
		
		List<Provider> results = providerDao.search(term, active, startIndex, itemsToReturn);
		
		if(logger.isDebugEnabled()) {
			logger.debug("search, active="+active+", term="+term+" result.size="+results.size());
		}
		
		//--- log action --- this seems useless
		for (Provider provider : results) {
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.search result", "providerNo=" + provider.getProviderNo());
		}

		return (results);
	}
	
	public List<String> getActiveTeams(LoggedInInfo loggedInInfo) {
		return providerDao.getActiveTeams();
	}
	
	public ProviderSettings getProviderSettings(String providerNo)
	{
		ProviderSettings settings = new ProviderSettings();
	
		ProviderPreference pp = providerPreferenceDao.find(providerNo);
		if(pp == null ) {
			pp = new ProviderPreference();
		}
		
		ProviderExt providerExt = providerExtDao.find(providerNo);
		if(providerExt == null) {
			providerExt = new ProviderExt();
		}
		
		Map<String,Property> map = new HashMap<String,Property>();
		for(Property prop:propertyDao.findByProvider(providerNo)) {
			map.put(prop.getName(), prop);
		}
		
		if(map.get("recentPatients") != null) {
			settings.setRecentPatients(map.get("recentPatients").getValue());
		}
		
		if(map.get("rxAddress") != null) {
			settings.setRxAddress(map.get("rxAddress").getValue());
		}
		if(map.get("rxCity") != null) {
			settings.setRxCity(map.get("rxCity").getValue());
		}
		if(map.get("rxProvince") != null) {
			settings.setRxProvince(map.get("rxProvince").getValue());
		}
		if(map.get("rxPostal") != null) {
			settings.setRxPostal(map.get("rxPostal").getValue());
		}
		if(map.get("rxPhone") != null) {
			settings.setRxPhone(map.get("rxPhone").getValue());
		}
		if(map.get("faxnumber") != null) {
			settings.setFaxNumber(map.get("faxnumber").getValue());
		}
		
		if(map.get("workload_management") != null) {
			settings.setWorkloadManagement(map.get("workload_management").getValue());
		}
		
		if(map.get("provider_for_tickler_warning") != null) {
			settings.setTicklerWarningProvider(map.get("provider_for_tickler_warning").getValue());
		}
		
		if(map.get("rx_use_rx3") != null) {
			settings.setUseRx3("yes".equals(map.get("rx_use_rx3").getValue())?true:false);
		}
		
		if(map.get("rx_show_patient_dob") != null) {
			settings.setShowPatientDob("yes".equals(map.get("rx_show_patient_dob").getValue())?true:false);
		}
		
		if(map.get("rx_default_quantity") != null) {
			settings.setRxDefaultQuantity(map.get("rx_default_quantity").getValue());
		}
		if(map.get("rx_page_size") != null) {
			settings.setRxPageSize(map.get("rx_page_size").getValue());
		}
		if(map.get("rxInteractionWarningLevel") != null) {
			settings.setRxInteractionWarningLevel(map.get("rxInteractionWarningLevel").getValue());
		}
		if(map.get("HC_Type") != null) {
			settings.setDefaultHcType(map.get("HC_Type").getValue());
		}
		if(map.get("default_sex") != null) {
			settings.setDefaultSex(map.get("default_sex").getValue());
		}
		
		if(map.get("consultation_time_period_warning") != null) {
			settings.setConsultationTimePeriodWarning(map.get("consultation_time_period_warning").getValue());
		}
		if(map.get("consultation_team_warning") != null) {
			settings.setConsultationTeamWarning(map.get("consultation_team_warning").getValue());
		}
		if(map.get("consultation_req_paste_fmt") != null) {
			settings.setConsultationPasteFormat(map.get("consultation_req_paste_fmt").getValue());
		}
		if(map.get("consultation_letterheadname_default") != null) {
			settings.setConsultationLetterHeadNameDefault(map.get("consultation_letterheadname_default").getValue());
		}
		if(map.get("edoc_browser_in_document_report") != null) {
			settings.setDocumentBrowserInDocumentReport("yes".equals(map.get("edoc_browser_in_document_report").getValue())?true:false);
		}
		if(map.get("edoc_browser_in_master_file") != null) {
			settings.setDocumentBrowserInMasterFile("yes".equals(map.get("edoc_browser_in_master_file").getValue())?true:false);
		}
		
		if(map.get("cpp_single_line") != null) {
			settings.setCppSingleLine("yes".equals(map.get("cpp_single_line").getValue())?true:false);
		}

		if(map.get(UserProperty.SCHEDULE_SITE) != null)
		{
			settings.setSiteSelected(map.get(UserProperty.SCHEDULE_SITE).getValue());
		}
		
		//custom summary display
		//NEW
		/*
		if(map.get("summary.item.custom.display") != null) {
			settings.setSummaryItemCustomDisplay("on".equals(map.get("summary.item.custom.display").getValue())?true:false);
		}
		*/
		
		//OLD
		//cpp.pref.enable - use for now so changes in the new to hide or display cpp items reflect in the encounter as well as new ui
		//if this =on then enabled other wise disabled
		 if(map.get("cpp.pref.enable") != null) {
			settings.setSummaryItemCustomDisplay("on".equals(map.get("cpp.pref.enable").getValue())?true:false);
		 }
		

		 //.position exists -> value blank = disable  --in the old ui if cpp.pref.enabled is "on" then changing the position to "None" sets the value to "" hides the CPP item
		 //.position exists -> value off = disable 
		 //except for cpp.pref.enable if the absence of the property in the db the UI should display "true" or "Enable" for .position.
			if(map.get(PreferenceManager.ONGOING_POS) != null) {
				settings.setCppDisplayOngoingConcerns("on".equals(map.get(PreferenceManager.ONGOING_POS).getValue())?true:false);
			}else if(map.get(PreferenceManager.OLD_ONGOING_CONCERNS_POS) != null) {
				settings.setCppDisplayOngoingConcerns(!"".equals(map.get(PreferenceManager.OLD_ONGOING_CONCERNS_POS).getValue())?true:false);
			}else{
				settings.setCppDisplayOngoingConcerns(true);
			}
			if(map.get(PreferenceManager.ONGOING_START_DATE) != null) {
				settings.setCppOngoingConcernsStartDate("on".equals(map.get(PreferenceManager.ONGOING_START_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.ONGOING_RES_DATE) != null) {
				settings.setCppOngoingConcernsResDate("on".equals(map.get(PreferenceManager.ONGOING_RES_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.ONGOING_PROBLEM_STATUS) != null) {
				settings.setCppOngoingConcernsProblemStatus("on".equals(map.get(PreferenceManager.ONGOING_PROBLEM_STATUS).getValue())?true:false);
			}
			
			/*
			 * 
			 * if .position dosen't exist = on
			 * if .position exists and not equal "" = on
			 * ignore the R* position eg: R1I1 R1I2 R2I1 R2I2
			 * 
			 * so setting position in old ui will have no effect on the new ui
			 * and when setting position in new ui it will have no effect on the old ui
			 *  
			 */		
			if(map.get(PreferenceManager.MED_HX_POS) != null) {
				settings.setCppDisplayMedHx("on".equals(map.get(PreferenceManager.MED_HX_POS).getValue())?true:false);
			}else if(map.get(PreferenceManager.OLD_MEDICAL_HISTORY_POS) != null) {
				settings.setCppDisplayMedHx(!"".equals(map.get(PreferenceManager.OLD_MEDICAL_HISTORY_POS).getValue())?true:false);
			}else{
				settings.setCppDisplayMedHx(true);
			}
			if(map.get(PreferenceManager.MED_HX_START_DATE) != null) {
				settings.setCppMedHxStartDate("on".equals(map.get(PreferenceManager.MED_HX_START_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.MED_HX_RES_DATE) != null) {
				settings.setCppMedHxResDate("on".equals(map.get(PreferenceManager.MED_HX_RES_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.MED_HX_TREATMENT) != null) {
				settings.setCppMedHxTreatment("on".equals(map.get(PreferenceManager.MED_HX_TREATMENT).getValue())?true:false);
			}
			if(map.get(PreferenceManager.MED_HX_PROCEDURE_DATE) != null) {
				settings.setCppMedHxProcedureDate("on".equals(map.get(PreferenceManager.MED_HX_PROCEDURE_DATE).getValue())?true:false);
			}
			
			if(map.get(PreferenceManager.SOC_HX_POS) != null) {
				settings.setCppDisplaySocialHx("on".equals(map.get(PreferenceManager.SOC_HX_POS).getValue())?true:false);
			}else if(map.get(PreferenceManager.OLD_SOCIAL_HISTORY_POS) != null){
				settings.setCppDisplaySocialHx(!"".equals(map.get(PreferenceManager.OLD_SOCIAL_HISTORY_POS).getValue())?true:false);
			}else{	
				settings.setCppDisplaySocialHx(true);
			}
			
			if(map.get(PreferenceManager.SOC_HX_START_DATE) != null) {
				settings.setCppSocialHxStartDate("on".equals(map.get(PreferenceManager.SOC_HX_START_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.SOC_HX_RES_DATE) != null) {
				settings.setCppSocialHxResDate("on".equals(map.get(PreferenceManager.SOC_HX_RES_DATE).getValue())?true:false);
			}

			if(map.get(PreferenceManager.REMINDERS_POS) != null) {
				settings.setCppDisplayReminders("on".equals(map.get(PreferenceManager.REMINDERS_POS).getValue())?true:false);
			}else if(map.get(PreferenceManager.OLD_REMINDERS_POS) != null) {
				settings.setCppDisplayReminders(!"".equals(map.get(PreferenceManager.OLD_REMINDERS_POS).getValue())?true:false);
			}else{
				settings.setCppDisplayReminders(true);
			}
			if(map.get(PreferenceManager.REMINDERS_START_DATE) != null) {
				settings.setCppRemindersStartDate("on".equals(map.get(PreferenceManager.REMINDERS_START_DATE).getValue())?true:false);
			}
			if(map.get(PreferenceManager.REMINDERS_RES_DATE) != null) {
				settings.setCppRemindersResDate("on".equals(map.get(PreferenceManager.REMINDERS_RES_DATE).getValue())?true:false);
			}
			
			if(map.get(PreferenceManager.PREVENTION_POS) != null) {
				settings.setSummaryItemDisplayPreventions("on".equals(map.get(PreferenceManager.PREVENTION_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayPreventions(true);
			}
			if(map.get(PreferenceManager.FAM_HX_POS) != null) {
				settings.setSummaryItemDisplayFamHx("on".equals(map.get(PreferenceManager.FAM_HX_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayFamHx(true);
			}
			if(map.get(PreferenceManager.RISK_FACTORS_POS) != null) {
				settings.setSummaryItemDisplayRiskFactors("on".equals(map.get(PreferenceManager.RISK_FACTORS_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayRiskFactors(true);
			}
			if(map.get(PreferenceManager.ALLERGIES_POS) != null) {
				settings.setSummaryItemDisplayAllergies("on".equals(map.get(PreferenceManager.ALLERGIES_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayAllergies(true);
			}
			
			if(map.get(PreferenceManager.MEDS_POS) != null) {
				settings.setSummaryItemDisplayMeds("on".equals(map.get(PreferenceManager.MEDS_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayMeds(true);
			}
			if(map.get(PreferenceManager.OTHER_MEDS_POS) != null) {
				settings.setSummaryItemDisplayOtherMeds("on".equals(map.get(PreferenceManager.OTHER_MEDS_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayOtherMeds(true);
			}
			if(map.get(PreferenceManager.ASSESSMENTS_POS) != null) {
				settings.setSummaryItemDisplayAssessments("on".equals(map.get(PreferenceManager.ASSESSMENTS_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayAssessments(true);
			}
						
			if(map.get(PreferenceManager.INCOMING_POS) != null) {
				settings.setSummaryItemDisplayIncoming("on".equals(map.get(PreferenceManager.INCOMING_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayIncoming(true);
			}
			
			if(map.get(PreferenceManager.DS_SUPPORT_POS) != null) {
				settings.setSummaryItemDisplayDsSupport("on".equals(map.get(PreferenceManager.DS_SUPPORT_POS).getValue())?true:false);
			}else{
				settings.setSummaryItemDisplayDsSupport(false);
			}

		if(map.get("cme_note_date") != null) {
			settings.setCmeNoteDate(map.get("cme_note_date").getValue());
		}

		if(map.get("cme_note_format") != null) {
			settings.setCmeNoteFormat("yes".equals(map.get("cme_note_format").getValue())?true:false);
		}
		
		if(map.get("quickChartsize") != null) {
			settings.setQuickChartSize(map.get("quickChartsize").getValue());
		}
		
		if(map.get("encounterWindowWidth") != null) {
			settings.setEncounterWindowWidth(map.get("encounterWindowWidth").getValue());
		}
		if(map.get("encounterWindowHeight") != null) {
			settings.setEncounterWindowHeight(map.get("encounterWindowHeight").getValue());
		}
		if(map.get("encounterWindowMaximize") != null) {
			settings.setEncounterWindowMaximize("yes".equals(map.get("encounterWindowMaximize").getValue())?true:false);
		}
		if(map.get("favourite_eform_group") != null) {
			settings.setFavoriteFormGroup(map.get("favourite_eform_group").getValue());
		}
		
		if(map.get("lab_ack_comment") != null) {
			settings.setDisableCommentOnAck("yes".equals(map.get("lab_ack_comment").getValue())?true:false);
		}
		
		if(map.get("olis_reportingLab") != null) {
			settings.setOlisDefaultReportingLab(map.get("olis_reportingLab").getValue());
		}
		if(map.get("olis_exreportingLab") != null) {
			settings.setOlisDefaultExcludeReportingLab(map.get("olis_exreportingLab").getValue());
		}
		
		if(map.get("mydrugref_id") != null) {
			settings.setMyDrugRefId(map.get("mydrugref_id").getValue());
		}
		
		if(map.get("cobalt") != null) {
			settings.setUseCobaltOnLogin("yes".equals(map.get("cobalt").getValue())?true:false);
		}
		
		if(map.get("use_mymeds") != null) {
			settings.setUseMyMeds(Boolean.valueOf(map.get("use_mymeds").getValue()));
		}
		
		if(map.get("disable_born_prompts") != null) {
			settings.setDisableBornPrompts("Y".equals(map.get("disable_born_prompts").getValue()));
		}
		
		if(map.get("hide_old_echart_link_in_appointment") != null) {
			settings.setHideOldEchartLinkInAppointment("Y".equals(map.get("hide_old_echart_link_in_appointment").getValue()));
		}
		/*
		if(settings.getFavoriteFormGroup()==null) {
			settings.setFavoriteFormGroup("");
		}
		*/
		
		settings.setNewTicklerWarningWindow(pp.getNewTicklerWarningWindow());
		
		settings.setStartHour(pp.getStartHour());
		settings.setEndHour(pp.getEndHour());
		settings.setPeriod(pp.getEveryMin());
		settings.setGroupNo(pp.getMyGroupNo());
		settings.setAppointmentScreenLinkNameDisplayLength(pp.getAppointmentScreenLinkNameDisplayLength());
		
		if(pp.getAppointmentScreenForms() != null) {
			settings.setAppointmentScreenForms(pp.getAppointmentScreenForms());
		}
		
		if(pp.getAppointmentScreenEForms() != null) {
			settings.setAppointmentScreenEforms(pp.getAppointmentScreenEForms());
		}
		
		if(pp.getAppointmentScreenQuickLinks() != null) {
			for(QuickLink ql:pp.getAppointmentScreenQuickLinks()) {
				org.oscarehr.managers.model.QuickLink qt = new org.oscarehr.managers.model.QuickLink();
				qt.setName(ql.getName());
				qt.setUrl(ql.getUrl());
				settings.getAppointmentScreenQuickLinks().add(qt);
			}
		}
	
		settings.setDefaultServiceType(pp.getDefaultServiceType());
		settings.setDefaultDxCode(pp.getDefaultDxCode());
		
		settings.setDefaultDoNotDeleteBilling(pp.getDefaultDoNotDeleteBilling()==1?true:false);
		
		settings.setPrintQrCodeOnPrescription(pp.isPrintQrCodeOnPrescriptions());
		settings.seteRxEnabled(pp.isERxEnabled());
		settings.seteRxTrainingMode(pp.isERxTrainingMode());
		settings.seteRxFacility(pp.getERxFacility());
		settings.seteRxURL(pp.getERx_SSO_URL());
		settings.seteRxUsername(pp.getERxUsername());
		settings.seteRxPassword(pp.getERxPassword());
		
		settings.setDefaultPmm("enabled".equals(pp.getDefaultCaisiPmm()));
		
		//from ProviderExt
		settings.setSignature(providerExt.getSignature());
		
		
		return settings;
	}
	
	private Property getMappedOrNewProperty(Map<String,Property> map, String key, String providerNo) {
		if(map.get(key) != null) {
			return map.get(key);
		} else {
			Property p = new Property();
			p.setProviderNo(providerNo);
			p.setName(key);
			map.put(p.getName(), p);
			
			return p;
		}
	}
	
	public void updateProviderSettings(LoggedInInfo loggedInInfo, String providerNo, ProviderSettings settings) {
		
		ProviderPreference pp = providerPreferenceDao.find(providerNo);
		if(pp == null ) {
			pp = new ProviderPreference();
		}
		
		ProviderExt providerExt = providerExtDao.find(providerNo);
		if(providerExt == null) {
			providerExt = new ProviderExt();
		}
		
		List<Property> props = propertyDao.findByProvider(providerNo);
		
		pp.setNewTicklerWarningWindow(settings.getNewTicklerWarningWindow());
		pp.setStartHour(settings.getStartHour());
		pp.setEndHour(settings.getEndHour());
		pp.setEveryMin(settings.getPeriod());
		pp.setMyGroupNo(settings.getGroupNo());
		pp.setAppointmentScreenLinkNameDisplayLength(settings.getAppointmentScreenLinkNameDisplayLength());
		
		pp.getAppointmentScreenForms().clear();
		for(String formName : settings.getAppointmentScreenForms()) {
			pp.getAppointmentScreenForms().add(formName);
		}
		
		pp.getAppointmentScreenEForms().clear();
		for(Integer eformId : settings.getAppointmentScreenEforms()) {
			pp.getAppointmentScreenEForms().add(eformId);
		}
		
		pp.getAppointmentScreenQuickLinks().clear();
		for(org.oscarehr.managers.model.QuickLink ql : settings.getAppointmentScreenQuickLinks()) {
			pp.getAppointmentScreenQuickLinks().add(new QuickLink(ql.getName(),ql.getUrl()));
		}
		
		pp.setDefaultDxCode(settings.getDefaultDxCode());
		pp.setDefaultServiceType(settings.getDefaultServiceType()==null||settings.getDefaultServiceType().isEmpty()?null:settings.getDefaultServiceType());
		
		pp.setDefaultDoNotDeleteBilling(settings.isDefaultDoNotDeleteBilling()?1:0);
		
		pp.setPrintQrCodeOnPrescriptions(settings.isPrintQrCodeOnPrescription());
		pp.setERxEnabled(settings.iseRxEnabled());
		
		pp.setERxTrainingMode(settings.iseRxTrainingMode());
		pp.setERxFacility(settings.geteRxFacility());
		pp.setERx_SSO_URL(settings.geteRxURL());
		pp.setERxUsername(settings.geteRxUsername());
		pp.setERxPassword(settings.geteRxPassword());
		
		pp.setDefaultCaisiPmm(settings.isDefaultPmm()?"enabled":"disabled");
		pp.setProviderNo(providerNo);
		
		
		providerPreferenceDao.merge(pp);
		
		Map<String,Property> map = new HashMap<String,Property>();
		for(Property prop:props) {
			map.put(prop.getName(), prop);
		}
		
		Property p = null;
		
		p = getMappedOrNewProperty(map, "recentPatients", providerNo);
		p.setValue(settings.getRecentPatients());
		p = getMappedOrNewProperty(map, "rxAddress", providerNo);
		p.setValueNoNull(settings.getRxAddress());
		p = getMappedOrNewProperty(map, "rxCity", providerNo);
		p.setValueNoNull(settings.getRxCity());
		p = getMappedOrNewProperty(map, "rxProvince", providerNo);
		p.setValueNoNull(settings.getRxProvince());
		p = getMappedOrNewProperty(map, "rxPostal", providerNo);
		p.setValueNoNull(settings.getRxPostal());
		p = getMappedOrNewProperty(map, "rxPhone", providerNo);
		p.setValueNoNull(settings.getRxPhone());
		p = getMappedOrNewProperty(map, "faxnumber", providerNo);
		p.setValueNoNull(settings.getFaxNumber());
		p = getMappedOrNewProperty(map, "workload_management", providerNo);
		p.setValue(settings.getWorkloadManagement());
		p = getMappedOrNewProperty(map, "provider_for_tickler_warning", providerNo);
		p.setValue(settings.getTicklerWarningProvider());
		p = getMappedOrNewProperty(map, "rx_use_rx3", providerNo);
		p.setValue(settings.isUseRx3()?"yes":"no");
		p = getMappedOrNewProperty(map, "rx_show_patient_dob", providerNo);
		p.setValue(settings.isShowPatientDob()?"yes":"no");
		p = getMappedOrNewProperty(map, "rx_default_quantity", providerNo);
		p.setValue(settings.getRxDefaultQuantity());
		p = getMappedOrNewProperty(map, "rx_page_size", providerNo);
		p.setValue(settings.getRxPageSize());
		p = getMappedOrNewProperty(map, "rxInteractionWarningLevel", providerNo);
		p.setValue(settings.getRxInteractionWarningLevel());
		p = getMappedOrNewProperty(map, "HC_Type", providerNo);
		p.setValue(settings.getDefaultHcType());
		p = getMappedOrNewProperty(map, "default_sex", providerNo);
		p.setValue(settings.getDefaultSex());
		p = getMappedOrNewProperty(map, "consultation_time_period_warning", providerNo);
		p.setValue(settings.getConsultationTimePeriodWarning());
		p = getMappedOrNewProperty(map, "consultation_team_warning", providerNo);
		p.setValue(settings.getConsultationTeamWarning());
		p = getMappedOrNewProperty(map, "consultation_req_paste_fmt", providerNo);
		p.setValue(settings.getConsultationPasteFormat());
		
		p = getMappedOrNewProperty(map, "consultation_letterheadname_default", providerNo);
		p.setValue(settings.getConsultationLetterHeadNameDefault());	
		
		p = getMappedOrNewProperty(map, "edoc_browser_in_document_report", providerNo);
		p.setValue(settings.isDocumentBrowserInDocumentReport()?"yes":"no");		
		p = getMappedOrNewProperty(map, "edoc_browser_in_master_file", providerNo);
		p.setValue(settings.isDocumentBrowserInMasterFile()?"yes":"no");
		
		p = getMappedOrNewProperty(map, "cpp_single_line", providerNo);
		p.setValue(settings.isCppSingleLine()?"yes":"no");
		
		p = getMappedOrNewProperty(map, PreferenceManager.CUSTOM_SUMMARY_ENABLE, providerNo);
		p.setValue(settings.isSummaryItemCustomDisplay()?"on":"off"); 	

		p = getMappedOrNewProperty(map, PreferenceManager.ONGOING_POS, providerNo);
		p.setValue(settings.isCppDisplayOngoingConcerns()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.ONGOING_START_DATE, providerNo);
		p.setValue(settings.isCppOngoingConcernsStartDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.ONGOING_RES_DATE, providerNo);
		p.setValue(settings.isCppOngoingConcernsResDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.ONGOING_PROBLEM_STATUS, providerNo);
		p.setValue(settings.isCppOngoingConcernsProblemStatus()?"on":"off");
		
		p = getMappedOrNewProperty(map, PreferenceManager.MED_HX_POS, providerNo);
		p.setValue(settings.isCppDisplayMedHx()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.MED_HX_START_DATE, providerNo);
		p.setValue(settings.isCppMedHxStartDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.MED_HX_RES_DATE, providerNo);
		p.setValue(settings.isCppMedHxResDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.MED_HX_TREATMENT, providerNo);
		p.setValue(settings.isCppMedHxTreatment()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.MED_HX_PROCEDURE_DATE, providerNo);
		p.setValue(settings.isCppMedHxProcedureDate()?"on":"off");
		
		p = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_POS, providerNo);
		p.setValue(settings.isCppDisplaySocialHx()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_START_DATE, providerNo);
		p.setValue(settings.isCppSocialHxStartDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_RES_DATE, providerNo);
		p.setValue(settings.isCppSocialHxResDate()?"on":"off");
		
		p = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_POS, providerNo);
		p.setValue(settings.isCppDisplayReminders()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_START_DATE, providerNo);
		p.setValue(settings.isCppRemindersStartDate()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_RES_DATE, providerNo);
		p.setValue(settings.isCppRemindersResDate()?"on":"off");

		p = getMappedOrNewProperty(map, PreferenceManager.PREVENTION_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayPreventions()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.FAM_HX_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayFamHx()?"on":"off"); 
		p = getMappedOrNewProperty(map, PreferenceManager.RISK_FACTORS_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayRiskFactors()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.ALLERGIES_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayAllergies()?"on":"off");
		p = getMappedOrNewProperty(map, PreferenceManager.MEDS_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayMeds()?"on":"off"); 
		p = getMappedOrNewProperty(map, PreferenceManager.OTHER_MEDS_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayOtherMeds()?"on":"off"); 
		p = getMappedOrNewProperty(map, PreferenceManager.ASSESSMENTS_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayAssessments()?"on":"off"); 

		p = getMappedOrNewProperty(map, PreferenceManager.INCOMING_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayIncoming()?"on":"off"); 	
		
		p = getMappedOrNewProperty(map, PreferenceManager.DS_SUPPORT_POS, providerNo);
		p.setValue(settings.isSummaryItemDisplayDsSupport()?"on":"off"); 
		
		p = getMappedOrNewProperty(map, "cme_note_date", providerNo);
		p.setValue(settings.getCmeNoteDate());	
		p = getMappedOrNewProperty(map, "cme_note_format", providerNo);
		p.setValue(settings.isCmeNoteFormat()?"yes":"no");
		p = getMappedOrNewProperty(map, "quickChartsize", providerNo);
		p.setValue(settings.getQuickChartSize());	
		p = getMappedOrNewProperty(map, "encounterWindowWidth", providerNo);
		p.setValue(settings.getEncounterWindowWidth());	
		p = getMappedOrNewProperty(map, "encounterWindowHeight", providerNo);
		p.setValue(settings.getEncounterWindowHeight());	
		p = getMappedOrNewProperty(map, "encounterWindowMaximize", providerNo);
		p.setValue(settings.isEncounterWindowMaximize()?"yes":"no");
		p = getMappedOrNewProperty(map, "favourite_eform_group", providerNo);
		p.setValue(settings.getFavoriteFormGroup());
		p = getMappedOrNewProperty(map, "lab_ack_comment", providerNo);
		p.setValue(settings.isDisableCommentOnAck()?"yes":"no");
		
		
		p = getMappedOrNewProperty(map, "olis_reportingLab", providerNo);
		p.setValue(settings.getOlisDefaultReportingLab());	
		p = getMappedOrNewProperty(map, "olis_exreportingLab", providerNo);
		p.setValue(settings.getOlisDefaultExcludeReportingLab());	
		p = getMappedOrNewProperty(map, "mydrugref_id", providerNo);
		p.setValue(settings.getMyDrugRefId());	
		p = getMappedOrNewProperty(map, "use_mymeds", providerNo);
		p.setValue(String.valueOf(settings.isUseMyMeds()));
		p = getMappedOrNewProperty(map, "disable_born_prompts", providerNo);
		p.setValue(String.valueOf(settings.isDisableBornPrompts()));
		
		p = getMappedOrNewProperty(map, "hide_old_echart_link_in_appointment", providerNo);
		p.setValue(settings.isHideOldEchartLinkInAppointment() ? "Y" : "N");
		
		p = getMappedOrNewProperty(map, "cobalt", providerNo);
		p.setValue(settings.isUseCobaltOnLogin()?"yes":"no");

		p = getMappedOrNewProperty(map, UserProperty.SCHEDULE_SITE, providerNo);
		p.setValue(settings.getSiteSelected());
	
		if(map.get("rx_use_rx3") != null) {
			settings.setUseRx3("yes".equals(map.get("rx_use_rx3").getValue())?true:false);
		}
		
		if(map.get("rx_show_patient_dob") != null) {
			settings.setShowPatientDob("yes".equals(map.get("rx_show_patient_dob").getValue())?true:false);
		}
		
		if(map.get("rx_default_quantity") != null) {
			settings.setRxDefaultQuantity(map.get("rx_default_quantity").getValue());
		}
		if(map.get("rx_page_size") != null) {
			settings.setRxPageSize(map.get("rx_page_size").getValue());
		}
		if(map.get("rxInteractionWarningLevel") != null) {
			settings.setRxInteractionWarningLevel(map.get("rxInteractionWarningLevel").getValue());
		}
		
		for(String key:map.keySet()) {
			Property prop = map.get(key);
			if(prop.getValue() != null) {
				propertyDao.merge(prop);
			}
		}
		
		providerExt.setProviderNo(providerNo);
		providerExt.setSignature(settings.getSignature());
		
		providerExtDao.merge(providerExt);		
				
	}

	/**
	 * update a single user setting
	 * @param providerNo - the provider id
	 * @param key - the property name
	 * @param value - the property value to save
	 * @throws IllegalArgumentException - if the key or value is invalid
	 */
	public void updateSingleSetting(String providerNo, String key, String value)
	{
		boolean isProviderPreferenceEntry = updateSinglePreference(providerNo, key, value);

		if(!isProviderPreferenceEntry)
		{
			//TODO check for valid key
//			throw new IllegalArgumentException(key + " is not a valid provider property");

			List<Property> userPropList = propertyDao.findByNameAndProvider(key, providerNo);

			Property userProp;
			if(userPropList.isEmpty())
			{
				userProp = new Property();
				userProp.setProviderNo(providerNo);
				userProp.setName(key);
			}
			else if(userPropList.size() == 1)
			{
				userProp = userPropList.get(0);
			}
			else
			{
				throw new IllegalStateException("Multiple values found for property " + key + " and provider " + providerNo);
			}
			userProp.setValue(value);
			propertyDao.merge(userProp);
		}
	}

	/**
	 * convert a key value pair to a provider preference entry. save if key value is valid.
	 * @param providerNo - the provider id
	 * @param key - the property name
	 * @param value - the property value to save
	 * @return true if the key was a valid provider preference entry and the entry was updated, false otherwise
	 */
	private boolean updateSinglePreference(String providerNo, String key, String value)
	{
		ProviderPreference preference = providerPreferenceDao.find(providerNo);
		if(preference == null)
		{
			preference = new ProviderPreference();
		}

		switch(key)
		{
			case "startHour" : preference.setStartHour(Integer.parseInt(value)); break;
			case "endHour" : preference.setEndHour(Integer.parseInt(value)); break;
			case "everyMin" : preference.setEveryMin(Integer.parseInt(value)); break;
			case "myGroupNo" : preference.setMyGroupNo(value); break;
			case "colourTemplate" : preference.setColourTemplate(value); break;
			case "newTicklerWarningWindow" : preference.setNewTicklerWarningWindow(value); break;
			case "defaultServiceType" : preference.setDefaultServiceType(value); break;
			case "defaultCaisiPmm" : preference.setDefaultCaisiPmm(value); break;
			case "defaultNewOscarCme" : preference.setDefaultNewOscarCme(value); break;
			case "printQrCodeOnPrescriptions" : preference.setPrintQrCodeOnPrescriptions(Boolean.parseBoolean(value)); break;
			case "appointmentScreenLinkNameDisplayLength" : preference.setAppointmentScreenLinkNameDisplayLength(Integer.parseInt(value)); break;
			case "defaultDoNotDeleteBilling" : preference.setDefaultDoNotDeleteBilling(Integer.parseInt(value)); break;
			case "defaultDxCode" : preference.setDefaultDxCode(value); break;
			default: return false;
		}

		providerPreferenceDao.merge(preference);
		return true;
	}
}
