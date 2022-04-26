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

import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.ws.external.rest.v1.transfer.demographic.DemographicTransferOutbound;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DemographicModelToExternalApiTransferConverter
		extends BaseModelToDbConverter<DemographicModel, DemographicTransferOutbound>
{
	@Override
	public DemographicTransferOutbound convert(DemographicModel model)
	{
		if(model == null)
		{
			return null;
		}

		DemographicTransferOutbound transfer = new DemographicTransferOutbound();
		transfer.setDemographicNo(model.getId());
		transfer.setLastUpdateDate(model.getLastUpdateDateTime().toLocalDate());

		transfer.setLastName(model.getLastName());
		transfer.setFirstName(model.getFirstName());
		transfer.setTitle(Optional.ofNullable(model.getTitle()).map(Person.TITLE::name).orElse(null));
		transfer.setSex(model.getSexString());
		transfer.setHin(model.getHealthNumber());
		transfer.setSin(model.getSin());
		transfer.setHcType(model.getHealthNumberProvinceCode());
		transfer.setHcVersion(model.getHealthNumberVersion());
		transfer.setHcRenewDate(model.getHealthNumberRenewDate());
		transfer.setHcEffectiveDate(model.getHealthNumberEffectiveDate());
		transfer.setDateJoined(model.getDateJoined());
		transfer.setEndDate(model.getDateEnded());
		transfer.setDateOfBirth(model.getDateOfBirth());
		transfer.setPatientStatus(model.getPatientStatus());
		transfer.setPatientStatusDate(model.getPatientStatusDate());
		transfer.setVeteranNo(model.getVeteranNumber());

		// contact info
		transfer.setEmail(model.getEmail());

		AddressModel address = model.getAddress();
		if(address != null)
		{
			transfer.setAddress(address.getAddressLine1());
			transfer.setCity(address.getCity());
			transfer.setPostal(address.getPostalCode());
			transfer.setProvince(address.getRegionCode());
		}

		transfer.setPrimaryPhone(Optional.ofNullable(model.getHomePhone()).map(PhoneNumberModel::getNumber).orElse(null));
		transfer.setSecondaryPhone(Optional.ofNullable(model.getWorkPhone()).map(PhoneNumberModel::getNumber).orElse(null));
		transfer.setCellPhone(Optional.ofNullable(model.getCellPhone()).map(PhoneNumberModel::getNumber).orElse(null));
		transfer.setPreviousAddress(model.getPreviousAddress());
		transfer.setPcnIndicator(model.getPcnIndicator());

		// roster info
		RosterData rosterData = model.getCurrentRosterData();
		if(rosterData != null)
		{
			transfer.setRosterStatus(rosterData.getStatusCode());
			transfer.setRosterDate(Optional.ofNullable(rosterData.getRosterDateTime()).map(LocalDateTime::toLocalDate).orElse(null));
			transfer.setRosterTerminationDate(Optional.ofNullable(rosterData.getTerminationDateTime()).map(LocalDateTime::toLocalDate).orElse(null));
			transfer.setRosterTerminationReason(
					Optional.ofNullable(rosterData.getTerminationReason())
							.map((reason) -> String.valueOf(reason.getTerminationCode())).orElse(null));
		}

		// care team info
		transfer.setProviderNo(Optional.ofNullable(model.getMrpProvider()).map(ProviderModel::getId).orElse(null));
		transfer.setResident(Optional.ofNullable(model.getResidentProvider()).map(ProviderModel::getId).orElse(null));
		transfer.setNurse(Optional.ofNullable(model.getNurseProvider()).map(ProviderModel::getId).orElse(null));
		transfer.setMidwife(Optional.ofNullable(model.getMidwifeProvider()).map(ProviderModel::getId).orElse(null));

		transfer.setReferralDoctorNo(Optional.ofNullable(model.getReferralDoctor()).map(ProviderModel::getOhipNumber).orElse(null));
		transfer.setReferralDoctorName(Optional.ofNullable(model.getReferralDoctor())
				.map((provider) -> provider.getLastName() + "," + provider.getFirstName()).orElse(null));

		transfer.setFamilyDoctorNo(Optional.ofNullable(model.getFamilyDoctor()).map(ProviderModel::getOhipNumber).orElse(null));
		transfer.setFamilyDoctorName(Optional.ofNullable(model.getFamilyDoctor())
				.map((provider) -> provider.getLastName() + "," + provider.getFirstName()).orElse(null));

		transfer.setChartNo(model.getChartNumber());
		transfer.setAlias(model.getAlias());
		transfer.setChildren(model.getChildren());
		transfer.setSourceOfIncome(model.getSourceOfIncome());

		transfer.setCitizenship(model.getCitizenship());
		transfer.setCountryOfOrigin(model.getCountryOfOrigin());
		transfer.setSpokenLanguage(model.getSpokenLanguage());
		transfer.setOfficialLanguage(Optional.ofNullable(model.getOfficialLanguage()).map(DemographicModel.OFFICIAL_LANGUAGE::getValue).orElse(null));
		transfer.setNewsletter(model.getNewsletter());
		transfer.setAnonymous(model.getAnonymous());
		transfer.setNotes(model.getPatientNote());
		transfer.setAlert(model.getPatientAlert());

		return transfer;
	}
}
