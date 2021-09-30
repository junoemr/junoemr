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


package org.oscarehr.security.model;

import lombok.Data;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.model.AbstractModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name="secObjectName")
public class SecObjectName extends AbstractModel<String>
{
	public enum OBJECT_NAME
	{
		ADMIN("_admin"), // indicates access to all admin sub-permissions below
		ADMIN_BACKUP("_admin.backup"),
		ADMIN_BILLING("_admin.billing"),
		ADMIN_CAISI("_admin.caisi"),
		ADMIN_CAISI_ROLES("_admin.caisiRoles"),
		ADMIN_CONSULT("_admin.consult"),
		ADMIN_COOKIE_REVOLVER("_admin.cookieRevolver"),
		ADMIN_DOCUMENT("_admin.document"),
		ADMIN_EFORM("_admin.eform"),
		ADMIN_EFORM_REPORT_TOOL("_admin.eformreporttool"),
		ADMIN_ENCOUNTER("_admin.encounter"),
		ADMIN_FACILITY_MESSAGE("_admin.facilityMessage"),
		ADMIN_FIELD_NOTE("_admin.fieldnote"),
		ADMIN_INVOICES("_admin.invoices"),
		ADMIN_ISSUE_EDITOR("_admin.issueEditor"),
		ADMIN_LOOKUP_FIELD_EDITOR("_admin.lookupFieldEditor"),
		ADMIN_MEASUREMENTS("_admin.measurements"),
		ADMIN_MESSENGER("_admin.messenger"),
		ADMIN_PANEL_MANAGEMENT("_admin.panelManagement"),
		ADMIN_PROVIDER("_admin.provider"),
		ADMIN_REPORTING("_admin.reporting"),
		ADMIN_SCHEDULE("_admin.schedule"),
		ADMIN_SECURITY("_admin.security"),
		ADMIN_SECURITY_LOG_REPORT("_admin.securityLogReport"),
		ADMIN_SHARING_CENTER("_admin.sharingcenter"),
		ADMIN_SYSTEM_MESSAGE("_admin.systemMessage"),
		ADMIN_TRACEABILITY("_admin.traceability"),
		ADMIN_UNLOCK_ACCOUNT("_admin.unlockAccount"),
		ADMIN_USER_ADMIN( "_admin.userAdmin"),
		ADMIN_USER_CREATED_FORMS("_admin.userCreatedForms"),

		ALLERGY("_allergy"),
		APP_DEFINITION("_appDefinition"),
		APPOINTMENT("_appointment"),
		APPOINTMENT_DOCTOR_LINK("_appointment.doctorLink"),
		AQS_QUEUE_CONFIG("_aqs.queueConfig"),
		AQS_QUEUED_APPOINTMENTS("_aqs.queuedAppointments"),
		BILLING("_billing"),

		CASELOAD_A1C("_caseload.A1C"),
		CASELOAD_ACCESS_ADMISSION_DATE("_caseload.Access1AdmissionDate"),
		CASELOAD_ACR("_caseload.ACR"),
		CASELOAD_AGE("_caseload.Age"),
		CASELOAD_APPOINTMENTS_LYTD("_caseload.ApptsLYTD"),
		CASELOAD_BMI("_caseload.BMI"),
		CASELOAD_BP("_caseload.BP"),
		CASELOAD_CASH_ADMISSION_DATE("_caseload.CashAdmissionDate"),
		CASELOAD_DISPLAY_MODE("_caseload.DisplayMode"),
		CASELOAD_DOC("_caseload.Doc"),
		CASELOAD_EGFR("_caseload.EGFR"),
		CASELOAD_EYEE("_caseload.EYEE"),
		CASELOAD_HDL("_caseload.HDL"),
		CASELOAD_LAB("_caseload.Lab"),
		CASELOAD_LAST_APPOINTMENT("_caseload.LastAppt"),
		CASELOAD_LAST_ENCOUNTER_DATE("_caseload.LastEncounterDate"),
		CASELOAD_LAST_ENCOUNTER_TYPE("_caseload.LastEncounterType"),
		CASELOAD_LDL("_caseload.LDL"),
		CASELOAD_MSG("_caseload.Msg"),
		CASELOAD_NEXT_APPOINTMENT("_caseload.NextAppt"),
		CASELOAD_SEX("_caseload.Sex"),
		CASELOAD_SMK("_caseload.SMK"),
		CASELOAD_TCHD("_caseload.TCHD"),
		CASELOAD_TICKLER("_caseload.Tickler"),
		CASELOAD_WT("_caseload.WT"),

