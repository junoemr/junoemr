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

package org.oscarehr.ws.external.soap.v1;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.cxf.annotations.GZIP;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentArchive;
import org.oscarehr.common.model.AppointmentType;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.managers.DayWorkSchedule;
import org.oscarehr.managers.ScheduleManager;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.external.soap.util.LocalDateAdapter;
import org.oscarehr.ws.external.soap.v1.transfer.Appointment.AppointmentArchiveTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.Appointment.AppointmentTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.Appointment.AppointmentTypeTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.Appointment.ValidatedAppointmentBookingTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.DayWorkScheduleTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.ScheduleCodeDurationTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.ScheduleTemplateCodeTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.DayTimeSlots;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.ProviderScheduleTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BlackoutRule;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRule;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRuleFactory;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.CutoffRule;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.MultipleBookingsRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

@WebService
@Component
@GZIP(threshold = AbstractWs.GZIP_THRESHOLD)
public class ScheduleWs extends AbstractWs {
	private static final Logger logger=MiscUtils.getLogger();
	
	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private ScheduleTemplateDao scheduleTemplateDao;

	public ScheduleTemplateCodeTransfer[] getScheduleTemplateCodes() {
		List<ScheduleTemplateCode> scheduleTemplateCodes = scheduleManager.getScheduleTemplateCodes();
		return (ScheduleTemplateCodeTransfer.toTransfer(scheduleTemplateCodes));
	}

	/**
	 * @deprecated you should use the method with the useGMTTime option
	 */
	public AppointmentTransfer getAppointment(Integer appointmentId) {
		Appointment appointment = scheduleManager.getAppointment(getLoggedInInfo(),appointmentId);
		return (AppointmentTransfer.toTransfer(appointment, false));
	}

	/**
	 * @deprecated you should use the method with the useGMTTime option
	 */
	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForProvider(String providerNo, Calendar date) {
		List<Appointment> appointments = scheduleManager.getDayAppointments(getLoggedInInfo(),providerNo, date);
		return (AppointmentTransfer.toTransfers(appointments, false));
	}

	/**
	 * @deprecated you should use the method with the useGMTTime option
	 */
	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForPatient(Integer demographicId, int startIndex, int itemsToReturn) {
		List<Appointment> appointments = scheduleManager.getAppointmentsForPatient(getLoggedInInfo(),demographicId, startIndex, itemsToReturn);
		return (AppointmentTransfer.toTransfers(appointments, false));
	}

	public AppointmentTransfer getAppointment2(Integer appointmentId, boolean useGMTTime) {
		Appointment appointment = scheduleManager.getAppointment(getLoggedInInfo(),appointmentId);
		return (AppointmentTransfer.toTransfer(appointment, useGMTTime));
	}

	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForProvider2(String providerNo, Calendar date, boolean useGMTTime) {
		List<Appointment> appointments = scheduleManager.getDayAppointments(getLoggedInInfo(),providerNo, date);
		return (AppointmentTransfer.toTransfers(appointments, useGMTTime));
	}

	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForPatient2(Integer demographicId, int startIndex, int itemsToReturn, boolean useGMTTime) {
		List<Appointment> appointments = scheduleManager.getAppointmentsForPatient(getLoggedInInfo(),demographicId, startIndex, itemsToReturn);
		return (AppointmentTransfer.toTransfers(appointments, useGMTTime));
	}
	
	public DayWorkScheduleTransfer getDayWorkSchedule(String providerNo, Calendar date) {
		DayWorkSchedule dayWorkSchedule = scheduleManager.getDayWorkSchedule(providerNo, date);
		if (dayWorkSchedule == null) return (null);
		else return (DayWorkScheduleTransfer.toTransfer(dayWorkSchedule));
	}

