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
package org.oscarehr.ws.rest;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.AdmissionManager;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.PMmodule.service.ProviderManager;
import org.oscarehr.casemgmt.dao.CaseManagementNoteLinkDAO;
import org.oscarehr.casemgmt.dao.IssueDAO;
import org.oscarehr.casemgmt.model.CaseManagementCPP;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.casemgmt.service.NoteSelectionCriteria;
import org.oscarehr.casemgmt.service.NoteService;
import org.oscarehr.casemgmt.web.CaseManagementEntryAction;
import org.oscarehr.casemgmt.web.NoteDisplay;
import org.oscarehr.casemgmt.web.NoteDisplayLocal;
import org.oscarehr.common.dao.PartialDateDao;
import org.oscarehr.common.model.PartialDate;
import org.oscarehr.common.model.Provider;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.encounterNote.converter.CaseManagementTmpSaveConverter;
import org.oscarehr.encounterNote.dao.CaseManagementTmpSaveDao;
import org.oscarehr.encounterNote.model.CaseManagementTmpSave;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.EncounterUtil;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.conversion.CaseManagementIssueConverter;
import org.oscarehr.ws.rest.conversion.IssueConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.GenericRESTResponse;
import org.oscarehr.ws.rest.to.TicklerNoteResponse;
import org.oscarehr.ws.rest.to.model.CaseManagementIssueTo1;
import org.oscarehr.ws.rest.to.model.CaseManagementTmpSaveTo1;
import org.oscarehr.ws.rest.to.model.IssueTo1;
import org.oscarehr.ws.rest.to.model.NoteExtTo1;
import org.oscarehr.ws.rest.to.model.NoteIssueTo1;
import org.oscarehr.ws.rest.to.model.NoteSelectionTo1;
import org.oscarehr.ws.rest.to.model.NoteTo1;
import org.oscarehr.ws.rest.to.model.TicklerNoteTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.pageUtil.EctSessionBean;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static org.oscarehr.encounterNote.model.Issue.SUMMARY_CODE_TICKLER_NOTE;


@Path("/notes")
@Component("notesService")
@Tag(name = "notes")
public class NotesService extends AbstractServiceImpl
{
	private static String SUMMARY_CODE_ONGOING_CONCERNS = "ongoingconcerns";
	private static String SUMMARY_CODE_MEDICAL_HISTORY = "medhx";
	private static String SUMMARY_CODE_REMINDERS = "reminders";
	private static String SUMMARY_CODE_OTHER_MEDS = "othermeds";
	private static String SUMMARY_CODE_SOCIAL_HISTORY = "sochx";
	private static String SUMMARY_CODE_FAMILY_HISTORY = "famhx";
	private static String SUMMARY_CODE_RISK_FACTORS = "riskfactors";

	private static String SYSTEM_CODE_ONGOING_CONCERNS = "Concerns";
	private static String SYSTEM_CODE_MEDICAL_HISTORY = "MedHistory";
	private static String SYSTEM_CODE_REMINDERS = "Reminders";
	private static String SYSTEM_CODE_OTHER_MEDS = "OMeds";
	private static String SYSTEM_CODE_SOCIAL_HISTORY = "SocHistory";
	private static String SYSTEM_CODE_FAMILY_HISTORY = "FamHistory";
	private static String SYSTEM_CODE_RISK_FACTORS = "RiskFactors";


//	public static String cppCodes[] = {"OMeds", "SocHistory", "MedHistory", "Concerns", "FamHistory", "Reminders", "RiskFactors","OcularMedication","TicklerNote"};
	
	private static Logger logger = MiscUtils.getLogger();
	
	private static ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> editList = new ConcurrentHashMap<String, ConcurrentHashMap<String, Long>>();
	
	@Autowired
	private NoteService noteService; 
	
	@Autowired
	private ProgramManager2 programManager2;
	
	@Autowired
	private ProgramManager programMgr;
	
	@Autowired
	private CaseManagementManager caseManagementMgr;

	@Autowired
	private ProviderManager providerMgr;
	
	@Autowired
	private IssueDAO issueDao;
	
	@Autowired
	private SecurityInfoManager securityInfoManager;

	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private EncounterNoteService encounterNoteService;

	@Autowired
	CaseManagementTmpSaveDao caseManagementTmpSaveDao;

	@Autowired
	CaseManagementTmpSaveConverter caseManagementTmpSaveConverter;

	@Autowired
	private PartialDateDao partialDateDao;
	
	@GET
	@Path("/{demographicNo}/all")
	@Produces("application/json")
	public RestResponse<NoteSelectionTo1> getNotesWithFilter(@PathParam("demographicNo") Integer demographicNo,
	                                                         @QueryParam("providerNoFilter") List<String> providerNos,
	                                                         @QueryParam("roleNoFilter") List<String> roleNos,
															 @QueryParam("issueFilter") List<String> issues,
															 @QueryParam("sortType") String sortType,
	                                                         @QueryParam("numToReturn") @DefaultValue("20") Integer numToReturn,
	                                                         @QueryParam("offset") @DefaultValue("0") Integer offset)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();

		HttpSession se = loggedInInfo.getSession();
		if(se.getAttribute("userrole") == null)
		{
			return RestResponse.errorResponse("Missing session userrole");
		}

		String demoNo = "" + demographicNo;

		// need to check to see if the client is in our program domain
		// if not...don't show this screen!
		String roles = (String) se.getAttribute("userrole");
		if(OscarProperties.getInstance().isOscarLearning() && roles != null && roles.contains("moderator"))
		{
			logger.info("skipping domain check..provider is a moderator");
		}
		else if(
			// TODO-legacy: speed this up
			!caseManagementMgr.isClientInProgramDomain(loggedInInfo.getLoggedInProviderNo(), demoNo) &&
			!caseManagementMgr.isClientReferredInProgramDomain(loggedInInfo.getLoggedInProviderNo(), demoNo)
		)
		{
			return RestResponse.errorResponse("Domain Error");
		}
		String programId = getProgram(loggedInInfo, loggedInInfo.getLoggedInProviderNo());

		NoteSelectionCriteria criteria = new NoteSelectionCriteria();

		criteria.setMaxResults(numToReturn);
		criteria.setFirstResult(offset);

		criteria.setDemographicId(demographicNo);
		criteria.setUserRole((String) se.getAttribute("userrole"));
		criteria.setUserName((String) se.getAttribute("user"));

		criteria.setProviders(providerNos);
		criteria.setRoles(roleNos);
		criteria.setIssues(issues);

		criteria.setNoteSort(sortType);

		criteria.setSliceFromEndOfList(false);

		if(programId != null && !programId.trim().isEmpty())
		{
			criteria.setProgramId(programId);
		}

		NoteSelectionTo1 returnResult  = noteService.searchEncounterNotes(loggedInInfo, criteria);

