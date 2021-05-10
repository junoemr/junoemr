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

package org.oscarehr.demographicRoster.service;

import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographicRoster.dao.DemographicRosterDao;
import org.oscarehr.demographicRoster.model.DemographicRoster;
import org.oscarehr.rosterStatus.model.RosterStatus;
import org.oscarehr.rosterStatus.service.RosterStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.ConversionUtils;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DemographicRosterService
{
	@Autowired
	DemographicRosterDao demographicRosterDao;

	@Autowired
	RosterStatusService rosterStatusService;

	/**
	 * Create a demographic roster history entry, given a demographic record.
	 * @param demographic reference that we're recording history for
	 * @return a newly made history entry
	 */
	public DemographicRoster saveRosterHistory(Demographic demographic)
	{
		DemographicRoster demographicRoster = new DemographicRoster();
		demographicRoster.setDemographicNo(demographic.getDemographicId());
		// We are assuming that MRP at the time of roster status existing is who the rostered provider was
		demographicRoster.setProviderNo(demographic.getProviderNo());
		demographicRoster.setRosterDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterDate()));

		// Going to take a second look at this
		// Optional implies, well, optional, but we require this
		Optional<RosterStatus> rosterStatus = rosterStatusService.findByStatus(demographic.getRosterStatus());
		rosterStatus.ifPresent(demographicRoster::setRosterStatus);

		// Only set terminated-y fields if it's a status that implies termination
		if (demographicRoster.getRosterStatus().isTerminated())
		{
			demographicRoster.setRosterTerminationDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterTerminationDate()));
			demographicRoster.setRosterTerminationReason(demographic.getRosterTerminationReason());
		}

		demographicRosterDao.persist(demographicRoster);

		return demographicRoster;
	}

	public List<DemographicRoster> getRosteredHistory(Integer demographicNo)
	{
		return demographicRosterDao.getActiveForDemographic(demographicNo);
	}

}
