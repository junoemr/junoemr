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
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DemographicMapper extends AbstractMapper
{
	private final PID messagePID;
	private final String DEMO_NULL_NAME="NULL_NAME";

	public DemographicMapper(ZPD_ZTR message, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, importSource);
		this.messagePID = message.getPATIENT().getPID();
	}

	/* Methods for converting to oscar model */

	public Demographic getDemographic() throws HL7Exception
	{
		if ((hasFirstName(0) && hasLastName(0)) || !CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
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

			demographic.setPhone(getHomePhone());
			demographic.setPhone2(getBuisnessPhone());

			return demographic;
		}
		return null;
	}

	public DemographicCust getDemographicCust()
	{
		DemographicCust demographicCust = null;

		return demographicCust;
	}

	public List<DemographicExt> getDemographicExtensions() throws HL7Exception
	{
		List<DemographicExt> extensionList = new LinkedList<>();

		// attach cell number
		String cellPhone = getPersonalPhone();
		if(cellPhone != null)
		{
			DemographicExt extension = new DemographicExt();
			extension.setDateCreated(new Date());
			extension.setKey("demo_cell");
			extension.setValue(cellPhone);
			extensionList.add(extension);
		}

		return extensionList;
	}

	/* Methods for accessing various values in the import message */

	public String getFirstName(int rep) throws HL7Exception
	{
		String firstName = messagePID.getPatientName(rep).getGivenName().getValue();
		if (firstName == null)
		{
			MiscUtils.getLogger().warn("demographic has no first name! using: " + DEMO_NULL_NAME);
			return 	DEMO_NULL_NAME;
		}
		return firstName;
	}
	public String getLastName(int rep) throws HL7Exception
	{
		String lastName = messagePID.getPatientName(rep).getFamilyName().getSurname().getValue();
		if (lastName == null)
		{
			MiscUtils.getLogger().warn("demographic has no last name! using: " + DEMO_NULL_NAME);
			return DEMO_NULL_NAME;
		}
		return lastName;
	}

	public boolean hasFirstName(int rep) throws HL7Exception
	{
		String firstName = messagePID.getPatientName(rep).getGivenName().getValue();
		return  firstName != null;
	}

	public boolean hasLastName(int rep) throws HL7Exception
	{
		String lastName = messagePID.getPatientName(rep).getFamilyName().getSurname().getValue();
		return  lastName != null;
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

	public String getHomePhone() throws HL7Exception
	{
		Integer rep = getPhoneRepByUsageType("RESD");
		if(rep != null)
		{
			return getPhone(rep);
		}
		return null;
	}
	public String getBuisnessPhone() throws HL7Exception
	{
		Integer rep = getPhoneRepByUsageType("BUSN");
		if(rep != null)
		{
			return getPhone(rep);
		}
		return null;
	}
	public String getPersonalPhone() throws HL7Exception
	{
		Integer rep = getPhoneRepByUsageType("PERS");
		if(rep != null)
		{
			return getPhone(rep);
		}
		return null;
	}

	public String getPhone(int rep) throws HL7Exception
	{
		String areaCode = messagePID.getPid13_PhoneNumberHome(rep).getAreaCityCode().getValue();
		String phoneNumber = messagePID.getPid13_PhoneNumberHome(rep).getPhoneNumber().getValue();

		return StringUtils.trimToNull(StringUtils.trimToEmpty(areaCode) + StringUtils.trimToEmpty(phoneNumber));
	}

	public String getPHN() throws HL7Exception
	{
		Integer rep = 0;
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			rep = getPatientIdentifierRepByCode("ULI");
		}
		else
		{
			rep = getPatientIdentifierRepByCode("PHN");
		}

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
				case "ACP":
				case "ACPS":
				case "AREG":
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

	private Integer getPhoneRepByUsageType(String phoneType) throws HL7Exception
	{
		for(int rep=0; rep<messagePID.getPid13_PhoneNumberHomeReps(); rep++)
		{
			String telecomUsage = messagePID.getPid13_PhoneNumberHome(rep).getXtn2_TelecommunicationUseCode().getValue();
			String telecomType = messagePID.getPid13_PhoneNumberHome(rep).getXtn3_TelecommunicationEquipmentType().getValue();

			if("PH".equalsIgnoreCase(telecomType) && phoneType.equalsIgnoreCase(telecomUsage))
			{
				return rep;
			}
		}
		return null;
	}
}
