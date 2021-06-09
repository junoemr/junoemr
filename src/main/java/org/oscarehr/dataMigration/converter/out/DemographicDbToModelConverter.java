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
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;
import static org.oscarehr.dataMigration.model.demographic.Demographic.OFFICIAL_LANGUAGE;

@Component
public class DemographicDbToModelConverter extends
		BaseDbToModelConverter<Demographic, org.oscarehr.dataMigration.model.demographic.Demographic>
{
	@Autowired
	private DemographicExtDao demographicExtDao;

	@Autowired
	private RosterDbToModelConverter rosterDbToModelConverter;

	@Override
	public org.oscarehr.dataMigration.model.demographic.Demographic convert(Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		org.oscarehr.dataMigration.model.demographic.Demographic exportDemographic = new org.oscarehr.dataMigration.model.demographic.Demographic();
		BeanUtils.copyProperties(input, exportDemographic, "address", "email", "dateOfBirth", "title", "sin", "officialLanguage");

		exportDemographic.setId(input.getDemographicId());
		exportDemographic.setDateOfBirth(input.getDateOfBirth());
		exportDemographic.setTitle(Person.TITLE.fromStringIgnoreCase(input.getTitle()));
		exportDemographic.setSex(Person.SEX.getIgnoreCase(input.getSex()));
		exportDemographic.setSin(numericSin(input.getSin()));
		exportDemographic.setEmail(StringUtils.trimToNull(input.getEmail()));

		exportDemographic.setHealthNumber(StringUtils.trimToNull(input.getHin()));
		exportDemographic.setHealthNumberVersion(StringUtils.trimToNull(input.getVer()));
		exportDemographic.setHealthNumberProvinceCode(StringUtils.trimToNull(input.getHcType()));
		exportDemographic.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(input.getHcRenewDate()));
		exportDemographic.setHealthNumberEffectiveDate(ConversionUtils.toNullableLocalDate(input.getHcEffectiveDate()));
		exportDemographic.setDateJoined(ConversionUtils.toNullableLocalDate(input.getDateJoined()));
		exportDemographic.setDateEnded(ConversionUtils.toNullableLocalDate(input.getEndDate()));
		exportDemographic.setChartNumber(StringUtils.trimToNull(input.getChartNo()));
		exportDemographic.setRosterHistory(rosterDbToModelConverter.convert(input.getRosterHistory()));
		exportDemographic.setMrpProvider(findProvider(input.getProviderNo()));
		exportDemographic.setReferralDoctor(getReferralProvider(input));
		exportDemographic.setFamilyDoctor(getFamilyProvider(input));
		exportDemographic.setPatientStatusDate(ConversionUtils.toNullableLocalDate(input.getPatientStatusDate()));
		exportDemographic.setOfficialLanguage(OFFICIAL_LANGUAGE.fromValueString(input.getOfficialLanguage()));

		Address address = new Address();
		address.setAddressLine1(StringUtils.trimToNull(input.getAddress()));
		address.setCity(StringUtils.trimToNull(input.getCity()));
		address.setRegionCode(StringUtils.trimToNull(input.getProvince()));
		address.setCountryCode(COUNTRY_CODE_CANADA);
		address.setPostalCode(StringUtils.deleteWhitespace(input.getPostal()));
		address.setResidencyStatusCurrent();
		exportDemographic.addAddress(address);

		// phone conversions
		if(input.getPhone() != null)
		{
			DemographicExt homePhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_H_PHONE_EXT);
			String homePhoneExtension = (homePhoneExtensionExt != null) ? StringUtils.trimToNull(homePhoneExtensionExt.getValue()) : null;
			exportDemographic.setHomePhone(buildPhoneNumber(input.getPhone(), homePhoneExtension));
		}
		if(input.getPhone2() != null)
		{
			DemographicExt workPhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_W_PHONE_EXT);
			String workPhoneExtension = (workPhoneExtensionExt != null) ? StringUtils.trimToNull(workPhoneExtensionExt.getValue()) : null;
			exportDemographic.setWorkPhone(buildPhoneNumber(input.getPhone2(), workPhoneExtension));
		}

		DemographicExt cellNoExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_CELL);
		String cellPhoneNumber = (cellNoExt != null) ? StringUtils.trimToNull(cellNoExt.getValue()) : null;
		if(cellPhoneNumber != null)
		{
			exportDemographic.setCellPhone(buildPhoneNumber(cellPhoneNumber, null));
		}

		DemographicCust demographicCustom = input.getDemographicCust();
		if(demographicCustom != null)
		{
			exportDemographic.setPatientNote(StringUtils.trimToNull(demographicCustom.getParsedNotes()));
			exportDemographic.setPatientAlert(StringUtils.trimToNull(demographicCustom.getAlert()));
			//TODO midwife/nurse,resident providers ?
		}
		return exportDemographic;
	}

	private PhoneNumber buildPhoneNumber(String phoneNumber, String extension)
	{
		boolean primaryPhone = phoneNumber.endsWith("*");
		return PhoneNumber.of(phoneNumber, extension, primaryPhone);
	}

	private Provider getReferralProvider(Demographic input)
	{
		return getProviderFromString(input.getReferralDoctorName(), input.getReferralDoctorNumber());
	}

	private Provider getFamilyProvider(Demographic input)
	{
		return getProviderFromString(input.getFamilyDoctorName(), input.getFamilyDoctorNumber());
	}

	private String numericSin(String unformatted)
	{
		return (unformatted == null) ? null : unformatted.replaceAll("[^0-9]", "");
	}
}
