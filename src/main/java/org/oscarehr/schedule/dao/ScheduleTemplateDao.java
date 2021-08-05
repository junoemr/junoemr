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

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import org.apache.log4j.Logger;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.schedule.dto.ScheduleSlot;
import org.oscarehr.schedule.model.ScheduleSearchResult;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.ScheduleCodeDurationTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.DayTimeSlots;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.ProviderScheduleTransfer;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.ScheduleSlotDto;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BlackoutRule;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRules;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.CutoffRule;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.MultipleBookingsRule;
import org.springframework.stereotype.Repository;
import oscar.OscarProperties;

import javax.persistence.Query;
import javax.persistence.TemporalType;
import java.math.BigInteger;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.oscarehr.schedule.model.ScheduleTemplatePrimaryKey.DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleTemplateDao extends AbstractDao<ScheduleTemplate>
{
	private static final Logger logger = MiscUtils.getLogger();
	private final boolean optimizeSmallSchedules = OscarProperties.getInstance().isOptimizeSmallSchedulesEnabled();

	public ScheduleTemplateDao() {
		super(ScheduleTemplate.class);
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

	public Integer getScheduleSlotLengthInMin(Integer providerNo, LocalDate date, Integer siteId)
	{
		Integer result = null;
		String sql = "SELECT " +
				"CAST(((24*60)/LENGTH(st.timecode)) AS integer) AS slotLength\n" +
				"FROM scheduledate sd " +
				"JOIN scheduletemplate st ON (sd.hour = st.name AND (sd.provider_no = st.provider_no OR st.provider_no = :publicCode ))\n" +
				"WHERE sd.status = 'A'\n" +
				"AND sd.available = :available\n" +
				"AND sd.sdate = :scheduleDate\n" +
				"AND sd.provider_no = :providerNo\n";
		if(siteId != null)
		{
			sql += "AND (sd.site_id = :siteId OR sd.site_id IS NULL)\n";
		}

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("scheduleDate", java.sql.Date.valueOf(date), TemporalType.DATE);
		query.setParameter("providerNo", providerNo);
		query.setParameter("available", 1);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		if(siteId != null)
		{
			query.setParameter("siteId", siteId);
		}

		List<BigInteger> results = query.getResultList();
		if(!results.isEmpty())
		{
			result = results.get(0).intValue();
			if(results.size() > 1)
			{
				logger.warn("Multiple values found for provider schedule slot length");
			}
		}
		return result;
	}

	private List<Object[]> getRawScheduleSlots(Integer providerNo, LocalDate date, Integer siteId)
	{
		String siteFilter = "";
		if(siteId != null)
		{
			siteFilter = "AND (sd.site_id = :siteId OR sd.site_id IS NULL)\n";
		}

		// This query is a bit hard to read.  The mess with all of the UNION ALLs is a way to make a
		// sequence of numbers.  This is then used to find the position in the scheduletemplate.timecode
		// value to split it into rows so it can be joined.
		// It uses the STRAIGHT_JOIN planner hint for large schedules because the scheduletemplatecode table was being
		// joined too soon by default.
		String sql;
		if(optimizeSmallSchedules)
		{
			sql = "SELECT\n";
			logger.info("Querying schedule and not using STRAIGHT_JOIN optimizer hint.");
		}
		else
		{
			sql = "SELECT STRAIGHT_JOIN\n";
		}

		sql +=  "  seq AS position, \n" +
				"  SUBSTRING(st.timecode, seq, 1) AS code_char,\n" +
				"  sd.sdate AS appt_date,\n" +
				"  SEC_TO_TIME(ROUND((24*60*60) * (seq - 1)/LENGTH(st.timecode))) AS appt_time,\n" +
				"  stc.code,\n" +
				"  CAST(COALESCE(stc.duration, ((24*60)/LENGTH(st.timecode))) AS integer) AS duration,\n" +
				"  stc.description,\n" +
				"  stc.color,\n" +
				"  stc.juno_color,\n" +
				"  stc.confirm,\n" +
				"  stc.bookinglimit\n" +
				"FROM scheduledate sd\n" +
				"JOIN scheduletemplate st ON (sd.hour = st.name AND (sd.provider_no = st.provider_no OR st.provider_no = :publicCode ))\n" +
				"CROSS JOIN seq_1_to_299 seq_no \n" +
				"LEFT JOIN scheduletemplatecode stc " +
				"  ON BINARY stc.code = SUBSTRING(st.timecode, seq, 1)\n" +
				"WHERE sd.status = 'A'\n" +
				"AND sd.available = :available\n" +
				"AND sd.sdate = :date\n" +
				siteFilter +
				"AND sd.provider_no = :providerNo\n" +
				"AND seq <= LENGTH(st.timecode)\n" +
				"ORDER BY seq;";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("date", java.sql.Date.valueOf(date), TemporalType.DATE);
		query.setParameter("providerNo", providerNo);
		query.setParameter("available", 1);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		if(siteId != null)
		{
			query.setParameter("siteId", siteId);
		}

		return query.getResultList();
	}

	public RangeMap<LocalTime, ScheduleSlot> findScheduleSlots(LocalDate date, Integer providerNo)
	{
		return findScheduleSlots(date, providerNo, null);
	}

	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public RangeMap<LocalTime, ScheduleSlot> findScheduleSlots(LocalDate date, Integer providerNo, Integer siteId)
	{
		List<Object[]> results = getRawScheduleSlots(providerNo, date, siteId);

		RangeMap<LocalTime, ScheduleSlot> slots = TreeRangeMap.create();
		for(Object[] result: results)
		{
			java.sql.Date appointmentDate = (java.sql.Date) result[2];
			Time appointmentTime = (java.sql.Time) result[3];
			String code = (String) result[1];
			Integer durationMinutes = ((BigInteger) result[5]).intValue();
			String description = (String) result[6];
			String color = (String) result[7];
			String junoColor = (String) result[8];
			String confirm = (String) result[9];
			Integer bookingLimit = (Integer) result[10];

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
					color, junoColor, confirm, bookingLimit));
		}

		return slots;
	}

	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public List<ScheduleSlotDto> getScheduleSlotsForProvider(
			String providerNo, LocalDate startDate, LocalDate endDate, String demographicNo,
			List<ScheduleCodeDurationTransfer> scheduleCodeDurationTransfer, BookingRules bookingRules) {

		List<String> appointmentTypeList = ScheduleCodeDurationTransfer.getAllTemplateCodes(scheduleCodeDurationTransfer);
		int appointmentDuration = scheduleCodeDurationTransfer.get(0).getDurationMinutes();

		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

		if (startDateTime.isBefore(LocalDateTime.now()))
		{
			startDateTime = LocalDateTime.now();
		}

		int multiBookingsRulesCount = 0;
		HashMap<String, Long> multiBookingsRuleParams = new HashMap<>();
		HashMap<String, Integer> multiBookingJoinTags = new HashMap<>();
		String multiBookingsRulesSelectSql = "";
		String multiBookingsRulesJoinSql = "";
		String multiBookingsRulesWhereSql = "";
		for (MultipleBookingsRule multipleBookingsRule : bookingRules.getMultipleBookingsRule())
		{
			String joinTag = "multi_bookings_appt_" + Integer.toString(multiBookingsRulesCount);

			multiBookingsRulesSelectSql += ", \n" +
					"  SUM(Case \n" +
					"    WHEN " + joinTag + ".appointment_no IS NOT NULL THEN 1 \n" +
					"    ELSE 0\n" +
					"  END) AS count_" + joinTag + " ";

			String durationTag = "multi_bookings_rule_" +
					Integer.toString(multiBookingsRulesCount) + "_duration";
			multiBookingsRuleParams.put(durationTag, multipleBookingsRule.getDurationOffsetDays());

			multiBookingsRulesJoinSql += "" +
					"  LEFT JOIN appointment AS " + joinTag + " ON\n" +
					"    " + joinTag + ".demographic_no = :demographicNo \n" +
					"    AND " + joinTag + ".status != 'C' \n" +
					"    AND " + joinTag + ".appointment_date <= " +
					"      (appointment_slots.slot_date + \n" +
					"      INTERVAL :" + durationTag + " DAY) \n" +
					"    AND " + joinTag + ".appointment_date >= " +
					"      (appointment_slots.slot_date - \n" +
					"      INTERVAL :" + durationTag + " DAY) \n";

			String multiBookingMaxAllowedTag = joinTag + "_max_allowed";
			multiBookingJoinTags.put(multiBookingMaxAllowedTag,
					multipleBookingsRule.getBookingAmount());
			multiBookingsRulesWhereSql += "" +
				"  count_" + joinTag + " < :" + multiBookingMaxAllowedTag + " AND \n";

			multiBookingsRulesCount++;
		}

		String getSlotsSql = "" +
				"  SELECT * FROM ( \n" +
				"  SELECT \n" +
				"   appointment_slots.code_char,\n" +
				"   appointment_slots.code,\n" +
				"   appointment_slots.slot_date,\n" +
				"   appointment_slots.start_datetime,\n" +
				"   appointment_slots.start_time,\n" +
				"   appointment_slots.start_time_offset,\n" +
				"   appointment_slots.duration,\n" +
				"   appointment_slots.start_datetime + INTERVAL " +
				"     appointment_slots.start_time_offset MINUTE AS end_time,\n" +
				"   GROUP_CONCAT(appt.appointment_no SEPARATOR ',') AS ids" +
				multiBookingsRulesSelectSql +
				"  \nFROM\n" +
				"  (\n" +
				"    SELECT\n" +
				"      SUBSTRING(st.timecode, seq + 1, 1) AS code_char,\n" +
				"      sd.sdate AS slot_date,\n" +
				"      CONCAT(sd.sdate, ' ', SEC_TO_TIME(ROUND( \n " +
				"        (24 * 60 * 60) * seq / LENGTH(st.timecode)))) as start_datetime,\n" +
				"      SEC_TO_TIME(ROUND((24*60*60)*seq/LENGTH(st.timecode))) as start_time,\n" +
				"      ROUND((24*60) / LENGTH(st.timecode)) as start_time_offset,\n" +
				"      stc.code,\n" +
				"      :appointmentDuration AS duration\n" +
				//"      CAST(COALESCE(stc.duration, ((24*60)/LENGTH(st.timecode))) AS integer) AS duration\n" +
				"    FROM \n" +
				"    (\n" +
				"      SELECT * FROM scheduledate\n" +
				"      WHERE sdate BETWEEN :startDate AND :endDateTime \n" +
				"      AND provider_no = :providerNo\n" +
				"      AND status = 'A'\n" +
				"      AND available = :available\n" +
				"    ) as sd\n" +
				"    CROSS JOIN (SELECT * from seq_1_to_299) as num\n" +
				"    JOIN scheduletemplate st ON (st.name = sd.hour \n " +
				"      AND st.provider_no IN (:providerNo, :publicCode))\n" +
				"    LEFT JOIN scheduletemplatecode stc ON BINARY stc.code = \n " +
				"      SUBSTRING(st.timecode, seq + 1, 1)\n" +
				"    WHERE stc.code IN (:appointmentTypes)\n" +
				"    AND CONCAT(sd.sdate, ' ', SEC_TO_TIME(ROUND( \n " +
				"      (24 * 60 * 60) * seq / LENGTH(st.timecode)))) \n " +
				"      BETWEEN :startDate AND (:endDate + INTERVAL 1 DAY)\n" +
				"    AND seq < LENGTH(st.timecode)\n" +
				"  ) AS appointment_slots\n" +
				"\n" +

				// Join appointments onto slots to exclude any slots that are already taken
				"  LEFT JOIN appointment appt ON\n" +
				"    appt.appointment_date = appointment_slots.slot_date\n" +
				"    AND appt.status != 'C' \n" +
				"    AND appt.provider_no = :providerNo \n" +
				"    AND appt.start_time < (appointment_slots.start_time + \n" +
				"      INTERVAL start_time_offset MINUTE) \n" +
				"    AND SEC_TO_TIME(FLOOR(TIME_TO_SEC(appt.end_time) / 60 ) * 60 + 60) > \n " +
				"      appointment_slots.start_time\n" +

				// Join appointments for the specified demographic in order to apply
				// X bookings in X time rule
				multiBookingsRulesJoinSql;

		getSlotsSql += "" +
				"\n" +
				"  WHERE appt.appointment_no is null\n" +
				"  AND appointment_slots.start_datetime >= :startDateTime \n" +
				"  AND appointment_slots.start_datetime >= :blackoutTime \n" +
				"  AND appointment_slots.start_datetime < :cutoffTime \n" +
				"  GROUP BY 1,2,3,4,5,6,7,8\n" +
				"  ORDER BY appointment_slots.start_datetime\n" +
				"\n" +

				" ) AS appointment_slots_multi_book_applied " +
						"WHERE " + multiBookingsRulesWhereSql + " TRUE";

		String addStartEndSlotSQL = "" +
				"SELECT \n" +
				"slots.code_char, \n" +
				"slots.code, \n" +
				"slots.slot_date, \n" +
				"slots.start_datetime, \n" +
				"slots.duration, \n" +
				"slots.start_time, \n" +
				"slots.end_time, \n" +
				"start_slot_filter.start_datetime IS NULL AS is_start_slot, \n" +
				"end_slot_filter.start_datetime IS NULL AS is_end_slot \n" +
				"FROM \n" +
				"(\n" +
					getSlotsSql +
				") AS slots \n" +

				// self join to get first slot in a series of slots
				"LEFT JOIN \n" +
				"(\n" +
					getSlotsSql +
				") AS start_slot_filter " +
				"  ON start_slot_filter.slot_date = slots.slot_date " +
				"  AND start_slot_filter.start_time = slots.start_time - " +
				"  INTERVAL slots.start_time_offset MINUTE\n" +
				"\n" +

				// self join to get last slot in a series of slots
				"LEFT JOIN \n" +
				"(\n" +
					getSlotsSql +
				") AS end_slot_filter " +
				"  ON end_slot_filter.slot_date = slots.slot_date " +
				"  AND end_slot_filter.start_time = slots.start_time + " +
				"  INTERVAL slots.start_time_offset MINUTE\n" +
				"GROUP BY 1,2,3,4,5,6,7,8,9 " +
				"\n";

		String addSlotFitsSQL = "" +
				"SELECT \n" +
				"CAST(possible_slots.start_datetime AS DATE) AS date, \n" +
				"possible_slots.start_time, \n" +
				"possible_slots.duration, \n" +
				"possible_slots.code, \n" +
				"possible_slots.start_datetime, \n" +
				"possible_slots.is_start_slot, \n" +
				"possible_slots.is_end_slot, \n" +
				"possible_slots.start_datetime + " +
				"  INTERVAL possible_slots.duration MINUTE <= MIN(end_slots.end_time) AS slot_fits \n" +
				"FROM \n" +
				"(\n" +
					addStartEndSlotSQL +
				") AS possible_slots " +
				"\n" +

				// self join to get distance from nearest end slot in the future
				"LEFT JOIN \n" +
				"(\n" +
					addStartEndSlotSQL +
				") AS end_slots " +
				"  ON end_slots.is_end_slot " +
				"  AND end_slots.slot_date = possible_slots.slot_date " +
				"  AND end_slots.start_time >= possible_slots.start_time " +
				"GROUP BY 1,2,3,4,5,6 " +
				"ORDER BY start_datetime ASC ";

		String availableSlots = "" +
				"SELECT * FROM \n" +
				"(\n" +
					addSlotFitsSQL +
				"\n) AS slots_with_end_slots \n " +
				"WHERE \n" +
				" slots_with_end_slots.slot_fits\n";

		// This logic limits results to slots with start times that are evenly divisible by the
		// appointment duration. If we end up using it, this needs to be tweeked to handle
		// when the start of end of a range isn't evenly divisible.
//				"( \n" +
//				"  (\n" +
//				"    FLOOR\n" +
//				"    (\n" +
//				"      TIME_TO_SEC(slots_with_end_slots.start_time) / (slots_with_end_slots.duration * 60)\n" +
//				"    ) * slots_with_end_slots.duration * 60\n" +
//				"  ) = TIME_TO_SEC(slots_with_end_slots.start_time) \n" +
//				"\n" +
//				"  OR slots_with_end_slots.is_start_slot \n" +
//				") AND slots_with_end_slots.slot_fits\n";


		logger.info("Query Start: " + LocalDateTime.now().toString());
		Query query = entityManager.createNativeQuery(availableSlots);
		query.setParameter("startDateTime", java.sql.Timestamp.valueOf(startDateTime), TemporalType.TIMESTAMP);
		query.setParameter("endDateTime", java.sql.Timestamp.valueOf(endDateTime), TemporalType.TIMESTAMP);
		query.setParameter("startDate", java.sql.Date.valueOf(startDate), TemporalType.DATE);
		query.setParameter("endDate", java.sql.Date.valueOf(endDate), TemporalType.DATE);
		query.setParameter("available", 1);
		query.setParameter("providerNo", providerNo);
		query.setParameter("appointmentTypes", appointmentTypeList);
		query.setParameter("appointmentDuration", appointmentDuration);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		query.setParameter("blackoutTime", java.sql.Timestamp.valueOf(
				bookingRules.getBlackoutRule().getBlackoutTime()),
				TemporalType.TIMESTAMP);
		query.setParameter("cutoffTime",
				java.sql.Timestamp.valueOf(bookingRules.getCutoffRule().getCutoffTime()),
				TemporalType.TIMESTAMP);

		if(multiBookingsRulesCount > 0)
		{
			query.setParameter("demographicNo", demographicNo);
		}
		for (Map.Entry<String, Long> multiBookingsRuleParam : multiBookingsRuleParams.entrySet())
		{
			query.setParameter(multiBookingsRuleParam.getKey(),
					multiBookingsRuleParam.getValue());
		}
		for (Map.Entry<String, Integer> multiBookingsRuleWhereParam : multiBookingJoinTags.entrySet())
		{
			query.setParameter(multiBookingsRuleWhereParam.getKey(),
					multiBookingsRuleWhereParam.getValue());
		}

		List<Object[]> results = query.getResultList();

		return results.stream().map((result) -> new ScheduleSlotDto(
				((java.sql.Date) result[0]).toLocalDate().toString(),
				((java.sql.Time) result[1]).toLocalTime().toString(),
				(int) result[2]
		)).collect(Collectors.toList());
	}

	// TODO: Temporary for backwards compatibility. Remove once released to all Juno instances
	@NativeSql({"scheduledate", "scheduletemplate", "scheduletemplate", "scheduletemplatecode"})
	public ProviderScheduleTransfer getValidProviderScheduleSlots(
			String providerNo, LocalDate startDate, LocalDate endDate,
			List<ScheduleCodeDurationTransfer> scheduleCodeDurationTransfer,
			String demographicNo, List<MultipleBookingsRule> multipleBookingsRules,
			BlackoutRule blackoutRule, CutoffRule cutoffRule)
	{
		List<String> appointmentTypeList = ScheduleCodeDurationTransfer.getAllTemplateCodes(
				scheduleCodeDurationTransfer);
		int appointmentDuration = scheduleCodeDurationTransfer.get(0).getDurationMinutes();

		LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
		LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

		if (startDateTime.isBefore(LocalDateTime.now()))
		{
			startDateTime = LocalDateTime.now();
		}

		int multiBookingsRulesCount = 0;
		HashMap<String, Long> multiBookingsRuleParams = new HashMap<>();
		HashMap<String, Integer> multiBookingJoinTags = new HashMap<>();
		String multiBookingsRulesSelectSql = "";
		String multiBookingsRulesJoinSql = "";
		String multiBookingsRulesWhereSql = "";
		for (MultipleBookingsRule multipleBookingsRule : multipleBookingsRules)
		{
			String joinTag = "multi_bookings_appt_" + Integer.toString(multiBookingsRulesCount);

			multiBookingsRulesSelectSql += ", \n" +
					"  SUM(Case \n" +
					"    WHEN " + joinTag + ".appointment_no IS NOT NULL THEN 1 \n" +
					"    ELSE 0\n" +
					"  END) AS count_" + joinTag + " ";

			String durationTag = "multi_bookings_rule_" +
					Integer.toString(multiBookingsRulesCount) + "_duration";
			multiBookingsRuleParams.put(durationTag, multipleBookingsRule.getDurationOffsetDays());

			multiBookingsRulesJoinSql += "" +
					"  LEFT JOIN appointment AS " + joinTag + " ON\n" +
					"    " + joinTag + ".demographic_no = :demographicNo \n" +
					"    AND " + joinTag + ".status != 'C' \n" +
					"    AND " + joinTag + ".appointment_date <= " +
					"      (appointment_slots.slot_date + \n" +
					"      INTERVAL :" + durationTag + " DAY) \n" +
					"    AND " + joinTag + ".appointment_date >= " +
					"      (appointment_slots.slot_date - \n" +
					"      INTERVAL :" + durationTag + " DAY) \n";

			String multiBookingMaxAllowedTag = joinTag + "_max_allowed";
			multiBookingJoinTags.put(multiBookingMaxAllowedTag,
					multipleBookingsRule.getBookingAmount());
			multiBookingsRulesWhereSql += "" +
					"  count_" + joinTag + " < :" + multiBookingMaxAllowedTag + " AND \n";

			multiBookingsRulesCount++;
		}

		String getSlotsSql = "" +
				"  SELECT * FROM ( \n" +
				"  SELECT \n" +
				"   appointment_slots.code_char,\n" +
				"   appointment_slots.code,\n" +
				"   appointment_slots.slot_date,\n" +
				"   appointment_slots.start_datetime,\n" +
				"   appointment_slots.start_time,\n" +
				"   appointment_slots.start_time_offset,\n" +
				"   appointment_slots.duration,\n" +
				"   appointment_slots.start_datetime + INTERVAL " +
				"     appointment_slots.start_time_offset MINUTE AS end_time,\n" +
				"   GROUP_CONCAT(appt.appointment_no SEPARATOR ',') AS ids" +
				multiBookingsRulesSelectSql +
				"  \nFROM\n" +
				"  (\n" +
				"    SELECT\n" +
				"      SUBSTRING(st.timecode, seq + 1, 1) AS code_char,\n" +
				"      sd.sdate AS slot_date,\n" +
				"      CONCAT(sd.sdate, ' ', SEC_TO_TIME(ROUND( \n " +
				"        (24 * 60 * 60) * seq / LENGTH(st.timecode)))) as start_datetime,\n" +
				"      SEC_TO_TIME(ROUND((24*60*60)*seq/LENGTH(st.timecode))) as start_time,\n" +
				"      ROUND((24*60) / LENGTH(st.timecode)) as start_time_offset,\n" +
				"      stc.code,\n" +
				"      :appointmentDuration AS duration\n" +
				//"      CAST(COALESCE(stc.duration, ((24*60)/LENGTH(st.timecode))) AS integer) AS duration\n" +
				"    FROM \n" +
				"    (\n" +
				"      SELECT * FROM scheduledate\n" +
				"      WHERE sdate BETWEEN :startDate AND :endDateTime \n" +
				"      AND provider_no = :providerNo\n" +
				"      AND status = 'A'\n" +
				"      AND available = :available\n" +
				"    ) as sd\n" +
				"    CROSS JOIN (SELECT * from seq_1_to_299) as num\n" +
				"    JOIN scheduletemplate st ON (st.name = sd.hour \n " +
				"      AND st.provider_no IN (:providerNo, :publicCode))\n" +
				"    LEFT JOIN scheduletemplatecode stc ON BINARY stc.code = \n " +
				"      SUBSTRING(st.timecode, seq + 1, 1)\n" +
				"    WHERE stc.code IN (:appointmentTypes)\n" +
				"    AND CONCAT(sd.sdate, ' ', SEC_TO_TIME(ROUND( \n " +
				"      (24 * 60 * 60) * seq / LENGTH(st.timecode)))) \n " +
				"      BETWEEN :startDate AND (:endDate + INTERVAL 1 DAY)\n" +
				"    AND seq < LENGTH(st.timecode)\n" +
				"  ) AS appointment_slots\n" +
				"\n" +

				// Join appointments onto slots to exclude any slots that are already taken
				"  LEFT JOIN appointment appt ON\n" +
				"    appt.appointment_date = appointment_slots.slot_date\n" +
				"    AND appt.status != 'C' \n" +
				"    AND appt.provider_no = :providerNo \n" +
				"    AND appt.start_time < (appointment_slots.start_time + \n" +
				"      INTERVAL start_time_offset MINUTE) \n" +
				"    AND SEC_TO_TIME(FLOOR(TIME_TO_SEC(appt.end_time) / 60 ) * 60 + 60) > \n " +
				"      appointment_slots.start_time\n" +

				// Join appointments for the specified demographic in order to apply
				// X bookings in X time rule
				multiBookingsRulesJoinSql;

		getSlotsSql += "" +
				"\n" +
				"  WHERE appt.appointment_no is null\n" +
				"  AND appointment_slots.start_datetime >= :startDateTime \n" +
				"  AND appointment_slots.start_datetime >= :blackoutTime \n" +
				"  AND appointment_slots.start_datetime < :cutoffTime \n" +
				"  GROUP BY 1,2,3,4,5,6,7,8\n" +
				"  ORDER BY appointment_slots.start_datetime\n" +
				"\n" +

				" ) AS appointment_slots_multi_book_applied " +
				"WHERE " + multiBookingsRulesWhereSql + " TRUE";

		String addStartEndSlotSQL = "" +
				"SELECT \n" +
				"slots.code_char, \n" +
				"slots.code, \n" +
				"slots.slot_date, \n" +
				"slots.start_datetime, \n" +
				"slots.duration, \n" +
				"slots.start_time, \n" +
				"slots.end_time, \n" +
				"start_slot_filter.start_datetime IS NULL AS is_start_slot, \n" +
				"end_slot_filter.start_datetime IS NULL AS is_end_slot \n" +
				"FROM \n" +
				"(\n" +
				getSlotsSql +
				") AS slots \n" +

				// self join to get first slot in a series of slots
				"LEFT JOIN \n" +
				"(\n" +
				getSlotsSql +
				") AS start_slot_filter " +
				"  ON start_slot_filter.slot_date = slots.slot_date " +
				"  AND start_slot_filter.start_time = slots.start_time - " +
				"  INTERVAL slots.start_time_offset MINUTE\n" +
				"\n" +

				// self join to get last slot in a series of slots
				"LEFT JOIN \n" +
				"(\n" +
				getSlotsSql +
				") AS end_slot_filter " +
				"  ON end_slot_filter.slot_date = slots.slot_date " +
				"  AND end_slot_filter.start_time = slots.start_time + " +
				"  INTERVAL slots.start_time_offset MINUTE\n" +
				"GROUP BY 1,2,3,4,5,6,7,8,9 " +
				"\n";

		String addSlotFitsSQL = "" +
				"SELECT \n" +
				"possible_slots.code, \n" +
				"possible_slots.start_datetime, \n" +
				"possible_slots.duration, \n" +
				"possible_slots.start_time, \n" +
				"possible_slots.is_start_slot, \n" +
				"possible_slots.is_end_slot, \n" +
				"possible_slots.start_datetime + " +
				"  INTERVAL possible_slots.duration MINUTE <= MIN(end_slots.end_time) AS slot_fits \n" +
				"FROM \n" +
				"(\n" +
				addStartEndSlotSQL +
				") AS possible_slots " +
				"\n" +

				// self join to get distance from nearest end slot in the future
				"LEFT JOIN \n" +
				"(\n" +
				addStartEndSlotSQL +
				") AS end_slots " +
				"  ON end_slots.is_end_slot " +
				"  AND end_slots.slot_date = possible_slots.slot_date " +
				"  AND end_slots.start_time >= possible_slots.start_time " +
				"GROUP BY 1,2,3,4,5,6 " +
				"ORDER BY start_datetime ASC ";

		String availableSlots = "" +
				"SELECT * FROM \n" +
				"(\n" +
				addSlotFitsSQL +
				"\n) AS slots_with_end_slots \n " +
				"WHERE \n" +
				" slots_with_end_slots.slot_fits\n";

		// This logic limits results to slots with start times that are evenly divisible by the
		// appointment duration. If we end up using it, this needs to be tweeked to handle
		// when the start of end of a range isn't evenly divisible.
//				"( \n" +
//				"  (\n" +
//				"    FLOOR\n" +
//				"    (\n" +
//				"      TIME_TO_SEC(slots_with_end_slots.start_time) / (slots_with_end_slots.duration * 60)\n" +
//				"    ) * slots_with_end_slots.duration * 60\n" +
//				"  ) = TIME_TO_SEC(slots_with_end_slots.start_time) \n" +
//				"\n" +
//				"  OR slots_with_end_slots.is_start_slot \n" +
//				") AND slots_with_end_slots.slot_fits\n";



		logger.info("Query Start: " + LocalDateTime.now().toString());
		Query query = entityManager.createNativeQuery(availableSlots);
		query.setParameter("startDateTime", java.sql.Timestamp.valueOf(startDateTime), TemporalType.TIMESTAMP);
		query.setParameter("endDateTime", java.sql.Timestamp.valueOf(endDateTime), TemporalType.TIMESTAMP);
		query.setParameter("startDate", java.sql.Date.valueOf(startDate), TemporalType.DATE);
		query.setParameter("endDate", java.sql.Date.valueOf(endDate), TemporalType.DATE);
		query.setParameter("available", 1);
		query.setParameter("providerNo", providerNo);
		query.setParameter("appointmentTypes", appointmentTypeList);
		query.setParameter("appointmentDuration", appointmentDuration);
		query.setParameter("publicCode", DODGY_FAKE_PROVIDER_NO_USED_TO_HOLD_PUBLIC_TEMPLATES);
		query.setParameter("blackoutTime", java.sql.Timestamp.valueOf(
				blackoutRule.getBlackoutTime()),
				TemporalType.TIMESTAMP);
		query.setParameter("cutoffTime",
				java.sql.Timestamp.valueOf(cutoffRule.getCutoffTime()),
				TemporalType.TIMESTAMP);

		if(multiBookingsRulesCount > 0)
		{
			query.setParameter("demographicNo", demographicNo);
		}
		for (Map.Entry<String, Long> multiBookingsRuleParam : multiBookingsRuleParams.entrySet())
		{
			query.setParameter(multiBookingsRuleParam.getKey(),
					multiBookingsRuleParam.getValue());
		}
		for (Map.Entry<String, Integer> multiBookingsRuleWhereParam : multiBookingJoinTags.entrySet())
		{
			query.setParameter(multiBookingsRuleWhereParam.getKey(),
					multiBookingsRuleWhereParam.getValue());
		}

		List<Object[]> results = query.getResultList();
		logger.info("Query End: " + LocalDateTime.now().toString());

		HashMap<String, List<DayTimeSlots>> providerSchedule = new HashMap<>();
		ProviderScheduleTransfer providerScheduleTransfer = new ProviderScheduleTransfer();
		List<DayTimeSlots> dayTimeSlots;
		HashMap<String, Boolean> scheduleArrMap = new HashMap<>();
		for (Object[] result : results)
		{
			char templateCode = (char) result[0];
			java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf((String) result[1]);
			Long slotDuration = new Long((int) result[2]);

			ScheduleSearchResult appointmentSlot = new ScheduleSearchResult(
					timestamp,
					templateCode,
					slotDuration,
					providerNo
			);

			String scheduleDate = appointmentSlot.dateTime.toLocalDate().toString();
			if (!scheduleArrMap.containsKey(scheduleDate))
			{
				scheduleArrMap.put(scheduleDate, true);
				dayTimeSlots = new ArrayList<>();
			} else
			{
				dayTimeSlots = providerSchedule.get(scheduleDate);
			}
			dayTimeSlots.add(new DayTimeSlots(
					appointmentSlot.dateTime.toString(),
					Integer.toString(appointmentDuration)));
			providerSchedule.put(scheduleDate, dayTimeSlots);
		}
		providerScheduleTransfer.setProviderScheduleResponse(providerSchedule);

		return providerScheduleTransfer;
	}
}
