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
import org.oscarehr.demographicRoster.transfer.DemographicRosterTransfer;
import org.oscarehr.rosterStatus.model.RosterStatus;
import org.oscarehr.rosterStatus.service.RosterStatusService;
import org.oscarehr.ws.conversion.DemographicRosterToTransferConverter;
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

	@Autowired
	DemographicRosterToTransferConverter demographicRosterToTransferConverter;

	/**
	 * Create a demographic roster history entry, given a demographic record.
	 * @param demographic reference that we're recording history for
	 * @return a newly made history entry
	 */
	public DemographicRoster saveRosterHistory(Demographic demographic)
	{
		DemographicRoster demographicRoster = new DemographicRoster();
		demographicRoster.setDemographicId(demographic.getDemographicId());
		demographicRoster.setRosterDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterDate()));

		// Going to take a second look at this
		// Optional implies, well, optional, but we require this
		Optional<RosterStatus> rosterStatus = rosterStatusService.findByStatus(demographic.getRosterStatus());
		rosterStatus.ifPresent(demographicRoster::setRosterStatus);

		// Only set rostered provider if the status is roster-y
		if (demographicRoster.getRosterStatus().isRostered())
		{
			// We are assuming that MRP at the time of roster status existing is who the rostered provider was
			demographicRoster.setRosteredPhysician(demographic.getFamilyDoctorName());
			demographicRoster.setOhipNo(demographic.getFamilyDoctorNumber());
		}

		// Only set terminated-y fields if it's a status that implies termination
		if (demographicRoster.getRosterStatus().isTerminated())
		{
			demographicRoster.setRosterDate(null);
			demographicRoster.setRosterTerminationDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterTerminationDate()));
			DemographicRoster.ROSTER_TERMINATION_REASON terminationReason = DemographicRoster.ROSTER_TERMINATION_REASON.getByCode(
					Integer.parseInt(demographic.getRosterTerminationReason()));
			demographicRoster.setRosterTerminationReason(terminationReason);
		}

		demographicRosterDao.persist(demographicRoster);

		return demographicRoster;
	}

	public List<DemographicRosterTransfer> getRosteredHistory(Integer demographicNo)
	{
		return demographicRosterToTransferConverter.convert(demographicRosterDao.findByDemographic(demographicNo));
	}
}
