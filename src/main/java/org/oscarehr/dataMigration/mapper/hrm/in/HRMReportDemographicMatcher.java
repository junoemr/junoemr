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
import org.apache.commons.lang.StringUtils;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		// Required matching params.  These are intentionally all applied even if empty.
		// This is an OMD requirement:  we have to use the entire set of four parameters as given.
		searchParams.setHin(importStructure.getHCN());
		searchParams.setSex(importStructure.getGender());
		searchParams.setLastName(importStructure.getLegalLastName());
		importStructure.getDateOfBirth().ifPresent(searchParams::setDateOfBirth);
		
		// Additional parameters, are optional and applied as found.
		Optional.ofNullable(StringUtils.trimToNull(extractSubRegionCode(importStructure.getHCNProvinceCode())))
			.ifPresent(searchParams::setHealthCardProvince);
		Optional.ofNullable(StringUtils.trimToNull(importStructure.getHCNVersion()))
			.ifPresent(searchParams::setHealthCardVersion);

		return demographicDao.criteriaSearch(searchParams);
	}
	
	private String extractSubRegionCode(String provinceCode)
	{
		// Two characters if alone, or the last two characters if preceeded by "US-" or "CA-";
		final String SUBREGION_REGEX = "^(?:US-|CA-)?([A-Z]{2})$";
		Pattern regex = Pattern.compile(SUBREGION_REGEX);
		Matcher matcher = regex.matcher(provinceCode);

		if (matcher.find())
		{
			return matcher.group(1);
		}

		return null;
	}
}