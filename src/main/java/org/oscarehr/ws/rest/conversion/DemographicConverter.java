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
package org.oscarehr.ws.rest.conversion;

import org.apache.log4j.Logger;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.demographic.entity.DemographicExt;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.DemographicTo1;
import oscar.util.ConversionUtils;

import java.time.LocalDate;

public class DemographicConverter extends AbstractConverter<Demographic, DemographicTo1> {
	
	private static Logger logger = Logger.getLogger(DemographicConverter.class);

	private DemographicExtConverter demoExtConverter = new DemographicExtConverter();
	private ProviderConverter providerConverter = new ProviderConverter();

	/**
	 * Converts TO, excluding provider and extras.
	 */
	@Override
	public Demographic getAsDomainObject(LoggedInInfo loggedInInfo,DemographicTo1 transfer) throws ConversionException {
		Demographic demographic = new Demographic();

		demographic.setDemographicNo(transfer.getDemographicNo());
		demographic.setPhone(transfer.getPhone());
		demographic.setPatientStatus(transfer.getPatientStatus());
		demographic.setPatientStatusDate(transfer.getPatientStatusDate());
		demographic.setRosterStatus(transfer.getRosterStatus());
		demographic.setProviderNo(transfer.getProviderNo());
		demographic.setMyOscarUserName(transfer.getMyOscarUserName());
		demographic.setHin(transfer.getHin());
		demographic.setAddress(transfer.getAddress().getAddress());
		demographic.setProvince(transfer.getAddress().getProvince());
		demographic.setVer(transfer.getVer());
		demographic.setSex(transfer.getSex());

		if (transfer.getDateOfBirth() != null)
		{
			LocalDate dateOfBirth = ConversionUtils.toNullableLocalDate(transfer.getDateOfBirth());
			if (dateOfBirth != null)
			{
				demographic.setDateOfBirth(dateOfBirth);
			}
		}
		else
		{
			demographic.setDateOfBirth(transfer.getDobDay());
			demographic.setMonthOfBirth(transfer.getDobMonth());
			demographic.setYearOfBirth(transfer.getDobYear());
		}

		demographic.setSexDesc(transfer.getSexDesc());
		demographic.setDateJoined(transfer.getDateJoined());
		demographic.setFamilyDoctor(transfer.getFamilyDoctor());
		demographic.setFamilyDoctor2(transfer.getFamilyDoctor2());
		demographic.setCity(transfer.getAddress().getCity());
		demographic.setFirstName(transfer.getFirstName());
		demographic.setPostal(transfer.getAddress().getPostal());
		demographic.setHcRenewDate(transfer.getHcRenewDate());
		demographic.setPhone2(transfer.getAlternativePhone());
		demographic.setPcnIndicator(transfer.getPcnIndicator());
		demographic.setEndDate(transfer.getEndDate());
		demographic.setLastName(transfer.getLastName());
		demographic.setHcType(transfer.getHcType());
		demographic.setChartNo(transfer.getChartNo());
		demographic.setEmail(transfer.getEmail());

		demographic.setEffDate(transfer.getEffDate());
		demographic.setRosterDate(transfer.getRosterDate());
		demographic.setRosterTerminationDate(transfer.getRosterTerminationDate());
		demographic.setRosterTerminationReason(transfer.getRosterTerminationReason());
		demographic.setLinks(transfer.getRosterTerminationReason());
		demographic.setAlias(transfer.getAlias());
		demographic.setPreviousAddress(transfer.getPreviousAddress().getAddress());
		demographic.setChildren(transfer.getChildren());
		demographic.setSourceOfIncome(transfer.getSourceOfIncome());
		demographic.setCitizenship(transfer.getCitizenship());
		demographic.setSin(transfer.getSin());
		demographic.setAnonymous(transfer.getAnonymous());
		demographic.setSpokenLanguage(transfer.getSpokenLanguage());
		demographic.setActiveCount(transfer.getActiveCount());
		demographic.setHsAlertCount(transfer.getHsAlertCount());
		demographic.setTitle(transfer.getTitle());
		demographic.setOfficialLanguage(transfer.getOfficialLanguage());
		demographic.setCountryOfOrigin(transfer.getCountryOfOrigin());
		demographic.setNewsletter(transfer.getNewsletter());
		demographic.setNameOfMother(transfer.getNameOfMother());
		demographic.setNameOfFather(transfer.getNameOfFather());
		demographic.updateElectronicMessagingConsentStatus(transfer.getElectronicMessagingConsentStatus());

		DemographicExt[] exts = new DemographicExt[transfer.getExtras().size()];
		for (int i = 0; i < transfer.getExtras().size(); i++) {
			exts[i] = demoExtConverter.getAsDomainObject(loggedInInfo,transfer.getExtras().get(i));
			
			if (exts[i].getDemographicNo() == null)
			{
				exts[i].setDemographicNo(demographic.getDemographicNo());
			}
			if (exts[i].getProviderNo() == null)
			{
				exts[i].setProviderNo(loggedInInfo.getLoggedInProviderNo());
			}
		}
		demographic.setExtras(exts);

		if (transfer.getProvider() != null) {
			demographic.setProvider(providerConverter.getAsDomainObject(loggedInInfo, transfer.getProvider()));
		}

		return demographic;
	}