	@SkipContentLoggingOutbound
	public HashMap<String, DayTimeSlots[]> getValidProviderScheduleSlots (String providerNo,
																		  @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate startDate,
																		  @XmlJavaTypeAdapter(LocalDateAdapter.class) LocalDate endDate,
																		  String templateDurations,
																		  String demographicNo,
																		  String jsonRules)
	{
		MiscUtils.getLogger().info("Start Get Provider Schedule Service: " + LocalDateTime.now().toString());
		HashMap<String, DayTimeSlots[]> scheduleTransfer = new HashMap<>();
		List<ScheduleCodeDurationTransfer> scheduleDurationTransfers = new ArrayList<>();

		try
		{
			JSONArray templateDurationJsonArr = (JSONArray) new JSONParser().parse(templateDurations);

			for (Object templateDurationObj : templateDurationJsonArr)
			{
				JSONObject templateDurationJson = (JSONObject) templateDurationObj;
				String templateCode = (String) templateDurationJson.get("schedule_template_id");
				Long duration = (Long) templateDurationJson.get("appointment_duration");

				ScheduleCodeDurationTransfer scheduleDurationTransfer =
						new ScheduleCodeDurationTransfer(templateCode, duration.intValue());
				scheduleDurationTransfers.add(scheduleDurationTransfer);
			}

			List<MultipleBookingsRule> multipleBookingsRules =
					BookingRuleFactory.buildMultipleBookingsRuleList(jsonRules);

			BlackoutRule blackoutRule = BookingRuleFactory.buildBlackoutRule(jsonRules);
			CutoffRule cutoffRule = BookingRuleFactory.buildCutoffRule(jsonRules);

			ProviderScheduleTransfer providerScheduleTransfer =
					scheduleTemplateDao.getValidProviderScheduleSlots(
							providerNo,
							startDate,
							endDate,
							scheduleDurationTransfers,
							demographicNo,
							multipleBookingsRules,
							blackoutRule,
							cutoffRule
					);

			scheduleTransfer = providerScheduleTransfer.toTransfer();
		}
		catch(ParseException e)
		{
			MiscUtils.getLogger().error("Exception: " + e);
		}

		MiscUtils.getLogger().info("End Get Provider Schedule Service: " + LocalDateTime.now().toString());
		return scheduleTransfer;
	}

	public ValidatedAppointmentBookingTransfer addAppointmentValidated(
			AppointmentTransfer appointmentTransfer, String jsonRules) throws ParseException
	{
		Appointment appointment = new Appointment();

		if (appointmentTransfer.getLastUpdateUser() == null)
		{
			appointmentTransfer.setLastUpdateUser(getLoggedInInfo().getLoggedInProviderNo());
		}

		appointmentTransfer.copyTo(appointment);

		List<BookingRule> bookingRules = BookingRuleFactory.createBookingRuleList(appointment.getDemographicNo(), jsonRules);
		List<BookingRule> violatedRules = new ArrayList<>();
		for (BookingRule rule : bookingRules)
		{
			if (rule.isViolated(appointment))
			{
				violatedRules.add(rule);
			}
		}

		if (violatedRules.isEmpty())
		{
			scheduleManager.addAppointment(getLoggedInInfo(), getLoggedInSecurity(), appointment);
		}

		AppointmentTransfer apptTransfer = AppointmentTransfer.toTransfer(appointment, false);
		ValidatedAppointmentBookingTransfer response =
				new ValidatedAppointmentBookingTransfer(apptTransfer, violatedRules);
		return response;
	}

	public AppointmentTypeTransfer[] getAppointmentTypes() {
		List<AppointmentType> appointmentTypes = scheduleManager.getAppointmentTypes();
		return (AppointmentTypeTransfer.toTransfer(appointmentTypes));
	}

	/**
	 * @return the ID of the appointment just added
	 */
	public Integer addAppointment(AppointmentTransfer appointmentTransfer)
	{
		Appointment appointment = new Appointment();
		appointmentTransfer.copyTo(appointment);

		try
		{
			if (appointment.getDemographicNo() != 0)
			{
				DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
				Demographic demographic = demographicDao.getDemographic(String.valueOf(appointment.getDemographicNo()));
				if (demographic != null)
				{
					appointment.setName(demographic.getDisplayName());
				}
			}
		} catch (Exception e)
		{
			logger.error("Error setting patient name while adding appointment via the Web Service", e);
		}

		if (appointment.getName() == null)
		{
			appointment.setName("");
		}

		if (appointment.getReason() == null)
		{
			appointment.setReason("");
		}

		scheduleManager.addAppointment(getLoggedInInfo(), getLoggedInSecurity(), appointment);
		return (appointment.getId());
	}

