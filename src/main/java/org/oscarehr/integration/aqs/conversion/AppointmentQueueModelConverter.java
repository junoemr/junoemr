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
import org.oscarehr.ws.rest.integrations.aqs.transfer.AppointmentQueueOnDemandSettingsTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.AppointmentQueueTo1;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilityDayTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class AppointmentQueueModelConverter extends AbstractModelConverter<AppointmentQueue, AppointmentQueueTo1>
{
	@Override
	public AppointmentQueueTo1 convert(AppointmentQueue input)
	{
		AppointmentQueueTo1 transfer = new AppointmentQueueTo1();
		BeanUtils.copyProperties(input, transfer, "name", "availability", "onDemandSettings");
		transfer.setQueueName(input.getName());

		QueueAvailability inputAvailability = input.getAvailability();
		QueueAvailabilitySettingsTransfer availabilitySettingsTransfer = new QueueAvailabilitySettingsTransfer();

		availabilitySettingsTransfer.setSunday(getAvailabilityTransfer(inputAvailability.getSunday()));
		availabilitySettingsTransfer.setMonday(getAvailabilityTransfer(inputAvailability.getMonday()));
		availabilitySettingsTransfer.setTuesday(getAvailabilityTransfer(inputAvailability.getTuesday()));
		availabilitySettingsTransfer.setWednesday(getAvailabilityTransfer(inputAvailability.getWednesday()));
		availabilitySettingsTransfer.setThursday(getAvailabilityTransfer(inputAvailability.getThursday()));
		availabilitySettingsTransfer.setFriday(getAvailabilityTransfer(inputAvailability.getFriday()));
		availabilitySettingsTransfer.setSaturday(getAvailabilityTransfer(inputAvailability.getSaturday()));
		availabilitySettingsTransfer.setEnabled(inputAvailability.getEnabled());

		transfer.setAvailabilitySettings(availabilitySettingsTransfer);
		transfer.setIsAvailable(input.getAvailable());


		AppointmentQueueOnDemandSettingsTransfer onDemandSettings = new AppointmentQueueOnDemandSettingsTransfer();
		onDemandSettings.setExpirationThresholdSeconds(input.getOnDemandSettings().getExpirationThresholdSeconds());
		onDemandSettings.setEnabled(input.getOnDemandSettings().getEnabled());
		transfer.setAppointmentQueueOnDemandSettings(onDemandSettings);

		return transfer;
	}

	private QueueAvailabilityDayTransfer getAvailabilityTransfer(QueueAvailabilityDay day)
	{
		QueueAvailabilityDayTransfer transfer = new QueueAvailabilityDayTransfer();
		transfer.setEnabled(day.getEnabled());
		transfer.setStartTime(day.getStart());
		transfer.setEndTime(day.getEnd());

		return transfer;
	}
}