	@Override
	public DemographicTo1 getAsTransferObject(LoggedInInfo loggedInInfo,Demographic demographic) throws ConversionException {
		DemographicTo1 transfer = new DemographicTo1();

		transfer.setDemographicNo(demographic.getDemographicNo());
		transfer.setPhone(demographic.getPhone());
		transfer.setPatientStatus(demographic.getPatientStatus());
		transfer.setPatientStatusDate(demographic.getPatientStatusDate());
		transfer.setRosterStatus(demographic.getRosterStatus());
		transfer.setProviderNo(demographic.getProviderNo());
		transfer.setMyOscarUserName(demographic.getMyOscarUserName());
		transfer.setHin(demographic.getHin());
		transfer.getAddress().setAddress(demographic.getAddress());
		transfer.getAddress().setProvince(demographic.getProvince());
		transfer.setVer(demographic.getVer());
		transfer.setSex(demographic.getSex());
		try {
			transfer.setDateOfBirth(demographic.getBirthDay().getTime());
		} catch (Exception e ) {
			logger.warn("Unable to parse date: " + demographic.getBirthDayAsString());
		}
		transfer.setDobYear(demographic.getYearOfBirth());
		transfer.setDobMonth(demographic.getMonthOfBirth());
		transfer.setDobDay(demographic.getDateOfBirth());
		transfer.setSexDesc(demographic.getSexDesc());
		transfer.setDateJoined(demographic.getDateJoined());
		transfer.setFamilyDoctor(demographic.getFamilyDoctor());
		transfer.setFamilyDoctor2(demographic.getFamilyDoctor2());
		transfer.getAddress().setCity(demographic.getCity());
		transfer.setFirstName(demographic.getFirstName());
		transfer.getAddress().setPostal(demographic.getPostal());
		transfer.setHcRenewDate(demographic.getHcRenewDate());
		transfer.setAlternativePhone(demographic.getPhone2());
		transfer.setPcnIndicator(demographic.getPcnIndicator());
		transfer.setEndDate(demographic.getEndDate());
		transfer.setLastName(demographic.getLastName());
		transfer.setHcType(demographic.getHcType());
		transfer.setChartNo(demographic.getChartNo());
		transfer.setEmail(demographic.getEmail());
		transfer.setEffDate(demographic.getEffDate());
		transfer.setRosterDate(demographic.getRosterDate());
		transfer.setRosterTerminationDate(demographic.getRosterTerminationDate());
		transfer.setRosterTerminationReason(demographic.getRosterTerminationReason());
		transfer.setLinks(demographic.getRosterTerminationReason());
		transfer.setAlias(demographic.getAlias());
		transfer.getPreviousAddress().setAddress(demographic.getPreviousAddress());
		transfer.setChildren(demographic.getChildren());
		transfer.setSourceOfIncome(demographic.getSourceOfIncome());
		transfer.setCitizenship(demographic.getCitizenship());
		transfer.setSin(demographic.getSin());
		transfer.setAnonymous(demographic.getAnonymous());
		transfer.setSpokenLanguage(demographic.getSpokenLanguage());
		transfer.setActiveCount(demographic.getActiveCount());
		transfer.setHsAlertCount(demographic.getHsAlertCount());
		transfer.setLastUpdateUser(demographic.getLastUpdateUser());
		transfer.setLastUpdateDate(demographic.getLastUpdateDate());
		transfer.setTitle(demographic.getTitle());
		transfer.setOfficialLanguage(demographic.getOfficialLanguage());
		transfer.setCountryOfOrigin(demographic.getCountryOfOrigin());
		transfer.setNewsletter(demographic.getNewsletter());
		transfer.setNameOfMother(demographic.getNameOfMother());
		transfer.setNameOfFather(demographic.getNameOfFather());
		transfer.setElectronicMessagingConsentGivenAt(demographic.getElectronicMessagingConsentGivenAt());
		transfer.setElectronicMessagingConsentRejectedAt(demographic.getElectronicMessagingConsentRejectedAt());
		transfer.setElectronicMessagingConsentStatus(demographic.getElectronicMessagingConsentStatus());

		if (demographic.getExtras() != null) {
			for (DemographicExt ext : demographic.getExtras()) {
				transfer.getExtras().add(demoExtConverter.getAsTransferObject(loggedInInfo,ext));
			}
		}

		if (demographic.getProvider() != null) {
			transfer.setProvider(providerConverter.getAsTransferObject(loggedInInfo,demographic.getProvider()));
		}

		return transfer;
	}
}
