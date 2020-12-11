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
package org.oscarehr.integration.aqs.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueueAvailability;
import org.oscarehr.integration.aqs.model.QueueAvailabilityDay;
import org.oscarehr.integration.aqs.model.QueueOnDemandSettings;
import org.oscarehr.ws.rest.integrations.aqs.transfer.AppointmentQueueTo1;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilityDayTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppointmentQueueTransferConverter extends AbstractModelConverter<AppointmentQueueTo1, AppointmentQueue>
{
	@Override
	public AppointmentQueue convert(AppointmentQueueTo1 input)
	{
		AppointmentQueue model = new AppointmentQueue();
		BeanUtils.copyProperties(input, model, "name", "availability", "onDemandSettings");
		model.setName(input.getQueueName());

		QueueAvailability availability = new QueueAvailability();

		QueueAvailabilitySettingsTransfer availabilitySettings = input.getAvailabilitySettings();
		availability.setEnabled(availabilitySettings.getEnabled());
		availability.setSunday(getAvailabilityDayModel(availabilitySettings.getSunday()));
		availability.setMonday(getAvailabilityDayModel(availabilitySettings.getMonday()));
		availability.setTuesday(getAvailabilityDayModel(availabilitySettings.getTuesday()));
		availability.setWednesday(getAvailabilityDayModel(availabilitySettings.getWednesday()));
		availability.setThursday(getAvailabilityDayModel(availabilitySettings.getThursday()));
		availability.setFriday(getAvailabilityDayModel(availabilitySettings.getFriday()));
		availability.setSaturday(getAvailabilityDayModel(availabilitySettings.getSaturday()));

		model.setAvailability(availability);
		model.setAvailable(input.getIsAvailable());

		QueueOnDemandSettings onDemandSettings = new QueueOnDemandSettings();
		onDemandSettings.setExpirationThresholdSeconds(input.getAppointmentQueueOnDemandSettings().getExpirationThresholdSeconds());
		onDemandSettings.setEnabled(input.getAppointmentQueueOnDemandSettings().getEnabled());
		model.setOnDemandSettings(onDemandSettings);

		return model;
	}

	private QueueAvailabilityDay getAvailabilityDayModel(QueueAvailabilityDayTransfer transfer)
	{
		QueueAvailabilityDay day = new QueueAvailabilityDay();
		day.setEnabled(transfer.isEnabled());
		day.setStart(transfer.getStartTime());
		day.setEnd(transfer.getEndTime());
		return day;
	}
}
