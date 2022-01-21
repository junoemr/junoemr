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
package org.oscarehr.dataMigration.mapper.hrm.out;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.mapper.cds.CDSDemographicInterface;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.contact.DemographicContact;
import org.oscarehr.demographic.model.DemographicModel;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.hrm.v4_3.Address;
import xml.hrm.v4_3.AddressStructured;
import xml.hrm.v4_3.AddressType;
import xml.hrm.v4_3.ContactPersonPurpose;
import xml.hrm.v4_3.Demographics;
import xml.hrm.v4_3.Gender;
import xml.hrm.v4_3.HealthCard;
import xml.hrm.v4_3.OfficialSpokenLanguageCode;
import xml.hrm.v4_3.PersonNamePartTypeCode;
import xml.hrm.v4_3.PersonNamePrefixCode;
import xml.hrm.v4_3.PersonNamePurposeCode;
import xml.hrm.v4_3.PersonNameSimpleWithMiddleName;
import xml.hrm.v4_3.PersonNameStandard;
import xml.hrm.v4_3.PersonStatus;
import xml.hrm.v4_3.PhoneNumber;
import xml.hrm.v4_3.PhoneNumberType;
import xml.hrm.v4_3.PostalZipCode;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.ENROLLMENT_STATUS_FALSE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.ENROLLMENT_STATUS_TRUE;
import static org.oscarehr.demographic.entity.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.entity.Demographic.STATUS_INACTIVE;

@Component
public class HRMDemographicExportMapper extends AbstractHRMExportMapper<CDSDemographicInterface, PatientRecord>
{
	private static final Logger logger = Logger.getLogger(HRMDemographicExportMapper.class);


	public HRMDemographicExportMapper()
	{
		super();
	}

	@Override
	public Demographics exportFromJuno(PatientRecord exportStructure)
	{
		Demographics demographics = objectFactory.createDemographics();
		DemographicModel exportDemographic = exportStructure.getDemographic();

		demographics.setNames(getExportNames(exportDemographic));
		demographics.setDateOfBirth(toNullableDateFullOrPartial(exportDemographic.getDateOfBirth()));
		demographics.setHealthCard(getExportHealthCard(exportDemographic));
		demographics.setChartNumber(exportDemographic.getChartNumber());
		demographics.setGender(getExportGender(exportDemographic.getSex()));
		demographics.setUniqueVendorIdSequence(String.valueOf(exportDemographic.getId()));
		demographics.getAddress().addAll(getExportAddresses(exportDemographic.getAddressList()));
		demographics.getPhoneNumber().addAll(getExportPhones(exportDemographic));
		demographics.setPreferredOfficialLanguage(getExportOfficialLanguage(exportDemographic.getOfficialLanguage()));
		demographics.setPreferredSpokenLanguage(exportDemographic.getSpokenLanguage());
		demographics.getContact().addAll(getContacts(exportStructure.getContactList()));
		demographics.setNoteAboutPatient(exportDemographic.getPatientNote());
		demographics.setPrimaryPhysician(getExportPrimaryPhysician(exportDemographic));
		demographics.setEmail(exportDemographic.getEmail());
		demographics.setPersonStatusCode(getExportStatusCode(exportDemographic.getPatientStatus()));
		demographics.setPersonStatusDate(toNullableDateFullOrPartial(exportDemographic.getPatientStatusDate()));
		demographics.setSIN(exportDemographic.getSin());

		RosterData rosterData = exportDemographic.getCurrentRosterData();
		if(rosterData != null)
		{
			demographics.setEnrollmentStatus(rosterData.isRostered() ? ENROLLMENT_STATUS_TRUE : ENROLLMENT_STATUS_FALSE);
			demographics.setEnrollmentDate(toNullableDateFullOrPartial(rosterData.getRosterDateTime()));
			demographics.setEnrollmentTerminationDate(toNullableDateFullOrPartial(rosterData.getTerminationDateTime()));
		}

		return demographics;
	}

