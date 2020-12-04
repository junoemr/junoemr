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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.AddressStructured;
import org.oscarehr.common.xml.cds.v5_0.model.AddressType;
import org.oscarehr.common.xml.cds.v5_0.model.Demographics;
import org.oscarehr.common.xml.cds.v5_0.model.Gender;
import org.oscarehr.common.xml.cds.v5_0.model.HealthCard;
import org.oscarehr.common.xml.cds.v5_0.model.OfficialSpokenLanguageCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePartTypeCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePrefixCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePurposeCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameStandard;
import org.oscarehr.common.xml.cds.v5_0.model.PersonStatus;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumberType;
import org.oscarehr.common.xml.cds.v5_0.model.PostalZipCode;
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.provider.Provider;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.oscarehr.demographic.model.Demographic.ROSTER_STATUS_ROSTERED;
import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.ENROLLMENT_STATUS_FALSE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.ENROLLMENT_STATUS_TRUE;

public class CDSDemographicExportMapper extends AbstractCDSExportMapper<Demographics, Demographic>
{
	private static final Logger logger = Logger.getLogger(CDSDemographicExportMapper.class);

	public CDSDemographicExportMapper()
	{
		super();
	}

	@Override
	public Demographics exportFromJuno(Demographic exportStructure)
	{
		Demographics demographics = objectFactory.createDemographics();

		demographics.setNames(getExportNames(exportStructure));
		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportStructure.getDateOfBirth()));
		demographics.setHealthCard(getExportHealthCard(exportStructure));
		demographics.setChartNumber(exportStructure.getChartNumber());
		demographics.setGender(getExportGender(exportStructure));
		demographics.setUniqueVendorIdSequence(String.valueOf(exportStructure.getId()));
		demographics.getAddress().addAll(getExportAddresses(exportStructure));
		demographics.getPhoneNumber().addAll(getExportPhones(exportStructure));
		demographics.setPreferredOfficialLanguage(getExportOfficialLanguage(exportStructure));
		demographics.setPreferredSpokenLanguage(exportStructure.getSpokenLanguage());
//		demographics.getContact().add(null); //TODO
		demographics.setNoteAboutPatient(exportStructure.getPatientNote());
		//TODO where to export alert, as part of patient Note?
		demographics.setEnrolment(getEnrollment(exportStructure));
		demographics.setPrimaryPhysician(getExportPrimaryPhysician(exportStructure));
		demographics.setEmail(exportStructure.getEmail());
		demographics.setPersonStatusCode(getExportStatusCode(exportStructure));
		demographics.setPersonStatusDate(ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getPatientStatusDate()));
		demographics.setSIN(exportStructure.getSin());
		demographics.setReferredPhysician(toPersonNameSimple(exportStructure.getReferralDoctor()));
		demographics.setFamilyPhysician(toPersonNameSimple(exportStructure.getFamilyDoctor()));
		demographics.setPreferredPharmacy(null); //TODO

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

		names.setNamePrefix(getExportNamePrefix(exportStructure));

		names.setLegalName(legalName);
		return names;
	}

	protected PersonNamePrefixCode getExportNamePrefix(Demographic exportStructure)
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

	protected Gender getExportGender(Demographic exportStructure)
	{
		Person.SEX sex = exportStructure.getSex();
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

		org.oscarehr.demographicImport.model.demographic.PhoneNumber homePhone = exportStructure.getHomePhoneNumber();
		org.oscarehr.demographicImport.model.demographic.PhoneNumber workPhone = exportStructure.getWorkPhoneNumber();
		org.oscarehr.demographicImport.model.demographic.PhoneNumber cellPhone = exportStructure.getCellPhoneNumber();
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

	protected PhoneNumber getExportPhone(PhoneNumberType type, 	org.oscarehr.demographicImport.model.demographic.PhoneNumber phoneNumber)
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
	protected Demographics.PersonStatusCode getExportStatusCode(Demographic exportStructure)
	{
		Demographics.PersonStatusCode personStatusCode = objectFactory.createDemographicsPersonStatusCode();
		String patientStatus = exportStructure.getPatientStatus();

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

	protected OfficialSpokenLanguageCode getExportOfficialLanguage(Demographic exportStructure)
	{
		String officialLanguage = StringUtils.trimToEmpty(exportStructure.getOfficialLanguage());
		switch(officialLanguage)
		{
			case "French" : return OfficialSpokenLanguageCode.FRE;
			case "English" : return OfficialSpokenLanguageCode.ENG;
		}
		return null;
	}

	// roster info
	protected Demographics.Enrolment getEnrollment(Demographic exportStructure)
	{
		Demographics.Enrolment enrolment = null;

		String rosterStatus = exportStructure.getRosterStatus();
		if(rosterStatus != null)
		{
			enrolment = objectFactory.createDemographicsEnrolment();
			Demographics.Enrolment.EnrolmentHistory enrolmentHistory = objectFactory.createDemographicsEnrolmentEnrolmentHistory();

			if(ROSTER_STATUS_ROSTERED.equals(rosterStatus))
			{
				enrolmentHistory.setEnrollmentStatus(ENROLLMENT_STATUS_TRUE);
				enrolmentHistory.setEnrollmentDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getRosterDate()));
			}
			else
			{
				enrolmentHistory.setEnrollmentStatus(ENROLLMENT_STATUS_FALSE);
				enrolmentHistory.setEnrollmentTerminationDate(ConversionUtils.toXmlGregorianCalendar(exportStructure.getRosterTerminationDate()));
				enrolmentHistory.setTerminationReason(exportStructure.getRosterTerminationReason());
			}

			enrolment.getEnrolmentHistory().add(enrolmentHistory);
		}
		//TODO include history from the archive?

		return enrolment;
	}

}
