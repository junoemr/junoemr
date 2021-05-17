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

package org.oscarehr.demographicRoster.transfer;

import lombok.Data;
import org.oscarehr.demographicRoster.model.DemographicRoster;
import org.oscarehr.rosterStatus.transfer.RosterStatusTransfer;

import java.time.LocalDateTime;

@Data
public class DemographicRosterTransfer
{
	private Integer id;
	private Integer demographicId;
	private String rosteredPhysician;
	private String ohipNo;
	private LocalDateTime rosterDate;
	private LocalDateTime rosterTerminationDate;
	private DemographicRoster.ROSTER_TERMINATION_REASON rosterTerminationReason;
	// extra field to help communicate mapping between enum and description
	private String rosterTerminationDescription;
	private RosterStatusTransfer rosterStatus;
	private LocalDateTime addedAt;
}
