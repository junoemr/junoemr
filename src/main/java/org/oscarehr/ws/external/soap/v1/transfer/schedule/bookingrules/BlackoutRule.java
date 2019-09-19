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
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Appointments cannot be booked within {amount} calendar {timePeriod(s)} of the current time
 */
public class BlackoutRule extends BookingRule
{
	static final Integer DEFAULT_AMOUNT = 0;
	static final ChronoUnit DEFAULT_TIME_PERIOD = ChronoUnit.DAYS;

    private Integer amount;
    private ChronoUnit timePeriod;

    BlackoutRule(String jsonType, Integer amount, ChronoUnit timePeriod)
    {
        super(BookingRuleType.BOOKING_BLACKOUT, jsonType);
        this.amount = amount;
        this.timePeriod = timePeriod;
    }

	BlackoutRule()
	{
		super(BookingRuleType.BOOKING_BLACKOUT, PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY);
		this.amount = DEFAULT_AMOUNT;
		this.timePeriod = DEFAULT_TIME_PERIOD;
	}


	@Override
	public Boolean isViolated(Appointment appointment)
    {
        return isBeforeBlackOutEnds(ConversionUtils.toLocalDateTime(appointment.getAppointmentDate()));
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        json.put("period_of_time", this.amount);

        return json;
    }

    public LocalDateTime getBlackoutTime()
	{
		return ConversionUtils.truncateLocalDateTime(
				LocalDateTime.now().plus(amount, timePeriod), timePeriod);
	}

	private Boolean isBeforeBlackOutEnds(LocalDateTime dateTime)
    {
        LocalDateTime cutoff = ConversionUtils.truncateLocalDateTime(
        		LocalDateTime.now(),timePeriod).plus(amount, timePeriod);

        return dateTime.isBefore(cutoff);
    }
}
