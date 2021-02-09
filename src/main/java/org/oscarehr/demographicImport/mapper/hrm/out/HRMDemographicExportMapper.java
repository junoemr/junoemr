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
package org.oscarehr.demographicImport.mapper.hrm.out;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import org.springframework.stereotype.Component;
import xml.hrm.v4_3.Demographics;
import xml.hrm.v4_3.PersonNamePartTypeCode;
import xml.hrm.v4_3.PersonNamePrefixCode;
import xml.hrm.v4_3.PersonNamePurposeCode;
import xml.hrm.v4_3.PersonNameStandard;

@Component
public class HRMDemographicExportMapper extends AbstractHRMExportMapper<Demographics, Demographic>
{
	private static final Logger logger = Logger.getLogger(HRMDemographicExportMapper.class);


	public HRMDemographicExportMapper()
	{
		super();
	}

	@Override
	public Demographics exportFromJuno(Demographic exportDemographic)
	{
		Demographics demographics = objectFactory.createDemographics();
		//TODO

		demographics.setNames(getExportNames(exportDemographic));
//		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportDemographic.getDateOfBirth()));
//		demographics.setHealthCard(getExportHealthCard(exportDemographic));
		demographics.setChartNumber(exportDemographic.getChartNumber());
//		demographics.setGender(getExportGender(exportDemographic.getSex()));
		demographics.setUniqueVendorIdSequence(String.valueOf(exportDemographic.getId()));
//		demographics.getAddress().addAll(getExportAddresses(exportDemographic));
//		demographics.getPhoneNumber().addAll(getExportPhones(exportDemographic));
//		demographics.setPreferredOfficialLanguage(getExportOfficialLanguage(exportDemographic.getOfficialLanguage()));
		demographics.setPreferredSpokenLanguage(exportDemographic.getSpokenLanguage());
//		demographics.getContact().addAll(getContacts(exportStructure.getContactList()));
		demographics.setNoteAboutPatient(exportDemographic.getPatientNote());
//		demographics.setPrimaryPhysician(getExportPrimaryPhysician(exportDemographic));
		demographics.setEmail(exportDemographic.getEmail());
//		demographics.setPersonStatusCode(getExportStatusCode(exportDemographic.getPatientStatus()));
//		demographics.setPersonStatusDate(ConversionUtils.toNullableXmlGregorianCalendar(exportDemographic.getPatientStatusDate()));
		demographics.setSIN(exportDemographic.getSin());
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
}
