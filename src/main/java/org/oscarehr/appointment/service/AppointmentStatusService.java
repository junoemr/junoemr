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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AppointmentStatusService
{
	@Autowired
	private AppointmentManager appointmentManager;
	
	@Autowired
	private AppointmentStatusDao appointmentStatusDao;

	private enum ReorderDirection {
		UP,
		DOWN,
	}

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
	 * Return the next available status code sorted alphabetically.  Priority is alphabetical with lowercase characters
	 * having priority.
	 *
	 * @return String unused appointment status code.
	 */
	private String getNextAvailableStatusCode()
	{
		// Method is private to make it unavailable to non-transactional code
		// No need to be clever here.  Could also be implemented with a regular unordered set.
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		TreeSet<Character> validCodes = alphabet.chars().mapToObj(e -> (char)e).collect(Collectors.toCollection(TreeSet::new));
		
		List<AppointmentStatus> existingStatuses = appointmentStatusDao.findAll();
		for (AppointmentStatus status : existingStatuses)
		{
			Character usedCode = status.getStatus().toCharArray()[0];
			validCodes.remove(usedCode);
		}
		
		return validCodes.first().toString();
	}

	private void swapUp(AppointmentStatus status)
	{
		List<AppointmentStatus> statuses = appointmentStatusDao.findAll();
		Integer index = findByIndex(statuses, status);

		if (index != null && index < 2)
		{
			Collections.swap(statuses, index, index - 1);

			// just swap their ids around....
		}
	}


	/**
	 * Insert the provided appointment status at the specified relative position.  Appointment statuses following
	 * the inserted status will be pushed to higher relative positions if space is needed.
	 *
	 * Position 1 is reserved, and attempts to insert into that position will result in an exception being thrown.
	 *
	 * There are a maximum of 26 appointment statuses, so for now we will attempt to do this in the ORM.
	 * @param status Appointment Status to insert
	 * @param relativePosition Relative position to insert the appointment status into.  Lower numbers result in the
	 *                         appointment status appearing closer to the top of the list when cycling.  Must be greater
	 *                         than or equals to 2
	 *
	 * @return An updated, ordered list of appointment statuses, with the provided appointment inserted into the correct relative position.
	 */
	private List<AppointmentStatus> swapPosition(AppointmentStatus status, ReorderDirection direction)
	{


		Integer index = null;
		for (int i = 0; i < statuses.size(); i++)
		{
			AppointmentStatus current = statuses.get(i);
			if (current.getStatus().equals(status.getStatus()))
			{
				index = i;
			}
		}

		if (index != null)
		{
			if (direction.equals(ReorderDirection.UP))
			{
				// Position 1 is reserved
				if (index > 2)
				{

				}
			}

			if (direction.equals(ReorderDirection.DOWN))
			{
				if (!index.equals(statuses.size() -1 ))
				{
					Collections.swap(statuses, index, index + 1);
				}
			}
		}

		return statuses;
	}
}
