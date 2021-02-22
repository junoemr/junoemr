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
package org.oscarehr.demographicImport.converter.out;

import org.oscarehr.common.model.Site;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.common.Address;
import org.oscarehr.demographicImport.model.common.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SiteDbToModelConverter extends BaseDbToModelConverter<Site, org.oscarehr.demographicImport.model.appointment.Site>
{
	@Override
	public org.oscarehr.demographicImport.model.appointment.Site convert(Site input)
	{
		org.oscarehr.demographicImport.model.appointment.Site site = new org.oscarehr.demographicImport.model.appointment.Site();

		site.setId(input.getId());
		site.setName(input.getName());
		site.setShortName(input.getShortName());

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode(CDSConstants.COUNTRY_CODE_CANADA);
		address.setPostalCode(input.getPostal());
		site.setAddress(address);

		site.setPhoneNumber(PhoneNumber.of(input.getPhone()));
		site.setFaxNumber(PhoneNumber.of(input.getFax()));

		return site;
	}
}
