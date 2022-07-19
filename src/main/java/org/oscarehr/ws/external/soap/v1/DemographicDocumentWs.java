/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.ws.external.soap.v1;

import org.apache.cxf.annotations.GZIP;
import org.oscarehr.document.model.CtlDocument;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.DocumentManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.external.soap.v1.transfer.DocumentTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.jws.WebService;
import java.io.IOException;

@WebService
@Component
@GZIP(threshold= AbstractWs.GZIP_THRESHOLD)
public class DemographicDocumentWs extends AbstractWs
{
	@Autowired
	protected DocumentService documentService;

	@Autowired
	protected DocumentManager documentManager;

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	/**
	 * upload a document and assign to patient
	 * @param demographicId - the demographic to which the document is being uploaded
	 * @param name - document name
	 * @param dataBase64 - document base64 encoded data.
	 * @return - the newly uploaded document.
	 */
	public DocumentTransfer create(String demographicId, String name, String dataBase64)
		throws IOException, InterruptedException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DOCUMENT_CREATE);

		Document newDocument = this.documentService.uploadNewDemographicDocument(getLoggedInInfo(), demographicId, name, dataBase64);
		CtlDocument ctlDocument = documentManager.getCtlDocumentByDocumentId(getLoggedInInfo(), newDocument.getDocumentNo());

		return DocumentTransfer.toTransfer(newDocument, ctlDocument);
	}

}
