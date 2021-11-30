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
import org.oscarehr.hospitalReportManager.converter.HRMCategoryImportMapper;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.hospitalReportManager.transfer.HRMCategoryTransferInbound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmCategoryWebService extends AbstractServiceImpl
{
	@Autowired
	HRMCategoryImportMapper importMapper;

	@Autowired
	HRMCategoryService categoryService;

	@Autowired
	SecurityInfoManager securityService;

	@POST
	@Path("/")
	public RestResponse<HrmCategoryModel> createCategory(HRMCategoryTransferInbound transferIn) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_CREATE);

		HrmCategoryModel categoryToCreate = importMapper.convert(transferIn);
		HrmCategoryModel created = categoryService.createCategory(categoryToCreate);
		return RestResponse.successResponse(created);
	}

	@GET
	@Path("/{categoryId}/")
	public RestResponse<HrmCategoryModel> getActiveCategory(@PathParam("categoryId") Integer categoryId) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_READ);

		HrmCategoryModel found = categoryService.getActiveCategory(categoryId);
		return RestResponse.successResponse(found);
	}

	@DELETE
	@Path("/{categoryId}/")
	public RestResponse<HrmCategoryModel> deactivateCategory(@PathParam("categoryId") Integer categoryId) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_DELETE);

		HrmCategoryModel deactivated = categoryService.deactivateCategory(categoryId);
		return RestResponse.successResponse(deactivated);
	}

	@PUT
	@Path("/{categoryId}/")
	public RestResponse<HrmCategoryModel> updateCategory(@PathParam("categoryId") Integer categoryId, HRMCategoryTransferInbound transferIn) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_UPDATE);

		HrmCategoryModel categoryToUpdate = importMapper.convert(transferIn);
		categoryToUpdate.setId(categoryId);

		HrmCategoryModel updated = categoryService.updateCategory(categoryToUpdate);
		return RestResponse.successResponse(updated);
	}
}