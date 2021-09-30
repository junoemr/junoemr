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

package org.oscarehr.ws.external.soap.v1;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.annotations.GZIP;
import org.apache.log4j.Logger;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.DocumentManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.common.annotation.SkipContentLoggingInbound;
import org.oscarehr.ws.external.soap.v1.transfer.DocumentTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class DocumentWs extends AbstractWs {
	private Logger logger = MiscUtils.getLogger();

	@Autowired
	private DocumentManager documentManager;

	@Autowired
	private DocumentService documentService;

	public DocumentTransfer getDocument(Integer documentId)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.DOCUMENT_READ);
		try
		{
			Document document = documentManager.getDocument(loggedInInfo, documentId);
			CtlDocument ctlDocument = documentManager.getCtlDocumentByDocumentId(loggedInInfo, documentId);
			return (DocumentTransfer.toTransfer(document, ctlDocument));
		}
		catch(IOException e)
		{
			logger.error("Unexpected error", e);
			throw (new WebServiceException(e));
		}
	}

	public DocumentTransfer[] getDocumentsUpdateAfterDate(Date updatedAfterThisDateExclusive, int itemsToReturn)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.DOCUMENT_READ);

		List<Document> documents = documentManager.getDocumentsUpdateAfterDate(loggedInInfo, updatedAfterThisDateExclusive, itemsToReturn);
		return (DocumentTransfer.getTransfers(loggedInInfo, documents));
	}

	public DocumentTransfer[] getDocumentsByProgramProviderDemographicDate(Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn)
	{
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.DOCUMENT_READ);

		List<Document> documents = documentManager.getDocumentsByProgramProviderDemographicDate(loggedInInfo, programId, providerNo, demographicId, updatedAfterThisDateExclusive, itemsToReturn);
		logger.debug("programId=" + programId + ", providerNo=" + providerNo + ", demographicId=" + demographicId + ", updatedAfterThisDateExclusive=" + DateFormatUtils.ISO_DATETIME_FORMAT.format(updatedAfterThisDateExclusive) + ", itemsToReturn=" + itemsToReturn + ", results=" + documents.size());
		return (DocumentTransfer.getTransfers(loggedInInfo, documents));
	}

	@SkipContentLoggingInbound
	public String addDocument(String docFilename, String docContentsBase64, String providerId, String responsibleId) throws IOException, InterruptedException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DOCUMENT_CREATE);
		// Decode document
		Base64 base64 = new Base64();
		byte[] docContents = base64.decode(docContentsBase64);

		if(docContents.length == 0)
		{
			throw new FileNotFoundException();
		}

		InputStream fileInputStream = new ByteArrayInputStream(docContents);

		Document document = new Document();
		document.setPublic1(false);
		document.setResponsible(responsibleId);
		document.setDocCreator(providerId);
		document.setDocdesc("");
		document.setDoctype("");
		document.setDocfilename(docFilename);
		document.setSource("");
		document.setObservationdate(new Date());

		documentService.uploadNewDemographicDocument(document, fileInputStream);
		documentService.routeToProviderInbox(document.getDocumentNo(), providerId);

		return "{\"success\":1,\"message\":\"\"}";
	}
}
