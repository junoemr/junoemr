
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
 
package org.oscarehr.ws.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.external.rest.v1.conversion.DocumentConverter;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferOutbound;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("document/")
@Component("DocumentWebService")
@Tag(name = "document")
public class DocumentWebService extends AbstractServiceImpl
{
	protected DocumentDao documentDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public DocumentWebService(DocumentDao documentDao)
	{
		this.documentDao = documentDao;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@GET
	@Path("/{documentNo}/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<DocumentTransferOutbound> getDocument(@PathParam("documentNo") String documentNo) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.DOCUMENT_READ);

		Document document = this.documentDao.findOrThrow(Integer.parseInt(documentNo));
		return RestResponse.successResponse(DocumentConverter.getAsTransferObject(document, true));
	}

}