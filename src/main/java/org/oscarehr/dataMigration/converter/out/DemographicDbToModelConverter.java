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
		exportDemographic.setHealthNumberProvinceCode(findRegionCodeValue(StringUtils.trimToNull(input.getHcType())));
		exportDemographic.setHealthNumberCountryCode(findCountryCodeValue(StringUtils.trimToNull(input.getHcType()), COUNTRY_CODE_CANADA));
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
		exportDemographic.addAddress(buildAddress(input));

		Address alternateAddress = buildAlternativeAddress(input);
		if (alternateAddress != null)
		{
			exportDemographic.addAddress(alternateAddress);
		}

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

	protected Address buildAddress(Demographic input)
	{
		Address address = new Address();
		address.setAddressLine1(StringUtils.trimToNull(input.getAddress()));
		address.setCity(StringUtils.trimToNull(input.getCity()));

		// non canadian regions (ie US states) are stored in the province field like 'US-NY' etc.
		String provinceCode = StringUtils.trimToNull(input.getProvince());
		address.setRegionCode(findRegionCodeValue(provinceCode));
		address.setCountryCode(findCountryCodeValue(provinceCode, COUNTRY_CODE_CANADA));
		address.setPostalCode(StringUtils.deleteWhitespace(input.getPostal()));
		address.setResidencyStatusCurrent();

		return address;
	}
	protected Address buildAlternativeAddress(Demographic input)
	{
		DemographicExt altAddressExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_ADDRESS);
		DemographicExt altCityExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_CITY);
		DemographicExt altProvinceExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_PROVINCE);
		DemographicExt altPostalExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_POSTAL);

		Address address = new Address();
		if (altAddressExt != null)
		{
			address.setAddressLine1(altAddressExt.getValue());
		}
		if (altCityExt != null)
		{
			address.setCity(altCityExt.getValue());
		}
		if (altProvinceExt != null)
		{
			address.setRegionCode(findRegionCodeValue(altProvinceExt.getValue()));
			address.setCountryCode(findCountryCodeValue(altProvinceExt.getValue(), COUNTRY_CODE_CANADA));
		}
		if (altPostalExt != null)
		{
			address.setPostalCode(StringUtils.deleteWhitespace(altPostalExt.getValue()));
		}

		if (address.getAddressLine1() == null && address.getCity() == null && address.getRegionCode() == null
			&& address.getCountryCode() == null && address.getPostalCode() == null)
		{
			return null;
		}
		address.setResidencyStatusPast();
		return address;
	}

	/**
	 * parse out the province code from the province value
	 * @param provinceCode to be parsed
	 * @return the province code
	 */
	private String findRegionCodeValue(String provinceCode)
	{
		if(provinceCode != null && provinceCode.contains("-"))
		{
			String[] provinceCodeSplit = provinceCode.split("-");
			return provinceCodeSplit[1];
		}
		else
		{
			return provinceCode;
		}
	}

	/**
	 * parse out the country code from the province value, or return the default.
	 * @param provinceCode to be parsed
	 * @param defaultCountry to be used in case province code has no country code part
	 * @return the country code
	 */
	private String findCountryCodeValue(String provinceCode, String defaultCountry)
	{
		if(provinceCode != null && provinceCode.contains("-"))
		{
			String[] provinceCodeSplit = provinceCode.split("-");
			return provinceCodeSplit[0];
		}
		else
		{
			return defaultCountry;
		}
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
