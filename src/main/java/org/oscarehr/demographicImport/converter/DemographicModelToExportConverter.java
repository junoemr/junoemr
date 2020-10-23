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
package org.oscarehr.demographicImport.converter;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class DemographicModelToExportConverter extends
		AbstractModelConverter<Demographic, org.oscarehr.demographicImport.model.demographic.Demographic>
{
	@Override
	public org.oscarehr.demographicImport.model.demographic.Demographic convert(Demographic input)
	{
		org.oscarehr.demographicImport.model.demographic.Demographic exportDemographic = new org.oscarehr.demographicImport.model.demographic.Demographic();
		BeanUtils.copyProperties(input, exportDemographic, "address", "dateOfBirth");

		exportDemographic.setDateOfBirth(input.getDateOfBirth());
		exportDemographic.setHealthNumber(input.getHin());
		exportDemographic.setHealthNumberVersion(input.getVer());
		exportDemographic.setHealthNumberProvinceCode(input.getHcType());
		exportDemographic.setDateJoined(ConversionUtils.toNullableLocalDate(input.getDateJoined()));
		exportDemographic.setDateEnded(ConversionUtils.toNullableLocalDate(input.getEndDate()));

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode("CA"); //TODO do we even store this with demographics in juno
		address.setPostalCode(input.getPostal());
		address.setResidencyStatusCurrent();
		exportDemographic.addAddress(address);

		return exportDemographic;
	}
}
