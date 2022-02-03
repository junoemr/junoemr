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
	public static String REGION_CODE_OTHER = "OT";

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
		if(regionCode != null && !regionCode.equals(REGION_CODE_OTHER))
		{
			code = countryCode + "-" + regionCode;
		}
		return code;
	}
}
