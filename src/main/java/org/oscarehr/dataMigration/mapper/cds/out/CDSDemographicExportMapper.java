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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.mapper.cds.CDSDemographicInterface;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.contact.DemographicContact;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.pharmacy.Pharmacy;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.AddressType;
import xml.cds.v5_0.Demographics;
import xml.cds.v5_0.Gender;
import xml.cds.v5_0.HealthCard;
import xml.cds.v5_0.OfficialSpokenLanguageCode;
import xml.cds.v5_0.PersonNamePartTypeCode;
import xml.cds.v5_0.PersonNamePrefixCode;
import xml.cds.v5_0.PersonNamePurposeCode;
import xml.cds.v5_0.PersonNameSimpleWithMiddleName;
import xml.cds.v5_0.PersonNameStandard;
import xml.cds.v5_0.PersonStatus;
import xml.cds.v5_0.PhoneNumber;
import xml.cds.v5_0.PhoneNumberType;
import xml.cds.v5_0.PurposeEnumOrPlainText;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.ENROLLMENT_STATUS_FALSE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.ENROLLMENT_STATUS_TRUE;
import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;

@Component
public class CDSDemographicExportMapper extends AbstractCDSExportMapper<CDSDemographicInterface, PatientRecord>
{
	public CDSDemographicExportMapper()
	{
		super();
	}

