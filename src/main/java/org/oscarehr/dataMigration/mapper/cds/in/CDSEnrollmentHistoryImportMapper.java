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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographicRoster.entity.RosterTerminationReason;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.Demographics;

import javax.xml.datatype.XMLGregorianCalendar;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.ENROLLMENT_STATUS_TRUE;

@Component
public class CDSEnrollmentHistoryImportMapper extends AbstractCDSImportMapper<Demographics.Enrolment.EnrolmentHistory, RosterData>
{
	@Override
	public RosterData importToJuno(Demographics.Enrolment.EnrolmentHistory importStructure) throws Exception
	{
		if(importStructure == null)
		{
			return null;
		}
		RosterData rosterData = new RosterData();

		rosterData.setRostered(ENROLLMENT_STATUS_TRUE.equals(importStructure.getEnrollmentStatus()));

		XMLGregorianCalendar enrollmentDateTime = importStructure.getEnrollmentDate();
		if(enrollmentDateTime != null)
		{
			enrollmentDateTime.setTime(0, 0, 0);
			rosterData.setRosterDateTime(ConversionUtils.toNullableLocalDateTime(enrollmentDateTime));
		}

		XMLGregorianCalendar terminationDateTime = importStructure.getEnrollmentTerminationDate();
		if(terminationDateTime != null)
		{
			terminationDateTime.setTime(0, 0, 0);
			rosterData.setTerminationDateTime(ConversionUtils.toNullableLocalDateTime(terminationDateTime));
		}

		String terminationReason = importStructure.getTerminationReason();
		if(terminationReason != null)
		{
			// this must be an integer (defined in CT-010) to meet cds requirements
			Integer terminationReasonCode = Integer.parseInt(terminationReason);
			rosterData.setTerminationReason(RosterTerminationReason.getByCode(terminationReasonCode));
		}

		Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician enrolledPhysician = importStructure.getEnrolledToPhysician();
		if(enrolledPhysician != null)
		{
			ProviderModel provider = toProvider(enrolledPhysician.getName());
			provider.setOhipNumber(enrolledPhysician.getOHIPPhysicianId());
			rosterData.setRosterProvider(provider);
		}

		return rosterData;
	}
}
