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
	public static final int INDEX_SUNDAY = 0;
	public static final int INDEX_MONDAY = 1;
	public static final int INDEX_TUESDAY = 2;
	public static final int INDEX_WEDNESDAY = 3;
	public static final int INDEX_THURSDAY = 4;
	public static final int INDEX_FRIDAY = 5;
	public static final int INDEX_SATURDAY = 6;

	public static final int INDEX_ISO_SUNDAY = 1;
	public static final int INDEX_ISO_MONDAY = 2;
	public static final int INDEX_ISO_TUESDAY = 3;
	public static final int INDEX_ISO_WEDNESDAY = 4;
	public static final int INDEX_ISO_THURSDAY = 5;
	public static final int INDEX_ISO_FRIDAY = 6;
	public static final int INDEX_ISO_SATURDAY = 7;

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
			this.bookingHours[INDEX_SUNDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_SUNDAY,    availability.getSunday());
			this.bookingHours[INDEX_MONDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_MONDAY,    availability.getMonday());
			this.bookingHours[INDEX_TUESDAY]    = new QueueAvailabilityDayTransfer(INDEX_ISO_TUESDAY,   availability.getTuesday());
			this.bookingHours[INDEX_WEDNESDAY]  = new QueueAvailabilityDayTransfer(INDEX_ISO_WEDNESDAY, availability.getWednesday());
			this.bookingHours[INDEX_THURSDAY]   = new QueueAvailabilityDayTransfer(INDEX_ISO_THURSDAY,  availability.getThursday());
			this.bookingHours[INDEX_FRIDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_FRIDAY,    availability.getFriday());
			this.bookingHours[INDEX_SATURDAY]   = new QueueAvailabilityDayTransfer(INDEX_ISO_SATURDAY,  availability.getSaturday());
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
		this.bookingHours[INDEX_SUNDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_SUNDAY,   false,
				LocalTime.of(8,0), LocalTime.of(14,0));
		this.bookingHours[INDEX_MONDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_MONDAY,   true,
				LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[INDEX_TUESDAY]    = new QueueAvailabilityDayTransfer(INDEX_ISO_TUESDAY,  true,
				LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[INDEX_WEDNESDAY]  = new QueueAvailabilityDayTransfer(INDEX_ISO_WEDNESDAY,true,
				LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[INDEX_THURSDAY]   = new QueueAvailabilityDayTransfer(INDEX_ISO_THURSDAY, true,
				LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[INDEX_FRIDAY]     = new QueueAvailabilityDayTransfer(INDEX_ISO_FRIDAY,   true,
				LocalTime.of(8,0), LocalTime.of(16,0));
		this.bookingHours[INDEX_SATURDAY]   = new QueueAvailabilityDayTransfer(INDEX_ISO_SATURDAY, false,
				LocalTime.of(8,0), LocalTime.of(14,0));
	}
}