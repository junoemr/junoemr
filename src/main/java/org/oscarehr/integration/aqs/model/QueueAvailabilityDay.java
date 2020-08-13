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

import lombok.Getter;
import lombok.Setter;
import org.oscarehr.ws.rest.integrations.aqs.transfer.QueueAvailabilityDayTransfer;
import oscar.util.ConversionUtils;

import java.time.LocalTime;

@Getter @Setter
public class QueueAvailabilityDay
{
	private Boolean enabled;
	private LocalTime start;
	private LocalTime stop;

	public QueueAvailabilityDay(ca.cloudpractice.aqs.client.model.QueueAvailabilityDay aqsServerDto)
	{
		this.enabled = true; // TODO - where does this live?
		this.start = ConversionUtils.toLocalTime(aqsServerDto.getStart());
		this.stop = ConversionUtils.toLocalTime(aqsServerDto.getStop());
	}
	public QueueAvailabilityDay(QueueAvailabilityDayTransfer availabilityDayDto)
	{
		this.enabled = availabilityDayDto.isEnabled();
		this.start = availabilityDayDto.getStartTime();
		this.stop = availabilityDayDto.getEndTime();
	}

	public ca.cloudpractice.aqs.client.model.QueueAvailabilityDay asAqsServerDto()
	{
		ca.cloudpractice.aqs.client.model.QueueAvailabilityDay dto = new ca.cloudpractice.aqs.client.model.QueueAvailabilityDay();
		dto.setStart(ConversionUtils.toTimeString(this.getStart()));
		dto.setStop(ConversionUtils.toTimeString(this.getStop()));
		// TODO set enabled flag

		return dto;
	}
}
