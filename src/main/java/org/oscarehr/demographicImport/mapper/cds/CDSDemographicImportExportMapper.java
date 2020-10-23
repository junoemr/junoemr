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
package org.oscarehr.demographicImport.mapper.cds;

import org.oscarehr.common.xml.cds.v5_0.model.AddressStructured;
import org.oscarehr.common.xml.cds.v5_0.model.AddressType;
import org.oscarehr.common.xml.cds.v5_0.model.Demographics;
import org.oscarehr.common.xml.cds.v5_0.model.Gender;
import org.oscarehr.common.xml.cds.v5_0.model.HealthCard;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.OmdCds;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePartTypeCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePurposeCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameStandard;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumberType;
import org.oscarehr.common.xml.cds.v5_0.model.PostalZipCode;
import org.oscarehr.demographicImport.mapper.AbstractDemographicImportExportMapper;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.demographic.model.Demographic.GENDER_FEMALE;
import static org.oscarehr.demographic.model.Demographic.GENDER_MALE;
import static org.oscarehr.demographic.model.Demographic.GENDER_OTHER;
import static org.oscarehr.demographic.model.Demographic.GENDER_TRANSGENDER;

public class CDSDemographicImportExportMapper extends AbstractDemographicImportExportMapper<OmdCds>
{
	protected final ObjectFactory objectFactory;

	public CDSDemographicImportExportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}

	@Override
	public Demographic importToJuno(OmdCds importStructure, Demographic demographic)
	{
		fillImportDemographic(importStructure.getPatientRecord().getDemographics(), demographic);
		return demographic;
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure)
	{
		OmdCds omdCds = objectFactory.createOmdCds();
		PatientRecord patientRecord = objectFactory.createPatientRecord();
		omdCds.setPatientRecord(patientRecord);
		return exportFromJuno(exportStructure, omdCds);
	}

	@Override
	public OmdCds exportFromJuno(Demographic exportStructure, OmdCds importStructure)
	{
		Demographics demographics = importStructure.getPatientRecord().getDemographics();
		if (demographics == null)
		{
			demographics = objectFactory.createDemographics();
		}
		fillExportDemographic(exportStructure, demographics);
		importStructure.getPatientRecord().setDemographics(demographics);
		return importStructure;
	}

	protected void fillExportDemographic(Demographic exportStructure, Demographics demographics)
	{
		demographics.setNames(getExportNames(exportStructure));
		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportStructure.getDateOfBirth()));
		demographics.setGender(getExportGender(exportStructure));
		demographics.getAddress().addAll(getExportAddresses(exportStructure));
		demographics.setEmail(exportStructure.getEmail());
		demographics.setHealthCard(getExportHealthCard(exportStructure));
		demographics.getPhoneNumber().addAll(getExportPhones(exportStructure));
	}

	protected void fillImportDemographic(Demographics importStructure, Demographic demographic)
	{
		demographic.setFirstName(importStructure.getNames().getLegalName().getFirstName().getPart());
		demographic.setLastName(importStructure.getNames().getLegalName().getLastName().getPart());
		demographic.setDateOfBirth(ConversionUtils.toLocalDate(importStructure.getDateOfBirth()));
		demographic.setSex(importStructure.getGender().toString());
		demographic.setEmail(importStructure.getEmail());
	}

	protected PersonNameStandard getExportNames(Demographic exportStructure)
	{
		PersonNameStandard names = objectFactory.createPersonNameStandard();
		PersonNameStandard.LegalName legalName = objectFactory.createPersonNameStandardLegalName();
		legalName.setNamePurpose(PersonNamePurposeCode.L);

		// first name
		PersonNameStandard.LegalName.FirstName firstName = objectFactory.createPersonNameStandardLegalNameFirstName();
		firstName.setPart(exportStructure.getFirstName());
		firstName.setPartType(PersonNamePartTypeCode.GIV);

		// last name
		PersonNameStandard.LegalName.LastName lastName = objectFactory.createPersonNameStandardLegalNameLastName();
		lastName.setPart(exportStructure.getLastName());
		lastName.setPartType(PersonNamePartTypeCode.FAMC);

		legalName.setFirstName(firstName);
		legalName.setLastName(lastName);

		names.setLegalName(legalName);
		return names;
	}

	protected Gender getExportGender(Demographic exportStructure)
	{
		String sex = exportStructure.getSex();
		switch(sex)
		{
			case GENDER_MALE: return Gender.M;
			case GENDER_FEMALE: return Gender.F;
			case GENDER_TRANSGENDER:
			case GENDER_OTHER: return Gender.O;
			default: return Gender.U;
		}
	}

	protected HealthCard getExportHealthCard(Demographic exportStructure)
	{
		HealthCard healthCard = objectFactory.createHealthCard();
		healthCard.setNumber(exportStructure.getHealthNumber());
		healthCard.setVersion(exportStructure.getHealthNumberVersion());
		healthCard.setExpirydate(ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getHealthNumberRenewDate()));
		healthCard.setProvinceCode(exportStructure.getHealthNumberProvinceCode());

		return healthCard;
	}

	protected List<org.oscarehr.common.xml.cds.v5_0.model.Address> getExportAddresses(Demographic exportStructure)
	{
		List<Address> addressList = exportStructure.getAddressList();
		List<org.oscarehr.common.xml.cds.v5_0.model.Address> exportAddressList = new ArrayList<>(addressList.size());

		for(Address address : addressList)
		{
			org.oscarehr.common.xml.cds.v5_0.model.Address cdsAddress = objectFactory.createAddress();
			AddressStructured structured = objectFactory.createAddressStructured();
			PostalZipCode postalZipCode = objectFactory.createPostalZipCode();
			postalZipCode.setPostalCode(address.getPostalCode());

			structured.setLine1(address.getAddressLine1());
			structured.setLine2(address.getAddressLine2());
			structured.setCity(address.getCity());
			structured.setCountrySubdivisionCode(address.getRegionCode());
			structured.setPostalZipCode(postalZipCode);

			cdsAddress.setStructured(structured);
			cdsAddress.setAddressType(AddressType.R);
			exportAddressList.add(cdsAddress);
		}
		return exportAddressList;
	}

	protected List<PhoneNumber> getExportPhones(Demographic exportStructure)
	{
		List<PhoneNumber> exportPhoneList = new ArrayList<>(3);

		String homePhone = exportStructure.getHomePhone();
		String workPhone = exportStructure.getHomePhone();
		String cellPhone = exportStructure.getHomePhone();
		if(homePhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.R, homePhone, null));
		}
		if(workPhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.W, workPhone, null));
		}
		if(cellPhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.C, cellPhone, null));
		}
		return exportPhoneList;
	}

	protected PhoneNumber getExportPhone(PhoneNumberType type, String number, String extension)
	{
		PhoneNumber phone = objectFactory.createPhoneNumber();
		phone.getContent().add(objectFactory.createPhoneNumberNumber(number));
		if(extension != null)
		{
			phone.getContent().add(objectFactory.createPhoneNumberExtension(number));
		}
		phone.setPhoneNumberType(type);
		return phone;
	}
}