		return RestResponse.successResponse(returnResult);
	}
	
	
	
	@POST
	@Path("/{demographicNo}/tmpSave")
	@Consumes("application/json")
	@Produces("application/json")
	public NoteTo1 tmpSaveNote(@PathParam("demographicNo") Integer demographicNo ,NoteTo1 note){
		logger.debug("autosave "+note);

		LoggedInInfo loggedInInfo = getLoggedInInfo();//  LoggedInInfo.loggedInInfo.get();
		String providerNo=loggedInInfo.getLoggedInProvider().getProviderNo();

		
		String programId = getProgram(loggedInInfo,providerNo);
		String noteStr = note.getNote();
		String noteId  = ""+note.getNoteId();
		
		try{  
			Integer.parseInt(noteId);
		}catch(Exception e){
			noteId = null;
		}

		if (noteStr == null || noteStr.length() == 0) {
			return null;
		}		
		
		//delete from tmp save and then add another
		try {
			caseManagementMgr.deleteTmpSave(providerNo, ""+demographicNo, programId);
			caseManagementMgr.tmpSave(providerNo, ""+demographicNo, programId, noteId, noteStr);
		} catch (Throwable e) {
			logger.error("AutoSave Error: ", e);
		}

		return note;
	}

	//TODO-legacy -- POST shouldn't return a transfer object
	/**
	 * Save a new note
	 * @param demographicNo
	 * @param note
	 * @return the note transfer object, or null if nothing to save
	 */
	@POST
	@Path("/{demographicNo}/save")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<NoteTo1> saveNote(
			@PathParam("demographicNo") Integer demographicNo,
			@QueryParam("deleteTmpSave") @DefaultValue("false") String deleteTmpSaveString,
			NoteTo1 note)
	{
		logger.debug("saveNote "+note);

		boolean deleteTmpSave = false;
		if(deleteTmpSaveString.toLowerCase().equals("true"))
		{
			deleteTmpSave = true;
		}

		try {

			String noteTxt = StringUtils.trimToNull(note.getNote());
			// if there is not a note to save, exit immediately
			if (noteTxt == null || noteTxt.trim().isEmpty()) {
				return RestResponse.errorResponse("Note text cannot be empty");
			}

			LoggedInInfo loggedInInfo = getLoggedInInfo();
			String providerNo = loggedInInfo.getLoggedInProviderNo();
			Provider provider = loggedInInfo.getLoggedInProvider();
			String userName = provider != null ? provider.getFullName() : "";

			String demographicNoStr = "" + demographicNo;
			String uuid = note.getUuid();
			Date now = new Date();

			// the note to be saved
			CaseManagementNote caseMangementNote = new CaseManagementNote();

			// -- set up the basic note info --

			caseMangementNote.setDemographic_no(demographicNoStr);
			caseMangementNote.setProvider(provider);
			caseMangementNote.setProviderNo(providerNo);
			caseMangementNote.setNote(noteTxt);
			logger.debug("enc TYPE " + note.getEncounterType());
			caseMangementNote.setEncounter_type(note.getEncounterType());
			// If uuid is not null and is not an empty string this note already exists and we must keep its uuid
			if (uuid != null && !uuid.trim().equals("")) {
				caseMangementNote.setUuid(uuid);
			}

			// set signed & signing provider
			//Need to check some how that if a note is signed that it must stay signed, currently this is done in the interface where the save button is not available.
			if (note.getIsSigned()) {
				caseMangementNote.setSigning_provider_no(providerNo);
				caseMangementNote.setSigned(true);
			}
			else {
				caseMangementNote.setSigning_provider_no("");
				caseMangementNote.setSigned(false);
			}

			// set program ID
			String programIdString = getProgram(loggedInInfo, providerNo); //might not to convert it.
			if (!NumberUtils.isDigits(programIdString)) {
				logger.warn("programId is not a valid number:" + programIdString);
			}
			caseMangementNote.setProgram_no(programIdString);

			// set note date & time
			Date observationDate = note.getObservationDate();

			// if observation date exists and is not in future, set it.
			if (observationDate != null && observationDate.getTime() <= now.getTime()) {
				caseMangementNote.setObservation_date(observationDate);
			}
			else { //default to current date
				caseMangementNote.setObservation_date(now);
			}
			caseMangementNote.setUpdate_date(now);

			// set appointment
			if (note.getAppointmentNo() != null) {
				caseMangementNote.setAppointmentNo(note.getAppointmentNo());
			}

			/* Save assigned issues & link with the note */
			List<CaseManagementIssue> issuelist = toAssignedCaseManagementIssueList(note.getAssignedIssues(), demographicNoStr, providerNo);

			if(note.isTicklerNote())
			{
				// tickler notes must always have the tickler issue attached
				// this should exist unless they are somehow adding a new tickler note through the chart, but not assigning the issue
				CaseManagementIssue cmi = caseManagementMgr.getIssueByIssueCode(demographicNoStr, SUMMARY_CODE_TICKLER_NOTE);
				if(cmi != null)
				{
					issuelist.add(cmi);
				}
			}

			caseMangementNote.setIssues(new HashSet<CaseManagementIssue>(issuelist));

			// update appointment and add verify message to note if verified
			boolean verify = (note.getIsVerified() != null && note.getIsVerified());

			CaseManagementCPP cpp = this.caseManagementMgr.getCPP(demographicNoStr);
			if (cpp == null) {
				cpp = new CaseManagementCPP();
				cpp.setDemographic_no(demographicNoStr);
			}

			// Load annotation if it exists
			org.oscarehr.encounterNote.model.CaseManagementNote annotationNoteJPA =
					encounterNoteService.getAnnotation(note.getNoteId());

			CaseManagementNote annotationNote = null;
			if(annotationNoteJPA != null)
			{
				// XXX: Loading the note using the Hibernate model to be compatible with the save
				// method in caseManagementMgr.  Change this if replacing the save method.
				annotationNote = caseManagementMgr.getNote(annotationNoteJPA.getId().toString());
			}

			//String ongoing = null; // figure out this
			String ongoing = new String();
			String lastSavedNoteString = null;
			String remoteAddr = ""; // Not sure how to get this
			caseMangementNote = caseManagementMgr.saveCaseManagementNote(
					loggedInInfo, caseMangementNote, issuelist, cpp, ongoing, verify,
					loggedInInfo.getLocale(), now, annotationNote, userName, providerNo,
					remoteAddr, lastSavedNoteString);

			caseManagementMgr.getEditors(caseMangementNote);

			note.setNoteId(Integer.parseInt("" + caseMangementNote.getId()));
			note.setUuid(caseMangementNote.getUuid());
			note.setUpdateDate(caseMangementNote.getUpdate_date());
			note.setObservationDate(caseMangementNote.getObservation_date());
			logger.debug("note should return like this " + note.getNote());
			logger.info("NOTE ID #" + caseMangementNote.getId() + " SAVED");

			String saveStatus = (caseMangementNote.getId() != null) ? LogConst.STATUS_SUCCESS : LogConst.STATUS_FAILURE;
			LogAction.addLogEntry(providerNo, demographicNo, LogConst.ACTION_ADD, LogConst.CON_CME_NOTE, saveStatus,
					String.valueOf(caseMangementNote.getId()), getLoggedInInfo().getIp(), caseMangementNote.getAuditString());

			if(deleteTmpSave)
			{
				try
				{
					String programId = getProgram(loggedInInfo, providerNo);
					caseManagementMgr.deleteTmpSave(note.getProviderNo(), demographicNo.toString(), programId);
				}
				catch (Exception e)
				{
					logger.warn("Error deleting tmpSave", e);
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Error saving Note", e);
			return RestResponse.errorResponse("Failed to save Note");
		}


		return RestResponse.successResponse(note);
	}


	//TODO-legacy -- POST shouldn't return a transfer object
	@POST
	@Path("/{demographicNo}/saveIssueNote")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<NoteIssueTo1> saveIssueNote(@PathParam("demographicNo") Integer demographicNo, NoteIssueTo1 noteIssue) {

		try {
			NoteTo1 note = noteIssue.getEncounterNote();
			NoteExtTo1 noteExtTo1 = noteIssue.getGroupNoteExt();

			if (noteExtTo1.getStartDate() != null && org.oscarehr.dataMigration.model.common.PartialDate.allFieldsEmpty(noteExtTo1.getStartDate()))
			{
				noteExtTo1.setStartDate(null);
			}

			if (noteExtTo1.getResolutionDate() != null && org.oscarehr.dataMigration.model.common.PartialDate.allFieldsEmpty(noteExtTo1.getResolutionDate()))
			{
				noteExtTo1.setResolutionDate(null);
			}

			if (noteExtTo1.getProcedureDate() != null && org.oscarehr.dataMigration.model.common.PartialDate.allFieldsEmpty(noteExtTo1.getProcedureDate()))
			{
				noteExtTo1.setProcedureDate(null);
			}

			IssueTo1 issueTo1 = noteIssue.getIssue();
			List<CaseManagementIssueTo1> assignedCMIssues = noteIssue.getAssignedCMIssues();
			String annotationAttribute = noteIssue.getAnnotation_attrib();

			String noteTxt = StringUtils.trimToNull(note.getNote());
			// if there is not a note to save, exit immediately
			if (noteTxt == null) {
				return RestResponse.errorResponse("Note text cannot be empty");
			}

			LoggedInInfo loggedInInfo = getLoggedInInfo();
			String providerNo = loggedInInfo.getLoggedInProviderNo();
			Provider provider = loggedInInfo.getLoggedInProvider();
			String providerName = provider != null ? provider.getFullName() : "";

			Integer oldNoteId = note.getNoteId();
			String demographicNoStr = "" + demographicNo;
			String noteIdStr = String.valueOf(note.getNoteId());
			String uuid = note.getUuid();

			String programId = getProgram(loggedInInfo, providerNo);

			/* Save a new Note*/
			CaseManagementNote caseMangementNote = new CaseManagementNote();

			// -- set up the basic note info --

			caseMangementNote.setDemographic_no(demographicNoStr);
			caseMangementNote.setProvider(provider);
			caseMangementNote.setProviderNo(providerNo);
			caseMangementNote.setNote(noteTxt);
			caseMangementNote.setEncounter_type(note.getEncounterType());
			caseMangementNote.setProgram_no(programId);

			// If uuid is not null and is not an empty string this note already exists and we must keep its uuid
			if (uuid != null && !uuid.trim().isEmpty()) {
				caseMangementNote.setUuid(uuid);
			}
			//Need to check some how that if a note is signed that it must stay signed, currently this is done in the interface where the save button is not available.
			if (note.getIsSigned()) {
				caseMangementNote.setSigning_provider_no(providerNo);
				caseMangementNote.setSigned(true);
			}
			else {
				caseMangementNote.setSigning_provider_no("");
				caseMangementNote.setSigned(false);
			}

			Date now = new Date();

			// set note date & time
			Date observationDate = note.getObservationDate();

			// if observation date exists and is not in future, set it.
			if (observationDate != null && observationDate.getTime() <= now.getTime()) {
				caseMangementNote.setObservation_date(observationDate);
			}
			else { //default to current date
				caseMangementNote.setObservation_date(now);
			}
			caseMangementNote.setUpdate_date(now);


			boolean newNote = false;

			// we don't want to try to remove an issue from a new note so we test here
			if (note.getNoteId() == null || note.getNoteId() == 0) {
				newNote = true;
			}
			else {
				boolean extChanged = true; //false
				// if note has not changed don't save
				caseManagementMgr.getNote(noteIdStr);
				if (note.getNote().equals(note.getNote()) && issueTo1.isIssueChange() && !extChanged && note.isArchived()) return null;
			}

			caseMangementNote.setArchived(note.isArchived());

			if (!newNote) {
				note.setRevision(Integer.parseInt(note.getRevision()) + 1 + "");
			}

			CaseManagementCPP cpp = this.caseManagementMgr.getCPP(demographicNoStr);
			if (cpp == null) {
				cpp = new CaseManagementCPP();
				cpp.setDemographic_no(demographicNoStr);
			}

			if (note.isCpp() && note.getSummaryCode() != null) {
				cpp = copyNote2cpp(cpp, note.getNote(), note.getSummaryCode());
			}

			ProgramManager programManager = (ProgramManager) SpringUtils.getBean("programManager");
			AdmissionManager admissionManager = (AdmissionManager) SpringUtils.getBean("admissionManager");

			String role = null;
			String team = null;

			try {
				role = String.valueOf((programManager.getProgramProvider(providerNo, programId)).getRole().getId());
			}
			catch (Exception e) {
				logger.error("Error", e);
				role = "0";
			}

			caseMangementNote.setReporter_caisi_role(role);

			try {
				team = String.valueOf((admissionManager.getAdmission(programId, demographicNo)).getTeamId());
			}
			catch (Exception e) {
				logger.error("Error", e);
				team = "0";
			}
			caseMangementNote.setReporter_program_team(team);


			//this code basically updates the CPP note with which issues were removed
			if (!newNote) {
				List<String> removedIssueNames = new ArrayList<String>();
				for (CaseManagementIssueTo1 cmit : assignedCMIssues) {
					if (cmit.isUnchecked() && cmit.getId() != null && cmit.getId().longValue() > 0) {
						//we want to remove this association, and append to the note
						removedIssueNames.add(cmit.getIssue().getDescription());
					}
				}

				if (!removedIssueNames.isEmpty()) {
					String text = new SimpleDateFormat("dd-MMM-yyyy").format(new Date()) + " " + "Removed following issue(s)" + ":\n" + StringUtils.join(removedIssueNames, ",");
					caseMangementNote.setNote(caseMangementNote.getNote() + "\n" + text);
				}
			}


			/* get assigned issues & link with the note */
			List<CaseManagementIssue> issuelist = toAssignedCaseManagementIssueList(note.getAssignedIssues(), demographicNoStr, providerNo);

			//this is actually just the issue for the main note
			//translate summary codes
			String mainIssueCode = translateSystemCode(note.getSummaryCode());

			// find the existing issue for the main note. create it if needed.
			Issue cppIssue = caseManagementMgr.getIssueInfoByCode(mainIssueCode);
			CaseManagementIssue mainIssueEntry = this.caseManagementMgr.getIssueByIssueCode(demographicNoStr, mainIssueCode);

			//no issue existing for this type of CPP note..create it
			if (mainIssueEntry == null) {
				Date creationDate = new Date();

				mainIssueEntry = new CaseManagementIssue();
				mainIssueEntry.setAcute(false);
				mainIssueEntry.setCertain(false);
				mainIssueEntry.setDemographic_no(demographicNoStr);
				mainIssueEntry.setIssue_id(cppIssue.getId());
				mainIssueEntry.setIssue(cppIssue);
				mainIssueEntry.setMajor(false);
				mainIssueEntry.setProgram_id(Integer.parseInt(programId));
				mainIssueEntry.setResolved(false);
				mainIssueEntry.setType(cppIssue.getRole());
				mainIssueEntry.setUpdate_date(creationDate);
			}

			issuelist.add(mainIssueEntry);
			caseMangementNote.setIssues(new HashSet<CaseManagementIssue>(issuelist));
			// save the issue List
			caseManagementMgr.saveAndUpdateCaseIssues(issuelist);


			//update positions
			/*
			 * There's a few cases to handle, but basically when user is adding, editing, or archiving,
			 * we go and set the positions so it's always 1,2,..,n across the group note. Archived notes,
			 * and older notes (not the latest based on uuid/id) have positions set to 0
			 */
			String[] strIssueId = {String.valueOf(cppIssue.getId())};
			List<CaseManagementNote> curCPPNotes = this.caseManagementMgr.getActiveNotes(demographicNoStr, strIssueId);
			Collections.sort(curCPPNotes, CaseManagementNote.getPositionComparator());


			if (note.isArchived()) {
				//this one will basically assign 1,2,3,..,n to the group and ignore the one to be archived..setting it's position to 0
				int positionToAssign = 1;
				for (int x = 0; x < curCPPNotes.size(); x++) {
					if (curCPPNotes.get(x).getUuid().equals(note.getUuid())) {
						curCPPNotes.get(x).setPosition(0);
						caseManagementMgr.updateNote(curCPPNotes.get(x));
						continue;
					}
					curCPPNotes.get(x).setPosition(positionToAssign);
					caseManagementMgr.updateNote(curCPPNotes.get(x));
					positionToAssign++;
				}

			}
			else {
				List<CaseManagementNote> curCPPNotes2 = new ArrayList<CaseManagementNote>();
				for (CaseManagementNote cn : curCPPNotes) {
					if (!cn.getUuid().equals(note.getUuid())) {
						curCPPNotes2.add(cn);
					}
					else {
						cn.setPosition(0);
						caseManagementMgr.updateNote(cn);
					}
				}
				//we make a fake CaseManagementNoteEntry into curCPPNotes, and insert it into desired location.
				//we then just set the positions to 1,2,...,n ignoring the fake one, but still incrementing the positionToAssign variable
				//when the new note is saved.it will have the missing position.
				int positionToAssign = 1;
				CaseManagementNote xn = new CaseManagementNote();
				xn.setId(-1L);
				curCPPNotes2.add(note.getPosition() - 1, xn);
				for (int x = 0; x < curCPPNotes2.size(); x++) {
					if (curCPPNotes2.get(x).getId() != -1L) {
						//update the note
						curCPPNotes2.get(x).setPosition(positionToAssign);
						caseManagementMgr.updateNote(curCPPNotes2.get(x));
					}
					if (curCPPNotes2.get(x).getId() != -1L && curCPPNotes2.get(x).getUuid().equals(note.getUuid())) {
						curCPPNotes2.get(x).setPosition(0);
						caseManagementMgr.updateNote(curCPPNotes2.get(x));
						positionToAssign--;
					}
					positionToAssign++;
				}
			}
			if (!note.isArchived()) {
				caseMangementNote.setPosition(note.getPosition());
			}

			/*
			 *
			 * update_date, observation_date,
			 * demographic_no, provider_no, note, signed,
			 * include_issue_innote, signing_provider_no, encounter_type, billing_code,
			 * program_no, reporter_caisi_role, reporter_program_team, history,
			 *  uuid, password, locked, archived, appointmentNo, hourOfEncounterTime, minuteOfEncounterTime, hourOfEncTransportationTime, minuteOfEncTransportationTime
			 *
			 */

			String savedStr = caseManagementMgr.saveNote(cpp, caseMangementNote, providerNo, providerName, null, note.getRoleName());
			caseManagementMgr.saveCPP(cpp, providerNo);

			caseManagementMgr.getEditors(caseMangementNote);

			note.setNoteId(Integer.parseInt("" + caseMangementNote.getId()));
			note.setUuid(caseMangementNote.getUuid());
			note.setUpdateDate(caseMangementNote.getUpdate_date());
			note.setObservationDate(caseMangementNote.getObservation_date());

			long newNoteId = Long.valueOf(note.getNoteId());

			if (note.getNoteId() != 0) {
				caseManagementMgr.addNewNoteLink(newNoteId);
			}

			/* save extra fields */
			CaseManagementNoteExt cme = new CaseManagementNoteExt();

			if (noteExtTo1.getStartDate() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.STARTDATE);
				cme.setDateValue(ConversionUtils.toLegacyDate(noteExtTo1.getStartDate().toLocalDate()));
				caseManagementMgr.saveNoteExt(cme);
				encounterNoteService.saveExtPartialDate(noteExtTo1.getStartDate(), cme.getId());
			}

			if (noteExtTo1.getResolutionDate() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.RESOLUTIONDATE);
				cme.setDateValue(ConversionUtils.toLegacyDate(noteExtTo1.getResolutionDate().toLocalDate()));
				caseManagementMgr.saveNoteExt(cme);
				encounterNoteService.saveExtPartialDate(noteExtTo1.getResolutionDate(), cme.getId());
			}

			if (noteExtTo1.getProcedureDate() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.PROCEDUREDATE);
				cme.setDateValue(ConversionUtils.toLegacyDate(noteExtTo1.getProcedureDate().toLocalDate()));
				caseManagementMgr.saveNoteExt(cme);
				encounterNoteService.saveExtPartialDate(noteExtTo1.getProcedureDate(), cme.getId());
			}

			if (noteExtTo1.getAgeAtOnset() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.AGEATONSET);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getAgeAtOnset());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getTreatment() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.TREATMENT);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getTreatment());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getProblemStatus() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.PROBLEMSTATUS);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getProblemStatus());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getExposureDetail() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.EXPOSUREDETAIL);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getExposureDetail());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getRelationship() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.RELATIONSHIP);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getRelationship());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getLifeStage() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.LIFESTAGE);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getLifeStage());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getHideCpp() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.HIDECPP);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getHideCpp());
				caseManagementMgr.saveNoteExt(cme);
			}

			if (noteExtTo1.getProblemDesc() != null)
			{
				cme.setNoteId(newNoteId);
				cme.setKeyVal(NoteExtTo1.PROBLEMDESC);
				cme.setDateValue((Date) null);
				cme.setValue(noteExtTo1.getProblemDesc());
				caseManagementMgr.saveNoteExt(cme);
			}

			/* save extra fields */
			noteIssue.setEncounterNote(note);
			noteIssue.setGroupNoteExt(noteExtTo1);

			String saveStatus = (caseMangementNote.getId() != null) ? LogConst.STATUS_SUCCESS : LogConst.STATUS_FAILURE;
			LogAction.addLogEntry(providerNo, demographicNo, LogConst.ACTION_ADD, LogConst.CON_CME_NOTE, saveStatus,
					String.valueOf(caseMangementNote.getId()), getLoggedInInfo().getIp(), caseMangementNote.getAuditString());


			// Save Annotation
			if (newNote)
			{
				if (annotationAttribute != null)
				{
					HttpSession session = loggedInInfo.getSession();

					CaseManagementNote annotationNote = (CaseManagementNote) session.getAttribute(annotationAttribute);

					if (annotationNote != null)
					{
						// new annotation created and got it in session attribute
						caseManagementMgr.saveNoteSimple(annotationNote);
						CaseManagementNoteLink cml = new CaseManagementNoteLink(CaseManagementNoteLink.CASEMGMTNOTE,
								newNoteId, annotationNote.getId());

						caseManagementMgr.saveNoteLink(cml);

						String annotationSaveStatus = (annotationNote.getId() != null) ? LogConst.STATUS_SUCCESS : LogConst.STATUS_FAILURE;
						LogAction.addLogEntry(
								providerNo,
								demographicNo,
								LogConst.ANNOTATE,
								LogConst.CON_CME_NOTE,
								annotationSaveStatus,
								String.valueOf(annotationNote.getId()),
								getLoggedInInfo().getIp(),
								caseMangementNote.getAuditString()
						);

						session.removeAttribute(annotationAttribute);
					}
				}
			}
			else
			{
				caseManagementMgr.addExistingAnnotation(oldNoteId.longValue(), newNoteId);
			}
		}
		catch(Exception e) {
			logger.error("Error saving Issue Note", e);
			return RestResponse.errorResponse("Error saving Issue Note");
		}
		return RestResponse.successResponse(noteIssue);
	}



	protected CaseManagementCPP copyNote2cpp(CaseManagementCPP cpp, String note, String code) {
		//TODO-legacy: change this back to a loop
		StringBuilder text = new StringBuilder();
		Date d = new Date();
		String separator = "\n-----[[" + d + "]]-----\n";
	
			if (code.equals("othermeds")) {
				text.append(cpp.getFamilyHistory());
				text.append(separator);
				text.append(note);
				cpp.setFamilyHistory(text.toString());
				
			} else if (code.equals("sochx")) {
				text.append(cpp.getSocialHistory());
				text.append(separator);
				text.append(note);
				cpp.setSocialHistory(text.toString());
				
			} else if (code.equals("medhx")) {
				text.append(cpp.getMedicalHistory());
				text.append(separator);
				text.append(note);
				cpp.setMedicalHistory(text.toString());
				
			} else if (code.equals("ongoingconcerns")) {
				text.append(cpp.getOngoingConcerns());
				text.append(separator);
				text.append(note);
				cpp.setOngoingConcerns(text.toString());
				
			} else if (code.equals("reminders")) {
				text.append(cpp.getReminders());
				text.append(separator);
				text.append(note);
				cpp.setReminders(text.toString());
				
			} else if (code.equals("famhx")) {
				text.append(cpp.getFamilyHistory());
				text.append(separator);
				text.append(note);
				cpp.setFamilyHistory(text.toString());
				
			} else if (code.equals("riskfactors")) {
				text.append(cpp.getRiskFactors());
				text.append(separator);
				text.append(note);
				cpp.setRiskFactors(text.toString());
				
			}
		

		return cpp;
	}



	private String getString(JSONObject jsonobject,String key){
		if(jsonobject.containsKey(key)){
			return jsonobject.getString(key); 
		}
		return null;
	}

	@GET
	@Path("/{demographicNo}/noteToEdit/latest")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<NoteIssueTo1> getLatestNoteToEdit(@PathParam("demographicNo") Integer demographicNo)
	{
		LoggedInInfo loggedInInfo =  getLoggedInInfo();

		NoteIssueTo1 returnNote = encounterNoteService.getLatestUnsignedNote(
				demographicNo,
				Integer.parseInt(loggedInInfo.getLoggedInProviderNo())
		);

		return RestResponse.successResponse(returnNote);
	}

	@GET
	@Path("/{demographicNo}/tmpSave")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<CaseManagementTmpSaveTo1> getTmpSave(@PathParam("demographicNo") Integer demographicNo)
	{
		LoggedInInfo loggedInInfo =  getLoggedInInfo();
		String providerNo = loggedInInfo.getLoggedInProviderNo();
		Integer programId = getProgramId(loggedInInfo, providerNo);

		CaseManagementTmpSave tmpSave = caseManagementTmpSaveDao.find(providerNo, demographicNo, programId);

		CaseManagementTmpSaveTo1 note = caseManagementTmpSaveConverter.convertCasemanagementTmpSaveToCaseManagementTmpSaveTo1(tmpSave);

		return RestResponse.successResponse(note);
	}

	@GET
	@Path("/{demographicNo}/getNoteToEdit/{noteId}")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<NoteIssueTo1> getNoteToEdit(@PathParam("demographicNo") Integer demographicNo,
											   @PathParam("noteId") Integer noteId)
	{
		NoteIssueTo1 returnNote = encounterNoteService.getNoteToEdit(demographicNo, noteId);

		return RestResponse.successResponse(returnNote);
	}

	@POST
	@Path("/{demographicNo}/getCurrentNote")
	@Consumes("application/json")
	@Produces("application/json")
	@Hidden
	public NoteTo1 getCurrentNote(@PathParam("demographicNo") Integer demographicNo ,JSONObject jsonobject){
		logger.debug("getCurrentNote "+jsonobject);
		LoggedInInfo loggedInInfo =  getLoggedInInfo(); //LoggedInInfo.loggedInInfo.get();

		String providerNo=loggedInInfo.getLoggedInProviderNo();

		
		HttpSession session = loggedInInfo.getSession();
		if (session.getAttribute("userrole") == null) {
//			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

//		CaseManagementEntryFormBean cform = (CaseManagementEntryFormBean) form;
//		cform.setChain("");
//		request.setAttribute("change_flag", "false");
//		request.setAttribute("from", "casemgmt");

		

		String programIdString = getProgram(loggedInInfo,providerNo);
		Integer programId = null;
		try {
			programId = Integer.parseInt(programIdString);
		} catch (Exception e) {
			logger.warn("Error parsing programId:" + programIdString, e);
		}

///////Not sure what this is about??		
//		/* process the request from other module */
//		if (!"casemgmt".equalsIgnoreCase(request.getParameter("from"))) {
//
//			// no demographic number, no page
//			if (request.getParameter("demographicNo") == null || "".equals(request.getParameter("demographicNo"))) {
//				return mapping.findForward("NoDemoERR");
//			}
//			request.setAttribute("from", "");
//		}


		

		CaseManagementNote note = null;

		String nId = getString(jsonobject,"noteId");// request.getParameter("noteId");
		String forceNote = getString(jsonobject,"forceNote");//request.getParameter("forceNote");
		if (forceNote == null) forceNote = "false";

		logger.debug("NoteId " + nId);

		CaseManagementTmpSave tmpsavenote = this.caseManagementMgr.restoreTmpSave(providerNo, ""+demographicNo, programIdString);
		

		logger.debug("Get Note for editing");
		String strBeanName = "casemgmt_oscar_bean" + demographicNo;
		EctSessionBean bean = (EctSessionBean) loggedInInfo.getSession().getAttribute(strBeanName);
		String encType = getString(jsonobject,"encType");
		
		logger.debug("Encounter Type : "+encType);
		
		

		// create a new note
		if (getString(jsonobject,"note_edit") != null && getString(jsonobject,"note_edit").equals("new")) {
			logger.debug("NEW NOTE GENERATED");
//			request.setAttribute("newNoteIdx", request.getParameter("newNoteIdx"));

			note = new CaseManagementNote();
			note.setProviderNo(providerNo);
			Provider prov = new Provider();
			prov.setProviderNo(providerNo);
			note.setProvider(prov);
			note.setDemographic_no(""+demographicNo);

//////This adds the note text i think
//			if (!OscarProperties.getInstance().isPropertyActive("encounter.empty_new_note")) {
//				this.insertReason(request, note);
//			} else {
//				note.setNote("");
//				note.setEncounter_type("");
//			}

			

			if (encType == null || encType.equals("")) {
				note.setEncounter_type("");
			} else {
				note.setEncounter_type(encType);
			}
			if (bean.encType != null && bean.encType.length() > 0) {
				note.setEncounter_type(bean.encType);
			}

//			resetTemp(providerNo, ""+demographicNo, programIdString);

		}
		// get the last temp note?
		else if (tmpsavenote != null && !forceNote.equals("true")) {
			logger.debug("tempsavenote is NOT NULL == noteId :"+tmpsavenote.getNoteId());
			if (tmpsavenote.getNoteId() > 0) {
//				session.setAttribute("newNote", "false");
				note = caseManagementMgr.getNote(String.valueOf(tmpsavenote.getNoteId()));
				logger.debug("Restoring " + String.valueOf(note.getId()));
			} else {
				logger.debug("creating new note");
//				session.setAttribute("newNote", "true");
//				session.setAttribute("issueStatusChanged", "false");
				note = new CaseManagementNote();
				note.setProviderNo(providerNo);
				Provider prov = new Provider();
				prov.setProviderNo(providerNo);
				note.setProvider(prov);
				note.setDemographic_no(""+demographicNo);
			}
			
			note.setNote(tmpsavenote.getNote());
			logger.debug("Setting note to " + note.getNote());

		}
		// get an existing non-temp note?
		else if (nId != null && Integer.parseInt(nId) > 0) {
			logger.debug("Using nId " + nId + " to fetch note");
//			session.setAttribute("newNote", "false");
			note = caseManagementMgr.getNote(nId);

			if (note.getHistory() == null || note.getHistory().equals("")) {
				// old note - we need to save the original in here
				note.setHistory(note.getNote());

				caseManagementMgr.saveNoteSimple(note);
//				addNewNoteLink(Long.parseLong(nId));
			}

		}
		// No note specified, make a new note
		else {
			logger.debug("in empty else");
			String appointmentNo = getString(jsonobject,"appointmentNo");
			note = caseManagementMgr.makeNewNote(providerNo, ""+demographicNo, encType, appointmentNo,loggedInInfo.getLocale());
		}
		

		/*
		 * do the restore if(restore != null && restore.booleanValue() == true) { String tmpsavenote = this.caseManagementMgr.restoreTmpSave(providerNo,demono,programId); if(tmpsavenote != null) { note.setNote(tmpsavenote); } }
		 */
		logger.debug("note ?" +note);
		logger.debug("Set Encounter Type: " + note.getEncounter_type());
		logger.debug("Fetched Note " + String.valueOf(note.getId()));

		logger.debug("Populate Note with editors");
		this.caseManagementMgr.getEditors(note);
		

		// put the new/retrieved not in the form object for rendering on page
		/* set issue checked list */

		// get issues for current demographic, based on provider rights


//		cform.setSign("off");
//		if (!note.isIncludeissue()) cform.setIncludeIssue("off");
//		else cform.setIncludeIssue("on");

//		boolean passwd = caseManagementMgr.getEnabled();
//		String chain = request.getParameter("chain");

		/*
		 ///Is it a specific thats being requested to edit

	      //YES  -- > load that note

	      //NO
	            //check to see if a note is in tmp-save? 
	                  //YES -->> load that tmp save note
	                  //NO 

	                        //Is there an unsigned note?
	                                //YES -->> load that unsigned save note
	                                //NO
	                                   //Is it a new note? What type?  -->> load the new note (ie visit note, tele note etc)
		 */
		
		NoteTo1 returnNote = new NoteTo1();
		
		NoteDisplay nd = new NoteDisplayLocal(loggedInInfo,note);
		
		returnNote.setNoteId(nd.getNoteId());
		
		returnNote.setIsSigned(nd.isSigned());
		returnNote.setIsEditable(nd.isEditable());
		returnNote.setObservationDate(nd.getObservationDate());
		returnNote.setRevision(nd.getRevision());
		returnNote.setUpdateDate(nd.getUpdateDate());
		returnNote.setProviderName(nd.getProviderName());
		returnNote.setProviderNo(nd.getProviderNo());
		returnNote.setStatus(nd.getStatus());
		returnNote.setProgramName(nd.getProgramName());
		returnNote.setLocation(nd.getLocation());
		returnNote.setRoleName(nd.getRoleName());
		returnNote.setRemoteFacilityId(nd.getRemoteFacilityId());
		returnNote.setUuid(nd.getUuid());
		returnNote.setHasHistory(nd.getHasHistory());
		returnNote.setLocked(nd.isLocked());
		returnNote.setNote(nd.getNote());
		returnNote.setDocument(nd.isDocument());
		returnNote.setRxAnnotation(nd.isRxAnnotation());
		returnNote.setEformData(nd.isEformData());
		returnNote.setEncounterForm(nd.isEncounterForm());
		returnNote.setInvoice(nd.isInvoice());
		returnNote.setTicklerNote(nd.isTicklerNote());
		returnNote.setEncounterType(nd.getEncounterType());
		returnNote.setEditorNames(nd.getEditorNames());
		returnNote.setIssueDescriptions(nd.getIssueDescriptions());
		returnNote.setReadOnly(nd.isReadOnly());
		returnNote.setGroupNote(nd.isGroupNote());
		returnNote.setCpp(nd.isCpp());
		returnNote.setEncounterTime(nd.getEncounterTime());	
		returnNote.setEncounterTransportationTime(nd.getEncounterTransportationTime());
		returnNote.setAppointmentNo(nd.getAppointmentNo());
		
		return returnNote;
	}
	
	
	private void processJsonArray( JSONObject jsonobject, String key, List<String> list){
		if( jsonobject != null && jsonobject.containsKey(key)){
			JSONArray arr = jsonobject.getJSONArray(key);
			for(int i =0; i < arr.size();i++){
				list.add(arr.getString(i));
			}
		}
	 
	}
	
	@GET
	@Path("/getIssueNote/{noteId}")	
	@Produces("application/json")
	public RestResponse<NoteIssueTo1> getIssueNote(@PathParam("noteId") Integer noteId){

		try {
			//get all note values NoteDisplay nd = new NoteDisplayLocal(loggedInInfo,note);
			CaseManagementNote casemgmtNote = caseManagementMgr.getNote(String.valueOf(noteId));
			if(casemgmtNote == null) {
				return RestResponse.errorResponse("No Issue Found with id " + noteId);
			}

			NoteTo1 note = new NoteTo1();
			note.setNoteId(noteId);
			note.setIsSigned(casemgmtNote.isSigned());
			note.setRevision(casemgmtNote.getRevision());
			note.setUpdateDate(casemgmtNote.getUpdate_date());
			note.setProviderName(casemgmtNote.getProviderName());
			note.setProviderNo(casemgmtNote.getProviderNo());
			note.setStatus(casemgmtNote.getStatus());
			note.setProgramName(casemgmtNote.getProgramName());
			note.setRoleName(casemgmtNote.getRoleName());
			note.setUuid(casemgmtNote.getUuid());
			note.setHasHistory(casemgmtNote.getHasHistory());
			note.setLocked(casemgmtNote.isLocked());
			note.setNote(casemgmtNote.getNote());
			note.setRxAnnotation(casemgmtNote.isRxAnnotation());
			note.setEncounterType(casemgmtNote.getEncounter_type());
			//note.setEditorNames(casemgmtNote.getEditors());
			//note.setIssueDescriptions(casemgmtNote.get);
			note.setPosition(casemgmtNote.getPosition());
			//note.getIssueDescriptions(casemgmtNote.getIssues());
			note.setAppointmentNo(casemgmtNote.getAppointmentNo());
			// note.setIssues(casemgmtNote.getIssues());


			//get all note extra values
			List<CaseManagementNoteExt> extNoteList = caseManagementMgr.getExtByNote(Long.valueOf(noteId));

			NoteExtTo1 noteExt = new NoteExtTo1();
			noteExt.setNoteId(Long.valueOf(noteId));

			copyToNoteExtTo1(extNoteList, noteExt);

			//assigned issues..remove the CPP one.
			List<CaseManagementIssue> rawCmeIssues = new ArrayList<CaseManagementIssue>(casemgmtNote.getIssues());
			List<CaseManagementIssue> cmeIssues = new ArrayList<CaseManagementIssue>();

			for(CaseManagementIssue cmei : rawCmeIssues)
			{
				if(!cmei.getIssue().getType().equalsIgnoreCase("system"))
				{
					cmeIssues.add(cmei);
				}
			}

			//set NoteIssue to return
			NoteIssueTo1 noteIssue = new NoteIssueTo1();
			noteIssue.setEncounterNote(note);
			noteIssue.setGroupNoteExt(noteExt);
			noteIssue.setAssignedCMIssues(new CaseManagementIssueConverter().getAllAsTransferObjects(getLoggedInInfo(), cmeIssues));

			return RestResponse.successResponse(noteIssue);
		}
		catch(Exception e) {
			logger.error("Error", e);
			return RestResponse.errorResponse("Error Retrieving Issue Note");
		}
	}
	
	@GET
	@Path("/getGroupNoteExt/{noteId}")	
	@Produces("application/json")
	public NoteExtTo1 getGroupNoteExt(@PathParam("noteId") Long noteId){
		
		List<CaseManagementNoteExt> extNoteList = new ArrayList<CaseManagementNoteExt>();
		extNoteList.addAll(caseManagementMgr.getExtByNote(noteId));

		NoteExtTo1 noteExt = new NoteExtTo1();
		noteExt.setNoteId(noteId);

		copyToNoteExtTo1(extNoteList, noteExt);
		return noteExt;
	}
	
	//TODO-legacy
	@GET
	@Path("/getIssueId/{issueCode}")	
	@Produces("application/json")
	public IssueTo1 getIssueId(@PathParam("issueCode") String issueCode){
		
		//translate summary codes
		issueCode = translateSystemCode(issueCode);
		Issue issues = caseManagementMgr.getIssueInfoByCode(issueCode);
		
		IssueTo1 issueId = new IssueTo1();
		issueId.setId(issues.getId());
		
		return issueId;
	}
	
	@POST
	@Path("/getIssueById/{issueId}")	
	@Produces("application/json")
	public IssueTo1 getIssueId(@PathParam("issueId") int issueId){
		
		Issue issue = caseManagementMgr.getIssue(String.valueOf(issueId));
		
		IssueTo1 issueTo = new IssueConverter().getAsTransferObject(getLoggedInInfo(), issue);
		
		return issueTo;
	}

	@GET
	@Path("/ticklerGetNote/{ticklerNo}")
	@Produces("application/json")
	//{"ticklerNote":{"editor":"oscardoc, doctor","note":"note 2","noteId":6,"observationDate":"2014-09-13T13:18:41-04:00","revision":2}}
	public TicklerNoteResponse ticklerGetNote(@PathParam("ticklerNo") Integer ticklerNo ){

		if(!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_tickler", "r", null)) {
			throw new RuntimeException("Access Denied");
		}
		if(!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_eChart", "r", null)) {
			throw new RuntimeException("Access Denied");
		}
		
		TicklerNoteResponse response = new TicklerNoteResponse();
		CaseManagementNoteLink link = caseManagementMgr.getLatestLinkByTableId(CaseManagementNoteLink.TICKLER, Long.valueOf(ticklerNo));
		
		if(link != null) {
			Long noteId = link.getNoteId();
			
			CaseManagementNote note = caseManagementMgr.getNote(noteId.toString());
			
			if(note != null) {
				TicklerNoteTo1 tNote = new TicklerNoteTo1();
				tNote.setNoteId(note.getId().intValue());
				tNote.setNote(note.getNote());
				tNote.setRevision(note.getRevision());
				tNote.setObservationDate(note.getObservation_date());
				tNote.setEditor(providerMgr.getProvider(note.getProviderNo()).getFormattedName());
				response.setTicklerNote(tNote);
			}
		}
		return response;
	}
	
	@POST
	@Path("/ticklerSaveNote")
	@Produces("application/json")
	@Consumes("application/json")
	@Hidden
	public GenericRESTResponse ticklerSaveNote(JSONObject json){
		
		if(!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_tickler", "w", null)) {
			throw new RuntimeException("Access Denied");
		}
		if(!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_eChart", "w", null)) {
			throw new RuntimeException("Access Denied");
		}

		logger.info("The config "+json.toString());
		
		String strNote = json.getString("note");
		Integer noteId = json.getInt("noteId");
		
		logger.info("want to save note id " + noteId + " with value " + strNote);
		
		JSONObject tickler = json.getJSONObject("tickler");
		Integer ticklerId = tickler.getInt("id");
		Integer demographicNo = tickler.getInt("demographicNo");
		
		logger.info("tickler id " + ticklerId + ", demographicNo " + demographicNo);
		
		Date creationDate = new Date();
		LoggedInInfo loggedInInfo=this.getLoggedInInfo();
		Provider loggedInProvider = loggedInInfo.getLoggedInProvider();

		String revision = "1";
		String history = strNote;
		String uuid = null;
		
		if(noteId != null  && noteId.intValue()>0) {
			CaseManagementNote existingNote = caseManagementMgr.getNote(String.valueOf(noteId));
			
			revision = String.valueOf(Integer.valueOf(existingNote.getRevision()).intValue() + 1);
			history = strNote + "\n" + existingNote.getHistory();
			uuid = existingNote.getUuid();
		}
		
		CaseManagementNote cmn = new CaseManagementNote();
		cmn.setAppointmentNo(0);
		cmn.setArchived(false);
		cmn.setCreate_date(creationDate);
		cmn.setDemographic_no(String.valueOf(demographicNo));
		cmn.setEncounter_type(EncounterUtil.EncounterType.FACE_TO_FACE_WITH_CLIENT.getOldDbValue());
		cmn.setNote(strNote);
		cmn.setObservation_date(creationDate);
		
		cmn.setProviderNo(loggedInProvider.getProviderNo());
		cmn.setRevision(revision);
		cmn.setSigned(true);
		cmn.setSigning_provider_no(loggedInProvider.getProviderNo());
		cmn.setUpdate_date(creationDate);
		cmn.setHistory(history);
		//just doing this because the other code does it.
		cmn.setReporter_program_team("null");
		cmn.setUuid(uuid);
		
		
		ProgramProvider pp = programManager2.getCurrentProgramInDomain(getLoggedInInfo(),getLoggedInInfo().getLoggedInProviderNo());
		if(pp != null) {
			cmn.setProgram_no(String.valueOf(pp.getProgramId()));
		} else {
			List<ProgramProvider> ppList = programManager2.getProgramDomain(getLoggedInInfo(),getLoggedInInfo().getLoggedInProviderNo());
			if(ppList != null && ppList.size()>0) {
				cmn.setProgram_no(String.valueOf(ppList.get(0).getProgramId()));
			}
			
		}
		
		//weird place for it , but for now.
		CaseManagementEntryAction.determineNoteRole(cmn,loggedInProvider.getProviderNo(),String.valueOf(demographicNo));
		
		caseManagementMgr.saveNoteSimple(cmn);

		logger.info("note id is " + cmn.getId());
		
		
		//save link, so we know what tickler this note is linked to
		CaseManagementNoteLink link = new CaseManagementNoteLink();
		link.setNoteId(cmn.getId());
		link.setTableId(ticklerId.longValue());
		link.setTableName(CaseManagementNoteLink.TICKLER);
		
		CaseManagementNoteLinkDAO caseManagementNoteLinkDao = (CaseManagementNoteLinkDAO) SpringUtils.getBean("CaseManagementNoteLinkDAO");
		caseManagementNoteLinkDao.save(link);
		
		
		
		Issue issue = this.issueDao.findIssueByTypeAndCode("system", SUMMARY_CODE_TICKLER_NOTE);
		if(issue == null) {
			logger.warn("missing TicklerNote issue, please run all database updates");
			return null;
		}
		
		CaseManagementIssue cmi = caseManagementMgr.getIssueById(demographicNo.toString(), issue.getId().toString());
		
		if(cmi == null) {
		//save issue..this will make it a "cpp looking" issue in the eChart
			cmi = new CaseManagementIssue();
			cmi.setAcute(false);
			cmi.setCertain(false);
			cmi.setDemographic_no(String.valueOf(demographicNo));
			cmi.setIssue_id(issue.getId());
			cmi.setMajor(false);
			cmi.setProgram_id(Integer.parseInt(cmn.getProgram_no()));
			cmi.setResolved(false);
			cmi.setType(issue.getRole());
			cmi.setUpdate_date(creationDate);
			
			caseManagementMgr.saveCaseIssue(cmi);
			
		}

		cmn.getIssues().add(cmi);
		caseManagementMgr.updateNote(cmn);
		
		 
		
		return new GenericRESTResponse();
	}

	
	@POST
	@Path("/searchIssues")
	@Produces("application/json")
	@Consumes("application/json")
	@Hidden
	public AbstractSearchResponse<IssueTo1> search(JSONObject json,@QueryParam("startIndex") Integer startIndex,@QueryParam("itemsToReturn") Integer itemsToReturn ) {
		AbstractSearchResponse<IssueTo1> response = new AbstractSearchResponse<IssueTo1>();
		
		//if(!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_demographic", "r", null)) {
		//	throw new RuntimeException("Access Denied");
		//}
		
		String term = json.getString("term");
		
		if(json.getString("term").length() >= 1) {

			// Program used to be set by the user on the front end using a button on the navbar. As of now this feature has been disabled indefinitely, thus, "pp" will be null
			// We are keeping this functionality in the event that we decide to add back the ability for users to select a program.
			ProgramProvider pp = programManager2.getCurrentProgramInDomain(getLoggedInInfo(), getLoggedInInfo().getLoggedInProviderNo());
			String programId = null;
		
			if(pp !=null && pp.getProgramId() != null){
				programId = String.valueOf(pp.getProgramId());
			}else{
				programId = String.valueOf(programMgr.getProgramIdByProgramName("OSCAR")); //Default to the oscar program if provider hasn't been assigned to a program
			}

			CaseManagementManager caseManagementManager = SpringUtils.getBean(CaseManagementManager.class);

			//change to get count, and get the slice
			Integer issuesCount = caseManagementManager.searchIssuesCount(getLoggedInInfo().getLoggedInProviderNo(), programId, term);
			
			List<Issue> issues = caseManagementManager.searchIssues(getLoggedInInfo().getLoggedInProviderNo(), programId, term, startIndex, itemsToReturn);
						
			List<IssueTo1> results = new IssueConverter().getAllAsTransferObjects(getLoggedInInfo(), issues);
			
			response.setContent(results);
			response.setTotal(issuesCount);
		}
				
		
		return response;
	}
	
	
	@POST
	@Path("/setEditingNoteFlag")
	@Produces("application/json")
	public GenericRESTResponse setEditingNoteFlag(@QueryParam("noteUUID") String noteUUID, @QueryParam("userId") String providerNo) {
		GenericRESTResponse resp = new GenericRESTResponse(false, "Parameter error");
		if (noteUUID==null || noteUUID.trim().isEmpty() || providerNo==null || providerNo.trim().isEmpty()) return resp;
		
		ConcurrentHashMap<String, Long> noteList = editList.get(noteUUID);
		if (noteList==null) {
			noteList = new ConcurrentHashMap<String, Long>();
			editList.put(noteUUID, noteList);
		}
		clearDanglingFlags();
		
		resp.setSuccess(true);
		resp.setMessage(null);
		
		if (!noteList.containsKey(providerNo)) { // only check for other editing user when initializing flag
			for (String key : noteList.keySet()) {
				if (key!=providerNo) {
					resp.setSuccess(false);
					break;
				}
			}
		}
		noteList.put(providerNo, new Date().getTime());
		editList.put(noteUUID, noteList);
		return resp;
	}
	
	private void clearDanglingFlags() {
		long now = new Date().getTime();
		String[] noteUUIDs = editList.keySet().toArray(new String[editList.keySet().size()]);
		for (String uuid : noteUUIDs) {
			ConcurrentHashMap<String, Long> noteList = editList.get(uuid);
			String[] providerNos = noteList.keySet().toArray(new String[noteList.keySet().size()]);
			for (String providerNo : providerNos) {
				Long editTime = noteList.get(providerNo);
				if (now-editTime>=360000) noteList.remove(providerNo); //remove flag due 6 min (should be renewed/removed within 5 min)
			}
			if (noteList.isEmpty()) editList.remove(uuid);
			else editList.put(uuid, noteList);
		}
	}
	

	@POST
	@Path("/checkEditNoteNew")
	@Produces("application/json")
	public GenericRESTResponse checkEditNoteNew(@QueryParam("noteUUID") String noteUUID, @QueryParam("userId") String providerNo) {
		GenericRESTResponse resp = new GenericRESTResponse(true, null);
		if (noteUUID==null || noteUUID.trim().isEmpty() || providerNo==null || providerNo.trim().isEmpty()) return resp;
		
		ConcurrentHashMap<String, Long> noteList = editList.get(noteUUID);
		if (noteList==null) return resp;
		if (noteList.size()==1 && noteList.containsKey(providerNo)) return resp;
		
		long myEditTime = 0;
		if (noteList.containsKey(providerNo)) myEditTime = noteList.get(providerNo);
		for (String key : noteList.keySet()) {
			if (key!=providerNo) {
				if (noteList.get(key)>myEditTime) {
					resp.setSuccess(false);
					break;
				}
			}
		}
		return resp;  //true = no new edit, false = warn about new edit
	}

	@POST
	@Path("/removeEditingNoteFlag")
	public void removeEditingNoteFlag(@QueryParam("noteUUID") String noteUUID, @QueryParam("userId") String providerNo) {
		if (noteUUID==null || noteUUID.trim().isEmpty() || providerNo==null || providerNo.trim().isEmpty()) return;
		
		ConcurrentHashMap<String, Long> noteList = editList.get(noteUUID);
		if (noteList!=null && noteList.containsKey(providerNo)) noteList.remove(providerNo);
		if (noteList==null || noteList.isEmpty()) editList.remove(noteUUID);
		else editList.put(noteUUID, noteList);
	}

	private static BiMap<String, String> getSummaryCodeToSystemCodeBiMap()
	{
		BiMap<String, String> lookupMap = HashBiMap.create();

		lookupMap.put(SUMMARY_CODE_ONGOING_CONCERNS, SYSTEM_CODE_ONGOING_CONCERNS);
		lookupMap.put(SUMMARY_CODE_MEDICAL_HISTORY, SYSTEM_CODE_MEDICAL_HISTORY);
		lookupMap.put(SUMMARY_CODE_REMINDERS, SYSTEM_CODE_REMINDERS);
		lookupMap.put(SUMMARY_CODE_OTHER_MEDS, SYSTEM_CODE_OTHER_MEDS);
		lookupMap.put(SUMMARY_CODE_SOCIAL_HISTORY, SYSTEM_CODE_SOCIAL_HISTORY);
		lookupMap.put(SUMMARY_CODE_FAMILY_HISTORY, SYSTEM_CODE_FAMILY_HISTORY);
		lookupMap.put(SUMMARY_CODE_RISK_FACTORS, SYSTEM_CODE_RISK_FACTORS);

		return lookupMap;
	}

	public static String getSummaryCodeFromSystemCode(String systemCode)
	{
		BiMap<String, String> lookupMap = getSummaryCodeToSystemCodeBiMap().inverse();

		if (lookupMap.containsKey(systemCode))
		{
			return lookupMap.get(systemCode);
		}

		return systemCode;
	}

	private String translateSystemCode(String summaryCode)
	{
		BiMap<String, String> lookupMap = getSummaryCodeToSystemCodeBiMap();

		if (lookupMap.containsKey(summaryCode))
		{
			return lookupMap.get(summaryCode);
		}

		return summaryCode;
		/*
		switch(summaryCode) {
			case "ongoingconcerns": return "Concerns";
			case "medhx": return "MedHistory";
			case "reminders": return "Reminders";
			case "othermeds": return "OMeds";
			case "sochx": return "SocHistory";
			case "famhx": return "FamHistory";
			case "riskfactors": return "RiskFactors";
			default: return summaryCode;
		}
		 */
	}


	// refactor this to converter
	private void copyToNoteExtTo1(List<CaseManagementNoteExt> extNoteList, NoteExtTo1 noteExt)
	{
		if(extNoteList == null)
		{
			return;
		}

		for(CaseManagementNoteExt extNote : extNoteList)
		{
			logger.debug("NOTE EXT KEY:" + extNote.getKeyVal() + extNote.getValue());

			if(extNote.getKeyVal().equals(CaseManagementNoteExt.STARTDATE))
			{
				Integer id = Math.toIntExact(extNote.getId());
				org.oscarehr.dataMigration.model.common.PartialDate partialDate = getExtPartialDate(id, extNote.getDateValue(), PartialDate.FIELD_CASEMGMT_NOTE_EXT_VALUE);
				noteExt.setStartDate(partialDate);
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.RESOLUTIONDATE))
			{
				Integer id = Math.toIntExact(extNote.getId());
				org.oscarehr.dataMigration.model.common.PartialDate partialDate = getExtPartialDate(id, extNote.getDateValue(), PartialDate.FIELD_CASEMGMT_NOTE_EXT_VALUE);
				noteExt.setResolutionDate(partialDate);
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.PROCEDUREDATE))
			{
				Integer id = Math.toIntExact(extNote.getId());
				org.oscarehr.dataMigration.model.common.PartialDate partialDate = getExtPartialDate(id, extNote.getDateValue(), PartialDate.FIELD_CASEMGMT_NOTE_EXT_VALUE);
				noteExt.setProcedureDate(partialDate);
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.AGEATONSET))
			{
				noteExt.setAgeAtOnset(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.TREATMENT))
			{
				noteExt.setTreatment(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.PROBLEMSTATUS))
			{
				noteExt.setProblemStatus(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.EXPOSUREDETAIL))
			{
				noteExt.setExposureDetail(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.RELATIONSHIP))
			{
				noteExt.setRelationship(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.LIFESTAGE))
			{
				noteExt.setLifeStage(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.HIDECPP))
			{
				noteExt.setHideCpp(extNote.getValue());
			}
			else if(extNote.getKeyVal().equals(CaseManagementNoteExt.PROBLEMDESC))
			{
				noteExt.setProblemDesc(extNote.getValue());
			}
		}
	}

	/**
	 * Given a list of CaseManagementIssueTo1, returns a list of corresponding CaseManagementIssue objects.
	 * This method looks for existing table entries for each list item, and if found includes the existing entry. Otherwise creates a new one.
	 * @param assignedIssueList list of CaseManagementIssueTo1
	 * @param demographicNoStr demographic number
	 * @param providerNo provider number
	 * @return returns a list of CaseManagementIssue
	 */
	private List<CaseManagementIssue> toAssignedCaseManagementIssueList(List<CaseManagementIssueTo1> assignedIssueList, String demographicNoStr, String providerNo) {
				/* Save assigned issues & link with the note */
		List<CaseManagementIssue> issuelist = new ArrayList<CaseManagementIssue>();
		for(CaseManagementIssueTo1 i:assignedIssueList) {
			CaseManagementIssue cmi = caseManagementMgr.getIssueByIssueCode(demographicNoStr, i.getIssue().getCode());
			if(cmi == null) {
				//new cmi
				cmi = new CaseManagementIssue();
				Issue is = issueDao.getIssue(i.getIssue_id());
				cmi.setIssue_id(is.getId());
				cmi.setIssue(is);
				cmi.setProgram_id(getProgramId(getLoggedInInfo(), providerNo));
				cmi.setType(is.getRole());
				cmi.setDemographic_no(demographicNoStr);

				// Only set properties with new notes
				cmi.setAcute(i.isAcute());
				cmi.setCertain(i.isCertain());
				cmi.setMajor(i.isMajor());
				cmi.setResolved(i.isResolved());
			}
			cmi.setUpdate_date(new Date());

			issuelist.add(cmi);
		}
		return issuelist;
	}

	private String getProgram(LoggedInInfo loggedInInfo,String providerNo){
		ProgramProvider pp = programManager2.getCurrentProgramInDomain(loggedInInfo,providerNo);
		String programId = null;

		if(pp !=null && pp.getProgramId() != null){
			programId = ""+pp.getProgramId();
		}else{
			programId = String.valueOf(programMgr.getProgramIdByProgramName("OSCAR")); //Default to the oscar program if provider hasn't been assigned to a program
		}
		return programId;
	}
	private Integer getProgramId(LoggedInInfo loggedInInfo,String providerNo) {
		return Integer.parseInt(getProgram(loggedInInfo, providerNo));
	}

	private Document getDocumentByNoteId(Long noteId)
	{
		Document linkedDoc = null;
		CaseManagementNoteLink link = caseManagementMgr.getLatestLinkByNote(noteId);
		if(link != null && CaseManagementNoteLink.DOCUMENT.equals(link.getTableName()))
		{
			long documentId = link.getTableId();
			linkedDoc = documentDao.find((int)documentId);
		}
		if(linkedDoc == null)
		{
			logger.error("Invalid or missing document link for note: " + noteId);
		}
		return linkedDoc;
	}

	private org.oscarehr.dataMigration.model.common.PartialDate getExtPartialDate(Integer noteId, Date fullDateValue, Integer partialDateType)
	{
		org.oscarehr.common.model.PartialDate partialDateEntity = partialDateDao.getPartialDate(PartialDate.TABLE_CASEMGMT_NOTE_EXT,
				noteId,
				partialDateType);

		return org.oscarehr.dataMigration.model.common.PartialDate.from(ConversionUtils.toNullableLocalDate(fullDateValue), partialDateEntity);
	}
}
