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

package org.oscarehr.rosterStatus.service;

import org.oscarehr.rosterStatus.converter.RosterStatusToEntityConverter;
import org.oscarehr.rosterStatus.converter.RosterStatusToModelConverter;
import org.oscarehr.rosterStatus.dao.RosterStatusDao;
import org.oscarehr.rosterStatus.entity.RosterStatus;
import org.oscarehr.rosterStatus.model.RosterStatusModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RosterStatusService
{
	@Autowired
	protected RosterStatusDao rosterStatusDao;

	@Autowired
	protected RosterStatusToModelConverter rosterStatusToModelConverter;

	@Autowired
	protected RosterStatusToEntityConverter rosterStatusToEntityConverter;

	public List<RosterStatusModel> getRosterStatusList(Boolean active)
	{
		List<RosterStatus> statuses;
		if (active != null && active)
		{
			statuses = rosterStatusDao.findAllActive();
		}
		else if (active != null)
		{
			statuses = rosterStatusDao.findAllInactive();
		}
		else
		{
			statuses = rosterStatusDao.findAll();
		}
		return rosterStatusToModelConverter.convert(statuses);
	}

	public RosterStatusModel addStatus(RosterStatusModel statusTransfer, String providerNo)
	{
		RosterStatus rosterStatus = rosterStatusToEntityConverter.convert(statusTransfer);
		rosterStatus.setCreatedAt(LocalDateTime.now());
		rosterStatus.setUpdatedAt(LocalDateTime.now());
		rosterStatus.setUpdatedBy(providerNo);

		if (rosterStatus.isActive())
		{
			rosterStatus.setDeletedAt(null);
		}
		else
		{
			rosterStatus.setDeletedAt(LocalDateTime.now());
		}

		rosterStatusDao.persist(rosterStatus);

		LogAction.addLogEntry(providerNo,
				LogConst.ACTION_ADD,
				LogConst.CON_ADMIN,
				LogConst.STATUS_SUCCESS,
				"Roster Status: " + rosterStatus.getRosterStatus());

		return rosterStatusToModelConverter.convert(rosterStatus);
	}

	public RosterStatusModel editStatus(RosterStatusModel statusTransfer, String editingProvider)
	{
		RosterStatus rosterStatus = rosterStatusToEntityConverter.convert(statusTransfer);
		rosterStatus.setUpdatedAt(LocalDateTime.now());
		rosterStatus.setUpdatedBy(editingProvider);

		if (rosterStatus.isActive())
		{
			rosterStatus.setDeletedAt(null);
		}
		else
		{
			rosterStatus.setDeletedAt(LocalDateTime.now());
		}

		rosterStatusDao.merge(rosterStatus);

		LogAction.addLogEntry(editingProvider,
				LogConst.ACTION_EDIT,
				LogConst.CON_ADMIN,
				LogConst.STATUS_SUCCESS,
				"Roster Status EDITED: " + rosterStatus.getRosterStatus());

		return rosterStatusToModelConverter.convert(rosterStatus);
	}

}
