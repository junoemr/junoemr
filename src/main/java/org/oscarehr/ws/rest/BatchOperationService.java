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

import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.batch.DemographicBatchOperationTransfer;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/batch")
@Component
public class BatchOperationService extends AbstractServiceImpl
{
	@POST
	@Path("/activate_deactivate_demographics")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Boolean> activateDeactivateDemographics(DemographicBatchOperationTransfer demoTransfer)
	{
		if (demoTransfer.getOperation() == null || demoTransfer.getOperation().equals("deactivate"))
		{
			for (Integer demoNo : demoTransfer.getDemographicNumbers())
			{
				MiscUtils.getLogger().info("demographic would be deactivated: " + demoNo);
			}
		}
		else if (demoTransfer.getOperation().equals("activate"))
		{
			for (Integer demoNo : demoTransfer.getDemographicNumbers())
			{
				MiscUtils.getLogger().info("demographic would be activated: " + demoNo);
			}
		}
		else
		{
			return RestResponse.errorResponse("Unknown Batch Operation: " + demoTransfer.getOperation());
		}

		return RestResponse.successResponse(true);
	}
}
