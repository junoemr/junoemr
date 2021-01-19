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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.AddressStructured;
import org.oscarehr.common.xml.cds.v5_0.model.Demographics;
import org.oscarehr.common.xml.cds.v5_0.model.HealthCard;
import org.oscarehr.common.xml.cds.v5_0.model.OfficialSpokenLanguageCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNamePrefixCode;
import org.oscarehr.common.xml.cds.v5_0.model.PersonStatus;
import org.oscarehr.common.xml.cds.v5_0.model.PhoneNumberType;
import org.oscarehr.common.xml.cds.v5_0.model.PostalZipCode;
import org.oscarehr.common.xml.cds.v5_0.model.PurposeEnumOrPlainText;
import org.oscarehr.demographicImport.model.common.Address;
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.common.PhoneNumber;
import org.oscarehr.demographicImport.model.contact.DemographicContact;
import org.oscarehr.demographicImport.model.contact.ExternalContact;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.oscarehr.demographic.model.Demographic.ROSTER_STATUS_NOT_ROSTERED;
import static org.oscarehr.demographic.model.Demographic.ROSTER_STATUS_ROSTERED;
import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.COUNTRY_CODE_USA;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.ENROLLMENT_STATUS_TRUE;

@Component
public class CDSDemographicImportMapper extends AbstractCDSImportMapper<Demographics, Demographic>
{
	private static final Logger logger = Logger.getLogger(CDSDemographicImportMapper.class);

	public CDSDemographicImportMapper()
	{
		super();
	}

	@Override
	public Demographic importToJuno(Demographics importStructure)
	{
		Demographic demographic = new Demographic();
		mapBasicInfo(importStructure, demographic);
		mapHealthInsuranceInfo(importStructure, demographic);
		mapContactInfo(importStructure, demographic);
		mapCareTeamInfo(importStructure, demographic);
		mapContacts(importStructure, demographic);
		return demographic;
	}

	protected void mapBasicInfo(Demographics importStructure, Demographic demographic)
	{
		demographic.setFirstName(importStructure.getNames().getLegalName().getFirstName().getPart());
		demographic.setLastName(importStructure.getNames().getLegalName().getLastName().getPart());
		demographic.setDateOfBirth(ConversionUtils.toLocalDate(importStructure.getDateOfBirth()));
		demographic.setSex(Person.SEX.getIgnoreCase(importStructure.getGender().toString()));

		PersonNamePrefixCode namePrefixCode = importStructure.getNames().getNamePrefix();
		if(namePrefixCode != null)
		{
			demographic.setTitle(Demographic.TITLE.fromStringIgnoreCase(namePrefixCode.value()));
		}

		OfficialSpokenLanguageCode officialLanguage = importStructure.getPreferredOfficialLanguage();
		if(officialLanguage != null)
		{
			//TODO language enum/constants
			demographic.setOfficialLanguage(OfficialSpokenLanguageCode.FRE.equals(officialLanguage) ? "French" : "English");
		}
		demographic.setSpokenLanguage(importStructure.getPreferredSpokenLanguage());
		demographic.setPatientNote(importStructure.getNoteAboutPatient());
	}

