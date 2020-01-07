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
package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentCacheTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService extends BaseService
{
	public void updateAppointmentCache(IntegrationData integrationData, AppointmentCacheTo1 appointmentTransfer)
	{
		String endpoint = "/clinic/%s/appointment/%s/cache";

		String apiKey = integrationData.getClinicApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();
		String appointmentId = appointmentTransfer.getId();

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId, appointmentId);
			Boolean response = put(endpoint, apiKey, appointmentTransfer, Boolean.class);
			if(!response)
			{
				throw new RuntimeException("Got bad response status: " + response);
			}
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}
	}
}
