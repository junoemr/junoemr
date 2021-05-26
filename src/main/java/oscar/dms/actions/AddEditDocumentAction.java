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


package oscar.dms.actions;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.DocumentStorageDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.model.DocumentStorage;
import org.oscarehr.common.model.Provider;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.MyDateFormat;
import oscar.dms.EDocUtil;
import oscar.dms.data.AddEditDocumentForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddEditDocumentAction extends DispatchAction {

	private static Logger logger = MiscUtils.getLogger();

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);
	private static final EncounterNoteService encounterNoteService = SpringUtils.getBean(EncounterNoteService.class);
	private static final DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
	private static final ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);

	public ActionForward html5MultiUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		
		AddEditDocumentForm fm = (AddEditDocumentForm) form;

		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_CREATE);

		FormFile docFile = fm.getFiledata();
		String fileName = docFile.getFileName();
		String user = (String) request.getSession().getAttribute("user");
		Integer programId = null;

		if (docFile.getFileSize() == 0) {
			//errors.put("uploaderror", "dms.error.uploadError");
			response.setHeader("oscar_error",props.getString("dms.addDocument.errorZeroSize") );
			response.sendError(500,props.getString("dms.addDocument.errorZeroSize") );
			return null;
		}

		// if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null) {
			programId = pp.getProgramId().intValue();
		}

		Document document = new Document();
		document.setPublic1(false);
		document.setResponsible(user);
		document.setDocCreator(user);
		document.setDocdesc("");
		document.setDoctype("");
		document.setDocfilename(fileName);
		document.setSource(fm.getSource());
		document.setObservationdate(new Date());
		document.setProgramId(programId);

		document = documentService.uploadNewDemographicDocument(document, docFile.getInputStream());

		LogAction.addLogEntry(user, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(document.getDocumentNo()), request.getRemoteAddr(), fileName);
		String providerId = request.getParameter("provider");

		if (providerId != null) // TODO-legacy: THIS NEEDS TO RUN THRU THE lab forwarding rules!
		{
			documentService.routeToProviderInbox(document.getDocumentNo(), providerId);
		}
		// add to queuelinkdocument
		String queueId = request.getParameter("queue");

		if (queueId != null && !queueId.equals("-1")) {
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) ctx.getBean("queueDocumentLinkDAO");
			Integer qid = Integer.parseInt(queueId.trim());
			queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, document.getDocumentNo());
			request.getSession().setAttribute("preferredQueue", queueId);
		}
		
		return null;
	}

	public ActionForward fastUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AddEditDocumentForm fm = (AddEditDocumentForm) form;

		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_CREATE);

		FormFile docFile = fm.getDocFile();
		String fileName = docFile.getFileName();
		HashMap<String, String> errors = new HashMap<>();
		String user = (String) request.getSession().getAttribute("user");

		Integer programId = null;
		
        // if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null) {
			programId = pp.getProgramId().intValue();
		}
		
		// save local file;
		if (docFile.getFileSize() == 0) {
			errors.put("uploaderror", "dms.error.uploadError");
			throw new FileNotFoundException();
		}

		Document document = new Document();
		document.setPublic1(false);
		document.setResponsible(user);
		document.setDocCreator(user);
		document.setDocdesc("");
		document.setDoctype("");
		document.setDocfilename(fileName);
		document.setSource(fm.getSource());
		document.setObservationdate(new Date());
		document.setProgramId(programId);
		document.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));

		documentService.uploadNewDemographicDocument(document, docFile.getInputStream());

		return mapping.findForward("fastUploadSuccess");
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return execute2(mapping, form, request, response);
	}

	public ActionForward execute2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		AddEditDocumentForm fm = (AddEditDocumentForm) form;

		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_CREATE);
		
		if (fm.getMode().equals("") && fm.getFunction().equals("") && fm.getFunctionId().equals("")) {
			// file size exceeds the upload limit
			HashMap<String, String> errors = new HashMap<String, String>();
			errors.put("uploaderror", "dms.error.uploadError");
			request.setAttribute("docerrors", errors);
			request.setAttribute("completedForm", fm);
			request.setAttribute("editDocumentNo", "");
			return mapping.findForward("failEdit");
		}
		else if (fm.getMode().equals("add")) {
			// if add/edit success then send redirect, if failed send a forward (need the formdata and errors hashtables while trying to avoid POSTDATA messages)
			if (addDocument(fm, mapping, request) == true) { // if success
				ActionRedirect redirect = new ActionRedirect(mapping.findForward("successAdd"));
				redirect.addParameter("docerrors", "docerrors"); // Allows the JSP to check if the document was just submitted
				redirect.addParameter("function", request.getParameter("function"));
				redirect.addParameter("functionid", request.getParameter("functionid"));
				redirect.addParameter("curUser", request.getParameter("curUser"));
				redirect.addParameter("appointmentNo",request.getParameter("appointmentNo"));
				String parentAjaxId = request.getParameter("parentAjaxId");
				// if we're called with parent ajax id inform jsp that parent needs to be updated
				if (!parentAjaxId.equals("")) {
					redirect.addParameter("parentAjaxId", parentAjaxId);
					redirect.addParameter("updateParent", "true");
				}
				return redirect;
			} else {
				request.setAttribute("function", request.getParameter("function"));
				request.setAttribute("functionid", request.getParameter("functionid"));
				request.setAttribute("parentAjaxId", request.getParameter("parentAjaxId"));
				request.setAttribute("curUser", request.getParameter("curUser"));
				request.setAttribute("appointmentNo",request.getParameter("appointmentNo"));
				return mapping.findForward("failAdd");
			}
		}
		else {
			ActionForward forward = editDocument(fm, mapping, request);
			return forward;
		}
	}

	/** 
	 * Add a new Document
	 * @param fm
	 * @param mapping
	 * @param request
	 * @return true if successful, false otherwise */
	private boolean addDocument(AddEditDocumentForm fm, ActionMapping mapping, HttpServletRequest request) {

		HashMap<String, String> errors = new HashMap<>();
		boolean documentValid = true;
		try {
			if ((fm.getDocDesc().length() == 0) || (fm.getDocDesc().equals("Enter Title"))) {
				errors.put("descmissing", "dms.error.descriptionInvalid");
				documentValid = false;
			}
			if (fm.getDocType().length() == 0) {
				errors.put("typemissing", "dms.error.typeMissing");
				documentValid = false;
			}
			FormFile docFile = fm.getDocFile();
			if (docFile.getFileSize() == 0) {
				errors.put("uploaderror", "dms.error.uploadError");
				documentValid = false;
			}
			
			if(documentValid) {
				// original file name
				Document document = new Document();
				String fileName1 = docFile.getFileName();

				// if the document was added in the context of a program
				ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
				LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
				ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
				if(pp != null && pp.getProgramId() != null) {
					document.setProgramId(pp.getProgramId().intValue());
				}
				String restrictToProgramStr = request.getParameter("restrictToProgram");
				document.setRestrictToProgram("on".equals(restrictToProgramStr));
				
				// if the document was added in the context of an appointment
				if(fm.getAppointmentNo() != null && fm.getAppointmentNo().length()>0) {
					document.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));
				}
				
			 	// If a new document type is added, include it in the database to create filters
			 	if (!EDocUtil.getDoctypes(fm.getFunction()).contains(fm.getDocType())){
			 		EDocUtil.addDocTypeSQL(fm.getDocType(),fm.getFunction());
			 	}

				boolean isPublicDoc = ("1".equals(fm.getDocPublic()) || "checked".equalsIgnoreCase(fm.getDocPublic()));
				document.setPublic1(isPublicDoc);
				document.setResponsible(fm.getResponsibleId());
				document.setDocCreator(fm.getDocCreator());
				document.setDocdesc(fm.getDocDesc());
				document.setDoctype(fm.getDocType());
				document.setDocfilename(fileName1);
				document.setSource(fm.getSource());
				document.setDocClass(fm.getDocClass());
				document.setDocSubClass(fm.getDocSubClass());
				document.setObservationdate(ConversionUtils.fromDateString(fm.getObservationDate(), "yyyy/MM/dd"));

				String module = fm.getFunction().trim();
				Integer moduleId = Integer.parseInt(fm.getFunctionId().trim());
				Integer demoNo = null;

				if(module.equalsIgnoreCase(CtlDocument.MODULE_PROVIDER))
				{
					document = documentService.uploadNewProviderDocument(document, docFile.getInputStream(), moduleId);
				}
				else
				{
					demoNo = moduleId;
					document = documentService.uploadNewDemographicDocument(document, docFile.getInputStream(), demoNo);
				}

				Integer documentNo = document.getDocumentNo();
				String userId = (String) request.getSession().getAttribute("user");
				LogAction.addLogEntry(userId, demoNo, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT,
						LogConst.STATUS_SUCCESS, String.valueOf(documentNo), request.getRemoteAddr(), fileName1);

				// add note if document is added under a patient
				if (module.equals(CtlDocument.MODULE_DEMOGRAPHIC))
				{
					// doc is uploaded under a patient,moduleId become demo no.
					ProviderData systemProvider = providerDao.find(ProviderData.SYSTEM_PROVIDER_NO);
	
					Date now = EDocUtil.getDmsDateTimeAsDate();
					String docDesc = document.getDocdesc();
	
					CaseManagementNote cmn = new CaseManagementNote();
					cmn.setUpdateDate(now);
					java.sql.Date od1 = MyDateFormat.getSysDate(ConversionUtils.toDateString(document.getObservationdate()));
					cmn.setObservationDate(od1);
					cmn.setDemographic(demographicDao.find(demoNo));
					String prog_no = new EctProgram(request.getSession()).getProgram(userId);
					cmn.setProvider(systemProvider);// set the provider no to be -1 so the editor appear as 'System'.
	
					Provider provider = EDocUtil.getProvider(fm.getDocCreator());
					String provFirstName = "";
					String provLastName = "";
					if(provider!=null) {
						provFirstName=provider.getFirstName();
						provLastName=provider.getLastName();
					}
	
					String strNote = "Document" + " " + docDesc + " " + "created at " + now + " by " + provFirstName + " " + provLastName + ".";
	
					cmn.setNote(strNote);
					cmn.setSigned(true);
					cmn.setSigningProvider(systemProvider);
					cmn.setProgramNo(prog_no);
					cmn.setHistory(strNote);

					request.setAttribute("document_no", documentNo);
					CaseManagementNote savedNote = encounterNoteService.saveDocumentNote(cmn, document);
					MiscUtils.getLogger().info("Document Note ID: " + savedNote.getId().toString());
				}
			}
		} 
		catch (Exception e) {
			MiscUtils.getLogger().error("Error Adding Document", e);
			errors.put("uploaderror", "dms.error.uploadError");
			documentValid = false;
		}
		if(!documentValid) {
			// ActionRedirect redirect = new ActionRedirect(mapping.findForward("failAdd"));
			request.setAttribute("docerrors", errors);
			request.setAttribute("completedForm", fm);
		}
		return documentValid;
	}

	private ActionForward editDocument(AddEditDocumentForm fm, ActionMapping mapping, HttpServletRequest request)
	{
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_UPDATE);

		HashMap<String, String> errors = new HashMap<>();
		boolean documentValid = true;

		try {
			if (fm.getDocDesc().length() == 0) {
				errors.put("descmissing", "dms.error.descriptionInvalid");
				documentValid = false;
			}
			if (fm.getDocType().length() == 0) {
				errors.put("typemissing", "dms.error.typeMissing");
				documentValid = false;
			}
			if(documentValid) {

				FormFile docFile = fm.getDocFile();
				Integer demographicNo = "demographic".equals(fm.getFunction()) ? Integer.parseInt(fm.getFunctionId()) : null;
				String providerNoStr = (String) request.getSession().getAttribute("user");
				Integer documentNo = Integer.parseInt(fm.getMode());
				String programIdStr = (String) request.getSession().getAttribute(SessionConstants.CURRENT_PROGRAM_ID);
				Integer programId = programIdStr != null ? Integer.valueOf(programIdStr) : null;

				documentService.updateDocument(fm, docFile.getInputStream(), docFile.getFileName(), documentNo, demographicNo,
						providerNoStr, request.getRemoteAddr(), programId);
			}
		}
		catch (Exception e) {
			MiscUtils.getLogger().error("Error Editing Document", e);
			errors.put("uploaderror", "dms.error.uploadError");
			documentValid = false;
		}
		if(!documentValid) {
			request.setAttribute("docerrors", errors);
			request.setAttribute("completedForm", fm);
			request.setAttribute("editDocumentNo", fm.getMode());
			return mapping.findForward("failEdit");
		}
		return mapping.findForward("successEdit");
	}

	public static int storeDocumentInDatabase(File file, Integer documentNo){
		Integer ret = 0;
		FileInputStream fin = null;
		try{
			fin=new FileInputStream(file);
			byte fileContents[] = new byte[(int)file.length()];
			fin.read(fileContents);
			DocumentStorage docStor = new DocumentStorage();
			docStor.setFileContents(fileContents);
			docStor.setDocumentNo(documentNo);
			docStor.setUploadDate(new Date());
			DocumentStorageDao documentStorageDao = (DocumentStorageDao) SpringUtils.getBean("documentStorageDao");
			documentStorageDao.persist(docStor);
			ret = docStor.getId();
		}catch(Exception e){
			MiscUtils.getLogger().error("Error putting file in database",e);
		}
		finally
		{
			IOUtils.closeQuietly(fin);
		}
		return ret;
	}
}
