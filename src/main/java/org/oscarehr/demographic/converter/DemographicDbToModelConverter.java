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

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.converter.out.RosterDbToModelConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographic.entity.DemographicCust;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.demographic.model.DemographicModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.Optional;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;
import static org.oscarehr.demographic.model.DemographicModel.OFFICIAL_LANGUAGE;

@Component
public class DemographicDbToModelConverter extends
		BaseDbToModelConverter<Demographic, DemographicModel>
{
	@Autowired
	private DemographicExtDao demographicExtDao;

	@Autowired
	private RosterDbToModelConverter rosterDbToModelConverter;

	@Override
	public DemographicModel convert(Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		DemographicModel model = new DemographicModel();
		BeanUtils.copyProperties(input, model, "address", "email", "dateOfBirth", "title", "sin", "officialLanguage");

		model.setId(input.getDemographicId());
		model.setDateOfBirth(input.getDateOfBirth());
		model.setTitle(Person.TITLE.fromStringIgnoreCase(input.getTitle()));
		model.setSex(Person.SEX.getIgnoreCase(input.getSex()));
		model.setSin(numericSin(input.getSin()));
		model.setEmail(StringUtils.trimToNull(input.getEmail()));

		model.setHealthNumber(StringUtils.trimToNull(input.getHin()));
		model.setHealthNumberVersion(StringUtils.trimToNull(input.getVer()));
		model.setHealthNumberProvinceCode(AddressModel.parseRegionCodeValue(StringUtils.trimToNull(input.getHcType())));
		model.setHealthNumberCountryCode(AddressModel.parseCountryCodeValue(StringUtils.trimToNull(input.getHcType()), COUNTRY_CODE_CANADA));
		model.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(input.getHcRenewDate()));
		model.setHealthNumberEffectiveDate(ConversionUtils.toNullableLocalDate(input.getHcEffectiveDate()));
		model.setDateJoined(ConversionUtils.toNullableLocalDate(input.getDateJoined()));
		model.setDateEnded(ConversionUtils.toNullableLocalDate(input.getEndDate()));
		model.setChartNumber(StringUtils.trimToNull(input.getChartNo()));
		model.setRosterHistory(rosterDbToModelConverter.convert(input.getRosterHistory()));
		model.setMrpProvider(findProvider(input.getProviderNo()));
		model.setReferralDoctor(getReferralProvider(input));
		model.setFamilyDoctor(getFamilyProvider(input));
		model.setPatientStatusDate(ConversionUtils.toNullableLocalDate(input.getPatientStatusDate()));
		model.setOfficialLanguage(OFFICIAL_LANGUAGE.fromValueString(input.getOfficialLanguage()));
		model.addAddress(buildAddress(input));

		model.setElectronicMessagingConsentStatus(input.getElectronicMessagingConsentStatus());
		model.setElectronicMessagingConsentGivenAt(ConversionUtils.toNullableLocalDate(input.getElectronicMessagingConsentGivenAt()));
		model.setElectronicMessagingConsentRejectedAt(ConversionUtils.toNullableLocalDate(input.getElectronicMessagingConsentRejectedAt()));

		AddressModel alternateAddress = buildAlternativeAddress(input);
		if (alternateAddress != null)
		{
			model.addAddress(alternateAddress);
		}

		// phone conversions
		if(input.getPhone() != null)
		{
			String homePhoneExtension = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_H_PHONE_EXT)
					.map(ext -> StringUtils.trimToNull(ext.getValue())).orElse(null);
			model.setHomePhone(buildPhoneNumber(input.getPhone(), homePhoneExtension, PhoneNumberModel.PHONE_TYPE.HOME));
		}
		if(input.getPhone2() != null)
		{
			String workPhoneExtension = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_W_PHONE_EXT)
					.map(ext -> StringUtils.trimToNull(ext.getValue())).orElse(null);
			model.setWorkPhone(buildPhoneNumber(input.getPhone2(), workPhoneExtension, PhoneNumberModel.PHONE_TYPE.WORK));
		}

		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_CELL)
				.map(ext -> StringUtils.trimToNull(ext.getValue())).ifPresent(
						cellPhoneNumber -> model.setCellPhone(buildPhoneNumber(cellPhoneNumber, null, PhoneNumberModel.PHONE_TYPE.CELL))
				);

		DemographicCust demographicCustom = input.getDemographicCust();
		if(demographicCustom != null)
		{
			model.setPatientNote(StringUtils.trimToNull(demographicCustom.getParsedNotes()));
			model.setPatientAlert(StringUtils.trimToNull(demographicCustom.getAlert()));
			model.setNurseProvider(findProvider(StringUtils.trimToNull(demographicCustom.getNurse())));
			model.setMidwifeProvider(findProvider(StringUtils.trimToNull(demographicCustom.getMidwife())));
			model.setResidentProvider(findProvider(StringUtils.trimToNull(demographicCustom.getResident())));
		}

		// fill additional EXT entries
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_ABORIGINAL).ifPresent(
				demographicExt -> model.setAboriginal("YES".equalsIgnoreCase(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_CYTOL_NO).ifPresent(
				demographicExt -> model.setCytolNum(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_PAPER_CHART_ARCHIVED).ifPresent(
				demographicExt -> model.setPaperChartArchived("YES".equalsIgnoreCase(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_PAPER_CHART_ARCHIVED_DATE).ifPresent(
				demographicExt -> model.setPaperChartArchivedDate(ConversionUtils.toNullableLocalDate(StringUtils.trimToNull(demographicExt.getValue())))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_US_SIGNED).ifPresent(
				demographicExt -> model.setUsSigned(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_PRIVACY_CONSENT).ifPresent(
				demographicExt -> model.setPrivacyConsent(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_INFORMED_CONSENT).ifPresent(
				demographicExt -> model.setInformedConsent(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_RX_INTERACTION_WARNING_LEVEL).ifPresent(
				demographicExt -> model.setRxInteractionWarningLevel(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_SECURITY_QUESTION_1).ifPresent(
				demographicExt -> model.setSecurityQuestion1(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_SECURITY_ANSWER_1).ifPresent(
				demographicExt -> model.setSecurityAnswer1(StringUtils.trimToNull(demographicExt.getValue()))
		);
		demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_PHONE_COMMENT).ifPresent(
				demographicExt -> model.setPhoneComment(StringUtils.trimToNull(demographicExt.getValue()))
		);

		return model;
	}

	protected AddressModel buildAddress(Demographic input)
	{
		AddressModel address = new AddressModel();
		address.setAddressLine1(StringUtils.trimToNull(input.getAddress()));
		address.setCity(StringUtils.trimToNull(input.getCity()));

		// non canadian regions (ie US states) are stored in the province field like 'US-NY' etc.
		String provinceCode = StringUtils.trimToNull(input.getProvince());
		address.setRegionCode(AddressModel.parseRegionCodeValue(provinceCode));
		address.setCountryCode(AddressModel.parseCountryCodeValue(provinceCode, COUNTRY_CODE_CANADA));
		address.setPostalCode(StringUtils.deleteWhitespace(input.getPostal()));
		address.setResidencyStatusCurrent();

		return address;
	}
	protected AddressModel buildAlternativeAddress(Demographic input)
	{
		Optional<DemographicExt> altAddressExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_ADDRESS);
		Optional<DemographicExt> altCityExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_CITY);
		Optional<DemographicExt> altProvinceExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_PROVINCE);
		Optional<DemographicExt> altPostalExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.ALTERNATE_POSTAL);

		AddressModel address = new AddressModel();
		altAddressExt.ifPresent(demographicExt -> address.setAddressLine1(demographicExt.getValue()));
		altCityExt.ifPresent(demographicExt -> address.setCity(demographicExt.getValue()));
		if (altProvinceExt.isPresent())
		{
			address.setRegionCode(AddressModel.parseRegionCodeValue(altProvinceExt.get().getValue()));
			address.setCountryCode(AddressModel.parseCountryCodeValue(altProvinceExt.get().getValue(), COUNTRY_CODE_CANADA));
		}
		altPostalExt.ifPresent(demographicExt -> address.setPostalCode(StringUtils.deleteWhitespace(demographicExt.getValue())));

		if (address.getAddressLine1() == null && address.getCity() == null && address.getRegionCode() == null
			&& address.getCountryCode() == null && address.getPostalCode() == null)
		{
			return null;
		}
		address.setResidencyStatusPast();
		return address;
	}

	private PhoneNumberModel buildPhoneNumber(String phoneNumber, String extension, PhoneNumberModel.PHONE_TYPE type)
	{
		boolean primaryPhone = phoneNumber.endsWith("*");
		return PhoneNumberModel.of(phoneNumber, extension, primaryPhone, type);
	}

	private ProviderModel getReferralProvider(Demographic input)
	{
		return getProviderFromString(input.getReferralDoctorName(), input.getReferralDoctorNumber());
	}

	private ProviderModel getFamilyProvider(Demographic input)
	{
		return getProviderFromString(input.getFamilyDoctorName(), input.getFamilyDoctorNumber());
	}

	private String numericSin(String unformatted)
	{
		return (unformatted == null) ? null : unformatted.replaceAll("[^0-9]", "");
	}
}
