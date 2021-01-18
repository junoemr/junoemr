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
package org.oscarehr.demographicImport.converter.in.contact;

import org.oscarehr.common.model.DemographicContact;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographicImport.converter.in.BaseModelToDbConverter;
import org.oscarehr.demographicImport.model.contact.Contact;
import org.oscarehr.demographicImport.model.contact.ExternalContact;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class DemographicContactModelToDbConverter
		extends BaseModelToDbConverter<org.oscarehr.demographicImport.model.contact.DemographicContact, DemographicContact>
{
	@Override
	public DemographicContact convert(org.oscarehr.demographicImport.model.contact.DemographicContact input)
	{
		Contact inputContact = input.getContact();
		DemographicContact convertedDemoContact = new DemographicContact();
		convertedDemoContact.setNote(input.getNote());
		convertedDemoContact.setRole(input.getRole());
		convertedDemoContact.setCategory(input.isCategoryProfessional() ? DemographicContact.CATEGORY_PROFESSIONAL : DemographicContact.CATEGORY_PERSONAL);
		convertedDemoContact.setEc(String.valueOf(input.isEmergencyContact()));
		convertedDemoContact.setSdm(String.valueOf(input.isSubstituteDecisionMaker()));
		convertedDemoContact.setConsentToContact(input.isConsentToContact());
		convertedDemoContact.setDeleted(input.isDeleted());
		convertedDemoContact.setCreator(findOrCreateProviderRecord(input.getCreatedBy(), false).getId());
		convertedDemoContact.setCreated(ConversionUtils.toNullableLegacyDateTime(input.getCreatedAt()));
		convertedDemoContact.setUpdateDate(ConversionUtils.toNullableLegacyDateTime(input.getUpdateDateTime()));

		int type = -1;
		if(inputContact instanceof Provider)
		{
			type = DemographicContact.TYPE_PROVIDER;
		}
		else if(inputContact instanceof Demographic)
		{
			type = DemographicContact.TYPE_DEMOGRAPHIC;
		}
		else if(inputContact instanceof ExternalContact)
		{
			type = DemographicContact.TYPE_CONTACT;
		}
		// TODO type of specialists

		convertedDemoContact.setType(type);
		convertedDemoContact.setContactId(inputContact.getIdString());

		return convertedDemoContact;
	}
}
