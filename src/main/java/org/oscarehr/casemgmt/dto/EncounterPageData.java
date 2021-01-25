/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.casemgmt.dto;

import com.quatro.model.security.Secrole;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;

import java.util.Date;
import java.util.List;

public class EncounterPageData
{
	private String userColour;
	private String inverseUserColour;
	private String roleName;
	private String familyDoctorColour;
	private String familyDoctorFirstName;
	private String familyDoctorLastName;
	private String windowName;
	private String demographicUrl;
	private String demographicNo;
	private String providerNo;
	private String patientFirstName;
	private String patientLastName;
	private String patientSex;
	private String patientAge;
	private String patientAgeInYears;
	private String patientBirthdate;
	private String patientPhone;
	private boolean isEchartAdditionalPatientInfoEnabled;
	private String demographicAdditionalInfoUrl;
	private String referringDoctorName;
	private String referringDoctorNumber;
	private boolean rosterDateEnabled;
	private String rosterDateString;
	private boolean isIncomingRequestorSet;
	private String diseaseListUrl;
	private String echartUuid;
	private String echartLinks;
	private String imagePresentPlaceholderUrl;
	private String imageMissingPlaceholderUrl;
	private String appointmentNo;
	private String source;
	private Date encounterNoteHideBeforeDate;
	private String billingUrl;
	private String cmeJs;
	private List<EncounterTemplate> encounterTemplates;
	private List<CaseManagementIssueTo1> caseManagementIssues;
	private List<Provider> providers;
	private List<Secrole> roles;
	private boolean encounterWindowCustomSize;
	private String encounterWindowHeight;
	private String encounterWindowWidth;
	private boolean encounterWindowMaximize;
	private boolean clientImagePresent;
	private boolean linkToOldEncounterPageEnabled;


	public String getUserColour()
	{
		return userColour;
	}

	public void setUserColour(String userColour)
	{
		this.userColour = userColour;
	}

	public String getInverseUserColour()
	{
		return inverseUserColour;
	}

	public void setInverseUserColour(String inverseUserColour)
	{
		this.inverseUserColour = inverseUserColour;
	}

	public String getRoleName()
	{
		return roleName;
	}

	public void setRoleName(String roleName)
	{
		this.roleName = roleName;
	}

	public String getFamilyDoctorColour()
	{
		return familyDoctorColour;
	}

	public void setFamilyDoctorColour(String familyDoctorColour)
	{
		this.familyDoctorColour = familyDoctorColour;
	}

	public String getFamilyDoctorFirstName()
	{
		return familyDoctorFirstName;
	}

	public void setFamilyDoctorFirstName(String familyDoctorFirstName)
	{
		this.familyDoctorFirstName = familyDoctorFirstName;
	}

	public String getFamilyDoctorLastName()
	{
		return familyDoctorLastName;
	}

	public void setFamilyDoctorLastName(String familyDoctorLastName)
	{
		this.familyDoctorLastName = familyDoctorLastName;
	}

	public String getWindowName()
	{
		return windowName;
	}

	public void setWindowName(String windowName)
	{
		this.windowName = windowName;
	}

	public String getDemographicUrl()
	{
		return demographicUrl;
	}

	public void setDemographicUrl(String demographicUrl)
	{
		this.demographicUrl = demographicUrl;
	}

