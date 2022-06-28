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

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeComparator;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographicRoster.dao.DemographicRosterDao;
import org.oscarehr.demographicRoster.entity.DemographicRoster;
import org.oscarehr.demographicRoster.entity.RosterTerminationReason;
import org.oscarehr.demographicRoster.model.DemographicRosterModel;
import org.oscarehr.rosterStatus.entity.RosterStatus;
import org.oscarehr.rosterStatus.service.RosterStatusService;
import org.oscarehr.demographicRoster.converter.DemographicRosterToModelConverter;
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
	protected DemographicRosterDao demographicRosterDao;

	@Autowired
	protected RosterStatusService rosterStatusService;

	@Autowired
	protected DemographicRosterToModelConverter demographicRosterToModelConverter;


	/**
	 * Creates a new roster history entry for a given demographic.
	 * should be used for new demographic creation only
	 * @param currentDemo current revision of the demographic we want to record
	 * @return optional new roster model
	 */
	public Optional<DemographicRosterModel> addRosterHistoryEntry(Demographic currentDemo)
	{
		if (StringUtils.isNotBlank(currentDemo.getRosterStatus()))
		{
			return Optional.ofNullable(demographicRosterToModelConverter.convert(saveRosterHistory(currentDemo)));
		}
		return Optional.empty();
	}

	/**
	 * Creates a new roster history entry for a given demographic.
	 * Only records changes if there is a difference between the two for any of the roster/enrollment fields.
	 * @param currentDemo current revision of the demographic we want to record
	 * @param previousDemo previous version of the demographic
	 * @return optional new roster model
	 */
	public Optional<DemographicRosterModel> addRosterHistoryEntry(Demographic currentDemo, Demographic previousDemo)
	{
		boolean hasChanged = false;

		// If the roster status is valid, check if any fields changed from last time we edited
		if (StringUtils.isNotBlank(currentDemo.getRosterStatus()))
		{
			DateTimeComparator dateComparator = DateTimeComparator.getDateOnlyInstance();

			hasChanged = currentDemo.getFamilyDoctor() != null && !currentDemo.getFamilyDoctor().equals(previousDemo.getFamilyDoctor());
			hasChanged |= currentDemo.getRosterDate() != null && dateComparator.compare(currentDemo.getRosterDate(), previousDemo.getRosterDate()) != 0;
			hasChanged |= currentDemo.getRosterStatus() != null && !currentDemo.getRosterStatus().equals(previousDemo.getRosterStatus());
			hasChanged |= currentDemo.getRosterTerminationDate() != null && dateComparator.compare(currentDemo.getRosterTerminationDate(), previousDemo.getRosterTerminationDate()) != 0;
			hasChanged |= currentDemo.getRosterTerminationReason() != null && !currentDemo.getRosterTerminationReason().equals(previousDemo.getRosterTerminationReason());
		}

		if (hasChanged)
		{
			return Optional.ofNullable(demographicRosterToModelConverter.convert(saveRosterHistory(currentDemo)));
		}
		return Optional.empty();
	}

	/**
	 * Create a demographic roster history entry, given a demographic record.
	 * @param demographic reference that we're recording history for
	 * @return a newly made history entry
	 */
	private DemographicRoster saveRosterHistory(Demographic demographic)
	{
		DemographicRoster demographicRoster = new DemographicRoster();
		demographicRoster.setDemographicId(demographic.getDemographicId());
		demographicRoster.setRosterDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterDate()));

		RosterStatus rosterStatus = rosterStatusService.findByStatus(demographic.getRosterStatus());
		demographicRoster.setRosterStatus(rosterStatus);
		
		if (!rosterStatus.isRostered())
		{
			// Set the date in all non-rostered cases, and the reason only if the status is terminated.  This is due to the front
			// end only displaying the termination reason select on status TE.
			
			if (RosterStatus.ROSTER_STATUS_TERMINATED.equals(rosterStatus.getRosterStatus()))
			{
				demographicRoster.setRosterTerminationDate(ConversionUtils.toNullableLocalDateTime(demographic.getRosterTerminationDate()));
				RosterTerminationReason terminationReason = RosterTerminationReason.getByCode(Integer.parseInt(demographic.getRosterTerminationReason()));
				demographicRoster.setRosterTerminationReason(terminationReason);
			}
		}
		
		demographicRoster.setRosteredPhysician(demographic.getFamilyDoctorName());
		demographicRoster.setOhipNo(demographic.getFamilyDoctorNumber());
			
		demographicRosterDao.persist(demographicRoster);

		return demographicRoster;
	}

	public List<DemographicRosterModel> getRosteredHistory(Integer demographicNo)
	{
		return demographicRosterToModelConverter.convert(demographicRosterDao.findByDemographic(demographicNo));
	}
}
