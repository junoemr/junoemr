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

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.time.LocalTime;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueAvailabilityDayTransfer implements Serializable
{
	private String dayOfWeek;
	private boolean enabled;
	private LocalTime startTime;
	private LocalTime endTime;

	public QueueAvailabilityDayTransfer()
	{
	}
	public QueueAvailabilityDayTransfer(String day, boolean enabled, LocalTime start, LocalTime end)
	{
		this.dayOfWeek = day;
		this.enabled = enabled;
		this.startTime = start;
		this.endTime = end;
	}

	public String getDayOfWeek()
	{
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek)
	{
		this.dayOfWeek = dayOfWeek;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public LocalTime getStartTime()
	{
		return startTime;
	}

	public void setStartTime(LocalTime startTime)
	{
		this.startTime = startTime;
	}

	public LocalTime getEndTime()
	{
		return endTime;
	}

	public void setEndTime(LocalTime endTime)
	{
		this.endTime = endTime;
	}
}