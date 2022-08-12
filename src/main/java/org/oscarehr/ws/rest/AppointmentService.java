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

package org.oscarehr.ws.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.appointment.dto.AppointmentEditRecord;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.managers.AppointmentManager;
import org.oscarehr.schedule.dto.CalendarAppointment;
import org.oscarehr.schedule.dto.CalendarAppointmentRepeating;
import org.oscarehr.schedule.exception.ScheduleException;
import org.oscarehr.security.model.Permission;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.model.AppointmentTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ConversionUtils;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/appointment")
@Component("appointmentService")
@Tag(name = "appointment")
public class AppointmentService extends AbstractServiceImpl
{
	@Autowired
	private AppointmentManager appointmentManager;

	@Autowired
	private org.oscarehr.appointment.service.Appointment appointmentService;

	@Autowired
	private SiteService siteService;

	@GET
	@Path("/{appointmentNo}")
	@Produces("application/json")
	public RestResponse<AppointmentTo1> getAppointment(@PathParam("appointmentNo") Integer appointmentNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_READ);

		AppointmentConverter converter = new AppointmentConverter();
		Appointment appointment = appointmentManager.getAppointment(getLoggedInInfo(), appointmentNo);
		return RestResponse.successResponse(converter.getAsTransferObject(getLoggedInInfo(), appointment));
	}

	@POST
	@Path("/")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<CalendarAppointment> addAppointment(CalendarAppointment calendarAppointment)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_CREATE);

		AppointmentConverter converter = new AppointmentConverter();
		Appointment appointment = converter.getAsDomainObject(calendarAppointment);

		Appointment savedAppointment = null;
		if (appointment.getIsVirtual())
		{
			savedAppointment = appointmentService.saveNewTelehealthAppointment(appointment, getLoggedInInfo(), getHttpServletRequest(), calendarAppointment.isSendNotification());
		}
		else
		{
			savedAppointment = appointmentService.saveNewAppointment(appointment, getLoggedInInfo(), getHttpServletRequest(), calendarAppointment.isSendNotification());
		}

		LoggedInInfo loggedInInfo = getLoggedInInfo();
		
		CalendarAppointment responseAppointment = converter.getAsCalendarAppointment(savedAppointment);

		responseAppointment.setBillingRegion(calendarAppointment.getBillingRegion());
		responseAppointment.setBillingForm(calendarAppointment.getBillingForm());
		responseAppointment.setBillingRdohip(calendarAppointment.getBillingRdohip());
		responseAppointment.setUserProviderNo(calendarAppointment.getUserProviderNo());
		responseAppointment.setUserFirstName(calendarAppointment.getUserFirstName());
		responseAppointment.setUserLastName(calendarAppointment.getUserLastName());

		return RestResponse.successResponse(responseAppointment);
	}

	@POST
	@Path("/repeating/")
	@Produces("application/json")
	@Consumes("application/json")
	public RestResponse<CalendarAppointment> addRepeatingAppointment(CalendarAppointmentRepeating calendarAppointmentContainer)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_CREATE);

		LoggedInInfo loggedInInfo = getLoggedInInfo();
		AppointmentConverter converter = new AppointmentConverter();
		Appointment appointment = converter.getAsDomainObject(calendarAppointmentContainer.getAppointment());

		List<Date> legacyDateList = new ArrayList<>();
		for(LocalDate date : calendarAppointmentContainer.getDateList())
		{
			legacyDateList.add(ConversionUtils.toLegacyDate(date));
		}

		List<Appointment> savedAppointments = appointmentManager.addRepeatingAppointment(loggedInInfo, appointment, legacyDateList);
		Appointment savedAppointment = savedAppointments.get(0);

		// do some stuff for security logging
		String[] idList = new String[savedAppointments.size()-1];
		for(int i=1; i< savedAppointments.size(); i++)
		{
			idList[i-1] = String.valueOf(savedAppointments.get(i).getId());
		}
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), savedAppointment.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS, String.valueOf(savedAppointment.getId()), loggedInInfo.getIp(),
				"Repeat booking ids: [" + String.join(",", idList) + "]");

		CalendarAppointment responseAppointment = converter.getAsCalendarAppointment(savedAppointment);
		return RestResponse.successResponse(responseAppointment);
	}

	@PUT
	@Path("/")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<CalendarAppointment> updateAppointment(CalendarAppointment calendarAppointment) throws ScheduleException
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_UPDATE);

		AppointmentConverter converter = new AppointmentConverter();

		String siteName = calendarAppointment.getSite();
		String providerNo = calendarAppointment.getProviderNo().toString();

		boolean isMultiSite = org.oscarehr.common.IsPropertiesOn.isMultisitesEnable();
		boolean isValidTargetSite = !isMultiSite || siteService.isProviderAssignedToSite(providerNo, siteName);

		if (isValidTargetSite)
		{
			org.oscarehr.common.model.Appointment savedAppointment = appointmentService.updateAppointment(calendarAppointment, getLoggedInInfo(), getHttpServletRequest());
			CalendarAppointment responseAppointment = converter.getAsCalendarAppointment(savedAppointment);

			responseAppointment.setBillingRegion(calendarAppointment.getBillingRegion());
			responseAppointment.setBillingForm(calendarAppointment.getBillingForm());
			responseAppointment.setBillingRdohip(calendarAppointment.getBillingRdohip());
			responseAppointment.setUserProviderNo(calendarAppointment.getUserProviderNo());
			responseAppointment.setUserFirstName(calendarAppointment.getUserFirstName());
			responseAppointment.setUserLastName(calendarAppointment.getUserLastName());

			return RestResponse.successResponse(responseAppointment);
		}
		else
		{
			String errorMessage = String.format("Provider is not assigned to site %s", siteName);
			throw new ScheduleException(errorMessage);
		}
	}

	@DELETE
	@Path("/{appointmentNo}")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<Integer> deleteAppointment(@PathParam("appointmentNo") Integer appointmentNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_DELETE);

		appointmentManager.deleteAppointment(getLoggedInInfo(), appointmentNo);

		LoggedInInfo loggedInInfo = getLoggedInInfo();
		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), null, LogConst.ACTION_DELETE, LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS, String.valueOf(appointmentNo), loggedInInfo.getIp());

		return RestResponse.successResponse(appointmentNo);
	}


	@PUT
	@Path("/{appointmentNo}/set_status")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<String> setStatus(@PathParam("appointmentNo") Integer appointmentNo,
	                                      String statusCode)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_UPDATE);

		Appointment appointment = appointmentManager.updateAppointmentStatus(getLoggedInInfo(), appointmentNo, statusCode);

		return RestResponse.successResponse(appointment.getAppointmentStatus());
	}

	@POST
	@Path("/{appointmentNo}/rotate_status")
	@Consumes("application/json")
	@Produces("application/json")
	public RestResponse<String> setNextStatus(@PathParam("appointmentNo") Integer appointmentNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_UPDATE);
		String newStatus = appointmentManager.rotateStatus(getLoggedInInfo(), appointmentNo);

		return RestResponse.successResponse(newStatus);
	}


	@GET
	@Path("/{appointmentNo}/edit_history")
	@Produces("application/json")
	public RestSearchResponse<AppointmentEditRecord> getEditHistory(@PathParam("appointmentNo") Integer appointmentNo)
	{
		securityInfoManager.requireAllPrivilege(getLoggedInProviderId(), Permission.APPOINTMENT_READ);
		List<AppointmentEditRecord> editList = appointmentManager.getAppointmentEdits(appointmentNo);
		return RestSearchResponse.successResponseOnePage(editList);
	}
}
