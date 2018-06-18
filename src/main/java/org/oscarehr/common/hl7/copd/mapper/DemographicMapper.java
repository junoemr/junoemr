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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.segment.PID;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographic.model.DemographicCust;
import org.oscarehr.demographic.model.DemographicExt;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class DemographicMapper
{
	private final ZPD_ZTR message;
	private final PID messagePID;

	public DemographicMapper()
	{
		message = null;
		messagePID = null;
	}
	public DemographicMapper(ZPD_ZTR message)
	{
		this.message = message;
		this.messagePID = message.getPATIENT().getPID();
	}

	/* Methods for converting to oscar model */

	public Demographic getDemographic() throws HL7Exception
	{
		Demographic demographic = new Demographic();
		demographic.setFirstName(getFirstName(0));
		demographic.setLastName(getLastName(0));
		demographic.setSex(getSex());
		demographic.setDateOfBirth(getDOB());
		demographic.setTitle(getTitle(0));
		demographic.setHin(getPHN());
		demographic.setHcType(getHCType());
		demographic.setSin(getSIN());

		demographic.setAddress(getStreetAddress(0));
		demographic.setCity(getCity(0));
		demographic.setProvince(getProvinceCode(0));
		demographic.setPostal(getPostalCode(0));

		demographic.setPhone(getPhone(0));
		demographic.setPhone2(getPhone(1));

		return demographic;
	}

	public DemographicCust getDemographicCust()
	{
		DemographicCust demographicCust = null;

		return demographicCust;
	}

	public List<DemographicExt> getDemographicExtensions()
	{
		List<DemographicExt> extensionList = new LinkedList<>();
//		DemographicExt demographic = new DemographicExt();

		return extensionList;
	}

	/* Methods for accessing various values in the import message */

	public String getFirstName(int rep) throws HL7Exception
	{
		return messagePID.getPatientName(rep).getGivenName().getValue();
	}
	public String getLastName(int rep) throws HL7Exception
	{
		return messagePID.getPatientName(rep).getFamilyName().getSurname().getValue();
	}
	public String getSex()
	{
		return messagePID.getAdministrativeSex().getValue();
	}
	public LocalDate getDOB()
	{
		String dateStr = messagePID.getDateTimeOfBirth().getTimeOfAnEvent().getValue();
		return ConversionUtils.toLocalDate(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
	}
	public String getTitle(int rep) throws HL7Exception
	{
		return messagePID.getPatientName(rep).getPrefixEgDR().getValue();
	}

	public String getStreetAddress(int rep) throws HL7Exception
	{
		String unitIdentifier = messagePID.getPatientAddress(rep).getStreetAddress().getStreetOrMailingAddress().getValue();
		String streetName = messagePID.getPatientAddress(rep).getStreetAddress().getStreetName().getValue();
		String streetNumber = messagePID.getPatientAddress(rep).getStreetAddress().getDwellingNumber().getValue();

		return StringUtils.trimToNull(String.join(" ",
				StringUtils.trimToEmpty(unitIdentifier), StringUtils.trimToEmpty(streetName), StringUtils.trimToEmpty(streetNumber)));
	}
	public String getCity(int rep) throws HL7Exception
	{
		return messagePID.getPatientAddress(rep).getCity().getValue();
	}
	public String getProvinceCode(int rep) throws HL7Exception
	{
		return messagePID.getPatientAddress(rep).getStateOrProvince().getValue();
	}
	public String getPostalCode(int rep) throws HL7Exception
	{
		return messagePID.getPatientAddress(rep).getZipOrPostalCode().getValue();
	}
	public String getPhone(int rep) throws HL7Exception
	{
		String areaCode = messagePID.getPhoneNumberHome(rep).getAreaCityCode().getValue();
		String phoneNumber = messagePID.getPhoneNumberHome(rep).getPhoneNumber().getValue();

		return StringUtils.trimToNull(StringUtils.trimToEmpty(areaCode) + " " + StringUtils.trimToEmpty(phoneNumber));
	}

	public String getPHN() throws HL7Exception
	{
		Integer rep = getPatientIdentifierRepByCode("PHN");
		if(rep != null)
		{
			return messagePID.getPid3_PatientIdentifierList(rep).getCx1_ID().getValue();
		}
		return null;
	}

	public String getHCType() throws HL7Exception
	{
		Integer rep = getPatientIdentifierRepByCode("PHN");
		if(rep != null)
		{
			String assigningAuth = messagePID.getPid3_PatientIdentifierList(rep).getCx4_AssigningAuthority().getHd1_NamespaceID().getValue();
			if(assigningAuth == null || assigningAuth.length() < 2)
			{
				return null;
			}
			switch(assigningAuth)
			{
				case "ACP": return "AB";
				case "ACPS": return "AB";
				case "AREG": return "AB";
				case "ADA": return "AB";
				default: return assigningAuth.substring(0,2);
			}
		}
		return null;
	}

	public String getSIN() throws HL7Exception
	{
		Integer rep = getPatientIdentifierRepByCode("SIN");
		if(rep != null)
		{
			return messagePID.getPid3_PatientIdentifierList(rep).getCx1_ID().getValue();
		}
		return null;
	}

	private Integer getPatientIdentifierRepByCode(String code) throws HL7Exception
	{
		for(int rep=0; rep < messagePID.getPid3_PatientIdentifierListReps(); rep++)
		{
			String typeCode = messagePID.getPid3_PatientIdentifierList(rep).getCx5_IdentifierTypeCode().getValue();
			if(code.equalsIgnoreCase(typeCode))
			{
				return rep;
			}
		}
		return null;
	}
}
