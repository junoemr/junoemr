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

import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.service.DemographicService;
import org.oscarehr.dx.service.DxResearchService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.batch.DemographicBatchDxUpdateTo1;
import org.oscarehr.ws.rest.transfer.batch.DemographicBatchOperationTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/batch")
@Component
public class BatchOperationService extends AbstractServiceImpl
{
	@Autowired
	DemographicDao demographicDao;

	@Autowired
	DemographicService demographicService;

	@Autowired
	SecurityInfoManager securityInfoManager;

	@Autowired
	DxResearchService dxResearchService;

	private static final String STANDARD_ERROR_STRING = "\"Error processing batch operation, with error: \"";

	@POST
	@Path("/deactivate_demographics")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Boolean> deactivateDemographics(DemographicBatchOperationTo1 demoTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_DELETE);
		return changeDemographicStatuses(demoTransfer, org.oscarehr.common.model.Demographic.PatientStatus.IN.name());
	}

	@POST
	@Path("/activate_demographics")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Boolean> activateDemographics(DemographicBatchOperationTo1 demoTransfer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_UPDATE);
		return changeDemographicStatuses(demoTransfer, org.oscarehr.common.model.Demographic.PatientStatus.AC.name());
	}

	@POST
	@Path("/set_dx_code")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Boolean> setDemographicDxCode(DemographicBatchDxUpdateTo1 demoTo1)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.ADMIN_UPDATE);

		MiscUtils.getLogger().info("Performing batch demographic dx code assignment: " + demoTo1.toString());
		try
		{
			for (Integer demoNo : demoTo1.getDemographicNumbers())
			{
				dxResearchService.assignDxCodeToDemographic(demoNo, Integer.parseInt(getLoggedInInfo().getLoggedInProviderNo()), demoTo1.getDxCode(), demoTo1.getDxCodingSystem());
			}
			LogAction.addLogEntry(getLoggedInInfo().getLoggedInProviderNo(), LogConst.ACTION_UPDATE, LogConst.CON_DISEASE_REG, LogConst.STATUS_SUCCESS, "Assign dx codes: " + demoTo1.toString());
			return RestResponse.successResponse(true);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Failed to batch update demographic Dx codes to [" + demoTo1.getDxCode() + "] with error: " + e.getMessage(), e);
			throw e;
		}
	}

	/**
	 * Batch update the status of demographics, while creating demographic archives and log entries
	 * @param demoTransfer - a demographic transfer object containing the demographic numbers to update
	 * @param newStatus - the status to which all demographics will be set
	 * @return - a rest response indicating the result
	 */
	private RestResponse<Boolean> changeDemographicStatuses(DemographicBatchOperationTo1 demoTransfer, String newStatus)
	{
		MiscUtils.getLogger().info("Performing batch demographic status update to [" + newStatus + "] with params: " + demoTransfer);
		try
		{
			for (Integer demoNo : demoTransfer.getDemographicNumbers())
			{
				Demographic demo = demographicDao.find(demoNo);
				demo.setPatientStatus(newStatus);
				demographicService.updateDemographicRecord(demo, getLoggedInInfo());
			}
			LogAction.addLogEntry(getLoggedInInfo().getLoggedInProviderNo(), LogConst.ACTION_UPDATE, LogConst.CON_DEMOGRAPHIC, LogConst.STATUS_SUCCESS, "Change Demographic Status: " + demoTransfer.toString());
			return RestResponse.successResponse(true);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Failed to batch update status to [" + newStatus + "] for demographics with error: " + e.getMessage(), e);
			throw e;
		}
	}
}
