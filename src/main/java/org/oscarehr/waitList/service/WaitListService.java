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
package org.oscarehr.waitList.service;

import org.oscarehr.common.dao.WaitingListDao;
import org.oscarehr.common.dao.WaitingListNameDao;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.waitList.converter.DemographicWaitListToModelConverter;
import org.oscarehr.waitList.converter.WaitListNameToModelConverter;
import org.oscarehr.waitList.model.DemographicWaitListModel;
import org.oscarehr.waitList.model.WaitListModel;
import org.oscarehr.waitList.transfer.DemographicWaitListUpdateInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarWaitingList.util.WLWaitingListUtil;
import oscar.util.ConversionUtils;

import java.util.List;

@Service
@Transactional
public class WaitListService
{
	@Autowired
	private WaitingListDao waitingListDao;

	@Autowired
	private WaitingListNameDao waitingListNameDao;

	@Autowired
	private WaitListNameToModelConverter waitListNameToModelConverter;

	@Autowired
	private DemographicWaitListToModelConverter demographicWaitListToModelConverter;

	public List<WaitListModel> getActiveWaitLists()
	{
		List<WaitingListName> waitingListNames = waitingListNameDao.findActiveWatingListNames();
		return waitListNameToModelConverter.convert(waitingListNames);
	}

	public void updateDemographicWaitList(Integer demographicId, DemographicWaitListUpdateInput updateInput)
	{
		WLWaitingListUtil.updateWaitingListRecord(
				updateInput.getWaitListId().toString(),
				updateInput.getNote(),
				demographicId.toString(),
				ConversionUtils.toDateString(updateInput.getDateAddedToWaitList()));
	}

	// holdover from old demographic model lookup, might need to be updated
	public DemographicWaitListModel getCurrentWaitList(Integer demographicId)
	{
		List<WaitingList> allWaitingLists = waitingListDao.search_wlstatus(demographicId);

		WaitingList waitingList = null;
		if(allWaitingLists != null && !allWaitingLists.isEmpty())
		{
			waitingList = allWaitingLists.get(0);
		}
		return demographicWaitListToModelConverter.convert(waitingList);
	}
}
