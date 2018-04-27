/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */
package org.oscarehr.ws.external.rest.v1;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.document.model.Document;
import org.oscarehr.document.service.DocumentService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.rest.AbstractExternalRestWs;
import org.oscarehr.ws.external.rest.v1.conversion.DocumentConverter;
import org.oscarehr.ws.external.rest.v1.transfer.DocumentTransfer;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
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
	ProgramManager programManager;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Operation(summary = "Add a new document to the system")
	public RestResponse<Integer> postDocument(@Valid DocumentTransfer transfer)
	{
		Document document;
		try
		{
			if(transfer.getDocumentNo() != null)
			{
				return RestResponse.errorResponse("Document number for a new record must be null");
			}

			InputStream inputStream;
			try
			{
				byte[] imageByteArray = Base64.getDecoder().decode(transfer.getBase64EncodedFile());
				inputStream = new ByteArrayInputStream(imageByteArray);
			}
			catch(IllegalArgumentException e)
			{
				return RestResponse.errorResponse("Error decoding file: " + e.getMessage());
			}

			document = DocumentConverter.getAsDomainObject(transfer);
			document.setProgramId(programManager.getDefaultProgramId());
			document = documentService.uploadNewDocument(document, inputStream);

			String providerNoStr = getOAuthProviderNo();
			String ip = getHttpServletRequest().getRemoteAddr();

			LogAction.addLogEntry(providerNoStr, null, LogConst.ACTION_ADD, LogConst.CON_DOCUMENT, LogConst.STATUS_SUCCESS,
					String.valueOf(document.getDocumentNo()), ip, document.getDocfilename());

		}
		catch(SecurityException e)
		{
			logger.error("Security Error", e);
			return RestResponse.errorResponse("User Permissions Error");
		}
		catch(Exception e)
		{
			logger.error("Error", e);
			return RestResponse.errorResponse("System Error");
		}
		return RestResponse.successResponse(document.getDocumentNo());
	}
}
