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
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.ws.rest.conversion.DemographicExtConverter;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DemographicToDomainConverter extends AbstractModelConverter<DemographicTo1, Demographic>
{
	@Autowired
	DemographicExtConverter demographicExtConverter;

	@Override
	public Demographic convert(DemographicTo1 transfer)
	{
		if (transfer == null)
		{
			return null;
		}

		Demographic demographic = new Demographic();

		String[] ignoreProperties = {
				"demographicNo",
				"dobDay",
				"dobMonth",
				"dobYear",
				"familyDoctor",
				"familyDoctor2",
				"effDate",
				"phone2"
		};

		BeanUtils.copyProperties(transfer, demographic, ignoreProperties);

		demographic.setDemographicId(transfer.getDemographicNo());
		LocalDateTime dateOfBirth = ConversionUtils.toLocalDateTime(transfer.getDateOfBirth());
		demographic.setDayOfBirth(Integer.toString(dateOfBirth.getDayOfMonth()));
		demographic.setMonthOfBirth(Integer.toString(dateOfBirth.getMonthValue()));
		demographic.setYearOfBirth(Integer.toString(dateOfBirth.getYear()));

		if (demographic.getDayOfBirth().length() == 1)
		{
			demographic.setDayOfBirth("0" + demographic.getDayOfBirth());
		}
		// Legacy shenanigans - Juno expects these to be zero-padded
		if (demographic.getMonthOfBirth().length() == 1)
		{
			demographic.setMonthOfBirth("0" + demographic.getMonthOfBirth());
		}

		demographic.setReferralDoctor(transfer.getFamilyDoctor());
		demographic.setFamilyDoctor(transfer.getFamilyDoctor2());
		demographic.setHcEffectiveDate(transfer.getEffDate());
		demographic.setPhone2(transfer.getAlternativePhone());

		demographic.setAddress(transfer.getAddress().getAddress());
		demographic.setCity(transfer.getAddress().getCity());
		demographic.setProvince(transfer.getAddress().getProvince());
		demographic.setPostal(transfer.getAddress().getPostal());

		List<DemographicExt> demographicExtList = transfer.getExtras()
				.stream()
				.map(extra -> demographicExtConverter.getAsDomainObject(null, extra))
				.collect(Collectors.toList());
		// No idea why this has to be done, it's not translating properly
		for (DemographicExt ext : demographicExtList)
		{
			ext.setDemographicNo(demographic.getDemographicId());
		}
		demographic.setDemographicExtList(demographicExtList);

		return demographic;
	}
}
