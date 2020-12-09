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
package org.oscarehr.demographicImport.service;

import org.oscarehr.appointment.service.AppointmentStatusService;
import org.oscarehr.demographicImport.model.appointment.AppointmentStatus;
import org.oscarehr.util.SpringUtils;

import java.util.Map;

//TODO - better way to look up intermediate objects
public class AppointmentStatusCache
{
	private static Map<String, AppointmentStatus> appointmentStatusCodeModelMap;
	private static Map<String, AppointmentStatus> appointmentStatusNameModelMap;

	public static AppointmentStatus findByCode(String code)
	{
		if(appointmentStatusCodeModelMap == null)
		{
			AppointmentStatusService appointmentStatusService = SpringUtils.getBean(AppointmentStatusService.class);
			appointmentStatusCodeModelMap = appointmentStatusService.getAppointmentStatusCodeModelMap();
		}
		return appointmentStatusCodeModelMap.get(code);
	}

	public static AppointmentStatus findByName(String name)
	{
		if(appointmentStatusNameModelMap == null)
		{
			AppointmentStatusService appointmentStatusService = SpringUtils.getBean(AppointmentStatusService.class);
			appointmentStatusNameModelMap = appointmentStatusService.getAppointmentStatusNameModelMap();
		}
		return appointmentStatusNameModelMap.get(name);
	}

	public static void clearAll()
	{
		appointmentStatusCodeModelMap = null;
		appointmentStatusNameModelMap = null;
	}
}
