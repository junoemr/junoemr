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
import org.oscarehr.PMmodule.caisi_integrator.ConformanceTestHelper;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.DocumentStorageDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.PDFFile;
import org.oscarehr.common.model.DocumentStorage;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SessionConstants;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.MyDateFormat;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.dms.data.AddEditDocumentForm;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarEncounter.data.EctProgram;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AddEditDocumentAction extends DispatchAction {

	private static Logger logger = MiscUtils.getLogger();

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private org.oscarehr.document.service.Document documentService = SpringUtils.getBean(org.oscarehr.document.service.Document.class);

	public ActionForward html5MultiUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ResourceBundle props = ResourceBundle.getBundle("oscarResources");
		
		AddEditDocumentForm fm = (AddEditDocumentForm) form;

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null)) {
			throw new SecurityException("missing required security object (_edoc)");
		}
		
		FormFile docFile = fm.getFiledata();
		String fileName = docFile.getFileName();
		String user = (String) request.getSession().getAttribute("user");

		GenericFile file = FileFactory.createDocumentFile(docFile.getInputStream(), fileName);
		file.moveToDocuments();

		EDoc newDoc = new EDoc("", "", fileName, "", user, user, fm.getSource(), 'A',
				oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "", "demographic", "-1",
				file.getPageCount());
		newDoc.setDocPublic("0");
		newDoc.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));
		newDoc.setContentType(file.getContentType());
		newDoc.setFileName(file.getName());
		
        // if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null) {
			newDoc.setProgramId(pp.getProgramId().intValue());
		}
		
		fileName = newDoc.getFileName();
		// save local file;
		if (docFile.getFileSize() == 0) {
			//errors.put("uploaderror", "dms.error.uploadError");
			response.setHeader("oscar_error",props.getString("dms.addDocument.errorZeroSize") );
			response.sendError(500,props.getString("dms.addDocument.errorZeroSize") );
			return null;
		}

		String doc_no = EDocUtil.addDocumentSQL(newDoc);
		LogAction.addLogEntry(user, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr(), fileName);
		String providerId = request.getParameter("provider");

		if (providerId != null) { // TODO: THIS NEEDS TO RUN THRU THE lab forwarding rules!
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) ctx.getBean("providerInboxRoutingDAO");
			providerInboxRoutingDao.addToProviderInbox(providerId, Integer.parseInt(doc_no), "DOC");
		}
		// add to queuelinkdocument
		String queueId = request.getParameter("queue");

		if (queueId != null && !queueId.equals("-1")) {
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) ctx.getBean("queueDocumentLinkDAO");
			Integer qid = Integer.parseInt(queueId.trim());
			Integer did = Integer.parseInt(doc_no.trim());
			queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, did);
			request.getSession().setAttribute("preferredQueue", queueId);
		}
		
		return null;
	}

	public ActionForward fastUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		AddEditDocumentForm fm = (AddEditDocumentForm) form;
		
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null)) {
			throw new SecurityException("missing required security object (_edoc)");
		}
		FormFile docFile = fm.getDocFile();
		String fileName = docFile.getFileName();
		HashMap<String, String> errors = new HashMap<String, String>();
		String user = (String) request.getSession().getAttribute("user");

		GenericFile file = FileFactory.createDocumentFile(docFile.getInputStream(), fileName);

		file.moveToDocuments();

		EDoc newDoc = new EDoc("", "", fileName, "", user, user, fm.getSource(), 'A',
				oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "", "demographic", "-1");
		newDoc.setDocPublic("0");
		newDoc.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));
		newDoc.setContentType(file.getContentType());
		newDoc.setNumberOfPages(file.getPageCount());
		
        // if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null) {
			newDoc.setProgramId(pp.getProgramId().intValue());
		}
		
		// save local file;
		if (docFile.getFileSize() == 0) {
			errors.put("uploaderror", "dms.error.uploadError");
			throw new FileNotFoundException();
		}

		EDocUtil.addDocumentSQL(newDoc);

		return mapping.findForward("fastUploadSuccess");
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return execute2(mapping, form, request, response);
	}

	public ActionForward execute2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		AddEditDocumentForm fm = (AddEditDocumentForm) form;
		
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null)) {
			throw new SecurityException("missing required security object (_edoc)");
		}
		
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

		HashMap<String, String> errors = new HashMap<String, String>();
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
				String fileName1 = docFile.getFileName();

				GenericFile file = FileFactory.createDocumentFile(docFile.getInputStream(), fileName1);

				file.moveToDocuments();

				EDoc newDoc = new EDoc(fm.getDocDesc(), fm.getDocType(), fileName1, "", fm.getDocCreator(), fm.getResponsibleId(), fm.getSource(), 'A', fm.getObservationDate(), "", "", fm.getFunction(), fm.getFunctionId());
				newDoc.setDocPublic(fm.getDocPublic());
				newDoc.setDocClass(fm.getDocClass());
				newDoc.setDocSubClass(fm.getDocSubClass());
				newDoc.setContentType(file.getContentType());
				newDoc.setNumberOfPages(file.getPageCount());
				newDoc.setFileName(file.getName());

				MiscUtils.getLogger().info("Content Type:" + newDoc.getContentType());

				// if the document was added in the context of a program
				ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
				LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
				ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
				if(pp != null && pp.getProgramId() != null) {
					newDoc.setProgramId(pp.getProgramId().intValue());
				}
				String restrictToProgramStr = request.getParameter("restrictToProgram");
				newDoc.setRestrictToProgram("on".equals(restrictToProgramStr));
				
				// if the document was added in the context of an appointment
				if(fm.getAppointmentNo() != null && fm.getAppointmentNo().length()>0) {
					newDoc.setAppointmentNo(Integer.parseInt(fm.getAppointmentNo()));
				}
				
			 	// If a new document type is added, include it in the database to create filters
			 	if (!EDocUtil.getDoctypes(fm.getFunction()).contains(fm.getDocType())){
			 		EDocUtil.addDocTypeSQL(fm.getDocType(),fm.getFunction());
			 	}
	
				String doc_no = EDocUtil.addDocumentSQL(newDoc);
				if(ConformanceTestHelper.enableConformanceOnlyTestFeatures){
					storeDocumentInDatabase(file.getFileObject(), Integer.parseInt(doc_no));
				}
				// add note if document is added under a patient
				String module = fm.getFunction().trim();
				String moduleId = fm.getFunctionId().trim();
				
				Integer demoNo = module.equals("demographic") ? Integer.parseInt(moduleId) : null;
				LogAction.addLogEntry((String) request.getSession().getAttribute("user"), demoNo, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr(), fileName1);

				
				if (module.equals("demographic")) {// doc is uploaded under a patient,moduleId become demo no.
	
					Date now = EDocUtil.getDmsDateTimeAsDate();
	
					String docDesc = EDocUtil.getLastDocumentDesc();
	
					CaseManagementNote cmn = new CaseManagementNote();
					cmn.setUpdate_date(now);
					java.sql.Date od1 = MyDateFormat.getSysDate(newDoc.getObservationDate());
					cmn.setObservation_date(od1);
					cmn.setDemographic_no(moduleId);
					HttpSession se = request.getSession();
					String user_no = (String) se.getAttribute("user");
					String prog_no = new EctProgram(se).getProgram(user_no);
					WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(se.getServletContext());
					CaseManagementManager cmm = (CaseManagementManager) ctx.getBean("caseManagementManager");
					cmn.setProviderNo("-1");// set the provider no to be -1 so the editor appear as 'System'.
	
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
					cmn.setSigning_provider_no("-1");
					cmn.setProgram_no(prog_no);
	
					SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
					SecRole doctorRole = secRoleDao.findByName("doctor");
					cmn.setReporter_caisi_role(doctorRole.getId().toString());
	
					cmn.setReporter_program_team("0");
					cmn.setPassword("NULL");
					cmn.setLocked(false);
					cmn.setHistory(strNote);
					cmn.setPosition(0);
	
					Long note_id = cmm.saveNoteSimpleReturnID(cmn);
	
					// Debugging purposes on the live server
					MiscUtils.getLogger().info("Document Note ID: "+note_id.toString());
	
					// Add a noteLink to casemgmt_note_link
					CaseManagementNoteLink cmnl = new CaseManagementNoteLink();
					cmnl.setTableName(CaseManagementNoteLink.DOCUMENT);
					cmnl.setTableId(Long.parseLong(EDocUtil.getLastDocumentNo()));
					cmnl.setNoteId(note_id);
	
					request.setAttribute("document_no", doc_no);
					MiscUtils.getLogger().info(" document no"+doc_no);
	
					EDocUtil.addCaseMgmtNoteLink(cmnl);
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

	private ActionForward editDocument(AddEditDocumentForm fm, ActionMapping mapping, HttpServletRequest request) {
		
		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null)) {
			throw new SecurityException("missing required security object (_edoc)");
		}

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


	private boolean filled(String s) {
		return (s != null && s.trim().length() > 0);
	}
}