	protected void mapHealthInsuranceInfo(Demographics importStructure, Demographic demographic)
	{
		HealthCard healthCard = importStructure.getHealthCard();
		if(healthCard != null)
		{
			demographic.setHealthNumber(healthCard.getNumber());
			demographic.setHealthNumberVersion(healthCard.getVersion());
			demographic.setHealthNumberProvinceCode(healthCard.getProvinceCode());
			demographic.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(healthCard.getExpirydate()));
		}
	}

	protected void mapContactInfo(Demographics importStructure, Demographic demographic)
	{
		demographic.setEmail(importStructure.getEmail());

		for(org.oscarehr.common.xml.cds.v5_0.model.Address importAddr : importStructure.getAddress())
		{
			Address address = new Address();
			AddressStructured structured = importAddr.getStructured();
			if(structured != null)
			{
				address.setAddressLine1(structured.getLine1());
				address.setAddressLine2(StringUtils.trimToNull(
						StringUtils.trimToEmpty(structured.getLine2()) + "\n" + StringUtils.trimToEmpty(structured.getLine3())));
				address.setCity(structured.getCity());
				address.setRegionCode(structured.getCountrySubdivisionCode());

				PostalZipCode postalZipCode = structured.getPostalZipCode();
				if(postalZipCode != null)
				{
					String postalCode = postalZipCode.getPostalCode();
					String zipCode = postalZipCode.getZipCode();
					if(postalCode != null)
					{
						address.setCountryCode(COUNTRY_CODE_CANADA);
						address.setPostalCode(postalCode);
					}
					else if(zipCode != null)
					{
						address.setCountryCode(COUNTRY_CODE_USA);
						address.setPostalCode(zipCode);
					}
				}
			}
			else
			{
				address.setAddressLine1(importAddr.getFormatted());
			}
			address.setResidencyStatusCurrent(); //TODO how to tell if this is the main address
			demographic.addAddress(address);
		}

		for(org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber importNumber : importStructure.getPhoneNumber())
		{
			PhoneNumber phoneNumber = getPhoneNumber(importNumber);

			if(phoneNumber.isTypeHome())
			{
				demographic.setHomePhone(phoneNumber);
			}
			else if(phoneNumber.isTypeWork())
			{
				demographic.setWorkPhone(phoneNumber);
			}
			else if(phoneNumber.isTypeCell())
			{
				demographic.setCellPhone(phoneNumber);
			}
		}
	}

	protected void mapCareTeamInfo(Demographics importStructure, Demographic demographic)
	{
		demographic.setEmail(importStructure.getEmail());
		demographic.setMrpProvider(getImportPrimaryPhysician(importStructure));
		demographic.setChartNumber(importStructure.getChartNumber());
		demographic.setPatientStatus(getPatientStatus(importStructure.getPersonStatusCode()));
		demographic.setPatientStatusDate(LocalDate.now());
		demographic.setDateJoined(LocalDate.now());
		demographic.setReferralDoctor(toProvider(importStructure.getReferredPhysician()));
		demographic.setFamilyDoctor(toProvider(importStructure.getFamilyPhysician()));

		Demographics.Enrolment enrollment = importStructure.getEnrolment();
		if(enrollment != null)
		{
			//TODO how to handle multiple enrollments?
			for(Demographics.Enrolment.EnrolmentHistory enrolmentHistory : enrollment.getEnrolmentHistory())
			{
				demographic.setRosterStatus(ENROLLMENT_STATUS_TRUE.equals(enrolmentHistory.getEnrollmentStatus()) ? ROSTER_STATUS_ROSTERED : ROSTER_STATUS_NOT_ROSTERED);
				demographic.setRosterTerminationReason(enrolmentHistory.getTerminationReason());
				demographic.setRosterDate(ConversionUtils.toNullableLocalDate(enrolmentHistory.getEnrollmentDate()));
				demographic.setRosterTerminationDate(ConversionUtils.toNullableLocalDate(enrolmentHistory.getEnrollmentDate()));
			}
		}
	}

	protected void mapContacts(Demographics importStructure, Demographic demographic)
	{
		for(Demographics.Contact importContact : importStructure.getContact())
		{
			ExternalContact contact = new ExternalContact();
			contact.setFirstName(importContact.getName().getFirstName());
			// contact middle name not imported for now.
			contact.setLastName(importContact.getName().getLastName());
			contact.setEmail(importContact.getEmailAddress());

			for(org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber importNumber : importContact.getPhoneNumber())
			{
				PhoneNumber phoneNumber = getPhoneNumber(importNumber);

				if(phoneNumber.isTypeHome())
				{
					contact.setHomePhone(phoneNumber);
				}
				else if(phoneNumber.isTypeWork())
				{
					contact.setWorkPhone(phoneNumber);
				}
				else if(phoneNumber.isTypeCell())
				{
					contact.setCellPhone(phoneNumber);
				}
			}

			DemographicContact demographicContact = new DemographicContact(contact);
			demographicContact.setRole(getContactRole(importContact.getContactPurpose()));
			demographicContact.setEmergencyContact(isEmergencyContact(importContact.getContactPurpose()));
			demographicContact.setSubstituteDecisionMaker(isSubstituteDecisionMaker(importContact.getContactPurpose()));
			demographicContact.setNote(importContact.getNote());
			demographicContact.setCategoryPersonal();
			demographicContact.setConsentToContact(false);
			demographicContact.setCreatedAt(LocalDateTime.now());
			demographicContact.setUpdateDateTime(LocalDateTime.now());

			demographic.addContact(demographicContact);
		}
	}

	protected String getPatientStatus(Demographics.PersonStatusCode code)
	{
		String status = STATUS_ACTIVE;
		if(code != null)
		{
			PersonStatus personStatusCode = code.getPersonStatusAsEnum();
			String plainTextCode = code.getPersonStatusAsPlainText();
			if(personStatusCode != null)
			{
				switch(personStatusCode)
				{
					case I: status = STATUS_INACTIVE; break;
					case D: status = STATUS_DECEASED; break;
					case A: status = STATUS_ACTIVE; break;
				}
			}
			else if(plainTextCode != null)
			{
				switch(plainTextCode)
				{
					case "I": status = STATUS_INACTIVE; break;
					case "D": status = STATUS_DECEASED; break;
					case "A": status = STATUS_ACTIVE; break;
					default:
					{
						status = STATUS_ACTIVE;
						logger.warn("Unknown patient status string: '" + plainTextCode + "'. Patient status set to active");
						break;
					}
				}
			}
			else
			{
				logger.warn("Patient status missing, set as active");
			}
		}
		else
		{
			logger.warn("Patient status missing, set as active");
		}
		return status;
	}

	protected Provider getImportPrimaryPhysician(Demographics importStructure)
	{
		Provider provider = null;
		Demographics.PrimaryPhysician mrp = importStructure.getPrimaryPhysician();
		if(mrp != null)
		{
			provider = new Provider();
			provider.setFirstName(mrp.getName().getFirstName());
			provider.setLastName(mrp.getName().getLastName());
			provider.setOhipNumber(mrp.getOHIPPhysicianId());
			provider.setPractitionerNumber(mrp.getPrimaryPhysicianCPSO());
		}
		return provider;
	}

	protected PhoneNumber getPhoneNumber(org.oscarehr.common.xml.cds.v5_0.model.PhoneNumber importNumber)
	{
		if(importNumber == null)
		{
			return null;
		}
		PhoneNumber phoneNumber = new PhoneNumber();

		//TODO handle discrete phone number cases
		for(JAXBElement<String> phoneElement : importNumber.getContent())
		{
			String key = phoneElement.getName().getLocalPart();
			String value = phoneElement.getValue();
			if("phoneNumber".equals(key) || "number".equals(key))
			{
				phoneNumber.setNumber(value);
			}
			else if("extension".equals(key))
			{
				phoneNumber.setExtension(value);
			}
			else
			{
				logger.error("Unknown Phone number component key: '" + key + "'");
			}
		}

		PhoneNumberType type = importNumber.getPhoneNumberType();
		if(PhoneNumberType.R.equals(type))
		{
			phoneNumber.setPhoneTypeHome();
		}
		else if(PhoneNumberType.W.equals(type))
		{
			phoneNumber.setPhoneTypeWork();
		}
		else if(PhoneNumberType.C.equals(type))
		{
			phoneNumber.setPhoneTypeCell();
		}
		else
		{
			logger.error("Invalid Phone Number Type: " + type);
		}

		return phoneNumber;
	}

	protected String getContactRole(List<PurposeEnumOrPlainText> purposeList)
	{
		String role = null;
		for(PurposeEnumOrPlainText purpose : purposeList)
		{
			// why is the enum also a string?
			String purposeStr = purpose.getPurposeAsEnum();
			if(purposeStr == null)
			{
				purposeStr = purpose.getPurposeAsPlainText();
			}

			switch(purposeStr)
			{
				// cases copied from oscars cds 4 importer.
				case "EC" : break; // special case value
				case "SDM" : break; // special case value
				case "NK" : role = "Next of Kin"; break;
				case "AS" : role = "Administrative Staff"; break;
				case "CG" : role = "Care Giver"; break;
				case "PA" : role = "Power of Attorney"; break;
				case "IN" : role = "Insurance"; break;
				case "GT" : role = "Guarantor"; break;
				default: role = purposeStr; break;
			}
		}

		return role;
	}


	protected boolean isEmergencyContact(List<PurposeEnumOrPlainText> purposeList)
	{
		boolean result = false;
		for(PurposeEnumOrPlainText purpose : purposeList)
		{
			String purposeStr = purpose.getPurposeAsEnum();
			if(purposeStr == null)
			{
				purposeStr = purpose.getPurposeAsPlainText();
			}
			if("EC".equalsIgnoreCase(purposeStr) || "Emergency contact".equalsIgnoreCase(purposeStr))
			{
				result = true;
				break;
			}
		}
		return result;
	}

	protected boolean isSubstituteDecisionMaker(List<PurposeEnumOrPlainText> purposeList)
	{
		boolean result = false;
		for(PurposeEnumOrPlainText purpose : purposeList)
		{
			String purposeStr = purpose.getPurposeAsEnum();
			if(purposeStr == null)
			{
				purposeStr = purpose.getPurposeAsPlainText();
			}
			if("SDM".equalsIgnoreCase(purposeStr) || "Substitute decision maker".equalsIgnoreCase(purposeStr))
			{
				result = true;
				break;
			}
		}
		return result;
	}
}
