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

import ca.cloudpractice.aqs.client.model.OnDemandQueueSettingsDto;
import ca.cloudpractice.aqs.client.model.QueueAvailabilityDto;
import ca.cloudpractice.aqs.client.model.QueueInput;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueueAvailability;
import org.oscarehr.integration.aqs.model.QueueAvailabilityDay;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class AppointmentQueueIntegrationModelConverter extends AbstractModelConverter<AppointmentQueue, QueueInput>
{
	@Override
	public QueueInput convert(AppointmentQueue input)
	{
		QueueInput queueInput = new QueueInput();
		queueInput.setName(input.getName());
		queueInput.setQueueLimit(input.getQueueLimit());
		queueInput.setDefaultAppointmentDurationMinutes(input.getDefaultAppointmentDurationMinutes());

		// only send odb settings if the enabled flag is set.
		if (input.getOnDemandSettings().getEnabled())
		{
			OnDemandQueueSettingsDto onDemandSettingsDto = new OnDemandQueueSettingsDto();
			onDemandSettingsDto.setExpirationThresholdSeconds(input.getOnDemandSettings().getExpirationThresholdSeconds());
			queueInput.setOnDemandSettings(onDemandSettingsDto);
		}

		// only send availability settings if the enabled flag is set.
		if(input.getAvailability().getEnabled())
		{
			QueueAvailability availability = input.getAvailability();

			QueueAvailabilityDto dto = new QueueAvailabilityDto();
			dto.setSunday(asAqsServerDto(availability.getSunday()));
			dto.setMonday(asAqsServerDto(availability.getMonday()));
			dto.setTuesday(asAqsServerDto(availability.getTuesday()));
			dto.setWednesday(asAqsServerDto(availability.getWednesday()));
			dto.setThursday(asAqsServerDto(availability.getThursday()));
			dto.setFriday(asAqsServerDto(availability.getFriday()));
			dto.setSaturday(asAqsServerDto(availability.getSaturday()));

			queueInput.setAvailability(dto);
		}
		return queueInput;
	}

	private ca.cloudpractice.aqs.client.model.QueueAvailabilityDay asAqsServerDto(QueueAvailabilityDay day)
	{
		ca.cloudpractice.aqs.client.model.QueueAvailabilityDay dto = null;

		// only send availability day settings if the enabled flag is set.
		if (day.getEnabled())
		{
			dto = new ca.cloudpractice.aqs.client.model.QueueAvailabilityDay();
			dto.setStart(ConversionUtils.toTimeString(day.getStart()));
			dto.setStop(ConversionUtils.toTimeString(day.getEnd()));
		}
		return dto;
	}
}
