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
package org.oscarehr.demographic.converter;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographic.transfer.DemographicUpdateInput;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_USA;
import static org.oscarehr.provider.model.ProviderData.SYSTEM_PROVIDER_NO;

@Component
public class DemographicUpdateInputToEntityConverter
		extends BaseModelToDbConverter<DemographicUpdateInput, Demographic>
{
	@Override
	public Demographic convert(DemographicUpdateInput input)
	{
		if(input == null)
		{
			return null;
		}

		Demographic dbDemographic = new Demographic();
		BeanUtils.copyProperties(input, dbDemographic, "dateOfBirth", "title", "sex", "officialLanguage");

		dbDemographic.setDemographicId(input.getId());
		dbDemographic.setDateOfBirth(input.getDateOfBirth());
		dbDemographic.setSex(Optional.ofNullable(input.getSex()).map(Person.SEX::getValue).orElse(null));
		dbDemographic.setTitle(Optional.ofNullable(input.getTitle()).map(Person.TITLE::name).orElse(null));
		dbDemographic.setHin(input.getHealthNumber());
		dbDemographic.setVer(input.getHealthNumberVersion());
		dbDemographic.setHcType(getProvinceCode(input.getHealthNumberProvinceCode(), input.getHealthNumberCountryCode()));
		dbDemographic.setHcRenewDate(ConversionUtils.toNullableLegacyDate(input.getHealthNumberRenewDate()));
		dbDemographic.setHcEffectiveDate(ConversionUtils.toNullableLegacyDate(input.getHealthNumberEffectiveDate()));
		dbDemographic.setDateJoined(ConversionUtils.toNullableLegacyDate(input.getDateJoined()));
		dbDemographic.setEndDate(ConversionUtils.toNullableLegacyDate(input.getDateEnded()));
		dbDemographic.setChartNo(input.getChartNumber());
		dbDemographic.setOfficialLanguage(Optional.ofNullable(input.getOfficialLanguage()).map(DemographicModel.OFFICIAL_LANGUAGE::getValue).orElse(null));

		ProviderData dbProvider = findOrCreateProviderRecord(input.getMrpProvider(), true);
		if(dbProvider != null)
		{
			dbDemographic.setProviderNo(dbProvider.getId());
			dbDemographic.setProvider(dbProvider);
		}

		dbDemographic.setPatientStatusDate(ConversionUtils.toNullableLegacyDate(input.getPatientStatusDate()));
		dbDemographic.setLastUpdateUser(SYSTEM_PROVIDER_NO);

		Set<DemographicExt> demographicExtSet = new HashSet<>();

		List<AddressModel> addressList = input.getAddressList();
		for(AddressModel address : addressList)
		{
			// If address is marked as current address then it's the primary address
			// Otherwise it's an alternative address.
			if(address.isCurrentAddress())
			{
				dbDemographic.setAddress(address.getAddressLinesString());
				dbDemographic.setCity(address.getCity());
				dbDemographic.setProvince(getProvinceCode(address.getRegionCode(), address.getCountryCode()));
				dbDemographic.setPostal(address.getPostalCode());
			}
			else
			{
				DemographicExt altAddress = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
						DemographicExt.ALTERNATE_ADDRESS, address.getAddressLinesString());
				DemographicExt altCity = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
						DemographicExt.ALTERNATE_CITY, address.getCity());
				DemographicExt altProvince = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
						DemographicExt.ALTERNATE_PROVINCE, getProvinceCode(address.getRegionCode(), address.getCountryCode()));
				DemographicExt altPostal = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
						DemographicExt.ALTERNATE_POSTAL, address.getPostalCode());
				demographicExtSet.add(altAddress);
				demographicExtSet.add(altCity);
				demographicExtSet.add(altProvince);
				demographicExtSet.add(altPostal);
			}
		}

		// phone conversions
		PhoneNumberModel homePhone = input.getHomePhone();
		if(homePhone != null)
		{
			dbDemographic.setPhone(homePhone.getNumber());

			String extension = homePhone.getExtension();
			if(extension != null)
			{
				DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_H_PHONE_EXT, extension);
				demographicExtSet.add(ext);
			}
		}

		PhoneNumberModel workPhone = input.getWorkPhone();
		if(workPhone != null)
		{
			dbDemographic.setPhone2(workPhone.getNumber());

			String extension = workPhone.getExtension();
			if(extension != null)
			{
				DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(), DemographicExt.KEY_DEMO_W_PHONE_EXT, extension);
				demographicExtSet.add(ext);
			}
		}

		PhoneNumberModel cellPhone = input.getCellPhone();
		if(cellPhone != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_DEMO_CELL, cellPhone.getNumber());
			demographicExtSet.add(ext);
		}

		if(input.getAboriginal() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_ABORIGINAL, input.getAboriginal() ? "Yes" : "No");
			demographicExtSet.add(ext);
		}
		if(input.getUsSigned() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_US_SIGNED, input.getUsSigned());
			demographicExtSet.add(ext);
		}
		if(input.getPrivacyConsent() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_PRIVACY_CONSENT, input.getPrivacyConsent());
			demographicExtSet.add(ext);
		}
		if(input.getInformedConsent() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_INFORMED_CONSENT, input.getInformedConsent());
			demographicExtSet.add(ext);
		}
		if(input.getPaperChartArchived() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_PAPER_CHART_ARCHIVED, input.getPaperChartArchived() ? "YES" : "NO");
			demographicExtSet.add(ext);
		}
		if(input.getPaperChartArchivedDate() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_PAPER_CHART_ARCHIVED_DATE,
					ConversionUtils.toDateString(input.getPaperChartArchivedDate()));
			demographicExtSet.add(ext);
		}
		if(input.getSecurityQuestion1() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_SECURITY_QUESTION_1, input.getSecurityQuestion1());
			demographicExtSet.add(ext);
		}
		if(input.getSecurityAnswer1() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_SECURITY_ANSWER_1, input.getSecurityAnswer1());
			demographicExtSet.add(ext);
		}
		if(input.getRxInteractionWarningLevel() != null)
		{
			DemographicExt ext = new DemographicExt(SYSTEM_PROVIDER_NO, input.getId(),
					DemographicExt.KEY_RX_INTERACTION_WARNING_LEVEL, input.getRxInteractionWarningLevel());
			demographicExtSet.add(ext);
		}


		dbDemographic.setDemographicExtSet(demographicExtSet);

		DemographicCust demographicCust = Optional.ofNullable(dbDemographic.getDemographicCust()).orElse(new DemographicCust());
		demographicCust.setParsedNotes(input.getPatientNote());
		demographicCust.setAlert(input.getPatientAlert());
		demographicCust.setDemographic(dbDemographic);

		// referral doc and family doc are not real providers and need to be handled differently from regular provider lookups
		Provider referralDoc = input.getReferralDoctor();
		if(referralDoc != null)
		{
			dbDemographic.setReferralDoctor("<rdohip>" + StringUtils.trimToEmpty(referralDoc.getOhipNumber()) + "</rdohip><rd>"
					+ StringUtils.trimToEmpty(referralDoc.getLastName()) + ", "
					+ StringUtils.trimToEmpty(referralDoc.getFirstName()) + "</rd>");
		}

		Provider familyDoc = input.getFamilyDoctor();
		if(familyDoc != null)
		{
			dbDemographic.setFamilyDoctor("<fd>" + StringUtils.trimToEmpty(familyDoc.getOhipNumber()) + "</fd><fdname>"
					+ StringUtils.trimToEmpty(familyDoc.getLastName()) + ", "
					+ StringUtils.trimToEmpty(familyDoc.getFirstName()) + "</fdname>");
		}

		return dbDemographic;
	}

	/** juno stores non Canadian regions (us states) with the country coe (ie US-NY). This method formats the province code based on country
	 * @param provinceCode the province code
	 * @param countryCode the country code
	 * @return province code that should be saved
	 */
	private String getProvinceCode(String provinceCode, String countryCode)
	{
		if(COUNTRY_CODE_USA.equals(countryCode))
		{
			return countryCode + "-" + provinceCode;
		}
		else
		{
			return provinceCode;
		}
	}
}
