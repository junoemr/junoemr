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

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.AbstractTransientModel;

@Data
public class AddressModel extends AbstractTransientModel
{
	public static String US_REGION_CODE_OTHER = "OT";
	public static String US_REGION_CODE_RESIDENT = "**"; // basically unknown state, appears in UI sometimes

	public enum RESIDENCY_STATUS
	{
		CURRENT,
		PAST,
	}

	private String addressLine1;
	private String addressLine2;
	private String city;
	private String postalCode;
	private String regionCode;
	private String countryCode;
	private RESIDENCY_STATUS residencyStatus = RESIDENCY_STATUS.CURRENT;

	public void setResidencyStatusCurrent()
	{
		this.setResidencyStatus(RESIDENCY_STATUS.CURRENT);
	}
	public void setResidencyStatusPast()
	{
		this.setResidencyStatus(RESIDENCY_STATUS.PAST);
	}

	@JsonIgnore
	public boolean isCurrentAddress()
	{
		return this.residencyStatus.equals(RESIDENCY_STATUS.CURRENT);
	}
	@JsonIgnore
	public boolean isPastAddress()
	{
		return this.residencyStatus.equals(RESIDENCY_STATUS.PAST);
	}

	@JsonIgnore
	public String getAddressLinesString()
	{
		return StringUtils.trimToNull(
				StringUtils.trimToEmpty(getAddressLine1()) + " " + StringUtils.trimToEmpty(getAddressLine2()));
	}

	@JsonIgnore
	public String getSubdivisionCodeCT013Format()
	{
		return getSubdivisionCodeCT013Format(regionCode, countryCode);
	}

	@JsonIgnore
	public static String getSubdivisionCodeCT013Format(String regionCode, String countryCode)
	{
		String code = null;
		if(regionCode != null && !regionCode.equals(US_REGION_CODE_OTHER))
		{
			code = countryCode + "-" + regionCode;
		}
		return code;
	}


	/**
	 * parse out the region code from the region string value
	 * this is usually in the form of a 2 digit province code: ie: AB, ON, BC, etc.
	 * or in the case of non-canadian residents, a country code followed by the region code, US-WA, US-NY, etc.
	 * @param provinceCode to be parsed
	 * @return the province code
	 */
	@JsonIgnore
	public static String parseRegionCodeValue(String provinceCode)
	{
		if(provinceCode != null && provinceCode.contains("-"))
		{
			String[] provinceCodeSplit = provinceCode.split("-", -1);

			// empty string is allowed, sometimes indicates US resident (state unknown) in old system
			// in that case use the new marker instead
			String regionCode = provinceCodeSplit[1];
			if(regionCode.isEmpty())
			{
				regionCode = US_REGION_CODE_RESIDENT;
			}
			return regionCode;
		}
		else
		{
			return provinceCode;
		}
	}

	/**
	 * parse out the country code from the province value, or return the default.
	 * this is usually in the form of a 2 digit province code: ie: AB, ON, BC, etc.
	 * or in the case of non-canadian residents, a country code followed by the region code, US-WA, US-NY, etc.
	 * @param provinceCode to be parsed
	 * @param defaultCountry to be used in case province code has no country code part
	 * @return the country code
	 */
	@JsonIgnore
	public static String parseCountryCodeValue(String provinceCode, String defaultCountry)
	{
		if(provinceCode != null && provinceCode.contains("-"))
		{
			String[] provinceCodeSplit = provinceCode.split("-");
			return provinceCodeSplit[0];
		}
		else
		{
			return defaultCountry;
		}
	}
}
