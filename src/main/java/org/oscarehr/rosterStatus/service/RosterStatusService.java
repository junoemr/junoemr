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

import org.oscarehr.rosterStatus.dao.RosterStatusDao;
import org.oscarehr.rosterStatus.model.RosterStatus;
import org.oscarehr.rosterStatus.transfer.RosterStatusTransfer;
import org.oscarehr.ws.conversion.RosterStatusToDomainConverter;
import org.oscarehr.ws.conversion.RosterStatusToTransferConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RosterStatusService
{
	@Autowired
	RosterStatusDao rosterStatusDao;

	@Autowired
	RosterStatusToTransferConverter rosterStatusToTransferConverter;

	@Autowired
	RosterStatusToDomainConverter rosterStatusToDomainConverter;

	public Optional<RosterStatus> findByStatus(String status)
	{
		return rosterStatusDao.findAll()
				.stream()
				.filter(rosterStatus -> rosterStatus.getRosterStatus().equals(status))
				.findFirst();
	}

	public List<RosterStatusTransfer> getRosterStatusList(Boolean active)
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
		return rosterStatusToTransferConverter.convert(statuses);
	}

	public List<RosterStatusTransfer> getActiveRosterStatusList()
	{
		List<RosterStatus> statuses = rosterStatusDao.findAll();
		return rosterStatusToTransferConverter.convert(statuses.stream()
				.filter(status -> status.getDeletedAt() == null)
				.collect(Collectors.toList()));
	}

	public RosterStatusTransfer addStatus(RosterStatusTransfer statusTransfer, String providerNo)
	{
		RosterStatus rosterStatus = rosterStatusToDomainConverter.convert(statusTransfer);
		rosterStatus.setCreatedAt(LocalDateTime.now());
		rosterStatus.setUpdatedAt(LocalDateTime.now());

		rosterStatus.setUpdatedBy(providerNo);
		rosterStatusDao.persist(rosterStatus);

		return rosterStatusToTransferConverter.convert(rosterStatus);
	}

	public RosterStatusTransfer editStatus(RosterStatusTransfer statusTransfer, String editingProvider)
	{
		RosterStatus rosterStatus = rosterStatusToDomainConverter.convert(statusTransfer);
		rosterStatus.setUpdatedAt(LocalDateTime.now());
		rosterStatus.setUpdatedBy(editingProvider);

		rosterStatusDao.merge(rosterStatus);

		return rosterStatusToTransferConverter.convert(rosterStatus);
	}

}
