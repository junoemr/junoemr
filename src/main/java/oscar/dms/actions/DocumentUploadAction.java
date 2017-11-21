/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.dms.actions;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.dms.IncomingDocUtil;
import oscar.dms.data.DocumentUploadForm;
import oscar.dms.util.OscarPdfValidator;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DocumentUploadAction extends DispatchAction {

	private static Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public ActionForward executeUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	                                   HttpServletResponse response) throws Exception
	{
		DocumentUploadForm fm = (DocumentUploadForm) form;

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null))
		{
			throw new SecurityException("missing required security object (_edoc)");
		}
		logger.info("BEGIN DOCUMENT UPLOAD");

		HashMap<String, Object> map = new HashMap<String, Object>();
		FormFile docFile = fm.getFiledata();
		String destination = request.getParameter("destination");
		java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
		ResourceBundle props = ResourceBundle.getBundle("oscarResources", locale);

		try
		{
			if(docFile == null)
			{
				map.put("error", 4);
			}
			else if(destination != null && destination.equals("incomingDocs"))
			{
				uploadIncomingDocs(request, props, docFile, map);
			}
			else
			{
				uploadRegularDocument(request, fm.getSource(), docFile, map);
			}
		}
		catch(IOException e)
		{
			logger.error("Document Upload Error", e);
			map.put("error", 4);
		}
		catch(RuntimeException e)
		{
			logger.error("Document Upload Error", e);
			map.put("error", "Document Encoding Error. Please contact support.");
		}

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = JSONObject.fromObject(map);
		jsonArray.add(jsonObject);
		response.getOutputStream().write(jsonArray.toString().getBytes());
		logger.info("DOCUMENT UPLOAD COMPLETE");
		return null;
	}

	/**
	 * Counts the number of pages in a local pdf file.
	 * @param fileName the name of the file
	 * @return the number of pages in the file
	 */
	public int countNumOfPages(String fileName) {
		int numOfPage = 0;
		String documentDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");

		try {
			PDDocument doc = PDDocument.load(new File(documentDir, fileName));
			numOfPage = doc.getNumberOfPages();
		}
		catch (IOException e) {
			logger.error("Error", e);
		}
		return numOfPage;
	}

	private void uploadIncomingDocs(HttpServletRequest request, ResourceBundle props, FormFile docFile, HashMap<String, Object> map) throws IOException
	{
		String fileName = docFile.getFileName();
		if(!fileName.toLowerCase().endsWith(".pdf"))
		{
			map.put("error", props.getString("dms.documentUpload.onlyPdf"));
		}
		else if(docFile.getFileSize() == 0)
		{
			map.put("error", 4);
			throw new FileNotFoundException();
		}
		else
		{

			String queueId = request.getParameter("queue");
			String destFolder = request.getParameter("destFolder");

			File f = new File(IncomingDocUtil.getAndCreateIncomingDocumentFilePathName(queueId, destFolder, fileName));
			if(f.exists())
			{
				map.put("error", fileName + " " + props.getString("dms.documentUpload.alreadyExists"));
			}
			else
			{
				writeToIncomingDocs(docFile, queueId, destFolder, fileName);
				map.put("name", docFile.getFileName());
				map.put("size", docFile.getFileSize());
			}
			request.getSession().setAttribute("preferredQueue", queueId);
			docFile.destroy();
		}
	}

	private void uploadRegularDocument(HttpServletRequest request, String fmSource, FormFile docFile, HashMap<String, Object> map) throws IOException
	{
		int numberOfPages = 0;
		String fileName = docFile.getFileName();
		String user = (String) request.getSession().getAttribute("user");
		EDoc newDoc = new EDoc("", "", fileName, "", user, user, fmSource, 'A',
				oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "", "demographic", "-1", 0);
		newDoc.setDocPublic("0");

		// if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null)
		{
			newDoc.setProgramId(pp.getProgramId().intValue());
		}

		fileName = newDoc.getFileName();
		// save local file;
		if(docFile.getFileSize() == 0)
		{
			throw new FileNotFoundException();
		}

		logger.debug("Write pdf file to server");
		// write file to local dir
		writeLocalFile(docFile, fileName);

		newDoc.setContentType(docFile.getContentType());
		if(fileName.toLowerCase().endsWith(".pdf"))
		{
			logger.info("validate pdf encoding");
			String documentDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
			File f = new File(documentDir, fileName);
			OscarPdfValidator validator = new OscarPdfValidator(f);
			if(!validator.validate())
			{
				logger.error("Pdf Encoding Error: " + validator.getReasonInvalid());
				throw new RuntimeException("Pdf Encoding Error: " + validator.getReasonInvalid());
			}
			newDoc.setContentType("application/pdf");
			// get number of pages when document is a PDF
			numberOfPages = countNumOfPages(fileName);
		}
		newDoc.setNumberOfPages(numberOfPages);
		String doc_no = EDocUtil.addDocumentSQL(newDoc);

		LogAction.addLogEntry((String) request.getSession().getAttribute("user"), null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr(), fileName);

		String providerId = request.getParameter("provider");
		if(providerId != null)
		{
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) ctx.getBean("providerInboxRoutingDAO");
			providerInboxRoutingDao.addToProviderInbox(providerId, Integer.parseInt(doc_no), "DOC");
		}

		String queueId = request.getParameter("queue");
		if(queueId != null && !queueId.equals("-1"))
		{
			WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
			QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) ctx.getBean("queueDocumentLinkDAO");
			Integer qid = Integer.parseInt(queueId.trim());
			Integer did = Integer.parseInt(doc_no.trim());
			queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, did);
			request.getSession().setAttribute("preferredQueue", queueId);
		}

		map.put("name", docFile.getFileName());
		map.put("size", docFile.getFileSize());

		docFile.destroy();
	}


	/**
	 * Writes an uploaded file to disk
	 * @param docFile the uploaded file
	 * @param fileName the name for the file on disk
	 * @throws Exception when an error occurs
	 */
	private void writeLocalFile(FormFile docFile, String fileName) throws IOException {
		InputStream fis = null;
		FileOutputStream fos = null;
		try
		{
			fis = docFile.getInputStream();
			String savePath = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "/" + fileName;
			fos = new FileOutputStream(savePath);
			byte[] buf = new byte[128 * 1024];
			int i = 0;
			while((i = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, i);
			}
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			throw e;
		}
		finally
		{
			if(fis != null)
				fis.close();
			if(fos != null)
				fos.close();
		}
	}

	private void writeToIncomingDocs(FormFile docFile, String queueId, String PdfDir, String fileName) throws IOException
	{
		InputStream fis = null;
		FileOutputStream fos = null;
		try
		{
			fis = docFile.getInputStream();
			String savePath = IncomingDocUtil.getAndCreateIncomingDocumentFilePathName(queueId, PdfDir, fileName);
			fos = new FileOutputStream(savePath);
			IOUtils.copy(fis, fos);
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			throw e;
		}
		finally
		{
			if(fis != null)
			{
				fis.close();
			}
			if(fos != null)
			{
				fos.close();
			}
		}
	}

	public ActionForward setUploadDestination(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	                                          HttpServletResponse response)
	{

		String user_no = (String) request.getSession().getAttribute("user");
		String destination = request.getParameter("destination");
		UserPropertyDAO pref = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");
		UserProperty up = pref.getProp(user_no, UserProperty.UPLOAD_DOCUMENT_DESTINATION);

		if(up == null)
		{
			up = new UserProperty();
			up.setName(UserProperty.UPLOAD_DOCUMENT_DESTINATION);
			up.setProviderNo(user_no);
		}

		if(up.getValue() == null || !(up.getValue().equals(destination)))
		{
			up.setValue(destination);
			pref.saveProp(up);
		}
		return null;
	}

	public ActionForward setUploadIncomingDocumentFolder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
	                                                     HttpServletResponse response)
	{

		String user_no = (String) request.getSession().getAttribute("user");
		String destFolder = request.getParameter("destFolder");
		UserPropertyDAO pref = (UserPropertyDAO) SpringUtils.getBean("UserPropertyDAO");
		UserProperty up = pref.getProp(user_no, UserProperty.UPLOAD_INCOMING_DOCUMENT_FOLDER);

		if(up == null)
		{
			up = new UserProperty();
			up.setName(UserProperty.UPLOAD_INCOMING_DOCUMENT_FOLDER);
			up.setProviderNo(user_no);
		}

		if(up.getValue() == null || !(up.getValue().equals(destFolder)))
		{
			up.setValue(destFolder);
			pref.saveProp(up);
		}
		return null;
	}

	//TODO remove this method
	public boolean checkPdfReadable(String fileName)
	{
		boolean canRead = false;
		String documentDir = oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR");

		ExecutorService executor = Executors.newSingleThreadExecutor();

		File f = new File(documentDir, fileName);
		Future<String> future = executor.submit(new PDFTask(f));

		try
		{
			logger.info("PDF Validation Started..");
			future.get(10, TimeUnit.SECONDS);

			logger.info("PDF Validation Passed!");
			canRead = true;
		}
		catch(InterruptedException | ExecutionException | TimeoutException e)
		{
			future.cancel(true);
			logger.error("PDF Validation Error", e);
			canRead = false;

			if(!future.isDone())
			{
				try
				{
					future.wait();
					future.cancel(true);
				}
				catch(InterruptedException ex)
				{
					logger.error("Thread Hack Kill Thing", e);
				}
			}
		}
		executor.shutdownNow();
		return canRead;
	}

	//TODO remove this too
	private class PDFTask implements Callable<String>
	{
		private File file;
		public PDFTask(File f)
		{
			super();
			this.file=f;
		}
		@Override
		public String call() throws Exception {
			PDDocument doc = PDDocument.load(this.file, MemoryUsageSetting.setupTempFileOnly(100 * 1000000));
			doc.getNumberOfPages();
			return "SUCCESS";
		}
	}
}

