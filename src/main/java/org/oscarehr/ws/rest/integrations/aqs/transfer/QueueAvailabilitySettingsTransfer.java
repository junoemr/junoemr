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
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;

@Getter @Setter
@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueAvailabilitySettingsTransfer implements Serializable
{
	public static final int INDEX_ISO_SUNDAY = 1;
	public static final int INDEX_ISO_MONDAY = 2;
	public static final int INDEX_ISO_TUESDAY = 3;
	public static final int INDEX_ISO_WEDNESDAY = 4;
	public static final int INDEX_ISO_THURSDAY = 5;
	public static final int INDEX_ISO_FRIDAY = 6;
	public static final int INDEX_ISO_SATURDAY = 7;

	private Boolean enabled;
	private HashMap<Integer, QueueAvailabilityDayTransfer> bookingHours;

	public QueueAvailabilitySettingsTransfer()
	{
		this.bookingHours = new HashMap<>();
	}

	public void setDay(Integer isoDayIndex, QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(isoDayIndex);
		this.bookingHours.put(isoDayIndex, transfer);
	}

	public QueueAvailabilityDayTransfer getDay(Integer isoDayIndex)
	{
		return this.bookingHours.get(isoDayIndex);
	}

	public void setSunday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_SUNDAY, transfer);
	}

	public void setMonday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_MONDAY, transfer);
	}

	public void setTuesday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_TUESDAY, transfer);
	}

	public void setWednesday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_WEDNESDAY, transfer);
	}

	public void setThursday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_THURSDAY, transfer);
	}

	public void setFriday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_FRIDAY, transfer);
	}

	public void setSaturday(QueueAvailabilityDayTransfer transfer)
	{
		this.setDay(INDEX_ISO_SATURDAY, transfer);
	}

	public QueueAvailabilityDayTransfer getSunday()
	{
		return this.getDay(INDEX_ISO_SUNDAY);
	}

	public QueueAvailabilityDayTransfer getMonday()
	{
		return this.getDay(INDEX_ISO_MONDAY);
	}

	public QueueAvailabilityDayTransfer getTuesday()
	{
		return this.getDay(INDEX_ISO_TUESDAY);
	}

	public QueueAvailabilityDayTransfer getWednesday()
	{
		return this.getDay(INDEX_ISO_WEDNESDAY);
	}

	public QueueAvailabilityDayTransfer getThursday()
	{
		return this.getDay(INDEX_ISO_THURSDAY);
	}

	public QueueAvailabilityDayTransfer getFriday()
	{
		return this.getDay(INDEX_ISO_FRIDAY);
	}

	public QueueAvailabilityDayTransfer getSaturday()
	{
		return this.getDay(INDEX_ISO_SATURDAY);
	}
}