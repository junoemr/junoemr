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
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;

// can't extend the base class because the base uses this converter
@Component
public class ProviderDbToModelConverter extends
		AbstractModelConverter<ProviderData, org.oscarehr.dataMigration.model.provider.Provider>
{
	@Override
	public org.oscarehr.dataMigration.model.provider.Provider convert(ProviderData input)
	{
		if(input == null)
		{
			return null;
		}
		org.oscarehr.dataMigration.model.provider.Provider exportProvider = new org.oscarehr.dataMigration.model.provider.Provider();
		BeanUtils.copyProperties(input, exportProvider, "address", "dob", "sex", "title");
		exportProvider.setId(input.getId());

		exportProvider.setDateOfBirth(ConversionUtils.toNullableLocalDate(input.getDob()));
		exportProvider.setSex(Person.SEX.getIgnoreCase(input.getSex()));
		exportProvider.setTitle(Person.TITLE.fromStringIgnoreCase(input.getTitle()));

		AddressModel address = new AddressModel();
		address.setAddressLine1(input.getAddress());
//		address.setCity(input.getCity());
//		address.setRegionCode(input.getProvince());
		address.setCountryCode(COUNTRY_CODE_CANADA);
//		address.setPostalCode(input.getPostal());
		address.setResidencyStatusCurrent();
		exportProvider.addAddress(address);

		exportProvider.setPractitionerNumber(StringUtils.trimToNull(input.getPractitionerNo()));
		exportProvider.setBillingNumber(StringUtils.trimToNull(input.getBillingNo()));
		exportProvider.setRmaNumber(StringUtils.trimToNull(input.getRmaNo()));
		exportProvider.setHsoNumber(StringUtils.trimToNull(input.getHsoNo()));
		exportProvider.setOhipNumber(StringUtils.trimToNull(input.getOhipNo()));

		exportProvider.setHomePhone(PhoneNumberModel.of(input.getPhone()));
		exportProvider.setWorkPhone(PhoneNumberModel.of(input.getWorkPhone()));
		exportProvider.setCellPhone(PhoneNumberModel.of(input.getCellPhone()));

		return exportProvider;
	}
}
