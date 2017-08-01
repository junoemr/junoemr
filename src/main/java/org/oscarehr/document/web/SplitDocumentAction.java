/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.document.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.DocumentDao;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.model.Document;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.oscarLab.ca.all.upload.ProviderLabRouting;

public class SplitDocumentAction extends DispatchAction {
	
	private static Logger logger = Logger.getLogger(SplitDocumentAction.class);

	private DocumentDao documentDao = SpringUtils.getBean(DocumentDao.class);
	private ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) SpringUtils.getBean("providerInboxRoutingDAO");
	private ProviderLabRoutingDao providerLabRoutingDao = (ProviderLabRoutingDao) SpringUtils.getBean("providerLabRoutingDao");
	private PatientLabRoutingDao patientLabRoutingDao = (PatientLabRoutingDao) SpringUtils.getBean("patientLabRoutingDao");
	private QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) SpringUtils.getBean("queueDocumentLinkDAO");

	public ActionForward split(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String docNum = request.getParameter("document");
		String[] commands = request.getParameterValues("page[]");
		String queueId = request.getParameter("queueID");

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		Document doc = documentDao.getDocument(docNum);

		String documentDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");

		String newFilename = doc.getDocfilename();

		PDDocument pdf = null;
		PDDocument newPdf = null;
		
		try {
		
			File inputFile = new File(documentDir, doc.getDocfilename());
			pdf = PDDocument.load(inputFile);
			newPdf = new PDDocument();
			
			if (commands != null) {
				for (String c : commands) {
					String[] command = c.split(",");
					
					int pageNum = Integer.parseInt(command[0]) - 1;//because indexes start at 0
					int rotation = Integer.parseInt(command[1]);

					PDPage page = pdf.getPage(pageNum);
					page.setRotation(rotation);

					newPdf.addPage(page);
				}
			}

			if (newPdf.getNumberOfPages() > 0) {

				/* Save the new pdf */
				EDoc newDoc = new EDoc("", "", newFilename, "", providerNo, doc.getDoccreator(), "", 'A', 
						DateFormatUtils.format(new Date(), "yyyy-MM-dd"), "", "", "demographic", "-1", 0);
				newDoc.setDocPublic("0");
				newDoc.setContentType("application/pdf");
				newDoc.setNumberOfPages(newPdf.getNumberOfPages());

				// Saves the document and adds links in ctl_document
				String newDocNo = EDocUtil.addDocumentSQL(newDoc);

				newPdf.save(documentDir + newDoc.getFileName());
				newPdf.close();

				/* add link in providerInbox */
				List<ProviderInboxItem> routeList = providerInboxRoutingDao.getProvidersWithRoutingForDocument("DOC", Integer.parseInt(docNum));
				for (ProviderInboxItem i : routeList) {
					providerInboxRoutingDao.addToProviderInbox(i.getProviderNo(), Integer.parseInt(newDocNo), "DOC");
				}
				providerInboxRoutingDao.addToProviderInbox(providerNo, Integer.parseInt(newDocNo), "DOC");

				/* add link in document queue */
				Integer qid = (queueId == null || queueId.equalsIgnoreCase("null")) ? 1 : Integer.parseInt(queueId);
				Integer did = Integer.parseInt(newDocNo.trim());
				queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, did);

				List<ProviderLabRoutingModel> result = providerLabRoutingDao.getProviderLabRoutingDocuments(Integer.parseInt(docNum));
				if (!result.isEmpty()) {
					new ProviderLabRouting().route(newDocNo, result.get(0).getProviderNo(), "DOC");
				}

				/* add link in patientLabRouting */
				List<PatientLabRouting> result2 = patientLabRoutingDao.findDocByDemographic(Integer.parseInt(docNum));
				if (!result2.isEmpty()) {
					PatientLabRouting newPatientRoute = new PatientLabRouting();

					newPatientRoute.setDemographicNo(result2.get(0).getDemographicNo());
					newPatientRoute.setLabNo(Integer.parseInt(newDocNo));
					newPatientRoute.setLabType("DOC");

					patientLabRoutingDao.persist(newPatientRoute);
				}
				
				if (result.isEmpty() || result2.isEmpty()) {
					String json = "{newDocNum:" + newDocNo + "}";
					JSONObject jsonObject = JSONObject.fromObject(json);
					response.setContentType("application/json");
					PrintWriter printWriter = response.getWriter();
					printWriter.print(jsonObject);
					printWriter.flush();
					return null;
				}

			}

		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		finally {
			try {
				if (pdf != null) pdf.close();
			}
			catch (IOException e) {
				// do nothing
			}
		}

		return mapping.findForward("success");
	}

	private ActionForward rotate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, int degrees) throws Exception {
		Document doc = documentDao.getDocument(request.getParameter("document"));
		String docDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");

		File pdfFile = new File(docDir, doc.getDocfilename());
		PDDocument pdf = PDDocument.load(pdfFile);
		
		for(int i=0; i<pdf.getNumberOfPages(); i++) {
			PDPage pg = pdf.getPage(i);
			int rotation = pg.getRotation();
			pg.setRotation((rotation + degrees) % 360);

			ManageDocumentAction.deleteCacheVersion(doc, (i+1));
		}
		
		pdf.save(pdfFile);
		pdf.close();
		
		return null;
	}
	public ActionForward rotate180(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return rotate(mapping, form, request, response, 180);
	}

	public ActionForward rotate90(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return rotate(mapping, form, request, response, 90);
	}

	public ActionForward removeFirstPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Document doc = documentDao.getDocument(request.getParameter("document"));

		String docDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		
		File pdfFile = new File(docDir, doc.getDocfilename());
		PDDocument pdf = PDDocument.load(pdfFile);

		// Documents must have at least 2 pages, for the first page to be removed.
		if (pdf.getNumberOfPages() > 1) {
			for(int i=0; i<pdf.getNumberOfPages(); i++) {
				ManageDocumentAction.deleteCacheVersion(doc, (i+1));
			}
			pdf.removePage(0);

			EDocUtil.subtractOnePage(request.getParameter("document"));

			pdf.save(pdfFile);
		}
		pdf.close();
		
		return null;
	}
}