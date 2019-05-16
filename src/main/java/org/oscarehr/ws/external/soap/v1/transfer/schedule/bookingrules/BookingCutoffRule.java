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
import org.oscarehr.common.model.Appointment;
import org.oscarehr.schedule.model.ScheduleSearchResult;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Appointments can only be booked within {amount} calendar {timePeriods(s)} of the current time
 */
public class BookingCutoffRule extends BookingRule
{
    private Integer amount;
    private ChronoUnit timePeriod;

    public BookingCutoffRule(String jsonType, Integer amount, ChronoUnit timePeriod)
    {
        super(BookingRuleType.BOOKING_CUTOFF, jsonType);
        this.amount = amount;
        this.timePeriod = timePeriod;
    }

    @Override
    public Boolean isViolated(Appointment appointment)
    {
        LocalDateTime appointmentTime = ConversionUtils.toLocalDateTime(appointment.getAppointmentDate());
        return isAfterCutoff(appointmentTime);
    }

    @Override
    public Boolean isViolated(ScheduleSearchResult result)
    {
        LocalDateTime slotDate = result.dateTime;
        return isAfterCutoff(slotDate);
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        json.put("period_of_time", this.amount);

        return json;
    }

    private Boolean isAfterCutoff(LocalDateTime dateTime)
    {
        LocalDateTime cutoff = ConversionUtils.truncateLocalDateTime(LocalDateTime.now(), timePeriod).plus(amount, timePeriod);

        return dateTime.isAfter(cutoff);
    }
}
