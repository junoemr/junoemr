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

import org.oscarehr.common.model.PharmacyInfo;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.Date;

@Component
public class PharmacyModelToDbConverter extends
		BaseDbToModelConverter<Pharmacy, PharmacyInfo>
{
	@Override
	public PharmacyInfo convert(Pharmacy input)
	{
		if(input == null)
		{
			return null;
		}
		PharmacyInfo pharmacyInfo = new PharmacyInfo();

		pharmacyInfo.setId(input.getId());
		pharmacyInfo.setName(input.getName());
		pharmacyInfo.setEmail(input.getEmail());
		pharmacyInfo.setNotes(input.getNotes());
		pharmacyInfo.setServiceLocationIdentifier(input.getServiceLocationIdentifier());
		pharmacyInfo.setStatus(input.isStatusDeleted() ? PharmacyInfo.DELETED : PharmacyInfo.ACTIVE);

		Date addDate = ConversionUtils.toNullableLegacyDateTime(input.getCreatedDateTime());
		if(addDate == null)
		{
			addDate = new Date();
		}
		pharmacyInfo.setAddDate(addDate);

		Address address = input.getAddress();

		if(address != null)
		{
			pharmacyInfo.setAddress(address.getAddressLinesString());
			pharmacyInfo.setCity(address.getCity());
			pharmacyInfo.setProvince(address.getRegionCode());
			pharmacyInfo.setPostalCode(address.getPostalCode());
		}

		PhoneNumber phone1 = input.getPhone1();
		PhoneNumber phone2 = input.getPhone2();
		PhoneNumber fax = input.getFax();

		if(phone1 != null)
		{
			pharmacyInfo.setPhone1(phone1.getNumber());
		}
		if(phone2 != null)
		{
			pharmacyInfo.setPhone2(phone2.getNumber());
		}
		if(fax != null)
		{
			pharmacyInfo.setFax(fax.getNumber());
		}

		return pharmacyInfo;
	}
}
