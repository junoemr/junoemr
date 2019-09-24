/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.PhaseInterceptorChain;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.log4j.Logger;
import org.apache.tools.ant.util.DateUtils;
import org.oscarehr.appointment.dto.CalendarAppointmentStatus;
import org.oscarehr.appointment.service.AppointmentStatusService;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.common.model.AppointmentType;
import org.oscarehr.common.model.LookupListItem;
import org.oscarehr.managers.AppointmentManager;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.ScheduleManager;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.schedule.dto.CalendarSchedule;
import org.oscarehr.schedule.dto.ScheduleGroup;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.oscarehr.schedule.service.Schedule;
import org.oscarehr.schedule.service.ScheduleGroupService;
import org.oscarehr.schedule.service.ScheduleTemplateService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.AppointmentConverter;
import org.oscarehr.ws.rest.conversion.AppointmentStatusConverter;
import org.oscarehr.ws.rest.conversion.AppointmentTypeConverter;
import org.oscarehr.ws.rest.conversion.LookupListItemConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestSearchResponse;
import org.oscarehr.ws.rest.to.AbstractSearchResponse;
import org.oscarehr.ws.rest.to.SchedulingResponse;
import org.oscarehr.ws.rest.to.model.AppointmentStatusTo1;
import org.oscarehr.ws.rest.to.model.AppointmentTypeTo1;
import org.oscarehr.ws.rest.to.model.LookupListItemTo1;
import org.oscarehr.ws.rest.transfer.PatientListItemTransfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/schedule")
@Component("scheduleService")
@Tag(name = "schedule")
public class ScheduleService extends AbstractServiceImpl {

	Logger logger = MiscUtils.getLogger();

	@Autowired
	private ScheduleManager scheduleManager;
	@Autowired
	private AppointmentManager appointmentManager;
	@Autowired
	private AppointmentStatusService appointmentStatusService;
	@Autowired
	private DemographicManager demographicManager;
	@Autowired
	private SecurityInfoManager securityInfoManager;
	@Autowired
	private Schedule scheduleService;
	@Autowired
	private ScheduleGroupService scheduleGroupService;
	@Autowired
	private ScheduleTemplateService scheduleTemplateService;

	@GET
	@Path("/day/{date}")
	@Produces("application/json")
	public RestSearchResponse<PatientListItemTransfer> getAppointmentsForDay(@PathParam("date") String dateStr) throws ParseException
	{
		String providerNo = this.getCurrentProvider().getProviderNo();
		LoggedInInfo loggedInInfo = getLoggedInInfo();
		if("".equals(providerNo))
		{
			providerNo = loggedInInfo.getLoggedInProviderNo();
		}
		securityInfoManager.requireAllPrivilege(providerNo, SecurityInfoManager.READ, null, "_appointment");

		SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");

		Date dateObj;
		if("today".equals(dateStr))
		{
			dateObj = new Date();
		}
		else
		{
			dateObj = DateUtils.parseIso8601Date(dateStr);
		}

		List<Appointment> appts = scheduleManager.getDayAppointments(loggedInInfo, providerNo, dateObj);
		List<PatientListItemTransfer> response = new ArrayList<>(appts.size());

		for(Appointment appt : appts)
		{
			PatientListItemTransfer item = new PatientListItemTransfer();
			item.setDemographicNo(appt.getDemographicNo());
			if(appt.getDemographicNo() == 0)
			{
				item.setName(appt.getName());
			}
			else
			{
				item.setName(demographicManager.getDemographicFormattedName(loggedInInfo, appt.getDemographicNo()));
			}

			String rawStatus = appt.getStatus();
			String status = null;
			String statusModifier = null;
			if(rawStatus != null && rawStatus.length() > 0)
			{
				status = rawStatus.substring(0, 1);

				if(rawStatus.length() > 1)
				{
					statusModifier = rawStatus.substring(1,2);
				}
			}

			item.setStartTime(timeFormatter.format(appt.getStartTime()));
			item.setReason(appt.getReason());
			item.setStatus(status);
			item.setStatusModifier(statusModifier);
			item.setAppointmentNo(appt.getId());
			item.setDate(appt.getStartTimeAsFullDate());
			response.add(item);
		}
		return RestSearchResponse.successResponseOnePage(response);
	}

	@GET
	@Path("/{demographicNo}/appointmentHistory")
	@Produces("application/json")
	public SchedulingResponse getAppointmentHistory(@PathParam("demographicNo") Integer demographicNo)
	{
		SchedulingResponse response = new SchedulingResponse();
		List<Appointment> appts = appointmentManager.getAppointmentHistoryWithoutDeleted(getLoggedInInfo(), demographicNo, 0, OscarAppointmentDao.MAX_LIST_RETURN_SIZE);
		if(appts.size() == OscarAppointmentDao.MAX_LIST_RETURN_SIZE)
		{
			logger.warn("appointment history over MAX_LIST_RETURN_SIZE for demographic " + demographicNo);
		}
		AppointmentConverter converter = new AppointmentConverter();
		response.setAppointments(converter.getAllAsTransferObjects(getLoggedInInfo(), appts));

		return response;
	}

