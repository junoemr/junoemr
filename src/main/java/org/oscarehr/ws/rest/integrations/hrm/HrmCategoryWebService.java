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

package org.oscarehr.ws.rest.integrations.hrm;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMCategoryImportMapper;
import org.oscarehr.dataMigration.mapper.hrm.out.HRMCategoryExportMapper;
import org.oscarehr.dataMigration.model.hrm.HrmCategory;
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.integration.hrm.HRMCategoryTransferInbound;
import org.oscarehr.ws.rest.transfer.integration.hrm.HRMCategoryTransferOutbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/integrations/hrm/category")
@Component("HRMCategoryWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "hrmCategory")
public class HrmCategoryWebService
{
	@Autowired
	HRMCategoryImportMapper importMapper;

	@Autowired
	HRMCategoryExportMapper exportMapper;

	@Autowired
	HRMCategoryService categoryService;

	@POST
	@Path("/")
	public RestResponse<HRMCategoryTransferOutbound> createCategory(HRMCategoryTransferInbound transferIn) throws Exception
	{
		HrmCategory categoryToCreate = importMapper.importToJuno(transferIn);
		HrmCategory created = categoryService.createCategory(categoryToCreate);
		HRMCategoryTransferOutbound transferOut = exportMapper.exportFromJuno(created);
		return RestResponse.successResponse(transferOut);
	}

	@DELETE
	@Path("/{categoryId}/")
	public RestResponse<HRMCategoryTransferOutbound> deactivateCategory(@PathParam("categoryId") Integer categoryId) throws Exception
	{
		HrmCategory deactivated = categoryService.deactivateCategory(categoryId);
		HRMCategoryTransferOutbound transferOut = exportMapper.exportFromJuno(deactivated);
		return RestResponse.successResponse(transferOut);
	}

	@PUT
	@Path("/{categoryId}/")
	public RestResponse<HRMCategoryTransferOutbound> updateCategory(@PathParam("categoryId") Integer categoryId, HRMCategoryTransferInbound transferIn) throws Exception
	{
		HrmCategory categoryToUpdate = importMapper.importToJuno(transferIn);
		categoryToUpdate.setId(categoryId);

		HrmCategory updated = categoryService.updateCategory(categoryToUpdate);
		HRMCategoryTransferOutbound transferOut = exportMapper.exportFromJuno(updated);
		return RestResponse.successResponse(transferOut);
	}
}