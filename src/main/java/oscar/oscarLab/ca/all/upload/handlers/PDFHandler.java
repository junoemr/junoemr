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

package oscar.oscarLab.ca.all.upload.handlers;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 *
 * @author mweston4
 */
public class PDFHandler implements MessageHandler
{
	protected static Logger logger = Logger.getLogger(PDFHandler.class);

	private DocumentService documentService = SpringUtils.getBean(DocumentService.class);

	@Override
	public String parse(LoggedInInfo loggedInInfo, String serviceName, String fileName, int fileId, String ipAddr)
	{

        String providerNo = "-1";
        String filePath = fileName;              
        if (!(fileName.endsWith(".pdf") || fileName.endsWith(".PDF"))) {
            logger.error("Document " + fileName + "does not have pdf extension");
            return null;
        }
        else {
            int fileNameIdx = fileName.lastIndexOf("/");
            fileName = fileName.substring(fileNameIdx+1);
        }

	    Document document = new Document();
	    document.setPublic1(false);
	    document.setResponsible(providerNo);
	    document.setDocCreator(providerNo);
	    document.setDocdesc("");
	    document.setDoctype("");
	    document.setDocfilename(fileName);
	    document.setSource("");
	    document.setObservationdate(new Date());

	    InputStream fileInputStream = null;

	    try
	    {
		    fileInputStream = new FileInputStream(filePath);

		    document = documentService.uploadNewDemographicDocument(document, fileInputStream);
		    Integer documentNo = document.getDocumentNo();

		    LogAction.addLogEntry(providerNo, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS, String.valueOf(documentNo), ipAddr, fileName);


		    //Get provider to route document to
		    String batchPDFProviderNo = OscarProperties.getInstance().getProperty("batch_pdf_provider_no");
		    if((batchPDFProviderNo != null) && !batchPDFProviderNo.isEmpty())
		    {
			    documentService.routeToProviderInbox(documentNo, batchPDFProviderNo);

			    //Add to default queue for now, not sure how or if any other queues can be used anyway (MAB)
			    QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) SpringUtils.getBean("queueDocumentLinkDAO");
			    queueDocumentLinkDAO.addToQueueDocumentLink(1, documentNo);
		    }
	    }
	    catch(FileNotFoundException e)
	    {
		    logger.info("Error, missing file (" + fileName + ")", e);
		    return null;
	    }
	    catch(Exception e)
	    {
		    logger.info("An unexpected error has occurred", e);
		    return null;
	    }
	    finally
	    {
		    try
		    {
			    if(fileInputStream != null)
			    {
				    fileInputStream.close();
			    }
		    }
		    catch(IOException e1)
		    {
			    logger.info("An unexpected error has occurred:" + e1.toString());
			    return null;
		    }
	    }

		return "success";
	}
}
