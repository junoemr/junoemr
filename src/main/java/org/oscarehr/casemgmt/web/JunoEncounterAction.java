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

package org.oscarehr.casemgmt.web;

import com.quatro.model.security.Secrole;
import com.quatro.service.security.RolesManager;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.billing.CA.service.BillingUrlService;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.exception.EncounterSectionException;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.casemgmt.service.CaseManagementIssueService;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.casemgmt.service.ClientImageManager;
import org.oscarehr.casemgmt.service.EncounterAllergyService;
import org.oscarehr.casemgmt.service.EncounterConsultationService;
import org.oscarehr.casemgmt.service.EncounterDiseaseRegistryService;
import org.oscarehr.casemgmt.service.EncounterDocumentService;
import org.oscarehr.casemgmt.service.EncounterEFormService;
import org.oscarehr.casemgmt.service.EncounterEpisodeService;
import org.oscarehr.casemgmt.service.EncounterFamilyHistoryService;
import org.oscarehr.casemgmt.service.EncounterFormService;
import org.oscarehr.casemgmt.service.EncounterLabResultService;
import org.oscarehr.casemgmt.service.EncounterMeasurementsService;
import org.oscarehr.casemgmt.service.EncounterMedicalHistoryService;
import org.oscarehr.casemgmt.service.EncounterMedicationService;
import org.oscarehr.casemgmt.service.EncounterMessengerService;
import org.oscarehr.casemgmt.service.EncounterOngoingConcernsService;
import org.oscarehr.casemgmt.service.EncounterOtherMedsService;
import org.oscarehr.casemgmt.service.EncounterRemindersService;
import org.oscarehr.casemgmt.service.EncounterResolvedIssueService;
import org.oscarehr.casemgmt.service.EncounterRiskFactorsService;
import org.oscarehr.casemgmt.service.EncounterSectionService;
import org.oscarehr.casemgmt.service.EncounterService;
import org.oscarehr.casemgmt.service.EncounterPreventionNoteService;
import org.oscarehr.casemgmt.service.EncounterSocialHistoryService;
import org.oscarehr.casemgmt.service.EncounterTeamService;
import org.oscarehr.casemgmt.service.EncounterTicklerService;
import org.oscarehr.casemgmt.service.EncounterUnresolvedIssueService;
import org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean;
import org.oscarehr.common.dao.EncounterTemplateDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.EncounterTemplate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.struts.DispatchActionSupport;
import oscar.OscarProperties;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.pageUtil.EctSessionBean;
import oscar.util.UtilDateUtilities;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class JunoEncounterAction extends DispatchActionSupport
{
	private static final String ACTION_FORWARD_VIEW = "view";
	private static final String ACTION_FORWARD_ERROR = "error";

	private static final String DEFAULT_APPOINTMENT_VALUE = "";
	private static final String DEFAULT_BILLING_URL_APPOINTMENT_VALUE = "0";

	private static final String CPP_TYPE_SOCIAL_HISTORY = "SocHistory";
	private static final String CPP_TYPE_MEDICAL_HISTORY = "MedHistory";
	private static final String CPP_TYPE_ONGOING_CONCERNS = "Concerns";
	private static final String CPP_TYPE_REMINDERS = "Reminders";

	/*
	socHistoryLabel = "oscarEncounter.socHistory.title";
	medHistoryLabel = "oscarEncounter.medHistory.title";
	onGoingLabel = "oscarEncounter.onGoing.title";;
	remindersLabel = "oscarEncounter.reminders.title";
	 */

	Logger logger = MiscUtils.getLogger();

	@Autowired
	private EncounterService encounterService;

/*
	@Autowired
	private EncounterCPPSectionService encounterCPPSectionService;
*/

	@Autowired
	private EncounterPreventionNoteService encounterPreventionNoteService;

	@Autowired
	private EncounterTicklerService encounterTicklerService;

	@Autowired
	private EncounterDiseaseRegistryService encounterDiseaseRegistryService;

	@Autowired
	private EncounterFormService encounterFormService;

	@Autowired
	private EncounterEFormService encounterEFormService;

	@Autowired
	private EncounterDocumentService encounterDocumentService;

	@Autowired
	private EncounterLabResultService encounterLabResultService;

	@Autowired
	private EncounterMessengerService encounterMessengerService;

	@Autowired
	private EncounterMeasurementsService encounterMeasurementsService;

	@Autowired
	private EncounterConsultationService encounterConsultationService;

	@Autowired
	private EncounterAllergyService encounterAllergyService;

	@Autowired
	private EncounterMedicationService encounterMedicationService;

	@Autowired
	private EncounterUnresolvedIssueService encounterUnresolvedIssueService;

	@Autowired
	private EncounterResolvedIssueService encounterResolvedIssueService;

	@Autowired
	private EncounterEpisodeService encounterEpisodeService;

	@Autowired
	private EncounterTeamService encounterTeamService;

	@Autowired
	private EncounterSocialHistoryService encounterSocialHistoryService;

	@Autowired
	private EncounterMedicalHistoryService encounterMedicalHistoryService;

	@Autowired
	private EncounterOngoingConcernsService encounterOngoingConcernsService;

	@Autowired
	private EncounterRemindersService encounterRemindersService;

	@Autowired
	private EncounterOtherMedsService encounterOtherMedsService;

	@Autowired
	private EncounterRiskFactorsService encounterRiskFactorsService;

	@Autowired
	private EncounterFamilyHistoryService encounterFamilyHistoryService;

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private BillingUrlService billingUrlService;

	@Autowired
	private CaseManagementManager caseManagementManager;

	@Autowired
	private ClientImageManager clientImageManager;

	@Autowired
	private EncounterTemplateDao encounterTemplateDao;

	@Autowired
	private CaseManagementIssueService caseManagementIssueService;

	@Autowired
	private RolesManager rolesManager;

	@Autowired
	private UserPropertyDAO userPropertyDAO;

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException, ServletException, EncounterSectionException
	{
		// Get the form bean.  I am using this to pass information from this controller to the jsp
		// view.
		JunoEncounterFormBean junoEncounterForm = (JunoEncounterFormBean) form;

		HttpSession session = request.getSession();
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String roleName = request.getSession().getAttribute("userrole") + "," + request.getSession().getAttribute("user");
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		EctSessionBean encounterSessionBean =
				(EctSessionBean) session.getAttribute("EctSessionBean");

		if (encounterSessionBean == null)
		{
			logger.info("encounterSessionBean does not exist");
			return (mapping.findForward(ACTION_FORWARD_ERROR));
		}

		String user = (String) session.getAttribute("user");

		String appointmentNo;

		try
		{
			int appointmentNoInt = Integer.parseInt(request.getParameter("appointmentNo"));
			appointmentNo = Integer.toString(appointmentNoInt);
		}
		catch(NumberFormatException e)
		{
		    appointmentNo = DEFAULT_APPOINTMENT_VALUE;
		}

		String contextPath = request.getContextPath();

		String identUrl = request.getQueryString();

		// XXX: get the program id.  I don't like having to get this, but some legacy code currently
		//      uses it.
		EctProgram prgrmMgr = new EctProgram(session);
		String programId = prgrmMgr.getProgram(user);

		// XXX: setting this because I'm using ChartNotes.jsp to get the chart notes.  I don't want
		//      this to be here.
		session.setAttribute("case_program_id", programId);

		//oscar.oscarEncounter.pageUtil.EctSessionBean bean = null;
		String beanName = "casemgmt_oscar_bean" + encounterSessionBean.demographicNo;
		session.setAttribute(beanName, encounterSessionBean);

		Locale locale = request.getLocale();

		Date hideBeforeDate = getEncounterNoteHideBeforeDate(providerNo);

		// XXX: this should be generated better if this gets refactored to not use the
		//      encounterSessionBean
		String patientAgeInYears = getPatientAgeInYears(encounterSessionBean.patientBirthdate);

		String echartUuid = UUID.randomUUID().toString();


		String date = request.getParameter("appointmentDate");
		String startTime = request.getParameter("startTime");
		String appointmentProvider = request.getParameter("apptProvider_no");

		String region = OscarProperties.getInstance().getBillingTypeUpperCase();
		//CaseManagementNote caseNote = cform.getCaseNote();

		String billingUrl = billingUrlService.buildUrl(
				providerNo,
				encounterSessionBean.getDemographicNo(),
				region, // ??
				(DEFAULT_APPOINTMENT_VALUE.equals(appointmentNo) ? DEFAULT_BILLING_URL_APPOINTMENT_VALUE : appointmentNo),
				caseManagementManager.getDemoDisplayName(encounterSessionBean.getDemographicNo()),
				date, // ??
				startTime, // ?? I think this is from the appointment, passed through the url
				appointmentProvider, // ??
				null, // We don't support reviewer in the juno encounter
				null
		);


		String cmeJs = OscarProperties.getInstance().getCmeJs();

		List<EncounterTemplate> encounterTemplates = encounterTemplateDao.findAll();

		List<CaseManagementIssue> issues = caseManagementIssueService.getIssues(
				loggedInInfo,
				encounterSessionBean.demographicNo,
				providerNo,
				programId,
				org.oscarehr.encounterNote.model.CaseManagementIssue.ISSUE_FILTER_ALL
		);

		CaseManagementIssueConverter converter = new CaseManagementIssueConverter();

		List<CaseManagementIssueTo1> issueTo1s = new ArrayList<>();
		for(CaseManagementIssue issue: issues)
		{
			issueTo1s.add(converter.getAsTransferObject(loggedInInfo, issue));
		}

		Collections.sort(issueTo1s, CaseManagementIssueTo1.COMPARATOR_ALPHABETICAL);

		List<Provider> providers = caseManagementManager.getAllEditors(encounterSessionBean.getDemographicNo());
		Collections.sort(providers,(new Provider()).ComparatorName());

		List<Secrole> roles = rolesManager.getRoles();

		UserProperty userPropertyHeight = userPropertyDAO.getProp(providerNo, "encounterWindowHeight");
		UserProperty userPropertyWidth = userPropertyDAO.getProp(providerNo, "encounterWindowWidth");
		UserProperty userPropertyMaximize = userPropertyDAO.getProp(providerNo, "encounterWindowMaximize");

		boolean encounterWindowCustomSize = false;
		String encounterWindowHeight = "";
		String encounterWindowWidth = "";

		if(userPropertyHeight != null && userPropertyWidth != null)
		{
			try
			{
				encounterWindowHeight = String.valueOf(Integer.parseInt(userPropertyHeight.getValue()));
				encounterWindowWidth = String.valueOf(Integer.parseInt(userPropertyWidth.getValue()));

				if (encounterWindowHeight != null && encounterWindowWidth != null)
				{
					encounterWindowCustomSize = true;
				}
			} catch (NumberFormatException e)
			{
				// Do nothing, don't set the values
			}
		}

		boolean encounterWindowMaximize = false;
		if(userPropertyMaximize != null && UserProperty.BOOLEAN_TRUE.equals(userPropertyMaximize.getValue()))
		{
			encounterWindowMaximize = true;
		}

		//get client image
		ClientImage img = clientImageManager.getClientImage(Integer.parseInt(encounterSessionBean.demographicNo));
		boolean clientImagePresent = false;
		if (img != null) {
			clientImagePresent = true;
		}


		// Get data for the header
		junoEncounterForm.setPageData(
			encounterService.getEncounterHeader(
				user,
				providerNo,
				roleName,
				encounterSessionBean.demographicNo,
				encounterSessionBean.familyDoctorNo,
				encounterSessionBean.patientFirstName,
				encounterSessionBean.patientLastName,
				encounterSessionBean.patientSex,
				encounterSessionBean.patientAge,
				patientAgeInYears,
				encounterSessionBean.patientBirthdate,
				encounterSessionBean.phone,
				encounterSessionBean.referringDoctorName,
				encounterSessionBean.referringDoctorNumber,
				echartUuid,
				encounterSessionBean.hasRosterDate(),
				encounterSessionBean.rosterDate,
				appointmentNo,
				contextPath,
				encounterSessionBean.source,
				hideBeforeDate,
				billingUrl,
				cmeJs,
				encounterTemplates,
				issueTo1s,
				providers,
				roles,
				encounterWindowCustomSize,
				encounterWindowHeight,
				encounterWindowWidth,
				encounterWindowMaximize,
				clientImagePresent
			)
		);

		// Get data for each of the sections
		Map<String, EncounterSection> sections = new HashMap<>();

		List<String> sectionList = new ArrayList<>();

		sectionList.add(EncounterSection.TYPE_PREVENTIONS);
		sectionList.add(EncounterSection.TYPE_TICKLER);
		sectionList.add(EncounterSection.TYPE_DISEASE_REGISTRY);
		sectionList.add(EncounterSection.TYPE_FORMS);
		sectionList.add(EncounterSection.TYPE_EFORMS);
		sectionList.add(EncounterSection.TYPE_DOCUMENTS);
		sectionList.add(EncounterSection.TYPE_LAB_RESULTS);
		sectionList.add(EncounterSection.TYPE_MESSENGER);
		sectionList.add(EncounterSection.TYPE_MEASUREMENTS);
		sectionList.add(EncounterSection.TYPE_CONSULTATIONS);
		sectionList.add(EncounterSection.TYPE_HRM);
		sectionList.add(EncounterSection.TYPE_ALLERGIES);
		sectionList.add(EncounterSection.TYPE_MEDICATIONS);
		sectionList.add(EncounterSection.TYPE_UNRESOLVED_ISSUES);
		sectionList.add(EncounterSection.TYPE_RESOLVED_ISSUES);
		sectionList.add(EncounterSection.TYPE_EPISODES);
		sectionList.add(EncounterSection.TYPE_PREGNANCIES);
		sectionList.add(EncounterSection.TYPE_HEALTH_CARE_TEAM);
		sectionList.add(encounterSocialHistoryService.getSectionId());
		sectionList.add(encounterMedicalHistoryService.getSectionId());
		sectionList.add(encounterOngoingConcernsService.getSectionId());
		sectionList.add(encounterRemindersService.getSectionId());
		sectionList.add(encounterOtherMedsService.getSectionId());
		sectionList.add(encounterRiskFactorsService.getSectionId());
		sectionList.add(encounterFamilyHistoryService.getSectionId());

		EncounterSectionService.SectionParameters sectionParams =
				new EncounterSectionService.SectionParameters();

		sectionParams.setLoggedInInfo(loggedInInfo);
		sectionParams.setLocale(locale);
		sectionParams.setContextPath(contextPath);
		sectionParams.setRoleName(roleName);
		sectionParams.setProviderNo(user);
		sectionParams.setDemographicNo(encounterSessionBean.demographicNo);
		sectionParams.setPatientFirstName(encounterSessionBean.patientFirstName);
		sectionParams.setPatientLastName(encounterSessionBean.patientLastName);
		sectionParams.setFamilyDoctorNo(encounterSessionBean.familyDoctorNo);
		sectionParams.setAppointmentNo(appointmentNo);
		sectionParams.setChartNo(encounterSessionBean.chartNo);
		sectionParams.setProgramId(programId);
		sectionParams.setUserName(encounterSessionBean.userName);
		sectionParams.seteChartUUID(echartUuid);

		for(String sectionName: sectionList)
		{
			EncounterSectionService sectionService =
					encounterService.getEncounterSectionServiceByName(sectionName);

			sections.put(sectionName, sectionService.getDefaultSection(sectionParams));
		}


		junoEncounterForm.setSections(sections);


		List<String> cppSections = new ArrayList<>();

		cppSections.add(encounterSocialHistoryService.getSectionId());
		cppSections.add(encounterMedicalHistoryService.getSectionId());
		cppSections.add(encounterOngoingConcernsService.getSectionId());
		cppSections.add(encounterRemindersService.getSectionId());

		junoEncounterForm.setCppNoteSections(cppSections);


		List<String> leftSections = new ArrayList<>();

		leftSections.add(EncounterSection.TYPE_PREVENTIONS);
		leftSections.add(EncounterSection.TYPE_TICKLER);
		leftSections.add(EncounterSection.TYPE_DISEASE_REGISTRY);
		leftSections.add(EncounterSection.TYPE_FORMS);
		leftSections.add(EncounterSection.TYPE_EFORMS);
		leftSections.add(EncounterSection.TYPE_DOCUMENTS);
		leftSections.add(EncounterSection.TYPE_LAB_RESULTS);
		leftSections.add(EncounterSection.TYPE_MESSENGER);
		leftSections.add(EncounterSection.TYPE_MEASUREMENTS);
		leftSections.add(EncounterSection.TYPE_CONSULTATIONS);

		if(OscarProperties.getInstance().isModuleEnabled(OscarProperties.Module.MODULE_HRM))
		{
			leftSections.add(EncounterSection.TYPE_HRM);
		}

		junoEncounterForm.setLeftNoteSections(leftSections);


		List<String> rightSections = new ArrayList<>();

		rightSections.add(EncounterSection.TYPE_ALLERGIES);
		rightSections.add(EncounterSection.TYPE_MEDICATIONS);

		rightSections.add(encounterOtherMedsService.getSectionId());
		rightSections.add(encounterRiskFactorsService.getSectionId());
		rightSections.add(encounterFamilyHistoryService.getSectionId());

		rightSections.add(EncounterSection.TYPE_UNRESOLVED_ISSUES);
		rightSections.add(EncounterSection.TYPE_RESOLVED_ISSUES);
		// XXX: Leaving this out for now.  Not used by our clients AFAIK.
		//rightSections.add(EncounterSection.TYPE_DECISION_SUPPORT_ALERTS);

		rightSections.add(EncounterSection.TYPE_EPISODES);
		rightSections.add(EncounterSection.TYPE_PREGNANCIES);
		rightSections.add(EncounterSection.TYPE_HEALTH_CARE_TEAM);

		junoEncounterForm.setRightNoteSections(rightSections);

		return (mapping.findForward(ACTION_FORWARD_VIEW));
	}

	// Calculate the date before which all encounter notes are displayed collapsed
	private Date getEncounterNoteHideBeforeDate(String providerNo)
	{
		UserProperty uProp = caseManagementManager.getUserProperty(providerNo, UserProperty.STALE_NOTEDATE);

		Calendar cal = Calendar.getInstance();
		if (uProp != null)
		{
			String strStaleDate = uProp.getValue();
			if (strStaleDate.equalsIgnoreCase("A"))
			{
				cal.set(1970, 1, 1);
			} else if (strStaleDate.equalsIgnoreCase("0"))
			{
				cal.add(Calendar.MONTH, 1);
			} else
			{
				int pastMths = Integer.parseInt(strStaleDate);
				cal.add(Calendar.MONTH, pastMths);
			}

		}
		else
		{
			cal.add(Calendar.YEAR, -1);
		}

		return cal.getTime();
	}

	private String getPatientAgeInYears(String birthdate)
	{
		String[] dateParts = birthdate.split("-", 3);

		String year = dateParts[0];
		String month = dateParts[1];
		String day = dateParts[2];

		return Integer.toString(UtilDateUtilities.calcAge(year, month, day));
	}
}
