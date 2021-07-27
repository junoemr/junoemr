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
package org.oscarehr.schedule.service;

import com.google.common.collect.RangeMap;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.appointment.service.Appointment;
import org.oscarehr.common.IsPropertiesOn;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Site;
import org.oscarehr.schedule.dao.RScheduleDao;
import org.oscarehr.schedule.dao.ScheduleDateDao;
import org.oscarehr.schedule.dao.ScheduleHolidayDao;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.CalendarEvent;
import org.oscarehr.schedule.dto.CalendarSchedule;
import org.oscarehr.schedule.dto.ResourceSchedule;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.dto.UserDateSchedule;
import org.oscarehr.schedule.model.RSchedule;
import org.oscarehr.schedule.model.ScheduleDate;
import org.oscarehr.schedule.model.ScheduleHoliday;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.ScheduleCodeDurationTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.ScheduleSlotDto;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.HScheduleDate;
import oscar.HScheduleHoliday;
import oscar.MyDateFormat;
import oscar.RscheduleBean;
import oscar.util.ConversionUtils;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
public class Schedule
{
	@Autowired
	OscarAppointmentDao appointmentDao;

	@Autowired
	Appointment appointmentService;

	@Autowired
	MyGroupDao myGroupDao;

	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProviderSiteDao providerSiteDao;

	@Autowired
	ScheduleDateDao scheduleDateDao;

	@Autowired
	RScheduleDao rScheduleDao;

	@Autowired
	ScheduleHolidayDao scheduleHolidayDao;

	@Autowired
	ScheduleTemplateService scheduleTemplateService;

	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;

	@Autowired
	SiteDao siteDao;

	@Autowired
	SiteService siteService;

	private static final Logger logger = MiscUtils.getLogger();


