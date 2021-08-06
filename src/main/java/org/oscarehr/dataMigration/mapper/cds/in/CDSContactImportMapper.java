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

import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.contact.DemographicContact;
import org.oscarehr.dataMigration.model.contact.ExternalContact;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.Demographics;
import xml.cds.v5_0.PurposeEnumOrPlainText;

import java.time.LocalDateTime;
import java.util.List;

import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE;
import static org.oscarehr.dataMigration.mapper.cds.CDSConstants.DEFAULT_CONTACT_FACILITY_ID;

@Component
public class CDSContactImportMapper extends AbstractCDSImportMapper<Demographics.Contact, DemographicContact>
{
	public CDSContactImportMapper()
	{
		super();
	}

	@Override
	public DemographicContact importToJuno(Demographics.Contact importContact)
	{
		ExternalContact contact = new ExternalContact();
		contact.setFirstName(importContact.getName().getFirstName());
		// contact middle name not imported for now.
		contact.setLastName(importContact.getName().getLastName());
		contact.setEmail(importContact.getEmailAddress());

		for(xml.cds.v5_0.PhoneNumber importNumber : importContact.getPhoneNumber())
		{
			PhoneNumber phoneNumber = getPhoneNumber(importNumber);

			if(phoneNumber.isTypeHome() && contact.getHomePhone() == null)
			{
				contact.setHomePhone(phoneNumber);
			}
			else if(phoneNumber.isTypeWork() && contact.getWorkPhone() == null)
			{
				contact.setWorkPhone(phoneNumber);
			}
			else if(phoneNumber.isTypeCell() && contact.getCellPhone() == null)
			{
				contact.setCellPhone(phoneNumber);
			}
			else
			{
				logEvent("Contact has excess phone number data that could not be used");
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
		demographicContact.setFacilityId(DEFAULT_CONTACT_FACILITY_ID);

		return demographicContact;
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
				case DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE : break; // special case value
				case DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE : break; // special case value
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
			if(DEMOGRAPHIC_CONTACT_EMERGENCY_CONTACT_CODE.equalsIgnoreCase(purposeStr) || "Emergency contact".equalsIgnoreCase(purposeStr))
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
			if(DEMOGRAPHIC_CONTACT_SUB_DECISION_MAKER_CODE.equalsIgnoreCase(purposeStr) || "Substitute decision maker".equalsIgnoreCase(purposeStr))
			{
				result = true;
				break;
			}
		}
		return result;
	}
}