	protected PersonNameStandard getExportNames(DemographicModel exportStructure)
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

		names.setNamePrefix(getExportNamePrefix(exportStructure));

		names.setLegalName(legalName);
		return names;
	}

	protected PersonNamePrefixCode getExportNamePrefix(DemographicModel exportStructure)
	{
		String title = exportStructure.getTitleString();
		PersonNamePrefixCode prefixCode = null;
		if(title != null)
		{
			if(EnumUtils.isValidEnum(PersonNamePrefixCode.class, title))
			{
				prefixCode = PersonNamePrefixCode.valueOf(title);
			}
			else
			{
				logger.error("(#" +exportStructure.getId()+ ") Invalid Name Prefix in Export: '" + title + "'");
			}
		}
		return prefixCode;
	}

	protected Gender getExportGender(Person.SEX sex)
	{
		switch(sex)
		{
			case MALE: return Gender.M;
			case FEMALE: return Gender.F;
			case TRANSGENDER:
			case OTHER: return Gender.O;
			case UNKNOWN:
			default: return Gender.U;
		}
	}

	protected HealthCard getExportHealthCard(DemographicModel exportStructure)
	{
		HealthCard healthCard = objectFactory.createHealthCard();
		healthCard.setNumber(exportStructure.getHealthNumber());
		healthCard.setVersion(exportStructure.getHealthNumberVersion());
		healthCard.setExpirydate(ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getHealthNumberRenewDate()));
		healthCard.setProvinceCode(org.oscarehr.dataMigration.model.common.Address.getSubdivisionCodeCT013Format(
				exportStructure.getHealthNumberProvinceCode(), CDSConstants.COUNTRY_CODE_CANADA));

		return healthCard;
	}

	protected List<Address> getExportAddresses(List<org.oscarehr.dataMigration.model.common.Address> addressList)
	{
		List<Address> exportAddressList = new ArrayList<>(addressList.size());
		for(org.oscarehr.dataMigration.model.common.Address address : addressList)
		{
			exportAddressList.add(toHrmAddress(address, AddressType.R));
		}
		return exportAddressList;
	}

	protected List<PhoneNumber> getExportPhones(DemographicModel exportStructure)
	{
		List<PhoneNumber> exportPhoneList = new ArrayList<>(3);

		org.oscarehr.dataMigration.model.common.PhoneNumber homePhone = exportStructure.getHomePhone();
		org.oscarehr.dataMigration.model.common.PhoneNumber workPhone = exportStructure.getWorkPhone();
		org.oscarehr.dataMigration.model.common.PhoneNumber cellPhone = exportStructure.getCellPhone();
		if(homePhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.R, homePhone));
		}
		if(workPhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.W, workPhone));
		}
		if(cellPhone != null)
		{
			exportPhoneList.add(getExportPhone(PhoneNumberType.C, cellPhone));
		}
		return exportPhoneList;
	}

	protected PhoneNumber getExportPhone(PhoneNumberType type, 	org.oscarehr.dataMigration.model.common.PhoneNumber phoneNumber)
	{
		String number = phoneNumber.getNumber();
		String extension = phoneNumber.getExtension();

		PhoneNumber phone = objectFactory.createPhoneNumber();
		phone.getContent().add(objectFactory.createPhoneNumberNumber(number));
		if(extension != null)
		{
			phone.getContent().add(objectFactory.createPhoneNumberExtension(extension));
		}
		phone.setPhoneNumberType(type);
		return phone;
	}

	protected Demographics.PrimaryPhysician getExportPrimaryPhysician(
		DemographicModel exportStructure)
	{
		Provider provider = exportStructure.getMrpProvider();
		Demographics.PrimaryPhysician primaryPhysician = null;
		if(provider != null)
		{
			primaryPhysician = objectFactory.createDemographicsPrimaryPhysician();
			primaryPhysician.setName(toPersonNameSimple(provider));
			primaryPhysician.setOHIPPhysicianId(provider.getOhipNumber());
		}
		return primaryPhysician;
	}
	protected PersonStatus getExportStatusCode(String patientStatus)
	{
		PersonStatus personStatus = PersonStatus.A;
		if(patientStatus != null)
		{
			switch(patientStatus)
			{
				case STATUS_INACTIVE: personStatus = PersonStatus.I; break;
				case STATUS_DECEASED: personStatus = PersonStatus.D; break;
			}
		}
		return personStatus;
	}

	protected OfficialSpokenLanguageCode getExportOfficialLanguage(DemographicModel.OFFICIAL_LANGUAGE officialLanguage)
	{
		if(officialLanguage != null)
		{
			switch(officialLanguage)
			{
				case FRENCH: return OfficialSpokenLanguageCode.FRE;
				case ENGLISH: return OfficialSpokenLanguageCode.ENG;
			}
		}
		return null;
	}

	protected List<Demographics.Contact> getContacts(List<DemographicContact> demographicContactList)
	{
		List<Demographics.Contact> contactList = new ArrayList<>();
		for(DemographicContact demographicContact : demographicContactList)
		{
			Demographics.Contact contact = objectFactory.createDemographicsContact();
			PersonNameSimpleWithMiddleName personNameSimpleWithMiddleName = objectFactory.createPersonNameSimpleWithMiddleName();
			personNameSimpleWithMiddleName.setFirstName(demographicContact.getContact().getFirstName());
			personNameSimpleWithMiddleName.setLastName(demographicContact.getContact().getLastName());
			// no middle names to export in Juno

			contact.setName(personNameSimpleWithMiddleName);
			contact.setEmailAddress(demographicContact.getContact().getEmail());
			contact.setNote(demographicContact.getNote());

			//contact phone conversion
			org.oscarehr.dataMigration.model.common.PhoneNumber homePhone = demographicContact.getContact().getHomePhone();
			org.oscarehr.dataMigration.model.common.PhoneNumber workPhone = demographicContact.getContact().getWorkPhone();
			org.oscarehr.dataMigration.model.common.PhoneNumber cellPhone = demographicContact.getContact().getCellPhone();
			if(homePhone != null)
			{
				contact.getPhoneNumber().add(getExportPhone(PhoneNumberType.R, homePhone));
			}
			if(workPhone != null)
			{
				contact.getPhoneNumber().add(getExportPhone(PhoneNumberType.W, workPhone));
			}
			if(cellPhone != null)
			{
				contact.getPhoneNumber().add(getExportPhone(PhoneNumberType.C, cellPhone));
			}

			// contact reason/purpose
			if(EnumUtils.isValidEnum(ContactPersonPurpose.class, demographicContact.getRole()))
			{
				contact.setContactPurpose(ContactPersonPurpose.valueOf(demographicContact.getRole()));
			}
			else if(demographicContact.isEmergencyContact())
			{
				contact.setContactPurpose(ContactPersonPurpose.EC);
			}
			else if(demographicContact.isSubstituteDecisionMaker())
			{
				//TODO ContactPersonPurpose.SDM is defined in the code-table spec but not the schema files
			}
			contactList.add(contact);
		}
		return contactList;
	}

	protected Address toHrmAddress(org.oscarehr.dataMigration.model.common.Address addressModel, AddressType addressType)
	{
		Address address = null;
		if(addressModel != null)
		{
			address = objectFactory.createAddress();
			AddressStructured structured = objectFactory.createAddressStructured();
			PostalZipCode postalZipCode = objectFactory.createPostalZipCode();
			postalZipCode.setPostalCode(addressModel.getPostalCode());

			structured.setLine1(addressModel.getAddressLine1());
			structured.setLine2(addressModel.getAddressLine2());
			structured.setCity(addressModel.getCity());
			structured.setCountrySubdivisionCode(addressModel.getSubdivisionCodeCT013Format());
			structured.setPostalZipCode(postalZipCode);

			address.setStructured(structured);
			address.setAddressType(addressType);
		}
		return address;
	}
}
