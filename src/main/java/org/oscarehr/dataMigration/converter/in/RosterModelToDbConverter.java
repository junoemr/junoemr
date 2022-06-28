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
package org.oscarehr.dataMigration.converter.in;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographicRoster.entity.DemographicRoster;
import org.oscarehr.rosterStatus.dao.RosterStatusDao;
import org.oscarehr.rosterStatus.entity.RosterStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.oscarehr.rosterStatus.entity.RosterStatus.ROSTER_STATUS_ROSTERED;
import static org.oscarehr.rosterStatus.entity.RosterStatus.ROSTER_STATUS_TERMINATED;

@Component
public class RosterModelToDbConverter extends BaseDbToModelConverter<RosterData, DemographicRoster>
{
	@Autowired
	private RosterStatusDao rosterStatusDao;

	@Override
	public DemographicRoster convert(RosterData input)
	{
		if(input == null)
		{
			return null;
		}

		DemographicRoster demographicRoster = new DemographicRoster();
		demographicRoster.setId(input.getId());
		demographicRoster.setRosterStatus(getRosterStatus(input.isRostered()));
		demographicRoster.setRosterDate(input.getRosterDateTime());
		demographicRoster.setRosterTerminationDate(input.getTerminationDateTime());
		demographicRoster.setRosterTerminationReason(input.getTerminationReason());

		ProviderModel rosterProvider = input.getRosterProvider();
		if(rosterProvider != null)
		{
			demographicRoster.setRosteredPhysician(
					StringUtils.trimToNull(String.join(",", rosterProvider.getLastName(), rosterProvider.getFirstName()))
			);
			demographicRoster.setOhipNo(rosterProvider.getOhipNumber());
		}

		return demographicRoster;
	}

	private RosterStatus getRosterStatus(boolean rostered)
	{
		String status = rostered ? ROSTER_STATUS_ROSTERED : ROSTER_STATUS_TERMINATED;
		return rosterStatusDao.findByStatus(status);
	}
}
