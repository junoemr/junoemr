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
    private final OscarAppointmentDao appointmentDao;

    MultipleBookingRule (String jsonType, Integer bookingAmount, Integer timePeriodAmount, ChronoUnit timePeriod)
    {
        super(jsonType);
        this.bookingAmount = bookingAmount;
        this.timePeriodAmount = timePeriodAmount;
        this.timePeriod = timePeriod;
        this.appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
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
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        json.put("bookings", this.bookingAmount);
        json.put("period_of_time", this.timePeriodAmount);
        return json;
    }


}