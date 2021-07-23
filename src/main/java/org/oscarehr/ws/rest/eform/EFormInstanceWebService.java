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

package org.oscarehr.ws.rest.eform;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.eform.service.EFormDataService;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Base64;

@Path("/eform/{fid}/instance/")
@Component("EFormInstanceWebService")
@Tag(name = "EForm")
public class EFormInstanceWebService extends AbstractServiceImpl
{

	protected EFormDataDao eFormDataDao;
	protected EFormDataService eFormDataService;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public EFormInstanceWebService(EFormDataDao eFormDataDao, EFormDataService eFormDataService)
	{
		this.eFormDataDao = eFormDataDao;
		this.eFormDataService = eFormDataService;
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	/**
	 * print an eform to pdf and return it as a base64 encoded string.
	 * @param fdid - the fdid to print
	 */
	@GET
	@Path("/{fdid}/print")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<String> getEFormPDFBase64(
			@PathParam("fid") String fid,
			@PathParam("fdid") String fdid) throws IOException, HtmlToPdfConversionException
	{
		EFormData eFormData = eFormDataDao.findByFormDataId(Integer.parseInt(fdid));
		return RestResponse.successResponse(Base64.getEncoder().encodeToString(this.eFormDataService.printEForm(getLoggedInInfo(), getHttpServletRequest().getContextPath(), eFormData)));
	}

}
