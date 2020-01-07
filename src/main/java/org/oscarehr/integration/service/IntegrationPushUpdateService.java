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
package org.oscarehr.integration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.oscarehr.common.server.ServerStateHandler;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.dao.IntegrationPushAppointmentUpdateDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.IntegrationPushAppointmentUpdate;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentCacheTo1;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.oscarehr.integration.model.Integration.INTEGRATION_TYPE_MHA;

@Service
public class IntegrationPushUpdateService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final Integer MAX_SEND_ATTEMPTS = 25;

	@Autowired
	private AppointmentService appointmentService;

	@Autowired
	private IntegrationDao integrationDao;

	@Autowired
	private IntegrationPushAppointmentUpdateDao integrationPushAppointmentUpdateDao;

	@Autowired
	private ServerStateHandler serverStateHandler;

	public void queueAppointmentCacheUpdate(Integration integration, AppointmentCacheTo1 appointment) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonData = mapper.writeValueAsString(appointment);

		IntegrationPushAppointmentUpdate pushUpdate = new IntegrationPushAppointmentUpdate();
		pushUpdate.setAppointmentId(appointment.getId());
		pushUpdate.setIntegrationType(INTEGRATION_TYPE_MHA);
		pushUpdate.setIntegrationId(integration.getId());
		pushUpdate.setStatusQueued();
		pushUpdate.setJsonData(jsonData);

		integrationPushAppointmentUpdateDao.persist(pushUpdate);
	}

	public void sendQueuedUpdates()
	{
		ObjectMapper mapper = new ObjectMapper();
		List<IntegrationPushAppointmentUpdate> unsentUpdates = integrationPushAppointmentUpdateDao.findUnsent(INTEGRATION_TYPE_MHA);
		ArrayList<String> failedIdList = new ArrayList<>();

		if(!unsentUpdates.isEmpty() && serverStateHandler.isThisServerMaster())
		{
			logger.info("pushing " + unsentUpdates.size() + " integration updates");
			for(IntegrationPushAppointmentUpdate update : unsentUpdates)
			{
				// if an error status found, skip it and all subsequent updates by id
				if(update.getStatus() == IntegrationPushAppointmentUpdate.PUSH_STATUS.ERROR)
				{
					failedIdList.add(update.getAppointmentId());
					logger.error("Integration updates for appointment_id " + update.getAppointmentId() +
							" are in an error state and cannot be pushed.");
					continue;
				}

				// skip sending subsequent updates for appointments that have failed
				if(failedIdList.contains(update.getAppointmentId()))
				{
					continue;
				}

				try
				{
					sendQueuedUpdate(mapper, update);

					update.setStatusSent();
					update.setSentAt(new Date());
				}
				catch(Exception e)
				{
					logger.error("Error sending integration update for appointment " + update.getAppointmentId(), e);
					failedIdList.add(update.getAppointmentId());

					// +1 to account for increment that is not added yet
					if(update.getSendCount() + 1 >= MAX_SEND_ATTEMPTS)
					{
						update.setStatusError();
					}
				}
				finally
				{
					update.incrementSendCount();
					integrationPushAppointmentUpdateDao.merge(update);
				}
			}
		}
	}

	private void sendQueuedUpdate(ObjectMapper mapper, IntegrationPushAppointmentUpdate update) throws IOException
	{
		AppointmentCacheTo1 appointmentTransfer = mapper.readValue(update.getJsonData(), AppointmentCacheTo1.class);

		Integration integration = integrationDao.find(update.getIntegrationId());
		IntegrationData integrationData = new IntegrationData(integration);

		appointmentService.updateAppointmentCache(integrationData, appointmentTransfer);
	}
}