	public String getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(String demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getPatientFirstName()
	{
		return patientFirstName;
	}

	public void setPatientFirstName(String patientFirstName)
	{
		this.patientFirstName = patientFirstName;
	}

	public String getPatientLastName()
	{
		return patientLastName;
	}

	public void setPatientLastName(String patientLastName)
	{
		this.patientLastName = patientLastName;
	}

	public String getPatientSex()
	{
		return patientSex;
	}

	public void setPatientSex(String patientSex)
	{
		this.patientSex = patientSex;
	}

	public String getPatientAge()
	{
		return patientAge;
	}

	public void setPatientAge(String patientAge)
	{
		this.patientAge = patientAge;
	}

	public String getPatientAgeInYears()
	{
		return patientAgeInYears;
	}

	public void setPatientAgeInYears(String patientAgeInYears)
	{
		this.patientAgeInYears = patientAgeInYears;
	}

	public String getPatientBirthdate()
	{
		return patientBirthdate;
	}

	public void setPatientBirthdate(String patientBirthdate)
	{
		this.patientBirthdate = patientBirthdate;
	}

	public String getPatientPhone()
	{
		return patientPhone;
	}

	public void setPatientPhone(String patientPhone)
	{
		this.patientPhone = patientPhone;
	}

	public boolean isEchartAdditionalPatientInfoEnabled()
	{
		return isEchartAdditionalPatientInfoEnabled;
	}

	public void setEchartAdditionalPatientInfoEnabled(boolean echartAdditionalPatientInfoEnabled)
	{
		isEchartAdditionalPatientInfoEnabled = echartAdditionalPatientInfoEnabled;
	}

	public String getDemographicAdditionalInfoUrl()
	{
		return demographicAdditionalInfoUrl;
	}

	public void setDemographicAdditionalInfoUrl(String demographicAdditionalInfoUrl)
	{
		this.demographicAdditionalInfoUrl = demographicAdditionalInfoUrl;
	}

	public String getReferringDoctorName()
	{
		return referringDoctorName;
	}

	public void setReferringDoctorName(String referringDoctorName)
	{
		this.referringDoctorName = referringDoctorName;
	}

	public String getReferringDoctorNumber()
	{
		return referringDoctorNumber;
	}

	public void setReferringDoctorNumber(String referringDoctorNumber)
	{
		this.referringDoctorNumber = referringDoctorNumber;
	}

	public boolean isRosterDateEnabled()
	{
		return rosterDateEnabled;
	}

	public void setRosterDateEnabled(boolean rosterDateEnabled)
	{
		this.rosterDateEnabled = rosterDateEnabled;
	}

	public String getRosterDateString()
	{
		return rosterDateString;
	}

	public void setRosterDateString(String rosterDateString)
	{
		this.rosterDateString = rosterDateString;
	}

	public boolean isIncomingRequestorSet()
	{
		return isIncomingRequestorSet;
	}

	public void setIncomingRequestorSet(boolean incomingRequestorSet)
	{
		isIncomingRequestorSet = incomingRequestorSet;
	}

	public String getDiseaseListUrl()
	{
		return diseaseListUrl;
	}

	public void setDiseaseListUrl(String diseaseListUrl)
	{
		this.diseaseListUrl = diseaseListUrl;
	}

	public String getEchartUuid()
	{
		return echartUuid;
	}

	public void setEchartUuid(String echartUuid)
	{
		this.echartUuid = echartUuid;
	}

	public String getEchartLinks()
	{
		return echartLinks;
	}

	public void setEchartLinks(String echartLinks)
	{
		this.echartLinks = echartLinks;
	}

	public String getImagePresentPlaceholderUrl()
	{
		return imagePresentPlaceholderUrl;
	}

	public void setImagePresentPlaceholderUrl(String imagePresentPlaceholderUrl)
	{
		this.imagePresentPlaceholderUrl = imagePresentPlaceholderUrl;
	}

	public String getImageMissingPlaceholderUrl()
	{
		return imageMissingPlaceholderUrl;
	}

	public void setImageMissingPlaceholderUrl(String imageMissingPlaceholderUrl)
	{
		this.imageMissingPlaceholderUrl = imageMissingPlaceholderUrl;
	}

	public String getAppointmentNo()
	{
		return appointmentNo;
	}

	public void setAppointmentNo(String appointmentNo)
	{
		this.appointmentNo = appointmentNo;
	}

	public String getSource()
	{
		return source;
	}

	public void setSource(String source)
	{
		this.source = source;
	}

	public Date getEncounterNoteHideBeforeDate()
	{
		return encounterNoteHideBeforeDate;
	}

	public void setEncounterNoteHideBeforeDate(Date encounterNoteHideBeforeDate)
	{
		this.encounterNoteHideBeforeDate = encounterNoteHideBeforeDate;
	}

	public String getBillingUrl()
	{
		return billingUrl;
	}

	public void setBillingUrl(String billingUrl)
	{
		this.billingUrl = billingUrl;
	}

	public String getCmeJs()
	{
		return cmeJs;
	}

	public void setCmeJs(String cmeJs)
	{
		this.cmeJs = cmeJs;
	}

	public List<EncounterTemplate> getEncounterTemplates()
	{
		return encounterTemplates;
	}

	public void setEncounterTemplates(List<EncounterTemplate> encounterTemplates)
	{
		this.encounterTemplates = encounterTemplates;
	}

	public List<CaseManagementIssueTo1> getCaseManagementIssues()
	{
		return caseManagementIssues;
	}

	public void setCaseManagementIssues(List<CaseManagementIssueTo1> caseManagementIssues)
	{
		this.caseManagementIssues = caseManagementIssues;
	}

	public List<Provider> getProviders()
	{
		return providers;
	}

	public void setProviders(List<Provider> providers)
	{
		this.providers = providers;
	}

	public List<Secrole> getRoles()
	{
		return roles;
	}

	public void setRoles(List<Secrole> roles)
	{
		this.roles = roles;
	}

	public boolean isEncounterWindowCustomSize()
	{
		return encounterWindowCustomSize;
	}

	public void setEncounterWindowCustomSize(boolean encounterWindowCustomSize)
	{
		this.encounterWindowCustomSize = encounterWindowCustomSize;
	}

	public String getEncounterWindowHeight()
	{
		return encounterWindowHeight;
	}

	public void setEncounterWindowHeight(String encounterWindowHeight)
	{
		this.encounterWindowHeight = encounterWindowHeight;
	}

	public String getEncounterWindowWidth()
	{
		return encounterWindowWidth;
	}

	public void setEncounterWindowWidth(String encounterWindowWidth)
	{
		this.encounterWindowWidth = encounterWindowWidth;
	}

	public boolean isEncounterWindowMaximize()
	{
		return encounterWindowMaximize;
	}

	public void setEncounterWindowMaximize(boolean encounterWindowMaximize)
	{
		this.encounterWindowMaximize = encounterWindowMaximize;
	}

	public boolean isClientImagePresent()
	{
		return clientImagePresent;
	}

	public void setClientImagePresent(boolean clientImagePresent)
	{
		this.clientImagePresent = clientImagePresent;
	}

	public boolean isLinkToOldEncounterPageEnabled()
	{
		return linkToOldEncounterPageEnabled;
	}

	public void setLinkToOldEncounterPageEnabled(boolean linkToOldEncounterPageEnabled)
	{
		this.linkToOldEncounterPageEnabled = linkToOldEncounterPageEnabled;
	}

	//=============================================================================
	// Data formatting methods
	//=============================================================================

	public String getFormattedFamilyDoctorName()
	{
		return this.familyDoctorFirstName.toUpperCase() + " " + this.familyDoctorLastName.toUpperCase();
	}

	public String getFormattedPatientName()
	{
		return patientLastName +", " + patientFirstName;
	}
	public String getFormattedPatientInfo()
	{
		return patientSex + " " + patientAge;
	}
}
