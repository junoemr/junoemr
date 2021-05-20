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
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.List;

// can't extend the base class because the base uses this converter
@Component
public class ProviderModelToDbConverter extends AbstractModelConverter<Provider, ProviderData>
{
	@Override
	public ProviderData convert(Provider input)
	{
		if(input == null)
		{
			return null;
		}

		ProviderData dbProvider = new ProviderData();
		BeanUtils.copyProperties(input, dbProvider, "addressList", "dob", "title", "sex");

		dbProvider.set(input.getId());
		dbProvider.setDob(ConversionUtils.toNullableLegacyDate(input.getDateOfBirth()));
		dbProvider.setSex(input.getSexString());
		dbProvider.setTitle(input.getTitleString());

		dbProvider.setPractitionerNo(input.getPractitionerNumber());
		dbProvider.setBillingNo(input.getBillingNumber());
		dbProvider.setRmaNo(input.getRmaNumber());
		dbProvider.setHsoNo(input.getHsoNumber());
		dbProvider.setOhipNo(input.getOhipNumber());


		List<Address> addressList = input.getAddressList();
		for(Address address : addressList)
		{
			// TODO how to handle multiple addresses?
			if(address.isCurrentAddress())
			{
				dbProvider.setAddress(StringUtils.trimToNull(
						StringUtils.trimToEmpty(address.getAddressLine1()) + " " +
							StringUtils.trimToEmpty(address.getAddressLine2()) + " " +
							StringUtils.trimToEmpty(address.getRegionCode()) + " " +
							StringUtils.trimToEmpty(address.getCity()) + " " +
							StringUtils.trimToEmpty(address.getCountryCode()) + " " +
							StringUtils.trimToEmpty(address.getPostalCode())
				));
			}
		}

		if(input.getHomePhone() != null)
		{
			dbProvider.setPhone(input.getHomePhone().getNumber());
		}
		if(input.getWorkPhone() != null)
		{
			dbProvider.setWorkPhone(input.getWorkPhone().getNumber());
		}
		if(input.getCellPhone() != null)
		{
//			dbProvider.getCellPhone(input.getCellPhone()); //TODO
		}

		return dbProvider;
	}
}
