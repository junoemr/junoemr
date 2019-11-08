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
import org.oscarehr.util.MiscUtils;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BookingRuleFactory
{

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

	public static List<MultipleBookingsRule> buildMultipleBookingsRuleList(
			String jsonRuleString) throws ParseException
	{
		List<MultipleBookingsRule> multipleBookingsRules = new ArrayList<>();

		JSONArray json = (JSONArray) new JSONParser().parse(jsonRuleString);

		for (Object jsonRule : json)
		{
			String type = (String) ((JSONObject) jsonRule).get("type");
			if (Arrays.asList(BookingRule.MULTIPLE_BOOKINGS_TYPES).contains(type))
			{
				MultipleBookingsRule rule = createMultipleBookingsRule((JSONObject) jsonRule);
				multipleBookingsRules.add(rule);
			}
		}
		Collections.sort(multipleBookingsRules);
		return multipleBookingsRules;
	}

	public static BlackoutRule buildBlackoutRule(String jsonRuleString) throws ParseException
	{
		JSONArray json = (JSONArray) new JSONParser().parse(jsonRuleString);
		BlackoutRule blackoutRule = null;
		for (Object jsonRule : json)
		{
			String type = (String) ((JSONObject) jsonRule).get("type");
			if (Arrays.asList(BookingRule.BLACKOUT_TYPES).contains(type))
			{
				blackoutRule = createBlackOutRule( (JSONObject) jsonRule);
				break;
			}
		}
		if(blackoutRule == null)
		{
			blackoutRule = new BlackoutRule();
		}
		return blackoutRule;
	}

	public static CutoffRule buildCutoffRule(String jsonRuleString) throws ParseException
	{
		JSONArray json = (JSONArray) new JSONParser().parse(jsonRuleString);
		CutoffRule cutoffRule = null;
		for (Object jsonRule : json)
		{
			String type = (String) ((JSONObject) jsonRule).get("type");

			MiscUtils.getLogger().warn("get cutoff rule TYPE: " + type);
			if (BookingRule.PERIOD_TYPE_CUTOFF_DAY.equals(type))
			{
				cutoffRule = createCutoffRule( (JSONObject) jsonRule);
				break;
			}
		}
		if(cutoffRule == null)
		{
			cutoffRule = new CutoffRule();
		}
		return cutoffRule;
	}


	private static BookingRule createBookingRule(Integer demographicNo, JSONObject jsonRule)
	{
		BookingRule bookingRule;
		String type = (String) jsonRule.get("type");

		switch (type)
		{
			case (BookingRule.PERIOD_TYPE_BLACKOUT_NOW_UNTIL_HOUR):
			case (BookingRule.PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY):
				bookingRule = createBlackOutRule(jsonRule);
				break;
			case (BookingRule.PERIOD_TYPE_CUTOFF_DAY):
				bookingRule = createCutoffRule(jsonRule);
				break;
			case (BookingRule.PERIOD_TYPE_HOUR):
			case (BookingRule.PERIOD_TYPE_DAY):
			case (BookingRule.PERIOD_TYPE_WEEK):
			case (BookingRule.PERIOD_TYPE_MONTH):
				bookingRule = createMultipleBookingsRule(jsonRule);
				break;
			case (BookingRule.PRIMARY_PROVIDER_ONLY):
				bookingRule = createPrimaryProviderOnlyRule(jsonRule, demographicNo);
				break;
			case (BookingRule.APPOINTMENT_AVAILABLE):
				bookingRule = createAvailableRule();
				break;
			default:
				bookingRule = null;
		}

		return bookingRule;
	}

	private static MultipleBookingsRule createMultipleBookingsRule(JSONObject jsonRule)
	{
		Integer timeAmount = jsonRule.get("period_of_time") != null ?
				((Long) jsonRule.get("period_of_time")).intValue() : null;
		Integer bookingAmount = jsonRule.get("bookings") != null ?
				((Long) jsonRule.get("bookings")).intValue() : null;
		String name = (String) jsonRule.get("type");
		ChronoUnit timeUnit = getChronoUnit(name);

		if (bookingAmount != null && timeAmount != null && timeUnit != null)
		{
			return new MultipleBookingsRule(name, bookingAmount, timeAmount, timeUnit);
		}

		return null;
	}

	private static CutoffRule createCutoffRule(JSONObject jsonRule)
	{
		Integer amount = jsonRule.get("period_of_time") != null ?
				((Long) jsonRule.get("period_of_time")).intValue() : null;
		String name = (String) jsonRule.get("type");
		ChronoUnit timeUnit = getChronoUnit(name);

		if (amount != null && timeUnit != null)
		{
			return new CutoffRule(name, amount, timeUnit);
		}

		return null;
	}

	private static BlackoutRule createBlackOutRule(JSONObject jsonRule)
	{
		Integer amount = jsonRule.get("period_of_time") != null ?
				((Long) jsonRule.get("period_of_time")).intValue() : null;
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
		return new AvailableRule(BookingRule.APPOINTMENT_AVAILABLE);
	}

	private static PrimaryProviderOnlyRule createPrimaryProviderOnlyRule(
			JSONObject jsonRule, Integer demographicNo)
	{
		String name = (String) jsonRule.get("type");
		return new PrimaryProviderOnlyRule(name, demographicNo);
	}

	private static List<BookingRule> createNewBookingRulesList()
	{
		List<BookingRule> bookingRules = new ArrayList<>();
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
			case (BookingRule.PERIOD_TYPE_BLACKOUT_NOW_UNTIL_DAY):
			case (BookingRule.PERIOD_TYPE_CUTOFF_DAY):
			case (BookingRule.PERIOD_TYPE_DAY):
				return ChronoUnit.DAYS;
			case (BookingRule.PERIOD_TYPE_BLACKOUT_NOW_UNTIL_HOUR):
			case (BookingRule.PERIOD_TYPE_HOUR):
				return ChronoUnit.HOURS;
			case (BookingRule.PERIOD_TYPE_WEEK):
				return ChronoUnit.WEEKS;
			case (BookingRule.PERIOD_TYPE_MONTH):
				return ChronoUnit.MONTHS;
			default:
				return null;
		}
	}
}
