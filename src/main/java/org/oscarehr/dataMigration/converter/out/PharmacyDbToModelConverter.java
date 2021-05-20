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
package org.oscarehr.dataMigration.converter.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class PharmacyDbToModelConverter extends
		BaseDbToModelConverter<PharmacyInfo, Pharmacy>
{
	@Override
	public Pharmacy convert(PharmacyInfo input)
	{
		if(input == null)
		{
			return null;
		}
		Pharmacy pharmacy = new Pharmacy();

		pharmacy.setId(input.getId());
		pharmacy.setName(input.getName());
		pharmacy.setEmail(input.getEmail());
		pharmacy.setNotes(input.getNotes());
		pharmacy.setServiceLocationIdentifier(input.getServiceLocationIdentifier());
		pharmacy.setCreatedDateTime(ConversionUtils.toNullableLocalDateTime(input.getAddDate()));

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode(CDSConstants.COUNTRY_CODE_CANADA);
		address.setPostalCode(input.getPostalCode());
		pharmacy.setAddress(address);

		pharmacy.setPhone1(PhoneNumber.of(StringUtils.trimToNull(input.getPhone1())));
		pharmacy.setPhone2(PhoneNumber.of(StringUtils.trimToNull(input.getPhone2())));
		pharmacy.setFax(PhoneNumber.of(StringUtils.trimToNull(input.getFax())));

		return pharmacy;
	}
}
