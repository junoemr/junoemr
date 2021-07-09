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

import java.text.SimpleDateFormat;
import java.util.List;

import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.common.model.Property;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PreferenceManager {
	
	@Autowired
	private ProviderManager2 providerManager;
	
	public static final String SOCHX = "SocHistory";
	public static final String MEDHX = "MedHistory";
	public static final String CONCERNS = "Concerns";
	public static final String REMINDERS = "Reminders";
	
	//NEW - summary.item.custom.display
	public static final String CUSTOM_SUMMARY_ENABLE = "cpp.pref.enable";
	
	
	public static final String OLD_SOCIAL_HISTORY_POS = "cpp.social_hx.position";
	public static final String OLD_MEDICAL_HISTORY_POS = "cpp.medical_hx.position";
	public static final String OLD_ONGOING_CONCERNS_POS = "cpp.ongoing_concerns.position";
	public static final String OLD_REMINDERS_POS = "cpp.reminders.position";
	
	/*
	 * 
	 * if .position dosne't exist = on
	 * if .position exists and not equal "" = on
	 * ignore the R* position eg: R1I1 R1I2 R2I1 R2I2
	 * 
	 * so setting position in old ui will have not effect on the new ui
	 * and when setting position in new ui it will have no effect on the old ui
	 *  
	 * 
	 */
	public static final String SOC_HX_POS = "summary.item.social_hx.position";
	public static final String MED_HX_POS = "summary.item.med_hx.position";
	public static final String ONGOING_POS = "summary.item.ongoing_concerns.position";
	public static final String REMINDERS_POS = "summary.item.reminders.position";
	
	public static final String SOC_HX_START_DATE = "cpp.social_hx.start_date";
	public static final String SOC_HX_RES_DATE = "cpp.social_hx.res_date";
	public static final String MED_HX_START_DATE = "cpp.med_hx.start_date";
	public static final String MED_HX_RES_DATE = "cpp.med_hx.res_date";
	public static final String MED_HX_TREATMENT = "cpp.med_hx.treatment";
	public static final String MED_HX_PROCEDURE_DATE = "cpp.med_hx.procedure_date";
	public static final String ONGOING_START_DATE = "cpp.ongoing_concerns.start_date";
	public static final String ONGOING_RES_DATE = "cpp.ongoing_concerns.res_date";
	public static final String ONGOING_PROBLEM_STATUS = "cpp.ongoing_concerns.problem_status";
	public static final String REMINDERS_START_DATE = "cpp.reminders.start_date";
	public static final String REMINDERS_RES_DATE = "cpp.reminders.res_date";
		
	public static final String PREVENTION_POS = "summary.item.prevention.position";
	public static final String FAM_HX_POS = "summary.item.famhx.position";
	public static final String RISK_FACTORS_POS = "summary.item.riskfactors.position";
	public static final String ALLERGIES_POS = "summary.item.allergies.position";
	public static final String MEDS_POS = "summary.item.meds.position";
	public static final String OTHER_MEDS_POS = "summary.item.othermeds.position";
	public static final String ASSESSMENTS_POS = "summary.item.assessments.position";
	public static final String INCOMING_POS = "summary.item.incoming.position";
	public static final String DS_SUPPORT_POS = "summary.item.dssupport.position";
	
	
	public boolean displaySummaryItem(LoggedInInfo loggedInInfo, String item){
		if(isCustomSummaryEnabled(loggedInInfo))
		{
			Property results = providerManager.getProviderProperties(loggedInInfo, loggedInInfo.getLoggedInProviderNo(), item);
			if(results != null)
			{
				String value = results.getValue();
				return !value.isEmpty() && !value.equals("off");
			}
			else
			{
				//check if the old cpp position property exist
				return isOldCppPosition(loggedInInfo, item);
			}
		}
		// default these to false instead
		return !DS_SUPPORT_POS.equals(item);
	}
	
	private boolean isOldCppPosition(LoggedInInfo loggedInInfo, String property){
		if(property.equals(SOC_HX_POS)){
			return displaySummaryItem(loggedInInfo, OLD_SOCIAL_HISTORY_POS);
		}else if(property.equals(MED_HX_POS)){
			return displaySummaryItem(loggedInInfo, OLD_MEDICAL_HISTORY_POS);
		}else if(property.equals(ONGOING_POS)){
			return displaySummaryItem(loggedInInfo, OLD_ONGOING_CONCERNS_POS);
		}else if(property.equals(REMINDERS_POS)){
			return displaySummaryItem(loggedInInfo, OLD_REMINDERS_POS);
		}
		
		return true;
	}

	/**
	 * Build CPP note extension for the given issue code and any augmenting information for a CPP note.
	 * @param loggedInInfo provider whose preferences we need to take into consideration
	 * @param noteExtList augmenting information for whatever note we're building a summary for
	 * @param issueCode the issue in question
	 * @return a String to append to a CPP note for displaying
	 */
	public String getCppExtsItem(LoggedInInfo loggedInInfo, List<CaseManagementNoteExt> noteExtList, String issueCode)
	{
		StringBuilder stringBuilder = new StringBuilder();
		// Append any relevant bits of info depending on the issue in question
		if(issueCode.contains(CONCERNS))
		{
			stringBuilder = getConcernsExtItems(loggedInInfo, noteExtList);
		}
		else if(issueCode.contains(MEDHX))
		{
			stringBuilder = getMedHistoryExtItems(loggedInInfo, noteExtList);
		}
		else if(issueCode.contains(SOCHX))
		{
			stringBuilder = getSocHistoryExtItems(loggedInInfo, noteExtList);
		}
		else if(issueCode.contains(REMINDERS))
		{
			stringBuilder = getRemindersExtItems(loggedInInfo, noteExtList);
		}

		// If we got something back, finish building the string
		if (stringBuilder.length() > 0)
		{
			stringBuilder.insert(0, " (");
			stringBuilder.append(")");
		}
		
		return stringBuilder.toString();
	}
	
	static String getNoteExt(String key, List<CaseManagementNoteExt> lcme) {
		for (CaseManagementNoteExt cme : lcme) {
			if (cme.getKeyVal().equals(key)) {
				String val = null;

				if (key.contains(" Date")) {
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					val = formatter.format(cme.getDateValue());
				} else {
					val = cme.getValue();
				}
				return val;
			}
		}
		return "";
	}


	/**
	 * Build MedHistory note extension according to logged in provider's preferences.
	 * @param loggedInInfo provider who is asking for CPP notes
	 * @param noteExtList any additional info the note may have with it
	 * @return an in-progress StringBuilder object with whichever sections the logged in provider wants.
	 */
	protected StringBuilder getMedHistoryExtItems(LoggedInInfo loggedInInfo, List<CaseManagementNoteExt> noteExtList)
	{
		StringBuilder cppExtendedNote = new StringBuilder();

		if(isCustomCppItemOn(loggedInInfo, MED_HX_START_DATE))
		{
			cppExtendedNote.append("Start Date:").append(getNoteExt("Start Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, MED_HX_RES_DATE))
		{
			cppExtendedNote.append(" Resolution Date:").append(getNoteExt("Resolution Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, MED_HX_TREATMENT))
		{
			cppExtendedNote.append(" Treatment:").append(getNoteExt("Treatment", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, MED_HX_PROCEDURE_DATE))
		{
			cppExtendedNote.append(" Procedure Date:").append(getNoteExt("Procedure Date", noteExtList));
		}

		return cppExtendedNote;
	}

	/**
	 * Build SocHistory note extension according to logged in provider's preferences.
	 * @param loggedInInfo provider who is asking for CPP notes
	 * @param noteExtList any additional info the note may have with it
	 * @return an in-progress StringBuilder object with whichever sections the logged in provider wants.
	 */
	protected StringBuilder getSocHistoryExtItems(LoggedInInfo loggedInInfo, List<CaseManagementNoteExt> noteExtList)
	{
		StringBuilder cppExtendedNote = new StringBuilder();
		if(isCustomCppItemOn(loggedInInfo, SOC_HX_START_DATE))
		{
			cppExtendedNote.append("Start Date:").append(getNoteExt("Start Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, SOC_HX_RES_DATE))
		{
			cppExtendedNote.append(" Resolution Date:").append(getNoteExt("Resolution Date", noteExtList));
		}

		return cppExtendedNote;
	}

	/**
	 * Build Reminders note extension according to logged in provider's preferences.
	 * @param loggedInInfo provider who is asking for CPP notes
	 * @param noteExtList any additional info the note may have with it
	 * @return an in-progress StringBuilder object with whichever sections the logged in provider wants.
	 */
	protected StringBuilder getRemindersExtItems(LoggedInInfo loggedInInfo, List<CaseManagementNoteExt> noteExtList)
	{
		StringBuilder cppExtendedNote = new StringBuilder();

		if(isCustomCppItemOn(loggedInInfo, REMINDERS_START_DATE))
		{
			cppExtendedNote.append("Start Date:").append(getNoteExt("Start Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, REMINDERS_RES_DATE))
		{
			cppExtendedNote.append(" Resolution Date:").append(getNoteExt("Resolution Date", noteExtList));
		}

		return cppExtendedNote;
	}

	/**
	 * Build Concerns note extension according to logged in provider's preferences.
	 * @param loggedInInfo provider who is asking for CPP notes
	 * @param noteExtList any additional info the note may have with it
	 * @return an in-progress StringBuilder object with whichever sections the logged in provider wants.
	 */
	protected StringBuilder getConcernsExtItems(LoggedInInfo loggedInInfo, List<CaseManagementNoteExt> noteExtList)
	{
		StringBuilder cppExtendedNote = new StringBuilder();
		if(isCustomCppItemOn(loggedInInfo, ONGOING_START_DATE))
		{
			cppExtendedNote.append("Start Date:").append(getNoteExt("Start Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, ONGOING_RES_DATE))
		{
			cppExtendedNote.append(" Resolution Date:").append(getNoteExt("Resolution Date", noteExtList));
		}

		if(isCustomCppItemOn(loggedInInfo, ONGOING_PROBLEM_STATUS))
		{
			cppExtendedNote.append(" Problem Status:").append(getNoteExt("Problem Status", noteExtList));
		}

		return cppExtendedNote;
	}

	/**
	 * Given a string that looks like an issue code, check to see whether our base issue code exists in there.
	 *
	 * Strings may take either of these forms: "{ISSUEID}{ISSUECODE}" or "ISSUECODE" and we want to check both.
	 * @param issueCode string representing a potential issue item
	 * @return true if it contains text relating to any of the given identified issues, false otherwise
	 */
	public boolean isCppItem(String issueCode)
	{
		return (issueCode.contains(SOCHX)
				|| issueCode.contains(MEDHX)
				|| issueCode.contains(CONCERNS)
				|| issueCode.contains(REMINDERS));

	}


	public boolean isCustomSummaryEnabled(LoggedInInfo loggedInInfo){
		Property results = providerManager.getProviderProperties(loggedInInfo, loggedInInfo.getLoggedInProviderNo(), CUSTOM_SUMMARY_ENABLE);
		
		if(results != null)
		{
			String value = results.getValue();
			return value.equals("on");
		}
		
		return false;		
	}
	
	private boolean isCustomCppItemOn(LoggedInInfo loggedInInfo, String propertyName){
		Property results = providerManager.getProviderProperties(loggedInInfo, loggedInInfo.getLoggedInProviderNo(), propertyName);
		
		if(results != null)
		{
			String value = results.getValue();
			return value.equals("on");
		}
		return false;
	}
	
	public String getProviderPreference(LoggedInInfo loggedInInfo, String propertyName){
		Property results = providerManager.getProviderProperties(loggedInInfo, loggedInInfo.getLoggedInProviderNo(), propertyName);

		if(results != null)
		{
			return results.getValue();
		}
		
		return null;
	}
	
}