		CASEMGMT_ISSUES("_casemgmt.issues"),
		CASEMGMT_NOTES("_casemgmt.notes"),

		CONSULTATION("_con"),
		DASHBOARD_DISPLAY("_dashboardDisplay"),
		DASHBOARD_DRILLDOWN("_dashboardDrilldown"),
		DASHBOARD_MANAGER("_dashboardManager"),
		DAY("_day"),
		DEMOGRAPHIC("_demographic"),
		DEMOGRAPHIC_EXPORT("_demographicExport"),
		DEMOGRAPHIC_IMPORT("_demographicImport"),
		DXRESEARCH("_dxresearch"),
		ECHART("_eChart"),
		ECHART_VERIFY_BUTTON("_eChart.verifyButton"),
		EDOC("_edoc"),
		EFORM("_eform"),
		EFORM_DOCTOR("_eform.doctor"),
		EYEFORM("_eyeform"),
		FAX_CONFIG("_admin.fax"),
		FAX_DOCUMENTS("_fax.documents"),
		FLOWSHEET("_flowsheet"),
		FORM("_form"),
		HRM("_hrm"),
		LAB("_lab"),
		MASTER_LINK("_masterLink"),
		MEASUREMENT("_measurement"),
		MONTH("_month"),
		MESSAGE("_msg"),

		NEW_CASEMGMT_ALLERGIES("_newCasemgmt.allergies"),
		NEW_CASEMGMT_APPOINTMENT_HISTORY("_newCasemgmt.apptHistory"),
		NEW_CASEMGMT_CALCULATORS("_newCasemgmt.calculators"),
		NEW_CASEMGMT_CONSULTATIONS("_newCasemgmt.consultations"),
		NEW_CASEMGMT_CPP("_newCasemgmt.cpp"),
		NEW_CASEMGMT_DECISION_SUPPORT_ALERTS("_newCasemgmt.decisionSupportAlerts"),
		NEW_CASEMGMT_DOCTOR_NAME("_newCasemgmt.doctorName"),
		NEW_CASEMGMT_DOCUMENTS("_newCasemgmt.documents"),
		NEW_CASEMGMT_DX_REGISTRY("_newCasemgmt.DxRegistry"),
		NEW_CASEMGMT_EFORMS("_newCasemgmt.eForms"),
		NEW_CASEMGMT_EPISODE("_newCasemgmt.episode"),
		NEW_CASEMGMT_FAMILY_HISTORY("_newCasemgmt.familyHistory"),
		NEW_CASEMGMT_FORMS("_newCasemgmt.forms"),
		NEW_CASEMGMT_LAB_RESULT("_newCasemgmt.labResult"),
		NEW_CASEMGMT_MEASUREMENT("_newCasemgmt.measurements"),
		NEW_CASEMGMT_MEDICAL_HISTORY("_newCasemgmt.medicalHistory"),
		NEW_CASEMGMT_OSCAR_MESSAGE("_newCasemgmt.oscarMsg"),
		NEW_CASEMGMT_OTHER_MEDS("_newCasemgmt.otherMeds"),
		NEW_CASEMGMT_PHOTO("_newCasemgmt.photo"),
		NEW_CASEMGMT_PREGNANCY("_newCasemgmt.pregnancy"),
		NEW_CASEMGMT_PRESCRIPTIONS("_newCasemgmt.prescriptions"),
		NEW_CASEMGMT_PREVENTIONS("_newCasemgmt.preventions"),
		NEW_CASEMGMT_RISK_FACTORS("_newCasemgmt.riskFactors"),
		NEW_CASEMGMT_TEMPLATES("_newCasemgmt.templates"),
		NEW_CASEMGMT_VIEW_TICKLER("_newCasemgmt.viewTickler"),

