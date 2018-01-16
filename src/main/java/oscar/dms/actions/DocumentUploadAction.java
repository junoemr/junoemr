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
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
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

public class DocumentUploadAction extends DispatchAction
{

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

		HashMap<String, Object> map = new HashMap<>();
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
			map.put("error", 6);
		}
		catch(RuntimeException e)
		{
			logger.error("Document Upload Error", e);
			map.put("error", "Document Error. Please contact support.");
		}

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = JSONObject.fromObject(map);
		jsonArray.add(jsonObject);
		response.getOutputStream().write(jsonArray.toString().getBytes());
		logger.info("DOCUMENT UPLOAD COMPLETE");
		return null;
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

	private void uploadRegularDocument(HttpServletRequest request, String fmSource, FormFile docFile, HashMap<String, Object> map) throws IOException, InterruptedException
	{
		String fileName = docFile.getFileName();
		String user = (String) request.getSession().getAttribute("user");
		if(docFile.getFileSize() == 0)
		{
			throw new FileNotFoundException();
		}
		map.put("name", docFile.getFileName());
		map.put("size", docFile.getFileSize());

		GenericFile file = FileFactory.createDocumentFile(docFile.getInputStream(), fileName);
		docFile.destroy();

		if(!file.validate())
		{
			file.reEncode();
		}
		file.moveToDocuments();

		EDoc newDoc = new EDoc("", "", fileName, "", user, user, fmSource, 'A',
				oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "",
				"demographic", "-1", file.getPageCount());

		newDoc.setDocPublic("0");
		newDoc.setContentType(file.getContentType());
		newDoc.setFileName(file.getName());

		// if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null)
		{
			newDoc.setProgramId(pp.getProgramId().intValue());
		}


		String doc_no = EDocUtil.addDocumentSQL(newDoc);
		LogAction.addLogEntry(user, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr(), fileName);

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
}

