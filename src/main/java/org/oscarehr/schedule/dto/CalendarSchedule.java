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
package org.oscarehr.schedule.dto;

import java.util.List;

public class CalendarSchedule
{
	private String groupName;

	private Integer preferredSlotDuration;

	private List<String> providerIdList;

	private List<Integer> hiddenDaysList;

	private Boolean visibleSchedules;

	//List of all calendar events combined
	private List<CalendarEvent> eventList;

	public String getGroupName()
	{
		return groupName;
	}

	public void setGroupName(String groupName)
	{
		this.groupName = groupName;
	}

	public Integer getPreferredSlotDuration()
	{
		return preferredSlotDuration;
	}

	public void setPreferredSlotDuration(Integer preferredSlotDuration)
	{
		this.preferredSlotDuration = preferredSlotDuration;
	}

	public List<String> getProviderIdList()
	{
		return providerIdList;
	}

	public void setProviderIdList(List<String> providerIdList)
	{
		this.providerIdList = providerIdList;
	}

	public List<Integer> getHiddenDaysList()
	{
		return hiddenDaysList;
	}

	public void setHiddenDaysList(List<Integer> hiddenDaysList)
	{
		this.hiddenDaysList = hiddenDaysList;
	}

	public Boolean getVisibleSchedules()
	{
		return visibleSchedules;
	}

	public void setVisibleSchedules(Boolean visibleSchedules)
	{
		this.visibleSchedules = visibleSchedules;
	}

	public List<CalendarEvent> getEventList()
	{
		return eventList;
	}

	public void setEventList(List<CalendarEvent> eventList)
	{
		this.eventList = eventList;
	}
}
