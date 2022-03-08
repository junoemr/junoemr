
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
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.external.rest.v1.conversion.DocumentConverter;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferOutbound;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Path("demographic/{demographicNo}/documents/")
@Component("DemographicDocumentsWebService")
@Tag(name = "demographic")
public class DemographicDocumentsWebService extends AbstractServiceImpl
{
	protected SecurityInfoManager securityInfoManager;
	protected DocumentService documentService;
	protected DemographicDao demographicDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public DemographicDocumentsWebService(
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

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public RestResponse<List<DocumentTransferOutbound>> searchDocuments(
			@PathParam("demographicNo") String demographicNo) throws IOException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Integer.parseInt(demographicNo), Permission.DOCUMENT_READ);

		Demographic demo = demographicDao.findOrThrow(Integer.parseInt(demographicNo));

		List<DocumentTransferOutbound> docTransfers = demo.getDocuments().stream().map((doc) ->
		{
			try
			{
				return DocumentConverter.getAsTransferObject(doc, false);
			}
			catch(IOException e)
			{
				throw new RuntimeException("Failed to convert document to transfer", e);
			}
		}).collect(Collectors.toList());

		return RestResponse.successResponse(docTransfers);
	}
}
