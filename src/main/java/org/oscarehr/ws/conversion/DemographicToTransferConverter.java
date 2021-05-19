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
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class DemographicToTransferConverter extends AbstractModelConverter<Demographic, DemographicTo1>
{
	@Override
	public DemographicTo1 convert(Demographic demographic)
	{
		if (demographic == null)
		{
			return null;
		}

		String[] ignoreProperties = {
				"demographicId",
				"dayOfBirth",
				"monthOfBirth",
				"yearOfBirth",
				"referralDoctor",
				"familyDoctor",
		};

		DemographicTo1 transfer = new DemographicTo1();
		BeanUtils.copyProperties(demographic, transfer, ignoreProperties);

		transfer.setDemographicNo(demographic.getDemographicId());
		transfer.setDobDay(demographic.getDayOfBirth());
		transfer.setDobMonth(demographic.getMonthOfBirth());
		transfer.setDobYear(demographic.getYearOfBirth());
		transfer.setFamilyDoctor(demographic.getReferralDoctor());
		transfer.setFamilyDoctor2(demographic.getFamilyDoctor());

		return transfer;
	}
}