	/*
	 * These are some method from the ERO branch which I didn't get to.
	 * 
	@GET
	@Path("/{startDate}/{endDate}/{providerId}/fetchFlipView")
	@Produces("application/json")
	public Response fetchFlipView(@PathParam("startDate") String startDate, @PathParam("endDate") String endDate, @PathParam("providerId") String providerId) {
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("/roomDetails/get")
	@Produces("application/json")
	public Response getRoomDetails() {
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("/{appDate}/{providerId}/{startTime}/{endTime}/checkProvAvali")
	@Produces("application/json")
	public Response checkProviderAvaliablity(@PathParam("appDate") String appDate, @PathParam("providerId") String providerId, @PathParam("startTime") String startTime, @PathParam("endTime") String endTime) {
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("/blockreason/get")
	@Produces("application/json")
	public Response getBlockTimeReason() {
		return Response.status(Status.OK).build();
	}

	@GET
	@Path("/scheduleTempCode/get")
	@Produces("application/json")
	public Response fetchScheduleTempCode() {
		return Response.status(Status.OK).build();
	}
*/

	@GET
	@Path("/statuses")
	@Produces("application/json")
	public AbstractSearchResponse<AppointmentStatusTo1> getAppointmentStatuses() {
		AbstractSearchResponse<AppointmentStatusTo1> response = new AbstractSearchResponse<>();

		List<AppointmentStatus> results = scheduleManager.getAppointmentStatuses(getLoggedInInfo());
		AppointmentStatusConverter converter = new AppointmentStatusConverter();

		response.setContent(converter.getAllAsTransferObjects(getLoggedInInfo(), results));
		response.setTotal(results.size());

		return response;
	}

	@GET
	@Path("/types")
	@Produces("application/json")
	public RestSearchResponse<AppointmentTypeTo1> getAppointmentTypes() {

		List<AppointmentType> types = scheduleManager.getAppointmentTypes();

		AppointmentTypeConverter converter = new AppointmentTypeConverter();
		List<AppointmentTypeTo1> transferList = converter.getAllAsTransferObjects(getLoggedInInfo(), types);

		return RestSearchResponse.successResponseOnePage(transferList);
	}

	@GET
	@Path("/reasons")
	@Produces("application/json")
	public RestSearchResponse<LookupListItemTo1> getAppointmentReasons()
	{
		List<LookupListItem> items = appointmentManager.getReasons();

		LookupListItemConverter converter = new LookupListItemConverter();
		List<LookupListItemTo1> transferList = converter.getAllAsTransferObjects(getLoggedInInfo(), items);

		return RestSearchResponse.successResponseOnePage(transferList);
	}

	// TODO: make the services below match the current status quo (logging, limits, etc)
	@GET
	@Path("/groups")
	@Produces("application/json")
	public RestSearchResponse<ScheduleGroup> getScheduleGroups()
	{
		List<ScheduleGroup> scheduleGroups = scheduleGroupService.getScheduleGroups();

		// TODO: paginate?
		return RestSearchResponse.successResponseOnePage(scheduleGroups);
	}

	@GET
	@Path("/templateCodes")
	@Produces("application/json")
	public RestSearchResponse<ScheduleTemplateCode> getScheduleTemplateCodes()
	{
		List<ScheduleTemplateCode> scheduleTemplateCodes =
			scheduleTemplateService.getScheduleTemplateCodes();

		return RestSearchResponse.successResponseOnePage(scheduleTemplateCodes);
	}

	@GET
	@Path("/calendar/statuses")
	@Produces("application/json")
	public RestSearchResponse<CalendarAppointmentStatus> getCalendarAppointmentStatuses()
	{
		List<CalendarAppointmentStatus> appointmentStatusList =
			appointmentStatusService.getCalendarAppointmentStatusList();

		return RestSearchResponse.successResponseOnePage(appointmentStatusList);
	}

	@GET
	@Path("/calendar")
	@Produces("application/json")
	public RestResponse<CalendarSchedule> getCalendarSchedule(
			@QueryParam("scheduleId") String scheduleId,
			@QueryParam("scheduleIdType") String scheduleIdType,
			@QueryParam("scheduleFilter") Boolean viewSchedulesOnly,
			@QueryParam("startDate") String startDateString,
			@QueryParam("endDate") String endDateString,
			@QueryParam("startTime") String startTimeString,
			@QueryParam("endTime") String endTimeString,
			@QueryParam("site") String siteName,
			@QueryParam("slotDuration") Integer slotDurationInMin
	)
	{
		Message message = PhaseInterceptorChain.getCurrentMessage();
		HttpServletRequest request = (HttpServletRequest)message.get(AbstractHTTPDestination.HTTP_REQUEST);
		HttpSession session = request.getSession(true);

		// conversions will throw exception without valid date/time strings
		LocalDate startDate = ConversionUtils.dateStringToLocalDate(startDateString);
		LocalDate endDate = ConversionUtils.dateStringToLocalDate(endDateString);
		LocalTime startTime = ConversionUtils.toLocalTime(startTimeString);
		LocalTime endTime = ConversionUtils.toLocalTime(endTimeString);

		CalendarSchedule calendarSchedule;
		if(ScheduleGroup.IdentifierType.valueOf(scheduleIdType).equals(ScheduleGroup.IdentifierType.GROUP))
		{
			calendarSchedule =
					scheduleService.getCalendarScheduleByGroup(session, scheduleId, viewSchedulesOnly,
							startDate, endDate, startTime, endTime, siteName, slotDurationInMin);
		}
		else
		{
			//TODO change all providerNos to strings in this chain
			calendarSchedule =
					scheduleService.getCalendarScheduleByProvider(session, Integer.parseInt(scheduleId), viewSchedulesOnly,
							startDate, endDate, startTime, endTime, siteName, slotDurationInMin);
		}
		return RestResponse.successResponse(calendarSchedule);
	}
}
