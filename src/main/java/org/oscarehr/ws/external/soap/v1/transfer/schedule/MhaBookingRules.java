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
    private Map<String, Object> bookingRules;

    private LocalDateTime blackOutEndDateTime;
    private LocalDateTime cutOffEndDateTime;
    private Map<String, List<Map<String, Integer>>> multiBookingRules;

    private List<Integer> multiBookingRuleDays;
    private List<Appointment> patientAppointments = new ArrayList<>();

    private Map<LocalDate, Boolean> invalidDates = new HashMap<>();

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

        Map<String, Object> blackOutMap = (Map<String, Object>) this.bookingRules.get("blackout");

        String blackOutPeriodType = (String) blackOutMap.get("period_type");

        blackOutHours = (Integer) blackOutMap.get("period_time");
        blackOutHours = blackOutPeriodType.equals("blackout_now_until_day") ? blackOutHours * 24 : blackOutHours;

        this.blackOutEndDateTime = blackOutEndDateTime.plusHours(blackOutHours.longValue());
    }

    private void setCutOffEndDateTime()
    {
        Map<String, Object> cutOffMap = (Map<String, Object>) this.bookingRules.get("cutoff");
        LocalDateTime currentDateTime = LocalDateTime.now();

        Integer cutOffDays = (Integer) cutOffMap.get("days");

        this.cutOffEndDateTime = currentDateTime.plusDays(cutOffDays);
    }

    public void setPatientAppointments(List<Appointment> patientAppointments)
    {
        this.patientAppointments = patientAppointments;
    }

    private void setMultiBookingRules()
    {
        this.multiBookingRules = (Map<String, List<Map<String, Integer>>>) this.bookingRules.get("multi");
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
        if (blackOutRuleIsBroken(slot)) return false;
        if (cutOffRuleIsBroken(slot)) return false;

        if (patientAppointments.size() > 0)
            return !multiBookRuleIsBroken(slot);

        return true;
    }

    // Patients can only book x number of appointments in x period of time
    private boolean multiBookRuleIsBroken(LocalDateTime slot)
    {
        List<Map<String, Integer>> dayRules = multiBookingRules.get("day_rules");
        List<Map<String, Integer>> weekRules = multiBookingRules.get("week_rules");
        List<Map<String, Integer>> monthRules = multiBookingRules.get("month_rules");

        for (Map<String, Integer> dayRule : dayRules)
        {
            Integer appointmentCount = appointmentCountInTimePeriod("day_rules", dayRule, slot);

            if (appointmentCount >= dayRule.get("bookings"))
            {
                this.invalidDates.put(slot.toLocalDate(), true);
                return true;
            }
        }

        for (Map<String, Integer> weekRule : weekRules)
        {
            Integer appointmentCount = appointmentCountInTimePeriod("week_rules", weekRule, slot);

            if (appointmentCount >= weekRule.get("bookings"))
            {
                this.invalidDates.put(slot.toLocalDate(), true);
                return true;
            }
        }

        for (Map<String, Integer> monthRule : monthRules)
        {
            Integer appointmentCount = appointmentCountInTimePeriod("month_rules", monthRule, slot);

            if (appointmentCount >= monthRule.get("bookings"))
            {
                this.invalidDates.put(slot.toLocalDate(), true);
                return true;
            }
        }

        return false;
    }

    // Patients can only book within x number of hours/days from the current time
    private boolean blackOutRuleIsBroken(LocalDateTime slot)
    {
        return slot.isBefore(this.blackOutEndDateTime);
    }

    // Patients can only book up until x days in the future
    private boolean cutOffRuleIsBroken(LocalDateTime slot)
    {
        if (slot.isAfter(this.cutOffEndDateTime))
        {
            this.invalidDates.put(slot.toLocalDate(), true);
            return true;
        }

        return false;
    }

    private Integer appointmentCountInTimePeriod(String ruleType, Map<String, Integer> multiBookRule, LocalDateTime slot)
    {
        List<Appointment> periodAppointments = new ArrayList<>();
        LocalDate slotDate = slot.toLocalDate();

        for (Appointment appointment : this.patientAppointments)
        {
            LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate().toString());
            LocalTime appointmentTime = LocalTime.parse(appointment.getStartTime().toString());

            LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);

            int ruleTimePeriod = multiBookRule.get("period_of_time") - 1;
            LocalDateTime rulePeriodStart = LocalDateTime.of(slotDate.minusDays(ruleTimePeriod), LocalTime.MIN);
            LocalDateTime rulePeriodEnd = LocalDateTime.of(slotDate.plusDays(ruleTimePeriod), LocalTime.MAX);

            if (ruleType.equals("week_rules"))
            {
                rulePeriodStart = LocalDateTime.of(slotDate.minusWeeks(ruleTimePeriod).with(WeekFields.of(Locale.US).dayOfWeek(), 1L), LocalTime.MIN);
                rulePeriodEnd = LocalDateTime.of(slotDate.plusWeeks(ruleTimePeriod).with(WeekFields.of(Locale.US).dayOfWeek(), 7L), LocalTime.MAX);
            }
            else if (ruleType.equals("month_rules"))
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
                if (period_type.equals("day_rules"))
                {
                    multiBookingRuleDays.add(rule.get("period_of_time"));
                }
                else
                {
                    LocalDate slotDateMinusRule = firstSlotDate;

                    if (period_type.equals("week_rules"))
                        slotDateMinusRule = firstSlotDate.minusWeeks(rule.get("period_of_time"));
                    if (period_type.equals("month_rules"))
                        slotDateMinusRule = firstSlotDate.minusMonths(rule.get("period_of_time"));

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
