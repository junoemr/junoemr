/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.document.web;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.PatientLabRoutingDao;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.PatientLabRouting;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.oscarLab.ca.all.upload.ProviderLabRouting;
import oscar.oscarLab.ca.on.LabResultData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

public class SplitDocumentAction extends DispatchAction
{
	private static Logger logger = Logger.getLogger(SplitDocumentAction.class);

	private DocumentDao documentDao = SpringUtils.getBean(DocumentDao.class);
	private ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) SpringUtils.getBean("providerInboxRoutingDAO");
	private ProviderLabRoutingDao providerLabRoutingDao = (ProviderLabRoutingDao) SpringUtils.getBean("providerLabRoutingDao");
	private PatientLabRoutingDao patientLabRoutingDao = (PatientLabRoutingDao) SpringUtils.getBean("patientLabRoutingDao");
	private QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) SpringUtils.getBean("queueDocumentLinkDAO");
	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);

	private long maxMemoryUsage = OscarProperties.getInstance().getPDFMaxMemUsage();

	public ActionForward split(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String docNum = request.getParameter("document");
		String[] commands = request.getParameterValues("page[]");
		String queueId = request.getParameter("queueID");

		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		Document existingDocument = documentDao.getDocument(docNum);
		if(existingDocument.hasEncodingError())
		{
			logger.warn("Invalid PDF file found. Edit operation aborted");
			return null;
		}

		PDDocument pdf = null;
		try
		{
			GenericFile existingPdf = FileFactory.getDocumentFile(existingDocument.getDocfilename());

			pdf = PDDocument.load(existingPdf.getFileObject(), MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));
			PDDocument newPdf = new PDDocument();
			
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

			if(newPdf.getNumberOfPages() > 0)
			{
				File tempFile = File.createTempFile("juno-doc-split", ".pdf");
				newPdf.save(tempFile);
				newPdf.close();

				GenericFile newPdfFile = FileFactory.getExistingFile(tempFile);

				Document document = new Document();
				document.setDocdesc("");
				document.setDoctype(existingDocument.getDoctype());
				document.setDocSubClass(existingDocument.getDocSubClass());
				document.setDocfilename(tempFile.getName());
				document.setDocCreator(providerNo);
				document.setResponsible(existingDocument.getDoccreator());
				document.setSource(existingDocument.getSource());
				document.setObservationdate(new Date());
				document.setPublic1(false);

				document = documentService.uploadNewDemographicDocument(document, newPdfFile, null);
				Integer newDocumentNo = document.getDocumentNo();

				/* add link in providerInbox */
				List<ProviderInboxItem> routeList = providerInboxRoutingDao.getProvidersWithRoutingForDocument(LabResultData.DOCUMENT, Integer.parseInt(docNum));
				for (ProviderInboxItem i : routeList) {
					documentService.routeToProviderInbox(newDocumentNo, i.getProviderNo());
				}
				documentService.routeToProviderInbox(newDocumentNo, providerNo);

				/* add link in document queue */
				Integer qid = (queueId == null || queueId.equalsIgnoreCase("null")) ? 1 : Integer.parseInt(queueId);
				queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, newDocumentNo);

				List<ProviderLabRoutingModel> result = providerLabRoutingDao.getProviderLabRoutingDocuments(Integer.parseInt(docNum));
				if (!result.isEmpty()) {
					new ProviderLabRouting().route(newDocumentNo, result.get(0).getProviderNo(), ProviderLabRoutingDao.LAB_TYPE_DOC);
				}

				/* add link in patientLabRouting and ctl_document */
				PatientLabRouting patientLabRoute = patientLabRoutingDao.findSingleDocRoute(Integer.parseInt(docNum));
				if(patientLabRoute != null)
				{
					documentService.assignDocumentToDemographic(document, patientLabRoute.getDemographicNo());
				}

				if(result.isEmpty() || patientLabRoute == null)
				{
					String json = "{newDocNum:" + newDocumentNo + "}";
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
		if(doc.hasEncodingError())
		{
			logger.warn("Invalid PDF file found. Edit operation aborted");
			return null;
		}

		File pdfFile = FileFactory.getDocumentFile(doc.getDocfilename()).getFileObject();
		PDDocument pdf = PDDocument.load(pdfFile, MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));
		
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

	public ActionForward removePage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		int pageNumber = Integer.parseInt(request.getParameter("page"));
		Document doc = documentDao.getDocument(request.getParameter("document"));
		if(doc.hasEncodingError())
		{
			logger.warn("Invalid PDF file found. Edit operation aborted");
			return null;
		}

		File pdfFile = FileFactory.getDocumentFile(doc.getDocfilename()).getFileObject();
		PDDocument pdf = PDDocument.load(pdfFile, MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));

		// Documents must have at least 2 pages for a page to be removed.
		if (pdf.getNumberOfPages() > 1)
		{
			for (int i = 0; i < pdf.getNumberOfPages(); i++)
			{
				ManageDocumentAction.deleteCacheVersion(doc, (i + 1));
			}
			pdf.removePage(pageNumber-1);
			pdf.save(pdfFile);
			doc.setNumberofpages(doc.getNumberofpages() - 1);
			documentDao.merge(doc);
		}
		pdf.close();

		return null;
	}
}