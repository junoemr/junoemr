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
package org.oscarehr.appointment.model;

import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.managers.AppointmentManager;
import oscar.OscarProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class AppointmentStatusList
{
	private static final String STATUS_BILLED = "B";
	private static final String STATUS_SIGNED = "S";
	private static final Map<String, String> titleMap;

	static
	{
		Map<String, String> buildMap = new HashMap<>();

		buildMap.put("t", "oscar.appt.ApptStatusData.msgTodo");
		buildMap.put("T", "oscar.appt.ApptStatusData.msgDaySheetPrinted");
		buildMap.put("H", "oscar.appt.ApptStatusData.msgHere");
		buildMap.put("P", "oscar.appt.ApptStatusData.msgPicked");
		buildMap.put("E", "oscar.appt.ApptStatusData.msgEmpty");
		buildMap.put("N", "oscar.appt.ApptStatusData.msgNoShow");
		buildMap.put("C", "oscar.appt.ApptStatusData.msgCanceled");
		buildMap.put("B", "oscar.appt.ApptStatusData.msgBilled");

		titleMap = Collections.unmodifiableMap(buildMap);
	}

	private List<String> orderedStatusList;
	private Map<String, String> descriptionMap;
	private boolean editable;

	public static AppointmentStatusList factory(AppointmentManager appointmentManager)
	{
		List<String> orderedStatusList = new ArrayList<>();
		Map<String, String> descriptionMap = new HashMap<>();

		for(AppointmentStatus appointmentStatus: appointmentManager.getAppointmentStatuses())
		{
			String status = appointmentStatus.getStatus();

			// Leave billed out of status rotation
			if(!STATUS_BILLED.equals(status))
			{
				orderedStatusList.add(appointmentStatus.getStatus());
			}
			descriptionMap.put(appointmentStatus.getStatus(), appointmentStatus.getDescription());
		}

		return new AppointmentStatusList(orderedStatusList, descriptionMap);
	}

	public AppointmentStatusList(List<String> orderedStatusList, Map<String, String> descriptionMap)
	{
		this.orderedStatusList = orderedStatusList;
		this.descriptionMap = descriptionMap;
		this.editable = OscarProperties.getInstance().isEditAppointmentStatusEnabled();
	}

	public String getStatusAfter(String status)
	{
		String statusChar = status.substring(0, 1);
		int currentStatusIndex = orderedStatusList.indexOf(statusChar);

		// Return current status if not found
		if(currentStatusIndex < 0)
		{
			return status;
		}

		int nextStatusIndex = (currentStatusIndex + 1) % orderedStatusList.size();

		return orderedStatusList.get(nextStatusIndex) + getModifierChar(status);
	}

	public String getTitle(String status, Locale locale)
	{
		String statusChar = status.substring(0, 1);
		String modifierChar = getModifierChar(status);

		String title = "";

		ResourceBundle bundle = ResourceBundle.getBundle("oscarResources", locale);

		if(bundle != null && !editable && titleMap.containsKey(statusChar))
		{
			title = bundle.getString(titleMap.get(statusChar));
		}
		else if(descriptionMap.containsKey(statusChar))
		{
			title = descriptionMap.get(statusChar);

			if(!"".equals(modifierChar))
			{
				if(STATUS_SIGNED.equals(modifierChar))
				{
					title += "/Signed";
				}
				else{
					title += "/Verified";
				}
			}
		}

		return title;
	}

	private String getModifierChar(String status)
	{
		String modifierChar = "";

		if(status.length() > 1)
		{
			modifierChar = status.substring(1, 2);
		}

		return modifierChar;
	}
}
