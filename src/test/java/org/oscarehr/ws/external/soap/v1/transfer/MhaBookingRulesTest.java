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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.MhaBookingRules;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MhaBookingRulesTest
{
    private String bookingRulesJsonString =
            "{" +
                "\"blackout\": {" +
                    "\"period_type\": \"blackout_now_until_day\", " +
                    "\"period_time\": 1" +
                "}," +
                "\"cutoff\": {" +
                    "\"days\": 90" +
                "}," +
                "\"multi\": {" +
                    "\"day_rules\": [{\"bookings\": 1, \"period_of_time\": 1}, {\"bookings\": 2, \"period_of_time\": 3}]," +
                    "\"week_rules\": [{\"bookings\": 3, \"period_of_time\": 1}]," +
                    "\"month_rules\": [{\"bookings\": 5, \"period_of_time\": 1}]" +
                "}" +
            "}";

    @Test
    public void mhaBookingRulesTest()
    {
        Map<String, Object> bookingRulesObj = getBookingRules();
        MhaBookingRules bookingRules = new MhaBookingRules(bookingRulesObj);

        String startDateStr = "2019-03-25 06:10";
        List<Appointment> patientAppointments = createTestAppointments(startDateStr, "days", 1, 4);

        bookingRules.setPatientAppointments(patientAppointments);

        LocalDateTime slot = LocalDateTime.of(2019, Month.MARCH, 29, 6, 0);

        boolean cutOffRuleIsBroken = bookingRules.cutOffRuleIsBroken(slot);
        boolean blackOutRuleIsBroken = bookingRules.blackOutRuleIsBroken(slot);
        boolean multiRuleIsBroken = bookingRules.multiBookRuleIsBroken(slot);

        assertFalse(cutOffRuleIsBroken);
        assertFalse(blackOutRuleIsBroken);
        assertTrue(multiRuleIsBroken);
    }

    private List<Appointment> createTestAppointments(String startDateTimeStr, String intervalType, int appointmentInterval, int numOfAppointments)
    {
        List<Appointment> patientAppointments = new ArrayList<>();

        try
        {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date startDate = dateFormat.parse(startDateTimeStr);

            for (int i = 0; i < numOfAppointments; i++)
            {
                Appointment appointment = new Appointment();
                appointment.setStartTime(startDate);
                appointment.setAppointmentDate(startDate);

                patientAppointments.add(appointment);

                switch (intervalType)
                {
                    case "minutes":
                        startDate = DateUtils.addMinutes(startDate, appointmentInterval);
                        break;

                    case "hours":
                        startDate = DateUtils.addHours(startDate, appointmentInterval);
                        break;

                    case "days":
                        startDate = DateUtils.addDays(startDate, appointmentInterval);
                        break;
                }
            }
        }
        catch (Exception e)
        {
            MiscUtils.getLogger().error("Exception " + e);
        }

        return patientAppointments;
    }

    private Map<String, Object> getBookingRules()
    {
        try
        {
            return new ObjectMapper().readValue(bookingRulesJsonString, new TypeReference<Map<String, Object>>(){});
        }
        catch(Exception e)
        {
            MiscUtils.getLogger().error("Exception: " + e);
        }

        return new HashMap<>();
    }
}
