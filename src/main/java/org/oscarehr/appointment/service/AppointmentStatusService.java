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
import org.oscarehr.dataMigration.converter.out.AppointmentStatusDbToModelConverter;
import org.oscarehr.dataMigration.model.appointment.AppointmentStatus;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AppointmentStatusService
{
	@Autowired
	private AppointmentManager appointmentManager;

	@Autowired
	private AppointmentStatusDbToModelConverter appointmentStatusConverter;

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

	public ConcurrentMap<String, AppointmentStatus> getAppointmentStatusCodeModelMap()
	{
		List<org.oscarehr.common.model.AppointmentStatus> apptStatusList = appointmentManager.getAppointmentStatuses();

		ConcurrentMap<String, AppointmentStatus> statusMap = new ConcurrentHashMap<>(apptStatusList.size());
		for(org.oscarehr.common.model.AppointmentStatus dbStatus : apptStatusList)
		{
			statusMap.put(dbStatus.getStatus(), appointmentStatusConverter.convert(dbStatus));
		}
		return statusMap;
	}
	public ConcurrentMap<String, AppointmentStatus> getAppointmentStatusNameModelMap()
	{
		List<org.oscarehr.common.model.AppointmentStatus> apptStatusList = appointmentManager.getAppointmentStatuses();

		ConcurrentMap<String, AppointmentStatus> statusMap = new ConcurrentHashMap<>(apptStatusList.size());
		for(org.oscarehr.common.model.AppointmentStatus dbStatus : apptStatusList)
		{
			statusMap.put(dbStatus.getDescription(), appointmentStatusConverter.convert(dbStatus));
		}
		return statusMap;
	}

	public List<org.oscarehr.common.model.AppointmentStatus> getAllAppointmentStatuses()
	{
		return appointmentStatusDao.findAll();
	}

	public List<org.oscarehr.common.model.AppointmentStatus> getActiveAppointmentStatuses()
	{
		return appointmentStatusDao.findByActive(true);
	}

	public List<org.oscarehr.common.model.AppointmentStatus> getInactiveAppointmentStatuses()
	{
		return appointmentStatusDao.findByActive(false);
	}

	public org.oscarehr.common.model.AppointmentStatus getAppointmentStatusById(Integer id)
	{
		return appointmentStatusDao.find(id);
	}

	public void updateAppointmentStatus(org.oscarehr.common.model.AppointmentStatus status)
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
	public org.oscarehr.common.model.AppointmentStatus assignStatusCodeAndSave(org.oscarehr.common.model.AppointmentStatus status)
	{
		String nextAvailableStatusCode = getNextAvailableStatusCode();

		status.setStatus(nextAvailableStatusCode);
		appointmentStatusDao.persist(status);

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
	public void swapUp(org.oscarehr.common.model.AppointmentStatus status)
	{
		List<org.oscarehr.common.model.AppointmentStatus> statuses = appointmentStatusDao.findAll();
		statuses.sort(Comparator.comparing(org.oscarehr.common.model.AppointmentStatus::getId));

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
	public void swapDown(org.oscarehr.common.model.AppointmentStatus status)
	{
		List<org.oscarehr.common.model.AppointmentStatus> statuses = appointmentStatusDao.findAll();
		statuses.sort(Comparator.comparing(org.oscarehr.common.model.AppointmentStatus::getId));

		Integer index = findByIndex(statuses, status);

		// The last item can't be moved down, as it is already on the bottom.
		// The 0th index is reserved for the 't' status, therefore it also can't be swapped down
		if (index != null && index != 0 && index < statuses.size() - 1)
		{
			swapPosition(statuses.get(index), statuses.get(index + 1));
		}
	}

	private Integer findByIndex(List<org.oscarehr.common.model.AppointmentStatus> allStatuses, org.oscarehr.common.model.AppointmentStatus target)
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
	private void swapPosition(org.oscarehr.common.model.AppointmentStatus source, org.oscarehr.common.model.AppointmentStatus target)
	{
		org.oscarehr.common.model.AppointmentStatus temp = new org.oscarehr.common.model.AppointmentStatus();

		// Abracadabra! Swap everything between the two statuses, except for the id.
		BeanUtils.copyProperties(source, temp, "id");
		BeanUtils.copyProperties(target, source, "id");
		BeanUtils.copyProperties(temp, target, "id");

		appointmentStatusDao.merge(source);
		appointmentStatusDao.merge(target);
	}

	/**
	 * Return which statuses from the given list are in use.
	 *
	 * @return List of appointment statuses in use.
	 */
	public List<String> checkStatusUsage(List<org.oscarehr.common.model.AppointmentStatus> statuses)
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
		        .map((org.oscarehr.common.model.AppointmentStatus status) -> status.getStatus())
		        .filter((String statusCode) -> allStatusComponents.contains(statusCode))
		        .collect(Collectors.toList());
	}
}
