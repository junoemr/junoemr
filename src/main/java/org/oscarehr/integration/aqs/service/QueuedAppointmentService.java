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
package org.oscarehr.integration.aqs.service;

import org.oscarehr.integration.aqs.model.AppointmentQueue;
import org.oscarehr.integration.aqs.model.QueuedAppointment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueuedAppointmentService extends BaseService
{
	/**
	 * calls through to lower definition of getAppointmentsInQueue
	 * @param appointmentQueue - queue object to get appointments for
	 * @return - list of queued appointments
	 */
	public List<QueuedAppointment> getAppointmentsInQueue(AppointmentQueue appointmentQueue)
	{
		return getAppointmentsInQueue(appointmentQueue.getRemoteId());
	}

	/**
	 * get a list of appointments in the queue
	 * @param queueId - the queue to get the appointment list for
	 * @return - list of queued appointments
	 */
	public List<QueuedAppointment> getAppointmentsInQueue(String queueId)
	{
		ArrayList<QueuedAppointment> list = new ArrayList<>();

		switch(queueId)
		{
			case "0":
				list.add(new QueuedAppointment("0", 0, "1" , "foobar", "Jon Doe"));
				list.add(new QueuedAppointment("0", 1, "2" , "warts on ass", "Frank. Dr."));
				break;
			case "1":
				list.add(new QueuedAppointment("1", 0, "3" , "power over whelming!", "Man "));
				list.add(new QueuedAppointment("1", 1, "4" , "Its over 9000!", "Super Saiyan "));
				list.add(new QueuedAppointment("1", 2, "5" , "What! 9000!", "Other guy"));
				list.add(new QueuedAppointment("1", 2, "5" , "Dragon Ball Z!", "Dragon Dragon Ball!"));
				break;
			case "2":
				for (Integer i =0; i < 64; i ++)
				{
					list.add(new QueuedAppointment("2", i, i.toString(), "Long", "appts"));
				}
				break;
		}

		return list;

		//TODO pull real data from AQS server
	}
}
