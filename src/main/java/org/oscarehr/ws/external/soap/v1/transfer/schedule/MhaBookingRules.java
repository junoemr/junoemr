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

package org.oscarehr.ws.external.soap.v1.transfer.schedule;

import org.oscarehr.common.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MhaBookingRules
{
    private static final String BOOKING_RULES_BLACKOUT = "blackout";
    private static final String BOOKING_RULES_CUTOFF = "cutoff";
    private static final String BOOKING_RULES_MULTI = "multi";

    private static final String BLACKOUT_RULE_DAYS = "blackout_now_until_day";
    private static final String CUTOFF_RULE_DAYS = "days";

    private static final String RULE_PERIOD_TYPE = "period_type";
    private static final String RULE_PERIOD_TIME = "period_time";
    private static final String RULE_PERIOD_OF_TIME = "period_of_time";
    private static final String RULE_BOOKINGS = "bookings";

    private static final String DAY_RULES = "day_rules";
    private static final String WEEK_RULES = "week_rules";
    private static final String MONTH_RULES = "month_rules";

    private Map<String, Object> bookingRules;

    private LocalDateTime blackOutEndDateTime;
    private LocalDateTime cutOffEndDateTime;
    private Map<String, List<Map<String, Integer>>> multiBookingRules;

    private List<Integer> multiBookingRuleDays;
    private List<Appointment> patientAppointments = new ArrayList<>();

    private Map<LocalDate, Boolean> invalidDates = new HashMap<>();

    public MhaBookingRules()
    {

    }

    public MhaBookingRules(Map<String, Object> bookingRules)
    {
        this.bookingRules = bookingRules;

        this.setBlackOutEndDateTime();
        this.setCutOffEndDateTime();
        this.setMultiBookingRules();
    }

    private void setBlackOutEndDateTime()
    {
        Integer blackOutHours;
        LocalDateTime blackOutEndDateTime = LocalDateTime.now();

        Map<String, Object> blackOutMap = (Map<String, Object>) this.bookingRules.get(BOOKING_RULES_BLACKOUT);

        String blackOutPeriodType = (String) blackOutMap.get(RULE_PERIOD_TYPE);

        blackOutHours = (Integer) blackOutMap.get(RULE_PERIOD_TIME);
        blackOutHours = blackOutPeriodType.equals(BLACKOUT_RULE_DAYS) ? blackOutHours * 24 : blackOutHours;

        this.blackOutEndDateTime = blackOutEndDateTime.plusHours(blackOutHours.longValue());
    }

    private void setCutOffEndDateTime()
    {
        Map<String, Object> cutOffMap = (Map<String, Object>) this.bookingRules.get(BOOKING_RULES_CUTOFF);
        LocalDateTime currentDateTime = LocalDateTime.now();

        Integer cutOffDays = (Integer) cutOffMap.get(CUTOFF_RULE_DAYS);

        this.cutOffEndDateTime = currentDateTime.plusDays(cutOffDays);
    }

    public void setPatientAppointments(List<Appointment> patientAppointments)
    {
        this.patientAppointments = patientAppointments;
    }

    private void setMultiBookingRules()
    {
        this.multiBookingRules = (Map<String, List<Map<String, Integer>>>) this.bookingRules.get(BOOKING_RULES_MULTI);
    }

    public Map<String, List<Map<String, Integer>>> getMultiBookingRules()
    {
        return multiBookingRules;
    }

    public List<Integer> getMultiBookingRuleDays()
    {
        return multiBookingRuleDays;
    }

    public Map<LocalDate, Boolean> getInvalidDates()
    {
        return invalidDates;
    }

    /**
     * Class Methods
     */
    public boolean scheduleSlotIsValid(LocalDateTime slot)
    {
        if (blackOutRuleIsBroken(slot))
        {
            return false;
        }
        if (cutOffRuleIsBroken(slot))
        {
            return false;
        }

        if (patientAppointments.size() > 0)
        {
            return !multiBookRuleIsBroken(slot);
        }

        return true;
    }

    // Patients can only book x number of appointments in x period of time
    public boolean multiBookRuleIsBroken(LocalDateTime slot)
    {
        List<Map<String, Integer>> dayRules = multiBookingRules.get(DAY_RULES);
        List<Map<String, Integer>> weekRules = multiBookingRules.get(WEEK_RULES);
        List<Map<String, Integer>> monthRules = multiBookingRules.get(MONTH_RULES);

        if (applyMultiBookRules(dayRules, DAY_RULES, slot))
        {
            return true;
        }

        if (applyMultiBookRules(weekRules, WEEK_RULES, slot))
        {
            return true;
        }

        if (applyMultiBookRules(monthRules, MONTH_RULES, slot))
        {
            return true;
        }

        return false;
    }

    public boolean applyMultiBookRules(List<Map<String, Integer>> multiBookingRules, String multiRuleType, LocalDateTime slot)
    {
        for (Map<String, Integer> rule : multiBookingRules)
        {
            int appointmentCount = appointmentCountInTimePeriod(multiRuleType, rule, slot);

            if (appointmentCount >= rule.get(RULE_BOOKINGS))
            {
                this.invalidDates.put(slot.toLocalDate(), true);
                return true;
            }
        }

        return false;
    }

    // Patients can only book within x number of hours/days from the current time
    public boolean blackOutRuleIsBroken(LocalDateTime slot)
    {
        return slot.isBefore(this.blackOutEndDateTime);
    }

    // Patients can only book up until x days in the future
    public boolean cutOffRuleIsBroken(LocalDateTime slot)
    {
        if (slot.isAfter(this.cutOffEndDateTime))
        {
            this.invalidDates.put(slot.toLocalDate(), true);
            return true;
        }

        return false;
    }

    public int appointmentCountInTimePeriod(String ruleType, Map<String, Integer> multiBookRule, LocalDateTime slot)
    {
        List<Appointment> periodAppointments = new ArrayList<>();
        LocalDate slotDate = slot.toLocalDate();

        for (Appointment appointment : this.patientAppointments)
        {
            LocalDateTime appointmentDateTime;

            LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate().toString());
            LocalTime appointmentTime = LocalTime.parse(appointment.getStartTime().toString());

            appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

            int ruleTimePeriod = multiBookRule.get(RULE_PERIOD_OF_TIME) - 1;
            LocalDateTime rulePeriodStart = LocalDateTime.of(slotDate.minusDays(ruleTimePeriod), LocalTime.MIN);
            LocalDateTime rulePeriodEnd = LocalDateTime.of(slotDate.plusDays(ruleTimePeriod), LocalTime.MAX);

            if (ruleType.equals(WEEK_RULES))
            {
                rulePeriodStart = LocalDateTime.of(slotDate.minusWeeks(ruleTimePeriod).with(WeekFields.of(Locale.US).dayOfWeek(), 1L), LocalTime.MIN);
                rulePeriodEnd = LocalDateTime.of(slotDate.plusWeeks(ruleTimePeriod).with(WeekFields.of(Locale.US).dayOfWeek(), 7L), LocalTime.MAX);
            }
            else if (ruleType.equals(MONTH_RULES))
            {
                rulePeriodStart = LocalDateTime.of(slotDate.minusMonths(ruleTimePeriod).withDayOfMonth(1), LocalTime.MIN);
                rulePeriodEnd = LocalDateTime.of(slotDate.plusMonths(ruleTimePeriod).withDayOfMonth(slotDate.lengthOfMonth()), LocalTime.MAX);
            }

            if (rulePeriodStart.isBefore(appointmentDateTime) && rulePeriodEnd.isAfter(appointmentDateTime))
            {
                periodAppointments.add(appointment);
            }
        }

        return periodAppointments.size();
    }

    /**
     * Pushes every multi booking rule in a list (in days).
     * We can use this to find the largest amount of days we need to query patient appointments for
     */
    public void setMultiBookingRuleDays(LocalDate firstSlotDate)
    {
        List<Integer> multiBookingRuleDays = new ArrayList<>();

        for(Map.Entry<String, List<Map<String, Integer>>> entry : multiBookingRules.entrySet())
        {
            String period_type = entry.getKey();
            List<Map<String, Integer>> period_rules = entry.getValue();

            for (Map<String, Integer> rule : period_rules)
            {
                if (period_type.equals(DAY_RULES))
                {
                    multiBookingRuleDays.add(rule.get(RULE_PERIOD_OF_TIME));
                }
                else
                {
                    LocalDate slotDateMinusRule = firstSlotDate;

                    if (period_type.equals(WEEK_RULES))
                    {
                        slotDateMinusRule = firstSlotDate.minusWeeks(rule.get(RULE_PERIOD_OF_TIME));
                    }
                    if (period_type.equals(MONTH_RULES)) {
                        slotDateMinusRule = firstSlotDate.minusMonths(rule.get(RULE_PERIOD_OF_TIME));
                    }

                    Long daysBetween = ChronoUnit.DAYS.between(slotDateMinusRule, firstSlotDate);
                    multiBookingRuleDays.add(daysBetween.intValue());
                }
            }
        }

        multiBookingRuleDays.sort(null);
        Collections.reverse(multiBookingRuleDays);

        this.multiBookingRuleDays = multiBookingRuleDays;
    }
}
