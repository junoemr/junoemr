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
package org.oscarehr.dataMigration.mapper.cds.in;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.common.Person;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.demographic.Demographic;
import org.oscarehr.dataMigration.model.demographic.RosterData;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;
import xml.cds.v5_0.Demographics;
import xml.cds.v5_0.HealthCard;
import xml.cds.v5_0.OfficialSpokenLanguageCode;
import xml.cds.v5_0.PersonNamePrefixCode;
import xml.cds.v5_0.PersonStatus;

import java.time.LocalDate;

import static org.oscarehr.demographic.model.Demographic.STATUS_ACTIVE;
import static org.oscarehr.demographic.model.Demographic.STATUS_DECEASED;
import static org.oscarehr.demographic.model.Demographic.STATUS_INACTIVE;

@Component
public class CDSDemographicImportMapper extends AbstractCDSImportMapper<Demographics, Demographic>
{
	@Autowired
	protected CDSEnrollmentHistoryImportMapper cdsEnrollmentHistoryImportMapper;

	public CDSDemographicImportMapper()
	{
		super();
	}

	@Override
	public Demographic importToJuno(Demographics importStructure) throws Exception
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
			if(demographic.getTitle() == null)
			{
				logEvent("Invalid Title value: " + namePrefixCode.value());
			}
		}

		OfficialSpokenLanguageCode officialLanguage = importStructure.getPreferredOfficialLanguage();
		if(officialLanguage != null)
		{
			demographic.setOfficialLanguage(OfficialSpokenLanguageCode.FRE.equals(officialLanguage) ?
					Demographic.OFFICIAL_LANGUAGE.FRENCH : Demographic.OFFICIAL_LANGUAGE.ENGLISH);
		}
		demographic.setSpokenLanguage(StringUtils.trimToNull(importStructure.getPreferredSpokenLanguage()));
		demographic.setPatientNote(StringUtils.trimToNull(importStructure.getNoteAboutPatient()));
	}

	protected void mapHealthInsuranceInfo(Demographics importStructure, Demographic demographic)
	{
		HealthCard healthCard = importStructure.getHealthCard();
		if(healthCard != null)
		{
			demographic.setHealthNumber(StringUtils.trimToNull(healthCard.getNumber()));
			demographic.setHealthNumberVersion(StringUtils.trimToNull(healthCard.getVersion()));
			demographic.setHealthNumberProvinceCode(getSubregionCode(StringUtils.trimToNull(healthCard.getProvinceCode())));
			demographic.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(healthCard.getExpirydate()));
		}
	}

	protected void mapContactInfo(Demographics importStructure, Demographic demographic)
	{
		demographic.setEmail(importStructure.getEmail());

		for(xml.cds.v5_0.Address importAddr : importStructure.getAddress())
		{
			demographic.addAddress(getAddress(importAddr));
		}

		for(xml.cds.v5_0.PhoneNumber importNumber : importStructure.getPhoneNumber())
		{
			PhoneNumber phoneNumber = getPhoneNumber(importNumber);

			if(phoneNumber.isTypeHome() && demographic.getHomePhone() == null)
			{
				demographic.setHomePhone(phoneNumber);
			}
			else if(phoneNumber.isTypeWork() && demographic.getWorkPhone() == null)
			{
				demographic.setWorkPhone(phoneNumber);
			}
			else if(phoneNumber.isTypeCell() && demographic.getCellPhone() == null)
			{
				demographic.setCellPhone(phoneNumber);
			}
			else
			{
				logEvent("Demographic has excess phone number data that could not be used");
			}
		}

		if(importStructure.getAddress().size() > 1)
		{
			logEvent("Demographic has multiple associated addresses, some data may be missing.");
		}
	}

	protected void mapCareTeamInfo(Demographics importStructure, Demographic demographic) throws Exception
	{
		demographic.setEmail(importStructure.getEmail());
		demographic.setMrpProvider(getImportPrimaryPhysician(importStructure));
		demographic.setChartNumber(importStructure.getChartNumber());
		demographic.setPatientStatus(getPatientStatus(importStructure.getPersonStatusCode()));
		demographic.setPatientStatusDate(LocalDate.now());
		demographic.setDateJoined(LocalDate.now());
		demographic.setReferralDoctor(toProvider(importStructure.getReferredPhysician()));

		Demographics.Enrolment enrollment = importStructure.getEnrolment();
		if(enrollment != null)
		{
			demographic.setRosterHistory(cdsEnrollmentHistoryImportMapper.importAll(enrollment.getEnrolmentHistory()));
		}

		/* family doctor import logic
		* Family doctor field in Oscar is the Family Doctor in CDS when rostered status is "not rostered"
		* Family doctor field in Oscar is the Enrollment Doctor in CDS when rostered status is "rostered" */
		RosterData currentRosterData = demographic.getCurrentRosterData();
		if(currentRosterData != null && currentRosterData.isRostered())
		{
			demographic.setFamilyDoctor(currentRosterData.getRosterProvider());
		}
		else
		{
			demographic.setFamilyDoctor(toProvider(importStructure.getFamilyPhysician()));
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
						logEvent("Unknown patient status value: '" + plainTextCode + "'. Patient status set to active");
						break;
					}
				}
			}
			else
			{
				logEvent("Patient status missing, set as active");
			}
		}
		else
		{
			logEvent("Patient status missing, set as active");
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
