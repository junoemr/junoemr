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
package org.oscarehr.demographicImport.model.medication;

import lombok.Data;
import org.oscarehr.demographicImport.model.AbstractTransientModel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class FrequencyCode extends AbstractTransientModel
{
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
		BID, 		// Two times daily
		ONCE, 	// One time only
		Q1_2H, 	// Every 1 to 2 hours
		Q12H, 	// Every 12 hours
		Q1H,		// Every hour
		Q2_3H,	// Every 2 to 3 hours
		Q2D,		// Every other day
		Q2H,		// Every 2 hours
		Q3_4H,	// Every 3 to 4 hours
		Q3H,		// Every 3 hours
		Q4_6H,	// Every 4 to 6 hours
		Q4H,		// Every 4 hours
		Q6_8H,	// Every 6 to 8 hours
		Q6H,		// Every 6 hours
		Q8_12H, // Every 8 to 12 hours
		Q8H,		// Every 8 hours
		QAM,		// Every morning
		QD,			// Once daily
		OD,			// Once daily
		QHS,		// Every day at bedtime
		QID,		// Four times daily
		QNOON,  // Every day at noon
		QPM,		// Every evening
		STAT,   // NOW
		TID  		// Three times a day
	}


	public Double toScaler()
	{
		if(code == null || code.isEmpty())
		{
			throw new RuntimeException("Frequency code conversion error. Missing frequency code!");
		}

		try
		{
			MEDICATION_FREQUENCY_CODES freq = MEDICATION_FREQUENCY_CODES.valueOf(code.replace("-", "_").toUpperCase());

			switch (freq)
			{
				case QD:
				case OD:
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
			}
		}
		catch (IllegalArgumentException e)
		{
			// may be dynamic code type. Try dynamic matching
			Matcher match = Pattern.compile("Q?(\\d+)(\\w)").matcher(code);
			if (match.matches())
			{
				Double num = Double.parseDouble(match.group(1));
				String unit = match.group(2);
				switch (unit)
				{
					case "ID":
						return num;
					case "D":
					case "Days":
						return 1.0 / num;
					case "H":
						return 24.0 / num;
					case "L":
						return 1.0 / (30.0 * num);
					case "M":
					case "Months":
						return 1440.0 / num;
					case "S":
						return 86400.0 / num;
					case "W":
						return 1 / (7.0 * num);
				}
			}
		}

		throw new RuntimeException("Frequency code conversion error. No mapping for [" + code + "]!");
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
}