	public void updateAppointment(AppointmentTransfer appointmentTransfer) {
		Appointment appointment = scheduleManager.getAppointment(getLoggedInInfo(),appointmentTransfer.getId());

		appointmentTransfer.copyTo(appointment);
		scheduleManager.updateAppointment(getLoggedInInfo(),appointment);
	}

	public void cancelAppointment(Integer appointmentId)
	{
		Appointment appointment = scheduleManager.getAppointment(getLoggedInInfo(), appointmentId);

		appointment.setStatus(Appointment.CANCELLED);
		scheduleManager.updateAppointment(getLoggedInInfo(), appointment);
	}

	/**
	 * @deprecated you should use the method with the useGMTTime option
	 */
	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForDateRangeAndProvider(Date startTime, Date endTime, String providerNo) {
		List<Appointment> appointments = scheduleManager.getAppointmentsForDateRangeAndProvider(getLoggedInInfo(),startTime, endTime, providerNo);
		return (AppointmentTransfer.toTransfers(appointments, false));
	}

	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsForDateRangeAndProvider2(Date startTime, Date endTime, String providerNo, boolean useGMTTime) {
		List<Appointment> appointments = scheduleManager.getAppointmentsForDateRangeAndProvider(getLoggedInInfo(),startTime, endTime, providerNo);
		return (AppointmentTransfer.toTransfers(appointments, useGMTTime));
	}

	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsUpdatedAfterDate(Date updatedAfterThisDateExclusive, int itemsToReturn, boolean useGMTTime) {
		List<Appointment> appointments=scheduleManager.getAppointmentUpdatedAfterDate(getLoggedInInfo(),updatedAfterThisDateExclusive, itemsToReturn);
		return(AppointmentTransfer.toTransfers(appointments, useGMTTime));
	}

	@SkipContentLoggingOutbound
	public AppointmentArchiveTransfer[] getAppointmentArchivesUpdatedAfterDate(Date updatedAfterThisDateExclusive, int itemsToReturn, boolean useGMTTime) {
		List<AppointmentArchive> appointments=scheduleManager.getAppointmentArchiveUpdatedAfterDate(getLoggedInInfo(),updatedAfterThisDateExclusive, itemsToReturn);
		return(AppointmentArchiveTransfer.toTransfers(appointments, useGMTTime));
	}

	@SkipContentLoggingOutbound
	public AppointmentTransfer[] getAppointmentsByProgramProviderDemographicDate(Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn, boolean useGMTTime) {
		List<Appointment> appointments = scheduleManager.getAppointmentsByProgramProviderDemographicDate(getLoggedInInfo(),programId, providerNo, demographicId, updatedAfterThisDateExclusive, itemsToReturn);
		return (AppointmentTransfer.toTransfers(appointments, useGMTTime));
	}
	
	/**
	 * This method is a helper method to help people code and test their clients against time zone differences.
	 * We will not support revisioning for this method, if / when we want to change this, we will.
	 */
	public Calendar testTimeZone_1492_05_12_18_26_32(boolean useGMTTime)
	{
		Calendar cal = new GregorianCalendar(1492, 05, 12, 18, 26, 32);
		cal=AppointmentTransfer.setToGMTIfRequired(cal,useGMTTime);
		
		logger.debug("timeZoneTest sent: "+cal);
		logger.debug("timeZoneTest sent: "+DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.format(cal));
		
		return(cal);
	}
	
	public Integer[] getAllDemographicIdByProgramProvider(Integer programId, String providerNo) {
		List<Integer> results=scheduleManager.getAllDemographicIdByProgramProvider(getLoggedInInfo(), programId, providerNo);
		return(results.toArray(new Integer[0]));
	}
}
