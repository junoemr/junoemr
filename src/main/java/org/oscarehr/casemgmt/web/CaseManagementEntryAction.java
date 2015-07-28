/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.casemgmt.web;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.caisi.dao.TicklerDAO;
import org.caisi.model.Tickler;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.model.Admission;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.AdmissionManager;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.billing.CA.dao.GstControlDao;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.casemgmt.common.EChartNoteEntry;
import org.oscarehr.casemgmt.dao.CaseManagementIssueDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteExtDAO;
import org.oscarehr.casemgmt.dao.IssueDAO;
import org.oscarehr.casemgmt.model.CaseManagementCPP;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.CaseManagementTmpSave;
import org.oscarehr.casemgmt.model.ClientImage;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.service.CaseManagementPrintPdf;
import org.oscarehr.casemgmt.util.ExtPrint;
import org.oscarehr.casemgmt.web.CaseManagementViewAction.IssueDisplay;
import org.oscarehr.casemgmt.web.formbeans.CaseManagementEntryFormBean;
import org.oscarehr.common.dao.AppointmentArchiveDao;
import org.oscarehr.common.dao.BillingServiceDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ProviderDefaultProgramDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.DxAssociation;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.ProviderDefaultProgram;
import org.oscarehr.common.model.ProviderPreference;
import org.oscarehr.eyeform.dao.EyeFormDao;
import org.oscarehr.eyeform.dao.FollowUpDao;
import org.oscarehr.eyeform.dao.MacroDao;
import org.oscarehr.eyeform.dao.TestBookRecordDao;
import org.oscarehr.eyeform.model.EyeForm;
import org.oscarehr.eyeform.model.EyeformFollowUp;
import org.oscarehr.eyeform.model.EyeformTestBook;
import org.oscarehr.eyeform.model.Macro;
import org.oscarehr.eyeform.web.FollowUpAction;
import org.oscarehr.eyeform.web.ProcedureBookAction;
import org.oscarehr.eyeform.web.TestBookAction;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WebUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.WebApplicationContext;

import oscar.OscarProperties;
import oscar.appt.ApptStatusData;
import oscar.dms.EDocUtil;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarBilling.ca.on.pageUtil.BillingSavePrep;
import oscar.oscarEncounter.pageUtil.EctSessionBean;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.oscarSurveillance.SurveillanceMaster;
import oscar.util.ConcatPDF;
import oscar.util.UtilDateUtilities;

import com.lowagie.text.DocumentException;
import com.quatro.model.security.Secrole;

/*
 * Updated by Eugene Petruhin on 12 and 13 jan 2009 while fixing #2482832 & #2494061
 * Updated by Eugene Petruhin on 21 jan 2009 while fixing missing "New Note" link
 */
public class CaseManagementEntryAction extends BaseCaseManagementEntryAction {

	private static Logger logger = MiscUtils.getLogger();

