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
package org.oscarehr.demographicImport.converter.in;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.PhoneNumber;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.provider.model.ProviderData.SYSTEM_PROVIDER_NO;

@Component
public class DemographicModelToDbConverter
		extends BaseModelToDbConverter<org.oscarehr.demographicImport.model.demographic.Demographic, Demographic>
{

	@Override
	public Demographic convert(org.oscarehr.demographicImport.model.demographic.Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		Demographic dbDemographic = new Demographic();
		BeanUtils.copyProperties(input, dbDemographic, "dateOfBirth", "title");

		dbDemographic.setDemographicId(input.getId());
		dbDemographic.setDateOfBirth(input.getDateOfBirth());
		dbDemographic.setHin(input.getHealthNumber());
		dbDemographic.setVer(input.getHealthNumberVersion());
		dbDemographic.setHcType(input.getHealthNumberProvinceCode());
		dbDemographic.setHcRenewDate(ConversionUtils.toNullableLegacyDate(input.getHealthNumberRenewDate()));
		dbDemographic.setHcEffectiveDate(ConversionUtils.toNullableLegacyDate(input.getHealthNumberEffectiveDate()));
		dbDemographic.setDateJoined(ConversionUtils.toNullableLegacyDate(input.getDateJoined()));
		dbDemographic.setEndDate(ConversionUtils.toNullableLegacyDate(input.getDateEnded()));
		dbDemographic.setChartNo(input.getChartNumber());
		dbDemographic.setRosterDate(ConversionUtils.toNullableLegacyDate(input.getRosterDate()));
		dbDemographic.setRosterTerminationDate(ConversionUtils.toNullableLegacyDate(input.getRosterTerminationDate()));
		dbDemographic.setTitle(input.getTitleString());

		ProviderData dbProvider = findOrCreateProviderRecord(input.getMrpProvider(), true);
		if(dbProvider != null)
		{
			dbDemographic.setProviderNo(dbProvider.getId());
			dbDemographic.setProvider(dbProvider);
		}

		dbDemographic.setPatientStatusDate(ConversionUtils.toNullableLegacyDate(input.getPatientStatusDate()));

		List<Address> addressList = input.getAddressList();
		for(Address address : addressList)
		{
			// TODO how to handle multiple addresses?
			if(address.isCurrentAddress())
			{
				dbDemographic.setAddress(StringUtils.trimToNull(
						StringUtils.trimToEmpty(address.getAddressLine1()) + " " + StringUtils.trimToEmpty(address.getAddressLine2())));
				dbDemographic.setCity(address.getCity());
				dbDemographic.setProvince(address.getRegionCode());
				dbDemographic.setPostal(address.getPostalCode());
			}
		}

		List<DemographicExt> demographicExtList = new ArrayList<>();

		// phone conversions
		PhoneNumber homePhone = input.getHomePhoneNumber();
		if(homePhone != null)
		{
			dbDemographic.setPhone(homePhone.getNumber());

			String extension = homePhone.getExtension();
			if(extension != null)
			{
				DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_H_PHONE_EXT, extension);
				demographicExtList.add(ext);
			}
		}

		PhoneNumber workPhone = input.getWorkPhoneNumber();
		if(workPhone != null)
		{
			dbDemographic.setPhone(workPhone.getNumber());

			String extension = workPhone.getExtension();
			if(extension != null)
			{
				DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_W_PHONE_EXT, extension);
				demographicExtList.add(ext);
			}
		}

		PhoneNumber cellPhone = input.getWorkPhoneNumber();
		if(cellPhone != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_CELL, cellPhone.getNumber());
			demographicExtList.add(ext);
		}

		dbDemographic.setLastUpdateUser(SYSTEM_PROVIDER_NO);
		dbDemographic.setDemographicExtList(demographicExtList);
		return dbDemographic;
	}
}
