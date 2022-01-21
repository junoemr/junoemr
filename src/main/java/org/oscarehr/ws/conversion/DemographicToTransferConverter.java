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
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.ws.rest.conversion.DemographicExtConverter;
import org.oscarehr.ws.rest.to.model.AddressTo1;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DemographicToTransferConverter extends AbstractModelConverter<Demographic, DemographicTo1>
{
	@Autowired
	DemographicExtConverter demographicExtConverter;

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
				"hcEffectiveDate",
				"phone2",
				"address"
		};

		DemographicTo1 transfer = new DemographicTo1();
		BeanUtils.copyProperties(demographic, transfer, ignoreProperties);

		transfer.setDemographicNo(demographic.getDemographicId());
		transfer.setDobDay(demographic.getDayOfBirth());
		transfer.setDobMonth(demographic.getMonthOfBirth());
		transfer.setDobYear(demographic.getYearOfBirth());
		transfer.setFamilyDoctor(demographic.getReferralDoctor());
		transfer.setFamilyDoctor2(demographic.getFamilyDoctor());
		transfer.setEffDate(demographic.getHcEffectiveDate());
		transfer.setAlternativePhone(demographic.getPhone2());

		// address is a nested object for reasons
		AddressTo1 address = new AddressTo1();
		address.setAddress(demographic.getAddress());
		address.setProvince(demographic.getProvince());
		address.setCity(demographic.getCity());
		address.setPostal(demographic.getPostal());

		transfer.setExtras(demographic.getDemographicExtSet().stream()
				.map(demographicExt -> demographicExtConverter.getAsTransferObject(null, demographicExt))
				.collect(Collectors.toList())
		);

		return transfer;
	}
}