	private CaseManagementNoteDAO caseManagementNoteDao = (CaseManagementNoteDAO) SpringUtils.getBean("caseManagementNoteDAO");
	private CaseManagementIssueDAO caseManagementIssueDao = (CaseManagementIssueDAO) SpringUtils.getBean("caseManagementIssueDAO");
	private CaseManagementNoteExtDAO caseManagementNoteExtDao = (CaseManagementNoteExtDAO) SpringUtils.getBean("CaseManagementNoteExtDAO");
	private IssueDAO issueDao = (IssueDAO) SpringUtils.getBean("IssueDAO");
	private AppointmentArchiveDao appointmentArchiveDao = (AppointmentArchiveDao)SpringUtils.getBean("appointmentArchiveDao");

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return edit(mapping, form, request, response);
	}

	public ActionForward setUpMainEncounter(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String demono = getDemographicNo(request);
		logger.debug("client Image?");
		
		// get client image
		ClientImage img = clientImageMgr.getClientImage(Integer.parseInt(demono));
					
		if (img != null) {
			request.setAttribute("image_exists", "true");
			request.setAttribute("demographicNo", demono);
		}
		return mapping.findForward("setUpMainEncounterPage");
	}

	public ActionForward edit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("Edit Starts");
		long start = System.currentTimeMillis();
		long beginning = start;
		long current = 0;
		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		cform.setChain("");
		request.setAttribute("change_flag", "false");
		request.setAttribute("from", "casemgmt");

		logger.debug("Get demo and provider no");
		String demono = getDemographicNo(request);
		Integer demographicNo = Integer.parseInt(demono);
		String providerNo = getProviderNo(request);
		current = System.currentTimeMillis();
		logger.debug("Get demo and provider no " + String.valueOf(current - start));
		start = current;

		String programIdString = (String) session.getAttribute("case_program_id");
		Integer programId = null;
		try {
			programId = Integer.parseInt(programIdString);
		} catch (Exception e) {
			logger.warn("Error parsing programId:" + programIdString, e);
		}

		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		/* process the request from other module */
		if (!"casemgmt".equalsIgnoreCase(request.getParameter("from"))) {

			// no demographic number, no page
			if (request.getParameter("demographicNo") == null || "".equals(request.getParameter("demographicNo"))) {
				return mapping.findForward("NoDemoERR");
			}
			request.setAttribute("from", "");
		}

		/* prepare url for billing */
		if (request.getParameter("from") != null) {
			request.setAttribute("from", request.getParameter("from"));
		}

		String url = "";
		if ("casemgmt".equals(request.getAttribute("from"))) {
			String ss = (String) session.getAttribute("casemgmt_VlCountry");

			String province = OscarProperties.getInstance().getProperty("billregion", "").trim().toUpperCase();

			String strBeanName = "casemgmt_oscar_bean" + demono;
			EctSessionBean bean = (EctSessionBean) session.getAttribute(strBeanName);

			if (bean.appointmentNo == null) {
				bean.appointmentNo = "0";
			}
			String bsurl = (String) session.getAttribute("casemgmt_oscar_baseurl");
			Date today = new Date();
			Calendar todayCal = Calendar.getInstance();
			todayCal.setTime(today);

			String Hour = Integer.toString(todayCal.get(Calendar.HOUR));
			String Min = Integer.toString(todayCal.get(Calendar.MINUTE));
			if ("BR".equals(ss)) {
				url = bsurl + "/oscar/billing/procedimentoRealizado/init.do?appId=" + bean.appointmentNo;
			} else {
				// StringEncoderUtils.a();
				String default_view = OscarProperties.getInstance().getProperty("default_view", "");

				url = bsurl + "/billing.do?billRegion=" + java.net.URLEncoder.encode(province, "UTF-8") + "&billForm=" + java.net.URLEncoder.encode(default_view, "UTF-8") + "&hotclick=" + java.net.URLEncoder.encode("", "UTF-8") + "&appointment_no=" + bean.appointmentNo + "&appointment_date=" + bean.appointmentDate + "&start_time=" + Hour + ":" + Min + "&demographic_name=" + java.net.URLEncoder.encode(bean.patientLastName + "," + bean.patientFirstName, "UTF-8") + "&demographic_no=" + bean.demographicNo
				+ "&providerview=" + bean.curProviderNo + "&user_no=" + bean.providerNo + "&apptProvider_no=" + bean.curProviderNo + "&bNewForm=1&status=t";
			}
			session.setAttribute("billing_url", url);
		}

		/* remove the remembered echart string */
		session.removeAttribute("lastSavedNoteString");

		logger.debug("Get Issues and filter them");
		current = System.currentTimeMillis();
		logger.debug("Get Issues and filter them " + String.valueOf(current - start));
		start = current;

		cform.setDemoNo(demono);
		CaseManagementNote note = null;

		String nId = request.getParameter("noteId");
		String forceNote = request.getParameter("forceNote");
		if (forceNote == null) forceNote = "false";

		logger.debug("NoteId " + nId);

		String maxTmpSave = oscar.OscarProperties.getInstance().getProperty("maxTmpSave", "");
		logger.debug("maxTmpSave " + maxTmpSave);
		// set date 2 weeks in past so we retrieve more recent saved notes
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -14);
		Date twoWeeksAgo = cal.getTime();
		logger.debug("Get tmp note");
		CaseManagementTmpSave tmpsavenote;
		if (maxTmpSave.equalsIgnoreCase("off")) {
			tmpsavenote = this.caseManagementMgr.restoreTmpSave(providerNo, demono, programIdString);
		} else {
			tmpsavenote = this.caseManagementMgr.restoreTmpSave(providerNo, demono, programIdString, twoWeeksAgo);
		}
		current = System.currentTimeMillis();
		logger.debug("Get tmp note " + String.valueOf(current - start));
		start = current;

		logger.debug("Get Note for editing");

		// create a new note
		if (request.getParameter("note_edit") != null && request.getParameter("note_edit").equals("new")) {
			logger.debug("NEW NOTE GENERATED");
			session.setAttribute("newNote", "true");
			session.setAttribute("issueStatusChanged", "false");
			request.setAttribute("newNoteIdx", request.getParameter("newNoteIdx"));

			note = new CaseManagementNote();
			note.setProviderNo(providerNo);
			Provider prov = new Provider();
			prov.setProviderNo(providerNo);
			note.setProvider(prov);
			note.setDemographic_no(demono);

			if (!OscarProperties.getInstance().isPropertyActive("encounter.empty_new_note")) {
				this.insertReason(request, note);
			} else {
				note.setNote("");
				note.setEncounter_type("");
			}

			String strBeanName = "casemgmt_oscar_bean" + demono;
			EctSessionBean bean = (EctSessionBean) session.getAttribute(strBeanName);
			String encType = request.getParameter("encType");

			if (encType == null || encType.equals("")) {
				note.setEncounter_type("");
			} else {
				note.setEncounter_type(encType);
			}
			if (bean.encType != null && bean.encType.length() > 0) {
				note.setEncounter_type(bean.encType);
			}

			resetTemp(providerNo, demono, programIdString);

		}
		// get the last temp note?
		else if (tmpsavenote != null && !forceNote.equals("true")) {
			logger.debug("tempsavenote is NOT NULL");
			if (tmpsavenote.getNote_id() > 0) {
				session.setAttribute("newNote", "false");
				request.setAttribute("noteId", String.valueOf(tmpsavenote.getNote_id()));
				note = caseManagementMgr.getNote(String.valueOf(tmpsavenote.getNote_id()));
				logger.debug("Restoring " + String.valueOf(note.getId()));
			} else {
				session.setAttribute("newNote", "true");
				session.setAttribute("issueStatusChanged", "false");
				note = new CaseManagementNote();
				note.setProviderNo(providerNo);
				Provider prov = new Provider();
				prov.setProviderNo(providerNo);
				note.setProvider(prov);
				note.setDemographic_no(demono);
			}

			note.setNote(tmpsavenote.getNote());

		}
		// get an existing non-temp note?
		else if (nId != null && Integer.parseInt(nId) > 0) {
			logger.debug("Using nId " + nId + " to fetch note");
			session.setAttribute("newNote", "false");
			note = caseManagementMgr.getNote(nId);

			if (note.getHistory() == null || note.getHistory().equals("")) {
				// old note - we need to save the original in here
				note.setHistory(note.getNote());

				caseManagementMgr.saveNoteSimple(note);
				addNewNoteLink(Long.parseLong(nId));
			}

		}
		// no note specified, get last unsigned
		else {
			// A hack to load last unsigned note when not specifying a particular note to edit
			// if there is no unsigned note load a new one
			if ((note = getLastSaved(request, demono, providerNo)) == null) {
				session.setAttribute("newNote", "true");
				session.setAttribute("issueStatusChanged", "false");
				note = new CaseManagementNote();
				note.setProviderNo(providerNo);
				Provider prov = new Provider();
				prov.setProviderNo(providerNo);
				note.setProvider(prov);
				note.setDemographic_no(demono);

				if (!OscarProperties.getInstance().isPropertyActive("encounter.empty_new_note")) {
					this.insertReason(request, note);
				} else {
					note.setNote("");
					note.setEncounter_type("");
				}
				String strBeanName = "casemgmt_oscar_bean" + demono;
				EctSessionBean bean = (EctSessionBean) session.getAttribute(strBeanName);
				String encType = request.getParameter("encType");

				if (encType == null || encType.equals("")) {
					note.setEncounter_type("");
				} else {
					note.setEncounter_type(encType);
				}
				if (bean.encType != null && bean.encType.length() > 0) {
					note.setEncounter_type(bean.encType);
				}
			}
		}
		current = System.currentTimeMillis();
		logger.debug("Get note to edit " + String.valueOf(current - start));
		start = current;

		/*
		 * do the restore if(restore != null && restore.booleanValue() == true) { String tmpsavenote = this.caseManagementMgr.restoreTmpSave(providerNo,demono,programId); if(tmpsavenote != null) { note.setNote(tmpsavenote); } }
		 */
		logger.debug("Set Encounter Type: " + note.getEncounter_type());
		logger.debug("Fetched Note " + String.valueOf(note.getId()));

		logger.debug("Populate Note with editors");
		this.caseManagementMgr.getEditors(note);
		current = System.currentTimeMillis();
		logger.debug("Populate Note with editors " + String.valueOf(current - start));
		start = current;

		// put the new/retrieved not in the form object for rendering on page
		cform.setCaseNote(note);
		/* set issue checked list */

		// get issues for current demographic, based on provider rights

		Boolean useNewCaseMgmt = new Boolean((String) session.getAttribute("newCaseManagement"));

		CheckBoxBean[] checkedList = null;
		if (useNewCaseMgmt) {

			CaseManagementViewAction caseManagementViewAction = new CaseManagementViewAction();
			ArrayList<CheckBoxBean> checkBoxBeanList = new ArrayList<CheckBoxBean>();
			caseManagementViewAction.addLocalIssues(checkBoxBeanList, demographicNo, false, programId);
			caseManagementViewAction.addRemoteIssues(checkBoxBeanList, demographicNo, false);

			caseManagementViewAction.sortIssuesByOrderId(checkBoxBeanList);
			
			checkedList = checkBoxBeanList.toArray(new CheckBoxBean[checkBoxBeanList.size()]);
			/*
			 * List<CaseManagementIssue> issues = caseManagementMgr.filterIssues(caseManagementMgr.getIssues(Integer.parseInt(demono)), programIdString); checkedList = new CheckBoxBean[issues.size()]; // set issue checked list
			 * log.debug("Set Checked Issues " + String.valueOf(current-start)); List allNotes = this.caseManagementMgr.getNotes(demono); for (int i = 0; i < issues.size(); i++) { checkedList[i] = new CheckBoxBean(); CaseManagementIssue iss =
			 * issues.get(i); checkedList[i].setIssue(iss); checkedList[i].setUsed(haveIssue(iss.getId(), allNotes)); current = System.currentTimeMillis(); log.debug("Set Checked Issues " + String.valueOf(current-start)); start = current; }
			 */
			Iterator itr = note.getIssues().iterator();
			while (itr.hasNext()) {
				int id = ((CaseManagementIssue) itr.next()).getId().intValue();
				SetChecked(checkedList, id);
			}

		} else // old CME
		{
			CaseManagementViewAction caseManagementViewAction = new CaseManagementViewAction();
			ArrayList<CheckBoxBean> checkBoxBeanList = new ArrayList<CheckBoxBean>();
			caseManagementViewAction.addLocalIssues(checkBoxBeanList, demographicNo, false, programId);
			caseManagementViewAction.addRemoteIssues(checkBoxBeanList, demographicNo, false);
			caseManagementViewAction.addGroupIssues(checkBoxBeanList, demographicNo, false);

			checkedList = checkBoxBeanList.toArray(new CheckBoxBean[0]);

			for (CaseManagementIssue cmi : note.getIssues()) {
				setChecked_oldCme(checkedList, cmi);
			}
		}

		current = System.currentTimeMillis();

		cform.setIssueCheckList(checkedList);

		cform.setSign("off");
		if (!note.isIncludeissue()) cform.setIncludeIssue("off");
		else cform.setIncludeIssue("on");

		boolean passwd = caseManagementMgr.getEnabled();
		String chain = request.getParameter("chain");

		current = System.currentTimeMillis();
		logger.debug("The End of Edit " + String.valueOf(current - beginning));
		start = current;

		LogAction.addLog((String) session.getAttribute("user"), LogConst.EDIT, LogConst.CON_CME_NOTE, String.valueOf(note.getId()), request.getRemoteAddr(), demono, note.getAuditString());

		String frmName = "caseManagementEntryForm" + demono;
		logger.debug("Setting session form - " + frmName + " - " + String.valueOf(cform != null));
		session.setAttribute(frmName, cform);

		ActionForward fwd, finalFwd = null;
		if (chain != null && chain.length() > 0) {
			session.setAttribute("passwordEnabled", passwd);
			fwd = mapping.findForward(chain);
		} else {
			request.setAttribute("passwordEnabled", passwd);

			String ajax = request.getParameter("ajax");
			if (ajax != null && ajax.equalsIgnoreCase("true")) {
				fwd = mapping.findForward("issueList_ajax");
			} else {
				fwd = mapping.findForward("view");
			}
		}

		if (fwd != null) {
			StringBuilder path = new StringBuilder(fwd.getPath());

			if (path.indexOf("?") == -1) path.append("?");
			else path.append("&");

			path.append("demographicNo=" + demono);
			String noteBody = request.getParameter("noteBody");

			if (noteBody != null) path.append("&noteBody=" + noteBody);

			finalFwd = new ActionForward(path.toString());
		}
		return finalFwd;
	}

	private void setChecked_oldCme(CheckBoxBean[] checkedList, CaseManagementIssue cmi) {
		for (CheckBoxBean cbb : checkedList) {
			if (cbb.getIssueDisplay().code.equals(cmi.getIssue().getCode())) {
				cbb.setChecked("on");
				return;
			}
		}
	}

	public void resetTemp(String providerNo, String demoNo, String programId) {
		try {
			this.caseManagementMgr.deleteTmpSave(providerNo, demoNo, programId);
		} catch (Throwable e) {
			logger.warn("Warning", e);
		}
	}

	public ActionForward issueNoteSaveJson(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strNote = request.getParameter("value");
		String appointmentNo = request.getParameter("appointment_no");
		String providerNo = LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo();
		String noteId = request.getParameter("noteId");
		String demographicNo = request.getParameter("demographic_no");
		String issueCode = request.getParameter("issue_id");

		String issueAlphaCode = request.getParameter("issue_code");

		String archived = request.getParameter("archived");

		Date noteDate = new Date();

		strNote = org.apache.commons.lang.StringUtils.trimToNull(strNote);
		if( (archived == null || !archived.equalsIgnoreCase("true")) && (strNote == null || strNote.equals("")) )
			return null;

		CaseManagementNote note = new CaseManagementNote();
		if (!noteId.equals("0")) {
			note = this.caseManagementMgr.getNote(noteId);
			if ((archived == null || !archived.equalsIgnoreCase("true"))
					&& (request.getParameter("sign") == null || !request.getParameter("sign").equalsIgnoreCase("true"))
					&& note.getNote().equalsIgnoreCase(strNote))
				return null;

			note.setRevision(Integer.parseInt(note.getRevision())+1 + "");

			if (archived != null && archived.equalsIgnoreCase("true"))
				note.setArchived(true);

		} else {
			note.setDemographic_no(demographicNo);

			CaseManagementIssue cIssue;
			if (issueAlphaCode != null && issueAlphaCode.length() > 0)
				cIssue = this.caseManagementMgr.getIssueByIssueCode(demographicNo, issueAlphaCode);
			else
				cIssue = this.caseManagementMgr.getIssueById(demographicNo,issueCode);

			Set<CaseManagementIssue> issueSet = new HashSet<CaseManagementIssue>();
			Set<CaseManagementNote> noteSet = new HashSet<CaseManagementNote>();

			if( cIssue == null ) {
				Issue issue;
				if (issueAlphaCode != null && issueAlphaCode.length() > 0)
					issue = this.caseManagementMgr.getIssueByCode(issueAlphaCode);
				else
					issue = this.caseManagementMgr.getIssue(issueCode);

				cIssue = this.newIssueToCIssue(demographicNo, issue, Integer.parseInt("10016"));
				cIssue.setNotes(noteSet);
			}

			issueSet.add(cIssue);
			note.setIssues(issueSet);

			note.setCreate_date(noteDate);
			note.setObservation_date(noteDate);
			note.setRevision("1");

		}

		try {
			note.setAppointmentNo(Integer.parseInt(appointmentNo));
		} catch (Exception e) {
			// No appointment number set for this encounter
		}

		if (strNote != null)
			note.setNote(strNote);

		note.setProviderNo(providerNo);
		note.setProvider(LoggedInInfo.loggedInInfo.get().loggedInProvider);

		if (request.getParameter("sign") != null && request.getParameter("sign").equalsIgnoreCase("true")) {
			note.setSigning_provider_no(providerNo);
			note.setSigned(true);
			if (request.getParameter("appendSignText") != null && request.getParameter("appendSignText").equalsIgnoreCase("true")) {
				SimpleDateFormat dt = new SimpleDateFormat("dd-MMM-yyyy H:mm", Locale.ENGLISH);
				Date now = new Date();
				ResourceBundle props = ResourceBundle.getBundle("oscarResources", Locale.ENGLISH);

				ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao");
				String providerName = providerDao.getProviderName(providerNo);

				String signature = "[" + props.getString("oscarEncounter.class.EctSaveEncounterAction.msgSigned") + " " + dt.format(now) + " " + props.getString("oscarEncounter.class.EctSaveEncounterAction.msgSigBy") + " " + providerName + "]";
				note.setNote(note.getNote() + "\n" + signature);
			}

			if (request.getParameter("signAndExit") != null && request.getParameter("signAndExit").equalsIgnoreCase("true")) {
				OscarAppointmentDao appointmentDao = (OscarAppointmentDao) SpringUtils.getBean("oscarAppointmentDao");
				try {
					Appointment appointment = appointmentDao.find(Integer.parseInt(appointmentNo));
					if (appointment != null) {
						ApptStatusData statusData = new ApptStatusData();
						appointment.setStatus(statusData.signStatus());
						appointmentDao.merge(appointment);
					}
				} catch (Exception e) {
					logger.error("Couldn't parse appointmentNo: " + appointmentNo, e);
				}
			}
		} else if (!note.isSigned() && (archived == null || !archived.equalsIgnoreCase("true"))) {
			note.setSigned(false);
			note.setSigning_provider_no("");
		}


		// Determines what program & role to assign the note to
		ProgramProviderDAO programProviderDao = (ProgramProviderDAO) SpringUtils.getBean("programProviderDAO");
		ProviderDefaultProgramDao defaultProgramDao = (ProviderDefaultProgramDao) SpringUtils.getBean("providerDefaultProgramDao");
		boolean programSet = false;

		List<ProviderDefaultProgram> programs = defaultProgramDao.getProgramByProviderNo(providerNo);
		HashMap<Program,List<Secrole>> rolesForDemo = NotePermissionsAction.getAllProviderAccessibleRolesForDemo(providerNo, demographicNo);
		for (ProviderDefaultProgram pdp : programs) {
			for (Program p : rolesForDemo.keySet()) {
				if (pdp.getProgramId() == p.getId().intValue()) {
					List<ProgramProvider> programProviderList = programProviderDao.getProgramProviderByProviderProgramId(providerNo, (long) pdp.getProgramId());

					note.setProgram_no("" + pdp.getProgramId());
					note.setReporter_caisi_role("" + programProviderList.get(0).getRoleId());

					programSet = true;
				}
			}
		}

		if (!programSet && !rolesForDemo.isEmpty()) {
			Program program = rolesForDemo.keySet().iterator().next();
			ProgramProvider programProvider = programProviderDao.getProgramProvider(providerNo, (long) program.getId());
			note.setProgram_no("" + programProvider.getProgramId());
			note.setReporter_caisi_role("" + programProvider.getRoleId());
		}

		note.setReporter_program_team("0");

		CaseManagementCPP cpp = this.caseManagementMgr.getCPP(demographicNo);
		if( cpp == null ) {
			cpp = new CaseManagementCPP();
			cpp.setDemographic_no(demographicNo);
		}

		String savedStr = caseManagementMgr.saveNote(cpp, note, providerNo, null, null, null);
		addNewNoteLink(note.getId());


		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("id", note.getId());
		JSONObject json = JSONObject.fromObject(hashMap);
		response.getOutputStream().write(json.toString().getBytes());

		return null;
	}

	public ActionForward issueNoteSave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String strNote = request.getParameter("value");
		String appointmentNo = request.getParameter("appointmentNo");
		HttpSession session = request.getSession();

		String[] extNames = { "startdate", "resolutiondate", "proceduredate", "ageatonset", "problemstatus", "treatment", "exposuredetail", "relationship", "lifestage", "hidecpp", "problemdescription" };
		String[] extKeys = { CaseManagementNoteExt.STARTDATE, CaseManagementNoteExt.RESOLUTIONDATE, CaseManagementNoteExt.PROCEDUREDATE, CaseManagementNoteExt.AGEATONSET, CaseManagementNoteExt.PROBLEMSTATUS, CaseManagementNoteExt.TREATMENT, CaseManagementNoteExt.EXPOSUREDETAIL, CaseManagementNoteExt.RELATIONSHIP, CaseManagementNoteExt.LIFESTAGE, CaseManagementNoteExt.HIDECPP, CaseManagementNoteExt.PROBLEMDESC };

		// strNote = strNote.trim();
		logger.debug("Saving: " + strNote);
		strNote = org.apache.commons.lang.StringUtils.trimToNull(strNote);
		if (strNote == null || strNote.equals("")) return null;

		String providerNo = getProviderNo(request);
		if (providerNo == null) {
			providerNo = LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo();
		}
		Provider provider = getProvider(request);
		if (provider == null) {
			provider = LoggedInInfo.loggedInInfo.get().loggedInProvider;
		}
		String userName = provider != null ? provider.getFullName() : "";

		String demo = getDemographicNo(request);

		String noteId = request.getParameter("noteId");
		logger.debug("SAVING NOTE " + noteId + " STRING: " + strNote);
		String issueChange = request.getParameter("issueChange");
		String archived = request.getParameter("archived");

		CaseManagementNote note;
		boolean newNote = false;
		// we don't want to try to remove an issue from a new note so we test here
		if(noteId.isEmpty())
			noteId = "0";

		if (noteId.equals("0")) {

			note = new CaseManagementNote();
			note.setDemographic_no(demo);
			newNote = true;
		} else {
			boolean extChanged = false;
			List<CaseManagementNoteExt> cmeList = caseManagementNoteExtDao.getExtByNote(Long.valueOf(noteId));

			extNames: for (int i = 0; i < extNames.length; i++) {
				boolean extKeyMatched = false;

				String val = request.getParameter(extNames[i]);
				for (CaseManagementNoteExt cme : cmeList) {
					if (!cme.getKeyVal().equals(extKeys[i])) continue;

					if (i <= 2) {
						if (!nullEmptyEqual(cme.getDateValueStr(), partialFullDate(val, partialDateFormat(val)))) {
							extChanged = true;
							break extNames;
						}
						val = partialDateFormat(val);
					}
					if (!nullEmptyEqual(cme.getValue(), val)) {
						extChanged = true;
						break extNames;
					}
					extKeyMatched = true;
					break;
				}
				if (filled(val) && !extKeyMatched) { // new ext value(s) added
					extChanged = true;
					break extNames;
				}
			}

			// if note has not changed don't save
			note = this.caseManagementMgr.getNote(noteId);
			if (strNote.equals(note.getNote()) && !issueChange.equals("true") && !extChanged && (archived == null || archived.equalsIgnoreCase("false"))) return null;
		}
		note.setNote(strNote);
		note.setProviderNo(providerNo);
		note.setSigning_provider_no(providerNo);
		note.setSigned(true);

		if (provider != null) note.setProvider(provider);

		String logAction = new String();
		if (archived == null || archived.equalsIgnoreCase("false")) {
			note.setArchived(false);
		} else {
			note.setArchived(true);
			logger.debug("Setting archived to true");
			logAction = LogConst.ARCHIVE;
		}

		logger.debug("Note archived " + note.isArchived());
		String programId = (String) session.getAttribute("case_program_id");
		note.setProgram_no(programId);

		WebApplicationContext ctx = this.getSpringContext();

		ProgramManager programManager = (ProgramManager) ctx.getBean("programManager");
		AdmissionManager admissionManager = (AdmissionManager) ctx.getBean("admissionManager");

		String role = null;
		String team = null;

		try {
			role = String.valueOf((programManager.getProgramProvider(note.getProviderNo(), note.getProgram_no())).getRole().getId());
		} catch (Throwable e) {
			logger.error("Error", e);
			role = "0";
		}

		note.setReporter_caisi_role(role);

		try {
			team = String.valueOf((admissionManager.getAdmission(note.getProgram_no(), Integer.valueOf(note.getDemographic_no()))).getTeamId());
		} catch (Throwable e) {
			logger.error("Error", e);
			team = "0";
		}
		note.setReporter_program_team(team);
		if (appointmentNo != null && appointmentNo.length() > 0) {
			try {
				note.setAppointmentNo(Integer.parseInt(appointmentNo));
			} catch (NumberFormatException e) {
				logger.info("no appt no");
			}
		}
		// update note issues
		String sessionFrmName = "caseManagementEntryForm" + demo;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);
		Set<CaseManagementIssue> issueSet = new HashSet<CaseManagementIssue>();
		Set<CaseManagementNote> noteSet = new HashSet<CaseManagementNote>();
		String[] issue_id = request.getParameterValues("issue_id");
		CheckBoxBean[] existingCaseIssueList = sessionFrm.getIssueCheckList();
		ArrayList<CheckBoxBean> caseIssueList = new ArrayList<CheckBoxBean>();

		// copy existing issues for sessionfrm
		for (int idx = 0; idx < existingCaseIssueList.length; ++idx) {
			caseIssueList.add(existingCaseIssueList[idx]);
		}

		// first we check if any notes have been removed
		Set<CaseManagementIssue> noteIssues = note.getIssues();
		Iterator<CaseManagementIssue> iter = noteIssues.iterator();
		CaseManagementIssue cIssue;
		boolean issueExists;
		StringBuilder issueNames = new StringBuilder();
		int j;

		// we need the defining issue as originally passed in
		String reloadQuery = request.getParameter("reloadUrl");
		String[] params = reloadQuery.split("&");
		String cppStrIssue = new String();
		for (int p = 0; p < params.length; ++p) {
			String[] keyVal = params[p].split("=");
			if (keyVal[0].equalsIgnoreCase("issue_code")) {
				cppStrIssue = keyVal[1];
				break;
			}
		}

		boolean removed = false;

		// we've removed all issues so record that
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		if (issue_id == null) {
			while (iter.hasNext()) {
				cIssue = iter.next();
				issueNames.append(cIssue.getIssue().getDescription() + "\n");
			}

			strNote += "\n" + new SimpleDateFormat("dd-MMM-yyyy", request.getLocale()).format(new Date()) + " " + props.getString("oscarEncounter.removedIssue.Msg") + ":\n" + issueNames.toString();
			note.setNote(strNote);
			removed = true;
		} else {
			// check to see if we have removed any issues
			while (iter.hasNext()) {
				cIssue = iter.next();
				issueExists = false;
				for (j = 0; j < issue_id.length; ++j) {
					if (Long.parseLong(issue_id[j]) == cIssue.getIssue_id()) {
						issueExists = true;
						break;
					}
				}

				if (!issueExists) {
					issueNames.append(cIssue.getIssue().getDescription() + "\n");
					if (cIssue.getIssue().getCode().equalsIgnoreCase(cppStrIssue)) {
						removed = true;
					}
				}
			}

			// if we have removed an issue add it to message body
			if (issueNames.length() > 0) {
				strNote += "\n" + new SimpleDateFormat("dd-MMM-yyyy", request.getLocale()).format(new Date()) + " " + props.getString("oscarEncounter.removedIssue.Msg") + ":\n" + issueNames.toString();
				note.setNote(strNote);
			}

			for (int idx = 0; idx < issue_id.length; ++idx) {
				cIssue = this.caseManagementMgr.getIssueById(demo, issue_id[idx]);
				if (cIssue == null) {
					Issue issue = this.caseManagementMgr.getIssue(issue_id[idx]);
					cIssue = this.newIssueToCIssue(demo, issue, Integer.parseInt(programId));
					cIssue.setNotes(noteSet);

					// we have a new issue so add it to sessionfrm list
					CheckBoxBean checkbox = new CheckBoxBean();
					checkbox.setIssue(cIssue);
					checkbox.setChecked("off");
					checkbox.setUsed(true);
					caseIssueList.add(checkbox);
				}
				issueSet.add(cIssue);

			} // end for

			CheckBoxBean[] newCheckBox = new CheckBoxBean[caseIssueList.size()];
			newCheckBox = caseIssueList.toArray(newCheckBox);
			sessionFrm.setIssueCheckList(newCheckBox);

		}
		note.setIssues(issueSet);

		// now we can update the order of the notes if necessary
		// if the note has been removed or archived we move notes up in the order
		// else we check if position has changed and move the note down
		int position = -1;
		int newPos = Integer.parseInt(request.getParameter("position"));

		List<Issue> cppIssue = caseManagementMgr.getIssueInfoByCode(providerNo, cppStrIssue);
		List<CaseManagementNote> curCPPNotes = new ArrayList<CaseManagementNote>();
		if (cppIssue.size() > 0) {
			String[] strIssueId = { String.valueOf(cppIssue.get(0).getId()) };
			curCPPNotes = this.caseManagementMgr.getActiveNotes(demo, strIssueId);
		}

		CaseManagementNote curNote;
		long nId = Long.parseLong(noteId);
		int numNotes = curCPPNotes.size();
		// Alas we have to cycle through to make sure an ordering has been set
		// this is for legacy data
		for (int idx = 1; idx < curCPPNotes.size(); ++idx) {
			curNote = curCPPNotes.get(idx);
			if (curNote.getPosition() == 0) {
				curNote.setPosition(idx);

				if (curNote.getId() == nId) {
					note.setPosition(idx);
				}

				this.caseManagementMgr.updateNote(curNote);
			}
		}

		if (removed || note.isArchived()) {
			position = note.getPosition();
			for (CaseManagementNote c : curCPPNotes) {
				if (c.getId() == nId) {
					continue;
				} else if (position < c.getPosition()) {
					newPos = c.getPosition() - 1;
					c.setPosition(newPos);
					this.caseManagementMgr.updateNote(c);
				}
			}
		} else if ((newPos != note.getPosition() && !(newPos == numNotes && note.getPosition() == (numNotes - 1))) || newNote) {
			for (CaseManagementNote c : curCPPNotes) {
				if (c.getId() != nId) {
					if (newNote && c.getPosition() >= newPos) {
						position = c.getPosition() + 1;
						c.setPosition(position);
						this.caseManagementMgr.updateNote(c);
					} else if ((!newNote && newPos < note.getPosition()) && c.getPosition() >= newPos && c.getPosition() < note.getPosition()) {
						position = c.getPosition() + 1;
						c.setPosition(position);
						this.caseManagementMgr.updateNote(c);
					} else if ((!newNote && newPos > note.getPosition()) && c.getPosition() <= newPos && c.getPosition() > note.getPosition()) {
						position = c.getPosition() - 1;
						c.setPosition(position);
						this.caseManagementMgr.updateNote(c);
					}

				}
			}

			if (newPos == numNotes && !newNote) {
				--newPos;
			}
			note.setPosition(newPos);
		}

		/*
		 * Remove linked issue(s) and insert message into note
		 *
		 * if( removeIssue.equals("true") ) { issue_id = request.getParameterValues("issue_id"); issueSet = note.getIssues(); StringBuilder issueNames = new StringBuilder(); for( int idx = 0; idx < issue_id.length; ++idx ) { for(Iterator iter =
		 * issueSet.iterator();iter.hasNext();) { CaseManagementIssue cIssue = (CaseManagementIssue)iter.next(); if( cIssue.getIssue_id() == Long.parseLong(issue_id[idx]) ) { issueSet.remove(cIssue); issueNames.append(cIssue.getIssue().getDescription() +
		 * "\n"); break; } } } //Force hibernate to save rather than update Set tmpIssues = new HashSet(issueSet); note.setIssues(tmpIssues); strNote += "\n" + new SimpleDateFormat("dd-MMM-yyyy").format(new Date()) + " Removed following issue(s):\n" +
		 * issueNames.toString(); note.setNote(strNote); }
		 */

		int revision;

		if (note.getRevision() != null) {
			revision = Integer.parseInt(note.getRevision());
			++revision;
		} else revision = 1;

		note.setRevision(String.valueOf(revision));
		Date now = new Date();
		if (note.getObservation_date() == null) {
			note.setObservation_date(now);
		}

		note.setUpdate_date(now);
		if (note.getCreate_date() == null) note.setCreate_date(now);

		/* save note including add signature */
		String lastSavedNoteString = (String) session.getAttribute("lastSavedNoteString");
		String roleName = caseManagementMgr.getRoleName(providerNo, note.getProgram_no());
		CaseManagementCPP cpp = this.caseManagementMgr.getCPP(demo);
		if (cpp == null) {
			cpp = new CaseManagementCPP();
			cpp.setDemographic_no(demo);
		}
		cpp = copyNote2cpp(cpp, note);
		String savedStr = caseManagementMgr.saveNote(cpp, note, providerNo, userName, lastSavedNoteString, roleName);
		addNewNoteLink(note.getId());
		logger.debug("Saved note " + savedStr);
		caseManagementMgr.saveCPP(cpp, providerNo);
		/* remember the str written into echart */
		session.setAttribute("lastSavedNoteString", savedStr);

		/* save extra fields */
		CaseManagementNoteExt cme = new CaseManagementNoteExt();
		cme.setNoteId(note.getId());
		for (int i = 0; i < extNames.length; i++) {
			String val = request.getParameter(extNames[i]);
			if (filled(val)) {
				cme.setKeyVal(extKeys[i]);
				cme.setDateValue((Date) null);
				cme.setValue(null);
				if (i <= 2) {
					if (writePartialDate(val, cme)) caseManagementMgr.saveNoteExt(cme);
				} else {
					cme.setValue(val);
					caseManagementMgr.saveNoteExt(cme);
				}
			}
		}

		/* Save annotation */

		String attrib_name = request.getParameter("annotation_attrib");
		CaseManagementNote cmn = (CaseManagementNote) session.getAttribute(attrib_name);
		if (cmn != null) {
			// new annotation created and got it in session attribute

			caseManagementMgr.saveNoteSimple(cmn);
			CaseManagementNoteLink cml = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cmn.getId());
			caseManagementMgr.saveNoteLink(cml);
			LogAction.addLog(providerNo, LogConst.ANNOTATE, LogConst.CON_CME_NOTE, String.valueOf(cmn.getId()), request.getRemoteAddr(), demo, cmn.getNote());
			session.removeAttribute(attrib_name);

		}
		if (!noteId.equals("0")) {
			// Not a new note, look for old annotation

			CaseManagementNoteLink cml_anno = null;
			CaseManagementNoteLink cml_dump = null;
			List<CaseManagementNoteLink> cmll = caseManagementMgr.getLinkByTableIdDesc(CaseManagementNoteLink.CASEMGMTNOTE, Long.valueOf(noteId));
			for (CaseManagementNoteLink link : cmll) {
				CaseManagementNote cmmn = caseManagementMgr.getNote(link.getNoteId().toString());
				if (cmmn == null) continue;

				if (cmmn.getNote().startsWith("imported.cms4.2011.06")) {
					if (cml_dump == null) cml_dump = link;
				} else {
					if (cml_anno == null) cml_anno = link;
				}
				if (cml_anno != null && cml_dump != null) break;
			}

			if (cml_anno != null) {// old annotation exists - create new link
				CaseManagementNoteLink cml_n = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cml_anno.getNoteId());
				caseManagementMgr.saveNoteLink(cml_n);
			}
			if (cml_dump != null) {// old dump exists - create new link
				CaseManagementNoteLink cml_n = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cml_dump.getNoteId());
				caseManagementMgr.saveNoteLink(cml_n);
			}
		}
		caseManagementMgr.getEditors(note);

		if (newNote) {
			logAction = LogConst.ADD;
		} else if (note.isArchived()) {
			logAction = LogConst.ARCHIVE;
		} else {
			logAction = LogConst.UPDATE;
		}

		LogAction.addLog((String) session.getAttribute("user"), logAction, LogConst.CON_CME_NOTE, String.valueOf(note.getId()), request.getRemoteAddr(), demo, note.getAuditString());

		String f = request.getParameter("forward");
		if (f != null && f.equals("none")) {
			response.getWriter().println(note.getId());
			return null;
		}

		ActionForward forward = mapping.findForward("listCPPNotes");
		StringBuilder path = new StringBuilder(forward.getPath());
		path.append("?" + reloadQuery);
		return new ActionForward(path.toString());
	}

	private long noteSave(CaseManagementEntryFormBean cform, HttpServletRequest request) throws Exception {
		WebApplicationContext ctx = getSpringContext();
		ProgramManager programManager = (ProgramManager) ctx.getBean("programManager");
		AdmissionManager admissionManager = (AdmissionManager) ctx.getBean("admissionManager");
		HttpSession session = request.getSession();

		// we don't want to save empty notes!

		String noteTxt = cform.getCaseNote_note();
		noteTxt = org.apache.commons.lang.StringUtils.trimToNull(noteTxt);
		if (noteTxt == null || noteTxt.equals("")) return -1;

		String demo = getDemographicNo(request);

		String sessionFrmName = "caseManagementEntryForm" + demo;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		CaseManagementNote note = sessionFrm.getCaseNote();
		Long old_note_id = note.getId(); // saved for use with annotation

		note.setNote(noteTxt);

		String providerNo = getProviderNo(request);
		Provider provider = getProvider(request);
		String userName = provider != null ? provider.getFullName() : "";

		CaseManagementCPP cpp = this.caseManagementMgr.getCPP(demo);
		if (cpp == null) {
			cpp = new CaseManagementCPP();
			cpp.setDemographic_no(demo);
		}

		boolean inCaisi = OscarProperties.getInstance().isCaisiLoaded();

		String lastSavedNoteString = (String) session.getAttribute("lastSavedNoteString");

		// bug fix - encounter type was not being updated.
		String encounterType = request.getParameter("caseNote.encounter_type");
		if (encounterType != null) {
			note.setEncounter_type(encounterType);
		}

		String hourOfEncounterTime = request.getParameter("hourOfEncounterTime");
		if (hourOfEncounterTime != null && hourOfEncounterTime != "") {
			note.setHourOfEncounterTime(Integer.valueOf(hourOfEncounterTime));
		}

		String minuteOfEncounterTime = request.getParameter("minuteOfEncounterTime");
		if (minuteOfEncounterTime != null && minuteOfEncounterTime != "") {
			note.setMinuteOfEncounterTime(Integer.valueOf(minuteOfEncounterTime));
		}

		String hourOfEncTransportationTime = request.getParameter("hourOfEncTransportationTime");
		if (hourOfEncTransportationTime != null && hourOfEncTransportationTime != "") {
			note.setHourOfEncTransportationTime(Integer.valueOf(hourOfEncTransportationTime));
		}

		String minuteOfEncTransportationTime = request.getParameter("minuteOfEncTransportationTime");
		if (minuteOfEncTransportationTime != null && minuteOfEncTransportationTime != "") {
			note.setMinuteOfEncTransportationTime(Integer.valueOf(minuteOfEncTransportationTime));
		}

		String sign = request.getParameter("sign");
		if (sign == null) {
			note.setSigning_provider_no("");
			note.setSigned(false);
			sessionFrm.setSign("off");
		} else if (sign.equalsIgnoreCase("persist")) {

			if (note.isSigned()) {
				note.setSigning_provider_no(providerNo);
				note.setSigned(true);
			} else {
				note.setSigning_provider_no("");
				note.setSigned(false);
				sessionFrm.setSign("off");
			}
		} else if (sign.equalsIgnoreCase("on")) {
			note.setSigning_provider_no(providerNo);
			note.setSigned(true);
		} else {
			note.setSigning_provider_no("");
			note.setSigned(false);
			sessionFrm.setSign("off");
		}

		note.setProviderNo(providerNo);
		if (provider != null) note.setProvider(provider);

		String role = null;
		String team = null;

		// if this is an update, don't overwrite the program id
		if (note.getProgram_no() == null || note.getProgram_no().equals("") || !note.getProgram_no().equals("")) {
			String programId = (String) session.getAttribute("case_program_id");
			note.setProgram_no(programId);
		}

		try {
			role = String.valueOf((programManager.getProgramProvider(note.getProviderNo(), note.getProgram_no())).getRole().getId());
		} catch (Throwable e) {
			role = "0";
		}
		/*
		 * if(session.getAttribute("archiveView")!="true") note.setReporter_caisi_role(role); else note.setReporter_caisi_role("1");
		 */
		note.setReporter_caisi_role(role);

		try {
			Admission admission = admissionManager.getAdmission(note.getProgram_no(), Integer.valueOf(note.getDemographic_no()));
			if (admission != null) {
				team = String.valueOf(admission.getTeamId());
			} else {
				team = "0";
			}
		} catch (Throwable e) {
			logger.error("Error", e);
			team = "0";
		}
		note.setReporter_program_team(team);

		/* get the checked issue save into note */
		// this goes into the database casemgmt_issue table
		List<CaseManagementIssue> issuelist = new ArrayList<CaseManagementIssue>();

		CheckBoxBean[] checkedlist = sessionFrm.getIssueCheckList();

		// this gets attached to the CaseManagementNote object
		Set<CaseManagementIssue> issueset = new HashSet<CaseManagementIssue>();
		// wherever this is populated, it's not here...
		Set<CaseManagementNote> noteSet = new HashSet<CaseManagementNote>();
		String ongoing = new String();
		Boolean useNewCaseMgmt = Boolean.valueOf((String) session.getAttribute("newCaseManagement"));
		if (useNewCaseMgmt) {
			ongoing = saveCheckedIssues_newCme(request, demo, note, issuelist, checkedlist, issueset, noteSet, ongoing);
		} else {
			ongoing = saveCheckedIssues_oldCme(request, demo, issuelist, checkedlist, issueset, noteSet, ongoing);
		}

		sessionFrm.setIssueCheckList(checkedlist);
		note.setIssues(issueset);

		/* remove signature and the related issues from note */
		String noteString = note.getNote();
		// noteString = removeSignature(noteString);
		noteString = removeCurrentIssue(noteString);
		note.setNote(noteString);

		/* add issues into notes */
		String includeIssue = request.getParameter("includeIssue");
		if (includeIssue == null || !includeIssue.equals("on")) {
			/* set includeissue in note */
			note.setIncludeissue(false);
			sessionFrm.setIncludeIssue("off");
		} else {
			note.setIncludeissue(true);
			/* add the related issues to note */

			String issueString = new String();
			issueString = createIssueString(issueset);
			// insert the string before signiture

			int index = noteString.indexOf("\n[[");
			if (index >= 0) {
				String begString = noteString.substring(0, index);
				String endString = noteString.substring(index + 1);
				note.setNote(begString + issueString + endString);
			} else {
				note.setNote(noteString + issueString);
			}
		}

		/* save all issue changes for demographic */
		caseManagementMgr.saveAndUpdateCaseIssues(issuelist);
		if (inCaisi) cpp.setOngoingConcerns(ongoing);

		// update appointment and add verify message to note if verified
		String strBeanName = "casemgmt_oscar_bean" + demo;
		EctSessionBean sessionBean = (EctSessionBean) session.getAttribute(strBeanName);
		String verify = request.getParameter("verify");
		OscarAppointmentDao appointmentDao = (OscarAppointmentDao) SpringUtils.getBean("oscarAppointmentDao");

		Date now = new Date();
		String roleName = caseManagementMgr.getRoleName(providerNo, note.getProgram_no());

		if (verify != null && verify.equalsIgnoreCase("on")) {
			String message = caseManagementMgr.getSignature(providerNo, userName, roleName, request.getLocale(), caseManagementMgr.SIGNATURE_VERIFY);

			String n = note.getNote() + "\n" + message;
			note.setNote(n);

			// only update appt if there is one
			if (sessionBean.appointmentNo != null && !(sessionBean.appointmentNo.equals("") || sessionBean.appointmentNo.equals("0"))) {
				String apptStatus = updateApptStatus(sessionBean.status, "verify");
				Appointment appointment = appointmentDao.find(Integer.parseInt(sessionBean.appointmentNo));
				appointmentArchiveDao.archiveAppointment(appointment);
				appointment.setStatus(apptStatus);
				appointment.setLastUpdateUser(providerNo);
				appointmentDao.merge(appointment);
			}

		} else if (note.isSigned()) {
			String message = caseManagementMgr.getSignature(providerNo, userName, roleName, request.getLocale(), caseManagementMgr.SIGNATURE_SIGNED);

			String n = note.getNote() + "\n" + message;
			note.setNote(n);

			// only update appt if there is one
			if (sessionBean.appointmentNo != null && !(sessionBean.appointmentNo.equals("") || sessionBean.appointmentNo.equals("0"))) {
				String apptStatus = updateApptStatus(sessionBean.status, "sign");
				Appointment appointment = appointmentDao.find(Integer.parseInt(sessionBean.appointmentNo));
				appointmentArchiveDao.archiveAppointment(appointment);
				appointment.setStatus(apptStatus);
				appointment.setLastUpdateUser(providerNo);
				appointmentDao.merge(appointment);
			}
		}

		// PLACEHOLDER FOR DX CHECK
		/*
		 * If an issue is checked, new , and certain - we want to check dx associations. if found in dx associations. we want to make an entry into dx.
		 */
		if (note.isSigned()) {
			for (CaseManagementIssue cmIssue : issueset) {
				if (cmIssue.isCertain()) {
					DxAssociation assoc = dxDao.findAssociation(cmIssue.getIssue().getType(), cmIssue.getIssue().getCode());
					if (assoc != null) {
						// we found a match. Let's add them to registry
						this.caseManagementMgr.saveToDx(getDemographicNo(request), assoc.getDxCode(), assoc.getDxCodeType(), true);

					}
				}
			}
		}

		/*
		 * if provider is a doctor or nurse,get all major and resolved medical issue for demograhhic and append them to CPP medical history
		 */
		if (inCaisi) {
			/* get access right */
			List accessRight = caseManagementMgr.getAccessRight(providerNo, getDemographicNo(request), (String) session.getAttribute("case_program_id"));
			setCPPMedicalHistory(cpp, providerNo, accessRight);
			cpp.setUpdate_date(now);
			caseManagementMgr.saveCPP(cpp, providerNo);
		}

		// update password
		String passwd = cform.getCaseNote().getPassword();
		if (passwd != null && passwd.trim().length() > 0) {
			note.setPassword(passwd);
			note.setLocked(true);
		}

		int revision;

		if (note.getRevision() != null) {
			revision = Integer.parseInt(note.getRevision());
			++revision;
		} else revision = 1;

		note.setRevision(String.valueOf(revision));

		String observationDate = cform.getObservation_date();
		ResourceBundle props = ResourceBundle.getBundle("oscarResources", request.getLocale());
		if (observationDate != null && !observationDate.equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy H:mm", request.getLocale());
			Date dateObserve = formatter.parse(observationDate);
			if (dateObserve.getTime() > now.getTime()) {
				request.setAttribute("DateError", props.getString("oscarEncounter.futureDate.Msg"));
				note.setObservation_date(now);
			} else note.setObservation_date(dateObserve);
		} else if (note.getObservation_date() == null) {
			note.setObservation_date(now);
		}

		note.setUpdate_date(now);
		boolean newNote = false;
		if (note.getCreate_date() == null) {
			note.setCreate_date(now);
			newNote = true;
		}

		// Checks whether the user can set the program via the UI - if so, make sure that they can't screw it up if they do
		if (OscarProperties.getInstance().getBooleanProperty("note_program_ui_enabled", "true")) {
			String noteProgramNo = request.getParameter("_note_program_no");
			String noteRoleId = request.getParameter("_note_role_id");

			if (noteProgramNo != null && noteRoleId != null && noteProgramNo.trim().length() > 0 && noteRoleId.trim().length() > 0) {
				if (noteProgramNo.equalsIgnoreCase("-2") || noteRoleId.equalsIgnoreCase("-2")) {
					throw new Exception("Patient is not admitted to any programs user has access to. [roleId=-2, programNo=-2]");
				} else if (!noteProgramNo.equalsIgnoreCase("-1") && !noteRoleId.equalsIgnoreCase("-1")) {
					note.setProgram_no(noteProgramNo);
					note.setReporter_caisi_role(noteRoleId);
				}
			} else {
				throw new Exception("Missing role id or program number. [roleId=" + noteRoleId + ", programNo=" + noteProgramNo + "]");
			}
		}

		if (sessionBean.appointmentNo != null && sessionBean.appointmentNo.length() > 0) {
			note.setAppointmentNo(Integer.parseInt(sessionBean.appointmentNo));
		}

		/* save note including add signature */
		String savedStr = caseManagementMgr.saveNote(cpp, note, providerNo, userName, lastSavedNoteString, roleName);
		addNewNoteLink(note.getId());
		/* remember the str written into echart */
		session.setAttribute("lastSavedNoteString", savedStr);
		caseManagementMgr.getEditors(note);
		cform.setCaseNote(note);

		try {
			this.caseManagementMgr.deleteTmpSave(providerNo, note.getDemographic_no(), note.getProgram_no());
		} catch (Throwable e) {
			logger.warn("warn", e);
		}

		/* Save annotation */

		String attrib_name = request.getParameter("annotation_attribname");
		CaseManagementNote cmn = (CaseManagementNote) session.getAttribute(attrib_name);
		if (cmn != null) {
			// new annotation created and got it in session attribute

			caseManagementMgr.saveNoteSimple(cmn);
			CaseManagementNoteLink cml = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cmn.getId());
			caseManagementMgr.saveNoteLink(cml);
			LogAction.addLog(providerNo, LogConst.ANNOTATE, LogConst.CON_CME_NOTE, String.valueOf(cmn.getId()), request.getRemoteAddr(), demo, cmn.getNote());
			session.removeAttribute(attrib_name);

		}
		if (old_note_id != null) {
			// Not a new note, look for old annotation

			CaseManagementNoteLink cml_anno = null;
			CaseManagementNoteLink cml_dump = null;
			List<CaseManagementNoteLink> cmll = caseManagementMgr.getLinkByTableIdDesc(CaseManagementNoteLink.CASEMGMTNOTE, old_note_id);
			for (CaseManagementNoteLink link : cmll) {
				CaseManagementNote cmmn = caseManagementMgr.getNote(link.getNoteId().toString());
				if (cmmn == null) continue;

				if (cmmn.getNote().startsWith("imported.cms4.2011.06")) {
					if (cml_dump == null) cml_dump = link;
				} else {
					if (cml_anno == null) cml_anno = link;
				}
				if (cml_anno != null && cml_dump != null) break;
			}

			if (cml_anno != null) {// old annotation exists - create new link
				CaseManagementNoteLink cml_n = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cml_anno.getNoteId());
				caseManagementMgr.saveNoteLink(cml_n);
			}
			if (cml_dump != null) {// old dump exists - create new link
				CaseManagementNoteLink cml_n = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE, note.getId(), cml_dump.getNoteId());
				caseManagementMgr.saveNoteLink(cml_n);
			}
		}

		String logAction;
		if (newNote) {
			logAction = LogConst.ADD;
		} else {
			logAction = LogConst.UPDATE;
		}
		LogAction.addLog((String) session.getAttribute("user"), logAction, LogConst.CON_CME_NOTE, "" + Long.valueOf(note.getId()).intValue(), request.getRemoteAddr(), demo, note.getAuditString());

		return note.getId();
	}

	private String updateApptStatus(String status, String type) {
		oscar.appt.ApptStatusData as = new oscar.appt.ApptStatusData();
		as.setApptStatus(status);

		if (type.equalsIgnoreCase("sign")) status = as.signStatus();
		if (type.equalsIgnoreCase("verify")) status = as.verifyStatus();

		return status;
	}

	private String saveCheckedIssues_oldCme(HttpServletRequest request, String demo, List<CaseManagementIssue> issuelist, CheckBoxBean[] checkedlist, Set<CaseManagementIssue> issueset, Set noteSet, String ongoing) {

		int demographicNo = Integer.parseInt(demo);

		for (int i = 0; i < checkedlist.length; i++) {
			CheckBoxBean checkBoxBean = checkedlist[i];
			IssueDisplay issueDisplay = checkBoxBean.getIssueDisplay();

			if (issueDisplay.resolved != null && issueDisplay.resolved.equals("unresolved")) {
				ongoing = ongoing + issueDisplay.getDescription() + "\n";
			}

			boolean isChecked = WebUtils.isChecked(request, "issueCheckList[" + i + "].checked");

			CaseManagementIssue caseManagementIssue = null;
			caseManagementIssue = caseManagementIssueDao.getIssuebyIssueCode(demo, issueDisplay.code);
			if (caseManagementIssue == null && isChecked) {
				Issue issue = issueDao.findIssueByCode(issueDisplay.code);
				if (issue != null) {
					caseManagementIssue = new CaseManagementIssue();
					caseManagementIssue.setDemographic_no(demo);
					caseManagementIssue.setIssue_id(issue.getId());
					caseManagementIssue.setType(issue.getRole());
					caseManagementIssue.setUpdate_date(new Date());

					// Should not save duplicated issue for one demographic
					if (caseManagementIssueDao.getIssuebyId(demo, String.valueOf(issue.getId())) == null) {
						caseManagementIssueDao.saveIssue(caseManagementIssue);
					}
					// reload to materliase generated fields.
					caseManagementIssue = caseManagementIssueDao.getIssuebyId(demo, String.valueOf(issue.getId()));
				}
			} else if( caseManagementIssue != null && isChecked) {
				caseManagementIssue.setAcute("acute".equals(issueDisplay.acute));
				caseManagementIssue.setCertain("certain".equals(issueDisplay.certain));
				caseManagementIssue.setMajor("major".equals(issueDisplay.major));
				caseManagementIssue.setResolved("resolved".equals(issueDisplay.resolved));
				Issue issue = issueDao.findIssueByCode(issueDisplay.code);
				if (issue != null) {
					caseManagementIssue.setUpdate_date(new Date());
					// Should not save duplicated issue for one demographic
					// But should be able to update existing issues.
					caseManagementIssueDao.saveIssue(caseManagementIssue);
					// reload to materliase generated fields.
					caseManagementIssue = caseManagementIssueDao.getIssuebyId(demo, String.valueOf(issue.getId()));
				}
			}

			if (caseManagementIssue == null) continue;
			else copyIssueDisplayToCaseManagementIssue(caseManagementIssue, issueDisplay);

			if (isChecked) {
				checkBoxBean.setChecked("on");
				checkBoxBean.setUsed(true);
				caseManagementIssue.setNotes(noteSet);

				issueset.add(caseManagementIssue);
			} else {
				checkBoxBean.setChecked("off");
				boolean isLocal = "local".equals(issueDisplay.location);
				if (!isLocal) {
					checkBoxBean.setUsed(false);
				} else {
					checkBoxBean.setUsed(caseManagementNoteDao.haveIssue(issueDisplay.code, demographicNo));
				}
			}

			if (!containsIssue(issuelist, caseManagementIssue)) issuelist.add(caseManagementIssue);
		}
		return ongoing;
	}

	private boolean containsIssue(List<CaseManagementIssue> issuelist, CaseManagementIssue issue) {
		for (CaseManagementIssue tempIssue : issuelist) {
			if (tempIssue.getId() != null && tempIssue.getId().equals(issue.getId())) return (true);
		}

		return (false);
	}

	private void copyIssueDisplayToCaseManagementIssue(CaseManagementIssue caseManagementIssue, IssueDisplay issueDisplay) {
		caseManagementIssue.setAcute("acute".equals(issueDisplay.acute));
		caseManagementIssue.setCertain("certain".equals(issueDisplay.certain));
		caseManagementIssue.setMajor("major".equals(issueDisplay.major));
		caseManagementIssue.setResolved("resolved".equals(issueDisplay.resolved));
	}

	private String saveCheckedIssues_newCme(HttpServletRequest request, String demo, CaseManagementNote note, List issuelist, CheckBoxBean[] checkedlist, Set issueset, Set noteSet, String ongoing) {
		/*
		 * for (int i = 0; i < checkedlist.length; i++) { if (!checkedlist[i].getIssue().isResolved()) ongoing = ongoing + checkedlist[i].getIssue().getIssue().getDescription() + "\n"; String ischecked = request.getParameter("issueCheckList[" + i +
		 * "].checked"); CaseManagementIssue iss = checkedlist[i].getIssue(); if (ischecked != null && ischecked.equalsIgnoreCase("on")) { checkedlist[i].setChecked("on"); checkedlist[i].setUsed(true); iss.setNotes(noteSet);
		 * issueset.add(checkedlist[i].getIssue()); } else { checkedlist[i].setChecked("off"); checkedlist[i].setUsed(caseManagementMgr.haveIssue(iss.getId(), note.getId(), demo)); checkedlist[i].setUsed(false); }
		 *
		 * issuelist.add(checkedlist[i].getIssue()); } return ongoing;
		 */

		int demographicNo = Integer.parseInt(demo);

		for (int i = 0; i < checkedlist.length; i++) {
			CheckBoxBean checkBoxBean = checkedlist[i];
			IssueDisplay issueDisplay = checkBoxBean.getIssueDisplay();

			if (issueDisplay.resolved != null && issueDisplay.resolved.equals("unresolved")) {
				ongoing = ongoing + issueDisplay.getDescription() + "\n";
			}

			boolean isChecked = WebUtils.isChecked(request, "issueCheckList[" + i + "].checked");

			CaseManagementIssue caseManagementIssue = null;
			caseManagementIssue = caseManagementIssueDao.getIssuebyIssueCode(demo, issueDisplay.code);
			if (caseManagementIssue == null && isChecked) {
				Issue issue = issueDao.findIssueByCode(issueDisplay.code);
				if (issue != null) {
					caseManagementIssue = new CaseManagementIssue();
					caseManagementIssue.setDemographic_no(demo);
					caseManagementIssue.setIssue_id(issue.getId());
					caseManagementIssue.setType(issue.getRole());
					caseManagementIssue.setUpdate_date(new Date());

					// Should not save duplicated issue for one demographic
					if (caseManagementIssueDao.getIssuebyId(demo, String.valueOf(issue.getId())) == null) {
						caseManagementIssueDao.saveIssue(caseManagementIssue);
					}
					// reload to materliase generated fields.
					caseManagementIssue = caseManagementIssueDao.getIssuebyId(demo, String.valueOf(issue.getId()));
				}
			}

			if (caseManagementIssue == null) continue;
			else copyIssueDisplayToCaseManagementIssue(caseManagementIssue, issueDisplay);

			if (isChecked) {
				checkBoxBean.setChecked("on");
				checkBoxBean.setUsed(true);
				caseManagementIssue.setNotes(noteSet);

				issueset.add(caseManagementIssue);
			} else {
				checkBoxBean.setChecked("off");
				boolean isLocal = "local".equals(issueDisplay.location);
				if (!isLocal) {
					checkBoxBean.setUsed(false);
				} else {
					checkBoxBean.setUsed(caseManagementNoteDao.haveIssue(issueDisplay.code, demographicNo));
				}
			}

			if (!containsIssue(issuelist, caseManagementIssue)) issuelist.add(caseManagementIssue);
		}
		return ongoing;

	}

	public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession();
		if (session == null || session.getAttribute("userrole") == null) return mapping.findForward("expired");

		// String providerNo = getProviderNo(request);
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		request.setAttribute("change_flag", "false");

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		request.setAttribute("from", request.getParameter("from"));
		long noteId = noteSave(cform, request);
		/*
		 * CaseManagementNote preNote=new CaseManagementNote(); if(cform.getNoteId()!=null) { Long nId=Long.parseLong(cform.getNoteId()); preNote.setId(nId); }
		 */

		/* prepare the message */
		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("note.saved"));
		saveMessages(request, messages);

		// are we in the new encounter and chaining actions?
		String chain = request.getParameter("chain");
		if (chain != null && !chain.equals("")) {
			String varName = "newNote";
			session.setAttribute(varName, false);
			varName = "saveNote" + demono;
			session.setAttribute(varName, new Boolean(true)); // tell CaseManagementView we have just saved note
			ActionForward fwd = mapping.findForward(chain);
			StringBuilder path = new StringBuilder(fwd.getPath());
			ResourceBundle props = ResourceBundle.getBundle("oscarResources");
			if (request.getAttribute("DateError") != null) {
				if (path.indexOf("?") == -1) path.append("?");
				else path.append("&");

				path.append("DateError=" + props.getString("oscarEncounter.futureDate.Msg"));
			}

			ActionForward forward = new ActionForward();
			forward.setPath(path.toString());
			return forward;
		}

		// this.caseManagementMgr.saveNote();
		return mapping.findForward("view");
	}

	public ActionForward ajaxsave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		String noteTxt = request.getParameter("noteTxt");
		noteTxt = org.apache.commons.lang.StringUtils.trimToNull(noteTxt);
		if (noteTxt == null || noteTxt.equals("")) return null;

		logger.debug("Saving Note" + request.getParameter("nId"));
		logger.debug("Text -- " + noteTxt);
		String demo = getDemographicNo(request);
		String providerNo = getProviderNo(request);
		Provider provider = getProvider(request);

		CaseManagementNote note;
		String history;
		String noteId = request.getParameter("nId");
		boolean newNote;
		Date now = new Date();
		if (noteId.substring(0, 1).equals("0")) {
			note = new CaseManagementNote();
			note.setDemographic_no(demo);
			history = new String();
			newNote = true;
		} else {
			note = this.caseManagementMgr.getNote(request.getParameter("nId"));
			history = note.getHistory();
			history = "---------History Record---------" + history;
			newNote = false;
		}

		String observationDate = request.getParameter("obsDate");
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		if (observationDate != null && !observationDate.equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy H:mm", request.getLocale());
			Date dateObserve = formatter.parse(observationDate);
			if (dateObserve.getTime() > now.getTime()) {
				request.setAttribute("DateError", props.getString("oscarEncounter.futureDate.Msg"));
				note.setObservation_date(now);
			} else note.setObservation_date(dateObserve);
		} else if (note.getObservation_date() == null) {
			note.setObservation_date(now);
		}

		history = noteTxt + "[[" + now + "]]" + history;
		note.setNote(noteTxt);
		note.setHistory(history);

		if (note.isSigned()) {
			note.setSigning_provider_no(providerNo);
			note.setSigned(true);
		} else {
			note.setSigning_provider_no("");
			note.setSigned(false);
		}

		note.setProviderNo(providerNo);
		if (provider != null) note.setProvider(provider);

		String programId = (String) session.getAttribute("case_program_id");
		note.setProgram_no(programId);

		WebApplicationContext ctx = this.getSpringContext();
		ProgramManager programManager = (ProgramManager) ctx.getBean("programManager");
		AdmissionManager admissionManager = (AdmissionManager) ctx.getBean("admissionManager");

		String role = null;
		try {
			role = String.valueOf((programManager.getProgramProvider(note.getProviderNo(), note.getProgram_no())).getRole().getId());
		} catch (Throwable e) {
			logger.error("Error", e);
			role = "0";
		}

		note.setReporter_caisi_role(role);

		String team = null;
		try {
			team = String.valueOf((admissionManager.getAdmission(note.getProgram_no(), Integer.valueOf(note.getDemographic_no()))).getTeamId());
		} catch (Throwable e) {
			logger.error("Error", e);
			team = "0";
		}

		note.setReporter_program_team(team);

		String sessionName = "caseManagementEntryForm" + demo;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionName);
		List<CaseManagementIssue> issuelist = new ArrayList<CaseManagementIssue>();
		Set<CaseManagementIssue> issueset = new HashSet<CaseManagementIssue>();
		Set<CaseManagementNote> noteSet = new HashSet<CaseManagementNote>();

		int numIssues = Integer.parseInt(request.getParameter("numIssues"));
		// CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;

		CheckBoxBean[] checkedlist = sessionFrm.getIssueCheckList();
		for (int i = 0; i < numIssues; i++) {
			String ischecked = request.getParameter("issue" + i);
			if (ischecked != null && ischecked.equalsIgnoreCase("on")) {
				checkedlist[i].setChecked("on");
				CaseManagementIssue iss = checkedlist[i].getIssue();
				iss.setNotes(noteSet);
				issueset.add(checkedlist[i].getIssue());
			} else {
				checkedlist[i].setChecked("off");
			}
			issuelist.add(checkedlist[i].getIssue());
		}

		note.setIssues(issueset);
		note.setIncludeissue(false);
		caseManagementMgr.saveAndUpdateCaseIssues(issuelist);
		sessionFrm.setIssueCheckList(checkedlist);

		int revision;

		if (note.getRevision() != null) {
			revision = Integer.parseInt(note.getRevision());
			++revision;
		} else revision = 1;

		note.setRevision(String.valueOf(revision));

		note.setUpdate_date(now);
		if (note.getCreate_date() == null) note.setCreate_date(now);

		note.setEncounter_type(request.getParameter("encType"));

		String hourOfEncounterTime = request.getParameter("hourOfEncounterTime");
		if (hourOfEncounterTime != null) {
			note.setHourOfEncounterTime(Integer.valueOf(hourOfEncounterTime));
		}
		String minuteOfEncounterTime = request.getParameter("minuteOfEncounterTime");
		if (minuteOfEncounterTime != null) {
			note.setMinuteOfEncounterTime(Integer.valueOf(minuteOfEncounterTime));
		}
		String hourOfEncTransportationTime = request.getParameter("hourOfEncTransportationTime");
		if (minuteOfEncounterTime != null) {
			note.setHourOfEncTransportationTime(Integer.valueOf(hourOfEncTransportationTime));
		}
		String minuteOfEncTransportationTime = request.getParameter("minuteOfEncTransportationTime");
		if (minuteOfEncounterTime != null) {
			note.setMinuteOfEncTransportationTime(Integer.valueOf(minuteOfEncTransportationTime));
		}

		// check if previous note is doc note.

		Long prevNoteId = note.getId();

		this.caseManagementMgr.saveNoteSimple(note);
		this.caseManagementMgr.getEditors(note);

		if (prevNoteId != null) {
			addNewNoteLink(prevNoteId);
		}

		try {
			this.caseManagementMgr.deleteTmpSave(providerNo, note.getDemographic_no(), note.getProgram_no());
		} catch (Throwable e) {
			logger.warn("Warning", e);
		}

		session.setAttribute(sessionName, sessionFrm);
		CaseManagementEntryFormBean newform = new CaseManagementEntryFormBean();
		newform.setCaseNote(note);
		newform.setIssueCheckList(checkedlist);
		request.setAttribute("caseManagementEntryForm", newform);
		String varName = "newNote";
		session.setAttribute(varName, false);
		request.setAttribute("ajaxsave", note.getId());
		request.setAttribute("origNoteId", noteId);

		String logAction;
		if (newNote) {
			logAction = LogConst.ADD;
		} else {
			logAction = LogConst.UPDATE;
		}

		LogAction.addLog((String) session.getAttribute("user"), logAction, LogConst.CON_CME_NOTE, String.valueOf(note.getId()), request.getRemoteAddr(), demo, note.getAuditString());

		return mapping.findForward("issueList_ajax");
		/*
		 * CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		 *
		 * String oldId = cform.getCaseNote().getId() == null ? request.getParameter("newNoteIdx") : String.valueOf(cform.getCaseNote().getId()); long newId = noteSave(cform, request);
		 *
		 * if( newId > -1 ) { log.debug("OLD ID " + oldId + " NEW ID " + String.valueOf(newId)); cform.setMethod("view"); session.setAttribute("newNote", false); session.setAttribute("caseManagementEntryForm", cform);
		 * request.setAttribute("caseManagementEntryForm", cform); request.setAttribute("ajaxsave", newId); request.setAttribute("origNoteId", oldId); return mapping.findForward("issueList_ajax"); }
		 *
		 * return null;
		 */
	}

	// add new note link if note is document or rx note
	private void addNewNoteLink(Long noteId) {
		CaseManagementNote cmn = caseManagementMgr.getNote(noteId.toString());
		List<CaseManagementNote> cmnList = caseManagementMgr.getNotesByUUID(cmn.getUuid());
		Long firstNoteId;
		Long lastNoteId;
		List<Long> noteIdList = new ArrayList<Long>();
		for (CaseManagementNote note : cmnList) {
			noteIdList.add(note.getId());
		}
		if (noteIdList.size() > 0) {
			Collections.sort(noteIdList);
			firstNoteId = noteIdList.get(0);
			lastNoteId = noteIdList.get(noteIdList.size() - 1);
			if (firstNoteId != lastNoteId) {
				CaseManagementNote firstNote = caseManagementMgr.getNote(firstNoteId.toString());
				if (firstNote.isDocumentNote()) {
					Long tableId = EDocUtil.getTableIdFromNoteId(firstNote.getId());
					CaseManagementNoteLink cmnl = new CaseManagementNoteLink();
					cmnl.setNoteId(lastNoteId);
					cmnl.setTableName(CaseManagementNoteLink.DOCUMENT);
					cmnl.setTableId(tableId);
					caseManagementMgr.saveNoteLink(cmnl);
				} else if (firstNote.isRxAnnotation()) {

					CaseManagementNoteLink latestLink = caseManagementMgr.getLatestLinkByNote(firstNote.getId());

					CaseManagementNoteLink cmnl = new CaseManagementNoteLink();
					cmnl.setNoteId(lastNoteId);
					cmnl.setTableName(CaseManagementNoteLink.DRUGS);
					cmnl.setTableId(latestLink.getTableId());
					caseManagementMgr.saveNoteLink(cmnl);
					// EDocUtil.addCaseMgmtNoteLink(cmnl);
				}
			}
		}
	}

	public ActionForward saveAndExit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("saveandexit");

		HttpSession session = request.getSession();
		String providerNo = getProviderNo(request);
		String demoNo = getDemographicNo(request);
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		request.setAttribute("change_flag", "false");

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;

		ActionMessages messages = new ActionMessages();
		messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("note.saved"));
		saveMessages(request, messages);

		long noteId = noteSave(cform, request);
		if (noteId == -1) {
			return mapping.findForward("windowClose");
		}

		cform.setMethod("view");
		String error = (String) request.getAttribute("DateError");
		if (error != null) {

			String varName = "newNote";
			session.setAttribute(varName, false);
			varName = "saveNote" + demoNo;
			session.setAttribute(varName, new Boolean(true)); // tell CaseManagementView we have just saved note
			ActionForward fwd = mapping.findForward("list");
			StringBuilder path = new StringBuilder(fwd.getPath());

			if (path.indexOf("?") == -1) path.append("?");
			else path.append("&");

			path.append("DateError=" + error);

			ActionForward forward = new ActionForward();
			forward.setPath(path.toString());
			return forward;
		}

		String toBill = request.getParameter("toBill");
		if (toBill != null && toBill.equalsIgnoreCase("true")) {
			String region = cform.getBillRegion();
			String appointmentNo = cform.getAppointmentNo();
			String name = this.getDemoName(demoNo);
			String date = cform.getAppointmentDate();
			String start_time = cform.getStart_time();
			String apptProvider = cform.getApptProvider();
			String providerview = cform.getProviderview();
			String defaultView = oscar.OscarProperties.getInstance().getProperty("default_view", "");
			
			ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean("providerPreferenceDao");
			DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
			Demographic demographic = demographicDao.getDemographic(demoNo);
			//Should we get the billform based on the appointment provider or the demographic's provider?
			ProviderPreference providerPreference = providerPreferenceDao.find(demographic.getProviderNo());
			
			if (providerPreference != null && !providerPreference.getDefaultServiceType().equals("no")) {
				defaultView = providerPreference.getDefaultServiceType();
			}

			Set setIssues = cform.getCaseNote().getIssues();
			Iterator iter = setIssues.iterator();
			StringBuilder dxCodes = new StringBuilder();
			String strDxCode;
			int dxNum = 0;
			while (iter.hasNext()) {
				CaseManagementIssue cIssue = (CaseManagementIssue) iter.next();
				dxCodes.append("&dxCode");
				strDxCode = String.valueOf(cIssue.getIssue().getCode());
				if (strDxCode.length() > 3) {
					strDxCode = strDxCode.substring(0, 3);
				}

				if (dxNum > 0) {
					dxCodes.append(String.valueOf(dxNum));
				}

				dxCodes.append("=" + strDxCode);
				++dxNum;
			}

			String url = "/billing.do?billRegion=" + region + "&billForm=" + defaultView + "&hotclick=&appointment_no=" + appointmentNo + "&demographic_name=" + java.net.URLEncoder.encode(name, "utf-8") + "&amp;status=t&demographic_no=" + demoNo + "&providerview=" + providerview + "&user_no=" + providerNo + "&apptProvider_no=" + apptProvider + "&appointment_date=" + date + "&start_time=" + start_time + "&bNewForm=1" + dxCodes.toString();
			if(OscarProperties.getInstance().isPropertyActive("auto_populate_billingreferral_bc")
				&& demographic.getFamilyDoctor() != null){
				url += "&referral_no_1=" + getRefNo(demographic.getFamilyDoctor());
			}
			if(Boolean.parseBoolean(OscarProperties.getInstance().getProperty("clinicaid_billing", ""))){
				if(apptProvider!= null) {
					url = "/billing/billingClinicAid.jsp?demographic_no="+demoNo+
									"&service_start_date="+date+
									"&appointment_no="+appointmentNo+
									"&chart_no="+
									"&appointment_start_time="+start_time+
									"&appointment_provider_no="+apptProvider+
									"&billing_action=create_invoice";
				}
				else {
					url = "/billing/billingClinicAid.jsp?demographic_no="+demoNo+
							"&service_start_date="+date+
							"&appointment_no="+appointmentNo+
							"&chart_no="+
							"&appointment_start_time="+start_time+
							"&appointment_provider_no="+providerNo+
							"&billing_action=create_invoice";
				}
								
			}
			logger.debug("BILLING URL " + url);
			ActionForward forward = new ActionForward();
			forward.setPath(url);
			return forward;
		}

		String chain = request.getParameter("chain");

		SurveillanceMaster sMaster = SurveillanceMaster.getInstance();
		if (!SurveillanceMaster.surveysEmpty()) {
			request.setAttribute("demoNo", demoNo);
			if (chain != null && !chain.equals("")) {
				request.setAttribute("proceedURL", chain);
			}
			logger.debug("sending to surveillance");
			return mapping.findForward("surveillance");
		}

		if (chain != null && !chain.equals("")) {
			ActionForward fwd = new ActionForward();
			fwd.setPath(chain);
			return fwd;
		}

		return mapping.findForward("windowClose");
	}

	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String providerNo = getProviderNo(request);

		String programNo = (String) session.getAttribute("case_program_id");

		String demo = cform.getDemographicNo();

		try {
			logger.debug("CANCEL P:" + providerNo + " D:" + demo + " PROG:" + programNo);
			this.caseManagementMgr.deleteTmpSave(providerNo, demo, programNo);
		} catch (Throwable e) {
			logger.warn("Warning", e);
		}

		return mapping.findForward("windowClose");
	}

	public ActionForward exit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("change_flag", "false");
		return mapping.findForward("list");
	}

	public ActionForward addNewIssue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("addNewIssue");

		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		request.setAttribute("change_flag", "true");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String demono = getDemographicNo(request);
		String sessionFrmName = "caseManagementEntryForm" + demono;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);
		CaseManagementNote note = sessionFrm.getCaseNote();
		String noteTxt = cform.getCaseNote_note();
		noteTxt = org.apache.commons.lang.StringUtils.trimToNull(noteTxt);
		note.setNote(noteTxt);

		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		request.setAttribute("from", request.getParameter("from"));

		// noteSave(cform, request);
		cform.setShowList("false");
		cform.setSearString("");
		return mapping.findForward("IssueSearch");
	}

	public ActionForward issueList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		String programId = (String) session.getAttribute("case_program_id");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;

		String demono = request.getParameter("amp;demographicNo");
		if(demono == null)
			demono = request.getParameter("demographicNo");

		// get current providerNo
		// String providerNo = request.getParameter("amp;providerNo");
		String providerNo = LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo();

		// get the issue list have search string
		String search = request.getParameter("issueSearch");

		List searchResults;
		searchResults = caseManagementMgr.searchIssues(providerNo, programId, search);

		// Don't remove issues which we already have. But don't insert duplicate issues when save the issues.
		List existingIssues = new ArrayList<Issue>();
		List<Issue> filteredSearchResults;

		if (request.getParameter("amp;all") != null) {
			filteredSearchResults = new ArrayList<Issue>(searchResults);
		} else {
			filteredSearchResults = new ArrayList<Issue>();
			Map existingIssuesMap = convertIssueListToMap(existingIssues);
			for (Iterator iter = searchResults.iterator(); iter.hasNext();) {
				Issue issue = (Issue) iter.next();
				if (existingIssuesMap.get(issue.getId()) == null) {
					filteredSearchResults.add(issue);
				}
			}

		}

		CheckIssueBoxBean[] issueList = new CheckIssueBoxBean[filteredSearchResults.size()];
		for (int i = 0; i < filteredSearchResults.size(); i++) {
			issueList[i] = new CheckIssueBoxBean();
			issueList[i].setIssue(filteredSearchResults.get(i));

		}
		cform.setNewIssueCheckList(issueList);

		request.setAttribute("issueList", filteredSearchResults);

		return mapping.findForward("issueAutoCompletion");
	}

	public ActionForward issueSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("issueSearch");

		HttpSession session = request.getSession();
		String programId = (String) session.getAttribute("case_program_id");

		request.setAttribute("change_flag", "true");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;

		request.setAttribute("from", request.getParameter("from"));
		cform.setShowList("true");

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		// get current providerNo
		String providerNo = getProviderNo(request);

		// get the issue list have search string
		String search = cform.getSearString();

		List searchResults;
		searchResults = caseManagementMgr.searchIssues(providerNo, programId, search);

		List<Issue> filteredSearchResults = new ArrayList<Issue>();

		// remove issues which we already have - we don't want duplicates
		List existingIssues = caseManagementMgr.filterIssues(caseManagementMgr.getIssues(Integer.parseInt(demono)), programId);
		Map existingIssuesMap = convertIssueListToMap(existingIssues);
		for (Iterator iter = searchResults.iterator(); iter.hasNext();) {
			Issue issue = (Issue) iter.next();
			if (existingIssuesMap.get(issue.getId()) == null) {
				filteredSearchResults.add(issue);
			}
		}

		CheckIssueBoxBean[] issueList = new CheckIssueBoxBean[filteredSearchResults.size()];
		for (int i = 0; i < filteredSearchResults.size(); i++) {
			Issue issue = filteredSearchResults.get(i);
			issueList[i] = new CheckIssueBoxBean();
			issueList[i].setIssue(issue);
		}
		logger.debug("Community issue reconciliation complete");
		String sessionFrmName = "caseManagementEntryForm" + demono;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);
		sessionFrm.setNewIssueCheckList(issueList);
		sessionFrm.setShowList("true");

		if (request.getParameter("change_diagnosis") != null) request.setAttribute("change_diagnosis", request.getParameter("change_diagnosis"));
		if (request.getParameter("change_diagnosis_id") != null) request.setAttribute("change_diagnosis_id", request.getParameter("change_diagnosis_id"));

		return mapping.findForward("IssueSearch");
	}

	// we need to convert single issue into checkbox array so we can play nicely with CaseManagementEntryFormBean
	public ActionForward makeIssue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		// String programId = (String) session.getAttribute("case_program_id");
		// grab the issue we want to add
		String issueId = request.getParameter("newIssueId");
		// String providerNo = getProviderNo(request);

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String sessionFrmName = "caseManagementEntryForm" + this.getDemographicNo(request);
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		// check to see if this issue has already been associated with this demographic
		boolean issueExists = false;
		long lIssueId = Long.parseLong(issueId);
		CheckBoxBean[] existingCaseIssueList = sessionFrm.getIssueCheckList();
		for (int idx = 0; idx < existingCaseIssueList.length; ++idx) {
			if (existingCaseIssueList[idx].getIssue().getIssue_id() == lIssueId) {
				issueExists = true;
				break;
			}
		}

		// if issue hasn't been added, add it
		// if it has do nothing;-> change to if it's already added, still keep it but won't
		//insert into casemgmt_issue, just add a new record to casemgmt_issue_notes.
		//if (!issueExists) {
			CheckIssueBoxBean[] caseIssueList = new CheckIssueBoxBean[1];

			caseIssueList[0] = new CheckIssueBoxBean();
			Issue issue = caseManagementMgr.getIssue(issueId);
			caseIssueList[0].setIssue(issue);
			caseIssueList[0].setChecked(true);
			sessionFrm.setNewIssueCheckList(caseIssueList);

			return issueAdd(mapping, cform, request, response);
		//} else return null;
	}

	public ActionForward issueAdd(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		String changeDiagnosis = request.getParameter("change_diagnosis");
		if (changeDiagnosis != null && changeDiagnosis.equalsIgnoreCase("true")) {
			return submitChangeDiagnosis(mapping, form, request, response);
		}
		logger.debug("issueAdd");
		request.setAttribute("change_flag", "true");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		request.setAttribute("from", request.getParameter("from"));

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		String sessionFrmName = "caseManagementEntryForm" + demono;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		// add checked new issues to client's issue list
		// client's old issues
		CheckBoxBean[] oldList = sessionFrm.getIssueCheckList();
		// client's new issues
		CheckIssueBoxBean[] issueList = sessionFrm.getNewIssueCheckList();
		int k = 0;
		if (issueList != null) {
			for (int i = 0; i < issueList.length; i++) {

				// duplicated issues should be discarded.
				if (caseManagementIssueDao.getIssuebyId(demono, String.valueOf(issueList[i].getIssue().getId())) != null) {
					continue;
				}

				if (issueList[i].isChecked()) k++;
			}
		}

		CheckBoxBean[] caseIssueList = new CheckBoxBean[oldList.length + k];
		for (int i = 0; i < oldList.length; i++) {
			caseIssueList[i] = new CheckBoxBean();
			caseIssueList[i].setChecked(oldList[i].getChecked());
			caseIssueList[i].setUsed(oldList[i].isUsed());
			caseIssueList[i].setIssue(oldList[i].getIssue());
			caseIssueList[i].setIssueDisplay(oldList[i].getIssueDisplay());
		}
		k = 0;

		String programIdStr = (String) session.getAttribute(SessionConstants.CURRENT_PROGRAM_ID);
		if(programIdStr==null)
			programIdStr = (String) session.getAttribute("case_program_id");
		Integer programId = null;
		if (programIdStr != null) programId = Integer.valueOf(programIdStr);

		Properties dxProps = new Properties();
		try {
			InputStream is = getClass().getResourceAsStream("/caisi_issues_dx.properties");
			dxProps.load(is);
		} catch (IOException e) {
			logger.warn("Unable to load Dx properties file");
		}

		if (issueList != null) {
			CaseManagementViewAction caseManagementViewAction = new CaseManagementViewAction();

			for (int i = 0; i < issueList.length; i++) {
				if (issueList[i].isChecked()) {
					if (caseManagementIssueDao.getIssuebyId(demono, String.valueOf(issueList[i].getIssue().getId())) != null) {
						//continue;
						//issue already added
						for(int j=0; j<oldList.length; j++) {
							if(oldList[j].getIssue().getIssue_id() == issueList[i].getIssue().getId().longValue() ) { //find old issue and check it
								caseIssueList[j].setChecked("on");
								caseIssueList[j].getIssue().setAcute(false);
								caseIssueList[j].getIssue().setCertain(false);
								caseIssueList[j].getIssue().setMajor(false);
								caseIssueList[j].getIssue().setResolved(false);
								
								caseIssueList[j].getIssueDisplay().setAcute("chronic");
								caseIssueList[j].getIssueDisplay().setCertain("uncertain");
								caseIssueList[j].getIssueDisplay().setMajor("not major");
								caseIssueList[j].getIssueDisplay().setResolved("unresolved");
							}
						}
					} else {
					caseIssueList[oldList.length + k] = new CheckBoxBean();
					CaseManagementIssue cmi = newIssueToCIssue(sessionFrm, issueList[i].getIssue(), programId);
					caseIssueList[oldList.length + k].setIssue(cmi);
					caseIssueList[oldList.length + k].setChecked("on");
					IssueDisplay issueDisplay = caseManagementViewAction.getIssueDisplay(programId, cmi);
					caseIssueList[oldList.length + k].setIssueDisplay(issueDisplay);

					// should issue be automagically added to Dx? check config file
					if (dxProps != null && dxProps.get(issueList[i].getIssue().getCode()) != null) {
						String codingSystem = dxProps.getProperty("coding_system");
						if (caseIssueList[oldList.length + k].getIssue().isCertain()) {
							logger.info("adding to Dx");
							this.caseManagementMgr.saveToDx(getDemographicNo(request), issueList[i].getIssue().getCode(), codingSystem, false);
							caseIssueList[oldList.length + k].getIssue().setMajor(true);
						}
					}

					k++;
					}
				}
			}
		}
		cform.setIssueCheckList(caseIssueList);
		sessionFrm.setIssueCheckList(caseIssueList);

		String ajax = request.getParameter("ajax");
		if (ajax != null && ajax.equalsIgnoreCase("true")) {
			request.setAttribute("caseManagementEntryForm", sessionFrm);
			return mapping.findForward("issueList_ajax");
		} else return mapping.findForward("view");
	}

	public ActionForward changeDiagnosis(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("changeDiagnosis");
		if (request.getSession().getAttribute("userrole") == null) return mapping.findForward("expired");

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String inds = cform.getDeleteId();

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		request.setAttribute("from", request.getParameter("from"));
		request.setAttribute("change_diagnosis", new Boolean(true));
		request.setAttribute("change_diagnosis_id", inds);
		cform.setShowList("false");
		cform.setSearString("");
		return mapping.findForward("IssueSearch");
	}

	public ActionForward submitChangeDiagnosis(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("submitChangeDiagnosis");
		if (request.getSession().getAttribute("userrole") == null) return mapping.findForward("expired");

		request.setAttribute("change_flag", "true");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		request.setAttribute("from", request.getParameter("from"));

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		// get issue we're changing
		String strIndex = request.getParameter("change_diagnosis_id");
		int index = Integer.parseInt(strIndex);

		// change issue
		CheckBoxBean[] oldList = cform.getIssueCheckList();

		CheckIssueBoxBean[] issueList = cform.getNewIssueCheckList();
		CheckIssueBoxBean substitution = null;
		String origIssueDesc = null;
		String newIssueDesc = null;
		// find the checked issue
		for (CheckIssueBoxBean curr : issueList) {
			if (curr.isChecked()) {
				substitution = curr;
				break;
			}
		}

		if (substitution != null) {
			for (int x = 0; x < oldList.length; x++) {
				if (x == index) {

					Issue oldIssue = oldList[x].getIssue().getIssue();
					origIssueDesc = oldIssue.getDescription();

					Issue newIssue = caseManagementMgr.getIssue(String.valueOf(substitution.getIssue().getId().longValue()));
					newIssueDesc = newIssue.getDescription();

					oldList[x].getIssue().setIssue(newIssue);
					oldList[x].getIssue().setIssue_id(substitution.getIssue().getId().longValue());
					oldList[x].getIssue().setType(newIssue.getType());
					oldList[x].getIssueDisplay().setCode(newIssue.getCode());
					oldList[x].getIssueDisplay().setCodeType(newIssue.getType());
					oldList[x].getIssueDisplay().setDescription(newIssue.getDescription());

					caseManagementMgr.saveCaseIssue(oldList[x].getIssue());
				}
			}
		}

		cform.setIssueCheckList(oldList);
		if (substitution != null && origIssueDesc != null) this.caseManagementMgr.changeIssueInCPP(demono, origIssueDesc, newIssueDesc);
		// updateIssueToConcern(cform);

		return mapping.findForward("view");
	}

	public ActionForward ajaxChangeDiagnosis(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("ajaxChangeDiagnosis");

		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		// get issue we're changing
		String strIndex = request.getParameter("change_diagnosis_id");
		int idx = Integer.parseInt(strIndex);

		String substitution = request.getParameter("newIssueId");
		String sessionFrmName = "caseManagementEntryForm" + getDemographicNo(request);
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		CheckBoxBean[] curIssues = sessionFrm.getIssueCheckList();

		if (substitution != null) {

			Issue iss = caseManagementMgr.getIssue(substitution);
			curIssues[idx].getIssue().setIssue(iss);
			curIssues[idx].getIssue().setIssue_id(iss.getId());
			this.caseManagementMgr.saveCaseIssue(curIssues[idx].getIssue());

			// update form with new issue list
			Set<CaseManagementIssue> issueset = new HashSet<CaseManagementIssue>();
			for (int i = 0; i < curIssues.length; ++i) {
				if (curIssues[i].getChecked().equalsIgnoreCase("on")) issueset.add(curIssues[i].getIssue());
			}

			sessionFrm.getCaseNote().setIssues(issueset);
		}

		sessionFrm.setIssueCheckList(curIssues);
		request.setAttribute("caseManagementEntryForm", sessionFrm);
		// updateIssueToConcern(cform);

		// request.setAttribute("issueCheckList", curIssues);
		return mapping.findForward("issueList_ajax");
	}

	public ActionForward issueDelete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("issueDelete");

		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String demono = getDemographicNo(request);
		String sessionFrmName = "caseManagementEntryForm" + demono;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		// noteSave(cform, request);
		request.setAttribute("change_flag", "true");
		request.setAttribute("from", request.getParameter("from"));

		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		CheckBoxBean[] oldList = sessionFrm.getIssueCheckList();

		String inds = cform.getDeleteId();
		Integer ind = new Integer(inds);

		// delete the right issue
		CheckBoxBean[] caseIssueList = new CheckBoxBean[oldList.length - 1];
		int k = 0;
		CaseManagementIssue iss = null;

		if (ind.intValue() >= oldList.length) {
			logger.error("issueDelete index error");
			return mapping.findForward("view");
		}
		for (int i = 0; i < oldList.length; i++) {

			if (i != ind.intValue()) {
				caseIssueList[k] = new CheckBoxBean();
				caseIssueList[k].setChecked(oldList[i].getChecked());
				caseIssueList[k].setUsed(oldList[i].isUsed());
				caseIssueList[k].setIssue(oldList[i].getIssue());
				caseIssueList[k].setIssueDisplay(oldList[i].getIssueDisplay());
				k++;
			}
			if (i == ind.intValue()) {
				// delete from caseissue table
				iss = oldList[i].getIssue();
				caseManagementMgr.deleteIssueById(iss);
			}
		}
		cform.setIssueCheckList(caseIssueList);
		sessionFrm.setIssueCheckList(caseIssueList);

		if (OscarProperties.getInstance().isCaisiLoaded() && iss != null) {
			// reset current concern in CPP
			// updateIssueToConcern(cform);
			caseManagementMgr.removeIssueFromCPP(demono, iss);
		}

		// added by Eugene Petrhin in order to guarantee the DB consistency if user hits Cancel afterwards
		if (sessionFrm.getCaseNote().getId() != null) {
			noteSave(cform, request);
		}

		String ajax = request.getParameter("ajax");
		if (ajax != null && ajax.equalsIgnoreCase("true")) {
			request.setAttribute("caseManagementEntryForm", sessionFrm);
			return mapping.findForward("issueList_ajax");
		} else return mapping.findForward("view");
	}

	public ActionForward issueChange(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		logger.debug("issueChange");

		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		request.setAttribute("from", request.getParameter("from"));
		request.setAttribute("change_flag", "true");
		session.setAttribute("issueStatusChanged", "true");
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		String demono = getDemographicNo(request);
		String sessionFrmName = "caseManagementEntryForm" + demono;
		CaseManagementEntryFormBean sessionFrm = (CaseManagementEntryFormBean) session.getAttribute(sessionFrmName);

		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		// noteSave(cform, request);
		CheckBoxBean[] oldList = sessionFrm.getIssueCheckList();

		String inds = cform.getLineId();

		Integer ind = new Integer(inds);
		List<CaseManagementIssue> iss = new ArrayList<CaseManagementIssue>();
		oldList[ind.intValue()].getIssue().setUpdate_date(new Date());
		iss.add(oldList[ind.intValue()].getIssue());
		caseManagementMgr.saveAndUpdateCaseIssues(iss);
		
		//Change issueDisplay if this issue is successfully saved.
		sessionFrm.getIssueCheckList()[ind.intValue()].getIssueDisplay().setAcute(oldList[ind.intValue()].getIssue().isAcute()?"acute":"chronic");
		sessionFrm.getIssueCheckList()[ind.intValue()].getIssueDisplay().setCertain(oldList[ind.intValue()].getIssue().isCertain()?"certain":"uncertain");
		sessionFrm.getIssueCheckList()[ind.intValue()].getIssueDisplay().setMajor(oldList[ind.intValue()].getIssue().isMajor()?"major":"not major");
		sessionFrm.getIssueCheckList()[ind.intValue()].getIssueDisplay().setResolved(oldList[ind.intValue()].getIssue().isResolved()?"resolved":"unresolved");
		
		if (OscarProperties.getInstance().isCaisiLoaded()) {
			String providerNo = this.getProviderNo(request);

			// get access right
			List accessRight = caseManagementMgr.getAccessRight(providerNo, demono, (String) session.getAttribute("case_program_id"));

			// add medical history to CPP
			CaseManagementCPP cpp = this.caseManagementMgr.getCPP(getDemographicNo(request));
			setCPPMedicalHistory(cpp, providerNo, accessRight);
			cpp.setUpdate_date(new Date());
			caseManagementMgr.saveCPP(cpp, providerNo);
		}

		String ajax = request.getParameter("ajax");
		if (ajax != null && ajax.equalsIgnoreCase("true")) {			
			request.setAttribute("caseManagementEntryForm", sessionFrm);
			return mapping.findForward("issueList_ajax");
		} else return mapping.findForward("view");
	}

	public ActionForward notehistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		if (request.getSession().getAttribute("userrole") == null) return mapping.findForward("expired");

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));

		String noteid = request.getParameter("noteId");

		List<CaseManagementNote> history = caseManagementMgr.getHistory(noteid);
		request.setAttribute("history", history);
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		request.setAttribute("title", props.getString("oscarEncounter.noteHistory.title"));
		return mapping.findForward("showHistory");
	}

	public ActionForward issuehistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		if (request.getSession().getAttribute("userrole") == null) return mapping.findForward("expired");

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		String issueIds = request.getParameter("issueIds");

		List<CaseManagementNote> history = new ArrayList<CaseManagementNote>();
		List<CaseManagementNote> temp = caseManagementMgr.getIssueHistory(issueIds, demono);

		ArrayList<Boolean> current = new ArrayList<Boolean>(history.size());
		for (Iterator<CaseManagementNote> iter = temp.listIterator(); iter.hasNext();) {
			CaseManagementNote historyNote = iter.next();
			CaseManagementNote recentNote = caseManagementMgr.getMostRecentNote(historyNote.getUuid());

			if (!recentNote.isLocked()) {
				history.add(historyNote);
				if (recentNote.getUpdate_date().compareTo(historyNote.getUpdate_date()) > 0) {
					current.add(new Boolean(false));
				} else current.add(new Boolean(true));
			}
		}

		request.setAttribute("history", history);
		request.setAttribute("current", current);

		StringBuilder title = new StringBuilder();
		String arrIssues[] = issueIds.split(",");
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		if (arrIssues != null) {
			for (int idx = 0; idx < arrIssues.length; ++idx) {
				String tempArrIssue = StringUtils.trimToNull(arrIssues[idx]);
				if (tempArrIssue == null) continue;
				Issue i = this.caseManagementMgr.getIssue(tempArrIssue);
				title.append(i.getDescription());
				if (idx < arrIssues.length - 1) title.append(", ");
			}
		}
		title.append(" " + props.getString("oscarEncounter.history.title"));
		request.setAttribute("title", title.toString());

		return mapping.findForward("showHistory");
	}

	public ActionForward history(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("history");

		HttpSession session = request.getSession();
		if (session.getAttribute("userrole") == null) return mapping.findForward("expired");

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("demoDOB", getDemoDOB(demono));

		String noteid = request.getParameter("noteId");
		CaseManagementNote note = caseManagementMgr.getNote(noteid);

		request.setAttribute("history", note.getHistory());

		cform.setCaseNote_history(note.getHistory());

		LogAction.addLog((String) session.getAttribute("user"), LogConst.READ, LogConst.CON_CME_NOTE, noteid, request.getRemoteAddr(), note.getAuditString());

		return mapping.findForward("historyview");
	}

	public ActionForward autosave(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		logger.debug("autosave");

		String demographicNo = getDemographicNo(request);
		String programId = request.getParameter("programId");
		String providerNo = this.getProviderNo(request);
		String note = request.getParameter("note");
		String noteId = request.getParameter("note_id");

		if (note == null || note.length() == 0) {
			return null;
		}

		try {
			caseManagementMgr.tmpSave(providerNo, demographicNo, programId, noteId, note);
		} catch (Throwable e) {
			logger.warn("AutoSave Error: " + e);
		}

		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		cform.getCaseNote().setNote(note);

		response.setStatus(HttpServletResponse.SC_OK);
		return null;
	}

	public ActionForward restore(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.getSession().setAttribute("restoring", "true"); // tell CaseManagementView we're handling temp note
		request.setAttribute("restore", new Boolean(true));

		return edit(mapping, form, request, response);
	}

	public ActionForward cleanup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String demoNo = this.getDemographicNo(request);
		String sessionFrmName = "caseManagementEntryForm" + demoNo;
		String strBeanName = "casemgmt_oscar_bean" + demoNo;

		request.getSession().setAttribute(sessionFrmName, null);
		request.getSession().setAttribute(strBeanName, null);

		return null;
	}

	public String[] getIssueIds(List<Issue> issues) {
		String[] issueIds = new String[issues.size()];
		int idx = 0;
		for (Issue i : issues) {
			issueIds[idx] = String.valueOf(i.getId());
			++idx;
		}
		return issueIds;
	}

	public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setContentType("application/pdf"); // octet-stream
		response.setHeader("Content-Disposition", "attachment; filename=\"Encounter-" + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf\"");
		doPrint(request, response.getOutputStream());
		return null;
	}

	public void doPrint(HttpServletRequest request, OutputStream os) throws IOException, DocumentException {
		String ids = request.getParameter("notes2print");

		String demono = getDemographicNo(request);
		request.setAttribute("demoName", getDemoName(demono));
		request.setAttribute("demoSex", getDemoSex(demono));
		request.setAttribute("demoAge", getDemoAge(demono));
		request.setAttribute("mrp", getMRP(request));
		String dob = getDemoDOB(demono);
		dob = convertDateFmt(dob, request);
		request.setAttribute("demoDOB", dob);
		String providerNo = getProviderNo(request);

		String[] noteIds;
		if (ids.length() > 0) noteIds = ids.split(",");
		else noteIds = (String[]) Array.newInstance(String.class, 0);

		List<CaseManagementNote> notes = new ArrayList<CaseManagementNote>();
		List<String> remoteNoteUUIDs = new ArrayList<String>();
		String uuid;
		for (int idx = 0; idx < noteIds.length; ++idx) {
			if (noteIds[idx].startsWith("UUID")) {
				uuid = noteIds[idx].substring(4);
				remoteNoteUUIDs.add(uuid);
			} else {
				notes.add(this.caseManagementMgr.getNote(noteIds[idx]));
			}
		}

		LoggedInInfo loggedInInfo = LoggedInInfo.loggedInInfo.get();
		if (loggedInInfo.currentFacility.isIntegratorEnabled() && remoteNoteUUIDs.size() > 0) {
			DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs();
			List<CachedDemographicNote> remoteNotes = demographicWs.getLinkedCachedDemographicNotes(Integer.parseInt(demono));
			for (CachedDemographicNote remoteNote : remoteNotes) {
				for (String remoteUUID : remoteNoteUUIDs) {
					if (remoteUUID.equals(remoteNote.getCachedDemographicNoteCompositePk().getUuid())) {
						CaseManagementNote fakeNote = getFakedNote(remoteNote);
						notes.add(fakeNote);
						break;
					}
				}
			}
		}

		// we're not guaranteed any ordering of notes given to us, so sort by observation date
		oscar.OscarProperties p = oscar.OscarProperties.getInstance();
		String noteSort = p.getProperty("CMESort", "");
		if (noteSort.trim().equalsIgnoreCase("UP")) {
			Collections.sort(notes, CaseManagementNote.noteObservationDateComparator);
			Collections.reverse(notes);
		} else Collections.sort(notes, CaseManagementNote.noteObservationDateComparator);

		List<CaseManagementNote> issueNotes;
		List<CaseManagementNote> tmpNotes;
		HashMap<String, List<CaseManagementNote>> cpp = null;
		if (request.getParameter("printCPP").equalsIgnoreCase("true")) {
			cpp = new HashMap<String, List<CaseManagementNote>>();
			String[] issueCodes = { "OMeds", "SocHistory", "MedHistory", "Concerns", "Reminders", "FamHistory", "RiskFactors" };
			for (int j = 0; j < issueCodes.length; ++j) {
				List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, issueCodes[j]);
				String[] issueIds = getIssueIds(issues);// = new String[issues.size()];
				tmpNotes = caseManagementMgr.getNotes(demono, issueIds);
				issueNotes = new ArrayList<CaseManagementNote>();
				for (int k = 0; k < tmpNotes.size(); ++k) {
					if (!tmpNotes.get(k).isLocked()) {
						List<CaseManagementNoteExt> exts = caseManagementMgr.getExtByNote(tmpNotes.get(k).getId());
						boolean exclude = false;
						for (CaseManagementNoteExt ext : exts) {
							if (ext.getKeyVal().equals("Hide Cpp")) {
								if (ext.getValue().equals("1")) {
									exclude = true;
								}
							}
						}
						if (!exclude) {
							issueNotes.add(tmpNotes.get(k));
						}
					}
				}
				cpp.put(issueCodes[j], issueNotes);
			}
		}
		String demoNo = null;
		List<CaseManagementNote> othermeds = null;
		if (request.getParameter("printRx").equalsIgnoreCase("true")) {
			demoNo = demono;
			if (cpp == null) {
				List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, "OMeds");
				String[] issueIds = getIssueIds(issues);// new String[issues.size()];
				othermeds = caseManagementMgr.getNotes(demono, issueIds);
			} else {
				othermeds = cpp.get("OMeds");
			}
		}

		// Create new file to save form to
		String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		String fileName = path + "EncounterForm-" + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf";
		FileOutputStream out = new FileOutputStream(fileName);

		CaseManagementPrintPdf printer = new CaseManagementPrintPdf(request, out);
		printer.printDocHeaderFooter();
		printer.printCPP(cpp);
		printer.printRx(demoNo, othermeds);
		printer.printNotes(notes);

		/* check extensions */
		Enumeration<String> e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = e.nextElement();
			if (name.startsWith("extPrint")) {
				if (request.getParameter(name).equals("true")) {
					ExtPrint printBean = (ExtPrint) SpringUtils.getBean(name);
					if (printBean != null) {
						printBean.printExt(printer, request);
					}
				}
			}
		}
		printer.finish();

		List<Object> pdfDocs = new ArrayList<Object>();
		pdfDocs.add(fileName);

		if (request.getParameter("printLabs") != null && request.getParameter("printLabs").equalsIgnoreCase("true")) {
			// get the labs which fall into the date range which are attached to this patient
			CommonLabResultData comLab = new CommonLabResultData();
			ArrayList<LabResultData> labs = comLab.populateLabResultsData("", demono, "", "", "", "U", null);
			LinkedHashMap<String, LabResultData> accessionMap = new LinkedHashMap<String, LabResultData>();
			for (int i = 0; i < labs.size(); i++) {
				LabResultData result = labs.get(i);
				if (result.isHL7TEXT()) {
					if (result.accessionNumber == null || result.accessionNumber.equals("")) {
						accessionMap.put("noAccessionNum" + i + result.labType, result);
					} else {
						if (!accessionMap.containsKey(result.accessionNumber + result.labType)) accessionMap.put(result.accessionNumber + result.labType, result);
					}
				}
			}
			for (LabResultData result : accessionMap.values()) {
				Date d = result.getDateObj();
				// TODO:filter out the ones which aren't in our date range if there's a date range????
				String segmentId = result.segmentID;
				MessageHandler handler = Factory.getHandler(segmentId);
				String fileName2 = OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "//" + handler.getPatientName().replaceAll("\\s", "_") + "_" + handler.getMsgDate() + "_LabReport.pdf";
				OutputStream os2 = new FileOutputStream(fileName2);
				LabPDFCreator pdfCreator = new LabPDFCreator(os2, segmentId, LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo());
				pdfCreator.printPdf();
				pdfDocs.add(fileName2);
			}

		}
		ConcatPDF.concat(pdfDocs, os);
	}

	private CaseManagementNote getFakedNote(CachedDemographicNote remoteNote) {
		CaseManagementNote note = new CaseManagementNote();

		if (remoteNote.getObservationDate() != null) note.setObservation_date(remoteNote.getObservationDate().getTime());
		note.setNote(remoteNote.getNote());

		return (note);
	}

	public ActionForward runMacro(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
		MacroDao macroDao = (MacroDao) SpringUtils.getBean("macroDao");
		Macro macro = macroDao.find(Integer.parseInt(request.getParameter("macro.id")));
		logger.info("loaded macro " + macro.getLabel());
		boolean cppFromMeasurements = false;
		String cpp = request.getParameter("cpp");
		if (cpp != null && cpp.equals("measurements")) {
			cppFromMeasurements = true;
		}

		cform.setCaseNote_note(cform.getCaseNote_note() + "\n" + macro.getImpression());

		ActionForward fwd = saveAndExit(mapping, form, request, response);

		if (fwd.getName().equals("windowClose")) {
			EyeFormDao eyeformDao = (EyeFormDao) SpringUtils.getBean("EyeFormDao");
			EyeForm eyeform = eyeformDao.getByAppointmentNo(Integer.parseInt(cform.getAppointmentNo()));
			// load up the eyeform to set/unset checkboxes
			if (macro.getDischargeFlag() != null && macro.getDischargeFlag().equals("dischargeFlag")) {
				eyeform.setDischarge("true");
			}
			if (macro.getOptFlag() != null && macro.getOptFlag().equals("optFlag")) {
				eyeform.setOpt("true");
			}
			if (macro.getStatFlag() != null && macro.getStatFlag().equals("statFlag")) {
				eyeform.setStat("true");
			}
			eyeformDao.merge(eyeform);

			// follow ups
			FollowUpDao followUpDao = (FollowUpDao) SpringUtils.getBean("FollowUpDAO");
			int followUpNo = macro.getFollowupNo();
			String followUpUnit = macro.getFollowupUnit();
			String followUpDr = macro.getFollowupDoctorId();
			if (followUpNo > 0) {
				EyeformFollowUp f = new EyeformFollowUp();
				f.setAppointmentNo(Integer.parseInt(cform.getAppointmentNo()));
				f.setDate(new Date());
				f.setDemographicNo(Integer.parseInt(cform.getDemographicNo()));
				f.setProvider(LoggedInInfo.loggedInInfo.get().loggedInProvider);
				f.setTimeframe(followUpUnit);
				f.setTimespan(followUpNo);
				f.setType("followup");
				f.setUrgency("routine");
				f.setFollowupProvider(followUpDr);
				followUpDao.persist(f);
			}

			// tests
			TestBookRecordDao testDao = (TestBookRecordDao) SpringUtils.getBean("TestBookDAO");
			String[] tests = macro.getTestRecords().split("\n");
			for (String test : tests) {
				String[] parts = test.trim().split("\\|");
				if (parts.length == 3 || parts.length == 4) {
					EyeformTestBook rec = new EyeformTestBook();
					rec.setAppointmentNo(Integer.parseInt(cform.getAppointmentNo()));
					if (parts.length == 4) rec.setComment(parts[3]);
					else rec.setComment("");
					rec.setDate(new Date());
					rec.setDemographicNo(Integer.parseInt(cform.getDemographicNo()));
					rec.setEye(parts[1]);
					rec.setProvider(LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo());
					// rec.setStatus(null);
					rec.setTestname(parts[0]);
					rec.setUrgency(parts[2]);
					testDao.save(rec);
				}
			}

			// send tickler
			if (macro.getTicklerRecipient() != null && macro.getTicklerRecipient().length() > 0) {
				TicklerDAO ticklerDao = (TicklerDAO) SpringUtils.getBean("ticklerDAOT");
				Tickler t = new Tickler();
				t.setCreator(LoggedInInfo.loggedInInfo.get().loggedInProvider.getPractitionerNo());
				t.setDemographic_no(cform.getDemographicNo());
				t.setMessage(getMacroTicklerText(Integer.parseInt(cform.getAppointmentNo())));
				t.setPriority("Normal");
				t.setService_date(new Date());
				t.setStatus('A');
				t.setTask_assigned_to(macro.getTicklerRecipient());
				t.setUpdate_date(new Date());
				ticklerDao.saveTickler(t);
			}

			// billing
			if (macro.getBillingCodes() != null && macro.getBillingCodes().length() > 0) {
				GstControlDao gstControlDao = (GstControlDao) SpringUtils.getBean("gstControlDao");
				BillingServiceDao billingServiceDao = (BillingServiceDao) SpringUtils.getBean("billingServiceDao");
				DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
				Provider provider = LoggedInInfo.loggedInInfo.get().loggedInProvider;
				OscarAppointmentDao apptDao = (OscarAppointmentDao) SpringUtils.getBean("oscarAppointmentDao");

				Appointment appt = null;
				if (cform.getAppointmentNo() != null && cform.getAppointmentNo().length() > 0 && !cform.getAppointmentDate().equals("0")) {
					appt = apptDao.find(Integer.parseInt(cform.getAppointmentNo()));
				}

				SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
				String serviceDate = sf.format(new Date());

				// create a mock httprequest to fill in the preset values
				MockHttpServletRequest mockReq = new MockHttpServletRequest();
				mockReq.addParameter("dxCode", macro.getBillingDxcode());
				String[] bcodes = macro.getBillingCodes().replace("\r", "").split("\n");

				BigDecimal btotal = new BigDecimal(0);
				// must use 100.0 otherwise result will be an int
				BigDecimal gstFactor = new BigDecimal(1 + gstControlDao.find(1).getGstPercent().intValue() / 100.0);
				ArrayList<String[]> percentUnits = new ArrayList<String[]>();
				for (int i = 0; i < bcodes.length; i++) {
					if (StringUtils.isBlank(bcodes[i])) continue;
					String[] codes = bcodes[i].split("\\|");
					mockReq.addParameter("xserviceCode_" + i, codes[0]);
					Object[] priceg = billingServiceDao.getUnitPrice(codes[0], serviceDate);
					mockReq.addParameter("xserviceUnit_" + i, codes[1]);
					String sliCode = OscarProperties.getInstance().getProperty("clinic_no");
					if(codes.length==3 && !codes[2].equals("NA")) {
						sliCode=codes[2];
					}
					mockReq.addParameter("xsliCode_"+i, sliCode);
					if (".00".equals(priceg[0])) {
						percentUnits.add(codes);
						mockReq.addParameter("percCodeSubtotal_" + i, (String) priceg[0]);
						// skip to next as we deal with percentage later
						continue;
					}

					// price is unit_price*unit*at_percent, but in macro we only assume at_percent=1
					// as it's not possible to enter percent value for macros (1-click action).
					BigDecimal price = new BigDecimal((String) priceg[0]).multiply(new BigDecimal(codes[1]));
					if ((Boolean) priceg[1] == true) {
						// add GST
						price = price.multiply(gstFactor);
					}
					mockReq.addParameter("percCodeSubtotal_" + i, price.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
					btotal = btotal.add(price).setScale(4, BigDecimal.ROUND_HALF_UP);

				}
				// now process percent codes
				BigDecimal stotal = new BigDecimal(0);
				for (String[] code : percentUnits) {
					String pct = billingServiceDao.getUnitPercentage(code[0], serviceDate);
					stotal = stotal.add(btotal.multiply(new BigDecimal(pct)));
				}
				btotal = btotal.add(stotal);
				mockReq.addParameter("totalItem", "" + bcodes.length);
				mockReq.addParameter("total", btotal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());

				Demographic demo = demographicDao.getClientByDemographicNo(Integer.parseInt(cform.getDemographicNo()));
				mockReq.setParameter("xml_billtype", macro.getBillingBilltype());
				if(macro.getSliCode() == null || macro.getSliCode().equals("NA")) {
					String value = OscarProperties.getInstance().getProperty("clinic_no","");
					mockReq.setParameter("xml_slicode", value);
				} else {
					mockReq.setParameter("xml_slicode", macro.getSliCode());
				}
				// mockReq.addParameter("xml_billtype", "ODP | Bill OHIP");
				mockReq.addParameter("hin", demo.getHin());
				mockReq.addParameter("ver", demo.getVer());
				mockReq.addParameter("demographic_dob", demo.getDateOfBirth());
				mockReq.addParameter("appointment_no", cform.getAppointmentNo());
				mockReq.addParameter("demographic_name", demo.getLastName() + "," + demo.getFirstName());
				mockReq.addParameter("sex", "F".equalsIgnoreCase(demo.getSex()) ? "2" : "1");
				mockReq.addParameter("hc_type", demo.getHcType());
				String referalNo = getRefNo(demo.getFamilyDoctor());
				mockReq.addParameter("referralCode", referalNo);
				mockReq.addParameter("xml_location", macro.getBillingVisitLocation()); // visit location
				mockReq.addParameter("m_review", "N"); // manual review, always No
				// as it's automated
				mockReq.addParameter("clinic_no", oscar.OscarProperties.getInstance().getProperty("clinic_no", "").trim());
				// clinic_location
				mockReq.addParameter("demographic_no", cform.getDemographicNo());
				mockReq.addParameter("service_date", serviceDate);
				SimpleDateFormat tf = new SimpleDateFormat("HH:mm");
				mockReq.addParameter("start_time", tf.format(new Date()));
				mockReq.addParameter("submit", "Save");
				mockReq.addParameter("comment", macro.getBillingComment());
				mockReq.addParameter("xml_visittype", macro.getBillingVisitType());
				mockReq.addParameter("xml_vdate", cform.getAppointmentDate());
				mockReq.addParameter("apptProvider_no", appt == null ? "" : appt.getProviderNo());
				mockReq.addParameter("xml_provider", provider.getProviderNo() + "|" + provider.getOhipNo());
				mockReq.getSession().setAttribute("user", LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo());

				BillingSavePrep bObj = new BillingSavePrep();
				boolean ret = bObj.addABillingRecord(bObj.getBillingClaimObj(mockReq));
				/*
				 * not applicable in macro context if (mockReq.getParameter("xml_billtype").substring(0, 3).matches( BillingDataHlp.BILLINGMATCHSTRING_3RDPARTY)) { mockReq.addParameter("billto", macro.getBillingBillto()); mockReq.addParameter("remitto",
				 * macro.getBillingRemitto()); mockReq.addParameter("gstBilledTotal", macro .getBillingGstBilledTotal()); mockReq.addParameter("payment", macro.getBillingPayment()); mockReq.addParameter("refund", macro.getBillingRefund());
				 * mockReq.addParameter("gst", macro.getBillingGst()); mockReq.addParameter("payMethod", macro.getBillingPayMethod());
				 *
				 * bObj.addPrivateBillExtRecord(mockReq); }
				 */
				// int billingNo = bObj.getBillingId();

				// update appt and close the page
				if (ret) {
					if (!cform.getAppointmentNo().equals("0")) {
						String apptCurStatus = bObj.getApptStatus(cform.getAppointmentNo());
						oscar.appt.ApptStatusData as = new oscar.appt.ApptStatusData();
						String billStatus = as.billStatus(apptCurStatus);
						bObj.updateApptStatus(cform.getAppointmentNo(), billStatus, cform.getProviderNo());
					}
				} else log.error("++++++++++++++ Failed to add billing codes");
			}
		}

		return fwd;
	}

	public String getRefNo(String referal) {
		if (referal == null) return "";
		int start = referal.indexOf("<rdohip>");
		int end = referal.indexOf("</rdohip>");
		String ref = new String();

		if (start >= 0 && end >= 0) {
			String subreferal = referal.substring(start + 8, end);
			if (!"".equalsIgnoreCase(subreferal.trim())) {
				ref = subreferal;

			}
		}
		return ref;
	}

	public String getMacroTicklerText(int appointmentNo) {
		StringBuilder sb = new StringBuilder();
		sb.append(FollowUpAction.getTicklerText(appointmentNo));
		sb.append(ProcedureBookAction.getTicklerText(appointmentNo));
		sb.append(TestBookAction.getTicklerText(appointmentNo));
		return sb.toString();
	}

	/**
	 * gets all the notes
	 * if we have a key, and the note is locked, consider it
	 * caisi - filter notes
	 * grab the last one, where i am provider, and it's not signed
	 *
	 * @param request
	 * @param demono
	 * @param providerNo
	 * @return
	 */
	public CaseManagementNote getLastSaved(HttpServletRequest request, String demono, String providerNo) {
		HttpSession session = request.getSession();
		//CaseManagementNote note = null;
		List<EChartNoteEntry> entries = new ArrayList<EChartNoteEntry>();

		//Gets some of the note data, no relationships, not the note/history..just enough
		List<Map<String,Object>> notes = this.caseManagementNoteDao.getUnsignedRawNoteInfoMapByDemographic(demono);
		Map<String,Object> filteredNotes = new LinkedHashMap<String,Object>();

		//This gets rid of old revisions (better than left join on a computed subset of itself
		for(Map<String,Object> note:notes) {
			if(filteredNotes.get(note.get("uuid"))!=null)
				continue;
			filteredNotes.put((String)note.get("uuid"),true);
			EChartNoteEntry e = new EChartNoteEntry();
			e.setId(note.get("id"));
			e.setDate((Date)note.get("observation_date"));
			e.setProviderNo((String)note.get("providerNo"));
			e.setProgramId(Integer.parseInt((String)note.get("program_no")));
			e.setRole((String)note.get("reporter_caisi_role"));
			e.setType("local_note");
			entries.add(e);

		}

		// UserProperty prop = caseManagementMgr.getUserProperty(providerNo, UserProperty.STALE_NOTEDATE);
		//notes = caseManagementMgr.getNotes(demono);
		//notes = manageLockedNotes(notes, false, this.getUnlockedNotesMap(request));

		String programId = (String) session.getAttribute("case_program_id");

		if (programId == null || programId.length() == 0) {
			programId = "0";
		}

		entries = caseManagementMgr.filterNotes1(entries, programId);

		Collections.sort(entries,EChartNoteEntry.getDateComparatorDesc());

		Map unlockedNotesMap = this.getUnlockedNotesMap(request);
		for(EChartNoteEntry entry:entries) {
			CaseManagementNote n = caseManagementMgr.getNote(String.valueOf(entry.getId()));
			if(n.isLocked() && unlockedNotesMap.get(entry.getId()) != null ) {
				n.setLocked(false);
			}
			if(n.getProviderNo().equals(providerNo)) {
				session.setAttribute("newNote", "false");
				return n;
			}
		}

		return null;
	}

	protected Map getUnlockedNotesMap(HttpServletRequest request) {
		Map<Long, Boolean> map = (Map<Long, Boolean>) request.getSession().getAttribute("unlockedNoteMap");
		if (map == null) {
			map = new HashMap<Long, Boolean>();
		}
		return map;
	}

	private List manageLockedNotes(List notes, boolean removeLockedNotes, Map unlockedNotesMap) {
		List<CaseManagementNote> notesNoLocked = new ArrayList<CaseManagementNote>();
		for (Iterator iter = notes.iterator(); iter.hasNext();) {
			CaseManagementNote note = (CaseManagementNote) iter.next();
			if (note.isLocked()) {
				if (unlockedNotesMap.get(note.getId()) != null) {
					note.setLocked(false);
				}
			}
			if (removeLockedNotes && !note.isLocked()) {
				notesNoLocked.add(note);
			}
		}
		if (removeLockedNotes) {
			return notesNoLocked;
		}
		return notes;
	}

	/*
	 * Grab family physician or MRP for demo
	 */
	protected String getMRP(HttpServletRequest request) {
		String strBeanName = "casemgmt_oscar_bean" + this.getDemographicNo(request);
		oscar.oscarEncounter.pageUtil.EctSessionBean bean = (oscar.oscarEncounter.pageUtil.EctSessionBean) request.getSession().getAttribute(strBeanName);
		if (bean == null) return new String("");

		if (bean.familyDoctorNo.equals("")) return new String("");

		oscar.oscarEncounter.data.EctProviderData.Provider prov = new oscar.oscarEncounter.data.EctProviderData().getProvider(bean.familyDoctorNo);
		String name = prov.getFirstName() + " " + prov.getSurname();
		return name;
	}

	/*
	 * Insert encounter reason for new note
	 */
	protected void insertReason(HttpServletRequest request, CaseManagementNote note) {

		String encounterText = "";
		String apptDate = request.getParameter("appointmentDate");
		String reason = request.getParameter("reason");

		if (reason == null) {
			reason = "";
		}

		if (apptDate == null || apptDate.equals("") || apptDate.equalsIgnoreCase("null")) {
			encounterText = "\n[" + oscar.util.UtilDateUtilities.DateToString(oscar.util.UtilDateUtilities.Today(), "dd-MMM-yyyy", request.getLocale()) + " .: " + reason + "] \n";
		} else {
			apptDate = convertDateFmt(apptDate, request);
			encounterText = "\n[" + apptDate + " .: " + reason + "]\n";
		}

		note.setNote(encounterText);
		String encType = request.getParameter("encType");

		if (encType == null || encType.equals("")) {
			note.setEncounter_type("");
		} else {
			note.setEncounter_type(encType);
		}

	}

	protected String convertDateFmt(String strOldDate, HttpServletRequest request) {
		String strNewDate = new String();
		if (strOldDate != null && strOldDate.length() > 0) {
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", request.getLocale());
			try {

				Date tempDate = fmt.parse(strOldDate);
				strNewDate = new SimpleDateFormat("dd-MMM-yyyy", request.getLocale()).format(tempDate);

			} catch (ParseException ex) {
				MiscUtils.getLogger().error("Error", ex);
			}
		}

		return strNewDate;
	}

	protected CaseManagementCPP copyNote2cpp(CaseManagementCPP cpp, CaseManagementNote note) {
		Set<CaseManagementIssue> issueSet = note.getIssues();
		StringBuilder text = new StringBuilder();
		Date d = new Date();
		String separator = "\n-----[[" + d + "]]-----\n";
		for (CaseManagementIssue issue : issueSet) {
			String code = issue.getIssue().getCode();
			if (code.equals("OMeds")) {
				text.append(cpp.getFamilyHistory());
				text.append(separator);
				text.append(note.getNote());
				cpp.setFamilyHistory(text.toString());
				break;
			} else if (code.equals("SocHistory")) {
				text.append(cpp.getSocialHistory());
				text.append(separator);
				text.append(note.getNote());
				cpp.setSocialHistory(text.toString());
				break;
			} else if (code.equals("MedHistory")) {
				text.append(cpp.getMedicalHistory());
				text.append(separator);
				text.append(note.getNote());
				cpp.setMedicalHistory(text.toString());
				break;
			} else if (code.equals("Concerns")) {
				text.append(cpp.getOngoingConcerns());
				text.append(separator);
				text.append(note.getNote());
				cpp.setOngoingConcerns(text.toString());
				break;
			} else if (code.equals("Reminders")) {
				text.append(cpp.getReminders());
				text.append(separator);
				text.append(note.getNote());
				cpp.setReminders(text.toString());
				break;
			} else if (code.equals("FamHistory")) {
				text.append(cpp.getFamilyHistory());
				text.append(separator);
				text.append(note.getNote());
				cpp.setFamilyHistory(text.toString());
				break;
			} else if (code.equals("RiskFactors")) {
				text.append(cpp.getRiskFactors());
				text.append(separator);
				text.append(note.getNote());
				cpp.setRiskFactors(text.toString());
				break;
			}
		}

		return cpp;
	}

	/*
	 * Retrieve CPP issuesIf not in session, load them
	 */
	protected HashMap getCPPIssues(HttpServletRequest request, String providerNo) {
		HttpSession session = request.getSession();
		HashMap<String, Issue> issues = (HashMap<String, Issue>) session.getAttribute("CPPIssues");
		if (issues == null) {
			String[] issueCodes = { "SocHistory", "MedHistory", "Concerns", "Reminders", "FamHistory" };
			issues = new HashMap<String, Issue>();
			for (String issue : issueCodes) {
				List<Issue> i = caseManagementMgr.getIssueInfoByCode(providerNo, issue);
				issues.put(issue, i.get(0));
			}

			session.setAttribute("CPPIssues", issues);
		}
		return issues;
	}

	boolean filled(String s) {
		return (s != null && s.length() > 0);
	}

	public boolean haveIssue(Long issid, List allNotes) {

		Iterator itr = allNotes.iterator();
		while (itr.hasNext()) {
			CaseManagementNote note = (CaseManagementNote) itr.next();
			Set issues = note.getIssues();
			Iterator its = issues.iterator();
			while (its.hasNext()) {
				CaseManagementIssue iss = (CaseManagementIssue) its.next();
				if (iss.getId().intValue() == issid.intValue()) return true;
			}
		}
		return false;
	}

	private String partialDateFormat(String dateValue) {
		if (!filled(dateValue)) return null;

		dateValue = dateValue.trim();
		dateValue = dateValue.replace("/", "-");
		if (dateValue.length() == 4 && NumberUtils.isDigits(dateValue)) return PartialDate.YEARONLY;

		String[] dateParts = dateValue.split("-");
		if (dateParts.length == 2 && NumberUtils.isDigits(dateParts[0]) && NumberUtils.isDigits(dateParts[1])) {
			if (dateParts[0].length() == 4 && dateParts[1].length() >= 1 && dateParts[1].length() <= 2) return PartialDate.YEARMONTH;
		}
		if (dateParts.length == 3 && NumberUtils.isDigits(dateParts[0]) && NumberUtils.isDigits(dateParts[1]) && NumberUtils.isDigits(dateParts[2])) {
			if (dateParts[0].length() == 4 && dateParts[1].length() >= 1 && dateParts[1].length() <= 2 && dateParts[2].length() >= 1 && dateParts[2].length() <= 2) return ""; // full date
		}
		return null;
	}

	private String partialFullDate(String dateValue, String type) {
		if (type == null) return null;

		dateValue = dateValue.replace("/", "-");
		if (type.equals(PartialDate.YEARONLY)) return dateValue + "-01-01";
		if (type.equals(PartialDate.YEARMONTH)) return dateValue + "-01";
		return dateValue;
	}

	private boolean writePartialDate(String dateValue, CaseManagementNoteExt cme) {
		if (cme == null) return false;

		String type = partialDateFormat(dateValue);
		if (type == null) return false;

		cme.setValue(type);
		cme.setDateValue(partialFullDate(dateValue, type));
		return true;
	}

	private boolean nullEmptyEqual(String s1, String s2) {
		if (s1 == null) s1 = "";
		if (s2 == null) s2 = "";
		return s1.trim().equals(s2.trim());
	}
}
