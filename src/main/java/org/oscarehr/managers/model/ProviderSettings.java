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
package org.oscarehr.managers.model;

import lombok.Data;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.eform.service.EFormTemplateService;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class ProviderSettings
{
	private String recentPatients;
	private String rxAddress;
	private String rxCity;
	private String rxProvince;
	private String rxPostal;
	private String rxPhone;
	private String faxNumber;
	private String newTicklerWarningWindow;
	private String workloadManagement;
	private String ticklerWarningProvider;
	private Boolean ticklerViewOnlyMine;

	private boolean useCobaltOnLogin;

	private int startHour = 8;
	private int endHour = 18;
	private int period = 15;
	private String groupNo;
	private String siteSelected;
	private String viewSelected;
	private int appointmentScreenLinkNameDisplayLength = 3;
	private boolean hideOldEchartLinkInAppointment = true;

	private Collection<String> appointmentScreenForms = new ArrayList<String>();
	private Collection<Integer> appointmentScreenEforms = new ArrayList<Integer>();
	private Collection<org.oscarehr.managers.model.QuickLink> appointmentScreenQuickLinks = new ArrayList<org.oscarehr.managers.model.QuickLink>();

	private String defaultServiceType = "no";
	private String defaultDxCode;
	private boolean defaultDoNotDeleteBilling;

	private boolean useRx3 = true;
	private boolean showPatientDob;
	private boolean printQrCodeOnPrescription;

	private boolean eRxEnabled;
	private boolean eRxTrainingMode;

	private String eRxUsername;
	private String eRxPassword;
	private String eRxFacility;
	private String eRxURL;

	private String signature;
	private String rxDefaultQuantity;
	private String rxPageSize;
	private String rxInteractionWarningLevel = "0";

	private String defaultHcType = "";
	private String defaultSex = "";

	private String consultationTimePeriodWarning;
	private String consultationTeamWarning = "";
	private String consultationPasteFormat;
	private String consultationLetterHeadNameDefault;

	private boolean documentBrowserInDocumentReport;
	private boolean documentBrowserInMasterFile;

	private boolean cppSingleLine;
	
	private boolean summaryItemCustomDisplay;

	private boolean cppDisplayOngoingConcerns;
	private boolean cppOngoingConcernsStartDate;
	private boolean cppOngoingConcernsResDate;
	private boolean cppOngoingConcernsProblemStatus;

	private boolean cppDisplayMedHx;
	private boolean cppMedHxStartDate;
	private boolean cppMedHxResDate;
	private boolean cppMedHxTreatment;
	private boolean cppMedHxProcedureDate;

	private boolean cppDisplaySocialHx;
	private boolean cppSocialHxStartDate;
	private boolean cppSocialHxResDate;

	private boolean cppDisplayReminders;
	private boolean cppRemindersStartDate;
	private boolean cppRemindersResDate;

	private boolean summaryItemDisplayPreventions;
	private boolean summaryItemDisplayFamHx;
	private boolean summaryItemDisplayRiskFactors;
	private boolean summaryItemDisplayAllergies;

	private boolean summaryItemDisplayMeds;
	private boolean summaryItemDisplayOtherMeds;
	private boolean summaryItemDisplayAssessments;

	//private boolean summaryItemDisplayOutgoing;

	private boolean summaryItemDisplayIncoming;
	private boolean summaryItemDisplayDsSupport;

	private String cmeNoteDate = "A";
	private boolean cmeNoteFormat;
	private String quickChartSize;
	private String encounterWindowWidth;
	private String encounterWindowHeight;
	private boolean encounterWindowMaximize;

	private String favoriteFormGroup = "";
	private Integer eformPopupWidth = EFormTemplateService.EFORM_DEFAULT_WIDTH;
	private Integer eformPopupHeight = EFormTemplateService.EFORM_DEFAULT_HEIGHT;

	private boolean disableCommentOnAck;
	private boolean defaultPmm;

	private String olisDefaultReportingLab = "";
	private String olisDefaultExcludeReportingLab = "";
	private String myDrugRefId;
	private boolean useMyMeds;
	private boolean disableBornPrompts;

	private Integer patientNameLength;

	private boolean intakeFormEnabled;
	
	private ProviderPreference.AppointmentReasonDisplayLevel appointmentReasonDisplayLevel;

	private boolean appointmentCountEnabled;
	private boolean appointmentCountIncludeCancelled;
	private boolean appointmentCountIncludeNoShow;
	private boolean appointmentCountIncludeNoDemographic;

	private boolean enableCareConnectPPNCheck;
}
