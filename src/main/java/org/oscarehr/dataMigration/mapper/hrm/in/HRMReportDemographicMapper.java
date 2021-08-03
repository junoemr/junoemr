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


package org.oscarehr.dataMigration.mapper.hrm.in;

import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;

import java.time.LocalDate;
import java.util.List;

public class HRMReportDemographicMapper extends AbstractHRMImportMapper<HRMReport_4_3, Demographic>
{
	
	@Override
	public Demographic importToJuno(HRMReport_4_3 importStructure) throws Exception
	{
		Demographic demographic = new Demographic();
		
		// Stub on matching criteria to map HRM documents to demographics, which are the health number and birthday
		demographic.setHealthNumber(importStructure.getHCN());
		demographic.setHealthNumberProvinceCode(extractSubRegionCode(importStructure.getHCNProvinceCode()));
		demographic.setHealthNumberVersion(importStructure.getHCNVersion());
		
		List<Integer> dateOfBirth = importStructure.getDateOfBirth();
		if (dateOfBirth != null && dateOfBirth.size() != 3)
		{
			LocalDate date = LocalDate.of(dateOfBirth.get(0), dateOfBirth.get(1), dateOfBirth.get(2));
			demographic.setDateOfBirth(date);
		}
		
		return demographic;
	}
	
	private String extractSubRegionCode(String provinceCode)
	{
		final String PREFIX_US = "US-";
		final String PREFIX_CA = "CA-";
		
		if (provinceCode.length() == 2)
		{
			return provinceCode;
		}
		else if (provinceCode.length() == 5 && (provinceCode.startsWith(PREFIX_CA) || provinceCode.startsWith(PREFIX_US)))
		{
			return provinceCode.substring(3,5);
		}
		else
		{
			// TODO: Exception?
			return "";
		}
	}
}
