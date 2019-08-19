/*
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

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.drools.FactException;
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.service.EncounterAllergyService;
import org.oscarehr.casemgmt.service.EncounterCPPNoteService;
import org.oscarehr.casemgmt.service.EncounterConsultationService;
import org.oscarehr.casemgmt.service.EncounterDiseaseRegistryService;
import org.oscarehr.casemgmt.service.EncounterDocumentService;
import org.oscarehr.casemgmt.service.EncounterEFormService;
import org.oscarehr.casemgmt.service.EncounterEpisodeService;
import org.oscarehr.casemgmt.service.EncounterFormService;
import org.oscarehr.casemgmt.service.EncounterMeasurementsService;
import org.oscarehr.casemgmt.service.EncounterMedicationService;
import org.oscarehr.casemgmt.service.EncounterMessengerService;
import org.oscarehr.casemgmt.service.EncounterResolvedIssueService;
import org.oscarehr.casemgmt.service.EncounterSectionService;
import org.oscarehr.casemgmt.service.EncounterService;
import org.oscarehr.casemgmt.service.EncounterPreventionNoteService;
import org.oscarehr.casemgmt.service.EncounterTeamService;
import org.oscarehr.casemgmt.service.EncounterTicklerService;
import org.oscarehr.casemgmt.service.EncounterUnresolvedIssueService;
import org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.struts.DispatchActionSupport;
import oscar.oscarEncounter.data.EctProgram;
import oscar.oscarEncounter.pageUtil.EctSessionBean;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JunoEncounterAction extends DispatchActionSupport
{
	private static final String ACTION_FORWARD_VIEW = "view";
	private static final String ACTION_FORWARD_ERROR = "error";

	private static final String CPP_TYPE_SOCIAL_HISTORY = "SocHistory";
	private static final String CPP_TYPE_MEDICAL_HISTORY = "MedHistory";
	private static final String CPP_TYPE_ONGOING_CONCERNS = "Concerns";
	private static final String CPP_TYPE_REMINDERS = "Reminders";

	private static final String LEFT_TYPE_PREVENTIONS = "Preventions";
	private static final String LEFT_TYPE_TICKLER = "Tickler";
	private static final String LEFT_TYPE_DISEASE_REGISTRY = "DiseaseRegistry";
	private static final String LEFT_TYPE_FORMS = "Forms";
	private static final String LEFT_TYPE_EFORMS = "eForms";
	private static final String LEFT_TYPE_DOCUMENTS = "Documents";
	private static final String LEFT_TYPE_LAB_RESULTS = "LabResults";
	private static final String LEFT_TYPE_MESSENGER = "Messenger";
	private static final String LEFT_TYPE_MEASUREMENTS = "Measurments";
	private static final String LEFT_TYPE_CONSULTATIONS = "Consultations";

	private static final String RIGHT_TYPE_ALLERGIES = "Allergies";
	private static final String RIGHT_TYPE_MEDICATIONS = "Medications";
	private static final String RIGHT_TYPE_OTHER_MEDS = "OMeds";
	private static final String RIGHT_TYPE_RISK_FACTORS = "RiskFactors";
	private static final String RIGHT_TYPE_FAMILY_HISTORY = "FamHistory";
	private static final String RIGHT_TYPE_UNRESOLVED_ISSUES = "UnresolvedIssues";
	private static final String RIGHT_TYPE_RESOLVED_ISSUES = "ResolvedIssues";
	private static final String RIGHT_TYPE_DECISION_SUPPORT_ALERTS = "DecisionSupportAlerts";
	private static final String RIGHT_TYPE_EPISODES = "Episodes";
	private static final String RIGHT_TYPE_HEALTH_CARE_TEAM = "HealthCareTeam";

	/*
	socHistoryLabel = "oscarEncounter.socHistory.title";
	medHistoryLabel = "oscarEncounter.medHistory.title";
	onGoingLabel = "oscarEncounter.onGoing.title";;
	remindersLabel = "oscarEncounter.reminders.title";
	 */

	Logger logger = MiscUtils.getLogger();

	@Autowired
	private EncounterService encounterService;

	@Autowired
	private EncounterCPPNoteService encounterCPPNoteService;

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
	private IssueDao issueDao;

	//@Autowired
	//private PreventionsSummary preventionsSummary;


	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException, ServletException, FactException
	{
		// Get the form bean.  I am using this to pass information from this controller to the jsp
		// view.
		JunoEncounterFormBean junoEncounterForm = (JunoEncounterFormBean) form;

		HttpSession session = request.getSession();
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String roleName = request.getSession().getAttribute("userrole") + "," + request.getSession().getAttribute("user");

		EctSessionBean encounterSessionBean =
				(EctSessionBean) request.getSession().getAttribute("EctSessionBean");

		if (encounterSessionBean == null)
		{
			logger.info("encounterSessionBean does not exist");
			return (mapping.findForward(ACTION_FORWARD_ERROR));
		}


		String user = (String) session.getAttribute("user");

		String appointmentNo = request.getParameter("appointmentNo");

		String contextPath = request.getContextPath();

		String identUrl = request.getQueryString();

		// XXX: get the program id.  I don't like having to get this, but some legacy code currently
		//      uses it.
		EctProgram prgrmMgr = new EctProgram(session);
		String programId = prgrmMgr.getProgram(user);


		// Get data for the header
		junoEncounterForm.setHeader(
				encounterService.getEncounterHeader(
						user,
						roleName,
						encounterSessionBean.demographicNo,
						encounterSessionBean.familyDoctorNo,
						encounterSessionBean.patientFirstName,
						encounterSessionBean.patientLastName,
						encounterSessionBean.patientSex,
						encounterSessionBean.patientAge,
						encounterSessionBean.patientBirthdate,
						encounterSessionBean.phone,
						encounterSessionBean.referringDoctorName,
						encounterSessionBean.referringDoctorNumber,
						encounterSessionBean.hasRosterDate(),
						encounterSessionBean.rosterDate,
						appointmentNo,
						contextPath
				)
		);

		// Get data for each of the sections
		Map<String, EncounterSection> sections = new HashMap<>();

		sections.put(CPP_TYPE_SOCIAL_HISTORY, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Social History", "#996633", CPP_TYPE_SOCIAL_HISTORY));

		sections.put(CPP_TYPE_MEDICAL_HISTORY, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Medical History", "#996633", CPP_TYPE_MEDICAL_HISTORY));

		sections.put(CPP_TYPE_ONGOING_CONCERNS, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Ongoing Concerns", "#996633", CPP_TYPE_ONGOING_CONCERNS));

		sections.put(CPP_TYPE_REMINDERS, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Reminders", "#996633", CPP_TYPE_REMINDERS));

		sections.put(LEFT_TYPE_PREVENTIONS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Preventions", "#009999", encounterPreventionNoteService));
		sections.put(LEFT_TYPE_TICKLER, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Tickler", "#FF6600", encounterTicklerService));
		sections.put(LEFT_TYPE_DISEASE_REGISTRY, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Disease Registry", "#5A5A5A", encounterDiseaseRegistryService));
		sections.put(LEFT_TYPE_FORMS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Forms", "#917611", encounterFormService));
		sections.put(LEFT_TYPE_EFORMS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "eForms", "#008000", encounterEFormService));
		sections.put(LEFT_TYPE_DOCUMENTS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Documents", "#476BB3", encounterDocumentService));
		sections.put(LEFT_TYPE_MESSENGER, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Messenger", "#7F462C", encounterMessengerService));
		sections.put(LEFT_TYPE_MEASUREMENTS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Measurements", "#344887", encounterMeasurementsService));
		sections.put(LEFT_TYPE_CONSULTATIONS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Consultations", "#6C2DC7", encounterConsultationService));

		sections.put(RIGHT_TYPE_ALLERGIES, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Allergies", "#C85A17", encounterAllergyService));
		sections.put(RIGHT_TYPE_MEDICATIONS, getSection(loggedInInfo, roleName, user, encounterSessionBean.demographicNo, appointmentNo, programId, "Medications", "#7D2252", encounterMedicationService));

		sections.put(RIGHT_TYPE_OTHER_MEDS, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Other Meds", "#306754", RIGHT_TYPE_OTHER_MEDS));

		sections.put(RIGHT_TYPE_RISK_FACTORS, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Risk Factors", "#993333", RIGHT_TYPE_RISK_FACTORS));

		sections.put(RIGHT_TYPE_FAMILY_HISTORY, getCppSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Family History", "#006600", RIGHT_TYPE_FAMILY_HISTORY));

		sections.put(RIGHT_TYPE_UNRESOLVED_ISSUES, getSection(loggedInInfo, roleName, user,
				encounterSessionBean.demographicNo, appointmentNo, programId,
				"Unresolved Issues", "#CC9900", encounterUnresolvedIssueService));
		sections.put(RIGHT_TYPE_RESOLVED_ISSUES, getSection(loggedInInfo, roleName, user,
				encounterSessionBean.demographicNo, appointmentNo, programId,
				"Resolved Issues", "#151B8D", encounterResolvedIssueService));
		sections.put(RIGHT_TYPE_EPISODES, getSection(loggedInInfo, roleName, user,
				encounterSessionBean.demographicNo, appointmentNo, programId,
				"Episodes", "#045228", encounterEpisodeService));
		sections.put(RIGHT_TYPE_HEALTH_CARE_TEAM, getSection(loggedInInfo, roleName, user,
				encounterSessionBean.demographicNo, appointmentNo, programId,
				"Health Care Team", "#6699CC", encounterTeamService));

		junoEncounterForm.setSections(sections);


		List<String> cppSections = new ArrayList<>();

		cppSections.add(CPP_TYPE_SOCIAL_HISTORY);
		cppSections.add(CPP_TYPE_MEDICAL_HISTORY);
		cppSections.add(CPP_TYPE_ONGOING_CONCERNS);
		cppSections.add(CPP_TYPE_REMINDERS);

		junoEncounterForm.setCppNoteSections(cppSections);


		List<String> leftSections = new ArrayList<>();

		leftSections.add(LEFT_TYPE_PREVENTIONS);
		leftSections.add(LEFT_TYPE_TICKLER);
		leftSections.add(LEFT_TYPE_DISEASE_REGISTRY);
		leftSections.add(LEFT_TYPE_FORMS);
		leftSections.add(LEFT_TYPE_EFORMS);
		leftSections.add(LEFT_TYPE_DOCUMENTS);
		//leftSections.add(LEFT_TYPE_LAB_RESULTS);
		leftSections.add(LEFT_TYPE_MESSENGER);
		leftSections.add(LEFT_TYPE_MEASUREMENTS);
		leftSections.add(LEFT_TYPE_CONSULTATIONS);

		junoEncounterForm.setLeftNoteSections(leftSections);


		List<String> rightSections = new ArrayList<>();

		rightSections.add(RIGHT_TYPE_ALLERGIES);
		rightSections.add(RIGHT_TYPE_MEDICATIONS);

		rightSections.add(RIGHT_TYPE_OTHER_MEDS);
		rightSections.add(RIGHT_TYPE_RISK_FACTORS);
		rightSections.add(RIGHT_TYPE_FAMILY_HISTORY);

		rightSections.add(RIGHT_TYPE_UNRESOLVED_ISSUES);
		rightSections.add(RIGHT_TYPE_RESOLVED_ISSUES);
		// XXX: Leaving this out for now.  Not used by our clients AFAIK.
		//rightSections.add(RIGHT_TYPE_DECISION_SUPPORT_ALERTS);

		rightSections.add(RIGHT_TYPE_EPISODES);
		rightSections.add(RIGHT_TYPE_HEALTH_CARE_TEAM);

		junoEncounterForm.setRightNoteSections(rightSections);



		// =========================================================================================
		// Summaries
		// =========================================================================================

		/*
		result.put("preventions","preventionsSummary");
		result.put("meds","rxSummary");
		result.put("othermeds","issueNoteSummary");
		result.put("ongoingconcerns","ongoingConcernDxRegSummary");
		result.put("medhx","issueNoteSummary");
		result.put("socfamhx","issueNoteSummary");
		result.put("reminders","issueNoteSummary");
		result.put("assessments","formsSummary");
		result.put("outgoing","formsSummary");
		result.put("sochx","issueNoteSummary");
		result.put("famhx","issueNoteSummary");
		result.put("incoming","labsDocsSummary");
		result.put("dssupport","decisionSupportSummary");
		result.put("allergies","allergiesSummary");
		result.put("riskfactors","issueNoteSummary");
		 */

		/*
		Integer demographicNo = Integer.parseInt(encounterSessionBean.demographicNo);

		List<SummaryTo1> summaries = new ArrayList<>();

		//Summary summaryInterface = (Summary) SpringUtils.getBean(MY_MAP.get(summaryCode));

		summaries.add(preventionsSummary.getSummary(loggedInInfo, demographicNo, LEFT_TYPE_PREVENTIONS));

		junoEncounterForm.setLeftSummaries(summaries);
		 */

		return (mapping.findForward(ACTION_FORWARD_VIEW));
	}

/*	private EncounterSection getPreventionSection(LoggedInInfo loggedInInfo, String demographicNo, String title) throws FactException
	{

		EncounterSection section = new EncounterSection();

		section.setTitle(title);
		section.setCppIssues("");
		section.setAddUrl("");
		section.setIdentUrl("");

		section.setNotes(encounterPreventionNoteService.getPreventionNotes(loggedInInfo, demographicNo));

		return section;
	}*/

	//private EncounterSection getTicklerSection(LoggedInInfo loggedInInfo, String demographicNo, String title) throws FactException
	//{

	//	EncounterSection section = new EncounterSection();

	//	section.setTitle(title);
	//	section.setCppIssues("");
	//	section.setAddUrl("");
	//	section.setIdentUrl("");

	//	section.setNotes(encounterTicklerService.getTicklers(loggedInInfo, demographicNo));

	//	return section;
	//}

	private EncounterSection getSection(
			LoggedInInfo loggedInInfo,
			String roleName,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String programId,
			String title,
			String colour,
			EncounterSectionService encounterSectionService
	) throws FactException
	{
		EncounterSection section = new EncounterSection();

		section.setTitle(title);
		section.setColour(colour);
		section.setCppIssues("");
		section.setAddUrl("");
		section.setIdentUrl("");

		section.setNotes(encounterSectionService.getNotes(loggedInInfo, roleName, providerNo,
				demographicNo, appointmentNo, programId));

		return section;
	}

	private EncounterSection getCppSection(
			String contextPath,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String identUrl,
			String title,
			String colour,
			String sectionName
	)
	{

		String addUrl = contextPath + "/CaseManagementEntry.do?method=issueNoteSave" +
				"&providerNo=" + providerNo + "" +
				"&demographicNo=" + demographicNo + "" +
				"&appointmentNo=" + appointmentNo + "" +
				"&noteId=";

		// Get issue id from type
		Issue issue = issueDao.findByCode(sectionName);

		String cppIssues = issue.getId() + ";" + issue.getCode() + ";" + issue.getDescription();

		EncounterSection section = new EncounterSection();

		section.setTitle(title);
		section.setColour(colour);
		section.setCppIssues(cppIssues);
		section.setAddUrl(addUrl);
		section.setIdentUrl(identUrl);
		section.setNotes(encounterCPPNoteService.getCPPNotes(demographicNo, issue.getIssueId()));

		return section;
	}
}
