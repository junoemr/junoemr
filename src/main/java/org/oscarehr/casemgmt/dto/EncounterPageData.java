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
import lombok.Data;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;

import java.util.Date;
import java.util.List;

@Data
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
	private boolean isCareConnectEnabled;
	private boolean isImdHealthEnabled;

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