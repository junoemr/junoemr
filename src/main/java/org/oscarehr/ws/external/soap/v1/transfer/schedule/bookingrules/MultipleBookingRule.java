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
    private Integer demographicNo;
    private Integer bookingAmount;
    private Integer timePeriodAmount;
    private ChronoUnit timePeriod;

    /*
        These two fields are used to cache existing patient appointment counts when calculating multi-booking rules.
        For any pair of potential appointment dates, the number of existing encounters will be the same as long as
        each potential date is within the same calendar timePeriod unit (defined by .truncateTo(timePeriod)).

        For example: If the timePeriod is weeks, the number of existing appointments relevant to the multi-booking rule
        will not change as long as the each of the queries are within the same calendar week.  This week is a window
        which can be cached.

        For an existing window, the existing appointment count is defined by the following boundaries:
        - Low End:  WindowStart - (timePeriodAmount * timePeriod -1)
        - High End:  WindowStart + (timePeriodAmount * timePeriod -1)
        The -1 arises because include the week of the window.  A two week period encompasses the time from the week
        before the window, to the week after the window.
     */
    private Integer appointmentCountCache;
    private LocalDateTime cacheWindow;

    private static final OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);

    MultipleBookingRule (String jsonType, Integer demographicNo, Integer bookingAmount, Integer timePeriodAmount, ChronoUnit timePeriod)
    {
        super(BookingRuleType.BOOKING_MULTI, jsonType);
        this.bookingAmount = bookingAmount;
        this.timePeriodAmount = timePeriodAmount;
        this.timePeriod = timePeriod;
        this.demographicNo = demographicNo;
        this.appointmentCountCache = null;
        this.cacheWindow = null;
    }

    @Override
    public Boolean isViolated(Appointment appointment)
    {
        LocalDateTime startDate = ConversionUtils.toLocalDateTime(appointment.getStartTimeAsFullDate()).truncatedTo(timePeriod);
        LocalDateTime endDate = startDate.plus(timePeriodAmount, timePeriod);
        List<Appointment> patientAppointments = appointmentDao.findByDateRangeAndDemographic(startDate.toLocalDate(),
                                                                                             endDate.toLocalDate(),
                                                                                             appointment.getDemographicNo());

        return patientAppointments.size() >= bookingAmount;
    }

    @Override
    public Boolean isViolated(ScheduleSearchResult result)
    {
        LocalDateTime resultWindow = ConversionUtils.truncateLocalDateTime(result.dateTime, timePeriod)
                                                    .minus(timePeriodAmount, timePeriod);

        if (this.cacheWindow == null || resultWindow != this.cacheWindow)
        {
            cacheAppointmentCounts(resultWindow);
        }

        return appointmentCountCache >= bookingAmount;
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

    /**
     * Recalculate the number of appointments within the date range specified by this rule and cache it.
     *
     * @param startOfCache Start of the time period to cache
     */
    private void cacheAppointmentCounts(LocalDateTime startOfCache)
    {
        LocalDateTime endOfCache = startOfCache.plus(2 * timePeriodAmount - 1, this.timePeriod);

        OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
        List<Appointment> appointments = appointmentDao.findByDateRangeAndDemographic(startOfCache.toLocalDate(),
                                                                                      endOfCache.toLocalDate(),
                                                                                      this.demographicNo);

        this.cacheWindow = startOfCache;
        this.appointmentCountCache = appointments.size();
    }
}