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

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.demographic.dao.DemographicExtDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicExt;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.PhoneNumber;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.List;

@Component
public class DemographicModelToExportConverter extends
		AbstractModelConverter<Demographic, org.oscarehr.demographicImport.model.demographic.Demographic>
{
	@Autowired
	private OscarAppointmentDao appointmentDao;

	@Autowired
	private AppointmentModelToExportConverter appointmentConverter;

	@Autowired
	private DemographicExtDao demographicExtDao;

	@Autowired
	private ProviderModelToExportConverter providerConverter;

	@Override
	public org.oscarehr.demographicImport.model.demographic.Demographic convert(Demographic input)
	{
		if(input == null)
		{
			return null;
		}

		org.oscarehr.demographicImport.model.demographic.Demographic exportDemographic = new org.oscarehr.demographicImport.model.demographic.Demographic();
		BeanUtils.copyProperties(input, exportDemographic, "address", "dateOfBirth");

		exportDemographic.setId(input.getDemographicId());
		exportDemographic.setDateOfBirth(input.getDateOfBirth());
		exportDemographic.setHealthNumber(input.getHin());
		exportDemographic.setHealthNumberVersion(input.getVer());
		exportDemographic.setHealthNumberProvinceCode(input.getHcType());
		exportDemographic.setHealthNumberRenewDate(ConversionUtils.toNullableLocalDate(input.getHcRenewDate()));
		exportDemographic.setHealthNumberEffectiveDate(ConversionUtils.toNullableLocalDate(input.getHcEffectiveDate()));
		exportDemographic.setDateJoined(ConversionUtils.toNullableLocalDate(input.getDateJoined()));
		exportDemographic.setDateEnded(ConversionUtils.toNullableLocalDate(input.getEndDate()));
		exportDemographic.setChartNumber(input.getChartNo());
		exportDemographic.setRosterDate(ConversionUtils.toNullableLocalDate(input.getRosterDate()));
		exportDemographic.setRosterTerminationDate(ConversionUtils.toNullableLocalDate(input.getRosterTerminationDate()));
		exportDemographic.setMrpProvider(providerConverter.convert(input.getProvider()));
//		exportDemographic.setReferralDoctor(providerConverter.convert(input.getProvider()));
//		exportDemographic.setFamilyDoctor(providerConverter.convert(input.getProvider()));
		exportDemographic.setPatientStatusDate(ConversionUtils.toNullableLocalDate(input.getPatientStatusDate()));
		exportDemographic.setChartNumber(input.getChartNo());

		Address address = new Address();
		address.setAddressLine1(input.getAddress());
		address.setCity(input.getCity());
		address.setRegionCode(input.getProvince());
		address.setCountryCode("CA"); //TODO do we even store this with demographics in juno
		address.setPostalCode(input.getPostal());
		address.setResidencyStatusCurrent();
		exportDemographic.addAddress(address);

		// phone conversions
		if(input.getPhone() != null)
		{
			DemographicExt homePhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_H_PHONE_EXT);
			String homePhoneExtension = (homePhoneExtensionExt != null) ? StringUtils.trimToNull(homePhoneExtensionExt.getValue()) : null;
			exportDemographic.setHomePhoneNumber(new PhoneNumber(input.getPhone(), homePhoneExtension));
		}
		if(input.getPhone2() != null)
		{
			DemographicExt workPhoneExtensionExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_W_PHONE_EXT);
			String workPhoneExtension = (workPhoneExtensionExt != null) ? StringUtils.trimToNull(workPhoneExtensionExt.getValue()) : null;
			exportDemographic.setWorkPhoneNumber(new PhoneNumber(input.getPhone2(), workPhoneExtension));
		}

		DemographicExt cellNoExt = demographicExtDao.getLatestDemographicExt(input.getDemographicId(), DemographicExt.KEY_DEMO_CELL);
		String cellPhoneNumber = (cellNoExt != null) ? StringUtils.trimToNull(cellNoExt.getValue()) : null;
		if(cellPhoneNumber != null)
		{
			exportDemographic.setCellPhoneNumber(new PhoneNumber(cellPhoneNumber));
		}

		//TODO how to handle lazy loading etc.?
		List<Appointment> appointments = appointmentDao.findByDemographicId(input.getDemographicId(), 0, 100);
		exportDemographic.addAppointments(appointmentConverter.convert(appointments));

		return exportDemographic;
	}
}
