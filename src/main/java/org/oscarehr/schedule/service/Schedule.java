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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.schedule.service;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

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


	public void updateSchedule(RscheduleBean scheduleRscheduleBean,
	                           Hashtable<String, HScheduleDate> scheduleDateBean,
	                           Hashtable<String, HScheduleHoliday> scheduleHolidayBean,
	                           String available,
	                           String dayOfWeek1,
	                           String dayOfWeek2,
	                           String availableHour1,
	                           String availableHour2,
	                           String providerNo,
	                           String providerName,
	                           String startDate,
	                           String endDate,
	                           String originalDate,
	                           GregorianCalendar cal,
	                           int yearLimit) throws ParseException
	{
		if(startDate.equals(scheduleRscheduleBean.sdate))
		{
			List<RSchedule> rsl = rScheduleDao.findByProviderAvailableAndDate(providerNo, "1", MyDateFormat.getSysDate(startDate));
			for(RSchedule rs : rsl)
			{
				rs.setStatus("D");
				rScheduleDao.merge(rs);
			}
			rsl = rScheduleDao.findByProviderAvailableAndDate(providerNo, "A", MyDateFormat.getSysDate(startDate));
			for(RSchedule rs : rsl)
			{
				rs.setStatus("D");
				rScheduleDao.merge(rs);
			}
		}


		Long overLapResult = rScheduleDao.search_rschedule_overlaps(providerNo, ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate),
				ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate), ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate),
				ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate), ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate),
				ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate), ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate));


		boolean scheduleOverlaps = overLapResult > 0;

		//if the schedule is the same we are editing instead

		Long existsResult = rScheduleDao.search_rschedule_exists(providerNo, ConversionUtils.fromDateString(startDate), ConversionUtils.fromDateString(endDate));
		boolean editingSchedule = existsResult > 0;

		//save rschedule data
		scheduleRscheduleBean.setRscheduleBean(providerNo, startDate, endDate, available, dayOfWeek1, dayOfWeek2, availableHour1, availableHour2, providerName);

		if(editingSchedule)
		{
			List<RSchedule> rsl = rScheduleDao.findByProviderAvailableAndDate(scheduleRscheduleBean.provider_no, scheduleRscheduleBean.available, MyDateFormat.getSysDate(scheduleRscheduleBean.sdate));
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
			rs.setsDate(MyDateFormat.getSysDate(scheduleRscheduleBean.sdate));
			rs.seteDate(MyDateFormat.getSysDate(scheduleRscheduleBean.edate));
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
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dnewEdate = df.parse(endDate);
		java.util.Date dorigEdate = df.parse(originalDate);

		List<ScheduleDate> sds = scheduleDateDao.findByProviderPriorityAndDateRange(providerNo, 'b', MyDateFormat.getSysDate(startDate), MyDateFormat.getSysDate(dnewEdate.before(dorigEdate) ? originalDate : endDate));
		for(ScheduleDate sd : sds)
		{
			sd.setStatus('D');
			scheduleDateDao.merge(sd);
		}

		for(int i = 0; i < 365 * yearLimit; i++)
		{
			int y = cal.get(Calendar.YEAR);
			int m = cal.get(Calendar.MONTH) + 1;
			int d = cal.get(Calendar.DATE);
			if(scheduleDateBean.get(y + "-" + MyDateFormat.getDigitalXX(m) + "-" + MyDateFormat.getDigitalXX(d)) == null && scheduleRscheduleBean.getDateAvail(cal))
			{
				ScheduleDate sd = new ScheduleDate();
				sd.setDate(MyDateFormat.getSysDate(y + "-" + m + "-" + d));
				sd.setProviderNo(providerNo);
				sd.setAvailable('1');
				sd.setPriority('b');
				sd.setReason(scheduleRscheduleBean.getSiteAvail(cal));
				sd.setHour(scheduleRscheduleBean.getDateAvailHour(cal));
				sd.setCreator(providerName);
				sd.setStatus(scheduleRscheduleBean.active.toCharArray()[0]);
				scheduleDateDao.persist(sd);
			}
			if((y + "-" + MyDateFormat.getDigitalXX(m) + "-" + MyDateFormat.getDigitalXX(d)).equals(endDate)) break;
			cal.add(Calendar.DATE, 1);
		}
	}
}
