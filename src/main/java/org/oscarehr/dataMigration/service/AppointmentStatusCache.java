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
package org.oscarehr.dataMigration.service;

import org.oscarehr.appointment.service.AppointmentStatusService;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

//TODO - better way to look up intermediate objects
@Service
public class AppointmentStatusCache
{
	private static ConcurrentMap<String, AppointmentStatus> appointmentStatusCodeModelMap;
	private static ConcurrentMap<String, AppointmentStatus> appointmentStatusNameModelMap;

	@Autowired
	private AppointmentStatusService appointmentStatusService;

	public AppointmentStatusCache()
	{
	}

	public synchronized AppointmentStatus findByCode(String code)
	{
		if(appointmentStatusCodeModelMap == null)
		{
			appointmentStatusCodeModelMap = appointmentStatusService.getAppointmentStatusCodeModelMap();
		}
		return appointmentStatusCodeModelMap.get(code);
	}

	public synchronized AppointmentStatus findByName(String name)
	{
		if(appointmentStatusNameModelMap == null)
		{
			appointmentStatusNameModelMap = appointmentStatusService.getAppointmentStatusNameModelMap();
		}
		return appointmentStatusNameModelMap.get(name);
	}

	public synchronized void clear()
	{
		appointmentStatusCodeModelMap = null;
		appointmentStatusNameModelMap = null;
	}
}
