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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
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
	
	/**
	 * Gets a list of appointment statuses for use in the calendar.
	 * @return List of appointment statuses.
	 */
	public List<CalendarAppointmentStatus> getCalendarAppointmentStatusList()
	{
		AppointmentStatusList appointmentStatusList = AppointmentStatusList.factory(appointmentManager);

		return appointmentStatusList.getCalendarAppointmentStatusList();
	}
	
	public List<AppointmentStatus> getAllAppointmentStatuses()
	{
		return appointmentStatusDao.findAll();
	}
	
	public List<AppointmentStatus> getActiveAppointmentStatuses()
	{
		return appointmentStatusDao.findByActive(true);
	}
	
	public List<AppointmentStatus> getInactiveAppointmentStatuses()
	{
		return appointmentStatusDao.findByActive(false);
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
	 * Persist the supplied appointment status. This method will automatically set a status code based on the next
	 * available unused code.  Any existing status code passed into this method will be lost.
	 *
	 * For safety, this code should be called from a synchronized block, as two simultaneous transactions looking up
	 * the next available appointment status code will return identical status codes, leading to multiple statuses
	 * sharing the same code.
	 *
	 * @param status AppointmentStatus to persist
	 */
	public AppointmentStatus assignStatusCodeAndSave(AppointmentStatus status)
	{
		String nextAvailableStatusCode = getNextAvailableStatusCode();
		
		status.setStatus(nextAvailableStatusCode);
		appointmentStatusDao.persist(status);
		// Status gets added at the end of statuses
		// Move it to a spot where it can be manipulated by users without fear of messing up the rest of the system
		fixNewStatusOrdering(status);

		return status;
	}
	
	/**
	 * Return the next available status code.
	 *
	 * Priority is determined by the ordinal value of the available status' ascii value.
	 * (Alphabetical, with the entire uppercase set having higher priority than the lowercase set)
	 *
	 * @return String unused appointment status code.  If there are no valid codes left, return null
	 * @throws NoSuchElementException if there are no available status codes left
	 */
	private String getNextAvailableStatusCode() throws NoSuchElementException
	{
		// Method is private to make it unavailable to non-transactional code
		// Ss and Vv are reserved statuses, for signed and verified.
		String alphabet = "ABCDEFGHIJKLMNOPQRTUWXYZabcdefghijklmnopqrtuwxyz";
		TreeSet<String> validCodes = alphabet.chars()
		                                        .mapToObj(e -> Character.toString((char)e))
		                                        .collect(Collectors.toCollection(() -> new TreeSet<String>()));
		
		appointmentStatusDao.findAll().forEach(s -> validCodes.remove(s.getStatus()));
		
		return validCodes.first();
	}
	
	/**
	 * Move the specified AppointmentStatus up one position in relative ordering
	 * when sorted by id.  The first position is reserved.  This method will have
	 * no effect if the status is already first or second in the list.
	 *
	 * @param status Appointment status to move up.
	 */
	public void swapUp(AppointmentStatus status)
	{
		List<AppointmentStatus> statuses = appointmentStatusDao.findAll();
		statuses.sort(Comparator.comparing(AppointmentStatus::getId));
		
		Integer index = findByIndex(statuses, status);
		
		// The 0th index is reserved for the 't' status, therefore index 1 cannot be moved up.
		// The earliest index that can be moved up is 1
		if (index != null && index > 1)
		{
			swapPosition(statuses.get(index), statuses.get(index - 1));
		}
	}
	
	/**
	 * Move the specified AppointmentStatus one step down in relative ordering.
	 * The first position is reserved and cannot be moved, and this method will have
	 * no effect if trying to move the first appointment.  Likewise, this method will
	 * have no effect if the status is already last in the list.
	 *
	 * @param status Appointment status to move down.
	 */
	public void swapDown(AppointmentStatus status)
	{
		List<AppointmentStatus> statuses = appointmentStatusDao.findAll();
		statuses.sort(Comparator.comparing(AppointmentStatus::getId));
		
		Integer index = findByIndex(statuses, status);
		
		// The last item can't be moved down, as it is already on the bottom.
		// The 0th index is reserved for the 't' status, therefore it also can't be swapped down
		if (index != null && index != 0 && index < statuses.size() - 1)
		{
			swapPosition(statuses.get(index), statuses.get(index + 1));
		}
	}
	
	private Integer findByIndex(List<AppointmentStatus> allStatuses, AppointmentStatus target)
	{
		for (int i = 0; i < allStatuses.size(); i++)
		{
			if (allStatuses.get(i).getStatus().equals(target.getStatus()))
			{
				return i;
			}
		}
		
		return null;
	}
	
	/**
	 * Swaps ids (and therefore the relative position) of two appointment statuses, and persists
	 * the changes to the database. After swapping, the new order of these statuses relative to
	 * the rest of the list is guaranteed because no other ids are changed.
	 *
	 * ie:  If elements A and B with relative order 3 and 6 respectively are swapped, it is
	 * guaranteed that A will be 6th in relative order, and B will be 3rd.
	 *
	 * @param source appointment status
	 * @param target appointment status to swap with
	 */
	private void swapPosition(AppointmentStatus source, AppointmentStatus target)
	{
		AppointmentStatus temp = new AppointmentStatus();
		
		// Abracadabra! Swap everything between the two statuses, except for the id.
		BeanUtils.copyProperties(source, temp, "id");
		BeanUtils.copyProperties(target, source, "id");
		BeanUtils.copyProperties(temp, target, "id");
		
		appointmentStatusDao.merge(source);
		appointmentStatusDao.merge(target);
	}

	/**
	 * Shuffles a new status to its correct location.
	 * Statuses currently get added at the end like so:
	 *
	 * N | C | B | {new_status}
	 *
	 * By default, N / C / B are uneditable.
	 * We need them at the top of this mini set of statuses, so it reads as follows:
	 *
	 * {new_status} | N | C | B
	 */
	private void fixNewStatusOrdering(AppointmentStatus status)
	{
		String originalStatusCode = status.getStatus();
		AppointmentStatus noShowDefault = appointmentStatusDao.findByStatus(AppointmentStatus.APPOINTMENT_STATUS_NO_SHOW);
		AppointmentStatus cancelledDefault = appointmentStatusDao.findByStatus(AppointmentStatus.APPOINTMENT_STATUS_CANCELLED);
		AppointmentStatus billedDefault = appointmentStatusDao.findByStatus(AppointmentStatus.APPOINTMENT_STATUS_BILLED);

		// Current order: N, C, B, <new status>
		swapPosition(status, billedDefault);
		// Since the statuses have swapped, we need to get a new reference to the new status
		status = appointmentStatusDao.findByStatus(originalStatusCode);
		swapPosition(status, cancelledDefault);
		// Current order: N, <new status>, C, B
		status = appointmentStatusDao.findByStatus(originalStatusCode);
		swapPosition(status, noShowDefault);
		// Now we have correct order of <new>, N, C, B
	}

	/**
	 * Return which statuses from the given list are in use.
	 *
	 * @return List of appointment statuses in use.
	 */
	public List<String> checkStatusUsage(List<AppointmentStatus> statuses)
	{
		List<String> usedStatuses = appointmentStatusDao.getStatusesInUse();
		Set<String> allStatusComponents = new HashSet<>();
		
		for (String status : usedStatuses)
		{
			for (char component : status.toCharArray())
			{
				allStatusComponents.add(Character.toString(component));
			}
		}
		
		return statuses.stream()
		        .map((AppointmentStatus status) -> status.getStatus())
		        .filter((String statusCode) -> allStatusComponents.contains(statusCode))
		        .collect(Collectors.toList());
	}
}
