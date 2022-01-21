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
import org.oscarehr.hospitalReportManager.service.HRMSubClassService;
import org.oscarehr.security.model.Permission;
import org.oscarehr.ws.rest.AbstractServiceImpl;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/integrations/hrm/subclass")
@Component("HRMSubClassWebService")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "hrmSubClass")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HrmSubClassWebService extends AbstractServiceImpl
{
	@Autowired
	HRMSubClassService hrmSubClassService;

	@GET
	@Path("/")
	public RestResponse<HrmSubClassModel> findActiveByAttributes(
		@QueryParam("sendingFacilityId") String facilityId,
		@QueryParam("reportClass") String reportClass,
		@QueryParam("subClassName") String subClassName,
		@QueryParam("accompanyingSubClassName") String accompanyingSubClassName)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.HRM_READ);

		HrmSubClassModel model = hrmSubClassService.findActiveByAttributes(facilityId, reportClass, subClassName, accompanyingSubClassName);
		return RestResponse.successResponse(model);
	}
}