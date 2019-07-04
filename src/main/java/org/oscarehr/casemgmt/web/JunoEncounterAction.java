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
import org.oscarehr.casemgmt.dto.EncounterSection;
import org.oscarehr.casemgmt.service.EncounterCPPNoteService;
import org.oscarehr.casemgmt.service.EncounterService;
import org.oscarehr.casemgmt.web.formbeans.JunoEncounterFormBean;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.struts.DispatchActionSupport;
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
	private IssueDao issueDao;

	public ActionForward execute(
		ActionMapping mapping,
		ActionForm form,
		HttpServletRequest request,
		HttpServletResponse response
	) throws IOException, ServletException
	{
		// Get the form bean.  I am using this to pass information from this controller to the jsp
		// view.
		JunoEncounterFormBean junoEncounterForm = (JunoEncounterFormBean) form;

		HttpSession session = request.getSession();
		//LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		EctSessionBean encounterSessionBean =
				(EctSessionBean) request.getSession().getAttribute("EctSessionBean");

		if (encounterSessionBean == null)
		{
			logger.info("encounterSessionBean does not exist");
			return (mapping.findForward(ACTION_FORWARD_ERROR));
		}


		String user = (String) session.getAttribute("user");
		String roleName = session.getAttribute("userrole") + "," + session.getAttribute("user");

		String appointmentNo = request.getParameter("appointmentNo");

		String contextPath = request.getContextPath();

		String identUrl = request.getQueryString();

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

		sections.put(CPP_TYPE_SOCIAL_HISTORY, getSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Social History", CPP_TYPE_SOCIAL_HISTORY));

		sections.put(CPP_TYPE_MEDICAL_HISTORY, getSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Medical History", CPP_TYPE_MEDICAL_HISTORY));

		sections.put(CPP_TYPE_ONGOING_CONCERNS, getSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Ongoing Concerns", CPP_TYPE_ONGOING_CONCERNS));

		sections.put(CPP_TYPE_REMINDERS, getSection(contextPath, user, encounterSessionBean.demographicNo, appointmentNo,
				identUrl, "Reminders", CPP_TYPE_REMINDERS));

		junoEncounterForm.setSections(sections);

		List<String> cppSections = new ArrayList<>();

		cppSections.add(CPP_TYPE_SOCIAL_HISTORY);
		cppSections.add(CPP_TYPE_MEDICAL_HISTORY);
		cppSections.add(CPP_TYPE_ONGOING_CONCERNS);
		cppSections.add(CPP_TYPE_REMINDERS);

		junoEncounterForm.setCppNoteSections(cppSections);

		return (mapping.findForward(ACTION_FORWARD_VIEW));
	}

	private EncounterSection getSection(
			String contextPath,
			String providerNo,
			String demographicNo,
			String appointmentNo,
			String identUrl,
			String title,
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
		section.setCppIssues(cppIssues);
		section.setAddUrl(addUrl);
		section.setIdentUrl(identUrl);
		section.setNotes(encounterCPPNoteService.getCPPNotes(demographicNo, issue.getIssueId()));

		return section;
	}
}
