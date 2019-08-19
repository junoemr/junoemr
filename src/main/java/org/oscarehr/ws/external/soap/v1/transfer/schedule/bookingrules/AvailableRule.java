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

/**
 * The appointment at {dateTime} is unbooked
 */
public class AvailableRule extends BookingRule
{
    private static final OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);

    AvailableRule(String jsonType)
    {
        super(BookingRuleType.BOOKING_AVAILABLE, jsonType);
    }

    @Override
    public Boolean isViolated(Appointment appointment)
    {
        return appointmentDao.checkForConflict(appointment);
    }

    @Override
    public Boolean isViolated(ScheduleSearchResult result)
    {
        // We don't actually validate this rule when generating slots because we create a list of valid slots,
        // instead of filtering out available slots from a list of all possibilities.  This value is a placeholder
        // in case we need to rework the logic at a later date.
        
        return false;
    }

    @Override
    public JSONObject toJSON()
    {
        JSONObject json = new JSONObject();
        json.put("name", this.jsonType);
        return json;
    }
}
