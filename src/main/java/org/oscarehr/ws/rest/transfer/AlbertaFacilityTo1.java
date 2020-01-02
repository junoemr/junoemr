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
package org.oscarehr.ws.rest.transfer;

import org.oscarehr.billing.CA.AB.model.AlbertaFacility;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AlbertaFacilityTo1
{
	private Integer code;
	private String type;
	private String description;
	private LocalDate startDate;
	private LocalDate endDate;

	public static List<AlbertaFacilityTo1> fromList(List<AlbertaFacility> albertaFacilities)
	{
		ArrayList<AlbertaFacilityTo1> albertaFacilityTo1s = new ArrayList<>();
		for (AlbertaFacility albertaFacility : albertaFacilities)
		{
			albertaFacilityTo1s.add(new AlbertaFacilityTo1(albertaFacility));
		}

		return albertaFacilityTo1s;
	}

	public AlbertaFacilityTo1(AlbertaFacility albertaFacility)
	{
		code = albertaFacility.getCode();
		type = albertaFacility.getType();
		description = albertaFacility.getDescription();
		startDate = ConversionUtils.toNullableLocalDate(albertaFacility.getStartDate());
		endDate = ConversionUtils.toNullableLocalDate(albertaFacility.getEndDate());
	}

	public Integer getCode()
	{
		return code;
	}

	public void setCode(Integer code)
	{
		this.code = code;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public LocalDate getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDate startDate)
	{
		this.startDate = startDate;
	}

	public LocalDate getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDate endDate)
	{
		this.endDate = endDate;
	}
}
