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
import org.oscarehr.demographicImport.model.common.Person;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.oscarehr.demographicImport.model.demographic.PhoneNumber;
import org.oscarehr.demographicImport.model.provider.Provider;
import oscar.util.ConversionUtils;

import javax.xml.bind.JAXBElement;
import java.time.LocalDate;

import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.COUNTRY_CODE_CANADA;
import static org.oscarehr.demographicImport.mapper.cds.CDSConstants.COUNTRY_CODE_USA;

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
			PhoneNumber phoneNumber = new PhoneNumber();

			//TODO handle discrete phone number cases
			for(JAXBElement<String> phoneElement : importNumber.getContent())
			{
				String key = phoneElement.getName().getLocalPart();
				String value = phoneElement.getValue();
				if("number".equals(key))
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
				demographic.setHomePhoneNumber(phoneNumber);
			}
			else if(PhoneNumberType.W.equals(type))
			{
				demographic.setWorkPhoneNumber(phoneNumber);
			}
			else if(PhoneNumberType.C.equals(type))
			{
				demographic.setCellPhoneNumber(phoneNumber);
			}
			else
			{
				logger.error("Invalid Phone Number Type: " + type);
			}
		}

		//TODO map contacts (other demographic?, relations, etc.)
	}

	protected void mapCareTeamInfo(Demographics importStructure, Demographic demographic)
	{
		demographic.setEmail(importStructure.getEmail());
		demographic.setMrpProvider(getImportPrimaryPhysician(importStructure));
		demographic.setChartNumber(importStructure.getChartNumber());
		demographic.setPatientStatus(getPatientStatus(importStructure.getPersonStatusCode()));
		demographic.setPatientStatusDate(LocalDate.now());
		demographic.setDateJoined(LocalDate.now()); //TODO can we get this from import data?
		demographic.setReferralDoctor(toProvider(importStructure.getReferredPhysician()));
		demographic.setFamilyDoctor(toProvider(importStructure.getFamilyPhysician()));
		//TODO enrollment (roster status?)
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
					//TODO additional mappings as we encounter them from external sources
					case "I": status = STATUS_INACTIVE; break;
					case "D": status = STATUS_DECEASED; break;
					case "A": status = STATUS_ACTIVE; break;
					default:
					{
						status = STATUS_ACTIVE;
						logger.warn("Unknown patient status string: '" + plainTextCode + "'. patient status set to active");
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
}
