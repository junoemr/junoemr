package org.oscarehr.casemgmt.service;

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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.util.ExtPrint;
import org.oscarehr.casemgmt.web.NoteDisplay;
import org.oscarehr.casemgmt.web.NoteDisplayLocal;
import org.oscarehr.consultations.service.ConsultationPDFCreationService;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import oscar.OscarProperties;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;
import oscar.util.ConversionUtils;

import com.lowagie.text.DocumentException;
import java.io.File;

public class CaseManagementPrint {

	private static Logger logger = MiscUtils.getLogger();
	
	private CaseManagementManager caseManagementMgr = SpringUtils.getBean(CaseManagementManager.class);

	private ConsultationPDFCreationService consultationPDFCreationService = SpringUtils.getBean(ConsultationPDFCreationService.class);
	private NoteService noteService = SpringUtils.getBean(NoteService.class);
	
	private ProgramManager2 programManager2 = SpringUtils.getBean(ProgramManager2.class);
	
	private ProgramManager programMgr = SpringUtils.getBean(ProgramManager.class);


	/*
	 *This method was in CaseManagementEntryAction but has been moved out so that both the classic Echart and the flat echart can use the same printing method.
	 * 
	 */
	public void doPrint(LoggedInInfo loggedInInfo,
						Integer demographicNo,
						boolean printAllNotes,
						String[] noteIds,
						boolean printCPP,
						boolean printRx,
						boolean printLabs,
						Calendar startDate,
						Calendar endDate,
						HttpServletRequest request,
						OutputStream os)
			throws IOException, DocumentException
	{
		
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		String demoNo = "" + demographicNo;

		if (printAllNotes)
		{
			noteIds = getAllNoteIds(loggedInInfo, request, demoNo);
		}

		request.setAttribute("demoName", getDemoName(demoNo));
		request.setAttribute("demoSex", getDemoSex(demoNo));
		request.setAttribute("demoAge", getDemoAge(demoNo));
		request.setAttribute("mrp", getMRP(request, demoNo));
		request.setAttribute("hin", StringUtils.trimToEmpty(getDemoHIN(demoNo)));
		String dob = getDemoDOB(demoNo);
		dob = convertDateFmt(dob, request);
		request.setAttribute("demoDOB", dob);

		List<CaseManagementNote> notes = getNotesToPrint(noteIds, loggedInInfo, demoNo, startDate, endDate);

		HashMap<String, List<CaseManagementNote>> cpp = null;

		if (printCPP)
		{
			cpp = getIssueNotesToPrint(providerNo, demoNo);
		}
		List<CaseManagementNote> othermeds = null;
		if (printRx)
		{
			// If we haven't already pulled out the OMeds issues, do so now
			if (cpp == null)
			{
				List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, "OMeds");
				String[] issueIds = getIssueIds(issues);
				othermeds = caseManagementMgr.getNotes(demoNo, issueIds);
			}
			else
			{
				othermeds = cpp.get("OMeds");
			}
		}

		SimpleDateFormat headerFormat = new SimpleDateFormat("yyyy-MM-dd.hh.mm.ss");
		Date now = new Date();
		String headerDate = headerFormat.format(now);
		
		// Create new file to save form to
		String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		String fileName = path + "EncounterForm-" + headerDate + ".pdf";
		File file = null;
		FileOutputStream out = null;

