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
package org.oscarehr.appointment.service;

import org.oscarehr.appointment.dao.AppointmentStatusDao;
import org.oscarehr.appointment.dto.CalendarAppointmentStatus;
import org.oscarehr.appointment.model.AppointmentStatusList;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.managers.AppointmentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentStatusService
{
	@Autowired
	private AppointmentManager appointmentManager;
	
	@Autowired
	private AppointmentStatusDao appointmentStatusDao;

	/**
	 * Gets a list of appointment statuses for use in the calendar.
	 * @return List of appointment statuses.
	 */
	public List<CalendarAppointmentStatus> getCalendarAppointmentStatusList()
	{
		AppointmentStatusList appointmentStatusList = AppointmentStatusList.factory(appointmentManager);

		return appointmentStatusList.getCalendarAppointmentStatusList();
	}
	
	public List<AppointmentStatus> getAppointmentStatuses()
	{
		return appointmentManager.getAppointmentStatuses();
	}
	
	public AppointmentStatus getAppointmentStatusById(Integer id)
	{
		return appointmentStatusDao.find(id);
	}
	
	public void updateAppointmentStatus(AppointmentStatus status)
	{
		appointmentStatusDao.merge(status);
	}
	
	/**
	 * Create a new appointment status. This method will automatically set a status code based on the next
	 * available unused code.  Any existing status code passed into this method will be lost.
	 *
	 * @param status AppointmentStatus to persist
	 */
	public void createAppointmentStatus(AppointmentStatus status)
	{
		status.setStatus(getNextAvailableStatusCode());
		appointmentStatusDao.persist(status);
	}
	
	/**
	 * Return the next available status code sorted alphabetically.  The lowercase set of letters has priority
	 * over the uppercase set.
	 *
	 * @return String unused appointment status code.
	 */
	private String getNextAvailableStatusCode()
	{
		// Method is private to make it unavailable to non-transactional code
		// No need to be clever here.  Could also be implemented with a regular unordered set.
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		TreeSet<Character> validCodes = alphabet.chars().mapToObj(e -> (char)e).collect(Collectors.toCollection(TreeSet::new));
		
		List<AppointmentStatus> existingStatuses = appointmentStatusDao.findAll();
		for (AppointmentStatus status : existingStatuses)
		{
			Character usedCode = status.getStatus().toCharArray()[0];
			validCodes.remove(usedCode);
		}
		
		return validCodes.first().toString();
	}
}
