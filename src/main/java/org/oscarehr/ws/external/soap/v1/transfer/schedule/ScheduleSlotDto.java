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

package org.oscarehr.ws.external.soap.v1.transfer.schedule;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ScheduleSlotDto
{
	String date;
	String startTime;
	int duration;

	public ScheduleSlotDto(String date, String startTime, int duration)
	{
		this.date = date;
		this.startTime = startTime;
		this.duration = duration;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	public String getStartTime()
	{
		return startTime;
	}

	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}

	public LocalDateTime getDateTime()
	{
		return LocalDateTime.parse(String.format("%sT%s", date, startTime));
	}

	/**
	 *
	 * @param scheduleSlots - A provider's schedule availability
	 * @param dateTimePivot - dateTime of slot to get surrounding slots for
	 * @param threshold - n Granularity
	 * @param granularity - Time unit to define the slot time threshold before and after dateTimePivot
	 * @return - Return all slots before and after dateTimePivot within the specified threshold.
	 *           Returns an empty list if dateTimePivot doesn't exist as a slot itself.
	 */
	public static List<ScheduleSlotDto> getSlotsInThreshold(List<ScheduleSlotDto> scheduleSlots, LocalDateTime dateTimePivot,
	                                                        long threshold, ChronoUnit granularity)
	{
		boolean slotIsAvailable = false;

		List<ScheduleSlotDto> slotsInThreshold = new ArrayList<>();

		for (ScheduleSlotDto slot : scheduleSlots)
		{
			LocalDateTime slotDateTime = slot.getDateTime();
			long nGranBetween = granularity.between(slotDateTime, dateTimePivot);

			if (nGranBetween < -threshold)
			{
				break;
			}
			else if (Math.abs(nGranBetween) <= threshold)
			{
				if (!slotIsAvailable)
				{
					slotIsAvailable = dateTimePivot.equals(slotDateTime);
				}

				slotsInThreshold.add(slot);
			}
		}

		if (!slotIsAvailable)
		{
			slotsInThreshold.clear();
		}

		return slotsInThreshold;
	}
}
