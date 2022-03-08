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
package org.oscarehr.dataMigration.converter.in.contact;

import org.oscarehr.contact.entity.Contact;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.oscarehr.dataMigration.model.contact.ExternalContact;
import org.springframework.stereotype.Component;

@Component
public class ExternalContactModelToDbConverter
		extends BaseModelToDbConverter<ExternalContact, Contact>
{
	@Override
	public Contact convert(ExternalContact input)
	{
		Contact contact = new Contact();

		if(input.getId() != null)
		{
			contact.setId(Integer.parseInt(input.getId()));
		}
		contact.setFirstName(input.getFirstName());
		contact.setLastName(input.getLastName());
		contact.setEmail(input.getEmail());

		AddressModel address = input.getAddress();
		if(address != null)
		{
			contact.setAddress(address.getAddressLine1());
			contact.setAddress2(address.getAddressLine2());
			contact.setCity(address.getCity());
			contact.setProvince(address.getRegionCode());
			contact.setCountry(address.getCountryCode());
			contact.setPostal(address.getPostalCode());
		}

		// phone conversions
		PhoneNumberModel homePhone = input.getHomePhone();
		if(homePhone != null)
		{
			contact.setResidencePhone(homePhone.getNumber());
		}

		PhoneNumberModel cellPhone = input.getCellPhone();
		if(cellPhone != null)
		{
			contact.setCellPhone(cellPhone.getNumber());
		}

		PhoneNumberModel workPhone = input.getWorkPhone();
		if(workPhone != null)
		{
			contact.setWorkPhone(workPhone.getNumber());
			contact.setWorkPhoneExtension(workPhone.getExtension());
		}

		return contact;
	}
}
