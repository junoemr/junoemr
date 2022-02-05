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
package org.oscarehr.dataMigration.converter.out;

import org.oscarehr.common.model.Site;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.common.AddressModel;
import org.oscarehr.dataMigration.model.common.PhoneNumberModel;
import org.springframework.stereotype.Component;

@Component
public class SiteDbToModelConverter extends BaseDbToModelConverter<Site, org.oscarehr.dataMigration.model.appointment.Site>
{
	@Override
	public org.oscarehr.dataMigration.model.appointment.Site convert(Site input)
	{
		org.oscarehr.dataMigration.model.appointment.Site site = new org.oscarehr.dataMigration.model.appointment.Site();

		site.setId(input.getId());
		site.setName(input.getName());
		site.setShortName(input.getShortName());

		AddressModel address = new AddressModel();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode(CDSConstants.COUNTRY_CODE_CANADA);
		address.setPostalCode(input.getPostal());
		site.setAddress(address);

		site.setPhoneNumber(PhoneNumberModel.of(input.getPhone()));
		site.setFaxNumber(PhoneNumberModel.of(input.getFax()));

		return site;
	}
}
