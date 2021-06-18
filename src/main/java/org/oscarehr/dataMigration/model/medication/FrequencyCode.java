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
package org.oscarehr.dataMigration.model.medication;

import lombok.Data;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.dataMigration.exception.InvalidFrequencyCodeException;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class FrequencyCode extends AbstractTransientModel
{
	private static final double DAYS_IN_MONTH = 30.0; // approximated
	private static final double DAYS_IN_WEEK = 7.0;
	private static final double HOURS_IN_DAY = 24.0;
	private static final double MINUTES_IN_DAY = 1440.0;
	private static final double SECONDS_IN_DAY = 86400.0;

	private String code;

	public FrequencyCode()
	{
		this(null);
	}
	public FrequencyCode(String code)
	{
		this.code = code;
	}

	enum MEDICATION_FREQUENCY_CODES
	{
		BID,        // Two times daily
		ONCE,       // One time only
		Q1_2H,      // Every 1 to 2 hours
		Q12H,       // Every 12 hours
		Q1H,        // Every hour
		Q2_3H,      // Every 2 to 3 hours
		Q2D,        // Every other day
		Q2H,        // Every 2 hours
		Q3_4H,      // Every 3 to 4 hours
		Q3H,        // Every 3 hours
		Q4_6H,      // Every 4 to 6 hours
		Q4H,        // Every 4 hours
		Q6_8H,      // Every 6 to 8 hours
		Q6H,        // Every 6 hours
		Q8_12H,     // Every 8 to 12 hours
		Q8H,        // Every 8 hours
		QAM,        // Every morning
		QD,         // Once daily
		OD,         // Once daily
		QHS,        // Every day at bedtime
		QID,        // Four times daily
		QNOON,      // Every day at noon
		QPM,        // Every evening
		STAT,       // NOW
		TID         // Three times a day
	}


	/**
	 * @return the frequency as a scalar double value in days
	 */
	public Double toScalar()
	{
		if(code == null || code.isEmpty())
		{
			throw new InvalidFrequencyCodeException("Frequency code conversion error. Missing frequency code!");
		}

		String formattedCode = code.replace("-", "_").toUpperCase();
		if(EnumUtils.isValidEnum(MEDICATION_FREQUENCY_CODES.class, formattedCode))
		{
			return toScalarFromEnumCode(MEDICATION_FREQUENCY_CODES.valueOf(formattedCode));
		}
		else
		{
			return toScalarByDynamicMatching(code);
		}
	}

	public static FrequencyCode from(String code)
	{
		FrequencyCode frequencyCode = null;
		if(code != null)
		{
			frequencyCode = new FrequencyCode(code);
		}
		return frequencyCode;
	}

	private double toScalarFromEnumCode(MEDICATION_FREQUENCY_CODES freq)
	{
		if(freq != null)
		{
			switch(freq)
			{
				case QD:
				case OD:
				case QAM:
				case QPM:
				case QNOON:
				case QHS:
					return 1.0;
				case Q12H:
				case Q8_12H:
				case BID:
					return 2.0;
				case Q2H:
				case Q1_2H:
					return 12.0;
				case Q1H:
					return 24.0;
				case Q3H:
				case Q2_3H:
					return 8.0;
				case Q4H:
				case Q3_4H:
					return 6.0;
				case QID:
				case Q6H:
				case Q4_6H:
					return 4.0;
				case TID:
				case Q8H:
				case Q6_8H:
					return 3.0;
				case Q2D:
					return 0.5;
				case STAT:
				case ONCE:
					return -1.0;
				default: throw new InvalidFrequencyCodeException("Frequency code conversion error. No mapping for '" + freq + "'");
			}
		}
		throw new InvalidFrequencyCodeException("Frequency code conversion error. Missing frequency code!");
	}

	private double toScalarByDynamicMatching(String code)
	{
		Double freq = toScalarByParseMedicalShortForm(code);
		if(freq != null)
		{
			return freq;
		}

		freq = toScalarByParseTimes(code);
		if(freq != null)
		{
			return freq;
		}

		freq = toScalarByParseEvery(code);
		if(freq != null)
		{
			return freq;
		}

		freq = getFrequencySpecialCaseLookup(code);
		if(freq != null)
		{
			return freq;
		}

		throw new InvalidFrequencyCodeException("Frequency code conversion error. Cannot dynamically map '" + code + "'");
	}

	private Double toScalarByParseMedicalShortForm(String code)
	{
		// may be dynamic code type. Try dynamic matching
		Matcher match = Pattern.compile("Q?(\\d+)(\\w+)").matcher(code);
		if (match.matches())
		{
			double num = Double.parseDouble(match.group(1));
			String unit = match.group(2);
			return getFrequencyEveryUnit(num, unit);
		}
		return null;
	}

	private Double toScalarByParseTimes(String code)
	{
		// match this pattern for something like 'n times daily'
		Matcher match = Pattern.compile("(\\d+)\\s+times?\\s+(\\w+)").matcher(code);
		if (match.matches())
		{
			double num = Double.parseDouble(match.group(1));
			String unit = match.group(2).toLowerCase();
			return getFrequencyPerUnit(num, unit);
		}
		return null;
	}

	private Double toScalarByParseEvery(String code)
	{
		// match this pattern for something like 'every n days' or 'every n-m days'
		Matcher matchV1 = Pattern.compile("every\\s+((\\d+-)?(\\d+))\\s+(\\w+)").matcher(code);
		if (matchV1.matches())
		{
			double num = Double.parseDouble(matchV1.group(3));
			String unit = matchV1.group(4).toLowerCase();
			return getFrequencyEveryUnit(num, unit);
		}
		// match this pattern for something like 'every day' or 'every evening before bed'
		Matcher matchV2 = Pattern.compile("every\\s+(\\w+).*").matcher(code);
		if (matchV2.matches())
		{
			String unit = matchV2.group(1).toLowerCase();
			switch(unit)
			{
				case "morning":
				case "evening":
				case "afternoon":
				case "night":
				case "day": return 1.0;
			}
		}
		return null;
	}

	private Double getFrequencyPerUnit(double num, String unit)
	{
		switch (unit.toUpperCase())
		{
			case "L":
			case "MONTHS":
			case "MONTHLY":
				return num / DAYS_IN_MONTH;
			case "W":
			case "WEEKS":
			case "WEEKLY":
				return num / DAYS_IN_WEEK;
			case "D":
			case "ID":
			case "DAYS":
			case "DAILY" :
				return num;
			case "H":
			case "HOURS":
			case "HOURLY":
				return HOURS_IN_DAY * num;
			case "M":
			case "MINUTE":
			case "MINUTES":
				return MINUTES_IN_DAY * num;
			case "S":
			case "SECOND":
			case "SECONDS":
				return SECONDS_IN_DAY * num;
		}
		return null;
	}

	private Double getFrequencyEveryUnit(double num, String unit)
	{
		switch (unit.toUpperCase())
		{
			case "L":
			case "MONTHS":
			case "MONTHLY":
				return 1 / (DAYS_IN_MONTH * num);
			case "W":
			case "WEEKS":
			case "WEEKLY":
				return 1 / (DAYS_IN_WEEK * num);
			case "D":
			case "ID":
			case "DAYS":
			case "DAILY" :
				return 1 / num;
			case "H":
			case "HOURS":
			case "HOURLY":
				return HOURS_IN_DAY / num;
			case "M":
			case "MINUTE":
			case "MINUTES":
				return MINUTES_IN_DAY / num;
			case "S":
			case "SECOND":
			case "SECONDS":
				return SECONDS_IN_DAY / num;
		}
		return null;
	}

	private Double getFrequencySpecialCaseLookup(String code)
	{
		switch (code.toLowerCase())
		{
			case "now":
			case "once":
			case "one time":
			case "one time only": return -1.0;
		}
		return null;
	}
}
