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
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.utility.Utility;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.casemgmt.util.ExtPrint;
import org.oscarehr.consultations.service.ConsultationPDFCreationService;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.encounterNote.dao.CaseManagementNoteDao;
import org.oscarehr.encounterNote.dao.IssueDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.Issue;
import org.oscarehr.encounterNote.service.EncounterNoteService;
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

	private CaseManagementNoteDao newCaseManagementNoteDao = (CaseManagementNoteDao) SpringUtils.getBean("encounterNote.dao.CaseManagementNoteDao");
	private ConsultationPDFCreationService consultationPDFCreationService = SpringUtils.getBean(ConsultationPDFCreationService.class);
	private DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographic.dao.DemographicDao");
	private IssueDao issueDao = (IssueDao)SpringUtils.getBean("encounterNote.dao.IssueDao");
	private EncounterNoteService encounterNoteService = SpringUtils.getBean(EncounterNoteService.class);

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
						Integer siteSelected,
						Calendar startDate,
						Calendar endDate,
						HttpServletRequest request,
						OutputStream os)
			throws IOException, DocumentException
	{
		String demoNo = String.valueOf(demographicNo);
		Demographic demographic = demographicDao.find(demographicNo);
		request.setAttribute("demoName", demographic.getFirstName() + " " + demographic.getLastName());
		request.setAttribute("demoSex", demographic.getSex());
		request.setAttribute("demoAge", getDemoAge(demographic));
		request.setAttribute("mrp", getMRP(demographic));
		request.setAttribute("hin", StringUtils.trimToEmpty(demographic.getHin()));
		request.setAttribute("site", siteSelected);
		String dob = ConversionUtils.toDateString(demographic.getDateOfBirth());
		dob = convertDateFmt(dob, request);
		request.setAttribute("demoDOB", dob);

		List<CaseManagementNote> notes;

		if (printAllNotes)
		{
			notes = newCaseManagementNoteDao.findLatestRevisionOfAllNotes(demographicNo, false);
		}
		else if (startDate != null && endDate != null)
		{
			notes = newCaseManagementNoteDao.findLatestRevisionOfAllNotes(demographicNo, false);
			notes = filterNotesByDate(notes, startDate, endDate);
		}
		else
		{
			notes = getNotesToPrint(noteIds, loggedInInfo, demoNo);
		}

		notes = filterNotesByDate(notes, startDate, endDate);

		HashMap<String, List<CaseManagementNote>> cpp = null;
		if (printCPP)
		{
			cpp = encounterNoteService.buildCPPHashMapForDemographic(demographicNo);
		}

		List<CaseManagementNote> othermeds = null;
		if (printRx)
		{
			// If we haven't already pulled out the OMeds issues, do so now
			if (cpp == null)
			{
				Issue issue = issueDao.findByCode(Issue.SUMMARY_CODE_OTHER_MEDS);
				othermeds = newCaseManagementNoteDao.findByDemographicAndIssue(demographicNo, issue.getIssueId());
			}
			else
			{
				// In this case, we're printing both CPP and Rx, and we only want these notes to show up under Rx
				othermeds = new ArrayList<>(cpp.get(Issue.SUMMARY_CODE_OTHER_MEDS));
				cpp.remove(Issue.SUMMARY_CODE_OTHER_MEDS);
			}
		}

		// This line doesn't actually set the filename that the user sees, but to lower the possibility of there being
		// conflicts (two people attempting to print same demographic's notes) we'll set the temp name
		String headerDate = ConversionUtils.toDateString(new Date(), ConversionUtils.DATE_TIME_FILENAME);
		
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
			printer.printEncounterNotes(notes);

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
			logger.error("Error ", e);
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
	 * @return a list of CaseManagementNote entries for the demographic that we can find and print
	 */
	public List<CaseManagementNote> getNotesToPrint(String[] noteIds, LoggedInInfo loggedInInfo, String demoNo)
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
					notes.add(newCaseManagementNoteDao.find(noteId));
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
		notes.sort(Comparator.comparing(
				CaseManagementNote::getObservationDate,
				Comparator.nullsLast(Comparator.reverseOrder())
		));

		if (noteSort.trim().equalsIgnoreCase("UP"))
		{
			Collections.reverse(notes);
		}

		return notes;
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

		Collections.sort(labs);

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

	private CaseManagementNote getFakedNote(CachedDemographicNote remoteNote) {
		CaseManagementNote note = new CaseManagementNote();

		if (remoteNote.getObservationDate() != null)
		{
			note.setObservationDate(remoteNote.getObservationDate().getTime());
		}
		note.setNote(remoteNote.getNote());

		return (note);
	}

	/**
	 * Given a list of notes and a date range, remove any notes that fall outside of that range.
	 * @param notes list of notes to filter
	 * @param startDate earliest date that we want to include notes from
	 * @param endDate latest date that we want to include notes up to
	 * @return list of notes that fall between the range
	 */
	private List<CaseManagementNote> filterNotesByDate(List<CaseManagementNote> notes, Calendar startDate, Calendar endDate)
	{
		// filter out any notes that do not fit within our start and end date range
		if (notes != null && startDate != null && endDate != null)
		{
			List<CaseManagementNote> dateFilteredList = new ArrayList<>();
			for (CaseManagementNote cmn : notes)
			{
				Date start = removeTime(startDate.getTime()); // Start date with hours/mins/secs set to 0
				Date end = removeTime(endDate.getTime()); // End date with hours/mins/secs set to 0
				Date observation = removeTime(cmn.getObservationDate()); // Observation date with hours/mins/secs set to 0
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

    protected String getDemoAge(Demographic demographic)
	{
		int age = Utility.calcAge(
				demographic.getYearOfBirth(),
				demographic.getMonthOfBirth(),
				demographic.getDayOfBirth()
		);
		return String.valueOf(age);
	}

	protected String getMRP(Demographic demographic)
	{
		oscar.oscarEncounter.data.EctProviderData.Provider prov = new oscar.oscarEncounter.data.EctProviderData().getProvider(demographic.getProviderNo());
		if (prov == null)
		{
			return "";
		}
		return prov.getFirstName() + " " + prov.getSurname();
	}

	protected String convertDateFmt(String strOldDate, HttpServletRequest request) {
		String strNewDate = "";
		if (strOldDate != null && strOldDate.length() > 0) {
			SimpleDateFormat fmt = new SimpleDateFormat(ConversionUtils.DEFAULT_DATE_PATTERN, request.getLocale());
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
