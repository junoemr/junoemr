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
package org.oscarehr.demographic.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.search.DemographicCriteriaSearch;
import org.oscarehr.demographic.util.HinValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;

@Service
@Transactional
public class HinValidationService
{
	public static final String BC_NEWBORN_CODE = "66";

	@Autowired
	private DemographicDao demographicDao;

	/**
	 * returns the HinValidator isValid
	 */
	public boolean isHinValid(String hin, String provinceCode)
	{
		return HinValidator.isValid(hin, provinceCode);
	}

	/**
	 * checks if there is a already a demographic with the hin in the system.
	 * @param hin - the hin to check
	 * @return - true if the hin is found, false otherwise
	 */
	public boolean hinInSystem(String hin)
	{
		DemographicCriteriaSearch searchQuery = new DemographicCriteriaSearch();
		searchQuery.setHin(hin);
		searchQuery.setMatchModeExact();
		int totalResultCount = demographicDao.criteriaSearchCount(searchQuery);

		return totalResultCount > 0;
	}

	/**
	 * some cases exist where a duplicated health insurance number is allowable in the system.
	 * this method is used to determine if those conditions are met
	 * @param versionCode - hin version code
	 * @param provinceCode - hin province/type
	 * @return - true if the hin can be duplicated, false otherwise
	 */
	public boolean isDuplicateAllowable(String versionCode, String provinceCode)
	{
		return ("BC".equalsIgnoreCase(provinceCode) && BC_NEWBORN_CODE.equals(versionCode));
	}

	/**
	 * performs province specific validation checks on the hin.
	 * Throws validation exception if hin is invalid or if it already exists in the system.
	 * @param hin - the hin to validate
	 * @param versionCode - the version code (some version codes allow hin duplication)
	 * @param provinceCode - the province
	 */
	public void validateNoDuplication(String hin, String versionCode, String provinceCode)
	{
		hin = StringUtils.trimToNull(hin);
		// allow null/empty hin
		if(hin != null)
		{
			if(!isHinValid(hin, provinceCode))
			{
				throw new ValidationException("Invalid Hin");
			}
			if(!isDuplicateAllowable(versionCode, provinceCode) && hinInSystem(hin))
			{
				throw new ValidationException("Duplicate Hin");
			}
		}
	}
}
