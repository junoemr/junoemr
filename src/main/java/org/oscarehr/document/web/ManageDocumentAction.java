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


package org.oscarehr.document.web;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.caisi_integrator.ws.CachedDemographicDocument;
import org.oscarehr.caisi_integrator.ws.CachedDemographicDocumentContents;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.caisi_integrator.ws.FacilityIdIntegerCompositePk;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.document.dao.CtlDocumentDao;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.service.EncounterNoteService;
import org.oscarehr.inbox.service.InboxManager;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.security.model.Permission;
import org.oscarehr.sharingcenter.SharingCenterUtil;
import org.oscarehr.sharingcenter.model.DemographicExport;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.dms.IncomingDocUtil;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarDemographic.data.DemographicData;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.UtilDateUtilities;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author jaygallagher
 */
public class ManageDocumentAction extends DispatchAction {

	private static Logger logger = MiscUtils.getLogger();

	private DocumentDao documentDao = SpringUtils.getBean(DocumentDao.class);
	private CtlDocumentDao ctlDocumentDao = SpringUtils.getBean(CtlDocumentDao.class);
	private ProviderInboxRoutingDao providerInboxRoutingDAO = SpringUtils.getBean(ProviderInboxRoutingDao.class);
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);
	private final InboxManager inboxManager = SpringUtils.getBean(InboxManager.class);

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		return null;
	}

	public ActionForward documentUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		String observationDate = request.getParameter("observationDate");// :2008-08-22<
		String documentDescription = request.getParameter("documentDescription");// :test2<
		String documentIdStr = request.getParameter("documentId");// :29<
		String docType = request.getParameter("docType");// :consult<
		String providerId = (String) request.getSession().getAttribute("user");
		String demographicNoStr = request.getParameter("demog");
		Integer demographicNo = Integer.parseInt(demographicNoStr);
		String[] flagProviders = request.getParameterValues("flagproviders");

		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_UPDATE);

		try
		{
			Document document = documentDao.getDocument(documentIdStr);
			Integer documentId = document.getDocumentNo();

			document.setDocdesc(documentDescription);
			document.setDoctype(docType);
			Date obDate = UtilDateUtilities.StringToDate(observationDate);

			if(obDate != null)
			{
				document.setObservationdate(obDate);
			}

			documentDao.merge(document);
			documentService.assignDocumentToDemographic(document, demographicNo);
			LogAction.addLogEntry(providerId, demographicNo, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
					documentIdStr, request.getRemoteAddr());

			// TODO-legacy: if demoLink is "on", check if msp is in flagproviders, if not save to providerInboxRouting, if yes, don't save.
			if(flagProviders != null && flagProviders.length > 0)
			{ // TODO-legacy: THIS NEEDS TO RUN THRU THE lab forwarding rules!

				for(String proNo : flagProviders)
				{
					documentService.routeToProviderInbox(documentId, proNo);
				}

				// Removes the link to the "0" provider so that the document no longer shows up as "unclaimed"
				providerInboxRoutingDAO.removeLinkFromDocument(documentId, "0");
			}
			saveDocNote(request, document.getDocdesc(), Integer.parseInt(demographicNoStr), Integer.parseInt(documentIdStr));
		}
		catch(Exception e)
		{
			logger.error("Error", e);
		}

		HashMap<String, String> returnValueMap = new HashMap<>();
		returnValueMap.put("patientId", demographicNoStr);
		JSONObject jsonObject = JSONObject.fromObject(returnValueMap);
		try
		{
			response.getOutputStream().write(jsonObject.toString().getBytes());
		}
		catch(IOException e)
		{
			logger.error("Error", e);
		}
		return null;
	}

	public ActionForward getDemoNameAjax(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String dn = request.getParameter("demo_no");

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DEMOGRAPHIC_READ);
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("demoName", getDemoName(LoggedInInfo.getLoggedInInfoFromSession(request), dn));
		JSONObject jsonObject = JSONObject.fromObject(hm);
		try {
			response.getOutputStream().write(jsonObject.toString().getBytes());
		} catch (IOException e) {
			MiscUtils.getLogger().error("Error", e);
		}

		return null;
	}

	public ActionForward removeLinkFromDocument(ActionMapping mapping, ActionForm form,
	                                            HttpServletRequest request, HttpServletResponse response)
	{
		String docType = request.getParameter("docType");
		String docId = request.getParameter("docId");
		String providerNo = request.getParameter("providerNo");
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		String logStatus = LogConst.STATUS_FAILURE;

		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_UPDATE);

		try
		{
			try
			{
				providerInboxRoutingDAO.removeLinkFromDocument(Integer.parseInt(docId), providerNo);
				logStatus = LogConst.STATUS_SUCCESS;
			}
			catch(SQLException e)
			{
				MiscUtils.getLogger().error("Failed to remove link from document.", e);
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to remove link from document.");
			}
			HashMap<String, List> hm = new HashMap<>();
			hm.put("linkedProviders", providerInboxRoutingDAO.getProvidersWithRoutingForDocument(docType, Integer.parseInt(docId)));

			JSONObject jsonObject = JSONObject.fromObject(hm);
			response.getOutputStream().write(jsonObject.toString().getBytes());
		}
		catch(IOException e)
		{
			MiscUtils.getLogger().error("Error writing response.", e);
		}
		LogAction.addLogEntry(loggedInProviderNo, null,
				LogConst.ACTION_UNLINK, LogConst.CON_DOCUMENT, logStatus, docId, request.getRemoteAddr(), providerNo);

		return null;
	}

	public ActionForward refileDocumentAjax(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{

		String documentId = request.getParameter("documentId");
		String queueId = request.getParameter("queueId");
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();

		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_CREATE);

		try
		{
			EDocUtil.refileDocument(documentId, queueId);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
		}
		return null;
	}

	private String getDemoName(LoggedInInfo loggedInInfo, String demog)
	{
		DemographicData demoD = new DemographicData();
		org.oscarehr.common.model.Demographic demo = demoD.getDemographic(loggedInInfo, demog);
		String demoName = demo.getLastName() + ", " + demo.getFirstName();
		return demoName;
	}

	private void saveDocNote(final HttpServletRequest request, String docDesc, Integer demog, Integer documentId)
	{
		EncounterNoteService encounterNoteService = SpringUtils.getBean(EncounterNoteService.class);
		DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographic.dao.DemographicDao");
		ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);

		Date now = EDocUtil.getDmsDateTimeAsDate();

		HttpSession se = request.getSession();
		String user_no = (String) se.getAttribute("user");
		String prog_no = new EctProgram(se).getProgram(user_no);

		ProviderData systemProvider = providerDao.find("-1");
		ProviderData provider = systemProvider;
		if(user_no != null)
		{
			provider = providerDao.find(user_no);
		}

		String strNote = "Document " + docDesc + " created at " + now + " by " + provider.getFirstName() + " " + provider.getLastName() + ".";

		CaseManagementNote cmn = new CaseManagementNote();
		cmn.setUpdateDate(now);
		cmn.setObservationDate(now);
		cmn.setDemographic(demographicDao.find(demog));
		cmn.setNote(strNote);
		cmn.setProvider(provider);
		cmn.setSigned(true);
		cmn.setSigningProvider(systemProvider);
		cmn.setProgramNo(prog_no);
		cmn.setHistory(strNote);

		CaseManagementNote savedNote = encounterNoteService.saveDocumentNote(cmn, documentDao.find(documentId));
		// Debugging purposes on the live server
		MiscUtils.getLogger().info("Document Note ID: "+savedNote.getId().toString());
	}

	/*
	 * private void savePatientLabRouting(String demog,String docId,String docType){ CommonLabResultData.updatePatientLabRouting(docId, demog, docType); }
	 */

	private static File getDocumentCacheDir(String docdownload) {
		// File cacheDir = new File(docdownload+"_cache");
		File docDir = new File(docdownload);
		String documentDirName = docDir.getName();
		File parentDir = docDir.getParentFile();

		File cacheDir = new File(parentDir, documentDirName + "_cache");

		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		return cacheDir;
	}

	private File hasCacheVersion2(Document d, Integer pageNum)
	{
		File outfile = new File(GenericFile.DOCUMENT_BASE_DIR, d.getDocfilename() + "_" + pageNum + ".png");
		if(!outfile.exists())
		{
			outfile = null;
		}
		return outfile;
	}

	public static void deleteCacheVersion(Document d, int pageNum)
	{
		File outfile = new File(GenericFile.DOCUMENT_BASE_DIR, d.getDocfilename() + "_" + pageNum + ".png");
		if(outfile.exists())
		{
			outfile.delete();
		}
	}

	private File hasCacheVersion(Document d, int pageNum)
	{
		File outfile = new File(GenericFile.DOCUMENT_BASE_DIR, d.getDocfilename() + "_" + pageNum + ".png");
		if(!outfile.exists())
		{
			outfile = null;
		}
		return outfile;
	}

	public File createCacheVersion2(Document document, Integer pageNum)
	{
		File returnFile = null;
		try
		{
			if(document.hasEncodingError())
			{
				returnFile = generatePdfPreviewUnavailableImage();
			}
			else
			{
				String docdownload = GenericFile.DOCUMENT_BASE_DIR;
				File documentCacheDir = getDocumentCacheDir(docdownload);

				Path filePath = Paths.get(docdownload).resolve(document.getDocfilename());

				returnFile = generatePdfPageImage(filePath.toString(),
						Paths.get(documentCacheDir.getCanonicalPath()).resolve(document.getDocfilename() + "_" + pageNum + ".png").toString(), pageNum);
			}
		}
		catch (IOException ioE)
		{
			MiscUtils.getLogger().error("IO Exception when resolving cache dir path: " + ioE.getMessage());
		}
		return returnFile;
	}

	public File generatePdfPageImage(String inputPdfPath, String outputFilePath, Integer pageNum)
	{
		try
		{
			String gs = OscarProperties.getInstance().getProperty("document.ghostscript_path", "/usr/bin/gs");
			String[] gsCmd = {gs, "-sDEVICE=png16m", "-dDownScaleFactor=4","-dFirstPage=" + pageNum, "-dLastPage=" + pageNum, "-o", outputFilePath, "-r384", inputPdfPath};
			Process gsProc = Runtime.getRuntime().exec(gsCmd);
			gsProc.waitFor();

			if (gsProc.exitValue() != 0)
			{
				throw new RuntimeException("GhostScript exited with value [" + gsProc.exitValue() +"] expecting 0.");
			}

			return new File(outputFilePath);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().warn("failed to convert page [" + pageNum + "] of pdf ["+ inputPdfPath +"] to png! with error: " + e.getMessage());
			return generatePdfPreviewErrorImage();
		}
	}

	private File generatePdfPreviewUnavailableImage()
	{
		final String errorHeader = "Preview Unavailable";
		final String subHeader = "Preview generation for this document type is not supported";
		return generatePdfDefaultPreviewImage(errorHeader, subHeader);
	}

	private File generatePdfPreviewErrorImage()
	{
		final String errorHeader = "Error generating PDF Preview";
		final String subHeader = "Please contact Juno support.";
		return generatePdfDefaultPreviewImage(errorHeader, subHeader);
	}

	private File generatePdfDefaultPreviewImage(final String headerText, final String subHeader)
	{
		final Integer errorImgWidth = 600;
		final Integer errorImgHeight= 800;
		final String  junoLogoPath = "/opt/oscarhost/oscarhost_maintenance/img/logo_juno_green.png";

		try
		{
			File outFile = File.createTempFile("oscarError", ".png");
			BufferedImage buffImg = new BufferedImage(errorImgWidth, errorImgHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = buffImg.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			Font headerFont = new Font("Arial", Font.BOLD, 20);
			FontMetrics headerFontMetric = g2d.getFontMetrics(headerFont);

			Font bodyFont = new Font("Arial", Font.PLAIN, 12);
			FontMetrics bodyFontMetric = g2d.getFontMetrics(bodyFont);

			//draw image

			//fill bg
			g2d.setColor(Color.white);
			g2d.fillRect(0, 0, errorImgWidth, errorImgHeight);

			//error header
			g2d.setFont(headerFont);
			g2d.setColor(Color.darkGray);
			int headerXInset = errorImgWidth/2 - headerFontMetric.stringWidth(headerText)/2;
			g2d.drawString(headerText, headerXInset, 150);

			//logo
			try
			{
				Image junoLogo = ImageIO.read(new File(junoLogoPath));
				g2d.drawImage(junoLogo,errorImgWidth/2 - ((BufferedImage) junoLogo).getWidth()/2 - 10, 10, Color.white, null);
			}
			catch (IOException ioE)
			{
				MiscUtils.getLogger().warn("could not open logo file with error: " + ioE.getMessage());
			}

			// main error msg
			g2d.setFont(bodyFont);
			g2d.setColor(Color.gray);
			g2d.drawString(subHeader, headerXInset, 170);

			g2d.dispose();
			ImageIO.write(buffImg, "png", outFile);
			return outFile;
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("failed to generate error image! with error: " + e.getMessage());
		}
		return null;
	}

	public ActionForward showPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return getPage(mapping, form, request, response, Integer.parseInt(request.getParameter("page")));
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		return getPage(mapping, form, request, response, 1);
	}

	// PNG version
	public ActionForward getPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, int pageNum) {

		try {
			String doc_no = request.getParameter("doc_no");
			logger.debug("Document No :" + doc_no);

			LogAction.addLogEntry((String) request.getSession().getAttribute("user"),
					LogConst.ACTION_READ, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr());

			Document d = documentDao.getDocument(doc_no);
			logger.debug("Document Name :" + d.getDocfilename());

			File outfile = hasCacheVersion(d, pageNum);
			if (outfile == null){
				outfile = createCacheVersion2( d, pageNum);
			}
			response.setContentType("image/png");
			// response.setHeader("Content-Disposition", "attachment;filename=\"" + filename+ "\"");
			// read the file name.

			logger.debug("about to Print to stream");
			ServletOutputStream outs = response.getOutputStream();

			response.setHeader("Content-Disposition", "attachment;filename=\"" + d.getDocfilename() + "\"");
			BufferedInputStream bfis = null;
			try {
				bfis = new BufferedInputStream(new FileInputStream(outfile));
				int data;
				while ((data = bfis.read()) != -1) {
					outs.write(data);
					// outs.flush();
				}
			} finally {
				if (bfis!=null) bfis.close();
			}
			outs.flush();
			outs.close();
		} catch (java.net.SocketException se) {
			MiscUtils.getLogger().error("Error", se);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Error", e);
		}
		return null;
	}

	public ActionForward viewDocPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);

		logger.debug("in viewDocPage");
		try
		{
			String doc_no = request.getParameter("doc_no");
			String pageNum = request.getParameter("curPage");
			if(pageNum == null)
			{
				pageNum = "0";
			}
			Integer pageNumber = Integer.parseInt(pageNum);
			logger.debug("Document No :" + doc_no);
			LogAction.addLogEntry((String) request.getSession().getAttribute("user"),
					LogConst.ACTION_READ, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr());

			Document document = documentDao.getDocument(doc_no);
			logger.debug("Document Name :" + document.getDocfilename());
			String name = document.getDocfilename() + "_" + pageNumber + ".png";
			logger.debug("name " + name);

			File outfile = hasCacheVersion2(document, pageNumber);
			if(outfile != null)
			{
				logger.debug("got doc from local cache   ");
			}
			else
			{
				outfile = createCacheVersion2(document, pageNumber);
				if(outfile != null)
				{
					logger.debug("create new doc  ");
				}
			}
			response.setContentType("image/png");
			ServletOutputStream outs = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment;filename=\"" + document.getDocfilename() + "\"");

			BufferedInputStream bfis = null;
			try
			{
				if(outfile != null)
				{
					bfis = new BufferedInputStream(new FileInputStream(outfile));
					int data;
					while((data = bfis.read()) != -1)
					{
						outs.write(data);
						// outs.flush();
					}
				}
				else
				{
					logger.info("Unable to retrieve content for " + document + ". This may indicate previous upload or save errors...");
				}
			}
			finally
			{
				if(bfis != null)
				{
					bfis.close();
				}
			}

			outs.flush();
			outs.close();
		}
		catch(java.net.SocketException se)
		{
			MiscUtils.getLogger().error("Error", se);
		}
		catch(Exception e)
		{
			MiscUtils.getLogger().error("Error", e);
		}
		return null;
	}

	public ActionForward view2(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);
		
		String doc_no = request.getParameter("doc_no");
		logger.debug("Document No :" + doc_no);

		LogAction.addLogEntry((String) request.getSession().getAttribute("user"),
				LogConst.ACTION_READ, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr());

		File documentDir = new File(GenericFile.DOCUMENT_BASE_DIR);
		logger.debug("Document Dir is a dir" + documentDir.isDirectory());

		Document d = documentDao.getDocument(doc_no);
		logger.debug("Document Name :" + d.getDocfilename());

		// TODO-legacy: Right now this assumes it's a pdf which it shouldn't

		response.setContentType("image/png");
		// response.setHeader("Content-Disposition", "attachment;filename=\"" + filename+ "\"");
		// read the file name.
		File file = new File(documentDir, d.getDocfilename());

		RandomAccessFile raf = new RandomAccessFile(file, "r");
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdffile = new PDFFile(buf);
		// long readfile = System.currentTimeMillis() - start;
		// draw the first page to an image
		PDFPage ppage = pdffile.getPage(0);

		logger.debug("WIDTH " + (int) ppage.getBBox().getWidth() + " height " + (int) ppage.getBBox().getHeight());

		// get the width and height for the doc at the default zoom
		Rectangle rect = new Rectangle(0, 0, (int) ppage.getBBox().getWidth(), (int) ppage.getBBox().getHeight());

		logger.debug("generate the image");
		Image img = ppage.getImage(rect.width, rect.height, // width & height
		        rect, // clip rect
		        null, // null for the ImageObserver
		        true, // fill background with white
		        true // block until drawing is done
		        );

		logger.debug("about to Print to stream");
		ServletOutputStream outs = response.getOutputStream();

		RenderedImage rendImage = (RenderedImage) img;
		ImageIO.write(rendImage, "png", outs);
		outs.flush();
		outs.close();
		return null;
	}
	
	public ActionForward downloadCDS(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);
		
		DemographicExport export = SharingCenterUtil.retrieveDemographicExport(Integer.valueOf(request.getParameter("doc_no")));
		String contentType = "application/zip";
		String filename = String.format("%s_%s", export.getDocumentType(), export.getId());
		byte[] contentBytes = export.getDocument();

		response.setContentType(contentType);
		response.setContentLength(contentBytes.length);
		response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
		logger.debug("about to Print to stream");
		ServletOutputStream outs = response.getOutputStream();
		outs.write(contentBytes);
		outs.flush();
		outs.close();
		return null;
	}

	public ActionForward display(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);

		String temp = request.getParameter("remoteFacilityId");
		Integer remoteFacilityId = null;
		if (temp != null && !temp.trim().isEmpty())
		{
			remoteFacilityId = Integer.parseInt(temp);
		}

		String doc_no = request.getParameter("doc_no");
		logger.debug("Document No :" + doc_no);

		String docxml = null;
		String contentType = null;
		byte[] contentBytes = null;
		String filename = null;

		// local document
		if (remoteFacilityId == null)
		{
			CtlDocument ctld = ctlDocumentDao.getCtrlDocument(Integer.parseInt(doc_no));
			Integer demographicNo = ctld.isDemographicDocument() ? ctld.getId().getModuleId() : null;
			LogAction.addLogEntry((String) request.getSession().getAttribute("user"), demographicNo,
					LogConst.ACTION_READ, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, doc_no, request.getRemoteAddr());

			File documentDir = new File(GenericFile.DOCUMENT_BASE_DIR);
			logger.debug("Document Dir is a dir" + documentDir.isDirectory());

			Document document = documentDao.getDocument(doc_no);
			logger.debug("Document Name :" + document.getDocfilename());

			docxml = document.getDocxml();
			contentType = document.getContenttype();

			File file = new File(documentDir, document.getDocfilename());
			filename = document.getDocfilename();

			if (contentType != null && !contentType.trim().equals("text/html"))
			{
				if(file.exists())
				{
					contentBytes = FileUtils.readFileToByteArray(file);
				}
				else
				{
					throw new IllegalStateException("Local document doesn't exist for eDoc (ID " + document.getId() + "): " + file.getAbsolutePath());
				}
			}
		}
		else // remote document
		{
			FacilityIdIntegerCompositePk remotePk = new FacilityIdIntegerCompositePk();
			remotePk.setIntegratorFacilityId(remoteFacilityId);
			remotePk.setCaisiItemId(Integer.parseInt(doc_no));

			
			CachedDemographicDocument remoteDocument = null;
			CachedDemographicDocumentContents remoteDocumentContents = null;

			try
			{
				if (!CaisiIntegratorManager.isIntegratorOffline(request.getSession()))
				{
					DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility());
					remoteDocument = demographicWs.getCachedDemographicDocument(remotePk);
					remoteDocumentContents = demographicWs.getCachedDemographicDocumentContents(remotePk);
				}
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error("Unexpected error.", e);
				CaisiIntegratorManager.checkForConnectionError(request.getSession(),e);
			}

			if (CaisiIntegratorManager.isIntegratorOffline(request.getSession()))
			{
				Integer demographicId = IntegratorFallBackManager.getDemographicNoFromRemoteDocument(loggedInInfo,remotePk);
				MiscUtils.getLogger().debug("got demographic no from remote document "+demographicId);
				List<CachedDemographicDocument> remoteDocuments = IntegratorFallBackManager.getRemoteDocuments(loggedInInfo,demographicId);
				for(CachedDemographicDocument demographicDocument: remoteDocuments)
				{
					if(demographicDocument.getFacilityIntegerPk().getIntegratorFacilityId() == remotePk.getIntegratorFacilityId()
							&& demographicDocument.getFacilityIntegerPk().getCaisiItemId() == remotePk.getCaisiItemId() )
					{
						remoteDocument = demographicDocument;
						remoteDocumentContents = IntegratorFallBackManager.getRemoteDocument(loggedInInfo,demographicId, remotePk);
						break;
					}
					MiscUtils.getLogger().error("End of the loop and didn't find the remoteDocument");
				}
			}

			docxml = remoteDocument.getDocXml();
			contentType = remoteDocument.getContentType();
			filename = remoteDocument.getDocFilename();
			contentBytes = remoteDocumentContents.getFileContents();
		}

		if (docxml != null && !docxml.trim().equals(""))
		{
			ServletOutputStream outs = response.getOutputStream();
			outs.write(docxml.getBytes());
			outs.flush();
			outs.close();
			return null;
		}

		// TODO-legacy: Right now this assumes it's a pdf which it shouldn't
		if (contentType == null)
		{
			contentType = "application/pdf";
		}

		response.setContentType(contentType);
		response.setContentLength(contentBytes.length);
		response.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
		logger.debug("about to Print to stream");
		ServletOutputStream outs = response.getOutputStream();
		outs.write(contentBytes);
		outs.flush();
		outs.close();
		return null;
	}

        public ActionForward viewDocumentInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType("text/html");
		doViewDocumentInfo(request, response.getWriter(),true,true);
		return null;
	}
        
        public ActionForward viewDocumentDescription(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType("text/html");
		doViewDocumentInfo(request, response.getWriter(),false,true);
		return null;
	}
        public ActionForward viewAnnotationAcknowledgementTickler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType("text/html");
		doViewDocumentInfo(request, response.getWriter(),true,false);
		return null;
	}
     
        public void doViewDocumentInfo(HttpServletRequest request, PrintWriter out,boolean viewAnnotationAcknowledgementTicklerFlag, boolean viewDocumentDescriptionFlag) {
        	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

	        securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);
        	
            String doc_no = request.getParameter("doc_no");
            Locale locale=request.getLocale();
                
            String annotation= "",acknowledgement="",tickler="";
                if(doc_no!=null && doc_no.length()>0) { 
                    annotation=EDocUtil.getHtmlAnnotation(doc_no);
                    acknowledgement=EDocUtil.getHtmlAcknowledgement(locale,doc_no);
                    if(acknowledgement==null) {acknowledgement="";}
                    tickler=EDocUtil.getHtmlTicklers(loggedInInfo, doc_no);
                }
                
                out.println("<!DOCTYPE html><html><head><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'></head><body>");
                
                if(viewAnnotationAcknowledgementTicklerFlag) {
                    if(annotation.length()>0)  {
                        out.println(annotation);
                    }
                    if(tickler.length()>0)
                    {
                        out.println(tickler+"<br>");
                    }
                    if(acknowledgement.length()>0) {
                        out.println(acknowledgement+"<br>");
                    }
                }
                
                if(viewDocumentDescriptionFlag) {
                    EDoc curDoc= EDocUtil.getDoc(doc_no);
                    ResourceBundle props = ResourceBundle.getBundle("oscarResources", locale);
                    out.println("<br>"+props.getString("dms.documentBrowser.DocumentUpdated")+": "+curDoc.getDateTimeStamp());
                    out.println("<br>"+props.getString("dms.documentBrowser.ContentUpdated")+": "+curDoc.getContentDateTime());
                    out.println("<br>"+props.getString("dms.documentBrowser.ObservationDate")+": "+curDoc.getObservationDate());
                    out.println("<br>"+props.getString("dms.documentBrowser.Type")+": "+curDoc.getType());
                    out.println("<br>"+props.getString("dms.documentBrowser.Class")+": "+curDoc.getDocClass());
                    out.println("<br>"+props.getString("dms.documentBrowser.Subclass")+": "+curDoc.getDocSubClass());
                    out.println("<br>"+props.getString("dms.documentBrowser.Description")+": "+curDoc.getDescription());
                    out.println("<br>"+props.getString("dms.documentBrowser.Creator")+": "+curDoc.getCreatorName());
                    out.println("<br>"+props.getString("dms.documentBrowser.Responsible")+": "+curDoc.getResponsibleName());
                    out.println("<br>"+props.getString("dms.documentBrowser.Reviewer")+": "+curDoc.getReviewerName());
                    out.println("<br>"+props.getString("dms.documentBrowser.Source")+": "+curDoc.getSource());
                }
                
                out.println("</body></html>");
		out.flush();
                out.close();
                
        }

	public ActionForward addIncomingDocument(ActionMapping mapping, ActionForm form,
	                                         HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		String demographicNoStr = request.getParameter("demog");
		String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
		Integer demographicNo = demographicNoStr != null ? Integer.parseInt(demographicNoStr) : null;

		securityInfoManager.requireAllPrivilege(loggedInProviderNo, Permission.DOCUMENT_CREATE);

		String pdfDir = request.getParameter("pdfDir");
		String fileName = request.getParameter("pdfName");
		String observationDate = request.getParameter("observationDate");
		String documentDescription = request.getParameter("documentDescription");
		String docType = request.getParameter("docType");
		String docClass = request.getParameter("docClass");
		String docSubClass = request.getParameter("docSubClass");
		String[] flagproviders = request.getParameterValues("flagproviders");
		String queueId1 = request.getParameter("queueId");
		String sourceFilePath = IncomingDocUtil.getIncomingDocumentFilePathName(queueId1, pdfDir, fileName);
        Date obDate = UtilDateUtilities.StringToDate(observationDate);
        String source = "";
        Integer programId = null;
        Integer documentNo = null;

        String user = (String) request.getSession().getAttribute("user");

        // if the document was added in the context of a program
		ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
		LoggedInInfo loggedInInfo  = LoggedInInfo.getLoggedInInfoFromSession(request);
		ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
		if(pp != null && pp.getProgramId() != null) {
			programId = pp.getProgramId().intValue();
		}

		GenericFile sourceFile = FileFactory.getExistingFile(sourceFilePath);
		boolean success = sourceFile.moveToDocuments();
		if(!success)
		{
			logger.error("Not able to move " + sourceFile.getName() + " to documents");
			// File was not successfully moved
		}
		else
		{
            try
            {
	            Document document = new Document();
	            document.setPublic1(false);
	            document.setDocClass(docClass);
	            document.setDocSubClass(docSubClass);
	            document.setResponsible(user);
	            document.setDocCreator(user);
	            document.setDocdesc(documentDescription);
	            document.setDoctype(docType);
	            document.setDocfilename(fileName);
	            document.setSource(source);
	            document.setObservationdate(obDate);
	            document.setProgramId(programId);

	            document = documentService.uploadNewDemographicDocument(document, sourceFile, demographicNo);
	            documentNo = document.getDocumentNo();

	            if(flagproviders != null && flagproviders.length > 0)
	            {
		            try
		            {
		            	inboxManager.addDocumentToProviderInbox(documentNo, flagproviders);
		            }
		            catch(Exception e)
		            {
			            MiscUtils.getLogger().error("Error routing to provider ", e);
		            }
	            }
	            success = true;
            }
            catch (IOException e) {
                MiscUtils.getLogger().error("Error", e);
	            success = false;
            }
        }
        String logStatus = success ? LogConst.STATUS_SUCCESS : LogConst.STATUS_FAILURE;
        LogAction.addLogEntry(user, demographicNo, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT,
		        logStatus, (documentNo==null)?null:String.valueOf(documentNo), request.getRemoteAddr(), fileName);

        return (mapping.findForward("nextIncomingDoc"));
    }
        
    public ActionForward viewIncomingDocPageAsPdf(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);

        String pageNum = request.getParameter("curPage");
        String queueId = request.getParameter("queueId");
        String pdfDir = request.getParameter("pdfDir");
        String pdfName = request.getParameter("pdfName");
        String filePath = IncomingDocUtil.getIncomingDocumentFilePathName(queueId, pdfDir, pdfName);
        Locale locale=request.getLocale();
        ResourceBundle props = ResourceBundle.getBundle("oscarResources", locale);
        
        if (pageNum == null) {
            pageNum = "0";
        }

        Integer pn = Integer.parseInt(pageNum);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdfName + UtilDateUtilities.getToday("yyyy-MM-dd.hh.mm.ss") + ".pdf\"");
        
        com.itextpdf.text.pdf.PdfCopy extractCopy = null;
        com.itextpdf.text.Document document = null;
        com.itextpdf.text.pdf.PdfReader reader = null;

        try {
            reader = new com.itextpdf.text.pdf.PdfReader(filePath);
            document = new com.itextpdf.text.Document(reader.getPageSizeWithRotation(1));
            extractCopy = new com.itextpdf.text.pdf.PdfCopy(document, response.getOutputStream());

            document.open();
            extractCopy.addPage(extractCopy.getImportedPage(reader, pn));


        } catch (Exception ex) {
            response.setContentType("text/html");
            response.getWriter().print(props.getString("dms.incomingDocs.errorInOpening") + pdfName);
            response.getWriter().print("<br>"+props.getString("dms.incomingDocs.PDFCouldBeCorrupted"));

            MiscUtils.getLogger().error("Error", ex);
        } finally {
            if (extractCopy != null) {
                extractCopy.close();
            }
            if (document != null) {
                document.close();
            }
            if (reader != null) {
                reader.close();
            }
        }

        return null;
    }

    public ActionForward displayIncomingDocs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

	    securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);
    	
        String queueId = request.getParameter("queueId");
        String pdfDir = request.getParameter("pdfDir");
        String pdfName = request.getParameter("pdfName");
        String filePath = IncomingDocUtil.getIncomingDocumentFilePathName(queueId, pdfDir, pdfName);

        String contentType = "application/pdf";
        File file = new File(filePath);

        response.setContentType(contentType);
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdfName + "\"");

        BufferedInputStream bfis = null;
        ServletOutputStream outs = response.getOutputStream();

        try {

            bfis = new BufferedInputStream(new FileInputStream(file));

            org.apache.commons.io.IOUtils.copy(bfis,outs);
            outs.flush();
            
        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);
        } finally {
            if (bfis != null) {
                bfis.close();
            }
        }

        return null;
    }
        
    public ActionForward viewIncomingDocPageAsImage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

	    securityInfoManager.requireAllPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(), Permission.DOCUMENT_READ);
    	
        String pageNum = request.getParameter("curPage");
        String queueId = request.getParameter("queueId");
        String pdfDir = request.getParameter("pdfDir");
        String pdfName = request.getParameter("pdfName");

        if (pageNum == null) {
            pageNum = "0";
        }

        BufferedInputStream bfis = null;
        ServletOutputStream outs = null;

        try {
            Integer pn = Integer.parseInt(pageNum);
            File outfile = createIncomingCacheVersion(queueId, pdfDir, pdfName, pn);
            outs = response.getOutputStream();

            if (outfile != null) {
                bfis = new BufferedInputStream(new FileInputStream(outfile));


                response.setContentType("image/png");
                response.setHeader("Content-Disposition", "inline;filename=\"" + pdfName + "\"");
                org.apache.commons.io.IOUtils.copy(bfis,outs);
                outs.flush();
                
            } else {
                logger.info("Unable to retrieve content for " + queueId + "/" + pdfDir + "/" + pdfName);
            }
        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);

        } finally {
            if (bfis != null) {
                bfis.close();
            }
        }
        return null;
    }

    public File createIncomingCacheVersion(String queueId, String pdfDir, String pdfName, Integer pageNum)
	{
        String incomingDocPath = IncomingDocUtil.getIncomingDocumentFilePath(queueId, pdfDir);
        File documentDir = new File(incomingDocPath);
        File documentCacheDir = getDocumentCacheDir(incomingDocPath);
        File file = new File(documentDir, pdfName);

        try
		{
			return generatePdfPageImage(file.getCanonicalPath(), Paths.get(documentCacheDir.getCanonicalPath()).resolve(pdfName + "_" + pageNum + ".png").toString(), pageNum);
		}
        catch (IOException ioE)
		{
			MiscUtils.getLogger().error("failed to resolve path name when creating pdf image with error: " + ioE.getMessage());
		}
        return null;
    }
}