	@Override
	public Demographics exportFromJuno(PatientRecord exportStructure)
	{
		Demographic exportDemographic = exportStructure.getDemographic();
		Demographics demographics = objectFactory.createDemographics();

		demographics.setNames(getExportNames(exportDemographic));
		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportDemographic.getDateOfBirth()));
		demographics.setHealthCard(getExportHealthCard(exportDemographic));
		demographics.setChartNumber(exportDemographic.getChartNumber());
		demographics.setGender(getExportGender(exportDemographic.getSex()));
		demographics.setUniqueVendorIdSequence(String.valueOf(exportDemographic.getId()));
		demographics.getAddress().addAll(getExportAddresses(exportDemographic));
		demographics.getPhoneNumber().addAll(getExportPhones(exportDemographic));
		demographics.setPreferredOfficialLanguage(getExportOfficialLanguage(exportDemographic.getOfficialLanguage()));
		demographics.setPreferredSpokenLanguage(exportDemographic.getSpokenLanguage());
		demographics.getContact().addAll(getContacts(exportStructure.getContactList()));
		demographics.setNoteAboutPatient(exportDemographic.getPatientNote());
		demographics.setEnrolment(getEnrollment(exportDemographic));
		demographics.setPrimaryPhysician(getExportPrimaryPhysician(exportDemographic));
		demographics.setEmail(exportDemographic.getEmail());
		demographics.setPersonStatusCode(getExportStatusCode(exportDemographic.getPatientStatus()));
		demographics.setPersonStatusDate(ConversionUtils.toNullableXmlGregorianCalendar(exportDemographic.getPatientStatusDate()));
		demographics.setSIN(exportDemographic.getSin());
		demographics.setReferredPhysician(toPersonNameSimple(exportDemographic.getReferralDoctor()));
		demographics.setFamilyPhysician(toPersonNameSimple(exportDemographic.getFamilyDoctor()));
		demographics.setPreferredPharmacy(getPreferredPharmacy(exportStructure.getPreferredPharmacy()));

		return demographics;
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

		names.setNamePrefix(getExportNamePrefix(exportStructure.getTitleString()));

		names.setLegalName(legalName);
		return names;
	}

	protected PersonNamePrefixCode getExportNamePrefix(String title)
	{
		PersonNamePrefixCode prefixCode = null;
		if(title != null)
		{
			if(EnumUtils.isValidEnum(PersonNamePrefixCode.class, title))
			{
				prefixCode = PersonNamePrefixCode.valueOf(title);
			}
			else
			{
				logEvent("Invalid Demographic Name Prefix in Export: '" + title + "'");
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

	protected HealthCard getExportHealthCard(Demographic exportStructure)
	{
		HealthCard healthCard = objectFactory.createHealthCard();
		healthCard.setNumber(exportStructure.getHealthNumber());
		healthCard.setVersion(exportStructure.getHealthNumberVersion());
		healthCard.setExpirydate(ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getHealthNumberRenewDate()));
		healthCard.setProvinceCode(Address.getSubdivisionCodeCT013Format(
				exportStructure.getHealthNumberProvinceCode(), CDSConstants.COUNTRY_CODE_CANADA));

		return healthCard;
	}

	protected List<xml.cds.v5_0.Address> getExportAddresses(Demographic exportStructure)
	{
		List<Address> addressList = exportStructure.getAddressList();
		List<xml.cds.v5_0.Address> exportAddressList = new ArrayList<>(addressList.size());

		for(Address address : addressList)
		{
			exportAddressList.add(toCdsAddress(address, AddressType.R));
		}
		return exportAddressList;
	}

	protected List<PhoneNumber> getExportPhones(Demographic exportStructure)
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
		phone.getContent().add(objectFactory.createPhoneNumberPhoneNumber(number));
		if(extension != null)
		{
			phone.getContent().add(objectFactory.createPhoneNumberExtension(extension));
		}
		phone.setPhoneNumberType(type);
		return phone;
	}

	protected Demographics.PrimaryPhysician getExportPrimaryPhysician(Demographic exportStructure)
	{
		Provider provider = exportStructure.getMrpProvider();
		Demographics.PrimaryPhysician primaryPhysician = null;
		if(provider != null)
		{
			primaryPhysician = objectFactory.createDemographicsPrimaryPhysician();
			primaryPhysician.setName(toPersonNameSimple(provider));
		    primaryPhysician.setOHIPPhysicianId(provider.getOhipNumber());
		    primaryPhysician.setPrimaryPhysicianCPSO(provider.getPractitionerNumber());
		}
		return primaryPhysician;
	}
	protected Demographics.PersonStatusCode getExportStatusCode(String patientStatus)
	{
		if(patientStatus == null)
		{
			patientStatus = STATUS_ACTIVE;
		}
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();

		PersonStatus personStatus = null;
		switch(patientStatus)
		{
			case STATUS_ACTIVE:   personStatus = PersonStatus.A; break;
			case STATUS_INACTIVE: personStatus = PersonStatus.I; break;
			case STATUS_DECEASED: personStatus = PersonStatus.D; break;
		}

		// set as enum type if it matches a standard status code
		if(personStatus != null)
		{
			personStatusCode.setPersonStatusAsEnum(personStatus);
		}
		else // plain text option for custom codes
		{
			personStatusCode.setPersonStatusAsPlainText(patientStatus);
		}

		return personStatusCode;
	}

	protected OfficialSpokenLanguageCode getExportOfficialLanguage(Demographic.OFFICIAL_LANGUAGE officialLanguage)
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

	// roster info
	protected Demographics.Enrolment getEnrollment(Demographic exportStructure)
	{
		Demographics.Enrolment enrolment = null;

		List<RosterData> rosterHistory = exportStructure.getRosterHistory();
		if(!rosterHistory.isEmpty())
		{
			enrolment = objectFactory.createDemographicsEnrolment();
			enrolment.getEnrolmentHistory().addAll(
					rosterHistory.stream()
							.map(this::getEnrollmentHistory)
							.collect(Collectors.toList()));
		}
		return enrolment;
	}

	protected Demographics.Enrolment.EnrolmentHistory getEnrollmentHistory(RosterData rosterData)
	{
		Demographics.Enrolment.EnrolmentHistory enrolmentHistory = objectFactory.createDemographicsEnrolmentEnrolmentHistory();

		enrolmentHistory.setEnrollmentDate(ConversionUtils.toNullableXmlGregorianCalendar(rosterData.getRosterDateTime()));
		if(rosterData.isRostered())
		{
			enrolmentHistory.setEnrollmentStatus(ENROLLMENT_STATUS_TRUE);
		}
		else
		{
			enrolmentHistory.setEnrollmentStatus(ENROLLMENT_STATUS_FALSE);
			enrolmentHistory.setEnrollmentTerminationDate(ConversionUtils.toNullableXmlGregorianCalendar(rosterData.getTerminationDateTime()));
			if(rosterData.getTerminationReason() != null)
			{
				enrolmentHistory.setTerminationReason(String.valueOf(rosterData.getTerminationReason().getTerminationCode()));
			}
		}

		Provider rosterProvider = rosterData.getRosterProvider();
		if(rosterProvider != null)
		{
			Demographics.Enrolment.EnrolmentHistory.EnrolledToPhysician enrolledToPhysician =
					objectFactory.createDemographicsEnrolmentEnrolmentHistoryEnrolledToPhysician();
			enrolledToPhysician.setName(toPersonNameSimple(rosterProvider));
			enrolledToPhysician.setOHIPPhysicianId(rosterProvider.getOhipNumber());

			enrolmentHistory.setEnrolledToPhysician(enrolledToPhysician);
		}
		return enrolmentHistory;
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
			PurposeEnumOrPlainText purposeEnumOrPlainText = objectFactory.createPurposeEnumOrPlainText();
			purposeEnumOrPlainText.setPurposeAsPlainText(demographicContact.getRole());
			contact.getContactPurpose().add(purposeEnumOrPlainText);

			if(demographicContact.isEmergencyContact())
			{
				// add a second purpose to indicate emergency contact designation
				PurposeEnumOrPlainText purposeText = objectFactory.createPurposeEnumOrPlainText();
				purposeEnumOrPlainText.setPurposeAsPlainText(DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE);
				contact.getContactPurpose().add(purposeText);
			}
			if(demographicContact.isSubstituteDecisionMaker())
			{
				// add a second purpose to indicate emergency contact designation
				PurposeEnumOrPlainText purposeText = objectFactory.createPurposeEnumOrPlainText();
				purposeEnumOrPlainText.setPurposeAsPlainText(DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE);
				contact.getContactPurpose().add(purposeText);
			}

			contactList.add(contact);
		}
		return contactList;
	}

	protected Demographics.PreferredPharmacy getPreferredPharmacy(Pharmacy exportPharmacy)
	{
		Demographics.PreferredPharmacy preferredPharmacy = null;
		if(exportPharmacy != null)
		{
			preferredPharmacy = objectFactory.createDemographicsPreferredPharmacy();
			preferredPharmacy.setName(exportPharmacy.getName());
			preferredPharmacy.setEmailAddress(exportPharmacy.getEmail());
			preferredPharmacy.setAddress(toCdsAddress(exportPharmacy.getAddress(), AddressType.M));

			// note: the spec says this handles multiple numbers, but the schema file structure does not
			org.oscarehr.dataMigration.model.common.PhoneNumber phoneNumber = exportPharmacy.getPhone1();
			if(phoneNumber != null)
			{
				preferredPharmacy.setPhoneNumber(getExportPhone(PhoneNumberType.W, phoneNumber));
			}
		}
		return preferredPharmacy;
	}
}