	public long updateSchedule(RscheduleBean scheduleRscheduleBean,
	                           Hashtable<String, HScheduleDate> scheduleDateBean,
	                           Hashtable<String, HScheduleHoliday> scheduleHolidayBean,
	                           String available,
	                           String dayOfWeek1,
	                           String dayOfWeek2,
	                           String availableHour1,
	                           String availableHour2,
	                           String providerNo,
	                           String providerName,
	                           String startDateString,
	                           String endDateString,
	                           String originalDateString,
	                           GregorianCalendar cal,
	                           int yearLimit) throws ParseException
	{
		Date startDate = ConversionUtils.fromDateString(startDateString);
		Date endDate = ConversionUtils.fromDateString(endDateString);
		Date originalDate = ConversionUtils.fromDateString(originalDateString);

		if(startDateString.equals(scheduleRscheduleBean.sdate))
		{
			List<RSchedule> rsl = rScheduleDao.findByProviderAvailableAndDate(providerNo, "1", startDate);

			for(RSchedule rs : rsl)
			{
				rs.setStatus(RSchedule.STATUS_DELETED);
				rScheduleDao.merge(rs);
			}

			// I don't believe that available is any value other than 0 or 1. left this here for compatibility
			rsl = rScheduleDao.findByProviderAvailableAndDate(providerNo, "A", startDate);
			for(RSchedule rs : rsl)
			{
				rs.setStatus(RSchedule.STATUS_DELETED);
				rScheduleDao.merge(rs);
			}
		}

		Long overLapResult = rScheduleDao.search_rschedule_overlaps(providerNo, startDate, endDate,
				startDate, endDate, startDate, endDate,
				startDate, endDate, startDate, endDate,
				startDate, endDate, startDate, endDate);

		//if the schedule is the same we are editing instead
		Long existsResult = rScheduleDao.search_rschedule_exists(providerNo, startDate, endDate);


		boolean editingSchedule = existsResult > 0;

		//save rschedule data
		scheduleRscheduleBean.setRscheduleBean(providerNo, startDateString, endDateString, available, dayOfWeek1, dayOfWeek2, availableHour1, availableHour2, providerName);
		Date beanStartDate = MyDateFormat.getSysDate(scheduleRscheduleBean.sdate);
		Date beanEndDate = MyDateFormat.getSysDate(scheduleRscheduleBean.edate);

		if(editingSchedule)
		{
			List<RSchedule> rsl = rScheduleDao.findByProviderAvailableAndDate(scheduleRscheduleBean.provider_no, scheduleRscheduleBean.available, beanStartDate);
			for(RSchedule rs : rsl)
			{
				rs.setDayOfWeek(scheduleRscheduleBean.day_of_week);
				rs.setAvailHourB(scheduleRscheduleBean.avail_hourB);
				rs.setAvailHour(scheduleRscheduleBean.avail_hour);
				rs.setCreator(scheduleRscheduleBean.creator);
				rs.setStatus(scheduleRscheduleBean.active);
				rScheduleDao.merge(rs);
			}
		}
		else
		{
			RSchedule rs = new RSchedule();
			rs.setProviderNo(scheduleRscheduleBean.provider_no);
			rs.setsDate(beanStartDate);
			rs.seteDate(beanEndDate);
			rs.setAvailable(scheduleRscheduleBean.available);
			rs.setDayOfWeek(scheduleRscheduleBean.day_of_week);
			rs.setAvailHourB(scheduleRscheduleBean.avail_hourB);
			rs.setAvailHour(scheduleRscheduleBean.avail_hour);
			rs.setCreator(scheduleRscheduleBean.creator);
			rs.setStatus(scheduleRscheduleBean.active);
			rScheduleDao.persist(rs);
		}

		//create scheduledate record and initial scheduleDateBean
		scheduleDateBean.clear();
		for(ScheduleDate sd : scheduleDateDao.search_scheduledate_c(providerNo))
		{
			scheduleDateBean.put(ConversionUtils.toDateString(sd.getDate()), new HScheduleDate(sd.isAvailable(), String.valueOf(sd.getPriority()), sd.getReason(), sd.getHour(), sd.getCreator()));
		}

		//initial scheduleHolidayBean record
		if(scheduleHolidayBean.isEmpty())
		{
			for(ScheduleHoliday sh : scheduleHolidayDao.findAll())
			{
				scheduleHolidayBean.put(ConversionUtils.toDateString(sh.getId()), new HScheduleHoliday(sh.getHolidayName()));
			}
		}

		//create scheduledate record by 'b' rate
		List<ScheduleDate> scheduleDates = scheduleDateDao.findByProviderPriorityAndDateRange(providerNo, 'b', startDate, (endDate.before(originalDate) ? originalDate : endDate));
		for(ScheduleDate scheduleDate : scheduleDates)
		{
			scheduleDate.setStatus(ScheduleDate.STATUS_DELETED);
			scheduleDateDao.merge(scheduleDate);
		}

		Map<String, Site> siteNameMap = new HashMap<>();

		// Precache the sites to avoid having to lookup on each iteration.
		if (IsPropertiesOn.isMultisitesEnable())
		{
			List<Site> sites = siteDao.getAllSites();
			for (Site site : sites)
			{
				siteNameMap.put(site.getName(), site);
			}
		}

		for(int i = 0; i < 365 * yearLimit; i++)
		{
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DATE);
			if(scheduleDateBean.get(year + "-" + MyDateFormat.getDigitalXX(month) + "-" + MyDateFormat.getDigitalXX(day)) == null && scheduleRscheduleBean.getDateAvail(cal))
			{
				ScheduleDate sd = new ScheduleDate();
				sd.setDate(MyDateFormat.getSysDate(year + "-" + month + "-" + day));
				sd.setProviderNo(providerNo);
				sd.setAvailable(true);
				sd.setPriority('b');
				sd.setReason(scheduleRscheduleBean.getSiteAvail(cal));
				sd.setHour(scheduleRscheduleBean.getDateAvailHour(cal));
				sd.setCreator(providerName);
				sd.setStatus(scheduleRscheduleBean.active.toCharArray()[0]);

				if (IsPropertiesOn.isMultisitesEnable())
				{
					String siteName = scheduleRscheduleBean.getSiteAvail(cal);

					if (siteNameMap.containsKey(siteName))
					{
						Site site = siteNameMap.get(siteName);
						sd.setSiteId(site.getId());
					}
				}

				scheduleDateDao.persist(sd);
			}
			if((year + "-" + MyDateFormat.getDigitalXX(month) + "-" + MyDateFormat.getDigitalXX(day)).equals(endDateString)) break;
			cal.add(Calendar.DATE, 1);
		}

