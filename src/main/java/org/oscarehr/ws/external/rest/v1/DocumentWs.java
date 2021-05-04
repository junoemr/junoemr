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
package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.log4j.Logger;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DocumentConverter;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferInbound;
import org.oscarehr.ws.rest.exception.MissingArgumentException;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.validator.DocumentNoConstraint;
import org.oscarehr.ws.validator.ProviderNoConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component("DocumentWs")
@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentWs extends AbstractExternalRestWs
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	DocumentService documentService;

	@Autowired
	private SecurityInfoManager securityInfoManager;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new document to the system")
	public RestResponse<Integer> postDocument(@Valid DocumentTransferInbound transfer) throws IOException, InterruptedException
	{
		String providerNoStr = getOAuthProviderNo();
		securityInfoManager.requireAllPrivilege(providerNoStr, Permission.DOCUMENT_CREATE);

		if(transfer.getDocumentNo() != null)
		{
			throw new MissingArgumentException("Argument Error", "documentNo", "Document number for a new record must be null");
		}

		InputStream inputStream;
		try
		{
			byte[] imageByteArray = Base64.getDecoder().decode(transfer.getBase64EncodedFile());
			inputStream = new ByteArrayInputStream(imageByteArray);
		}
		catch(IllegalArgumentException e)
		{
			throw new MissingArgumentException("Argument Error", "getBase64EncodedFile", "Error decoding file: " + e.getMessage());
		}

		// upload the document record
		Document document = DocumentConverter.getInboundAsDomainObject(transfer);
		document = documentService.uploadNewDemographicDocument(document, inputStream);

		String ip = getHttpServletRequest().getRemoteAddr();
		LogAction.addLogEntry(providerNoStr, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(document.getDocumentNo()), ip, document.getDocfilename());

		return RestResponse.successResponse(document.getDocumentNo());
	}

	@POST
	@Path("/{documentId}/inbox/general")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Route the document to the unclaimed inbox")
	public RestResponse<Integer> postDocument(@DocumentNoConstraint @PathParam("documentId") Integer documentId)
	{
		String providerNoStr = getOAuthProviderNo();
		String ip = getHttpServletRequest().getRemoteAddr();
		securityInfoManager.requireAllPrivilege(providerNoStr, Permission.DOCUMENT_UPDATE);

		documentService.routeToGeneralInbox(documentId);
		LogAction.addLogEntry(providerNoStr, null, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(documentId), ip);

		return RestResponse.successResponse(documentId);
	}
	@POST
	@Path("/{documentId}/inbox/provider/{providerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Route the document to the given provider inbox")
	public RestResponse<Integer> postDocument(@DocumentNoConstraint @PathParam("documentId") Integer documentId,
	                                          @ProviderNoConstraint @PathParam("providerId") String providerId)
	{
		String providerNoStr = getOAuthProviderNo();
		String ip = getHttpServletRequest().getRemoteAddr();
		securityInfoManager.requireAllPrivilege(providerNoStr, Permission.DOCUMENT_UPDATE);

		documentService.routeToProviderInbox(documentId, providerId);
		LogAction.addLogEntry(providerNoStr, null, LogConst.ACTION_UPDATE, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
				String.valueOf(documentId), ip);

		return RestResponse.successResponse(documentId);
	}
}
