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
package org.oscarehr.integration.aqs.model;

import ca.cloudpractice.aqs.client.model.QueueAvailabilityDto;
import lombok.Getter;
import lombok.Setter;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilityDayTransfer;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer;

import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_FRIDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_MONDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_SATURDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_SUNDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_THURSDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_TUESDAY;
import static org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer.INDEX_WEDNESDAY;

@Getter @Setter
public class QueueAvailability
{
	private QueueAvailabilityDay sunday;
	private QueueAvailabilityDay monday;
	private QueueAvailabilityDay tuesday;
	private QueueAvailabilityDay wednesday;
	private QueueAvailabilityDay thursday;
	private QueueAvailabilityDay friday;
	private QueueAvailabilityDay saturday;

	public QueueAvailability(QueueAvailabilityDto availabilityDto)
	{
		this.sunday = getDayAvailability(availabilityDto.getSunday());
		this.monday = getDayAvailability(availabilityDto.getMonday());
		this.tuesday = getDayAvailability(availabilityDto.getTuesday());
		this.wednesday = getDayAvailability(availabilityDto.getWednesday());
		this.thursday = getDayAvailability(availabilityDto.getThursday());
		this.friday = getDayAvailability(availabilityDto.getFriday());
		this.saturday = getDayAvailability(availabilityDto.getSaturday());
	}

	public QueueAvailability(QueueAvailabilitySettingsTransfer availabilitySettingsDto)
	{
		this.sunday = getDayAvailability(availabilitySettingsDto, INDEX_SUNDAY);
		this.monday = getDayAvailability(availabilitySettingsDto, INDEX_MONDAY);
		this.tuesday = getDayAvailability(availabilitySettingsDto, INDEX_TUESDAY);
		this.wednesday = getDayAvailability(availabilitySettingsDto, INDEX_WEDNESDAY);
		this.thursday = getDayAvailability(availabilitySettingsDto, INDEX_THURSDAY);
		this.friday = getDayAvailability(availabilitySettingsDto, INDEX_FRIDAY);
		this.saturday = getDayAvailability(availabilitySettingsDto, INDEX_SATURDAY);
	}

	public QueueAvailabilityDto asAqsServerDto()
	{
		QueueAvailabilityDto dto = new QueueAvailabilityDto();
		dto.setSunday(asAqsServerDto(this.getSunday()));
		dto.setMonday(asAqsServerDto(this.getMonday()));
		dto.setTuesday(asAqsServerDto(this.getTuesday()));
		dto.setWednesday(asAqsServerDto(this.getWednesday()));
		dto.setThursday(asAqsServerDto(this.getThursday()));
		dto.setFriday(asAqsServerDto(this.getFriday()));
		dto.setSaturday(asAqsServerDto(this.getSaturday()));

		return dto;
	}

	private ca.cloudpractice.aqs.client.model.QueueAvailabilityDay asAqsServerDto(QueueAvailabilityDay day)
	{
		if(day != null)
		{
			return day.asAqsServerDto();
		}
		return null;
	}

	private QueueAvailabilityDay getDayAvailability(QueueAvailabilitySettingsTransfer transfer, int dayOfWeekIndex)
	{
		QueueAvailabilityDayTransfer[] bookingHours = transfer.getBookingHours();
		QueueAvailabilityDay availabilityDay = null;

		if(bookingHours.length > dayOfWeekIndex)
		{
			QueueAvailabilityDayTransfer availability = bookingHours[dayOfWeekIndex];

			// the aqs server expects only enabled day availability objects. use null to signify disabled
			if(availability.isEnabled())
			{
				availabilityDay = new QueueAvailabilityDay(availability);
			}

		}
		return availabilityDay;
	}

	private QueueAvailabilityDay getDayAvailability(ca.cloudpractice.aqs.client.model.QueueAvailabilityDay transfer)
	{
		if( transfer != null)
		{
			return new QueueAvailabilityDay(transfer);
		}
		return null;
	}
}
