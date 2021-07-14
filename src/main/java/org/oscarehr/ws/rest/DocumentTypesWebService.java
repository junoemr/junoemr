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
import org.oscarehr.common.dao.CtlDocTypeDao;
import org.oscarehr.common.model.CtlDocType;
import org.oscarehr.ws.rest.conversion.document.CtlDocTypeToDocumentTypeDtoConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.document.DocumentTypeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

@Path("document/types")
@Component("DocumentTypesWebService")
@Tag(name = "document")
public class DocumentTypesWebService extends AbstractServiceImpl
{
	protected CtlDocTypeDao ctlDocTypeDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public DocumentTypesWebService(CtlDocTypeDao ctlDocTypeDao)
	{
		this.ctlDocTypeDao = ctlDocTypeDao;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@Path("/")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<DocumentTypeDto>> getDocumentTypes(
			@QueryParam("status") String status,
			@QueryParam("module") String module
	)
	{
		List<CtlDocType> docTypes = ctlDocTypeDao.findByStatusAndModule(Collections.singletonList(status), module);
		return RestResponse.successResponse((new CtlDocTypeToDocumentTypeDtoConverter()).convert(docTypes));
	}

}
