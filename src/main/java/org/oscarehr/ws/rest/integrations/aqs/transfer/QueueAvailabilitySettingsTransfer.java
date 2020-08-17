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

package org.oscarehr.ws.rest.integrations.aqs.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.oscarehr.integration.aqs.model.QueueAvailability;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalTime;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueAvailabilitySettingsTransfer implements Serializable
{
	private Boolean enabled;
	private QueueAvailabilityDayTransfer[] bookingHours;

	public QueueAvailabilitySettingsTransfer()
	{
		this.bookingHours = new QueueAvailabilityDayTransfer[7];
		this.enabled = false;
		initDefaultBookingHours();
	}

	public QueueAvailabilitySettingsTransfer(QueueAvailability availability)
	{
		this.bookingHours = new QueueAvailabilityDayTransfer[7];
		this.enabled = (availability != null);

		if(enabled)
		{
			this.bookingHours[0] = new QueueAvailabilityDayTransfer(1, availability.getSunday());
			this.bookingHours[1] = new QueueAvailabilityDayTransfer(2, availability.getMonday());
			this.bookingHours[2] = new QueueAvailabilityDayTransfer(3, availability.getTuesday());
			this.bookingHours[3] = new QueueAvailabilityDayTransfer(4, availability.getWednesday());
			this.bookingHours[4] = new QueueAvailabilityDayTransfer(5, availability.getThursday());
			this.bookingHours[5] = new QueueAvailabilityDayTransfer(6, availability.getFriday());
			this.bookingHours[6] = new QueueAvailabilityDayTransfer(7, availability.getSaturday());
		}
		else
		{
			initDefaultBookingHours();
		}
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public QueueAvailabilityDayTransfer[] getBookingHours()
	{
		return bookingHours;
	}

	public void setBookingHours(QueueAvailabilityDayTransfer[] bookingHours)
	{
		this.bookingHours = bookingHours;
	}

	private void initDefaultBookingHours()
	{
		this.bookingHours[0] = new QueueAvailabilityDayTransfer(1, false, LocalTime.of(8,0), LocalTime.of(14,0));
		this.bookingHours[1] = new QueueAvailabilityDayTransfer(2, true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[2] = new QueueAvailabilityDayTransfer(3, true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[3] = new QueueAvailabilityDayTransfer(4, true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[4] = new QueueAvailabilityDayTransfer(5, true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[5] = new QueueAvailabilityDayTransfer(6, true, LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[6] = new QueueAvailabilityDayTransfer(7, false, LocalTime.of(8,0), LocalTime.of(14,0));
	}
}