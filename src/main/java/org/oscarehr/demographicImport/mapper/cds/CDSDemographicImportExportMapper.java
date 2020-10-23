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
package org.oscarehr.demographicImport.mapper.cds;

import org.oscarehr.common.xml.cds.v5_0.model.LegalName;
import org.oscarehr.common.xml.cds.v5_0.model.ObjectFactory;
import org.oscarehr.common.xml.cds.v5_0.model.PatientRecord;
import org.oscarehr.demographicImport.mapper.AbstractDemographicImportExportMapper;
import org.oscarehr.demographicImport.model.demographic.Address;
import org.oscarehr.demographicImport.model.demographic.Demographic;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

public class CDSDemographicImportExportMapper extends AbstractDemographicImportExportMapper<PatientRecord>
{
	protected final ObjectFactory objectFactory;

	public CDSDemographicImportExportMapper()
	{
		this.objectFactory = new ObjectFactory();
	}

	@Override
	public Demographic importToJuno(PatientRecord importStructure, Demographic demographic)
	{
		fillImportDemographic(importStructure.getDemographics(), demographic);
		return demographic;
	}

	@Override
	public PatientRecord exportFromJuno(Demographic exportStructure)
	{
		PatientRecord patientRecord = objectFactory.createPatientRecord();
		return exportFromJuno(exportStructure, patientRecord);
	}

	@Override
	public PatientRecord exportFromJuno(Demographic exportStructure, PatientRecord importStructure)
	{
		PatientRecord.Demographics demographics = importStructure.getDemographics();
		if (demographics == null)
		{
			demographics = objectFactory.createPatientRecordDemographics();
		}
		fillExportDemographic(exportStructure, demographics);
		importStructure.setDemographics(demographics);
		return importStructure;
	}

	protected void fillExportDemographic(Demographic exportStructure, PatientRecord.Demographics demographics)
	{
		demographics.setNames(getExportNames(exportStructure));
		demographics.setDateOfBirth(ConversionUtils.toXmlGregorianCalendar(exportStructure.getDateOfBirth()));
		demographics.setGender(exportStructure.getSex());
		demographics.getAddress().addAll(getExportAddresses(exportStructure));
		demographics.setEmail(exportStructure.getEmail());
		demographics.setHealthCard(getExportHealthCard(exportStructure));
		demographics.getPhoneNumber().addAll(getExportPhones(exportStructure));
	}

	protected void fillImportDemographic(PatientRecord.Demographics importStructure, Demographic demographic)
	{
		demographic.setFirstName(importStructure.getNames().getLegalName().getFirstName().getPart());
		demographic.setLastName(importStructure.getNames().getLegalName().getLastName().getPart());
		demographic.setDateOfBirth(ConversionUtils.toLocalDate(importStructure.getDateOfBirth()));
		demographic.setSex(importStructure.getGender());
		demographic.setEmail(importStructure.getEmail());
	}

	protected PatientRecord.Demographics.Names getExportNames(Demographic exportStructure)
	{
		PatientRecord.Demographics.Names names = objectFactory.createPatientRecordDemographicsNames();
		LegalName legalName = objectFactory.createLegalName();
		legalName.setNamePurpose("L"); //TODO lookup value

		// first name
		LegalName.FirstName firstName = objectFactory.createLegalNameFirstName();
		firstName.setPart(exportStructure.getFirstName());
		firstName.setPartType("GIV"); //TODO lookup CDS code

		// last name
		LegalName.LastName lastName = objectFactory.createLegalNameLastName();
		lastName.setPart(exportStructure.getLastName());
		lastName.setPartType("FAMC"); //TODO lookup CDS code

		legalName.setFirstName(firstName);
		legalName.setLastName(lastName);

		names.setLegalName(legalName);
		return names;
	}

	protected PatientRecord.Demographics.HealthCard getExportHealthCard(Demographic exportStructure)
	{
		PatientRecord.Demographics.HealthCard healthCard = objectFactory.createPatientRecordDemographicsHealthCard();
		healthCard.setNumber(exportStructure.getHealthNumber());
		healthCard.setVersion(exportStructure.getHealthNumberVersion());
		healthCard.setExpiryDate(ConversionUtils.toNullableXmlGregorianCalendar(exportStructure.getHealthNumberRenewDate()));
		healthCard.setProvinceCode(exportStructure.getHealthNumberProvinceCode());

		return healthCard;
	}

	protected List<PatientRecord.Demographics.Address> getExportAddresses(Demographic exportStructure)
	{
		List<Address> addressList = exportStructure.getAddressList();
		List<PatientRecord.Demographics.Address> exportAddressList = new ArrayList<>(addressList.size());

		for(Address address : addressList)
		{
			PatientRecord.Demographics.Address cdsAddress = objectFactory.createPatientRecordDemographicsAddress();
			org.oscarehr.common.xml.cds.v5_0.model.Structured cdsAddressData = objectFactory.createStructured();
			cdsAddressData.setLine1(address.getAddressLine1());
			cdsAddressData.setLine2(address.getAddressLine2());
			cdsAddressData.setCity(address.getCity());
			cdsAddressData.setCountrySubDivisionCode(address.getRegionCode());
			cdsAddressData.setPostalZipCode(address.getPostalCode());

			cdsAddress.setStructured(cdsAddressData);
			cdsAddress.setAddressType("R"); //TODO load from Table CT-011: Address Type
			exportAddressList.add(cdsAddress);
		}
		return exportAddressList;
	}

	protected List<PatientRecord.Demographics.PhoneNumber> getExportPhones(Demographic exportStructure)
	{
		List<PatientRecord.Demographics.PhoneNumber> exportPhoneList = new ArrayList<>(3);

		PatientRecord.Demographics.PhoneNumber homePhone = objectFactory.createPatientRecordDemographicsPhoneNumber();
		homePhone.setPhoneNumber(exportStructure.getHomePhone());
		homePhone.setPhoneNumberType("R"); //TODO lookup CDS code

		PatientRecord.Demographics.PhoneNumber workPhone = objectFactory.createPatientRecordDemographicsPhoneNumber();
		workPhone.setPhoneNumber(exportStructure.getWorkPhone());
		workPhone.setPhoneNumberType("W"); //TODO lookup CDS code

		PatientRecord.Demographics.PhoneNumber cellPhone = objectFactory.createPatientRecordDemographicsPhoneNumber();
		cellPhone.setPhoneNumber(exportStructure.getCellPhone());
		cellPhone.setPhoneNumberType("C"); //TODO lookup CDS code

		if(homePhone.getPhoneNumber() != null)
		{
			exportPhoneList.add(homePhone);
		}
		if(workPhone.getPhoneNumber() != null)
		{
			exportPhoneList.add(workPhone);
		}
		if(cellPhone.getPhoneNumber() != null)
		{
			exportPhoneList.add(cellPhone);
		}

		return exportPhoneList;
	}
}
