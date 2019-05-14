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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.oscarehr.ws.external.soap.v1.transfer.schedule.cancelrules.CancelCutoffRule;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingRuleFactory
{
    private static final String PERIOD_TYPE_CUTOFF_DAY = "cuttoff_day";
    private static final String PERIOD_TYPE_BLACKOUT_NOW_UNTIL_HOUR = "blackout_now_until_hour";
    private static final String PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY = "blackout_now_until_day";
    private static final String PERIOD_TYPE_HOUR = "hour";
    private static final String PERIOD_TYPE_DAY = "day";
    private static final String PERIOD_TYPE_WEEK = "week";
    private static final String PERIOD_TYPE_MONTH = "month";
    private static final String PRIMARY_PROVIDER_ONLY = "primary_provider_only";    // TODO: Placeholder
    private static final String APPOINTMENT_AVAILABLE = "appointment_is_available"; // TODO: Placeholder

    // TODO: Class into component and autowire?
    public static List<BookingRule> createBookingRuleList(Integer demographicNo, String jsonRuleString) throws ParseException
    {
        List<BookingRule> bookingRules = createNewBookingRulesList();

        JSONArray json = (JSONArray) new JSONParser().parse(jsonRuleString);

        for (Object jsonRule : json)
        {
            BookingRule rule = createBookingRule(demographicNo, (JSONObject) jsonRule);

            if (rule != null)
            {
                bookingRules.add(rule);
            }
        }

        Collections.sort(bookingRules);
        return bookingRules;
    }

    private static BookingRule createBookingRule(Integer demographicNo, JSONObject jsonRule)
    {
        BookingRule bookingRule;
        String type = (String) jsonRule.get("type");

        switch (type)
        {
            case (PERIOD_TYPE_BLACKOUT_NOW_UNTIL_HOUR):
            case (PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY):
                bookingRule = createBlackOutRule(jsonRule);
                break;
            case (PERIOD_TYPE_CUTOFF_DAY):
                bookingRule = createCutoffRule(jsonRule);
                break;
            case (PERIOD_TYPE_HOUR):
            case (PERIOD_TYPE_DAY):
            case (PERIOD_TYPE_WEEK):
            case (PERIOD_TYPE_MONTH):
                bookingRule = createMultipleBookingRule(jsonRule, demographicNo);
                break;
            case (PRIMARY_PROVIDER_ONLY):
                bookingRule = createPrimaryProviderOnlyRule(jsonRule, demographicNo);
                break;
            case (APPOINTMENT_AVAILABLE):
                bookingRule = createAvailableRule();
                break;
            default:
                bookingRule = null;
        }

        return bookingRule;
    }

    private static MultipleBookingRule createMultipleBookingRule(JSONObject jsonRule, Integer demographicNo)
    {
        Integer timeAmount = jsonRule.get("period_of_time") != null ? ((Long) jsonRule.get("period_of_time")).intValue() : null;
        Integer bookingAmount = jsonRule.get("bookings") != null ? ((Long) jsonRule.get("bookings")).intValue() : null;
        String name = (String) jsonRule.get("type");
        ChronoUnit timeUnit = getChronoUnit(name);

        if (bookingAmount != null && timeAmount != null && timeUnit != null)
        {
            return new MultipleBookingRule(name, demographicNo, bookingAmount, timeAmount, timeUnit);
        }

        return null;
    }

    private static BookingCutoffRule createCutoffRule(JSONObject jsonRule)
    {
        Integer amount = jsonRule.get("period_of_time") != null ? ((Long) jsonRule.get("period_of_time")).intValue() : null;
        String name = (String) jsonRule.get("type");
        ChronoUnit timeUnit = getChronoUnit(name);

        if (amount != null && timeUnit != null)
        {
            return new BookingCutoffRule(name, amount, timeUnit);
        }

        return null;
    }

    private static BlackoutRule createBlackOutRule(JSONObject jsonRule)
    {
        Integer amount = jsonRule.get("period_of_time") != null ? ((Long) jsonRule.get("period_of_time")).intValue() : null;
        String name = (String) jsonRule.get("type");
        ChronoUnit timeUnit = getChronoUnit(name);

        if (amount != null && timeUnit != null)
        {
            return new BlackoutRule(name, amount, timeUnit);
        }

        return null;
    }

    private static AvailableRule createAvailableRule()
    {
        // TODO:  Should this come in from MHA?
        return new AvailableRule(APPOINTMENT_AVAILABLE);
    }

    private static PrimaryProviderOnlyRule createPrimaryProviderOnlyRule(JSONObject jsonRule, Integer demographicNo)
    {
        String name = (String) jsonRule.get("type");
        return new PrimaryProviderOnlyRule(name, demographicNo);
    }

    private static List<BookingRule> createNewBookingRulesList()
    {
        List<BookingRule> bookingRules = new ArrayList<>();

        // There is an implicit booking rule that the appointment must be unbooked for each set of booking rules.
        bookingRules.add(createAvailableRule());

        return bookingRules;
    }

    private static ChronoUnit getChronoUnit(String bookingRuleName)
    {
        if (bookingRuleName == null)
        {
            return null;
        }

        switch (bookingRuleName)
        {
            case (PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY):
            case (PERIOD_TYPE_CUTOFF_DAY):
            case (PERIOD_TYPE_DAY):
                return ChronoUnit.DAYS;
            case (PERIOD_TYPE_BLACKOUT_NOW_UNTIL_HOUR):
            case (PERIOD_TYPE_HOUR):
                return ChronoUnit.HOURS;
            case (PERIOD_TYPE_WEEK):
                return ChronoUnit.WEEKS;
            case (PERIOD_TYPE_MONTH):
                return ChronoUnit.MONTHS;
            default:
                return null;
        }
    }
}
