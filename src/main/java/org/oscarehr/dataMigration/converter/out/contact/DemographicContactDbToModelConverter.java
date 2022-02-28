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
package org.oscarehr.dataMigration.converter.out.contact;

import org.apache.commons.lang3.NotImplementedException;
import org.oscarehr.contact.dao.ContactDao;
import org.oscarehr.contact.entity.DemographicContact;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.demographic.converter.DemographicDbToModelConverter;
import org.oscarehr.dataMigration.model.contact.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.contact.entity.DemographicContact.TYPE_CONTACT;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_DEMOGRAPHIC;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_PROFESSIONALSPECIALIST;
import static org.oscarehr.contact.entity.DemographicContact.TYPE_PROVIDER;
import static org.oscarehr.dataMigration.model.contact.DemographicContact.CATEGORY;

@Component
public class DemographicContactDbToModelConverter extends
		BaseDbToModelConverter<DemographicContact, org.oscarehr.dataMigration.model.contact.DemographicContact>
{
	@Autowired
	protected ContactDao contactDao;

	@Autowired
	protected ExternalContactDbToModelConverter externalContactDbToModelConverter;

	@Autowired
	protected DemographicDao demographicDao;

	@Autowired
	protected DemographicDbToModelConverter demographicDbToModelConverter;

	@Override
	public org.oscarehr.dataMigration.model.contact.DemographicContact convert(DemographicContact input)
	{
		if(input == null)
		{
			return null;
		}

		Contact contact;
		switch(input.getType())
		{
			case TYPE_PROVIDER:
			{
				contact = findProvider(input.getContactId());
				break;
			}
			case TYPE_DEMOGRAPHIC:
			{
				contact = demographicDbToModelConverter.convert(demographicDao.find(Integer.parseInt(input.getContactId())));
				break;
			}
			case TYPE_CONTACT:
			{
				contact = externalContactDbToModelConverter.convert(contactDao.find(Integer.parseInt(input.getContactId())));
				break;
			}
			case TYPE_PROFESSIONALSPECIALIST:
			{
				throw new NotImplementedException("Contact Conversion type not implemented: " + input.getType());
			}
			default:
			{
				throw new IllegalArgumentException("Unknown Contact type: " + input.getType());
			}
		}

		org.oscarehr.dataMigration.model.contact.DemographicContact demographicContact =
				new org.oscarehr.dataMigration.model.contact.DemographicContact(contact);

		demographicContact.setRole(input.getRole());
		demographicContact.setNote(input.getNote());
		demographicContact.setEmergencyContact(Boolean.parseBoolean(input.getEc()));
		demographicContact.setSubstituteDecisionMaker(Boolean.parseBoolean(input.getSdm()));
		demographicContact.setConsentToContact(input.isConsentToContact());
		demographicContact.setDeleted(input.isDeleted());

		demographicContact.setCreatedBy(findProvider(input.getCreator()));
		demographicContact.setCreatedAt(ConversionUtils.toNullableLocalDateTime(input.getCreated()));
		demographicContact.setUpdateDateTime(ConversionUtils.toNullableLocalDateTime(input.getUpdateDate()));

		demographicContact.setCategory(CATEGORY.fromStringIgnoreCase(input.getCategory()));

		return demographicContact;
	}

}
