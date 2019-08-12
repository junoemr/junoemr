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

package org.oscarehr.ws.external.soap.v1.transfer.Appointment;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.bookingrules.BookingRule;

import java.io.Serializable;
import java.util.List;

/**
 * Outbound transfer object for booking appointments using a set of booking rules.  If the appointment booking
 * is successful (no booking rules are violated), then the appointment ID will be returned and validated will be
 * set to true.  Otherwise, validated is set to false and a list of rules violations is returned.
 */
public class ValidatedAppointmentBookingTransfer implements Serializable
{
    private AppointmentTransfer appointment;
    private Boolean validated;
    private String ruleViolations;

    public ValidatedAppointmentBookingTransfer(){};

    public ValidatedAppointmentBookingTransfer(AppointmentTransfer appointment, List<BookingRule> violatedBookingRules)
    {
        if (violatedBookingRules.isEmpty())
        {
            setValidated(true);
            setAppointment(appointment);
            setRuleViolations(new JSONArray().toJSONString());
        }
        else
        {
            setValidated(false);
            setAppointment(null);
            setRuleViolations(bookingRulesToJSONString(violatedBookingRules));
        }
    }

    private String bookingRulesToJSONString(List<BookingRule> bookingRules)
    {
        JSONArray jsonArray = new JSONArray();
        for (BookingRule rule : bookingRules)
        {
            JSONObject ruleJSON = rule.toJSON();
            jsonArray.add(ruleJSON);

        }
        return jsonArray.toJSONString();
    }

    public AppointmentTransfer getAppointment()
    {
        return this.appointment;
    }

    public void setAppointment(AppointmentTransfer appointment)
    {
        this.appointment = appointment;
    }

    public Boolean getValidated()
    {
        return validated;
    }

    public void setValidated(Boolean validated)
    {
        this.validated = validated;
    }

    public String getRuleViolations()
    {
        return ruleViolations;
    }

    public void setRuleViolations(String ruleViolations)
    {
        this.ruleViolations = ruleViolations;
    }
}
