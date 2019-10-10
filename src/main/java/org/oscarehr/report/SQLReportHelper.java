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
package org.oscarehr.report;

import org.apache.log4j.Logger;
import org.oscarehr.common.model.Explain;
import org.oscarehr.util.MiscUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLReportHelper
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final List<String> tableList;

	/**
	 * compare sql explain results with a maximum row cound and determine if the query is allowable
	 * @param explainResults - list of explain results
	 * @param maxRows - maximum rows to allow
	 * @return true if row counts examined by the query are within the maximum value limit, false otherwise
	 */
	public static boolean allowQueryRun(List<Explain> explainResults, long maxRows)
	{
		logger.info("Explain Results:\n" + explainResults.toString());

		for(Explain result : explainResults)
		{
			BigInteger rows = result.getRows();
			// if rows > maxRows
			if(rows != null && BigInteger.valueOf(maxRows).compareTo(rows) < 0)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * determine if a sql query can run without an explain step. some queries are allowed such as show tables, etc.
	 * @param sql - the query to check
	 * @return - true if the explain can be skipped, false otherwise
	 */
	public static String getExplainSkippableQuery(String sql)
	{
		String sqlTrimmed = sql.trim().replaceAll(";", "");
		String sqlTrimmedUpper = sqlTrimmed.toUpperCase();

		String queryString = null;

		// show tables can't be explained
		if("SHOW TABLES".equals(sqlTrimmedUpper))
		{
			queryString = "SHOW TABLES";
		}
		else if(sqlTrimmedUpper.startsWith("DESCRIBE"))
		{
			String[] queryWords = sqlTrimmed.split(" ");
			if(queryWords.length == 2 && isValidTable(queryWords[1]))
			{
				queryString = "DESCRIBE " + queryWords[1];
			}
		}
		else if(sqlTrimmedUpper.startsWith("SHOW COLUMNS FROM"))
		{
			String[] queryWords = sqlTrimmed.split(" ");
			if(queryWords.length == 4 && isValidTable(queryWords[3]))
			{
				queryString = "SHOW COLUMNS FROM " + queryWords[3];
			}
		}
		else if(sqlTrimmedUpper.startsWith("EXPLAIN"))
		{
			queryString = sqlTrimmed; // has no semi-colons so you can't run 2 queries
		}
		return queryString;
	}

	/** if a limit is found, check that it does not exceed the enforced maximum.
	 * if it does, replace the limit statement with the max
	 */
	public static String applyEnforcedLimit(String unlimitedSql, Integer maxLimit)
	{
		String maxLimitStr = " LIMIT " + maxLimit;
		String limitedSql;

		Pattern limitPattern = Pattern.compile("(\\s+LIMIT\\s+(\\d+))?((,|\\s+OFFSET\\s)\\s*(\\d+))?\\s*;?\\s*$", Pattern.CASE_INSENSITIVE);
		Matcher patternMatcher = limitPattern.matcher(unlimitedSql);

		if(patternMatcher.find())
		{
			StringBuffer sb = new StringBuffer(unlimitedSql.length());

			String limitStr = patternMatcher.group(1);
			String offsetStr = patternMatcher.group(3);

			// a limit is already specified.
			if(limitStr != null)
			{
				String limitNoStr = patternMatcher.group(2);
				Integer existingLimit = Integer.parseInt(limitNoStr);

				if(existingLimit > maxLimit) // existing limit exceeds max, replace it
				{
					if(offsetStr != null)
					{
						maxLimitStr += offsetStr;
					}
					patternMatcher.appendReplacement(sb, maxLimitStr);
					patternMatcher.appendTail(sb);
					limitedSql = sb.toString();
				}
				else // current limit is OK, no modification needed
				{
					limitedSql = unlimitedSql;
				}
			}
			else // no limit, we will add one
			{
				if(offsetStr != null)
				{
					maxLimitStr += offsetStr;
				}
				patternMatcher.appendReplacement(sb, maxLimitStr);
				patternMatcher.appendTail(sb);
				limitedSql = sb.toString();
			}
		}
		else
		{
			throw new RuntimeException("Unknown SQL syntax. unable to enforce required limit");
		}
		return limitedSql;
	}

	private static boolean isValidTable(String tableName)
	{
		return tableList.contains(tableName);
	}

	static
	{
		tableList = Arrays.asList(
				"AppDefinition",
				"AppUser",
				"BORNPathwayMapping",
				"BornTransmissionLog",
				"CdsClientForm",
				"CdsClientFormData",
				"CdsFormOption",
				"CdsHospitalisationDays",
				"ClientLink",
				"Consent",
				"Contact",
				"ContactSpecialty",
				"CtlRelationships",
				"DemographicContact",
				"Department",
				"DigitalSignature",
				"DrugDispensing",
				"DrugDispensingMapping",
				"DrugProduct",
				"DrugProductTemplate",
				"EFormReportTool",
				"Episode",
				"Eyeform",
				"EyeformConsultationReport",
				"EyeformFollowUp",
				"EyeformMacro",
				"EyeformOcularProcedure",
				"EyeformProcedureBook",
				"EyeformSpecsHistory",
				"EyeformTestBook",
				"Facility",
				"FaxClientLog",
				"FlowSheetUserCreated",
				"Flowsheet",
				"FunctionalCentre",
				"GroupNoteLink",
				"HL7HandlerMSHMapping",
				"HRMCategory",
				"HRMDocument",
				"HRMDocumentComment",
				"HRMDocumentSubClass",
				"HRMDocumentToDemographic",
				"HRMDocumentToProvider",
				"HRMProviderConfidentialityStatement",
				"HRMSubClass",
				"HnrDataValidation",
				"Icd9Synonym",
				"Institution",
				"InstitutionDepartment",
				"IntegratorConsent",
				"IntegratorConsentComplexExitInterview",
				"IntegratorConsentShareDataMap",
				"IntegratorControl",
				"IntegratorProgress",
				"IntegratorProgressItem",
				"IssueGroup",
				"IssueGroupIssues",
				"LookupList",
				"LookupListItem",
				"MyGroupAccessRestriction",
				"ORNCkdScreeningReportLog",
				"ORNPreImplementationReportLog",
				"OcanConnexOption",
				"OcanFormOption",
				"OcanStaffForm",
				"OcanStaffFormData",
				"OcanSubmissionLog",
				"OscarCode",
				"OscarJob",
				"OscarJobType",
				"PHRVerification",
				"PageMonitor",
				"PreventionsLotNrs",
				"PrintResourceLog",
				"ProductLocation",
				"ProviderPreference",
				"ProviderPreferenceAppointmentScreenEForm",
				"ProviderPreferenceAppointmentScreenForm",
				"ProviderPreferenceAppointmentScreenQuickLink",
				"RemoteDataLog",
				"RemoteIntegratedDataCopy",
				"RemoteReferral",
				"ResourceStorage",
				"SecurityArchive",
				"SecurityToken",
				"SentToPHRTracking",
				"ServiceAccessToken",
				"ServiceClient",
				"ServiceRequestToken",
				"SnomedCore",
				"SystemMessage",
				"access_type",
				"admission",
				"agency",
				"allergies",
				"app_lookuptable",
				"app_lookuptable_fields",
				"app_module",
				"appointment",
				"appointmentArchive",
				"appointmentType",
				"appointment_status",
				"batchEligibility",
				"batch_billing",
				"bed",
				"bed_check_time",
				"bed_demographic",
				"bed_demographic_historical",
				"bed_demographic_status",
				"bed_type",
				"bill_recipients",
				"billactivity",
				"billcenter",
				"billing",
				"billing_cdm_service_codes",
				"billing_history",
				"billing_msp_servicecode_times",
				"billing_on_3rdPartyAddress",
				"billing_on_cheader1",
				"billing_on_cheader2",
				"billing_on_diskname",
				"billing_on_eareport",
				"billing_on_errorCode",
				"billing_on_ext",
				"billing_on_favourite",
				"billing_on_filename",
				"billing_on_header",
				"billing_on_item",
				"billing_on_item_payment",
				"billing_on_payment",
				"billing_on_premium",
				"billing_on_proc",
				"billing_on_repo",
				"billing_on_transaction",
				"billing_payment_type",
				"billing_preferences",
				"billing_private_transactions",
				"billing_service_code_conditions",
				"billing_trayfees",
				"billingdetail",
				"billinginr",
				"billinglocation",
				"billingmaster",
				"billingmaster_clinicaid",
				"billingnote",
				"billingperclimit",
				"billingreferral",
				"billingservice",
				"billingstatus_types",
				"billingtypes",
				"billingvisit",
				"caisi_form",
				"caisi_form_data",
				"caisi_form_data_tmpsave",
				"caisi_form_instance",
				"caisi_form_instance_tmpsave",
				"caisi_form_question",
				"caisi_role",
				"casemgmt_cpp",
				"casemgmt_issue",
				"casemgmt_issue_notes",
				"casemgmt_note",
				"casemgmt_note_ext",
				"casemgmt_note_link",
				"casemgmt_note_lock",
				"casemgmt_tmpsave",
				"client_image",
				"client_referral",
				"clinic",
				"clinic_billing_address",
				"clinic_location",
				"clinic_nbr",
				"config_Immunization",
				"consentType",
				"consultResponseDoc",
				"consultationRequestExt",
				"consultationRequests",
				"consultationResponse",
				"consultationServices",
				"consultdocs",
				"country_codes",
				"cr_cert",
				"cr_machine",
				"cr_policy",
				"cr_securityquestion",
				"cr_user",
				"cr_userrole",
				"criteria",
				"criteria_selection_option",
				"criteria_type",
				"criteria_type_option",
				"cssStyles",
				"ctl_billingservice",
				"ctl_billingservice_age_rules",
				"ctl_billingservice_premium",
				"ctl_billingservice_sex_rules",
				"ctl_billingtype",
				"ctl_diagcode",
				"ctl_doc_class",
				"ctl_doctype",
				"ctl_document",
				"ctl_frequency",
				"ctl_servicecodes_dxcodes",
				"ctl_specialinstructions",
				"custom_filter",
				"custom_filter_assignees",
				"custom_filter_providers",
				"dashboard",
				"dashboard_report_view",
				"dataExport",
				"default_issue",
				"default_role_access",
				"demographic",
				"demographicArchive",
				"demographicExt",
				"demographicExtArchive",
				"demographicPharmacy",
				"demographicQueryFavourites",
				"demographicSets",
				"demographic_licensed_producer",
				"demographic_merged",
				"demographicaccessory",
				"demographiccust",
				"demographiccustArchive",
				"demographicstudy",
				"desannualreviewplan",
				"desaprisk",
				"diagnosticcode",
				"diseases",
				"doc_category",
				"doc_manager",
				"document",
				"documentDescriptionTemplate",
				"document_storage",
				"drugReason",
				"drugs",
				"dsGuidelineProviderMap",
				"dsGuidelines",
				"dxCodeTranslations",
				"dx_associations",
				"dxresearch",
				"eChart",
				"eform",
				"eform_data",
				"eform_groups",
				"eform_instance",
				"eform_values",
				"encounter",
				"encounterForm",
				"encounterWindow",
				"encountertemplate",
				"eyeform_macro_billing",
				"eyeform_macro_def",
				"facility_message",
				"favorites",
				"favoritesprivilege",
				"fax_account",
				"fax_config",
				"fax_inbound",
				"fax_outbound",
				"faxes",
				"fileUploadCheck",
				"flowsheet_customization",
				"flowsheet_drug",
				"flowsheet_dx",
				"form",
				"form2MinWalk",
				"formAR",
				"formAdf",
				"formAdfV2",
				"formAlpha",
				"formAnnual",
				"formAnnualV2",
				"formBCAR",
				"formBCAR2007",
				"formBCAR2012",
				"formBCBirthSumMo",
				"formBCBirthSumMo2008",
				"formBCClientChartChecklist",
				"formBCHP",
				"formBCINR",
				"formBCNewBorn",
				"formBCNewBorn2008",
				"formBPMH",
				"formCESD",
				"formCaregiver",
				"formConsult",
				"formCostQuestionnaire",
				"formCounseling",
				"formDischargeSummary",
				"formFalls",
				"formGripStrength",
				"formGrowth0_36",
				"formGrowthChart",
				"formHomeFalls",
				"formImmunAllergy",
				"formIntakeHx",
				"formIntakeInfo",
				"formInternetAccess",
				"formLabReq",
				"formLabReq07",
				"formLabReq10",
				"formLateLifeFDIDisability",
				"formLateLifeFDIFunction",
				"formMMSE",
				"formMentalHealth",
				"formMentalHealthForm1",
				"formMentalHealthForm14",
				"formMentalHealthForm42",
				"formNoShowPolicy",
				"formONAR",
				"formONAREnhanced",
				"formONAREnhancedRecord",
				"formONAREnhancedRecordExt1",
				"formONAREnhancedRecordExt2",
				"formPalliativeCare",
				"formPeriMenopausal",
				"formPositionHazard",
				"formRhImmuneGlobulin",
				"formRourke",
				"formRourke2006",
				"formRourke2009",
				"formSF36",
				"formSF36Caregiver",
				"formSatisfactionScale",
				"formSelfAdministered",
				"formSelfAssessment",
				"formSelfEfficacy",
				"formSelfManagement",
				"formTreatmentPref",
				"formType2Diabetes",
				"formVTForm",
				"formWCB",
				"form_hsfo2_visit",
				"formchf",
				"formfollowup",
				"formovulation",
				"formreceptionassessment",
				"frm_labreq_preset",
				"functional_user_type",
				"groupMembers_tbl",
				"groups_tbl",
				"gstControl",
				"hash_audit",
				"health_safety",
				"hl7TextInfo",
				"hl7TextMessage",
				"hl7_embedded_document_link",
				"hl7_link",
				"hl7_message",
				"hl7_msh",
				"hl7_obr",
				"hl7_obx",
				"hl7_orc",
				"hl7_pid",
				"hsfo2_patient",
				"hsfo2_system",
				"hsfo_recommit_schedule",
				"icd9",
				"ichppccode",
				"immunizations",
				"incomingLabRules",
				"indicatorTemplate",
				"indivoDocs",
				"intake",
				"intake_answer",
				"intake_answer_element",
				"intake_answer_validation",
				"intake_node",
				"intake_node_js",
				"intake_node_label",
				"intake_node_template",
				"intake_node_type",
				"issue",
				"joint_admissions",
				"labPatientPhysicianInfo",
				"labReportInformation",
				"labRequestReportLink",
				"labTestResults",
				"licensed_producer",
				"licensed_producer_address",
				"log",
				"log_emails",
				"log_letters",
				"log_report_by_template",
				"log_report_by_template_explain",
				"log_teleplantx",
				"log_ws_rest",
				"log_ws_soap",
				"lst_admission_status",
				"lst_discharge_reason",
				"lst_field_category",
				"lst_gender",
				"lst_organization",
				"lst_orgcd",
				"lst_program_type",
				"lst_sector",
				"lst_service_restriction",
				"mdsMSH",
				"mdsNTE",
				"mdsOBR",
				"mdsOBX",
				"mdsPID",
				"mdsPV1",
				"mdsZCL",
				"mdsZCT",
				"mdsZFR",
				"mdsZLB",
				"mdsZMC",
				"mdsZMN",
				"mdsZRG",
				"measurementCSSLocation",
				"measurementGroup",
				"measurementGroupStyle",
				"measurementMap",
				"measurementType",
				"measurementTypeDeleted",
				"measurements",
				"measurementsDeleted",
				"measurementsExt",
				"messagelisttbl",
				"messagetbl",
				"msgDemoMap",
				"mygroup",
				"oncall_questionnaire",
				"oscarKeys",
				"oscar_annotations",
				"oscar_msg_type",
				"oscarcommlocations",
				"other_id",
				"partial_date",
				"patientLabRouting",
				"pharmacyInfo",
				"pmm_log",
				"prescribe",
				"prescription",
				"preventions",
				"preventionsExt",
				"professionalSpecialists",
				"program",
				"programSignature",
				"program_access",
				"program_access_roles",
				"program_client_restriction",
				"program_clientstatus",
				"program_functional_user",
				"program_provider",
				"program_provider_team",
				"program_queue",
				"program_team",
				"property",
				"provider",
				"providerArchive",
				"providerExt",
				"providerLabRouting",
				"providerLabRoutingFavorites",
				"provider_default_program",
				"provider_facility",
				"provider_recent_demographic_access",
				"providerbillcenter",
				"providersite",
				"providerstudy",
				"publicKeys",
				"queue",
				"queue_document_link",
				"quickList",
				"quickListUser",
				"radetail",
				"raheader",
				"recycle_bin",
				"recyclebin",
				"rehabStudy2004",
				"relationships",
				"remoteAttachments",
				"report",
				"reportByExamples",
				"reportByExamples_explain",
				"reportByExamplesFavorite",
				"reportConfig",
				"reportFilter",
				"reportItem",
				"reportTableFieldCaption",
				"reportTemplates",
				"report_date",
				"report_date_sp",
				"report_doctext",
				"report_document",
				"report_filter",
				"report_letters",
				"report_lk_reportgroup",
				"report_option",
				"report_qgviewfield",
				"report_qgviewsummary",
				"report_role",
				"report_template",
				"report_template_criteria",
				"report_template_org",
				"reportagesex",
				"reportprovider",
				"reporttemp",
				"resident_oscarMsg",
				"room",
				"room_bed",
				"room_bed_historical",
				"room_demographic",
				"room_type",
				"rschedule",
				"scheduledate",
				"scheduleholiday",
				"scheduletemplate",
				"scheduletemplatecode",
				"scratch_pad",
				"secObjPrivilege",
				"secObjectName",
				"secPrivilege",
				"secRole",
				"secUserRole",
				"security",
				"serviceSpecialists",
				"sharing_acl_definition",
				"sharing_actor",
				"sharing_affinity_domain",
				"sharing_clinic_info",
				"sharing_code_mapping",
				"sharing_code_value",
				"sharing_document_export",
				"sharing_exported_doc",
				"sharing_infrastructure",
				"sharing_mapping_code",
				"sharing_mapping_edoc",
				"sharing_mapping_eform",
				"sharing_mapping_misc",
				"sharing_mapping_site",
				"sharing_patient_document",
				"sharing_patient_network",
				"sharing_patient_policy_consent",
				"sharing_policy_definition",
				"sharing_value_set",
				"site",
				"specialistsJavascript",
				"specialty",
				"study",
				"studydata",
				"studylogin",
				"survey",
				"surveyData",
				"survey_test_data",
				"survey_test_instance",
				"table_modification",
				"teleplanC12",
				"teleplanS00",
				"teleplanS21",
				"teleplanS22",
				"teleplanS23",
				"teleplanS25",
				"teleplan_adj_codes",
				"teleplan_refusal_code",
				"teleplan_response_log",
				"teleplan_submission_link",
				"tickler",
				"tickler_category",
				"tickler_comments",
				"tickler_link",
				"tickler_text_suggest",
				"tickler_update",
				"uploadfile_from",
				"user_ds_message_prefs",
				"vacancy",
				"vacancy_client_match",
				"vacancy_template",
				"validations",
				"view",
				"waitingList",
				"waitingListName",
				"wcb",
				"wcb_bp_code",
				"wcb_noi_code",
				"wcb_side",
				"workflow"
		);
	}
}
