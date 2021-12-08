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
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HRMReportDemographicMatcher extends AbstractHRMImportMapper<HRMReport_4_3, List<Demographic>>
{
	@Autowired
	private DemographicDao demographicDao;
	
	@Override
	public List<Demographic> importToJuno(HRMReport_4_3 importStructure) throws Exception
	{
		DemographicCriteriaSearch searchParams = new DemographicCriteriaSearch();
		searchParams.setMatchModeExact();
		searchParams.setJunctionTypeAND();
		
		// Required matching params
		searchParams.setHin(importStructure.getHCN());
		searchParams.setSex(importStructure.getGender());
		searchParams.setLastName(importStructure.getLegalLastName());
		importStructure.getDateOfBirthAsLocalDate()
			.ifPresent(searchParams::setDateOfBirth);
		
		// Additional parameters
		searchParams.setHealthCardProvince(extractSubRegionCode(importStructure.getHCNProvinceCode()));
		searchParams.setHealthCardVersion(importStructure.getHCNVersion());
		
		return demographicDao.criteriaSearch(searchParams);
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
			return "";
		}
	}
}