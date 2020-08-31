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

import java.io.Serializable;

@Getter @Setter
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
	private QueueAvailabilityDayTransfer sunday;
	private QueueAvailabilityDayTransfer monday;
	private QueueAvailabilityDayTransfer tuesday;
	private QueueAvailabilityDayTransfer wednesday;
	private QueueAvailabilityDayTransfer thursday;
	private QueueAvailabilityDayTransfer friday;
	private QueueAvailabilityDayTransfer saturday;

	public QueueAvailabilitySettingsTransfer()
	{
	}

	public void setSunday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_SUNDAY);
		this.sunday = transfer;
	}

	public void setMonday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_MONDAY);
		this.monday = transfer;
	}

	public void setTuesday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_TUESDAY);
		this.tuesday = transfer;
	}

	public void setWednesday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_WEDNESDAY);
		this.wednesday = transfer;
	}

	public void setThursday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_THURSDAY);
		this.thursday = transfer;
	}

	public void setFriday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_FRIDAY);
		this.friday = transfer;
	}

	public void setSaturday(QueueAvailabilityDayTransfer transfer)
	{
		transfer.setWeekdayNumber(INDEX_ISO_SATURDAY);
		this.saturday = transfer;
	}
}