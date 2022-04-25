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
package org.oscarehr.dataMigration.model.common;

import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.demographic.model.DemographicModel;

import java.util.HashMap;
import java.util.Map;

public interface Person
{
	enum TITLE {
		MISS,
		MRS,
		MS,
		MR,
		MSSR,
		DR,
		PROF,
		REEVE,
		REV,
		RT_HON,
		SEN,
		SGT,
		SR;

		public static TITLE fromStringIgnoreCase(String enumString)
		{
			if(EnumUtils.isValidEnumIgnoreCase(DemographicModel.TITLE.class, enumString))
			{
				return DemographicModel.TITLE.valueOf(enumString.toUpperCase());
			}
			return null;
		}
	}
	enum SEX {
		MALE("M"),
		FEMALE("F"),
		OTHER("O"),
		TRANSGENDER("T"),
		UNKNOWN("U");

		private final String value;
		//Lookup table
		private static final Map<String, SEX> lookup = new HashMap<>();

		//Populate the lookup table on loading time
		static
		{
			for(SEX sex : SEX.values())
			{
				lookup.put(sex.getValue(), sex);
			}
		}

		SEX(String value)
		{
			this.value = value;
		}

		@JsonValue
		public String getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return getValue();
		}

		public static SEX fromStringIgnoreCase(String enumString)
		{
			if(EnumUtils.isValidEnumIgnoreCase(DemographicModel.SEX.class, enumString))
			{
				return DemographicModel.SEX.valueOf(enumString.toUpperCase());
			}
			return null;
		}

		//This method can be used for reverse lookup purpose
		public static SEX getIgnoreCase(String val)
		{
			if(val != null)
			{
				return lookup.get(val.toUpperCase());
			}
			return null;
		}
	}

	String getSexString();
	String getTitleString();
	String getDisplayName();
}
