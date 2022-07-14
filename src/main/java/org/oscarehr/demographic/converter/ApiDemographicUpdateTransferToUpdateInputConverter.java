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
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.demographic.transfer.DemographicUpdateInput;
import org.oscarehr.demographicRoster.entity.RosterTerminationReason;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.ApiDemographicUpdateTransfer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;

@Component
public class ApiDemographicUpdateTransferToUpdateInputConverter
		extends BaseModelToDbConverter<ApiDemographicUpdateTransfer, DemographicUpdateInput>
{
	@Override
	public DemographicUpdateInput convert(ApiDemographicUpdateTransfer transfer)
	{
		if(transfer == null)
		{
			return null;
		}

		DemographicUpdateInput input = new DemographicUpdateInput();

		input.setId(transfer.getDemographicNo());
		input.setLastName(transfer.getLastName());
		input.setFirstName(transfer.getFirstName());
		input.setTitle(Person.TITLE.fromStringIgnoreCase(transfer.getTitle()));
		input.setSex(Person.SEX.getIgnoreCase(transfer.getSex()));
		input.setHealthNumber(transfer.getHin());
		input.setSin(transfer.getSin());
		input.setHealthNumberProvinceCode(transfer.getHcType());
		input.setHealthNumberVersion(transfer.getHcVersion());
		input.setHealthNumberRenewDate(transfer.getHcRenewDate());
		input.setHealthNumberEffectiveDate(transfer.getHcEffectiveDate());
		input.setDateJoined(transfer.getDateJoined());
		input.setDateEnded(transfer.getEndDate());
		input.setDateOfBirth(transfer.getDateOfBirth());
		input.setPatientStatus(transfer.getPatientStatus());
		input.setPatientStatusDate(transfer.getPatientStatusDate());
		input.setVeteranNumber(transfer.getVeteranNo());

		// contact info
		input.setEmail(transfer.getEmail());

		List<AddressModel> addressList = new ArrayList<>(1);
		if(!StringUtils.isAnyBlank(transfer.getAddress(), transfer.getCity(), transfer.getProvince(), transfer.getPostal()))
		{
			AddressModel address = new AddressModel();
			address.setAddressLine1(transfer.getAddress());
			address.setCity(transfer.getCity());
			address.setRegionCode(AddressModel.parseRegionCodeValue(transfer.getProvince()));
			address.setCountryCode(AddressModel.parseCountryCodeValue(transfer.getProvince(), COUNTRY_CODE_CANADA));
			address.setPostalCode(StringUtils.deleteWhitespace(transfer.getPostal()));
			addressList.add(address);
		}
		input.setAddressList(addressList);

		input.setHomePhone(PhoneNumberModel.of(transfer.getPrimaryPhone()));
		input.setWorkPhone(PhoneNumberModel.of(transfer.getSecondaryPhone()));
		input.setCellPhone(PhoneNumberModel.of(transfer.getCellPhone()));
		input.setPreviousAddress(transfer.getPreviousAddress());
		input.setPcnIndicator(transfer.getPcnIndicator());

		// roster info
		if(!StringUtils.isAnyBlank(transfer.getRosterStatus(), transfer.getRosterTerminationReason())
				|| transfer.getRosterDate() != null
				|| transfer.getRosterTerminationDate() != null)
		{
			RosterData rosterData = new RosterData();
			rosterData.setStatusCode(transfer.getRosterStatus());
			rosterData.setRosterDateTime(Optional.ofNullable(transfer.getRosterDate()).map(LocalDate::atStartOfDay).orElse(null));
			rosterData.setTerminationDateTime(Optional.ofNullable(transfer.getRosterTerminationDate()).map(LocalDate::atStartOfDay).orElse(null));
			if(transfer.getRosterTerminationReason() != null)
			{
				rosterData.setTerminationReason(RosterTerminationReason.getByCode(Integer.parseInt(transfer.getRosterTerminationReason())));
			}
			input.setCurrentRosterData(rosterData);
		}

		// care team info
		input.setMrpProviderId(transfer.getProviderNo());
		input.setResidentProviderId(transfer.getResident());
		input.setNurseProviderId(transfer.getNurse());
		input.setMidwifeProviderId(transfer.getMidwife());

		if(!StringUtils.isAnyBlank(transfer.getReferralDoctorName(), transfer.getReferralDoctorNo()))
		{
			ProviderModel referralProvider = new ProviderModel();
			if(StringUtils.isNotBlank(transfer.getReferralDoctorName()))
			{
				String[] names = transfer.getReferralDoctorName().split(",");
				referralProvider.setLastName(StringUtils.trimToNull(names[0]));
				referralProvider.setFirstName(StringUtils.trimToNull(names[1]));
			}
			if(StringUtils.isNotBlank(transfer.getReferralDoctorNo()))
			{
				referralProvider.setOhipNumber(transfer.getReferralDoctorNo());
			}
			input.setReferralDoctor(referralProvider);
		}

		if(!StringUtils.isAnyBlank(transfer.getFamilyDoctorName(), transfer.getFamilyDoctorNo()))
		{
			ProviderModel familyProvider = new ProviderModel();
			if(StringUtils.isNotBlank(transfer.getFamilyDoctorName()))
			{
				String[] names = transfer.getFamilyDoctorName().split(",");
				familyProvider.setLastName(StringUtils.trimToNull(names[0]));
				familyProvider.setFirstName(StringUtils.trimToNull(names[1]));
			}
			if(StringUtils.isNotBlank(transfer.getFamilyDoctorNo()))
			{
				familyProvider.setOhipNumber(transfer.getFamilyDoctorNo());
			}
			input.setFamilyDoctor(familyProvider);
		}

		input.setChartNumber(transfer.getChartNo());
		input.setAlias(transfer.getAlias());

		input.setChildren(transfer.getChildren());
		input.setSourceOfIncome(transfer.getSourceOfIncome());

		input.setCitizenship(transfer.getCitizenship());
		input.setCountryOfOrigin(transfer.getCountryOfOrigin());
		input.setSpokenLanguage(transfer.getSpokenLanguage());
		input.setOfficialLanguage(DemographicModel.OFFICIAL_LANGUAGE.fromValueString(transfer.getOfficialLanguage()));
		input.setNewsletter(transfer.getNewsletter());
		input.setAnonymous(transfer.getAnonymous());
		input.setPatientNote(transfer.getNotes());
		input.setPatientAlert(transfer.getAlert());


		return input;
	}
}