		try
		{
			file= new File(fileName);
			out = new FileOutputStream(file);

			CaseManagementPrintPdf printer = new CaseManagementPrintPdf(request, out);
			printer.printDocHeaderFooter();
			printer.printCPP(cpp);
			if(printRx)
			{
				printer.printRx(demoNo, othermeds);
			}
			printer.printNotes(notes);

			/* check extensions */
			Enumeration requestParameterNames = request.getParameterNames();
			while (requestParameterNames.hasMoreElements()) {
				String name = (String)requestParameterNames.nextElement();
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

			List<Object> pdfDocs = new ArrayList<>();
			pdfDocs.add(fileName);

			if (printLabs)
			{
				List<LabResultData> labResults = getLabsForPrint(loggedInInfo, demoNo, startDate, endDate);
				List<InputStream> labPrintouts = consultationPDFCreationService.toLabInputStreams(labResults);
				pdfDocs.addAll(labPrintouts);

			}
			ConcatPDF.concat(pdfDocs, os);
		}
		catch (IOException e)
		{
			logger.error("Error ",e);
		}
		finally
		{
			if (out != null)
			{
				out.close();
			}
			if (file != null)
			{
				file.delete();
			}
		}
	}

	/**
	 * Fetch all notes applicable for printing for the given demographic
	 * @param noteIds a list of note IDs we want to print
	 * @param loggedInInfo session information for the currently logged in user
	 * @param demoNo demographic number to print notes for
	 * @param startDate optional start date to print notes from
	 * @param endDate optional end date to print notes until
	 * @return a list of CaseManagementNote entries for the demographic that we can find and print
	 */
	public List<CaseManagementNote> getNotesToPrint(String[] noteIds, LoggedInInfo loggedInInfo, String demoNo, Calendar startDate, Calendar endDate)
		throws MalformedURLException
	{
		List<CaseManagementNote> notes = new ArrayList<>();
		List<String> remoteNoteUUIDs = new ArrayList<>();
		String uuid;

		// Get all local notes where available, and mark down any notes that are remote
		for (String note : noteIds)
		{
			if (note.startsWith("UUID"))
			{
				uuid = note.substring(4);
				remoteNoteUUIDs.add(uuid);
			}
			else
			{
				Long noteId = ConversionUtils.fromLongString(note);
				if (noteId > 0)
				{
					notes.add(caseManagementMgr.getNote(noteId.toString()));
				}
			}
		}

		// Check for any remote notes where available
		if (loggedInInfo.getCurrentFacility().isIntegratorEnabled() && remoteNoteUUIDs.size() > 0)
		{
			DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility());
			List<CachedDemographicNote> remoteNotes = demographicWs.getLinkedCachedDemographicNotes(Integer.parseInt(demoNo));
			for (CachedDemographicNote remoteNote : remoteNotes)
			{
				for (String remoteUUID : remoteNoteUUIDs)
				{
					if (remoteUUID.equals(remoteNote.getCachedDemographicNoteCompositePk().getUuid()))
					{
						CaseManagementNote fakeNote = getFakedNote(remoteNote);
						notes.add(fakeNote);
						break;
					}
				}
			}
		}

		// Notes are unordered - sort by observation date
		OscarProperties properties = OscarProperties.getInstance();
		String noteSort = properties.getProperty("CMESort", "");
		notes.sort(CaseManagementNote.noteObservationDateComparator);

		if (noteSort.trim().equalsIgnoreCase("UP"))
		{
			Collections.reverse(notes);
		}

