/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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

package org.oscarehr.schedule.service;

import com.google.common.collect.RangeMap;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ScheduleTemplateDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.Provider;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.schedule.dto.ResourceSchedule;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.dto.UserDateSchedule;
import org.oscarehr.schedule.dao.RScheduleDao;
import org.oscarehr.schedule.dao.ScheduleDateDao;
import org.oscarehr.schedule.dao.ScheduleHolidayDao;
import org.oscarehr.schedule.model.RSchedule;
import org.oscarehr.schedule.model.ScheduleDate;
import org.oscarehr.schedule.model.ScheduleHoliday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.HScheduleDate;
import oscar.HScheduleHoliday;
import oscar.MyDateFormat;
import oscar.RscheduleBean;
import oscar.util.ConversionUtils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.SortedMap;


@Service
@Transactional
public class Schedule
{
	@Autowired
	ScheduleDateDao scheduleDateDao;

	@Autowired
	RScheduleDao rScheduleDao;

	@Autowired
	ScheduleHolidayDao scheduleHolidayDao;


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
			scheduleDateBean.put(ConversionUtils.toDateString(sd.getDate()), new HScheduleDate(String.valueOf(sd.getAvailable()), String.valueOf(sd.getPriority()), sd.getReason(), sd.getHour(), sd.getCreator()));
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
				sd.setAvailable('1');
				sd.setPriority('b');
				sd.setReason(scheduleRscheduleBean.getSiteAvail(cal));
				sd.setHour(scheduleRscheduleBean.getDateAvailHour(cal));
				sd.setCreator(providerName);
				sd.setStatus(scheduleRscheduleBean.active.toCharArray()[0]);
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

		List<RSchedule> rsl = rScheduleDao.findByProviderNoAndDates(providerNo, startDate);
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
	 * delete any existing schedules by date & provider, and create a new one with the given values.
	 * @param providerNoStr provider id
	 * @param date date of schedule
	 * @param available 1 or 0
	 * @param priority priority
	 * @param reason reason
	 * @param hour hour
	 * @param userName username
	 */
	public void saveScheduleByDate(String providerNoStr, Date date, String available, String priority, String reason, String hour, String userName, String status)
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
		sd.setAvailable(available.toCharArray()[0]);
		sd.setPriority(priority.toCharArray()[0]);
		sd.setReason(reason);
		sd.setHour(hour);
		sd.setCreator(userName);
		sd.setStatus(status.toCharArray()[0]);
		scheduleDateDao.persist(sd);
	}

	/**
	 * Get the schedule for the provider on the date.
	 * @param providerNo Provider to get schedule for.
	 * @param date Date to get schedule for.
	 * @return The schedule for this provider.
	 */
	public ResourceSchedule getResourceScheduleByProvider(String providerNo, LocalDate date, String site)
	{
		Provider provider = providerDao.getProvider(providerNo);

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		// get a UserDateSchedule for each
		userDateSchedules.add(getUserDateSchedule(
			date,
			new Integer(provider.getProviderNo()),
			provider.getFirstName(),
			provider.getLastName(),
			site
		));

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	/**
	 * Get the schedule for the provided date for each member of the group.
	 * @param group The name of the group to get the schedule for.
	 * @param date The date to get the schedule for.
	 * @return The schedule for the group.
	 */
	public ResourceSchedule getResourceScheduleByGroup(String group, LocalDate date, String site)
	{
		List<MyGroup> results = myGroupDao.getGroupByGroupNo(group);

		List<UserDateSchedule> userDateSchedules = new ArrayList<>();

		for(MyGroup result: results)
		{
			// get a UserDateSchedule for each
			userDateSchedules.add(getUserDateSchedule(
				date,
				new Integer(result.getId().getProviderNo()),
				result.getFirstName(),
				result.getLastName(),
				site
			));
		}

		// Create transfer object
		return new ResourceSchedule(userDateSchedules);
	}

	/**
	 * Get the provider's schedule for the week (sun-sat) that includes the date.
	 * @param providerNo Provider to get the schedule for.
	 * @param date Get the schedule for the week (sun-sat) including this date.
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
		LocalDate date, Integer providerNo, String firstName, String lastName, String site)
	{
		// Get schedule slots
		RangeMap<LocalTime, ScheduleSlot> scheduleSlots = scheduleTemplateDao.findScheduleSlots(date, providerNo);

		// Get appointments
		SortedMap<LocalTime, List<AppointmentDetails>> appointments =
			appointmentDao.findAppointmentDetailsByDateAndProvider(date, providerNo, site);

		return new UserDateSchedule(providerNo, date, firstName, lastName, scheduleSlots, appointments);
	}
}
