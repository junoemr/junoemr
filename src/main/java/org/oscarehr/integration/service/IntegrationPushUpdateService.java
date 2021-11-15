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
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.dao.IntegrationPushUpdateDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.model.IntegrationPushUpdate;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentCacheTo1;
import org.oscarehr.integration.myhealthaccess.dto.ClinicUserLoginTokenTo1;
import org.oscarehr.integration.myhealthaccess.dto.integrationPushUpdate.PatientConnectionTo1;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.service.AppointmentService;
import org.oscarehr.integration.myhealthaccess.service.ClinicService;
import org.oscarehr.integration.myhealthaccess.service.PatientService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("myHealthAppointmentService")
	private AppointmentService appointmentService;

	@Autowired
	private ClinicService clinicService;

	@Autowired
	private PatientService patientService;

	@Autowired
	private IntegrationDao integrationDao;

	@Autowired
	private IntegrationService integrationService;

	@Autowired
	private IntegrationPushUpdateDao integrationPushUpdateDao;

	@Autowired
	private ServerStateHandler serverStateHandler;

	@Autowired
	private DemographicDao demographicDao;

	public void queueAppointmentCacheUpdate(Integration integration, AppointmentCacheTo1 appointment) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		String jsonData = mapper.writeValueAsString(appointment);

		IntegrationPushUpdate pushUpdate = new IntegrationPushUpdate();
		pushUpdate.setIntegrationType(INTEGRATION_TYPE_MHA);
		pushUpdate.setIntegrationId(integration.getId());
		pushUpdate.setUpdateType(IntegrationPushUpdate.UPDATE_TYPE.APPOINTMENT_CACHE);
		pushUpdate.setStatus(IntegrationPushUpdate.PUSH_STATUS.QUEUED);
		pushUpdate.setJsonData(jsonData);
		pushUpdate.setTargetId(appointment.getId());

		integrationPushUpdateDao.persist(pushUpdate);
	}

	public void queuePatientConnectionUpdate(Integer securityNo, Integer demographicId, Boolean rejected) throws JsonProcessingException
	{
		if (!integrationService.hasMyHealthAccessIntegration())
		{// no mha integrations configured
			return;
		}

		ObjectMapper mapper = new ObjectMapper();
		PatientConnectionTo1 patientConnectionTo1 = new PatientConnectionTo1(securityNo, demographicId, rejected);

		IntegrationPushUpdate pushUpdate = new IntegrationPushUpdate();
		pushUpdate.setIntegrationType(INTEGRATION_TYPE_MHA);
		pushUpdate.setSecurityNo(securityNo);
		pushUpdate.setUpdateType(IntegrationPushUpdate.UPDATE_TYPE.PATIENT_CONNECTION);
		pushUpdate.setStatus(IntegrationPushUpdate.PUSH_STATUS.QUEUED);
		pushUpdate.setJsonData(mapper.writeValueAsString(patientConnectionTo1));
		pushUpdate.setTargetId(demographicId.toString());

		integrationPushUpdateDao.persist(pushUpdate);
	}

	public void sendQueuedUpdates()
	{
		ObjectMapper mapper = new ObjectMapper();
		List<IntegrationPushUpdate> unsentUpdates = integrationPushUpdateDao.findUnsent(INTEGRATION_TYPE_MHA);
		ArrayList<String> failedIdList = new ArrayList<>();

		if(!unsentUpdates.isEmpty() && serverStateHandler.isThisServerMaster())
		{
			logger.info("pushing " + unsentUpdates.size() + " integration updates");
			for(IntegrationPushUpdate update : unsentUpdates)
			{

				// if an error status found, skip it and all subsequent updates by id
				if(update.getStatus() == IntegrationPushUpdate.PUSH_STATUS.ERROR)
				{
					if (update.hasTarget())
					{
						failedIdList.add(update.getTargetId());
					}
					logger.error("Integration update failed [" + update.getId() + "] " +
							" is in an error state and cannot be pushed. All future updates" +
							" for target Id [" + update.getTargetId() + "] blocked.");
					continue;
				}

				// skip sending subsequent updates for appointments that have failed
				if(update.hasTarget() && failedIdList.contains(update.getTargetId()))
				{
					continue;
				}

				try
				{
					sendQueuedUpdate(mapper, update);

					update.setStatus(IntegrationPushUpdate.PUSH_STATUS.SENT);
					update.setSentAt(new Date());
				}
				catch(Exception e)
				{
					logger.error("Error sending integration update [" + update.getId() + "]", e);
					if (update.hasTarget())
					{
						failedIdList.add(update.getTargetId());
					}

					// +1 to account for increment that is not added yet
					if(update.getSendCount() + 1 >= MAX_SEND_ATTEMPTS)
					{
						update.recordError(e);
					}
				}
				finally
				{
					update.incrementSendCount();
					integrationPushUpdateDao.merge(update);
				}
			}
		}
	}

	private void sendQueuedUpdate(ObjectMapper mapper, IntegrationPushUpdate update) throws IOException
	{
		switch (update.getUpdateType())
		{
			case APPOINTMENT_CACHE:
				handleAppointmentUpdate(mapper, update);
				break;
			case PATIENT_CONNECTION:
				handlePatientConnectionUpdate(mapper, update);
				break;
		}
	}

	private void handleAppointmentUpdate(ObjectMapper mapper, IntegrationPushUpdate update) throws IOException
	{
		AppointmentCacheTo1 appointmentTransfer = mapper.readValue(update.getJsonData(), AppointmentCacheTo1.class);

		Integration integration = integrationDao.find(update.getIntegrationId());
		IntegrationData integrationData = new IntegrationData(integration);

		appointmentService.updateAppointmentCache(integrationData, appointmentTransfer);
	}

	private void handlePatientConnectionUpdate(ObjectMapper mapper, IntegrationPushUpdate update) throws IOException
	{
		PatientConnectionTo1 patientConnectionTo1 = mapper.readValue(update.getJsonData(), PatientConnectionTo1.class);

		for (Integration integration : integrationService.getMyHealthAccessIntegrations())
		{
			ClinicUserLoginTokenTo1 loginTokenTo1 = clinicService.loginOrCreateClinicUser(integration, patientConnectionTo1.getSecurityNo());
			Demographic demographic = demographicDao.find(patientConnectionTo1.getDemographicNo());
			if (demographic == null)
			{
				throw new RecordNotFoundException("No demographic with demographic_no: [" + patientConnectionTo1.getDemographicNo() + "]");
			}

			try
			{
				patientService.updatePatientConnection(integration, loginTokenTo1.getToken(), demographic, patientConnectionTo1.getRejected());
			}
			catch(RecordNotFoundException e)
			{
				// No MHA patient for this demographic. suppress.
			}
		}
	}
}
