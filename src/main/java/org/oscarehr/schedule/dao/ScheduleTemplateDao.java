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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.managers.ScheduleManager;
import org.oscarehr.schedule.model.ScheduleSearchResult;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.ws.external.soap.v1.transfer.ScheduleCodeDurationTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.DayTimeSlots;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.ProviderScheduleTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import static org.oscarehr.schedule.model.ScheduleTemplatePrimaryKey.DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleTemplateDao extends AbstractDao<ScheduleTemplate>
{
	@Autowired
	private ScheduleManager scheduleManager;

	private static final int SCHEDULE_SLOT_DURATION = 5;

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
        		"	OR st.id.providerNo = :publicCode " +
        		") ORDER BY sd.date";
		Query query = entityManager.createQuery(sql);
		query.setParameter("date_from", date_from);
		query.setParameter("date_to", date_to);
		query.setParameter("provider_no", provider_no);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
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
				"	OR st.providerNo = :publicCode " +
				") ORDER BY sd.date";
		Query query = entityManager.createQuery(sql);
		query.setParameter("dateFrom", dateFrom);
		query.setParameter("providerIds", providerIds);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
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
		String sql = "select timecode from scheduletemplate, (select hour from (select provider_no, hour, status from scheduledate where sdate = :date) as df where status = 'A' and provider_no= :providerNo) as hf where scheduletemplate.name=hf.hour and (scheduletemplate.provider_no= :providerNo or scheduletemplate.provider_no= :publicCode)";
		Query query = entityManager.createNativeQuery(sql, modelClass);
		query.setParameter("date", date);
		query.setParameter("providerNo", providerNo);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		return query.getResultList();
	}
	
	//TODO:modelClass causing error on master record
	@NativeSql({"scheduletemplate", "scheduledate"})
	public List<Object> findTimeCodeByProviderNo2(String providerNo, Date date) {
		String sql = "select timecode from scheduletemplate, (select hour from (select provider_no, hour, status from scheduledate where sdate = :date) as df where status = 'A' and provider_no= :providerNo) as hf where scheduletemplate.name=hf.hour and (scheduletemplate.provider_no= :providerNo or scheduletemplate.provider_no= :publicCode)";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("date", date);
		query.setParameter("providerNo", providerNo);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		return query.getResultList();
	}

	public List<Object[]> getRawScheduleSlots(Integer providerNo, LocalDate date)
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
				"JOIN scheduletemplate st ON (sd.hour = st.name AND (sd.provider_no = st.provider_no OR st.provider_no = :publicCode ))\n" +
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
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);

		return query.getResultList();
	}

	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public RangeMap<LocalTime, ScheduleSlot> findScheduleSlots(LocalDate date, Integer providerNo)
	{
		List<Object[]> results = getRawScheduleSlots(providerNo, date);

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

	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public ProviderScheduleTransfer getValidProviderScheduleSlots(String providerNo, LocalDate startDate, LocalDate endDate, List<ScheduleCodeDurationTransfer> scheduleCodeDurationTransfer, String demographicNo, List<BookingRule> bookingRules)
	{
		List<String> appointmentTypeList = ScheduleCodeDurationTransfer.getAllTemplateCodes(scheduleCodeDurationTransfer);

		if (startDate.isBefore(LocalDate.now()))
		{
			startDate = LocalDate.now();
		}

		String sql = "SELECT STRAIGHT_JOIN\n" +
				"  SUBSTRING(st.timecode, (n3.i + (10 * n2.i) + (100 * n1.i))+1, 1) AS code_char,\n" +
				"  sd.sdate AS appt_date,\n" +
				"  SEC_TO_TIME(ROUND((24*60*60)*(n3.i + (10 * n2.i) + (100 * n1.i))/LENGTH(st.timecode))) AS appt_time,\n" +
				"  stc.code,\n" +
				"  CAST(COALESCE(stc.duration, ((24*60)/LENGTH(st.timecode))) AS integer) AS duration\n" +
				"FROM \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2) as n1    \n" +
				"    CROSS JOIN \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) as n2     \n" +
				"    CROSS JOIN \n" +
				"    (SELECT 0 as i UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) as n3 \n" +
				"CROSS JOIN scheduledate sd\n" +
				"JOIN scheduletemplate st ON (sd.hour = st.name AND (sd.provider_no = st.provider_no OR st.provider_no = :publicCode ))\n" +
				"LEFT JOIN scheduletemplatecode stc " +
				"  ON BINARY stc.code = SUBSTRING(st.timecode, (n3.i + (10 * n2.i) + (100 * n1.i))+1, 1)\n" +
				"WHERE sd.status = 'A'\n" +
				"AND stc.code IN (:appointmentTypes) \n" +
				"AND sd.sdate BETWEEN :minDate AND :maxDate\n" +
				"AND sd.provider_no = :providerNo\n" +
				"AND (n3.i + (10 * n2.i) + (100 * n1.i)) < LENGTH(st.timecode)\n" +
				"ORDER BY sd.sdate, (n3.i + (10 * n2.i) + (100 * n1.i));";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("minDate", java.sql.Date.valueOf(startDate), TemporalType.DATE);
		query.setParameter("maxDate", java.sql.Date.valueOf(endDate), TemporalType.DATE);
		query.setParameter("providerNo", providerNo);
		query.setParameter("appointmentTypes", appointmentTypeList);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);

		List<Object[]> results = query.getResultList();

		/*
		 * Use a linked list here, because we will be removing individual elements from this list if they violate booking rules.
		 * Adding to the end is not a concern here because the LinkedList is doubly linked.
		 */
		LinkedList<ScheduleSearchResult> possibleSlots = new LinkedList<>();

		for (Object[] result : results)
		{
			java.sql.Date date = (java.sql.Date) result[1];
			java.sql.Time time = (java.sql.Time) result[2];
			char templateCode = (char) result[3];
			Long length = ((BigInteger)result[4]).longValueExact();

			ScheduleSearchResult ssr = new ScheduleSearchResult(date, time, templateCode, length, providerNo);

			possibleSlots.addLast(ssr);
		}

		applyRules(bookingRules, possibleSlots);

		Map<LocalDate, List<Appointment>> monthlyAppointments = scheduleManager.getProviderAppointmentsForMonth(providerNo, startDate, endDate);
		return generateAppointmentSlots(possibleSlots, monthlyAppointments, scheduleCodeDurationTransfer);
	}

	private ProviderScheduleTransfer generateAppointmentSlots(
			List<ScheduleSearchResult> results, Map<LocalDate, List<Appointment>> monthlyAppointments,
			List<ScheduleCodeDurationTransfer> scheduleCodeDurationTransfer)
	{
		HashMap<String, List<DayTimeSlots>> providerSchedule = new HashMap<>();

		HashMap<String, Boolean> scheduleArrMap = new HashMap<>();

		ProviderScheduleTransfer scheduleResponse = new ProviderScheduleTransfer();

		int bookingDuration = scheduleCodeDurationTransfer.get(0).getDurationMinutes();

		for (ScheduleSearchResult result : results)
		{
			List<DayTimeSlots> dayTimeSlots;

			List<Appointment> dayAppointments = monthlyAppointments.get(result.dateTime.toLocalDate());
			List<Map<String, LocalTime>> appointmentsTimeMap = this.getAppointmentsTimeMap(dayAppointments);

			String scheduleDate = result.dateTime.toLocalDate().toString();
			// String timeSlotCodeStr = Character.toString(result.templateCode);

			LocalTime windowSlotStartTime = result.dateTime.toLocalTime();
			LocalTime scheduleSlotEndTime = windowSlotStartTime.plusMinutes(result.length);

			if (bookingDuration > result.length)
			{
				continue;
			}

			// Loop through all the 5 minute iterations of this schedule slot, and check whether or not
			// there is an appointment booked within that 5 minute window in this specific slot
			while (windowSlotStartTime.isBefore(scheduleSlotEndTime))
			{
				Long maxBookingDuration = this.getMaxBookingDurationForSlot(appointmentsTimeMap, windowSlotStartTime, scheduleSlotEndTime);

				if (maxBookingDuration > 0L && maxBookingDuration >= bookingDuration)
				{
					LocalDateTime windowDateTime = LocalDateTime.of(result.dateTime.toLocalDate(), windowSlotStartTime);

					DayTimeSlots timeSlotEntry = new DayTimeSlots(
						windowDateTime.toString(),
						String.valueOf(bookingDuration)
					);

					// scheduleArrMap keeps track of schedule slots that have already been added to this slot's date.
					// Update the schedule slots for this day if it's in scheduleArrMap. Otherwise, create a new list of schedule slots.
					if (!scheduleArrMap.containsKey(scheduleDate))
					{
						scheduleArrMap.put(scheduleDate, true);
						dayTimeSlots = new ArrayList<>();
					}
					else
					{
						dayTimeSlots = providerSchedule.get(scheduleDate);
					}

					dayTimeSlots.add(timeSlotEntry);
					providerSchedule.put(scheduleDate, dayTimeSlots);

				}

				windowSlotStartTime = windowSlotStartTime.plusMinutes(bookingDuration);
			}
		}

		scheduleResponse.setProviderScheduleResponse(providerSchedule);
		return scheduleResponse;
	}

	/**
	 * Remove elements from the list of possible slots if they violate any of the booking rules.  To minimize time complexity,
	 * we enforce the requirement that the schedule search results be stored in a LinkedList.
	 */
	private void applyRules(List<BookingRule> bookingRules, LinkedList<ScheduleSearchResult> possibleSlots)
	{
		/*
		 * An important consideration here is which .remove method is used.
		 *
		 * Java's LinkedList.remove is O(n) because it requires an additional find as the list cannot be modified in place
		 * without an iterator. Iterator.remove on a LinkedList is O(1).
		 */
		Iterator<ScheduleSearchResult> it = possibleSlots.iterator();

		while (it.hasNext())
		{
			ScheduleSearchResult result = it.next();

			for (BookingRule rule : bookingRules)
			{
				if (rule.isViolated(result))
				{
					it.remove();
					break;
				}
			}
		}
	}

	private List<Map<String, LocalTime>> getAppointmentsTimeMap(List<Appointment> dayAppointments)
	{
		List<Map<String, LocalTime>> startEndTimeMap = new ArrayList<>();

		if (dayAppointments != null)
		{
			for (Appointment appointment : dayAppointments)
			{
				LocalTime startTime = LocalTime.parse(appointment.getStartTime().toString());
				LocalTime endTime = LocalTime.parse(appointment.getEndTime().toString());
				Map<String, LocalTime> appointmentTime = new HashMap<>();

				appointmentTime.put("startTime", startTime);
				appointmentTime.put("endTime", endTime);

				startEndTimeMap.add(appointmentTime);
			}
		}

		return startEndTimeMap;
	}

	private boolean maxBookingDurationIsValid(List<ScheduleCodeDurationTransfer> codeDurationTransfers, Long maxBookingDuration)
	{
		int maxTransferDuration = codeDurationTransfers.get(0).getDurationMinutes();
		int minTransferDuration = codeDurationTransfers.get(codeDurationTransfers.size() - 1).getDurationMinutes();

		return maxBookingDuration <= maxTransferDuration && maxBookingDuration >= minTransferDuration;
	}

	private List<Appointment> getPatientAppointmentsForBookingRules(String demographicNo, String providerNo, LocalDate minDate, LocalDate maxDate, Integer daysToQuery)
	{
		LocalDate minMultiDate = minDate.minusDays(daysToQuery);
		LocalDate maxMultiDate = maxDate.plusDays(daysToQuery);

		return scheduleManager.getPatientAppointmentsWithProvider(demographicNo, providerNo, minMultiDate, maxMultiDate);
	}

	private Long getMaxBookingDurationForSlot(List<Map<String, LocalTime>> appointmentsTimeMap, LocalTime windowStartTime, LocalTime scheduleSlotEndTime)
	{
		Long maxBookingDuration = Duration.between(windowStartTime, scheduleSlotEndTime).toMinutes();

		if (appointmentsTimeMap.isEmpty())
		{
			return maxBookingDuration;
		}
		else
		{
			// Loop through each scheduled appointment for this day, and check if it falls within this
			// 5 minute window of this specific schedule slot
			for (Map<String, LocalTime> appointmentTime : appointmentsTimeMap)
			{
				LocalTime bookedStartTime = appointmentTime.get("startTime");
				LocalTime bookedEndTime = appointmentTime.get("endTime");

				// This 5 minute slot falls within a booked appointment
				if (windowStartTime.equals(bookedStartTime) || (windowStartTime.isAfter(bookedStartTime) && windowStartTime.isBefore(bookedEndTime)))
				{
					return 0L;
				}
				// There is a booked appointment within the 5 minute slot, and the schedule slot's end time
				else if (bookedStartTime.isAfter(windowStartTime) && bookedStartTime.isBefore(scheduleSlotEndTime))
				{
					Long durationBetween = Duration.between(windowStartTime, bookedStartTime).toMinutes();
					if (durationBetween < maxBookingDuration)
					{
						maxBookingDuration = durationBetween;
					}
				}
			}
		}

		return maxBookingDuration;
	}
}
