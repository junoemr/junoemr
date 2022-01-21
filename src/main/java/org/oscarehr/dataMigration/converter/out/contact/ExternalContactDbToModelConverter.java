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

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.contact.entity.Contact;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.model.common.Address;
import org.oscarehr.dataMigration.model.common.PhoneNumber;
import org.oscarehr.dataMigration.model.contact.ExternalContact;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class ExternalContactDbToModelConverter extends
		BaseDbToModelConverter<Contact, ExternalContact>
{
	@Override
	public ExternalContact convert(Contact input)
	{
		if(input == null)
		{
			return null;
		}

		ExternalContact externalContact = new ExternalContact();

		externalContact.setId(String.valueOf(input.getId()));
		externalContact.setFirstName(input.getFirstName());
		externalContact.setLastName(input.getLastName());
		externalContact.setNote(input.getNote());
		externalContact.setEmail(input.getEmail());
		externalContact.setDeleted(input.isDeleted());
		externalContact.setUpdateDate(ConversionUtils.toNullableLocalDateTime(input.getUpdateDate()));

		String homePhone = StringUtils.trimToNull(input.getResidencePhone());
		String cellPhone = StringUtils.trimToNull(input.getCellPhone());
		String workPhone = StringUtils.trimToNull(input.getWorkPhone());
		String workExtension = StringUtils.trimToNull(input.getWorkPhoneExtension());
		String fax = StringUtils.trimToNull(input.getFax());

		externalContact.setHomePhone(PhoneNumber.of(homePhone));
		externalContact.setCellPhone(PhoneNumber.of(cellPhone));
		externalContact.setWorkPhone(PhoneNumber.of(workPhone, workExtension));
		externalContact.setFax(PhoneNumber.of(fax));

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setAddressLine2(input.getAddress2());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode(input.getCountry());
		address.setPostalCode(StringUtils.deleteWhitespace(input.getPostal()));
		address.setResidencyStatusCurrent();
		externalContact.setAddress(address);

		return externalContact;
	}
}