		return overLapResult;
	}

	public void deleteSchedule(String providerNo, String startDateString, String deletePriority)
	{
		Date startDate = MyDateFormat.getSysDate(startDateString);
		Date endDate = null;

		RSchedule rs1 = rScheduleDao.search_rschedule_current1(providerNo, startDate);
		if(rs1 != null)
		{
			String endDateStr = ConversionUtils.toDateString(rs1.geteDate());
			endDate = MyDateFormat.getSysDate(endDateStr);
		}

		if (endDate == null)
		{
			throw new IllegalArgumentException("End date cannot be null");
		}

		List<RSchedule> rsl = rScheduleDao.findByProviderNoAndStartEndDates(providerNo, startDate, endDate);
		for(RSchedule rs : rsl)
		{
			rs.setStatus(RSchedule.STATUS_DELETED);
			rScheduleDao.merge(rs);
		}

		rsl = rScheduleDao.findByProviderAvailableAndDate(providerNo, "A", startDate);
		for(RSchedule rs : rsl)
		{
			rs.setStatus(RSchedule.STATUS_DELETED);
			rScheduleDao.merge(rs);
		}

		//delete scheduledate
		List<ScheduleDate> sds;
		if("b".equals(deletePriority))
		{
			sds = scheduleDateDao.findByProviderPriorityAndDateRange(providerNo, 'b', startDate, endDate);
		}
		else
		{
			sds = scheduleDateDao.findByProviderAndDateRange(providerNo, startDate, endDate);
		}
		for(ScheduleDate sd : sds)
		{
			sd.setStatus(ScheduleDate.STATUS_DELETED);
			scheduleDateDao.merge(sd);
		}
	}

	/**
	 * delete any existing schedules by date &amp; provider, and create a new one with the given values.
	 * @param providerNoStr provider id
	 * @param date date of schedule
	 * @param available 1 or 0
	 * @param priority priority
	 * @param reason reason
	 * @param hour hour
	 * @param userName username
	 */
	public void saveScheduleByDate(String providerNoStr, Date date, boolean available, String priority, String reason, String hour, String userName, String status)
	{
		// flag existing schedule(s) as deleted.
		ScheduleDate sd = scheduleDateDao.findByProviderNoAndDate(providerNoStr, date);
		if(sd != null) {
			sd.setStatus(ScheduleDate.STATUS_DELETED);
			scheduleDateDao.merge(sd);
		}

		sd = new ScheduleDate();
		sd.setDate(date);
		sd.setProviderNo(providerNoStr);
		sd.setAvailable(available);
		sd.setPriority(priority.toCharArray()[0]);
		sd.setReason(reason);
		sd.setHour(hour);
		sd.setCreator(userName);
		sd.setStatus(status.toCharArray()[0]);

		//attempt to map to a schedule
		Site site = siteDao.findByName(reason);
		if (site != null)
		{
			sd.setSiteId(site.getId());
		}

		scheduleDateDao.persist(sd);
	}

	/**
	 * Get the schedule for the provider on the date.
	 * @param providerNo Provider to get schedule for.
	 * @param date Date to get schedule for.
	 * @param site String the name of the site to get the schedule for.
	 * @return The schedule for this provider.
	 */
	public ResourceSchedule getResourceScheduleByProvider(String providerNo, LocalDate date,
		String site, boolean viewAll)
	{
		Provider provider = providerDao.getProvider(providerNo);

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();
		UserDateSchedule userDateSchedule;

		if (viewAll && site != null)
		{
			if (!siteService.isProviderAssignedToSite(providerNo, site))
			{ // skip this provider
				return new ResourceSchedule(userDateSchedules);
			}

			// get a UserDateSchedule for each
			userDateSchedule = getUserDateSchedule(
					date,
					new Integer(provider.getProviderNo()),
					provider.getFirstName(),
					provider.getLastName(),
					site
			);
		}
		else
		{
			// get a UserDateSchedule for each
			userDateSchedule = getUserDateSchedule(
					date,
					new Integer(provider.getProviderNo()),
					provider.getFirstName(),
					provider.getLastName(),
					site,
					true
			);
		}

		// When not viewing all schedules, only add if there is a schedule set
		if(viewAll || userDateSchedule.getScheduleSlots().asMapOfRanges().size() > 0)
		{
			userDateSchedules.add(userDateSchedule);
		}

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	/**
	 * Get the schedule for the provided date for each member of the group.
	 * @param group The name of the group to get the schedule for.
	 * @param date The date to get the schedule for.
	 * @param site String the name of the site to get the schedule for.
	 * @param viewAll boolean If false, only show group members with a schedule set.
	 * @return The schedule for the group.
	 */
	public ResourceSchedule getResourceScheduleByGroup(String group, LocalDate date, String site,
		boolean viewAll, Integer limitProviderNo)
	{
		List<MyGroup> userGroupMappings;

		if(viewAll)
		{
			userGroupMappings = myGroupDao.getGroupByGroupNo(group);
		}
		else
		{
			userGroupMappings = myGroupDao.getGroupWithScheduleByGroupNo(group, date, limitProviderNo);
		}

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		for(MyGroup result: userGroupMappings)
		{
			UserDateSchedule userSchedule;
			if (viewAll)
			{
				//in view all we filter by site assigned to provider
				if (site != null)
				{
					if (!siteService.isProviderAssignedToSite(result.getId().getProviderNo(), site))
					{ // skip this provider
						continue;
					}
				}
				userSchedule = getUserDateSchedule(
						date,
						new Integer(result.getId().getProviderNo()),
						result.getFirstName(),
						result.getLastName(),
						site
				);
			}
			else
			{
				userSchedule = getUserDateSchedule(
						date,
						new Integer(result.getId().getProviderNo()),
						result.getFirstName(),
						result.getLastName(),
						site,
						true
				);
			}

			Provider provider = providerDao.getProvider(result.getId().getProviderNo());
			if (provider != null)
			{
				userSchedule.setFirstName(provider.getFirstName());
				userSchedule.setLastName(provider.getLastName());
			}
			else
			{
				MiscUtils.getLogger().error("failed to lookup provider with no [" +
						result.getId().getProviderNo() + "] for group [" + result.getId().getMyGroupNo() + "]");
			}

			userDateSchedules.add(userSchedule);
		}
		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	/**
	 * Get the provider's schedule for the week (sun-sat) that includes the provided date.
	 * @param providerNo Provider to get the schedule for.
	 * @param date Get the schedule for the week (sun-sat) including this date.
	 * @param site String the name of the site to get the schedule for.
	 * @return The schedule for the week.
	 */
	public ResourceSchedule getWeekScheduleByProvider(String providerNo, LocalDate date, String site)
	{
		Provider provider = providerDao.getProvider(providerNo);

		// Get date of the sunday on or before
		final DayOfWeek firstDayOfWeek = WeekFields.of(Locale.CANADA).getFirstDayOfWeek();
		LocalDate sunday = date.with(TemporalAdjusters.previousOrSame(firstDayOfWeek));

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		// Get 7 days worth of schedule, starting on the first day of the week
		for(int i = 0; i < 7; i++)
		{
			LocalDate currentDay = sunday.plusDays(i);

			// get a UserDateSchedule for each
			userDateSchedules.add(getUserDateSchedule(
				currentDay,
				new Integer(provider.getProviderNo()),
				provider.getFirstName(),
				provider.getLastName(),
				site
			));
		}

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	private UserDateSchedule getUserDateSchedule(
			LocalDate date,
			Integer providerNo,
			String firstName,
			String lastName,
			String site
	)
	{
		return getUserDateSchedule(date, providerNo, firstName, lastName, site, false);
	}

	private UserDateSchedule getUserDateSchedule(
		LocalDate date,
		Integer providerNo,
		String firstName,
		String lastName,
		String site,
		boolean filterScheduleBySite
	)
	{
		boolean isAvailable = false;

		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(
			date, providerNo);

		// Get appointments
		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			appointmentDao.findAppointmentDetailsByDateAndProvider(date, providerNo, site);

		ScheduleDate scheduleDate;
		if (site != null && filterScheduleBySite)
		{
			scheduleDate = scheduleDateDao.findByProviderNoSiteAndDate(Integer.toString(providerNo), site, java.sql.Date.valueOf(date));
		}
		else
		{
			scheduleDate = scheduleDateDao.findByProviderNoAndDate(Integer.toString(providerNo), java.sql.Date.valueOf(date));
		}

		if (scheduleDate != null)
		{
			isAvailable = scheduleDate.isAvailable();
		}

		return new UserDateSchedule(
			providerNo,
			date,
			firstName,
			lastName,
			scheduleSlots,
			appointments,
			isAvailable
		);
	}

	public List<CalendarEvent> getCalendarEvents(
		HttpSession session,
		Integer providerId,
		LocalDate startDate,
		LocalDate endDate,
		LocalTime startTime,
		LocalTime endTime,
		String siteName,
		Integer siteId,
		Integer slotDurationInMin
	)
	{
		List<CalendarEvent> calendarEvents = new ArrayList<>();

		// Loop through the dates between startDate and endDate (inclusive) and add schedule templates
		for(LocalDate date: ConversionUtils.getDateList(startDate, endDate))
		{
			// Get schedule templates for this provider/date
			calendarEvents.addAll(scheduleTemplateService.getCalendarEvents(providerId, date, startTime, endTime, siteId, slotDurationInMin));
		}

		// Get appointments for this provider/date range
		calendarEvents.addAll(appointmentService.getCalendarEvents(
			session, providerId, startDate, endDate, siteName));

		return calendarEvents;
	}
	public CalendarSchedule getCalendarScheduleByProvider(
			HttpSession session,
			Integer providerId,
			boolean viewSchedulesOnly,
			LocalDate startDate,
			LocalDate endDate,
			LocalTime startTime,
			LocalTime endTime,
			String siteName,
			Integer slotDurationInMin
	)
	{
		List<CalendarEvent> allCalendarEvents;
		List<Integer> hiddenDaysList;
		List<String> providerIdList;
		boolean visibleSchedules = false;

		if(siteName == null || siteService.isProviderAssignedToSite(String.valueOf(providerId), siteName))
		{
			providerIdList = new ArrayList<>(1);
			providerIdList.add(String.valueOf(providerId));

			Integer siteId = null;
			if(siteName != null)
			{
				Site site = siteDao.findByName(siteName);
				if(site != null)
				{
					siteId = site.getSiteId();
				}
			}

			if(viewSchedulesOnly)
			{
				allCalendarEvents = new ArrayList<>();

				/* The fullCalendar plugin we are using has a hiddenDays parameter that allows us to hide certain days in the middle of a week/month view etc.
				   We are utilizing this setting to create the schedule view, but we don't want to send back events for hidden days, so here we are building a
				   'day of the week' filter and sending back the days to be hidden based on if they have a schedule set up.
				 */
				int[] daysWithSchedules = {0, 0, 0, 0, 0, 0, 0};

				//TODO-legacy somehow consolidate with regular getCalendarEvents method
				// Loop through the dates between startDate and endDate (inclusive) and add schedule templates
				for(LocalDate date : ConversionUtils.getDateList(startDate, endDate))
				{
					// Get schedule templates for this provider/date
					List<CalendarEvent> eventList = scheduleTemplateService.getCalendarEventsScheduleOnly(providerId, date, startTime, endTime, siteId);
					if(eventList != null)
					{
						// provider has a schedule, add them to results normally
						allCalendarEvents.addAll(eventList);

						int dayOfWeek = date.getDayOfWeek().getValue(); // 1 index based starting Monday
						dayOfWeek = dayOfWeek % 7;// shift to be 0 index based starting on Sunday
						daysWithSchedules[dayOfWeek] = 1;
					}
				}
				hiddenDaysList = new ArrayList<>(7);
				for(int i = 0; i < daysWithSchedules.length; i++)
				{
					if(daysWithSchedules[i] == 0)
					{
						hiddenDaysList.add(i);
					}
					else if(daysWithSchedules[i] == 1)
					{
						visibleSchedules = true;
					}
				}

				// Get appointments for this provider/date range
				allCalendarEvents.addAll(appointmentService.getCalendarEvents(
						session, providerId, startDate, endDate, siteName, hiddenDaysList));
			}
			else
			{
				allCalendarEvents = getCalendarEvents(session, providerId,
						startDate, endDate, startTime, endTime, siteName, siteId, slotDurationInMin);
				hiddenDaysList = new ArrayList<>(0); //always empty for all view
				visibleSchedules = true;
			}
		}
		else //provider not available with this site, return nothing.
		{
			allCalendarEvents = new ArrayList<>(0);
			hiddenDaysList = Arrays.asList(0,1,2,3,4,5,6); //hide all the days (for consistency)
			providerIdList = new ArrayList<>(0);
			visibleSchedules = false;
		}

		CalendarSchedule calendarSchedule = new CalendarSchedule();

		calendarSchedule.setGroupName(String.valueOf(providerId));
		calendarSchedule.setProviderIdList(providerIdList);
		calendarSchedule.setEventList(allCalendarEvents);
		calendarSchedule.setPreferredSlotDuration(slotDurationInMin);
		calendarSchedule.setVisibleSchedules(visibleSchedules);
		calendarSchedule.setHiddenDaysList(hiddenDaysList);

		return calendarSchedule;
	}

	public CalendarSchedule getCalendarScheduleByGroup(
			HttpSession session,
			String groupName,
			boolean viewSchedulesOnly,
			LocalDate startDate,
			LocalDate endDate,
			LocalTime startTime,
			LocalTime endTime,
			String siteName,
			Integer slotDurationInMin
	)
	{
		String userProviderNo = (String) session.getAttribute("user");

		Integer siteId = null;
		if(siteName != null)
		{
			Site site = siteDao.findByName(siteName);
			if(site != null)
			{
				siteId = site.getSiteId();
			}
		}

		List<MyGroup> userGroupMappings;
		if(viewSchedulesOnly)
		{
			userGroupMappings = myGroupDao.getGroupWithScheduleByGroupNo(groupName, startDate, Integer.parseInt(userProviderNo));
		}
		else
		{
			userGroupMappings = myGroupDao.getGroupByGroupNo(groupName);
		}

		List<String> providerIdList = new ArrayList<>(userGroupMappings.size());
		List<CalendarEvent> allCalendarEvents = new ArrayList<>();
		for(MyGroup userGroup : userGroupMappings)
		{
			String providerIdStr = userGroup.getId().getProviderNo();
			List<CalendarEvent> calendarEvents;

			// filter by site selection if applicable
			if(siteName != null)
			{
				if (!siteService.isProviderAssignedToSite(providerIdStr, siteName))
				{ // skip this provider
					continue;
				}
			}

			if(viewSchedulesOnly)
			{
				//TODO-legacy refactor similar logic with provider version
				calendarEvents = new ArrayList<>();

				// Loop through the dates between startDate and endDate (inclusive) and add schedule templates
				for(LocalDate date: ConversionUtils.getDateList(startDate, endDate))
				{
					// Get schedule templates for this provider/date/site
					List<CalendarEvent> eventList = scheduleTemplateService.getCalendarEventsScheduleOnly(Integer.parseInt(providerIdStr), date, startTime, endTime, siteId);
					if(eventList != null)
					{
						// only add the provider to the provider list if they have a schedule for the correct site
						calendarEvents.addAll(eventList);
						providerIdList.add(providerIdStr);

						// Get appointments for this provider/date range
						calendarEvents.addAll(appointmentService.getCalendarEvents(
								session, Integer.parseInt(providerIdStr), startDate, endDate, siteName));
					}
				}
			}
			else
			{
				providerIdList.add(providerIdStr);

				calendarEvents = getCalendarEvents(session, Integer.parseInt(providerIdStr),
						startDate, endDate, startTime, endTime, siteName, siteId, slotDurationInMin);
			}
			allCalendarEvents.addAll(calendarEvents);
		}

		CalendarSchedule calendarSchedule = new CalendarSchedule();

		calendarSchedule.setGroupName(groupName);
		calendarSchedule.setProviderIdList(providerIdList);
		calendarSchedule.setVisibleSchedules(!providerIdList.isEmpty());
		calendarSchedule.setEventList(allCalendarEvents);
		calendarSchedule.setPreferredSlotDuration(5); //TODO-legacy calculate based on lowest common slot size
		calendarSchedule.setHiddenDaysList(new ArrayList<>(0)); // always empty in group view

		return calendarSchedule;
	}

	// TODO-legacy: Generic provider schedule availability lookup shouldn't require booking rules and schedule template transfer
	public List<ScheduleSlotDto> getProviderAvailability(
			String[] providerNos, LocalDate startDate, LocalDate endDate, String demographicNo,
			String jsonTemplateDurations, String jsonRules) throws org.json.simple.parser.ParseException
	{
		List<ScheduleCodeDurationTransfer> scheduleDurationTransfers = ScheduleCodeDurationTransfer.parse(jsonTemplateDurations);
		BookingRules bookingRules = new BookingRules(jsonRules);

		List<List<ScheduleSlotDto>> allAvailableSlots = Collections.synchronizedList(new ArrayList<>());

		ExecutorService pool = Executors.newFixedThreadPool(4);

		for (String providerNo : providerNos)
		{
			pool.execute(() -> {
				List<ScheduleSlotDto> scheduleSlots = scheduleTemplateDao.getScheduleSlotsForProvider(
						providerNo, startDate, endDate, demographicNo, scheduleDurationTransfers, bookingRules);

				allAvailableSlots.add(scheduleSlots);
			});
		}

		pool.shutdown();

		try
		{
			pool.awaitTermination(1, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			MiscUtils.getLogger().error("Starting interrupted");
		}

		// Return unique list of provider availability schedule slots
		return allAvailableSlots.stream()
					.flatMap(Collection::stream)
					.sorted(Comparator.comparing(ScheduleSlotDto::getDateTime))
					.distinct()
					.collect(Collectors.toList());
	}

	// TODO-legacy: See: getProviderAvailability
	public ConcurrentHashMap<String, List<ScheduleSlotDto>> getProviderSlotsInRange(
			String[] providerNos, org.oscarehr.common.model.Appointment appointment,
			String jsonTemplateDurations, String jsonRules) throws org.json.simple.parser.ParseException
	{
		ConcurrentHashMap<String, List<ScheduleSlotDto>> providerSlotMap = new ConcurrentHashMap<>();

		final long MINUTE_RANGE = 60;

		List<ScheduleCodeDurationTransfer> scheduleDurationTransfers = ScheduleCodeDurationTransfer.parse(jsonTemplateDurations);
		BookingRules bookingRules = new BookingRules(jsonRules);

		LocalDateTime apptDateTime = appointment.getStartDateTime();
		LocalDate appointmentDate = apptDateTime.toLocalDate();

		ExecutorService pool = Executors.newFixedThreadPool(4);
		for (String providerNo : providerNos)
		{
			pool.execute(() -> {
				List<ScheduleSlotDto> availableSlots = scheduleTemplateDao.getScheduleSlotsForProvider(
						providerNo, appointmentDate, appointmentDate, String.valueOf(appointment.getDemographicNo()),
						scheduleDurationTransfers, bookingRules);

				List<ScheduleSlotDto> slotsInRange = ScheduleSlotDto.slotsInRangeOfAvailableTime(
						availableSlots, apptDateTime, MINUTE_RANGE, ChronoUnit.MINUTES);

				if (!slotsInRange.isEmpty())
				{
					providerSlotMap.put(providerNo, slotsInRange);
				}
			});
		}

		pool.shutdown();

		try
		{
			pool.awaitTermination(1, TimeUnit.MINUTES);
		}
		catch (InterruptedException e)
		{
			MiscUtils.getLogger().error(
					"Thread execution interrupted while getting provider slots threshold", e);
		}

		return providerSlotMap;
	}
}
