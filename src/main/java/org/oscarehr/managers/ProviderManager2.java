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

import org.apache.commons.lang.StringUtils;
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
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProviderManager2
{
	private static Logger logger = MiscUtils.getLogger();


	@Autowired
	private ProviderDao providerDao;

	@Autowired
	private PropertyDao propertyDao;

	@Autowired
	private ProviderPreferenceDao providerPreferenceDao;

	@Autowired
	private ProviderExtDao providerExtDao;

	public List<Provider> getProviders(LoggedInInfo loggedInInfo, Boolean active)
	{
		List<Provider> results = null;

		if (active == null)
		{
			results = providerDao.getProviders();
		}
		else
		{
			results = providerDao.getProviders(active);
		}

		//--- log action ---
		LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProviders", "active=" + active);

		return (results);
	}

	public Provider getProvider(LoggedInInfo loggedInInfo, String providerNo)
	{

		Provider result = providerDao.getProvider(providerNo);

		//--- log action ---
		LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProvider", "providerNo=" + providerNo);

		return (result);
	}

	public List<Provider> getProvidersByIds(LoggedInInfo loggedInInfo, List<String> ids)
	{
		List<Provider> results = new ArrayList<Provider>();
		for (String id : ids)
		{
			results.add(getProvider(loggedInInfo, id));
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.getProviders", "providerNo=" + id);
		}
		return results;
	}

	public Property getProviderProperties(LoggedInInfo loggedInInfo, String providerNo, String propertyName)
	{
		Property results = propertyDao.findByNameAndProvider(propertyName, providerNo);

		//--- log action ---
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
				"getProviderProperties",
				"ProviderManager",
				LogConst.ACTION_ACCESS,
				"providerNo=" + providerNo + ", propertyName=" + propertyName);

		return results;
	}

	/*
	 * Format is LastName[,FirstName]
	 */
	public List<Provider> searchProviderByNames(LoggedInInfo loggedInInfo, String searchString, int startIndex, int itemsToReturn)
	{

		List<Provider> results = providerDao.searchProviderByNamesString(searchString, startIndex, itemsToReturn);

		if (logger.isDebugEnabled())
		{
			logger.debug("searchProviderByNames, searchString=" + searchString + ", result.size=" + results.size());
		}

		//--- log action ---
		for (Provider provider : results)
		{
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.searchProviderByNames result", "provideRNo=" + provider.getProviderNo());
		}

		return (results);
	}

	public List<Provider> search(LoggedInInfo loggedInInfo, String term, String status, int startIndex, int itemsToReturn)
	{

		List<Provider> results = providerDao.search(term, status, startIndex, itemsToReturn);

		if (logger.isDebugEnabled())
		{
			logger.debug("search, active=" + status + ", term=" + term + " result.size=" + results.size());
		}

		//--- log action --- this seems useless
		for (Provider provider : results)
		{
			LogAction.addLogSynchronous(loggedInInfo, "ProviderManager.search result", "providerNo=" + provider.getProviderNo());
		}

		return (results);
	}

	public List<String> getActiveTeams(LoggedInInfo loggedInInfo)
	{
		return providerDao.getActiveTeams();
	}

	public ProviderSettings getProviderSettings(String providerNo)
	{
		ProviderSettings settings = new ProviderSettings();

		ProviderPreference pp = providerPreferenceDao.find(providerNo);
		if (pp == null)
		{
			pp = new ProviderPreference();
		}

		ProviderExt providerExt = providerExtDao.find(providerNo);
		if (providerExt == null)
		{
			providerExt = new ProviderExt();
		}

		Map<String, Property> map = new HashMap<String, Property>();
		for (Property prop : propertyDao.findByProvider(providerNo))
		{
			map.put(prop.getName(), prop);
		}

		if (map.get("recentPatients") != null)
		{
			settings.setRecentPatients(map.get("recentPatients").getValue());
		}

		if (map.get("rxAddress") != null)
		{
			settings.setRxAddress(map.get("rxAddress").getValue());
		}
		if (map.get("rxCity") != null)
		{
			settings.setRxCity(map.get("rxCity").getValue());
		}
		if (map.get("rxProvince") != null)
		{
			settings.setRxProvince(map.get("rxProvince").getValue());
		}
		if (map.get("rxPostal") != null)
		{
			settings.setRxPostal(map.get("rxPostal").getValue());
		}
		if (map.get("rxPhone") != null)
		{
			settings.setRxPhone(map.get("rxPhone").getValue());
		}
		if (map.get("faxnumber") != null)
		{
			settings.setFaxNumber(map.get("faxnumber").getValue());
		}

		if (map.get("workload_management") != null)
		{
			settings.setWorkloadManagement(map.get("workload_management").getValue());
		}

		if (map.get("provider_for_tickler_warning") != null)
		{
			settings.setTicklerWarningProvider(map.get("provider_for_tickler_warning").getValue());
		}

		if (map.get("rx_use_rx3") != null)
		{
			settings.setUseRx3("yes".equals(map.get("rx_use_rx3").getValue()) ? true : false);
		}

		if (map.get("rx_show_patient_dob") != null)
		{
			settings.setShowPatientDob("yes".equals(map.get("rx_show_patient_dob").getValue()) ? true : false);
		}

		if (map.get("rx_default_quantity") != null)
		{
			settings.setRxDefaultQuantity(map.get("rx_default_quantity").getValue());
		}
		if (map.get("rx_page_size") != null)
		{
			settings.setRxPageSize(map.get("rx_page_size").getValue());
		}
		if (map.get("rxInteractionWarningLevel") != null)
		{
			settings.setRxInteractionWarningLevel(map.get("rxInteractionWarningLevel").getValue());
		}

		if (map.get("HC_Type") != null)
		{
			settings.setDefaultHcType(map.get("HC_Type").getValue());
		}
		else
		{
			// if no user property use system property
			String hcType = OscarProperties.getInstance().getProperty("hctype", "");
			if (hcType != null)
			{
				settings.setDefaultHcType(hcType);
			}
		}

		if (map.get("default_sex") != null)
		{
			settings.setDefaultSex(map.get("default_sex").getValue());
		}

		if (map.get("consultation_time_period_warning") != null)
		{
			settings.setConsultationTimePeriodWarning(map.get("consultation_time_period_warning").getValue());
		}
		if (map.get("consultation_team_warning") != null)
		{
			settings.setConsultationTeamWarning(map.get("consultation_team_warning").getValue());
		}
		if (map.get("consultation_req_paste_fmt") != null)
		{
			settings.setConsultationPasteFormat(map.get("consultation_req_paste_fmt").getValue());
		}
		if (map.get("consultation_letterheadname_default") != null)
		{
			settings.setConsultationLetterHeadNameDefault(map.get("consultation_letterheadname_default").getValue());
		}
		if (map.get("edoc_browser_in_document_report") != null)
		{
			settings.setDocumentBrowserInDocumentReport("yes".equals(map.get("edoc_browser_in_document_report").getValue()) ? true : false);
		}
		if (map.get("edoc_browser_in_master_file") != null)
		{
			settings.setDocumentBrowserInMasterFile("yes".equals(map.get("edoc_browser_in_master_file").getValue()) ? true : false);
		}

		if (map.get("cpp_single_line") != null)
		{
			settings.setCppSingleLine("yes".equals(map.get("cpp_single_line").getValue()) ? true : false);
		}

		if (map.get(UserProperty.TICKLER_VIEW_ONLY_MINE) != null)
		{
			try
			{
				settings.setTicklerViewOnlyMine(ConversionUtils.parseBoolean(map.get(UserProperty.TICKLER_VIEW_ONLY_MINE).getValue()));
			}
			catch (ParseException e)
			{
				settings.setTicklerViewOnlyMine(false);
			}
		}
		else
		{
			settings.setTicklerViewOnlyMine(false);
		}

		if(map.get(UserProperty.EFORM_POPUP_WIDTH) != null)
		{
			try
			{
				settings.setEformPopupWidth(Integer.parseInt(map.get(UserProperty.EFORM_POPUP_WIDTH).getValue()));
			}
			catch (NumberFormatException ne)
			{
				MiscUtils.getLogger().error("Failed to lookup eform popup width due to error: " + ne.getMessage(), ne);
			}
		}
		if(map.get(UserProperty.EFORM_POPUP_HEIGHT) != null)
		{
			try
			{
				settings.setEformPopupHeight(Integer.parseInt(map.get(UserProperty.EFORM_POPUP_HEIGHT).getValue()));
			}
			catch (NumberFormatException ne)
			{
				MiscUtils.getLogger().error("Failed to lookup eform popup height due to error: " + ne.getMessage(), ne);
			}
		}

		if (map.get(UserProperty.SCHEDULE_SITE) != null)
		{
			settings.setSiteSelected(map.get(UserProperty.SCHEDULE_SITE).getValue());
		}
		if (map.get(UserProperty.SCHEDULE_VIEW) != null)
		{
			settings.setViewSelected(map.get(UserProperty.SCHEDULE_VIEW).getValue());
		}

		Property patientNameLengthProp = map.get(UserProperty.PATIENT_NAME_LENGTH);
		if (patientNameLengthProp != null)
		{
			String patientNameLengthStr = patientNameLengthProp.getValue();
			if (StringUtils.isNumeric(patientNameLengthStr))
			{
				settings.setPatientNameLength(Integer.parseInt(patientNameLengthStr));
			}
		}

		// default to the legacy properties file setting
		boolean intakeFormEnabled = OscarProperties.getInstance().isAppointmentIntakeFormEnabled();
		if (map.get(UserProperty.INTAKE_FORM_ENABLED) != null)
		{
			intakeFormEnabled = "yes".equals(map.get(UserProperty.INTAKE_FORM_ENABLED).getValue());
		}
		settings.setIntakeFormEnabled(intakeFormEnabled);

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
		if (map.get("cpp.pref.enable") != null)
		{
			settings.setSummaryItemCustomDisplay("on".equals(map.get("cpp.pref.enable").getValue()) ? true : false);
		}


		//.position exists -> value blank = disable  --in the old ui if cpp.pref.enabled is "on" then changing the position to "None" sets the value to "" hides the CPP item
		//.position exists -> value off = disable
		//except for cpp.pref.enable if the absence of the property in the db the UI should display "true" or "Enable" for .position.
		if (map.get(PreferenceManager.ONGOING_POS) != null)
		{
			settings.setCppDisplayOngoingConcerns("on".equals(map.get(PreferenceManager.ONGOING_POS).getValue()) ? true : false);
		}
		else if (map.get(PreferenceManager.OLD_ONGOING_CONCERNS_POS) != null)
		{
			settings.setCppDisplayOngoingConcerns(!"".equals(map.get(PreferenceManager.OLD_ONGOING_CONCERNS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setCppDisplayOngoingConcerns(true);
		}
		if (map.get(PreferenceManager.ONGOING_START_DATE) != null)
		{
			settings.setCppOngoingConcernsStartDate("on".equals(map.get(PreferenceManager.ONGOING_START_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.ONGOING_RES_DATE) != null)
		{
			settings.setCppOngoingConcernsResDate("on".equals(map.get(PreferenceManager.ONGOING_RES_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.ONGOING_PROBLEM_STATUS) != null)
		{
			settings.setCppOngoingConcernsProblemStatus("on".equals(map.get(PreferenceManager.ONGOING_PROBLEM_STATUS).getValue()) ? true : false);
		}

		if (map.get(PreferenceManager.DISEASE_REGISTRY_POS) != null)
		{
			settings.setCppDisplayDiseaseRegistry("on".equals(map.get(PreferenceManager.DISEASE_REGISTRY_POS).getValue()));
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
		if (map.get(PreferenceManager.MED_HX_POS) != null)
		{
			settings.setCppDisplayMedHx("on".equals(map.get(PreferenceManager.MED_HX_POS).getValue()) ? true : false);
		}
		else if (map.get(PreferenceManager.OLD_MEDICAL_HISTORY_POS) != null)
		{
			settings.setCppDisplayMedHx(!"".equals(map.get(PreferenceManager.OLD_MEDICAL_HISTORY_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setCppDisplayMedHx(true);
		}
		if (map.get(PreferenceManager.MED_HX_START_DATE) != null)
		{
			settings.setCppMedHxStartDate("on".equals(map.get(PreferenceManager.MED_HX_START_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.MED_HX_RES_DATE) != null)
		{
			settings.setCppMedHxResDate("on".equals(map.get(PreferenceManager.MED_HX_RES_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.MED_HX_TREATMENT) != null)
		{
			settings.setCppMedHxTreatment("on".equals(map.get(PreferenceManager.MED_HX_TREATMENT).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.MED_HX_PROCEDURE_DATE) != null)
		{
			settings.setCppMedHxProcedureDate("on".equals(map.get(PreferenceManager.MED_HX_PROCEDURE_DATE).getValue()) ? true : false);
		}

		if (map.get(PreferenceManager.SOC_HX_POS) != null)
		{
			settings.setCppDisplaySocialHx("on".equals(map.get(PreferenceManager.SOC_HX_POS).getValue()) ? true : false);
		}
		else if (map.get(PreferenceManager.OLD_SOCIAL_HISTORY_POS) != null)
		{
			settings.setCppDisplaySocialHx(!"".equals(map.get(PreferenceManager.OLD_SOCIAL_HISTORY_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setCppDisplaySocialHx(true);
		}

		if (map.get(PreferenceManager.SOC_HX_START_DATE) != null)
		{
			settings.setCppSocialHxStartDate("on".equals(map.get(PreferenceManager.SOC_HX_START_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.SOC_HX_RES_DATE) != null)
		{
			settings.setCppSocialHxResDate("on".equals(map.get(PreferenceManager.SOC_HX_RES_DATE).getValue()) ? true : false);
		}

		if (map.get(PreferenceManager.REMINDERS_POS) != null)
		{
			settings.setCppDisplayReminders("on".equals(map.get(PreferenceManager.REMINDERS_POS).getValue()) ? true : false);
		}
		else if (map.get(PreferenceManager.OLD_REMINDERS_POS) != null)
		{
			settings.setCppDisplayReminders(!"".equals(map.get(PreferenceManager.OLD_REMINDERS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setCppDisplayReminders(true);
		}
		if (map.get(PreferenceManager.REMINDERS_START_DATE) != null)
		{
			settings.setCppRemindersStartDate("on".equals(map.get(PreferenceManager.REMINDERS_START_DATE).getValue()) ? true : false);
		}
		if (map.get(PreferenceManager.REMINDERS_RES_DATE) != null)
		{
			settings.setCppRemindersResDate("on".equals(map.get(PreferenceManager.REMINDERS_RES_DATE).getValue()) ? true : false);
		}

		if (map.get(PreferenceManager.PREVENTION_POS) != null)
		{
			settings.setSummaryItemDisplayPreventions("on".equals(map.get(PreferenceManager.PREVENTION_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayPreventions(true);
		}
		if (map.get(PreferenceManager.FAM_HX_POS) != null)
		{
			settings.setSummaryItemDisplayFamHx("on".equals(map.get(PreferenceManager.FAM_HX_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayFamHx(true);
		}
		if (map.get(PreferenceManager.RISK_FACTORS_POS) != null)
		{
			settings.setSummaryItemDisplayRiskFactors("on".equals(map.get(PreferenceManager.RISK_FACTORS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayRiskFactors(true);
		}
		if (map.get(PreferenceManager.ALLERGIES_POS) != null)
		{
			settings.setSummaryItemDisplayAllergies("on".equals(map.get(PreferenceManager.ALLERGIES_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayAllergies(true);
		}

		if (map.get(PreferenceManager.MEDS_POS) != null)
		{
			settings.setSummaryItemDisplayMeds("on".equals(map.get(PreferenceManager.MEDS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayMeds(true);
		}
		if (map.get(PreferenceManager.OTHER_MEDS_POS) != null)
		{
			settings.setSummaryItemDisplayOtherMeds("on".equals(map.get(PreferenceManager.OTHER_MEDS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayOtherMeds(true);
		}
		if (map.get(PreferenceManager.ASSESSMENTS_POS) != null)
		{
			settings.setSummaryItemDisplayAssessments("on".equals(map.get(PreferenceManager.ASSESSMENTS_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayAssessments(true);
		}

		if (map.get(PreferenceManager.INCOMING_POS) != null)
		{
			settings.setSummaryItemDisplayIncoming("on".equals(map.get(PreferenceManager.INCOMING_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayIncoming(true);
		}

		if (map.get(PreferenceManager.DS_SUPPORT_POS) != null)
		{
			settings.setSummaryItemDisplayDsSupport("on".equals(map.get(PreferenceManager.DS_SUPPORT_POS).getValue()) ? true : false);
		}
		else
		{
			settings.setSummaryItemDisplayDsSupport(false);
		}

		if (map.get("cme_note_date") != null)
		{
			settings.setCmeNoteDate(map.get("cme_note_date").getValue());
		}

		if (map.get("cme_note_format") != null)
		{
			settings.setCmeNoteFormat("yes".equals(map.get("cme_note_format").getValue()) ? true : false);
		}

		if (map.get("quickChartsize") != null)
		{
			settings.setQuickChartSize(map.get("quickChartsize").getValue());
		}

		if (map.get("encounterWindowWidth") != null)
		{
			settings.setEncounterWindowWidth(map.get("encounterWindowWidth").getValue());
		}
		if (map.get("encounterWindowHeight") != null)
		{
			settings.setEncounterWindowHeight(map.get("encounterWindowHeight").getValue());
		}
		if (map.get("encounterWindowMaximize") != null)
		{
			settings.setEncounterWindowMaximize("yes".equals(map.get("encounterWindowMaximize").getValue()) ? true : false);
		}
		if (map.get("favourite_eform_group") != null)
		{
			settings.setFavoriteFormGroup(map.get("favourite_eform_group").getValue());
		}

		if (map.get("lab_ack_comment") != null)
		{
			settings.setDisableCommentOnAck("yes".equals(map.get("lab_ack_comment").getValue()) ? true : false);
		}

		if (map.get("olis_reportingLab") != null)
		{
			settings.setOlisDefaultReportingLab(map.get("olis_reportingLab").getValue());
		}
		if (map.get("olis_exreportingLab") != null)
		{
			settings.setOlisDefaultExcludeReportingLab(map.get("olis_exreportingLab").getValue());
		}

		if (map.get("mydrugref_id") != null)
		{
			settings.setMyDrugRefId(map.get("mydrugref_id").getValue());
		}

		if (map.get("cobalt") != null)
		{
			settings.setUseCobaltOnLogin("yes".equals(map.get("cobalt").getValue()) ? true : false);
		}

		if (map.get("use_mymeds") != null)
		{
			settings.setUseMyMeds(Boolean.valueOf(map.get("use_mymeds").getValue()));
		}

		if (map.get("disable_born_prompts") != null)
		{
			settings.setDisableBornPrompts("Y".equals(map.get("disable_born_prompts").getValue()));
		}

		if (map.get("hide_old_echart_link_in_appointment") != null)
		{
			settings.setHideOldEchartLinkInAppointment("Y".equals(map.get("hide_old_echart_link_in_appointment").getValue()));
		}
		if (map.get(UserProperty.SCHEDULE_COUNT_ENABLED) != null)
		{
			settings.setAppointmentCountEnabled("true".equals(map.get(UserProperty.SCHEDULE_COUNT_ENABLED).getValue()));
		}
		if (map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_CANCELLED) != null)
		{
			settings.setAppointmentCountIncludeCancelled("true".equals(map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_CANCELLED).getValue()));
		}
		if (map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_NO_SHOW) != null)
		{
			settings.setAppointmentCountIncludeNoShow("true".equals(map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_NO_SHOW).getValue()));
		}
		if (map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_NO_DEMOGRAPHIC) != null)
		{
			settings.setAppointmentCountIncludeNoDemographic("true".equals(map.get(UserProperty.SCHEDULE_COUNT_INCLUDE_NO_DEMOGRAPHIC).getValue()));
		}

		if (map.get(UserProperty.MESSAGING_UNREAD_COUNT_MODE) == null)
		{
			settings.setMessageCountMode(ProviderSettings.MESSAGE_COUNT_SUM_MODE.MHA_INTERNAL);
		}
		else
		{
			settings.setMessageCountMode(ProviderSettings.MESSAGE_COUNT_SUM_MODE.valueOf(map.get(UserProperty.MESSAGING_UNREAD_COUNT_MODE).getValue()));
		}

		if (map.get(UserProperty.CARECONNECT_PPN_CHECK) != null)
		{
			settings.setEnableCareConnectPPNCheck(ConversionUtils.fromBoolString(map.get(UserProperty.CARECONNECT_PPN_CHECK).getValue()));
		}


		settings.setNewTicklerWarningWindow(pp.getNewTicklerWarningWindow());

		settings.setStartHour(pp.getStartHour());
		settings.setEndHour(pp.getEndHour());
		settings.setPeriod(pp.getEveryMin());
		settings.setGroupNo(pp.getMyGroupNo());
		settings.setAppointmentScreenLinkNameDisplayLength(pp.getAppointmentScreenLinkNameDisplayLength());
		settings.setAppointmentReasonDisplayLevel(pp.getAppointmentReasonDisplayLevel());

		if (pp.getAppointmentScreenForms() != null)
		{
			settings.setAppointmentScreenForms(pp.getAppointmentScreenForms());
		}

		if (pp.getAppointmentScreenEForms() != null)
		{
			settings.setAppointmentScreenEforms(pp.getAppointmentScreenEForms());
		}

		if (pp.getAppointmentScreenQuickLinks() != null)
		{
			for (QuickLink ql : pp.getAppointmentScreenQuickLinks())
			{
				org.oscarehr.managers.model.QuickLink qt = new org.oscarehr.managers.model.QuickLink();
				qt.setName(ql.getName());
				qt.setUrl(ql.getUrl());
				settings.getAppointmentScreenQuickLinks().add(qt);
			}
		}

		settings.setDefaultServiceType(pp.getDefaultServiceType());
		settings.setDefaultDxCode(pp.getDefaultDxCode());

		settings.setDefaultDoNotDeleteBilling(pp.getDefaultDoNotDeleteBilling() == 1 ? true : false);

		settings.setPrintQrCodeOnPrescription(pp.isPrintQrCodeOnPrescriptions());
		settings.setERxEnabled(pp.isERxEnabled());
		settings.setERxTrainingMode(pp.isERxTrainingMode());
		settings.setERxFacility(pp.getERxFacility());
		settings.setERxURL(pp.getERx_SSO_URL());
		settings.setERxUsername(pp.getERxUsername());
		settings.setERxPassword(pp.getERxPassword());

		settings.setDefaultPmm("enabled".equals(pp.getDefaultCaisiPmm()));

		//from ProviderExt
		settings.setSignature(providerExt.getSignature());


		return settings;
	}

	private Property getMappedOrNewProperty(Map<String, Property> map, String key, String providerNo)
	{
		if (map.get(key) != null)
		{
			return map.get(key);
		}
		else
		{
			Property p = new Property();
			p.setProviderNo(providerNo);
			p.setName(key);
			map.put(p.getName(), p);

			return p;
		}
	}

	public void updateProviderSettings(LoggedInInfo loggedInInfo, String providerNo, ProviderSettings settings)
	{

		ProviderPreference pp = providerPreferenceDao.find(providerNo);
		if (pp == null)
		{
			pp = new ProviderPreference();
		}

		if (settings.getSignature() != null)
		{
			ProviderExt providerExt = providerExtDao.find(providerNo);
			if (providerExt == null)
			{
				providerExt = new ProviderExt();
			}

			providerExt.setProviderNo(providerNo);
			providerExt.setSignature(settings.getSignature());
			providerExtDao.merge(providerExt);
		}

		List<Property> props = propertyDao.findByProvider(providerNo);

		pp.setNewTicklerWarningWindow(settings.getNewTicklerWarningWindow());
		pp.setStartHour(settings.getStartHour());
		pp.setEndHour(settings.getEndHour());
		pp.setEveryMin(settings.getPeriod());
		pp.setMyGroupNo(settings.getGroupNo());
		pp.setAppointmentScreenLinkNameDisplayLength(settings.getAppointmentScreenLinkNameDisplayLength());

		pp.getAppointmentScreenForms().clear();
		for (String formName : settings.getAppointmentScreenForms())
		{
			pp.getAppointmentScreenForms().add(formName);
		}

		pp.getAppointmentScreenEForms().clear();
		for (Integer eformId : settings.getAppointmentScreenEforms())
		{
			pp.getAppointmentScreenEForms().add(eformId);
		}

		pp.getAppointmentScreenQuickLinks().clear();
		for (org.oscarehr.managers.model.QuickLink ql : settings.getAppointmentScreenQuickLinks())
		{
			pp.getAppointmentScreenQuickLinks().add(new QuickLink(ql.getName(), ql.getUrl()));
		}

		pp.setDefaultDxCode(settings.getDefaultDxCode());
		pp.setDefaultServiceType(settings.getDefaultServiceType() == null || settings.getDefaultServiceType().isEmpty() ? null : settings.getDefaultServiceType());

		pp.setDefaultDoNotDeleteBilling(settings.isDefaultDoNotDeleteBilling() ? 1 : 0);

		pp.setPrintQrCodeOnPrescriptions(settings.isPrintQrCodeOnPrescription());
		pp.setERxEnabled(settings.isERxEnabled());

		pp.setERxTrainingMode(settings.isERxTrainingMode());
		pp.setERxFacility(settings.getERxFacility());
		pp.setERx_SSO_URL(settings.getERxURL());
		pp.setERxUsername(settings.getERxUsername());
		pp.setERxPassword(settings.getERxPassword());

		pp.setDefaultCaisiPmm(settings.isDefaultPmm() ? "enabled" : "disabled");
		pp.setProviderNo(providerNo);

		pp.setAppointmentReasonDisplayLevel(settings.getAppointmentReasonDisplayLevel());

		providerPreferenceDao.merge(pp);

		Map<String, Property> map = new HashMap<String, Property>();
		for (Property prop : props)
		{
			map.put(prop.getName(), prop);
		}

		Property property = null;

		property = getMappedOrNewProperty(map, "recentPatients", providerNo);
		property.setValue(settings.getRecentPatients());
		property = getMappedOrNewProperty(map, "rxAddress", providerNo);
		property.setValueNoNull(settings.getRxAddress());
		property = getMappedOrNewProperty(map, "rxCity", providerNo);
		property.setValueNoNull(settings.getRxCity());
		property = getMappedOrNewProperty(map, "rxProvince", providerNo);
		property.setValueNoNull(settings.getRxProvince());
		property = getMappedOrNewProperty(map, "rxPostal", providerNo);
		property.setValueNoNull(settings.getRxPostal());
		property = getMappedOrNewProperty(map, "rxPhone", providerNo);
		property.setValueNoNull(settings.getRxPhone());
		property = getMappedOrNewProperty(map, "faxnumber", providerNo);
		property.setValueNoNull(settings.getFaxNumber());
		property = getMappedOrNewProperty(map, "workload_management", providerNo);
		property.setValue(settings.getWorkloadManagement());
		property = getMappedOrNewProperty(map, "provider_for_tickler_warning", providerNo);
		property.setValue(settings.getTicklerWarningProvider());
		property = getMappedOrNewProperty(map, "rx_use_rx3", providerNo);
		property.setValue(settings.isUseRx3() ? "yes" : "no");
		property = getMappedOrNewProperty(map, "rx_show_patient_dob", providerNo);
		property.setValue(settings.isShowPatientDob() ? "yes" : "no");
		property = getMappedOrNewProperty(map, "rx_default_quantity", providerNo);
		property.setValue(settings.getRxDefaultQuantity());
		property = getMappedOrNewProperty(map, "rx_page_size", providerNo);
		property.setValue(settings.getRxPageSize());
		property = getMappedOrNewProperty(map, "rxInteractionWarningLevel", providerNo);
		property.setValue(settings.getRxInteractionWarningLevel());
		property = getMappedOrNewProperty(map, "HC_Type", providerNo);
		property.setValue(settings.getDefaultHcType());
		property = getMappedOrNewProperty(map, "default_sex", providerNo);
		property.setValue(settings.getDefaultSex());
		property = getMappedOrNewProperty(map, "consultation_time_period_warning", providerNo);
		// if deleted from frontend
		property.setValue(StringUtils.trimToNull(settings.getConsultationTimePeriodWarning()));
		property.setValue(settings.getConsultationTimePeriodWarning());
		property = getMappedOrNewProperty(map, "consultation_team_warning", providerNo);
		property.setValue(settings.getConsultationTeamWarning());
		property = getMappedOrNewProperty(map, "consultation_req_paste_fmt", providerNo);
		property.setValue(settings.getConsultationPasteFormat());

		property = getMappedOrNewProperty(map, "consultation_letterheadname_default", providerNo);
		property.setValue(settings.getConsultationLetterHeadNameDefault());

		property = getMappedOrNewProperty(map, "edoc_browser_in_document_report", providerNo);
		property.setValue(settings.isDocumentBrowserInDocumentReport() ? "yes" : "no");
		property = getMappedOrNewProperty(map, "edoc_browser_in_master_file", providerNo);
		property.setValue(settings.isDocumentBrowserInMasterFile() ? "yes" : "no");

		property = getMappedOrNewProperty(map, "cpp_single_line", providerNo);
		property.setValue(settings.isCppSingleLine() ? "yes" : "no");

		property = getMappedOrNewProperty(map, UserProperty.TICKLER_VIEW_ONLY_MINE, providerNo);
		property.setValue(settings.getTicklerViewOnlyMine().toString());

		property = getMappedOrNewProperty(map, PreferenceManager.CUSTOM_SUMMARY_ENABLE, providerNo);
		property.setValue(settings.isSummaryItemCustomDisplay() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.ONGOING_POS, providerNo);
		property.setValue(settings.isCppDisplayOngoingConcerns() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.ONGOING_START_DATE, providerNo);
		property.setValue(settings.isCppOngoingConcernsStartDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.ONGOING_RES_DATE, providerNo);
		property.setValue(settings.isCppOngoingConcernsResDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.ONGOING_PROBLEM_STATUS, providerNo);
		property.setValue(settings.isCppOngoingConcernsProblemStatus() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.DISEASE_REGISTRY_POS, providerNo);
		property.setValue(settings.isCppDisplayDiseaseRegistry() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.MED_HX_POS, providerNo);
		property.setValue(settings.isCppDisplayMedHx() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.MED_HX_START_DATE, providerNo);
		property.setValue(settings.isCppMedHxStartDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.MED_HX_RES_DATE, providerNo);
		property.setValue(settings.isCppMedHxResDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.MED_HX_TREATMENT, providerNo);
		property.setValue(settings.isCppMedHxTreatment() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.MED_HX_PROCEDURE_DATE, providerNo);
		property.setValue(settings.isCppMedHxProcedureDate() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_POS, providerNo);
		property.setValue(settings.isCppDisplaySocialHx() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_START_DATE, providerNo);
		property.setValue(settings.isCppSocialHxStartDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.SOC_HX_RES_DATE, providerNo);
		property.setValue(settings.isCppSocialHxResDate() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_POS, providerNo);
		property.setValue(settings.isCppDisplayReminders() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_START_DATE, providerNo);
		property.setValue(settings.isCppRemindersStartDate() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.REMINDERS_RES_DATE, providerNo);
		property.setValue(settings.isCppRemindersResDate() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.PREVENTION_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayPreventions() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.FAM_HX_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayFamHx() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.RISK_FACTORS_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayRiskFactors() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.ALLERGIES_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayAllergies() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.MEDS_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayMeds() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.OTHER_MEDS_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayOtherMeds() ? "on" : "off");
		property = getMappedOrNewProperty(map, PreferenceManager.ASSESSMENTS_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayAssessments() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.INCOMING_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayIncoming() ? "on" : "off");

		property = getMappedOrNewProperty(map, PreferenceManager.DS_SUPPORT_POS, providerNo);
		property.setValue(settings.isSummaryItemDisplayDsSupport() ? "on" : "off");

		property = getMappedOrNewProperty(map, "cme_note_date", providerNo);
		property.setValue(settings.getCmeNoteDate());
		property = getMappedOrNewProperty(map, "cme_note_format", providerNo);
		property.setValue(settings.isCmeNoteFormat() ? "yes" : "no");
		property = getMappedOrNewProperty(map, "quickChartsize", providerNo);
		property.setValue(settings.getQuickChartSize());
		property = getMappedOrNewProperty(map, "encounterWindowWidth", providerNo);
		property.setValue(settings.getEncounterWindowWidth());
		property = getMappedOrNewProperty(map, "encounterWindowHeight", providerNo);
		property.setValue(settings.getEncounterWindowHeight());
		property = getMappedOrNewProperty(map, "encounterWindowMaximize", providerNo);
		property.setValue(settings.isEncounterWindowMaximize() ? "yes" : "no");
		property = getMappedOrNewProperty(map, "favourite_eform_group", providerNo);
		property.setValue(settings.getFavoriteFormGroup());
		property = getMappedOrNewProperty(map, "lab_ack_comment", providerNo);
		property.setValue(settings.isDisableCommentOnAck() ? "yes" : "no");
		property = getMappedOrNewProperty(map, UserProperty.EFORM_POPUP_WIDTH, providerNo);
		property.setValue(settings.getEformPopupWidth().toString());
		property = getMappedOrNewProperty(map, UserProperty.EFORM_POPUP_HEIGHT, providerNo);
		property.setValue(settings.getEformPopupHeight().toString());


		property = getMappedOrNewProperty(map, "olis_reportingLab", providerNo);
		property.setValue(settings.getOlisDefaultReportingLab());
		property = getMappedOrNewProperty(map, "olis_exreportingLab", providerNo);
		property.setValue(settings.getOlisDefaultExcludeReportingLab());
		property = getMappedOrNewProperty(map, "mydrugref_id", providerNo);
		property.setValue(settings.getMyDrugRefId());
		property = getMappedOrNewProperty(map, "use_mymeds", providerNo);
		property.setValue(String.valueOf(settings.isUseMyMeds()));
		property = getMappedOrNewProperty(map, "disable_born_prompts", providerNo);
		property.setValue(String.valueOf(settings.isDisableBornPrompts()));

		property = getMappedOrNewProperty(map, "hide_old_echart_link_in_appointment", providerNo);
		property.setValue(settings.isHideOldEchartLinkInAppointment() ? "Y" : "N");

		property = getMappedOrNewProperty(map, "cobalt", providerNo);
		property.setValue(settings.isUseCobaltOnLogin() ? "yes" : "no");

		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_SITE, providerNo);
		property.setValue(settings.getSiteSelected());

		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_VIEW, providerNo);
		property.setValue(settings.getViewSelected());

		property = getMappedOrNewProperty(map, UserProperty.PATIENT_NAME_LENGTH, providerNo);
		Integer patientNameLength = settings.getPatientNameLength();
		property.setValue((patientNameLength == null) ? null : String.valueOf(patientNameLength));

		property = getMappedOrNewProperty(map, UserProperty.INTAKE_FORM_ENABLED, providerNo);
		property.setValue(settings.isIntakeFormEnabled() ? "yes" : "no");

		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_COUNT_ENABLED, providerNo);
		property.setValue(Boolean.toString(settings.isAppointmentCountEnabled()));
		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_COUNT_INCLUDE_CANCELLED, providerNo);
		property.setValue(Boolean.toString(settings.isAppointmentCountEnabled()));
		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_COUNT_INCLUDE_NO_SHOW, providerNo);
		property.setValue(Boolean.toString(settings.isAppointmentCountIncludeNoShow()));
		property = getMappedOrNewProperty(map, UserProperty.SCHEDULE_COUNT_INCLUDE_NO_DEMOGRAPHIC, providerNo);
		property.setValue(Boolean.toString(settings.isAppointmentCountIncludeNoDemographic()));
		property = getMappedOrNewProperty(map, UserProperty.MESSAGING_UNREAD_COUNT_MODE, providerNo);
		property.setValue(settings.getMessageCountMode().name());

		property = getMappedOrNewProperty(map, UserProperty.CARECONNECT_PPN_CHECK, providerNo);
		property.setValue(Boolean.toString(settings.isEnableCareConnectPPNCheck()));

		if (map.get("rx_use_rx3") != null)
		{
			settings.setUseRx3("yes".equals(map.get("rx_use_rx3").getValue()) ? true : false);
		}

		if (map.get("rx_show_patient_dob") != null)
		{
			settings.setShowPatientDob("yes".equals(map.get("rx_show_patient_dob").getValue()) ? true : false);
		}

		if (map.get("rx_default_quantity") != null)
		{
			settings.setRxDefaultQuantity(map.get("rx_default_quantity").getValue());
		}
		if (map.get("rx_page_size") != null)
		{
			settings.setRxPageSize(map.get("rx_page_size").getValue());
		}
		if (map.get("rxInteractionWarningLevel") != null)
		{
			settings.setRxInteractionWarningLevel(map.get("rxInteractionWarningLevel").getValue());
		}

		for (String key : map.keySet())
		{
			Property prop = map.get(key);
			if (prop.getValue() != null)
			{
				propertyDao.merge(prop);
			}
			// Specific case where we do actually wanna delete the value if it's null
			else if (prop.getName().equals("consultation_time_period_warning"))
			{
				propertyDao.remove(prop);
			}
		}
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

		if (!isProviderPreferenceEntry)
		{
			Property userPropList = propertyDao.findByNameAndProvider(key, providerNo);

			Property userProp;
			if (userPropList == null)
			{
				userProp = new Property();
				userProp.setProviderNo(providerNo);
				userProp.setName(key);
			}
			else
			{
				userProp = userPropList;
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
		if (preference == null)
		{
			preference = new ProviderPreference();
			preference.setProviderNo(providerNo);
		}

		switch (key)
		{
			case "startHour":
				preference.setStartHour(Integer.parseInt(value));
				break;
			case "endHour":
				preference.setEndHour(Integer.parseInt(value));
				break;
			case "everyMin":
				preference.setEveryMin(Integer.parseInt(value));
				break;
			case "myGroupNo":
				preference.setMyGroupNo(value);
				break;
			case "colourTemplate":
				preference.setColourTemplate(value);
				break;
			case "newTicklerWarningWindow":
				preference.setNewTicklerWarningWindow(value);
				break;
			case "defaultServiceType":
				preference.setDefaultServiceType(value);
				break;
			case "defaultCaisiPmm":
				preference.setDefaultCaisiPmm(value);
				break;
			case "defaultNewOscarCme":
				preference.setDefaultNewOscarCme(value);
				break;
			case "printQrCodeOnPrescriptions":
				preference.setPrintQrCodeOnPrescriptions(Boolean.parseBoolean(value));
				break;
			case "appointmentScreenLinkNameDisplayLength":
				preference.setAppointmentScreenLinkNameDisplayLength(Integer.parseInt(value));
				break;
			case "defaultDoNotDeleteBilling":
				preference.setDefaultDoNotDeleteBilling(Integer.parseInt(value));
				break;
			case "defaultDxCode":
				preference.setDefaultDxCode(value);
				break;
			default:
				return false;
		}

		providerPreferenceDao.merge(preference);
		return true;
	}
}