		PHR("_phr"),

		PMM_ADD_PROGRAM("_pmm.addProgram"),
		PMM_AGENCY_INFO("_pmm.agencyInformation"),
		PMM_CAISI_ROLES("_pmm.caisiRoles"),
		PMM_CASE_MANAGEMENT("_pmm.caseManagement"),
		PMM_CLIENT_SEARCH("_pmm.clientSearch"),
		PMM_EDITOR("_pmm.editor"),
		PMM_GLOBAL_ROLE_ACCESS("_pmm.globalRoleAccess"),
		PMM_MANAGE_FACILITIES("_pmm.manageFacilities"),
		PMM_MERGE_RECORDS("_pmm.mergeRecords"),
		PMM_NEW_CLIENT("_pmm.newClient"),
		PMM_PROGRAM_LIST("_pmm.programList"),
		PMM_STAFF_LIST("_pmm.staffList"),
		PMM_AGENCY_LIST("_pmm_agencyList"),
		PMM_CLIENT_BED_ROOM_RESERVATION("_pmm_client.BedRoomReservation"),
		PMM_EDIT_PROGRAM_ACCESS("_pmm_editProgram.access"),
		PMM_EDIT_PROGRAM_BED_CHECK("_pmm_editProgram.bedCheck"),
		PMM_EDIT_PROGRAM_CLIENTS("_pmm_editProgram.clients"),
		PMM_EDIT_PROGRAM_CLIENT_STATUS("_pmm_editProgram.clientStatus"),
		PMM_EDIT_PROGRAM_FUNCTION_USER("_pmm_editProgram.functionUser"),
		PMM_EDIT_PROGRAM_GENERAL("_pmm_editProgram.general"),
		PMM_EDIT_PROGRAM_QUEUE("_pmm_editProgram.queue"),
		PMM_EDIT_PROGRAM_SERVICE_RESTRICTIONS("_pmm_editProgram.serviceRestrictions"),
		PMM_EDIT_PROGRAM_STAFF("_pmm_editProgram.staff"),
		PMM_EDIT_PROGRAM_TEAMS("_pmm_editProgram.teams"),
		PMM_EDIT_PROGRAM_VACANCIES("_pmm_editProgram.vacancies"),

		PREF("_pref"),
		PREVENTION("_prevention"),
		REPORT("_report"),
		RESOURCE("_resource"),
		RX("_rx"),
		RX_DISPENSE("_rx.dispense"),
		SEARCH("_search"),
		TASKS("_tasks"),
		TICKLER("_tickler");

		private final String value;

		OBJECT_NAME(String value)
		{
			this.value = value;
		}

		public String getValue()
		{
			return this.value;
		}

		public static OBJECT_NAME fromStringIgnoreCase(String enumString)
		{
			if(EnumUtils.isValidEnumIgnoreCase(OBJECT_NAME.class, enumString))
			{
				return OBJECT_NAME.valueOf(enumString.toUpperCase());
			}
			return null;
		}

		public static OBJECT_NAME fromValueString(String value)
		{
			for(OBJECT_NAME name : OBJECT_NAME.values())
			{
				if(name.getValue().equalsIgnoreCase(value))
				{
					return name;
				}
			}
			return null;
		}
	}

	@Id
	@Column(name="objectName")
	private String id;

	private String description;

	@Column(name="orgapplicable")
	private Boolean orgApplicable;

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "secObjectName")
//	private List<SecObjPrivilege> secObjPrivileges;

	@Override
	public String getId()
	{
		return id;
	}
}
