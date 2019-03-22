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

package org.oscarehr.ws.external.soap.v1.transfer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.oscarehr.common.model.Appointment;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.MhaBookingRules;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MhaBookingRulesTest
{
    private MhaBookingRules bookingRules;

    private static final String DAY_RULES = "day_rules";
    private static final String WEEK_RULES = "week_rules";
    private static final String MONTH_RULES = "month_rules";

    @Before
    public void setUp()
    {
        bookingRules = Mockito.spy(new MhaBookingRules());
    }

    @Test
    public void applyMultiBookRulesTest()
    {
        Map<String, Integer> dayRule = new HashMap<>();
        dayRule.put("bookings", 2);

        List<Map<String, Integer>> dayRules = new ArrayList<>();
        dayRules.add(dayRule);

        LocalDateTime bookingSlot = LocalDateTime.of(2019, 3, 25, 7, 15);
        String multiRuleType = "days";

        Mockito.when(bookingRules.appointmentCountInTimePeriod(multiRuleType, dayRule, bookingSlot)).thenReturn(4);
        boolean dayRuleIsBroken = bookingRules.applyMultiBookRules(dayRules, multiRuleType, bookingSlot);
        // If appointmentCountInTimePeriod returns a higher number than specified in the dayRule bookings
        assertTrue(dayRuleIsBroken);

        Mockito.when(bookingRules.appointmentCountInTimePeriod(multiRuleType, dayRule, bookingSlot)).thenReturn(1);
        dayRuleIsBroken = bookingRules.applyMultiBookRules(dayRules, multiRuleType, bookingSlot);
        // If appointmentCountInTimePeriod returns a lower number than specified in the dayRule bookings
        assertFalse(dayRuleIsBroken);
    }

    @Test
    public void applyAppointmentCountInTimePeriodTest()
    {
        List<Appointment> appointments = new ArrayList<>();

        appointments.add(createMockAppointment("2019-03-25", "06:10:00"));
        appointments.add(createMockAppointment("2019-03-26", "07:00:00"));
        appointments.add(createMockAppointment("2019-03-27", "07:30:00"));
        appointments.add(createMockAppointment("2019-03-29", "06:30:00"));
        bookingRules.setPatientAppointments(appointments);

        LocalDateTime bookingSlot = LocalDateTime.of(2019, 3, 26, 9, 10);

        Map<String, Integer> weekRule = new HashMap<>();
        weekRule.put("period_of_time", 2);
        int appointmentCountInPeriod = bookingRules.appointmentCountInTimePeriod(WEEK_RULES, weekRule, bookingSlot);

        assertEquals(4, appointmentCountInPeriod);

        Map<String, Integer> dayRule = new HashMap<>();
        dayRule.put("period_of_time", 1);
        appointmentCountInPeriod = bookingRules.appointmentCountInTimePeriod(DAY_RULES, dayRule, bookingSlot);

        assertEquals(1, appointmentCountInPeriod);
    }

    private Appointment createMockAppointment(String dateString, String timeString)
    {
        Appointment appointment = Mockito.mock(Appointment.class);

        Date appointmentDate = Date.valueOf(dateString);
        Time startTime = Time.valueOf(timeString);

        Mockito.when(appointment.getAppointmentDate()).thenReturn(appointmentDate);
        Mockito.when(appointment.getStartTime()).thenReturn(startTime);

        return appointment;
    }
}
