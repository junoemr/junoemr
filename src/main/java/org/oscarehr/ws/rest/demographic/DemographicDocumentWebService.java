
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
 
package org.oscarehr.ws.rest.demographic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.external.rest.v1.conversion.DocumentConverter;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferOutbound;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("demographic/{demographicNo}/document/")
@Component("DemographicDocumentWebService")
@Tag(name = "demographic")
public class DemographicDocumentWebService extends AbstractServiceImpl
{
	protected SecurityInfoManager securityInfoManager;
	protected DocumentService documentService;
	protected DemographicDao demographicDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public DemographicDocumentWebService(
			SecurityInfoManager securityInfoManager,
			DocumentService documentService,
			DemographicDao demographicDao)
	{
		this.securityInfoManager = securityInfoManager;
		this.documentService = documentService;
		this.demographicDao = demographicDao;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@Path("/")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DocumentTransferOutbound> createDocument(@PathParam("demographicNo") String demographicNo, DocumentTransferInbound documentTransfer) throws IOException, InterruptedException
	{
		securityInfoManager.requireAllPrivilege(
				getLoggedInInfo().getLoggedInProviderNo(),
				SecurityInfoManager.WRITE,
				Integer.parseInt(demographicNo),
				"_edoc");

		documentTransfer.setDocumentNo(null);

		Document newDocument = documentService.uploadNewDemographicDocument(
				getLoggedInInfo(),
				DocumentConverter.getInboundAsDomainObject(documentTransfer),
				demographicDao.findOrThrow(Integer.parseInt(demographicNo)),
				documentTransfer.getBase64EncodedFile());

		return RestResponse.successResponse(DocumentConverter.getAsTransferObject(newDocument, false));
	}
}
