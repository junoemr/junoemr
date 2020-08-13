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
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilitySettingsTransfer;

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
		this.sunday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.monday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.tuesday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.wednesday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.thursday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.friday = new QueueAvailabilityDay(availabilityDto.getSaturday());
		this.saturday = new QueueAvailabilityDay(availabilityDto.getSaturday());
	}

	public QueueAvailability(QueueAvailabilitySettingsTransfer availabilitySettingsDto)
	{
		this.monday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[0]);
		this.tuesday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[1]);
		this.wednesday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[2]);
		this.thursday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[3]);
		this.friday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[4]);
		this.saturday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[5]);
		this.sunday = new QueueAvailabilityDay(availabilitySettingsDto.getBookingHours()[6]);
	}

	public QueueAvailabilityDto asAqsServerDto()
	{
		QueueAvailabilityDto dto = new QueueAvailabilityDto();
		dto.setMonday(this.getMonday().asAqsServerDto());
		dto.setTuesday(this.getTuesday().asAqsServerDto());
		dto.setWednesday(this.getWednesday().asAqsServerDto());
		dto.setThursday(this.getThursday().asAqsServerDto());
		dto.setFriday(this.getFriday().asAqsServerDto());
		dto.setSaturday(this.getSaturday().asAqsServerDto());
		dto.setSunday(this.getSunday().asAqsServerDto());

		return dto;
	}
}
