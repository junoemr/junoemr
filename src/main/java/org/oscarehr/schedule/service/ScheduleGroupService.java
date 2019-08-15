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
package org.oscarehr.schedule.service;

import org.apache.log4j.Logger;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.schedule.dto.ScheduleGroup;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ScheduleGroupService
{
	private static Logger logger = MiscUtils.getLogger();

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	MyGroupDao myGroupDao;

	/**
	 * Creates a list of schedule groups.  The list is made of groups from the mygroup table and then
	 * each provider is appended as there is an implicit schedule for each provider.
	 * @return The list of valid schedule groups.
	 */
	public List<ScheduleGroup> getScheduleGroups()
	{
		List<ScheduleGroup> groups = getMyGroupScheduleGroups();

		// Get a list of providers
		List<ProviderData> providers = providerDataDao.findAll(false);

		for(ProviderData provider: providers)
		{
			Integer providerNo;
			try
			{
				providerNo = Integer.parseInt(provider.getId());
			}
			catch(NumberFormatException e)
			{
				logger.error("Bad providerNo in the provider table", e);
				continue;
			}

			String providerName = provider.getLastName() + ", " + provider.getFirstName();
			ArrayList<Integer> providerIds = new ArrayList<>();
			providerIds.add(providerNo);

			groups.add(new ScheduleGroup(provider.getId(), ScheduleGroup.IdentifierType.PROVIDER, providerName, providerIds));
		}

		return groups;
	}

	private List<ScheduleGroup> getMyGroupScheduleGroups()
	{
		List<ScheduleGroup> scheduleGroupList = new ArrayList<>();

		List<MyGroup> rawResult = myGroupDao.findAllOrdered();

		String currentGroupName = null;
		List<Integer> currentProviderList = new ArrayList<>();
		for(MyGroup myGroup: rawResult)
		{
			Integer providerNo;
			try
			{
				providerNo = Integer.parseInt(myGroup.getId().getProviderNo());
			}
			catch(NumberFormatException e)
			{
				logger.error("Bad providerNo in the mygroup table", e);
				continue;
			}

			String newGroupName = myGroup.getId().getMyGroupNo();

			if(currentGroupName == null)
			{
				currentGroupName = newGroupName;
			}
			else if(!newGroupName.equals(currentGroupName))
			{
				// Add the group to the group list
				scheduleGroupList.add(
					new ScheduleGroup(currentGroupName, ScheduleGroup.IdentifierType.GROUP, currentGroupName, currentProviderList));

				// Reset
				currentProviderList = new ArrayList<>();
				currentGroupName = newGroupName;
			}

			currentProviderList.add(providerNo);
		}

		if(currentGroupName != null)
		{
			scheduleGroupList
				.add(new ScheduleGroup(currentGroupName, ScheduleGroup.IdentifierType.GROUP, currentGroupName, currentProviderList));
		}

		return scheduleGroupList;
	}
}
