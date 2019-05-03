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

package org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules;

import org.json.simple.JSONObject;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.schedule.model.ScheduleSearchResult;
import org.oscarehr.util.SpringUtils;
import oscar.util.ConversionUtils;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Patients can only have {bookingAmount} bookings within {timePeriodAmount} calendar {timePeriod(s)}
 */
public class MultipleBookingRule extends BookingRule
{
    private Integer bookingAmount;
    private Integer timePeriodAmount;
    private ChronoUnit timePeriod;

    /*
        These two fields are used to cache existing patient appointment counts when calculating multi-booking rules.
        For any pair of potential appointment dates, the number of existing encounters will be the same as long as
        each potential date is within the same calendar timePeriod unit (defined by .truncateTo(timePeriod)).

        For example: If the timePeriod is weeks, the number of existing appointments relevant to the multi-booking rule
        will not change as long as the queries are within the same calendar week.

        As a result cache the result and reuse it for any additional searches within the same window.
     */

    private Integer appointmentCountCache;                      // lazy instantiation of this variable
    private LocalDateTime cacheWindow;                          // lazy instatiation of this variable

    private static final OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);

    MultipleBookingRule (String jsonType, Integer bookingAmount, Integer timePeriodAmount, ChronoUnit timePeriod)
    {
        super(BookingRuleType.BOOKING_MULTI, jsonType);
        this.bookingAmount = bookingAmount;
        this.timePeriodAmount = timePeriodAmount;
        this.timePeriod = timePeriod;
    }

    @Override
    public Boolean isViolated(Appointment appointment)
    {
        LocalDateTime startDate = ConversionUtils.toLocalDateTime(appointment.getStartTimeAsFullDate()).truncatedTo(timePeriod);
        LocalDateTime endDate = startDate.plus(timePeriodAmount, timePeriod);
        List<Appointment> patientAppointments = appointmentDao.findByDateRangeAndDemographic(startDate, endDate, appointment.getDemographicNo());

        return patientAppointments.size() >= bookingAmount;
    }

    @Override
    public Boolean isViolated(ScheduleSearchResult result)
    {
        // calculate result start time
        // if it's in the window, return the result


        // else, recalculate result store it then return the cached value
        //return appointmentCountCache >= bookingAmount;


        return false;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        json.put("bookings", this.bookingAmount);
        json.put("period_of_time", this.timePeriodAmount);
        return json;
    }
}