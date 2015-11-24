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

/*
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import javax.jws.WebParam;
*/
import javax.jws.WebService;

//import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;

//import oscar.OscarProperties;
/*
import oscar.oscarLab.FileUploadCheck;
import oscar.oscarLab.ca.all.upload.HandlerClassFactory;
import oscar.oscarLab.ca.all.upload.handlers.MessageHandler;
*/

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import com.lowagie.text.pdf.PdfReader;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import javax.servlet.ServletContext;
import javax.xml.ws.handler.MessageContext;


@WebService
public class DocumentWs extends AbstractWs {

    private static final Logger logger=MiscUtils.getLogger();

    public String addDocument(String docFilename, String docContentsBase64,
		String providerId, String responsibleId) 
			throws IOException
    {
		// Gather required data
		int numberOfPages = 0;

		// Decode document
		Base64 base64 = new Base64();
		byte[] docContents = base64.decode(docContentsBase64);

		// Make document object
		EDoc newDoc = new EDoc("", "", docFilename, "", providerId, 
			responsibleId, "", 'A', 
			oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "", 
			"demographic", "-1", "", 0);

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

		String doc_no = EDocUtil.addDocumentSQL(newDoc);
	
		ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

		ProviderInboxRoutingDao providerInboxRoutingDao = 
			(ProviderInboxRoutingDao) ctx.getBean("providerInboxRoutingDAO");
		providerInboxRoutingDao.addToProviderInbox(providerId, doc_no, "DOC");
	
        return "{\"success\":1,\"message\":\"\"}";
    }

	private void saveDocumentFile(byte[] docContents, String fileName)
		throws IOException 
	{
		FileOutputStream fos = null;

		try {
			String savePath = oscar.OscarProperties.getInstance()
				.getProperty("DOCUMENT_DIR") + "/" + fileName;

			fos = new FileOutputStream(savePath);

			fos.write(docContents);
			fos.flush();
		} 
		catch (Exception e) 
		{
			logger.debug(e.toString());
		} 
		finally 
		{
			if (fos != null)
			{
				fos.close();
			}
		}
	}




	/**
	 * Counts the number of pages in a local pdf file.
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

