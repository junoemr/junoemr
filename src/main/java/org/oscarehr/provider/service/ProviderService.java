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
package org.oscarehr.provider.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarProvider.data.ProviderBillCenter;

import java.util.Date;

@Service("provider.service.ProviderService")
@Transactional
public class ProviderService
{
	@Autowired
	ProviderDataDao providerDataDao;

	public ProviderData addNewProvider(String creatingProviderNo, ProviderData provider, String billCenterCode)
	{
		provider.setLastUpdateDate(new Date());
		provider.setLastUpdateUser(creatingProviderNo);
		if(provider.getStatus() == null)
		{
			provider.setStatus("1");
		}
		if(provider.getProviderType() == null)
		{
			provider.setProviderType("doctor");
		}
		if(provider.getSex() == null || provider.getSex().trim().isEmpty())
		{
			provider.setSex("U");
		}
		if(provider.getSpecialty() == null)
		{
			provider.setSpecialty("");
		}
		if(provider.getTeam() == null)
		{
			provider.setTeam("");
		}
		if(provider.getPhone() == null)
		{
			provider.setPhone("");
		}
		if(provider.getWorkPhone() == null)
		{
			provider.setWorkPhone("");
		}

		// providers don't have auto-generated IDs, so we have to pick one if it has not been provided
		if(provider.getId() == null)
		{
			Integer autoNumber = getNextProviderNumberInSequence(0, 900000);
			if(autoNumber == null)
			{
				// no providers exist in the given range, use id 1
				autoNumber = 1;
			}
			provider.set(String.valueOf(autoNumber));
		}
		providerDataDao.persist(provider);

		ProviderBillCenter billCenter = new ProviderBillCenter();
		billCenter.addBillCenter(String.valueOf(provider.getProviderNo()),StringUtils.trimToEmpty(billCenterCode));

		return provider;
	}

	/**
	 * get the highest id + 1 ignoring provider id's below the min threshold and above the ignore threshold
	 * @param minThreshold
	 * @param ignoreThreshold
	 * @return null if there are no providers in the given range, or the highest provider number + 1 otherwise
	 */
	public Integer getNextProviderNumberInSequence(int minThreshold, int ignoreThreshold)
	{
		return providerDataDao.getNextIdWithThreshold(minThreshold, ignoreThreshold);
	}
}
