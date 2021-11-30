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
import org.oscarehr.dataMigration.model.hrm.HrmSubClassModel;
import org.oscarehr.hospitalReportManager.converter.HRMCategoryImportMapper;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.hospitalReportManager.converter.HRMSubClassImportMapper;
import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.oscarehr.hospitalReportManager.service.HRMSubClassService;
import org.oscarehr.hospitalReportManager.transfer.HRMSubClassTransferInbound;
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
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/integrations/hrm/category")
@Component("HRMCategoryWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "hrmCategory")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmCategoryWebService extends AbstractServiceImpl
{
	@Autowired
	HRMCategoryImportMapper categoryImportMapper;

	@Autowired
	HRMSubClassImportMapper subClassImportMapper;

	@Autowired
	HRMCategoryService categoryService;

	@Autowired
	HRMSubClassService subClassService;

	@Autowired
	SecurityInfoManager securityService;

	@POST
	@Path("/")
	public RestResponse<HrmCategoryModel> createCategory(HRMCategoryTransferInbound transferIn) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_CREATE);

		HrmCategoryModel categoryToCreate = categoryImportMapper.convert(transferIn);
		HrmCategoryModel created = categoryService.createCategory(categoryToCreate);
		return RestResponse.successResponse(created);
	}

	@DELETE
	@Path("/{categoryId}/")
	public RestResponse<HrmCategoryModel> deactivateCategory(@PathParam("categoryId") Integer categoryId) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_DELETE);

		HrmCategoryModel deactivated = categoryService.deactivateCategory(categoryId);
		return RestResponse.successResponse(deactivated);
	}

	@PATCH
	@Path("/{categoryId}/")
	public RestResponse<HrmCategoryModel> updateCategoryName(@PathParam("categoryId") Integer categoryId, @QueryParam("name") String newName) throws Exception
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_UPDATE);
		HrmCategoryModel updated = categoryService.updateCategoryName(categoryId, newName);
		return RestResponse.successResponse(updated);
	}

	@POST
	@Path("/{categoryId}/subClass/")
	public RestResponse<HrmSubClassModel> addSubClassToCategory(@PathParam("categoryId") Integer categoryId, HRMSubClassTransferInbound subClass)
	{
		securityService.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_CREATE);
		HrmSubClassModel model = subClassImportMapper.convert(subClass, categoryId);
		HrmSubClassModel created = subClassService.createSubClass(model);

		return RestResponse.successResponse(created);
	}
}