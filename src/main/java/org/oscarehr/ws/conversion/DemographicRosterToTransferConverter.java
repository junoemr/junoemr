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

package org.oscarehr.ws.conversion;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.demographicRoster.model.DemographicRoster;
import org.oscarehr.demographicRoster.transfer.DemographicRosterTransfer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemographicRosterToTransferConverter extends AbstractModelConverter<DemographicRoster, DemographicRosterTransfer>
{
	@Autowired
	RosterStatusToTransferConverter rosterStatusToTransferConverter;

	@Override
	public DemographicRosterTransfer convert(DemographicRoster demographicRoster)
	{
		if (demographicRoster == null)
		{
			return null;
		}

		DemographicRosterTransfer transfer = new DemographicRosterTransfer();
		BeanUtils.copyProperties(demographicRoster, transfer, "rosterStatus");

		transfer.setRosterStatus(rosterStatusToTransferConverter.convert(demographicRoster.getRosterStatus()));
		if (demographicRoster.getRosterTerminationReason() != null)
		{
			transfer.setRosterTerminationDescription(demographicRoster.getRosterTerminationReason().getDescription());
		}

		return transfer;
	}
}