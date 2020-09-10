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

import ca.cloudpractice.aqs.client.model.QueueAvailabilityDto;
import ca.cloudpractice.aqs.client.model.QueueDto;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueueAvailability;
import org.oscarehr.integration.aqs.model.QueueAvailabilityDay;
import org.oscarehr.integration.aqs.model.QueueOnDemandSettings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AppointmentQueueIntegrationTransferConverter extends AbstractModelConverter<QueueDto, AppointmentQueue>
{
	@Override
	public AppointmentQueue convert(QueueDto input)
	{
		AppointmentQueue model = new AppointmentQueue();
		BeanUtils.copyProperties(input, model, "id", "availability", "onDemandSettings");
		model.setRemoteId(input.getId());


		// convert availability settings
		model.setAvailable(input.getAvailable());
		model.setAvailability(getAvailability(input.getAvailability()));

		// convert on-demand settings
		boolean isOnDemand = (input.getOnDemandSettings() != null);
		QueueOnDemandSettings onDemandSettings = new QueueOnDemandSettings();
		onDemandSettings.setEnabled(isOnDemand);
		if(isOnDemand)
		{
			onDemandSettings.setExpirationThresholdSeconds(input.getOnDemandSettings().getExpirationThresholdSeconds());
		}
		model.setOnDemandSettings(onDemandSettings);

		return model;
	}

	private QueueAvailability getAvailability(QueueAvailabilityDto inputAvailability)
	{
		boolean enabled = (inputAvailability != null);
		QueueAvailability availabilitySettings = new QueueAvailability();
		availabilitySettings.setEnabled(enabled);
		if(enabled)
		{
			availabilitySettings.setSunday(getDayAvailability(inputAvailability.getSunday()));
			availabilitySettings.setMonday(getDayAvailability(inputAvailability.getMonday()));
			availabilitySettings.setTuesday(getDayAvailability(inputAvailability.getTuesday()));
			availabilitySettings.setWednesday(getDayAvailability(inputAvailability.getWednesday()));
			availabilitySettings.setThursday(getDayAvailability(inputAvailability.getThursday()));
			availabilitySettings.setFriday(getDayAvailability(inputAvailability.getFriday()));
			availabilitySettings.setSaturday(getDayAvailability(inputAvailability.getSaturday()));
		}

		return availabilitySettings;
	}

	private QueueAvailabilityDay getDayAvailability(ca.cloudpractice.aqs.client.model.QueueAvailabilityDay transfer)
	{
		QueueAvailabilityDay dayModel = new QueueAvailabilityDay();
		if(transfer != null)
		{
			dayModel.setEnabled(true);
			dayModel.setStart(ConversionUtils.toLocalTime(transfer.getStart()));
			dayModel.setEnd(ConversionUtils.toLocalTime(transfer.getStop()));
		}
		return dayModel;
	}
}