		// Now that notes are ordered, filter out any notes that do not fit within our start and end date range
		if (startDate != null && endDate != null)
		{
			List<CaseManagementNote> dateFilteredList = new ArrayList<>();
			for (CaseManagementNote cmn : notes)
			{
				Date start = removeTime(startDate.getTime()); // Start date with hours/mins/secs set to 0
				Date end = removeTime(endDate.getTime()); // End date with hours/mins/secs set to 0
				Date observation = removeTime(cmn.getObservation_date()); // Observation date with hours/mins/secs set to 0
				if ((start.before(observation) || start.equals(observation))
						&& (end.after(observation) || end.equals(observation)))
				{
					dateFilteredList.add(cmn);
				}
			}
			notes = dateFilteredList;
		}
		return notes;
	}

	/**
	 * Given a demographic and the currently logged in provider, grab all issue-related notes for printing.
	 * @param providerNo logged in provider
	 * @param demoNo demographic to pull CPP notes for
	 * @return a map containing issues as keys and any associated notes with each issue
	 */
	public HashMap<String, List<CaseManagementNote>> getIssueNotesToPrint(String providerNo, String demoNo)
	{
		HashMap<String, List<CaseManagementNote>> cpp = new HashMap<>();
		String[] issueCodes = {
				"OMeds",
				"SocHistory",
				"MedHistory",
				"Concerns",
				"Reminders",
				"FamHistory",
				"RiskFactors"
		};

		List<CaseManagementNote> issueNotes;
		List<CaseManagementNote> tmpNotes;

		for (String issueCode : issueCodes)
		{
			List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, issueCode);
			String[] issueIds = getIssueIds(issues);
			tmpNotes = caseManagementMgr.getNotes(demoNo, issueIds);
			issueNotes = new ArrayList<>();
			for (CaseManagementNote tmpNote: tmpNotes)
			{
				if (!tmpNote.isLocked())
				{
					List<CaseManagementNoteExt> exts = caseManagementMgr.getExtByNote(tmpNote.getId());
					boolean exclude = false;
					for (CaseManagementNoteExt ext : exts)
					{
						if (ext.getKeyVal().equals("Hide Cpp"))
						{
							if (ext.getValue().equals("1"))
							{
								exclude = true;
								break;
							}
						}
					}

					if (!exclude)
					{
						issueNotes.add(tmpNote);
					}
				}
			}
			cpp.put(issueCode, issueNotes);
		}

		return cpp;
	}

	/**
	 * Given a demographic and a date range, get a list of all labs for printing.
	 * @param loggedInInfo current session info
	 * @param demoNo demographic to get labs for
	 * @param startDate optional start date to begin searching from
	 * @param endDate optional end date to search until
	 */
	public List<LabResultData> getLabsForPrint(LoggedInInfo loggedInInfo, String demoNo, Calendar startDate, Calendar endDate)
	{
		CommonLabResultData commonLabs = new CommonLabResultData();
		List<LabResultData> labs = commonLabs.populateLabsData(loggedInInfo,
				"",
				demoNo,
				"",
				"",
				"",
				"U",
				"");
		LinkedHashMap<String, LabResultData> accessionMap = new LinkedHashMap<>();

		for (int i = 0; i < labs.size(); i++)
		{
			LabResultData result = labs.get(i);
			if (result.isHL7TEXT())
			{
				if (result.accessionNumber == null || result.accessionNumber.isEmpty())
				{
					accessionMap.put("noAccessionNum" + i + result.labType, result);
				}
				else
				{
					if (!accessionMap.containsKey(result.accessionNumber + result.labType))
					{
						accessionMap.put(result.accessionNumber + result.labType, result);
					}
				}
			}
		}

		List<LabResultData> labResults = new ArrayList<>(accessionMap.values());
		//remove out of date range results
		Iterator<LabResultData> labResultIterator = labResults.iterator();
		while (labResultIterator.hasNext())
		{
			LabResultData lab = labResultIterator.next();
			if (startDate != null && lab.getDateObj().getTime() - startDate.getTimeInMillis() < 0)
			{// filter out lab results not in range.
				labResultIterator.remove();
			}
			if (endDate != null && lab.getDateObj().getTime() - endDate.getTimeInMillis() > 0)
			{// filter out lab results not in range
				labResultIterator.remove();
			}
		}

		//sort lab results.
		labResults.sort(new java.util.Comparator<LabResultData>()
		{
			@Override
			public int compare(LabResultData l1, LabResultData l2)
			{
				return -l1.getDateObj().compareTo(l2.getDateObj());
			}
		});

		return labResults;
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
	
	private CaseManagementNote getFakedNote(CachedDemographicNote remoteNote) {
		CaseManagementNote note = new CaseManagementNote();

		if (remoteNote.getObservationDate() != null) note.setObservation_date(remoteNote.getObservationDate().getTime());
		note.setNote(remoteNote.getNote());

		return (note);
	}
	
	
	@SuppressWarnings("unchecked")
    private String[] getAllNoteIds(LoggedInInfo loggedInInfo,HttpServletRequest request,String demoNo) {
		
		HttpSession se = loggedInInfo.getSession();
		
		ProgramProvider pp = programManager2.getCurrentProgramInDomain(loggedInInfo,loggedInInfo.getLoggedInProviderNo());
		String programId = null;
		
		if(pp !=null && pp.getProgramId() != null){
			programId = ""+pp.getProgramId();
		}else{
			programId = String.valueOf(programMgr.getProgramIdByProgramName("OSCAR")); //Default to the oscar program if provider hasn't been assigned to a program
		}
		
		NoteSelectionCriteria criteria = new NoteSelectionCriteria();
		criteria.setMaxResults(Integer.MAX_VALUE);
		criteria.setDemographicId(ConversionUtils.fromIntString(demoNo));
		criteria.setUserRole((String) request.getSession().getAttribute("userrole"));
		criteria.setUserName((String) request.getSession().getAttribute("user"));
		if (request.getParameter("note_sort") != null && request.getParameter("note_sort").length() > 0) {
			criteria.setNoteSort(request.getParameter("note_sort"));
		}
		if (programId != null && !programId.trim().isEmpty()) {
			criteria.setProgramId(programId);
		}
		
		
		if (se.getAttribute("CaseManagementViewAction_filter_roles") != null) {
			criteria.getRoles().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_roles"));
		}
		
		if (se.getAttribute("CaseManagementViewAction_filter_providers") != null) {
			criteria.getProviders().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_providers"));
		}

		if (se.getAttribute("CaseManagementViewAction_filter_providers") != null) {
			criteria.getIssues().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_issues"));
		}

		if (logger.isDebugEnabled()) {
			logger.debug("SEARCHING FOR NOTES WITH CRITERIA: " + criteria);
		}
		
		NoteSelectionResult result = noteService.findNotes(loggedInInfo, criteria);
		
		
		List<String>  buf = new ArrayList<String>();
		for(NoteDisplay nd : result.getNotes()) {
			if (!(nd instanceof NoteDisplayLocal)) {
				continue;
			}
			buf.add(nd.getNoteId().toString());
		}
		
		
		return buf.toArray(new String[0]);
    }

	
	protected String getDemoName(String demoNo) {
		if (demoNo == null) {
			return "";
		}
		return caseManagementMgr.getDemoName(demoNo);
	}

	protected String getDemoSex(String demoNo) {
            if(demoNo == null) {
                return "";
            }
            return caseManagementMgr.getDemoGender(demoNo);
        }

        protected String getDemoAge(String demoNo){
		if (demoNo==null) return "";
		return caseManagementMgr.getDemoAge(demoNo);
	}

	protected String getDemoDOB(String demoNo){
		if (demoNo==null) return "";
		return caseManagementMgr.getDemoDOB(demoNo);
	}

	protected String getDemoHIN(String demoNo)
	{
		if (demoNo == null)
		{
			return null;
		}
		return caseManagementMgr.getDemoHIN(demoNo);
	}
	
	protected String getMRP(HttpServletRequest request,String demographicNo) {
		String strBeanName = "casemgmt_oscar_bean" + demographicNo;
		oscar.oscarEncounter.pageUtil.EctSessionBean bean = (oscar.oscarEncounter.pageUtil.EctSessionBean) request.getSession().getAttribute(strBeanName);
		if (bean == null) return new String("");
		if (bean.familyDoctorNo == null) return new String("");
		if (bean.familyDoctorNo.isEmpty()) return new String("");

		oscar.oscarEncounter.data.EctProviderData.Provider prov = new oscar.oscarEncounter.data.EctProviderData().getProvider(bean.familyDoctorNo);
		String name = prov.getFirstName() + " " + prov.getSurname();
		return name;
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

	private Date removeTime(Date date)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}
	
}
