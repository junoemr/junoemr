/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.ws.external.rest.v1.conversion;

import org.oscarehr.common.model.Demographic;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.external.rest.v1.transfer.DemographicTransfer;
import org.oscarehr.ws.rest.conversion.AbstractConverter;
import org.oscarehr.ws.rest.conversion.ConversionException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DemographicConverter extends AbstractConverter<Demographic, DemographicTransfer>
{
	@Override
	public Demographic getAsDomainObject(LoggedInInfo loggedInInfo, DemographicTransfer transfer) throws ConversionException
	{

		Demographic demographic = new Demographic();

		// base info
		demographic.setDemographicNo(transfer.getDemographicNo());
		demographic.setFirstName(transfer.getFirstName());
		demographic.setLastName(transfer.getLastName());
		demographic.setDateOfBirth(toNullableLegacyDate(transfer.getDateOfBirth()));
		demographic.setTitle(transfer.getTitle());
		demographic.setHin(transfer.getHin());
		demographic.setVer(transfer.getHcVersion());
		demographic.setHcType(transfer.getHcType());
		demographic.setHcRenewDate(toNullableLegacyDate(transfer.getHcRenewDate()));
		demographic.setSex(transfer.getSex());
		demographic.setEffDate(toNullableLegacyDate(transfer.getHcEffectiveDate()));
		demographic.setSin(transfer.getSin());
		demographic.setDateJoined(toNullableLegacyDate(transfer.getDateJoined()));
		demographic.setEndDate(toNullableLegacyDate(transfer.getEndDate()));
		demographic.setProviderNo(transfer.getProviderNo());
		demographic.setPatientStatus(transfer.getPatientStatus());
		demographic.setPatientStatusDate(toNullableLegacyDate(transfer.getPatientStatusDate()));

		// contact info
		demographic.setAddress(transfer.getAddress());
		demographic.setProvince(transfer.getProvince());
		demographic.setCity(transfer.getCity());
		demographic.setPostal(transfer.getPostal());
		demographic.setEmail(transfer.getEmail());
		demographic.setPhone(transfer.getPrimaryPhone());
		demographic.setPhone2(transfer.getSecondaryPhone());

		//roster info
		demographic.setRosterStatus(transfer.getRosterStatus());
		demographic.setRosterDate(toNullableLegacyDate(transfer.getRosterDate()));
		demographic.setRosterTerminationDate(toNullableLegacyDate(transfer.getRosterTerminationDate()));
		demographic.setRosterTerminationReason(transfer.getRosterTerminationReason());

		// other info
		demographic.setFamilyDoctor(transfer.getFamilyDoctor());
		demographic.setPcnIndicator(transfer.getPcnIndicator());
		demographic.setChartNo(transfer.getChartNo());
		demographic.setLinks(transfer.getRosterTerminationReason());
		demographic.setAlias(transfer.getAlias());
		demographic.setChildren(transfer.getChildren());
		demographic.setSourceOfIncome(transfer.getSourceOfIncome());
		demographic.setCitizenship(transfer.getCitizenship());
		demographic.setSpokenLanguage(transfer.getSpokenLanguage());
		demographic.setOfficialLanguage(transfer.getOfficialLanguage());

//		DemographicExt[] exts = new DemographicExt[transfer.getExtras().size()];
//		for (int i = 0; i < transfer.getExtras().size(); i++) {
//			exts[i] = demoExtConverter.getAsDomainObject(loggedInInfo, transfer.getExtras().get(i));
//
//			if (exts[i].getDemographicNo()==null) exts[i].setDemographicNo(demographic.getDemographicNo());
//			if (exts[i].getProviderNo()==null) exts[i].setProviderNo(loggedInInfo.getLoggedInProviderNo());
//		}
//		demographic.setExtras(exts);
//
//		if (transfer.getProvider() != null) {
//			demographic.setProvider(providerConverter.getAsDomainObject(loggedInInfo, transfer.getProvider()));
//		}

		return demographic;
	}

	@Override
	public DemographicTransfer getAsTransferObject(LoggedInInfo loggedInInfo, Demographic demographic) throws ConversionException
	{
		DemographicTransfer transfer = new DemographicTransfer();

		// base info
		transfer.setDemographicNo(demographic.getDemographicNo());
		transfer.setFirstName(demographic.getFirstName());
		transfer.setLastName(demographic.getLastName());
		transfer.setDateOfBirth(toNullableLocalDate(demographic.getBirthDate()));
		transfer.setTitle(demographic.getTitle());
		transfer.setSex(demographic.getSex());
		transfer.setHin(demographic.getHin());
		transfer.setSin(demographic.getSin());
		transfer.setHcVersion(demographic.getVer());
		transfer.setHcType(demographic.getHcType());
		transfer.setHcRenewDate(toNullableLocalDate(demographic.getHcRenewDate()));
		transfer.setHcEffectiveDate(toNullableLocalDate(demographic.getEffDate()));
		transfer.setProviderNo(demographic.getProviderNo());
		transfer.setPatientStatus(demographic.getPatientStatus());
		transfer.setPatientStatusDate(toNullableLocalDate(demographic.getPatientStatusDate()));
		transfer.setDateJoined(toNullableLocalDate(demographic.getDateJoined()));
		transfer.setEndDate(toNullableLocalDate(demographic.getEndDate()));


		// contact info
		transfer.setAddress(demographic.getAddress());
		transfer.setProvince(demographic.getProvince());
		transfer.setCity(demographic.getCity());
		transfer.setPostal(demographic.getPostal());
		transfer.setPrimaryPhone(demographic.getPhone());
		transfer.setSecondaryPhone(demographic.getPhone2());
		transfer.setEmail(demographic.getEmail());

		// roster info
		transfer.setRosterStatus(demographic.getRosterStatus());
		transfer.setRosterDate(toNullableLocalDate(demographic.getRosterDate()));
		transfer.setRosterTerminationDate(toNullableLocalDate(demographic.getRosterTerminationDate()));
		transfer.setRosterTerminationReason(demographic.getRosterTerminationReason());

		//other info
		transfer.setFamilyDoctor(demographic.getFamilyDoctor());
		transfer.setPcnIndicator(demographic.getPcnIndicator());
		transfer.setChartNo(demographic.getChartNo());
		transfer.setAlias(demographic.getAlias());
		transfer.setChildren(demographic.getChildren());
		transfer.setSourceOfIncome(demographic.getSourceOfIncome());
		transfer.setCitizenship(demographic.getCitizenship());
		transfer.setSpokenLanguage(demographic.getSpokenLanguage());
		transfer.setOfficialLanguage(demographic.getOfficialLanguage());

//		if (d.getExtras() != null) {
//			for (DemographicExt ext : d.getExtras()) {
//				t.getExtras().add(demoExtConverter.getAsTransferObject(loggedInInfo,ext));
//			}
//		}
//
//		if (d.getProvider() != null) {
//			t.setProvider(providerConverter.getAsTransferObject(loggedInInfo,d.getProvider()));
//		}

		return transfer;
	}

	//TODO move to new location and consolidate with AbstractServiceImpl version
	private Date toNullableLegacyDate(LocalDate localDate)
	{
		if(localDate == null) return null;
		return toLegacyDate(localDate);
	}

	private Date toLegacyDate(LocalDate localDate)
	{
		return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	private LocalDate toNullableLocalDate(Date legacyDate)
	{
		if(legacyDate == null) return null;
		return toLocalDate(legacyDate);
	}
	private LocalDate toLocalDate(Date legacyDate)
	{
		return legacyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
