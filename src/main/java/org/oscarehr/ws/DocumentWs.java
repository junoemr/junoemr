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

package org.oscarehr.ws;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import javax.servlet.ServletContext;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.cxf.annotations.GZIP;
import org.apache.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.lowagie.text.pdf.PdfReader;

import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.managers.DocumentManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.transfer_objects.DocumentTransfer;

import oscar.dms.EDoc;
import oscar.dms.EDocUtil;


@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class DocumentWs extends AbstractWs {
	private Logger logger = MiscUtils.getLogger();

	@Autowired
	private DocumentManager documentManager;

	@Autowired
	private ProgramManager programManager;

	public DocumentTransfer getDocument(Integer documentId) {
		try {
			LoggedInInfo loggedInInfo = getLoggedInInfo();
			Document document = documentManager.getDocument(loggedInInfo, documentId);
			CtlDocument ctlDocument = documentManager.getCtlDocumentByDocumentId(loggedInInfo, documentId);
			return (DocumentTransfer.toTransfer(document, ctlDocument));
		} catch (IOException e) {
			logger.error("Unexpected error", e);
			throw (new WebServiceException(e));
		}
	}

	public DocumentTransfer[] getDocumentsUpdateAfterDate(Date updatedAfterThisDateExclusive, int itemsToReturn) {
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		List<Document> documents = documentManager.getDocumentsUpdateAfterDate(loggedInInfo, updatedAfterThisDateExclusive, itemsToReturn);
		return (DocumentTransfer.getTransfers(loggedInInfo, documents));
	}

	public DocumentTransfer[] getDocumentsByProgramProviderDemographicDate(Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn) {
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		List<Document> documents = documentManager.getDocumentsByProgramProviderDemographicDate(loggedInInfo, programId, providerNo, demographicId, updatedAfterThisDateExclusive, itemsToReturn);
		logger.debug("programId="+programId+", providerNo="+providerNo+", demographicId="+demographicId+", updatedAfterThisDateExclusive="+DateFormatUtils.ISO_DATETIME_FORMAT.format(updatedAfterThisDateExclusive)+", itemsToReturn="+itemsToReturn+", results="+documents.size());
		return (DocumentTransfer.getTransfers(loggedInInfo, documents));
	}

	public String addDocument(String docFilename, String docContentsBase64,
					String providerId, String responsibleId)
					throws IOException {
		// Gather required data                                                 
		int numberOfPages = 0;

		// Decode document                                                      
		Base64 base64 = new Base64();
		byte[] docContents = base64.decode(docContentsBase64);

		// Make document object                                                 
		EDoc newDoc = new EDoc("", "", docFilename, "", providerId,
						responsibleId, "", 'A',
						oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "",
						"demographic", "-1", 0);

		newDoc.setDocPublic("0");
		String systemFilename = newDoc.getFileName();

		if (docContents.length == 0) {
			throw new FileNotFoundException();
		}

		// Save file to document folder                                         
		saveDocumentFile(docContents, systemFilename);

		// Set content type                                                     
		if (systemFilename.endsWith(".PDF") || systemFilename.endsWith(".pdf")) {
			newDoc.setContentType("application/pdf");
			numberOfPages = countNumOfPages(systemFilename);
		}
		newDoc.setNumberOfPages(numberOfPages);

		Integer doc_no = Integer.parseInt(EDocUtil.addDocumentSQL(newDoc));

		ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		ProviderInboxRoutingDao providerInboxRoutingDao
						= (ProviderInboxRoutingDao) ctx.getBean("providerInboxRoutingDAO");
		providerInboxRoutingDao.addToProviderInbox(providerId, doc_no, "DOC");

		return "{\"success\":1,\"message\":\"\"}";
	}

	private void saveDocumentFile(byte[] docContents, String fileName)
					throws IOException {
		FileOutputStream fos = null;

		try {
			String savePath = 
				oscar.OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "/" + fileName;

			fos = new FileOutputStream(savePath);

			fos.write(docContents);
			fos.flush();
		} catch (Exception e) {
			logger.debug(e.toString());
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * Counts the number of pages in a local pdf file.
	 *
	 * @param fileName the name of the file
	 * @return the number of pages in the file
	 */
	public int countNumOfPages(String fileName) {// count number of pages in a  
		// local pdf file           
		int numOfPage = 0;

		String filePath = oscar.OscarProperties.getInstance()
						.getProperty("DOCUMENT_DIR") + "/" + fileName;

		try {
			PdfReader reader = new PdfReader(filePath);
			numOfPage = reader.getNumberOfPages();
			reader.close();
		} catch (IOException e) {
			logger.debug(e.toString());
		}
		return numOfPage;
	}
}
