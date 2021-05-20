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
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.provider.model.ProviderData.SYSTEM_PROVIDER_NO;

@Component
public class DemographicModelToDbConverter
		extends BaseModelToDbConverter<org.oscarehr.dataMigration.model.demographic.Demographic, Demographic>
{

	@Override
	public Demographic convert(org.oscarehr.dataMigration.model.demographic.Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		Demographic dbDemographic = new Demographic();
		BeanUtils.copyProperties(input, dbDemographic, "dateOfBirth", "title", "sex", "officialLanguage");

		dbDemographic.setDemographicId(input.getId());
		dbDemographic.setDateOfBirth(input.getDateOfBirth());
		dbDemographic.setSex(input.getSexString());
		dbDemographic.setTitle(input.getTitleString());
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
		dbDemographic.setOfficialLanguage((input.getOfficialLanguage() != null) ? input.getOfficialLanguage().getValue() : null);

		ProviderData dbProvider = findOrCreateProviderRecord(input.getMrpProvider(), true);
		if(dbProvider != null)
		{
			dbDemographic.setProviderNo(dbProvider.getId());
			dbDemographic.setProvider(dbProvider);
		}

		dbDemographic.setPatientStatusDate(ConversionUtils.toNullableLegacyDate(input.getPatientStatusDate()));
		dbDemographic.setLastUpdateUser(SYSTEM_PROVIDER_NO);

		List<Address> addressList = input.getAddressList();
		for(Address address : addressList)
		{
			// TODO how to handle multiple addresses?
			if(address.isCurrentAddress())
			{
				dbDemographic.setAddress(address.getAddressLinesString());
				dbDemographic.setCity(address.getCity());
				dbDemographic.setProvince(address.getRegionCode());
				dbDemographic.setPostal(address.getPostalCode());
			}
		}

		List<DemographicExt> demographicExtList = new ArrayList<>();

		// phone conversions
		PhoneNumber homePhone = input.getHomePhone();
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

		PhoneNumber workPhone = input.getWorkPhone();
		if(workPhone != null)
		{
			dbDemographic.setPhone2(workPhone.getNumber());

			String extension = workPhone.getExtension();
			if(extension != null)
			{
				DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_W_PHONE_EXT, extension);
				demographicExtList.add(ext);
			}
		}

		PhoneNumber cellPhone = input.getCellPhone();
		if(cellPhone != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_CELL, cellPhone.getNumber());
			demographicExtList.add(ext);
		}
		dbDemographic.setDemographicExtList(demographicExtList);

		DemographicCust demographicCust = new DemographicCust();
		demographicCust.setParsedNotes(input.getPatientNote());
		demographicCust.setAlert(input.getPatientAlert());
		demographicCust.setDemographic(dbDemographic);
		dbDemographic.setDemographicCust(demographicCust);

		// referral doc and family doc are not real providers and need to be handled differently from regular provider lookups
		//TODO look up in specialists table?
		Provider referralDoc = input.getReferralDoctor();
		if(referralDoc != null)
		{
			dbDemographic.setReferralDoctor("<rdohip>" + StringUtils.trimToEmpty(referralDoc.getOhipNumber()) + "</rdohip><rd>"
					+ StringUtils.trimToEmpty(referralDoc.getLastName()) + ","
					+ StringUtils.trimToEmpty(referralDoc.getFirstName()) + "</rd>");
		}

		Provider familyDoc = input.getFamilyDoctor();
		if(familyDoc != null)
		{
			dbDemographic.setReferralDoctor("<fdname>" + StringUtils.trimToEmpty(familyDoc.getOhipNumber()) + "</fdname><fd>"
					+ StringUtils.trimToEmpty(familyDoc.getLastName()) + ","
					+ StringUtils.trimToEmpty(familyDoc.getFirstName()) + "</fd>");
		}

		return dbDemographic;
	}
}
