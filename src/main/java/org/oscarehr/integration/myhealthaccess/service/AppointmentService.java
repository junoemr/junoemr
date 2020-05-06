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
package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.common.IsPropertiesOn;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.IntegrationData;
import org.oscarehr.integration.myhealthaccess.ErrorHandler;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentBookResponseTo1;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentBookTo1;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentCacheTo1;
import org.oscarehr.integration.myhealthaccess.dto.AppointmentSearchTo1;
import org.oscarehr.integration.myhealthaccess.exception.BaseException;
import org.oscarehr.integration.myhealthaccess.exception.BookingException;
import org.oscarehr.integration.myhealthaccess.exception.InvalidIntegrationException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotUniqueException;
import org.oscarehr.integration.myhealthaccess.model.MHAAppointment;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService extends BaseService
{
	@Autowired
	ClinicService clinicService;

	public void updateAppointmentCache(IntegrationData integrationData, AppointmentCacheTo1 appointmentTransfer)
	{
		String endpoint = "/clinic/%s/appointment/%s/cache";

		String apiKey = integrationData.getClinicApiKey();
		String clinicId = integrationData.getIntegration().getRemoteId();
		String appointmentId = appointmentTransfer.getId();

		try
		{
			endpoint = formatEndpoint(endpoint, clinicId, appointmentId);
			Boolean response = put(endpoint, apiKey, appointmentTransfer, Boolean.class);
			if(!response)
			{
				throw new RuntimeException("Got bad response status: " + response);
			}
		}
		catch (BaseException e)
		{
			ErrorHandler.handleError(e);
		}
	}

	/**
	 * book a telehealth appointment in MHA.
	 * @param loggedInInfo - logged in info
	 * @param appointment - the appointment to book.
	 * @throws InvalidIntegrationException
	 */
	public void bookTelehealthAppointment(LoggedInInfo loggedInInfo, Appointment appointment) throws InvalidIntegrationException
	{
		String appointmentSite = null;
		if (IsPropertiesOn.isMultisitesEnable())
		{
			appointmentSite = appointment.getLocation();
		}

		String loginToken = clinicService.loginOrCreateClinicUser(loggedInInfo, appointmentSite).getToken();
		String apiKey = getApiKey(appointmentSite);
		AppointmentBookResponseTo1 appointmentBookResponseTo1 = postWithToken(formatEndpoint("/clinic_user/appointment/book"),
				apiKey, new AppointmentBookTo1(appointment), AppointmentBookResponseTo1.class, loginToken);
		if (!appointmentBookResponseTo1.isSuccess())
		{
			throw new BookingException(appointmentBookResponseTo1.getMessage());
		}
	}

	/**
	 * book a one time telehealth appointment in MHA
	 * @param loggedInInfo - logged in info
	 * @param appointment - the appointment to book.
	 * @param sendNotification - If true MHA will send a notification to the user. This notification will include the one time link.
	 * @throws InvalidIntegrationException
	 */
	public void bookOneTimeTelehealthAppointment(LoggedInInfo loggedInInfo, Appointment appointment, Boolean sendNotification) throws InvalidIntegrationException
	{
		String appointmentSite = null;
		if (IsPropertiesOn.isMultisitesEnable())
		{
			appointmentSite = appointment.getLocation();
		}

		String loginToken = clinicService.loginOrCreateClinicUser(loggedInInfo, appointmentSite).getToken();
		String apiKey = getApiKey(appointmentSite);
		AppointmentBookResponseTo1 appointmentBookResponseTo1 = postWithToken(formatEndpoint("/clinic_user/appointment/book"),
				apiKey, new AppointmentBookTo1(appointment, true, sendNotification), AppointmentBookResponseTo1.class, loginToken);
		if (!appointmentBookResponseTo1.isSuccess())
		{
			throw new BookingException(appointmentBookResponseTo1.getMessage());
		}
	}

	/**
	 * send a one time telehealth notification for the specified appointment, to the patient.
	 * @param integration - the integration under which to perform the action
	 * @param loginToken - the login token of the user performing the action
	 * @param remote_id - the appointments remote_id (mha id)
	 */
	public void sendOneTimeTelehealthNotification(Integration integration, String loginToken, String remote_id)
	{
		postWithToken(formatEndpoint("/clinic_user/clinic/appointment/%s/send_one_time_link", remote_id),
				integration.getApiKey(), null, Boolean.class, loginToken);
	}

	/**
	 * fetch remote appointment from MHA by appointmentNo
	 * @param integration - the integration to search for the appointment in
	 * @param appointmentNo - the appointment number to fetch
	 * @return - a new MHAAppointment object
	 */
	public MHAAppointment getAppointment(Integration integration, Integer appointmentNo)
	{
		String url = formatEndpoint("/clinic/%s/appointments?search_by=appointment_no&appointment_no=%s",
				integration.getRemoteId(), appointmentNo);
		AppointmentSearchTo1 result = get(url,
				integration.getApiKey(), AppointmentSearchTo1.class);
		if (result.isSuccess())
		{
			if (result.getAppointments().size() == 1)
			{
				return new MHAAppointment(result.getAppointments().get(0));
			}
			else
			{
				throw new RecordNotUniqueException("Multiple results returned when looking up MHA appointment by appointmentNo" +
						" for integration [" + integration.getId() + "] appointmentNo [" + appointmentNo + "]");
			}
		}
		else if (result.isNotFound())
		{
			throw new RecordNotFoundException("Could not find MHA appointment for integration [" + integration.getId() +
					"] With appointment No [" + appointmentNo + "]");
		}
		else
		{
			throw new RuntimeException("Unexpected status type when looking up MHA appointment for integration [" + integration.getId() +
					"] appointmentNo [" + appointmentNo + "]");
		}
	}

}
