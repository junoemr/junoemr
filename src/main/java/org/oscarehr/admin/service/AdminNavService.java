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

package org.oscarehr.admin.service;

import org.apache.axis2.transport.http.util.URIEncoderDecoder;
import org.opensaml.xmlsec.signature.P;
import org.oscarehr.common.model.Security;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.to.model.AdminNavGroupTo1;
import org.oscarehr.ws.rest.to.model.AdminNavItemTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;
import oscar.OscarProperties;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class AdminNavService
{
	@Autowired
	private SecurityInfoManager securityInfoManager;

	private OscarProperties oscarProperties = OscarProperties.getInstance();

	/**
	 * construct a list of admin nav group transfer objects. This group is passed to the adminController.js to build the ui.
	 * @param resourceBundle - a resource bundle from which to pull message strings
	 * @param providerNo - the provider viewing this resources
	 * @return - a list of admin nav group objects
	 */
	public List<AdminNavGroupTo1> getAdminNavGroups(String contextPath, ResourceBundle resourceBundle, String providerNo)
	{
		List<AdminNavGroupTo1> adminNavList = new ArrayList<>();

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.userAdmin" , "_admin.provider" ))
		{
			adminNavList.add(getAdminNavUserManagementGroup(contextPath, resourceBundle));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin.invoices", "_admin", "_admin.billing"))
		{
			adminNavList.add(getAdminNavBilling(contextPath, resourceBundle));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin"))
		{
			adminNavList.add(getAdminNavLab(contextPath, resourceBundle));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.eform"))
		{
			adminNavList.add(getAdminNavForms(contextPath, resourceBundle));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.reporting"))
		{
			adminNavList.add(getAdminNavReports(contextPath, resourceBundle, providerNo));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.encounter"))
		{
			adminNavList.add(getAdminNavEchart(contextPath, resourceBundle));
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, securityInfoManager.READ, null, "_admin", "_admin.schedule"))
		{
			adminNavList.add(getAdminNavSchedule(contextPath, resourceBundle));
		}
		if (oscarProperties.isPropertyActive("caisi"))
		{// CAISI module loaded
			if (securityInfoManager.hasPrivilege(providerNo, "_admin.caisi", SecurityInfoManager.READ, null))
			{
				adminNavList.add(getAdminNavCaisiHasPermission(contextPath, resourceBundle));
			}
			else
			{
				adminNavList.add(getAdminNavCaisiNoPermission(contextPath, resourceBundle, providerNo));
			}
		}
		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.measurements", "_admin.document", "_admin.consult"))
		{
			adminNavList.add(getAdminNavSystemManagement(contextPath, resourceBundle, providerNo));
		}

		return adminNavList;
	}

	/**
	 * get the user management admin nav group
	 * @param contextPath - the instance context path
	 * @param resourceBundle - a resource bundle from which to pull message strings
	 * @return - the adminNavGroup for the user management list.
	 */
	private AdminNavGroupTo1 getAdminNavUserManagementGroup(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 userManagement = new AdminNavGroupTo1();
		List<AdminNavItemTo1> userManagementItems = new ArrayList<>();

		userManagement.setName(resourceBundle.getString("admin.admin.UserManagement"));

		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddProvider"), "frame?frameUrl=" + contextPath + "/admin/provideraddarecordhtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSearchProvider"), "frame?frameUrl=" + contextPath + "/admin/providersearchrecordshtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddLogin"), "frame?frameUrl=" + contextPath + "/admin/securityaddarecord.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSearchLogin"), "frame?frameUrl=" + contextPath + "/admin/providersearchrecordshtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.assignRole"), "frame?frameUrl=" + contextPath + "/admin/providerRole.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.unlockAcct"), "frame?frameUrl=" + contextPath + "/admin/unLock.jsp"));

		userManagement.setItems(userManagementItems);
		return userManagement;
	}

	/**
	 * get the billing admin nav group
	 * @param contextPath - the instance context path
	 * @param resourceBundle - a resource bundle from which to pull message strings
	 * @return - the admin nav group for the billing section
	 */
	private AdminNavGroupTo1 getAdminNavBilling(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 billing = new AdminNavGroupTo1();
		List<AdminNavItemTo1> billingItems = new ArrayList<>();

		billing.setName(resourceBundle.getString("admin.admin.billing"));

		if (oscarProperties.isClinicaidBillingType())
		{// CLINICAID BILLING
			billingItems.add(new AdminNavItemTo1("Manage Invoices", "frame?frameUrl=" + contextPath + "/billing.do?billRegion=CLINICAID&action=invoice_reports"));

			if (oscarProperties.isBritishColumbiaBillingType())
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageReferralDoc"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingManageReferralDoc.jsp&useCompat=true"));
			}
		}
		else if (oscarProperties.isBritishColumbiaBillingType())
		{// BC BILLING
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageBillFrm"), "frame?frameUrl=" + contextPath + "/billing/manageBillingform.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManagePrivFrm"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingPrivateCodeAdjust.jsp"));

			if (oscarProperties.isPropertyActive("BC_BILLING_CODE_MANAGEMENT"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageBillCodes"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingCodeAdjust.jsp"));
			}

			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageServiceDiagnosticCodeAssoc"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/showServiceCodeAssocs.do"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageProcedureFeeCodeAssoc"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/supServiceCodeAssocAction.do"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ManageReferralDoc"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingManageReferralDoc.jsp&useCompat=true"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.bcQuickBilling"), "frame?frameUrl=" + contextPath + "/quickBillingBC.do"));

			if (oscarProperties.isPropertyActive("NEW_BC_TELEPLAN"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.simulateSubFile2"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/TeleplanSimulation.jsp"));
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.genTeleplanFile2"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/TeleplanSubmission.jsp"));
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.manageTeleplan"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/teleplan/ManageTeleplan.jsp"));
			}

			if (!oscarProperties.isPropertyActive("NEW_BC_TELEPLAN"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.SimulateSubFile"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingSim.jsp"));
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.genTeleplanFile"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingTeleplanGroupReport.jsp"));
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.uploadRemittance"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingTA.jsp"));
			}

			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.reconciliationReports"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/viewReconcileReports.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.AccountingRpts"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billingAccountReports.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.editInvoices"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/billStatus.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.settlePaidClaims"), "frame?frameUrl=" + contextPath + "/billing/CA/BC/settleBG.jsp"));
		}
		else if (oscarProperties.isOntarioBillingType())
		{
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.scheduleOfBenefits"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/ScheduleOfBenefitsUpload.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.manageBillingServiceCode"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/addEditServiceCode.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.managePrivBillingCode"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingONEditPrivateCode.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.manageCodeStyles"), "frame?frameUrl=" + contextPath + "/admin/manageCSSStyles.do"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.manageGSTControl"), "frame?frameUrl=" + contextPath + "/admin/gstControl.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.gstReport"), "frame?frameUrl=" + contextPath + "/admin/gstreport.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddBillingLocation"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/manageBillingLocation.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnManageBillingForm"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/manageBillingform.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSimulationOHIPDiskette"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingOHIPsimulation.jsp&useCompat=true"));// TODO Fix
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnGenerateOHIPDiskette"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingOHIPreport.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnBillingCorrection"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingCorrection.jsp?admin&billing_no="));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnBatchBilling"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/batchBilling.jsp?service_code=all"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnINRBatchBilling"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/inr/reportINR.jsp?provider_no=all"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.uploadMOHFile"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingONUpload.jsp"));

			if (oscarProperties.isPropertyActive("moh_file_management_enabled"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.viewMOHFiles"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/viewMOHFiles.jsp"));
			}

			if (!oscarProperties.isPropertyActive("mcedt.mailbox.enabled"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.mcedt"), "frame?frameUrl=" + contextPath + "/mcedt/mcedt.do"));
			}

			if (oscarProperties.isPropertyActive("mcedt.mailbox.enabled"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.mcedt.mailbox"), "frame?frameUrl=" + contextPath + "/mcedt/kaimcedt.do"));
			}

			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnBillingReconciliation"), "frame?frameUrl=" + contextPath + "/servlet/oscar.DocumentUploadServlet"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.invoiceRpts"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billStatus.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.endYearStatement"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/endYearStatement.do"));

			if (oscarProperties.isPropertyActive("rma_enabled"))
			{
				billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.endYearStatement"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/endYearStatement.do"));
			}

			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.paymentReceived"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/billingONPayment.jsp"));
			billingItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.managePaymentType"), "frame?frameUrl=" + contextPath + "/billing/CA/ON/managePaymentType.do"));
		}

		billing.setItems(billingItems);
		return billing;
	}

	private AdminNavGroupTo1 getAdminNavLab(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 labGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> labItems = new ArrayList<>();

		labGroup.setName(resourceBundle.getString("admin.admin.LabsInbox"));

		labItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.hl7LabUpload"), "frame?frameUrl=" + contextPath + "/lab/CA/ALL/testUploader.jsp"));

		if (oscarProperties.isPropertyActive("OLD_LAB_UPLOAD"))
		{
			labItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.oldLabUpload"), "frame?frameUrl=" + contextPath + "/lab/CA/BC/LabUpload.jsp"));
		}

		labItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.labFwdRules"), "frame?frameUrl=" + contextPath + "/admin/labforwardingrules.jsp&useCompat=true"));
		labItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.AddNewQueue"), "frame?frameUrl=" + contextPath + "/admin/addQueue.jsp&useCompat=true"));

		labGroup.setItems(labItems);
		return labGroup;
	}

	private AdminNavGroupTo1 getAdminNavForms(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 formGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> formItems = new ArrayList<>();

		formGroup.setName(resourceBundle.getString("admin.admin.FormsEforms"));

		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSelectForm"), "frame?frameUrl=" + contextPath + "/form/setupSelect.do&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnImportFormData"), "frame?frameUrl=" + contextPath + "/form/formXmlUpload.jsp&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("eform.showmyform.msgManageEFrm"), "frame?frameUrl=" + contextPath + "/eform/efmformmanager.jsp&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnUploadImage"), "frame?frameUrl=" + contextPath + "/eform/efmimagemanager.jsp&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.frmGroups"), "frame?frameUrl=" + contextPath + "/eform/efmmanageformgroups.jsp&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.richTextLetter"), "frame?frameUrl=" + contextPath + "/eform/efmformrtl_config.jsp&useCompat=true"));
		formItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.frmIndependent"), "frame?frameUrl=" + contextPath + "/eform/efmmanageindependent.jsp&useCompat=true"));

		formGroup.setItems(formItems);
		return formGroup;
	}

	private AdminNavGroupTo1 getAdminNavReports(String contextPath, ResourceBundle resourceBundle, String providerNo)
	{
		AdminNavGroupTo1 reportGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> reportItems = new ArrayList<>();

		reportGroup.setName(resourceBundle.getString("admin.admin.oscarReport"));

		if (oscarProperties.isPropertyActive("enable_dashboards") && oscarProperties.isBritishColumbiaInstanceType() &&
			securityInfoManager.hasPrivilege(providerNo, "_dashboardManager", SecurityInfoManager.WRITE, null))
		{
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("dashboard.dashboardmanager.title"), "frame?frameUrl=" + contextPath + "/web/dashboard/admin/DashboardManager.do"));
		}

		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnQueryByExample"), "frame?frameUrl=" + contextPath + "/oscarReport/RptByExample.do"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.rptbyTemplate"), "frame?frameUrl=" + contextPath + "/oscarReport/reportByTemplate/homePage.jsp"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAgeSexReport"), "frame?frameUrl=" + contextPath + "/oscarReport/dbReportAgeSex.jsp"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnVisitReport"), "frame?frameUrl=" + contextPath + "/oscarReport/oscarReportVisitControl.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1("PCN", "frame?frameUrl=" + contextPath + "/oscarReport/oscarReportCatchment.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnFluBillingReport"), "frame?frameUrl=" + contextPath + "/oscarReport/FluBilling.do&useCompat=true"));
		reportItems.add(new AdminNavItemTo1("Overnight\t\t\t\t\t\t\tBatch", "frame?frameUrl=" + contextPath + "/oscarReport/obec.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.report.SurveillanceReport"), "frame?frameUrl=" + contextPath + "/oscarSurveillance/ReportSurveillance.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.rehabStudy"), "frame?frameUrl=" + contextPath + "/oscarReport/oscarReportRehabStudy.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.exportPatientbyAppt"), "frame?frameUrl=" + contextPath + "/oscarReport/patientlist.jsp&useCompat=true"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.providerServiceRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/provider_service_report_form.jsp&useCompat=true"));

		if (oscarProperties.isPropertyActive("caisi"))
		{// CAISI module loaded
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.activityRpt"), "frame?frameUrl=" + contextPath + "/PMmodule/Reports/ProgramActivityReport"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.popRpt"), "frame?frameUrl=" + contextPath + "/PopulationReport.do&useCompat=true"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.cdsRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/cds_4_report_form.jsp&useCompat=true"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.misRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/mis_report_form.jsp&useCompat=true"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ocanRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/ocan_report_form.jsp&useCompat=true"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ocanIarRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/ocan_iar.jsp&useCompat=true"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.ocanReporting"), "frame?frameUrl=" + contextPath + "/oscarReport/ocan_reporting.js"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.cbiSubmit"), "frame?frameUrl=" + contextPath + "/oscarReport/cbi_submit_form.jsp"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.cbi.reportlink"), "frame?frameUrl=" + contextPath + "/admin/cbiAdmin.jsp"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.cbiRpt"), "frame?frameUrl=" + contextPath + "/oscarReport/cbi_report_form.jsp"));
		}

		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.usageRpt"), "frame?frameUrl=" + contextPath + "/admin/UsageReport.jsp&useCompat=true"));
		if (oscarProperties.isPropertyActive("SERVERLOGGING"))
		{
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.serverLog"), "frame?frameUrl=" + contextPath + "/admin/oscarLogging.jsp&useCompat=true"));
		}
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.DiseaseRegistry"), "frame?frameUrl=" + contextPath + "/report/DxresearchReport.do"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnStudy"), "frame?frameUrl=" + contextPath + "/admin/demographicstudy.jsp"));

		if (oscarProperties.isPropertyActive("eaaps.enabled"))
		{// eaaps module loaded
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnEaaps"), "frame?frameUrl=" + contextPath + "/eaaps/index.jsp"));
		}

		if (oscarProperties.isOntarioBillingType())
		{
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.PHCP"), "frame?frameUrl=" + contextPath + "/report/reportonbilledphcp.jsp"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.provider"), "frame?frameUrl=" + contextPath + "/report/reportonbilledvisitprovider.jsp"));
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.dx"), "frame?frameUrl=" + contextPath + "/report/reportonbilleddxgrp.jsp"));
		}

		reportItems.add(new AdminNavItemTo1("CKD Screening Report (async)", "frame?frameUrl=" + contextPath + "/renal/ckdScreeningReportSubmit.jsp"));
		reportItems.add(new AdminNavItemTo1("Pre-Implementation Report", "frame?frameUrl=" + contextPath + "/renal/preImplementationSubmit.jsp"));
		reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.renal.managePatientLetter"), "frame?frameUrl=" + contextPath + "/renal/patientLetterManager.jsp"));

		if (securityInfoManager.hasPrivilege(providerNo, "_admin.fieldnote", SecurityInfoManager.READ, null))
		{
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.fieldNoteReport"), "frame?frameUrl=" + contextPath + "/eform/fieldNoteReport/fieldnotereport.jsp"));
		}

		if (securityInfoManager.hasPrivilege(providerNo, "_admin.eformreporttool", SecurityInfoManager.READ, null))
		{
			reportItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.eformReportTool"), "frame?frameUrl=" + contextPath + "/admin/eformReportTool/eformReportTool.jsp"));
		}

		reportGroup.setItems(reportItems);
		return reportGroup;
	}

	private AdminNavGroupTo1 getAdminNavEchart(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 echartGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> echartItems = new ArrayList<>();

		echartGroup.setName(resourceBundle.getString("admin.admin.eChart"));

		echartItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnInsertTemplate"), "frame?frameUrl=" + contextPath + "/admin/providertemplate.jsp"));

		echartGroup.setItems(echartItems);
		return echartGroup;
	}

	private AdminNavGroupTo1 getAdminNavSchedule(String contextPath, ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 scheduleGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> scheduleItems = new ArrayList<>();

		scheduleGroup.setName(resourceBundle.getString("admin.admin.ScheduleManagement"));

		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.scheduleSetting"), "frame?frameUrl=" + contextPath + "/schedule/scheduletemplatesetting.jsp"));
		if (oscarProperties.isPropertyActive("ENABLE_EDIT_APPT_STATUS"))
		{
			scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.appointmentStatusSetting"), "frame?frameUrl=" + contextPath + "/appointment/appointmentstatuscontrol.jsp"));
		}

		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.appointmentTypeList"), "frame?frameUrl=" + contextPath + "/appointment/appointmentTypeAction.do"));
		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddGroupNoRecord"), "frame?frameUrl=" + contextPath + "/admin/adminnewgroup.jsp"));
		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSearchGroupNoRecords"), "frame?frameUrl=" + contextPath + "/admin/admindisplaymygroup.jsp"));
		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnGroupNoAcl"), "frame?frameUrl=" + contextPath + "/admin/groupnoacl.jsp"));
		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnGroupPreference"), "frame?frameUrl=" + contextPath + "/admin/groupPreferences.jsp"));
		scheduleItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.preventionNotification.title"), "frame?frameUrl=" + contextPath + "/oscarPrevention/PreventionManager.jsp"));

		scheduleGroup.setItems(scheduleItems);
		return scheduleGroup;
	}

	private AdminNavGroupTo1 getAdminNavCaisiHasPermission(String contextPath, ResourceBundle resourcebundle)
	{
		AdminNavGroupTo1 caisiGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> caisiItems = new ArrayList<>();

		caisiGroup.setName(resourcebundle.getString("admin.admin.caisi"));

		caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.systemMessage"), "frame?frameUrl=" + contextPath + "/SystemMessage.do"));
		caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.FacilitiesMsgs"), "frame?frameUrl=" + contextPath + "/FacilityMessage.do"));
		caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.issueEditor"), "frame?frameUrl=" + contextPath + "/issueAdmin.do"));
		caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.surveyManager"), "frame?frameUrl=" + contextPath + "/SurveyManager.do"));
		caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.defaultEncounterIssue"), "frame?frameUrl=" + contextPath + "/DefaultEncounterIssue.do"));

		caisiGroup.setItems(caisiItems);
		return caisiGroup;
	}

	private AdminNavGroupTo1 getAdminNavCaisiNoPermission(String contextPath, ResourceBundle resourcebundle, String providerNo)
	{
		AdminNavGroupTo1 caisiGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> caisiItems = new ArrayList<>();

		caisiGroup.setName(resourcebundle.getString("admin.admin.caisi"));

		if (securityInfoManager.hasPrivilege(providerNo, "_admin.systemMessage", SecurityInfoManager.READ, null))
		{
			caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.systemMessage"), "frame?frameUrl=" + contextPath + "/SystemMessage.do"));
		}
		if (securityInfoManager.hasPrivilege(providerNo, "_admin.facilityMessage", SecurityInfoManager.READ, null))
		{
			caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.FacilitiesMsgs"), "frame?frameUrl=" + contextPath + "/FacilityMessage.do"));
		}
		if (securityInfoManager.hasPrivilege(providerNo, "_admin.lookupFieldEditor", SecurityInfoManager.READ, null))
		{
			caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.LookupFieldEditor"), "frame?frameUrl=" + contextPath + "/Lookup/LookupTableList.do"));
		}
		if (securityInfoManager.hasPrivilege(providerNo, "_admin.issueEditor", SecurityInfoManager.READ, null))
		{
			caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.issueEditor"), "frame?frameUrl=" + contextPath + "/issueAdmin.do"));
		}
		if(securityInfoManager.hasPrivilege(providerNo, "_admin.userCreatedForms", SecurityInfoManager.READ, null))
		{
			caisiItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.surveyManager"), "frame?frameUrl=" + contextPath + "/SurveyManager.do"));
		}

		caisiGroup.setItems(caisiItems);
		return caisiGroup;
	}

	private AdminNavGroupTo1 getAdminNavSystemManagement(String contextPath, ResourceBundle resourcebundle, String providerNo)
	{
		AdminNavGroupTo1 systemManagementGroup = new AdminNavGroupTo1();
		List<AdminNavItemTo1> systemManagementItems = new ArrayList<>();

		systemManagementGroup.setName(resourcebundle.getString("admin.admin.SystemManagement"));

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.userAdmin"))
		{
			systemManagementItems.add(new AdminNavItemTo1(resourcebundle.getString("admin.admin.addRole"), "frame?frameUrl=" + contextPath + "/admin/providerAddRole.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.document"))
		{
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("admin.admin.DocumentCategories"), "frame?frameUrl=" + contextPath + "/admin/displayDocumentCategories.jsp"));
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("admin.admin.DocumentDescriptionTemplate"), "frame?frameUrl=" + contextPath + "/admin/displayDocumentDescriptionTemplate.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin"))
		{
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("admin.admin.clinicAdmin"), "frame?frameUrl=" + contextPath + "/admin/ManageClinic.do"));
			if (oscarProperties.isMultisiteEnabled())
			{
				systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("admin.admin.sitesAdmin"), "frame?frameUrl=" + contextPath + "/admin/ManageSites.do"));
			}
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("oscarEncounter.Index.btnCustomize") + resourcebundle.getString("oscar.admin.diseaseRegistryQuickList"),
					"frame?frameUrl=" + contextPath + "/oscarResearch/oscarDxResearch/dxResearchCustomization.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.consult"))
		{
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("admin.admin.consultationSettings"), "frame?frameUrl=" + contextPath + "/oscarEncounter/oscarConsultationRequest/config/EditSpecialists.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.measurements"))
		{
			systemManagementItems.add( new AdminNavItemTo1(resourcebundle.getString("oscarEncounter.Index.btnCustomize") + resourcebundle.getString("admin.admin.oscarMeasurements"),
					"frame?frameUrl=" + contextPath + "/oscarEncounter/oscarMeasurements/Customization.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin"))
		{
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("oscarprevention.preventionlistmanager.title"), "frame?frameUrl=" + contextPath + "/oscarPrevention/PreventionListManager.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin"))
		{
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.btnBaseURLSetting"), "frame?frameUrl=" + contextPath + "/admin/resourcebaseurl.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.messenger"))
		{
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.messages"),
					"frame?frameUrl=" + URLEncoder.encode(contextPath + "/oscarMessenger/DisplayMessages.do?providerNo=" + providerNo)));// TODO fix (probably broken, requires params)
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.btnMessengerAdmin"), "frame?frameUrl=" + contextPath + "/oscarMessenger/config/MessengerAdmin.jsp"));
		}

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin"))
		{
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.keyPairGen"), "frame?frameUrl=" + contextPath + "/admin/keygen/keyManager.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.manageFacilities"), "frame?frameUrl=" + contextPath + "/FacilityManager.do"));
			systemManagementItems.add( new AdminNavItemTo1( "Create New Flowsheet", "frame?frameUrl=" + contextPath + "/oscarEncounter/oscarMeasurements/adminFlowsheet/NewFlowsheet.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.flowsheetManager"), "frame?frameUrl=" + contextPath + "/admin/manageFlowsheets.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.add_lot_nr.title"), "frame?frameUrl=" + contextPath + "/admin/lotnraddrecordhtm.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.lotnrsearchrecordshtm.title"), "frame?frameUrl=" + contextPath + "/admin/lotnrsearchrecordshtm.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.jobs.title"), "frame?frameUrl=" + contextPath + "/admin/jobs.jsp"));
			systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.jobtypes.title"), "frame?frameUrl=" + contextPath + "/admin/jobTypes.jsp"));

			if (oscarProperties.isPropertyActive("LOGINTEST"))
			{
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.uploadEntryTxt"), "frame?frameUrl=" + contextPath + "/admin/uploadEntryText.jsp"));
			}
		}

		if (oscar.oscarSecurity.CRHelper.isCRFrameworkEnabled())
		{
			if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin.cookieRevolver"))
			{
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.ipFilter"), "frame?frameUrl=" + contextPath + "/gatekeeper/ip/show"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.setCert"), "frame?frameUrl=" + contextPath + "/gatekeeper/cert/"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.genCert"), "frame?frameUrl=" + contextPath + "/gatekeeper/supercert"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.clearCookie"), "frame?frameUrl=" + contextPath + "/gatekeeper/clear"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.adminSecQuestions"), "frame?frameUrl=" + contextPath + "/gatekeeper/quest/adminQuestions"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.adminSecPolicies"), "frame?frameUrl=" + contextPath + "/gatekeeper/policyadmin/select"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.removeBans"), "frame?frameUrl=" + contextPath + "/gatekeeper/banremover/show"));
				systemManagementItems.add( new AdminNavItemTo1( resourcebundle.getString("admin.admin.genMatrixCards"), "frame?frameUrl=" + contextPath + "/gatekeeper/matrixadmin/show"));
			}
		}

		systemManagementGroup.setItems(systemManagementItems);
		return systemManagementGroup;
	}
}
