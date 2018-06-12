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


package org.oscarehr.schedule.dao;

import java.math.BigInteger;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.oscarehr.common.NativeSql;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.common.dao.AbstractDao;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleTemplateDao extends AbstractDao<ScheduleTemplate>
{
	
	public ScheduleTemplateDao() {
		super(ScheduleTemplate.class);
	}
	
	public List<ScheduleTemplate> findBySummary(String summary) {
		Query query = entityManager.createQuery("SELECT e FROM ScheduleTemplate e WHERE e.summary=? ");
		query.setParameter(1, summary);

        List<ScheduleTemplate> results = query.getResultList();
		return results;
	}

	public List<Object[]> findSchedules(Date date_from, Date date_to, String provider_no) {
	    String sql = "FROM ScheduleTemplate st, ScheduleDate sd " +
        		"WHERE st.id.name = sd.hour " +
        		"AND sd.date >= :date_from " + 
        		"AND sd.date <= :date_to " +
        		"AND sd.providerNo = :provider_no " +
        		"AND sd.status = 'A' " +
        		"AND (" +
        		"	st.id.providerNo = sd.providerNo " +
        		"	OR st.id.providerNo = 'Public' " +
        		") ORDER BY sd.date";
		Query query = entityManager.createQuery(sql);
		query.setParameter("date_from", date_from);
		query.setParameter("date_to", date_to);
		query.setParameter("provider_no", provider_no);
		return query.getResultList();
    }

	public List<Object[]> findSchedules(Date dateFrom, List<String> providerIds) {
		String sql = "FROM ScheduleTemplate st, ScheduleDate sd " +
				"WHERE st.id.name = sd.hour " +
				"AND sd.date >= :dateFrom " +
				"AND sd.providerNo in ( :providerIds ) " +
				"AND sd.status = 'A' " +
				"AND (" +
				"	st.providerNo = sd.providerNo " +
				"	OR st.providerNo = 'Public' " +
				") ORDER BY sd.date";
		Query query = entityManager.createQuery(sql);
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("providerIds", providerIds);
		return query.getResultList();
    }

	public List<ScheduleTemplate> findByProviderNoAndName(String providerNo, String name) {
		Query query = entityManager.createQuery("SELECT e FROM ScheduleTemplate e WHERE e.id.providerNo=? and e.id.name=? ");
		query.setParameter(1, providerNo);
		query.setParameter(2, name);

        List<ScheduleTemplate> results = query.getResultList();
		return results;
	}
	
	public List<ScheduleTemplate> findByProviderNo(String providerNo) {
		Query query = entityManager.createQuery("SELECT e FROM ScheduleTemplate e WHERE e.id.providerNo=? order by e.id.name");
		query.setParameter(1, providerNo);
		
        List<ScheduleTemplate> results = query.getResultList();
		return results;
	}
	
	@NativeSql({"scheduletemplate", "scheduledate"})
	public List<Object> findTimeCodeByProviderNo(String providerNo, Date date) {
		String sql = "select timecode from scheduletemplate, (select hour from (select provider_no, hour, status from scheduledate where sdate = :date) as df where status = 'A' and provider_no= :providerNo) as hf where scheduletemplate.name=hf.hour and (scheduletemplate.provider_no= :providerNo or scheduletemplate.provider_no='Public')";
		Query query = entityManager.createNativeQuery(sql, modelClass);
		query.setParameter("date", date);
		query.setParameter("providerNo", providerNo);
		return query.getResultList();
	}
	
	//TODO:modelClass causing error on master record
	@NativeSql({"scheduletemplate", "scheduledate"})
	public List<Object> findTimeCodeByProviderNo2(String providerNo, Date date) {
		String sql = "select timecode from scheduletemplate, (select hour from (select provider_no, hour, status from scheduledate where sdate = :date) as df where status = 'A' and provider_no= :providerNo) as hf where scheduletemplate.name=hf.hour and (scheduletemplate.provider_no= :providerNo or scheduletemplate.provider_no='Public')";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("date", date);
		query.setParameter("providerNo", providerNo);
		return query.getResultList();
	}


	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public RangeMap<LocalTime, ScheduleSlot> findScheduleSlots(LocalDate date, Integer providerNo)
	{
		// This query is a bit hard to read.  The mess with all of the UNION ALLs is a way to make a
		// sequence of numbers.  This is then used to find the position in the scheduletemplate.timecode
		// value to split it into rows so it can be joined.
		// It uses the STRAIGHT_JOIN planner hint because the scheduletemplatecode table was being
		// joined too soon by default.
		String sql = "SELECT STRAIGHT_JOIN\n" +
				"  (n3.i + (10 * n2.i) + (100 * n1.i))+1 AS position, \n" +
				"  SUBSTRING(st.timecode, (n3.i + (10 * n2.i) + (100 * n1.i))+1, 1) AS code_char,\n" +
				"  sd.sdate AS appt_date,\n" +
				"  SEC_TO_TIME(ROUND((24*60*60)*(n3.i + (10 * n2.i) + (100 * n1.i))/LENGTH(st.timecode))) AS appt_time,\n" +
				"  stc.code,\n" +
				"  CAST(COALESCE(stc.duration, ((24*60)/LENGTH(st.timecode))) AS integer) AS duration,\n" +
				"  stc.description,\n" +
				"  stc.color,\n" +
				"  stc.confirm,\n" +
				"  stc.bookinglimit\n" +
				"FROM \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) as n1    \n" +
				"    CROSS JOIN \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) as n2     \n" +
				"    CROSS JOIN \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) as n3 \n" +
				"CROSS JOIN scheduledate sd\n" +
				"JOIN scheduletemplate st ON sd.hour = st.name\n" +
				"LEFT JOIN scheduletemplatecode stc " +
				"  ON BINARY stc.code = SUBSTRING(st.timecode, (n3.i + (10 * n2.i) + (100 * n1.i))+1, 1)\n" +
				"WHERE sd.status = 'A'\n" +
				"AND sd.sdate = :date\n" +
				"AND sd.provider_no = :providerNo\n" +
				"AND (n3.i + (10 * n2.i) + (100 * n1.i)) < LENGTH(st.timecode)\n" +
				"ORDER BY (n3.i + (10 * n2.i) + (100 * n1.i));";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("date", java.sql.Date.valueOf(date), TemporalType.DATE);
		query.setParameter("providerNo", providerNo);

		List<Object[]> results = query.getResultList();

		RangeMap<LocalTime, ScheduleSlot> slots = TreeRangeMap.create();
		for(Object[] result: results)
		{
			java.sql.Date appointmentDate = (java.sql.Date) result[2];
			Time appointmentTime = (java.sql.Time) result[3];
			String code = (String) result[1];
			Integer durationMinutes = ((BigInteger) result[5]).intValue();
			String description = (String) result[6];
			String color = (String) result[7];
			String confirm = (String) result[8];
			Integer bookingLimit = (Integer) result[9];

			LocalDate slotDate = appointmentDate.toLocalDate();
			LocalTime slotTime = appointmentTime.toLocalTime();

			LocalDateTime appointmentDateTime = LocalDateTime.of(slotDate, slotTime);

			// Get the end time by adding the duration
			Range range;
			Duration slotDuration = Duration.ofMinutes(durationMinutes);
			if(
				// Use Max time if the duration is more than a day or if the slot duration will wrap
				// to the next day
				slotDuration.compareTo(Duration.ofDays(1)) >= 0 ||
				LocalTime.MAX.minus(slotDuration).compareTo(slotTime) <= 0)
			{
				LocalTime endTime = LocalTime.MAX;
				range = Range.closed(slotTime, endTime);
			}
			else
			{
				LocalTime endTime = slotTime.plus(Duration.ofMinutes(durationMinutes));
				range = Range.closedOpen(slotTime, endTime);
			}

			slots.put(range, new ScheduleSlot(appointmentDateTime, code, durationMinutes, description,
					color, confirm, bookingLimit));
		}

		return slots;
	}
}
